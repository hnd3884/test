package com.sun.xml.internal.fastinfoset.dom;

import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithm;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.internal.fastinfoset.util.CharArrayString;
import org.w3c.dom.Text;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.CharArray;
import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.DecoderStateTables;
import java.io.IOException;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import java.io.InputStream;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import com.sun.xml.internal.fastinfoset.Decoder;

public class DOMDocumentParser extends Decoder
{
    protected Document _document;
    protected Node _currentNode;
    protected Element _currentElement;
    protected Attr[] _namespaceAttributes;
    protected int _namespaceAttributesIndex;
    protected int[] _namespacePrefixes;
    protected int _namespacePrefixesIndex;
    
    public DOMDocumentParser() {
        this._namespaceAttributes = new Attr[16];
        this._namespacePrefixes = new int[16];
    }
    
    public void parse(final Document d, final InputStream s) throws FastInfosetException, IOException {
        this._document = d;
        this._currentNode = d;
        this._namespaceAttributesIndex = 0;
        this.parse(s);
    }
    
    protected final void parse(final InputStream s) throws FastInfosetException, IOException {
        this.setInputStream(s);
        this.parse();
    }
    
    protected void resetOnError() {
        this._namespacePrefixesIndex = 0;
        if (this._v == null) {
            this._prefixTable.clearCompletely();
        }
        this._duplicateAttributeVerifier.clear();
    }
    
    protected final void parse() throws FastInfosetException, IOException {
        try {
            this.reset();
            this.decodeHeader();
            this.processDII();
        }
        catch (final RuntimeException e) {
            this.resetOnError();
            throw new FastInfosetException(e);
        }
        catch (final FastInfosetException e2) {
            this.resetOnError();
            throw e2;
        }
        catch (final IOException e3) {
            this.resetOnError();
            throw e3;
        }
    }
    
    protected final void processDII() throws FastInfosetException, IOException {
        this._b = this.read();
        if (this._b > 0) {
            this.processDIIOptionalProperties();
        }
        boolean firstElementHasOccured = false;
        boolean documentTypeDeclarationOccured = false;
        while (!this._terminate || !firstElementHasOccured) {
            this._b = this.read();
            switch (DecoderStateTables.DII(this._b)) {
                case 0: {
                    this.processEII(this._elementNameTable._array[this._b], false);
                    firstElementHasOccured = true;
                    continue;
                }
                case 1: {
                    this.processEII(this._elementNameTable._array[this._b & 0x1F], true);
                    firstElementHasOccured = true;
                    continue;
                }
                case 2: {
                    this.processEII(this.decodeEIIIndexMedium(), (this._b & 0x40) > 0);
                    firstElementHasOccured = true;
                    continue;
                }
                case 3: {
                    this.processEII(this.decodeEIIIndexLarge(), (this._b & 0x40) > 0);
                    firstElementHasOccured = true;
                    continue;
                }
                case 5: {
                    final QualifiedName qn = this.processLiteralQualifiedName(this._b & 0x3, this._elementNameTable.getNext());
                    this._elementNameTable.add(qn);
                    this.processEII(qn, (this._b & 0x40) > 0);
                    firstElementHasOccured = true;
                    continue;
                }
                case 4: {
                    this.processEIIWithNamespaces();
                    firstElementHasOccured = true;
                    continue;
                }
                case 20: {
                    if (documentTypeDeclarationOccured) {
                        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.secondOccurenceOfDTDII"));
                    }
                    documentTypeDeclarationOccured = true;
                    final String system_identifier = ((this._b & 0x2) > 0) ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : null;
                    final String public_identifier = ((this._b & 0x1) > 0) ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : null;
                    this._b = this.read();
                    while (this._b == 225) {
                        switch (this.decodeNonIdentifyingStringOnFirstBit()) {
                            case 0: {
                                if (this._addToTable) {
                                    this._v.otherString.add(new CharArray(this._charBuffer, 0, this._charBufferLength, true));
                                    break;
                                }
                                break;
                            }
                            case 2: {
                                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.processingIIWithEncodingAlgorithm"));
                            }
                        }
                        this._b = this.read();
                    }
                    if ((this._b & 0xF0) != 0xF0) {
                        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.processingInstructionIIsNotTerminatedCorrectly"));
                    }
                    if (this._b == 255) {
                        this._terminate = true;
                    }
                    this._notations.clear();
                    this._unparsedEntities.clear();
                    continue;
                }
                case 18: {
                    this.processCommentII();
                    continue;
                }
                case 19: {
                    this.processProcessingII();
                    continue;
                }
                case 23: {
                    this._doubleTerminate = true;
                }
                case 22: {
                    this._terminate = true;
                    continue;
                }
                default: {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingDII"));
                }
            }
        }
        while (!this._terminate) {
            this._b = this.read();
            switch (DecoderStateTables.DII(this._b)) {
                case 18: {
                    this.processCommentII();
                    continue;
                }
                case 19: {
                    this.processProcessingII();
                    continue;
                }
                case 23: {
                    this._doubleTerminate = true;
                }
                case 22: {
                    this._terminate = true;
                    continue;
                }
                default: {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingDII"));
                }
            }
        }
    }
    
    protected final void processDIIOptionalProperties() throws FastInfosetException, IOException {
        if (this._b == 32) {
            this.decodeInitialVocabulary();
            return;
        }
        if ((this._b & 0x40) > 0) {
            this.decodeAdditionalData();
        }
        if ((this._b & 0x20) > 0) {
            this.decodeInitialVocabulary();
        }
        if ((this._b & 0x10) > 0) {
            this.decodeNotations();
        }
        if ((this._b & 0x8) > 0) {
            this.decodeUnparsedEntities();
        }
        if ((this._b & 0x4) > 0) {
            this.decodeCharacterEncodingScheme();
        }
        if ((this._b & 0x2) > 0) {
            this.read();
        }
        if ((this._b & 0x1) > 0) {
            this.decodeVersion();
        }
    }
    
    protected final void processEII(final QualifiedName name, final boolean hasAttributes) throws FastInfosetException, IOException {
        if (this._prefixTable._currentInScope[name.prefixIndex] != name.namespaceNameIndex) {
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qnameOfEIINotInScope"));
        }
        final Node parentCurrentNode = this._currentNode;
        final Element element = this.createElement(name.namespaceName, name.qName, name.localName);
        this._currentElement = element;
        this._currentNode = element;
        if (this._namespaceAttributesIndex > 0) {
            for (int i = 0; i < this._namespaceAttributesIndex; ++i) {
                this._currentElement.setAttributeNode(this._namespaceAttributes[i]);
                this._namespaceAttributes[i] = null;
            }
            this._namespaceAttributesIndex = 0;
        }
        if (hasAttributes) {
            this.processAIIs();
        }
        parentCurrentNode.appendChild(this._currentElement);
        while (!this._terminate) {
            this._b = this.read();
            switch (DecoderStateTables.EII(this._b)) {
                case 0: {
                    this.processEII(this._elementNameTable._array[this._b], false);
                    continue;
                }
                case 1: {
                    this.processEII(this._elementNameTable._array[this._b & 0x1F], true);
                    continue;
                }
                case 2: {
                    this.processEII(this.decodeEIIIndexMedium(), (this._b & 0x40) > 0);
                    continue;
                }
                case 3: {
                    this.processEII(this.decodeEIIIndexLarge(), (this._b & 0x40) > 0);
                    continue;
                }
                case 5: {
                    final QualifiedName qn = this.processLiteralQualifiedName(this._b & 0x3, this._elementNameTable.getNext());
                    this._elementNameTable.add(qn);
                    this.processEII(qn, (this._b & 0x40) > 0);
                    continue;
                }
                case 4: {
                    this.processEIIWithNamespaces();
                    continue;
                }
                case 6: {
                    this._octetBufferLength = (this._b & 0x1) + 1;
                    this.appendOrCreateTextData(this.processUtf8CharacterString());
                    continue;
                }
                case 7: {
                    this._octetBufferLength = this.read() + 3;
                    this.appendOrCreateTextData(this.processUtf8CharacterString());
                    continue;
                }
                case 8: {
                    this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read());
                    this._octetBufferLength += 259;
                    this.appendOrCreateTextData(this.processUtf8CharacterString());
                    continue;
                }
                case 9: {
                    this._octetBufferLength = (this._b & 0x1) + 1;
                    final String v = this.decodeUtf16StringAsString();
                    if ((this._b & 0x10) > 0) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    this.appendOrCreateTextData(v);
                    continue;
                }
                case 10: {
                    this._octetBufferLength = this.read() + 3;
                    final String v = this.decodeUtf16StringAsString();
                    if ((this._b & 0x10) > 0) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    this.appendOrCreateTextData(v);
                    continue;
                }
                case 11: {
                    this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read());
                    this._octetBufferLength += 259;
                    final String v = this.decodeUtf16StringAsString();
                    if ((this._b & 0x10) > 0) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    this.appendOrCreateTextData(v);
                    continue;
                }
                case 12: {
                    final boolean addToTable = (this._b & 0x10) > 0;
                    this._identifier = (this._b & 0x2) << 6;
                    this._b = this.read();
                    this._identifier |= (this._b & 0xFC) >> 2;
                    this.decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
                    final String v2 = this.decodeRestrictedAlphabetAsString();
                    if (addToTable) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    this.appendOrCreateTextData(v2);
                    continue;
                }
                case 13: {
                    final boolean addToTable = (this._b & 0x10) > 0;
                    this._identifier = (this._b & 0x2) << 6;
                    this._b = this.read();
                    this._identifier |= (this._b & 0xFC) >> 2;
                    this.decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
                    final String s = this.convertEncodingAlgorithmDataToCharacters(false);
                    if (addToTable) {
                        this._characterContentChunkTable.add(s.toCharArray(), s.length());
                    }
                    this.appendOrCreateTextData(s);
                    continue;
                }
                case 14: {
                    final String s2 = this._characterContentChunkTable.getString(this._b & 0xF);
                    this.appendOrCreateTextData(s2);
                    continue;
                }
                case 15: {
                    final int index = ((this._b & 0x3) << 8 | this.read()) + 16;
                    final String s = this._characterContentChunkTable.getString(index);
                    this.appendOrCreateTextData(s);
                    continue;
                }
                case 16: {
                    int index = (this._b & 0x3) << 16 | this.read() << 8 | this.read();
                    index += 1040;
                    final String s = this._characterContentChunkTable.getString(index);
                    this.appendOrCreateTextData(s);
                    continue;
                }
                case 17: {
                    int index = this.read() << 16 | this.read() << 8 | this.read();
                    index += 263184;
                    final String s = this._characterContentChunkTable.getString(index);
                    this.appendOrCreateTextData(s);
                    continue;
                }
                case 18: {
                    this.processCommentII();
                    continue;
                }
                case 19: {
                    this.processProcessingII();
                    continue;
                }
                case 21: {
                    final String entity_reference_name = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
                    final String system_identifier = ((this._b & 0x2) > 0) ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : null;
                    final String public_identifier = ((this._b & 0x1) > 0) ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : null;
                    continue;
                }
                case 23: {
                    this._doubleTerminate = true;
                }
                case 22: {
                    this._terminate = true;
                    continue;
                }
                default: {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEII"));
                }
            }
        }
        this._terminate = this._doubleTerminate;
        this._doubleTerminate = false;
        this._currentNode = parentCurrentNode;
    }
    
    private void appendOrCreateTextData(final String textData) {
        final Node lastChild = this._currentNode.getLastChild();
        if (lastChild instanceof Text) {
            ((Text)lastChild).appendData(textData);
        }
        else {
            this._currentNode.appendChild(this._document.createTextNode(textData));
        }
    }
    
    private final String processUtf8CharacterString() throws FastInfosetException, IOException {
        if ((this._b & 0x10) > 0) {
            this._characterContentChunkTable.ensureSize(this._octetBufferLength);
            final int charactersOffset = this._characterContentChunkTable._arrayIndex;
            this.decodeUtf8StringAsCharBuffer(this._characterContentChunkTable._array, charactersOffset);
            this._characterContentChunkTable.add(this._charBufferLength);
            return this._characterContentChunkTable.getString(this._characterContentChunkTable._cachedIndex);
        }
        this.decodeUtf8StringAsCharBuffer();
        return new String(this._charBuffer, 0, this._charBufferLength);
    }
    
    protected final void processEIIWithNamespaces() throws FastInfosetException, IOException {
        final boolean hasAttributes = (this._b & 0x40) > 0;
        if (++this._prefixTable._declarationId == Integer.MAX_VALUE) {
            this._prefixTable.clearDeclarationIds();
        }
        Attr a = null;
        final int start = this._namespacePrefixesIndex;
        int b;
        for (b = this.read(); (b & 0xFC) == 0xCC; b = this.read()) {
            if (this._namespaceAttributesIndex == this._namespaceAttributes.length) {
                final Attr[] newNamespaceAttributes = new Attr[this._namespaceAttributesIndex * 3 / 2 + 1];
                System.arraycopy(this._namespaceAttributes, 0, newNamespaceAttributes, 0, this._namespaceAttributesIndex);
                this._namespaceAttributes = newNamespaceAttributes;
            }
            if (this._namespacePrefixesIndex == this._namespacePrefixes.length) {
                final int[] namespaceAIIs = new int[this._namespacePrefixesIndex * 3 / 2 + 1];
                System.arraycopy(this._namespacePrefixes, 0, namespaceAIIs, 0, this._namespacePrefixesIndex);
                this._namespacePrefixes = namespaceAIIs;
            }
            switch (b & 0x3) {
                case 0: {
                    a = this.createAttribute("http://www.w3.org/2000/xmlns/", "xmlns", "xmlns");
                    a.setValue("");
                    final int[] namespacePrefixes = this._namespacePrefixes;
                    final int n = this._namespacePrefixesIndex++;
                    final int n2 = -1;
                    namespacePrefixes[n] = n2;
                    this._namespaceNameIndex = n2;
                    this._prefixIndex = n2;
                    break;
                }
                case 1: {
                    a = this.createAttribute("http://www.w3.org/2000/xmlns/", "xmlns", "xmlns");
                    a.setValue(this.decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(false));
                    final int[] namespacePrefixes2 = this._namespacePrefixes;
                    final int n3 = this._namespacePrefixesIndex++;
                    final int prefixIndex = -1;
                    namespacePrefixes2[n3] = prefixIndex;
                    this._prefixIndex = prefixIndex;
                    break;
                }
                case 2: {
                    final String prefix = this.decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(false);
                    a = this.createAttribute("http://www.w3.org/2000/xmlns/", this.createQualifiedNameString(prefix), prefix);
                    a.setValue("");
                    this._namespaceNameIndex = -1;
                    this._namespacePrefixes[this._namespacePrefixesIndex++] = this._prefixIndex;
                    break;
                }
                case 3: {
                    final String prefix = this.decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(true);
                    a = this.createAttribute("http://www.w3.org/2000/xmlns/", this.createQualifiedNameString(prefix), prefix);
                    a.setValue(this.decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(true));
                    this._namespacePrefixes[this._namespacePrefixesIndex++] = this._prefixIndex;
                    break;
                }
            }
            this._prefixTable.pushScope(this._prefixIndex, this._namespaceNameIndex);
            this._namespaceAttributes[this._namespaceAttributesIndex++] = a;
        }
        if (b != 240) {
            throw new IOException(CommonResourceBundle.getInstance().getString("message.EIInamespaceNameNotTerminatedCorrectly"));
        }
        final int end = this._namespacePrefixesIndex;
        this._b = this.read();
        switch (DecoderStateTables.EII(this._b)) {
            case 0: {
                this.processEII(this._elementNameTable._array[this._b], hasAttributes);
                break;
            }
            case 2: {
                this.processEII(this.decodeEIIIndexMedium(), hasAttributes);
                break;
            }
            case 3: {
                this.processEII(this.decodeEIIIndexLarge(), hasAttributes);
                break;
            }
            case 5: {
                final QualifiedName qn = this.processLiteralQualifiedName(this._b & 0x3, this._elementNameTable.getNext());
                this._elementNameTable.add(qn);
                this.processEII(qn, hasAttributes);
                break;
            }
            default: {
                throw new IOException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEIIAfterAIIs"));
            }
        }
        for (int i = start; i < end; ++i) {
            this._prefixTable.popScope(this._namespacePrefixes[i]);
        }
        this._namespacePrefixesIndex = start;
    }
    
    protected final QualifiedName processLiteralQualifiedName(final int state, QualifiedName q) throws FastInfosetException, IOException {
        if (q == null) {
            q = new QualifiedName();
        }
        switch (state) {
            case 0: {
                return q.set(null, null, this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), -1, -1, this._identifier, null);
            }
            case 1: {
                return q.set(null, this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(false), this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), -1, this._namespaceNameIndex, this._identifier, null);
            }
            case 2: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qNameMissingNamespaceName"));
            }
            case 3: {
                return q.set(this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsPrefix(true), this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(true), this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), this._prefixIndex, this._namespaceNameIndex, this._identifier, this._charBuffer);
            }
            default: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingEII"));
            }
        }
    }
    
    protected final QualifiedName processLiteralQualifiedName(final int state) throws FastInfosetException, IOException {
        switch (state) {
            case 0: {
                return new QualifiedName(null, null, this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), -1, -1, this._identifier, null);
            }
            case 1: {
                return new QualifiedName(null, this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(false), this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), -1, this._namespaceNameIndex, this._identifier, null);
            }
            case 2: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qNameMissingNamespaceName"));
            }
            case 3: {
                return new QualifiedName(this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsPrefix(true), this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(true), this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), this._prefixIndex, this._namespaceNameIndex, this._identifier, this._charBuffer);
            }
            default: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingEII"));
            }
        }
    }
    
    protected final void processAIIs() throws FastInfosetException, IOException {
        if (++this._duplicateAttributeVerifier._currentIteration == Integer.MAX_VALUE) {
            this._duplicateAttributeVerifier.clear();
        }
        do {
            int b = this.read();
            QualifiedName name = null;
            switch (DecoderStateTables.AII(b)) {
                case 0: {
                    name = this._attributeNameTable._array[b];
                    break;
                }
                case 1: {
                    final int i = ((b & 0x1F) << 8 | this.read()) + 64;
                    name = this._attributeNameTable._array[i];
                    break;
                }
                case 2: {
                    final int i = ((b & 0xF) << 16 | this.read() << 8 | this.read()) + 8256;
                    name = this._attributeNameTable._array[i];
                    break;
                }
                case 3: {
                    name = this.processLiteralQualifiedName(b & 0x3, this._attributeNameTable.getNext());
                    name.createAttributeValues(256);
                    this._attributeNameTable.add(name);
                    break;
                }
                case 5: {
                    this._doubleTerminate = true;
                }
                case 4: {
                    this._terminate = true;
                    continue;
                }
                default: {
                    throw new IOException(CommonResourceBundle.getInstance().getString("message.decodingAIIs"));
                }
            }
            if (name.prefixIndex > 0 && this._prefixTable._currentInScope[name.prefixIndex] != name.namespaceNameIndex) {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.AIIqNameNotInScope"));
            }
            this._duplicateAttributeVerifier.checkForDuplicateAttribute(name.attributeHash, name.attributeId);
            final Attr a = this.createAttribute(name.namespaceName, name.qName, name.localName);
            b = this.read();
            switch (DecoderStateTables.NISTRING(b)) {
                case 0: {
                    final boolean addToTable = (b & 0x40) > 0;
                    this._octetBufferLength = (b & 0x7) + 1;
                    final String value = this.decodeUtf8StringAsString();
                    if (addToTable) {
                        this._attributeValueTable.add(value);
                    }
                    a.setValue(value);
                    this._currentElement.setAttributeNode(a);
                    continue;
                }
                case 1: {
                    final boolean addToTable = (b & 0x40) > 0;
                    this._octetBufferLength = this.read() + 9;
                    final String value = this.decodeUtf8StringAsString();
                    if (addToTable) {
                        this._attributeValueTable.add(value);
                    }
                    a.setValue(value);
                    this._currentElement.setAttributeNode(a);
                    continue;
                }
                case 2: {
                    final boolean addToTable = (b & 0x40) > 0;
                    final int length = this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read();
                    this._octetBufferLength = length + 265;
                    final String value = this.decodeUtf8StringAsString();
                    if (addToTable) {
                        this._attributeValueTable.add(value);
                    }
                    a.setValue(value);
                    this._currentElement.setAttributeNode(a);
                    continue;
                }
                case 3: {
                    final boolean addToTable = (b & 0x40) > 0;
                    this._octetBufferLength = (b & 0x7) + 1;
                    final String value = this.decodeUtf16StringAsString();
                    if (addToTable) {
                        this._attributeValueTable.add(value);
                    }
                    a.setValue(value);
                    this._currentElement.setAttributeNode(a);
                    continue;
                }
                case 4: {
                    final boolean addToTable = (b & 0x40) > 0;
                    this._octetBufferLength = this.read() + 9;
                    final String value = this.decodeUtf16StringAsString();
                    if (addToTable) {
                        this._attributeValueTable.add(value);
                    }
                    a.setValue(value);
                    this._currentElement.setAttributeNode(a);
                    continue;
                }
                case 5: {
                    final boolean addToTable = (b & 0x40) > 0;
                    final int length = this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read();
                    this._octetBufferLength = length + 265;
                    final String value = this.decodeUtf16StringAsString();
                    if (addToTable) {
                        this._attributeValueTable.add(value);
                    }
                    a.setValue(value);
                    this._currentElement.setAttributeNode(a);
                    continue;
                }
                case 6: {
                    final boolean addToTable = (b & 0x40) > 0;
                    this._identifier = (b & 0xF) << 4;
                    b = this.read();
                    this._identifier |= (b & 0xF0) >> 4;
                    this.decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(b);
                    final String value = this.decodeRestrictedAlphabetAsString();
                    if (addToTable) {
                        this._attributeValueTable.add(value);
                    }
                    a.setValue(value);
                    this._currentElement.setAttributeNode(a);
                    continue;
                }
                case 7: {
                    final boolean addToTable = (b & 0x40) > 0;
                    this._identifier = (b & 0xF) << 4;
                    b = this.read();
                    this._identifier |= (b & 0xF0) >> 4;
                    this.decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(b);
                    final String value = this.convertEncodingAlgorithmDataToCharacters(true);
                    if (addToTable) {
                        this._attributeValueTable.add(value);
                    }
                    a.setValue(value);
                    this._currentElement.setAttributeNode(a);
                    continue;
                }
                case 8: {
                    final String value = this._attributeValueTable._array[b & 0x3F];
                    a.setValue(value);
                    this._currentElement.setAttributeNode(a);
                    continue;
                }
                case 9: {
                    final int index = ((b & 0x1F) << 8 | this.read()) + 64;
                    final String value = this._attributeValueTable._array[index];
                    a.setValue(value);
                    this._currentElement.setAttributeNode(a);
                    continue;
                }
                case 10: {
                    final int index = ((b & 0xF) << 16 | this.read() << 8 | this.read()) + 8256;
                    final String value = this._attributeValueTable._array[index];
                    a.setValue(value);
                    this._currentElement.setAttributeNode(a);
                    continue;
                }
                case 11: {
                    a.setValue("");
                    this._currentElement.setAttributeNode(a);
                    continue;
                }
                default: {
                    throw new IOException(CommonResourceBundle.getInstance().getString("message.decodingAIIValue"));
                }
            }
        } while (!this._terminate);
        this._duplicateAttributeVerifier._poolCurrent = this._duplicateAttributeVerifier._poolHead;
        this._terminate = this._doubleTerminate;
        this._doubleTerminate = false;
    }
    
    protected final void processCommentII() throws FastInfosetException, IOException {
        switch (this.decodeNonIdentifyingStringOnFirstBit()) {
            case 0: {
                final String s = new String(this._charBuffer, 0, this._charBufferLength);
                if (this._addToTable) {
                    this._v.otherString.add(new CharArrayString(s, false));
                }
                this._currentNode.appendChild(this._document.createComment(s));
                break;
            }
            case 2: {
                throw new IOException(CommonResourceBundle.getInstance().getString("message.commentIIAlgorithmNotSupported"));
            }
            case 1: {
                final String s = this._v.otherString.get(this._integer).toString();
                this._currentNode.appendChild(this._document.createComment(s));
                break;
            }
            case 3: {
                this._currentNode.appendChild(this._document.createComment(""));
                break;
            }
        }
    }
    
    protected final void processProcessingII() throws FastInfosetException, IOException {
        final String target = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
        switch (this.decodeNonIdentifyingStringOnFirstBit()) {
            case 0: {
                final String data = new String(this._charBuffer, 0, this._charBufferLength);
                if (this._addToTable) {
                    this._v.otherString.add(new CharArrayString(data, false));
                }
                this._currentNode.appendChild(this._document.createProcessingInstruction(target, data));
                break;
            }
            case 2: {
                throw new IOException(CommonResourceBundle.getInstance().getString("message.processingIIWithEncodingAlgorithm"));
            }
            case 1: {
                final String data = this._v.otherString.get(this._integer).toString();
                this._currentNode.appendChild(this._document.createProcessingInstruction(target, data));
                break;
            }
            case 3: {
                this._currentNode.appendChild(this._document.createProcessingInstruction(target, ""));
                break;
            }
        }
    }
    
    protected Element createElement(final String namespaceName, final String qName, final String localName) {
        return this._document.createElementNS(namespaceName, qName);
    }
    
    protected Attr createAttribute(final String namespaceName, final String qName, final String localName) {
        return this._document.createAttributeNS(namespaceName, qName);
    }
    
    protected String convertEncodingAlgorithmDataToCharacters(final boolean isAttributeValue) throws FastInfosetException, IOException {
        final StringBuffer buffer = new StringBuffer();
        if (this._identifier < 9) {
            final Object array = BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier).decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
            BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier).convertToCharacters(array, buffer);
        }
        else if (this._identifier == 9) {
            if (!isAttributeValue) {
                this._octetBufferOffset -= this._octetBufferLength;
                return this.decodeUtf8StringAsString();
            }
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.CDATAAlgorithmNotSupported"));
        }
        else if (this._identifier >= 32) {
            final String URI = this._v.encodingAlgorithm.get(this._identifier - 32);
            final EncodingAlgorithm ea = this._registeredEncodingAlgorithms.get(URI);
            if (ea == null) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmDataCannotBeReported"));
            }
            final Object data = ea.decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
            ea.convertToCharacters(data, buffer);
        }
        return buffer.toString();
    }
}
