package com.unboundid.ldap.sdk.unboundidds.controls;

import java.util.Iterator;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GetAuthorizationEntryRequestControl extends Control
{
    public static final String GET_AUTHORIZATION_ENTRY_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.6";
    private static final byte TYPE_INCLUDE_AUTHN_ENTRY = Byte.MIN_VALUE;
    private static final byte TYPE_INCLUDE_AUTHZ_ENTRY = -127;
    private static final byte TYPE_ATTRIBUTES = -94;
    private static final long serialVersionUID = -5540345171260624216L;
    private final boolean includeAuthNEntry;
    private final boolean includeAuthZEntry;
    private final List<String> attributes;
    
    public GetAuthorizationEntryRequestControl() {
        this(false, true, true, (List<String>)null);
    }
    
    public GetAuthorizationEntryRequestControl(final boolean includeAuthNEntry, final boolean includeAuthZEntry, final String... attributes) {
        this(false, includeAuthNEntry, includeAuthZEntry, (attributes == null) ? null : Arrays.asList(attributes));
    }
    
    public GetAuthorizationEntryRequestControl(final boolean includeAuthNEntry, final boolean includeAuthZEntry, final List<String> attributes) {
        this(false, includeAuthNEntry, includeAuthZEntry, attributes);
    }
    
    public GetAuthorizationEntryRequestControl(final boolean isCritical, final boolean includeAuthNEntry, final boolean includeAuthZEntry, final String... attributes) {
        this(isCritical, includeAuthNEntry, includeAuthZEntry, (attributes == null) ? null : Arrays.asList(attributes));
    }
    
    public GetAuthorizationEntryRequestControl(final boolean isCritical, final boolean includeAuthNEntry, final boolean includeAuthZEntry, final List<String> attributes) {
        super("1.3.6.1.4.1.30221.2.5.6", isCritical, encodeValue(includeAuthNEntry, includeAuthZEntry, attributes));
        this.includeAuthNEntry = includeAuthNEntry;
        this.includeAuthZEntry = includeAuthZEntry;
        if (attributes == null || attributes.isEmpty()) {
            this.attributes = Collections.emptyList();
        }
        else {
            this.attributes = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(attributes));
        }
    }
    
    public GetAuthorizationEntryRequestControl(final Control control) throws LDAPException {
        super(control);
        final ASN1OctetString value = control.getValue();
        if (value == null) {
            this.includeAuthNEntry = true;
            this.includeAuthZEntry = true;
            this.attributes = Collections.emptyList();
            return;
        }
        try {
            final ArrayList<String> attrs = new ArrayList<String>(20);
            boolean includeAuthN = true;
            boolean includeAuthZ = true;
            final ASN1Element element = ASN1Element.decode(value.getValue());
            for (final ASN1Element e : ASN1Sequence.decodeAsSequence(element).elements()) {
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        includeAuthN = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    case -127: {
                        includeAuthZ = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    case -94: {
                        for (final ASN1Element ae : ASN1Sequence.decodeAsSequence(e).elements()) {
                            attrs.add(ASN1OctetString.decodeAsOctetString(ae).stringValue());
                        }
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GET_AUTHORIZATION_ENTRY_REQUEST_INVALID_SEQUENCE_ELEMENT.get(StaticUtils.toHex(e.getType())));
                    }
                }
            }
            this.includeAuthNEntry = includeAuthN;
            this.includeAuthZEntry = includeAuthZ;
            this.attributes = attrs;
        }
        catch (final LDAPException le) {
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GET_AUTHORIZATION_ENTRY_REQUEST_CANNOT_DECODE_VALUE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    private static ASN1OctetString encodeValue(final boolean includeAuthNEntry, final boolean includeAuthZEntry, final List<String> attributes) {
        if (includeAuthNEntry && includeAuthZEntry && (attributes == null || attributes.isEmpty())) {
            return null;
        }
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(3);
        if (!includeAuthNEntry) {
            elements.add(new ASN1Boolean((byte)(-128), false));
        }
        if (!includeAuthZEntry) {
            elements.add(new ASN1Boolean((byte)(-127), false));
        }
        if (attributes != null && !attributes.isEmpty()) {
            final ArrayList<ASN1Element> attrElements = new ArrayList<ASN1Element>(attributes.size());
            for (final String s : attributes) {
                attrElements.add(new ASN1OctetString(s));
            }
            elements.add(new ASN1Sequence((byte)(-94), attrElements));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public boolean includeAuthNEntry() {
        return this.includeAuthNEntry;
    }
    
    public boolean includeAuthZEntry() {
        return this.includeAuthZEntry;
    }
    
    public List<String> getAttributes() {
        return this.attributes;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_GET_AUTHORIZATION_ENTRY_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetAuthorizationEntryRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(", includeAuthNEntry=");
        buffer.append(this.includeAuthNEntry);
        buffer.append(", includeAuthZEntry=");
        buffer.append(this.includeAuthZEntry);
        buffer.append(", attributes={");
        final Iterator<String> iterator = this.attributes.iterator();
        while (iterator.hasNext()) {
            buffer.append(iterator.next());
            if (iterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("})");
    }
}
