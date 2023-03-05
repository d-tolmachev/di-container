package team.zavod.di.config;

public class ValueHolder {
  private Object value;
  private String type;
  private String name;

  public ValueHolder(Object value, String type, String name) {
    this.value = value;
    this.type = type;
    this.name = name;
  }

  public Object getValue() {
    return this.value;
  }

  public void setValue(Object value) {
    this.value = value;
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
}
