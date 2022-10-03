package com.microsoft.sqlserver.jdbc;

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.KeySpec;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;

abstract class BaseAttestationResponse
{
    protected int totalSize;
    protected int identitySize;
    protected int attestationTokenSize;
    protected int enclaveType;
    protected byte[] enclavePK;
    protected int sessionInfoSize;
    protected byte[] sessionID;
    protected int DHPKsize;
    protected int DHPKSsize;
    protected byte[] DHpublicKey;
    protected byte[] publicKeySig;
    
    BaseAttestationResponse() {
        this.sessionID = new byte[8];
    }
    
    void validateDHPublicKey() throws SQLServerException, GeneralSecurityException {
        final ByteBuffer enclavePKBuffer = ByteBuffer.wrap(this.enclavePK).order(ByteOrder.LITTLE_ENDIAN);
        final byte[] rsa1 = new byte[4];
        enclavePKBuffer.get(rsa1);
        final int bitCount = enclavePKBuffer.getInt();
        final int publicExponentLength = enclavePKBuffer.getInt();
        final int publicModulusLength = enclavePKBuffer.getInt();
        final int prime1 = enclavePKBuffer.getInt();
        final int prime2 = enclavePKBuffer.getInt();
        final byte[] exponent = new byte[publicExponentLength];
        enclavePKBuffer.get(exponent);
        final byte[] modulus = new byte[publicModulusLength];
        enclavePKBuffer.get(modulus);
        if (enclavePKBuffer.remaining() != 0) {
            SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_EnclavePKLengthError"), "0", false);
        }
        final RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(1, modulus), new BigInteger(1, exponent));
        final KeyFactory factory = KeyFactory.getInstance("RSA");
        final PublicKey pub = factory.generatePublic(spec);
        final Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(pub);
        sig.update(this.DHpublicKey);
        if (!sig.verify(this.publicKeySig)) {
            SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_InvalidDHKeySignature"), "0", false);
        }
    }
    
    byte[] getDHpublicKey() {
        return this.DHpublicKey;
    }
    
    byte[] getSessionID() {
        return this.sessionID;
    }
}
