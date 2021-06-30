package net.kittenpla.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

@Getter
@Setter
public class TokenGroup implements Serializable {
  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("refresh_token")
  private String refreshToken;

  @JsonProperty("expire_time")
  private long expireTime;

  @JsonIgnore private String email;

  public TokenGroup() {}

  public TokenGroup(String accessToken, String refreshToken, String email) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    expireTime = DateTime.now().plusDays(30).getMillis();
    this.email = email;
  }

  @JsonIgnore
  public boolean isExpired() {
    return expireTime < DateTime.now().getMillis();
  }

  public void postponeExpiry() {
    expireTime = new DateTime().plusDays(30).getMillis();
  }

  @Override
  public String toString() {
    return "TokenGroup{" +
        "accessToken='" + accessToken + '\'' +
        ", refreshToken='" + refreshToken + '\'' +
        ", expireTime=" + expireTime +
        ", email='" + email + '\'' +
        '}';
  }
}
