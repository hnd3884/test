package org.bouncycastle.crypto.tls;

import org.bouncycastle.util.Arrays;

public class SecurityParameters
{
    int entity;
    int cipherSuite;
    short compressionAlgorithm;
    int prfAlgorithm;
    int verifyDataLength;
    byte[] masterSecret;
    byte[] clientRandom;
    byte[] serverRandom;
    byte[] sessionHash;
    byte[] pskIdentity;
    byte[] srpIdentity;
    short maxFragmentLength;
    boolean truncatedHMac;
    boolean encryptThenMAC;
    boolean extendedMasterSecret;
    
    public SecurityParameters() {
        this.entity = -1;
        this.cipherSuite = -1;
        this.compressionAlgorithm = 0;
        this.prfAlgorithm = -1;
        this.verifyDataLength = -1;
        this.masterSecret = null;
        this.clientRandom = null;
        this.serverRandom = null;
        this.sessionHash = null;
        this.pskIdentity = null;
        this.srpIdentity = null;
        this.maxFragmentLength = -1;
        this.truncatedHMac = false;
        this.encryptThenMAC = false;
        this.extendedMasterSecret = false;
    }
    
    void clear() {
        if (this.masterSecret != null) {
            Arrays.fill(this.masterSecret, (byte)0);
            this.masterSecret = null;
        }
    }
    
    public int getEntity() {
        return this.entity;
    }
    
    public int getCipherSuite() {
        return this.cipherSuite;
    }
    
    public short getCompressionAlgorithm() {
        return this.compressionAlgorithm;
    }
    
    public int getPrfAlgorithm() {
        return this.prfAlgorithm;
    }
    
    public int getVerifyDataLength() {
        return this.verifyDataLength;
    }
    
    public byte[] getMasterSecret() {
        return this.masterSecret;
    }
    
    public byte[] getClientRandom() {
        return this.clientRandom;
    }
    
    public byte[] getServerRandom() {
        return this.serverRandom;
    }
    
    public byte[] getSessionHash() {
        return this.sessionHash;
    }
    
    @Deprecated
    public byte[] getPskIdentity() {
        return this.pskIdentity;
    }
    
    public byte[] getPSKIdentity() {
        return this.pskIdentity;
    }
    
    public byte[] getSRPIdentity() {
        return this.srpIdentity;
    }
}
