package net.kittenpla.tokenbatch.redis;

import javax.annotation.Resource;
import net.kittenpla.pojo.TokenGroup;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;

@Component
public class RedisComponent {

  @Resource(name = "redisTemplate")
  private HashOperations<String, String, TokenGroup> redisHash;

  public TokenGroup get(String key, String hashKey) {
    TokenGroup tokenGroup = redisHash.get(key, hashKey);
    return tokenGroup;
  }

  public void save(TokenGroup token, String tokenType, String tokenValue) {
    redisHash.put(tokenValue, tokenType, token);
  }
}
