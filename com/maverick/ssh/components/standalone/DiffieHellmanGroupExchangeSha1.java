package com.maverick.ssh.components.standalone;

import com.maverick.ssh.components.ComponentManager;
import com.maverick.ssh.components.Digest;
import java.io.IOException;
import java.util.Random;
import com.maverick.crypto.security.SecureRandom;
import com.maverick.util.ByteArrayReader;
import com.maverick.ssh.SshException;
import com.maverick.util.ByteArrayWriter;
import java.math.BigInteger;
import com.maverick.ssh.components.SshKeyExchangeClient;

public class DiffieHellmanGroupExchangeSha1 extends SshKeyExchangeClient
{
    public static final String DIFFIE_HELLMAN_GROUP_EXCHANGE_SHA1 = "diffie-hellman-group-exchange-sha1";
    BigInteger vc;
    BigInteger tc;
    static BigInteger sc;
    BigInteger xc;
    BigInteger wc;
    BigInteger ad;
    BigInteger zc;
    String rc;
    String uc;
    byte[] yc;
    byte[] bd;
    
    public DiffieHellmanGroupExchangeSha1() {
        super("SHA-1");
        this.xc = null;
        this.wc = null;
        this.ad = null;
        this.zc = null;
    }
    
    public boolean isKeyExchangeMessage(final int n) {
        switch (n) {
            case 30:
            case 31:
            case 32:
            case 33:
            case 34: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public String getAlgorithm() {
        return "diffie-hellman-group-exchange-sha1";
    }
    
    public void performClientExchange(final String rc, final String uc, final byte[] yc, final byte[] bd) throws SshException {
        try {
            this.rc = rc;
            this.uc = uc;
            this.yc = yc;
            this.bd = bd;
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.write(30);
            byteArrayWriter.writeInt(1024);
            super.transport.sendMessage(byteArrayWriter.toByteArray(), true);
            final byte[] nextMessage = super.transport.nextMessage();
            if (nextMessage[0] != 31) {
                super.transport.disconnect(3, "Expected SSH_MSG_KEX_GEX_GROUP");
                throw new SshException("Key exchange failed: Expected SSH_MSG_KEX_GEX_GROUP [id=" + nextMessage[0] + "]", 5);
            }
            final ByteArrayReader byteArrayReader = new ByteArrayReader(nextMessage, 1, nextMessage.length - 1);
            this.tc = byteArrayReader.readBigInteger();
            this.vc = byteArrayReader.readBigInteger();
            final BigInteger divide = this.tc.subtract(DiffieHellmanGroupExchangeSha1.sc).divide(BigInteger.valueOf(2L));
            do {
                this.ad = new BigInteger(this.tc.bitLength(), SecureRandom.getInstance());
            } while (this.ad.compareTo(DiffieHellmanGroupExchangeSha1.sc) < 0 || this.ad.compareTo(divide) > 0);
            this.xc = this.vc.modPow(this.ad, this.tc);
            if (this.xc.compareTo(DiffieHellmanGroupExchangeSha1.sc) < 0 || this.xc.compareTo(this.tc.subtract(DiffieHellmanGroupExchangeSha1.sc)) > 0) {
                super.transport.disconnect(3, "Failed to generate key exchange value");
                throw new SshException("Key exchange failed to generate e value", 5);
            }
            byteArrayWriter.reset();
            byteArrayWriter.write(32);
            byteArrayWriter.writeBigInteger(this.xc);
            super.transport.sendMessage(byteArrayWriter.toByteArray(), true);
            final byte[] nextMessage2 = super.transport.nextMessage();
            if (nextMessage2[0] != 33) {
                super.transport.disconnect(3, "Expected SSH_MSG_KEXDH_GEX_REPLY");
                throw new SshException("Key exchange failed: Expected SSH_MSG_KEXDH_GEX_REPLY [id=" + nextMessage2[0] + "]", 5);
            }
            final ByteArrayReader byteArrayReader2 = new ByteArrayReader(nextMessage2, 1, nextMessage2.length - 1);
            super.hostKey = byteArrayReader2.readBinaryString();
            this.wc = byteArrayReader2.readBigInteger();
            super.signature = byteArrayReader2.readBinaryString();
            super.secret = this.wc.modPow(this.ad, this.tc);
            this.calculateExchangeHash();
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    protected void calculateExchangeHash() throws SshException {
        final Digest digest = (Digest)ComponentManager.getInstance().supportedDigests().getInstance("SHA-1");
        digest.putString(this.rc);
        digest.putString(this.uc);
        digest.putInt(this.yc.length);
        digest.putBytes(this.yc);
        digest.putInt(this.bd.length);
        digest.putBytes(this.bd);
        digest.putInt(super.hostKey.length);
        digest.putBytes(super.hostKey);
        digest.putInt(1024);
        digest.putBigInteger(this.tc);
        digest.putBigInteger(this.vc);
        digest.putBigInteger(this.xc);
        digest.putBigInteger(this.wc);
        digest.putBigInteger(super.secret);
        super.exchangeHash = digest.doFinal();
    }
    
    static {
        DiffieHellmanGroupExchangeSha1.sc = BigInteger.valueOf(1L);
    }
}
