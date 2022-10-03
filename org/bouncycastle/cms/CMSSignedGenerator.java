package org.bouncycastle.cms;

import java.util.HashSet;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import java.util.Iterator;
import org.bouncycastle.asn1.cms.OtherRevocationInfoFormat;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import java.util.Collection;
import org.bouncycastle.util.Store;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CMSSignedGenerator
{
    public static final String DATA;
    public static final String DIGEST_SHA1;
    public static final String DIGEST_SHA224;
    public static final String DIGEST_SHA256;
    public static final String DIGEST_SHA384;
    public static final String DIGEST_SHA512;
    public static final String DIGEST_MD5;
    public static final String DIGEST_GOST3411;
    public static final String DIGEST_RIPEMD128;
    public static final String DIGEST_RIPEMD160;
    public static final String DIGEST_RIPEMD256;
    public static final String ENCRYPTION_RSA;
    public static final String ENCRYPTION_DSA;
    public static final String ENCRYPTION_ECDSA;
    public static final String ENCRYPTION_RSA_PSS;
    public static final String ENCRYPTION_GOST3410;
    public static final String ENCRYPTION_ECGOST3410;
    public static final String ENCRYPTION_ECGOST3410_2012_256;
    public static final String ENCRYPTION_ECGOST3410_2012_512;
    private static final String ENCRYPTION_ECDSA_WITH_SHA1;
    private static final String ENCRYPTION_ECDSA_WITH_SHA224;
    private static final String ENCRYPTION_ECDSA_WITH_SHA256;
    private static final String ENCRYPTION_ECDSA_WITH_SHA384;
    private static final String ENCRYPTION_ECDSA_WITH_SHA512;
    private static final Set NO_PARAMS;
    private static final Map EC_ALGORITHMS;
    protected List certs;
    protected List crls;
    protected List _signers;
    protected List signerGens;
    protected Map digests;
    
    protected CMSSignedGenerator() {
        this.certs = new ArrayList();
        this.crls = new ArrayList();
        this._signers = new ArrayList();
        this.signerGens = new ArrayList();
        this.digests = new HashMap();
    }
    
    protected Map getBaseParameters(final ASN1ObjectIdentifier asn1ObjectIdentifier, final AlgorithmIdentifier algorithmIdentifier, final byte[] array) {
        final HashMap hashMap = new HashMap();
        hashMap.put("contentType", asn1ObjectIdentifier);
        hashMap.put("digestAlgID", algorithmIdentifier);
        hashMap.put("digest", Arrays.clone(array));
        return hashMap;
    }
    
    public void addCertificate(final X509CertificateHolder x509CertificateHolder) throws CMSException {
        this.certs.add(x509CertificateHolder.toASN1Structure());
    }
    
    public void addCertificates(final Store store) throws CMSException {
        this.certs.addAll(CMSUtils.getCertificatesFromStore(store));
    }
    
    public void addCRL(final X509CRLHolder x509CRLHolder) {
        this.crls.add(x509CRLHolder.toASN1Structure());
    }
    
    public void addCRLs(final Store store) throws CMSException {
        this.crls.addAll(CMSUtils.getCRLsFromStore(store));
    }
    
    public void addAttributeCertificate(final X509AttributeCertificateHolder x509AttributeCertificateHolder) throws CMSException {
        this.certs.add(new DERTaggedObject(false, 2, (ASN1Encodable)x509AttributeCertificateHolder.toASN1Structure()));
    }
    
    public void addAttributeCertificates(final Store store) throws CMSException {
        this.certs.addAll(CMSUtils.getAttributeCertificatesFromStore(store));
    }
    
    public void addOtherRevocationInfo(final ASN1ObjectIdentifier asn1ObjectIdentifier, final ASN1Encodable asn1Encodable) {
        this.crls.add(new DERTaggedObject(false, 1, (ASN1Encodable)new OtherRevocationInfoFormat(asn1ObjectIdentifier, asn1Encodable)));
    }
    
    public void addOtherRevocationInfo(final ASN1ObjectIdentifier asn1ObjectIdentifier, final Store store) {
        this.crls.addAll(CMSUtils.getOthersFromStore(asn1ObjectIdentifier, store));
    }
    
    public void addSigners(final SignerInformationStore signerInformationStore) {
        final Iterator<SignerInformation> iterator = signerInformationStore.getSigners().iterator();
        while (iterator.hasNext()) {
            this._signers.add(iterator.next());
        }
    }
    
    public void addSignerInfoGenerator(final SignerInfoGenerator signerInfoGenerator) {
        this.signerGens.add(signerInfoGenerator);
    }
    
    public Map getGeneratedDigests() {
        return new HashMap(this.digests);
    }
    
    static {
        DATA = CMSObjectIdentifiers.data.getId();
        DIGEST_SHA1 = OIWObjectIdentifiers.idSHA1.getId();
        DIGEST_SHA224 = NISTObjectIdentifiers.id_sha224.getId();
        DIGEST_SHA256 = NISTObjectIdentifiers.id_sha256.getId();
        DIGEST_SHA384 = NISTObjectIdentifiers.id_sha384.getId();
        DIGEST_SHA512 = NISTObjectIdentifiers.id_sha512.getId();
        DIGEST_MD5 = PKCSObjectIdentifiers.md5.getId();
        DIGEST_GOST3411 = CryptoProObjectIdentifiers.gostR3411.getId();
        DIGEST_RIPEMD128 = TeleTrusTObjectIdentifiers.ripemd128.getId();
        DIGEST_RIPEMD160 = TeleTrusTObjectIdentifiers.ripemd160.getId();
        DIGEST_RIPEMD256 = TeleTrusTObjectIdentifiers.ripemd256.getId();
        ENCRYPTION_RSA = PKCSObjectIdentifiers.rsaEncryption.getId();
        ENCRYPTION_DSA = X9ObjectIdentifiers.id_dsa_with_sha1.getId();
        ENCRYPTION_ECDSA = X9ObjectIdentifiers.ecdsa_with_SHA1.getId();
        ENCRYPTION_RSA_PSS = PKCSObjectIdentifiers.id_RSASSA_PSS.getId();
        ENCRYPTION_GOST3410 = CryptoProObjectIdentifiers.gostR3410_94.getId();
        ENCRYPTION_ECGOST3410 = CryptoProObjectIdentifiers.gostR3410_2001.getId();
        ENCRYPTION_ECGOST3410_2012_256 = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256.getId();
        ENCRYPTION_ECGOST3410_2012_512 = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512.getId();
        ENCRYPTION_ECDSA_WITH_SHA1 = X9ObjectIdentifiers.ecdsa_with_SHA1.getId();
        ENCRYPTION_ECDSA_WITH_SHA224 = X9ObjectIdentifiers.ecdsa_with_SHA224.getId();
        ENCRYPTION_ECDSA_WITH_SHA256 = X9ObjectIdentifiers.ecdsa_with_SHA256.getId();
        ENCRYPTION_ECDSA_WITH_SHA384 = X9ObjectIdentifiers.ecdsa_with_SHA384.getId();
        ENCRYPTION_ECDSA_WITH_SHA512 = X9ObjectIdentifiers.ecdsa_with_SHA512.getId();
        NO_PARAMS = new HashSet();
        EC_ALGORITHMS = new HashMap();
        CMSSignedGenerator.NO_PARAMS.add(CMSSignedGenerator.ENCRYPTION_DSA);
        CMSSignedGenerator.NO_PARAMS.add(CMSSignedGenerator.ENCRYPTION_ECDSA);
        CMSSignedGenerator.NO_PARAMS.add(CMSSignedGenerator.ENCRYPTION_ECDSA_WITH_SHA1);
        CMSSignedGenerator.NO_PARAMS.add(CMSSignedGenerator.ENCRYPTION_ECDSA_WITH_SHA224);
        CMSSignedGenerator.NO_PARAMS.add(CMSSignedGenerator.ENCRYPTION_ECDSA_WITH_SHA256);
        CMSSignedGenerator.NO_PARAMS.add(CMSSignedGenerator.ENCRYPTION_ECDSA_WITH_SHA384);
        CMSSignedGenerator.NO_PARAMS.add(CMSSignedGenerator.ENCRYPTION_ECDSA_WITH_SHA512);
        CMSSignedGenerator.EC_ALGORITHMS.put(CMSSignedGenerator.DIGEST_SHA1, CMSSignedGenerator.ENCRYPTION_ECDSA_WITH_SHA1);
        CMSSignedGenerator.EC_ALGORITHMS.put(CMSSignedGenerator.DIGEST_SHA224, CMSSignedGenerator.ENCRYPTION_ECDSA_WITH_SHA224);
        CMSSignedGenerator.EC_ALGORITHMS.put(CMSSignedGenerator.DIGEST_SHA256, CMSSignedGenerator.ENCRYPTION_ECDSA_WITH_SHA256);
        CMSSignedGenerator.EC_ALGORITHMS.put(CMSSignedGenerator.DIGEST_SHA384, CMSSignedGenerator.ENCRYPTION_ECDSA_WITH_SHA384);
        CMSSignedGenerator.EC_ALGORITHMS.put(CMSSignedGenerator.DIGEST_SHA512, CMSSignedGenerator.ENCRYPTION_ECDSA_WITH_SHA512);
    }
}
