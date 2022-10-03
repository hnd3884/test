package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Sequence;
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
public final class TransactionSettingsResponseControl extends Control implements DecodeableControl
{
    public static final String TRANSACTION_SETTINGS_RESPONSE_OID = "1.3.6.1.4.1.30221.2.5.39";
    private static final byte TYPE_NUM_LOCK_CONFLICTS = Byte.MIN_VALUE;
    private static final byte TYPE_BACKEND_LOCK_ACQUIRED = -127;
    private static final long serialVersionUID = 7290122856855738454L;
    private final boolean backendLockAcquired;
    private final int numLockConflicts;
    
    TransactionSettingsResponseControl() {
        this.backendLockAcquired = false;
        this.numLockConflicts = -1;
    }
    
    public TransactionSettingsResponseControl(final int numLockConflicts, final boolean backendLockAcquired) {
        super("1.3.6.1.4.1.30221.2.5.39", false, encodeValue(numLockConflicts, backendLockAcquired));
        this.numLockConflicts = numLockConflicts;
        this.backendLockAcquired = backendLockAcquired;
    }
    
    public TransactionSettingsResponseControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_TXN_SETTINGS_RESPONSE_NO_VALUE.get());
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            this.numLockConflicts = ASN1Integer.decodeAsInteger(elements[0]).intValue();
            this.backendLockAcquired = ASN1Boolean.decodeAsBoolean(elements[1]).booleanValue();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_TXN_SETTINGS_RESPONSE_ERROR_DECODING_VALUE.get(StaticUtils.getExceptionMessage(e)));
        }
    }
    
    private static ASN1OctetString encodeValue(final int numLockConflicts, final boolean backendLockAcquired) {
        final ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Element[] { new ASN1Integer((byte)(-128), numLockConflicts), new ASN1Boolean((byte)(-127), backendLockAcquired) });
        return new ASN1OctetString(valueSequence.encode());
    }
    
    @Override
    public TransactionSettingsResponseControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new TransactionSettingsResponseControl(oid, isCritical, value);
    }
    
    public int getNumLockConflicts() {
        return this.numLockConflicts;
    }
    
    public boolean backendLockAcquired() {
        return this.backendLockAcquired;
    }
    
    public static TransactionSettingsResponseControl get(final ExtendedResult extendedResult) throws LDAPException {
        final Control c = extendedResult.getResponseControl("1.3.6.1.4.1.30221.2.5.39");
        if (c == null) {
            return null;
        }
        if (c instanceof TransactionSettingsResponseControl) {
            return (TransactionSettingsResponseControl)c;
        }
        return new TransactionSettingsResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_TXN_SETTINGS_RESPONSE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("TransactionSettingsResponseControl(numLockConflicts=");
        buffer.append(this.numLockConflicts);
        buffer.append(", backendLockAcquired=");
        buffer.append(this.backendLockAcquired);
        buffer.append(')');
    }
}
