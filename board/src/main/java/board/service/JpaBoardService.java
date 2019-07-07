package board.service;

import java.util.List;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import board.dto.BoardDto;
import board.dto.BoardFileDto;
import board.entity.BoardEntity;
import board.entity.BoardFileEntity;

public interface JpaBoardService {
	
	List<BoardEntity> selectBoardList() throws Exception;

	void saveBoard(BoardEntity board, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception;

	BoardEntity selectBoardDetail(int boardIdx) throws Exception;

	void deleteBoard(int boardIdx) throws Exception;
	
	BoardFileEntity selectBoardFileInfomation(int idx, int boardIdx) throws Exception;
}
