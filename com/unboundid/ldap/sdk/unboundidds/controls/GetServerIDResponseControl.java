package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.LDAPResult;
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
public final class GetServerIDResponseControl extends Control implements DecodeableControl
{
    public static final String GET_SERVER_ID_RESPONSE_OID = "1.3.6.1.4.1.30221.2.5.15";
    private static final long serialVersionUID = 5271084342514677677L;
    private final String serverID;
    
    GetServerIDResponseControl() {
        this.serverID = null;
    }
    
    public GetServerIDResponseControl(final String serverID) {
        super("1.3.6.1.4.1.30221.2.5.15", false, new ASN1OctetString(serverID));
        Validator.ensureNotNull(serverID);
        this.serverID = serverID;
    }
    
    public GetServerIDResponseControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GET_SERVER_ID_RESPONSE_MISSING_VALUE.get());
        }
        this.serverID = value.stringValue();
    }
    
    @Override
    public GetServerIDResponseControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new GetServerIDResponseControl(oid, isCritical, value);
    }
    
    public static GetServerIDResponseControl get(final LDAPResult result) throws LDAPException {
        final Control c = result.getResponseControl("1.3.6.1.4.1.30221.2.5.15");
        if (c == null) {
            return null;
        }
        if (c instanceof GetServerIDResponseControl) {
            return (GetServerIDResponseControl)c;
        }
        return new GetServerIDResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    public static GetServerIDResponseControl get(final SearchResultEntry entry) throws LDAPException {
        final Control c = entry.getControl("1.3.6.1.4.1.30221.2.5.15");
        if (c == null) {
            return null;
        }
        if (c instanceof GetServerIDResponseControl) {
            return (GetServerIDResponseControl)c;
        }
        return new GetServerIDResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    public static GetServerIDResponseControl get(final SearchResultReference ref) throws LDAPException {
        final Control c = ref.getControl("1.3.6.1.4.1.30221.2.5.15");
        if (c == null) {
            return null;
        }
        if (c instanceof GetServerIDResponseControl) {
            return (GetServerIDResponseControl)c;
        }
        return new GetServerIDResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    public String getServerID() {
        return this.serverID;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_GET_SERVER_ID_RESPONSE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetServerIDResponseControl(serverID='");
        buffer.append(this.serverID);
        buffer.append("')");
    }
}
