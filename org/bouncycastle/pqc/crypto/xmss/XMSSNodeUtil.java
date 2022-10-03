package org.bouncycastle.pqc.crypto.xmss;

class XMSSNodeUtil
{
    static XMSSNode lTree(final WOTSPlus wotsPlus, final WOTSPlusPublicKeyParameters wotsPlusPublicKeyParameters, LTreeAddress lTreeAddress) {
        if (wotsPlusPublicKeyParameters == null) {
            throw new NullPointerException("publicKey == null");
        }
        if (lTreeAddress == null) {
            throw new NullPointerException("address == null");
        }
        int i = wotsPlus.getParams().getLen();
        final byte[][] byteArray = wotsPlusPublicKeyParameters.toByteArray();
        final XMSSNode[] array = new XMSSNode[byteArray.length];
        for (int j = 0; j < byteArray.length; ++j) {
            array[j] = new XMSSNode(0, byteArray[j]);
        }
        int k;
        for (lTreeAddress = (LTreeAddress)new LTreeAddress.Builder().withLayerAddress(lTreeAddress.getLayerAddress()).withTreeAddress(lTreeAddress.getTreeAddress()).withLTreeAddress(lTreeAddress.getLTreeAddress()).withTreeHeight(0).withTreeIndex(lTreeAddress.getTreeIndex()).withKeyAndMask(lTreeAddress.getKeyAndMask()).build(); i > 1; i = (int)Math.ceil(i / 2.0), lTreeAddress = (LTreeAddress)new LTreeAddress.Builder().withLayerAddress(lTreeAddress.getLayerAddress()).withTreeAddress(lTreeAddress.getTreeAddress()).withLTreeAddress(lTreeAddress.getLTreeAddress()).withTreeHeight(lTreeAddress.getTreeHeight() + 1).withTreeIndex(lTreeAddress.getTreeIndex()).withKeyAndMask(lTreeAddress.getKeyAndMask()).build()) {
            for (k = 0; k < (int)Math.floor(i / 2); ++k) {
                lTreeAddress = (LTreeAddress)new LTreeAddress.Builder().withLayerAddress(lTreeAddress.getLayerAddress()).withTreeAddress(lTreeAddress.getTreeAddress()).withLTreeAddress(lTreeAddress.getLTreeAddress()).withTreeHeight(lTreeAddress.getTreeHeight()).withTreeIndex(k).withKeyAndMask(lTreeAddress.getKeyAndMask()).build();
                array[k] = randomizeHash(wotsPlus, array[2 * k], array[2 * k + 1], lTreeAddress);
            }
            if (i % 2 == 1) {
                array[(int)Math.floor(i / 2)] = array[i - 1];
            }
        }
        return array[0];
    }
    
    static XMSSNode randomizeHash(final WOTSPlus wotsPlus, final XMSSNode xmssNode, final XMSSNode xmssNode2, XMSSAddress xmssAddress) {
        if (xmssNode == null) {
            throw new NullPointerException("left == null");
        }
        if (xmssNode2 == null) {
            throw new NullPointerException("right == null");
        }
        if (xmssNode.getHeight() != xmssNode2.getHeight()) {
            throw new IllegalStateException("height of both nodes must be equal");
        }
        if (xmssAddress == null) {
            throw new NullPointerException("address == null");
        }
        final byte[] publicSeed = wotsPlus.getPublicSeed();
        if (xmssAddress instanceof LTreeAddress) {
            final LTreeAddress lTreeAddress = (LTreeAddress)xmssAddress;
            xmssAddress = new LTreeAddress.Builder().withLayerAddress(lTreeAddress.getLayerAddress()).withTreeAddress(lTreeAddress.getTreeAddress()).withLTreeAddress(lTreeAddress.getLTreeAddress()).withTreeHeight(lTreeAddress.getTreeHeight()).withTreeIndex(lTreeAddress.getTreeIndex()).withKeyAndMask(0).build();
        }
        else if (xmssAddress instanceof HashTreeAddress) {
            final HashTreeAddress hashTreeAddress = (HashTreeAddress)xmssAddress;
            xmssAddress = new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress.getLayerAddress()).withTreeAddress(hashTreeAddress.getTreeAddress()).withTreeHeight(hashTreeAddress.getTreeHeight()).withTreeIndex(hashTreeAddress.getTreeIndex()).withKeyAndMask(0).build();
        }
        final byte[] prf = wotsPlus.getKhf().PRF(publicSeed, xmssAddress.toByteArray());
        if (xmssAddress instanceof LTreeAddress) {
            final LTreeAddress lTreeAddress2 = (LTreeAddress)xmssAddress;
            xmssAddress = new LTreeAddress.Builder().withLayerAddress(lTreeAddress2.getLayerAddress()).withTreeAddress(lTreeAddress2.getTreeAddress()).withLTreeAddress(lTreeAddress2.getLTreeAddress()).withTreeHeight(lTreeAddress2.getTreeHeight()).withTreeIndex(lTreeAddress2.getTreeIndex()).withKeyAndMask(1).build();
        }
        else if (xmssAddress instanceof HashTreeAddress) {
            final HashTreeAddress hashTreeAddress2 = (HashTreeAddress)xmssAddress;
            xmssAddress = new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress2.getLayerAddress()).withTreeAddress(hashTreeAddress2.getTreeAddress()).withTreeHeight(hashTreeAddress2.getTreeHeight()).withTreeIndex(hashTreeAddress2.getTreeIndex()).withKeyAndMask(1).build();
        }
        final byte[] prf2 = wotsPlus.getKhf().PRF(publicSeed, xmssAddress.toByteArray());
        if (xmssAddress instanceof LTreeAddress) {
            final LTreeAddress lTreeAddress3 = (LTreeAddress)xmssAddress;
            xmssAddress = new LTreeAddress.Builder().withLayerAddress(lTreeAddress3.getLayerAddress()).withTreeAddress(lTreeAddress3.getTreeAddress()).withLTreeAddress(lTreeAddress3.getLTreeAddress()).withTreeHeight(lTreeAddress3.getTreeHeight()).withTreeIndex(lTreeAddress3.getTreeIndex()).withKeyAndMask(2).build();
        }
        else if (xmssAddress instanceof HashTreeAddress) {
            final HashTreeAddress hashTreeAddress3 = (HashTreeAddress)xmssAddress;
            xmssAddress = new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress3.getLayerAddress()).withTreeAddress(hashTreeAddress3.getTreeAddress()).withTreeHeight(hashTreeAddress3.getTreeHeight()).withTreeIndex(hashTreeAddress3.getTreeIndex()).withKeyAndMask(2).build();
        }
        final byte[] prf3 = wotsPlus.getKhf().PRF(publicSeed, xmssAddress.toByteArray());
        final int digestSize = wotsPlus.getParams().getDigestSize();
        final byte[] array = new byte[2 * digestSize];
        for (int i = 0; i < digestSize; ++i) {
            array[i] = (byte)(xmssNode.getValue()[i] ^ prf2[i]);
        }
        for (int j = 0; j < digestSize; ++j) {
            array[j + digestSize] = (byte)(xmssNode2.getValue()[j] ^ prf3[j]);
        }
        return new XMSSNode(xmssNode.getHeight(), wotsPlus.getKhf().H(prf, array));
    }
}
