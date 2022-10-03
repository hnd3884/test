package com.maverick.ssh.components.jce;

import com.maverick.ssh.components.DiffieHellmanGroups;
import com.maverick.ssh.components.ComponentManager;
import com.maverick.ssh.components.Digest;
import java.security.KeyPair;
import java.security.Provider;
import java.security.spec.KeySpec;
import javax.crypto.spec.DHPublicKeySpec;
import java.security.PublicKey;
import com.maverick.util.ByteArrayReader;
import java.io.IOException;
import com.maverick.util.ByteArrayWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.interfaces.DHPublicKey;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.DHParameterSpec;
import java.security.NoSuchAlgorithmException;
import com.maverick.ssh.SshException;
import java.security.Security;
import java.security.KeyFactory;
import javax.crypto.KeyAgreement;
import java.security.KeyPairGenerator;
import java.math.BigInteger;
import com.maverick.ssh.components.SshKeyExchangeClient;

public class DiffieHellmanGroup14Sha1 extends SshKeyExchangeClient implements AbstractKeyExchange
{
    public static final String DIFFIE_HELLMAN_GROUP14_SHA1 = "diffie-hellman-group14-sha1";
    static final BigInteger db;
    static final BigInteger jb;
    static final BigInteger hb;
    static final BigInteger fb;
    BigInteger kb;
    BigInteger ib;
    BigInteger nb;
    String cb;
    String gb;
    byte[] lb;
    byte[] pb;
    KeyPairGenerator ob;
    KeyAgreement mb;
    KeyFactory eb;
    
    public DiffieHellmanGroup14Sha1() {
        super("SHA-1");
        this.kb = null;
        this.ib = null;
        this.nb = null;
    }
    
    public void performClientExchange(final String cb, final String gb, final byte[] lb, final byte[] pb) throws SshException {
        this.cb = cb;
        this.gb = gb;
        this.lb = lb;
        this.pb = pb;
        try {
            final Provider provider = Security.getProvider("BC");
            this.eb = ((JCEProvider.getProviderForAlgorithm("DH") == null) ? ((provider == null) ? KeyFactory.getInstance("DH") : KeyFactory.getInstance("DH", provider)) : KeyFactory.getInstance("DH", JCEProvider.getProviderForAlgorithm("DH")));
            this.ob = ((JCEProvider.getProviderForAlgorithm("DH") == null) ? ((provider == null) ? KeyPairGenerator.getInstance("DH") : KeyPairGenerator.getInstance("DH", provider)) : KeyPairGenerator.getInstance("DH", JCEProvider.getProviderForAlgorithm("DH")));
            this.mb = ((JCEProvider.getProviderForAlgorithm("DH") == null) ? ((provider == null) ? KeyAgreement.getInstance("DH") : KeyAgreement.getInstance("DH", provider)) : KeyAgreement.getInstance("DH", JCEProvider.getProviderForAlgorithm("DH")));
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new SshException("JCE does not support Diffie Hellman key exchange", 16);
        }
        try {
            this.ob.initialize(new DHParameterSpec(DiffieHellmanGroup14Sha1.fb, DiffieHellmanGroup14Sha1.hb));
            final KeyPair generateKeyPair = this.ob.generateKeyPair();
            this.mb.init(generateKeyPair.getPrivate());
            this.kb = ((DHPublicKey)generateKeyPair.getPublic()).getY();
        }
        catch (final InvalidKeyException ex2) {
            throw new SshException("Failed to generate DH value", 16, ex2);
        }
        catch (final InvalidAlgorithmParameterException ex3) {
            throw new SshException("Failed to generate DH value", 16, ex3);
        }
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.write(30);
            byteArrayWriter.writeBigInteger(this.kb);
            super.transport.sendMessage(byteArrayWriter.toByteArray(), true);
        }
        catch (final IOException ex4) {
            throw new SshException("Failed to write SSH_MSG_KEXDH_INIT to message buffer", 5);
        }
        final byte[] nextMessage = super.transport.nextMessage();
        if (nextMessage[0] != 31) {
            super.transport.disconnect(3, "Key exchange failed");
            throw new SshException("Key exchange failed [id=" + nextMessage[0] + "]", 5);
        }
        final ByteArrayReader byteArrayReader = new ByteArrayReader(nextMessage, 1, nextMessage.length - 1);
        try {
            super.hostKey = byteArrayReader.readBinaryString();
            this.ib = byteArrayReader.readBigInteger();
            super.signature = byteArrayReader.readBinaryString();
            this.mb.doPhase(this.eb.generatePublic(new DHPublicKeySpec(this.ib, DiffieHellmanGroup14Sha1.fb, DiffieHellmanGroup14Sha1.hb)), true);
            byte[] generateSecret = this.mb.generateSecret();
            if ((generateSecret[0] & 0x80) == 0x80) {
                final byte[] array = new byte[generateSecret.length + 1];
                System.arraycopy(generateSecret, 0, array, 1, generateSecret.length);
                generateSecret = array;
            }
            super.secret = new BigInteger(generateSecret);
            this.calculateExchangeHash();
        }
        catch (final Exception ex5) {
            throw new SshException("Failed to read SSH_MSG_KEXDH_REPLY from message buffer", 5);
        }
    }
    
    public String getProvider() {
        if (this.mb != null) {
            return this.mb.getProvider().getName();
        }
        return "";
    }
    
    protected void calculateExchangeHash() throws SshException {
        final Digest digest = (Digest)ComponentManager.getInstance().supportedDigests().getInstance("SHA-1");
        digest.putString(this.cb);
        digest.putString(this.gb);
        digest.putInt(this.lb.length);
        digest.putBytes(this.lb);
        digest.putInt(this.pb.length);
        digest.putBytes(this.pb);
        digest.putInt(super.hostKey.length);
        digest.putBytes(super.hostKey);
        digest.putBigInteger(this.kb);
        digest.putBigInteger(this.ib);
        digest.putBigInteger(super.secret);
        super.exchangeHash = digest.doFinal();
    }
    
    public String getAlgorithm() {
        return "diffie-hellman-group14-sha1";
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
        db = BigInteger.valueOf(1L);
        jb = BigInteger.valueOf(2L);
        hb = DiffieHellmanGroup14Sha1.jb;
        fb = DiffieHellmanGroups.group14;
    }
}
