package org.glassfish.jersey.message.filtering.spi;

import java.util.Set;
import java.lang.annotation.Annotation;
import org.glassfish.jersey.spi.Contract;

@Contract
public interface ScopeResolver
{
    Set<String> resolve(final Annotation[] p0);
}
