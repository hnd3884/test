package com.unboundid.ldap.sdk.controls;

import com.unboundid.ldap.sdk.LDAPResult;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Boolean;
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
public final class ContentSyncDoneControl extends Control implements DecodeableControl
{
    public static final String SYNC_DONE_OID = "1.3.6.1.4.1.4203.1.9.1.3";
    private static final long serialVersionUID = -2723009401737612274L;
    private final ASN1OctetString cookie;
    private final boolean refreshDeletes;
    
    ContentSyncDoneControl() {
        this.cookie = null;
        this.refreshDeletes = false;
    }
    
    public ContentSyncDoneControl(final ASN1OctetString cookie, final boolean refreshDeletes) {
        super("1.3.6.1.4.1.4203.1.9.1.3", false, encodeValue(cookie, refreshDeletes));
        this.cookie = cookie;
        this.refreshDeletes = refreshDeletes;
    }
    
    public ContentSyncDoneControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_DONE_NO_VALUE.get());
        }
        ASN1OctetString c = null;
        Boolean r = null;
        try {
            final ASN1Sequence s = ASN1Sequence.decodeAsSequence(value.getValue());
            for (final ASN1Element e : s.elements()) {
                switch (e.getType()) {
                    case 4: {
                        if (c == null) {
                            c = ASN1OctetString.decodeAsOctetString(e);
                            break;
                        }
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_DONE_VALUE_MULTIPLE_COOKIES.get());
                    }
                    case 1: {
                        if (r == null) {
                            r = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                            break;
                        }
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_DONE_VALUE_MULTIPLE_REFRESH_DELETE.get());
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_DONE_VALUE_INVALID_ELEMENT_TYPE.get(StaticUtils.toHex(e.getType())));
                    }
                }
            }
        }
        catch (final LDAPException le) {
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_DONE_VALUE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
        this.cookie = c;
        if (r == null) {
            this.refreshDeletes = false;
        }
        else {
            this.refreshDeletes = r;
        }
    }
    
    private static ASN1OctetString encodeValue(final ASN1OctetString cookie, final boolean refreshDeletes) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(2);
        if (cookie != null) {
            elements.add(cookie);
        }
        if (refreshDeletes) {
            elements.add(new ASN1Boolean(refreshDeletes));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    @Override
    public ContentSyncDoneControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new ContentSyncDoneControl(oid, isCritical, value);
    }
    
    public static ContentSyncDoneControl get(final LDAPResult result) throws LDAPException {
        final Control c = result.getResponseControl("1.3.6.1.4.1.4203.1.9.1.3");
        if (c == null) {
            return null;
        }
        if (c instanceof ContentSyncDoneControl) {
            return (ContentSyncDoneControl)c;
        }
        return new ContentSyncDoneControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    public ASN1OctetString getCookie() {
        return this.cookie;
    }
    
    public boolean refreshDeletes() {
        return this.refreshDeletes;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_CONTENT_SYNC_DONE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ContentSyncDoneControl(");
        if (this.cookie != null) {
            buffer.append("cookie='");
            StaticUtils.toHex(this.cookie.getValue(), buffer);
            buffer.append("', ");
        }
        buffer.append("refreshDeletes=");
        buffer.append(this.refreshDeletes);
        buffer.append(')');
    }
}
