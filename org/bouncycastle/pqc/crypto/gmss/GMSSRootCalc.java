package org.bouncycastle.pqc.crypto.gmss;

import org.bouncycastle.util.encoders.Hex;
import java.util.Enumeration;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.crypto.Digest;
import java.util.Vector;

public class GMSSRootCalc
{
    private int heightOfTree;
    private int mdLength;
    private Treehash[] treehash;
    private Vector[] retain;
    private byte[] root;
    private byte[][] AuthPath;
    private int K;
    private Vector tailStack;
    private Vector heightOfNodes;
    private Digest messDigestTree;
    private GMSSDigestProvider digestProvider;
    private int[] index;
    private boolean isInitialized;
    private boolean isFinished;
    private int indexForNextSeed;
    private int heightOfNextSeed;
    
    public GMSSRootCalc(final Digest digest, final byte[][] array, final int[] array2, final Treehash[] array3, final Vector[] array4) {
        this.messDigestTree = this.digestProvider.get();
        this.digestProvider = this.digestProvider;
        this.heightOfTree = array2[0];
        this.mdLength = array2[1];
        this.K = array2[2];
        this.indexForNextSeed = array2[3];
        this.heightOfNextSeed = array2[4];
        if (array2[5] == 1) {
            this.isFinished = true;
        }
        else {
            this.isFinished = false;
        }
        if (array2[6] == 1) {
            this.isInitialized = true;
        }
        else {
            this.isInitialized = false;
        }
        final int n = array2[7];
        this.index = new int[this.heightOfTree];
        for (int i = 0; i < this.heightOfTree; ++i) {
            this.index[i] = array2[8 + i];
        }
        this.heightOfNodes = new Vector();
        for (int j = 0; j < n; ++j) {
            this.heightOfNodes.addElement(Integers.valueOf(array2[8 + this.heightOfTree + j]));
        }
        this.root = array[0];
        this.AuthPath = new byte[this.heightOfTree][this.mdLength];
        for (int k = 0; k < this.heightOfTree; ++k) {
            this.AuthPath[k] = array[1 + k];
        }
        this.tailStack = new Vector();
        for (int l = 0; l < n; ++l) {
            this.tailStack.addElement(array[1 + this.heightOfTree + l]);
        }
        this.treehash = GMSSUtils.clone(array3);
        this.retain = GMSSUtils.clone(array4);
    }
    
    public GMSSRootCalc(final int heightOfTree, final int k, final GMSSDigestProvider digestProvider) {
        this.heightOfTree = heightOfTree;
        this.digestProvider = digestProvider;
        this.messDigestTree = digestProvider.get();
        this.mdLength = this.messDigestTree.getDigestSize();
        this.K = k;
        this.index = new int[heightOfTree];
        this.AuthPath = new byte[heightOfTree][this.mdLength];
        this.root = new byte[this.mdLength];
        this.retain = new Vector[this.K - 1];
        for (int i = 0; i < k - 1; ++i) {
            this.retain[i] = new Vector();
        }
    }
    
    public void initialize(final Vector vector) {
        this.treehash = new Treehash[this.heightOfTree - this.K];
        for (int i = 0; i < this.heightOfTree - this.K; ++i) {
            this.treehash[i] = new Treehash(vector, i, this.digestProvider.get());
        }
        this.index = new int[this.heightOfTree];
        this.AuthPath = new byte[this.heightOfTree][this.mdLength];
        this.root = new byte[this.mdLength];
        this.tailStack = new Vector();
        this.heightOfNodes = new Vector();
        this.isInitialized = true;
        this.isFinished = false;
        for (int j = 0; j < this.heightOfTree; ++j) {
            this.index[j] = -1;
        }
        this.retain = new Vector[this.K - 1];
        for (int k = 0; k < this.K - 1; ++k) {
            this.retain[k] = new Vector();
        }
        this.indexForNextSeed = 3;
        this.heightOfNextSeed = 0;
    }
    
    public void update(final byte[] array, final byte[] array2) {
        if (this.heightOfNextSeed < this.heightOfTree - this.K && this.indexForNextSeed - 2 == this.index[0]) {
            this.initializeTreehashSeed(array, this.heightOfNextSeed);
            ++this.heightOfNextSeed;
            this.indexForNextSeed *= 2;
        }
        this.update(array2);
    }
    
    public void update(final byte[] firstNode) {
        if (this.isFinished) {
            System.out.print("Too much updates for Tree!!");
            return;
        }
        if (!this.isInitialized) {
            System.err.println("GMSSRootCalc not initialized!");
            return;
        }
        final int[] index = this.index;
        final int n = 0;
        ++index[n];
        if (this.index[0] == 1) {
            System.arraycopy(firstNode, 0, this.AuthPath[0], 0, this.mdLength);
        }
        else if (this.index[0] == 3 && this.heightOfTree > this.K) {
            this.treehash[0].setFirstNode(firstNode);
        }
        if ((this.index[0] - 3) % 2 == 0 && this.index[0] >= 3 && this.heightOfTree == this.K) {
            this.retain[0].insertElementAt(firstNode, 0);
        }
        if (this.index[0] == 0) {
            this.tailStack.addElement(firstNode);
            this.heightOfNodes.addElement(Integers.valueOf(0));
        }
        else {
            byte[] firstNode2 = new byte[this.mdLength];
            final byte[] array = new byte[this.mdLength << 1];
            System.arraycopy(firstNode, 0, firstNode2, 0, this.mdLength);
            int n2 = 0;
            while (this.tailStack.size() > 0 && n2 == this.heightOfNodes.lastElement()) {
                System.arraycopy(this.tailStack.lastElement(), 0, array, 0, this.mdLength);
                this.tailStack.removeElementAt(this.tailStack.size() - 1);
                this.heightOfNodes.removeElementAt(this.heightOfNodes.size() - 1);
                System.arraycopy(firstNode2, 0, array, this.mdLength, this.mdLength);
                this.messDigestTree.update(array, 0, array.length);
                firstNode2 = new byte[this.messDigestTree.getDigestSize()];
                this.messDigestTree.doFinal(firstNode2, 0);
                if (++n2 < this.heightOfTree) {
                    final int[] index2 = this.index;
                    final int n3 = n2;
                    ++index2[n3];
                    if (this.index[n2] == 1) {
                        System.arraycopy(firstNode2, 0, this.AuthPath[n2], 0, this.mdLength);
                    }
                    if (n2 >= this.heightOfTree - this.K) {
                        if (n2 == 0) {
                            System.out.println("M\ufffd\ufffd\ufffdP");
                        }
                        if ((this.index[n2] - 3) % 2 != 0 || this.index[n2] < 3) {
                            continue;
                        }
                        this.retain[n2 - (this.heightOfTree - this.K)].insertElementAt(firstNode2, 0);
                    }
                    else {
                        if (this.index[n2] != 3) {
                            continue;
                        }
                        this.treehash[n2].setFirstNode(firstNode2);
                    }
                }
            }
            this.tailStack.addElement(firstNode2);
            this.heightOfNodes.addElement(Integers.valueOf(n2));
            if (n2 == this.heightOfTree) {
                this.isFinished = true;
                this.isInitialized = false;
                this.root = this.tailStack.lastElement();
            }
        }
    }
    
    public void initializeTreehashSeed(final byte[] array, final int n) {
        this.treehash[n].initializeSeed(array);
    }
    
    public boolean wasInitialized() {
        return this.isInitialized;
    }
    
    public boolean wasFinished() {
        return this.isFinished;
    }
    
    public byte[][] getAuthPath() {
        return GMSSUtils.clone(this.AuthPath);
    }
    
    public Treehash[] getTreehash() {
        return GMSSUtils.clone(this.treehash);
    }
    
    public Vector[] getRetain() {
        return GMSSUtils.clone(this.retain);
    }
    
    public byte[] getRoot() {
        return Arrays.clone(this.root);
    }
    
    public Vector getStack() {
        final Vector vector = new Vector();
        final Enumeration elements = this.tailStack.elements();
        while (elements.hasMoreElements()) {
            vector.addElement(elements.nextElement());
        }
        return vector;
    }
    
    public byte[][] getStatByte() {
        int size;
        if (this.tailStack == null) {
            size = 0;
        }
        else {
            size = this.tailStack.size();
        }
        final byte[][] array = new byte[1 + this.heightOfTree + size][64];
        array[0] = this.root;
        for (int i = 0; i < this.heightOfTree; ++i) {
            array[1 + i] = this.AuthPath[i];
        }
        for (int j = 0; j < size; ++j) {
            array[1 + this.heightOfTree + j] = (byte[])this.tailStack.elementAt(j);
        }
        return array;
    }
    
    public int[] getStatInt() {
        int size;
        if (this.tailStack == null) {
            size = 0;
        }
        else {
            size = this.tailStack.size();
        }
        final int[] array = new int[8 + this.heightOfTree + size];
        array[0] = this.heightOfTree;
        array[1] = this.mdLength;
        array[2] = this.K;
        array[3] = this.indexForNextSeed;
        array[4] = this.heightOfNextSeed;
        if (this.isFinished) {
            array[5] = 1;
        }
        else {
            array[5] = 0;
        }
        if (this.isInitialized) {
            array[6] = 1;
        }
        else {
            array[6] = 0;
        }
        array[7] = size;
        for (int i = 0; i < this.heightOfTree; ++i) {
            array[8 + i] = this.index[i];
        }
        for (int j = 0; j < size; ++j) {
            array[8 + this.heightOfTree + j] = (int)this.heightOfNodes.elementAt(j);
        }
        return array;
    }
    
    @Override
    public String toString() {
        String s = "";
        int size;
        if (this.tailStack == null) {
            size = 0;
        }
        else {
            size = this.tailStack.size();
        }
        for (int i = 0; i < 8 + this.heightOfTree + size; ++i) {
            s = s + this.getStatInt()[i] + " ";
        }
        for (int j = 0; j < 1 + this.heightOfTree + size; ++j) {
            s = s + new String(Hex.encode(this.getStatByte()[j])) + " ";
        }
        return s + "  " + this.digestProvider.get().getDigestSize();
    }
}
