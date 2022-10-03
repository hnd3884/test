package org.bouncycastle.crypto;

public class Commitment
{
    private final byte[] secret;
    private final byte[] commitment;
    
    public Commitment(final byte[] secret, final byte[] commitment) {
        this.secret = secret;
        this.commitment = commitment;
    }
    
    public byte[] getSecret() {
        return this.secret;
    }
    
    public byte[] getCommitment() {
        return this.commitment;
    }
}
