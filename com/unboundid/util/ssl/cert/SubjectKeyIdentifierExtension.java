package com.unboundid.util.ssl.cert;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.OID;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SubjectKeyIdentifierExtension extends X509CertificateExtension
{
    public static final OID SUBJECT_KEY_IDENTIFIER_OID;
    static final String SUBJECT_KEY_IDENTIFIER_DIGEST_ALGORITHM = "SHA-1";
    private static final long serialVersionUID = -7175921866230880172L;
    private final ASN1OctetString keyIdentifier;
    
    SubjectKeyIdentifierExtension(final boolean isCritical, final ASN1OctetString keyIdentifier) {
        super(SubjectKeyIdentifierExtension.SUBJECT_KEY_IDENTIFIER_OID, isCritical, keyIdentifier.encode());
        this.keyIdentifier = keyIdentifier;
    }
    
    SubjectKeyIdentifierExtension(final X509CertificateExtension extension) throws CertException {
        super(extension);
        try {
            this.keyIdentifier = ASN1OctetString.decodeAsOctetString(extension.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_SUBJECT_KEY_ID_EXTENSION_CANNOT_PARSE.get(String.valueOf(extension), StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public ASN1OctetString getKeyIdentifier() {
        return this.keyIdentifier;
    }
    
    @Override
    public String getExtensionName() {
        return CertMessages.INFO_SUBJECT_KEY_IDENTIFIER_EXTENSION_NAME.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("SubjectKeyIdentifierExtension(oid='");
        buffer.append(this.getOID());
        buffer.append(", isCritical=");
        buffer.append(this.isCritical());
        buffer.append(", identifierBytes='");
        StaticUtils.toHex(this.keyIdentifier.getValue(), ":", buffer);
        buffer.append("')");
    }
    
    static {
        SUBJECT_KEY_IDENTIFIER_OID = new OID("2.5.29.14");
    }
}
