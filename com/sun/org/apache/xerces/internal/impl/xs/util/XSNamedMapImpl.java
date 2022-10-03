package com.sun.org.apache.xerces.internal.impl.xs.util;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.AbstractSet;
import javax.xml.namespace.QName;
import java.util.Set;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
import java.util.AbstractMap;

public class XSNamedMapImpl extends AbstractMap implements XSNamedMap
{
    public static final XSNamedMapImpl EMPTY_MAP;
    final String[] fNamespaces;
    final int fNSNum;
    final SymbolHash[] fMaps;
    XSObject[] fArray;
    int fLength;
    private Set fEntrySet;
    
    public XSNamedMapImpl(final String namespace, final SymbolHash map) {
        this.fArray = null;
        this.fLength = -1;
        this.fEntrySet = null;
        this.fNamespaces = new String[] { namespace };
        this.fMaps = new SymbolHash[] { map };
        this.fNSNum = 1;
    }
    
    public XSNamedMapImpl(final String[] namespaces, final SymbolHash[] maps, final int num) {
        this.fArray = null;
        this.fLength = -1;
        this.fEntrySet = null;
        this.fNamespaces = namespaces;
        this.fMaps = maps;
        this.fNSNum = num;
    }
    
    public XSNamedMapImpl(final XSObject[] array, final int length) {
        this.fArray = null;
        this.fLength = -1;
        this.fEntrySet = null;
        if (length == 0) {
            this.fNamespaces = null;
            this.fMaps = null;
            this.fNSNum = 0;
            this.fArray = array;
            this.fLength = 0;
            return;
        }
        this.fNamespaces = new String[] { array[0].getNamespace() };
        this.fMaps = null;
        this.fNSNum = 1;
        this.fArray = array;
        this.fLength = length;
    }
    
    @Override
    public synchronized int getLength() {
        if (this.fLength == -1) {
            this.fLength = 0;
            for (int i = 0; i < this.fNSNum; ++i) {
                this.fLength += this.fMaps[i].getLength();
            }
        }
        return this.fLength;
    }
    
    @Override
    public XSObject itemByName(final String namespace, final String localName) {
        int i = 0;
        while (i < this.fNSNum) {
            if (isEqual(namespace, this.fNamespaces[i])) {
                if (this.fMaps != null) {
                    return (XSObject)this.fMaps[i].get(localName);
                }
                for (int j = 0; j < this.fLength; ++j) {
                    final XSObject ret = this.fArray[j];
                    if (ret.getName().equals(localName)) {
                        return ret;
                    }
                }
                return null;
            }
            else {
                ++i;
            }
        }
        return null;
    }
    
    @Override
    public synchronized XSObject item(final int index) {
        if (this.fArray == null) {
            this.getLength();
            this.fArray = new XSObject[this.fLength];
            int pos = 0;
            for (int i = 0; i < this.fNSNum; ++i) {
                pos += this.fMaps[i].getValues(this.fArray, pos);
            }
        }
        if (index < 0 || index >= this.fLength) {
            return null;
        }
        return this.fArray[index];
    }
    
    static boolean isEqual(final String one, final String two) {
        return (one != null) ? one.equals(two) : (two == null);
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.get(key) != null;
    }
    
    @Override
    public Object get(final Object key) {
        if (key instanceof QName) {
            final QName name = (QName)key;
            String namespaceURI = name.getNamespaceURI();
            if ("".equals(namespaceURI)) {
                namespaceURI = null;
            }
            final String localPart = name.getLocalPart();
            return this.itemByName(namespaceURI, localPart);
        }
        return null;
    }
    
    @Override
    public int size() {
        return this.getLength();
    }
    
    @Override
    public synchronized Set entrySet() {
        if (this.fEntrySet == null) {
            final int length = this.getLength();
            final XSNamedMapEntry[] entries = new XSNamedMapEntry[length];
            for (int i = 0; i < length; ++i) {
                final XSObject xso = this.item(i);
                entries[i] = new XSNamedMapEntry(new QName(xso.getNamespace(), xso.getName()), xso);
            }
            this.fEntrySet = new AbstractSet() {
                @Override
                public Iterator iterator() {
                    return new Iterator() {
                        private int index = 0;
                        
                        @Override
                        public boolean hasNext() {
                            return this.index < length;
                        }
                        
                        @Override
                        public Object next() {
                            if (this.index < length) {
                                return entries[this.index++];
                            }
                            throw new NoSuchElementException();
                        }
                        
                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
                
                @Override
                public int size() {
                    return length;
                }
            };
        }
        return this.fEntrySet;
    }
    
    static {
        EMPTY_MAP = new XSNamedMapImpl(new XSObject[0], 0);
    }
    
    private static final class XSNamedMapEntry implements Map.Entry
    {
        private final QName key;
        private final XSObject value;
        
        public XSNamedMapEntry(final QName key, final XSObject value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public Object getKey() {
            return this.key;
        }
        
        @Override
        public Object getValue() {
            return this.value;
        }
        
        @Override
        public Object setValue(final Object value) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o instanceof Map.Entry) {
                final Map.Entry e = (Map.Entry)o;
                final Object otherKey = e.getKey();
                final Object otherValue = e.getValue();
                if (this.key == null) {
                    if (otherKey != null) {
                        return false;
                    }
                }
                else if (!this.key.equals(otherKey)) {
                    return false;
                }
                if ((this.value != null) ? this.value.equals(otherValue) : (otherValue == null)) {
                    return true;
                }
                return false;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return ((this.key == null) ? 0 : this.key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode());
        }
        
        @Override
        public String toString() {
            final StringBuffer buffer = new StringBuffer();
            buffer.append(String.valueOf(this.key));
            buffer.append('=');
            buffer.append(String.valueOf(this.value));
            return buffer.toString();
        }
    }
}
