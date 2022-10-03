package org.bouncycastle.pqc.jcajce.interfaces;

import java.nio.ByteBuffer;
import java.security.SignatureException;
import java.security.SecureRandom;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.InvalidKeyException;
import java.security.PublicKey;

public interface StateAwareSignature
{
    void initVerify(final PublicKey p0) throws InvalidKeyException;
    
    void initVerify(final Certificate p0) throws InvalidKeyException;
    
    void initSign(final PrivateKey p0) throws InvalidKeyException;
    
    void initSign(final PrivateKey p0, final SecureRandom p1) throws InvalidKeyException;
    
    byte[] sign() throws SignatureException;
    
    int sign(final byte[] p0, final int p1, final int p2) throws SignatureException;
    
    boolean verify(final byte[] p0) throws SignatureException;
    
    boolean verify(final byte[] p0, final int p1, final int p2) throws SignatureException;
    
    void update(final byte p0) throws SignatureException;
    
    void update(final byte[] p0) throws SignatureException;
    
    void update(final byte[] p0, final int p1, final int p2) throws SignatureException;
    
    void update(final ByteBuffer p0) throws SignatureException;
    
    String getAlgorithm();
    
    PrivateKey getUpdatedPrivateKey();
}
