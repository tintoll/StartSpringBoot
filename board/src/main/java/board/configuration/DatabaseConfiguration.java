package board.configuration;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@PropertySource("classpath:/application.properties") // 설정파일의 위치를 지정해 줍니다.
@EnableTransactionManagement // 트랜잭션을 활성화 한다. 
public class DatabaseConfiguration {
	
	@Autowired
	private ApplicationContext applicationContext;
	
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
	
	@Bean
	@ConfigurationProperties(prefix = "mybatis.configuration")
	public org.apache.ibatis.session.Configuration mybatisConfig() {
		return new org.apache.ibatis.session.Configuration();
	}
	
	// 트랙잭션 매니저를 등록한다. 
	@Bean
	public PlatformTransactionManager transationManager() throws Exception {
		return new DataSourceTransactionManager(dataSource());
	}
	
	
	// JPA빈
	@Bean
	@ConfigurationProperties(prefix = "spring.jpa") 
	public Properties hibernateConfig() {
		return new Properties();
	}
}

