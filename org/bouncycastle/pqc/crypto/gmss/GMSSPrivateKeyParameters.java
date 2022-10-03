package org.bouncycastle.pqc.crypto.gmss;

import org.bouncycastle.pqc.crypto.gmss.util.WinternitzOTSignature;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.pqc.crypto.gmss.util.GMSSRandom;
import org.bouncycastle.crypto.Digest;
import java.util.Vector;

public class GMSSPrivateKeyParameters extends GMSSKeyParameters
{
    private int[] index;
    private byte[][] currentSeeds;
    private byte[][] nextNextSeeds;
    private byte[][][] currentAuthPaths;
    private byte[][][] nextAuthPaths;
    private Treehash[][] currentTreehash;
    private Treehash[][] nextTreehash;
    private Vector[] currentStack;
    private Vector[] nextStack;
    private Vector[][] currentRetain;
    private Vector[][] nextRetain;
    private byte[][][] keep;
    private GMSSLeaf[] nextNextLeaf;
    private GMSSLeaf[] upperLeaf;
    private GMSSLeaf[] upperTreehashLeaf;
    private int[] minTreehash;
    private GMSSParameters gmssPS;
    private byte[][] nextRoot;
    private GMSSRootCalc[] nextNextRoot;
    private byte[][] currentRootSig;
    private GMSSRootSig[] nextRootSig;
    private GMSSDigestProvider digestProvider;
    private boolean used;
    private int[] heightOfTrees;
    private int[] otsIndex;
    private int[] K;
    private int numLayer;
    private Digest messDigestTrees;
    private int mdLength;
    private GMSSRandom gmssRandom;
    private int[] numLeafs;
    
    public GMSSPrivateKeyParameters(final byte[][] array, final byte[][] array2, final byte[][][] array3, final byte[][][] array4, final Treehash[][] array5, final Treehash[][] array6, final Vector[] array7, final Vector[] array8, final Vector[][] array9, final Vector[][] array10, final byte[][] array11, final byte[][] array12, final GMSSParameters gmssParameters, final GMSSDigestProvider gmssDigestProvider) {
        this(null, array, array2, array3, array4, null, array5, array6, array7, array8, array9, array10, null, null, null, null, array11, null, array12, null, gmssParameters, gmssDigestProvider);
    }
    
    public GMSSPrivateKeyParameters(final int[] index, final byte[][] currentSeeds, final byte[][] nextNextSeeds, final byte[][][] currentAuthPaths, final byte[][][] nextAuthPaths, final byte[][][] keep, final Treehash[][] currentTreehash, final Treehash[][] nextTreehash, final Vector[] currentStack, final Vector[] nextStack, final Vector[][] currentRetain, final Vector[][] nextRetain, final GMSSLeaf[] nextNextLeaf, final GMSSLeaf[] upperLeaf, final GMSSLeaf[] upperTreehashLeaf, final int[] minTreehash, final byte[][] nextRoot, final GMSSRootCalc[] nextNextRoot, final byte[][] currentRootSig, final GMSSRootSig[] nextRootSig, final GMSSParameters gmssPS, final GMSSDigestProvider digestProvider) {
        super(true, gmssPS);
        this.used = false;
        this.messDigestTrees = digestProvider.get();
        this.mdLength = this.messDigestTrees.getDigestSize();
        this.gmssPS = gmssPS;
        this.otsIndex = gmssPS.getWinternitzParameter();
        this.K = gmssPS.getK();
        this.heightOfTrees = gmssPS.getHeightOfTrees();
        this.numLayer = this.gmssPS.getNumOfLayers();
        if (index == null) {
            this.index = new int[this.numLayer];
            for (int i = 0; i < this.numLayer; ++i) {
                this.index[i] = 0;
            }
        }
        else {
            this.index = index;
        }
        this.currentSeeds = currentSeeds;
        this.nextNextSeeds = nextNextSeeds;
        this.currentAuthPaths = currentAuthPaths;
        this.nextAuthPaths = nextAuthPaths;
        if (keep == null) {
            this.keep = new byte[this.numLayer][][];
            for (int j = 0; j < this.numLayer; ++j) {
                this.keep[j] = new byte[(int)Math.floor(this.heightOfTrees[j] / 2)][this.mdLength];
            }
        }
        else {
            this.keep = keep;
        }
        if (currentStack == null) {
            this.currentStack = new Vector[this.numLayer];
            for (int k = 0; k < this.numLayer; ++k) {
                this.currentStack[k] = new Vector();
            }
        }
        else {
            this.currentStack = currentStack;
        }
        if (nextStack == null) {
            this.nextStack = new Vector[this.numLayer - 1];
            for (int l = 0; l < this.numLayer - 1; ++l) {
                this.nextStack[l] = new Vector();
            }
        }
        else {
            this.nextStack = nextStack;
        }
        this.currentTreehash = currentTreehash;
        this.nextTreehash = nextTreehash;
        this.currentRetain = currentRetain;
        this.nextRetain = nextRetain;
        this.nextRoot = nextRoot;
        this.digestProvider = digestProvider;
        if (nextNextRoot == null) {
            this.nextNextRoot = new GMSSRootCalc[this.numLayer - 1];
            for (int n = 0; n < this.numLayer - 1; ++n) {
                this.nextNextRoot[n] = new GMSSRootCalc(this.heightOfTrees[n + 1], this.K[n + 1], this.digestProvider);
            }
        }
        else {
            this.nextNextRoot = nextNextRoot;
        }
        this.currentRootSig = currentRootSig;
        this.numLeafs = new int[this.numLayer];
        for (int n2 = 0; n2 < this.numLayer; ++n2) {
            this.numLeafs[n2] = 1 << this.heightOfTrees[n2];
        }
        this.gmssRandom = new GMSSRandom(this.messDigestTrees);
        if (this.numLayer > 1) {
            if (nextNextLeaf == null) {
                this.nextNextLeaf = new GMSSLeaf[this.numLayer - 2];
                for (int n3 = 0; n3 < this.numLayer - 2; ++n3) {
                    this.nextNextLeaf[n3] = new GMSSLeaf(digestProvider.get(), this.otsIndex[n3 + 1], this.numLeafs[n3 + 2], this.nextNextSeeds[n3]);
                }
            }
            else {
                this.nextNextLeaf = nextNextLeaf;
            }
        }
        else {
            this.nextNextLeaf = new GMSSLeaf[0];
        }
        if (upperLeaf == null) {
            this.upperLeaf = new GMSSLeaf[this.numLayer - 1];
            for (int n4 = 0; n4 < this.numLayer - 1; ++n4) {
                this.upperLeaf[n4] = new GMSSLeaf(digestProvider.get(), this.otsIndex[n4], this.numLeafs[n4 + 1], this.currentSeeds[n4]);
            }
        }
        else {
            this.upperLeaf = upperLeaf;
        }
        if (upperTreehashLeaf == null) {
            this.upperTreehashLeaf = new GMSSLeaf[this.numLayer - 1];
            for (int n5 = 0; n5 < this.numLayer - 1; ++n5) {
                this.upperTreehashLeaf[n5] = new GMSSLeaf(digestProvider.get(), this.otsIndex[n5], this.numLeafs[n5 + 1]);
            }
        }
        else {
            this.upperTreehashLeaf = upperTreehashLeaf;
        }
        if (minTreehash == null) {
            this.minTreehash = new int[this.numLayer - 1];
            for (int n6 = 0; n6 < this.numLayer - 1; ++n6) {
                this.minTreehash[n6] = -1;
            }
        }
        else {
            this.minTreehash = minTreehash;
        }
        final byte[] array = new byte[this.mdLength];
        final byte[] array2 = new byte[this.mdLength];
        if (nextRootSig == null) {
            this.nextRootSig = new GMSSRootSig[this.numLayer - 1];
            for (int n7 = 0; n7 < this.numLayer - 1; ++n7) {
                System.arraycopy(currentSeeds[n7], 0, array, 0, this.mdLength);
                this.gmssRandom.nextSeed(array);
                (this.nextRootSig[n7] = new GMSSRootSig(digestProvider.get(), this.otsIndex[n7], this.heightOfTrees[n7 + 1])).initSign(this.gmssRandom.nextSeed(array), nextRoot[n7]);
            }
        }
        else {
            this.nextRootSig = nextRootSig;
        }
    }
    
    private GMSSPrivateKeyParameters(final GMSSPrivateKeyParameters gmssPrivateKeyParameters) {
        super(true, gmssPrivateKeyParameters.getParameters());
        this.used = false;
        this.index = Arrays.clone(gmssPrivateKeyParameters.index);
        this.currentSeeds = Arrays.clone(gmssPrivateKeyParameters.currentSeeds);
        this.nextNextSeeds = Arrays.clone(gmssPrivateKeyParameters.nextNextSeeds);
        this.currentAuthPaths = Arrays.clone(gmssPrivateKeyParameters.currentAuthPaths);
        this.nextAuthPaths = Arrays.clone(gmssPrivateKeyParameters.nextAuthPaths);
        this.currentTreehash = gmssPrivateKeyParameters.currentTreehash;
        this.nextTreehash = gmssPrivateKeyParameters.nextTreehash;
        this.currentStack = gmssPrivateKeyParameters.currentStack;
        this.nextStack = gmssPrivateKeyParameters.nextStack;
        this.currentRetain = gmssPrivateKeyParameters.currentRetain;
        this.nextRetain = gmssPrivateKeyParameters.nextRetain;
        this.keep = Arrays.clone(gmssPrivateKeyParameters.keep);
        this.nextNextLeaf = gmssPrivateKeyParameters.nextNextLeaf;
        this.upperLeaf = gmssPrivateKeyParameters.upperLeaf;
        this.upperTreehashLeaf = gmssPrivateKeyParameters.upperTreehashLeaf;
        this.minTreehash = gmssPrivateKeyParameters.minTreehash;
        this.gmssPS = gmssPrivateKeyParameters.gmssPS;
        this.nextRoot = Arrays.clone(gmssPrivateKeyParameters.nextRoot);
        this.nextNextRoot = gmssPrivateKeyParameters.nextNextRoot;
        this.currentRootSig = gmssPrivateKeyParameters.currentRootSig;
        this.nextRootSig = gmssPrivateKeyParameters.nextRootSig;
        this.digestProvider = gmssPrivateKeyParameters.digestProvider;
        this.heightOfTrees = gmssPrivateKeyParameters.heightOfTrees;
        this.otsIndex = gmssPrivateKeyParameters.otsIndex;
        this.K = gmssPrivateKeyParameters.K;
        this.numLayer = gmssPrivateKeyParameters.numLayer;
        this.messDigestTrees = gmssPrivateKeyParameters.messDigestTrees;
        this.mdLength = gmssPrivateKeyParameters.mdLength;
        this.gmssRandom = gmssPrivateKeyParameters.gmssRandom;
        this.numLeafs = gmssPrivateKeyParameters.numLeafs;
    }
    
    public boolean isUsed() {
        return this.used;
    }
    
    public void markUsed() {
        this.used = true;
    }
    
    public GMSSPrivateKeyParameters nextKey() {
        final GMSSPrivateKeyParameters gmssPrivateKeyParameters = new GMSSPrivateKeyParameters(this);
        gmssPrivateKeyParameters.nextKey(this.gmssPS.getNumOfLayers() - 1);
        return gmssPrivateKeyParameters;
    }
    
    private void nextKey(final int n) {
        if (n == this.numLayer - 1) {
            final int[] index = this.index;
            ++index[n];
        }
        if (this.index[n] == this.numLeafs[n]) {
            if (this.numLayer != 1) {
                this.nextTree(n);
                this.index[n] = 0;
            }
        }
        else {
            this.updateKey(n);
        }
    }
    
    private void nextTree(final int n) {
        if (n > 0) {
            final int[] index = this.index;
            final int n2 = n - 1;
            ++index[n2];
            boolean b = true;
            int n3 = n;
            do {
                --n3;
                if (this.index[n3] < this.numLeafs[n3]) {
                    b = false;
                }
            } while (b && n3 > 0);
            if (!b) {
                this.gmssRandom.nextSeed(this.currentSeeds[n]);
                this.nextRootSig[n - 1].updateSign();
                if (n > 1) {
                    this.nextNextLeaf[n - 1 - 1] = this.nextNextLeaf[n - 1 - 1].nextLeaf();
                }
                this.upperLeaf[n - 1] = this.upperLeaf[n - 1].nextLeaf();
                if (this.minTreehash[n - 1] >= 0) {
                    this.upperTreehashLeaf[n - 1] = this.upperTreehashLeaf[n - 1].nextLeaf();
                    final byte[] leaf = this.upperTreehashLeaf[n - 1].getLeaf();
                    try {
                        this.currentTreehash[n - 1][this.minTreehash[n - 1]].update(this.gmssRandom, leaf);
                        if (this.currentTreehash[n - 1][this.minTreehash[n - 1]].wasFinished()) {}
                    }
                    catch (final Exception ex) {
                        System.out.println(ex);
                    }
                }
                this.updateNextNextAuthRoot(n);
                this.currentRootSig[n - 1] = this.nextRootSig[n - 1].getSig();
                for (int i = 0; i < this.heightOfTrees[n] - this.K[n]; ++i) {
                    this.currentTreehash[n][i] = this.nextTreehash[n - 1][i];
                    this.nextTreehash[n - 1][i] = this.nextNextRoot[n - 1].getTreehash()[i];
                }
                for (int j = 0; j < this.heightOfTrees[n]; ++j) {
                    System.arraycopy(this.nextAuthPaths[n - 1][j], 0, this.currentAuthPaths[n][j], 0, this.mdLength);
                    System.arraycopy(this.nextNextRoot[n - 1].getAuthPath()[j], 0, this.nextAuthPaths[n - 1][j], 0, this.mdLength);
                }
                for (int k = 0; k < this.K[n] - 1; ++k) {
                    this.currentRetain[n][k] = this.nextRetain[n - 1][k];
                    this.nextRetain[n - 1][k] = this.nextNextRoot[n - 1].getRetain()[k];
                }
                this.currentStack[n] = this.nextStack[n - 1];
                this.nextStack[n - 1] = this.nextNextRoot[n - 1].getStack();
                this.nextRoot[n - 1] = this.nextNextRoot[n - 1].getRoot();
                final byte[] array = new byte[this.mdLength];
                final byte[] array2 = new byte[this.mdLength];
                System.arraycopy(this.currentSeeds[n - 1], 0, array2, 0, this.mdLength);
                this.gmssRandom.nextSeed(array2);
                this.gmssRandom.nextSeed(array2);
                this.nextRootSig[n - 1].initSign(this.gmssRandom.nextSeed(array2), this.nextRoot[n - 1]);
                this.nextKey(n - 1);
            }
        }
    }
    
    private void updateKey(final int n) {
        this.computeAuthPaths(n);
        if (n > 0) {
            if (n > 1) {
                this.nextNextLeaf[n - 1 - 1] = this.nextNextLeaf[n - 1 - 1].nextLeaf();
            }
            this.upperLeaf[n - 1] = this.upperLeaf[n - 1].nextLeaf();
            final int n2 = (int)Math.floor(this.getNumLeafs(n) * 2 / (double)(this.heightOfTrees[n - 1] - this.K[n - 1]));
            if (this.index[n] % n2 == 1) {
                if (this.index[n] > 1 && this.minTreehash[n - 1] >= 0) {
                    final byte[] leaf = this.upperTreehashLeaf[n - 1].getLeaf();
                    try {
                        this.currentTreehash[n - 1][this.minTreehash[n - 1]].update(this.gmssRandom, leaf);
                        if (this.currentTreehash[n - 1][this.minTreehash[n - 1]].wasFinished()) {}
                    }
                    catch (final Exception ex) {
                        System.out.println(ex);
                    }
                }
                this.minTreehash[n - 1] = this.getMinTreehashIndex(n - 1);
                if (this.minTreehash[n - 1] >= 0) {
                    this.upperTreehashLeaf[n - 1] = new GMSSLeaf(this.digestProvider.get(), this.otsIndex[n - 1], n2, this.currentTreehash[n - 1][this.minTreehash[n - 1]].getSeedActive());
                    this.upperTreehashLeaf[n - 1] = this.upperTreehashLeaf[n - 1].nextLeaf();
                }
            }
            else if (this.minTreehash[n - 1] >= 0) {
                this.upperTreehashLeaf[n - 1] = this.upperTreehashLeaf[n - 1].nextLeaf();
            }
            this.nextRootSig[n - 1].updateSign();
            if (this.index[n] == 1) {
                this.nextNextRoot[n - 1].initialize(new Vector());
            }
            this.updateNextNextAuthRoot(n);
        }
    }
    
    private int getMinTreehashIndex(final int n) {
        int n2 = -1;
        for (int i = 0; i < this.heightOfTrees[n] - this.K[n]; ++i) {
            if (this.currentTreehash[n][i].wasInitialized() && !this.currentTreehash[n][i].wasFinished()) {
                if (n2 == -1) {
                    n2 = i;
                }
                else if (this.currentTreehash[n][i].getLowestNodeHeight() < this.currentTreehash[n][n2].getLowestNodeHeight()) {
                    n2 = i;
                }
            }
        }
        return n2;
    }
    
    private void computeAuthPaths(final int n) {
        final int n2 = this.index[n];
        final int n3 = this.heightOfTrees[n];
        final int n4 = this.K[n];
        for (int i = 0; i < n3 - n4; ++i) {
            this.currentTreehash[n][i].updateNextSeed(this.gmssRandom);
        }
        final int heightOfPhi = this.heightOfPhi(n2);
        final byte[] array = new byte[this.mdLength];
        final byte[] nextSeed = this.gmssRandom.nextSeed(this.currentSeeds[n]);
        final int n5 = n2 >>> heightOfPhi + 1 & 0x1;
        final byte[] array2 = new byte[this.mdLength];
        if (heightOfPhi < n3 - 1 && n5 == 0) {
            System.arraycopy(this.currentAuthPaths[n][heightOfPhi], 0, array2, 0, this.mdLength);
        }
        final byte[] array3 = new byte[this.mdLength];
        if (heightOfPhi == 0) {
            byte[] array4;
            if (n == this.numLayer - 1) {
                array4 = new WinternitzOTSignature(nextSeed, this.digestProvider.get(), this.otsIndex[n]).getPublicKey();
            }
            else {
                final byte[] array5 = new byte[this.mdLength];
                System.arraycopy(this.currentSeeds[n], 0, array5, 0, this.mdLength);
                this.gmssRandom.nextSeed(array5);
                array4 = this.upperLeaf[n].getLeaf();
                this.upperLeaf[n].initLeafCalc(array5);
            }
            System.arraycopy(array4, 0, this.currentAuthPaths[n][0], 0, this.mdLength);
        }
        else {
            final byte[] array6 = new byte[this.mdLength << 1];
            System.arraycopy(this.currentAuthPaths[n][heightOfPhi - 1], 0, array6, 0, this.mdLength);
            System.arraycopy(this.keep[n][(int)Math.floor((heightOfPhi - 1) / 2)], 0, array6, this.mdLength, this.mdLength);
            this.messDigestTrees.update(array6, 0, array6.length);
            this.currentAuthPaths[n][heightOfPhi] = new byte[this.messDigestTrees.getDigestSize()];
            this.messDigestTrees.doFinal(this.currentAuthPaths[n][heightOfPhi], 0);
            for (int j = 0; j < heightOfPhi; ++j) {
                if (j < n3 - n4) {
                    if (this.currentTreehash[n][j].wasFinished()) {
                        System.arraycopy(this.currentTreehash[n][j].getFirstNode(), 0, this.currentAuthPaths[n][j], 0, this.mdLength);
                        this.currentTreehash[n][j].destroy();
                    }
                    else {
                        System.err.println("Treehash (" + n + "," + j + ") not finished when needed in AuthPathComputation");
                    }
                }
                if (j < n3 - 1 && j >= n3 - n4 && this.currentRetain[n][j - (n3 - n4)].size() > 0) {
                    System.arraycopy(this.currentRetain[n][j - (n3 - n4)].lastElement(), 0, this.currentAuthPaths[n][j], 0, this.mdLength);
                    this.currentRetain[n][j - (n3 - n4)].removeElementAt(this.currentRetain[n][j - (n3 - n4)].size() - 1);
                }
                if (j < n3 - n4 && n2 + 3 * (1 << j) < this.numLeafs[n]) {
                    this.currentTreehash[n][j].initialize();
                }
            }
        }
        if (heightOfPhi < n3 - 1 && n5 == 0) {
            System.arraycopy(array2, 0, this.keep[n][(int)Math.floor(heightOfPhi / 2)], 0, this.mdLength);
        }
        if (n == this.numLayer - 1) {
            for (int k = 1; k <= (n3 - n4) / 2; ++k) {
                final int minTreehashIndex = this.getMinTreehashIndex(n);
                if (minTreehashIndex >= 0) {
                    try {
                        final byte[] array7 = new byte[this.mdLength];
                        System.arraycopy(this.currentTreehash[n][minTreehashIndex].getSeedActive(), 0, array7, 0, this.mdLength);
                        this.currentTreehash[n][minTreehashIndex].update(this.gmssRandom, new WinternitzOTSignature(this.gmssRandom.nextSeed(array7), this.digestProvider.get(), this.otsIndex[n]).getPublicKey());
                    }
                    catch (final Exception ex) {
                        System.out.println(ex);
                    }
                }
            }
        }
        else {
            this.minTreehash[n] = this.getMinTreehashIndex(n);
        }
    }
    
    private int heightOfPhi(final int n) {
        if (n == 0) {
            return -1;
        }
        int n2 = 0;
        for (int n3 = 1; n % n3 == 0; n3 *= 2, ++n2) {}
        return n2 - 1;
    }
    
    private void updateNextNextAuthRoot(final int n) {
        final byte[] array = new byte[this.mdLength];
        final byte[] nextSeed = this.gmssRandom.nextSeed(this.nextNextSeeds[n - 1]);
        if (n == this.numLayer - 1) {
            this.nextNextRoot[n - 1].update(this.nextNextSeeds[n - 1], new WinternitzOTSignature(nextSeed, this.digestProvider.get(), this.otsIndex[n]).getPublicKey());
        }
        else {
            this.nextNextRoot[n - 1].update(this.nextNextSeeds[n - 1], this.nextNextLeaf[n - 1].getLeaf());
            this.nextNextLeaf[n - 1].initLeafCalc(this.nextNextSeeds[n - 1]);
        }
    }
    
    public int[] getIndex() {
        return this.index;
    }
    
    public int getIndex(final int n) {
        return this.index[n];
    }
    
    public byte[][] getCurrentSeeds() {
        return Arrays.clone(this.currentSeeds);
    }
    
    public byte[][][] getCurrentAuthPaths() {
        return Arrays.clone(this.currentAuthPaths);
    }
    
    public byte[] getSubtreeRootSig(final int n) {
        return this.currentRootSig[n];
    }
    
    public GMSSDigestProvider getName() {
        return this.digestProvider;
    }
    
    public int getNumLeafs(final int n) {
        return this.numLeafs[n];
    }
}
