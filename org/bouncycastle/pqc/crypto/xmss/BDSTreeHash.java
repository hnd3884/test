package org.bouncycastle.pqc.crypto.xmss;

import java.util.Stack;
import java.io.Serializable;

class BDSTreeHash implements Serializable
{
    private static final long serialVersionUID = 1L;
    private XMSSNode tailNode;
    private final int initialHeight;
    private int height;
    private int nextIndex;
    private boolean initialized;
    private boolean finished;
    
    BDSTreeHash(final int initialHeight) {
        this.initialHeight = initialHeight;
        this.initialized = false;
        this.finished = false;
    }
    
    void initialize(final int nextIndex) {
        this.tailNode = null;
        this.height = this.initialHeight;
        this.nextIndex = nextIndex;
        this.initialized = true;
        this.finished = false;
    }
    
    void update(final Stack<XMSSNode> stack, final WOTSPlus wotsPlus, final byte[] array, final byte[] array2, OTSHashAddress otsHashAddress) {
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        if (this.finished || !this.initialized) {
            throw new IllegalStateException("finished or not initialized");
        }
        otsHashAddress = (OTSHashAddress)new OTSHashAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).withOTSAddress(this.nextIndex).withChainAddress(otsHashAddress.getChainAddress()).withHashAddress(otsHashAddress.getHashAddress()).withKeyAndMask(otsHashAddress.getKeyAndMask()).build();
        final LTreeAddress lTreeAddress = (LTreeAddress)new LTreeAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).withLTreeAddress(this.nextIndex).build();
        HashTreeAddress hashTreeAddress = (HashTreeAddress)new HashTreeAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).withTreeIndex(this.nextIndex).build();
        wotsPlus.importKeys(wotsPlus.getWOTSPlusSecretKey(array2, otsHashAddress), array);
        XMSSNode lTree;
        HashTreeAddress hashTreeAddress2;
        XMSSNode randomizeHash;
        for (lTree = XMSSNodeUtil.lTree(wotsPlus, wotsPlus.getPublicKey(otsHashAddress), lTreeAddress); !stack.isEmpty() && stack.peek().getHeight() == lTree.getHeight() && stack.peek().getHeight() != this.initialHeight; lTree = new XMSSNode(randomizeHash.getHeight() + 1, randomizeHash.getValue()), hashTreeAddress = (HashTreeAddress)new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress2.getLayerAddress()).withTreeAddress(hashTreeAddress2.getTreeAddress()).withTreeHeight(hashTreeAddress2.getTreeHeight() + 1).withTreeIndex(hashTreeAddress2.getTreeIndex()).withKeyAndMask(hashTreeAddress2.getKeyAndMask()).build()) {
            hashTreeAddress2 = (HashTreeAddress)new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress.getLayerAddress()).withTreeAddress(hashTreeAddress.getTreeAddress()).withTreeHeight(hashTreeAddress.getTreeHeight()).withTreeIndex((hashTreeAddress.getTreeIndex() - 1) / 2).withKeyAndMask(hashTreeAddress.getKeyAndMask()).build();
            randomizeHash = XMSSNodeUtil.randomizeHash(wotsPlus, stack.pop(), lTree, hashTreeAddress2);
        }
        if (this.tailNode == null) {
            this.tailNode = lTree;
        }
        else if (this.tailNode.getHeight() == lTree.getHeight()) {
            final HashTreeAddress hashTreeAddress3 = (HashTreeAddress)new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress.getLayerAddress()).withTreeAddress(hashTreeAddress.getTreeAddress()).withTreeHeight(hashTreeAddress.getTreeHeight()).withTreeIndex((hashTreeAddress.getTreeIndex() - 1) / 2).withKeyAndMask(hashTreeAddress.getKeyAndMask()).build();
            lTree = new XMSSNode(this.tailNode.getHeight() + 1, XMSSNodeUtil.randomizeHash(wotsPlus, this.tailNode, lTree, hashTreeAddress3).getValue());
            this.tailNode = lTree;
            final HashTreeAddress hashTreeAddress4 = (HashTreeAddress)new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress3.getLayerAddress()).withTreeAddress(hashTreeAddress3.getTreeAddress()).withTreeHeight(hashTreeAddress3.getTreeHeight() + 1).withTreeIndex(hashTreeAddress3.getTreeIndex()).withKeyAndMask(hashTreeAddress3.getKeyAndMask()).build();
        }
        else {
            stack.push(lTree);
        }
        if (this.tailNode.getHeight() == this.initialHeight) {
            this.finished = true;
        }
        else {
            this.height = lTree.getHeight();
            ++this.nextIndex;
        }
    }
    
    int getHeight() {
        if (!this.initialized || this.finished) {
            return Integer.MAX_VALUE;
        }
        return this.height;
    }
    
    int getIndexLeaf() {
        return this.nextIndex;
    }
    
    void setNode(final XMSSNode tailNode) {
        this.tailNode = tailNode;
        this.height = tailNode.getHeight();
        if (this.height == this.initialHeight) {
            this.finished = true;
        }
    }
    
    boolean isFinished() {
        return this.finished;
    }
    
    boolean isInitialized() {
        return this.initialized;
    }
    
    public XMSSNode getTailNode() {
        return this.tailNode.clone();
    }
}
