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
public final class RealAttributesOnlyRequestControl extends Control
{
    public static final String REAL_ATTRIBUTES_ONLY_REQUEST_OID = "2.16.840.1.113730.3.4.17";
    private static final long serialVersionUID = -3092359699532262022L;
    
    public RealAttributesOnlyRequestControl() {
        this(false);
    }
    
    public RealAttributesOnlyRequestControl(final boolean isCritical) {
        super("2.16.840.1.113730.3.4.17", isCritical, null);
    }
    
    public RealAttributesOnlyRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_REAL_ATTRS_ONLY_REQUEST_HAS_VALUE.get());
        }
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_REAL_ATTRS_ONLY_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("RealAttributesOnlyRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
