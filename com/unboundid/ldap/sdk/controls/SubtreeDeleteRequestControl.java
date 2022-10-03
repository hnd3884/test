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
public final class SubtreeDeleteRequestControl extends Control
{
    public static final String SUBTREE_DELETE_REQUEST_OID = "1.2.840.113556.1.4.805";
    private static final long serialVersionUID = 3748121547717081961L;
    
    public SubtreeDeleteRequestControl() {
        super("1.2.840.113556.1.4.805", false, null);
    }
    
    public SubtreeDeleteRequestControl(final boolean isCritical) {
        super("1.2.840.113556.1.4.805", isCritical, null);
    }
    
    public SubtreeDeleteRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SUBTREE_DELETE_HAS_VALUE.get());
        }
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_SUBTREE_DELETE_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("SubtreeDeleteRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
