package org.apache.catalina;

public interface Contained
{
    Container getContainer();
    
    void setContainer(final Container p0);
}
