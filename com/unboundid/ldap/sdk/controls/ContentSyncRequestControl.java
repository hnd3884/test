package com.unboundid.ldap.sdk.controls;

import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ContentSyncRequestControl extends Control
{
    public static final String SYNC_REQUEST_OID = "1.3.6.1.4.1.4203.1.9.1.1";
    private static final long serialVersionUID = -3183343423271667072L;
    private final ASN1OctetString cookie;
    private final boolean reloadHint;
    private final ContentSyncRequestMode mode;
    
    public ContentSyncRequestControl(final ContentSyncRequestMode mode) {
        this(true, mode, null, false);
    }
    
    public ContentSyncRequestControl(final ContentSyncRequestMode mode, final ASN1OctetString cookie, final boolean reloadHint) {
        this(true, mode, cookie, reloadHint);
    }
    
    public ContentSyncRequestControl(final boolean isCritical, final ContentSyncRequestMode mode, final ASN1OctetString cookie, final boolean reloadHint) {
        super("1.3.6.1.4.1.4203.1.9.1.1", isCritical, encodeValue(mode, cookie, reloadHint));
        this.mode = mode;
        this.cookie = cookie;
        this.reloadHint = reloadHint;
    }
    
    public ContentSyncRequestControl(final Control control) throws LDAPException {
        super(control);
        final ASN1OctetString value = control.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_REQUEST_NO_VALUE.get());
        }
        ASN1OctetString c = null;
        Boolean h = null;
        ContentSyncRequestMode m = null;
        try {
            final ASN1Sequence s = ASN1Sequence.decodeAsSequence(value.getValue());
            for (final ASN1Element e : s.elements()) {
                switch (e.getType()) {
                    case 10: {
                        if (m != null) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_REQUEST_VALUE_MULTIPLE_MODES.get());
                        }
                        final ASN1Enumerated modeElement = ASN1Enumerated.decodeAsEnumerated(e);
                        m = ContentSyncRequestMode.valueOf(modeElement.intValue());
                        if (m == null) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_REQUEST_VALUE_INVALID_MODE.get(modeElement.intValue()));
                        }
                        break;
                    }
                    case 4: {
                        if (c == null) {
                            c = ASN1OctetString.decodeAsOctetString(e);
                            break;
                        }
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_REQUEST_VALUE_MULTIPLE_COOKIES.get());
                    }
                    case 1: {
                        if (h == null) {
                            h = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                            break;
                        }
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_REQUEST_VALUE_MULTIPLE_HINTS.get());
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_REQUEST_VALUE_INVALID_ELEMENT_TYPE.get(StaticUtils.toHex(e.getType())));
                    }
                }
            }
        }
        catch (final LDAPException le) {
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_REQUEST_VALUE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
        if (m == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_REQUEST_VALUE_NO_MODE.get());
        }
        this.mode = m;
        if (h == null) {
            this.reloadHint = false;
        }
        else {
            this.reloadHint = h;
        }
        this.cookie = c;
    }
    
    private static ASN1OctetString encodeValue(final ContentSyncRequestMode mode, final ASN1OctetString cookie, final boolean reloadHint) {
        Validator.ensureNotNull(mode);
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(3);
        elements.add(new ASN1Enumerated(mode.intValue()));
        if (cookie != null) {
            elements.add(cookie);
        }
        if (reloadHint) {
            elements.add(ASN1Boolean.UNIVERSAL_BOOLEAN_TRUE_ELEMENT);
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public ContentSyncRequestMode getMode() {
        return this.mode;
    }
    
    public ASN1OctetString getCookie() {
        return this.cookie;
    }
    
    public boolean getReloadHint() {
        return this.reloadHint;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_CONTENT_SYNC_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ContentSyncRequestControl(mode='");
        buffer.append(this.mode.name());
        buffer.append('\'');
        if (this.cookie != null) {
            buffer.append(", cookie='");
            StaticUtils.toHex(this.cookie.getValue(), buffer);
            buffer.append('\'');
        }
        buffer.append(", reloadHint=");
        buffer.append(this.reloadHint);
        buffer.append(')');
    }
}
