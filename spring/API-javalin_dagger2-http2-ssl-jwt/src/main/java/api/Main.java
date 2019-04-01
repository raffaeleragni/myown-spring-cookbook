package api;

import api.modules.DaggerApplication;

public class Main {

  public static void main(String[] args) {
    DaggerApplication.create().server().start();
  }

}
