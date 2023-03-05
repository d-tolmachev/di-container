package team.zavod.di.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.ws.rs.ext.Provider;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Provider
public @interface Configuration {
  String value() default "";
}
