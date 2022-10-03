package com.sun.xml.internal.stream.dtd;

import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.xml.internal.stream.dtd.nonvalidating.XMLAttributeDecl;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.xml.internal.stream.dtd.nonvalidating.DTDGrammar;

public class DTDGrammarUtil
{
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    private static final boolean DEBUG_ATTRIBUTES = false;
    private static final boolean DEBUG_ELEMENT_CHILDREN = false;
    protected DTDGrammar fDTDGrammar;
    protected boolean fNamespaces;
    protected SymbolTable fSymbolTable;
    private int fCurrentElementIndex;
    private int fCurrentContentSpecType;
    private boolean[] fElementContentState;
    private int fElementDepth;
    private boolean fInElementContent;
    private XMLAttributeDecl fTempAttDecl;
    private QName fTempQName;
    private StringBuffer fBuffer;
    private NamespaceContext fNamespaceContext;
    
    public DTDGrammarUtil(final SymbolTable symbolTable) {
        this.fDTDGrammar = null;
        this.fSymbolTable = null;
        this.fCurrentElementIndex = -1;
        this.fCurrentContentSpecType = -1;
        this.fElementContentState = new boolean[8];
        this.fElementDepth = -1;
        this.fInElementContent = false;
        this.fTempAttDecl = new XMLAttributeDecl();
        this.fTempQName = new QName();
        this.fBuffer = new StringBuffer();
        this.fNamespaceContext = null;
        this.fSymbolTable = symbolTable;
    }
    
    public DTDGrammarUtil(final DTDGrammar grammar, final SymbolTable symbolTable) {
        this.fDTDGrammar = null;
        this.fSymbolTable = null;
        this.fCurrentElementIndex = -1;
        this.fCurrentContentSpecType = -1;
        this.fElementContentState = new boolean[8];
        this.fElementDepth = -1;
        this.fInElementContent = false;
        this.fTempAttDecl = new XMLAttributeDecl();
        this.fTempQName = new QName();
        this.fBuffer = new StringBuffer();
        this.fNamespaceContext = null;
        this.fDTDGrammar = grammar;
        this.fSymbolTable = symbolTable;
    }
    
    public DTDGrammarUtil(final DTDGrammar grammar, final SymbolTable symbolTable, final NamespaceContext namespaceContext) {
        this.fDTDGrammar = null;
        this.fSymbolTable = null;
        this.fCurrentElementIndex = -1;
        this.fCurrentContentSpecType = -1;
        this.fElementContentState = new boolean[8];
        this.fElementDepth = -1;
        this.fInElementContent = false;
        this.fTempAttDecl = new XMLAttributeDecl();
        this.fTempQName = new QName();
        this.fBuffer = new StringBuffer();
        this.fNamespaceContext = null;
        this.fDTDGrammar = grammar;
        this.fSymbolTable = symbolTable;
        this.fNamespaceContext = namespaceContext;
    }
    
    public void reset(final XMLComponentManager componentManager) throws XMLConfigurationException {
        this.fDTDGrammar = null;
        this.fInElementContent = false;
        this.fCurrentElementIndex = -1;
        this.fCurrentContentSpecType = -1;
        this.fNamespaces = componentManager.getFeature("http://xml.org/sax/features/namespaces", true);
        this.fSymbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fElementDepth = -1;
    }
    
    public void startElement(final QName element, final XMLAttributes attributes) throws XNIException {
        this.handleStartElement(element, attributes);
    }
    
    public void endElement(final QName element) throws XNIException {
        this.handleEndElement(element);
    }
    
    public void startCDATA(final Augmentations augs) throws XNIException {
    }
    
    public void endCDATA(final Augmentations augs) throws XNIException {
    }
    
    public void addDTDDefaultAttrs(final QName elementName, final XMLAttributes attributes) throws XNIException {
        final int elementIndex = this.fDTDGrammar.getElementDeclIndex(elementName);
        if (elementIndex == -1 || this.fDTDGrammar == null) {
            return;
        }
        for (int attlistIndex = this.fDTDGrammar.getFirstAttributeDeclIndex(elementIndex); attlistIndex != -1; attlistIndex = this.fDTDGrammar.getNextAttributeDeclIndex(attlistIndex)) {
            this.fDTDGrammar.getAttributeDecl(attlistIndex, this.fTempAttDecl);
            String attPrefix = this.fTempAttDecl.name.prefix;
            String attLocalpart = this.fTempAttDecl.name.localpart;
            final String attRawName = this.fTempAttDecl.name.rawname;
            final String attType = this.getAttributeTypeName(this.fTempAttDecl);
            final int attDefaultType = this.fTempAttDecl.simpleType.defaultType;
            String attValue = null;
            if (this.fTempAttDecl.simpleType.defaultValue != null) {
                attValue = this.fTempAttDecl.simpleType.defaultValue;
            }
            boolean specified = false;
            final boolean required = attDefaultType == 2;
            final boolean cdata = attType == XMLSymbols.fCDATASymbol;
            if (!cdata || required || attValue != null) {
                if (this.fNamespaceContext != null && attRawName.startsWith("xmlns")) {
                    String prefix = "";
                    final int pos = attRawName.indexOf(58);
                    if (pos != -1) {
                        prefix = attRawName.substring(0, pos);
                    }
                    else {
                        prefix = attRawName;
                    }
                    prefix = this.fSymbolTable.addSymbol(prefix);
                    if (!((NamespaceSupport)this.fNamespaceContext).containsPrefixInCurrentContext(prefix)) {
                        this.fNamespaceContext.declarePrefix(prefix, attValue);
                    }
                    specified = true;
                }
                else {
                    for (int attrCount = attributes.getLength(), i = 0; i < attrCount; ++i) {
                        if (attributes.getQName(i) == attRawName) {
                            specified = true;
                            break;
                        }
                    }
                }
            }
            if (!specified && attValue != null) {
                if (this.fNamespaces) {
                    final int index = attRawName.indexOf(58);
                    if (index != -1) {
                        attPrefix = attRawName.substring(0, index);
                        attPrefix = this.fSymbolTable.addSymbol(attPrefix);
                        attLocalpart = attRawName.substring(index + 1);
                        attLocalpart = this.fSymbolTable.addSymbol(attLocalpart);
                    }
                }
                this.fTempQName.setValues(attPrefix, attLocalpart, attRawName, this.fTempAttDecl.name.uri);
                attributes.addAttribute(this.fTempQName, attType, attValue);
            }
        }
        for (int attrCount2 = attributes.getLength(), j = 0; j < attrCount2; ++j) {
            final String attrRawName = attributes.getQName(j);
            boolean declared = false;
            for (int position = this.fDTDGrammar.getFirstAttributeDeclIndex(elementIndex); position != -1; position = this.fDTDGrammar.getNextAttributeDeclIndex(position)) {
                this.fDTDGrammar.getAttributeDecl(position, this.fTempAttDecl);
                if (this.fTempAttDecl.name.rawname == attrRawName) {
                    declared = true;
                    break;
                }
            }
            if (declared) {
                final String type = this.getAttributeTypeName(this.fTempAttDecl);
                attributes.setType(j, type);
                boolean changedByNormalization = false;
                if (attributes.isSpecified(j) && type != XMLSymbols.fCDATASymbol) {
                    changedByNormalization = this.normalizeAttrValue(attributes, j);
                }
            }
        }
    }
    
    private boolean normalizeAttrValue(final XMLAttributes attributes, final int index) {
        boolean leadingSpace = true;
        boolean spaceStart = false;
        boolean readingNonSpace = false;
        int count = 0;
        int eaten = 0;
        final String attrValue = attributes.getValue(index);
        final char[] attValue = new char[attrValue.length()];
        this.fBuffer.setLength(0);
        attrValue.getChars(0, attrValue.length(), attValue, 0);
        for (int i = 0; i < attValue.length; ++i) {
            if (attValue[i] == ' ') {
                if (readingNonSpace) {
                    spaceStart = true;
                    readingNonSpace = false;
                }
                if (spaceStart && !leadingSpace) {
                    spaceStart = false;
                    this.fBuffer.append(attValue[i]);
                    ++count;
                }
                else if (leadingSpace || !spaceStart) {
                    ++eaten;
                }
            }
            else {
                readingNonSpace = true;
                spaceStart = false;
                leadingSpace = false;
                this.fBuffer.append(attValue[i]);
                ++count;
            }
        }
        if (count > 0 && this.fBuffer.charAt(count - 1) == ' ') {
            this.fBuffer.setLength(count - 1);
        }
        final String newValue = this.fBuffer.toString();
        attributes.setValue(index, newValue);
        return !attrValue.equals(newValue);
    }
    
    private String getAttributeTypeName(final XMLAttributeDecl attrDecl) {
        switch (attrDecl.simpleType.type) {
            case 1: {
                return attrDecl.simpleType.list ? XMLSymbols.fENTITIESSymbol : XMLSymbols.fENTITYSymbol;
            }
            case 2: {
                final StringBuffer buffer = new StringBuffer();
                buffer.append('(');
                for (int i = 0; i < attrDecl.simpleType.enumeration.length; ++i) {
                    if (i > 0) {
                        buffer.append("|");
                    }
                    buffer.append(attrDecl.simpleType.enumeration[i]);
                }
                buffer.append(')');
                return this.fSymbolTable.addSymbol(buffer.toString());
            }
            case 3: {
                return XMLSymbols.fIDSymbol;
            }
            case 4: {
                return attrDecl.simpleType.list ? XMLSymbols.fIDREFSSymbol : XMLSymbols.fIDREFSymbol;
            }
            case 5: {
                return attrDecl.simpleType.list ? XMLSymbols.fNMTOKENSSymbol : XMLSymbols.fNMTOKENSymbol;
            }
            case 6: {
                return XMLSymbols.fNOTATIONSymbol;
            }
            default: {
                return XMLSymbols.fCDATASymbol;
            }
        }
    }
    
    private void ensureStackCapacity(final int newElementDepth) {
        if (newElementDepth == this.fElementContentState.length) {
            final boolean[] newStack = new boolean[newElementDepth * 2];
            System.arraycopy(this.fElementContentState, 0, newStack, 0, newElementDepth);
            this.fElementContentState = newStack;
        }
    }
    
    protected void handleStartElement(final QName element, final XMLAttributes attributes) throws XNIException {
        if (this.fDTDGrammar == null) {
            this.fCurrentElementIndex = -1;
            this.fCurrentContentSpecType = -1;
            this.fInElementContent = false;
            return;
        }
        this.fCurrentElementIndex = this.fDTDGrammar.getElementDeclIndex(element);
        this.fCurrentContentSpecType = this.fDTDGrammar.getContentSpecType(this.fCurrentElementIndex);
        this.addDTDDefaultAttrs(element, attributes);
        this.fInElementContent = (this.fCurrentContentSpecType == 3);
        this.ensureStackCapacity(++this.fElementDepth);
        this.fElementContentState[this.fElementDepth] = this.fInElementContent;
    }
    
    protected void handleEndElement(final QName element) throws XNIException {
        if (this.fDTDGrammar == null) {
            return;
        }
        --this.fElementDepth;
        if (this.fElementDepth < -1) {
            throw new RuntimeException("FWK008 Element stack underflow");
        }
        if (this.fElementDepth < 0) {
            this.fCurrentElementIndex = -1;
            this.fCurrentContentSpecType = -1;
            this.fInElementContent = false;
            return;
        }
        this.fInElementContent = this.fElementContentState[this.fElementDepth];
    }
    
    public boolean isInElementContent() {
        return this.fInElementContent;
    }
    
    public boolean isIgnorableWhiteSpace(final XMLString text) {
        if (this.isInElementContent()) {
            for (int i = text.offset; i < text.offset + text.length; ++i) {
                if (!XMLChar.isSpace(text.ch[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
