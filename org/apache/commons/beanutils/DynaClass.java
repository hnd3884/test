package org.apache.commons.beanutils;

public interface DynaClass
{
    String getName();
    
    DynaProperty getDynaProperty(final String p0);
    
    DynaProperty[] getDynaProperties();
    
    DynaBean newInstance() throws IllegalAccessException, InstantiationException;
}
