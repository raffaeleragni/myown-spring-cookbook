package api.modules;

import io.javalin.Javalin;
import org.junit.Test;

public class ServerTest {
  @Test
  public void test() {
    Javalin app = DaggerApplication.create().server();
  }
}
