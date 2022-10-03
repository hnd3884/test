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
public final class ReturnConflictEntriesRequestControl extends Control
{
    public static final String RETURN_CONFLICT_ENTRIES_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.13";
    private static final long serialVersionUID = -7688556660280234650L;
    
    public ReturnConflictEntriesRequestControl() {
        this(true);
    }
    
    public ReturnConflictEntriesRequestControl(final boolean isCritical) {
        super("1.3.6.1.4.1.30221.2.5.13", isCritical, null);
    }
    
    public ReturnConflictEntriesRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_RETURN_CONFLICT_ENTRIES_REQUEST_HAS_VALUE.get());
        }
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_RETURN_CONFLICT_ENTRIES_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ReturnConflictEntriesRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
