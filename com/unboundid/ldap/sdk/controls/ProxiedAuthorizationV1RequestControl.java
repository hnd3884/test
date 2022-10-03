package com.unboundid.ldap.sdk.controls;

import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.util.Validator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ProxiedAuthorizationV1RequestControl extends Control
{
    public static final String PROXIED_AUTHORIZATION_V1_REQUEST_OID = "2.16.840.1.113730.3.4.12";
    private static final long serialVersionUID = 7312632337431962774L;
    private final String proxyDN;
    
    public ProxiedAuthorizationV1RequestControl(final String proxyDN) {
        super("2.16.840.1.113730.3.4.12", true, encodeValue(proxyDN));
        Validator.ensureNotNull(proxyDN);
        this.proxyDN = proxyDN;
    }
    
    public ProxiedAuthorizationV1RequestControl(final DN proxyDN) {
        super("2.16.840.1.113730.3.4.12", true, encodeValue(proxyDN.toString()));
        this.proxyDN = proxyDN.toString();
    }
    
    public ProxiedAuthorizationV1RequestControl(final Control control) throws LDAPException {
        super(control);
        final ASN1OctetString value = control.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PROXY_V1_NO_VALUE.get());
        }
        try {
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(valueElement).elements();
            this.proxyDN = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PROXYV1_DECODE_ERROR.get(e), e);
        }
    }
    
    private static ASN1OctetString encodeValue(final String proxyDN) {
        final ASN1Element[] valueElements = { new ASN1OctetString(proxyDN) };
        return new ASN1OctetString(new ASN1Sequence(valueElements).encode());
    }
    
    public String getProxyDN() {
        return this.proxyDN;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_PROXIED_AUTHZ_V1_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ProxiedAuthorizationV1RequestControl(proxyDN='");
        buffer.append(this.proxyDN);
        buffer.append("')");
    }
}
