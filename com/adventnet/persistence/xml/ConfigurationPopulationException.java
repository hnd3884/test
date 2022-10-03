package com.adventnet.persistence.xml;

public class ConfigurationPopulationException extends Exception
{
    public ConfigurationPopulationException(final Throwable exc) {
        super(exc);
    }
    
    public ConfigurationPopulationException(final String message) {
        super(message);
    }
    
    public ConfigurationPopulationException(final String message, final Throwable exc) {
        super(message, exc);
    }
}
