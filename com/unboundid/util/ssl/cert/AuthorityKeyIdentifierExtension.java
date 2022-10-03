package com.unboundid.util.ssl.cert;

import java.util.Collection;
import com.unboundid.asn1.ASN1BigInteger;
import java.util.ArrayList;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1Sequence;
import java.math.BigInteger;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.OID;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AuthorityKeyIdentifierExtension extends X509CertificateExtension
{
    public static final OID AUTHORITY_KEY_IDENTIFIER_OID;
    private static final byte TYPE_KEY_IDENTIFIER = Byte.MIN_VALUE;
    private static final byte TYPE_AUTHORITY_CERT_ISSUER = -95;
    private static final byte TYPE_AUTHORITY_CERT_SERIAL_NUMBER = -126;
    private static final long serialVersionUID = 8913323557731547122L;
    private final ASN1OctetString keyIdentifier;
    private final BigInteger authorityCertSerialNumber;
    private final GeneralNames authorityCertIssuer;
    
    AuthorityKeyIdentifierExtension(final boolean isCritical, final ASN1OctetString keyIdentifier, final GeneralNames authorityCertIssuer, final BigInteger authorityCertSerialNumber) throws CertException {
        super(AuthorityKeyIdentifierExtension.AUTHORITY_KEY_IDENTIFIER_OID, isCritical, encodeValue(keyIdentifier, authorityCertIssuer, authorityCertSerialNumber));
        this.keyIdentifier = keyIdentifier;
        this.authorityCertIssuer = authorityCertIssuer;
        this.authorityCertSerialNumber = authorityCertSerialNumber;
    }
    
    AuthorityKeyIdentifierExtension(final X509CertificateExtension extension) throws CertException {
        super(extension);
        try {
            ASN1OctetString keyID = null;
            BigInteger serialNumber = null;
            GeneralNames generalNames = null;
            for (final ASN1Element element : ASN1Sequence.decodeAsSequence(extension.getValue()).elements()) {
                switch (element.getType()) {
                    case Byte.MIN_VALUE: {
                        keyID = element.decodeAsOctetString();
                        break;
                    }
                    case -95: {
                        final ASN1Element generalNamesElement = ASN1Element.decode(element.getValue());
                        generalNames = new GeneralNames(generalNamesElement);
                        break;
                    }
                    case -126: {
                        serialNumber = element.decodeAsBigInteger().getBigIntegerValue();
                        break;
                    }
                }
            }
            this.keyIdentifier = keyID;
            this.authorityCertIssuer = generalNames;
            this.authorityCertSerialNumber = serialNumber;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_AUTHORITY_KEY_ID_EXTENSION_CANNOT_PARSE.get(String.valueOf(extension), StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private static byte[] encodeValue(final ASN1OctetString keyIdentifier, final GeneralNames authorityCertIssuer, final BigInteger authorityCertSerialNumber) throws CertException {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(3);
        if (keyIdentifier != null) {
            elements.add(new ASN1OctetString((byte)(-128), keyIdentifier.getValue()));
        }
        if (authorityCertIssuer != null) {
            elements.add(new ASN1Element((byte)(-95), authorityCertIssuer.encode().encode()));
        }
        if (authorityCertSerialNumber != null) {
            elements.add(new ASN1BigInteger((byte)(-126), authorityCertSerialNumber));
        }
        return new ASN1Sequence(elements).encode();
    }
    
    public ASN1OctetString getKeyIdentifier() {
        return this.keyIdentifier;
    }
    
    public GeneralNames getAuthorityCertIssuer() {
        return this.authorityCertIssuer;
    }
    
    public BigInteger getAuthorityCertSerialNumber() {
        return this.authorityCertSerialNumber;
    }
    
    @Override
    public String getExtensionName() {
        return CertMessages.INFO_AUTHORITY_KEY_ID_EXTENSION_NAME.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("AuthorityKeyIdentifierExtension(oid='");
        buffer.append(this.getOID());
        buffer.append("', isCritical=");
        buffer.append(this.isCritical());
        if (this.keyIdentifier != null) {
            buffer.append(", keyIdentifierBytes='");
            StaticUtils.toHex(this.keyIdentifier.getValue(), ":", buffer);
            buffer.append('\'');
        }
        if (this.authorityCertIssuer != null) {
            buffer.append(", authorityCertIssuer=");
            this.authorityCertIssuer.toString(buffer);
        }
        if (this.authorityCertSerialNumber != null) {
            buffer.append(", authorityCertSerialNumber='");
            StaticUtils.toHex(this.authorityCertSerialNumber.toByteArray(), ":", buffer);
            buffer.append('\'');
        }
        buffer.append(')');
    }
    
    static {
        AUTHORITY_KEY_IDENTIFIER_OID = new OID("2.5.29.35");
    }
}
