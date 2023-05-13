package com.tang.chat.config;

import com.tang.chat.domain.Chat;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Objects;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.tang.chat.repository.chat",
    entityManagerFactoryRef = "chatEntityManager",
    transactionManagerRef = "chatTransactionManager"
)
public class ChatDataSourceConfig {
  @Bean
  @Primary
  @ConfigurationProperties("spring.datasource.chat")
  public DataSourceProperties chatDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @Primary
  public DataSource chatDataSource() {
    return chatDataSourceProperties()
        .initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  @Bean(name = "chatEntityManager")
  @Primary
  public LocalContainerEntityManagerFactoryBean chatEntityManager(
      EntityManagerFactoryBuilder builder
  ) {
    return builder.dataSource(chatDataSource()).packages(Chat.class)
        .build();
  }

  @Bean(name = "chatTransactionManager")
  @Primary
  public PlatformTransactionManager chatTransactionManager(
      @Qualifier("chatEntityManager")
      LocalContainerEntityManagerFactoryBean entityManagerFactoryBean
  ) {
    return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactoryBean.getObject()));
  }
}
