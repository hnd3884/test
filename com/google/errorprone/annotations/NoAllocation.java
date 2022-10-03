package com.google.errorprone.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

@Documented
@Target({ ElementType.METHOD })
public @interface NoAllocation {
}
