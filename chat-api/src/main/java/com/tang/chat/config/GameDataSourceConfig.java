package com.tang.chat.config;

import com.tang.core.domain.Participant;
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
    basePackages = {"com.tang.core.repository"},
    entityManagerFactoryRef = "gameEntityManager",
    transactionManagerRef = "gameTransactionManager"
)
public class GameDataSourceConfig {
  @Bean
  @ConfigurationProperties("spring.datasource.game")
  public DataSourceProperties gameDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  public DataSource gameDataSource() {
    return gameDataSourceProperties()
        .initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  @Bean(name = "gameEntityManager")
  public LocalContainerEntityManagerFactoryBean gameEntityManager(
      EntityManagerFactoryBuilder builder
  ) {
    return builder.dataSource(gameDataSource())
        .packages(new Class[]{
            Participant.class
        })
        .build();
  }

  @Bean(name = "gameTransactionManager")
  public PlatformTransactionManager gameTransactionManager(
      @Qualifier("gameEntityManager")
      LocalContainerEntityManagerFactoryBean entityManagerFactoryBean
  ) {
    return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactoryBean.getObject()));
  }
}
