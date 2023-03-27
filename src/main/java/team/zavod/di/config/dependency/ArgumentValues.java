package team.zavod.di.config.dependency;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import team.zavod.di.factory.ObjectProvider;

public abstract class ArgumentValues extends Values {
  private final Map<Integer, ValueHolder> indexedValues;

  public ArgumentValues() {
    this(new HashMap<>());
  }

  public ArgumentValues(List<ValueHolder> genericValues) {
    this(genericValues, new HashMap<>());
  }

  public ArgumentValues(Map<Integer, ValueHolder> indexedValues) {
    super();
    this.indexedValues = indexedValues;
  }

  public ArgumentValues(List<ValueHolder> genericValues, Map<Integer, ValueHolder> indexedValues) {
    super(genericValues);
    this.indexedValues = indexedValues;
  }

  public boolean hasIndexedValue(int index) {
    return this.indexedValues.containsKey(index);
  }

  public void addIndexedValue(int index, ObjectProvider<?> valueProvider, String type, String beanName) {
    addIndexedValue(index, new ValueHolder(valueProvider, type, null, beanName));
  }

  public void addIndexedValue(int index, ValueHolder newValue) {
    this.indexedValues.put(index, newValue);
  }

  public void removeIndexedValue(int index) {
    this.indexedValues.remove(index);
  }

  public ValueHolder getIndexedValue(int index) {
    return this.indexedValues.get(index);
  }

  public Map<Integer, ValueHolder> getIndexedValues() {
    return this.indexedValues;
  }

  @Override
  public boolean isEmpty() {
    return super.isEmpty() && this.indexedValues.isEmpty();
  }

  @Override
  public int size() {
    return super.size() + this.indexedValues.size();
  }

  @Override
  public void clear() {
    super.clear();
    this.indexedValues.clear();
  }
}
