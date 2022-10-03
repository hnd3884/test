package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class UnsolicitedCancelResponseControl extends Control implements DecodeableControl
{
    public static final String UNSOLICITED_CANCEL_RESPONSE_OID = "1.3.6.1.4.1.30221.2.5.7";
    private static final long serialVersionUID = 36962888392922937L;
    
    public UnsolicitedCancelResponseControl() {
        super("1.3.6.1.4.1.30221.2.5.7", false, null);
    }
    
    public UnsolicitedCancelResponseControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value != null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_UNSOLICITED_CANCEL_RESPONSE_HAS_VALUE.get());
        }
    }
    
    @Override
    public UnsolicitedCancelResponseControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new UnsolicitedCancelResponseControl(oid, isCritical, value);
    }
    
    public static UnsolicitedCancelResponseControl get(final LDAPResult result) throws LDAPException {
        final Control c = result.getResponseControl("1.3.6.1.4.1.30221.2.5.7");
        if (c == null) {
            return null;
        }
        if (c instanceof UnsolicitedCancelResponseControl) {
            return (UnsolicitedCancelResponseControl)c;
        }
        return new UnsolicitedCancelResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_UNSOLICITED_CANCEL_RESPONSE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("UnsolicitedCancelResponseControl()");
    }
}
