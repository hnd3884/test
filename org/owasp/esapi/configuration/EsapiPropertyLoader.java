package org.owasp.esapi.configuration;

import org.owasp.esapi.errors.ConfigurationException;

public interface EsapiPropertyLoader
{
    int getIntProp(final String p0) throws ConfigurationException;
    
    byte[] getByteArrayProp(final String p0) throws ConfigurationException;
    
    Boolean getBooleanProp(final String p0) throws ConfigurationException;
    
    String getStringProp(final String p0) throws ConfigurationException;
}
