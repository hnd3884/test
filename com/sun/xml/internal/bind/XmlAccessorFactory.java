package com.sun.xml.internal.bind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.PACKAGE })
public @interface XmlAccessorFactory {
    Class<? extends AccessorFactory> value();
}
