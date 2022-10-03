package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AdministrativeOperationRequestControl extends Control
{
    public static final String ADMINISTRATIVE_OPERATION_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.11";
    private static final long serialVersionUID = 4958642483402677725L;
    private final String message;
    
    public AdministrativeOperationRequestControl() {
        this((String)null);
    }
    
    public AdministrativeOperationRequestControl(final String message) {
        super("1.3.6.1.4.1.30221.2.5.11", false, encodeValue(message));
        this.message = message;
    }
    
    public AdministrativeOperationRequestControl(final Control control) {
        super(control);
        if (control.hasValue()) {
            this.message = control.getValue().stringValue();
        }
        else {
            this.message = null;
        }
    }
    
    private static ASN1OctetString encodeValue(final String message) {
        if (message == null) {
            return null;
        }
        return new ASN1OctetString(message);
    }
    
    public String getMessage() {
        return this.message;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_ADMINISTRATIVE_OPERATION_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("AdministrativeOperationRequestControl(");
        if (this.message != null) {
            buffer.append("message='");
            buffer.append(this.message);
            buffer.append('\'');
        }
        buffer.append(')');
    }
}
