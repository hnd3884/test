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

public class DiffieHellmanGroup14Sha1 extends SshKeyExchangeClient
{
    public static final String DIFFIE_HELLMAN_GROUP14_SHA1 = "diffie-hellman-group14-sha1";
    static final BigInteger d;
    static final BigInteger j;
    static final BigInteger h;
    static final BigInteger f;
    static final BigInteger e;
    BigInteger k;
    BigInteger i;
    BigInteger n;
    BigInteger m;
    String c;
    String g;
    byte[] l;
    byte[] o;
    
    public DiffieHellmanGroup14Sha1() {
        super("SHA-1");
        this.k = null;
        this.i = null;
        this.n = null;
        this.m = null;
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
        return "diffie-hellman-group14-sha1";
    }
    
    public void performClientExchange(final String c, final String g, final byte[] l, final byte[] o) throws SshException {
        try {
            this.c = c;
            this.g = g;
            this.l = l;
            this.o = o;
            do {
                this.n = new BigInteger(DiffieHellmanGroup14Sha1.f.bitLength(), SecureRandom.getInstance());
            } while (this.n.compareTo(DiffieHellmanGroup14Sha1.d) < 0 || this.n.compareTo(DiffieHellmanGroup14Sha1.e) > 0);
            this.k = DiffieHellmanGroup14Sha1.h.modPow(this.n, DiffieHellmanGroup14Sha1.f);
            if (this.k.compareTo(DiffieHellmanGroup14Sha1.d) < 0 || this.k.compareTo(DiffieHellmanGroup14Sha1.f.subtract(DiffieHellmanGroup14Sha1.d)) > 0) {
                super.transport.disconnect(3, "Failed to generate key exchange value");
                throw new SshException("Key exchange failed to generate e value", 5);
            }
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.write(30);
            byteArrayWriter.writeBigInteger(this.k);
            super.transport.sendMessage(byteArrayWriter.toByteArray(), true);
            final byte[] nextMessage = super.transport.nextMessage();
            if (nextMessage[0] != 31) {
                super.transport.disconnect(3, "Key exchange failed");
                throw new SshException("Key exchange failed [id=" + nextMessage[0] + "]", 5);
            }
            final ByteArrayReader byteArrayReader = new ByteArrayReader(nextMessage, 1, nextMessage.length - 1);
            super.hostKey = byteArrayReader.readBinaryString();
            this.i = byteArrayReader.readBigInteger();
            super.signature = byteArrayReader.readBinaryString();
            super.secret = this.i.modPow(this.n, DiffieHellmanGroup14Sha1.f);
            this.calculateExchangeHash();
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    protected void calculateExchangeHash() throws SshException {
        final Digest digest = (Digest)ComponentManager.getInstance().supportedDigests().getInstance("SHA-1");
        digest.putString(this.c);
        digest.putString(this.g);
        digest.putInt(this.l.length);
        digest.putBytes(this.l);
        digest.putInt(this.o.length);
        digest.putBytes(this.o);
        digest.putInt(super.hostKey.length);
        digest.putBytes(super.hostKey);
        digest.putBigInteger(this.k);
        digest.putBigInteger(this.i);
        digest.putBigInteger(super.secret);
        super.exchangeHash = digest.doFinal();
    }
    
    static {
        d = BigInteger.valueOf(1L);
        j = BigInteger.valueOf(2L);
        h = DiffieHellmanGroup14Sha1.j;
        f = DiffieHellmanGroups.group14;
        e = DiffieHellmanGroup14Sha1.f.subtract(DiffieHellmanGroup14Sha1.d).divide(DiffieHellmanGroup14Sha1.j);
    }
}
