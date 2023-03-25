package team.zavod.di.config.dependency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import team.zavod.di.factory.ObjectProvider;

public class ConstructorArgumentValues {
  private final Map<Integer, ValueHolder> indexedArgumentValues;
  private final List<ValueHolder> genericArgumentValues;

  public ConstructorArgumentValues() {
    this.indexedArgumentValues = new HashMap<>();
    this.genericArgumentValues = new ArrayList<>();
  }

  public ConstructorArgumentValues(Map<Integer, ValueHolder> indexedArgumentValues) {
    this.indexedArgumentValues = indexedArgumentValues;
    this.genericArgumentValues = new ArrayList<>();
  }

  public ConstructorArgumentValues(List<ValueHolder> genericArgumentValues) {
    this.indexedArgumentValues = new HashMap<>();
    this.genericArgumentValues = genericArgumentValues;
  }

  public ConstructorArgumentValues(Map<Integer, ValueHolder> indexedArgumentValues, List<ValueHolder> genericArgumentValues) {
    this.indexedArgumentValues = indexedArgumentValues;
    this.genericArgumentValues = genericArgumentValues;
  }

  public boolean hasIndexedArgumentValue(int index) {
    return this.indexedArgumentValues.containsKey(index);
  }

  public void addIndexedArgumentValue(int index, ObjectProvider<?> valueProvider, String type, String beanName) {
    addIndexedArgumentValue(index, new ValueHolder(valueProvider, type, null, beanName));
  }

  public void addIndexedArgumentValue(int index, ValueHolder newValue) {
    this.indexedArgumentValues.put(index, newValue);
  }

  public void removeIndexedArgumentValue(int index) {
    this.indexedArgumentValues.remove(index);
  }

  public ValueHolder getIndexedArgumentValue(int index) {
    return this.indexedArgumentValues.get(index);
  }

  public Map<Integer, ValueHolder> getIndexedArgumentValues() {
    return this.indexedArgumentValues;
  }

  public boolean hasGenericArgumentValue(String name) {
    return this.genericArgumentValues.stream().anyMatch(value -> value.getName().equals(name));
  }

  public void addGenericArgumentValue(ObjectProvider<?> valueProvider, String type, String name, String beanName) {
    addGenericArgumentValue(new ValueHolder(valueProvider, type, name, beanName));
  }

  public void addGenericArgumentValue(ValueHolder newValue) {
    this.genericArgumentValues.add(newValue);
  }

  public void removeGenericArgumentValue(String name) {
    this.genericArgumentValues.removeIf(value -> value.getName().equals(name));
  }

  public ValueHolder getGenericArgumentValue(String name) {
    return this.genericArgumentValues.stream()
        .filter(value -> value.getName().equals(name))
        .findFirst().orElse(null);
  }

  public List<ValueHolder> getGenericArgumentValues() {
    return this.genericArgumentValues;
  }

  public ValueHolder getArgumentValue(int index, String name) {
    return hasIndexedArgumentValue(index) ? getIndexedArgumentValue(index) : getGenericArgumentValue(name);
  }

  public boolean isEmpty() {
    return this.indexedArgumentValues.isEmpty() && this.genericArgumentValues.isEmpty();
  }

  public int size() {
    return this.indexedArgumentValues.size() + this.genericArgumentValues.size();
  }

  public void clear() {
    this.indexedArgumentValues.clear();
    this.genericArgumentValues.clear();
  }
}
