package org.apache.xerces.impl.dtd;

import java.util.Random;
import org.apache.xerces.impl.dtd.models.CMUniOp;
import org.apache.xerces.impl.dtd.models.CMBinOp;
import org.apache.xerces.impl.dtd.models.CMLeaf;
import org.apache.xerces.impl.dtd.models.CMAny;
import org.apache.xerces.impl.dtd.models.CMNode;
import org.apache.xerces.impl.dtd.models.DFAContentModel;
import org.apache.xerces.impl.dtd.models.SimpleContentModel;
import org.apache.xerces.impl.dtd.models.MixedContentModel;
import java.util.ArrayList;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import java.util.Hashtable;
import org.apache.xerces.impl.dv.DatatypeValidator;
import org.apache.xerces.impl.dtd.models.ContentModelValidator;
import org.apache.xerces.xni.QName;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.parser.XMLDTDContentModelSource;
import org.apache.xerces.xni.parser.XMLDTDSource;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.impl.validation.EntityState;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.XMLDTDHandler;

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
    private QNameHashtable fElementIndexMap;
    private QNameHashtable fEntityIndexMap;
    private QNameHashtable fNotationIndexMap;
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
    Hashtable fElementDeclTab;
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
    
    public DTDGrammar(final SymbolTable fSymbolTable, final XMLDTDDescription fGrammarDescription) {
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
        this.fElementIndexMap = new QNameHashtable();
        this.fEntityIndexMap = new QNameHashtable();
        this.fNotationIndexMap = new QNameHashtable();
        this.fQName = new QName();
        this.fQName2 = new QName();
        this.fAttributeDecl = new XMLAttributeDecl();
        this.fLeafCount = 0;
        this.fEpsilonIndex = -1;
        this.fElementDecl = new XMLElementDecl();
        this.fEntityDecl = new XMLEntityDecl();
        this.fSimpleType = new XMLSimpleType();
        this.fContentSpec = new XMLContentSpec();
        this.fElementDeclTab = new Hashtable();
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
        this.fSymbolTable = fSymbolTable;
        this.fGrammarDescription = fGrammarDescription;
    }
    
    public XMLGrammarDescription getGrammarDescription() {
        return this.fGrammarDescription;
    }
    
    public boolean getElementDeclIsExternal(final int n) {
        return n >= 0 && this.fElementDeclIsExternal[n >> 8][n & 0xFF] != 0;
    }
    
    public boolean getAttributeDeclIsExternal(final int n) {
        return n >= 0 && this.fAttributeDeclIsExternal[n >> 8][n & 0xFF] != 0;
    }
    
    public int getAttributeDeclIndex(final int n, final String s) {
        if (n == -1) {
            return -1;
        }
        for (int i = this.getFirstAttributeDeclIndex(n); i != -1; i = this.getNextAttributeDeclIndex(i)) {
            this.getAttributeDecl(i, this.fAttributeDecl);
            if (this.fAttributeDecl.name.rawname == s || s.equals(this.fAttributeDecl.name.rawname)) {
                return i;
            }
        }
        return -1;
    }
    
    public void startDTD(final XMLLocator xmlLocator, final Augmentations augmentations) throws XNIException {
        this.fOpStack = null;
        this.fNodeIndexStack = null;
        this.fPrevNodeIndexStack = null;
    }
    
    public void startParameterEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
        if (this.fPEDepth == this.fPEntityStack.length) {
            final boolean[] fpEntityStack = new boolean[this.fPEntityStack.length * 2];
            System.arraycopy(this.fPEntityStack, 0, fpEntityStack, 0, this.fPEntityStack.length);
            this.fPEntityStack = fpEntityStack;
        }
        this.fPEntityStack[this.fPEDepth] = this.fReadingExternalDTD;
        ++this.fPEDepth;
    }
    
    public void startExternalSubset(final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
        this.fReadingExternalDTD = true;
    }
    
    public void endParameterEntity(final String s, final Augmentations augmentations) throws XNIException {
        --this.fPEDepth;
        this.fReadingExternalDTD = this.fPEntityStack[this.fPEDepth];
    }
    
    public void endExternalSubset(final Augmentations augmentations) throws XNIException {
        this.fReadingExternalDTD = false;
    }
    
    public void elementDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
        final XMLElementDecl xmlElementDecl = this.fElementDeclTab.get(s);
        if (xmlElementDecl != null) {
            if (xmlElementDecl.type != -1) {
                return;
            }
            this.fCurrentElementIndex = this.getElementDeclIndex(s);
        }
        else {
            this.fCurrentElementIndex = this.createElementDecl();
        }
        final XMLElementDecl fElementDecl = new XMLElementDecl();
        this.fQName.setValues(null, s, s, null);
        fElementDecl.name.setValues(this.fQName);
        fElementDecl.contentModelValidator = null;
        fElementDecl.scope = -1;
        if (s2.equals("EMPTY")) {
            fElementDecl.type = 1;
        }
        else if (s2.equals("ANY")) {
            fElementDecl.type = 0;
        }
        else if (s2.startsWith("(")) {
            if (s2.indexOf("#PCDATA") > 0) {
                fElementDecl.type = 2;
            }
            else {
                fElementDecl.type = 3;
            }
        }
        this.fElementDeclTab.put(s, fElementDecl);
        this.addContentSpecToElement(this.fElementDecl = fElementDecl);
        this.setElementDecl(this.fCurrentElementIndex, this.fElementDecl);
        final int n = this.fCurrentElementIndex >> 8;
        final int n2 = this.fCurrentElementIndex & 0xFF;
        this.ensureElementDeclCapacity(n);
        this.fElementDeclIsExternal[n][n2] = ((this.fReadingExternalDTD || this.fPEDepth > 0) ? 1 : 0);
    }
    
    public void attributeDecl(final String s, final String s2, final String s3, final String[] enumeration, final String s4, final XMLString xmlString, final XMLString xmlString2, final Augmentations augmentations) throws XNIException {
        if (!this.fElementDeclTab.containsKey(s)) {
            this.fCurrentElementIndex = this.createElementDecl();
            final XMLElementDecl xmlElementDecl = new XMLElementDecl();
            xmlElementDecl.name.setValues(null, s, s, null);
            xmlElementDecl.scope = -1;
            this.fElementDeclTab.put(s, xmlElementDecl);
            this.setElementDecl(this.fCurrentElementIndex, xmlElementDecl);
        }
        final int elementDeclIndex = this.getElementDeclIndex(s);
        if (this.getAttributeDeclIndex(elementDeclIndex, s2) != -1) {
            return;
        }
        this.fCurrentAttributeIndex = this.createAttributeDecl();
        this.fSimpleType.clear();
        if (s4 != null) {
            if (s4.equals("#FIXED")) {
                this.fSimpleType.defaultType = 1;
            }
            else if (s4.equals("#IMPLIED")) {
                this.fSimpleType.defaultType = 0;
            }
            else if (s4.equals("#REQUIRED")) {
                this.fSimpleType.defaultType = 2;
            }
        }
        this.fSimpleType.defaultValue = ((xmlString != null) ? xmlString.toString() : null);
        this.fSimpleType.nonNormalizedDefaultValue = ((xmlString2 != null) ? xmlString2.toString() : null);
        this.fSimpleType.enumeration = enumeration;
        if (s3.equals("CDATA")) {
            this.fSimpleType.type = 0;
        }
        else if (s3.equals("ID")) {
            this.fSimpleType.type = 3;
        }
        else if (s3.startsWith("IDREF")) {
            this.fSimpleType.type = 4;
            if (s3.indexOf("S") > 0) {
                this.fSimpleType.list = true;
            }
        }
        else if (s3.equals("ENTITIES")) {
            this.fSimpleType.type = 1;
            this.fSimpleType.list = true;
        }
        else if (s3.equals("ENTITY")) {
            this.fSimpleType.type = 1;
        }
        else if (s3.equals("NMTOKENS")) {
            this.fSimpleType.type = 5;
            this.fSimpleType.list = true;
        }
        else if (s3.equals("NMTOKEN")) {
            this.fSimpleType.type = 5;
        }
        else if (s3.startsWith("NOTATION")) {
            this.fSimpleType.type = 6;
        }
        else if (s3.startsWith("ENUMERATION")) {
            this.fSimpleType.type = 2;
        }
        else {
            System.err.println("!!! unknown attribute type " + s3);
        }
        this.fQName.setValues(null, s2, s2, null);
        this.fAttributeDecl.setValues(this.fQName, this.fSimpleType, false);
        this.setAttributeDecl(elementDeclIndex, this.fCurrentAttributeIndex, this.fAttributeDecl);
        final int n = this.fCurrentAttributeIndex >> 8;
        final int n2 = this.fCurrentAttributeIndex & 0xFF;
        this.ensureAttributeDeclCapacity(n);
        this.fAttributeDeclIsExternal[n][n2] = ((this.fReadingExternalDTD || this.fPEDepth > 0) ? 1 : 0);
    }
    
    public void internalEntityDecl(final String s, final XMLString xmlString, final XMLString xmlString2, final Augmentations augmentations) throws XNIException {
        if (this.getEntityDeclIndex(s) == -1) {
            final int entityDecl = this.createEntityDecl();
            final boolean startsWith = s.startsWith("%");
            final boolean b = this.fReadingExternalDTD || this.fPEDepth > 0;
            final XMLEntityDecl xmlEntityDecl = new XMLEntityDecl();
            xmlEntityDecl.setValues(s, null, null, null, null, xmlString.toString(), startsWith, b);
            this.setEntityDecl(entityDecl, xmlEntityDecl);
        }
    }
    
    public void externalEntityDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
        if (this.getEntityDeclIndex(s) == -1) {
            final int entityDecl = this.createEntityDecl();
            final boolean startsWith = s.startsWith("%");
            final boolean b = this.fReadingExternalDTD || this.fPEDepth > 0;
            final XMLEntityDecl xmlEntityDecl = new XMLEntityDecl();
            xmlEntityDecl.setValues(s, xmlResourceIdentifier.getPublicId(), xmlResourceIdentifier.getLiteralSystemId(), xmlResourceIdentifier.getBaseSystemId(), null, null, startsWith, b);
            this.setEntityDecl(entityDecl, xmlEntityDecl);
        }
    }
    
    public void unparsedEntityDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
        final XMLEntityDecl xmlEntityDecl = new XMLEntityDecl();
        xmlEntityDecl.setValues(s, xmlResourceIdentifier.getPublicId(), xmlResourceIdentifier.getLiteralSystemId(), xmlResourceIdentifier.getBaseSystemId(), s2, null, s.startsWith("%"), this.fReadingExternalDTD || this.fPEDepth > 0);
        if (this.getEntityDeclIndex(s) == -1) {
            this.setEntityDecl(this.createEntityDecl(), xmlEntityDecl);
        }
    }
    
    public void notationDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
        final XMLNotationDecl xmlNotationDecl = new XMLNotationDecl();
        xmlNotationDecl.setValues(s, xmlResourceIdentifier.getPublicId(), xmlResourceIdentifier.getLiteralSystemId(), xmlResourceIdentifier.getBaseSystemId());
        if (this.getNotationDeclIndex(s) == -1) {
            this.setNotationDecl(this.createNotationDecl(), xmlNotationDecl);
        }
    }
    
    public void endDTD(final Augmentations augmentations) throws XNIException {
        this.fIsImmutable = true;
        if (this.fGrammarDescription.getRootName() == null) {
            final int fElementDeclCount = this.fElementDeclCount;
            final ArrayList possibleRoots = new ArrayList<String>(fElementDeclCount);
            for (int i = 0; i < fElementDeclCount; ++i) {
                possibleRoots.add(this.fElementDeclName[i >> 8][i & 0xFF].rawname);
            }
            this.fGrammarDescription.setPossibleRoots(possibleRoots);
        }
    }
    
    public void setDTDSource(final XMLDTDSource fdtdSource) {
        this.fDTDSource = fdtdSource;
    }
    
    public XMLDTDSource getDTDSource() {
        return this.fDTDSource;
    }
    
    public void textDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
    }
    
    public void comment(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
    }
    
    public void processingInstruction(final String s, final XMLString xmlString, final Augmentations augmentations) throws XNIException {
    }
    
    public void startAttlist(final String s, final Augmentations augmentations) throws XNIException {
    }
    
    public void endAttlist(final Augmentations augmentations) throws XNIException {
    }
    
    public void startConditional(final short n, final Augmentations augmentations) throws XNIException {
    }
    
    public void ignoredCharacters(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
    }
    
    public void endConditional(final Augmentations augmentations) throws XNIException {
    }
    
    public void setDTDContentModelSource(final XMLDTDContentModelSource fdtdContentModelSource) {
        this.fDTDContentModelSource = fdtdContentModelSource;
    }
    
    public XMLDTDContentModelSource getDTDContentModelSource() {
        return this.fDTDContentModelSource;
    }
    
    public void startContentModel(final String s, final Augmentations augmentations) throws XNIException {
        final XMLElementDecl fElementDecl = this.fElementDeclTab.get(s);
        if (fElementDecl != null) {
            this.fElementDecl = fElementDecl;
        }
        this.fDepth = 0;
        this.initializeContentModelStack();
    }
    
    public void startGroup(final Augmentations augmentations) throws XNIException {
        ++this.fDepth;
        this.initializeContentModelStack();
        this.fMixed = false;
    }
    
    public void pcdata(final Augmentations augmentations) throws XNIException {
        this.fMixed = true;
    }
    
    public void element(final String s, final Augmentations augmentations) throws XNIException {
        if (this.fMixed) {
            if (this.fNodeIndexStack[this.fDepth] == -1) {
                this.fNodeIndexStack[this.fDepth] = this.addUniqueLeafNode(s);
            }
            else {
                this.fNodeIndexStack[this.fDepth] = this.addContentSpecNode((short)4, this.fNodeIndexStack[this.fDepth], this.addUniqueLeafNode(s));
            }
        }
        else {
            this.fNodeIndexStack[this.fDepth] = this.addContentSpecNode((short)0, s);
        }
    }
    
    public void separator(final short n, final Augmentations augmentations) throws XNIException {
        if (!this.fMixed) {
            if (this.fOpStack[this.fDepth] != 5 && n == 0) {
                if (this.fPrevNodeIndexStack[this.fDepth] != -1) {
                    this.fNodeIndexStack[this.fDepth] = this.addContentSpecNode(this.fOpStack[this.fDepth], this.fPrevNodeIndexStack[this.fDepth], this.fNodeIndexStack[this.fDepth]);
                }
                this.fPrevNodeIndexStack[this.fDepth] = this.fNodeIndexStack[this.fDepth];
                this.fOpStack[this.fDepth] = 4;
            }
            else if (this.fOpStack[this.fDepth] != 4 && n == 1) {
                if (this.fPrevNodeIndexStack[this.fDepth] != -1) {
                    this.fNodeIndexStack[this.fDepth] = this.addContentSpecNode(this.fOpStack[this.fDepth], this.fPrevNodeIndexStack[this.fDepth], this.fNodeIndexStack[this.fDepth]);
                }
                this.fPrevNodeIndexStack[this.fDepth] = this.fNodeIndexStack[this.fDepth];
                this.fOpStack[this.fDepth] = 5;
            }
        }
    }
    
    public void occurrence(final short n, final Augmentations augmentations) throws XNIException {
        if (!this.fMixed) {
            if (n == 2) {
                this.fNodeIndexStack[this.fDepth] = this.addContentSpecNode((short)1, this.fNodeIndexStack[this.fDepth], -1);
            }
            else if (n == 3) {
                this.fNodeIndexStack[this.fDepth] = this.addContentSpecNode((short)2, this.fNodeIndexStack[this.fDepth], -1);
            }
            else if (n == 4) {
                this.fNodeIndexStack[this.fDepth] = this.addContentSpecNode((short)3, this.fNodeIndexStack[this.fDepth], -1);
            }
        }
    }
    
    public void endGroup(final Augmentations augmentations) throws XNIException {
        if (!this.fMixed) {
            if (this.fPrevNodeIndexStack[this.fDepth] != -1) {
                this.fNodeIndexStack[this.fDepth] = this.addContentSpecNode(this.fOpStack[this.fDepth], this.fPrevNodeIndexStack[this.fDepth], this.fNodeIndexStack[this.fDepth]);
            }
            this.fNodeIndexStack[this.fDepth] = this.fNodeIndexStack[this.fDepth--];
        }
    }
    
    public void any(final Augmentations augmentations) throws XNIException {
    }
    
    public void empty(final Augmentations augmentations) throws XNIException {
    }
    
    public void endContentModel(final Augmentations augmentations) throws XNIException {
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
    
    public int getNextElementDeclIndex(final int n) {
        return (n < this.fElementDeclCount - 1) ? (n + 1) : -1;
    }
    
    public int getElementDeclIndex(final String s) {
        return this.fElementIndexMap.get(s);
    }
    
    public int getElementDeclIndex(final QName qName) {
        return this.getElementDeclIndex(qName.rawname);
    }
    
    public short getContentSpecType(final int n) {
        if (n < 0 || n >= this.fElementDeclCount) {
            return -1;
        }
        final int n2 = n >> 8;
        final int n3 = n & 0xFF;
        if (this.fElementDeclType[n2][n3] == -1) {
            return -1;
        }
        return (short)(this.fElementDeclType[n2][n3] & 0xFFFFFF7F);
    }
    
    public boolean getElementDecl(final int n, final XMLElementDecl xmlElementDecl) {
        if (n < 0 || n >= this.fElementDeclCount) {
            return false;
        }
        final int n2 = n >> 8;
        final int n3 = n & 0xFF;
        xmlElementDecl.name.setValues(this.fElementDeclName[n2][n3]);
        if (this.fElementDeclType[n2][n3] == -1) {
            xmlElementDecl.type = -1;
            xmlElementDecl.simpleType.list = false;
        }
        else {
            xmlElementDecl.type = (short)(this.fElementDeclType[n2][n3] & 0xFFFFFF7F);
            xmlElementDecl.simpleType.list = ((this.fElementDeclType[n2][n3] & 0x80) != 0x0);
        }
        if (xmlElementDecl.type == 3 || xmlElementDecl.type == 2) {
            xmlElementDecl.contentModelValidator = this.getElementContentModelValidator(n);
        }
        xmlElementDecl.simpleType.datatypeValidator = null;
        xmlElementDecl.simpleType.defaultType = -1;
        xmlElementDecl.simpleType.defaultValue = null;
        return true;
    }
    
    QName getElementDeclName(final int n) {
        if (n < 0 || n >= this.fElementDeclCount) {
            return null;
        }
        return this.fElementDeclName[n >> 8][n & 0xFF];
    }
    
    public int getFirstAttributeDeclIndex(final int n) {
        return this.fElementDeclFirstAttributeDeclIndex[n >> 8][n & 0xFF];
    }
    
    public int getNextAttributeDeclIndex(final int n) {
        return this.fAttributeDeclNextAttributeDeclIndex[n >> 8][n & 0xFF];
    }
    
    public boolean getAttributeDecl(final int n, final XMLAttributeDecl xmlAttributeDecl) {
        if (n < 0 || n >= this.fAttributeDeclCount) {
            return false;
        }
        final int n2 = n >> 8;
        final int n3 = n & 0xFF;
        xmlAttributeDecl.name.setValues(this.fAttributeDeclName[n2][n3]);
        short n4;
        boolean b;
        if (this.fAttributeDeclType[n2][n3] == -1) {
            n4 = -1;
            b = false;
        }
        else {
            n4 = (short)(this.fAttributeDeclType[n2][n3] & 0xFFFFFF7F);
            b = ((this.fAttributeDeclType[n2][n3] & 0x80) != 0x0);
        }
        xmlAttributeDecl.simpleType.setValues(n4, this.fAttributeDeclName[n2][n3].localpart, this.fAttributeDeclEnumeration[n2][n3], b, this.fAttributeDeclDefaultType[n2][n3], this.fAttributeDeclDefaultValue[n2][n3], this.fAttributeDeclNonNormalizedDefaultValue[n2][n3], this.fAttributeDeclDatatypeValidator[n2][n3]);
        return true;
    }
    
    public boolean isCDATAAttribute(final QName qName, final QName qName2) {
        return !this.getAttributeDecl(this.getElementDeclIndex(qName), this.fAttributeDecl) || this.fAttributeDecl.simpleType.type == 0;
    }
    
    public int getEntityDeclIndex(final String s) {
        if (s == null) {
            return -1;
        }
        return this.fEntityIndexMap.get(s);
    }
    
    public boolean getEntityDecl(final int n, final XMLEntityDecl xmlEntityDecl) {
        if (n < 0 || n >= this.fEntityCount) {
            return false;
        }
        final int n2 = n >> 8;
        final int n3 = n & 0xFF;
        xmlEntityDecl.setValues(this.fEntityName[n2][n3], this.fEntityPublicId[n2][n3], this.fEntitySystemId[n2][n3], this.fEntityBaseSystemId[n2][n3], this.fEntityNotation[n2][n3], this.fEntityValue[n2][n3], this.fEntityIsPE[n2][n3] != 0, this.fEntityInExternal[n2][n3] != 0);
        return true;
    }
    
    public int getNotationDeclIndex(final String s) {
        if (s == null) {
            return -1;
        }
        return this.fNotationIndexMap.get(s);
    }
    
    public boolean getNotationDecl(final int n, final XMLNotationDecl xmlNotationDecl) {
        if (n < 0 || n >= this.fNotationCount) {
            return false;
        }
        final int n2 = n >> 8;
        final int n3 = n & 0xFF;
        xmlNotationDecl.setValues(this.fNotationName[n2][n3], this.fNotationPublicId[n2][n3], this.fNotationSystemId[n2][n3], this.fNotationBaseSystemId[n2][n3]);
        return true;
    }
    
    public boolean getContentSpec(final int n, final XMLContentSpec xmlContentSpec) {
        if (n < 0 || n >= this.fContentSpecCount) {
            return false;
        }
        final int n2 = n >> 8;
        final int n3 = n & 0xFF;
        xmlContentSpec.type = this.fContentSpecType[n2][n3];
        xmlContentSpec.value = this.fContentSpecValue[n2][n3];
        xmlContentSpec.otherValue = this.fContentSpecOtherValue[n2][n3];
        return true;
    }
    
    public int getContentSpecIndex(final int n) {
        if (n < 0 || n >= this.fElementDeclCount) {
            return -1;
        }
        return this.fElementDeclContentSpecIndex[n >> 8][n & 0xFF];
    }
    
    public String getContentSpecAsString(final int n) {
        if (n < 0 || n >= this.fElementDeclCount) {
            return null;
        }
        final int n2 = this.fElementDeclContentSpecIndex[n >> 8][n & 0xFF];
        final XMLContentSpec xmlContentSpec = new XMLContentSpec();
        if (this.getContentSpec(n2, xmlContentSpec)) {
            final StringBuffer sb = new StringBuffer();
            final int n3 = xmlContentSpec.type & 0xF;
            switch (n3) {
                case 0: {
                    sb.append('(');
                    if (xmlContentSpec.value == null && xmlContentSpec.otherValue == null) {
                        sb.append("#PCDATA");
                    }
                    else {
                        sb.append(xmlContentSpec.value);
                    }
                    sb.append(')');
                    break;
                }
                case 1: {
                    this.getContentSpec(((int[])xmlContentSpec.value)[0], xmlContentSpec);
                    final short type = xmlContentSpec.type;
                    if (type == 0) {
                        sb.append('(');
                        sb.append(xmlContentSpec.value);
                        sb.append(')');
                    }
                    else if (type == 3 || type == 2 || type == 1) {
                        sb.append('(');
                        this.appendContentSpec(xmlContentSpec, sb, true, n3);
                        sb.append(')');
                    }
                    else {
                        this.appendContentSpec(xmlContentSpec, sb, true, n3);
                    }
                    sb.append('?');
                    break;
                }
                case 2: {
                    this.getContentSpec(((int[])xmlContentSpec.value)[0], xmlContentSpec);
                    final short type2 = xmlContentSpec.type;
                    if (type2 == 0) {
                        sb.append('(');
                        if (xmlContentSpec.value == null && xmlContentSpec.otherValue == null) {
                            sb.append("#PCDATA");
                        }
                        else if (xmlContentSpec.otherValue != null) {
                            sb.append("##any:uri=").append(xmlContentSpec.otherValue);
                        }
                        else if (xmlContentSpec.value == null) {
                            sb.append("##any");
                        }
                        else {
                            this.appendContentSpec(xmlContentSpec, sb, true, n3);
                        }
                        sb.append(')');
                    }
                    else if (type2 == 3 || type2 == 2 || type2 == 1) {
                        sb.append('(');
                        this.appendContentSpec(xmlContentSpec, sb, true, n3);
                        sb.append(')');
                    }
                    else {
                        this.appendContentSpec(xmlContentSpec, sb, true, n3);
                    }
                    sb.append('*');
                    break;
                }
                case 3: {
                    this.getContentSpec(((int[])xmlContentSpec.value)[0], xmlContentSpec);
                    final short type3 = xmlContentSpec.type;
                    if (type3 == 0) {
                        sb.append('(');
                        if (xmlContentSpec.value == null && xmlContentSpec.otherValue == null) {
                            sb.append("#PCDATA");
                        }
                        else if (xmlContentSpec.otherValue != null) {
                            sb.append("##any:uri=").append(xmlContentSpec.otherValue);
                        }
                        else if (xmlContentSpec.value == null) {
                            sb.append("##any");
                        }
                        else {
                            sb.append(xmlContentSpec.value);
                        }
                        sb.append(')');
                    }
                    else if (type3 == 3 || type3 == 2 || type3 == 1) {
                        sb.append('(');
                        this.appendContentSpec(xmlContentSpec, sb, true, n3);
                        sb.append(')');
                    }
                    else {
                        this.appendContentSpec(xmlContentSpec, sb, true, n3);
                    }
                    sb.append('+');
                    break;
                }
                case 4:
                case 5: {
                    this.appendContentSpec(xmlContentSpec, sb, true, n3);
                    break;
                }
                case 6: {
                    sb.append("##any");
                    if (xmlContentSpec.otherValue != null) {
                        sb.append(":uri=");
                        sb.append(xmlContentSpec.otherValue);
                        break;
                    }
                    break;
                }
                case 7: {
                    sb.append("##other:uri=");
                    sb.append(xmlContentSpec.otherValue);
                    break;
                }
                case 8: {
                    sb.append("##local");
                    break;
                }
                default: {
                    sb.append("???");
                    break;
                }
            }
            return sb.toString();
        }
        return null;
    }
    
    public void printElements() {
        int n = 0;
        final XMLElementDecl xmlElementDecl = new XMLElementDecl();
        while (this.getElementDecl(n++, xmlElementDecl)) {
            System.out.println("element decl: " + xmlElementDecl.name + ", " + xmlElementDecl.name.rawname);
        }
    }
    
    public void printAttributes(final int n) {
        int i = this.getFirstAttributeDeclIndex(n);
        System.out.print(n);
        System.out.print(" [");
        while (i != -1) {
            System.out.print(' ');
            System.out.print(i);
            this.printAttribute(i);
            i = this.getNextAttributeDeclIndex(i);
            if (i != -1) {
                System.out.print(",");
            }
        }
        System.out.println(" ]");
    }
    
    protected void addContentSpecToElement(final XMLElementDecl xmlElementDecl) {
        if ((this.fDepth == 0 || (this.fDepth == 1 && xmlElementDecl.type == 2)) && this.fNodeIndexStack != null) {
            if (xmlElementDecl.type == 2) {
                final int addUniqueLeafNode = this.addUniqueLeafNode(null);
                if (this.fNodeIndexStack[0] == -1) {
                    this.fNodeIndexStack[0] = addUniqueLeafNode;
                }
                else {
                    this.fNodeIndexStack[0] = this.addContentSpecNode((short)4, addUniqueLeafNode, this.fNodeIndexStack[0]);
                }
            }
            this.setContentSpecIndex(this.fCurrentElementIndex, this.fNodeIndexStack[this.fDepth]);
        }
    }
    
    protected ContentModelValidator getElementContentModelValidator(final int n) {
        final int n2 = n >> 8;
        final int n3 = n & 0xFF;
        final ContentModelValidator contentModelValidator = this.fElementDeclContentModelValidator[n2][n3];
        if (contentModelValidator != null) {
            return contentModelValidator;
        }
        final short n4 = this.fElementDeclType[n2][n3];
        if (n4 == 4) {
            return null;
        }
        final int n5 = this.fElementDeclContentSpecIndex[n2][n3];
        final XMLContentSpec xmlContentSpec = new XMLContentSpec();
        this.getContentSpec(n5, xmlContentSpec);
        ContentModelValidator childModel;
        if (n4 == 2) {
            final ChildrenList list = new ChildrenList();
            this.contentSpecTree(n5, xmlContentSpec, list);
            childModel = new MixedContentModel(list.qname, list.type, 0, list.length, false);
        }
        else {
            if (n4 != 3) {
                throw new RuntimeException("Unknown content type for a element decl in getElementContentModelValidator() in AbstractDTDGrammar class");
            }
            childModel = this.createChildModel(n5);
        }
        return this.fElementDeclContentModelValidator[n2][n3] = childModel;
    }
    
    protected int createElementDecl() {
        final int n = this.fElementDeclCount >> 8;
        final int n2 = this.fElementDeclCount & 0xFF;
        this.ensureElementDeclCapacity(n);
        this.fElementDeclName[n][n2] = new QName();
        this.fElementDeclType[n][n2] = -1;
        this.fElementDeclContentModelValidator[n][n2] = null;
        this.fElementDeclFirstAttributeDeclIndex[n][n2] = -1;
        this.fElementDeclLastAttributeDeclIndex[n][n2] = -1;
        return this.fElementDeclCount++;
    }
    
    protected void setElementDecl(final int n, final XMLElementDecl xmlElementDecl) {
        if (n < 0 || n >= this.fElementDeclCount) {
            return;
        }
        final int n2 = n >> 8;
        final int n3 = n & 0xFF;
        this.fElementDeclName[n2][n3].setValues(xmlElementDecl.name);
        this.fElementDeclType[n2][n3] = xmlElementDecl.type;
        this.fElementDeclContentModelValidator[n2][n3] = xmlElementDecl.contentModelValidator;
        if (xmlElementDecl.simpleType.list) {
            final short[] array = this.fElementDeclType[n2];
            final int n4 = n3;
            array[n4] |= 0x80;
        }
        this.fElementIndexMap.put(xmlElementDecl.name.rawname, n);
    }
    
    protected void putElementNameMapping(final QName qName, final int n, final int n2) {
    }
    
    protected void setFirstAttributeDeclIndex(final int n, final int n2) {
        if (n < 0 || n >= this.fElementDeclCount) {
            return;
        }
        this.fElementDeclFirstAttributeDeclIndex[n >> 8][n & 0xFF] = n2;
    }
    
    protected void setContentSpecIndex(final int n, final int n2) {
        if (n < 0 || n >= this.fElementDeclCount) {
            return;
        }
        this.fElementDeclContentSpecIndex[n >> 8][n & 0xFF] = n2;
    }
    
    protected int createAttributeDecl() {
        final int n = this.fAttributeDeclCount >> 8;
        final int n2 = this.fAttributeDeclCount & 0xFF;
        this.ensureAttributeDeclCapacity(n);
        this.fAttributeDeclName[n][n2] = new QName();
        this.fAttributeDeclType[n][n2] = -1;
        this.fAttributeDeclDatatypeValidator[n][n2] = null;
        this.fAttributeDeclEnumeration[n][n2] = null;
        this.fAttributeDeclDefaultType[n][n2] = 0;
        this.fAttributeDeclDefaultValue[n][n2] = null;
        this.fAttributeDeclNonNormalizedDefaultValue[n][n2] = null;
        this.fAttributeDeclNextAttributeDeclIndex[n][n2] = -1;
        return this.fAttributeDeclCount++;
    }
    
    protected void setAttributeDecl(final int n, final int n2, final XMLAttributeDecl xmlAttributeDecl) {
        final int n3 = n2 >> 8;
        final int n4 = n2 & 0xFF;
        this.fAttributeDeclName[n3][n4].setValues(xmlAttributeDecl.name);
        this.fAttributeDeclType[n3][n4] = xmlAttributeDecl.simpleType.type;
        if (xmlAttributeDecl.simpleType.list) {
            final short[] array = this.fAttributeDeclType[n3];
            final int n5 = n4;
            array[n5] |= 0x80;
        }
        this.fAttributeDeclEnumeration[n3][n4] = xmlAttributeDecl.simpleType.enumeration;
        this.fAttributeDeclDefaultType[n3][n4] = xmlAttributeDecl.simpleType.defaultType;
        this.fAttributeDeclDatatypeValidator[n3][n4] = xmlAttributeDecl.simpleType.datatypeValidator;
        this.fAttributeDeclDefaultValue[n3][n4] = xmlAttributeDecl.simpleType.defaultValue;
        this.fAttributeDeclNonNormalizedDefaultValue[n3][n4] = xmlAttributeDecl.simpleType.nonNormalizedDefaultValue;
        final int n6 = n >> 8;
        final int n7 = n & 0xFF;
        int n8;
        for (n8 = this.fElementDeclFirstAttributeDeclIndex[n6][n7]; n8 != -1 && n8 != n2; n8 = this.fAttributeDeclNextAttributeDeclIndex[n8 >> 8][n8 & 0xFF]) {}
        if (n8 == -1) {
            if (this.fElementDeclFirstAttributeDeclIndex[n6][n7] == -1) {
                this.fElementDeclFirstAttributeDeclIndex[n6][n7] = n2;
            }
            else {
                final int n9 = this.fElementDeclLastAttributeDeclIndex[n6][n7];
                this.fAttributeDeclNextAttributeDeclIndex[n9 >> 8][n9 & 0xFF] = n2;
            }
            this.fElementDeclLastAttributeDeclIndex[n6][n7] = n2;
        }
    }
    
    protected int createContentSpec() {
        final int n = this.fContentSpecCount >> 8;
        final int n2 = this.fContentSpecCount & 0xFF;
        this.ensureContentSpecCapacity(n);
        this.fContentSpecType[n][n2] = -1;
        this.fContentSpecValue[n][n2] = null;
        this.fContentSpecOtherValue[n][n2] = null;
        return this.fContentSpecCount++;
    }
    
    protected void setContentSpec(final int n, final XMLContentSpec xmlContentSpec) {
        final int n2 = n >> 8;
        final int n3 = n & 0xFF;
        this.fContentSpecType[n2][n3] = xmlContentSpec.type;
        this.fContentSpecValue[n2][n3] = xmlContentSpec.value;
        this.fContentSpecOtherValue[n2][n3] = xmlContentSpec.otherValue;
    }
    
    protected int createEntityDecl() {
        final int n = this.fEntityCount >> 8;
        final int n2 = this.fEntityCount & 0xFF;
        this.ensureEntityDeclCapacity(n);
        this.fEntityIsPE[n][n2] = 0;
        this.fEntityInExternal[n][n2] = 0;
        return this.fEntityCount++;
    }
    
    protected void setEntityDecl(final int n, final XMLEntityDecl xmlEntityDecl) {
        final int n2 = n >> 8;
        final int n3 = n & 0xFF;
        this.fEntityName[n2][n3] = xmlEntityDecl.name;
        this.fEntityValue[n2][n3] = xmlEntityDecl.value;
        this.fEntityPublicId[n2][n3] = xmlEntityDecl.publicId;
        this.fEntitySystemId[n2][n3] = xmlEntityDecl.systemId;
        this.fEntityBaseSystemId[n2][n3] = xmlEntityDecl.baseSystemId;
        this.fEntityNotation[n2][n3] = xmlEntityDecl.notation;
        this.fEntityIsPE[n2][n3] = (byte)(xmlEntityDecl.isPE ? 1 : 0);
        this.fEntityInExternal[n2][n3] = (byte)(xmlEntityDecl.inExternal ? 1 : 0);
        this.fEntityIndexMap.put(xmlEntityDecl.name, n);
    }
    
    protected int createNotationDecl() {
        this.ensureNotationDeclCapacity(this.fNotationCount >> 8);
        return this.fNotationCount++;
    }
    
    protected void setNotationDecl(final int n, final XMLNotationDecl xmlNotationDecl) {
        final int n2 = n >> 8;
        final int n3 = n & 0xFF;
        this.fNotationName[n2][n3] = xmlNotationDecl.name;
        this.fNotationPublicId[n2][n3] = xmlNotationDecl.publicId;
        this.fNotationSystemId[n2][n3] = xmlNotationDecl.systemId;
        this.fNotationBaseSystemId[n2][n3] = xmlNotationDecl.baseSystemId;
        this.fNotationIndexMap.put(xmlNotationDecl.name, n);
    }
    
    protected int addContentSpecNode(final short n, final String s) {
        final int contentSpec = this.createContentSpec();
        this.fContentSpec.setValues(n, s, null);
        this.setContentSpec(contentSpec, this.fContentSpec);
        return contentSpec;
    }
    
    protected int addUniqueLeafNode(final String s) {
        final int contentSpec = this.createContentSpec();
        this.fContentSpec.setValues((short)0, s, null);
        this.setContentSpec(contentSpec, this.fContentSpec);
        return contentSpec;
    }
    
    protected int addContentSpecNode(final short n, final int n2, final int n3) {
        final int contentSpec = this.createContentSpec();
        final int[] array = { 0 };
        final int[] array2 = { 0 };
        array[0] = n2;
        array2[0] = n3;
        this.fContentSpec.setValues(n, array, array2);
        this.setContentSpec(contentSpec, this.fContentSpec);
        return contentSpec;
    }
    
    protected void initializeContentModelStack() {
        if (this.fOpStack == null) {
            this.fOpStack = new short[8];
            this.fNodeIndexStack = new int[8];
            this.fPrevNodeIndexStack = new int[8];
        }
        else if (this.fDepth == this.fOpStack.length) {
            final short[] fOpStack = new short[this.fDepth * 2];
            System.arraycopy(this.fOpStack, 0, fOpStack, 0, this.fDepth);
            this.fOpStack = fOpStack;
            final int[] fNodeIndexStack = new int[this.fDepth * 2];
            System.arraycopy(this.fNodeIndexStack, 0, fNodeIndexStack, 0, this.fDepth);
            this.fNodeIndexStack = fNodeIndexStack;
            final int[] fPrevNodeIndexStack = new int[this.fDepth * 2];
            System.arraycopy(this.fPrevNodeIndexStack, 0, fPrevNodeIndexStack, 0, this.fDepth);
            this.fPrevNodeIndexStack = fPrevNodeIndexStack;
        }
        this.fOpStack[this.fDepth] = -1;
        this.fNodeIndexStack[this.fDepth] = -1;
        this.fPrevNodeIndexStack[this.fDepth] = -1;
    }
    
    boolean isImmutable() {
        return this.fIsImmutable;
    }
    
    private void appendContentSpec(final XMLContentSpec xmlContentSpec, final StringBuffer sb, final boolean b, final int n) {
        final int n2 = xmlContentSpec.type & 0xF;
        switch (n2) {
            case 0: {
                if (xmlContentSpec.value == null && xmlContentSpec.otherValue == null) {
                    sb.append("#PCDATA");
                    break;
                }
                if (xmlContentSpec.value == null && xmlContentSpec.otherValue != null) {
                    sb.append("##any:uri=").append(xmlContentSpec.otherValue);
                    break;
                }
                if (xmlContentSpec.value == null) {
                    sb.append("##any");
                    break;
                }
                sb.append(xmlContentSpec.value);
                break;
            }
            case 1: {
                if (n == 3 || n == 2 || n == 1) {
                    this.getContentSpec(((int[])xmlContentSpec.value)[0], xmlContentSpec);
                    sb.append('(');
                    this.appendContentSpec(xmlContentSpec, sb, true, n2);
                    sb.append(')');
                }
                else {
                    this.getContentSpec(((int[])xmlContentSpec.value)[0], xmlContentSpec);
                    this.appendContentSpec(xmlContentSpec, sb, true, n2);
                }
                sb.append('?');
                break;
            }
            case 2: {
                if (n == 3 || n == 2 || n == 1) {
                    this.getContentSpec(((int[])xmlContentSpec.value)[0], xmlContentSpec);
                    sb.append('(');
                    this.appendContentSpec(xmlContentSpec, sb, true, n2);
                    sb.append(')');
                }
                else {
                    this.getContentSpec(((int[])xmlContentSpec.value)[0], xmlContentSpec);
                    this.appendContentSpec(xmlContentSpec, sb, true, n2);
                }
                sb.append('*');
                break;
            }
            case 3: {
                if (n == 3 || n == 2 || n == 1) {
                    sb.append('(');
                    this.getContentSpec(((int[])xmlContentSpec.value)[0], xmlContentSpec);
                    this.appendContentSpec(xmlContentSpec, sb, true, n2);
                    sb.append(')');
                }
                else {
                    this.getContentSpec(((int[])xmlContentSpec.value)[0], xmlContentSpec);
                    this.appendContentSpec(xmlContentSpec, sb, true, n2);
                }
                sb.append('+');
                break;
            }
            case 4:
            case 5: {
                if (b) {
                    sb.append('(');
                }
                final short type = xmlContentSpec.type;
                final int n3 = ((int[])xmlContentSpec.otherValue)[0];
                this.getContentSpec(((int[])xmlContentSpec.value)[0], xmlContentSpec);
                this.appendContentSpec(xmlContentSpec, sb, xmlContentSpec.type != type, n2);
                if (type == 4) {
                    sb.append('|');
                }
                else {
                    sb.append(',');
                }
                this.getContentSpec(n3, xmlContentSpec);
                this.appendContentSpec(xmlContentSpec, sb, true, n2);
                if (b) {
                    sb.append(')');
                    break;
                }
                break;
            }
            case 6: {
                sb.append("##any");
                if (xmlContentSpec.otherValue != null) {
                    sb.append(":uri=");
                    sb.append(xmlContentSpec.otherValue);
                    break;
                }
                break;
            }
            case 7: {
                sb.append("##other:uri=");
                sb.append(xmlContentSpec.otherValue);
                break;
            }
            case 8: {
                sb.append("##local");
                break;
            }
            default: {
                sb.append("???");
                break;
            }
        }
    }
    
    private void printAttribute(final int n) {
        final XMLAttributeDecl xmlAttributeDecl = new XMLAttributeDecl();
        if (this.getAttributeDecl(n, xmlAttributeDecl)) {
            System.out.print(" { ");
            System.out.print(xmlAttributeDecl.name.localpart);
            System.out.print(" }");
        }
    }
    
    private synchronized ContentModelValidator createChildModel(final int n) {
        final XMLContentSpec xmlContentSpec = new XMLContentSpec();
        this.getContentSpec(n, xmlContentSpec);
        if ((xmlContentSpec.type & 0xF) != 0x6 && (xmlContentSpec.type & 0xF) != 0x7) {
            if ((xmlContentSpec.type & 0xF) != 0x8) {
                if (xmlContentSpec.type == 0) {
                    if (xmlContentSpec.value == null && xmlContentSpec.otherValue == null) {
                        throw new RuntimeException("ImplementationMessages.VAL_NPCD");
                    }
                    this.fQName.setValues(null, (String)xmlContentSpec.value, (String)xmlContentSpec.value, (String)xmlContentSpec.otherValue);
                    return new SimpleContentModel(xmlContentSpec.type, this.fQName, null);
                }
                else if (xmlContentSpec.type == 4 || xmlContentSpec.type == 5) {
                    final XMLContentSpec xmlContentSpec2 = new XMLContentSpec();
                    final XMLContentSpec xmlContentSpec3 = new XMLContentSpec();
                    this.getContentSpec(((int[])xmlContentSpec.value)[0], xmlContentSpec2);
                    this.getContentSpec(((int[])xmlContentSpec.otherValue)[0], xmlContentSpec3);
                    if (xmlContentSpec2.type == 0 && xmlContentSpec3.type == 0) {
                        this.fQName.setValues(null, (String)xmlContentSpec2.value, (String)xmlContentSpec2.value, (String)xmlContentSpec2.otherValue);
                        this.fQName2.setValues(null, (String)xmlContentSpec3.value, (String)xmlContentSpec3.value, (String)xmlContentSpec3.otherValue);
                        return new SimpleContentModel(xmlContentSpec.type, this.fQName, this.fQName2);
                    }
                }
                else {
                    if (xmlContentSpec.type != 1 && xmlContentSpec.type != 2 && xmlContentSpec.type != 3) {
                        throw new RuntimeException("ImplementationMessages.VAL_CST");
                    }
                    final XMLContentSpec xmlContentSpec4 = new XMLContentSpec();
                    this.getContentSpec(((int[])xmlContentSpec.value)[0], xmlContentSpec4);
                    if (xmlContentSpec4.type == 0) {
                        this.fQName.setValues(null, (String)xmlContentSpec4.value, (String)xmlContentSpec4.value, (String)xmlContentSpec4.otherValue);
                        return new SimpleContentModel(xmlContentSpec.type, this.fQName, null);
                    }
                }
            }
        }
        this.fLeafCount = 0;
        this.fLeafCount = 0;
        return new DFAContentModel(this.buildSyntaxTree(n, xmlContentSpec), this.fLeafCount, false);
    }
    
    private final CMNode buildSyntaxTree(final int n, final XMLContentSpec xmlContentSpec) {
        this.getContentSpec(n, xmlContentSpec);
        CMNode cmNode;
        if ((xmlContentSpec.type & 0xF) == 0x6) {
            cmNode = new CMAny(xmlContentSpec.type, (String)xmlContentSpec.otherValue, this.fLeafCount++);
        }
        else if ((xmlContentSpec.type & 0xF) == 0x7) {
            cmNode = new CMAny(xmlContentSpec.type, (String)xmlContentSpec.otherValue, this.fLeafCount++);
        }
        else if ((xmlContentSpec.type & 0xF) == 0x8) {
            cmNode = new CMAny(xmlContentSpec.type, null, this.fLeafCount++);
        }
        else if (xmlContentSpec.type == 0) {
            this.fQName.setValues(null, (String)xmlContentSpec.value, (String)xmlContentSpec.value, (String)xmlContentSpec.otherValue);
            cmNode = new CMLeaf(this.fQName, this.fLeafCount++);
        }
        else {
            final int n2 = ((int[])xmlContentSpec.value)[0];
            final int n3 = ((int[])xmlContentSpec.otherValue)[0];
            if (xmlContentSpec.type == 4 || xmlContentSpec.type == 5) {
                cmNode = new CMBinOp(xmlContentSpec.type, this.buildSyntaxTree(n2, xmlContentSpec), this.buildSyntaxTree(n3, xmlContentSpec));
            }
            else if (xmlContentSpec.type == 2) {
                cmNode = new CMUniOp(xmlContentSpec.type, this.buildSyntaxTree(n2, xmlContentSpec));
            }
            else {
                if (xmlContentSpec.type != 2 && xmlContentSpec.type != 1 && xmlContentSpec.type != 3) {
                    throw new RuntimeException("ImplementationMessages.VAL_CST");
                }
                cmNode = new CMUniOp(xmlContentSpec.type, this.buildSyntaxTree(n2, xmlContentSpec));
            }
        }
        return cmNode;
    }
    
    private void contentSpecTree(final int n, final XMLContentSpec xmlContentSpec, final ChildrenList list) {
        this.getContentSpec(n, xmlContentSpec);
        if (xmlContentSpec.type == 0 || (xmlContentSpec.type & 0xF) == 0x6 || (xmlContentSpec.type & 0xF) == 0x8 || (xmlContentSpec.type & 0xF) == 0x7) {
            if (list.length == list.qname.length) {
                final QName[] qname = new QName[list.length * 2];
                System.arraycopy(list.qname, 0, qname, 0, list.length);
                list.qname = qname;
                final int[] type = new int[list.length * 2];
                System.arraycopy(list.type, 0, type, 0, list.length);
                list.type = type;
            }
            list.qname[list.length] = new QName(null, (String)xmlContentSpec.value, (String)xmlContentSpec.value, (String)xmlContentSpec.otherValue);
            list.type[list.length] = xmlContentSpec.type;
            ++list.length;
            return;
        }
        final int n2 = (xmlContentSpec.value != null) ? ((int[])xmlContentSpec.value)[0] : -1;
        if (xmlContentSpec.otherValue == null) {
            return;
        }
        final int n3 = ((int[])xmlContentSpec.otherValue)[0];
        if (xmlContentSpec.type == 4 || xmlContentSpec.type == 5) {
            this.contentSpecTree(n2, xmlContentSpec, list);
            this.contentSpecTree(n3, xmlContentSpec, list);
            return;
        }
        if (xmlContentSpec.type == 1 || xmlContentSpec.type == 2 || xmlContentSpec.type == 3) {
            this.contentSpecTree(n2, xmlContentSpec, list);
            return;
        }
        throw new RuntimeException("Invalid content spec type seen in contentSpecTree() method of AbstractDTDGrammar class : " + xmlContentSpec.type);
    }
    
    private void ensureElementDeclCapacity(final int n) {
        if (n >= this.fElementDeclName.length) {
            this.fElementDeclIsExternal = resize(this.fElementDeclIsExternal, this.fElementDeclIsExternal.length * 2);
            this.fElementDeclName = resize(this.fElementDeclName, this.fElementDeclName.length * 2);
            this.fElementDeclType = resize(this.fElementDeclType, this.fElementDeclType.length * 2);
            this.fElementDeclContentModelValidator = resize(this.fElementDeclContentModelValidator, this.fElementDeclContentModelValidator.length * 2);
            this.fElementDeclContentSpecIndex = resize(this.fElementDeclContentSpecIndex, this.fElementDeclContentSpecIndex.length * 2);
            this.fElementDeclFirstAttributeDeclIndex = resize(this.fElementDeclFirstAttributeDeclIndex, this.fElementDeclFirstAttributeDeclIndex.length * 2);
            this.fElementDeclLastAttributeDeclIndex = resize(this.fElementDeclLastAttributeDeclIndex, this.fElementDeclLastAttributeDeclIndex.length * 2);
        }
        else if (this.fElementDeclName[n] != null) {
            return;
        }
        this.fElementDeclIsExternal[n] = new int[256];
        this.fElementDeclName[n] = new QName[256];
        this.fElementDeclType[n] = new short[256];
        this.fElementDeclContentModelValidator[n] = new ContentModelValidator[256];
        this.fElementDeclContentSpecIndex[n] = new int[256];
        this.fElementDeclFirstAttributeDeclIndex[n] = new int[256];
        this.fElementDeclLastAttributeDeclIndex[n] = new int[256];
    }
    
    private void ensureAttributeDeclCapacity(final int n) {
        if (n >= this.fAttributeDeclName.length) {
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
        else if (this.fAttributeDeclName[n] != null) {
            return;
        }
        this.fAttributeDeclIsExternal[n] = new int[256];
        this.fAttributeDeclName[n] = new QName[256];
        this.fAttributeDeclType[n] = new short[256];
        this.fAttributeDeclEnumeration[n] = new String[256][];
        this.fAttributeDeclDefaultType[n] = new short[256];
        this.fAttributeDeclDatatypeValidator[n] = new DatatypeValidator[256];
        this.fAttributeDeclDefaultValue[n] = new String[256];
        this.fAttributeDeclNonNormalizedDefaultValue[n] = new String[256];
        this.fAttributeDeclNextAttributeDeclIndex[n] = new int[256];
    }
    
    private void ensureEntityDeclCapacity(final int n) {
        if (n >= this.fEntityName.length) {
            this.fEntityName = resize(this.fEntityName, this.fEntityName.length * 2);
            this.fEntityValue = resize(this.fEntityValue, this.fEntityValue.length * 2);
            this.fEntityPublicId = resize(this.fEntityPublicId, this.fEntityPublicId.length * 2);
            this.fEntitySystemId = resize(this.fEntitySystemId, this.fEntitySystemId.length * 2);
            this.fEntityBaseSystemId = resize(this.fEntityBaseSystemId, this.fEntityBaseSystemId.length * 2);
            this.fEntityNotation = resize(this.fEntityNotation, this.fEntityNotation.length * 2);
            this.fEntityIsPE = resize(this.fEntityIsPE, this.fEntityIsPE.length * 2);
            this.fEntityInExternal = resize(this.fEntityInExternal, this.fEntityInExternal.length * 2);
        }
        else if (this.fEntityName[n] != null) {
            return;
        }
        this.fEntityName[n] = new String[256];
        this.fEntityValue[n] = new String[256];
        this.fEntityPublicId[n] = new String[256];
        this.fEntitySystemId[n] = new String[256];
        this.fEntityBaseSystemId[n] = new String[256];
        this.fEntityNotation[n] = new String[256];
        this.fEntityIsPE[n] = new byte[256];
        this.fEntityInExternal[n] = new byte[256];
    }
    
    private void ensureNotationDeclCapacity(final int n) {
        if (n >= this.fNotationName.length) {
            this.fNotationName = resize(this.fNotationName, this.fNotationName.length * 2);
            this.fNotationPublicId = resize(this.fNotationPublicId, this.fNotationPublicId.length * 2);
            this.fNotationSystemId = resize(this.fNotationSystemId, this.fNotationSystemId.length * 2);
            this.fNotationBaseSystemId = resize(this.fNotationBaseSystemId, this.fNotationBaseSystemId.length * 2);
        }
        else if (this.fNotationName[n] != null) {
            return;
        }
        this.fNotationName[n] = new String[256];
        this.fNotationPublicId[n] = new String[256];
        this.fNotationSystemId[n] = new String[256];
        this.fNotationBaseSystemId[n] = new String[256];
    }
    
    private void ensureContentSpecCapacity(final int n) {
        if (n >= this.fContentSpecType.length) {
            this.fContentSpecType = resize(this.fContentSpecType, this.fContentSpecType.length * 2);
            this.fContentSpecValue = resize(this.fContentSpecValue, this.fContentSpecValue.length * 2);
            this.fContentSpecOtherValue = resize(this.fContentSpecOtherValue, this.fContentSpecOtherValue.length * 2);
        }
        else if (this.fContentSpecType[n] != null) {
            return;
        }
        this.fContentSpecType[n] = new short[256];
        this.fContentSpecValue[n] = new Object[256];
        this.fContentSpecOtherValue[n] = new Object[256];
    }
    
    private static byte[][] resize(final byte[][] array, final int n) {
        final byte[][] array2 = new byte[n][];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    private static short[][] resize(final short[][] array, final int n) {
        final short[][] array2 = new short[n][];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    private static int[][] resize(final int[][] array, final int n) {
        final int[][] array2 = new int[n][];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    private static DatatypeValidator[][] resize(final DatatypeValidator[][] array, final int n) {
        final DatatypeValidator[][] array2 = new DatatypeValidator[n][];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    private static ContentModelValidator[][] resize(final ContentModelValidator[][] array, final int n) {
        final ContentModelValidator[][] array2 = new ContentModelValidator[n][];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    private static Object[][] resize(final Object[][] array, final int n) {
        final Object[][] array2 = new Object[n][];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    private static QName[][] resize(final QName[][] array, final int n) {
        final QName[][] array2 = new QName[n][];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    private static String[][] resize(final String[][] array, final int n) {
        final String[][] array2 = new String[n][];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    private static String[][][] resize(final String[][][] array, final int n) {
        final String[][][] array2 = new String[n][][];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    public boolean isEntityDeclared(final String s) {
        return this.getEntityDeclIndex(s) != -1;
    }
    
    public boolean isEntityUnparsed(final String s) {
        final int entityDeclIndex = this.getEntityDeclIndex(s);
        return entityDeclIndex > -1 && this.fEntityNotation[entityDeclIndex >> 8][entityDeclIndex & 0xFF] != null;
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
    
    protected static final class QNameHashtable
    {
        private static final int INITIAL_BUCKET_SIZE = 4;
        private static final int HASHTABLE_SIZE = 101;
        private static final int MAX_HASH_COLLISIONS = 40;
        private static final int MULTIPLIERS_SIZE = 32;
        private static final int MULTIPLIERS_MASK = 31;
        private Object[][] fHashTable;
        private int fTableSize;
        private int fCount;
        private int[] fHashMultipliers;
        
        protected QNameHashtable() {
            this.fHashTable = new Object[101][];
            this.fTableSize = 101;
            this.fCount = 0;
        }
        
        public void put(final String s, final int n) {
            final int n2 = (this.hash(s) & Integer.MAX_VALUE) % this.fTableSize;
            Object[] array = this.fHashTable[n2];
            if (array == null) {
                final Object[] array2 = new Object[9];
                array2[0] = new int[] { 1 };
                array2[1] = s;
                array2[2] = new int[] { n };
                this.fHashTable[n2] = array2;
                if (++this.fCount > this.fTableSize) {
                    this.rehash();
                }
            }
            else {
                int n3 = ((int[])array[0])[0];
                int n4 = 1 + 2 * n3;
                if (n4 == array.length) {
                    final Object[] array3 = new Object[1 + 2 * (n3 + 4)];
                    System.arraycopy(array, 0, array3, 0, n4);
                    array = array3;
                    this.fHashTable[n2] = array;
                }
                boolean b = false;
                int n5 = 1;
                for (int i = 0; i < n3; ++i) {
                    if (array[n5] == s) {
                        ((int[])array[n5 + 1])[0] = n;
                        b = true;
                        break;
                    }
                    n5 += 2;
                }
                if (!b) {
                    array[n4++] = s;
                    array[n4] = new int[] { n };
                    ((int[])array[0])[0] = ++n3;
                    if (++this.fCount > this.fTableSize) {
                        this.rehash();
                    }
                    else if (n3 > 40) {
                        this.rebalance();
                    }
                }
            }
        }
        
        public int get(final String s) {
            final Object[] array = this.fHashTable[(this.hash(s) & Integer.MAX_VALUE) % this.fTableSize];
            if (array == null) {
                return -1;
            }
            final int n = ((int[])array[0])[0];
            int n2 = 1;
            for (int i = 0; i < n; ++i) {
                if (array[n2] == s) {
                    return ((int[])array[n2 + 1])[0];
                }
                n2 += 2;
            }
            return -1;
        }
        
        public int hash(final String s) {
            if (this.fHashMultipliers == null) {
                return s.hashCode();
            }
            return this.hash0(s);
        }
        
        private int hash0(final String s) {
            int n = 0;
            final int length = s.length();
            final int[] fHashMultipliers = this.fHashMultipliers;
            for (int i = 0; i < length; ++i) {
                n = n * fHashMultipliers[i & 0x1F] + s.charAt(i);
            }
            return n;
        }
        
        private void rehash() {
            this.rehashCommon(this.fHashTable.length * 2 + 1);
        }
        
        private void rebalance() {
            if (this.fHashMultipliers == null) {
                this.fHashMultipliers = new int[32];
            }
            PrimeNumberSequenceGenerator.generateSequence(this.fHashMultipliers);
            this.rehashCommon(this.fHashTable.length);
        }
        
        private void rehashCommon(final int n) {
            final int length = this.fHashTable.length;
            final Object[][] fHashTable = this.fHashTable;
            this.fHashTable = new Object[n][];
            this.fTableSize = this.fHashTable.length;
            for (final Object[] array : fHashTable) {
                if (array != null) {
                    final int n2 = ((int[])array[0])[0];
                    int n3 = 0;
                    int n4 = 1;
                    for (int j = 0; j < n2; ++j) {
                        final String s = (String)array[n4];
                        final Object o = array[n4 + 1];
                        final int n5 = (this.hash(s) & Integer.MAX_VALUE) % this.fTableSize;
                        Object[] array2 = this.fHashTable[n5];
                        if (array2 == null) {
                            Object[] array3;
                            if (n3 != 0) {
                                array3 = new Object[9];
                                array3[0] = new int[] { 1 };
                            }
                            else {
                                array3 = array;
                                ((int[])array3[0])[0] = 1;
                                n3 = 1;
                            }
                            array3[1] = s;
                            array3[2] = o;
                            this.fHashTable[n5] = array3;
                        }
                        else {
                            int n6 = ((int[])array2[0])[0];
                            int n7 = 1 + 2 * n6;
                            if (n7 == array2.length) {
                                final Object[] array4 = new Object[1 + 2 * (n6 + 4)];
                                System.arraycopy(array2, 0, array4, 0, n7);
                                array2 = array4;
                                this.fHashTable[n5] = array2;
                            }
                            array2[n7++] = s;
                            array2[n7] = o;
                            ((int[])array2[0])[0] = ++n6;
                        }
                        n4 += 2;
                    }
                }
            }
        }
        
        private static final class PrimeNumberSequenceGenerator
        {
            private static int[] PRIMES;
            
            static void generateSequence(final int[] array) {
                final Random random = new Random();
                for (int i = 0; i < array.length; ++i) {
                    array[i] = PrimeNumberSequenceGenerator.PRIMES[random.nextInt(PrimeNumberSequenceGenerator.PRIMES.length)];
                }
            }
            
            static {
                PrimeNumberSequenceGenerator.PRIMES = new int[] { 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641, 643, 647, 653, 659, 661, 673, 677, 683, 691, 701, 709, 719, 727 };
            }
        }
    }
}
