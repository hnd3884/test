package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.asn1.ASN1Exception;
import java.util.Iterator;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.ReadOnlyEntry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GetAuthorizationEntryResponseControl extends Control implements DecodeableControl
{
    public static final String GET_AUTHORIZATION_ENTRY_RESPONSE_OID = "1.3.6.1.4.1.30221.2.5.6";
    private static final byte TYPE_IS_AUTHENTICATED = Byte.MIN_VALUE;
    private static final byte TYPE_IDENTITIES_MATCH = -127;
    private static final byte TYPE_AUTHN_ENTRY = -94;
    private static final byte TYPE_AUTHZ_ENTRY = -93;
    private static final byte TYPE_AUTHID = Byte.MIN_VALUE;
    private static final byte TYPE_AUTHDN = -127;
    private static final byte TYPE_ATTRIBUTES = -94;
    private static final long serialVersionUID = -5443107150740697226L;
    private final boolean identitiesMatch;
    private final boolean isAuthenticated;
    private final ReadOnlyEntry authNEntry;
    private final ReadOnlyEntry authZEntry;
    private final String authNID;
    private final String authZID;
    
    GetAuthorizationEntryResponseControl() {
        this.isAuthenticated = false;
        this.identitiesMatch = true;
        this.authNEntry = null;
        this.authNID = null;
        this.authZEntry = null;
        this.authZID = null;
    }
    
    public GetAuthorizationEntryResponseControl(final boolean isAuthenticated, final boolean identitiesMatch, final String authNID, final ReadOnlyEntry authNEntry, final String authZID, final ReadOnlyEntry authZEntry) {
        super("1.3.6.1.4.1.30221.2.5.6", false, encodeValue(isAuthenticated, identitiesMatch, authNID, authNEntry, authZID, authZEntry));
        this.isAuthenticated = isAuthenticated;
        this.identitiesMatch = identitiesMatch;
        this.authNID = authNID;
        this.authNEntry = authNEntry;
        this.authZID = authZID;
        this.authZEntry = authZEntry;
    }
    
    public GetAuthorizationEntryResponseControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GET_AUTHORIZATION_ENTRY_RESPONSE_NO_VALUE.get());
        }
        try {
            boolean isAuth = false;
            boolean idsMatch = false;
            String nID = null;
            String zID = null;
            ReadOnlyEntry nEntry = null;
            ReadOnlyEntry zEntry = null;
            final ASN1Element valElement = ASN1Element.decode(value.getValue());
            for (final ASN1Element e : ASN1Sequence.decodeAsSequence(valElement).elements()) {
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        isAuth = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    case -127: {
                        idsMatch = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    case -94: {
                        final Object[] nObjects = decodeAuthEntry(e);
                        nID = (String)nObjects[0];
                        nEntry = (ReadOnlyEntry)nObjects[1];
                        break;
                    }
                    case -93: {
                        final Object[] zObjects = decodeAuthEntry(e);
                        zID = (String)zObjects[0];
                        zEntry = (ReadOnlyEntry)zObjects[1];
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GET_AUTHORIZATION_ENTRY_RESPONSE_INVALID_VALUE_TYPE.get(StaticUtils.toHex(e.getType())));
                    }
                }
            }
            this.isAuthenticated = isAuth;
            this.identitiesMatch = idsMatch;
            this.authNID = nID;
            this.authNEntry = nEntry;
            this.authZID = zID;
            this.authZEntry = zEntry;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GET_AUTHORIZATION_ENTRY_RESPONSE_CANNOT_DECODE_VALUE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    @Override
    public GetAuthorizationEntryResponseControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new GetAuthorizationEntryResponseControl(oid, isCritical, value);
    }
    
    public static GetAuthorizationEntryResponseControl get(final BindResult result) throws LDAPException {
        final Control c = result.getResponseControl("1.3.6.1.4.1.30221.2.5.6");
        if (c == null) {
            return null;
        }
        if (c instanceof GetAuthorizationEntryResponseControl) {
            return (GetAuthorizationEntryResponseControl)c;
        }
        return new GetAuthorizationEntryResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    private static ASN1OctetString encodeValue(final boolean isAuthenticated, final boolean identitiesMatch, final String authNID, final ReadOnlyEntry authNEntry, final String authZID, final ReadOnlyEntry authZEntry) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(4);
        elements.add(new ASN1Boolean((byte)(-128), isAuthenticated));
        elements.add(new ASN1Boolean((byte)(-127), identitiesMatch));
        if (authNEntry != null) {
            elements.add(encodeAuthEntry((byte)(-94), authNID, authNEntry));
        }
        if (authZEntry != null) {
            elements.add(encodeAuthEntry((byte)(-93), authZID, authZEntry));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    private static ASN1Sequence encodeAuthEntry(final byte type, final String authID, final ReadOnlyEntry authEntry) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(3);
        if (authID != null) {
            elements.add(new ASN1OctetString((byte)(-128), authID));
        }
        elements.add(new ASN1OctetString((byte)(-127), authEntry.getDN()));
        final Collection<Attribute> attributes = authEntry.getAttributes();
        final ArrayList<ASN1Element> attrElements = new ArrayList<ASN1Element>(attributes.size());
        for (final Attribute a : attributes) {
            attrElements.add(a.encode());
        }
        elements.add(new ASN1Sequence((byte)(-94), attrElements));
        return new ASN1Sequence(type, elements);
    }
    
    private static Object[] decodeAuthEntry(final ASN1Element element) throws ASN1Exception, LDAPException {
        String authID = null;
        String authDN = null;
        final ArrayList<Attribute> attrs = new ArrayList<Attribute>(20);
        for (final ASN1Element e : ASN1Sequence.decodeAsSequence(element).elements()) {
            switch (e.getType()) {
                case Byte.MIN_VALUE: {
                    authID = ASN1OctetString.decodeAsOctetString(e).stringValue();
                    break;
                }
                case -127: {
                    authDN = ASN1OctetString.decodeAsOctetString(e).stringValue();
                    break;
                }
                case -94: {
                    for (final ASN1Element ae : ASN1Sequence.decodeAsSequence(e).elements()) {
                        attrs.add(Attribute.decode(ASN1Sequence.decodeAsSequence(ae)));
                    }
                    break;
                }
                default: {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GET_AUTHORIZATION_ENTRY_RESPONSE_INVALID_ENTRY_TYPE.get(StaticUtils.toHex(e.getType())));
                }
            }
        }
        return new Object[] { authID, new ReadOnlyEntry(authDN, attrs) };
    }
    
    public boolean isAuthenticated() {
        return this.isAuthenticated;
    }
    
    public boolean identitiesMatch() {
        return this.identitiesMatch;
    }
    
    public String getAuthNID() {
        if (this.authNID == null && this.identitiesMatch) {
            return this.authZID;
        }
        return this.authNID;
    }
    
    public ReadOnlyEntry getAuthNEntry() {
        if (this.authNEntry == null && this.identitiesMatch) {
            return this.authZEntry;
        }
        return this.authNEntry;
    }
    
    public String getAuthZID() {
        if (this.authZID == null && this.identitiesMatch) {
            return this.authNID;
        }
        return this.authZID;
    }
    
    public ReadOnlyEntry getAuthZEntry() {
        if (this.authZEntry == null && this.identitiesMatch) {
            return this.authNEntry;
        }
        return this.authZEntry;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_GET_AUTHORIZATION_ENTRY_RESPONSE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetAuthorizationEntryResponseControl(identitiesMatch=");
        buffer.append(this.identitiesMatch);
        if (this.authNID != null) {
            buffer.append(", authNID='");
            buffer.append(this.authNID);
            buffer.append('\'');
        }
        if (this.authNEntry != null) {
            buffer.append(", authNEntry=");
            this.authNEntry.toString(buffer);
        }
        if (this.authZID != null) {
            buffer.append(", authZID='");
            buffer.append(this.authZID);
            buffer.append('\'');
        }
        if (this.authZEntry != null) {
            buffer.append(", authZEntry=");
            this.authZEntry.toString(buffer);
        }
        buffer.append(')');
    }
}
