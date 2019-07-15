## 스프링부트

#### 장점

- 프로젝트에 따라 자주 사용되는 라이브러리들이 미리 조합되어 있다.
- 복잡한 설정을 자동으로 처리해 준다.
- 내장 서버를 포함해서 톰캣과 같은 서버를 추가로 설치하지 않아도 바로 개발이 가능하다.

#### 어노테이션 정리

- `@SpringBootApplication` : 스프링 부트의 핵심 어노테이션이라 해도 무방할 정도로 다양한 기능을 합니다. 3개의 어노테이션 구성되어 있습니다.
  - `@EnableAutoConfiguration` : 스프링의 다양한 설정이 자동으로 완료된다.
  - `@ComponentScan` : 컴포넌트 검색기능을 활성화해서 자동으로 여러가지 컴포넌트 클래스를 검색하고 검색된 컴포넌트 및 빈 클래스를 스프링 애플리케이션 컨텍스트에 등록하는 역할을 합니다.
  - `@Configuration` : 이 어노테이션이 붙은 클래스는 자바기반 설정 파일임을 의미합니다.



### 데이터베이스 연결
#### 히카리 CP란?

스프링 부트 2.0.0 버전 부터는 기본적으로 사용되는 커넥션 풀이 톰캣에서 히카리CP로 변경되었다. 
커넥션풀이란 애플리케이션과 데이터베이스를 연결할때 이를 효과적으로 관리하기 위해 사용되는 라이브러리입니다.

application.properties 설정 
```properties
spring.datasource.hikari.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.hikari.jdbc-url: jdbc:mysql://localhost:3306/board?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC
spring.datasource.hikari.username=spring
spring.datasource.hikari.password=lee1234@
spring.datasource.hikari.connection-test-query=SELECT 1
```
DatabaseConfiguration.java
```java
@Configuration
@PropertySource("classpath:/application.properties") // 설정파일의 위치를 지정해 줍니다.
// @EnableTransactionManagement // 트랜잭션을 활성화 한다. 
public class DatabaseConfiguration {	
	// spring.datasource.hikari로 시작하는 설정을 이용하여 설정파일을 만듭니다.
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.hikari") 
	public HikariConfig hikariConfig() {
		return new HikariConfig();
	}
	// 히카CP의 설정 파일을 이용하여 데이터베이스와 연결하는 데이터 소스를 생성합니다.
	@Bean
	public DataSource dataSource() throws Exception{
		DataSource dataSource = new HikariDataSource(hikariConfig());
		System.out.println(dataSource.toString());
		return dataSource;
	}
  
  
  // Mybatis 설정 
  @Autowired
	private ApplicationContext applicationContext;
	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		// SqlSessionFactory를 생서하기 위하여 SqlSessionFactoryBean을 사용합니다.
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);
		// 마이바티스 매퍼(Mapper)파일의 위치를 설정합니다. 
		// classpath는 resources폴더를 의미
		// ** 패턴은 모든 폴더를 의미
		// sql-*.xml은 sql-로 시작하고 xml인 모든 파일을 의
		sqlSessionFactoryBean.setMapperLocations(
				applicationContext.getResources("classpath:/mapper/**/sql-*.xml")
		);
		// 스네이크표기법 컬럼을 카멜케이스표기법으로 변경하는 설
		sqlSessionFactoryBean.setConfiguration(mybatisConfig());
		return sqlSessionFactoryBean.getObject();
	}
	
	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}
```

#### 마이바티스 설정

데이터를 조회하면 board_idx라는 이름으로 조회되지만 DTO 변수는 boardIdx라는 이름을 가지고 있기때문에 양측의 데이터를 매핑해줄 필요가 있다. 마이바티스 설정으로 이 문제를 해결해준다.

```java
// application.properties
mybatis.configuration.map-underscore-to-camel-case=true

// DatabaseConfiguration.java
@Bean
@ConfigurationProperties(prefix = "mybatis.configuration")
public org.apache.ibatis.session.Configuration mybatisConfig() {
	return new org.apache.ibatis.session.Configuration();
}

// sqlSessionFactoryBean설정하는 부분에서 config를 변경해줌
sqlSessionFactoryBean.setConfiguration(mybatisConfig());
```

#### 서비스 영역
서비스영역은 일반적으로 두개의 파일로 구성됩니다. Service 인터페이스와 ServiceImpl클래스입니다. 이렇게 인터페이스와 실제 구현하는 클래스를 분리할경우 여러가지 장점이 있습니다.
- 느슨한 결합을 유지하여 각 기능 간의 의존관계를 최소화합니다.
- 의존관계의 최소화로 인해 기능의 변화에도 최소한의 수정으로 개발할 수 있는 유연함을 가질수 있습니다.
- 모듈화를 통해 어디서든 사용할 수 있도록 재사용성을 높입니다.
- 스프링의 IoC/DI기능을 이용하여 빈 관리 기능을 사용할 수 있습니다.

하지만 개발하는 시스템의 환경에 따라서 굳이 나눌 필요값 없을 수도 있습니다.

## 스프링의 다양한 기능

### Logback

##### 설정하기 

- src/main/resources/logback-sping.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xml>
<configuration debug="true">
	<!-- Appenders -->
	<appender name="console" class="ch.qos.logback.core.ConsoleAppender">
		<encoder>
			<Pattern>%d %5p [%c] %m%n</Pattern>
		</encoder>   
	</appender>

	<appender name="console-infolog" class="ch.qos.logback.core.ConsoleAppender">
		<encoder>
			<Pattern>%d %5p %m%n</Pattern>
		</encoder>   
	</appender>

	<!-- 로거 -->
	<logger name="board" level="DEBUG" appender-ref="console"/>
	
	<!-- 루트 로거 -->
    <root level="error">
        <appender-ref ref="console"/>
    </root>
</configuration>
```

- appender는 로그를 어디에 출력할지(콘솔, 파일 기록, DB저장등) 결정하는 역할 
- encodeㄱ는 appender에 포함되어 출력할 로그를 지정한 형식으로 변환하는 역할
- logger는 로그를 출력하는 요소로 level속성을 통해서 출력할 로그의 레벨을 조절하여 appender에 전달합니다.

##### 로그레벨

1. `trace` : 모든 로그를 출력합니다.
2. `debug` : 개발할때 디버그 용도로 사용합니다.
3. `info` : 상태변경 등과 같은 정보성 메시지를 나타냅니다.
4. `warn` : 프로그램의 실행에는 문제가 없지만 추후 시스템 에러의 원인이 될 수 있다는 경고성 메시지를 의미합니다.
5. `error` : 요청을 처리하던 중 문제가 발생한 것을 의미합니다.

- 아래로 갈수록 레벨이 높아지면 `설정한 로그 레벨 이상의 로그만 출력`됩니다.

##### 사용하기

- 사용하려는 클래스에 아래와 같이 등록하여 사용할수 있습니다.

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private Logger log = LoggerFactory.getLogger(this.getClass());

log.trace("trace");
log.debug("debug");
log.info("info");
log.warn("warn");
log.error("error");
```

### Log4JDBC로 쿼리로그 정렬하기

- logback을 사용하면 쿼리에서 사용되는 파라미터가 보이지 않기때문에 쿼리를 한눈에 알아보기 힘들다.
- 이러한 문제들을 해결하기 위해서 로그가 정렬되어 출력되고 쿼리에 대한 추가적인 정보도 제공하는 Log4JDBC 라이브러리를 이용할 수 있다. 

##### 라이브러리 추가

```gradle
implementation group: 'org.bgee.log4jdbc-log4j2', name: 'log4jdbc-log4j2-jdbc4.1', version: '1.16'
```

##### 설정

```properties
# log4jdbc.log4j2.properties
log4jdbc.spylogdelegator.name = net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator
log4jdbc.dump.sql.maxlineLength = 0

# application.properties
spring.datasource.hikari.driver-class-name=net.sf.log4jdbc.sql.jdbcapi.DriverSpy
spring.datasource.hikari.jdbc-url: jdbc:log4jdbc:mysql://localhost:3306/board?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC
```

```xml
<!-- logger 추가 -->
<logger name="jdbc.sqlonly" level="INFO" apperder-ref="console-infolog" />
<logger name="jdbc.resultsettable" level="INFO" apperder-ref="console-infolog" />
```

##### log4jdbc가 제공하는 로거

- jdbc.sqlonly : SQL을 보여줍니다. Prepared statement의 경우 관련된 파라미터는 자동으로 변경되어 출력됩니다.
- jdbc.sqltiming : SQL문과 해당 SQL문의 실행 시간을 밀리초 단위로 보여준다.
- jdbc.audit : ResultSets를 제외한 모든 JDBC 호출정보를 보여줍니다. 너무 많은 로그가 찍혀서 잘 사용하지 않는다.
- jdbc.resultset : ResultSets를 포함함 모든 JDBC 호출정보를 보여줍니다.  너무 많은 로그가 찍혀서 잘 사용하지 않는다.
- jdbc.resulttable : SQL의 조회 결과를 테이블로 보여줍니다.
- jdbc.connection : Connection의 연결과 종료에 관련된 로그를 보여줍니다. Connection 누수(leak)문제를 해결하는데 도움이 됩니다.

### 인터셉터

스프링의 인터셉터는 어떠한 URI를 호출 했을때 해당 요청의 컨트롤러가 처리되기 전 또는 후에 작업을 하기 위해서 사용됩니다. 이러한 역할은 필터(Filter)와 인터셉터(Interceptor)로 수행할 수 있습니다.

##### 필터와 인터셉터의 차이점

- 필터는 디스패처 서블릿 앞단에서 동작하지만 인터셉터는 디스패치 서블릭에서 핸들러 컨트롤러로 가기전에 동작합니다.
- 필터는 J2EE표준 스펙에 있는 서블릿의 기능 중 일부이지만 인터셉터는 스프링 프레임워크에서 제공되는 기능입니다. 따라서 필터와 달리 `인터셉터에서는 스프링 빈을 사용할 수 있다.`
- 문자열 인코딩과 같은 웹 전반에서 사용되는 기능은 필터로 구현을 하고, 클라이언트 요청과 관련이 있는 여러가지 처리(로그인이나 인증, 권한등)는 인터셉터로 처리합니다.

##### HandlerInterceptorAdapter로 인터셉터 구현하기

```java
public class LoggerInterceptor extends HandlerInterceptorAdapter {
	// 컨트롤러 실행전에 수행
  @Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		log.debug("======= START =======");
		log.debug(" Request URI \t : {} ", request.getRequestURI());
		return super.preHandle(request, response, handler);
	}
	// 컨트롤러 수행후 결과를 뷰로 보내기 전에 수행됩니다.
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		log.debug("======= END =======");
	}
	
  // 뷰의 작업까지 완료된후 수행됩니다.
  @Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		super.afterCompletion(request, response, handler, ex);
	}
}

```

##### Interceptor 등록하기

```java
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoggerInterceptor());
	}
}
```

