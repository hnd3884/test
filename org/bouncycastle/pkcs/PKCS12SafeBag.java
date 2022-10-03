package org.bouncycastle.pkcs;

import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.pkcs.CRLBag;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.pkcs.CertBag;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.SafeBag;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class PKCS12SafeBag
{
    public static final ASN1ObjectIdentifier friendlyNameAttribute;
    public static final ASN1ObjectIdentifier localKeyIdAttribute;
    private SafeBag safeBag;
    
    public PKCS12SafeBag(final SafeBag safeBag) {
        this.safeBag = safeBag;
    }
    
    public SafeBag toASN1Structure() {
        return this.safeBag;
    }
    
    public ASN1ObjectIdentifier getType() {
        return this.safeBag.getBagId();
    }
    
    public Attribute[] getAttributes() {
        final ASN1Set bagAttributes = this.safeBag.getBagAttributes();
        if (bagAttributes == null) {
            return null;
        }
        final Attribute[] array = new Attribute[bagAttributes.size()];
        for (int i = 0; i != bagAttributes.size(); ++i) {
            array[i] = Attribute.getInstance((Object)bagAttributes.getObjectAt(i));
        }
        return array;
    }
    
    public Object getBagValue() {
        if (this.getType().equals((Object)PKCSObjectIdentifiers.pkcs8ShroudedKeyBag)) {
            return new PKCS8EncryptedPrivateKeyInfo(EncryptedPrivateKeyInfo.getInstance((Object)this.safeBag.getBagValue()));
        }
        if (this.getType().equals((Object)PKCSObjectIdentifiers.certBag)) {
            return new X509CertificateHolder(Certificate.getInstance((Object)ASN1OctetString.getInstance((Object)CertBag.getInstance((Object)this.safeBag.getBagValue()).getCertValue()).getOctets()));
        }
        if (this.getType().equals((Object)PKCSObjectIdentifiers.keyBag)) {
            return PrivateKeyInfo.getInstance((Object)this.safeBag.getBagValue());
        }
        if (this.getType().equals((Object)PKCSObjectIdentifiers.crlBag)) {
            return new X509CRLHolder(CertificateList.getInstance((Object)ASN1OctetString.getInstance((Object)CRLBag.getInstance((Object)this.safeBag.getBagValue()).getCrlValue()).getOctets()));
        }
        return this.safeBag.getBagValue();
    }
    
    static {
        friendlyNameAttribute = PKCSObjectIdentifiers.pkcs_9_at_friendlyName;
        localKeyIdAttribute = PKCSObjectIdentifiers.pkcs_9_at_localKeyId;
    }
}
