package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.util.ByteListImpl;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class Base64BinaryDV extends TypeValidator
{
    @Override
    public short getAllowedFacets() {
        return 2079;
    }
    
    @Override
    public Object getActualValue(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        final byte[] decoded = Base64.decode(content);
        if (decoded == null) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { content, "base64Binary" });
        }
        return new XBase64(decoded);
    }
    
    @Override
    public int getDataLength(final Object value) {
        return ((XBase64)value).getLength();
    }
    
    private static final class XBase64 extends ByteListImpl
    {
        public XBase64(final byte[] data) {
            super(data);
        }
        
        @Override
        public synchronized String toString() {
            if (this.canonical == null) {
                this.canonical = Base64.encode(this.data);
            }
            return this.canonical;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof XBase64)) {
                return false;
            }
            final byte[] odata = ((XBase64)obj).data;
            final int len = this.data.length;
            if (len != odata.length) {
                return false;
            }
            for (int i = 0; i < len; ++i) {
                if (this.data[i] != odata[i]) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hash = 0;
            for (int i = 0; i < this.data.length; ++i) {
                hash = hash * 37 + (this.data[i] & 0xFF);
            }
            return hash;
        }
    }
}
