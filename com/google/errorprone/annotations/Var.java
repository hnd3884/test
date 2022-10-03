package com.google.errorprone.annotations;

import javax.lang.model.element.Modifier;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE })
@Retention(RetentionPolicy.RUNTIME)
@IncompatibleModifiers({ Modifier.FINAL })
public @interface Var {
}
