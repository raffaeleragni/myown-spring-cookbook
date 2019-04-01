package app;

import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class QueueReceiver {

  @SqsListener(value = "queue", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
  @SendTo("queue-out")
  public String process(String input, @Header("SenderId") String senderId) {
    return Mono.just(input)
        .map(this::event)
        .block();
  }

  String event(String input) {
    System.out.println(input);
    String output = "Response";
    return output;
  }

  @MessageExceptionHandler(RuntimeException.class)
  @SendTo("queue-err")
  public String exceptionHandler(Exception e) {
    return e.getMessage();
  }

}
