package org.cyberneko.html.filters;

import java.util.Vector;
import java.util.Enumeration;
import org.cyberneko.html.HTMLElements;
import java.util.Locale;
import org.cyberneko.html.xercesbridge.XercesBridge;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.QName;

public class NamespaceBinder extends DefaultFilter
{
    public static final String XHTML_1_0_URI = "http://www.w3.org/1999/xhtml";
    public static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
    public static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String OVERRIDE_NAMESPACES = "http://cyberneko.org/html/features/override-namespaces";
    protected static final String INSERT_NAMESPACES = "http://cyberneko.org/html/features/insert-namespaces";
    private static final String[] RECOGNIZED_FEATURES;
    private static final Boolean[] FEATURE_DEFAULTS;
    protected static final String NAMES_ELEMS = "http://cyberneko.org/html/properties/names/elems";
    protected static final String NAMES_ATTRS = "http://cyberneko.org/html/properties/names/attrs";
    protected static final String NAMESPACES_URI = "http://cyberneko.org/html/properties/namespaces-uri";
    private static final String[] RECOGNIZED_PROPERTIES;
    private static final Object[] PROPERTY_DEFAULTS;
    protected static final short NAMES_NO_CHANGE = 0;
    protected static final short NAMES_UPPERCASE = 1;
    protected static final short NAMES_LOWERCASE = 2;
    protected boolean fNamespaces;
    protected boolean fNamespacePrefixes;
    protected boolean fOverrideNamespaces;
    protected boolean fInsertNamespaces;
    protected short fNamesElems;
    protected short fNamesAttrs;
    protected String fNamespacesURI;
    protected final NamespaceSupport fNamespaceContext;
    private final QName fQName;
    
    public NamespaceBinder() {
        this.fNamespaceContext = new NamespaceSupport();
        this.fQName = new QName();
    }
    
    public String[] getRecognizedFeatures() {
        return DefaultFilter.merge(super.getRecognizedFeatures(), NamespaceBinder.RECOGNIZED_FEATURES);
    }
    
    public Boolean getFeatureDefault(final String featureId) {
        for (int i = 0; i < NamespaceBinder.RECOGNIZED_FEATURES.length; ++i) {
            if (NamespaceBinder.RECOGNIZED_FEATURES[i].equals(featureId)) {
                return NamespaceBinder.FEATURE_DEFAULTS[i];
            }
        }
        return super.getFeatureDefault(featureId);
    }
    
    public String[] getRecognizedProperties() {
        return DefaultFilter.merge(super.getRecognizedProperties(), NamespaceBinder.RECOGNIZED_PROPERTIES);
    }
    
    public Object getPropertyDefault(final String propertyId) {
        for (int i = 0; i < NamespaceBinder.RECOGNIZED_PROPERTIES.length; ++i) {
            if (NamespaceBinder.RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
                return NamespaceBinder.PROPERTY_DEFAULTS[i];
            }
        }
        return super.getPropertyDefault(propertyId);
    }
    
    public void reset(final XMLComponentManager manager) throws XMLConfigurationException {
        super.reset(manager);
        this.fNamespaces = manager.getFeature("http://xml.org/sax/features/namespaces");
        this.fOverrideNamespaces = manager.getFeature("http://cyberneko.org/html/features/override-namespaces");
        this.fInsertNamespaces = manager.getFeature("http://cyberneko.org/html/features/insert-namespaces");
        this.fNamesElems = getNamesValue(String.valueOf(manager.getProperty("http://cyberneko.org/html/properties/names/elems")));
        this.fNamesAttrs = getNamesValue(String.valueOf(manager.getProperty("http://cyberneko.org/html/properties/names/attrs")));
        this.fNamespacesURI = String.valueOf(manager.getProperty("http://cyberneko.org/html/properties/namespaces-uri"));
        this.fNamespaceContext.reset();
    }
    
    public void startDocument(final XMLLocator locator, final String encoding, final NamespaceContext nscontext, final Augmentations augs) throws XNIException {
        super.startDocument(locator, encoding, (NamespaceContext)this.fNamespaceContext, augs);
    }
    
    public void startElement(final QName element, final XMLAttributes attrs, final Augmentations augs) throws XNIException {
        if (this.fNamespaces) {
            this.fNamespaceContext.pushContext();
            this.bindNamespaces(element, attrs);
            final int dcount = this.fNamespaceContext.getDeclaredPrefixCount();
            if (this.fDocumentHandler != null && dcount > 0) {
                for (int i = 0; i < dcount; ++i) {
                    final String prefix = this.fNamespaceContext.getDeclaredPrefixAt(i);
                    final String uri = this.fNamespaceContext.getURI(prefix);
                    XercesBridge.getInstance().XMLDocumentHandler_startPrefixMapping(this.fDocumentHandler, prefix, uri, augs);
                }
            }
        }
        super.startElement(element, attrs, augs);
    }
    
    public void emptyElement(final QName element, final XMLAttributes attrs, final Augmentations augs) throws XNIException {
        if (this.fNamespaces) {
            this.fNamespaceContext.pushContext();
            this.bindNamespaces(element, attrs);
            final int dcount = this.fNamespaceContext.getDeclaredPrefixCount();
            if (this.fDocumentHandler != null && dcount > 0) {
                for (int i = 0; i < dcount; ++i) {
                    final String prefix = this.fNamespaceContext.getDeclaredPrefixAt(i);
                    final String uri = this.fNamespaceContext.getURI(prefix);
                    XercesBridge.getInstance().XMLDocumentHandler_startPrefixMapping(this.fDocumentHandler, prefix, uri, augs);
                }
            }
        }
        super.emptyElement(element, attrs, augs);
        if (this.fNamespaces) {
            final int dcount = this.fNamespaceContext.getDeclaredPrefixCount();
            if (this.fDocumentHandler != null && dcount > 0) {
                for (int i = dcount - 1; i >= 0; --i) {
                    final String prefix = this.fNamespaceContext.getDeclaredPrefixAt(i);
                    XercesBridge.getInstance().XMLDocumentHandler_endPrefixMapping(this.fDocumentHandler, prefix, augs);
                }
            }
            this.fNamespaceContext.popContext();
        }
    }
    
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        if (this.fNamespaces) {
            this.bindNamespaces(element, null);
        }
        super.endElement(element, augs);
        if (this.fNamespaces) {
            final int dcount = this.fNamespaceContext.getDeclaredPrefixCount();
            if (this.fDocumentHandler != null && dcount > 0) {
                for (int i = dcount - 1; i >= 0; --i) {
                    final String prefix = this.fNamespaceContext.getDeclaredPrefixAt(i);
                    XercesBridge.getInstance().XMLDocumentHandler_endPrefixMapping(this.fDocumentHandler, prefix, augs);
                }
            }
            this.fNamespaceContext.popContext();
        }
    }
    
    protected static void splitQName(final QName qname) {
        final int index = qname.rawname.indexOf(58);
        if (index != -1) {
            qname.prefix = qname.rawname.substring(0, index);
            qname.localpart = qname.rawname.substring(index + 1);
        }
    }
    
    protected static final short getNamesValue(final String value) {
        if (value.equals("lower")) {
            return 2;
        }
        if (value.equals("upper")) {
            return 1;
        }
        return 0;
    }
    
    protected static final String modifyName(final String name, final short mode) {
        switch (mode) {
            case 1: {
                return name.toUpperCase(Locale.ENGLISH);
            }
            case 2: {
                return name.toLowerCase(Locale.ENGLISH);
            }
            default: {
                return name;
            }
        }
    }
    
    protected void bindNamespaces(final QName element, final XMLAttributes attrs) {
        splitQName(element);
        int attrCount = (attrs != null) ? attrs.getLength() : 0;
        for (int i = attrCount - 1; i >= 0; --i) {
            attrs.getName(i, this.fQName);
            String aname = this.fQName.rawname;
            final String ANAME = aname.toUpperCase(Locale.ENGLISH);
            if (ANAME.startsWith("XMLNS:") || ANAME.equals("XMLNS")) {
                final int anamelen = aname.length();
                String aprefix = (anamelen > 5) ? aname.substring(0, 5) : null;
                String alocal = (anamelen > 5) ? aname.substring(6) : aname;
                final String avalue = attrs.getValue(i);
                if (anamelen > 5) {
                    aprefix = modifyName(aprefix, (short)2);
                    alocal = modifyName(alocal, this.fNamesElems);
                    aname = aprefix + ':' + alocal;
                }
                else {
                    alocal = (aname = modifyName(alocal, (short)2));
                }
                this.fQName.setValues(aprefix, alocal, aname, (String)null);
                attrs.setName(i, this.fQName);
                final String prefix = (alocal != aname) ? alocal : "";
                String uri = (avalue.length() > 0) ? avalue : null;
                if (this.fOverrideNamespaces && prefix.equals(element.prefix) && HTMLElements.getElement(element.localpart, null) != null) {
                    uri = this.fNamespacesURI;
                }
                this.fNamespaceContext.declarePrefix(prefix, uri);
            }
        }
        String prefix2 = (element.prefix != null) ? element.prefix : "";
        element.uri = this.fNamespaceContext.getURI(prefix2);
        if (element.uri != null && element.prefix == null) {
            element.prefix = "";
        }
        if (this.fInsertNamespaces && attrs != null && HTMLElements.getElement(element.localpart, null) != null && (element.prefix == null || this.fNamespaceContext.getURI(element.prefix) == null)) {
            final String xmlns = "xmlns" + ((element.prefix != null) ? (":" + element.prefix) : "");
            this.fQName.setValues((String)null, xmlns, xmlns, (String)null);
            attrs.addAttribute(this.fQName, "CDATA", this.fNamespacesURI);
            this.bindNamespaces(element, attrs);
            return;
        }
        attrCount = ((attrs != null) ? attrs.getLength() : 0);
        for (int j = 0; j < attrCount; ++j) {
            attrs.getName(j, this.fQName);
            splitQName(this.fQName);
            prefix2 = (this.fQName.rawname.equals("xmlns") ? "xmlns" : ((this.fQName.prefix != null) ? this.fQName.prefix : ""));
            if (!prefix2.equals("")) {
                this.fQName.uri = (prefix2.equals("xml") ? "http://www.w3.org/XML/1998/namespace" : this.fNamespaceContext.getURI(prefix2));
            }
            if (prefix2.equals("xmlns") && this.fQName.uri == null) {
                this.fQName.uri = "http://www.w3.org/2000/xmlns/";
            }
            attrs.setName(j, this.fQName);
        }
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/namespaces", "http://cyberneko.org/html/features/override-namespaces", "http://cyberneko.org/html/features/insert-namespaces" };
        FEATURE_DEFAULTS = new Boolean[] { null, Boolean.FALSE, Boolean.FALSE };
        RECOGNIZED_PROPERTIES = new String[] { "http://cyberneko.org/html/properties/names/elems", "http://cyberneko.org/html/properties/names/attrs", "http://cyberneko.org/html/properties/namespaces-uri" };
        PROPERTY_DEFAULTS = new Object[] { null, null, "http://www.w3.org/1999/xhtml" };
    }
    
    public static class NamespaceSupport implements NamespaceContext
    {
        protected int fTop;
        protected int[] fLevels;
        protected Entry[] fEntries;
        
        public NamespaceSupport() {
            this.fTop = 0;
            this.fLevels = new int[10];
            this.fEntries = new Entry[10];
            this.pushContext();
            this.declarePrefix("xml", NamespaceContext.XML_URI);
            this.declarePrefix("xmlns", NamespaceContext.XMLNS_URI);
        }
        
        public String getURI(final String prefix) {
            for (int i = this.fLevels[this.fTop] - 1; i >= 0; --i) {
                final Entry entry = this.fEntries[i];
                if (entry.prefix.equals(prefix)) {
                    return entry.uri;
                }
            }
            return null;
        }
        
        public int getDeclaredPrefixCount() {
            return this.fLevels[this.fTop] - this.fLevels[this.fTop - 1];
        }
        
        public String getDeclaredPrefixAt(final int index) {
            return this.fEntries[this.fLevels[this.fTop - 1] + index].prefix;
        }
        
        public NamespaceContext getParentContext() {
            return (NamespaceContext)this;
        }
        
        public void reset() {
            this.fLevels[this.fTop = 1] = this.fLevels[this.fTop - 1];
        }
        
        public void pushContext() {
            if (++this.fTop == this.fLevels.length) {
                final int[] iarray = new int[this.fLevels.length + 10];
                System.arraycopy(this.fLevels, 0, iarray, 0, this.fLevels.length);
                this.fLevels = iarray;
            }
            this.fLevels[this.fTop] = this.fLevels[this.fTop - 1];
        }
        
        public void popContext() {
            if (this.fTop > 1) {
                --this.fTop;
            }
        }
        
        public boolean declarePrefix(final String prefix, final String uri) {
            for (int count = this.getDeclaredPrefixCount(), i = 0; i < count; ++i) {
                final String dprefix = this.getDeclaredPrefixAt(i);
                if (dprefix.equals(prefix)) {
                    return false;
                }
            }
            final Entry entry = new Entry(prefix, uri);
            if (this.fLevels[this.fTop] == this.fEntries.length) {
                final Entry[] earray = new Entry[this.fEntries.length + 10];
                System.arraycopy(this.fEntries, 0, earray, 0, this.fEntries.length);
                this.fEntries = earray;
            }
            this.fEntries[this.fLevels[this.fTop]++] = entry;
            return true;
        }
        
        public String getPrefix(final String uri) {
            for (int i = this.fLevels[this.fTop] - 1; i >= 0; --i) {
                final Entry entry = this.fEntries[i];
                if (entry.uri.equals(uri)) {
                    return entry.prefix;
                }
            }
            return null;
        }
        
        public Enumeration getAllPrefixes() {
            final Vector prefixes = new Vector();
            for (int i = this.fLevels[1]; i < this.fLevels[this.fTop]; ++i) {
                final String prefix = this.fEntries[i].prefix;
                if (!prefixes.contains(prefix)) {
                    prefixes.addElement(prefix);
                }
            }
            return prefixes.elements();
        }
        
        static class Entry
        {
            public String prefix;
            public String uri;
            
            public Entry(final String prefix, final String uri) {
                this.prefix = prefix;
                this.uri = uri;
            }
        }
    }
}
