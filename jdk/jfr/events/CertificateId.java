package jdk.jfr.events;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import jdk.jfr.Relational;
import jdk.jfr.Label;

@Label("X509 Certificate Id")
@Relational
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CertificateId {
}
