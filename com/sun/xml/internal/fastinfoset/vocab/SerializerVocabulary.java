package com.sun.xml.internal.fastinfoset.vocab;

import com.sun.xml.internal.fastinfoset.QualifiedName;
import javax.xml.namespace.QName;
import java.util.Iterator;
import com.sun.xml.internal.fastinfoset.util.FixedEntryStringIntMap;
import com.sun.xml.internal.fastinfoset.util.KeyIntMap;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.fastinfoset.util.CharArrayIntMap;
import com.sun.xml.internal.fastinfoset.util.StringIntMap;

public class SerializerVocabulary extends Vocabulary
{
    public final StringIntMap restrictedAlphabet;
    public final StringIntMap encodingAlgorithm;
    public final StringIntMap namespaceName;
    public final StringIntMap prefix;
    public final StringIntMap localName;
    public final StringIntMap otherNCName;
    public final StringIntMap otherURI;
    public final StringIntMap attributeValue;
    public final CharArrayIntMap otherString;
    public final CharArrayIntMap characterContentChunk;
    public final LocalNameQualifiedNamesMap elementName;
    public final LocalNameQualifiedNamesMap attributeName;
    public final KeyIntMap[] tables;
    protected boolean _useLocalNameAsKey;
    protected SerializerVocabulary _readOnlyVocabulary;
    
    public SerializerVocabulary() {
        (this.tables = new KeyIntMap[12])[0] = (this.restrictedAlphabet = new StringIntMap(4));
        this.tables[1] = (this.encodingAlgorithm = new StringIntMap(4));
        this.tables[2] = (this.prefix = new FixedEntryStringIntMap("xml", 8));
        this.tables[3] = (this.namespaceName = new FixedEntryStringIntMap("http://www.w3.org/XML/1998/namespace", 8));
        this.tables[4] = (this.localName = new StringIntMap());
        this.tables[5] = (this.otherNCName = new StringIntMap(4));
        this.tables[6] = (this.otherURI = new StringIntMap(4));
        this.tables[7] = (this.attributeValue = new StringIntMap());
        this.tables[8] = (this.otherString = new CharArrayIntMap(4));
        this.tables[9] = (this.characterContentChunk = new CharArrayIntMap());
        this.tables[10] = (this.elementName = new LocalNameQualifiedNamesMap());
        this.tables[11] = (this.attributeName = new LocalNameQualifiedNamesMap());
    }
    
    public SerializerVocabulary(final com.sun.xml.internal.org.jvnet.fastinfoset.Vocabulary v, final boolean useLocalNameAsKey) {
        this();
        this._useLocalNameAsKey = useLocalNameAsKey;
        this.convertVocabulary(v);
    }
    
    public SerializerVocabulary getReadOnlyVocabulary() {
        return this._readOnlyVocabulary;
    }
    
    protected void setReadOnlyVocabulary(final SerializerVocabulary readOnlyVocabulary, final boolean clear) {
        for (int i = 0; i < this.tables.length; ++i) {
            this.tables[i].setReadOnlyMap(readOnlyVocabulary.tables[i], clear);
        }
    }
    
    public void setInitialVocabulary(final SerializerVocabulary initialVocabulary, final boolean clear) {
        this.setExternalVocabularyURI(null);
        this.setInitialReadOnlyVocabulary(true);
        this.setReadOnlyVocabulary(initialVocabulary, clear);
    }
    
    public void setExternalVocabulary(final String externalVocabularyURI, final SerializerVocabulary externalVocabulary, final boolean clear) {
        this.setInitialReadOnlyVocabulary(false);
        this.setExternalVocabularyURI(externalVocabularyURI);
        this.setReadOnlyVocabulary(externalVocabulary, clear);
    }
    
    public void clear() {
        for (int i = 0; i < this.tables.length; ++i) {
            this.tables[i].clear();
        }
    }
    
    private void convertVocabulary(final com.sun.xml.internal.org.jvnet.fastinfoset.Vocabulary v) {
        this.addToTable(v.restrictedAlphabets.iterator(), this.restrictedAlphabet);
        this.addToTable(v.encodingAlgorithms.iterator(), this.encodingAlgorithm);
        this.addToTable(v.prefixes.iterator(), this.prefix);
        this.addToTable(v.namespaceNames.iterator(), this.namespaceName);
        this.addToTable(v.localNames.iterator(), this.localName);
        this.addToTable(v.otherNCNames.iterator(), this.otherNCName);
        this.addToTable(v.otherURIs.iterator(), this.otherURI);
        this.addToTable(v.attributeValues.iterator(), this.attributeValue);
        this.addToTable(v.otherStrings.iterator(), this.otherString);
        this.addToTable(v.characterContentChunks.iterator(), this.characterContentChunk);
        this.addToTable(v.elements.iterator(), this.elementName);
        this.addToTable(v.attributes.iterator(), this.attributeName);
    }
    
    private void addToTable(final Iterator i, final StringIntMap m) {
        while (i.hasNext()) {
            this.addToTable(i.next(), m);
        }
    }
    
    private void addToTable(final String s, final StringIntMap m) {
        if (s.length() == 0) {
            return;
        }
        m.obtainIndex(s);
    }
    
    private void addToTable(final Iterator i, final CharArrayIntMap m) {
        while (i.hasNext()) {
            this.addToTable(i.next(), m);
        }
    }
    
    private void addToTable(final String s, final CharArrayIntMap m) {
        if (s.length() == 0) {
            return;
        }
        final char[] c = s.toCharArray();
        m.obtainIndex(c, 0, c.length, false);
    }
    
    private void addToTable(final Iterator i, final LocalNameQualifiedNamesMap m) {
        while (i.hasNext()) {
            this.addToNameTable(i.next(), m);
        }
    }
    
    private void addToNameTable(final QName n, final LocalNameQualifiedNamesMap m) {
        int namespaceURIIndex = -1;
        int prefixIndex = -1;
        if (n.getNamespaceURI().length() > 0) {
            namespaceURIIndex = this.namespaceName.obtainIndex(n.getNamespaceURI());
            if (namespaceURIIndex == -1) {
                namespaceURIIndex = this.namespaceName.get(n.getNamespaceURI());
            }
            if (n.getPrefix().length() > 0) {
                prefixIndex = this.prefix.obtainIndex(n.getPrefix());
                if (prefixIndex == -1) {
                    prefixIndex = this.prefix.get(n.getPrefix());
                }
            }
        }
        int localNameIndex = this.localName.obtainIndex(n.getLocalPart());
        if (localNameIndex == -1) {
            localNameIndex = this.localName.get(n.getLocalPart());
        }
        final QualifiedName name = new QualifiedName(n.getPrefix(), n.getNamespaceURI(), n.getLocalPart(), m.getNextIndex(), prefixIndex, namespaceURIIndex, localNameIndex);
        LocalNameQualifiedNamesMap.Entry entry = null;
        if (this._useLocalNameAsKey) {
            entry = m.obtainEntry(n.getLocalPart());
        }
        else {
            final String qName = (prefixIndex == -1) ? n.getLocalPart() : (n.getPrefix() + ":" + n.getLocalPart());
            entry = m.obtainEntry(qName);
        }
        entry.addQualifiedName(name);
    }
}
