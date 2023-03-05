package team.zavod.di.context;

public interface LifeCycle {
  void start();

  void stop();

  boolean isRunning();
}
