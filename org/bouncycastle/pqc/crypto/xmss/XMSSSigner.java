package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.crypto.StateAwareMessageSigner;

public class XMSSSigner implements StateAwareMessageSigner
{
    private XMSSPrivateKeyParameters privateKey;
    private XMSSPrivateKeyParameters nextKeyGenerator;
    private XMSSPublicKeyParameters publicKey;
    private XMSSParameters params;
    private KeyedHashFunctions khf;
    private boolean initSign;
    private boolean hasGenerated;
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        if (b) {
            this.initSign = true;
            this.hasGenerated = false;
            this.privateKey = (XMSSPrivateKeyParameters)cipherParameters;
            this.nextKeyGenerator = this.privateKey;
            this.params = this.privateKey.getParameters();
            this.khf = this.params.getWOTSPlus().getKhf();
        }
        else {
            this.initSign = false;
            this.publicKey = (XMSSPublicKeyParameters)cipherParameters;
            this.params = this.publicKey.getParameters();
            this.khf = this.params.getWOTSPlus().getKhf();
        }
    }
    
    public byte[] generateSignature(final byte[] array) {
        if (array == null) {
            throw new NullPointerException("message == null");
        }
        if (!this.initSign) {
            throw new IllegalStateException("signer not initialized for signature generation");
        }
        if (this.privateKey == null) {
            throw new IllegalStateException("signing key no longer usable");
        }
        if (this.privateKey.getBDSState().getAuthenticationPath().isEmpty()) {
            throw new IllegalStateException("not initialized");
        }
        final int index = this.privateKey.getIndex();
        if (!XMSSUtil.isIndexValid(this.params.getHeight(), index)) {
            throw new IllegalStateException("index out of bounds");
        }
        final byte[] prf = this.khf.PRF(this.privateKey.getSecretKeyPRF(), XMSSUtil.toBytesBigEndian(index, 32));
        final XMSSSignature xmssSignature = (XMSSSignature)new XMSSSignature.Builder(this.params).withIndex(index).withRandom(prf).withWOTSPlusSignature(this.wotsSign(this.khf.HMsg(Arrays.concatenate(prf, this.privateKey.getRoot(), XMSSUtil.toBytesBigEndian(index, this.params.getDigestSize())), array), (OTSHashAddress)new OTSHashAddress.Builder().withOTSAddress(index).build())).withAuthPath(this.privateKey.getBDSState().getAuthenticationPath()).build();
        this.hasGenerated = true;
        if (this.nextKeyGenerator != null) {
            this.privateKey = this.nextKeyGenerator.getNextKey();
            this.nextKeyGenerator = this.privateKey;
        }
        else {
            this.privateKey = null;
        }
        return xmssSignature.toByteArray();
    }
    
    public boolean verifySignature(final byte[] array, final byte[] array2) {
        final XMSSSignature build = new XMSSSignature.Builder(this.params).withSignature(array2).build();
        final int index = build.getIndex();
        this.params.getWOTSPlus().importKeys(new byte[this.params.getDigestSize()], this.publicKey.getPublicSeed());
        final byte[] hMsg = this.khf.HMsg(Arrays.concatenate(build.getRandom(), this.publicKey.getRoot(), XMSSUtil.toBytesBigEndian(index, this.params.getDigestSize())), array);
        final int height = this.params.getHeight();
        return Arrays.constantTimeAreEqual(XMSSVerifierUtil.getRootNodeFromSignature(this.params.getWOTSPlus(), height, hMsg, build, (OTSHashAddress)new OTSHashAddress.Builder().withOTSAddress(index).build(), XMSSUtil.getLeafIndex(index, height)).getValue(), this.publicKey.getRoot());
    }
    
    public AsymmetricKeyParameter getUpdatedPrivateKey() {
        if (this.hasGenerated) {
            final XMSSPrivateKeyParameters privateKey = this.privateKey;
            this.privateKey = null;
            this.nextKeyGenerator = null;
            return privateKey;
        }
        final XMSSPrivateKeyParameters nextKey = this.nextKeyGenerator.getNextKey();
        this.nextKeyGenerator = null;
        return nextKey;
    }
    
    private WOTSPlusSignature wotsSign(final byte[] array, final OTSHashAddress otsHashAddress) {
        if (array.length != this.params.getDigestSize()) {
            throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest");
        }
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        this.params.getWOTSPlus().importKeys(this.params.getWOTSPlus().getWOTSPlusSecretKey(this.privateKey.getSecretKeySeed(), otsHashAddress), this.privateKey.getPublicSeed());
        return this.params.getWOTSPlus().sign(array, otsHashAddress);
    }
}
