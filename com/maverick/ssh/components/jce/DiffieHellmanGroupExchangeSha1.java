package com.maverick.ssh.components.jce;

import com.maverick.ssh.components.ComponentManager;
import com.maverick.ssh.components.Digest;
import java.security.KeyPair;
import java.security.spec.KeySpec;
import javax.crypto.spec.DHPublicKeySpec;
import java.security.PublicKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.interfaces.DHPublicKey;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.DHParameterSpec;
import com.maverick.util.ByteArrayReader;
import com.maverick.util.ByteArrayWriter;
import java.security.NoSuchAlgorithmException;
import com.maverick.ssh.SshException;
import java.security.KeyFactory;
import javax.crypto.KeyAgreement;
import java.security.KeyPairGenerator;
import java.math.BigInteger;
import com.maverick.ssh.components.SshKeyExchangeClient;

public class DiffieHellmanGroupExchangeSha1 extends SshKeyExchangeClient implements AbstractKeyExchange
{
    public static final String DIFFIE_HELLMAN_GROUP_EXCHANGE_SHA1 = "diffie-hellman-group-exchange-sha1";
    BigInteger jc;
    BigInteger hc;
    static BigInteger ec;
    BigInteger lc;
    BigInteger kc;
    BigInteger oc;
    String fc;
    String ic;
    byte[] mc;
    byte[] qc;
    KeyPairGenerator pc;
    KeyAgreement nc;
    KeyFactory gc;
    
    public DiffieHellmanGroupExchangeSha1() {
        this("SHA-1");
    }
    
    protected DiffieHellmanGroupExchangeSha1(final String s) {
        super(s);
        this.lc = null;
        this.kc = null;
        this.oc = null;
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
    
    public void performClientExchange(final String fc, final String ic, final byte[] mc, final byte[] qc) throws SshException {
        try {
            this.fc = fc;
            this.ic = ic;
            this.mc = mc;
            this.qc = qc;
            try {
                this.gc = ((JCEProvider.getProviderForAlgorithm("DH") == null) ? KeyFactory.getInstance("DH") : KeyFactory.getInstance("DH", JCEProvider.getProviderForAlgorithm("DH")));
                this.pc = ((JCEProvider.getProviderForAlgorithm("DH") == null) ? KeyPairGenerator.getInstance("DH") : KeyPairGenerator.getInstance("DH", JCEProvider.getProviderForAlgorithm("DH")));
                this.nc = ((JCEProvider.getProviderForAlgorithm("DH") == null) ? KeyAgreement.getInstance("DH") : KeyAgreement.getInstance("DH", JCEProvider.getProviderForAlgorithm("DH")));
            }
            catch (final NoSuchAlgorithmException ex) {
                throw new SshException("JCE does not support Diffie Hellman key exchange", 16);
            }
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
            this.hc = byteArrayReader.readBigInteger();
            this.jc = byteArrayReader.readBigInteger();
            try {
                this.pc.initialize(new DHParameterSpec(this.hc, this.jc));
                final KeyPair generateKeyPair = this.pc.generateKeyPair();
                this.nc.init(generateKeyPair.getPrivate());
                this.lc = ((DHPublicKey)generateKeyPair.getPublic()).getY();
            }
            catch (final InvalidKeyException ex2) {
                throw new SshException("Failed to generate DH value", 16);
            }
            catch (final InvalidAlgorithmParameterException ex3) {
                throw new SshException("Failed to generate DH value", 16);
            }
            byteArrayWriter.reset();
            byteArrayWriter.write(32);
            byteArrayWriter.writeBigInteger(this.lc);
            super.transport.sendMessage(byteArrayWriter.toByteArray(), true);
            final byte[] nextMessage2 = super.transport.nextMessage();
            if (nextMessage2[0] != 33) {
                super.transport.disconnect(3, "Expected SSH_MSG_KEXDH_GEX_REPLY");
                throw new SshException("Key exchange failed: Expected SSH_MSG_KEXDH_GEX_REPLY [id=" + nextMessage2[0] + "]", 5);
            }
            final ByteArrayReader byteArrayReader2 = new ByteArrayReader(nextMessage2, 1, nextMessage2.length - 1);
            super.hostKey = byteArrayReader2.readBinaryString();
            this.kc = byteArrayReader2.readBigInteger();
            super.signature = byteArrayReader2.readBinaryString();
            this.nc.doPhase(this.gc.generatePublic(new DHPublicKeySpec(this.kc, this.hc, this.jc)), true);
            byte[] generateSecret = this.nc.generateSecret();
            if ((generateSecret[0] & 0x80) == 0x80) {
                final byte[] array = new byte[generateSecret.length + 1];
                System.arraycopy(generateSecret, 0, array, 1, generateSecret.length);
                generateSecret = array;
            }
            super.secret = new BigInteger(generateSecret);
            this.calculateExchangeHash();
        }
        catch (final Exception ex4) {
            throw new SshException(ex4, 5);
        }
    }
    
    public String getProvider() {
        if (this.nc != null) {
            return this.nc.getProvider().getName();
        }
        return "";
    }
    
    protected void calculateExchangeHash() throws SshException {
        final Digest digest = (Digest)ComponentManager.getInstance().supportedDigests().getInstance(this.getHashAlgorithm());
        digest.putString(this.fc);
        digest.putString(this.ic);
        digest.putInt(this.mc.length);
        digest.putBytes(this.mc);
        digest.putInt(this.qc.length);
        digest.putBytes(this.qc);
        digest.putInt(super.hostKey.length);
        digest.putBytes(super.hostKey);
        digest.putInt(1024);
        digest.putBigInteger(this.hc);
        digest.putBigInteger(this.jc);
        digest.putBigInteger(this.lc);
        digest.putBigInteger(this.kc);
        digest.putBigInteger(super.secret);
        super.exchangeHash = digest.doFinal();
    }
    
    static {
        DiffieHellmanGroupExchangeSha1.ec = BigInteger.valueOf(1L);
    }
}
