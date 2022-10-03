package com.maverick.ssh.components.jce;

import com.maverick.ssh.components.DiffieHellmanGroups;
import com.maverick.ssh.components.ComponentManager;
import com.maverick.ssh.components.Digest;
import java.security.KeyPair;
import java.security.spec.KeySpec;
import javax.crypto.spec.DHPublicKeySpec;
import com.maverick.util.ByteArrayReader;
import java.security.InvalidKeyException;
import java.io.IOException;
import com.maverick.util.ByteArrayWriter;
import java.security.Key;
import java.security.InvalidAlgorithmParameterException;
import javax.crypto.interfaces.DHPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.DHParameterSpec;
import java.security.NoSuchAlgorithmException;
import com.maverick.ssh.SshException;
import java.security.KeyFactory;
import javax.crypto.KeyAgreement;
import java.security.KeyPairGenerator;
import java.math.BigInteger;
import com.maverick.ssh.components.SshKeyExchangeClient;

public class DiffieHellmanGroup1Sha1 extends SshKeyExchangeClient implements AbstractKeyExchange
{
    public static final String DIFFIE_HELLMAN_GROUP1_SHA1 = "diffie-hellman-group1-sha1";
    static final BigInteger rb;
    static final BigInteger xb;
    static final BigInteger vb;
    static final BigInteger tb;
    BigInteger yb;
    BigInteger wb;
    BigInteger bc;
    String qb;
    String ub;
    byte[] zb;
    byte[] dc;
    KeyPairGenerator cc;
    KeyAgreement ac;
    KeyFactory sb;
    
    public DiffieHellmanGroup1Sha1() {
        super("SHA-1");
        this.yb = null;
        this.wb = null;
        this.bc = null;
    }
    
    public String getAlgorithm() {
        return "diffie-hellman-group1-sha1";
    }
    
    public String getProvider() {
        if (this.ac != null) {
            return this.ac.getProvider().getName();
        }
        return "";
    }
    
    public void performClientExchange(final String qb, final String ub, final byte[] zb, final byte[] dc) throws SshException {
        this.qb = qb;
        this.ub = ub;
        this.zb = zb;
        this.dc = dc;
        try {
            this.sb = ((JCEProvider.getProviderForAlgorithm("DH") == null) ? KeyFactory.getInstance("DH") : KeyFactory.getInstance("DH", JCEProvider.getProviderForAlgorithm("DH")));
            this.ac = ((JCEProvider.getProviderForAlgorithm("DH") == null) ? KeyAgreement.getInstance("DH") : KeyAgreement.getInstance("DH", JCEProvider.getProviderForAlgorithm("DH")));
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new SshException("JCE does not support Diffie Hellman key exchange", 16);
        }
        int i = 3;
        while (i != 0) {
            --i;
            KeyPair generateKeyPair;
            try {
                (this.cc = ((JCEProvider.getProviderForAlgorithm("DH") == null) ? KeyPairGenerator.getInstance("DH") : KeyPairGenerator.getInstance("DH", JCEProvider.getProviderForAlgorithm("DH")))).initialize(new DHParameterSpec(DiffieHellmanGroup1Sha1.tb, DiffieHellmanGroup1Sha1.vb));
                generateKeyPair = this.cc.generateKeyPair();
                this.yb = ((DHPublicKey)generateKeyPair.getPublic()).getY();
            }
            catch (final InvalidAlgorithmParameterException ex2) {
                throw new SshException("Failed to generate DH value", 16);
            }
            catch (final NoSuchAlgorithmException ex3) {
                throw new SshException("JCE does not support Diffie Hellman key exchange", 16);
            }
            if (this.yb.compareTo(DiffieHellmanGroup1Sha1.rb) >= 0 && this.yb.compareTo(DiffieHellmanGroup1Sha1.tb.subtract(DiffieHellmanGroup1Sha1.rb)) <= 0) {
                try {
                    this.ac.init(generateKeyPair.getPrivate());
                    final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
                    byteArrayWriter.write(30);
                    byteArrayWriter.writeBigInteger(this.yb);
                    super.transport.sendMessage(byteArrayWriter.toByteArray(), true);
                }
                catch (final IOException ex4) {
                    throw new SshException("Failed to write SSH_MSG_KEXDH_INIT to message buffer", 5);
                }
                catch (final InvalidKeyException ex5) {
                    throw new SshException("JCE reported Diffie Hellman invalid key", 16);
                }
                final byte[] nextMessage = super.transport.nextMessage();
                if (nextMessage[0] != 31) {
                    super.transport.disconnect(3, "Key exchange failed");
                    throw new SshException("Key exchange failed [id=" + nextMessage[0] + "]", 5);
                }
                final ByteArrayReader byteArrayReader = new ByteArrayReader(nextMessage, 1, nextMessage.length - 1);
                try {
                    super.hostKey = byteArrayReader.readBinaryString();
                    this.wb = byteArrayReader.readBigInteger();
                    super.signature = byteArrayReader.readBinaryString();
                    this.ac.doPhase(this.sb.generatePublic(new DHPublicKeySpec(this.wb, DiffieHellmanGroup1Sha1.tb, DiffieHellmanGroup1Sha1.vb)), true);
                    byte[] generateSecret = this.ac.generateSecret();
                    if ((generateSecret[0] & 0x80) == 0x80) {
                        final byte[] array = new byte[generateSecret.length + 1];
                        System.arraycopy(generateSecret, 0, array, 1, generateSecret.length);
                        generateSecret = array;
                    }
                    super.secret = new BigInteger(generateSecret);
                    this.calculateExchangeHash();
                }
                catch (final Exception ex6) {
                    throw new SshException("Failed to read SSH_MSG_KEXDH_REPLY from message buffer", 5, ex6);
                }
                return;
            }
        }
        super.transport.disconnect(3, "Failed to generate key exchange value");
        throw new SshException("Key exchange failed to generate e value", 5);
    }
    
    protected void calculateExchangeHash() throws SshException {
        final Digest digest = (Digest)ComponentManager.getInstance().supportedDigests().getInstance("SHA-1");
        digest.putString(this.qb);
        digest.putString(this.ub);
        digest.putInt(this.zb.length);
        digest.putBytes(this.zb);
        digest.putInt(this.dc.length);
        digest.putBytes(this.dc);
        digest.putInt(super.hostKey.length);
        digest.putBytes(super.hostKey);
        digest.putBigInteger(this.yb);
        digest.putBigInteger(this.wb);
        digest.putBigInteger(super.secret);
        super.exchangeHash = digest.doFinal();
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
    
    static {
        rb = BigInteger.valueOf(1L);
        xb = BigInteger.valueOf(2L);
        vb = DiffieHellmanGroup1Sha1.xb;
        tb = DiffieHellmanGroups.group1;
    }
}
