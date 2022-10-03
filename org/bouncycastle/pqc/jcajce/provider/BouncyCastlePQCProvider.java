package org.bouncycastle.pqc.jcajce.provider;

import java.util.HashMap;
import java.security.PrivateKey;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import java.io.IOException;
import java.security.PublicKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.util.Iterator;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import java.security.Provider;

public class BouncyCastlePQCProvider extends Provider implements ConfigurableProvider
{
    private static String info;
    public static String PROVIDER_NAME;
    public static final ProviderConfiguration CONFIGURATION;
    private static final Map keyInfoConverters;
    private static final String ALGORITHM_PACKAGE = "org.bouncycastle.pqc.jcajce.provider.";
    private static final String[] ALGORITHMS;
    
    public BouncyCastlePQCProvider() {
        super(BouncyCastlePQCProvider.PROVIDER_NAME, 1.59, BouncyCastlePQCProvider.info);
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            public Object run() {
                BouncyCastlePQCProvider.this.setup();
                return null;
            }
        });
    }
    
    private void setup() {
        this.loadAlgorithms("org.bouncycastle.pqc.jcajce.provider.", BouncyCastlePQCProvider.ALGORITHMS);
    }
    
    private void loadAlgorithms(final String s, final String[] array) {
        for (int i = 0; i != array.length; ++i) {
            final Class loadClass = loadClass(BouncyCastlePQCProvider.class, s + array[i] + "$Mappings");
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
    
    public void setParameter(final String s, final Object o) {
        synchronized (BouncyCastlePQCProvider.CONFIGURATION) {
            monitorexit(BouncyCastlePQCProvider.CONFIGURATION);
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
        if (!this.containsKey(s + "." + s2)) {
            throw new IllegalStateException("primary key (" + s + "." + s2 + ") not found");
        }
        this.addAlgorithm(s + "." + asn1ObjectIdentifier, s2);
        this.addAlgorithm(s + ".OID." + asn1ObjectIdentifier, s2);
    }
    
    public void addKeyInfoConverter(final ASN1ObjectIdentifier asn1ObjectIdentifier, final AsymmetricKeyInfoConverter asymmetricKeyInfoConverter) {
        synchronized (BouncyCastlePQCProvider.keyInfoConverters) {
            BouncyCastlePQCProvider.keyInfoConverters.put(asn1ObjectIdentifier, asymmetricKeyInfoConverter);
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
        synchronized (BouncyCastlePQCProvider.keyInfoConverters) {
            return BouncyCastlePQCProvider.keyInfoConverters.get(asn1ObjectIdentifier);
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
    
    static Class loadClass(final Class clazz, final String s) {
        try {
            final ClassLoader classLoader = clazz.getClassLoader();
            if (classLoader != null) {
                return classLoader.loadClass(s);
            }
            return AccessController.doPrivileged((PrivilegedAction<Class>)new PrivilegedAction() {
                public Object run() {
                    try {
                        return Class.forName(s);
                    }
                    catch (final Exception ex) {
                        return null;
                    }
                }
            });
        }
        catch (final ClassNotFoundException ex) {
            return null;
        }
    }
    
    static {
        BouncyCastlePQCProvider.info = "BouncyCastle Post-Quantum Security Provider v1.59";
        BouncyCastlePQCProvider.PROVIDER_NAME = "BCPQC";
        CONFIGURATION = null;
        keyInfoConverters = new HashMap();
        ALGORITHMS = new String[] { "Rainbow", "McEliece", "SPHINCS", "NH", "XMSS" };
    }
}
