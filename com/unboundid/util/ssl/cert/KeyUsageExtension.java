package com.unboundid.util.ssl.cert;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1BitString;
import com.unboundid.util.OID;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class KeyUsageExtension extends X509CertificateExtension
{
    public static final OID KEY_USAGE_OID;
    private static final long serialVersionUID = 5453303403925657600L;
    private final boolean crlSign;
    private final boolean dataEncipherment;
    private final boolean decipherOnly;
    private final boolean digitalSignature;
    private final boolean encipherOnly;
    private final boolean keyAgreement;
    private final boolean keyCertSign;
    private final boolean keyEncipherment;
    private final boolean nonRepudiation;
    
    KeyUsageExtension(final boolean isCritical, final boolean digitalSignature, final boolean nonRepudiation, final boolean keyEncipherment, final boolean dataEncipherment, final boolean keyAgreement, final boolean keyCertSign, final boolean crlSign, final boolean encipherOnly, final boolean decipherOnly) {
        super(KeyUsageExtension.KEY_USAGE_OID, isCritical, new ASN1BitString(new boolean[] { digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment, keyAgreement, keyCertSign, crlSign, encipherOnly, decipherOnly }).encode());
        this.digitalSignature = digitalSignature;
        this.nonRepudiation = nonRepudiation;
        this.keyEncipherment = keyEncipherment;
        this.dataEncipherment = dataEncipherment;
        this.keyAgreement = keyAgreement;
        this.keyCertSign = keyCertSign;
        this.crlSign = crlSign;
        this.encipherOnly = encipherOnly;
        this.decipherOnly = decipherOnly;
    }
    
    KeyUsageExtension(final X509CertificateExtension extension) throws CertException {
        super(extension);
        try {
            final ASN1BitString valueBitString = ASN1BitString.decodeAsBitString(extension.getValue());
            final boolean[] bits = valueBitString.getBits();
            this.digitalSignature = (bits.length > 0 && bits[0]);
            this.nonRepudiation = (bits.length > 1 && bits[1]);
            this.keyEncipherment = (bits.length > 2 && bits[2]);
            this.dataEncipherment = (bits.length > 3 && bits[3]);
            this.keyAgreement = (bits.length > 4 && bits[4]);
            this.keyCertSign = (bits.length > 5 && bits[5]);
            this.crlSign = (bits.length > 6 && bits[6]);
            this.encipherOnly = (bits.length > 7 && bits[7]);
            this.decipherOnly = (bits.length > 8 && bits[8]);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_KEY_USAGE_EXTENSION_CANNOT_PARSE.get(String.valueOf(extension), StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public boolean isDigitalSignatureBitSet() {
        return this.digitalSignature;
    }
    
    public boolean isNonRepudiationBitSet() {
        return this.nonRepudiation;
    }
    
    public boolean isKeyEnciphermentBitSet() {
        return this.keyEncipherment;
    }
    
    public boolean isDataEnciphermentBitSet() {
        return this.dataEncipherment;
    }
    
    public boolean isKeyAgreementBitSet() {
        return this.keyAgreement;
    }
    
    public boolean isKeyCertSignBitSet() {
        return this.keyCertSign;
    }
    
    public boolean isCRLSignBitSet() {
        return this.crlSign;
    }
    
    public boolean isEncipherOnlyBitSet() {
        return this.encipherOnly;
    }
    
    public boolean isDecipherOnlyBitSet() {
        return this.decipherOnly;
    }
    
    @Override
    public String getExtensionName() {
        return CertMessages.INFO_KEY_USAGE_EXTENSION_NAME.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("KeyUsageExtension(oid='");
        buffer.append(this.getOID());
        buffer.append("', isCritical=");
        buffer.append(this.isCritical());
        buffer.append(", digitalSignature=");
        buffer.append(this.digitalSignature);
        buffer.append(", nonRepudiation=");
        buffer.append(this.nonRepudiation);
        buffer.append(", keyEncipherment=");
        buffer.append(this.keyEncipherment);
        buffer.append(", dataEncipherment=");
        buffer.append(this.dataEncipherment);
        buffer.append(", keyAgreement=");
        buffer.append(this.keyAgreement);
        buffer.append(", keyCertSign=");
        buffer.append(this.keyCertSign);
        buffer.append(", clrSign=");
        buffer.append(this.crlSign);
        buffer.append(", encipherOnly=");
        buffer.append(this.encipherOnly);
        buffer.append(", decipherOnly=");
        buffer.append(this.decipherOnly);
        buffer.append(')');
    }
    
    static {
        KEY_USAGE_OID = new OID("2.5.29.15");
    }
}
