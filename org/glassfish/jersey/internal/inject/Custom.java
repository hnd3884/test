package org.glassfish.jersey.internal.inject;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import javax.inject.Qualifier;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Custom {
}
