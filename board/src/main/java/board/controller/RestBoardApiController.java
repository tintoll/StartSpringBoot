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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import board.dto.BoardDto;
import board.dto.BoardFileDto;
import board.service.BoardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@ApiOperation(value = "게시판 REST API")
@RestController
public class RestBoardApiController {

	@Autowired
	private BoardService boardService;
	
	
	@GetMapping("/api/board")
	public List<BoardDto> openBoardList() throws Exception {
		return boardService.selectBoardList();
	}
	
	// post나 put은 http body에서 값을 가져오기 때문에 @RequestBody사용한다. 
	@PostMapping("/api/board/write")
	public void insertBoard(@RequestBody BoardDto board) throws Exception {
		boardService.insertBoard(board, null);
	}
	
	@ApiOperation(value = "게시글 상세내용 조회")
	@GetMapping("/api/board/{boardIdx}")
	public BoardDto openBoardDetail(@PathVariable("boardIdx") @ApiParam(value="게시글 번호") int boardIdx) throws Exception {
		return boardService.selectBoardDetail(boardIdx);
	}
	
	@PutMapping("/api/board/{boardIdx}")
	public String updateBoard(@RequestBody BoardDto board) throws Exception {
		boardService.updateBoard(board);
		return "redirect:/board";
	}
	
	@DeleteMapping("/api/board/{boardIdx}")
	public String deleteBoard(@PathVariable("boardIdx") int boardIdx) throws Exception {
		boardService.deleteBoard(boardIdx);
		return "redirect:/board";
	}
	
}
