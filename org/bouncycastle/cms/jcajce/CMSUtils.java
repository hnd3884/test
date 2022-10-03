package org.bouncycastle.cms.jcajce;

import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import java.util.HashSet;
import java.io.IOException;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jcajce.util.AlgorithmParametersUtils;
import org.bouncycastle.asn1.ASN1Encodable;
import java.security.AlgorithmParameters;
import java.security.Provider;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.Extension;
import java.security.cert.CertificateEncodingException;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Set;

class CMSUtils
{
    private static final Set mqvAlgs;
    private static final Set ecAlgs;
    private static final Set gostAlgs;
    
    static boolean isMQV(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return CMSUtils.mqvAlgs.contains(asn1ObjectIdentifier);
    }
    
    static boolean isEC(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return CMSUtils.ecAlgs.contains(asn1ObjectIdentifier);
    }
    
    static boolean isGOST(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return CMSUtils.gostAlgs.contains(asn1ObjectIdentifier);
    }
    
    static boolean isRFC2631(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return asn1ObjectIdentifier.equals((Object)PKCSObjectIdentifiers.id_alg_ESDH) || asn1ObjectIdentifier.equals((Object)PKCSObjectIdentifiers.id_alg_SSDH);
    }
    
    static IssuerAndSerialNumber getIssuerAndSerialNumber(final X509Certificate x509Certificate) throws CertificateEncodingException {
        return new IssuerAndSerialNumber(Certificate.getInstance((Object)x509Certificate.getEncoded()).getIssuer(), x509Certificate.getSerialNumber());
    }
    
    static byte[] getSubjectKeyId(final X509Certificate x509Certificate) {
        final byte[] extensionValue = x509Certificate.getExtensionValue(Extension.subjectKeyIdentifier.getId());
        if (extensionValue != null) {
            return ASN1OctetString.getInstance((Object)ASN1OctetString.getInstance((Object)extensionValue).getOctets()).getOctets();
        }
        return null;
    }
    
    static EnvelopedDataHelper createContentHelper(final Provider provider) {
        if (provider != null) {
            return new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        }
        return new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
    }
    
    static EnvelopedDataHelper createContentHelper(final String s) {
        if (s != null) {
            return new EnvelopedDataHelper(new NamedJcaJceExtHelper(s));
        }
        return new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
    }
    
    static ASN1Encodable extractParameters(final AlgorithmParameters algorithmParameters) throws CMSException {
        try {
            return AlgorithmParametersUtils.extractParameters(algorithmParameters);
        }
        catch (final IOException ex) {
            throw new CMSException("cannot extract parameters: " + ex.getMessage(), ex);
        }
    }
    
    static void loadParameters(final AlgorithmParameters algorithmParameters, final ASN1Encodable asn1Encodable) throws CMSException {
        try {
            AlgorithmParametersUtils.loadParameters(algorithmParameters, asn1Encodable);
        }
        catch (final IOException ex) {
            throw new CMSException("error encoding algorithm parameters.", ex);
        }
    }
    
    static {
        mqvAlgs = new HashSet();
        ecAlgs = new HashSet();
        gostAlgs = new HashSet();
        CMSUtils.mqvAlgs.add(X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme);
        CMSUtils.mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha224kdf_scheme);
        CMSUtils.mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha256kdf_scheme);
        CMSUtils.mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha384kdf_scheme);
        CMSUtils.mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha512kdf_scheme);
        CMSUtils.ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_cofactorDH_sha1kdf_scheme);
        CMSUtils.ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme);
        CMSUtils.ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha224kdf_scheme);
        CMSUtils.ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha224kdf_scheme);
        CMSUtils.ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha256kdf_scheme);
        CMSUtils.ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha256kdf_scheme);
        CMSUtils.ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha384kdf_scheme);
        CMSUtils.ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha384kdf_scheme);
        CMSUtils.ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha512kdf_scheme);
        CMSUtils.ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha512kdf_scheme);
        CMSUtils.gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH);
        CMSUtils.gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001);
        CMSUtils.gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256);
        CMSUtils.gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512);
        CMSUtils.gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256);
        CMSUtils.gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512);
    }
}
