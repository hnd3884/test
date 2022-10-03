package org.apache.commons.beanutils;

import java.beans.IntrospectionException;

public interface BeanIntrospector
{
    void introspect(final IntrospectionContext p0) throws IntrospectionException;
}
