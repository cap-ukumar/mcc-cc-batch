package org.cap.cc.batch.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


@Configuration
public class MultipleDBConfig {
	
	 @Value("${springds.informix.url}")
	    private String informixUrl;
	 
	    @Value("${springds.informix.username}")
	    private String informixUserName;
	 
	    @Value("${springds.informix.password}")
	    private String informixPassword;
	 
	    @Value("${springds.informix.driverClassName}")
	    private String informixDriverClass;
	
	
	
	@Bean(name ="informixDb")
	public DataSource dataSourceInformix() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		   dataSource.setDriverClassName(informixDriverClass);
	        dataSource.setUrl(informixUrl);
	        dataSource.setUsername(informixUserName);
	        dataSource.setPassword(informixPassword);
	        return dataSource;
	}
	
	@Bean(name = "informixJdbcTemplate")
	public NamedParameterJdbcTemplate jdbcTemplate(@Qualifier("informixDb") DataSource dsMySQL) {
		return new NamedParameterJdbcTemplate(dsMySQL);
	}
	
	
	
}
