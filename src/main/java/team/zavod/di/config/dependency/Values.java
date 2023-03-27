package team.zavod.di.config.dependency;

import java.util.ArrayList;
import java.util.List;
import team.zavod.di.factory.ObjectProvider;

public abstract class Values {
  private final List<ValueHolder> genericValues;

  public Values() {
    this(new ArrayList<>());
  }

  public Values(List<ValueHolder> genericValues) {
    this.genericValues = genericValues;
  }

  public boolean hasGenericValue(String name) {
    return this.genericValues.stream().anyMatch(value -> value.getName().equals(name));
  }

  public void addGenericValue(ObjectProvider<?> valueProvider, String type, String name, String beanName) {
    addGenericValue(new ValueHolder(valueProvider, type, name, beanName));
  }

  public void addGenericValue(ValueHolder newValue) {
    this.genericValues.add(newValue);
  }

  public void removeGenericValue(String name) {
    this.genericValues.removeIf(value -> value.getName().equals(name));
  }

  public ValueHolder getGenericValue(String name) {
    return this.genericValues.stream()
        .filter(value -> value.getName().equals(name))
        .findFirst().orElse(null);
  }

  public List<ValueHolder> getGenericValues() {
    return this.genericValues;
  }

  public boolean isEmpty() {
    return this.genericValues.isEmpty();
  }

  public int size() {
    return this.genericValues.size();
  }

  public void clear() {
    this.genericValues.clear();
  }
}
