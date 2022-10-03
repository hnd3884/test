package org.owasp.esapi.errors;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;

public class EnterpriseSecurityException extends Exception
{
    protected static final long serialVersionUID = 1L;
    protected final transient Logger logger;
    protected String logMessage;
    
    protected EnterpriseSecurityException() {
        this.logger = ESAPI.getLogger("EnterpriseSecurityException");
        this.logMessage = null;
    }
    
    protected EnterpriseSecurityException(final String userMessage) {
        super(userMessage);
        this.logger = ESAPI.getLogger("EnterpriseSecurityException");
        this.logMessage = null;
    }
    
    protected EnterpriseSecurityException(final String userMessage, final Throwable cause) {
        super(userMessage, cause);
        this.logger = ESAPI.getLogger("EnterpriseSecurityException");
        this.logMessage = null;
    }
    
    public EnterpriseSecurityException(final String userMessage, final String logMessage) {
        super(userMessage);
        this.logger = ESAPI.getLogger("EnterpriseSecurityException");
        this.logMessage = null;
        this.logMessage = logMessage;
        if (!ESAPI.securityConfiguration().getDisableIntrusionDetection()) {
            ESAPI.intrusionDetector().addException(this);
        }
    }
    
    public EnterpriseSecurityException(final String userMessage, final String logMessage, final Throwable cause) {
        super(userMessage, cause);
        this.logger = ESAPI.getLogger("EnterpriseSecurityException");
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
