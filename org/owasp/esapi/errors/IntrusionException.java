package org.owasp.esapi.errors;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;

public class IntrusionException extends EnterpriseSecurityRuntimeException
{
    private static final long serialVersionUID = 1L;
    protected final transient Logger logger;
    protected String logMessage;
    
    public IntrusionException(final String userMessage, final String logMessage) {
        super(userMessage);
        this.logger = ESAPI.getLogger("IntrusionException");
        this.logMessage = null;
        this.logMessage = logMessage;
        this.logger.error(Logger.SECURITY_FAILURE, "INTRUSION - " + logMessage);
    }
    
    public IntrusionException(final String userMessage, final String logMessage, final Throwable cause) {
        super(userMessage, cause);
        this.logger = ESAPI.getLogger("IntrusionException");
        this.logMessage = null;
        this.logMessage = logMessage;
        this.logger.error(Logger.SECURITY_FAILURE, "INTRUSION - " + logMessage, cause);
    }
    
    @Override
    public String getUserMessage() {
        return this.getMessage();
    }
    
    @Override
    public String getLogMessage() {
        return this.logMessage;
    }
}
