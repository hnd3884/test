package org.bouncycastle.jce;

import java.security.spec.PKCS8EncodedKeySpec;
import java.io.UnsupportedEncodingException;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import java.security.PrivateKey;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import java.security.KeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.ASN1Primitive;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.PublicKey;

public class ECKeyUtil
{
    public static PublicKey publicToExplicitParameters(final PublicKey publicKey, final String s) throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException {
        final Provider provider = Security.getProvider(s);
        if (provider == null) {
            throw new NoSuchProviderException("cannot find provider: " + s);
        }
        return publicToExplicitParameters(publicKey, provider);
    }
    
    public static PublicKey publicToExplicitParameters(final PublicKey publicKey, final Provider provider) throws IllegalArgumentException, NoSuchAlgorithmException {
        try {
            final SubjectPublicKeyInfo instance = SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(publicKey.getEncoded()));
            if (instance.getAlgorithmId().getAlgorithm().equals(CryptoProObjectIdentifiers.gostR3410_2001)) {
                throw new IllegalArgumentException("cannot convert GOST key to explicit parameters.");
            }
            final X962Parameters instance2 = X962Parameters.getInstance(instance.getAlgorithmId().getParameters());
            X9ECParameters x9ECParameters;
            if (instance2.isNamedCurve()) {
                final X9ECParameters namedCurveByOid = ECUtil.getNamedCurveByOid(ASN1ObjectIdentifier.getInstance(instance2.getParameters()));
                x9ECParameters = new X9ECParameters(namedCurveByOid.getCurve(), namedCurveByOid.getG(), namedCurveByOid.getN(), namedCurveByOid.getH());
            }
            else {
                if (!instance2.isImplicitlyCA()) {
                    return publicKey;
                }
                x9ECParameters = new X9ECParameters(BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getCurve(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getG(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getN(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getH());
            }
            return KeyFactory.getInstance(publicKey.getAlgorithm(), provider).generatePublic(new X509EncodedKeySpec(new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, new X962Parameters(x9ECParameters)), instance.getPublicKeyData().getBytes()).getEncoded()));
        }
        catch (final IllegalArgumentException ex) {
            throw ex;
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new UnexpectedException(ex3);
        }
    }
    
    public static PrivateKey privateToExplicitParameters(final PrivateKey privateKey, final String s) throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException {
        final Provider provider = Security.getProvider(s);
        if (provider == null) {
            throw new NoSuchProviderException("cannot find provider: " + s);
        }
        return privateToExplicitParameters(privateKey, provider);
    }
    
    public static PrivateKey privateToExplicitParameters(final PrivateKey privateKey, final Provider provider) throws IllegalArgumentException, NoSuchAlgorithmException {
        try {
            final PrivateKeyInfo instance = PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(privateKey.getEncoded()));
            if (instance.getAlgorithmId().getAlgorithm().equals(CryptoProObjectIdentifiers.gostR3410_2001)) {
                throw new UnsupportedEncodingException("cannot convert GOST key to explicit parameters.");
            }
            final X962Parameters instance2 = X962Parameters.getInstance(instance.getAlgorithmId().getParameters());
            X9ECParameters x9ECParameters;
            if (instance2.isNamedCurve()) {
                final X9ECParameters namedCurveByOid = ECUtil.getNamedCurveByOid(ASN1ObjectIdentifier.getInstance(instance2.getParameters()));
                x9ECParameters = new X9ECParameters(namedCurveByOid.getCurve(), namedCurveByOid.getG(), namedCurveByOid.getN(), namedCurveByOid.getH());
            }
            else {
                if (!instance2.isImplicitlyCA()) {
                    return privateKey;
                }
                x9ECParameters = new X9ECParameters(BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getCurve(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getG(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getN(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getH());
            }
            return KeyFactory.getInstance(privateKey.getAlgorithm(), provider).generatePrivate(new PKCS8EncodedKeySpec(new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, new X962Parameters(x9ECParameters)), instance.parsePrivateKey()).getEncoded()));
        }
        catch (final IllegalArgumentException ex) {
            throw ex;
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new UnexpectedException(ex3);
        }
    }
    
    private static class UnexpectedException extends RuntimeException
    {
        private Throwable cause;
        
        UnexpectedException(final Throwable cause) {
            super(cause.toString());
            this.cause = cause;
        }
        
        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }
}
