package com.sun.xml.internal.fastinfoset.dom;

import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import java.io.IOException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import com.sun.xml.internal.fastinfoset.util.NamespaceContextImplementation;
import com.sun.xml.internal.fastinfoset.Encoder;

public class DOMDocumentSerializer extends Encoder
{
    protected NamespaceContextImplementation _namespaceScopeContext;
    protected Node[] _attributes;
    
    public DOMDocumentSerializer() {
        this._namespaceScopeContext = new NamespaceContextImplementation();
        this._attributes = new Node[32];
    }
    
    public final void serialize(final Node n) throws IOException {
        switch (n.getNodeType()) {
            case 9: {
                this.serialize((Document)n);
                break;
            }
            case 1: {
                this.serializeElementAsDocument(n);
                break;
            }
            case 8: {
                this.serializeComment(n);
                break;
            }
            case 7: {
                this.serializeProcessingInstruction(n);
                break;
            }
        }
    }
    
    public final void serialize(final Document d) throws IOException {
        this.reset();
        this.encodeHeader(false);
        this.encodeInitialVocabulary();
        final NodeList nl = d.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node n = nl.item(i);
            switch (n.getNodeType()) {
                case 1: {
                    this.serializeElement(n);
                    break;
                }
                case 8: {
                    this.serializeComment(n);
                    break;
                }
                case 7: {
                    this.serializeProcessingInstruction(n);
                    break;
                }
            }
        }
        this.encodeDocumentTermination();
    }
    
    protected final void serializeElementAsDocument(final Node e) throws IOException {
        this.reset();
        this.encodeHeader(false);
        this.encodeInitialVocabulary();
        this.serializeElement(e);
        this.encodeDocumentTermination();
    }
    
    protected final void serializeElement(final Node e) throws IOException {
        this.encodeTermination();
        int attributesSize = 0;
        this._namespaceScopeContext.pushContext();
        if (e.hasAttributes()) {
            final NamedNodeMap nnm = e.getAttributes();
            for (int i = 0; i < nnm.getLength(); ++i) {
                final Node a = nnm.item(i);
                final String namespaceURI = a.getNamespaceURI();
                if (namespaceURI != null && namespaceURI.equals("http://www.w3.org/2000/xmlns/")) {
                    String attrPrefix = a.getLocalName();
                    final String attrNamespace = a.getNodeValue();
                    if (attrPrefix == "xmlns" || attrPrefix.equals("xmlns")) {
                        attrPrefix = "";
                    }
                    this._namespaceScopeContext.declarePrefix(attrPrefix, attrNamespace);
                }
                else {
                    if (attributesSize == this._attributes.length) {
                        final Node[] attributes = new Node[attributesSize * 3 / 2 + 1];
                        System.arraycopy(this._attributes, 0, attributes, 0, attributesSize);
                        this._attributes = attributes;
                    }
                    this._attributes[attributesSize++] = a;
                    final String attrNamespaceURI = a.getNamespaceURI();
                    final String attrPrefix2 = a.getPrefix();
                    if (attrPrefix2 != null && !this._namespaceScopeContext.getNamespaceURI(attrPrefix2).equals(attrNamespaceURI)) {
                        this._namespaceScopeContext.declarePrefix(attrPrefix2, attrNamespaceURI);
                    }
                }
            }
        }
        final String elementNamespaceURI = e.getNamespaceURI();
        String elementPrefix = e.getPrefix();
        if (elementPrefix == null) {
            elementPrefix = "";
        }
        if (elementNamespaceURI != null && !this._namespaceScopeContext.getNamespaceURI(elementPrefix).equals(elementNamespaceURI)) {
            this._namespaceScopeContext.declarePrefix(elementPrefix, elementNamespaceURI);
        }
        if (!this._namespaceScopeContext.isCurrentContextEmpty()) {
            if (attributesSize > 0) {
                this.write(120);
            }
            else {
                this.write(56);
            }
            for (int j = this._namespaceScopeContext.getCurrentContextStartIndex(); j < this._namespaceScopeContext.getCurrentContextEndIndex(); ++j) {
                final String prefix = this._namespaceScopeContext.getPrefix(j);
                final String uri = this._namespaceScopeContext.getNamespaceURI(j);
                this.encodeNamespaceAttribute(prefix, uri);
            }
            this.write(240);
            this._b = 0;
        }
        else {
            this._b = ((attributesSize > 0) ? 64 : 0);
        }
        String namespaceURI2 = elementNamespaceURI;
        namespaceURI2 = ((namespaceURI2 == null) ? "" : namespaceURI2);
        this.encodeElement(namespaceURI2, e.getNodeName(), e.getLocalName());
        if (attributesSize > 0) {
            for (int k = 0; k < attributesSize; ++k) {
                final Node a2 = this._attributes[k];
                this._attributes[k] = null;
                namespaceURI2 = a2.getNamespaceURI();
                namespaceURI2 = ((namespaceURI2 == null) ? "" : namespaceURI2);
                this.encodeAttribute(namespaceURI2, a2.getNodeName(), a2.getLocalName());
                final String value = a2.getNodeValue();
                final boolean addToTable = this.isAttributeValueLengthMatchesLimit(value.length());
                this.encodeNonIdentifyingStringOnFirstBit(value, this._v.attributeValue, addToTable, false);
            }
            this._b = 240;
            this._terminate = true;
        }
        if (e.hasChildNodes()) {
            final NodeList nl = e.getChildNodes();
            for (int l = 0; l < nl.getLength(); ++l) {
                final Node n = nl.item(l);
                switch (n.getNodeType()) {
                    case 1: {
                        this.serializeElement(n);
                        break;
                    }
                    case 3: {
                        this.serializeText(n);
                        break;
                    }
                    case 4: {
                        this.serializeCDATA(n);
                        break;
                    }
                    case 8: {
                        this.serializeComment(n);
                        break;
                    }
                    case 7: {
                        this.serializeProcessingInstruction(n);
                        break;
                    }
                }
            }
        }
        this.encodeElementTermination();
        this._namespaceScopeContext.popContext();
    }
    
    protected final void serializeText(final Node t) throws IOException {
        final String text = t.getNodeValue();
        final int length = (text != null) ? text.length() : 0;
        if (length == 0) {
            return;
        }
        if (length < this._charBuffer.length) {
            text.getChars(0, length, this._charBuffer, 0);
            if (this.getIgnoreWhiteSpaceTextContent() && Encoder.isWhiteSpace(this._charBuffer, 0, length)) {
                return;
            }
            this.encodeTermination();
            this.encodeCharacters(this._charBuffer, 0, length);
        }
        else {
            final char[] ch = text.toCharArray();
            if (this.getIgnoreWhiteSpaceTextContent() && Encoder.isWhiteSpace(ch, 0, length)) {
                return;
            }
            this.encodeTermination();
            this.encodeCharactersNoClone(ch, 0, length);
        }
    }
    
    protected final void serializeCDATA(final Node t) throws IOException {
        final String text = t.getNodeValue();
        final int length = (text != null) ? text.length() : 0;
        if (length == 0) {
            return;
        }
        final char[] ch = text.toCharArray();
        if (this.getIgnoreWhiteSpaceTextContent() && Encoder.isWhiteSpace(ch, 0, length)) {
            return;
        }
        this.encodeTermination();
        try {
            this.encodeCIIBuiltInAlgorithmDataAsCDATA(ch, 0, length);
        }
        catch (final FastInfosetException e) {
            throw new IOException("");
        }
    }
    
    protected final void serializeComment(final Node c) throws IOException {
        if (this.getIgnoreComments()) {
            return;
        }
        this.encodeTermination();
        final String comment = c.getNodeValue();
        final int length = (comment != null) ? comment.length() : 0;
        if (length == 0) {
            this.encodeComment(this._charBuffer, 0, 0);
        }
        else if (length < this._charBuffer.length) {
            comment.getChars(0, length, this._charBuffer, 0);
            this.encodeComment(this._charBuffer, 0, length);
        }
        else {
            final char[] ch = comment.toCharArray();
            this.encodeCommentNoClone(ch, 0, length);
        }
    }
    
    protected final void serializeProcessingInstruction(final Node pi) throws IOException {
        if (this.getIgnoreProcesingInstructions()) {
            return;
        }
        this.encodeTermination();
        final String target = pi.getNodeName();
        final String data = pi.getNodeValue();
        this.encodeProcessingInstruction(target, data);
    }
    
    protected final void encodeElement(final String namespaceURI, final String qName, final String localName) throws IOException {
        final LocalNameQualifiedNamesMap.Entry entry = this._v.elementName.obtainEntry(qName);
        if (entry._valueIndex > 0) {
            final QualifiedName[] names = entry._value;
            for (int i = 0; i < entry._valueIndex; ++i) {
                if (namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName)) {
                    this.encodeNonZeroIntegerOnThirdBit(names[i].index);
                    return;
                }
            }
        }
        if (localName != null) {
            this.encodeLiteralElementQualifiedNameOnThirdBit(namespaceURI, Encoder.getPrefixFromQualifiedName(qName), localName, entry);
        }
        else {
            this.encodeLiteralElementQualifiedNameOnThirdBit(namespaceURI, "", qName, entry);
        }
    }
    
    protected final void encodeAttribute(final String namespaceURI, final String qName, final String localName) throws IOException {
        final LocalNameQualifiedNamesMap.Entry entry = this._v.attributeName.obtainEntry(qName);
        if (entry._valueIndex > 0) {
            final QualifiedName[] names = entry._value;
            for (int i = 0; i < entry._valueIndex; ++i) {
                if (namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName)) {
                    this.encodeNonZeroIntegerOnSecondBitFirstBitZero(names[i].index);
                    return;
                }
            }
        }
        if (localName != null) {
            this.encodeLiteralAttributeQualifiedNameOnSecondBit(namespaceURI, Encoder.getPrefixFromQualifiedName(qName), localName, entry);
        }
        else {
            this.encodeLiteralAttributeQualifiedNameOnSecondBit(namespaceURI, "", qName, entry);
        }
    }
}
