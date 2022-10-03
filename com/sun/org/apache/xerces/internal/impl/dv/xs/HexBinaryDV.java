package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.util.ByteListImpl;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class HexBinaryDV extends TypeValidator
{
    @Override
    public short getAllowedFacets() {
        return 2079;
    }
    
    @Override
    public Object getActualValue(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        final byte[] decoded = HexBin.decode(content);
        if (decoded == null) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { content, "hexBinary" });
        }
        return new XHex(decoded);
    }
    
    @Override
    public int getDataLength(final Object value) {
        return ((XHex)value).getLength();
    }
    
    private static final class XHex extends ByteListImpl
    {
        public XHex(final byte[] data) {
            super(data);
        }
        
        @Override
        public synchronized String toString() {
            if (this.canonical == null) {
                this.canonical = HexBin.encode(this.data);
            }
            return this.canonical;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof XHex)) {
                return false;
            }
            final byte[] odata = ((XHex)obj).data;
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
