package api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureWebTestClient
public class ApplicationTest {
  @Autowired
  private WebTestClient client;

  @Test
  public void testUnauthorized() throws Exception {
    client.get()
        .uri("/auth/protected")
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  public void testTest() throws Exception {
    client.get()
        .uri("/test/a")
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .exchange()
        .expectStatus().isOk()
        .expectBody().json("{\"value\":\"a\"}");
  }

  @Test
  public void testTestStream() throws Exception {
    client.get()
        .uri("/test/")
        .accept(MediaType.parseMediaType("application/stream+json"))
        .exchange()
        .expectStatus().isOk()
        .expectBody();
  }
}
