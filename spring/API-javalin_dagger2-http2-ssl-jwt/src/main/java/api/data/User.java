package api.data;

import org.immutables.value.Value;

@Value.Immutable
public interface User {
  String name();
}
