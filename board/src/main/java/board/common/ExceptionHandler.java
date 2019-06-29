package board.common;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;


// 해당 클래스가 예외처리 클래스임을 알려준다.
@ControllerAdvice
@Slf4j
public class ExceptionHandler {
	
	// 실무에서는 Exception 을 하면안되고 예외별로 정해줘야 한다. 
	@org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
	public ModelAndView defaultExceptionHandler(HttpServletRequest request, Exception exception) {
		
		ModelAndView mv = new ModelAndView("/error/error_default");
		mv.addObject("exception", exception);
		log.debug("exception",exception);
		
		return mv;
	}
}
