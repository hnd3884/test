package com.unboundid.ldap.sdk.unboundidds.logs;

import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.Collections;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class IntermediateResponseAccessLogMessage extends OperationRequestAccessLogMessage
{
    private static final long serialVersionUID = 4480365381503945078L;
    private final AccessLogOperationType operationType;
    private final List<String> responseControlOIDs;
    private final String name;
    private final String oid;
    private final String value;
    
    public IntermediateResponseAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public IntermediateResponseAccessLogMessage(final LogMessage m) {
        super(m);
        this.oid = this.getNamedValue("oid");
        this.name = this.getNamedValue("name");
        this.value = this.getNamedValue("value");
        final String controlStr = this.getNamedValue("responseControls");
        if (controlStr == null) {
            this.responseControlOIDs = Collections.emptyList();
        }
        else {
            final LinkedList<String> controlList = new LinkedList<String>();
            final StringTokenizer t = new StringTokenizer(controlStr, ",");
            while (t.hasMoreTokens()) {
                controlList.add(t.nextToken());
            }
            this.responseControlOIDs = Collections.unmodifiableList((List<? extends String>)controlList);
        }
        if (m.hasUnnamedValue(AccessLogOperationType.ADD.getLogIdentifier())) {
            this.operationType = AccessLogOperationType.ADD;
        }
        else if (m.hasUnnamedValue(AccessLogOperationType.BIND.getLogIdentifier())) {
            this.operationType = AccessLogOperationType.BIND;
        }
        else if (m.hasUnnamedValue(AccessLogOperationType.COMPARE.getLogIdentifier())) {
            this.operationType = AccessLogOperationType.COMPARE;
        }
        else if (m.hasUnnamedValue(AccessLogOperationType.DELETE.getLogIdentifier())) {
            this.operationType = AccessLogOperationType.DELETE;
        }
        else if (m.hasUnnamedValue(AccessLogOperationType.EXTENDED.getLogIdentifier())) {
            this.operationType = AccessLogOperationType.EXTENDED;
        }
        else if (m.hasUnnamedValue(AccessLogOperationType.MODIFY.getLogIdentifier())) {
            this.operationType = AccessLogOperationType.MODIFY;
        }
        else if (m.hasUnnamedValue(AccessLogOperationType.MODDN.getLogIdentifier())) {
            this.operationType = AccessLogOperationType.MODDN;
        }
        else if (m.hasUnnamedValue(AccessLogOperationType.SEARCH.getLogIdentifier())) {
            this.operationType = AccessLogOperationType.SEARCH;
        }
        else {
            this.operationType = AccessLogOperationType.EXTENDED;
        }
    }
    
    public String getOID() {
        return this.oid;
    }
    
    public String getIntermediateResponseName() {
        return this.name;
    }
    
    public String getValueString() {
        return this.value;
    }
    
    public List<String> getResponseControlOIDs() {
        return this.responseControlOIDs;
    }
    
    @Override
    public AccessLogMessageType getMessageType() {
        return AccessLogMessageType.INTERMEDIATE_RESPONSE;
    }
    
    @Override
    public AccessLogOperationType getOperationType() {
        return this.operationType;
    }
}
