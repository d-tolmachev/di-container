package team.zavod.di.config.dependency;

import java.util.List;
import java.util.Map;

public class ConstructorArgumentValues extends ArgumentValues {
  public ConstructorArgumentValues() {
    super();
  }

  public ConstructorArgumentValues(List<ValueHolder> genericValues) {
    super(genericValues);
  }

  public ConstructorArgumentValues(Map<Integer, ValueHolder> indexedValues) {
    super(indexedValues);
  }

  public ConstructorArgumentValues(List<ValueHolder> genericValues, Map<Integer, ValueHolder> indexedValues) {
    super(genericValues, indexedValues);
  }
}
