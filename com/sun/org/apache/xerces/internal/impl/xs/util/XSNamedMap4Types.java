package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.util.SymbolHash;

public final class XSNamedMap4Types extends XSNamedMapImpl
{
    private final short fType;
    
    public XSNamedMap4Types(final String namespace, final SymbolHash map, final short type) {
        super(namespace, map);
        this.fType = type;
    }
    
    public XSNamedMap4Types(final String[] namespaces, final SymbolHash[] maps, final int num, final short type) {
        super(namespaces, maps, num);
        this.fType = type;
    }
    
    @Override
    public synchronized int getLength() {
        if (this.fLength == -1) {
            int length = 0;
            for (int i = 0; i < this.fNSNum; ++i) {
                length += this.fMaps[i].getLength();
            }
            int pos = 0;
            final XSObject[] array = new XSObject[length];
            for (int j = 0; j < this.fNSNum; ++j) {
                pos += this.fMaps[j].getValues(array, pos);
            }
            this.fLength = 0;
            this.fArray = new XSObject[length];
            for (int k = 0; k < length; ++k) {
                final XSTypeDefinition type = (XSTypeDefinition)array[k];
                if (type.getTypeCategory() == this.fType) {
                    this.fArray[this.fLength++] = type;
                }
            }
        }
        return this.fLength;
    }
    
    @Override
    public XSObject itemByName(final String namespace, final String localName) {
        int i = 0;
        while (i < this.fNSNum) {
            if (XSNamedMapImpl.isEqual(namespace, this.fNamespaces[i])) {
                final XSTypeDefinition type = (XSTypeDefinition)this.fMaps[i].get(localName);
                if (type != null && type.getTypeCategory() == this.fType) {
                    return type;
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
        }
        if (index < 0 || index >= this.fLength) {
            return null;
        }
        return this.fArray[index];
    }
}
