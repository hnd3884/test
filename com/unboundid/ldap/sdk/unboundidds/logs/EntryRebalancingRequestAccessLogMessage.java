package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import com.unboundid.util.NotMutable;

@NotMutable
@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class EntryRebalancingRequestAccessLogMessage extends AccessLogMessage
{
    private static final long serialVersionUID = -7183383454122018479L;
    private final Integer sizeLimit;
    private final Long rebalancingOperationID;
    private final Long triggeringConnectionID;
    private final Long triggeringOperationID;
    private final String sourceBackendSetName;
    private final String sourceBackendServer;
    private final String subtreeBaseDN;
    private final String targetBackendSetName;
    private final String targetBackendServer;
    
    public EntryRebalancingRequestAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public EntryRebalancingRequestAccessLogMessage(final LogMessage m) {
        super(m);
        this.rebalancingOperationID = this.getNamedValueAsLong("rebalancingOp");
        this.sizeLimit = this.getNamedValueAsInteger("sizeLimit");
        this.sourceBackendServer = this.getNamedValue("sourceServer");
        this.sourceBackendSetName = this.getNamedValue("sourceBackendSet");
        this.subtreeBaseDN = this.getNamedValue("base");
        this.targetBackendServer = this.getNamedValue("targetServer");
        this.targetBackendSetName = this.getNamedValue("targetBackendSet");
        this.triggeringConnectionID = this.getNamedValueAsLong("triggeredByConn");
        this.triggeringOperationID = this.getNamedValueAsLong("triggeredByOp");
    }
    
    public final Long getRebalancingOperationID() {
        return this.rebalancingOperationID;
    }
    
    public final Long getTriggeringConnectionID() {
        return this.triggeringConnectionID;
    }
    
    public final Long getTriggeringOperationID() {
        return this.triggeringOperationID;
    }
    
    public final String getSubtreeBaseDN() {
        return this.subtreeBaseDN;
    }
    
    public final Integer getSizeLimit() {
        return this.sizeLimit;
    }
    
    public final String getSourceBackendSetName() {
        return this.sourceBackendSetName;
    }
    
    public final String getSourceBackendServer() {
        return this.sourceBackendServer;
    }
    
    public final String getTargetBackendSetName() {
        return this.targetBackendSetName;
    }
    
    public final String getTargetBackendServer() {
        return this.targetBackendServer;
    }
    
    @Override
    public AccessLogMessageType getMessageType() {
        return AccessLogMessageType.ENTRY_REBALANCING_REQUEST;
    }
}
