package org.aopalliance.reflect;

public interface Field extends Member
{
    CodeLocator getReadLocator();
    
    CodeLocator getReadLocator(final int p0);
    
    CodeLocator getWriteLocator();
    
    CodeLocator getWriteLocator(final int p0);
}
