package api.proto;

import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

@Immutable
public interface Message {

  long messageId();

  @Default
  default long correlationId() {return 0;}

  String messageBody();

}
