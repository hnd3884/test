package com.google.errorprone.annotations.concurrent;

import javax.lang.model.element.Modifier;
import com.google.errorprone.annotations.IncompatibleModifiers;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@IncompatibleModifiers({ Modifier.FINAL })
public @interface LazyInit {
}
