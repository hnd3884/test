package com.maverick.ssh.components;

import com.maverick.ssh.SshException;
import com.maverick.ssh2.TransportProtocol;
import java.math.BigInteger;

public abstract class SshKeyExchangeClient implements SshKeyExchange
{
    String b;
    protected BigInteger secret;
    protected byte[] exchangeHash;
    protected byte[] hostKey;
    protected byte[] signature;
    protected TransportProtocol transport;
    
    protected SshKeyExchangeClient(final String b) {
        this.b = b;
    }
    
    public SshKeyExchangeClient() {
    }
    
    public abstract String getAlgorithm();
    
    public byte[] getExchangeHash() {
        return this.exchangeHash;
    }
    
    public byte[] getHostKey() {
        return this.hostKey;
    }
    
    public BigInteger getSecret() {
        return this.secret;
    }
    
    public byte[] getSignature() {
        return this.signature;
    }
    
    public String getHashAlgorithm() {
        return this.b;
    }
    
    public void init(final TransportProtocol transport, final boolean b) {
        this.transport = transport;
    }
    
    public abstract void performClientExchange(final String p0, final String p1, final byte[] p2, final byte[] p3) throws SshException;
    
    public abstract boolean isKeyExchangeMessage(final int p0);
    
    public void reset() {
        this.exchangeHash = null;
        this.hostKey = null;
        this.signature = null;
        this.secret = null;
    }
}
