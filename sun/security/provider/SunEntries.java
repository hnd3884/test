package sun.security.provider;

import java.security.AccessController;
import java.security.Security;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URI;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.security.Provider;
import java.util.LinkedHashSet;

public final class SunEntries
{
    public static final String DEF_SECURE_RANDOM_ALGO;
    private LinkedHashSet<Provider.Service> services;
    private static final String PROP_EGD = "java.security.egd";
    private static final String PROP_RNDSOURCE = "securerandom.source";
    private static final boolean useLegacyDSA;
    static final String URL_DEV_RANDOM = "file:/dev/random";
    static final String URL_DEV_URANDOM = "file:/dev/urandom";
    private static final String seedSource;
    
    public static List<String> createAliases(final String... array) {
        return Arrays.asList(array);
    }
    
    public static List<String> createAliasesWithOid(final String... array) {
        final String[] array2 = Arrays.copyOf(array, array.length + 1);
        array2[array2.length - 1] = "OID." + array[0];
        return Arrays.asList(array2);
    }
    
    SunEntries(final Provider provider) {
        this.services = new LinkedHashSet<Provider.Service>(50, 0.9f);
        final HashMap hashMap = new HashMap(3);
        if (NativePRNG.isAvailable()) {
            this.add(provider, "SecureRandom", "NativePRNG", "sun.security.provider.NativePRNG", null, hashMap);
        }
        if (NativePRNG.Blocking.isAvailable()) {
            this.add(provider, "SecureRandom", "NativePRNGBlocking", "sun.security.provider.NativePRNG$Blocking", null, null);
        }
        if (NativePRNG.NonBlocking.isAvailable()) {
            this.add(provider, "SecureRandom", "NativePRNGNonBlocking", "sun.security.provider.NativePRNG$NonBlocking", null, null);
        }
        hashMap.put("ImplementedIn", "Software");
        this.add(provider, "SecureRandom", "SHA1PRNG", "sun.security.provider.SecureRandom", null, hashMap);
        hashMap.put("SupportedKeyClasses", "java.security.interfaces.DSAPublicKey|java.security.interfaces.DSAPrivateKey");
        hashMap.put("KeySize", "1024");
        this.add(provider, "Signature", "SHA1withDSA", "sun.security.provider.DSA$SHA1withDSA", createAliasesWithOid("1.2.840.10040.4.3", "DSA", "DSS", "SHA/DSA", "SHA-1/DSA", "SHA1/DSA", "SHAwithDSA", "DSAWithSHA1", "1.3.14.3.2.13", "1.3.14.3.2.27"), hashMap);
        hashMap.remove("ImplementedIn");
        this.add(provider, "Signature", "NONEwithDSA", "sun.security.provider.DSA$RawDSA", createAliases("RawDSA"), hashMap);
        hashMap.replace("KeySize", "1024", "2048");
        this.add(provider, "Signature", "SHA224withDSA", "sun.security.provider.DSA$SHA224withDSA", createAliasesWithOid("2.16.840.1.101.3.4.3.1"), hashMap);
        this.add(provider, "Signature", "SHA256withDSA", "sun.security.provider.DSA$SHA256withDSA", createAliasesWithOid("2.16.840.1.101.3.4.3.2"), hashMap);
        hashMap.clear();
        hashMap.put("ImplementedIn", "Software");
        hashMap.put("KeySize", "2048");
        final List<String> aliasesWithOid = createAliasesWithOid("1.2.840.10040.4.1", "1.3.14.3.2.12");
        this.add(provider, "KeyPairGenerator", "DSA", "sun.security.provider.DSAKeyPairGenerator$" + (SunEntries.useLegacyDSA ? "Legacy" : "Current"), aliasesWithOid, hashMap);
        this.add(provider, "AlgorithmParameterGenerator", "DSA", "sun.security.provider.DSAParameterGenerator", aliasesWithOid, hashMap);
        hashMap.remove("KeySize");
        this.add(provider, "AlgorithmParameters", "DSA", "sun.security.provider.DSAParameters", aliasesWithOid, hashMap);
        this.add(provider, "KeyFactory", "DSA", "sun.security.provider.DSAKeyFactory", aliasesWithOid, hashMap);
        this.add(provider, "MessageDigest", "MD2", "sun.security.provider.MD2", null, null);
        this.add(provider, "MessageDigest", "MD5", "sun.security.provider.MD5", null, hashMap);
        this.add(provider, "MessageDigest", "SHA", "sun.security.provider.SHA", createAliasesWithOid("1.3.14.3.2.26", "SHA-1", "SHA1"), hashMap);
        final String s = "2.16.840.1.101.3.4.2";
        this.add(provider, "MessageDigest", "SHA-224", "sun.security.provider.SHA2$SHA224", createAliasesWithOid(s + ".4"), null);
        this.add(provider, "MessageDigest", "SHA-256", "sun.security.provider.SHA2$SHA256", createAliasesWithOid(s + ".1"), null);
        this.add(provider, "MessageDigest", "SHA-384", "sun.security.provider.SHA5$SHA384", createAliasesWithOid(s + ".2"), null);
        this.add(provider, "MessageDigest", "SHA-512", "sun.security.provider.SHA5$SHA512", createAliasesWithOid(s + ".3"), null);
        this.add(provider, "MessageDigest", "SHA-512/224", "sun.security.provider.SHA5$SHA512_224", createAliasesWithOid(s + ".5"), null);
        this.add(provider, "MessageDigest", "SHA-512/256", "sun.security.provider.SHA5$SHA512_256", createAliasesWithOid(s + ".6"), null);
        this.add(provider, "MessageDigest", "SHA3-224", "sun.security.provider.SHA3$SHA224", createAliasesWithOid(s + ".7"), null);
        this.add(provider, "MessageDigest", "SHA3-256", "sun.security.provider.SHA3$SHA256", createAliasesWithOid(s + ".8"), null);
        this.add(provider, "MessageDigest", "SHA3-384", "sun.security.provider.SHA3$SHA384", createAliasesWithOid(s + ".9"), null);
        this.add(provider, "MessageDigest", "SHA3-512", "sun.security.provider.SHA3$SHA512", createAliasesWithOid(s + ".10"), null);
        this.add(provider, "CertificateFactory", "X.509", "sun.security.provider.X509Factory", createAliases("X509"), hashMap);
        this.add(provider, "KeyStore", "JKS", "sun.security.provider.JavaKeyStore$DualFormatJKS", null, hashMap);
        this.add(provider, "KeyStore", "CaseExactJKS", "sun.security.provider.JavaKeyStore$CaseExactJKS", null, null);
        this.add(provider, "KeyStore", "DKS", "sun.security.provider.DomainKeyStore$DKS", null, null);
        this.add(provider, "CertStore", "Collection", "sun.security.provider.certpath.CollectionCertStore", null, hashMap);
        this.add(provider, "CertStore", "com.sun.security.IndexedCollection", "sun.security.provider.certpath.IndexedCollectionCertStore", null, hashMap);
        hashMap.put("LDAPSchema", "RFC2587");
        this.add(provider, "CertStore", "LDAP", "sun.security.provider.certpath.ldap.LDAPCertStore", null, hashMap);
        hashMap.remove("LDAPSchema");
        this.add(provider, "Policy", "JavaPolicy", "sun.security.provider.PolicySpiFile", null, null);
        this.add(provider, "Configuration", "JavaLoginConfig", "sun.security.provider.ConfigFile$Spi", null, null);
        hashMap.put("ValidationAlgorithm", "RFC5280");
        this.add(provider, "CertPathBuilder", "PKIX", "sun.security.provider.certpath.SunCertPathBuilder", null, hashMap);
        this.add(provider, "CertPathValidator", "PKIX", "sun.security.provider.certpath.PKIXCertPathValidator", null, hashMap);
    }
    
    Iterator<Provider.Service> iterator() {
        return this.services.iterator();
    }
    
    private void add(final Provider provider, final String s, final String s2, final String s3, final List<String> list, final HashMap<String, String> hashMap) {
        this.services.add(new Provider.Service(provider, s, s2, s3, list, hashMap));
    }
    
    static String getSeedSource() {
        return SunEntries.seedSource;
    }
    
    static File getDeviceFile(final URL url) throws IOException {
        try {
            final URI uri = url.toURI();
            if (uri.isOpaque()) {
                return new File(URI.create(new File(System.getProperty("user.dir")).toURI().toString() + uri.toString().substring(5)));
            }
            return new File(uri);
        }
        catch (final URISyntaxException ex) {
            return new File(url.getPath());
        }
    }
    
    static {
        useLegacyDSA = Boolean.parseBoolean(GetPropertyAction.privilegedGetProperty("jdk.security.legacyDSAKeyPairGenerator"));
        seedSource = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                final String property = System.getProperty("java.security.egd", "");
                if (property.length() != 0) {
                    return property;
                }
                final String property2 = Security.getProperty("securerandom.source");
                if (property2 == null) {
                    return "";
                }
                return property2;
            }
        });
        DEF_SECURE_RANDOM_ALGO = ((NativePRNG.isAvailable() && (SunEntries.seedSource.equals("file:/dev/urandom") || SunEntries.seedSource.equals("file:/dev/random"))) ? "NativePRNG" : "SHA1PRNG");
    }
}
