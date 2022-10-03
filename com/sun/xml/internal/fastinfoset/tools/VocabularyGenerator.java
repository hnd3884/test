package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.util.QualifiedNameArray;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.fastinfoset.util.PrefixArray;
import com.sun.xml.internal.fastinfoset.util.StringArray;
import com.sun.xml.internal.fastinfoset.util.StringIntMap;
import java.util.Set;
import org.xml.sax.Locator;
import com.sun.xml.internal.fastinfoset.util.CharArray;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import com.sun.xml.internal.org.jvnet.fastinfoset.Vocabulary;
import com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary;
import com.sun.xml.internal.fastinfoset.vocab.SerializerVocabulary;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class VocabularyGenerator extends DefaultHandler implements LexicalHandler
{
    protected SerializerVocabulary _serializerVocabulary;
    protected ParserVocabulary _parserVocabulary;
    protected Vocabulary _v;
    protected int attributeValueSizeConstraint;
    protected int characterContentChunkSizeContraint;
    
    public VocabularyGenerator() {
        this.attributeValueSizeConstraint = 32;
        this.characterContentChunkSizeContraint = 32;
        this._serializerVocabulary = new SerializerVocabulary();
        this._parserVocabulary = new ParserVocabulary();
        this._v = new Vocabulary();
    }
    
    public VocabularyGenerator(final SerializerVocabulary serializerVocabulary) {
        this.attributeValueSizeConstraint = 32;
        this.characterContentChunkSizeContraint = 32;
        this._serializerVocabulary = serializerVocabulary;
        this._parserVocabulary = new ParserVocabulary();
        this._v = new Vocabulary();
    }
    
    public VocabularyGenerator(final ParserVocabulary parserVocabulary) {
        this.attributeValueSizeConstraint = 32;
        this.characterContentChunkSizeContraint = 32;
        this._serializerVocabulary = new SerializerVocabulary();
        this._parserVocabulary = parserVocabulary;
        this._v = new Vocabulary();
    }
    
    public VocabularyGenerator(final SerializerVocabulary serializerVocabulary, final ParserVocabulary parserVocabulary) {
        this.attributeValueSizeConstraint = 32;
        this.characterContentChunkSizeContraint = 32;
        this._serializerVocabulary = serializerVocabulary;
        this._parserVocabulary = parserVocabulary;
        this._v = new Vocabulary();
    }
    
    public Vocabulary getVocabulary() {
        return this._v;
    }
    
    public void setCharacterContentChunkSizeLimit(int size) {
        if (size < 0) {
            size = 0;
        }
        this.characterContentChunkSizeContraint = size;
    }
    
    public int getCharacterContentChunkSizeLimit() {
        return this.characterContentChunkSizeContraint;
    }
    
    public void setAttributeValueSizeLimit(int size) {
        if (size < 0) {
            size = 0;
        }
        this.attributeValueSizeConstraint = size;
    }
    
    public int getAttributeValueSizeLimit() {
        return this.attributeValueSizeConstraint;
    }
    
    @Override
    public void startDocument() throws SAXException {
    }
    
    @Override
    public void endDocument() throws SAXException {
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        this.addToTable(prefix, this._v.prefixes, this._serializerVocabulary.prefix, this._parserVocabulary.prefix);
        this.addToTable(uri, this._v.namespaceNames, this._serializerVocabulary.namespaceName, this._parserVocabulary.namespaceName);
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
    }
    
    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        this.addToNameTable(namespaceURI, qName, localName, this._v.elements, this._serializerVocabulary.elementName, this._parserVocabulary.elementName, false);
        for (int a = 0; a < atts.getLength(); ++a) {
            this.addToNameTable(atts.getURI(a), atts.getQName(a), atts.getLocalName(a), this._v.attributes, this._serializerVocabulary.attributeName, this._parserVocabulary.attributeName, true);
            final String value = atts.getValue(a);
            if (value.length() < this.attributeValueSizeConstraint) {
                this.addToTable(value, this._v.attributeValues, this._serializerVocabulary.attributeValue, this._parserVocabulary.attributeValue);
            }
        }
    }
    
    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (length < this.characterContentChunkSizeContraint) {
            this.addToCharArrayTable(new CharArray(ch, start, length, true));
        }
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
    }
    
    @Override
    public void skippedEntity(final String name) throws SAXException {
    }
    
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
    }
    
    @Override
    public void startCDATA() throws SAXException {
    }
    
    @Override
    public void endCDATA() throws SAXException {
    }
    
    @Override
    public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
    }
    
    @Override
    public void endDTD() throws SAXException {
    }
    
    @Override
    public void startEntity(final String name) throws SAXException {
    }
    
    @Override
    public void endEntity(final String name) throws SAXException {
    }
    
    public void addToTable(final String s, final Set v, final StringIntMap m, final StringArray a) {
        if (s.length() == 0) {
            return;
        }
        if (m.obtainIndex(s) == -1) {
            a.add(s);
        }
        v.add(s);
    }
    
    public void addToTable(final String s, final Set v, final StringIntMap m, final PrefixArray a) {
        if (s.length() == 0) {
            return;
        }
        if (m.obtainIndex(s) == -1) {
            a.add(s);
        }
        v.add(s);
    }
    
    public void addToCharArrayTable(final CharArray c) {
        if (this._serializerVocabulary.characterContentChunk.obtainIndex(c.ch, c.start, c.length, false) == -1) {
            this._parserVocabulary.characterContentChunk.add(c.ch, c.length);
        }
        this._v.characterContentChunks.add(c.toString());
    }
    
    public void addToNameTable(final String namespaceURI, final String qName, final String localName, final Set v, final LocalNameQualifiedNamesMap m, final QualifiedNameArray a, final boolean isAttribute) throws SAXException {
        final LocalNameQualifiedNamesMap.Entry entry = m.obtainEntry(qName);
        if (entry._valueIndex > 0) {
            final QualifiedName[] names = entry._value;
            for (int i = 0; i < entry._valueIndex; ++i) {
                if (namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName)) {
                    return;
                }
            }
        }
        final String prefix = getPrefixFromQualifiedName(qName);
        int namespaceURIIndex = -1;
        int prefixIndex = -1;
        int localNameIndex = -1;
        if (namespaceURI.length() > 0) {
            namespaceURIIndex = this._serializerVocabulary.namespaceName.get(namespaceURI);
            if (namespaceURIIndex == -1) {
                throw new SAXException(CommonResourceBundle.getInstance().getString("message.namespaceURINotIndexed", new Object[] { namespaceURIIndex }));
            }
            if (prefix.length() > 0) {
                prefixIndex = this._serializerVocabulary.prefix.get(prefix);
                if (prefixIndex == -1) {
                    throw new SAXException(CommonResourceBundle.getInstance().getString("message.prefixNotIndexed", new Object[] { prefixIndex }));
                }
            }
        }
        localNameIndex = this._serializerVocabulary.localName.obtainIndex(localName);
        if (localNameIndex == -1) {
            this._parserVocabulary.localName.add(localName);
            localNameIndex = this._parserVocabulary.localName.getSize() - 1;
        }
        final QualifiedName name = new QualifiedName(prefix, namespaceURI, localName, m.getNextIndex(), prefixIndex, namespaceURIIndex, localNameIndex);
        if (isAttribute) {
            name.createAttributeValues(256);
        }
        entry.addQualifiedName(name);
        a.add(name);
        v.add(name.getQName());
    }
    
    public static String getPrefixFromQualifiedName(final String qName) {
        final int i = qName.indexOf(58);
        String prefix = "";
        if (i != -1) {
            prefix = qName.substring(0, i);
        }
        return prefix;
    }
}
