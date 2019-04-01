package api.modules;

import io.javalin.Javalin;

@dagger.Component(modules = {ServerModule.class})
public interface Application {
  Javalin server();
}
