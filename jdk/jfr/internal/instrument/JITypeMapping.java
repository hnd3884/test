package jdk.jfr.internal.instrument;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@interface JITypeMapping {
    String from();
    
    String to();
}
