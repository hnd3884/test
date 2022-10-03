package org.apache.commons.beanutils;

public interface Converter
{
     <T> T convert(final Class<T> p0, final Object p1);
}
