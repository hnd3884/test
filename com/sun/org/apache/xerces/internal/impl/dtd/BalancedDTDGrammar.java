package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.util.SymbolTable;

final class BalancedDTDGrammar extends DTDGrammar
{
    private boolean fMixed;
    private int fDepth;
    private short[] fOpStack;
    private int[][] fGroupIndexStack;
    private int[] fGroupIndexStackSizes;
    
    public BalancedDTDGrammar(final SymbolTable symbolTable, final XMLDTDDescription desc) {
        super(symbolTable, desc);
        this.fDepth = 0;
        this.fOpStack = null;
    }
    
    @Override
    public final void startContentModel(final String elementName, final Augmentations augs) throws XNIException {
        this.fDepth = 0;
        this.initializeContentModelStacks();
        super.startContentModel(elementName, augs);
    }
    
    @Override
    public final void startGroup(final Augmentations augs) throws XNIException {
        ++this.fDepth;
        this.initializeContentModelStacks();
        this.fMixed = false;
    }
    
    @Override
    public final void pcdata(final Augmentations augs) throws XNIException {
        this.fMixed = true;
    }
    
    @Override
    public final void element(final String elementName, final Augmentations augs) throws XNIException {
        this.addToCurrentGroup(this.addUniqueLeafNode(elementName));
    }
    
    @Override
    public final void separator(final short separator, final Augmentations augs) throws XNIException {
        if (separator == 0) {
            this.fOpStack[this.fDepth] = 4;
        }
        else if (separator == 1) {
            this.fOpStack[this.fDepth] = 5;
        }
    }
    
    @Override
    public final void occurrence(final short occurrence, final Augmentations augs) throws XNIException {
        if (!this.fMixed) {
            final int currentIndex = this.fGroupIndexStackSizes[this.fDepth] - 1;
            if (occurrence == 2) {
                this.fGroupIndexStack[this.fDepth][currentIndex] = this.addContentSpecNode((short)1, this.fGroupIndexStack[this.fDepth][currentIndex], -1);
            }
            else if (occurrence == 3) {
                this.fGroupIndexStack[this.fDepth][currentIndex] = this.addContentSpecNode((short)2, this.fGroupIndexStack[this.fDepth][currentIndex], -1);
            }
            else if (occurrence == 4) {
                this.fGroupIndexStack[this.fDepth][currentIndex] = this.addContentSpecNode((short)3, this.fGroupIndexStack[this.fDepth][currentIndex], -1);
            }
        }
    }
    
    @Override
    public final void endGroup(final Augmentations augs) throws XNIException {
        final int length = this.fGroupIndexStackSizes[this.fDepth];
        final int group = (length > 0) ? this.addContentSpecNodes(0, length - 1) : this.addUniqueLeafNode(null);
        --this.fDepth;
        this.addToCurrentGroup(group);
    }
    
    @Override
    public final void endDTD(final Augmentations augs) throws XNIException {
        super.endDTD(augs);
        this.fOpStack = null;
        this.fGroupIndexStack = null;
        this.fGroupIndexStackSizes = null;
    }
    
    @Override
    protected final void addContentSpecToElement(final XMLElementDecl elementDecl) {
        final int contentSpec = (this.fGroupIndexStackSizes[0] > 0) ? this.fGroupIndexStack[0][0] : -1;
        this.setContentSpecIndex(this.fCurrentElementIndex, contentSpec);
    }
    
    private int addContentSpecNodes(final int begin, final int end) {
        if (begin == end) {
            return this.fGroupIndexStack[this.fDepth][begin];
        }
        final int middle = begin + end >>> 1;
        return this.addContentSpecNode(this.fOpStack[this.fDepth], this.addContentSpecNodes(begin, middle), this.addContentSpecNodes(middle + 1, end));
    }
    
    private void initializeContentModelStacks() {
        if (this.fOpStack == null) {
            this.fOpStack = new short[8];
            this.fGroupIndexStack = new int[8][];
            this.fGroupIndexStackSizes = new int[8];
        }
        else if (this.fDepth == this.fOpStack.length) {
            final short[] newOpStack = new short[this.fDepth * 2];
            System.arraycopy(this.fOpStack, 0, newOpStack, 0, this.fDepth);
            this.fOpStack = newOpStack;
            final int[][] newGroupIndexStack = new int[this.fDepth * 2][];
            System.arraycopy(this.fGroupIndexStack, 0, newGroupIndexStack, 0, this.fDepth);
            this.fGroupIndexStack = newGroupIndexStack;
            final int[] newGroupIndexStackLengths = new int[this.fDepth * 2];
            System.arraycopy(this.fGroupIndexStackSizes, 0, newGroupIndexStackLengths, 0, this.fDepth);
            this.fGroupIndexStackSizes = newGroupIndexStackLengths;
        }
        this.fOpStack[this.fDepth] = -1;
        this.fGroupIndexStackSizes[this.fDepth] = 0;
    }
    
    private void addToCurrentGroup(final int contentSpec) {
        int[] currentGroup = this.fGroupIndexStack[this.fDepth];
        final int length = this.fGroupIndexStackSizes[this.fDepth]++;
        if (currentGroup == null) {
            currentGroup = new int[8];
            this.fGroupIndexStack[this.fDepth] = currentGroup;
        }
        else if (length == currentGroup.length) {
            final int[] newGroup = new int[currentGroup.length * 2];
            System.arraycopy(currentGroup, 0, newGroup, 0, currentGroup.length);
            currentGroup = newGroup;
            this.fGroupIndexStack[this.fDepth] = currentGroup;
        }
        currentGroup[length] = contentSpec;
    }
}
