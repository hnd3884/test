package org.glassfish.jersey.message.filtering.spi;

import org.glassfish.jersey.spi.Contract;

@Contract
public interface EntityProcessor
{
    Result process(final EntityProcessorContext p0);
    
    public enum Result
    {
        APPLY, 
        SKIP, 
        ROLLBACK;
    }
}
