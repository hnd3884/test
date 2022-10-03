package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ReplicationRepairRequestControl extends Control
{
    public static final String REPLICATION_REPAIR_REQUEST_OID = "1.3.6.1.4.1.30221.1.5.2";
    private static final long serialVersionUID = 8036161025439278805L;
    
    public ReplicationRepairRequestControl() {
        super("1.3.6.1.4.1.30221.1.5.2", true, null);
    }
    
    public ReplicationRepairRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_REPLICATION_REPAIR_REQUEST_HAS_VALUE.get());
        }
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_REPLICATION_REPAIR_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ReplicationRepairRequestControl()");
    }
}
