package org.glassfish.hk2.api;

public interface HK2Loader
{
    Class<?> loadClass(final String p0) throws MultiException;
}
