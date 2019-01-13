package com.cs.ordermanagement;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableAutoConfiguration
@EnableSwagger2
@PropertySource("classpath:application.properties")
@Import({springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration.class})
public class OrdermanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdermanagementApplication.class, args);
	}
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer configs() {
	        return new PropertySourcesPlaceholderConfigurer();
	    }
	
	@Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:ordermanagement;MVCC=TRUE;DB_CLOSE_DELAY=-1;DEFAULT_LOCK_TIMEOUT=100000");
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");
        Properties properties = new Properties();
        properties.setProperty("spring.datasource.connectionTimeout", "200000");
        properties.setProperty("pring.datasource.max-active","500");
        properties.setProperty(" spring.jpa.properties.hibernate.c3p0.timeout","500000");
        dataSource.setConnectionProperties(properties);
       
 
        return dataSource;
    }
	

	
	
}



