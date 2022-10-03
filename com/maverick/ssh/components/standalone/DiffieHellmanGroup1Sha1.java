package com.maverick.ssh.components.standalone;

import com.maverick.ssh.components.DiffieHellmanGroups;
import com.maverick.ssh.components.ComponentManager;
import com.maverick.ssh.components.Digest;
import java.io.IOException;
import com.maverick.util.ByteArrayReader;
import com.maverick.util.ByteArrayWriter;
import com.maverick.ssh.SshException;
import java.util.Random;
import com.maverick.crypto.security.SecureRandom;
import java.math.BigInteger;
import com.maverick.ssh.components.SshKeyExchangeClient;

public class DiffieHellmanGroup1Sha1 extends SshKeyExchangeClient
{
    public static final String DIFFIE_HELLMAN_GROUP1_SHA1 = "diffie-hellman-group1-sha1";
    static final BigInteger q;
    static final BigInteger w;
    static final BigInteger u;
    static final BigInteger s;
    static final BigInteger r;
    BigInteger x;
    BigInteger v;
    BigInteger ab;
    BigInteger z;
    String p;
    String t;
    byte[] y;
    byte[] bb;
    
    public DiffieHellmanGroup1Sha1() {
        super("SHA-1");
        this.x = null;
        this.v = null;
        this.ab = null;
        this.z = null;
    }
    
    public boolean isKeyExchangeMessage(final int n) {
        switch (n) {
            case 30:
            case 31: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public String getAlgorithm() {
        return "diffie-hellman-group1-sha1";
    }
    
    public void performClientExchange(final String p4, final String t, final byte[] y, final byte[] bb) throws SshException {
        try {
            this.p = p4;
            this.t = t;
            this.y = y;
            this.bb = bb;
            do {
                this.ab = new BigInteger(DiffieHellmanGroup1Sha1.s.bitLength(), SecureRandom.getInstance());
            } while (this.ab.compareTo(DiffieHellmanGroup1Sha1.q) < 0 || this.ab.compareTo(DiffieHellmanGroup1Sha1.r) > 0);
            this.x = DiffieHellmanGroup1Sha1.u.modPow(this.ab, DiffieHellmanGroup1Sha1.s);
            if (this.x.compareTo(DiffieHellmanGroup1Sha1.q) < 0 || this.x.compareTo(DiffieHellmanGroup1Sha1.s.subtract(DiffieHellmanGroup1Sha1.q)) > 0) {
                super.transport.disconnect(3, "Failed to generate key exchange value");
                throw new SshException("Key exchange failed to generate e value", 5);
            }
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.write(30);
            byteArrayWriter.writeBigInteger(this.x);
            super.transport.sendMessage(byteArrayWriter.toByteArray(), true);
            final byte[] nextMessage = super.transport.nextMessage();
            if (nextMessage[0] != 31) {
                super.transport.disconnect(3, "Key exchange failed");
                throw new SshException("Key exchange failed [id=" + nextMessage[0] + "]", 5);
            }
            final ByteArrayReader byteArrayReader = new ByteArrayReader(nextMessage, 1, nextMessage.length - 1);
            super.hostKey = byteArrayReader.readBinaryString();
            this.v = byteArrayReader.readBigInteger();
            super.signature = byteArrayReader.readBinaryString();
            super.secret = this.v.modPow(this.ab, DiffieHellmanGroup1Sha1.s);
            this.calculateExchangeHash();
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    protected void calculateExchangeHash() throws SshException {
        final Digest digest = (Digest)ComponentManager.getInstance().supportedDigests().getInstance("SHA-1");
        digest.putString(this.p);
        digest.putString(this.t);
        digest.putInt(this.y.length);
        digest.putBytes(this.y);
        digest.putInt(this.bb.length);
        digest.putBytes(this.bb);
        digest.putInt(super.hostKey.length);
        digest.putBytes(super.hostKey);
        digest.putBigInteger(this.x);
        digest.putBigInteger(this.v);
        digest.putBigInteger(super.secret);
        super.exchangeHash = digest.doFinal();
    }
    
    static {
        q = BigInteger.valueOf(1L);
        w = BigInteger.valueOf(2L);
        u = DiffieHellmanGroup1Sha1.w;
        s = DiffieHellmanGroups.group1;
        r = DiffieHellmanGroup1Sha1.s.subtract(DiffieHellmanGroup1Sha1.q).divide(DiffieHellmanGroup1Sha1.w);
    }
}
