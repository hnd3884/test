package org.bouncycastle.x509;

import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import java.util.HashSet;
import java.security.Security;
import java.security.Provider;
import org.bouncycastle.jce.X509Principal;
import javax.security.auth.x500.X500Principal;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.PrivateKey;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Iterator;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.util.Strings;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.util.Set;
import java.util.Hashtable;

class X509Util
{
    private static Hashtable algorithms;
    private static Hashtable params;
    private static Set noParams;
    
    private static RSASSAPSSparams creatPSSParams(final AlgorithmIdentifier algorithmIdentifier, final int n) {
        return new RSASSAPSSparams(algorithmIdentifier, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, algorithmIdentifier), new ASN1Integer(n), new ASN1Integer(1L));
    }
    
    static ASN1ObjectIdentifier getAlgorithmOID(String upperCase) {
        upperCase = Strings.toUpperCase(upperCase);
        if (X509Util.algorithms.containsKey(upperCase)) {
            return X509Util.algorithms.get(upperCase);
        }
        return new ASN1ObjectIdentifier(upperCase);
    }
    
    static AlgorithmIdentifier getSigAlgID(final ASN1ObjectIdentifier asn1ObjectIdentifier, String upperCase) {
        if (X509Util.noParams.contains(asn1ObjectIdentifier)) {
            return new AlgorithmIdentifier(asn1ObjectIdentifier);
        }
        upperCase = Strings.toUpperCase(upperCase);
        if (X509Util.params.containsKey(upperCase)) {
            return new AlgorithmIdentifier(asn1ObjectIdentifier, X509Util.params.get(upperCase));
        }
        return new AlgorithmIdentifier(asn1ObjectIdentifier, DERNull.INSTANCE);
    }
    
    static Iterator getAlgNames() {
        final Enumeration keys = X509Util.algorithms.keys();
        final ArrayList list = new ArrayList();
        while (keys.hasMoreElements()) {
            list.add(keys.nextElement());
        }
        return list.iterator();
    }
    
    static Signature getSignatureInstance(final String s) throws NoSuchAlgorithmException {
        return Signature.getInstance(s);
    }
    
    static Signature getSignatureInstance(final String s, final String s2) throws NoSuchProviderException, NoSuchAlgorithmException {
        if (s2 != null) {
            return Signature.getInstance(s, s2);
        }
        return Signature.getInstance(s);
    }
    
    static byte[] calculateSignature(final ASN1ObjectIdentifier asn1ObjectIdentifier, final String s, final PrivateKey privateKey, final SecureRandom secureRandom, final ASN1Encodable asn1Encodable) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        if (asn1ObjectIdentifier == null) {
            throw new IllegalStateException("no signature algorithm specified");
        }
        final Signature signatureInstance = getSignatureInstance(s);
        if (secureRandom != null) {
            signatureInstance.initSign(privateKey, secureRandom);
        }
        else {
            signatureInstance.initSign(privateKey);
        }
        signatureInstance.update(asn1Encodable.toASN1Primitive().getEncoded("DER"));
        return signatureInstance.sign();
    }
    
    static byte[] calculateSignature(final ASN1ObjectIdentifier asn1ObjectIdentifier, final String s, final String s2, final PrivateKey privateKey, final SecureRandom secureRandom, final ASN1Encodable asn1Encodable) throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        if (asn1ObjectIdentifier == null) {
            throw new IllegalStateException("no signature algorithm specified");
        }
        final Signature signatureInstance = getSignatureInstance(s, s2);
        if (secureRandom != null) {
            signatureInstance.initSign(privateKey, secureRandom);
        }
        else {
            signatureInstance.initSign(privateKey);
        }
        signatureInstance.update(asn1Encodable.toASN1Primitive().getEncoded("DER"));
        return signatureInstance.sign();
    }
    
    static X509Principal convertPrincipal(final X500Principal x500Principal) {
        try {
            return new X509Principal(x500Principal.getEncoded());
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("cannot convert principal");
        }
    }
    
    static Implementation getImplementation(final String s, String upperCase, final Provider provider) throws NoSuchAlgorithmException {
        String property;
        for (upperCase = Strings.toUpperCase(upperCase); (property = provider.getProperty("Alg.Alias." + s + "." + upperCase)) != null; upperCase = property) {}
        final String property2 = provider.getProperty(s + "." + upperCase);
        if (property2 != null) {
            try {
                final ClassLoader classLoader = provider.getClass().getClassLoader();
                Class<?> clazz;
                if (classLoader != null) {
                    clazz = classLoader.loadClass(property2);
                }
                else {
                    clazz = Class.forName(property2);
                }
                return new Implementation(clazz.newInstance(), provider);
            }
            catch (final ClassNotFoundException ex) {
                throw new IllegalStateException("algorithm " + upperCase + " in provider " + provider.getName() + " but no class \"" + property2 + "\" found!");
            }
            catch (final Exception ex2) {
                throw new IllegalStateException("algorithm " + upperCase + " in provider " + provider.getName() + " but class \"" + property2 + "\" inaccessible!");
            }
        }
        throw new NoSuchAlgorithmException("cannot find implementation " + upperCase + " for provider " + provider.getName());
    }
    
    static Implementation getImplementation(final String s, final String s2) throws NoSuchAlgorithmException {
        final Provider[] providers = Security.getProviders();
        for (int i = 0; i != providers.length; ++i) {
            final Implementation implementation = getImplementation(s, Strings.toUpperCase(s2), providers[i]);
            if (implementation != null) {
                return implementation;
            }
            try {
                getImplementation(s, s2, providers[i]);
            }
            catch (final NoSuchAlgorithmException ex) {}
        }
        throw new NoSuchAlgorithmException("cannot find implementation " + s2);
    }
    
    static Provider getProvider(final String s) throws NoSuchProviderException {
        final Provider provider = Security.getProvider(s);
        if (provider == null) {
            throw new NoSuchProviderException("Provider " + s + " not found");
        }
        return provider;
    }
    
    static {
        X509Util.algorithms = new Hashtable();
        X509Util.params = new Hashtable();
        X509Util.noParams = new HashSet();
        X509Util.algorithms.put("MD2WITHRSAENCRYPTION", PKCSObjectIdentifiers.md2WithRSAEncryption);
        X509Util.algorithms.put("MD2WITHRSA", PKCSObjectIdentifiers.md2WithRSAEncryption);
        X509Util.algorithms.put("MD5WITHRSAENCRYPTION", PKCSObjectIdentifiers.md5WithRSAEncryption);
        X509Util.algorithms.put("MD5WITHRSA", PKCSObjectIdentifiers.md5WithRSAEncryption);
        X509Util.algorithms.put("SHA1WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha1WithRSAEncryption);
        X509Util.algorithms.put("SHA1WITHRSA", PKCSObjectIdentifiers.sha1WithRSAEncryption);
        X509Util.algorithms.put("SHA224WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha224WithRSAEncryption);
        X509Util.algorithms.put("SHA224WITHRSA", PKCSObjectIdentifiers.sha224WithRSAEncryption);
        X509Util.algorithms.put("SHA256WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha256WithRSAEncryption);
        X509Util.algorithms.put("SHA256WITHRSA", PKCSObjectIdentifiers.sha256WithRSAEncryption);
        X509Util.algorithms.put("SHA384WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha384WithRSAEncryption);
        X509Util.algorithms.put("SHA384WITHRSA", PKCSObjectIdentifiers.sha384WithRSAEncryption);
        X509Util.algorithms.put("SHA512WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha512WithRSAEncryption);
        X509Util.algorithms.put("SHA512WITHRSA", PKCSObjectIdentifiers.sha512WithRSAEncryption);
        X509Util.algorithms.put("SHA1WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
        X509Util.algorithms.put("SHA224WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
        X509Util.algorithms.put("SHA256WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
        X509Util.algorithms.put("SHA384WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
        X509Util.algorithms.put("SHA512WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
        X509Util.algorithms.put("RIPEMD160WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
        X509Util.algorithms.put("RIPEMD160WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
        X509Util.algorithms.put("RIPEMD128WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
        X509Util.algorithms.put("RIPEMD128WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
        X509Util.algorithms.put("RIPEMD256WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
        X509Util.algorithms.put("RIPEMD256WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
        X509Util.algorithms.put("SHA1WITHDSA", X9ObjectIdentifiers.id_dsa_with_sha1);
        X509Util.algorithms.put("DSAWITHSHA1", X9ObjectIdentifiers.id_dsa_with_sha1);
        X509Util.algorithms.put("SHA224WITHDSA", NISTObjectIdentifiers.dsa_with_sha224);
        X509Util.algorithms.put("SHA256WITHDSA", NISTObjectIdentifiers.dsa_with_sha256);
        X509Util.algorithms.put("SHA384WITHDSA", NISTObjectIdentifiers.dsa_with_sha384);
        X509Util.algorithms.put("SHA512WITHDSA", NISTObjectIdentifiers.dsa_with_sha512);
        X509Util.algorithms.put("SHA1WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA1);
        X509Util.algorithms.put("ECDSAWITHSHA1", X9ObjectIdentifiers.ecdsa_with_SHA1);
        X509Util.algorithms.put("SHA224WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA224);
        X509Util.algorithms.put("SHA256WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA256);
        X509Util.algorithms.put("SHA384WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA384);
        X509Util.algorithms.put("SHA512WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA512);
        X509Util.algorithms.put("GOST3411WITHGOST3410", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
        X509Util.algorithms.put("GOST3411WITHGOST3410-94", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
        X509Util.algorithms.put("GOST3411WITHECGOST3410", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
        X509Util.algorithms.put("GOST3411WITHECGOST3410-2001", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
        X509Util.algorithms.put("GOST3411WITHGOST3410-2001", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
        X509Util.noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA1);
        X509Util.noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA224);
        X509Util.noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA256);
        X509Util.noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA384);
        X509Util.noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA512);
        X509Util.noParams.add(X9ObjectIdentifiers.id_dsa_with_sha1);
        X509Util.noParams.add(NISTObjectIdentifiers.dsa_with_sha224);
        X509Util.noParams.add(NISTObjectIdentifiers.dsa_with_sha256);
        X509Util.noParams.add(NISTObjectIdentifiers.dsa_with_sha384);
        X509Util.noParams.add(NISTObjectIdentifiers.dsa_with_sha512);
        X509Util.noParams.add(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
        X509Util.noParams.add(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
        X509Util.params.put("SHA1WITHRSAANDMGF1", creatPSSParams(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE), 20));
        X509Util.params.put("SHA224WITHRSAANDMGF1", creatPSSParams(new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, DERNull.INSTANCE), 28));
        X509Util.params.put("SHA256WITHRSAANDMGF1", creatPSSParams(new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, DERNull.INSTANCE), 32));
        X509Util.params.put("SHA384WITHRSAANDMGF1", creatPSSParams(new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, DERNull.INSTANCE), 48));
        X509Util.params.put("SHA512WITHRSAANDMGF1", creatPSSParams(new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, DERNull.INSTANCE), 64));
    }
    
    static class Implementation
    {
        Object engine;
        Provider provider;
        
        Implementation(final Object engine, final Provider provider) {
            this.engine = engine;
            this.provider = provider;
        }
        
        Object getEngine() {
            return this.engine;
        }
        
        Provider getProvider() {
            return this.provider;
        }
    }
}
