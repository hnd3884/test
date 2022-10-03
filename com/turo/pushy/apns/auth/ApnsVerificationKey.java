package com.turo.pushy.apns.auth;

import java.security.spec.ECParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.security.spec.ECPoint;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.ECKey;
import java.security.interfaces.ECPublicKey;

public class ApnsVerificationKey extends ApnsKey implements ECPublicKey
{
    private static final long serialVersionUID = 1L;
    
    public ApnsVerificationKey(final String keyId, final String teamId, final ECPublicKey key) throws NoSuchAlgorithmException, InvalidKeyException {
        super(keyId, teamId, key);
        final Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initVerify(key);
    }
    
    public ECPublicKey getKey() {
        return (ECPublicKey)super.getKey();
    }
    
    @Override
    public String getAlgorithm() {
        return this.getKey().getAlgorithm();
    }
    
    @Override
    public String getFormat() {
        return this.getKey().getFormat();
    }
    
    @Override
    public byte[] getEncoded() {
        return this.getKey().getEncoded();
    }
    
    @Override
    public ECPoint getW() {
        return this.getKey().getW();
    }
    
    public static ApnsVerificationKey loadFromPkcs8File(final File pkcs8File, final String teamId, final String keyId) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try (final FileInputStream fileInputStream = new FileInputStream(pkcs8File)) {
            return loadFromInputStream(fileInputStream, teamId, keyId);
        }
    }
    
    public static ApnsVerificationKey loadFromInputStream(final InputStream inputStream, final String teamId, final String keyId) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        final StringBuilder publicKeyBuilder = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        boolean haveReadHeader = false;
        boolean haveReadFooter = false;
        String line;
        while ((line = reader.readLine()) != null) {
            if (!haveReadHeader) {
                if (!line.contains("BEGIN PUBLIC KEY")) {
                    continue;
                }
                haveReadHeader = true;
            }
            else {
                if (line.contains("END PUBLIC KEY")) {
                    haveReadFooter = true;
                    break;
                }
                publicKeyBuilder.append(line);
            }
        }
        if (!haveReadHeader || !haveReadFooter) {
            throw new IOException("Could not find public key header/footer");
        }
        final String base64EncodedPublicKey = publicKeyBuilder.toString();
        final byte[] keyBytes = ApnsKey.decodeBase64EncodedString(base64EncodedPublicKey);
        final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        final KeyFactory keyFactory = KeyFactory.getInstance("EC");
        ECPublicKey verificationKey;
        try {
            verificationKey = (ECPublicKey)keyFactory.generatePublic(keySpec);
        }
        catch (final InvalidKeySpecException e) {
            throw new InvalidKeyException(e);
        }
        return new ApnsVerificationKey(keyId, teamId, verificationKey);
    }
}
