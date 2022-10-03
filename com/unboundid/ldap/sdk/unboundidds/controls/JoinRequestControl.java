package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class JoinRequestControl extends Control
{
    public static final String JOIN_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.9";
    private static final long serialVersionUID = -1321645105838145996L;
    private final JoinRequestValue joinRequestValue;
    
    public JoinRequestControl(final JoinRequestValue joinRequestValue) {
        super("1.3.6.1.4.1.30221.2.5.9", true, new ASN1OctetString(joinRequestValue.encode().encode()));
        this.joinRequestValue = joinRequestValue;
    }
    
    public JoinRequestControl(final Control control) throws LDAPException {
        super(control);
        final ASN1OctetString value = control.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_JOIN_REQUEST_CONTROL_NO_VALUE.get());
        }
        ASN1Element valueElement;
        try {
            valueElement = ASN1Element.decode(value.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_JOIN_REQUEST_VALUE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
        this.joinRequestValue = JoinRequestValue.decode(valueElement);
    }
    
    public JoinRequestValue getJoinRequestValue() {
        return this.joinRequestValue;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_JOIN_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("JoinRequestControl(value=");
        this.joinRequestValue.toString(buffer);
        buffer.append(')');
    }
}
