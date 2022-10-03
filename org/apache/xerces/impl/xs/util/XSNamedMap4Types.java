package org.apache.xerces.impl.xs.util;

import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.util.SymbolHash;

public final class XSNamedMap4Types extends XSNamedMapImpl
{
    private final short fType;
    
    public XSNamedMap4Types(final String s, final SymbolHash symbolHash, final short fType) {
        super(s, symbolHash);
        this.fType = fType;
    }
    
    public XSNamedMap4Types(final String[] array, final SymbolHash[] array2, final int n, final short fType) {
        super(array, array2, n);
        this.fType = fType;
    }
    
    public synchronized int getLength() {
        if (this.fLength == -1) {
            int n = 0;
            for (int i = 0; i < this.fNSNum; ++i) {
                n += this.fMaps[i].getLength();
            }
            int n2 = 0;
            final XSObject[] array = new XSObject[n];
            for (int j = 0; j < this.fNSNum; ++j) {
                n2 += this.fMaps[j].getValues(array, n2);
            }
            this.fLength = 0;
            this.fArray = new XSObject[n];
            for (int k = 0; k < n; ++k) {
                final XSTypeDefinition xsTypeDefinition = (XSTypeDefinition)array[k];
                if (xsTypeDefinition.getTypeCategory() == this.fType) {
                    this.fArray[this.fLength++] = xsTypeDefinition;
                }
            }
        }
        return this.fLength;
    }
    
    public XSObject itemByName(final String s, final String s2) {
        int i = 0;
        while (i < this.fNSNum) {
            if (XSNamedMapImpl.isEqual(s, this.fNamespaces[i])) {
                final XSTypeDefinition xsTypeDefinition = (XSTypeDefinition)this.fMaps[i].get(s2);
                if (xsTypeDefinition != null && xsTypeDefinition.getTypeCategory() == this.fType) {
                    return xsTypeDefinition;
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
        }
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        return this.fArray[n];
    }
}
