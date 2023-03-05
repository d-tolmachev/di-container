package team.zavod.di.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  public void addIndexedArgumentValue(int index, Object value, String type) {
    addIndexedArgumentValue(index, new ValueHolder(value, type, null));
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

  public void addGenericArgumentValue(Object value, String type, String name) {
    addGenericArgumentValue(new ValueHolder(value, type, name));
  }

  public void addGenericArgumentValue(ValueHolder newValue) {
    this.genericArgumentValues.add(newValue);
  }

  public void removeGenericArgumentValue(String name) {
    this.genericArgumentValues.removeIf(value -> value.getName().equals(name));
  }

  public ValueHolder getGenericArgumentValue(Class<?> requiredType, String requiredName) {
    return this.genericArgumentValues.stream()
        .filter(value -> value.getType().equals(requiredType.getName()) && value.getName().equals(requiredName))
        .findFirst().orElse(null);
  }

  public List<ValueHolder> getGenericArgumentValues() {
    return this.genericArgumentValues;
  }

  public ValueHolder getArgumentValue(int index, Class<?> requiredType, String requiredName) {
    return hasIndexedArgumentValue(index) ? getIndexedArgumentValue(index) : getGenericArgumentValue(requiredType, requiredName);
  }

  public boolean isEmpty() {
    return this.indexedArgumentValues.isEmpty() && this.genericArgumentValues.isEmpty();
  }

  public void clear() {
    this.indexedArgumentValues.clear();
    this.genericArgumentValues.clear();
  }
}
