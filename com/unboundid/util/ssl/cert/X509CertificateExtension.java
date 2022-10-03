package com.unboundid.util.ssl.cert;

import java.util.Collection;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1ObjectIdentifier;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.OID;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;
import java.io.Serializable;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class X509CertificateExtension implements Serializable
{
    private static final long serialVersionUID = -4044598072050168580L;
    private final boolean isCritical;
    private final byte[] value;
    private final OID oid;
    
    protected X509CertificateExtension(final X509CertificateExtension extension) {
        this.oid = extension.oid;
        this.isCritical = extension.isCritical;
        this.value = extension.value;
    }
    
    public X509CertificateExtension(final OID oid, final boolean isCritical, final byte[] value) {
        this.oid = oid;
        this.isCritical = isCritical;
        this.value = value;
    }
    
    static X509CertificateExtension decode(final ASN1Element extensionElement) throws CertException {
        OID oid;
        X509CertificateExtension extension;
        try {
            final ASN1Element[] elements = extensionElement.decodeAsSequence().elements();
            oid = elements[0].decodeAsObjectIdentifier().getOID();
            boolean isCritical;
            byte[] value;
            if (elements[1].getType() == 1) {
                isCritical = elements[1].decodeAsBoolean().booleanValue();
                value = elements[2].decodeAsOctetString().getValue();
            }
            else {
                isCritical = false;
                value = elements[1].decodeAsOctetString().getValue();
            }
            extension = new X509CertificateExtension(oid, isCritical, value);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_EXTENSION_DECODE_ERROR.get(StaticUtils.getExceptionMessage(e)), e);
        }
        if (oid.equals(AuthorityKeyIdentifierExtension.AUTHORITY_KEY_IDENTIFIER_OID)) {
            try {
                return new AuthorityKeyIdentifierExtension(extension);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                return extension;
            }
        }
        if (oid.equals(SubjectKeyIdentifierExtension.SUBJECT_KEY_IDENTIFIER_OID)) {
            try {
                return new SubjectKeyIdentifierExtension(extension);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                return extension;
            }
        }
        if (oid.equals(KeyUsageExtension.KEY_USAGE_OID)) {
            try {
                return new KeyUsageExtension(extension);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                return extension;
            }
        }
        if (oid.equals(SubjectAlternativeNameExtension.SUBJECT_ALTERNATIVE_NAME_OID)) {
            try {
                return new SubjectAlternativeNameExtension(extension);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                return extension;
            }
        }
        if (oid.equals(IssuerAlternativeNameExtension.ISSUER_ALTERNATIVE_NAME_OID)) {
            try {
                return new IssuerAlternativeNameExtension(extension);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                return extension;
            }
        }
        if (oid.equals(BasicConstraintsExtension.BASIC_CONSTRAINTS_OID)) {
            try {
                return new BasicConstraintsExtension(extension);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                return extension;
            }
        }
        if (oid.equals(ExtendedKeyUsageExtension.EXTENDED_KEY_USAGE_OID)) {
            try {
                return new ExtendedKeyUsageExtension(extension);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                return extension;
            }
        }
        if (oid.equals(CRLDistributionPointsExtension.CRL_DISTRIBUTION_POINTS_OID)) {
            try {
                return new CRLDistributionPointsExtension(extension);
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        return extension;
    }
    
    public final OID getOID() {
        return this.oid;
    }
    
    public final boolean isCritical() {
        return this.isCritical;
    }
    
    public final byte[] getValue() {
        return this.value;
    }
    
    ASN1Sequence encode() throws CertException {
        try {
            final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(3);
            elements.add(new ASN1ObjectIdentifier(this.oid));
            if (this.isCritical) {
                elements.add(ASN1Boolean.UNIVERSAL_BOOLEAN_TRUE_ELEMENT);
            }
            elements.add(new ASN1OctetString(this.value));
            return new ASN1Sequence(elements);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_EXTENSION_ENCODE_ERROR.get(this.toString(), StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public String getExtensionName() {
        return this.oid.toString();
    }
    
    @Override
    public final String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("X509CertificateExtension(oid='");
        buffer.append(this.oid.toString());
        buffer.append("', isCritical=");
        buffer.append(this.isCritical);
        if (StaticUtils.isPrintableString(this.value)) {
            buffer.append(", value='");
            buffer.append(StaticUtils.toUTF8String(this.value));
            buffer.append('\'');
        }
        else {
            buffer.append(", valueLength=");
            buffer.append(this.value.length);
        }
        buffer.append(')');
    }
}
