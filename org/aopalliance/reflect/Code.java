package org.aopalliance.reflect;

public interface Code
{
    CodeLocator getLocator();
    
    CodeLocator getCallLocator(final Method p0);
    
    CodeLocator getReadLocator(final Field p0);
    
    CodeLocator getWriteLocator(final Field p0);
    
    CodeLocator getThrowLocator(final Class p0);
    
    CodeLocator getCatchLocator(final Class p0);
}
