package com.unboundid.ldap.sdk.controls;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SubentriesRequestControl extends Control
{
    public static final String SUBENTRIES_REQUEST_OID = "1.3.6.1.4.1.7628.5.101.1";
    private static final long serialVersionUID = 4772130172594841481L;
    
    public SubentriesRequestControl() {
        this(false);
    }
    
    public SubentriesRequestControl(final boolean isCritical) {
        super("1.3.6.1.4.1.7628.5.101.1", isCritical, null);
    }
    
    public SubentriesRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SUBENTRIES_HAS_VALUE.get());
        }
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_SUBENTRIES_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("SubentriesRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
