package team.zavod.di.config.dependency;

import java.util.ArrayList;
import java.util.List;
import team.zavod.di.factory.ObjectProvider;

public class PropertyValues {
  private final List<ValueHolder> propertyValues;

  public PropertyValues() {
    this.propertyValues = new ArrayList<>();
  }

  public PropertyValues(List<ValueHolder> propertyValues) {
    this.propertyValues = propertyValues;
  }

  public boolean hasPropertyValue(String name) {
    return this.propertyValues.stream().anyMatch(value -> value.getName().equals(name));
  }

  public void addPropertyValue(ObjectProvider<?> valueProvider, String type, String name, String beanName) {
    addPropertyValue(new ValueHolder(valueProvider, type, name, beanName));
  }

  public void addPropertyValue(ValueHolder newValue) {
    this.propertyValues.add(newValue);
  }

  public void removePropertyValue(String name) {
    this.propertyValues.removeIf(value -> value.getName().equals(name));
  }

  public ValueHolder getPropertyValue(String name) {
    return this.propertyValues.stream()
        .filter(value -> value.getName().equals(name))
        .findFirst().orElse(null);
  }

  public List<ValueHolder> getPropertyValues() {
    return this.propertyValues;
  }

  public boolean isEmpty() {
    return this.propertyValues.isEmpty();
  }

  public int size() {
    return this.propertyValues.size();
  }

  public void clear() {
    this.propertyValues.clear();
  }
}
