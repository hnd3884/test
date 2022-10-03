package org.bouncycastle.pqc.crypto.xmss;

import java.util.Iterator;
import java.util.Collection;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Stack;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;
import java.io.Serializable;

public final class BDS implements Serializable
{
    private static final long serialVersionUID = 1L;
    private transient WOTSPlus wotsPlus;
    private final int treeHeight;
    private final List<BDSTreeHash> treeHashInstances;
    private int k;
    private XMSSNode root;
    private List<XMSSNode> authenticationPath;
    private Map<Integer, LinkedList<XMSSNode>> retain;
    private Stack<XMSSNode> stack;
    private Map<Integer, XMSSNode> keep;
    private int index;
    private boolean used;
    
    BDS(final XMSSParameters xmssParameters, final int index) {
        this(xmssParameters.getWOTSPlus(), xmssParameters.getHeight(), xmssParameters.getK());
        this.index = index;
        this.used = true;
    }
    
    BDS(final XMSSParameters xmssParameters, final byte[] array, final byte[] array2, final OTSHashAddress otsHashAddress) {
        this(xmssParameters.getWOTSPlus(), xmssParameters.getHeight(), xmssParameters.getK());
        this.initialize(array, array2, otsHashAddress);
    }
    
    BDS(final XMSSParameters xmssParameters, final byte[] array, final byte[] array2, final OTSHashAddress otsHashAddress, final int n) {
        this(xmssParameters.getWOTSPlus(), xmssParameters.getHeight(), xmssParameters.getK());
        this.initialize(array, array2, otsHashAddress);
        while (this.index < n) {
            this.nextAuthenticationPath(array, array2, otsHashAddress);
            this.used = false;
        }
    }
    
    private BDS(final WOTSPlus wotsPlus, final int treeHeight, final int k) {
        this.wotsPlus = wotsPlus;
        this.treeHeight = treeHeight;
        this.k = k;
        if (k > treeHeight || k < 2 || (treeHeight - k) % 2 != 0) {
            throw new IllegalArgumentException("illegal value for BDS parameter k");
        }
        this.authenticationPath = new ArrayList<XMSSNode>();
        this.retain = new TreeMap<Integer, LinkedList<XMSSNode>>();
        this.stack = new Stack<XMSSNode>();
        this.treeHashInstances = new ArrayList<BDSTreeHash>();
        for (int i = 0; i < treeHeight - k; ++i) {
            this.treeHashInstances.add(new BDSTreeHash(i));
        }
        this.keep = new TreeMap<Integer, XMSSNode>();
        this.index = 0;
        this.used = false;
    }
    
    private BDS(final BDS bds, final byte[] array, final byte[] array2, final OTSHashAddress otsHashAddress) {
        this.wotsPlus = bds.wotsPlus;
        this.treeHeight = bds.treeHeight;
        this.k = bds.k;
        this.root = bds.root;
        this.authenticationPath = new ArrayList<XMSSNode>(bds.authenticationPath);
        this.retain = bds.retain;
        this.stack = (Stack)bds.stack.clone();
        this.treeHashInstances = bds.treeHashInstances;
        this.keep = new TreeMap<Integer, XMSSNode>(bds.keep);
        this.index = bds.index;
        this.nextAuthenticationPath(array, array2, otsHashAddress);
        bds.used = true;
    }
    
    public BDS getNextState(final byte[] array, final byte[] array2, final OTSHashAddress otsHashAddress) {
        return new BDS(this, array, array2, otsHashAddress);
    }
    
    private void initialize(final byte[] array, final byte[] array2, OTSHashAddress otsHashAddress) {
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        LTreeAddress lTreeAddress = (LTreeAddress)new LTreeAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).build();
        HashTreeAddress hashTreeAddress = (HashTreeAddress)new HashTreeAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).build();
        for (int i = 0; i < 1 << this.treeHeight; ++i) {
            otsHashAddress = (OTSHashAddress)new OTSHashAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).withOTSAddress(i).withChainAddress(otsHashAddress.getChainAddress()).withHashAddress(otsHashAddress.getHashAddress()).withKeyAndMask(otsHashAddress.getKeyAndMask()).build();
            this.wotsPlus.importKeys(this.wotsPlus.getWOTSPlusSecretKey(array2, otsHashAddress), array);
            final WOTSPlusPublicKeyParameters publicKey = this.wotsPlus.getPublicKey(otsHashAddress);
            lTreeAddress = (LTreeAddress)new LTreeAddress.Builder().withLayerAddress(lTreeAddress.getLayerAddress()).withTreeAddress(lTreeAddress.getTreeAddress()).withLTreeAddress(i).withTreeHeight(lTreeAddress.getTreeHeight()).withTreeIndex(lTreeAddress.getTreeIndex()).withKeyAndMask(lTreeAddress.getKeyAndMask()).build();
            XMSSNode lTree = XMSSNodeUtil.lTree(this.wotsPlus, publicKey, lTreeAddress);
            hashTreeAddress = (HashTreeAddress)new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress.getLayerAddress()).withTreeAddress(hashTreeAddress.getTreeAddress()).withTreeIndex(i).withKeyAndMask(hashTreeAddress.getKeyAndMask()).build();
            while (!this.stack.isEmpty() && this.stack.peek().getHeight() == lTree.getHeight()) {
                final int n = (int)Math.floor(i / (1 << lTree.getHeight()));
                if (n == 1) {
                    this.authenticationPath.add(lTree.clone());
                }
                if (n == 3 && lTree.getHeight() < this.treeHeight - this.k) {
                    this.treeHashInstances.get(lTree.getHeight()).setNode(lTree.clone());
                }
                if (n >= 3 && (n & 0x1) == 0x1 && lTree.getHeight() >= this.treeHeight - this.k && lTree.getHeight() <= this.treeHeight - 2) {
                    if (this.retain.get(lTree.getHeight()) == null) {
                        final LinkedList<XMSSNode> list = new LinkedList<XMSSNode>();
                        list.add(lTree.clone());
                        this.retain.put(lTree.getHeight(), list);
                    }
                    else {
                        this.retain.get(lTree.getHeight()).add(lTree.clone());
                    }
                }
                final HashTreeAddress hashTreeAddress2 = (HashTreeAddress)new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress.getLayerAddress()).withTreeAddress(hashTreeAddress.getTreeAddress()).withTreeHeight(hashTreeAddress.getTreeHeight()).withTreeIndex((hashTreeAddress.getTreeIndex() - 1) / 2).withKeyAndMask(hashTreeAddress.getKeyAndMask()).build();
                final XMSSNode randomizeHash = XMSSNodeUtil.randomizeHash(this.wotsPlus, this.stack.pop(), lTree, hashTreeAddress2);
                lTree = new XMSSNode(randomizeHash.getHeight() + 1, randomizeHash.getValue());
                hashTreeAddress = (HashTreeAddress)new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress2.getLayerAddress()).withTreeAddress(hashTreeAddress2.getTreeAddress()).withTreeHeight(hashTreeAddress2.getTreeHeight() + 1).withTreeIndex(hashTreeAddress2.getTreeIndex()).withKeyAndMask(hashTreeAddress2.getKeyAndMask()).build();
            }
            this.stack.push(lTree);
        }
        this.root = this.stack.pop();
    }
    
    private void nextAuthenticationPath(final byte[] array, final byte[] array2, OTSHashAddress otsHashAddress) {
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        if (this.used) {
            throw new IllegalStateException("index already used");
        }
        if (this.index > (1 << this.treeHeight) - 2) {
            throw new IllegalStateException("index out of bounds");
        }
        final LTreeAddress lTreeAddress = (LTreeAddress)new LTreeAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).build();
        final HashTreeAddress hashTreeAddress = (HashTreeAddress)new HashTreeAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).build();
        final int calculateTau = XMSSUtil.calculateTau(this.index, this.treeHeight);
        if ((this.index >> calculateTau + 1 & 0x1) == 0x0 && calculateTau < this.treeHeight - 1) {
            this.keep.put(calculateTau, this.authenticationPath.get(calculateTau).clone());
        }
        if (calculateTau == 0) {
            otsHashAddress = (OTSHashAddress)new OTSHashAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).withOTSAddress(this.index).withChainAddress(otsHashAddress.getChainAddress()).withHashAddress(otsHashAddress.getHashAddress()).withKeyAndMask(otsHashAddress.getKeyAndMask()).build();
            this.wotsPlus.importKeys(this.wotsPlus.getWOTSPlusSecretKey(array2, otsHashAddress), array);
            this.authenticationPath.set(0, XMSSNodeUtil.lTree(this.wotsPlus, this.wotsPlus.getPublicKey(otsHashAddress), (LTreeAddress)new LTreeAddress.Builder().withLayerAddress(lTreeAddress.getLayerAddress()).withTreeAddress(lTreeAddress.getTreeAddress()).withLTreeAddress(this.index).withTreeHeight(lTreeAddress.getTreeHeight()).withTreeIndex(lTreeAddress.getTreeIndex()).withKeyAndMask(lTreeAddress.getKeyAndMask()).build()));
        }
        else {
            final XMSSNode randomizeHash = XMSSNodeUtil.randomizeHash(this.wotsPlus, this.authenticationPath.get(calculateTau - 1), this.keep.get(calculateTau - 1), new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress.getLayerAddress()).withTreeAddress(hashTreeAddress.getTreeAddress()).withTreeHeight(calculateTau - 1).withTreeIndex(this.index >> calculateTau).withKeyAndMask(hashTreeAddress.getKeyAndMask()).build());
            this.authenticationPath.set(calculateTau, new XMSSNode(randomizeHash.getHeight() + 1, randomizeHash.getValue()));
            this.keep.remove(calculateTau - 1);
            for (int i = 0; i < calculateTau; ++i) {
                if (i < this.treeHeight - this.k) {
                    this.authenticationPath.set(i, this.treeHashInstances.get(i).getTailNode());
                }
                else {
                    this.authenticationPath.set(i, this.retain.get(i).removeFirst());
                }
            }
            for (int min = Math.min(calculateTau, this.treeHeight - this.k), j = 0; j < min; ++j) {
                final int n = this.index + 1 + 3 * (1 << j);
                if (n < 1 << this.treeHeight) {
                    this.treeHashInstances.get(j).initialize(n);
                }
            }
        }
        for (int k = 0; k < this.treeHeight - this.k >> 1; ++k) {
            final BDSTreeHash bdsTreeHashInstanceForUpdate = this.getBDSTreeHashInstanceForUpdate();
            if (bdsTreeHashInstanceForUpdate != null) {
                bdsTreeHashInstanceForUpdate.update(this.stack, this.wotsPlus, array, array2, otsHashAddress);
            }
        }
        ++this.index;
    }
    
    boolean isUsed() {
        return this.used;
    }
    
    private BDSTreeHash getBDSTreeHashInstanceForUpdate() {
        BDSTreeHash bdsTreeHash = null;
        for (final BDSTreeHash bdsTreeHash2 : this.treeHashInstances) {
            if (!bdsTreeHash2.isFinished()) {
                if (!bdsTreeHash2.isInitialized()) {
                    continue;
                }
                if (bdsTreeHash == null) {
                    bdsTreeHash = bdsTreeHash2;
                }
                else if (bdsTreeHash2.getHeight() < bdsTreeHash.getHeight()) {
                    bdsTreeHash = bdsTreeHash2;
                }
                else {
                    if (bdsTreeHash2.getHeight() != bdsTreeHash.getHeight() || bdsTreeHash2.getIndexLeaf() >= bdsTreeHash.getIndexLeaf()) {
                        continue;
                    }
                    bdsTreeHash = bdsTreeHash2;
                }
            }
        }
        return bdsTreeHash;
    }
    
    protected void validate() {
        if (this.authenticationPath == null) {
            throw new IllegalStateException("authenticationPath == null");
        }
        if (this.retain == null) {
            throw new IllegalStateException("retain == null");
        }
        if (this.stack == null) {
            throw new IllegalStateException("stack == null");
        }
        if (this.treeHashInstances == null) {
            throw new IllegalStateException("treeHashInstances == null");
        }
        if (this.keep == null) {
            throw new IllegalStateException("keep == null");
        }
        if (!XMSSUtil.isIndexValid(this.treeHeight, this.index)) {
            throw new IllegalStateException("index in BDS state out of bounds");
        }
    }
    
    protected int getTreeHeight() {
        return this.treeHeight;
    }
    
    protected XMSSNode getRoot() {
        return this.root.clone();
    }
    
    protected List<XMSSNode> getAuthenticationPath() {
        final ArrayList list = new ArrayList();
        final Iterator<XMSSNode> iterator = this.authenticationPath.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().clone());
        }
        return list;
    }
    
    protected void setXMSS(final XMSSParameters xmssParameters) {
        if (this.treeHeight != xmssParameters.getHeight()) {
            throw new IllegalStateException("wrong height");
        }
        this.wotsPlus = xmssParameters.getWOTSPlus();
    }
    
    protected int getIndex() {
        return this.index;
    }
}
