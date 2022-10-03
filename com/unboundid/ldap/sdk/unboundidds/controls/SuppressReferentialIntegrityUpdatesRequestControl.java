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
public final class SuppressReferentialIntegrityUpdatesRequestControl extends Control
{
    public static final String SUPPRESS_REFINT_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.30";
    private static final long serialVersionUID = 4761880447993567116L;
    
    public SuppressReferentialIntegrityUpdatesRequestControl() {
        this(true);
    }
    
    public SuppressReferentialIntegrityUpdatesRequestControl(final boolean isCritical) {
        super("1.3.6.1.4.1.30221.2.5.30", isCritical, null);
    }
    
    public SuppressReferentialIntegrityUpdatesRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SUPPRESS_REFINT_REQUEST_CONTROL_HAS_VALUE.get());
        }
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_SUPPRESS_REFINT_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("SuppressReferentialIntegrityUpdatesRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
