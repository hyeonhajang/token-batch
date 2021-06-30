package net.kittenpla.tokenbatch.job;

import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kittenpla.pojo.TokenGroup;
import net.kittenpla.tokenbatch.redis.RedisComponent;
import net.smartam.leeloo.common.OAuth;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class ExpireTokenJobConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final RedisComponent redisComponent;

  private static Set<String> supportTokenTypes = new HashSet<>();

  static {
    supportTokenTypes.add(OAuth.OAUTH_REFRESH_TOKEN);
    supportTokenTypes.add(OAuth.OAUTH_ACCESS_TOKEN);
  }

  @Bean
  public Job expireTokenJob() {
    return jobBuilderFactory.get("expireTokenJob")
        .start(expireTokenStep(null, null))
        .build();
  }

  /*
    TODO 1.tokenType accessToken 지원 추가
    TODO 2.클래스 중복 금지(변경 적용안되므로 BrushBackend로부터 jar import
   */

  @Bean
  @JobScope
  public Step expireTokenStep(
      @Value("#{jobParameters[tokenType]}") String tokenType,
      @Value("#{jobParameters[token]}") String token
  ) {
    return stepBuilderFactory.get("expireTokenStep")
        .tasklet((contribution, chunkContext) -> {
          if (!supportTokenTypes.contains(tokenType)) {
            throw new RuntimeException("not supported tokenType");
          }

          TokenGroup refreshToken = redisComponent.get(token, tokenType);
          TokenGroup accessToken = redisComponent.get(refreshToken.getAccessToken(), OAuth.OAUTH_ACCESS_TOKEN);

          log.info("current token information");
          log.info("refresh token : {}", refreshToken.toString());
          log.info("access token : {}", accessToken.toString());

          long now = System.currentTimeMillis();

          refreshToken.setExpireTime(now);
          accessToken.setExpireTime(now);

          redisComponent.save(refreshToken, OAuth.OAUTH_REFRESH_TOKEN, refreshToken.getRefreshToken());
          redisComponent.save(accessToken, OAuth.OAUTH_ACCESS_TOKEN, refreshToken.getAccessToken());

          TokenGroup updatedRefreshToken = redisComponent.get(token, tokenType);
          TokenGroup updatedAccessToken = redisComponent.get(refreshToken.getAccessToken(), OAuth.OAUTH_ACCESS_TOKEN);

          log.info("after change token information");
          log.info("refresh token : {}", updatedRefreshToken.toString());
          log.info("access token : {}", updatedAccessToken.toString());

          return RepeatStatus.FINISHED;
        }).build();
  }
}
