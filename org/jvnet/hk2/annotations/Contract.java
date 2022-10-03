package org.jvnet.hk2.annotations;

import org.glassfish.hk2.api.ContractIndicator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@ContractIndicator
public @interface Contract {
}
