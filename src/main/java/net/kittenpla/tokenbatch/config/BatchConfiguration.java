package net.kittenpla.tokenbatch.config;

import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class BatchConfiguration extends DefaultBatchConfigurer {

  @Override
  public void setDataSource(DataSource dataSource) {
    // 수행하지 않음. RDB 작업을 하지 않으므로
  }
}
