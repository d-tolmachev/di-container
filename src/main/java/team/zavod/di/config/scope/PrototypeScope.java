package team.zavod.di.config.scope;

import team.zavod.di.factory.ObjectProvider;

public class PrototypeScope implements Scope {
  private static final String SCOPE_NAME = "prototype";

  public PrototypeScope() {
  }

  @Override
  public Object get(String beanName, Class<?> beanType, ObjectProvider<?> beanProvider) {
    return beanProvider.getObject();
  }

  public static String getName() {
    return SCOPE_NAME;
  }
}
