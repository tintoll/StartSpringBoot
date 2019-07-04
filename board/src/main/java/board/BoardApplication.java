package board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


// java8 날짜관련 클래스를 그대로 사용하기 위한 설정 
@EnableJpaAuditing
@EntityScan(
		basePackageClasses = {Jsr310JpaConverters.class},
		basePackages = {"board"}
		)
@SpringBootApplication(exclude = {MultipartAutoConfiguration.class}) // 부트 자동설정을 제외시킨다.
public class BoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardApplication.class, args);
	}

}
