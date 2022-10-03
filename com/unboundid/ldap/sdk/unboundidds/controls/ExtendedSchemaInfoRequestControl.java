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
public final class ExtendedSchemaInfoRequestControl extends Control
{
    public static final String EXTENDED_SCHEMA_INFO_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.12";
    private static final long serialVersionUID = -5668945270252160026L;
    
    public ExtendedSchemaInfoRequestControl() {
        this(false);
    }
    
    public ExtendedSchemaInfoRequestControl(final boolean isCritical) {
        super("1.3.6.1.4.1.30221.2.5.12", isCritical, null);
    }
    
    public ExtendedSchemaInfoRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_EXTENDED_SCHEMA_INFO_REQUEST_HAS_VALUE.get());
        }
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_EXTENDED_SCHEMA_INFO.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ExtendedSchemaInfoRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
