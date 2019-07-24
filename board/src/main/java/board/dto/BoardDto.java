package board.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value="BoardDto : 게시글 내용", description = "게시글 내용")
@Data
public class BoardDto {
	@ApiModelProperty(value="게시글 번호")
	private int boardIdx;
	@ApiModelProperty(value="게시글 제목")
	private String title;
	@ApiModelProperty(value="게시글 내용")
	private String contents;
	private int hitCnt;
	private String creatorId;
	private String createdDatetime;
	private String updaterId;
	private String updatedDatetime;	
	
	private List<BoardFileDto> fileList;
}
