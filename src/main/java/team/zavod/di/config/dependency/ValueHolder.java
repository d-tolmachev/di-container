package team.zavod.di.config.dependency;

import team.zavod.di.factory.ObjectProvider;

public class ValueHolder {
  private ObjectProvider<?> valueProvider;
  private String type;
  private String name;
  private String beanName;

  public ValueHolder() {
  }

  public ValueHolder(ObjectProvider<?> valueProvider, String type, String name, String beanName) {
    this.valueProvider = valueProvider;
    this.type = type;
    this.name = name;
    this.beanName = beanName;
  }

  public ObjectProvider<?> getValueProvider() {
    return this.valueProvider;
  }

  public void setValueProvider(ObjectProvider<?> valueProvider) {
    this.valueProvider = valueProvider;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBeanName() {
    return this.beanName;
  }

  public void setBeanName(String beanName) {
    this.beanName = beanName;
  }
}
