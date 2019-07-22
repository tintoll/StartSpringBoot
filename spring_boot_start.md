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



### AOP 사용하기

AOP는 OOP를 더욱 OOP답게 사용하도록 도와주는 개념으로 AOP를 이해하기 위해서는 우선 OOP의 이해가 필요합니다.

어플리케이션을 개발하다보면 객체의 핵심코드 외에도 여러가지 기능이 들어갑니다. 메서드 호출 전후의 로그, 데이터 검증 및 확인 로그, 예외처리 등 핵심기능과 관계는 없지만 그렇다고 없어서도 안 되는 코드들이 삽이되면서 객체의 모듈화가 어려워지곤 합니다. 이런 부가 기능의 관점에서 보는 것이 AOP이다. 부가 기능의 관점에서는 핵심 로직이 어떤 역할을 하는지는 몰라도 됩니다. 단지 부가 기능이 적용될 시점, 즉 핵심 로직의 시작이나 종료 시점에 그곳에서 필요한 부가 기능이 적용되기만 하면 됩니다. 

AOP는 애플리케이션 전반에서 사용되는 기능을 여러 코드에 쉽게 적용할 수 있도록 합니다. 결국 공통된 기능을 재사용할수 있게 해주는 것이라고 이해하면 쉬울듯 하다. 

##### AOP 용어

- `관점(Aspect)` : 공통적으로 적용될 기능을 의미합니다. 횡단 관심사의 기능이라고도 할 수 있으며 한 개 이상의 포인트컷과 어드바이스의 조합으로 만들어 집니다.

- `어드바이스(Advice)` : 관점의 구현체로 조인포인트에 삽입되어 동작하는 것을 의미합니다. 

  - @Before : 대상 메서드가 실행되기 전에 적용할 어드바이스를 정의합니다.
  - @AfterReturning : 대상 메서드가 성공적으로 실행되고 결과값을 반환한후 적용할 어드바이스를 정의합니다.
  - @AfterThrowing : 대상 메서드에서 예외가 발생했을때 적용할 어드바이스를 정의합니다.
  - @After : 대상 메서드의 정상적인 수행 여부와 상관없이 무조건 실행되는 어드바이스를 정의합니다.
  - @Around : 대상 메서드의 호출 전후, 예외발생등 모든 시점에 적용할 수 있는 어드바이스를 정의합니다.

- `조인포인트(Joinpoint)` : 어드바이스를 적용하는 지점을 의미합니다. 스프링 프레임워크에서 조인포인트는 항상 메서드 실행 단계만 가능합니다.

- `포인트컷(Pointcut)` : 어드바이스를 적용할 조인포인트를 선별하는 과정이나 그 기능을 정의한 모듈을 의미합니다.

  - execution : 가장 많이 상용되는 지시자로서 접근제어자, 리턴타입, 타입패턴, 메서드, 파라미터 타입, 예외타입 등을 조합해서 가장 정교한 포인트컷을 만들 수 있다. `*`는 모든 값이라는 의미, `..` 은 0개 이상이라는 의미입니다.

  - within : 특정 타입에 속하는 메서드를 포인트컷으로 설정합니다.

    ```java
    within(board.service.boardServiceImpl)
    within(board.service.*ServiceImpl)
    ```

  - bean : 스프링의 빈 이름의 패턴으로 포인트컷을 설정합니다.

    ```java
    bean(boardServiceImpl)
    bean(*ServiceImpl)
    ```

- `타깃(Target)` : 어드바이스를 받을 대상을 의미합니다. 

- `위빙(Weaving)` : 어드바이스를 적용하는 것을 의미합니다. 즉, 공통 토드를 원하는 대상에 삽입하는 것을 뜻합니다.

##### AOP 적용하기

```java
@Component
@Aspect // 자바코드에서 AOP를 설정
@Slf4j
public class LoggerAspect {
	
  // 해당기능이 실행될 시점, 즉 어드바이스를 정의합니다.
  // @Around는 대상 메서드의 호출 전후, 예외 발생 등 모든 시점에 적용할수 있는 어드바이스 이다. 
  // execution은 포인트컷 표현식으로 적용할 메서드를 명시할 때 사용됩니다.
	@Around("execution(* board..controller.*Controller.*(..)) or execution(* board..service.*Impl.*(..)) or execution(* board..mapper.*Mapper.*(..))")
	public Object logPrint(ProceedingJoinPoint joinPoint) throws Throwable {
		String type = "";
		String name = joinPoint.getSignature().getDeclaringTypeName();
		if(name.indexOf("Controller") > 1) {
			type = "Controller \t: ";
		} else if(name.indexOf("Impl") > 1) {
			type = "Service \t: ";
		} else if(name.indexOf("Mapper") > 1) {
			type = "Mapper \t: ";
		}
		
		log.debug(type+name+","+joinPoint.getSignature().getName()+ "()");
		return joinPoint.proceed();
	}
}
```



### 트랙잭션 적용하기 

스프링에서 트랜잭션을 처리하는 방식은 XML설정 과 어노테이션을 이용하는 방식, AOP를 이용하는 방식으로 나눌 수 있습니다.

##### 트랜잭션

- 데이터베이스의 상태를 변화시킬 때 더 이상 분리할 수 없는 작업의 단위를 의미합니다. 
- 즉 되려면 모두 다 되어야하고, 하나라도 안된다면 모두 안되어야 한다.

###### ACID 속성

- 원자성(Atomicity) :  트랜잭션은 하나 이상의 관련된 동작을 하나의 작업 단위로 처리합니다.
- 일관성(Consistency) : 트랜잭션은 성공적으로 처리되면 데이터베이스의 관련된 모든 데이터는 일관성을 유지해야 합니다.
- 고립성(Isolation) : 트랜잭션은 독립적으로 처리되며, 처리되는 중간에 외부에서의 간섭은 없어야 합니다.
- 지속성(Durability) : 트랜잭션이 성공적으로 처리되면 그 결과는 지속적으로 유지되어야 합니다.

##### @Transaction  어노테이션 이용해 설정

```java
@Configuration
@PropertySource("classpath:/application.properties") // 설정파일의 위치를 지정해 줍니다.
@EnableTransactionManagement // 트랜잭션을 활성화 한다. 
public class DatabaseConfiguration {
  
  ....
  
  // 트랙잭션 매니저를 등록한다. 
	@Bean
	public PlatformTransactionManager transationManager() throws Exception {
		return new DataSourceTransactionManager(dataSource());
	}	
}  


@Service
@Transactional // @Transactional는 인터페이스나 클래스, 메서드에 사용할 수 있습니다.
public class BoardServiceImpl implements BoardService {
	...
}
```

##### AOP를 이용해 트랜잭션 설정

- @Transaction 어노테이션 방식을 사용하면 새로운 클래스 또는 메서드 등을 만들 때마나 어노테이션을 붙여줘야 하는데 누락되거나 일관되지 않게 적용될 수 도 있습니다. 
- 외부라이브러리를 사용하면 해당 라이브러리의 코드를 편집할 수 없기 때문에 트랜잭션이 적절하게 처리되지 않을 수 있습니다. 
- 이러한 문제를 해결하려면 AOP를 이용해서 트랜잭션을 설정하면 됩니다.

```java
@Configuration
public class TransactionAspect {
	
	// 트랜잭션에 사용되는 설정값 
	private static final String AOP_TRANSACTION_METHOD_NAME = "*";
	private static final String AOP_TRANSACTION_EXPRESSION = "execution(* board..service.*Impl.*(..))";
	
	@Autowired
	private PlatformTransactionManager transactionManager;
	
	
	@Bean
	public TransactionInterceptor transactionAdvice() {
		MatchAlwaysTransactionAttributeSource source = new MatchAlwaysTransactionAttributeSource();
		RuleBasedTransactionAttribute transactionAttribute = new RuleBasedTransactionAttribute();
		// 트랜잭션 모니터에서 트랜잭션의 이름을 확인할수 있다.
		transactionAttribute.setName(AOP_TRANSACTION_METHOD_NAME); 
		// 롤백 룰설정(Exception을 하면 모든 예외시 발생한다.
		transactionAttribute.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
		source.setTransactionAttribute(transactionAttribute);
		return new TransactionInterceptor(transactionManager, source); 
	}
	
	@Bean
	public Advisor transactionAdviceAdvisor() {
		// AOP 포인트것을 설정
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(AOP_TRANSACTION_EXPRESSION);	
		return new DefaultPointcutAdvisor(pointcut, transactionAdvice());
	}
}
```



### 예외처리하기

예외처리방식은 크게 3가지가 있습니다.

1. Try/catch를 이용한 예외처리
2. 각각의 컨트롤러 단에서 @ExceptionHandler를 이용한 예외처리
   1. 컨트롤러별로 동일한 예외처리를 추가해야 하기 때문에 코드가 많이 중복됩니다.
3. @ControllerAdvice를 이용한 전역 예외처리

##### @ControllerAdvice 추가하기

```java
// 해당 클래스가 예외처리 클래스임을 알려준다.
@ControllerAdvice
@Slf4j
public class ExceptionHandler {
	
	// 실무에서는 Exception 을 하면안되고 예외별로 정해줘야 한다. 
	@org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
	public ModelAndView defaultExceptionHandler(HttpServletRequest request, Exception exception) {
		ModelAndView mv = new ModelAndView("/error/error_default"); //에러를 보여줄 화면
		mv.addObject("exception", exception);
		log.debug("exception",exception);
		return mv;
	}
}
```



## 파일 업로드 & 다운로드

- 스프링 프레임워크에는 파일 업로드를 위한 MultipartResolver인터페이스가 정의되어 있다. 일반적으로 사용되는 구현체는 아래와 같다.
  - 아파치의 Common Fileupload를 이용한 CommonsMultipartResolver
  - 서블릿 3.0이상의 API를 이용한 StandardServletMultipartResolver

###아파치의 Common Fileupload 이용한 업로드 

- 라이브러리 추가 

```gradle

implementation group:'commons-io', name:'commons-io', version:'2.5'
implementation group:'commons-fileupload', name:'commons-fileupload', version:'1.3.3'
```

- 빈 생성 

```java
// Multipart 설정 
@Bean
public CommonsMultipartResolver multipartResolver() {
  CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
  commonsMultipartResolver.setDefaultEncoding("UTF-8"); // 인코딩 설정 
  commonsMultipartResolver.setMaxUploadSizePerFile(5 * 1045 * 1024); // 파일 용량 5Mb로 제한
  return commonsMultipartResolver;
}
```

* 스프링 부트는 자동구성이 되어 있는 부분이 많기 때문에 파일 업로드에 대한 자동구성을 사용하지 않도록 변경해줘야한다. 

```java
  @SpringBootApplication(exclude = {MultipartAutoConfiguration.class}) // 부트 자동설정을 제외시킨다.
  public class BoardApplication {
  	public static void main(String[] args) {
  		SpringApplication.run(BoardApplication.class, args);
  	}
  }  
```

- 뷰 부분 소스

```html
<form id="frm" name="frm" method="post" enctype="multipart/form-data" action="/board/insertBoard.do">
  <table class="board_detail">
    <tr>
      <td>제목</td>
      <td><input type="text" id="title" name="title"/></td>
    </tr>
    <tr>
      <td colspan="2">
        <textarea id="contents" name="contents"></textarea>
      </td>
    </tr>
  </table>
  <input type="file" id="files" name="files" multiple="multiple" />
  <input type="submit" id="submit" value="저장" class="btn">
</form>
```

- 컨트롤러 부분

```java
@RequestMapping("/board/insertBoard.do")
	public String insertBoard(BoardDto board, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception {
		boardService.insertBoard(board, multipartHttpServletRequest);
		return "redirect:/board/openBoardList.do";
	}
```

- 파일 저장 관련 서비스 부분

```java
public List<BoardFileDto> parseFileInfo(int boardIdx, 
			MultipartHttpServletRequest multipartHttpServletRequest) throws Exception {
  if(ObjectUtils.isEmpty(multipartHttpServletRequest)) {
    return null;
  }
  List<BoardFileDto> fileList = new ArrayList<>();
  // 파일 저장 경로 설정 
  DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
  ZonedDateTime current = ZonedDateTime.now(); // 오늘날짜를 가져옵니다. 
  String path = "images/"+current.format(format);
  File file = new File(path);
  if(file.exists() == false) {
    file.mkdirs();
  }

  Iterator<String> iterator = multipartHttpServletRequest.getFileNames();
  String newFileName, originalFileExtension, contentType;
  while(iterator.hasNext()) {
    // iterator.next() == files
    // 화면에서 files로 보낸 파일들 리스트를 가져올수 있다.
    List<MultipartFile> list = multipartHttpServletRequest.getFiles(iterator.next());
    for(MultipartFile multipartFile : list) {
      if(multipartFile.isEmpty() == false) {
        // 파일 확장자 체크는 contentType으로 해야한다. 파일명에서 가져오면 위변조 할수 있기때문이다.
        contentType = multipartFile.getContentType();
        if(ObjectUtils.isEmpty(contentType)) {
          break;
        } else {
          if(contentType.contains("image/jpeg")) {
            originalFileExtension = ".jpg";
          }else if(contentType.contains("image/png")) {
            originalFileExtension = ".png";
          }else if(contentType.contains("image/gif")) {
            originalFileExtension = ".gif";
          }else {
            break;
          }
        }

        // 파일이름은 중복되지 않게 나노타임을 사용했다.
        newFileName = Long.toString(System.nanoTime()) + originalFileExtension;
        BoardFileDto boardFile = new BoardFileDto();
        boardFile.setBoardIdx(boardIdx);
        boardFile.setFileSize(multipartFile.getSize());
        boardFile.setOriginalFileName(multipartFile.getOriginalFilename());
        boardFile.setStoredFilePath(path + "/" + newFileName);
        fileList.add(boardFile);

        // 새로운 이름을 변경된 파일을 저장한다.
        file = new File(path + "/" + newFileName);
        multipartFile.transferTo(file);
      }
    }
  }
  return fileList;
}
```



- Mapper
  - useGeneratedKeys속성은 DBMS가 자동 생성키를 지원할 경우에 사용할 수 있습니다. 
  - keyProperty는 useGeneratedKeys나 selectKey의 하위 엘리먼트에 의해 리턴퇴는 키를 의미합니다.
  - BoardDto에 boardIdx에 생성된 키가 저장이 된다. 

```xml
<insert id="insertBoard" parameterType="board.dto.BoardDto" 
        useGeneratedKeys="true" keyProperty="boardIdx">
		<![CDATA[
			INSERT INTO T_BOARD
			(
				TITLE,
				CONTENTS,
				CREATED_DATETIME,
				CREATOR_ID
			)
			VALUES
			(
				#{title},
				#{contents},
				NOW(),
				'admin'
			)
		]]>
	</insert>
```



### 파일 다운로드

```java
import org.apache.commons.io.FileUtils;

@RequestMapping("/board/downloadBoardFile.do")
public void downloadBoardFile(@RequestParam int idx, 
                              @RequestParam int boardIdx, HttpServletResponse response) throws Exception {

  BoardFileDto boardFile = boardService.selectBoardFileInfomation(idx, boardIdx);
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
```



## REST API

REST : 잘 표현된 HTTP URI로 리소스를 정의하고 HTTP메소드로 리소스에 대한 행위를 정의합니다. 

리소스 : 서비스를 제공하는 시스템의 자원을 의미하는 것으로 URI(Uniform Resource Identifier)로 정의됩니다.

#### URI 설계 규칙

1. URI는 명사를 사용합니다 
2. 슬래시(/)로 계층 관계를 나타냅니다.
3. URI의 마지막에는 슬래시를 사용하지 않습니다.
4. URI는 소문자로만 작성합니다.
5. 가독성을 높이기 위해 하이픈(-)를 사용할 수는 있지만 밑줄(_)은 사용하지 않습습니다.

#### PUT, DELETE 사용하기

```html
<input type="hidden" id="method" name="_method" value="put" />
```

HTML은 POST와 GET방식의 요청만 지원하고 PUT, DELETE방식은 지원하지 않습니다.  스프링은 웹 브라우저에서 사용되는 POST, GET방식을 이용하여 PUT과 DELETE방식을 사용할수 있는 기능을 지원하는데 `HiddenHttpMethodFilter`가 바로 그것입니다. 이 필터는 스프링 부트 2.0에서는 직접 빈으로 등록해야 했지만 스프링 부트 2.1.x에는 이미 필터가 등록되어 있어 별도로 설정하지 않아도 된다.

#### Rest API Controller

```java
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
	
	@GetMapping("/api/board/{boardIdx}")
	public BoardDto openBoardDetail(@PathVariable("boardIdx") int boardIdx) throws Exception {
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
```

- `@RestController` 어노테이션은 @Controller와 @ResponseBody 어노테이션을 합친 어노테이션입니다.  이 어노테이션을 사용하면 해당 API의 응답 결과를 웹 응답 바디(Web response body)를 이용해서 보내줍니다. 일반적으로 서버와 클라이언트의 통신에 JSON형식을 사용합니다.

- GET과 POST의 주요한 차이점 중하나는 GET은 요청주소에 파라미터를 같이 보내는 것이고 POST는 GET과 달리 파라미터를 HTTP 패킷의 바디에 담아서 전송한다는 것이다. `@RequestBody` 어노테이션은 메서드의 파라미터가 반드시 HTTP 패킷의 바디에 담겨 있어야 한다는 것을 나타냅니다.  반대로 GET메서드는 @RequestParam 어노테이션을 사용합니다.



## Spring Data JPA

JPA(Java Persistence API)란 자바 객체와 데이터베이스 테이블 간의 매핑을 처리하는 ORM(Object Relational Mapping) 기술의 표준입니다.

##### 장점

1. 개발이 편리하다
2. 데이터베이스에 독립적인 개발이 가능하다
3. 유지보수가 쉽다.

##### 단점

1. 학습곡선이 크다
2. 특정 데이터베이스의 기능을 사용할 수 없다.
3. 객체지향 설계가 필요하다

#### 스프링 데이터 JPA란?

스프링 데이터 JPA는 리포지터리(Repository)라는 인터페이스를 제공합니다. 이 인터페이스만 상속받아 정해진 규칙에 맞게 메서드를 작성하면 개발자가 작성해야 할 코드가 완성됩니다.

#### 스프링 데이터 JPA 기본설정

- application.properties

  ```properties
  ## jpa 설정 
  spring.jpa.database=mysql
  # InnoDB엔진을 사용하게 설정
  spring.jpa.database-platform=org.hibernate.dialect.MySQL5InnoDBDialect
  # JPA의 엔티티 연관관계를 바탕으로 테이블 생성과 같은 스크립트를 자동으로 실행하도록합니다.
  # 실제 개발에서는 false로 해야한다.
  spring.jpa.generate-ddl=true
  # 하이버네이트의 새로운 ID 생성 옵션의 사용여부
  spring.jpa.hibernate.use-new-id-generator-mappings=false
  ```

- 빈등록하기

  ```java
  @Bean
  @ConfigurationProperties(prefix = "spring.jpa") 
  public Properties hibernateConfig() {
    return new Properties();
  }
  ```

- 자바8의 날짜 API 설정

  - 자바8의 날짜 및 시간 관련 클래스를 그대로 사용할 경우 MySQL의 버전에 따라 문제가 발생할 수 있습니다. 이 문제를 해결하는 방법이 여러가지 있는데 Jsr310JpaConverters 적용방법을 설정해보자

    ```java
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
    ```

#### 스프링 데이터 JPA 이용하기

- 엔티티 생성

```java
@Entity
@Table(name = "t_jpa_board")
@NoArgsConstructor
@Data
public class BoardEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int boardIdx;
	
	@Column(nullable = false)
	private String title;
	
	@Column(nullable = false)
	private String contents;
	
	@Column(nullable = false)
	private int hitCnt = 0;
	
	@Column(nullable = false)
	private String creatorId;
	
	@Column(nullable = false)
	private LocalDateTime createdDatetime = LocalDateTime.now();
	
	private String updateId;
	private LocalDateTime updatedDatetime;
	
  // 1:N의 관계를 표현하는 어노테이션
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  // 릴레이션 관계가 있는 테이블의 컬럼을 지정합니다.
	@JoinColumn(name = "boardIdx")
	private Collection<BoardFileEntity> fileList;
}
```

- 레파지토리 생성

```java
// 가장 간단히 사용하는 CrudRepository를 상속받는다.
public interface JpaBoardRepository extends CrudRepository<BoardEntity, Integer>{
	// 규칙에 맞도록 리포지터리에 메서드를 추가하면 실행시 메서드의 이름에 따라 쿼리가 생성되어 실행됩니다.
	List<BoardEntity> findAllByOrderByBoardIdxDesc();
	
  // 실행하고 싶은 쿼리를 직접 정의할수 있다 
	@Query("SELECT file FROM BoardFileEntity file WHERE board_idx = :boardIdx AND idx = :idx")
	BoardFileEntity findBoardFile(@Param("boardIdx") int boardIdx,@Param("idx") int idx);
}
```

- 리포지터리 인터페이스 종류
  - Repository : 아무런 기능이 없어 잘 사용안함.
  - CrudRepository : CRUD기능을 기본적으로 제공 
  - PaginAndSotingRepository : 페이징 및 정렬기능이 추가 됨
  - JpaRepository : JPA에 특화된 기능까지 추가된 인터페이스 

- CrudRepository가 제공하는 메서드
  - save : 주어진 엔티티를 저장
  - saveAll : 주어진 엔티티 목록을 저장
  - findById : 주어진 아이디로 식별된 엔티티를 반환
  - existsById : 주어진 아이디로 식별된 엔티티가 존재하는지를 반환
  - findAll : 모든 엔티티를 반환
  - findAllById : 주어진 아이디 목록에 맞는 모든 엔티티 목록을 반환합니다.
  - count : 사용 가능한 엔티티의 개수를 반환
  - deleteById : 주어진 아이디로 식별된 엔티티를 삭제
  - delete : 엔티티를 삭제
  - deleteAll : 모든 엔티티를 삭제
- 쿼리 메서드 
  - https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation

| Keyword             | Sample                                                       | JPQL snippet                                                 |
| :------------------ | :----------------------------------------------------------- | :----------------------------------------------------------- |
| `And`               | `findByLastnameAndFirstname`                                 | `… where x.lastname = ?1 and x.firstname = ?2`               |
| `Or`                | `findByLastnameOrFirstname`                                  | `… where x.lastname = ?1 or x.firstname = ?2`                |
| `Is,Equals`         | `findByFirstname`,`findByFirstnameIs`,`findByFirstnameEquals` | `… where x.firstname = ?1`                                   |
| `Between`           | `findByStartDateBetween`                                     | `… where x.startDate between ?1 and ?2`                      |
| `LessThan`          | `findByAgeLessThan`                                          | `… where x.age < ?1`                                         |
| `LessThanEqual`     | `findByAgeLessThanEqual`                                     | `… where x.age <= ?1`                                        |
| `GreaterThan`       | `findByAgeGreaterThan`                                       | `… where x.age > ?1`                                         |
| `GreaterThanEqual`  | `findByAgeGreaterThanEqual`                                  | `… where x.age >= ?1`                                        |
| `After`             | `findByStartDateAfter`                                       | `… where x.startDate > ?1`                                   |
| `Before`            | `findByStartDateBefore`                                      | `… where x.startDate < ?1`                                   |
| `IsNull`            | `findByAgeIsNull`                                            | `… where x.age is null`                                      |
| `IsNotNull,NotNull` | `findByAge(Is)NotNull`                                       | `… where x.age not null`                                     |
| `Like`              | `findByFirstnameLike`                                        | `… where x.firstname like ?1`                                |
| `NotLike`           | `findByFirstnameNotLike`                                     | `… where x.firstname not like ?1`                            |
| `StartingWith`      | `findByFirstnameStartingWith`                                | `… where x.firstname like ?1` (parameter bound with appended `%`) |
| `EndingWith`        | `findByFirstnameEndingWith`                                  | `… where x.firstname like ?1` (parameter bound with prepended `%`) |
| `Containing`        | `findByFirstnameContaining`                                  | `… where x.firstname like ?1` (parameter bound wrapped in `%`) |
| `OrderBy`           | `findByAgeOrderByLastnameDesc`                               | `… where x.age = ?1 order by x.lastname desc`                |
| `Not`               | `findByLastnameNot`                                          | `… where x.lastname <> ?1`                                   |
| `In`                | `findByAgeIn(Collection<Age> ages)`                          | `… where x.age in ?1`                                        |
| `NotIn`             | `findByAgeNotIn(Collection<Age> ages)`                       | `… where x.age not in ?1`                                    |
| `True`              | `findByActiveTrue()`                                         | `… where x.active = true`                                    |
| `False`             | `findByActiveFalse()`                                        | `… where x.active = false`                                   |
| `IgnoreCase`        | `findByFirstnameIgnoreCase`                                  | `… where UPPER(x.firstame) = UPPER(?1)`                      |

