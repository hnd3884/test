package org.glassfish.jersey.process;

public interface Inflector<DATA, RESULT>
{
    RESULT apply(final DATA p0);
}
