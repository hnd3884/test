package com.sun.xml.internal.stream.buffer;

public class AbstractCreator extends AbstractCreatorProcessor
{
    protected MutableXMLStreamBuffer _buffer;
    
    public void setXMLStreamBuffer(final MutableXMLStreamBuffer buffer) {
        if (buffer == null) {
            throw new NullPointerException("buffer cannot be null");
        }
        this.setBuffer(buffer);
    }
    
    public MutableXMLStreamBuffer getXMLStreamBuffer() {
        return this._buffer;
    }
    
    protected final void createBuffer() {
        this.setBuffer(new MutableXMLStreamBuffer());
    }
    
    protected final void increaseTreeCount() {
        final MutableXMLStreamBuffer buffer = this._buffer;
        ++buffer.treeCount;
    }
    
    protected final void setBuffer(final MutableXMLStreamBuffer buffer) {
        this._buffer = buffer;
        this._currentStructureFragment = this._buffer.getStructure();
        this._structure = this._currentStructureFragment.getArray();
        this._structurePtr = 0;
        this._currentStructureStringFragment = this._buffer.getStructureStrings();
        this._structureStrings = this._currentStructureStringFragment.getArray();
        this._structureStringsPtr = 0;
        this._currentContentCharactersBufferFragment = this._buffer.getContentCharactersBuffer();
        this._contentCharactersBuffer = this._currentContentCharactersBufferFragment.getArray();
        this._contentCharactersBufferPtr = 0;
        this._currentContentObjectFragment = this._buffer.getContentObjects();
        this._contentObjects = this._currentContentObjectFragment.getArray();
        this._contentObjectsPtr = 0;
    }
    
    protected final void setHasInternedStrings(final boolean hasInternedStrings) {
        this._buffer.setHasInternedStrings(hasInternedStrings);
    }
    
    protected final void storeStructure(final int b) {
        this._structure[this._structurePtr++] = (byte)b;
        if (this._structurePtr == this._structure.length) {
            this.resizeStructure();
        }
    }
    
    protected final void resizeStructure() {
        this._structurePtr = 0;
        if (this._currentStructureFragment.getNext() != null) {
            this._currentStructureFragment = this._currentStructureFragment.getNext();
            this._structure = this._currentStructureFragment.getArray();
        }
        else {
            this._structure = new byte[this._structure.length];
            this._currentStructureFragment = new FragmentedArray<byte[]>(this._structure, this._currentStructureFragment);
        }
    }
    
    protected final void storeStructureString(final String s) {
        this._structureStrings[this._structureStringsPtr++] = s;
        if (this._structureStringsPtr == this._structureStrings.length) {
            this.resizeStructureStrings();
        }
    }
    
    protected final void resizeStructureStrings() {
        this._structureStringsPtr = 0;
        if (this._currentStructureStringFragment.getNext() != null) {
            this._currentStructureStringFragment = this._currentStructureStringFragment.getNext();
            this._structureStrings = this._currentStructureStringFragment.getArray();
        }
        else {
            this._structureStrings = new String[this._structureStrings.length];
            this._currentStructureStringFragment = new FragmentedArray<String[]>(this._structureStrings, this._currentStructureStringFragment);
        }
    }
    
    protected final void storeContentString(final String s) {
        this.storeContentObject(s);
    }
    
    protected final void storeContentCharacters(final int type, final char[] ch, final int start, final int length) {
        if (this._contentCharactersBufferPtr + length >= this._contentCharactersBuffer.length) {
            if (length >= 512) {
                this.storeStructure(type | 0x4);
                this.storeContentCharactersCopy(ch, start, length);
                return;
            }
            this.resizeContentCharacters();
        }
        if (length < 256) {
            this.storeStructure(type);
            this.storeStructure(length);
            System.arraycopy(ch, start, this._contentCharactersBuffer, this._contentCharactersBufferPtr, length);
            this._contentCharactersBufferPtr += length;
        }
        else if (length < 65536) {
            this.storeStructure(type | 0x1);
            this.storeStructure(length >> 8);
            this.storeStructure(length & 0xFF);
            System.arraycopy(ch, start, this._contentCharactersBuffer, this._contentCharactersBufferPtr, length);
            this._contentCharactersBufferPtr += length;
        }
        else {
            this.storeStructure(type | 0x4);
            this.storeContentCharactersCopy(ch, start, length);
        }
    }
    
    protected final void resizeContentCharacters() {
        this._contentCharactersBufferPtr = 0;
        if (this._currentContentCharactersBufferFragment.getNext() != null) {
            this._currentContentCharactersBufferFragment = this._currentContentCharactersBufferFragment.getNext();
            this._contentCharactersBuffer = this._currentContentCharactersBufferFragment.getArray();
        }
        else {
            this._contentCharactersBuffer = new char[this._contentCharactersBuffer.length];
            this._currentContentCharactersBufferFragment = new FragmentedArray<char[]>(this._contentCharactersBuffer, this._currentContentCharactersBufferFragment);
        }
    }
    
    protected final void storeContentCharactersCopy(final char[] ch, final int start, final int length) {
        final char[] copyOfCh = new char[length];
        System.arraycopy(ch, start, copyOfCh, 0, length);
        this.storeContentObject(copyOfCh);
    }
    
    protected final Object peekAtContentObject() {
        return this._contentObjects[this._contentObjectsPtr];
    }
    
    protected final void storeContentObject(final Object s) {
        this._contentObjects[this._contentObjectsPtr++] = s;
        if (this._contentObjectsPtr == this._contentObjects.length) {
            this.resizeContentObjects();
        }
    }
    
    protected final void resizeContentObjects() {
        this._contentObjectsPtr = 0;
        if (this._currentContentObjectFragment.getNext() != null) {
            this._currentContentObjectFragment = this._currentContentObjectFragment.getNext();
            this._contentObjects = this._currentContentObjectFragment.getArray();
        }
        else {
            this._contentObjects = new Object[this._contentObjects.length];
            this._currentContentObjectFragment = new FragmentedArray<Object[]>(this._contentObjects, this._currentContentObjectFragment);
        }
    }
}
