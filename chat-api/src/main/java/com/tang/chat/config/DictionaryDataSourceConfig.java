package com.tang.chat.config;

import com.tang.chat.domain.Dictionary;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Objects;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.tang.chat.repository.dictionary",
    entityManagerFactoryRef = "chatEntityManager",
    transactionManagerRef = "chatTransactionManager"
)
public class DictionaryDataSourceConfig {
  @Bean
  @ConfigurationProperties("spring.datasource.dictionary")
  public DataSourceProperties dictionaryDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  public DataSource dictionaryDataSource() {
    return dictionaryDataSourceProperties()
        .initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  @Bean(name = "dictionaryEntityManager")
  public LocalContainerEntityManagerFactoryBean dictionaryEntityManager(
      EntityManagerFactoryBuilder builder
  ) {
    return builder.dataSource(dictionaryDataSource()).packages(Dictionary.class)
        .build();
  }

  @Bean(name = "dictionaryTransactionManager")
  public PlatformTransactionManager dictionaryTransactionManager(
      @Qualifier("dictionaryEntityManager")
      LocalContainerEntityManagerFactoryBean entityManagerFactoryBean
  ) {
    return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactoryBean.getObject()));
  }
}
