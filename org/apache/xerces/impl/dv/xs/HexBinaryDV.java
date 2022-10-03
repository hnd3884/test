package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.util.ByteListImpl;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.util.HexBin;
import org.apache.xerces.impl.dv.ValidationContext;

public class HexBinaryDV extends TypeValidator
{
    public Object getActualValue(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        final byte[] decode = HexBin.decode(s);
        if (decode == null) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s, "hexBinary" });
        }
        return new XHex(decode);
    }
    
    public int getDataLength(final Object o) {
        return ((XHex)o).getLength();
    }
    
    private static final class XHex extends ByteListImpl
    {
        public XHex(final byte[] array) {
            super(array);
        }
        
        public synchronized String toString() {
            if (this.canonical == null) {
                this.canonical = HexBin.encode(this.data);
            }
            return this.canonical;
        }
        
        public boolean equals(final Object o) {
            if (!(o instanceof XHex)) {
                return false;
            }
            final byte[] data = ((XHex)o).data;
            final int length = this.data.length;
            if (length != data.length) {
                return false;
            }
            for (int i = 0; i < length; ++i) {
                if (this.data[i] != data[i]) {
                    return false;
                }
            }
            return true;
        }
        
        public int hashCode() {
            int n = 0;
            for (int i = 0; i < this.data.length; ++i) {
                n = n * 37 + (this.data[i] & 0xFF);
            }
            return n;
        }
    }
}
