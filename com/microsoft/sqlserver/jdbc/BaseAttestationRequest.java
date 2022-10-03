package com.microsoft.sqlserver.jdbc;

import java.io.ByteArrayOutputStream;
import java.security.KeyPair;
import java.security.interfaces.ECPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.KeyPairGenerator;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.security.KeyFactory;
import java.security.Key;
import javax.crypto.KeyAgreement;
import java.security.spec.ECPublicKeySpec;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECPoint;
import java.math.BigInteger;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.security.PrivateKey;

abstract class BaseAttestationRequest
{
    protected static final byte[] ECDH_MAGIC;
    protected static final int ENCLAVE_LENGTH = 104;
    protected static final int BIG_INTEGER_SIZE = 48;
    protected PrivateKey privateKey;
    protected byte[] enclaveChallenge;
    protected byte[] x;
    protected byte[] y;
    
    byte[] getBytes() throws IOException {
        return null;
    }
    
    byte[] createSessionSecret(final byte[] serverResponse) throws GeneralSecurityException, SQLServerException {
        if (serverResponse == null || serverResponse.length != 104) {
            SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_MalformedECDHPublicKey"), "0", false);
        }
        final ByteBuffer sr = ByteBuffer.wrap(serverResponse);
        final byte[] magic = new byte[8];
        sr.get(magic);
        if (!Arrays.equals(magic, BaseAttestationRequest.ECDH_MAGIC)) {
            SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_MalformedECDHHeader"), "0", false);
        }
        final byte[] x = new byte[48];
        final byte[] y = new byte[48];
        sr.get(x);
        sr.get(y);
        final ECPublicKeySpec keySpec = new ECPublicKeySpec(new ECPoint(new BigInteger(1, x), new BigInteger(1, y)), ((ECPrivateKey)this.privateKey).getParams());
        final KeyAgreement ka = KeyAgreement.getInstance("ECDH");
        ka.init(this.privateKey);
        ka.doPhase(KeyFactory.getInstance("EC").generatePublic(keySpec), true);
        return MessageDigest.getInstance("SHA-256").digest(ka.generateSecret());
    }
    
    void initBcryptECDH() throws SQLServerException {
        try {
            final KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
            kpg.initialize(new ECGenParameterSpec("secp384r1"));
            final KeyPair kp = kpg.generateKeyPair();
            final ECPublicKey publicKey = (ECPublicKey)kp.getPublic();
            this.privateKey = kp.getPrivate();
            final ECPoint w = publicKey.getW();
            this.x = this.adjustBigInt(w.getAffineX().toByteArray());
            this.y = this.adjustBigInt(w.getAffineY().toByteArray());
        }
        catch (final GeneralSecurityException | IOException e) {
            SQLServerException.makeFromDriverError(null, this, e.getLocalizedMessage(), "0", false);
        }
    }
    
    private byte[] adjustBigInt(byte[] b) throws IOException {
        if (0 == b[0] && 48 < b.length) {
            b = Arrays.copyOfRange(b, 1, b.length);
        }
        if (b.length < 48) {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            for (int i = 0; i < 48 - b.length; ++i) {
                output.write(new byte[] { 0 });
            }
            output.write(b);
            b = output.toByteArray();
        }
        return b;
    }
    
    static {
        ECDH_MAGIC = new byte[] { 69, 67, 75, 51, 48, 0, 0, 0 };
    }
}
