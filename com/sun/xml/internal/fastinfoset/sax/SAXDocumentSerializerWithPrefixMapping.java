package com.sun.xml.internal.fastinfoset.sax;

import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;
import org.xml.sax.Attributes;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.util.HashMap;
import com.sun.xml.internal.fastinfoset.util.StringIntMap;
import java.util.Map;

public class SAXDocumentSerializerWithPrefixMapping extends SAXDocumentSerializer
{
    protected Map _namespaceToPrefixMapping;
    protected Map _prefixToPrefixMapping;
    protected String _lastCheckedNamespace;
    protected String _lastCheckedPrefix;
    protected StringIntMap _declaredNamespaces;
    
    public SAXDocumentSerializerWithPrefixMapping(final Map namespaceToPrefixMapping) {
        super(true);
        this._namespaceToPrefixMapping = new HashMap(namespaceToPrefixMapping);
        this._prefixToPrefixMapping = new HashMap();
        this._namespaceToPrefixMapping.put("", "");
        this._namespaceToPrefixMapping.put("http://www.w3.org/XML/1998/namespace", "xml");
        this._declaredNamespaces = new StringIntMap(4);
    }
    
    @Override
    public final void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        try {
            if (!this._elementHasNamespaces) {
                this.encodeTermination();
                this.mark();
                this._elementHasNamespaces = true;
                this.write(56);
                this._declaredNamespaces.clear();
                this._declaredNamespaces.obtainIndex(uri);
            }
            else if (this._declaredNamespaces.obtainIndex(uri) != -1) {
                final String p = this.getPrefix(uri);
                if (p != null) {
                    this._prefixToPrefixMapping.put(prefix, p);
                }
                return;
            }
            final String p = this.getPrefix(uri);
            if (p != null) {
                this.encodeNamespaceAttribute(p, uri);
                this._prefixToPrefixMapping.put(prefix, p);
            }
            else {
                this.putPrefix(uri, prefix);
                this.encodeNamespaceAttribute(prefix, uri);
            }
        }
        catch (final IOException e) {
            throw new SAXException("startElement", e);
        }
    }
    
    @Override
    protected final void encodeElement(final String namespaceURI, final String qName, final String localName) throws IOException {
        LocalNameQualifiedNamesMap.Entry entry = this._v.elementName.obtainEntry(localName);
        if (entry._valueIndex > 0) {
            if (this.encodeElementMapEntry(entry, namespaceURI)) {
                return;
            }
            if (this._v.elementName.isQNameFromReadOnlyMap(entry._value[0])) {
                entry = this._v.elementName.obtainDynamicEntry(localName);
                if (entry._valueIndex > 0 && this.encodeElementMapEntry(entry, namespaceURI)) {
                    return;
                }
            }
        }
        this.encodeLiteralElementQualifiedNameOnThirdBit(namespaceURI, this.getPrefix(namespaceURI), localName, entry);
    }
    
    protected boolean encodeElementMapEntry(final LocalNameQualifiedNamesMap.Entry entry, final String namespaceURI) throws IOException {
        final QualifiedName[] names = entry._value;
        for (int i = 0; i < entry._valueIndex; ++i) {
            if (namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName)) {
                this.encodeNonZeroIntegerOnThirdBit(names[i].index);
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected final void encodeAttributes(final Attributes atts) throws IOException, FastInfosetException {
        if (atts instanceof EncodingAlgorithmAttributes) {
            final EncodingAlgorithmAttributes eAtts = (EncodingAlgorithmAttributes)atts;
            for (int i = 0; i < eAtts.getLength(); ++i) {
                final String uri = atts.getURI(i);
                if (this.encodeAttribute(uri, atts.getQName(i), atts.getLocalName(i))) {
                    final Object data = eAtts.getAlgorithmData(i);
                    if (data == null) {
                        String value = eAtts.getValue(i);
                        final boolean addToTable = this.isAttributeValueLengthMatchesLimit(value.length());
                        final boolean mustToBeAddedToTable = eAtts.getToIndex(i);
                        final String alphabet = eAtts.getAlpababet(i);
                        if (alphabet == null) {
                            if (uri == "http://www.w3.org/2001/XMLSchema-instance" || uri.equals("http://www.w3.org/2001/XMLSchema-instance")) {
                                value = this.convertQName(value);
                            }
                            this.encodeNonIdentifyingStringOnFirstBit(value, this._v.attributeValue, addToTable, mustToBeAddedToTable);
                        }
                        else if (alphabet == "0123456789-:TZ ") {
                            this.encodeDateTimeNonIdentifyingStringOnFirstBit(value, addToTable, mustToBeAddedToTable);
                        }
                        else if (alphabet == "0123456789-+.E ") {
                            this.encodeNumericNonIdentifyingStringOnFirstBit(value, addToTable, mustToBeAddedToTable);
                        }
                        else {
                            this.encodeNonIdentifyingStringOnFirstBit(value, this._v.attributeValue, addToTable, mustToBeAddedToTable);
                        }
                    }
                    else {
                        this.encodeNonIdentifyingStringOnFirstBit(eAtts.getAlgorithmURI(i), eAtts.getAlgorithmIndex(i), data);
                    }
                }
            }
        }
        else {
            for (int j = 0; j < atts.getLength(); ++j) {
                final String uri2 = atts.getURI(j);
                if (this.encodeAttribute(atts.getURI(j), atts.getQName(j), atts.getLocalName(j))) {
                    String value = atts.getValue(j);
                    final boolean addToTable = this.isAttributeValueLengthMatchesLimit(value.length());
                    if (uri2 == "http://www.w3.org/2001/XMLSchema-instance" || uri2.equals("http://www.w3.org/2001/XMLSchema-instance")) {
                        value = this.convertQName(value);
                    }
                    this.encodeNonIdentifyingStringOnFirstBit(value, this._v.attributeValue, addToTable, false);
                }
            }
        }
        this._b = 240;
        this._terminate = true;
    }
    
    private String convertQName(final String qName) {
        final int i = qName.indexOf(58);
        String prefix = "";
        String localName = qName;
        if (i != -1) {
            prefix = qName.substring(0, i);
            localName = qName.substring(i + 1);
        }
        final String p = this._prefixToPrefixMapping.get(prefix);
        if (p == null) {
            return qName;
        }
        if (p.length() == 0) {
            return localName;
        }
        return p + ":" + localName;
    }
    
    @Override
    protected final boolean encodeAttribute(final String namespaceURI, final String qName, final String localName) throws IOException {
        LocalNameQualifiedNamesMap.Entry entry = this._v.attributeName.obtainEntry(localName);
        if (entry._valueIndex > 0) {
            if (this.encodeAttributeMapEntry(entry, namespaceURI)) {
                return true;
            }
            if (this._v.attributeName.isQNameFromReadOnlyMap(entry._value[0])) {
                entry = this._v.attributeName.obtainDynamicEntry(localName);
                if (entry._valueIndex > 0 && this.encodeAttributeMapEntry(entry, namespaceURI)) {
                    return true;
                }
            }
        }
        return this.encodeLiteralAttributeQualifiedNameOnSecondBit(namespaceURI, this.getPrefix(namespaceURI), localName, entry);
    }
    
    protected boolean encodeAttributeMapEntry(final LocalNameQualifiedNamesMap.Entry entry, final String namespaceURI) throws IOException {
        final QualifiedName[] names = entry._value;
        for (int i = 0; i < entry._valueIndex; ++i) {
            if (namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName)) {
                this.encodeNonZeroIntegerOnSecondBitFirstBitZero(names[i].index);
                return true;
            }
        }
        return false;
    }
    
    protected final String getPrefix(final String namespaceURI) {
        if (this._lastCheckedNamespace == namespaceURI) {
            return this._lastCheckedPrefix;
        }
        this._lastCheckedNamespace = namespaceURI;
        return this._lastCheckedPrefix = this._namespaceToPrefixMapping.get(namespaceURI);
    }
    
    protected final void putPrefix(final String namespaceURI, final String prefix) {
        this._namespaceToPrefixMapping.put(namespaceURI, prefix);
        this._lastCheckedNamespace = namespaceURI;
        this._lastCheckedPrefix = prefix;
    }
}
