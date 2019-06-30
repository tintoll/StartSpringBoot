package board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import board.dto.BoardDto;
import board.dto.BoardFileDto;

@Mapper
public interface BoardMapper {
	List<BoardDto> selectBoardList() throws Exception;

	void insertBoard(BoardDto board) throws Exception;

	void updateHitCount(int boardIdx) throws Exception;

	BoardDto selectBoardDetail(int boardIdx) throws Exception;

	void deleteBoard(int boardIdx) throws Exception;

	void updateBoard(BoardDto board) throws Exception;

	void insertBoardFileList(List<BoardFileDto> list) throws Exception;

	List<BoardFileDto> selectBoardFileList(int boardIdx) throws Exception;
	
	
	// @Param("idx")를 이용하여 여러개의파라미터를 보내주면 
	// sql에서는 parameterType="map"으로 받아서 사용할 수 있다. 
	BoardFileDto selectBoardFileInformation(@Param("idx") int idx,@Param("boardIdx") int boardIdx);
	
	
}
