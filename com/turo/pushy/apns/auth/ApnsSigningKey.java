package com.turo.pushy.apns.auth;

import java.security.spec.ECParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.ECKey;
import java.security.interfaces.ECPrivateKey;

public class ApnsSigningKey extends ApnsKey implements ECPrivateKey
{
    private static final long serialVersionUID = 1L;
    
    public ApnsSigningKey(final String keyId, final String teamId, final ECPrivateKey key) throws NoSuchAlgorithmException, InvalidKeyException {
        super(keyId, teamId, key);
        final Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initSign(key);
    }
    
    @Override
    protected ECPrivateKey getKey() {
        return (ECPrivateKey)super.getKey();
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
    public BigInteger getS() {
        return this.getKey().getS();
    }
    
    public static ApnsSigningKey loadFromPkcs8File(final File pkcs8File, final String teamId, final String keyId) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try (final FileInputStream fileInputStream = new FileInputStream(pkcs8File)) {
            return loadFromInputStream(fileInputStream, teamId, keyId);
        }
    }
    
    public static ApnsSigningKey loadFromInputStream(final InputStream inputStream, final String teamId, final String keyId) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        final StringBuilder privateKeyBuilder = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        boolean haveReadHeader = false;
        boolean haveReadFooter = false;
        String line;
        while ((line = reader.readLine()) != null) {
            if (!haveReadHeader) {
                if (!line.contains("BEGIN PRIVATE KEY")) {
                    continue;
                }
                haveReadHeader = true;
            }
            else {
                if (line.contains("END PRIVATE KEY")) {
                    haveReadFooter = true;
                    break;
                }
                privateKeyBuilder.append(line);
            }
        }
        if (!haveReadHeader || !haveReadFooter) {
            throw new IOException("Could not find private key header/footer");
        }
        final String base64EncodedPrivateKey = privateKeyBuilder.toString();
        final byte[] keyBytes = ApnsKey.decodeBase64EncodedString(base64EncodedPrivateKey);
        final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        final KeyFactory keyFactory = KeyFactory.getInstance("EC");
        ECPrivateKey signingKey;
        try {
            signingKey = (ECPrivateKey)keyFactory.generatePrivate(keySpec);
        }
        catch (final InvalidKeySpecException e) {
            throw new InvalidKeyException(e);
        }
        return new ApnsSigningKey(keyId, teamId, signingKey);
    }
}
