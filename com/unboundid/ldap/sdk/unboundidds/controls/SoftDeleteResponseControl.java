package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SoftDeleteResponseControl extends Control implements DecodeableControl
{
    public static final String SOFT_DELETE_RESPONSE_OID = "1.3.6.1.4.1.30221.2.5.21";
    private static final long serialVersionUID = 3163679387266190228L;
    private final String softDeletedEntryDN;
    
    SoftDeleteResponseControl() {
        this.softDeletedEntryDN = null;
    }
    
    public SoftDeleteResponseControl(final String softDeletedEntryDN) {
        super("1.3.6.1.4.1.30221.2.5.21", false, new ASN1OctetString(softDeletedEntryDN));
        Validator.ensureNotNull(softDeletedEntryDN);
        this.softDeletedEntryDN = softDeletedEntryDN;
    }
    
    public SoftDeleteResponseControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SOFT_DELETE_RESPONSE_NO_VALUE.get());
        }
        this.softDeletedEntryDN = value.stringValue();
        if (!DN.isValidDN(this.softDeletedEntryDN)) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SOFT_DELETE_RESPONSE_VALUE_NOT_DN.get());
        }
    }
    
    @Override
    public SoftDeleteResponseControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new SoftDeleteResponseControl(oid, isCritical, value);
    }
    
    public String getSoftDeletedEntryDN() {
        return this.softDeletedEntryDN;
    }
    
    public static SoftDeleteResponseControl get(final LDAPResult deleteResult) throws LDAPException {
        final Control c = deleteResult.getResponseControl("1.3.6.1.4.1.30221.2.5.21");
        if (c == null) {
            return null;
        }
        if (c instanceof SoftDeleteResponseControl) {
            return (SoftDeleteResponseControl)c;
        }
        return new SoftDeleteResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_SOFT_DELETE_RESPONSE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("SoftDeleteResponseControl(softDeletedEntryDN='");
        buffer.append(this.softDeletedEntryDN);
        buffer.append("')");
    }
}
