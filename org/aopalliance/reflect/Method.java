package org.aopalliance.reflect;

public interface Method extends Member
{
    CodeLocator getCallLocator();
    
    CodeLocator getCallLocator(final int p0);
    
    Code getBody();
}
