package com.adventnet.audit;

public class AuditException extends Exception
{
    public AuditException(final String msg) {
        super(msg);
    }
    
    public AuditException(final String msg, final Exception ex) {
        super(msg, ex);
    }
}
