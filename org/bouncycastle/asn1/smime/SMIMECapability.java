package org.bouncycastle.asn1.smime;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class SMIMECapability extends ASN1Object
{
    public static final ASN1ObjectIdentifier preferSignedData;
    public static final ASN1ObjectIdentifier canNotDecryptAny;
    public static final ASN1ObjectIdentifier sMIMECapabilitiesVersions;
    public static final ASN1ObjectIdentifier dES_CBC;
    public static final ASN1ObjectIdentifier dES_EDE3_CBC;
    public static final ASN1ObjectIdentifier rC2_CBC;
    public static final ASN1ObjectIdentifier aES128_CBC;
    public static final ASN1ObjectIdentifier aES192_CBC;
    public static final ASN1ObjectIdentifier aES256_CBC;
    private ASN1ObjectIdentifier capabilityID;
    private ASN1Encodable parameters;
    
    public SMIMECapability(final ASN1Sequence asn1Sequence) {
        this.capabilityID = (ASN1ObjectIdentifier)asn1Sequence.getObjectAt(0);
        if (asn1Sequence.size() > 1) {
            this.parameters = asn1Sequence.getObjectAt(1);
        }
    }
    
    public SMIMECapability(final ASN1ObjectIdentifier capabilityID, final ASN1Encodable parameters) {
        this.capabilityID = capabilityID;
        this.parameters = parameters;
    }
    
    public static SMIMECapability getInstance(final Object o) {
        if (o == null || o instanceof SMIMECapability) {
            return (SMIMECapability)o;
        }
        if (o instanceof ASN1Sequence) {
            return new SMIMECapability((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("Invalid SMIMECapability");
    }
    
    public ASN1ObjectIdentifier getCapabilityID() {
        return this.capabilityID;
    }
    
    public ASN1Encodable getParameters() {
        return this.parameters;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.capabilityID);
        if (this.parameters != null) {
            asn1EncodableVector.add(this.parameters);
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    static {
        preferSignedData = PKCSObjectIdentifiers.preferSignedData;
        canNotDecryptAny = PKCSObjectIdentifiers.canNotDecryptAny;
        sMIMECapabilitiesVersions = PKCSObjectIdentifiers.sMIMECapabilitiesVersions;
        dES_CBC = new ASN1ObjectIdentifier("1.3.14.3.2.7");
        dES_EDE3_CBC = PKCSObjectIdentifiers.des_EDE3_CBC;
        rC2_CBC = PKCSObjectIdentifiers.RC2_CBC;
        aES128_CBC = NISTObjectIdentifiers.id_aes128_CBC;
        aES192_CBC = NISTObjectIdentifiers.id_aes192_CBC;
        aES256_CBC = NISTObjectIdentifiers.id_aes256_CBC;
    }
}
