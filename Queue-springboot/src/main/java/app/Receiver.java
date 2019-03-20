package app;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
  @JmsListener(destination = "receive")
  public void receive(String message) {
    System.out.println(message);
  }
}
