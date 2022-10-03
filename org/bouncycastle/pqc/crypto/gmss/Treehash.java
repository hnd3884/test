package org.bouncycastle.pqc.crypto.gmss;

import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.pqc.crypto.gmss.util.GMSSRandom;
import org.bouncycastle.util.Integers;
import org.bouncycastle.crypto.Digest;
import java.util.Vector;

public class Treehash
{
    private int maxHeight;
    private Vector tailStack;
    private Vector heightOfNodes;
    private byte[] firstNode;
    private byte[] seedActive;
    private byte[] seedNext;
    private int tailLength;
    private int firstNodeHeight;
    private boolean isInitialized;
    private boolean isFinished;
    private boolean seedInitialized;
    private Digest messDigestTree;
    
    public Treehash(final Digest messDigestTree, final byte[][] array, final int[] array2) {
        this.messDigestTree = messDigestTree;
        this.maxHeight = array2[0];
        this.tailLength = array2[1];
        this.firstNodeHeight = array2[2];
        if (array2[3] == 1) {
            this.isFinished = true;
        }
        else {
            this.isFinished = false;
        }
        if (array2[4] == 1) {
            this.isInitialized = true;
        }
        else {
            this.isInitialized = false;
        }
        if (array2[5] == 1) {
            this.seedInitialized = true;
        }
        else {
            this.seedInitialized = false;
        }
        this.heightOfNodes = new Vector();
        for (int i = 0; i < this.tailLength; ++i) {
            this.heightOfNodes.addElement(Integers.valueOf(array2[6 + i]));
        }
        this.firstNode = array[0];
        this.seedActive = array[1];
        this.seedNext = array[2];
        this.tailStack = new Vector();
        for (int j = 0; j < this.tailLength; ++j) {
            this.tailStack.addElement(array[3 + j]);
        }
    }
    
    public Treehash(final Vector tailStack, final int maxHeight, final Digest messDigestTree) {
        this.tailStack = tailStack;
        this.maxHeight = maxHeight;
        this.firstNode = null;
        this.isInitialized = false;
        this.isFinished = false;
        this.seedInitialized = false;
        this.messDigestTree = messDigestTree;
        this.seedNext = new byte[this.messDigestTree.getDigestSize()];
        this.seedActive = new byte[this.messDigestTree.getDigestSize()];
    }
    
    public void initializeSeed(final byte[] array) {
        System.arraycopy(array, 0, this.seedNext, 0, this.messDigestTree.getDigestSize());
        this.seedInitialized = true;
    }
    
    public void initialize() {
        if (!this.seedInitialized) {
            System.err.println("Seed " + this.maxHeight + " not initialized");
            return;
        }
        this.heightOfNodes = new Vector();
        this.tailLength = 0;
        this.firstNode = null;
        this.firstNodeHeight = -1;
        this.isInitialized = true;
        System.arraycopy(this.seedNext, 0, this.seedActive, 0, this.messDigestTree.getDigestSize());
    }
    
    public void update(final GMSSRandom gmssRandom, final byte[] firstNode) {
        if (this.isFinished) {
            System.err.println("No more update possible for treehash instance!");
            return;
        }
        if (!this.isInitialized) {
            System.err.println("Treehash instance not initialized before update");
            return;
        }
        final byte[] array = new byte[this.messDigestTree.getDigestSize()];
        gmssRandom.nextSeed(this.seedActive);
        if (this.firstNode == null) {
            this.firstNode = firstNode;
            this.firstNodeHeight = 0;
        }
        else {
            byte[] array2 = firstNode;
            int n;
            for (n = 0; this.tailLength > 0 && n == this.heightOfNodes.lastElement(); ++n, --this.tailLength) {
                final byte[] array3 = new byte[this.messDigestTree.getDigestSize() << 1];
                System.arraycopy(this.tailStack.lastElement(), 0, array3, 0, this.messDigestTree.getDigestSize());
                this.tailStack.removeElementAt(this.tailStack.size() - 1);
                this.heightOfNodes.removeElementAt(this.heightOfNodes.size() - 1);
                System.arraycopy(array2, 0, array3, this.messDigestTree.getDigestSize(), this.messDigestTree.getDigestSize());
                this.messDigestTree.update(array3, 0, array3.length);
                array2 = new byte[this.messDigestTree.getDigestSize()];
                this.messDigestTree.doFinal(array2, 0);
            }
            this.tailStack.addElement(array2);
            this.heightOfNodes.addElement(Integers.valueOf(n));
            ++this.tailLength;
            if (this.heightOfNodes.lastElement() == this.firstNodeHeight) {
                final byte[] array4 = new byte[this.messDigestTree.getDigestSize() << 1];
                System.arraycopy(this.firstNode, 0, array4, 0, this.messDigestTree.getDigestSize());
                System.arraycopy(this.tailStack.lastElement(), 0, array4, this.messDigestTree.getDigestSize(), this.messDigestTree.getDigestSize());
                this.tailStack.removeElementAt(this.tailStack.size() - 1);
                this.heightOfNodes.removeElementAt(this.heightOfNodes.size() - 1);
                this.messDigestTree.update(array4, 0, array4.length);
                this.firstNode = new byte[this.messDigestTree.getDigestSize()];
                this.messDigestTree.doFinal(this.firstNode, 0);
                ++this.firstNodeHeight;
                this.tailLength = 0;
            }
        }
        if (this.firstNodeHeight == this.maxHeight) {
            this.isFinished = true;
        }
    }
    
    public void destroy() {
        this.isInitialized = false;
        this.isFinished = false;
        this.firstNode = null;
        this.tailLength = 0;
        this.firstNodeHeight = -1;
    }
    
    public int getLowestNodeHeight() {
        if (this.firstNode == null) {
            return this.maxHeight;
        }
        if (this.tailLength == 0) {
            return this.firstNodeHeight;
        }
        return Math.min(this.firstNodeHeight, this.heightOfNodes.lastElement());
    }
    
    public int getFirstNodeHeight() {
        if (this.firstNode == null) {
            return this.maxHeight;
        }
        return this.firstNodeHeight;
    }
    
    public boolean wasInitialized() {
        return this.isInitialized;
    }
    
    public boolean wasFinished() {
        return this.isFinished;
    }
    
    public byte[] getFirstNode() {
        return this.firstNode;
    }
    
    public byte[] getSeedActive() {
        return this.seedActive;
    }
    
    public void setFirstNode(final byte[] firstNode) {
        if (!this.isInitialized) {
            this.initialize();
        }
        this.firstNode = firstNode;
        this.firstNodeHeight = this.maxHeight;
        this.isFinished = true;
    }
    
    public void updateNextSeed(final GMSSRandom gmssRandom) {
        gmssRandom.nextSeed(this.seedNext);
    }
    
    public Vector getTailStack() {
        return this.tailStack;
    }
    
    public byte[][] getStatByte() {
        final byte[][] array = new byte[3 + this.tailLength][this.messDigestTree.getDigestSize()];
        array[0] = this.firstNode;
        array[1] = this.seedActive;
        array[2] = this.seedNext;
        for (int i = 0; i < this.tailLength; ++i) {
            array[3 + i] = (byte[])this.tailStack.elementAt(i);
        }
        return array;
    }
    
    public int[] getStatInt() {
        final int[] array = new int[6 + this.tailLength];
        array[0] = this.maxHeight;
        array[1] = this.tailLength;
        array[2] = this.firstNodeHeight;
        if (this.isFinished) {
            array[3] = 1;
        }
        else {
            array[3] = 0;
        }
        if (this.isInitialized) {
            array[4] = 1;
        }
        else {
            array[4] = 0;
        }
        if (this.seedInitialized) {
            array[5] = 1;
        }
        else {
            array[5] = 0;
        }
        for (int i = 0; i < this.tailLength; ++i) {
            array[6 + i] = (int)this.heightOfNodes.elementAt(i);
        }
        return array;
    }
    
    @Override
    public String toString() {
        String s = "Treehash    : ";
        for (int i = 0; i < 6 + this.tailLength; ++i) {
            s = s + this.getStatInt()[i] + " ";
        }
        for (int j = 0; j < 3 + this.tailLength; ++j) {
            if (this.getStatByte()[j] != null) {
                s = s + new String(Hex.encode(this.getStatByte()[j])) + " ";
            }
            else {
                s += "null ";
            }
        }
        return s + "  " + this.messDigestTree.getDigestSize();
    }
}
