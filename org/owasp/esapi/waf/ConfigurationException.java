package org.owasp.esapi.waf;

import org.owasp.esapi.errors.EnterpriseSecurityException;

public class ConfigurationException extends EnterpriseSecurityException
{
    public ConfigurationException(final String userMsg, final String logMsg) {
        super(userMsg, logMsg);
    }
    
    public ConfigurationException(final String userMsg, final String logMsg, final Throwable t) {
        super(userMsg, logMsg, t);
    }
}
