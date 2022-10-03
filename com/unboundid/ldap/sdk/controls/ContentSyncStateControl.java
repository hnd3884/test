package com.unboundid.ldap.sdk.controls;

import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.SearchResultEntry;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import java.text.ParseException;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.UUID;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ContentSyncStateControl extends Control implements DecodeableControl
{
    public static final String SYNC_STATE_OID = "1.3.6.1.4.1.4203.1.9.1.2";
    private static final long serialVersionUID = 4796325788870542241L;
    private final ASN1OctetString cookie;
    private final ContentSyncState state;
    private final UUID entryUUID;
    
    ContentSyncStateControl() {
        this.state = null;
        this.entryUUID = null;
        this.cookie = null;
    }
    
    public ContentSyncStateControl(final ContentSyncState state, final UUID entryUUID, final ASN1OctetString cookie) {
        super("1.3.6.1.4.1.4203.1.9.1.2", false, encodeValue(state, entryUUID, cookie));
        this.state = state;
        this.entryUUID = entryUUID;
        this.cookie = cookie;
    }
    
    public ContentSyncStateControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_STATE_NO_VALUE.get());
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            final ASN1Enumerated e = ASN1Enumerated.decodeAsEnumerated(elements[0]);
            this.state = ContentSyncState.valueOf(e.intValue());
            if (this.state == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_STATE_VALUE_INVALID_STATE.get(e.intValue()));
            }
            try {
                this.entryUUID = StaticUtils.decodeUUID(elements[1].getValue());
            }
            catch (final ParseException pe) {
                Debug.debugException(pe);
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_STATE_VALUE_MALFORMED_UUID.get(pe.getMessage()), pe);
            }
            if (elements.length == 3) {
                this.cookie = ASN1OctetString.decodeAsOctetString(elements[2]);
            }
            else {
                this.cookie = null;
            }
        }
        catch (final LDAPException le) {
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_STATE_VALUE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    private static ASN1OctetString encodeValue(final ContentSyncState state, final UUID entryUUID, final ASN1OctetString cookie) {
        Validator.ensureNotNull(state, entryUUID);
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(3);
        elements.add(new ASN1Enumerated(state.intValue()));
        elements.add(new ASN1OctetString(StaticUtils.encodeUUID(entryUUID)));
        if (cookie != null) {
            elements.add(cookie);
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    @Override
    public ContentSyncStateControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new ContentSyncStateControl(oid, isCritical, value);
    }
    
    public static ContentSyncStateControl get(final SearchResultEntry entry) throws LDAPException {
        final Control c = entry.getControl("1.3.6.1.4.1.4203.1.9.1.2");
        if (c == null) {
            return null;
        }
        if (c instanceof ContentSyncStateControl) {
            return (ContentSyncStateControl)c;
        }
        return new ContentSyncStateControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    public static ContentSyncStateControl get(final SearchResultReference ref) throws LDAPException {
        final Control c = ref.getControl("1.3.6.1.4.1.4203.1.9.1.2");
        if (c == null) {
            return null;
        }
        if (c instanceof ContentSyncStateControl) {
            return (ContentSyncStateControl)c;
        }
        return new ContentSyncStateControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    public ContentSyncState getState() {
        return this.state;
    }
    
    public UUID getEntryUUID() {
        return this.entryUUID;
    }
    
    public ASN1OctetString getCookie() {
        return this.cookie;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_CONTENT_SYNC_STATE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ContentSyncStateControl(state='");
        buffer.append(this.state.name());
        buffer.append("', entryUUID='");
        buffer.append(this.entryUUID);
        buffer.append('\'');
        if (this.cookie != null) {
            buffer.append(", cookie=");
            StaticUtils.toHex(this.cookie.getValue(), buffer);
        }
        buffer.append(')');
    }
}
