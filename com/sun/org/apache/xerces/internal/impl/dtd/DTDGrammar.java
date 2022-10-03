package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMUniOp;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMBinOp;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMLeaf;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMAny;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.impl.dtd.models.DFAContentModel;
import com.sun.org.apache.xerces.internal.impl.dtd.models.SimpleContentModel;
import com.sun.org.apache.xerces.internal.impl.dtd.models.MixedContentModel;
import java.util.ArrayList;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import java.util.HashMap;
import java.util.Map;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import com.sun.org.apache.xerces.internal.impl.dtd.models.ContentModelValidator;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDContentModelSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.impl.validation.EntityState;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;

public class DTDGrammar implements XMLDTDHandler, XMLDTDContentModelHandler, EntityState, Grammar
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
    protected XMLDTDDescription fGrammarDescription;
    private int fElementDeclCount;
    private QName[][] fElementDeclName;
    private short[][] fElementDeclType;
    private int[][] fElementDeclContentSpecIndex;
    private ContentModelValidator[][] fElementDeclContentModelValidator;
    private int[][] fElementDeclFirstAttributeDeclIndex;
    private int[][] fElementDeclLastAttributeDeclIndex;
    private int fAttributeDeclCount;
    private QName[][] fAttributeDeclName;
    private boolean fIsImmutable;
    private short[][] fAttributeDeclType;
    private String[][][] fAttributeDeclEnumeration;
    private short[][] fAttributeDeclDefaultType;
    private DatatypeValidator[][] fAttributeDeclDatatypeValidator;
    private String[][] fAttributeDeclDefaultValue;
    private String[][] fAttributeDeclNonNormalizedDefaultValue;
    private int[][] fAttributeDeclNextAttributeDeclIndex;
    private int fContentSpecCount;
    private short[][] fContentSpecType;
    private Object[][] fContentSpecValue;
    private Object[][] fContentSpecOtherValue;
    private int fEntityCount;
    private String[][] fEntityName;
    private String[][] fEntityValue;
    private String[][] fEntityPublicId;
    private String[][] fEntitySystemId;
    private String[][] fEntityBaseSystemId;
    private String[][] fEntityNotation;
    private byte[][] fEntityIsPE;
    private byte[][] fEntityInExternal;
    private int fNotationCount;
    private String[][] fNotationName;
    private String[][] fNotationPublicId;
    private String[][] fNotationSystemId;
    private String[][] fNotationBaseSystemId;
    private final Map<String, Integer> fElementIndexMap;
    private final Map<String, Integer> fEntityIndexMap;
    private final Map<String, Integer> fNotationIndexMap;
    private boolean fMixed;
    private final QName fQName;
    private final QName fQName2;
    protected final XMLAttributeDecl fAttributeDecl;
    private int fLeafCount;
    private int fEpsilonIndex;
    private XMLElementDecl fElementDecl;
    private XMLEntityDecl fEntityDecl;
    private XMLSimpleType fSimpleType;
    private XMLContentSpec fContentSpec;
    Map<String, XMLElementDecl> fElementDeclTab;
    private short[] fOpStack;
    private int[] fNodeIndexStack;
    private int[] fPrevNodeIndexStack;
    private int fDepth;
    private boolean[] fPEntityStack;
    private int fPEDepth;
    private int[][] fElementDeclIsExternal;
    private int[][] fAttributeDeclIsExternal;
    int valueIndex;
    int prevNodeIndex;
    int nodeIndex;
    
    public DTDGrammar(final SymbolTable symbolTable, final XMLDTDDescription desc) {
        this.fDTDSource = null;
        this.fDTDContentModelSource = null;
        this.fReadingExternalDTD = false;
        this.fGrammarDescription = null;
        this.fElementDeclCount = 0;
        this.fElementDeclName = new QName[4][];
        this.fElementDeclType = new short[4][];
        this.fElementDeclContentSpecIndex = new int[4][];
        this.fElementDeclContentModelValidator = new ContentModelValidator[4][];
        this.fElementDeclFirstAttributeDeclIndex = new int[4][];
        this.fElementDeclLastAttributeDeclIndex = new int[4][];
        this.fAttributeDeclCount = 0;
        this.fAttributeDeclName = new QName[4][];
        this.fIsImmutable = false;
        this.fAttributeDeclType = new short[4][];
        this.fAttributeDeclEnumeration = new String[4][][];
        this.fAttributeDeclDefaultType = new short[4][];
        this.fAttributeDeclDatatypeValidator = new DatatypeValidator[4][];
        this.fAttributeDeclDefaultValue = new String[4][];
        this.fAttributeDeclNonNormalizedDefaultValue = new String[4][];
        this.fAttributeDeclNextAttributeDeclIndex = new int[4][];
        this.fContentSpecCount = 0;
        this.fContentSpecType = new short[4][];
        this.fContentSpecValue = new Object[4][];
        this.fContentSpecOtherValue = new Object[4][];
        this.fEntityCount = 0;
        this.fEntityName = new String[4][];
        this.fEntityValue = new String[4][];
        this.fEntityPublicId = new String[4][];
        this.fEntitySystemId = new String[4][];
        this.fEntityBaseSystemId = new String[4][];
        this.fEntityNotation = new String[4][];
        this.fEntityIsPE = new byte[4][];
        this.fEntityInExternal = new byte[4][];
        this.fNotationCount = 0;
        this.fNotationName = new String[4][];
        this.fNotationPublicId = new String[4][];
        this.fNotationSystemId = new String[4][];
        this.fNotationBaseSystemId = new String[4][];
        this.fElementIndexMap = new HashMap<String, Integer>();
        this.fEntityIndexMap = new HashMap<String, Integer>();
        this.fNotationIndexMap = new HashMap<String, Integer>();
        this.fQName = new QName();
        this.fQName2 = new QName();
        this.fAttributeDecl = new XMLAttributeDecl();
        this.fLeafCount = 0;
        this.fEpsilonIndex = -1;
        this.fElementDecl = new XMLElementDecl();
        this.fEntityDecl = new XMLEntityDecl();
        this.fSimpleType = new XMLSimpleType();
        this.fContentSpec = new XMLContentSpec();
        this.fElementDeclTab = new HashMap<String, XMLElementDecl>();
        this.fOpStack = null;
        this.fNodeIndexStack = null;
        this.fPrevNodeIndexStack = null;
        this.fDepth = 0;
        this.fPEntityStack = new boolean[4];
        this.fPEDepth = 0;
        this.fElementDeclIsExternal = new int[4][];
        this.fAttributeDeclIsExternal = new int[4][];
        this.valueIndex = -1;
        this.prevNodeIndex = -1;
        this.nodeIndex = -1;
        this.fSymbolTable = symbolTable;
        this.fGrammarDescription = desc;
    }
    
    @Override
    public XMLGrammarDescription getGrammarDescription() {
        return this.fGrammarDescription;
    }
    
    public boolean getElementDeclIsExternal(final int elementDeclIndex) {
        if (elementDeclIndex < 0) {
            return false;
        }
        final int chunk = elementDeclIndex >> 8;
        final int index = elementDeclIndex & 0xFF;
        return this.fElementDeclIsExternal[chunk][index] != 0;
    }
    
    public boolean getAttributeDeclIsExternal(final int attributeDeclIndex) {
        if (attributeDeclIndex < 0) {
            return false;
        }
        final int chunk = attributeDeclIndex >> 8;
        final int index = attributeDeclIndex & 0xFF;
        return this.fAttributeDeclIsExternal[chunk][index] != 0;
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
    
    @Override
    public void startDTD(final XMLLocator locator, final Augmentations augs) throws XNIException {
        this.fOpStack = null;
        this.fNodeIndexStack = null;
        this.fPrevNodeIndexStack = null;
    }
    
    @Override
    public void startParameterEntity(final String name, final XMLResourceIdentifier identifier, final String encoding, final Augmentations augs) throws XNIException {
        if (this.fPEDepth == this.fPEntityStack.length) {
            final boolean[] entityarray = new boolean[this.fPEntityStack.length * 2];
            System.arraycopy(this.fPEntityStack, 0, entityarray, 0, this.fPEntityStack.length);
            this.fPEntityStack = entityarray;
        }
        this.fPEntityStack[this.fPEDepth] = this.fReadingExternalDTD;
        ++this.fPEDepth;
    }
    
    @Override
    public void startExternalSubset(final XMLResourceIdentifier identifier, final Augmentations augs) throws XNIException {
        this.fReadingExternalDTD = true;
    }
    
    @Override
    public void endParameterEntity(final String name, final Augmentations augs) throws XNIException {
        if (this.fPEDepth > 0) {
            --this.fPEDepth;
            this.fReadingExternalDTD = this.fPEntityStack[this.fPEDepth];
        }
    }
    
    @Override
    public void endExternalSubset(final Augmentations augs) throws XNIException {
        this.fReadingExternalDTD = false;
    }
    
    @Override
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
        this.fQName.setValues(null, name, name, null);
        elementDecl.name.setValues(this.fQName);
        elementDecl.contentModelValidator = null;
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
        this.addContentSpecToElement(this.fElementDecl = elementDecl);
        this.setElementDecl(this.fCurrentElementIndex, this.fElementDecl);
        final int chunk = this.fCurrentElementIndex >> 8;
        final int index = this.fCurrentElementIndex & 0xFF;
        this.ensureElementDeclCapacity(chunk);
        this.fElementDeclIsExternal[chunk][index] = ((this.fReadingExternalDTD || this.fPEDepth > 0) ? 1 : 0);
    }
    
    @Override
    public void attributeDecl(final String elementName, final String attributeName, final String type, final String[] enumeration, final String defaultType, final XMLString defaultValue, final XMLString nonNormalizedDefaultValue, final Augmentations augs) throws XNIException {
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
                this.fSimpleType.defaultType = 1;
            }
            else if (defaultType.equals("#IMPLIED")) {
                this.fSimpleType.defaultType = 0;
            }
            else if (defaultType.equals("#REQUIRED")) {
                this.fSimpleType.defaultType = 2;
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
        final int index = this.fCurrentAttributeIndex & 0xFF;
        this.ensureAttributeDeclCapacity(chunk);
        this.fAttributeDeclIsExternal[chunk][index] = ((this.fReadingExternalDTD || this.fPEDepth > 0) ? 1 : 0);
    }
    
    @Override
    public void internalEntityDecl(final String name, final XMLString text, final XMLString nonNormalizedText, final Augmentations augs) throws XNIException {
        int entityIndex = this.getEntityDeclIndex(name);
        if (entityIndex == -1) {
            entityIndex = this.createEntityDecl();
            final boolean isPE = name.startsWith("%");
            final boolean inExternal = this.fReadingExternalDTD || this.fPEDepth > 0;
            final XMLEntityDecl entityDecl = new XMLEntityDecl();
            entityDecl.setValues(name, null, null, null, null, text.toString(), isPE, inExternal);
            this.setEntityDecl(entityIndex, entityDecl);
        }
    }
    
    @Override
    public void externalEntityDecl(final String name, final XMLResourceIdentifier identifier, final Augmentations augs) throws XNIException {
        int entityIndex = this.getEntityDeclIndex(name);
        if (entityIndex == -1) {
            entityIndex = this.createEntityDecl();
            final boolean isPE = name.startsWith("%");
            final boolean inExternal = this.fReadingExternalDTD || this.fPEDepth > 0;
            final XMLEntityDecl entityDecl = new XMLEntityDecl();
            entityDecl.setValues(name, identifier.getPublicId(), identifier.getLiteralSystemId(), identifier.getBaseSystemId(), null, null, isPE, inExternal);
            this.setEntityDecl(entityIndex, entityDecl);
        }
    }
    
    @Override
    public void unparsedEntityDecl(final String name, final XMLResourceIdentifier identifier, final String notation, final Augmentations augs) throws XNIException {
        final XMLEntityDecl entityDecl = new XMLEntityDecl();
        final boolean isPE = name.startsWith("%");
        final boolean inExternal = this.fReadingExternalDTD || this.fPEDepth > 0;
        entityDecl.setValues(name, identifier.getPublicId(), identifier.getLiteralSystemId(), identifier.getBaseSystemId(), notation, null, isPE, inExternal);
        int entityIndex = this.getEntityDeclIndex(name);
        if (entityIndex == -1) {
            entityIndex = this.createEntityDecl();
            this.setEntityDecl(entityIndex, entityDecl);
        }
    }
    
    @Override
    public void notationDecl(final String name, final XMLResourceIdentifier identifier, final Augmentations augs) throws XNIException {
        final XMLNotationDecl notationDecl = new XMLNotationDecl();
        notationDecl.setValues(name, identifier.getPublicId(), identifier.getLiteralSystemId(), identifier.getBaseSystemId());
        int notationIndex = this.getNotationDeclIndex(name);
        if (notationIndex == -1) {
            notationIndex = this.createNotationDecl();
            this.setNotationDecl(notationIndex, notationDecl);
        }
    }
    
    @Override
    public void endDTD(final Augmentations augs) throws XNIException {
        this.fIsImmutable = true;
        if (this.fGrammarDescription.getRootName() == null) {
            int index = 0;
            String currName = null;
            final int size = this.fElementDeclCount;
            final ArrayList elements = new ArrayList(size);
            for (int i = 0; i < size; ++i) {
                final int chunk = i >> 8;
                index = (i & 0xFF);
                currName = this.fElementDeclName[chunk][index].rawname;
                elements.add(currName);
            }
            this.fGrammarDescription.setPossibleRoots(elements);
        }
    }
    
    @Override
    public void setDTDSource(final XMLDTDSource source) {
        this.fDTDSource = source;
    }
    
    @Override
    public XMLDTDSource getDTDSource() {
        return this.fDTDSource;
    }
    
    @Override
    public void textDecl(final String version, final String encoding, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void comment(final XMLString text, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void processingInstruction(final String target, final XMLString data, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void startAttlist(final String elementName, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void endAttlist(final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void startConditional(final short type, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void ignoredCharacters(final XMLString text, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void endConditional(final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void setDTDContentModelSource(final XMLDTDContentModelSource source) {
        this.fDTDContentModelSource = source;
    }
    
    @Override
    public XMLDTDContentModelSource getDTDContentModelSource() {
        return this.fDTDContentModelSource;
    }
    
    @Override
    public void startContentModel(final String elementName, final Augmentations augs) throws XNIException {
        final XMLElementDecl elementDecl = this.fElementDeclTab.get(elementName);
        if (elementDecl != null) {
            this.fElementDecl = elementDecl;
        }
        this.fDepth = 0;
        this.initializeContentModelStack();
    }
    
    @Override
    public void startGroup(final Augmentations augs) throws XNIException {
        ++this.fDepth;
        this.initializeContentModelStack();
        this.fMixed = false;
    }
    
    @Override
    public void pcdata(final Augmentations augs) throws XNIException {
        this.fMixed = true;
    }
    
    @Override
    public void element(final String elementName, final Augmentations augs) throws XNIException {
        if (this.fMixed) {
            if (this.fNodeIndexStack[this.fDepth] == -1) {
                this.fNodeIndexStack[this.fDepth] = this.addUniqueLeafNode(elementName);
            }
            else {
                this.fNodeIndexStack[this.fDepth] = this.addContentSpecNode((short)4, this.fNodeIndexStack[this.fDepth], this.addUniqueLeafNode(elementName));
            }
        }
        else {
            this.fNodeIndexStack[this.fDepth] = this.addContentSpecNode((short)0, elementName);
        }
    }
    
    @Override
    public void separator(final short separator, final Augmentations augs) throws XNIException {
        if (!this.fMixed) {
            if (this.fOpStack[this.fDepth] != 5 && separator == 0) {
                if (this.fPrevNodeIndexStack[this.fDepth] != -1) {
                    this.fNodeIndexStack[this.fDepth] = this.addContentSpecNode(this.fOpStack[this.fDepth], this.fPrevNodeIndexStack[this.fDepth], this.fNodeIndexStack[this.fDepth]);
                }
                this.fPrevNodeIndexStack[this.fDepth] = this.fNodeIndexStack[this.fDepth];
                this.fOpStack[this.fDepth] = 4;
            }
            else if (this.fOpStack[this.fDepth] != 4 && separator == 1) {
                if (this.fPrevNodeIndexStack[this.fDepth] != -1) {
                    this.fNodeIndexStack[this.fDepth] = this.addContentSpecNode(this.fOpStack[this.fDepth], this.fPrevNodeIndexStack[this.fDepth], this.fNodeIndexStack[this.fDepth]);
                }
                this.fPrevNodeIndexStack[this.fDepth] = this.fNodeIndexStack[this.fDepth];
                this.fOpStack[this.fDepth] = 5;
            }
        }
    }
    
    @Override
    public void occurrence(final short occurrence, final Augmentations augs) throws XNIException {
        if (!this.fMixed) {
            if (occurrence == 2) {
                this.fNodeIndexStack[this.fDepth] = this.addContentSpecNode((short)1, this.fNodeIndexStack[this.fDepth], -1);
            }
            else if (occurrence == 3) {
                this.fNodeIndexStack[this.fDepth] = this.addContentSpecNode((short)2, this.fNodeIndexStack[this.fDepth], -1);
            }
            else if (occurrence == 4) {
                this.fNodeIndexStack[this.fDepth] = this.addContentSpecNode((short)3, this.fNodeIndexStack[this.fDepth], -1);
            }
        }
    }
    
    @Override
    public void endGroup(final Augmentations augs) throws XNIException {
        if (!this.fMixed) {
            if (this.fPrevNodeIndexStack[this.fDepth] != -1) {
                this.fNodeIndexStack[this.fDepth] = this.addContentSpecNode(this.fOpStack[this.fDepth], this.fPrevNodeIndexStack[this.fDepth], this.fNodeIndexStack[this.fDepth]);
            }
            final int nodeIndex = this.fNodeIndexStack[this.fDepth--];
            this.fNodeIndexStack[this.fDepth] = nodeIndex;
        }
    }
    
    @Override
    public void any(final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void empty(final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void endContentModel(final Augmentations augs) throws XNIException {
    }
    
    public boolean isNamespaceAware() {
        return false;
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
        if (elementDecl.type == 3 || elementDecl.type == 2) {
            elementDecl.contentModelValidator = this.getElementContentModelValidator(elementDeclIndex);
        }
        elementDecl.simpleType.datatypeValidator = null;
        elementDecl.simpleType.defaultType = -1;
        elementDecl.simpleType.defaultValue = null;
        return true;
    }
    
    QName getElementDeclName(final int elementDeclIndex) {
        if (elementDeclIndex < 0 || elementDeclIndex >= this.fElementDeclCount) {
            return null;
        }
        final int chunk = elementDeclIndex >> 8;
        final int index = elementDeclIndex & 0xFF;
        return this.fElementDeclName[chunk][index];
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
        attributeDecl.simpleType.setValues(attributeType, this.fAttributeDeclName[chunk][index].localpart, this.fAttributeDeclEnumeration[chunk][index], isList, this.fAttributeDeclDefaultType[chunk][index], this.fAttributeDeclDefaultValue[chunk][index], this.fAttributeDeclNonNormalizedDefaultValue[chunk][index], this.fAttributeDeclDatatypeValidator[chunk][index]);
        return true;
    }
    
    public boolean isCDATAAttribute(final QName elName, final QName atName) {
        final int elDeclIdx = this.getElementDeclIndex(elName);
        return !this.getAttributeDecl(elDeclIdx, this.fAttributeDecl) || this.fAttributeDecl.simpleType.type == 0;
    }
    
    public int getEntityDeclIndex(final String entityDeclName) {
        if (entityDeclName == null || this.fEntityIndexMap.get(entityDeclName) == null) {
            return -1;
        }
        return this.fEntityIndexMap.get(entityDeclName);
    }
    
    public boolean getEntityDecl(final int entityDeclIndex, final XMLEntityDecl entityDecl) {
        if (entityDeclIndex < 0 || entityDeclIndex >= this.fEntityCount) {
            return false;
        }
        final int chunk = entityDeclIndex >> 8;
        final int index = entityDeclIndex & 0xFF;
        entityDecl.setValues(this.fEntityName[chunk][index], this.fEntityPublicId[chunk][index], this.fEntitySystemId[chunk][index], this.fEntityBaseSystemId[chunk][index], this.fEntityNotation[chunk][index], this.fEntityValue[chunk][index], this.fEntityIsPE[chunk][index] != 0, this.fEntityInExternal[chunk][index] != 0);
        return true;
    }
    
    public int getNotationDeclIndex(final String notationDeclName) {
        if (notationDeclName == null || this.fNotationIndexMap.get(notationDeclName) == null) {
            return -1;
        }
        return this.fNotationIndexMap.get(notationDeclName);
    }
    
    public boolean getNotationDecl(final int notationDeclIndex, final XMLNotationDecl notationDecl) {
        if (notationDeclIndex < 0 || notationDeclIndex >= this.fNotationCount) {
            return false;
        }
        final int chunk = notationDeclIndex >> 8;
        final int index = notationDeclIndex & 0xFF;
        notationDecl.setValues(this.fNotationName[chunk][index], this.fNotationPublicId[chunk][index], this.fNotationSystemId[chunk][index], this.fNotationBaseSystemId[chunk][index]);
        return true;
    }
    
    public boolean getContentSpec(final int contentSpecIndex, final XMLContentSpec contentSpec) {
        if (contentSpecIndex < 0 || contentSpecIndex >= this.fContentSpecCount) {
            return false;
        }
        final int chunk = contentSpecIndex >> 8;
        final int index = contentSpecIndex & 0xFF;
        contentSpec.type = this.fContentSpecType[chunk][index];
        contentSpec.value = this.fContentSpecValue[chunk][index];
        contentSpec.otherValue = this.fContentSpecOtherValue[chunk][index];
        return true;
    }
    
    public int getContentSpecIndex(final int elementDeclIndex) {
        if (elementDeclIndex < 0 || elementDeclIndex >= this.fElementDeclCount) {
            return -1;
        }
        final int chunk = elementDeclIndex >> 8;
        final int index = elementDeclIndex & 0xFF;
        return this.fElementDeclContentSpecIndex[chunk][index];
    }
    
    public String getContentSpecAsString(final int elementDeclIndex) {
        if (elementDeclIndex < 0 || elementDeclIndex >= this.fElementDeclCount) {
            return null;
        }
        final int chunk = elementDeclIndex >> 8;
        final int index = elementDeclIndex & 0xFF;
        final int contentSpecIndex = this.fElementDeclContentSpecIndex[chunk][index];
        final XMLContentSpec contentSpec = new XMLContentSpec();
        if (this.getContentSpec(contentSpecIndex, contentSpec)) {
            final StringBuffer str = new StringBuffer();
            final int parentContentSpecType = contentSpec.type & 0xF;
            switch (parentContentSpecType) {
                case 0: {
                    str.append('(');
                    if (contentSpec.value == null && contentSpec.otherValue == null) {
                        str.append("#PCDATA");
                    }
                    else {
                        str.append(contentSpec.value);
                    }
                    str.append(')');
                    break;
                }
                case 1: {
                    this.getContentSpec(((int[])contentSpec.value)[0], contentSpec);
                    final int nextContentSpec = contentSpec.type;
                    if (nextContentSpec == 0) {
                        str.append('(');
                        str.append(contentSpec.value);
                        str.append(')');
                    }
                    else if (nextContentSpec == 3 || nextContentSpec == 2 || nextContentSpec == 1) {
                        str.append('(');
                        this.appendContentSpec(contentSpec, str, true, parentContentSpecType);
                        str.append(')');
                    }
                    else {
                        this.appendContentSpec(contentSpec, str, true, parentContentSpecType);
                    }
                    str.append('?');
                    break;
                }
                case 2: {
                    this.getContentSpec(((int[])contentSpec.value)[0], contentSpec);
                    final int nextContentSpec = contentSpec.type;
                    if (nextContentSpec == 0) {
                        str.append('(');
                        if (contentSpec.value == null && contentSpec.otherValue == null) {
                            str.append("#PCDATA");
                        }
                        else if (contentSpec.otherValue != null) {
                            str.append("##any:uri=").append(contentSpec.otherValue);
                        }
                        else if (contentSpec.value == null) {
                            str.append("##any");
                        }
                        else {
                            this.appendContentSpec(contentSpec, str, true, parentContentSpecType);
                        }
                        str.append(')');
                    }
                    else if (nextContentSpec == 3 || nextContentSpec == 2 || nextContentSpec == 1) {
                        str.append('(');
                        this.appendContentSpec(contentSpec, str, true, parentContentSpecType);
                        str.append(')');
                    }
                    else {
                        this.appendContentSpec(contentSpec, str, true, parentContentSpecType);
                    }
                    str.append('*');
                    break;
                }
                case 3: {
                    this.getContentSpec(((int[])contentSpec.value)[0], contentSpec);
                    final int nextContentSpec = contentSpec.type;
                    if (nextContentSpec == 0) {
                        str.append('(');
                        if (contentSpec.value == null && contentSpec.otherValue == null) {
                            str.append("#PCDATA");
                        }
                        else if (contentSpec.otherValue != null) {
                            str.append("##any:uri=").append(contentSpec.otherValue);
                        }
                        else if (contentSpec.value == null) {
                            str.append("##any");
                        }
                        else {
                            str.append(contentSpec.value);
                        }
                        str.append(')');
                    }
                    else if (nextContentSpec == 3 || nextContentSpec == 2 || nextContentSpec == 1) {
                        str.append('(');
                        this.appendContentSpec(contentSpec, str, true, parentContentSpecType);
                        str.append(')');
                    }
                    else {
                        this.appendContentSpec(contentSpec, str, true, parentContentSpecType);
                    }
                    str.append('+');
                    break;
                }
                case 4:
                case 5: {
                    this.appendContentSpec(contentSpec, str, true, parentContentSpecType);
                    break;
                }
                case 6: {
                    str.append("##any");
                    if (contentSpec.otherValue != null) {
                        str.append(":uri=");
                        str.append(contentSpec.otherValue);
                        break;
                    }
                    break;
                }
                case 7: {
                    str.append("##other:uri=");
                    str.append(contentSpec.otherValue);
                    break;
                }
                case 8: {
                    str.append("##local");
                    break;
                }
                default: {
                    str.append("???");
                    break;
                }
            }
            return str.toString();
        }
        return null;
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
    
    protected void addContentSpecToElement(final XMLElementDecl elementDecl) {
        if ((this.fDepth == 0 || (this.fDepth == 1 && elementDecl.type == 2)) && this.fNodeIndexStack != null) {
            if (elementDecl.type == 2) {
                final int pcdata = this.addUniqueLeafNode(null);
                if (this.fNodeIndexStack[0] == -1) {
                    this.fNodeIndexStack[0] = pcdata;
                }
                else {
                    this.fNodeIndexStack[0] = this.addContentSpecNode((short)4, pcdata, this.fNodeIndexStack[0]);
                }
            }
            this.setContentSpecIndex(this.fCurrentElementIndex, this.fNodeIndexStack[this.fDepth]);
        }
    }
    
    protected ContentModelValidator getElementContentModelValidator(final int elementDeclIndex) {
        final int chunk = elementDeclIndex >> 8;
        final int index = elementDeclIndex & 0xFF;
        ContentModelValidator contentModel = this.fElementDeclContentModelValidator[chunk][index];
        if (contentModel != null) {
            return contentModel;
        }
        final int contentType = this.fElementDeclType[chunk][index];
        if (contentType == 4) {
            return null;
        }
        final int contentSpecIndex = this.fElementDeclContentSpecIndex[chunk][index];
        final XMLContentSpec contentSpec = new XMLContentSpec();
        this.getContentSpec(contentSpecIndex, contentSpec);
        if (contentType == 2) {
            final ChildrenList children = new ChildrenList();
            this.contentSpecTree(contentSpecIndex, contentSpec, children);
            contentModel = new MixedContentModel(children.qname, children.type, 0, children.length, false);
        }
        else {
            if (contentType != 3) {
                throw new RuntimeException("Unknown content type for a element decl in getElementContentModelValidator() in AbstractDTDGrammar class");
            }
            contentModel = this.createChildModel(contentSpecIndex);
        }
        return this.fElementDeclContentModelValidator[chunk][index] = contentModel;
    }
    
    protected int createElementDecl() {
        final int chunk = this.fElementDeclCount >> 8;
        final int index = this.fElementDeclCount & 0xFF;
        this.ensureElementDeclCapacity(chunk);
        this.fElementDeclName[chunk][index] = new QName();
        this.fElementDeclType[chunk][index] = -1;
        this.fElementDeclContentModelValidator[chunk][index] = null;
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
        this.fElementDeclName[chunk][index].setValues(elementDecl.name);
        this.fElementDeclType[chunk][index] = elementDecl.type;
        this.fElementDeclContentModelValidator[chunk][index] = elementDecl.contentModelValidator;
        if (elementDecl.simpleType.list) {
            final short[] array = this.fElementDeclType[chunk];
            final int n = index;
            array[n] |= 0x80;
        }
        this.fElementIndexMap.put(elementDecl.name.rawname, elementDeclIndex);
    }
    
    protected void putElementNameMapping(final QName name, final int scope, final int elementDeclIndex) {
    }
    
    protected void setFirstAttributeDeclIndex(final int elementDeclIndex, final int newFirstAttrIndex) {
        if (elementDeclIndex < 0 || elementDeclIndex >= this.fElementDeclCount) {
            return;
        }
        final int chunk = elementDeclIndex >> 8;
        final int index = elementDeclIndex & 0xFF;
        this.fElementDeclFirstAttributeDeclIndex[chunk][index] = newFirstAttrIndex;
    }
    
    protected void setContentSpecIndex(final int elementDeclIndex, final int contentSpecIndex) {
        if (elementDeclIndex < 0 || elementDeclIndex >= this.fElementDeclCount) {
            return;
        }
        final int chunk = elementDeclIndex >> 8;
        final int index = elementDeclIndex & 0xFF;
        this.fElementDeclContentSpecIndex[chunk][index] = contentSpecIndex;
    }
    
    protected int createAttributeDecl() {
        final int chunk = this.fAttributeDeclCount >> 8;
        final int index = this.fAttributeDeclCount & 0xFF;
        this.ensureAttributeDeclCapacity(chunk);
        this.fAttributeDeclName[chunk][index] = new QName();
        this.fAttributeDeclType[chunk][index] = -1;
        this.fAttributeDeclDatatypeValidator[chunk][index] = null;
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
        this.fAttributeDeclDatatypeValidator[attrChunk][attrIndex] = attributeDecl.simpleType.datatypeValidator;
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
    
    protected int createContentSpec() {
        final int chunk = this.fContentSpecCount >> 8;
        final int index = this.fContentSpecCount & 0xFF;
        this.ensureContentSpecCapacity(chunk);
        this.fContentSpecType[chunk][index] = -1;
        this.fContentSpecValue[chunk][index] = null;
        this.fContentSpecOtherValue[chunk][index] = null;
        return this.fContentSpecCount++;
    }
    
    protected void setContentSpec(final int contentSpecIndex, final XMLContentSpec contentSpec) {
        final int chunk = contentSpecIndex >> 8;
        final int index = contentSpecIndex & 0xFF;
        this.fContentSpecType[chunk][index] = contentSpec.type;
        this.fContentSpecValue[chunk][index] = contentSpec.value;
        this.fContentSpecOtherValue[chunk][index] = contentSpec.otherValue;
    }
    
    protected int createEntityDecl() {
        final int chunk = this.fEntityCount >> 8;
        final int index = this.fEntityCount & 0xFF;
        this.ensureEntityDeclCapacity(chunk);
        this.fEntityIsPE[chunk][index] = 0;
        this.fEntityInExternal[chunk][index] = 0;
        return this.fEntityCount++;
    }
    
    protected void setEntityDecl(final int entityDeclIndex, final XMLEntityDecl entityDecl) {
        final int chunk = entityDeclIndex >> 8;
        final int index = entityDeclIndex & 0xFF;
        this.fEntityName[chunk][index] = entityDecl.name;
        this.fEntityValue[chunk][index] = entityDecl.value;
        this.fEntityPublicId[chunk][index] = entityDecl.publicId;
        this.fEntitySystemId[chunk][index] = entityDecl.systemId;
        this.fEntityBaseSystemId[chunk][index] = entityDecl.baseSystemId;
        this.fEntityNotation[chunk][index] = entityDecl.notation;
        this.fEntityIsPE[chunk][index] = (byte)(entityDecl.isPE ? 1 : 0);
        this.fEntityInExternal[chunk][index] = (byte)(entityDecl.inExternal ? 1 : 0);
        this.fEntityIndexMap.put(entityDecl.name, entityDeclIndex);
    }
    
    protected int createNotationDecl() {
        final int chunk = this.fNotationCount >> 8;
        this.ensureNotationDeclCapacity(chunk);
        return this.fNotationCount++;
    }
    
    protected void setNotationDecl(final int notationDeclIndex, final XMLNotationDecl notationDecl) {
        final int chunk = notationDeclIndex >> 8;
        final int index = notationDeclIndex & 0xFF;
        this.fNotationName[chunk][index] = notationDecl.name;
        this.fNotationPublicId[chunk][index] = notationDecl.publicId;
        this.fNotationSystemId[chunk][index] = notationDecl.systemId;
        this.fNotationBaseSystemId[chunk][index] = notationDecl.baseSystemId;
        this.fNotationIndexMap.put(notationDecl.name, notationDeclIndex);
    }
    
    protected int addContentSpecNode(final short nodeType, final String nodeValue) {
        final int contentSpecIndex = this.createContentSpec();
        this.fContentSpec.setValues(nodeType, nodeValue, null);
        this.setContentSpec(contentSpecIndex, this.fContentSpec);
        return contentSpecIndex;
    }
    
    protected int addUniqueLeafNode(final String elementName) {
        final int contentSpecIndex = this.createContentSpec();
        this.fContentSpec.setValues((short)0, elementName, null);
        this.setContentSpec(contentSpecIndex, this.fContentSpec);
        return contentSpecIndex;
    }
    
    protected int addContentSpecNode(final short nodeType, final int leftNodeIndex, final int rightNodeIndex) {
        final int contentSpecIndex = this.createContentSpec();
        final int[] leftIntArray = { 0 };
        final int[] rightIntArray = { 0 };
        leftIntArray[0] = leftNodeIndex;
        rightIntArray[0] = rightNodeIndex;
        this.fContentSpec.setValues(nodeType, leftIntArray, rightIntArray);
        this.setContentSpec(contentSpecIndex, this.fContentSpec);
        return contentSpecIndex;
    }
    
    protected void initializeContentModelStack() {
        if (this.fOpStack == null) {
            this.fOpStack = new short[8];
            this.fNodeIndexStack = new int[8];
            this.fPrevNodeIndexStack = new int[8];
        }
        else if (this.fDepth == this.fOpStack.length) {
            final short[] newStack = new short[this.fDepth * 2];
            System.arraycopy(this.fOpStack, 0, newStack, 0, this.fDepth);
            this.fOpStack = newStack;
            int[] newIntStack = new int[this.fDepth * 2];
            System.arraycopy(this.fNodeIndexStack, 0, newIntStack, 0, this.fDepth);
            this.fNodeIndexStack = newIntStack;
            newIntStack = new int[this.fDepth * 2];
            System.arraycopy(this.fPrevNodeIndexStack, 0, newIntStack, 0, this.fDepth);
            this.fPrevNodeIndexStack = newIntStack;
        }
        this.fOpStack[this.fDepth] = -1;
        this.fNodeIndexStack[this.fDepth] = -1;
        this.fPrevNodeIndexStack[this.fDepth] = -1;
    }
    
    boolean isImmutable() {
        return this.fIsImmutable;
    }
    
    private void appendContentSpec(final XMLContentSpec contentSpec, final StringBuffer str, final boolean parens, final int parentContentSpecType) {
        final int thisContentSpec = contentSpec.type & 0xF;
        switch (thisContentSpec) {
            case 0: {
                if (contentSpec.value == null && contentSpec.otherValue == null) {
                    str.append("#PCDATA");
                    break;
                }
                if (contentSpec.value == null && contentSpec.otherValue != null) {
                    str.append("##any:uri=").append(contentSpec.otherValue);
                    break;
                }
                if (contentSpec.value == null) {
                    str.append("##any");
                    break;
                }
                str.append(contentSpec.value);
                break;
            }
            case 1: {
                if (parentContentSpecType == 3 || parentContentSpecType == 2 || parentContentSpecType == 1) {
                    this.getContentSpec(((int[])contentSpec.value)[0], contentSpec);
                    str.append('(');
                    this.appendContentSpec(contentSpec, str, true, thisContentSpec);
                    str.append(')');
                }
                else {
                    this.getContentSpec(((int[])contentSpec.value)[0], contentSpec);
                    this.appendContentSpec(contentSpec, str, true, thisContentSpec);
                }
                str.append('?');
                break;
            }
            case 2: {
                if (parentContentSpecType == 3 || parentContentSpecType == 2 || parentContentSpecType == 1) {
                    this.getContentSpec(((int[])contentSpec.value)[0], contentSpec);
                    str.append('(');
                    this.appendContentSpec(contentSpec, str, true, thisContentSpec);
                    str.append(')');
                }
                else {
                    this.getContentSpec(((int[])contentSpec.value)[0], contentSpec);
                    this.appendContentSpec(contentSpec, str, true, thisContentSpec);
                }
                str.append('*');
                break;
            }
            case 3: {
                if (parentContentSpecType == 3 || parentContentSpecType == 2 || parentContentSpecType == 1) {
                    str.append('(');
                    this.getContentSpec(((int[])contentSpec.value)[0], contentSpec);
                    this.appendContentSpec(contentSpec, str, true, thisContentSpec);
                    str.append(')');
                }
                else {
                    this.getContentSpec(((int[])contentSpec.value)[0], contentSpec);
                    this.appendContentSpec(contentSpec, str, true, thisContentSpec);
                }
                str.append('+');
                break;
            }
            case 4:
            case 5: {
                if (parens) {
                    str.append('(');
                }
                final int type = contentSpec.type;
                final int otherValue = ((int[])contentSpec.otherValue)[0];
                this.getContentSpec(((int[])contentSpec.value)[0], contentSpec);
                this.appendContentSpec(contentSpec, str, contentSpec.type != type, thisContentSpec);
                if (type == 4) {
                    str.append('|');
                }
                else {
                    str.append(',');
                }
                this.getContentSpec(otherValue, contentSpec);
                this.appendContentSpec(contentSpec, str, true, thisContentSpec);
                if (parens) {
                    str.append(')');
                    break;
                }
                break;
            }
            case 6: {
                str.append("##any");
                if (contentSpec.otherValue != null) {
                    str.append(":uri=");
                    str.append(contentSpec.otherValue);
                    break;
                }
                break;
            }
            case 7: {
                str.append("##other:uri=");
                str.append(contentSpec.otherValue);
                break;
            }
            case 8: {
                str.append("##local");
                break;
            }
            default: {
                str.append("???");
                break;
            }
        }
    }
    
    private void printAttribute(final int attributeDeclIndex) {
        final XMLAttributeDecl attributeDecl = new XMLAttributeDecl();
        if (this.getAttributeDecl(attributeDeclIndex, attributeDecl)) {
            System.out.print(" { ");
            System.out.print(attributeDecl.name.localpart);
            System.out.print(" }");
        }
    }
    
    private synchronized ContentModelValidator createChildModel(final int contentSpecIndex) {
        final XMLContentSpec contentSpec = new XMLContentSpec();
        this.getContentSpec(contentSpecIndex, contentSpec);
        if ((contentSpec.type & 0xF) != 0x6 && (contentSpec.type & 0xF) != 0x7) {
            if ((contentSpec.type & 0xF) != 0x8) {
                if (contentSpec.type == 0) {
                    if (contentSpec.value == null && contentSpec.otherValue == null) {
                        throw new RuntimeException("ImplementationMessages.VAL_NPCD");
                    }
                    this.fQName.setValues(null, (String)contentSpec.value, (String)contentSpec.value, (String)contentSpec.otherValue);
                    return new SimpleContentModel(contentSpec.type, this.fQName, null);
                }
                else if (contentSpec.type == 4 || contentSpec.type == 5) {
                    final XMLContentSpec contentSpecLeft = new XMLContentSpec();
                    final XMLContentSpec contentSpecRight = new XMLContentSpec();
                    this.getContentSpec(((int[])contentSpec.value)[0], contentSpecLeft);
                    this.getContentSpec(((int[])contentSpec.otherValue)[0], contentSpecRight);
                    if (contentSpecLeft.type == 0 && contentSpecRight.type == 0) {
                        this.fQName.setValues(null, (String)contentSpecLeft.value, (String)contentSpecLeft.value, (String)contentSpecLeft.otherValue);
                        this.fQName2.setValues(null, (String)contentSpecRight.value, (String)contentSpecRight.value, (String)contentSpecRight.otherValue);
                        return new SimpleContentModel(contentSpec.type, this.fQName, this.fQName2);
                    }
                }
                else {
                    if (contentSpec.type != 1 && contentSpec.type != 2 && contentSpec.type != 3) {
                        throw new RuntimeException("ImplementationMessages.VAL_CST");
                    }
                    final XMLContentSpec contentSpecLeft = new XMLContentSpec();
                    this.getContentSpec(((int[])contentSpec.value)[0], contentSpecLeft);
                    if (contentSpecLeft.type == 0) {
                        this.fQName.setValues(null, (String)contentSpecLeft.value, (String)contentSpecLeft.value, (String)contentSpecLeft.otherValue);
                        return new SimpleContentModel(contentSpec.type, this.fQName, null);
                    }
                }
            }
        }
        this.fLeafCount = 0;
        this.fLeafCount = 0;
        final CMNode cmn = this.buildSyntaxTree(contentSpecIndex, contentSpec);
        return new DFAContentModel(cmn, this.fLeafCount, false);
    }
    
    private final CMNode buildSyntaxTree(final int startNode, final XMLContentSpec contentSpec) {
        CMNode nodeRet = null;
        this.getContentSpec(startNode, contentSpec);
        if ((contentSpec.type & 0xF) == 0x6) {
            nodeRet = new CMAny(contentSpec.type, (String)contentSpec.otherValue, this.fLeafCount++);
        }
        else if ((contentSpec.type & 0xF) == 0x7) {
            nodeRet = new CMAny(contentSpec.type, (String)contentSpec.otherValue, this.fLeafCount++);
        }
        else if ((contentSpec.type & 0xF) == 0x8) {
            nodeRet = new CMAny(contentSpec.type, null, this.fLeafCount++);
        }
        else if (contentSpec.type == 0) {
            this.fQName.setValues(null, (String)contentSpec.value, (String)contentSpec.value, (String)contentSpec.otherValue);
            nodeRet = new CMLeaf(this.fQName, this.fLeafCount++);
        }
        else {
            final int leftNode = ((int[])contentSpec.value)[0];
            final int rightNode = ((int[])contentSpec.otherValue)[0];
            if (contentSpec.type == 4 || contentSpec.type == 5) {
                nodeRet = new CMBinOp(contentSpec.type, this.buildSyntaxTree(leftNode, contentSpec), this.buildSyntaxTree(rightNode, contentSpec));
            }
            else if (contentSpec.type == 2) {
                nodeRet = new CMUniOp(contentSpec.type, this.buildSyntaxTree(leftNode, contentSpec));
            }
            else {
                if (contentSpec.type != 2 && contentSpec.type != 1 && contentSpec.type != 3) {
                    throw new RuntimeException("ImplementationMessages.VAL_CST");
                }
                nodeRet = new CMUniOp(contentSpec.type, this.buildSyntaxTree(leftNode, contentSpec));
            }
        }
        return nodeRet;
    }
    
    private void contentSpecTree(final int contentSpecIndex, final XMLContentSpec contentSpec, final ChildrenList children) {
        this.getContentSpec(contentSpecIndex, contentSpec);
        if (contentSpec.type == 0 || (contentSpec.type & 0xF) == 0x6 || (contentSpec.type & 0xF) == 0x8 || (contentSpec.type & 0xF) == 0x7) {
            if (children.length == children.qname.length) {
                final QName[] newQName = new QName[children.length * 2];
                System.arraycopy(children.qname, 0, newQName, 0, children.length);
                children.qname = newQName;
                final int[] newType = new int[children.length * 2];
                System.arraycopy(children.type, 0, newType, 0, children.length);
                children.type = newType;
            }
            children.qname[children.length] = new QName(null, (String)contentSpec.value, (String)contentSpec.value, (String)contentSpec.otherValue);
            children.type[children.length] = contentSpec.type;
            ++children.length;
            return;
        }
        final int leftNode = (contentSpec.value != null) ? ((int[])contentSpec.value)[0] : -1;
        int rightNode = -1;
        if (contentSpec.otherValue == null) {
            return;
        }
        rightNode = ((int[])contentSpec.otherValue)[0];
        if (contentSpec.type == 4 || contentSpec.type == 5) {
            this.contentSpecTree(leftNode, contentSpec, children);
            this.contentSpecTree(rightNode, contentSpec, children);
            return;
        }
        if (contentSpec.type == 1 || contentSpec.type == 2 || contentSpec.type == 3) {
            this.contentSpecTree(leftNode, contentSpec, children);
            return;
        }
        throw new RuntimeException("Invalid content spec type seen in contentSpecTree() method of AbstractDTDGrammar class : " + contentSpec.type);
    }
    
    private void ensureElementDeclCapacity(final int chunk) {
        if (chunk >= this.fElementDeclName.length) {
            this.fElementDeclIsExternal = resize(this.fElementDeclIsExternal, this.fElementDeclIsExternal.length * 2);
            this.fElementDeclName = resize(this.fElementDeclName, this.fElementDeclName.length * 2);
            this.fElementDeclType = resize(this.fElementDeclType, this.fElementDeclType.length * 2);
            this.fElementDeclContentModelValidator = resize(this.fElementDeclContentModelValidator, this.fElementDeclContentModelValidator.length * 2);
            this.fElementDeclContentSpecIndex = resize(this.fElementDeclContentSpecIndex, this.fElementDeclContentSpecIndex.length * 2);
            this.fElementDeclFirstAttributeDeclIndex = resize(this.fElementDeclFirstAttributeDeclIndex, this.fElementDeclFirstAttributeDeclIndex.length * 2);
            this.fElementDeclLastAttributeDeclIndex = resize(this.fElementDeclLastAttributeDeclIndex, this.fElementDeclLastAttributeDeclIndex.length * 2);
        }
        else if (this.fElementDeclName[chunk] != null) {
            return;
        }
        this.fElementDeclIsExternal[chunk] = new int[256];
        this.fElementDeclName[chunk] = new QName[256];
        this.fElementDeclType[chunk] = new short[256];
        this.fElementDeclContentModelValidator[chunk] = new ContentModelValidator[256];
        this.fElementDeclContentSpecIndex[chunk] = new int[256];
        this.fElementDeclFirstAttributeDeclIndex[chunk] = new int[256];
        this.fElementDeclLastAttributeDeclIndex[chunk] = new int[256];
    }
    
    private void ensureAttributeDeclCapacity(final int chunk) {
        if (chunk >= this.fAttributeDeclName.length) {
            this.fAttributeDeclIsExternal = resize(this.fAttributeDeclIsExternal, this.fAttributeDeclIsExternal.length * 2);
            this.fAttributeDeclName = resize(this.fAttributeDeclName, this.fAttributeDeclName.length * 2);
            this.fAttributeDeclType = resize(this.fAttributeDeclType, this.fAttributeDeclType.length * 2);
            this.fAttributeDeclEnumeration = resize(this.fAttributeDeclEnumeration, this.fAttributeDeclEnumeration.length * 2);
            this.fAttributeDeclDefaultType = resize(this.fAttributeDeclDefaultType, this.fAttributeDeclDefaultType.length * 2);
            this.fAttributeDeclDatatypeValidator = resize(this.fAttributeDeclDatatypeValidator, this.fAttributeDeclDatatypeValidator.length * 2);
            this.fAttributeDeclDefaultValue = resize(this.fAttributeDeclDefaultValue, this.fAttributeDeclDefaultValue.length * 2);
            this.fAttributeDeclNonNormalizedDefaultValue = resize(this.fAttributeDeclNonNormalizedDefaultValue, this.fAttributeDeclNonNormalizedDefaultValue.length * 2);
            this.fAttributeDeclNextAttributeDeclIndex = resize(this.fAttributeDeclNextAttributeDeclIndex, this.fAttributeDeclNextAttributeDeclIndex.length * 2);
        }
        else if (this.fAttributeDeclName[chunk] != null) {
            return;
        }
        this.fAttributeDeclIsExternal[chunk] = new int[256];
        this.fAttributeDeclName[chunk] = new QName[256];
        this.fAttributeDeclType[chunk] = new short[256];
        this.fAttributeDeclEnumeration[chunk] = new String[256][];
        this.fAttributeDeclDefaultType[chunk] = new short[256];
        this.fAttributeDeclDatatypeValidator[chunk] = new DatatypeValidator[256];
        this.fAttributeDeclDefaultValue[chunk] = new String[256];
        this.fAttributeDeclNonNormalizedDefaultValue[chunk] = new String[256];
        this.fAttributeDeclNextAttributeDeclIndex[chunk] = new int[256];
    }
    
    private void ensureEntityDeclCapacity(final int chunk) {
        if (chunk >= this.fEntityName.length) {
            this.fEntityName = resize(this.fEntityName, this.fEntityName.length * 2);
            this.fEntityValue = resize(this.fEntityValue, this.fEntityValue.length * 2);
            this.fEntityPublicId = resize(this.fEntityPublicId, this.fEntityPublicId.length * 2);
            this.fEntitySystemId = resize(this.fEntitySystemId, this.fEntitySystemId.length * 2);
            this.fEntityBaseSystemId = resize(this.fEntityBaseSystemId, this.fEntityBaseSystemId.length * 2);
            this.fEntityNotation = resize(this.fEntityNotation, this.fEntityNotation.length * 2);
            this.fEntityIsPE = resize(this.fEntityIsPE, this.fEntityIsPE.length * 2);
            this.fEntityInExternal = resize(this.fEntityInExternal, this.fEntityInExternal.length * 2);
        }
        else if (this.fEntityName[chunk] != null) {
            return;
        }
        this.fEntityName[chunk] = new String[256];
        this.fEntityValue[chunk] = new String[256];
        this.fEntityPublicId[chunk] = new String[256];
        this.fEntitySystemId[chunk] = new String[256];
        this.fEntityBaseSystemId[chunk] = new String[256];
        this.fEntityNotation[chunk] = new String[256];
        this.fEntityIsPE[chunk] = new byte[256];
        this.fEntityInExternal[chunk] = new byte[256];
    }
    
    private void ensureNotationDeclCapacity(final int chunk) {
        if (chunk >= this.fNotationName.length) {
            this.fNotationName = resize(this.fNotationName, this.fNotationName.length * 2);
            this.fNotationPublicId = resize(this.fNotationPublicId, this.fNotationPublicId.length * 2);
            this.fNotationSystemId = resize(this.fNotationSystemId, this.fNotationSystemId.length * 2);
            this.fNotationBaseSystemId = resize(this.fNotationBaseSystemId, this.fNotationBaseSystemId.length * 2);
        }
        else if (this.fNotationName[chunk] != null) {
            return;
        }
        this.fNotationName[chunk] = new String[256];
        this.fNotationPublicId[chunk] = new String[256];
        this.fNotationSystemId[chunk] = new String[256];
        this.fNotationBaseSystemId[chunk] = new String[256];
    }
    
    private void ensureContentSpecCapacity(final int chunk) {
        if (chunk >= this.fContentSpecType.length) {
            this.fContentSpecType = resize(this.fContentSpecType, this.fContentSpecType.length * 2);
            this.fContentSpecValue = resize(this.fContentSpecValue, this.fContentSpecValue.length * 2);
            this.fContentSpecOtherValue = resize(this.fContentSpecOtherValue, this.fContentSpecOtherValue.length * 2);
        }
        else if (this.fContentSpecType[chunk] != null) {
            return;
        }
        this.fContentSpecType[chunk] = new short[256];
        this.fContentSpecValue[chunk] = new Object[256];
        this.fContentSpecOtherValue[chunk] = new Object[256];
    }
    
    private static byte[][] resize(final byte[][] array, final int newsize) {
        final byte[][] newarray = new byte[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
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
    
    private static DatatypeValidator[][] resize(final DatatypeValidator[][] array, final int newsize) {
        final DatatypeValidator[][] newarray = new DatatypeValidator[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }
    
    private static ContentModelValidator[][] resize(final ContentModelValidator[][] array, final int newsize) {
        final ContentModelValidator[][] newarray = new ContentModelValidator[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }
    
    private static Object[][] resize(final Object[][] array, final int newsize) {
        final Object[][] newarray = new Object[newsize][];
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
    
    @Override
    public boolean isEntityDeclared(final String name) {
        return this.getEntityDeclIndex(name) != -1;
    }
    
    @Override
    public boolean isEntityUnparsed(final String name) {
        final int entityIndex = this.getEntityDeclIndex(name);
        if (entityIndex > -1) {
            final int chunk = entityIndex >> 8;
            final int index = entityIndex & 0xFF;
            return this.fEntityNotation[chunk][index] != null;
        }
        return false;
    }
    
    private static class ChildrenList
    {
        public int length;
        public QName[] qname;
        public int[] type;
        
        public ChildrenList() {
            this.length = 0;
            this.qname = new QName[2];
            this.type = new int[2];
        }
    }
}
