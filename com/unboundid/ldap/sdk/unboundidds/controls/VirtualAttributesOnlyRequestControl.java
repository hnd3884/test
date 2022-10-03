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
public final class VirtualAttributesOnlyRequestControl extends Control
{
    public static final String VIRTUAL_ATTRIBUTES_ONLY_REQUEST_OID = "2.16.840.1.113730.3.4.19";
    private static final long serialVersionUID = 1509094615426408618L;
    
    public VirtualAttributesOnlyRequestControl() {
        this(false);
    }
    
    public VirtualAttributesOnlyRequestControl(final boolean isCritical) {
        super("2.16.840.1.113730.3.4.19", isCritical, null);
    }
    
    public VirtualAttributesOnlyRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_VIRTUAL_ATTRS_ONLY_REQUEST_HAS_VALUE.get());
        }
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_VIRTUAL_ATTRS_ONLY_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("VirtualAttributesOnlyRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
