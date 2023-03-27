package team.zavod.di.config.dependency;

import java.util.List;
import java.util.Map;

public class MethodArgumentValues extends ArgumentValues {
  private final String methodName;

  public MethodArgumentValues(String methodName) {
    super();
    this.methodName = methodName;
  }

  public MethodArgumentValues(String methodName, List<ValueHolder> genericValues) {
    super(genericValues);
    this.methodName = methodName;
  }

  public MethodArgumentValues(String methodName, Map<Integer, ValueHolder> indexedValues) {
    super(indexedValues);
    this.methodName = methodName;
  }

  public MethodArgumentValues(String methodName, List<ValueHolder> genericValues, Map<Integer, ValueHolder> indexedValues) {
    super(genericValues, indexedValues);
    this.methodName = methodName;
  }

  public String getMethodName() {
    return this.methodName;
  }
}
