package board.controller;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import board.dto.BoardDto;
import board.dto.BoardFileDto;
import board.entity.BoardEntity;
import board.entity.BoardFileEntity;
import board.service.JpaBoardService;

@Controller
public class JpaBoardController {

	@Autowired
	private JpaBoardService jpaBoardService;
	
	@GetMapping("/jpa/board")
	public ModelAndView openBoardList() throws Exception {
		
		ModelAndView mv = new ModelAndView("/board/jpaBoardList");
		
		List<BoardEntity> list = jpaBoardService.selectBoardList();
		mv.addObject("list", list);
		
		return mv;
	}
	
	@GetMapping("/jpa/board/write")
	public String openBoardWrite() throws Exception {
		return "/board/jpaBoardWrite";
	}
	
	@PostMapping("/jpa/board/write")
	public String insertBoard(BoardEntity board, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception {
		jpaBoardService.saveBoard(board, multipartHttpServletRequest);
		return "redirect:/jpa/board";
	}
	
	@GetMapping("/jpa/board/{boardIdx}")
	public ModelAndView openBoardDetail(@PathVariable("boardIdx") int boardIdx) throws Exception {
		ModelAndView mv = new ModelAndView("/board/jpaBoardDetail");
		
		BoardEntity board = jpaBoardService.selectBoardDetail(boardIdx);
		mv.addObject("board", board);
		
		return mv;
	}
	
	@PutMapping("/jpa/board/{boardIdx}")
	public String updateBoard(BoardEntity board) throws Exception {
		jpaBoardService.saveBoard(board, null);
		return "redirect:/jpa/board";
	}
	
	@DeleteMapping("/jpa/board/{boardIdx}")
	public String deleteBoard(@PathVariable("boardIdx") int boardIdx) throws Exception {
		jpaBoardService.deleteBoard(boardIdx);
		return "redirect:/jpa/board";
	}
	
	
	@GetMapping("/jpa/board/file")
	public void downloadBoardFile(@RequestParam int idx, 
			@RequestParam int boardIdx, HttpServletResponse response) throws Exception {
		
		BoardFileEntity boardFile = jpaBoardService.selectBoardFileInfomation(idx, boardIdx);
		if(ObjectUtils.isEmpty(boardFile) == false) {
			String fileName = boardFile.getOriginalFileName();
			
			// 파일을 읽어온후 바이트 배열 형태로 변환합니다. org.apache.commons.io 패키지의 FileUtils이다. 
			byte[] files = FileUtils.readFileToByteArray(new File(boardFile.getStoredFilePath()));
			
			// response의 헤더에 콘텐츠 타입, 크키 및 형태등을 설정 
			response.setContentType("application/octet-stream");
			response.setContentLength(files.length);
			response.setHeader("Content-Disposition","attachment; fileName=\""+URLEncoder.encode(fileName,"UTF-8")+"\";");
			response.setHeader("Content-Transfer-Encoding", "binary");
			
			// 읽어온 파일 정보의 바이트 배열 데이터를 response에 작성합니다.
			response.getOutputStream().write(files);
			// response의 버퍼를 정리하고 닫아줍니다.
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}
		
		
	}
	
}
