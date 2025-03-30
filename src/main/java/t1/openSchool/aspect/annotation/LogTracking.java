package t1.openSchool.aspect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target (ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogTracking {
}