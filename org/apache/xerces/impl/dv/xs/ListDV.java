package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.xs.datatypes.ObjectList;
import java.util.AbstractList;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

public class ListDV extends TypeValidator
{
    public Object getActualValue(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        return s;
    }
    
    public int getDataLength(final Object o) {
        return ((ListData)o).getLength();
    }
    
    static final class ListData extends AbstractList implements ObjectList
    {
        final Object[] data;
        private String canonical;
        
        public ListData(final Object[] data) {
            this.data = data;
        }
        
        public synchronized String toString() {
            if (this.canonical == null) {
                final int length = this.data.length;
                final StringBuffer sb = new StringBuffer();
                if (length > 0) {
                    sb.append(this.data[0].toString());
                }
                for (int i = 1; i < length; ++i) {
                    sb.append(' ');
                    sb.append(this.data[i].toString());
                }
                this.canonical = sb.toString();
            }
            return this.canonical;
        }
        
        public int getLength() {
            return this.data.length;
        }
        
        public boolean equals(final Object o) {
            if (!(o instanceof ListData)) {
                return false;
            }
            final Object[] data = ((ListData)o).data;
            final int length = this.data.length;
            if (length != data.length) {
                return false;
            }
            for (int i = 0; i < length; ++i) {
                if (!this.data[i].equals(data[i])) {
                    return false;
                }
            }
            return true;
        }
        
        public int hashCode() {
            int n = 0;
            for (int i = 0; i < this.data.length; ++i) {
                n ^= this.data[i].hashCode();
            }
            return n;
        }
        
        public boolean contains(final Object o) {
            for (int i = 0; i < this.data.length; ++i) {
                if (o == this.data[i]) {
                    return true;
                }
            }
            return false;
        }
        
        public Object item(final int n) {
            if (n < 0 || n >= this.data.length) {
                return null;
            }
            return this.data[n];
        }
        
        public Object get(final int n) {
            if (n >= 0 && n < this.data.length) {
                return this.data[n];
            }
            throw new IndexOutOfBoundsException("Index: " + n);
        }
        
        public int size() {
            return this.getLength();
        }
    }
}
