package org.owasp.esapi;

public interface LogFactory
{
    Logger getLogger(final String p0);
    
    Logger getLogger(final Class p0);
}
