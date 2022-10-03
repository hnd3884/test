package org.owasp.esapi.errors;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;

public class EnterpriseSecurityRuntimeException extends RuntimeException
{
    protected static final long serialVersionUID = 1L;
    protected final Logger logger;
    protected String logMessage;
    
    protected EnterpriseSecurityRuntimeException() {
        this.logger = ESAPI.getLogger(this.getClass());
        this.logMessage = null;
    }
    
    protected EnterpriseSecurityRuntimeException(final String userMessage) {
        super(userMessage);
        this.logger = ESAPI.getLogger(this.getClass());
        this.logMessage = null;
    }
    
    protected EnterpriseSecurityRuntimeException(final String userMessage, final Throwable cause) {
        super(userMessage, cause);
        this.logger = ESAPI.getLogger(this.getClass());
        this.logMessage = null;
    }
    
    public EnterpriseSecurityRuntimeException(final String userMessage, final String logMessage) {
        super(userMessage);
        this.logger = ESAPI.getLogger(this.getClass());
        this.logMessage = null;
        this.logMessage = logMessage;
        if (!ESAPI.securityConfiguration().getDisableIntrusionDetection()) {
            ESAPI.intrusionDetector().addException(this);
        }
    }
    
    public EnterpriseSecurityRuntimeException(final String userMessage, final String logMessage, final Throwable cause) {
        super(userMessage, cause);
        this.logger = ESAPI.getLogger(this.getClass());
        this.logMessage = null;
        this.logMessage = logMessage;
        if (!ESAPI.securityConfiguration().getDisableIntrusionDetection()) {
            ESAPI.intrusionDetector().addException(this);
        }
    }
    
    public String getUserMessage() {
        return this.getMessage();
    }
    
    public String getLogMessage() {
        return this.logMessage;
    }
}
