package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.util.Set;
import java.util.EnumSet;
import java.security.CryptoPrimitive;
import java.security.AlgorithmConstraints;
import java.security.KeyFactory;
import java.security.spec.ECPoint;
import java.io.IOException;
import java.security.spec.KeySpec;
import java.security.spec.ECPublicKeySpec;
import javax.crypto.KeyAgreement;
import javax.net.ssl.SSLHandshakeException;
import java.security.Key;
import javax.crypto.SecretKey;
import java.security.spec.ECParameterSpec;
import java.security.KeyPair;
import java.security.spec.ECGenParameterSpec;
import java.security.KeyPairGenerator;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.PrivateKey;

final class ECDHCrypt
{
    private PrivateKey privateKey;
    private ECPublicKey publicKey;
    
    ECDHCrypt(final PrivateKey privateKey, final PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = (ECPublicKey)publicKey;
    }
    
    ECDHCrypt(final int curveId, final SecureRandom random) {
        try {
            final KeyPairGenerator kpg = JsseJce.getKeyPairGenerator("EC");
            final ECGenParameterSpec params = EllipticCurvesExtension.getECGenParamSpec(curveId);
            kpg.initialize(params, random);
            final KeyPair kp = kpg.generateKeyPair();
            this.privateKey = kp.getPrivate();
            this.publicKey = (ECPublicKey)kp.getPublic();
        }
        catch (final GeneralSecurityException e) {
            throw new RuntimeException("Could not generate DH keypair", e);
        }
    }
    
    ECDHCrypt(final ECParameterSpec params, final SecureRandom random) {
        try {
            final KeyPairGenerator kpg = JsseJce.getKeyPairGenerator("EC");
            kpg.initialize(params, random);
            final KeyPair kp = kpg.generateKeyPair();
            this.privateKey = kp.getPrivate();
            this.publicKey = (ECPublicKey)kp.getPublic();
        }
        catch (final GeneralSecurityException e) {
            throw new RuntimeException("Could not generate DH keypair", e);
        }
    }
    
    PublicKey getPublicKey() {
        return this.publicKey;
    }
    
    SecretKey getAgreedSecret(final PublicKey peerPublicKey) throws SSLHandshakeException {
        try {
            final KeyAgreement ka = JsseJce.getKeyAgreement("ECDH");
            ka.init(this.privateKey);
            ka.doPhase(peerPublicKey, true);
            return ka.generateSecret("TlsPremasterSecret");
        }
        catch (final GeneralSecurityException e) {
            throw (SSLHandshakeException)new SSLHandshakeException("Could not generate secret").initCause(e);
        }
    }
    
    SecretKey getAgreedSecret(final byte[] encodedPoint) throws SSLHandshakeException {
        try {
            final ECParameterSpec params = this.publicKey.getParams();
            final ECPoint point = JsseJce.decodePoint(encodedPoint, params.getCurve());
            final KeyFactory kf = JsseJce.getKeyFactory("EC");
            final ECPublicKeySpec spec = new ECPublicKeySpec(point, params);
            final PublicKey peerPublicKey = kf.generatePublic(spec);
            return this.getAgreedSecret(peerPublicKey);
        }
        catch (final GeneralSecurityException | IOException e) {
            throw (SSLHandshakeException)new SSLHandshakeException("Could not generate secret").initCause(e);
        }
    }
    
    void checkConstraints(final AlgorithmConstraints constraints, final byte[] encodedPoint) throws SSLHandshakeException {
        try {
            final ECParameterSpec params = this.publicKey.getParams();
            final ECPoint point = JsseJce.decodePoint(encodedPoint, params.getCurve());
            final ECPublicKeySpec spec = new ECPublicKeySpec(point, params);
            final KeyFactory kf = JsseJce.getKeyFactory("EC");
            final ECPublicKey publicKey = (ECPublicKey)kf.generatePublic(spec);
            if (!constraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), publicKey)) {
                throw new SSLHandshakeException("ECPublicKey does not comply to algorithm constraints");
            }
        }
        catch (final GeneralSecurityException | IOException e) {
            throw (SSLHandshakeException)new SSLHandshakeException("Could not generate ECPublicKey").initCause(e);
        }
    }
}
