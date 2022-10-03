package com.sun.xml.internal.bind.v2.runtime.output;

import org.xml.sax.SAXException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.xml.internal.bind.v2.runtime.Name;
import java.util.Collections;
import java.util.Iterator;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.NamespaceContext2;

public final class NamespaceContextImpl implements NamespaceContext2
{
    private final XMLSerializer owner;
    private String[] prefixes;
    private String[] nsUris;
    private int size;
    private Element current;
    private final Element top;
    private NamespacePrefixMapper prefixMapper;
    public boolean collectionMode;
    private static final NamespacePrefixMapper defaultNamespacePrefixMapper;
    
    public NamespaceContextImpl(final XMLSerializer owner) {
        this.prefixes = new String[4];
        this.nsUris = new String[4];
        this.prefixMapper = NamespaceContextImpl.defaultNamespacePrefixMapper;
        this.owner = owner;
        final Element element = new Element(this, (Element)null);
        this.top = element;
        this.current = element;
        this.put("http://www.w3.org/XML/1998/namespace", "xml");
    }
    
    public void setPrefixMapper(NamespacePrefixMapper mapper) {
        if (mapper == null) {
            mapper = NamespaceContextImpl.defaultNamespacePrefixMapper;
        }
        this.prefixMapper = mapper;
    }
    
    public NamespacePrefixMapper getPrefixMapper() {
        return this.prefixMapper;
    }
    
    public void reset() {
        this.current = this.top;
        this.size = 1;
        this.collectionMode = false;
    }
    
    public int declareNsUri(final String uri, String preferedPrefix, final boolean requirePrefix) {
        preferedPrefix = this.prefixMapper.getPreferredPrefix(uri, preferedPrefix, requirePrefix);
        if (uri.length() == 0) {
            int i = this.size - 1;
            while (i >= 0) {
                if (this.nsUris[i].length() == 0) {
                    return i;
                }
                if (this.prefixes[i].length() == 0) {
                    assert this.current.defaultPrefixIndex == -1 && this.current.oldDefaultNamespaceUriIndex == -1;
                    final String oldUri = this.nsUris[i];
                    final String[] knownURIs = this.owner.nameList.namespaceURIs;
                    if (this.current.baseIndex <= i) {
                        this.nsUris[i] = "";
                        final int subst = this.put(oldUri, null);
                        for (int j = knownURIs.length - 1; j >= 0; --j) {
                            if (knownURIs[j].equals(oldUri)) {
                                this.owner.knownUri2prefixIndexMap[j] = subst;
                                break;
                            }
                        }
                        if (this.current.elementLocalName != null) {
                            this.current.setTagName(subst, this.current.elementLocalName, this.current.getOuterPeer());
                        }
                        return i;
                    }
                    for (int k = knownURIs.length - 1; k >= 0; --k) {
                        if (knownURIs[k].equals(oldUri)) {
                            this.current.defaultPrefixIndex = i;
                            this.current.oldDefaultNamespaceUriIndex = k;
                            this.owner.knownUri2prefixIndexMap[k] = this.size;
                            break;
                        }
                    }
                    if (this.current.elementLocalName != null) {
                        this.current.setTagName(this.size, this.current.elementLocalName, this.current.getOuterPeer());
                    }
                    this.put(this.nsUris[i], null);
                    return this.put("", "");
                }
                else {
                    --i;
                }
            }
            return this.put("", "");
        }
        for (int i = this.size - 1; i >= 0; --i) {
            final String p = this.prefixes[i];
            if (this.nsUris[i].equals(uri) && (!requirePrefix || p.length() > 0)) {
                return i;
            }
            if (p.equals(preferedPrefix)) {
                preferedPrefix = null;
            }
        }
        if (preferedPrefix == null && requirePrefix) {
            preferedPrefix = this.makeUniquePrefix();
        }
        return this.put(uri, preferedPrefix);
    }
    
    @Override
    public int force(@NotNull final String uri, @NotNull final String prefix) {
        int i = this.size - 1;
        while (i >= 0) {
            if (this.prefixes[i].equals(prefix)) {
                if (this.nsUris[i].equals(uri)) {
                    return i;
                }
                break;
            }
            else {
                --i;
            }
        }
        return this.put(uri, prefix);
    }
    
    public int put(@NotNull final String uri, @Nullable String prefix) {
        if (this.size == this.nsUris.length) {
            final String[] u = new String[this.nsUris.length * 2];
            final String[] p = new String[this.prefixes.length * 2];
            System.arraycopy(this.nsUris, 0, u, 0, this.nsUris.length);
            System.arraycopy(this.prefixes, 0, p, 0, this.prefixes.length);
            this.nsUris = u;
            this.prefixes = p;
        }
        if (prefix == null) {
            if (this.size == 1) {
                prefix = "";
            }
            else {
                prefix = this.makeUniquePrefix();
            }
        }
        this.nsUris[this.size] = uri;
        this.prefixes[this.size] = prefix;
        return this.size++;
    }
    
    private String makeUniquePrefix() {
        String prefix;
        for (prefix = new StringBuilder(5).append("ns").append(this.size).toString(); this.getNamespaceURI(prefix) != null; prefix += '_') {}
        return prefix;
    }
    
    public Element getCurrent() {
        return this.current;
    }
    
    public int getPrefixIndex(final String uri) {
        for (int i = this.size - 1; i >= 0; --i) {
            if (this.nsUris[i].equals(uri)) {
                return i;
            }
        }
        throw new IllegalStateException();
    }
    
    public String getPrefix(final int prefixIndex) {
        return this.prefixes[prefixIndex];
    }
    
    public String getNamespaceURI(final int prefixIndex) {
        return this.nsUris[prefixIndex];
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        for (int i = this.size - 1; i >= 0; --i) {
            if (this.prefixes[i].equals(prefix)) {
                return this.nsUris[i];
            }
        }
        return null;
    }
    
    @Override
    public String getPrefix(final String uri) {
        if (this.collectionMode) {
            return this.declareNamespace(uri, null, false);
        }
        for (int i = this.size - 1; i >= 0; --i) {
            if (this.nsUris[i].equals(uri)) {
                return this.prefixes[i];
            }
        }
        return null;
    }
    
    @Override
    public Iterator<String> getPrefixes(final String uri) {
        final String prefix = this.getPrefix(uri);
        if (prefix == null) {
            return Collections.emptySet().iterator();
        }
        return Collections.singleton(uri).iterator();
    }
    
    @Override
    public String declareNamespace(final String namespaceUri, final String preferedPrefix, final boolean requirePrefix) {
        final int idx = this.declareNsUri(namespaceUri, preferedPrefix, requirePrefix);
        return this.getPrefix(idx);
    }
    
    public int count() {
        return this.size;
    }
    
    static {
        defaultNamespacePrefixMapper = new NamespacePrefixMapper() {
            @Override
            public String getPreferredPrefix(final String namespaceUri, final String suggestion, final boolean requirePrefix) {
                if (namespaceUri.equals("http://www.w3.org/2001/XMLSchema-instance")) {
                    return "xsi";
                }
                if (namespaceUri.equals("http://www.w3.org/2001/XMLSchema")) {
                    return "xs";
                }
                if (namespaceUri.equals("http://www.w3.org/2005/05/xmlmime")) {
                    return "xmime";
                }
                return suggestion;
            }
        };
    }
    
    public final class Element
    {
        public final NamespaceContextImpl context;
        private final Element prev;
        private Element next;
        private int oldDefaultNamespaceUriIndex;
        private int defaultPrefixIndex;
        private int baseIndex;
        private final int depth;
        private int elementNamePrefix;
        private String elementLocalName;
        private Name elementName;
        private Object outerPeer;
        private Object innerPeer;
        
        private Element(final NamespaceContextImpl context, final Element prev) {
            this.context = context;
            this.prev = prev;
            this.depth = ((prev == null) ? 0 : (prev.depth + 1));
        }
        
        public boolean isRootElement() {
            return this.depth == 1;
        }
        
        public Element push() {
            if (this.next == null) {
                this.next = new Element(this.context, this);
            }
            this.next.onPushed();
            return this.next;
        }
        
        public Element pop() {
            if (this.oldDefaultNamespaceUriIndex >= 0) {
                this.context.owner.knownUri2prefixIndexMap[this.oldDefaultNamespaceUriIndex] = this.defaultPrefixIndex;
            }
            this.context.size = this.baseIndex;
            this.context.current = this.prev;
            final Object o = null;
            this.innerPeer = o;
            this.outerPeer = o;
            return this.prev;
        }
        
        private void onPushed() {
            final int n = -1;
            this.defaultPrefixIndex = n;
            this.oldDefaultNamespaceUriIndex = n;
            this.baseIndex = this.context.size;
            this.context.current = this;
        }
        
        public void setTagName(final int prefix, final String localName, final Object outerPeer) {
            assert localName != null;
            this.elementNamePrefix = prefix;
            this.elementLocalName = localName;
            this.elementName = null;
            this.outerPeer = outerPeer;
        }
        
        public void setTagName(final Name tagName, final Object outerPeer) {
            assert tagName != null;
            this.elementName = tagName;
            this.outerPeer = outerPeer;
        }
        
        public void startElement(final XmlOutput out, final Object innerPeer) throws IOException, XMLStreamException {
            this.innerPeer = innerPeer;
            if (this.elementName != null) {
                out.beginStartTag(this.elementName);
            }
            else {
                out.beginStartTag(this.elementNamePrefix, this.elementLocalName);
            }
        }
        
        public void endElement(final XmlOutput out) throws IOException, SAXException, XMLStreamException {
            if (this.elementName != null) {
                out.endTag(this.elementName);
                this.elementName = null;
            }
            else {
                out.endTag(this.elementNamePrefix, this.elementLocalName);
            }
        }
        
        public final int count() {
            return this.context.size - this.baseIndex;
        }
        
        public final String getPrefix(final int idx) {
            return this.context.prefixes[this.baseIndex + idx];
        }
        
        public final String getNsUri(final int idx) {
            return this.context.nsUris[this.baseIndex + idx];
        }
        
        public int getBase() {
            return this.baseIndex;
        }
        
        public Object getOuterPeer() {
            return this.outerPeer;
        }
        
        public Object getInnerPeer() {
            return this.innerPeer;
        }
        
        public Element getParent() {
            return this.prev;
        }
    }
}
