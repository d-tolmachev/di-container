package team.zavod.di.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import team.zavod.di.config.DefaultScopes;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
  String name() default "";

  boolean lazyInit() default true;

  DefaultScopes scope() default DefaultScopes.SINGLETON;
}
