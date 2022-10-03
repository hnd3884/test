package com.unboundid.ldap.sdk.experimental;

import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
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
public final class ActiveDirectoryDirSyncControl extends Control implements DecodeableControl
{
    public static final String DIRSYNC_OID = "1.2.840.113556.1.4.841";
    public static final int FLAG_OBJECT_SECURITY = 1;
    public static final int FLAG_ANCESTORS_FIRST_ORDER = 2048;
    public static final int FLAG_PUBLIC_DATA_ONLY = 8192;
    public static final int FLAG_INCREMENTAL_VALUES = Integer.MIN_VALUE;
    private static final long serialVersionUID = -2871267685237800654L;
    private final ASN1OctetString cookie;
    private final int flags;
    private final int maxAttributeCount;
    
    ActiveDirectoryDirSyncControl() {
        this(true, 0, 0, null);
    }
    
    public ActiveDirectoryDirSyncControl(final boolean isCritical, final int flags, final int maxAttributeCount, final ASN1OctetString cookie) {
        super("1.2.840.113556.1.4.841", isCritical, encodeValue(flags, maxAttributeCount, cookie));
        this.flags = flags;
        this.maxAttributeCount = maxAttributeCount;
        if (cookie == null) {
            this.cookie = new ASN1OctetString();
        }
        else {
            this.cookie = cookie;
        }
    }
    
    public ActiveDirectoryDirSyncControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_DIRSYNC_CONTROL_NO_VALUE.get());
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            this.flags = ASN1Integer.decodeAsInteger(elements[0]).intValue();
            this.maxAttributeCount = ASN1Integer.decodeAsInteger(elements[1]).intValue();
            this.cookie = ASN1OctetString.decodeAsOctetString(elements[2]);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_DIRSYNC_CONTROL_DECODE_ERROR.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private static ASN1OctetString encodeValue(final int flags, final int maxAttributeCount, final ASN1OctetString cookie) {
        final ASN1Element[] valueElements = { new ASN1Integer(flags), new ASN1Integer(maxAttributeCount), null };
        if (cookie == null) {
            valueElements[2] = new ASN1OctetString();
        }
        else {
            valueElements[2] = cookie;
        }
        return new ASN1OctetString(new ASN1Sequence(valueElements).encode());
    }
    
    @Override
    public ActiveDirectoryDirSyncControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new ActiveDirectoryDirSyncControl(oid, isCritical, value);
    }
    
    public int getFlags() {
        return this.flags;
    }
    
    public int getMaxAttributeCount() {
        return this.maxAttributeCount;
    }
    
    public ASN1OctetString getCookie() {
        return this.cookie;
    }
    
    public static ActiveDirectoryDirSyncControl get(final SearchResult result) throws LDAPException {
        final Control c = result.getResponseControl("1.2.840.113556.1.4.841");
        if (c == null) {
            return null;
        }
        if (c instanceof ActiveDirectoryDirSyncControl) {
            return (ActiveDirectoryDirSyncControl)c;
        }
        return new ActiveDirectoryDirSyncControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    @Override
    public String getControlName() {
        return ExperimentalMessages.INFO_CONTROL_NAME_DIRSYNC.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ActiveDirectoryDirSyncControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(", flags=");
        buffer.append(this.flags);
        buffer.append(", maxAttributeCount=");
        buffer.append(this.maxAttributeCount);
        buffer.append(", cookie=byte[");
        buffer.append(this.cookie.getValueLength());
        buffer.append("])");
    }
}
