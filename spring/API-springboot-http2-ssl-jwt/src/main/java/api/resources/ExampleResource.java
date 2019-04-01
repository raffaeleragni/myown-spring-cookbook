package api.resources;

import static api.auth.UserDetailsServiceImpl.HAS_ANY_ROLE;
import java.util.HashMap;
import java.util.Map;
import org.davidmoten.rx.jdbc.Database;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static reactor.core.publisher.Mono.just;

@RestController
public class ExampleResource {

  @RequestMapping(method = GET, value = "flux")
  public Flux<String> flux() {
    Database db = Database.test();
    Flux<String> ff = Flux.from(db.select("values 1").get(rs -> rs.getString(1)));
    
    return ff;
  }
  
  @RequestMapping(method = GET, value = "/test/{a}")
  public Mono<Map<String, String>> test(@PathVariable("a") String a) {
    return just(new HashMap<>(){{ put("value", a); }});
  }

  @RequestMapping(method = GET, value = "/test/", produces = "application/stream+json")
  public Flux<Map<String, String>> testStream() {
    return Flux.just(
      new HashMap<>(){{ put("value", "1"); }},
      new HashMap<>(){{ put("value", "2"); }},
      new HashMap<>(){{ put("value", "3"); }},
      new HashMap<>(){{ put("value", "4"); }}
    );
  }

  @PreAuthorize(HAS_ANY_ROLE)
  @RequestMapping(method = GET, value = "/auth/protected")
  public Mono<String> test() {
    return just("secret");
  }
}
