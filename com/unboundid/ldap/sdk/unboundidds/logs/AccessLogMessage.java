package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public abstract class AccessLogMessage extends LogMessage
{
    private static final long serialVersionUID = 111497572975341652L;
    private final Long connectionID;
    private final String instanceName;
    private final String productName;
    private final String startupID;
    
    protected AccessLogMessage(final LogMessage m) {
        super(m);
        this.productName = this.getNamedValue("product");
        this.instanceName = this.getNamedValue("instanceName");
        this.startupID = this.getNamedValue("startupID");
        this.connectionID = this.getNamedValueAsLong("conn");
    }
    
    public static AccessLogMessage parse(final String s) throws LogException {
        return AccessLogReader.parse(s);
    }
    
    public final String getProductName() {
        return this.productName;
    }
    
    public final String getInstanceName() {
        return this.instanceName;
    }
    
    public final String getStartupID() {
        return this.startupID;
    }
    
    public final Long getConnectionID() {
        return this.connectionID;
    }
    
    public abstract AccessLogMessageType getMessageType();
}
