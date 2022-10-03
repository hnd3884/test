package org.glassfish.hk2.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.inject.Scope;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Scope
@Unproxiable
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface PerLookup {
}
