package org.glassfish.jersey.servlet.spi;

public interface AsyncContextDelegate
{
    void suspend() throws IllegalStateException;
    
    void complete();
}
