package board.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import board.interceptor.LoggerInterceptor;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoggerInterceptor());
	}
	
	// Multipart 설정 
	@Bean
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
		commonsMultipartResolver.setDefaultEncoding("UTF-8");
		commonsMultipartResolver.setMaxUploadSizePerFile(5 * 1045 * 1024);
		return commonsMultipartResolver;
	}
	
	/*
	// 부트 2.1 이하에만 인코딩적용을 하면됨
	@Bean
	public Filter characterEncodingFilter() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		// 웹에서 주고 받는 데이터의 헤더값을 UTF-8로 인코딩해준다. 
		characterEncodingFilter.setEncoding("UTF-8");
		// 입려값(HttpServlerRequest)과 결과값(HttpServletResponse) 모두에 강제적으로 설정된 인코딩으로 변경한다. 
		characterEncodingFilter.setForceEncoding(true);
		return characterEncodingFilter;
	}
	
	@Bean
	public HttpMessageConverter<String> responseBodyConverter() {
		// @ResponseBody를 이용하여 결과를 출력할때 그 결과를 UTF-8로 설정합니다.
		return new StringHttpMessageConverter(Charset.forName("UTF-8"));
	}
	*/
}
