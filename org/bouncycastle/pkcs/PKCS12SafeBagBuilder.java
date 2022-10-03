package org.bouncycastle.pkcs;

import org.bouncycastle.asn1.pkcs.SafeBag;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.pkcs.CertBag;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.cert.X509CRLHolder;
import java.io.IOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class PKCS12SafeBagBuilder
{
    private ASN1ObjectIdentifier bagType;
    private ASN1Encodable bagValue;
    private ASN1EncodableVector bagAttrs;
    
    public PKCS12SafeBagBuilder(final PrivateKeyInfo privateKeyInfo, final OutputEncryptor outputEncryptor) {
        this.bagAttrs = new ASN1EncodableVector();
        this.bagType = PKCSObjectIdentifiers.pkcs8ShroudedKeyBag;
        this.bagValue = (ASN1Encodable)new PKCS8EncryptedPrivateKeyInfoBuilder(privateKeyInfo).build(outputEncryptor).toASN1Structure();
    }
    
    public PKCS12SafeBagBuilder(final PrivateKeyInfo bagValue) {
        this.bagAttrs = new ASN1EncodableVector();
        this.bagType = PKCSObjectIdentifiers.keyBag;
        this.bagValue = (ASN1Encodable)bagValue;
    }
    
    public PKCS12SafeBagBuilder(final X509CertificateHolder x509CertificateHolder) throws IOException {
        this(x509CertificateHolder.toASN1Structure());
    }
    
    public PKCS12SafeBagBuilder(final X509CRLHolder x509CRLHolder) throws IOException {
        this(x509CRLHolder.toASN1Structure());
    }
    
    public PKCS12SafeBagBuilder(final Certificate certificate) throws IOException {
        this.bagAttrs = new ASN1EncodableVector();
        this.bagType = PKCSObjectIdentifiers.certBag;
        this.bagValue = (ASN1Encodable)new CertBag(PKCSObjectIdentifiers.x509Certificate, (ASN1Encodable)new DEROctetString(certificate.getEncoded()));
    }
    
    public PKCS12SafeBagBuilder(final CertificateList list) throws IOException {
        this.bagAttrs = new ASN1EncodableVector();
        this.bagType = PKCSObjectIdentifiers.crlBag;
        this.bagValue = (ASN1Encodable)new CertBag(PKCSObjectIdentifiers.x509Crl, (ASN1Encodable)new DEROctetString(list.getEncoded()));
    }
    
    public PKCS12SafeBagBuilder addBagAttribute(final ASN1ObjectIdentifier asn1ObjectIdentifier, final ASN1Encodable asn1Encodable) {
        this.bagAttrs.add((ASN1Encodable)new Attribute(asn1ObjectIdentifier, (ASN1Set)new DERSet(asn1Encodable)));
        return this;
    }
    
    public PKCS12SafeBag build() {
        return new PKCS12SafeBag(new SafeBag(this.bagType, this.bagValue, (ASN1Set)new DERSet(this.bagAttrs)));
    }
}
