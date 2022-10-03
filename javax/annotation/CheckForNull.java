package javax.annotation;

import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import javax.annotation.meta.When;
import java.lang.annotation.Documented;

@Documented
@Nonnull(when = When.MAYBE)
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifierNickname
public @interface CheckForNull {
}