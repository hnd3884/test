package javax.inject;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

@Scope
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Singleton {
}
