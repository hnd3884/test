package org.bouncycastle.cert;

import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Locale;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x509.AttCertIssuer;
import java.util.Date;
import java.math.BigInteger;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.V2AttributeCertificateInfoGenerator;

public class X509v2AttributeCertificateBuilder
{
    private V2AttributeCertificateInfoGenerator acInfoGen;
    private ExtensionsGenerator extGenerator;
    
    public X509v2AttributeCertificateBuilder(final AttributeCertificateHolder attributeCertificateHolder, final AttributeCertificateIssuer attributeCertificateIssuer, final BigInteger bigInteger, final Date date, final Date date2) {
        this.acInfoGen = new V2AttributeCertificateInfoGenerator();
        this.extGenerator = new ExtensionsGenerator();
        this.acInfoGen.setHolder(attributeCertificateHolder.holder);
        this.acInfoGen.setIssuer(AttCertIssuer.getInstance((Object)attributeCertificateIssuer.form));
        this.acInfoGen.setSerialNumber(new ASN1Integer(bigInteger));
        this.acInfoGen.setStartDate(new ASN1GeneralizedTime(date));
        this.acInfoGen.setEndDate(new ASN1GeneralizedTime(date2));
    }
    
    public X509v2AttributeCertificateBuilder(final AttributeCertificateHolder attributeCertificateHolder, final AttributeCertificateIssuer attributeCertificateIssuer, final BigInteger bigInteger, final Date date, final Date date2, final Locale locale) {
        this.acInfoGen = new V2AttributeCertificateInfoGenerator();
        this.extGenerator = new ExtensionsGenerator();
        this.acInfoGen.setHolder(attributeCertificateHolder.holder);
        this.acInfoGen.setIssuer(AttCertIssuer.getInstance((Object)attributeCertificateIssuer.form));
        this.acInfoGen.setSerialNumber(new ASN1Integer(bigInteger));
        this.acInfoGen.setStartDate(new ASN1GeneralizedTime(date, locale));
        this.acInfoGen.setEndDate(new ASN1GeneralizedTime(date2, locale));
    }
    
    public X509v2AttributeCertificateBuilder addAttribute(final ASN1ObjectIdentifier asn1ObjectIdentifier, final ASN1Encodable asn1Encodable) {
        this.acInfoGen.addAttribute(new Attribute(asn1ObjectIdentifier, (ASN1Set)new DERSet(asn1Encodable)));
        return this;
    }
    
    public X509v2AttributeCertificateBuilder addAttribute(final ASN1ObjectIdentifier asn1ObjectIdentifier, final ASN1Encodable[] array) {
        this.acInfoGen.addAttribute(new Attribute(asn1ObjectIdentifier, (ASN1Set)new DERSet(array)));
        return this;
    }
    
    public void setIssuerUniqueId(final boolean[] array) {
        this.acInfoGen.setIssuerUniqueID(CertUtils.booleanToBitString(array));
    }
    
    public X509v2AttributeCertificateBuilder addExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean b, final ASN1Encodable asn1Encodable) throws CertIOException {
        CertUtils.addExtension(this.extGenerator, asn1ObjectIdentifier, b, asn1Encodable);
        return this;
    }
    
    public X509v2AttributeCertificateBuilder addExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean b, final byte[] array) throws CertIOException {
        this.extGenerator.addExtension(asn1ObjectIdentifier, b, array);
        return this;
    }
    
    public X509v2AttributeCertificateBuilder addExtension(final Extension extension) throws CertIOException {
        this.extGenerator.addExtension(extension);
        return this;
    }
    
    public X509AttributeCertificateHolder build(final ContentSigner contentSigner) {
        this.acInfoGen.setSignature(contentSigner.getAlgorithmIdentifier());
        if (!this.extGenerator.isEmpty()) {
            this.acInfoGen.setExtensions(this.extGenerator.generate());
        }
        return CertUtils.generateFullAttrCert(contentSigner, this.acInfoGen.generateAttributeCertificateInfo());
    }
}
