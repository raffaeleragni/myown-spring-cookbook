package api.auth;

import api.Application;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import java.security.Key;
import java.util.Date;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureWebTestClient
public class TokenAuthEndpointsTest {

  @Autowired
  private WebTestClient client;
  @Autowired
  private JWTSecurity provider;

  @Test
  public void testToken() throws Exception {
    String token = provider.createToken("user").block();
    client.get()
        .uri("/auth/protected")
        .header("Authorization", "Bearer " + token)
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class).isEqualTo("secret");
  }

  @Test
  public void testBadToken() throws Exception {
    client.get()
        .uri("/auth/protected")
        .header("Authorization", "Bearer blah")
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  public void testInvalidToken() throws Exception {
    // Use a different secret so the token will be a formatted JWT, but not valid for this api.
    // default is 'secret' in the class
    JWTSecurity differentProvider = new JWTSecurity();
    ReflectionTestUtils.setField(differentProvider, "userDetailsService",
        ReflectionTestUtils.getField(provider, "userDetailsService"));
    differentProvider.init();
    String token = differentProvider.createToken("user").block();
    client.get()
        .uri("/auth/protected")
        .header("Authorization", "Bearer " + token)
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  public void testExpiredToken() throws Exception {
    Claims claims = Jwts.claims().setSubject("user");
    claims.put("roles", "admin");
    Date now = new Date();
    now = new Date(now.getTime() - 3600_000 * 2);
    Date validity = new Date(now.getTime() + 3600_000);
    String token = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith((Key) ReflectionTestUtils.getField(provider, "key"), HS256)
        .compact();
    client.get()
        .uri("/auth/protected")
        .header("Authorization", "Bearer " + token)
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .exchange()
        .expectStatus().isUnauthorized();
  }

}
