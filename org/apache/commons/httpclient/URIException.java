package org.apache.commons.httpclient;

public class URIException extends HttpException
{
    public static final int UNKNOWN = 0;
    public static final int PARSING = 1;
    public static final int UNSUPPORTED_ENCODING = 2;
    public static final int ESCAPING = 3;
    public static final int PUNYCODE = 4;
    protected int reasonCode;
    protected String reason;
    
    public URIException() {
    }
    
    public URIException(final int reasonCode) {
        this.reasonCode = reasonCode;
    }
    
    public URIException(final int reasonCode, final String reason) {
        super(reason);
        this.reason = reason;
        this.reasonCode = reasonCode;
    }
    
    public URIException(final String reason) {
        super(reason);
        this.reason = reason;
        this.reasonCode = 0;
    }
    
    public int getReasonCode() {
        return this.reasonCode;
    }
    
    public void setReasonCode(final int reasonCode) {
        this.reasonCode = reasonCode;
    }
    
    public String getReason() {
        return this.reason;
    }
    
    public void setReason(final String reason) {
        this.reason = reason;
    }
}
