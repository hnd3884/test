package org.apache.xerces.impl.dtd;

import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.util.SymbolTable;

final class BalancedDTDGrammar extends DTDGrammar
{
    private boolean fMixed;
    private int fDepth;
    private short[] fOpStack;
    private int[][] fGroupIndexStack;
    private int[] fGroupIndexStackSizes;
    
    public BalancedDTDGrammar(final SymbolTable symbolTable, final XMLDTDDescription xmldtdDescription) {
        super(symbolTable, xmldtdDescription);
        this.fDepth = 0;
        this.fOpStack = null;
    }
    
    public final void startContentModel(final String s, final Augmentations augmentations) throws XNIException {
        this.fDepth = 0;
        this.initializeContentModelStacks();
        super.startContentModel(s, augmentations);
    }
    
    public final void startGroup(final Augmentations augmentations) throws XNIException {
        ++this.fDepth;
        this.initializeContentModelStacks();
        this.fMixed = false;
    }
    
    public final void pcdata(final Augmentations augmentations) throws XNIException {
        this.fMixed = true;
    }
    
    public final void element(final String s, final Augmentations augmentations) throws XNIException {
        this.addToCurrentGroup(this.addUniqueLeafNode(s));
    }
    
    public final void separator(final short n, final Augmentations augmentations) throws XNIException {
        if (n == 0) {
            this.fOpStack[this.fDepth] = 4;
        }
        else if (n == 1) {
            this.fOpStack[this.fDepth] = 5;
        }
    }
    
    public final void occurrence(final short n, final Augmentations augmentations) throws XNIException {
        if (!this.fMixed) {
            final int n2 = this.fGroupIndexStackSizes[this.fDepth] - 1;
            if (n == 2) {
                this.fGroupIndexStack[this.fDepth][n2] = this.addContentSpecNode((short)1, this.fGroupIndexStack[this.fDepth][n2], -1);
            }
            else if (n == 3) {
                this.fGroupIndexStack[this.fDepth][n2] = this.addContentSpecNode((short)2, this.fGroupIndexStack[this.fDepth][n2], -1);
            }
            else if (n == 4) {
                this.fGroupIndexStack[this.fDepth][n2] = this.addContentSpecNode((short)3, this.fGroupIndexStack[this.fDepth][n2], -1);
            }
        }
    }
    
    public final void endGroup(final Augmentations augmentations) throws XNIException {
        final int n = this.fGroupIndexStackSizes[this.fDepth];
        final int n2 = (n > 0) ? this.addContentSpecNodes(0, n - 1) : this.addUniqueLeafNode(null);
        --this.fDepth;
        this.addToCurrentGroup(n2);
    }
    
    public final void endDTD(final Augmentations augmentations) throws XNIException {
        super.endDTD(augmentations);
        this.fOpStack = null;
        this.fGroupIndexStack = null;
        this.fGroupIndexStackSizes = null;
    }
    
    protected final void addContentSpecToElement(final XMLElementDecl xmlElementDecl) {
        this.setContentSpecIndex(this.fCurrentElementIndex, (this.fGroupIndexStackSizes[0] > 0) ? this.fGroupIndexStack[0][0] : -1);
    }
    
    private int addContentSpecNodes(final int n, final int n2) {
        if (n == n2) {
            return this.fGroupIndexStack[this.fDepth][n];
        }
        final int n3 = n + n2 >>> 1;
        return this.addContentSpecNode(this.fOpStack[this.fDepth], this.addContentSpecNodes(n, n3), this.addContentSpecNodes(n3 + 1, n2));
    }
    
    private void initializeContentModelStacks() {
        if (this.fOpStack == null) {
            this.fOpStack = new short[8];
            this.fGroupIndexStack = new int[8][];
            this.fGroupIndexStackSizes = new int[8];
        }
        else if (this.fDepth == this.fOpStack.length) {
            final short[] fOpStack = new short[this.fDepth * 2];
            System.arraycopy(this.fOpStack, 0, fOpStack, 0, this.fDepth);
            this.fOpStack = fOpStack;
            final int[][] fGroupIndexStack = new int[this.fDepth * 2][];
            System.arraycopy(this.fGroupIndexStack, 0, fGroupIndexStack, 0, this.fDepth);
            this.fGroupIndexStack = fGroupIndexStack;
            final int[] fGroupIndexStackSizes = new int[this.fDepth * 2];
            System.arraycopy(this.fGroupIndexStackSizes, 0, fGroupIndexStackSizes, 0, this.fDepth);
            this.fGroupIndexStackSizes = fGroupIndexStackSizes;
        }
        this.fOpStack[this.fDepth] = -1;
        this.fGroupIndexStackSizes[this.fDepth] = 0;
    }
    
    private void addToCurrentGroup(final int n) {
        int[] array = this.fGroupIndexStack[this.fDepth];
        final int n2 = this.fGroupIndexStackSizes[this.fDepth]++;
        if (array == null) {
            array = new int[8];
            this.fGroupIndexStack[this.fDepth] = array;
        }
        else if (n2 == array.length) {
            final int[] array2 = new int[array.length * 2];
            System.arraycopy(array, 0, array2, 0, array.length);
            array = array2;
            this.fGroupIndexStack[this.fDepth] = array;
        }
        array[n2] = n;
    }
}
