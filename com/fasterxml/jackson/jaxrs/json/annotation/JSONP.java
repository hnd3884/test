package com.fasterxml.jackson.jaxrs.json.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JSONP {
    String value() default "";
    
    String prefix() default "";
    
    String suffix() default "";
    
    public static class Def
    {
        public final String method;
        public final String prefix;
        public final String suffix;
        
        public Def(final String m) {
            this.method = m;
            this.prefix = null;
            this.suffix = null;
        }
        
        public Def(final JSONP json) {
            this.method = emptyAsNull(json.value());
            this.prefix = emptyAsNull(json.prefix());
            this.suffix = emptyAsNull(json.suffix());
        }
        
        private static final String emptyAsNull(final String str) {
            if (str == null || str.length() == 0) {
                return null;
            }
            return str;
        }
    }
}
