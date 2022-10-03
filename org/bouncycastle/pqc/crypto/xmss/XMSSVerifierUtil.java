package org.bouncycastle.pqc.crypto.xmss;

class XMSSVerifierUtil
{
    static XMSSNode getRootNodeFromSignature(final WOTSPlus wotsPlus, final int n, final byte[] array, final XMSSReducedSignature xmssReducedSignature, final OTSHashAddress otsHashAddress, final int n2) {
        if (array.length != wotsPlus.getParams().getDigestSize()) {
            throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest");
        }
        if (xmssReducedSignature == null) {
            throw new NullPointerException("signature == null");
        }
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        final LTreeAddress lTreeAddress = (LTreeAddress)new LTreeAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).withLTreeAddress(otsHashAddress.getOTSAddress()).build();
        HashTreeAddress hashTreeAddress = (HashTreeAddress)new HashTreeAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).withTreeIndex(otsHashAddress.getOTSAddress()).build();
        final XMSSNode[] array2 = { XMSSNodeUtil.lTree(wotsPlus, wotsPlus.getPublicKeyFromSignature(array, xmssReducedSignature.getWOTSPlusSignature(), otsHashAddress), lTreeAddress), null };
        for (int i = 0; i < n; ++i) {
            final HashTreeAddress hashTreeAddress2 = (HashTreeAddress)new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress.getLayerAddress()).withTreeAddress(hashTreeAddress.getTreeAddress()).withTreeHeight(i).withTreeIndex(hashTreeAddress.getTreeIndex()).withKeyAndMask(hashTreeAddress.getKeyAndMask()).build();
            if (Math.floor(n2 / (1 << i)) % 2.0 == 0.0) {
                hashTreeAddress = (HashTreeAddress)new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress2.getLayerAddress()).withTreeAddress(hashTreeAddress2.getTreeAddress()).withTreeHeight(hashTreeAddress2.getTreeHeight()).withTreeIndex(hashTreeAddress2.getTreeIndex() / 2).withKeyAndMask(hashTreeAddress2.getKeyAndMask()).build();
                array2[1] = XMSSNodeUtil.randomizeHash(wotsPlus, array2[0], xmssReducedSignature.getAuthPath().get(i), hashTreeAddress);
                array2[1] = new XMSSNode(array2[1].getHeight() + 1, array2[1].getValue());
            }
            else {
                hashTreeAddress = (HashTreeAddress)new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress2.getLayerAddress()).withTreeAddress(hashTreeAddress2.getTreeAddress()).withTreeHeight(hashTreeAddress2.getTreeHeight()).withTreeIndex((hashTreeAddress2.getTreeIndex() - 1) / 2).withKeyAndMask(hashTreeAddress2.getKeyAndMask()).build();
                array2[1] = XMSSNodeUtil.randomizeHash(wotsPlus, xmssReducedSignature.getAuthPath().get(i), array2[0], hashTreeAddress);
                array2[1] = new XMSSNode(array2[1].getHeight() + 1, array2[1].getValue());
            }
            array2[0] = array2[1];
        }
        return array2[0];
    }
}
