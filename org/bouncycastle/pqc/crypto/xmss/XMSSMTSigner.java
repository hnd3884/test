package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.crypto.StateAwareMessageSigner;

public class XMSSMTSigner implements StateAwareMessageSigner
{
    private XMSSMTPrivateKeyParameters privateKey;
    private XMSSMTPrivateKeyParameters nextKeyGenerator;
    private XMSSMTPublicKeyParameters publicKey;
    private XMSSMTParameters params;
    private XMSSParameters xmssParams;
    private WOTSPlus wotsPlus;
    private boolean hasGenerated;
    private boolean initSign;
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        if (b) {
            this.initSign = true;
            this.hasGenerated = false;
            this.privateKey = (XMSSMTPrivateKeyParameters)cipherParameters;
            this.nextKeyGenerator = this.privateKey;
            this.params = this.privateKey.getParameters();
            this.xmssParams = this.params.getXMSSParameters();
        }
        else {
            this.initSign = false;
            this.publicKey = (XMSSMTPublicKeyParameters)cipherParameters;
            this.params = this.publicKey.getParameters();
            this.xmssParams = this.params.getXMSSParameters();
        }
        this.wotsPlus = new WOTSPlus(new WOTSPlusParameters(this.params.getDigest()));
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
        if (this.privateKey.getBDSState().isEmpty()) {
            throw new IllegalStateException("not initialized");
        }
        final BDSStateMap bdsState = this.privateKey.getBDSState();
        final long index = this.privateKey.getIndex();
        final int height = this.params.getHeight();
        final int height2 = this.xmssParams.getHeight();
        if (!XMSSUtil.isIndexValid(height, index)) {
            throw new IllegalStateException("index out of bounds");
        }
        final byte[] prf = this.wotsPlus.getKhf().PRF(this.privateKey.getSecretKeyPRF(), XMSSUtil.toBytesBigEndian(index, 32));
        final byte[] hMsg = this.wotsPlus.getKhf().HMsg(Arrays.concatenate(prf, this.privateKey.getRoot(), XMSSUtil.toBytesBigEndian(index, this.params.getDigestSize())), array);
        final XMSSMTSignature build = new XMSSMTSignature.Builder(this.params).withIndex(index).withRandom(prf).build();
        long n = XMSSUtil.getTreeIndex(index, height2);
        final int leafIndex = XMSSUtil.getLeafIndex(index, height2);
        this.wotsPlus.importKeys(new byte[this.params.getDigestSize()], this.privateKey.getPublicSeed());
        final OTSHashAddress otsHashAddress = (OTSHashAddress)new OTSHashAddress.Builder().withTreeAddress(n).withOTSAddress(leafIndex).build();
        if (bdsState.get(0) == null || leafIndex == 0) {
            bdsState.put(0, new BDS(this.xmssParams, this.privateKey.getPublicSeed(), this.privateKey.getSecretKeySeed(), otsHashAddress));
        }
        build.getReducedSignatures().add(new XMSSReducedSignature.Builder(this.xmssParams).withWOTSPlusSignature(this.wotsSign(hMsg, otsHashAddress)).withAuthPath(bdsState.get(0).getAuthenticationPath()).build());
        for (int i = 1; i < this.params.getLayers(); ++i) {
            final XMSSNode root = bdsState.get(i - 1).getRoot();
            final int leafIndex2 = XMSSUtil.getLeafIndex(n, height2);
            n = XMSSUtil.getTreeIndex(n, height2);
            final OTSHashAddress otsHashAddress2 = (OTSHashAddress)new OTSHashAddress.Builder().withLayerAddress(i).withTreeAddress(n).withOTSAddress(leafIndex2).build();
            final WOTSPlusSignature wotsSign = this.wotsSign(root.getValue(), otsHashAddress2);
            if (bdsState.get(i) == null || XMSSUtil.isNewBDSInitNeeded(index, height2, i)) {
                bdsState.put(i, new BDS(this.xmssParams, this.privateKey.getPublicSeed(), this.privateKey.getSecretKeySeed(), otsHashAddress2));
            }
            build.getReducedSignatures().add(new XMSSReducedSignature.Builder(this.xmssParams).withWOTSPlusSignature(wotsSign).withAuthPath(bdsState.get(i).getAuthenticationPath()).build());
        }
        this.hasGenerated = true;
        if (this.nextKeyGenerator != null) {
            this.privateKey = this.nextKeyGenerator.getNextKey();
            this.nextKeyGenerator = this.privateKey;
        }
        else {
            this.privateKey = null;
        }
        return build.toByteArray();
    }
    
    public boolean verifySignature(final byte[] array, final byte[] array2) {
        if (array == null) {
            throw new NullPointerException("message == null");
        }
        if (array2 == null) {
            throw new NullPointerException("signature == null");
        }
        if (this.publicKey == null) {
            throw new NullPointerException("publicKey == null");
        }
        final XMSSMTSignature build = new XMSSMTSignature.Builder(this.params).withSignature(array2).build();
        final byte[] hMsg = this.wotsPlus.getKhf().HMsg(Arrays.concatenate(build.getRandom(), this.publicKey.getRoot(), XMSSUtil.toBytesBigEndian(build.getIndex(), this.params.getDigestSize())), array);
        final long index = build.getIndex();
        final int height = this.xmssParams.getHeight();
        long n = XMSSUtil.getTreeIndex(index, height);
        final int leafIndex = XMSSUtil.getLeafIndex(index, height);
        this.wotsPlus.importKeys(new byte[this.params.getDigestSize()], this.publicKey.getPublicSeed());
        XMSSNode xmssNode = XMSSVerifierUtil.getRootNodeFromSignature(this.wotsPlus, height, hMsg, build.getReducedSignatures().get(0), (OTSHashAddress)new OTSHashAddress.Builder().withTreeAddress(n).withOTSAddress(leafIndex).build(), leafIndex);
        for (int i = 1; i < this.params.getLayers(); ++i) {
            final XMSSReducedSignature xmssReducedSignature = build.getReducedSignatures().get(i);
            final int leafIndex2 = XMSSUtil.getLeafIndex(n, height);
            n = XMSSUtil.getTreeIndex(n, height);
            xmssNode = XMSSVerifierUtil.getRootNodeFromSignature(this.wotsPlus, height, xmssNode.getValue(), xmssReducedSignature, (OTSHashAddress)new OTSHashAddress.Builder().withLayerAddress(i).withTreeAddress(n).withOTSAddress(leafIndex2).build(), leafIndex2);
        }
        return Arrays.constantTimeAreEqual(xmssNode.getValue(), this.publicKey.getRoot());
    }
    
    private WOTSPlusSignature wotsSign(final byte[] array, final OTSHashAddress otsHashAddress) {
        if (array.length != this.params.getDigestSize()) {
            throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest");
        }
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        this.wotsPlus.importKeys(this.wotsPlus.getWOTSPlusSecretKey(this.privateKey.getSecretKeySeed(), otsHashAddress), this.privateKey.getPublicSeed());
        return this.wotsPlus.sign(array, otsHashAddress);
    }
    
    public AsymmetricKeyParameter getUpdatedPrivateKey() {
        if (this.hasGenerated) {
            final XMSSMTPrivateKeyParameters privateKey = this.privateKey;
            this.privateKey = null;
            this.nextKeyGenerator = null;
            return privateKey;
        }
        final XMSSMTPrivateKeyParameters nextKey = this.nextKeyGenerator.getNextKey();
        this.nextKeyGenerator = null;
        return nextKey;
    }
}
