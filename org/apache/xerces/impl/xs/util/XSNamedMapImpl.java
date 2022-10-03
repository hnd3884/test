package org.apache.xerces.impl.xs.util;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.AbstractSet;
import javax.xml.namespace.QName;
import java.util.Set;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.util.SymbolHash;
import org.apache.xerces.xs.XSNamedMap;
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
    
    public XSNamedMapImpl(final String s, final SymbolHash symbolHash) {
        this.fArray = null;
        this.fLength = -1;
        this.fEntrySet = null;
        this.fNamespaces = new String[] { s };
        this.fMaps = new SymbolHash[] { symbolHash };
        this.fNSNum = 1;
    }
    
    public XSNamedMapImpl(final String[] fNamespaces, final SymbolHash[] fMaps, final int fnsNum) {
        this.fArray = null;
        this.fLength = -1;
        this.fEntrySet = null;
        this.fNamespaces = fNamespaces;
        this.fMaps = fMaps;
        this.fNSNum = fnsNum;
    }
    
    public XSNamedMapImpl(final XSObject[] array, final int fLength) {
        this.fArray = null;
        this.fLength = -1;
        this.fEntrySet = null;
        if (fLength == 0) {
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
        this.fLength = fLength;
    }
    
    public synchronized int getLength() {
        if (this.fLength == -1) {
            this.fLength = 0;
            for (int i = 0; i < this.fNSNum; ++i) {
                this.fLength += this.fMaps[i].getLength();
            }
        }
        return this.fLength;
    }
    
    public XSObject itemByName(final String s, final String s2) {
        int i = 0;
        while (i < this.fNSNum) {
            if (isEqual(s, this.fNamespaces[i])) {
                if (this.fMaps != null) {
                    return (XSObject)this.fMaps[i].get(s2);
                }
                for (int j = 0; j < this.fLength; ++j) {
                    final XSObject xsObject = this.fArray[j];
                    if (xsObject.getName().equals(s2)) {
                        return xsObject;
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
    
    public synchronized XSObject item(final int n) {
        if (this.fArray == null) {
            this.getLength();
            this.fArray = new XSObject[this.fLength];
            int n2 = 0;
            for (int i = 0; i < this.fNSNum; ++i) {
                n2 += this.fMaps[i].getValues(this.fArray, n2);
            }
        }
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        return this.fArray[n];
    }
    
    static boolean isEqual(final String s, final String s2) {
        return (s != null) ? s.equals(s2) : (s2 == null);
    }
    
    public boolean containsKey(final Object o) {
        return this.get(o) != null;
    }
    
    public Object get(final Object o) {
        if (o instanceof QName) {
            final QName qName = (QName)o;
            String namespaceURI = qName.getNamespaceURI();
            if ("".equals(namespaceURI)) {
                namespaceURI = null;
            }
            return this.itemByName(namespaceURI, qName.getLocalPart());
        }
        return null;
    }
    
    public int size() {
        return this.getLength();
    }
    
    public synchronized Set entrySet() {
        if (this.fEntrySet == null) {
            final int length = this.getLength();
            final XSNamedMapEntry[] array = new XSNamedMapEntry[length];
            for (int i = 0; i < length; ++i) {
                final XSObject item = this.item(i);
                array[i] = new XSNamedMapEntry(new QName(item.getNamespace(), item.getName()), item);
            }
            this.fEntrySet = new AbstractSet() {
                public Iterator iterator() {
                    return new Iterator() {
                        private int index = 0;
                        
                        public boolean hasNext() {
                            return this.index < length;
                        }
                        
                        public Object next() {
                            if (this.index < length) {
                                return array[this.index++];
                            }
                            throw new NoSuchElementException();
                        }
                        
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
                
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
        
        public Object getKey() {
            return this.key;
        }
        
        public Object getValue() {
            return this.value;
        }
        
        public Object setValue(final Object o) {
            throw new UnsupportedOperationException();
        }
        
        public boolean equals(final Object o) {
            if (o instanceof Map.Entry) {
                final Map.Entry entry = (Map.Entry)o;
                final Object key = entry.getKey();
                final Object value = entry.getValue();
                if (this.key == null) {
                    if (key != null) {
                        return false;
                    }
                }
                else if (!this.key.equals(key)) {
                    return false;
                }
                if ((this.value != null) ? this.value.equals(value) : (value == null)) {
                    return true;
                }
                return false;
            }
            return false;
        }
        
        public int hashCode() {
            return ((this.key == null) ? 0 : this.key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode());
        }
        
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append(String.valueOf(this.key));
            sb.append('=');
            sb.append(String.valueOf(this.value));
            return sb.toString();
        }
    }
}
