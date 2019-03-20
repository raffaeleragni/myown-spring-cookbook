package api;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleResource {

  @RequestMapping(method = GET, value = "/test/{a}")
  public Map<String, String> test(@PathVariable("a") String a) {
    return new HashMap<>(){{ put("value", a); }};
  }
}
