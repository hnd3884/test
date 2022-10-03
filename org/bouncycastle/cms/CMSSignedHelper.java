package org.bouncycastle.cms;

import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import java.util.HashMap;
import org.bouncycastle.asn1.cms.OtherRevocationInfoFormat;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import java.util.Collection;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.ASN1Sequence;
import java.util.ArrayList;
import org.bouncycastle.util.Store;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Map;

class CMSSignedHelper
{
    static final CMSSignedHelper INSTANCE;
    private static final Map encryptionAlgs;
    private static final Map digestAlgs;
    private static final Map digestAliases;
    
    private static void addEntries(final ASN1ObjectIdentifier asn1ObjectIdentifier, final String s, final String s2) {
        CMSSignedHelper.digestAlgs.put(asn1ObjectIdentifier.getId(), s);
        CMSSignedHelper.encryptionAlgs.put(asn1ObjectIdentifier.getId(), s2);
    }
    
    String getEncryptionAlgName(final String s) {
        final String s2 = CMSSignedHelper.encryptionAlgs.get(s);
        if (s2 != null) {
            return s2;
        }
        return s;
    }
    
    AlgorithmIdentifier fixAlgID(final AlgorithmIdentifier algorithmIdentifier) {
        if (algorithmIdentifier.getParameters() == null) {
            return new AlgorithmIdentifier(algorithmIdentifier.getAlgorithm(), (ASN1Encodable)DERNull.INSTANCE);
        }
        return algorithmIdentifier;
    }
    
    void setSigningEncryptionAlgorithmMapping(final ASN1ObjectIdentifier asn1ObjectIdentifier, final String s) {
        CMSSignedHelper.encryptionAlgs.put(asn1ObjectIdentifier.getId(), s);
    }
    
    void setSigningDigestAlgorithmMapping(final ASN1ObjectIdentifier asn1ObjectIdentifier, final String s) {
        CMSSignedHelper.digestAlgs.put(asn1ObjectIdentifier.getId(), s);
    }
    
    Store getCertificates(final ASN1Set set) {
        if (set != null) {
            final ArrayList list = new ArrayList(set.size());
            final Enumeration objects = set.getObjects();
            while (objects.hasMoreElements()) {
                final ASN1Primitive asn1Primitive = objects.nextElement().toASN1Primitive();
                if (asn1Primitive instanceof ASN1Sequence) {
                    list.add(new X509CertificateHolder(Certificate.getInstance((Object)asn1Primitive)));
                }
            }
            return (Store)new CollectionStore((Collection)list);
        }
        return (Store)new CollectionStore((Collection)new ArrayList());
    }
    
    Store getAttributeCertificates(final ASN1Set set) {
        if (set != null) {
            final ArrayList list = new ArrayList(set.size());
            final Enumeration objects = set.getObjects();
            while (objects.hasMoreElements()) {
                final ASN1Primitive asn1Primitive = objects.nextElement().toASN1Primitive();
                if (asn1Primitive instanceof ASN1TaggedObject) {
                    list.add(new X509AttributeCertificateHolder(AttributeCertificate.getInstance((Object)((ASN1TaggedObject)asn1Primitive).getObject())));
                }
            }
            return (Store)new CollectionStore((Collection)list);
        }
        return (Store)new CollectionStore((Collection)new ArrayList());
    }
    
    Store getCRLs(final ASN1Set set) {
        if (set != null) {
            final ArrayList list = new ArrayList(set.size());
            final Enumeration objects = set.getObjects();
            while (objects.hasMoreElements()) {
                final ASN1Primitive asn1Primitive = objects.nextElement().toASN1Primitive();
                if (asn1Primitive instanceof ASN1Sequence) {
                    list.add(new X509CRLHolder(CertificateList.getInstance((Object)asn1Primitive)));
                }
            }
            return (Store)new CollectionStore((Collection)list);
        }
        return (Store)new CollectionStore((Collection)new ArrayList());
    }
    
    Store getOtherRevocationInfo(final ASN1ObjectIdentifier asn1ObjectIdentifier, final ASN1Set set) {
        if (set != null) {
            final ArrayList list = new ArrayList(set.size());
            final Enumeration objects = set.getObjects();
            while (objects.hasMoreElements()) {
                final ASN1Primitive asn1Primitive = objects.nextElement().toASN1Primitive();
                if (asn1Primitive instanceof ASN1TaggedObject) {
                    final ASN1TaggedObject instance = ASN1TaggedObject.getInstance((Object)asn1Primitive);
                    if (instance.getTagNo() != 1) {
                        continue;
                    }
                    final OtherRevocationInfoFormat instance2 = OtherRevocationInfoFormat.getInstance(instance, false);
                    if (!asn1ObjectIdentifier.equals((Object)instance2.getInfoFormat())) {
                        continue;
                    }
                    list.add(instance2.getInfo());
                }
            }
            return (Store)new CollectionStore((Collection)list);
        }
        return (Store)new CollectionStore((Collection)new ArrayList());
    }
    
    static {
        INSTANCE = new CMSSignedHelper();
        encryptionAlgs = new HashMap();
        digestAlgs = new HashMap();
        digestAliases = new HashMap();
        addEntries(NISTObjectIdentifiers.dsa_with_sha224, "SHA224", "DSA");
        addEntries(NISTObjectIdentifiers.dsa_with_sha256, "SHA256", "DSA");
        addEntries(NISTObjectIdentifiers.dsa_with_sha384, "SHA384", "DSA");
        addEntries(NISTObjectIdentifiers.dsa_with_sha512, "SHA512", "DSA");
        addEntries(OIWObjectIdentifiers.dsaWithSHA1, "SHA1", "DSA");
        addEntries(OIWObjectIdentifiers.md4WithRSA, "MD4", "RSA");
        addEntries(OIWObjectIdentifiers.md4WithRSAEncryption, "MD4", "RSA");
        addEntries(OIWObjectIdentifiers.md5WithRSA, "MD5", "RSA");
        addEntries(OIWObjectIdentifiers.sha1WithRSA, "SHA1", "RSA");
        addEntries(PKCSObjectIdentifiers.md2WithRSAEncryption, "MD2", "RSA");
        addEntries(PKCSObjectIdentifiers.md4WithRSAEncryption, "MD4", "RSA");
        addEntries(PKCSObjectIdentifiers.md5WithRSAEncryption, "MD5", "RSA");
        addEntries(PKCSObjectIdentifiers.sha1WithRSAEncryption, "SHA1", "RSA");
        addEntries(PKCSObjectIdentifiers.sha224WithRSAEncryption, "SHA224", "RSA");
        addEntries(PKCSObjectIdentifiers.sha256WithRSAEncryption, "SHA256", "RSA");
        addEntries(PKCSObjectIdentifiers.sha384WithRSAEncryption, "SHA384", "RSA");
        addEntries(PKCSObjectIdentifiers.sha512WithRSAEncryption, "SHA512", "RSA");
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA1, "SHA1", "ECDSA");
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA224, "SHA224", "ECDSA");
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA256, "SHA256", "ECDSA");
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA384, "SHA384", "ECDSA");
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA512, "SHA512", "ECDSA");
        addEntries(X9ObjectIdentifiers.id_dsa_with_sha1, "SHA1", "DSA");
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_1, "SHA1", "ECDSA");
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_224, "SHA224", "ECDSA");
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_256, "SHA256", "ECDSA");
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_384, "SHA384", "ECDSA");
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_512, "SHA512", "ECDSA");
        addEntries(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_1, "SHA1", "RSA");
        addEntries(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_256, "SHA256", "RSA");
        addEntries(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_1, "SHA1", "RSAandMGF1");
        addEntries(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_256, "SHA256", "RSAandMGF1");
        CMSSignedHelper.encryptionAlgs.put(X9ObjectIdentifiers.id_dsa.getId(), "DSA");
        CMSSignedHelper.encryptionAlgs.put(PKCSObjectIdentifiers.rsaEncryption.getId(), "RSA");
        CMSSignedHelper.encryptionAlgs.put(TeleTrusTObjectIdentifiers.teleTrusTRSAsignatureAlgorithm, "RSA");
        CMSSignedHelper.encryptionAlgs.put(X509ObjectIdentifiers.id_ea_rsa.getId(), "RSA");
        CMSSignedHelper.encryptionAlgs.put(CMSSignedDataGenerator.ENCRYPTION_RSA_PSS, "RSAandMGF1");
        CMSSignedHelper.encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3410_94.getId(), "GOST3410");
        CMSSignedHelper.encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3410_2001.getId(), "ECGOST3410");
        CMSSignedHelper.encryptionAlgs.put("1.3.6.1.4.1.5849.1.6.2", "ECGOST3410");
        CMSSignedHelper.encryptionAlgs.put("1.3.6.1.4.1.5849.1.1.5", "GOST3410");
        CMSSignedHelper.encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, "ECGOST3410-2012-256");
        CMSSignedHelper.encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, "ECGOST3410-2012-512");
        CMSSignedHelper.encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001.getId(), "ECGOST3410");
        CMSSignedHelper.encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94.getId(), "GOST3410");
        CMSSignedHelper.encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, "ECGOST3410-2012-256");
        CMSSignedHelper.encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, "ECGOST3410-2012-512");
        CMSSignedHelper.digestAlgs.put(PKCSObjectIdentifiers.md2.getId(), "MD2");
        CMSSignedHelper.digestAlgs.put(PKCSObjectIdentifiers.md4.getId(), "MD4");
        CMSSignedHelper.digestAlgs.put(PKCSObjectIdentifiers.md5.getId(), "MD5");
        CMSSignedHelper.digestAlgs.put(OIWObjectIdentifiers.idSHA1.getId(), "SHA1");
        CMSSignedHelper.digestAlgs.put(NISTObjectIdentifiers.id_sha224.getId(), "SHA224");
        CMSSignedHelper.digestAlgs.put(NISTObjectIdentifiers.id_sha256.getId(), "SHA256");
        CMSSignedHelper.digestAlgs.put(NISTObjectIdentifiers.id_sha384.getId(), "SHA384");
        CMSSignedHelper.digestAlgs.put(NISTObjectIdentifiers.id_sha512.getId(), "SHA512");
        CMSSignedHelper.digestAlgs.put(TeleTrusTObjectIdentifiers.ripemd128.getId(), "RIPEMD128");
        CMSSignedHelper.digestAlgs.put(TeleTrusTObjectIdentifiers.ripemd160.getId(), "RIPEMD160");
        CMSSignedHelper.digestAlgs.put(TeleTrusTObjectIdentifiers.ripemd256.getId(), "RIPEMD256");
        CMSSignedHelper.digestAlgs.put(CryptoProObjectIdentifiers.gostR3411.getId(), "GOST3411");
        CMSSignedHelper.digestAlgs.put("1.3.6.1.4.1.5849.1.2.1", "GOST3411");
        CMSSignedHelper.digestAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256, "GOST3411-2012-256");
        CMSSignedHelper.digestAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512, "GOST3411-2012-512");
        CMSSignedHelper.digestAliases.put("SHA1", new String[] { "SHA-1" });
        CMSSignedHelper.digestAliases.put("SHA224", new String[] { "SHA-224" });
        CMSSignedHelper.digestAliases.put("SHA256", new String[] { "SHA-256" });
        CMSSignedHelper.digestAliases.put("SHA384", new String[] { "SHA-384" });
        CMSSignedHelper.digestAliases.put("SHA512", new String[] { "SHA-512" });
    }
}
