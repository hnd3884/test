package com.unboundid.ldap.sdk.unboundidds.logs;

import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.Collections;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public abstract class OperationRequestAccessLogMessage extends OperationAccessLogMessage
{
    private static final long serialVersionUID = -8942685623238040482L;
    private final Boolean usingAdminSessionWorkerThread;
    private final List<String> requestControlOIDs;
    private final String intermediateClientRequest;
    private final String operationPurpose;
    private final String requesterDN;
    private final String requesterIP;
    
    protected OperationRequestAccessLogMessage(final LogMessage m) {
        super(m);
        this.intermediateClientRequest = this.getNamedValue("via");
        this.operationPurpose = this.getNamedValue("opPurpose");
        this.requesterDN = this.getNamedValue("requesterDN");
        this.requesterIP = this.getNamedValue("requesterIP");
        this.usingAdminSessionWorkerThread = this.getNamedValueAsBoolean("usingAdminSessionWorkerThread");
        final String controlStr = this.getNamedValue("requestControls");
        if (controlStr == null) {
            this.requestControlOIDs = Collections.emptyList();
        }
        else {
            final LinkedList<String> controlList = new LinkedList<String>();
            final StringTokenizer t = new StringTokenizer(controlStr, ",");
            while (t.hasMoreTokens()) {
                controlList.add(t.nextToken());
            }
            this.requestControlOIDs = Collections.unmodifiableList((List<? extends String>)controlList);
        }
    }
    
    public final String getRequesterDN() {
        return this.requesterDN;
    }
    
    public final String getRequesterIPAddress() {
        return this.requesterIP;
    }
    
    public final String getIntermediateClientRequest() {
        return this.intermediateClientRequest;
    }
    
    public final String getOperationPurpose() {
        return this.operationPurpose;
    }
    
    public final List<String> getRequestControlOIDs() {
        return this.requestControlOIDs;
    }
    
    public final Boolean usingAdminSessionWorkerThread() {
        return this.usingAdminSessionWorkerThread;
    }
    
    @Override
    public AccessLogMessageType getMessageType() {
        return AccessLogMessageType.REQUEST;
    }
}
