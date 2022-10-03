package org.bouncycastle.jce.provider;

import java.util.HashMap;
import java.security.PrivateKey;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import java.io.IOException;
import java.security.PublicKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.util.Iterator;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.pqc.jcajce.provider.rainbow.RainbowKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.xmss.XMSSKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.newhope.NHKeyFactorySpi;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.pqc.jcajce.provider.sphincs.Sphincs256KeyFactorySpi;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.ClassUtil;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import java.security.Provider;

public final class BouncyCastleProvider extends Provider implements ConfigurableProvider
{
    private static String info;
    public static final String PROVIDER_NAME = "BC";
    public static final ProviderConfiguration CONFIGURATION;
    private static final Map keyInfoConverters;
    private static final String SYMMETRIC_PACKAGE = "org.bouncycastle.jcajce.provider.symmetric.";
    private static final String[] SYMMETRIC_GENERIC;
    private static final String[] SYMMETRIC_MACS;
    private static final String[] SYMMETRIC_CIPHERS;
    private static final String ASYMMETRIC_PACKAGE = "org.bouncycastle.jcajce.provider.asymmetric.";
    private static final String[] ASYMMETRIC_GENERIC;
    private static final String[] ASYMMETRIC_CIPHERS;
    private static final String DIGEST_PACKAGE = "org.bouncycastle.jcajce.provider.digest.";
    private static final String[] DIGESTS;
    private static final String KEYSTORE_PACKAGE = "org.bouncycastle.jcajce.provider.keystore.";
    private static final String[] KEYSTORES;
    private static final String SECURE_RANDOM_PACKAGE = "org.bouncycastle.jcajce.provider.drbg.";
    private static final String[] SECURE_RANDOMS;
    
    public BouncyCastleProvider() {
        super("BC", 1.59, BouncyCastleProvider.info);
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            public Object run() {
                BouncyCastleProvider.this.setup();
                return null;
            }
        });
    }
    
    private void setup() {
        this.loadAlgorithms("org.bouncycastle.jcajce.provider.digest.", BouncyCastleProvider.DIGESTS);
        this.loadAlgorithms("org.bouncycastle.jcajce.provider.symmetric.", BouncyCastleProvider.SYMMETRIC_GENERIC);
        this.loadAlgorithms("org.bouncycastle.jcajce.provider.symmetric.", BouncyCastleProvider.SYMMETRIC_MACS);
        this.loadAlgorithms("org.bouncycastle.jcajce.provider.symmetric.", BouncyCastleProvider.SYMMETRIC_CIPHERS);
        this.loadAlgorithms("org.bouncycastle.jcajce.provider.asymmetric.", BouncyCastleProvider.ASYMMETRIC_GENERIC);
        this.loadAlgorithms("org.bouncycastle.jcajce.provider.asymmetric.", BouncyCastleProvider.ASYMMETRIC_CIPHERS);
        this.loadAlgorithms("org.bouncycastle.jcajce.provider.keystore.", BouncyCastleProvider.KEYSTORES);
        this.loadAlgorithms("org.bouncycastle.jcajce.provider.drbg.", BouncyCastleProvider.SECURE_RANDOMS);
        this.loadPQCKeys();
        this.put("X509Store.CERTIFICATE/COLLECTION", "org.bouncycastle.jce.provider.X509StoreCertCollection");
        this.put("X509Store.ATTRIBUTECERTIFICATE/COLLECTION", "org.bouncycastle.jce.provider.X509StoreAttrCertCollection");
        this.put("X509Store.CRL/COLLECTION", "org.bouncycastle.jce.provider.X509StoreCRLCollection");
        this.put("X509Store.CERTIFICATEPAIR/COLLECTION", "org.bouncycastle.jce.provider.X509StoreCertPairCollection");
        this.put("X509Store.CERTIFICATE/LDAP", "org.bouncycastle.jce.provider.X509StoreLDAPCerts");
        this.put("X509Store.CRL/LDAP", "org.bouncycastle.jce.provider.X509StoreLDAPCRLs");
        this.put("X509Store.ATTRIBUTECERTIFICATE/LDAP", "org.bouncycastle.jce.provider.X509StoreLDAPAttrCerts");
        this.put("X509Store.CERTIFICATEPAIR/LDAP", "org.bouncycastle.jce.provider.X509StoreLDAPCertPairs");
        this.put("X509StreamParser.CERTIFICATE", "org.bouncycastle.jce.provider.X509CertParser");
        this.put("X509StreamParser.ATTRIBUTECERTIFICATE", "org.bouncycastle.jce.provider.X509AttrCertParser");
        this.put("X509StreamParser.CRL", "org.bouncycastle.jce.provider.X509CRLParser");
        this.put("X509StreamParser.CERTIFICATEPAIR", "org.bouncycastle.jce.provider.X509CertPairParser");
        this.put("Cipher.BROKENPBEWITHMD5ANDDES", "org.bouncycastle.jce.provider.BrokenJCEBlockCipher$BrokePBEWithMD5AndDES");
        this.put("Cipher.BROKENPBEWITHSHA1ANDDES", "org.bouncycastle.jce.provider.BrokenJCEBlockCipher$BrokePBEWithSHA1AndDES");
        this.put("Cipher.OLDPBEWITHSHAANDTWOFISH-CBC", "org.bouncycastle.jce.provider.BrokenJCEBlockCipher$OldPBEWithSHAAndTwofish");
        this.put("CertPathValidator.RFC3281", "org.bouncycastle.jce.provider.PKIXAttrCertPathValidatorSpi");
        this.put("CertPathBuilder.RFC3281", "org.bouncycastle.jce.provider.PKIXAttrCertPathBuilderSpi");
        this.put("CertPathValidator.RFC3280", "org.bouncycastle.jce.provider.PKIXCertPathValidatorSpi");
        this.put("CertPathBuilder.RFC3280", "org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi");
        this.put("CertPathValidator.PKIX", "org.bouncycastle.jce.provider.PKIXCertPathValidatorSpi");
        this.put("CertPathBuilder.PKIX", "org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi");
        this.put("CertStore.Collection", "org.bouncycastle.jce.provider.CertStoreCollectionSpi");
        this.put("CertStore.LDAP", "org.bouncycastle.jce.provider.X509LDAPCertStoreSpi");
        this.put("CertStore.Multi", "org.bouncycastle.jce.provider.MultiCertStoreSpi");
        this.put("Alg.Alias.CertStore.X509LDAP", "LDAP");
    }
    
    private void loadAlgorithms(final String s, final String[] array) {
        for (int i = 0; i != array.length; ++i) {
            final Class loadClass = ClassUtil.loadClass(BouncyCastleProvider.class, s + array[i] + "$Mappings");
            if (loadClass != null) {
                try {
                    ((AlgorithmProvider)loadClass.newInstance()).configure(this);
                }
                catch (final Exception ex) {
                    throw new InternalError("cannot create instance of " + s + array[i] + "$Mappings : " + ex);
                }
            }
        }
    }
    
    private void loadPQCKeys() {
        this.addKeyInfoConverter(PQCObjectIdentifiers.sphincs256, new Sphincs256KeyFactorySpi());
        this.addKeyInfoConverter(PQCObjectIdentifiers.newHope, new NHKeyFactorySpi());
        this.addKeyInfoConverter(PQCObjectIdentifiers.xmss, new XMSSKeyFactorySpi());
        this.addKeyInfoConverter(PQCObjectIdentifiers.xmss_mt, new XMSSMTKeyFactorySpi());
        this.addKeyInfoConverter(PQCObjectIdentifiers.mcEliece, new McElieceKeyFactorySpi());
        this.addKeyInfoConverter(PQCObjectIdentifiers.mcElieceCca2, new McElieceCCA2KeyFactorySpi());
        this.addKeyInfoConverter(PQCObjectIdentifiers.rainbow, new RainbowKeyFactorySpi());
    }
    
    public void setParameter(final String s, final Object o) {
        synchronized (BouncyCastleProvider.CONFIGURATION) {
            ((BouncyCastleProviderConfiguration)BouncyCastleProvider.CONFIGURATION).setParameter(s, o);
        }
    }
    
    public boolean hasAlgorithm(final String s, final String s2) {
        return this.containsKey(s + "." + s2) || this.containsKey("Alg.Alias." + s + "." + s2);
    }
    
    public void addAlgorithm(final String s, final String s2) {
        if (this.containsKey(s)) {
            throw new IllegalStateException("duplicate provider key (" + s + ") found");
        }
        this.put(s, s2);
    }
    
    public void addAlgorithm(final String s, final ASN1ObjectIdentifier asn1ObjectIdentifier, final String s2) {
        this.addAlgorithm(s + "." + asn1ObjectIdentifier, s2);
        this.addAlgorithm(s + ".OID." + asn1ObjectIdentifier, s2);
    }
    
    public void addKeyInfoConverter(final ASN1ObjectIdentifier asn1ObjectIdentifier, final AsymmetricKeyInfoConverter asymmetricKeyInfoConverter) {
        synchronized (BouncyCastleProvider.keyInfoConverters) {
            BouncyCastleProvider.keyInfoConverters.put(asn1ObjectIdentifier, asymmetricKeyInfoConverter);
        }
    }
    
    public void addAttributes(final String s, final Map<String, String> map) {
        for (final String s2 : map.keySet()) {
            final String string = s + " " + s2;
            if (this.containsKey(string)) {
                throw new IllegalStateException("duplicate provider attribute key (" + string + ") found");
            }
            this.put(string, map.get(s2));
        }
    }
    
    private static AsymmetricKeyInfoConverter getAsymmetricKeyInfoConverter(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        synchronized (BouncyCastleProvider.keyInfoConverters) {
            return BouncyCastleProvider.keyInfoConverters.get(asn1ObjectIdentifier);
        }
    }
    
    public static PublicKey getPublicKey(final SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        final AsymmetricKeyInfoConverter asymmetricKeyInfoConverter = getAsymmetricKeyInfoConverter(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
        if (asymmetricKeyInfoConverter == null) {
            return null;
        }
        return asymmetricKeyInfoConverter.generatePublic(subjectPublicKeyInfo);
    }
    
    public static PrivateKey getPrivateKey(final PrivateKeyInfo privateKeyInfo) throws IOException {
        final AsymmetricKeyInfoConverter asymmetricKeyInfoConverter = getAsymmetricKeyInfoConverter(privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm());
        if (asymmetricKeyInfoConverter == null) {
            return null;
        }
        return asymmetricKeyInfoConverter.generatePrivate(privateKeyInfo);
    }
    
    static {
        BouncyCastleProvider.info = "BouncyCastle Security Provider v1.59";
        CONFIGURATION = new BouncyCastleProviderConfiguration();
        keyInfoConverters = new HashMap();
        SYMMETRIC_GENERIC = new String[] { "PBEPBKDF1", "PBEPBKDF2", "PBEPKCS12", "TLSKDF", "SCRYPT" };
        SYMMETRIC_MACS = new String[] { "SipHash", "Poly1305" };
        SYMMETRIC_CIPHERS = new String[] { "AES", "ARC4", "ARIA", "Blowfish", "Camellia", "CAST5", "CAST6", "ChaCha", "DES", "DESede", "GOST28147", "Grainv1", "Grain128", "HC128", "HC256", "IDEA", "Noekeon", "RC2", "RC5", "RC6", "Rijndael", "Salsa20", "SEED", "Serpent", "Shacal2", "Skipjack", "SM4", "TEA", "Twofish", "Threefish", "VMPC", "VMPCKSA3", "XTEA", "XSalsa20", "OpenSSLPBKDF", "DSTU7624", "GOST3412_2015" };
        ASYMMETRIC_GENERIC = new String[] { "X509", "IES" };
        ASYMMETRIC_CIPHERS = new String[] { "DSA", "DH", "EC", "RSA", "GOST", "ECGOST", "ElGamal", "DSTU4145", "GM" };
        DIGESTS = new String[] { "GOST3411", "Keccak", "MD2", "MD4", "MD5", "SHA1", "RIPEMD128", "RIPEMD160", "RIPEMD256", "RIPEMD320", "SHA224", "SHA256", "SHA384", "SHA512", "SHA3", "Skein", "SM3", "Tiger", "Whirlpool", "Blake2b", "Blake2s", "DSTU7564" };
        KEYSTORES = new String[] { "BC", "BCFKS", "PKCS12" };
        SECURE_RANDOMS = new String[] { "DRBG" };
    }
}
