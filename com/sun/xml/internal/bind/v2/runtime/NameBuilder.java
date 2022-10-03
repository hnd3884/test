package com.sun.xml.internal.bind.v2.runtime;

import java.util.Iterator;
import javax.xml.namespace.QName;
import java.util.HashSet;
import java.util.HashMap;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.util.Set;
import java.util.Map;

public final class NameBuilder
{
    private Map<String, Integer> uriIndexMap;
    private Set<String> nonDefaultableNsUris;
    private Map<String, Integer> localNameIndexMap;
    private QNameMap<Integer> elementQNameIndexMap;
    private QNameMap<Integer> attributeQNameIndexMap;
    
    public NameBuilder() {
        this.uriIndexMap = new HashMap<String, Integer>();
        this.nonDefaultableNsUris = new HashSet<String>();
        this.localNameIndexMap = new HashMap<String, Integer>();
        this.elementQNameIndexMap = new QNameMap<Integer>();
        this.attributeQNameIndexMap = new QNameMap<Integer>();
    }
    
    public Name createElementName(final QName name) {
        return this.createElementName(name.getNamespaceURI(), name.getLocalPart());
    }
    
    public Name createElementName(final String nsUri, final String localName) {
        return this.createName(nsUri, localName, false, this.elementQNameIndexMap);
    }
    
    public Name createAttributeName(final QName name) {
        return this.createAttributeName(name.getNamespaceURI(), name.getLocalPart());
    }
    
    public Name createAttributeName(final String nsUri, final String localName) {
        assert nsUri.intern() == nsUri;
        assert localName.intern() == localName;
        if (nsUri.length() == 0) {
            return new Name(this.allocIndex(this.attributeQNameIndexMap, "", localName), -1, nsUri, this.allocIndex(this.localNameIndexMap, localName), localName, true);
        }
        this.nonDefaultableNsUris.add(nsUri);
        return this.createName(nsUri, localName, true, this.attributeQNameIndexMap);
    }
    
    private Name createName(final String nsUri, final String localName, final boolean isAttribute, final QNameMap<Integer> map) {
        assert nsUri.intern() == nsUri;
        assert localName.intern() == localName;
        return new Name(this.allocIndex(map, nsUri, localName), this.allocIndex(this.uriIndexMap, nsUri), nsUri, this.allocIndex(this.localNameIndexMap, localName), localName, isAttribute);
    }
    
    private int allocIndex(final Map<String, Integer> map, final String str) {
        Integer i = map.get(str);
        if (i == null) {
            i = map.size();
            map.put(str, i);
        }
        return i;
    }
    
    private int allocIndex(final QNameMap<Integer> map, final String nsUri, final String localName) {
        Integer i = map.get(nsUri, localName);
        if (i == null) {
            i = map.size();
            map.put(nsUri, localName, i);
        }
        return i;
    }
    
    public NameList conclude() {
        final boolean[] nsUriCannotBeDefaulted = new boolean[this.uriIndexMap.size()];
        for (final Map.Entry<String, Integer> e : this.uriIndexMap.entrySet()) {
            nsUriCannotBeDefaulted[e.getValue()] = this.nonDefaultableNsUris.contains(e.getKey());
        }
        final NameList r = new NameList(this.list(this.uriIndexMap), nsUriCannotBeDefaulted, this.list(this.localNameIndexMap), this.elementQNameIndexMap.size(), this.attributeQNameIndexMap.size());
        this.uriIndexMap = null;
        this.localNameIndexMap = null;
        return r;
    }
    
    private String[] list(final Map<String, Integer> map) {
        final String[] r = new String[map.size()];
        for (final Map.Entry<String, Integer> e : map.entrySet()) {
            r[e.getValue()] = e.getKey();
        }
        return r;
    }
}
