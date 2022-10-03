package org.bouncycastle.pqc.crypto.xmss;

import java.text.ParseException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import java.security.SecureRandom;

public class XMSS
{
    private final XMSSParameters params;
    private WOTSPlus wotsPlus;
    private SecureRandom prng;
    private XMSSPrivateKeyParameters privateKey;
    private XMSSPublicKeyParameters publicKey;
    
    public XMSS(final XMSSParameters params, final SecureRandom prng) {
        if (params == null) {
            throw new NullPointerException("params == null");
        }
        this.params = params;
        this.wotsPlus = params.getWOTSPlus();
        this.prng = prng;
    }
    
    public void generateKeys() {
        final XMSSKeyPairGenerator xmssKeyPairGenerator = new XMSSKeyPairGenerator();
        xmssKeyPairGenerator.init(new XMSSKeyGenerationParameters(this.getParams(), this.prng));
        final AsymmetricCipherKeyPair generateKeyPair = xmssKeyPairGenerator.generateKeyPair();
        this.privateKey = (XMSSPrivateKeyParameters)generateKeyPair.getPrivate();
        this.publicKey = (XMSSPublicKeyParameters)generateKeyPair.getPublic();
        this.wotsPlus.importKeys(new byte[this.params.getDigestSize()], this.privateKey.getPublicSeed());
    }
    
    void importState(final XMSSPrivateKeyParameters privateKey, final XMSSPublicKeyParameters publicKey) {
        if (!Arrays.areEqual(privateKey.getRoot(), publicKey.getRoot())) {
            throw new IllegalStateException("root of private key and public key do not match");
        }
        if (!Arrays.areEqual(privateKey.getPublicSeed(), publicKey.getPublicSeed())) {
            throw new IllegalStateException("public seed of private key and public key do not match");
        }
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.wotsPlus.importKeys(new byte[this.params.getDigestSize()], this.privateKey.getPublicSeed());
    }
    
    public void importState(final byte[] array, final byte[] array2) {
        if (array == null) {
            throw new NullPointerException("privateKey == null");
        }
        if (array2 == null) {
            throw new NullPointerException("publicKey == null");
        }
        final XMSSPrivateKeyParameters build = new XMSSPrivateKeyParameters.Builder(this.params).withPrivateKey(array, this.getParams()).build();
        final XMSSPublicKeyParameters build2 = new XMSSPublicKeyParameters.Builder(this.params).withPublicKey(array2).build();
        if (!Arrays.areEqual(build.getRoot(), build2.getRoot())) {
            throw new IllegalStateException("root of private key and public key do not match");
        }
        if (!Arrays.areEqual(build.getPublicSeed(), build2.getPublicSeed())) {
            throw new IllegalStateException("public seed of private key and public key do not match");
        }
        this.privateKey = build;
        this.publicKey = build2;
        this.wotsPlus.importKeys(new byte[this.params.getDigestSize()], this.privateKey.getPublicSeed());
    }
    
    public byte[] sign(final byte[] array) {
        if (array == null) {
            throw new NullPointerException("message == null");
        }
        final XMSSSigner xmssSigner = new XMSSSigner();
        xmssSigner.init(true, this.privateKey);
        final byte[] generateSignature = xmssSigner.generateSignature(array);
        this.importState(this.privateKey = (XMSSPrivateKeyParameters)xmssSigner.getUpdatedPrivateKey(), this.publicKey);
        return generateSignature;
    }
    
    public boolean verifySignature(final byte[] array, final byte[] array2, final byte[] array3) throws ParseException {
        if (array == null) {
            throw new NullPointerException("message == null");
        }
        if (array2 == null) {
            throw new NullPointerException("signature == null");
        }
        if (array3 == null) {
            throw new NullPointerException("publicKey == null");
        }
        final XMSSSigner xmssSigner = new XMSSSigner();
        xmssSigner.init(false, new XMSSPublicKeyParameters.Builder(this.getParams()).withPublicKey(array3).build());
        return xmssSigner.verifySignature(array, array2);
    }
    
    public byte[] exportPrivateKey() {
        return this.privateKey.toByteArray();
    }
    
    public byte[] exportPublicKey() {
        return this.publicKey.toByteArray();
    }
    
    protected WOTSPlusSignature wotsSign(final byte[] array, final OTSHashAddress otsHashAddress) {
        if (array.length != this.params.getDigestSize()) {
            throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest");
        }
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        this.wotsPlus.importKeys(this.wotsPlus.getWOTSPlusSecretKey(this.privateKey.getSecretKeySeed(), otsHashAddress), this.getPublicSeed());
        return this.wotsPlus.sign(array, otsHashAddress);
    }
    
    public XMSSParameters getParams() {
        return this.params;
    }
    
    protected WOTSPlus getWOTSPlus() {
        return this.wotsPlus;
    }
    
    public byte[] getRoot() {
        return this.privateKey.getRoot();
    }
    
    protected void setRoot(final byte[] array) {
        this.privateKey = new XMSSPrivateKeyParameters.Builder(this.params).withSecretKeySeed(this.privateKey.getSecretKeySeed()).withSecretKeyPRF(this.privateKey.getSecretKeyPRF()).withPublicSeed(this.getPublicSeed()).withRoot(array).withBDSState(this.privateKey.getBDSState()).build();
        this.publicKey = new XMSSPublicKeyParameters.Builder(this.params).withRoot(array).withPublicSeed(this.getPublicSeed()).build();
    }
    
    public int getIndex() {
        return this.privateKey.getIndex();
    }
    
    protected void setIndex(final int n) {
        this.privateKey = new XMSSPrivateKeyParameters.Builder(this.params).withSecretKeySeed(this.privateKey.getSecretKeySeed()).withSecretKeyPRF(this.privateKey.getSecretKeyPRF()).withPublicSeed(this.privateKey.getPublicSeed()).withRoot(this.privateKey.getRoot()).withBDSState(this.privateKey.getBDSState()).build();
    }
    
    public byte[] getPublicSeed() {
        return this.privateKey.getPublicSeed();
    }
    
    protected void setPublicSeed(final byte[] array) {
        this.privateKey = new XMSSPrivateKeyParameters.Builder(this.params).withSecretKeySeed(this.privateKey.getSecretKeySeed()).withSecretKeyPRF(this.privateKey.getSecretKeyPRF()).withPublicSeed(array).withRoot(this.getRoot()).withBDSState(this.privateKey.getBDSState()).build();
        this.publicKey = new XMSSPublicKeyParameters.Builder(this.params).withRoot(this.getRoot()).withPublicSeed(array).build();
        this.wotsPlus.importKeys(new byte[this.params.getDigestSize()], array);
    }
    
    public XMSSPrivateKeyParameters getPrivateKey() {
        return this.privateKey;
    }
}
