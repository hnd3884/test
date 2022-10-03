package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class ModifyDNRequestAccessLogMessage extends OperationRequestAccessLogMessage
{
    private static final long serialVersionUID = -1968625384801993253L;
    private final Boolean deleteOldRDN;
    private final String dn;
    private final String newRDN;
    private final String newSuperiorDN;
    
    public ModifyDNRequestAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public ModifyDNRequestAccessLogMessage(final LogMessage m) {
        super(m);
        this.dn = this.getNamedValue("dn");
        this.newRDN = this.getNamedValue("newRDN");
        this.deleteOldRDN = this.getNamedValueAsBoolean("deleteOldRDN");
        this.newSuperiorDN = this.getNamedValue("newSuperior");
    }
    
    public final String getDN() {
        return this.dn;
    }
    
    public final String getNewRDN() {
        return this.newRDN;
    }
    
    public final Boolean deleteOldRDN() {
        return this.deleteOldRDN;
    }
    
    public final String getNewSuperiorDN() {
        return this.newSuperiorDN;
    }
    
    @Override
    public final AccessLogOperationType getOperationType() {
        return AccessLogOperationType.MODDN;
    }
}
