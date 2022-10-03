package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class CompareRequestAccessLogMessage extends OperationRequestAccessLogMessage
{
    private static final long serialVersionUID = 5478010218343234128L;
    private final String attributeName;
    private final String dn;
    
    public CompareRequestAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public CompareRequestAccessLogMessage(final LogMessage m) {
        super(m);
        this.dn = this.getNamedValue("dn");
        this.attributeName = this.getNamedValue("attr");
    }
    
    public final String getDN() {
        return this.dn;
    }
    
    public final String getAttributeName() {
        return this.attributeName;
    }
    
    @Override
    public final AccessLogOperationType getOperationType() {
        return AccessLogOperationType.COMPARE;
    }
}
