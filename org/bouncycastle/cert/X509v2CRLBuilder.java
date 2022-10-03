package org.bouncycastle.cert;

import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Enumeration;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import java.math.BigInteger;
import java.util.Locale;
import org.bouncycastle.asn1.x509.Time;
import java.util.Date;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.V2TBSCertListGenerator;

public class X509v2CRLBuilder
{
    private V2TBSCertListGenerator tbsGen;
    private ExtensionsGenerator extGenerator;
    
    public X509v2CRLBuilder(final X500Name issuer, final Date date) {
        this.tbsGen = new V2TBSCertListGenerator();
        this.extGenerator = new ExtensionsGenerator();
        this.tbsGen.setIssuer(issuer);
        this.tbsGen.setThisUpdate(new Time(date));
    }
    
    public X509v2CRLBuilder(final X500Name issuer, final Date date, final Locale locale) {
        this.tbsGen = new V2TBSCertListGenerator();
        this.extGenerator = new ExtensionsGenerator();
        this.tbsGen.setIssuer(issuer);
        this.tbsGen.setThisUpdate(new Time(date, locale));
    }
    
    public X509v2CRLBuilder(final X500Name issuer, final Time thisUpdate) {
        this.tbsGen = new V2TBSCertListGenerator();
        this.extGenerator = new ExtensionsGenerator();
        this.tbsGen.setIssuer(issuer);
        this.tbsGen.setThisUpdate(thisUpdate);
    }
    
    public X509v2CRLBuilder setNextUpdate(final Date date) {
        return this.setNextUpdate(new Time(date));
    }
    
    public X509v2CRLBuilder setNextUpdate(final Date date, final Locale locale) {
        return this.setNextUpdate(new Time(date, locale));
    }
    
    public X509v2CRLBuilder setNextUpdate(final Time nextUpdate) {
        this.tbsGen.setNextUpdate(nextUpdate);
        return this;
    }
    
    public X509v2CRLBuilder addCRLEntry(final BigInteger bigInteger, final Date date, final int n) {
        this.tbsGen.addCRLEntry(new ASN1Integer(bigInteger), new Time(date), n);
        return this;
    }
    
    public X509v2CRLBuilder addCRLEntry(final BigInteger bigInteger, final Date date, final int n, final Date date2) {
        this.tbsGen.addCRLEntry(new ASN1Integer(bigInteger), new Time(date), n, new ASN1GeneralizedTime(date2));
        return this;
    }
    
    public X509v2CRLBuilder addCRLEntry(final BigInteger bigInteger, final Date date, final Extensions extensions) {
        this.tbsGen.addCRLEntry(new ASN1Integer(bigInteger), new Time(date), extensions);
        return this;
    }
    
    public X509v2CRLBuilder addCRL(final X509CRLHolder x509CRLHolder) {
        final TBSCertList tbsCertList = x509CRLHolder.toASN1Structure().getTBSCertList();
        if (tbsCertList != null) {
            final Enumeration revokedCertificateEnumeration = tbsCertList.getRevokedCertificateEnumeration();
            while (revokedCertificateEnumeration.hasMoreElements()) {
                this.tbsGen.addCRLEntry(ASN1Sequence.getInstance((Object)((ASN1Encodable)revokedCertificateEnumeration.nextElement()).toASN1Primitive()));
            }
        }
        return this;
    }
    
    public X509v2CRLBuilder addExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean b, final ASN1Encodable asn1Encodable) throws CertIOException {
        CertUtils.addExtension(this.extGenerator, asn1ObjectIdentifier, b, asn1Encodable);
        return this;
    }
    
    public X509v2CRLBuilder addExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean b, final byte[] array) throws CertIOException {
        this.extGenerator.addExtension(asn1ObjectIdentifier, b, array);
        return this;
    }
    
    public X509v2CRLBuilder addExtension(final Extension extension) throws CertIOException {
        this.extGenerator.addExtension(extension);
        return this;
    }
    
    public X509CRLHolder build(final ContentSigner contentSigner) {
        this.tbsGen.setSignature(contentSigner.getAlgorithmIdentifier());
        if (!this.extGenerator.isEmpty()) {
            this.tbsGen.setExtensions(this.extGenerator.generate());
        }
        return CertUtils.generateFullCRL(contentSigner, this.tbsGen.generateTBSCertList());
    }
}
