package com.cs.ordermanagement.config;

public class PersistenceJPAConfig{/*
 
   @Bean
   public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
      LocalContainerEntityManagerFactoryBean em 
        = new LocalContainerEntityManagerFactoryBean();
      em.setDataSource(dataSource());
      em.setPackagesToScan(new String[] { "com.cs.ordermanagement.domain" });
 
   //   JpaVendorAdapter vendorAdapter = new H2A();
     // em.setJpaVendorAdapter(vendorAdapter);
      //em.setJpaProperties(additionalProperties());
 
      return em;
   }
 
   @Bean
   @ConfigurationProperties("app.datasource")
   public DataSource dataSource() {
   	return DataSourceBuilder.create().build();
   }
 
   @Bean
   public PlatformTransactionManager transactionManager(
     EntityManagerFactory emf){
       JpaTransactionManager transactionManager = new JpaTransactionManager();
       transactionManager.setEntityManagerFactory(emf);
 
       return transactionManager;
   }
 
   @Bean
   public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
       return new PersistenceExceptionTranslationPostProcessor();
   }
 
   Properties additionalProperties() {
       Properties properties = new Properties();
       properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
       properties.setProperty(
         "hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        
       return properties;
   }
*/}
