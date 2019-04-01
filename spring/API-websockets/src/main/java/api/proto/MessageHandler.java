package api.proto;

import java.util.Optional;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessageHandler {

  @MessageMapping("/message")
  @SendTo("/response")
  public String process(String input) {
    System.out.println("Received: "+input);
    return Optional
        .ofNullable(input)
        .filter(s -> !s.isEmpty())
        .map(s -> s + " Received.")
        .orElse("No message");
  }

}
