package org.bouncycastle.asn1.smime;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class SMIMECapabilities extends ASN1Object
{
    public static final ASN1ObjectIdentifier preferSignedData;
    public static final ASN1ObjectIdentifier canNotDecryptAny;
    public static final ASN1ObjectIdentifier sMIMECapabilitesVersions;
    public static final ASN1ObjectIdentifier aes256_CBC;
    public static final ASN1ObjectIdentifier aes192_CBC;
    public static final ASN1ObjectIdentifier aes128_CBC;
    public static final ASN1ObjectIdentifier idea_CBC;
    public static final ASN1ObjectIdentifier cast5_CBC;
    public static final ASN1ObjectIdentifier dES_CBC;
    public static final ASN1ObjectIdentifier dES_EDE3_CBC;
    public static final ASN1ObjectIdentifier rC2_CBC;
    private ASN1Sequence capabilities;
    
    public static SMIMECapabilities getInstance(final Object o) {
        if (o == null || o instanceof SMIMECapabilities) {
            return (SMIMECapabilities)o;
        }
        if (o instanceof ASN1Sequence) {
            return new SMIMECapabilities((ASN1Sequence)o);
        }
        if (o instanceof Attribute) {
            return new SMIMECapabilities((ASN1Sequence)((Attribute)o).getAttrValues().getObjectAt(0));
        }
        throw new IllegalArgumentException("unknown object in factory: " + o.getClass().getName());
    }
    
    public SMIMECapabilities(final ASN1Sequence capabilities) {
        this.capabilities = capabilities;
    }
    
    public Vector getCapabilities(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final Enumeration objects = this.capabilities.getObjects();
        final Vector vector = new Vector();
        if (asn1ObjectIdentifier == null) {
            while (objects.hasMoreElements()) {
                vector.addElement(SMIMECapability.getInstance(objects.nextElement()));
            }
        }
        else {
            while (objects.hasMoreElements()) {
                final SMIMECapability instance = SMIMECapability.getInstance(objects.nextElement());
                if (asn1ObjectIdentifier.equals(instance.getCapabilityID())) {
                    vector.addElement(instance);
                }
            }
        }
        return vector;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.capabilities;
    }
    
    static {
        preferSignedData = PKCSObjectIdentifiers.preferSignedData;
        canNotDecryptAny = PKCSObjectIdentifiers.canNotDecryptAny;
        sMIMECapabilitesVersions = PKCSObjectIdentifiers.sMIMECapabilitiesVersions;
        aes256_CBC = NISTObjectIdentifiers.id_aes256_CBC;
        aes192_CBC = NISTObjectIdentifiers.id_aes192_CBC;
        aes128_CBC = NISTObjectIdentifiers.id_aes128_CBC;
        idea_CBC = new ASN1ObjectIdentifier("1.3.6.1.4.1.188.7.1.1.2");
        cast5_CBC = new ASN1ObjectIdentifier("1.2.840.113533.7.66.10");
        dES_CBC = new ASN1ObjectIdentifier("1.3.14.3.2.7");
        dES_EDE3_CBC = PKCSObjectIdentifiers.des_EDE3_CBC;
        rC2_CBC = PKCSObjectIdentifiers.RC2_CBC;
    }
}
