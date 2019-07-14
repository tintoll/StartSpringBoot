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