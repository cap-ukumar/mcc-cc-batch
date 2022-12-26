package org.cap.cc.batch.config;

import javax.sql.DataSource;

import org.cap.cc.batch.utils.CapConfigConstants;
import org.cap.cc.batch.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


//@Configuration
@Deprecated
public class MultipleDBConfig {
	
	
	@Bean(name ="informixDb")
	public DataSource dataSourceInformix() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		   dataSource.setDriverClassName(CommonUtils.getProperty(CapConfigConstants.INFORMIX_DRIVER_CLASS));
	        dataSource.setUrl(CommonUtils.getProperty(CapConfigConstants.INFORMIX_URL));
	        dataSource.setUsername( CommonUtils.getProperty(CapConfigConstants.INFORMIX_USERNAME));
	        dataSource.setPassword(CommonUtils.getProperty(CapConfigConstants.INFORMIX_PASSWORD));
	        return dataSource;
	}
	
	@Bean(name = "informixJdbcTemplate")
	public NamedParameterJdbcTemplate jdbcTemplate(@Qualifier("informixDb") DataSource dsMySQL) {
		return new NamedParameterJdbcTemplate(dsMySQL);
	}
	
	
	@Bean(name ="oracleDb")
	public DataSource dataSourceOracle() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		   dataSource.setDriverClassName(CommonUtils.getProperty(CapConfigConstants.ORACLE_DRIVER_CLASS));
	        dataSource.setUrl(CommonUtils.getProperty(CapConfigConstants.ORACLE_URL));
	        dataSource.setUsername( CommonUtils.getProperty(CapConfigConstants.ORACLE_USERNAME));
	        dataSource.setPassword(CommonUtils.getProperty(CapConfigConstants.ORACLE_PASSWORD));
	        return dataSource;
	}
	
	@Bean(name = "oracleJdbcTemplate")
	public NamedParameterJdbcTemplate oracleJdbcTemplate(@Qualifier("oracleDb") DataSource dsMySQL) {
		return new NamedParameterJdbcTemplate(dsMySQL);
	}
	
	
	
}
