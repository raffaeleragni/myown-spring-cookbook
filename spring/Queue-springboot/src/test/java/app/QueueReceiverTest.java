package app;

import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class QueueReceiverTest {

  @Autowired QueueMessagingTemplate queue;
  @Autowired QueueReceiver receiver;

  @Test
  public void testQueue() {
    queue.convertAndSend("queue", "hello world");
  }

  @Test
  public void testReceiver() {
    receiver.process("receiver message", UUID.randomUUID().toString());
  }

}
