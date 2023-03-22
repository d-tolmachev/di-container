package team.zavod.di.config;

import java.util.ArrayList;
import java.util.List;

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

  public void addPropertyValue(Object value, String type, String name) {
    addPropertyValue(new ValueHolder(value, type, name));
  }

  public void addPropertyValue(ValueHolder newValue) {
    this.propertyValues.add(newValue);
  }

  public void removePropertyValue(String name) {
    this.propertyValues.removeIf(value -> value.getName().equals(name));
  }

  public ValueHolder getPropertyValue(String requiredType, String requiredName) {
    return this.propertyValues.stream()
        .filter(value -> value.getType().equals(requiredType) && value.getName().equals(requiredName))
        .findFirst().orElse(null);
  }

  public List<ValueHolder> getPropertyValues() {
    return this.propertyValues;
  }

  public boolean isEmpty() {
    return this.propertyValues.isEmpty();
  }

  public void clear() {
    this.propertyValues.clear();
  }
}
