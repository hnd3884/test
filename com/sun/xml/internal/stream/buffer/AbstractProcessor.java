package com.sun.xml.internal.stream.buffer;

public abstract class AbstractProcessor extends AbstractCreatorProcessor
{
    protected static final int STATE_ILLEGAL = 0;
    protected static final int STATE_DOCUMENT = 1;
    protected static final int STATE_DOCUMENT_FRAGMENT = 2;
    protected static final int STATE_ELEMENT_U_LN_QN = 3;
    protected static final int STATE_ELEMENT_P_U_LN = 4;
    protected static final int STATE_ELEMENT_U_LN = 5;
    protected static final int STATE_ELEMENT_LN = 6;
    protected static final int STATE_TEXT_AS_CHAR_ARRAY_SMALL = 7;
    protected static final int STATE_TEXT_AS_CHAR_ARRAY_MEDIUM = 8;
    protected static final int STATE_TEXT_AS_CHAR_ARRAY_COPY = 9;
    protected static final int STATE_TEXT_AS_STRING = 10;
    protected static final int STATE_TEXT_AS_OBJECT = 11;
    protected static final int STATE_COMMENT_AS_CHAR_ARRAY_SMALL = 12;
    protected static final int STATE_COMMENT_AS_CHAR_ARRAY_MEDIUM = 13;
    protected static final int STATE_COMMENT_AS_CHAR_ARRAY_COPY = 14;
    protected static final int STATE_COMMENT_AS_STRING = 15;
    protected static final int STATE_PROCESSING_INSTRUCTION = 16;
    protected static final int STATE_END = 17;
    private static final int[] _eiiStateTable;
    protected static final int STATE_NAMESPACE_ATTRIBUTE = 1;
    protected static final int STATE_NAMESPACE_ATTRIBUTE_P = 2;
    protected static final int STATE_NAMESPACE_ATTRIBUTE_P_U = 3;
    protected static final int STATE_NAMESPACE_ATTRIBUTE_U = 4;
    private static final int[] _niiStateTable;
    protected static final int STATE_ATTRIBUTE_U_LN_QN = 1;
    protected static final int STATE_ATTRIBUTE_P_U_LN = 2;
    protected static final int STATE_ATTRIBUTE_U_LN = 3;
    protected static final int STATE_ATTRIBUTE_LN = 4;
    protected static final int STATE_ATTRIBUTE_U_LN_QN_OBJECT = 5;
    protected static final int STATE_ATTRIBUTE_P_U_LN_OBJECT = 6;
    protected static final int STATE_ATTRIBUTE_U_LN_OBJECT = 7;
    protected static final int STATE_ATTRIBUTE_LN_OBJECT = 8;
    private static final int[] _aiiStateTable;
    protected XMLStreamBuffer _buffer;
    protected boolean _fragmentMode;
    protected boolean _stringInterningFeature;
    protected int _treeCount;
    protected final StringBuilder _qNameBuffer;
    
    public AbstractProcessor() {
        this._stringInterningFeature = false;
        this._qNameBuffer = new StringBuilder();
    }
    
    @Deprecated
    protected final void setBuffer(final XMLStreamBuffer buffer) {
        this.setBuffer(buffer, buffer.isFragment());
    }
    
    protected final void setBuffer(final XMLStreamBuffer buffer, final boolean fragmentMode) {
        this._buffer = buffer;
        this._fragmentMode = fragmentMode;
        this._currentStructureFragment = this._buffer.getStructure();
        this._structure = this._currentStructureFragment.getArray();
        this._structurePtr = this._buffer.getStructurePtr();
        this._currentStructureStringFragment = this._buffer.getStructureStrings();
        this._structureStrings = this._currentStructureStringFragment.getArray();
        this._structureStringsPtr = this._buffer.getStructureStringsPtr();
        this._currentContentCharactersBufferFragment = this._buffer.getContentCharactersBuffer();
        this._contentCharactersBuffer = this._currentContentCharactersBufferFragment.getArray();
        this._contentCharactersBufferPtr = this._buffer.getContentCharactersBufferPtr();
        this._currentContentObjectFragment = this._buffer.getContentObjects();
        this._contentObjects = this._currentContentObjectFragment.getArray();
        this._contentObjectsPtr = this._buffer.getContentObjectsPtr();
        this._stringInterningFeature = this._buffer.hasInternedStrings();
        this._treeCount = this._buffer.treeCount;
    }
    
    protected final int peekStructure() {
        if (this._structurePtr < this._structure.length) {
            return this._structure[this._structurePtr] & 0xFF;
        }
        return this.readFromNextStructure(0);
    }
    
    protected final int readStructure() {
        if (this._structurePtr < this._structure.length) {
            return this._structure[this._structurePtr++] & 0xFF;
        }
        return this.readFromNextStructure(1);
    }
    
    protected final int readEiiState() {
        return AbstractProcessor._eiiStateTable[this.readStructure()];
    }
    
    protected static int getEIIState(final int item) {
        return AbstractProcessor._eiiStateTable[item];
    }
    
    protected static int getNIIState(final int item) {
        return AbstractProcessor._niiStateTable[item];
    }
    
    protected static int getAIIState(final int item) {
        return AbstractProcessor._aiiStateTable[item];
    }
    
    protected final int readStructure16() {
        return this.readStructure() << 8 | this.readStructure();
    }
    
    private int readFromNextStructure(final int v) {
        this._structurePtr = v;
        this._currentStructureFragment = this._currentStructureFragment.getNext();
        this._structure = this._currentStructureFragment.getArray();
        return this._structure[0] & 0xFF;
    }
    
    protected final String readStructureString() {
        if (this._structureStringsPtr < this._structureStrings.length) {
            return this._structureStrings[this._structureStringsPtr++];
        }
        this._structureStringsPtr = 1;
        this._currentStructureStringFragment = this._currentStructureStringFragment.getNext();
        this._structureStrings = this._currentStructureStringFragment.getArray();
        return this._structureStrings[0];
    }
    
    protected final String readContentString() {
        return (String)this.readContentObject();
    }
    
    protected final char[] readContentCharactersCopy() {
        return (char[])this.readContentObject();
    }
    
    protected final int readContentCharactersBuffer(final int length) {
        if (this._contentCharactersBufferPtr + length < this._contentCharactersBuffer.length) {
            final int start = this._contentCharactersBufferPtr;
            this._contentCharactersBufferPtr += length;
            return start;
        }
        this._contentCharactersBufferPtr = length;
        this._currentContentCharactersBufferFragment = this._currentContentCharactersBufferFragment.getNext();
        this._contentCharactersBuffer = this._currentContentCharactersBufferFragment.getArray();
        return 0;
    }
    
    protected final Object readContentObject() {
        if (this._contentObjectsPtr < this._contentObjects.length) {
            return this._contentObjects[this._contentObjectsPtr++];
        }
        this._contentObjectsPtr = 1;
        this._currentContentObjectFragment = this._currentContentObjectFragment.getNext();
        this._contentObjects = this._currentContentObjectFragment.getArray();
        return this._contentObjects[0];
    }
    
    protected final String getQName(final String prefix, final String localName) {
        this._qNameBuffer.append(prefix).append(':').append(localName);
        final String qName = this._qNameBuffer.toString();
        this._qNameBuffer.setLength(0);
        return this._stringInterningFeature ? qName.intern() : qName;
    }
    
    protected final String getPrefixFromQName(final String qName) {
        final int pIndex = qName.indexOf(58);
        if (this._stringInterningFeature) {
            return (pIndex != -1) ? qName.substring(0, pIndex).intern() : "";
        }
        return (pIndex != -1) ? qName.substring(0, pIndex) : "";
    }
    
    static {
        _eiiStateTable = new int[256];
        _niiStateTable = new int[256];
        _aiiStateTable = new int[256];
        AbstractProcessor._eiiStateTable[16] = 1;
        AbstractProcessor._eiiStateTable[17] = 2;
        AbstractProcessor._eiiStateTable[38] = 3;
        AbstractProcessor._eiiStateTable[35] = 4;
        AbstractProcessor._eiiStateTable[34] = 5;
        AbstractProcessor._eiiStateTable[32] = 6;
        AbstractProcessor._eiiStateTable[80] = 7;
        AbstractProcessor._eiiStateTable[81] = 8;
        AbstractProcessor._eiiStateTable[84] = 9;
        AbstractProcessor._eiiStateTable[88] = 10;
        AbstractProcessor._eiiStateTable[92] = 11;
        AbstractProcessor._eiiStateTable[96] = 12;
        AbstractProcessor._eiiStateTable[97] = 13;
        AbstractProcessor._eiiStateTable[100] = 14;
        AbstractProcessor._eiiStateTable[104] = 15;
        AbstractProcessor._eiiStateTable[112] = 16;
        AbstractProcessor._eiiStateTable[144] = 17;
        AbstractProcessor._niiStateTable[64] = 1;
        AbstractProcessor._niiStateTable[65] = 2;
        AbstractProcessor._niiStateTable[67] = 3;
        AbstractProcessor._niiStateTable[66] = 4;
        AbstractProcessor._aiiStateTable[54] = 1;
        AbstractProcessor._aiiStateTable[51] = 2;
        AbstractProcessor._aiiStateTable[50] = 3;
        AbstractProcessor._aiiStateTable[48] = 4;
        AbstractProcessor._aiiStateTable[62] = 5;
        AbstractProcessor._aiiStateTable[59] = 6;
        AbstractProcessor._aiiStateTable[58] = 7;
        AbstractProcessor._aiiStateTable[56] = 8;
    }
}
