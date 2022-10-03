package org.apache.axiom.locator;

abstract class Loader
{
    abstract Class<?> load(final String p0) throws ClassNotFoundException;
}
