package com.sun.xml.internal.stream.dtd.nonvalidating;

import java.util.List;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import java.util.HashMap;
import java.util.Map;
import com.sun.org.apache.xerces.internal.xni.QName;
import java.util.ArrayList;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDContentModelSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;

public class DTDGrammar
{
    public static final int TOP_LEVEL_SCOPE = -1;
    private static final int CHUNK_SHIFT = 8;
    private static final int CHUNK_SIZE = 256;
    private static final int CHUNK_MASK = 255;
    private static final int INITIAL_CHUNK_COUNT = 4;
    private static final short LIST_FLAG = 128;
    private static final short LIST_MASK = -129;
    private static final boolean DEBUG = false;
    protected XMLDTDSource fDTDSource;
    protected XMLDTDContentModelSource fDTDContentModelSource;
    protected int fCurrentElementIndex;
    protected int fCurrentAttributeIndex;
    protected boolean fReadingExternalDTD;
    private SymbolTable fSymbolTable;
    private ArrayList notationDecls;
    private int fElementDeclCount;
    private QName[][] fElementDeclName;
    private short[][] fElementDeclType;
    private int[][] fElementDeclFirstAttributeDeclIndex;
    private int[][] fElementDeclLastAttributeDeclIndex;
    private int fAttributeDeclCount;
    private QName[][] fAttributeDeclName;
    private short[][] fAttributeDeclType;
    private String[][][] fAttributeDeclEnumeration;
    private short[][] fAttributeDeclDefaultType;
    private String[][] fAttributeDeclDefaultValue;
    private String[][] fAttributeDeclNonNormalizedDefaultValue;
    private int[][] fAttributeDeclNextAttributeDeclIndex;
    private final Map<String, Integer> fElementIndexMap;
    private final QName fQName;
    protected XMLAttributeDecl fAttributeDecl;
    private XMLElementDecl fElementDecl;
    private XMLSimpleType fSimpleType;
    Map<String, XMLElementDecl> fElementDeclTab;
    
    public DTDGrammar(final SymbolTable symbolTable) {
        this.fDTDSource = null;
        this.fDTDContentModelSource = null;
        this.fReadingExternalDTD = false;
        this.notationDecls = new ArrayList();
        this.fElementDeclCount = 0;
        this.fElementDeclName = new QName[4][];
        this.fElementDeclType = new short[4][];
        this.fElementDeclFirstAttributeDeclIndex = new int[4][];
        this.fElementDeclLastAttributeDeclIndex = new int[4][];
        this.fAttributeDeclCount = 0;
        this.fAttributeDeclName = new QName[4][];
        this.fAttributeDeclType = new short[4][];
        this.fAttributeDeclEnumeration = new String[4][][];
        this.fAttributeDeclDefaultType = new short[4][];
        this.fAttributeDeclDefaultValue = new String[4][];
        this.fAttributeDeclNonNormalizedDefaultValue = new String[4][];
        this.fAttributeDeclNextAttributeDeclIndex = new int[4][];
        this.fElementIndexMap = new HashMap<String, Integer>();
        this.fQName = new QName();
        this.fAttributeDecl = new XMLAttributeDecl();
        this.fElementDecl = new XMLElementDecl();
        this.fSimpleType = new XMLSimpleType();
        this.fElementDeclTab = new HashMap<String, XMLElementDecl>();
        this.fSymbolTable = symbolTable;
    }
    
    public int getAttributeDeclIndex(final int elementDeclIndex, final String attributeDeclName) {
        if (elementDeclIndex == -1) {
            return -1;
        }
        for (int attDefIndex = this.getFirstAttributeDeclIndex(elementDeclIndex); attDefIndex != -1; attDefIndex = this.getNextAttributeDeclIndex(attDefIndex)) {
            this.getAttributeDecl(attDefIndex, this.fAttributeDecl);
            if (this.fAttributeDecl.name.rawname == attributeDeclName || attributeDeclName.equals(this.fAttributeDecl.name.rawname)) {
                return attDefIndex;
            }
        }
        return -1;
    }
    
    public void startDTD(final XMLLocator locator, final Augmentations augs) throws XNIException {
    }
    
    public void elementDecl(final String name, final String contentModel, final Augmentations augs) throws XNIException {
        final XMLElementDecl tmpElementDecl = this.fElementDeclTab.get(name);
        if (tmpElementDecl != null) {
            if (tmpElementDecl.type != -1) {
                return;
            }
            this.fCurrentElementIndex = this.getElementDeclIndex(name);
        }
        else {
            this.fCurrentElementIndex = this.createElementDecl();
        }
        final XMLElementDecl elementDecl = new XMLElementDecl();
        final QName elementName = new QName(null, name, name, null);
        elementDecl.name.setValues(elementName);
        elementDecl.scope = -1;
        if (contentModel.equals("EMPTY")) {
            elementDecl.type = 1;
        }
        else if (contentModel.equals("ANY")) {
            elementDecl.type = 0;
        }
        else if (contentModel.startsWith("(")) {
            if (contentModel.indexOf("#PCDATA") > 0) {
                elementDecl.type = 2;
            }
            else {
                elementDecl.type = 3;
            }
        }
        this.fElementDeclTab.put(name, elementDecl);
        this.fElementDecl = elementDecl;
        this.setElementDecl(this.fCurrentElementIndex, this.fElementDecl);
        final int chunk = this.fCurrentElementIndex >> 8;
        this.ensureElementDeclCapacity(chunk);
    }
    
    public void attributeDecl(final String elementName, final String attributeName, final String type, final String[] enumeration, final String defaultType, final XMLString defaultValue, final XMLString nonNormalizedDefaultValue, final Augmentations augs) throws XNIException {
        if (type != XMLSymbols.fCDATASymbol && defaultValue != null) {
            this.normalizeDefaultAttrValue(defaultValue);
        }
        if (!this.fElementDeclTab.containsKey(elementName)) {
            this.fCurrentElementIndex = this.createElementDecl();
            final XMLElementDecl elementDecl = new XMLElementDecl();
            elementDecl.name.setValues(null, elementName, elementName, null);
            elementDecl.scope = -1;
            this.fElementDeclTab.put(elementName, elementDecl);
            this.setElementDecl(this.fCurrentElementIndex, elementDecl);
        }
        final int elementIndex = this.getElementDeclIndex(elementName);
        if (this.getAttributeDeclIndex(elementIndex, attributeName) != -1) {
            return;
        }
        this.fCurrentAttributeIndex = this.createAttributeDecl();
        this.fSimpleType.clear();
        if (defaultType != null) {
            if (defaultType.equals("#FIXED")) {
                final XMLSimpleType fSimpleType = this.fSimpleType;
                final XMLSimpleType fSimpleType2 = this.fSimpleType;
                fSimpleType.defaultType = 1;
            }
            else if (defaultType.equals("#IMPLIED")) {
                final XMLSimpleType fSimpleType3 = this.fSimpleType;
                final XMLSimpleType fSimpleType4 = this.fSimpleType;
                fSimpleType3.defaultType = 0;
            }
            else if (defaultType.equals("#REQUIRED")) {
                final XMLSimpleType fSimpleType5 = this.fSimpleType;
                final XMLSimpleType fSimpleType6 = this.fSimpleType;
                fSimpleType5.defaultType = 2;
            }
        }
        this.fSimpleType.defaultValue = ((defaultValue != null) ? defaultValue.toString() : null);
        this.fSimpleType.nonNormalizedDefaultValue = ((nonNormalizedDefaultValue != null) ? nonNormalizedDefaultValue.toString() : null);
        this.fSimpleType.enumeration = enumeration;
        if (type.equals("CDATA")) {
            this.fSimpleType.type = 0;
        }
        else if (type.equals("ID")) {
            this.fSimpleType.type = 3;
        }
        else if (type.startsWith("IDREF")) {
            this.fSimpleType.type = 4;
            if (type.indexOf("S") > 0) {
                this.fSimpleType.list = true;
            }
        }
        else if (type.equals("ENTITIES")) {
            this.fSimpleType.type = 1;
            this.fSimpleType.list = true;
        }
        else if (type.equals("ENTITY")) {
            this.fSimpleType.type = 1;
        }
        else if (type.equals("NMTOKENS")) {
            this.fSimpleType.type = 5;
            this.fSimpleType.list = true;
        }
        else if (type.equals("NMTOKEN")) {
            this.fSimpleType.type = 5;
        }
        else if (type.startsWith("NOTATION")) {
            this.fSimpleType.type = 6;
        }
        else if (type.startsWith("ENUMERATION")) {
            this.fSimpleType.type = 2;
        }
        else {
            System.err.println("!!! unknown attribute type " + type);
        }
        this.fQName.setValues(null, attributeName, attributeName, null);
        this.fAttributeDecl.setValues(this.fQName, this.fSimpleType, false);
        this.setAttributeDecl(elementIndex, this.fCurrentAttributeIndex, this.fAttributeDecl);
        final int chunk = this.fCurrentAttributeIndex >> 8;
        this.ensureAttributeDeclCapacity(chunk);
    }
    
    public SymbolTable getSymbolTable() {
        return this.fSymbolTable;
    }
    
    public int getFirstElementDeclIndex() {
        return (this.fElementDeclCount >= 0) ? 0 : -1;
    }
    
    public int getNextElementDeclIndex(final int elementDeclIndex) {
        return (elementDeclIndex < this.fElementDeclCount - 1) ? (elementDeclIndex + 1) : -1;
    }
    
    public int getElementDeclIndex(final String elementDeclName) {
        Integer mapping = this.fElementIndexMap.get(elementDeclName);
        if (mapping == null) {
            mapping = -1;
        }
        return mapping;
    }
    
    public int getElementDeclIndex(final QName elementDeclQName) {
        return this.getElementDeclIndex(elementDeclQName.rawname);
    }
    
    public short getContentSpecType(final int elementIndex) {
        if (elementIndex < 0 || elementIndex >= this.fElementDeclCount) {
            return -1;
        }
        final int chunk = elementIndex >> 8;
        final int index = elementIndex & 0xFF;
        if (this.fElementDeclType[chunk][index] == -1) {
            return -1;
        }
        return (short)(this.fElementDeclType[chunk][index] & 0xFFFFFF7F);
    }
    
    public boolean getElementDecl(final int elementDeclIndex, final XMLElementDecl elementDecl) {
        if (elementDeclIndex < 0 || elementDeclIndex >= this.fElementDeclCount) {
            return false;
        }
        final int chunk = elementDeclIndex >> 8;
        final int index = elementDeclIndex & 0xFF;
        elementDecl.name.setValues(this.fElementDeclName[chunk][index]);
        if (this.fElementDeclType[chunk][index] == -1) {
            elementDecl.type = -1;
            elementDecl.simpleType.list = false;
        }
        else {
            elementDecl.type = (short)(this.fElementDeclType[chunk][index] & 0xFFFFFF7F);
            elementDecl.simpleType.list = ((this.fElementDeclType[chunk][index] & 0x80) != 0x0);
        }
        elementDecl.simpleType.defaultType = -1;
        elementDecl.simpleType.defaultValue = null;
        return true;
    }
    
    public int getFirstAttributeDeclIndex(final int elementDeclIndex) {
        final int chunk = elementDeclIndex >> 8;
        final int index = elementDeclIndex & 0xFF;
        return this.fElementDeclFirstAttributeDeclIndex[chunk][index];
    }
    
    public int getNextAttributeDeclIndex(final int attributeDeclIndex) {
        final int chunk = attributeDeclIndex >> 8;
        final int index = attributeDeclIndex & 0xFF;
        return this.fAttributeDeclNextAttributeDeclIndex[chunk][index];
    }
    
    public boolean getAttributeDecl(final int attributeDeclIndex, final XMLAttributeDecl attributeDecl) {
        if (attributeDeclIndex < 0 || attributeDeclIndex >= this.fAttributeDeclCount) {
            return false;
        }
        final int chunk = attributeDeclIndex >> 8;
        final int index = attributeDeclIndex & 0xFF;
        attributeDecl.name.setValues(this.fAttributeDeclName[chunk][index]);
        short attributeType;
        boolean isList;
        if (this.fAttributeDeclType[chunk][index] == -1) {
            attributeType = -1;
            isList = false;
        }
        else {
            attributeType = (short)(this.fAttributeDeclType[chunk][index] & 0xFFFFFF7F);
            isList = ((this.fAttributeDeclType[chunk][index] & 0x80) != 0x0);
        }
        attributeDecl.simpleType.setValues(attributeType, this.fAttributeDeclName[chunk][index].localpart, this.fAttributeDeclEnumeration[chunk][index], isList, this.fAttributeDeclDefaultType[chunk][index], this.fAttributeDeclDefaultValue[chunk][index], this.fAttributeDeclNonNormalizedDefaultValue[chunk][index]);
        return true;
    }
    
    public boolean isCDATAAttribute(final QName elName, final QName atName) {
        final int elDeclIdx = this.getElementDeclIndex(elName);
        return !this.getAttributeDecl(elDeclIdx, this.fAttributeDecl) || this.fAttributeDecl.simpleType.type == 0;
    }
    
    public void printElements() {
        int elementDeclIndex = 0;
        final XMLElementDecl elementDecl = new XMLElementDecl();
        while (this.getElementDecl(elementDeclIndex++, elementDecl)) {
            System.out.println("element decl: " + elementDecl.name + ", " + elementDecl.name.rawname);
        }
    }
    
    public void printAttributes(final int elementDeclIndex) {
        int attributeDeclIndex = this.getFirstAttributeDeclIndex(elementDeclIndex);
        System.out.print(elementDeclIndex);
        System.out.print(" [");
        while (attributeDeclIndex != -1) {
            System.out.print(' ');
            System.out.print(attributeDeclIndex);
            this.printAttribute(attributeDeclIndex);
            attributeDeclIndex = this.getNextAttributeDeclIndex(attributeDeclIndex);
            if (attributeDeclIndex != -1) {
                System.out.print(",");
            }
        }
        System.out.println(" ]");
    }
    
    protected int createElementDecl() {
        final int chunk = this.fElementDeclCount >> 8;
        final int index = this.fElementDeclCount & 0xFF;
        this.ensureElementDeclCapacity(chunk);
        this.fElementDeclName[chunk][index] = new QName();
        this.fElementDeclType[chunk][index] = -1;
        this.fElementDeclFirstAttributeDeclIndex[chunk][index] = -1;
        this.fElementDeclLastAttributeDeclIndex[chunk][index] = -1;
        return this.fElementDeclCount++;
    }
    
    protected void setElementDecl(final int elementDeclIndex, final XMLElementDecl elementDecl) {
        if (elementDeclIndex < 0 || elementDeclIndex >= this.fElementDeclCount) {
            return;
        }
        final int chunk = elementDeclIndex >> 8;
        final int index = elementDeclIndex & 0xFF;
        final int scope = elementDecl.scope;
        this.fElementDeclName[chunk][index].setValues(elementDecl.name);
        this.fElementDeclType[chunk][index] = elementDecl.type;
        if (elementDecl.simpleType.list) {
            final short[] array = this.fElementDeclType[chunk];
            final int n = index;
            array[n] |= 0x80;
        }
        this.fElementIndexMap.put(elementDecl.name.rawname, elementDeclIndex);
    }
    
    protected void setFirstAttributeDeclIndex(final int elementDeclIndex, final int newFirstAttrIndex) {
        if (elementDeclIndex < 0 || elementDeclIndex >= this.fElementDeclCount) {
            return;
        }
        final int chunk = elementDeclIndex >> 8;
        final int index = elementDeclIndex & 0xFF;
        this.fElementDeclFirstAttributeDeclIndex[chunk][index] = newFirstAttrIndex;
    }
    
    protected int createAttributeDecl() {
        final int chunk = this.fAttributeDeclCount >> 8;
        final int index = this.fAttributeDeclCount & 0xFF;
        this.ensureAttributeDeclCapacity(chunk);
        this.fAttributeDeclName[chunk][index] = new QName();
        this.fAttributeDeclType[chunk][index] = -1;
        this.fAttributeDeclEnumeration[chunk][index] = null;
        this.fAttributeDeclDefaultType[chunk][index] = 0;
        this.fAttributeDeclDefaultValue[chunk][index] = null;
        this.fAttributeDeclNonNormalizedDefaultValue[chunk][index] = null;
        this.fAttributeDeclNextAttributeDeclIndex[chunk][index] = -1;
        return this.fAttributeDeclCount++;
    }
    
    protected void setAttributeDecl(final int elementDeclIndex, final int attributeDeclIndex, final XMLAttributeDecl attributeDecl) {
        int attrChunk = attributeDeclIndex >> 8;
        int attrIndex = attributeDeclIndex & 0xFF;
        this.fAttributeDeclName[attrChunk][attrIndex].setValues(attributeDecl.name);
        this.fAttributeDeclType[attrChunk][attrIndex] = attributeDecl.simpleType.type;
        if (attributeDecl.simpleType.list) {
            final short[] array = this.fAttributeDeclType[attrChunk];
            final int n = attrIndex;
            array[n] |= 0x80;
        }
        this.fAttributeDeclEnumeration[attrChunk][attrIndex] = attributeDecl.simpleType.enumeration;
        this.fAttributeDeclDefaultType[attrChunk][attrIndex] = attributeDecl.simpleType.defaultType;
        this.fAttributeDeclDefaultValue[attrChunk][attrIndex] = attributeDecl.simpleType.defaultValue;
        this.fAttributeDeclNonNormalizedDefaultValue[attrChunk][attrIndex] = attributeDecl.simpleType.nonNormalizedDefaultValue;
        final int elemChunk = elementDeclIndex >> 8;
        final int elemIndex = elementDeclIndex & 0xFF;
        int index;
        for (index = this.fElementDeclFirstAttributeDeclIndex[elemChunk][elemIndex]; index != -1 && index != attributeDeclIndex; index = this.fAttributeDeclNextAttributeDeclIndex[attrChunk][attrIndex]) {
            attrChunk = index >> 8;
            attrIndex = (index & 0xFF);
        }
        if (index == -1) {
            if (this.fElementDeclFirstAttributeDeclIndex[elemChunk][elemIndex] == -1) {
                this.fElementDeclFirstAttributeDeclIndex[elemChunk][elemIndex] = attributeDeclIndex;
            }
            else {
                index = this.fElementDeclLastAttributeDeclIndex[elemChunk][elemIndex];
                attrChunk = index >> 8;
                attrIndex = (index & 0xFF);
                this.fAttributeDeclNextAttributeDeclIndex[attrChunk][attrIndex] = attributeDeclIndex;
            }
            this.fElementDeclLastAttributeDeclIndex[elemChunk][elemIndex] = attributeDeclIndex;
        }
    }
    
    public void notationDecl(final String name, final XMLResourceIdentifier identifier, final Augmentations augs) throws XNIException {
        final XMLNotationDecl notationDecl = new XMLNotationDecl();
        notationDecl.setValues(name, identifier.getPublicId(), identifier.getLiteralSystemId(), identifier.getBaseSystemId());
        this.notationDecls.add(notationDecl);
    }
    
    public List getNotationDecls() {
        return this.notationDecls;
    }
    
    private void printAttribute(final int attributeDeclIndex) {
        final XMLAttributeDecl attributeDecl = new XMLAttributeDecl();
        if (this.getAttributeDecl(attributeDeclIndex, attributeDecl)) {
            System.out.print(" { ");
            System.out.print(attributeDecl.name.localpart);
            System.out.print(" }");
        }
    }
    
    private void ensureElementDeclCapacity(final int chunk) {
        if (chunk >= this.fElementDeclName.length) {
            this.fElementDeclName = resize(this.fElementDeclName, this.fElementDeclName.length * 2);
            this.fElementDeclType = resize(this.fElementDeclType, this.fElementDeclType.length * 2);
            this.fElementDeclFirstAttributeDeclIndex = resize(this.fElementDeclFirstAttributeDeclIndex, this.fElementDeclFirstAttributeDeclIndex.length * 2);
            this.fElementDeclLastAttributeDeclIndex = resize(this.fElementDeclLastAttributeDeclIndex, this.fElementDeclLastAttributeDeclIndex.length * 2);
        }
        else if (this.fElementDeclName[chunk] != null) {
            return;
        }
        this.fElementDeclName[chunk] = new QName[256];
        this.fElementDeclType[chunk] = new short[256];
        this.fElementDeclFirstAttributeDeclIndex[chunk] = new int[256];
        this.fElementDeclLastAttributeDeclIndex[chunk] = new int[256];
    }
    
    private void ensureAttributeDeclCapacity(final int chunk) {
        if (chunk >= this.fAttributeDeclName.length) {
            this.fAttributeDeclName = resize(this.fAttributeDeclName, this.fAttributeDeclName.length * 2);
            this.fAttributeDeclType = resize(this.fAttributeDeclType, this.fAttributeDeclType.length * 2);
            this.fAttributeDeclEnumeration = resize(this.fAttributeDeclEnumeration, this.fAttributeDeclEnumeration.length * 2);
            this.fAttributeDeclDefaultType = resize(this.fAttributeDeclDefaultType, this.fAttributeDeclDefaultType.length * 2);
            this.fAttributeDeclDefaultValue = resize(this.fAttributeDeclDefaultValue, this.fAttributeDeclDefaultValue.length * 2);
            this.fAttributeDeclNonNormalizedDefaultValue = resize(this.fAttributeDeclNonNormalizedDefaultValue, this.fAttributeDeclNonNormalizedDefaultValue.length * 2);
            this.fAttributeDeclNextAttributeDeclIndex = resize(this.fAttributeDeclNextAttributeDeclIndex, this.fAttributeDeclNextAttributeDeclIndex.length * 2);
        }
        else if (this.fAttributeDeclName[chunk] != null) {
            return;
        }
        this.fAttributeDeclName[chunk] = new QName[256];
        this.fAttributeDeclType[chunk] = new short[256];
        this.fAttributeDeclEnumeration[chunk] = new String[256][];
        this.fAttributeDeclDefaultType[chunk] = new short[256];
        this.fAttributeDeclDefaultValue[chunk] = new String[256];
        this.fAttributeDeclNonNormalizedDefaultValue[chunk] = new String[256];
        this.fAttributeDeclNextAttributeDeclIndex[chunk] = new int[256];
    }
    
    private static short[][] resize(final short[][] array, final int newsize) {
        final short[][] newarray = new short[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }
    
    private static int[][] resize(final int[][] array, final int newsize) {
        final int[][] newarray = new int[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }
    
    private static QName[][] resize(final QName[][] array, final int newsize) {
        final QName[][] newarray = new QName[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }
    
    private static String[][] resize(final String[][] array, final int newsize) {
        final String[][] newarray = new String[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }
    
    private static String[][][] resize(final String[][][] array, final int newsize) {
        final String[][][] newarray = new String[newsize][][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }
    
    private boolean normalizeDefaultAttrValue(final XMLString value) {
        final int oldLength = value.length;
        boolean skipSpace = true;
        int current = value.offset;
        final int end = value.offset + value.length;
        for (int i = value.offset; i < end; ++i) {
            if (value.ch[i] == ' ') {
                if (!skipSpace) {
                    value.ch[current++] = ' ';
                    skipSpace = true;
                }
            }
            else {
                if (current != i) {
                    value.ch[current] = value.ch[i];
                }
                ++current;
                skipSpace = false;
            }
        }
        if (current != end) {
            if (skipSpace) {
                --current;
            }
            value.length = current - value.offset;
            return true;
        }
        return false;
    }
    
    public void endDTD(final Augmentations augs) throws XNIException {
    }
}
