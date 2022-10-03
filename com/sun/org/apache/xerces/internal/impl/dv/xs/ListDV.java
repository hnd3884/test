package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import java.util.AbstractList;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class ListDV extends TypeValidator
{
    @Override
    public short getAllowedFacets() {
        return 2079;
    }
    
    @Override
    public Object getActualValue(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        return content;
    }
    
    @Override
    public int getDataLength(final Object value) {
        return ((ListData)value).getLength();
    }
    
    static final class ListData extends AbstractList implements ObjectList
    {
        final Object[] data;
        private String canonical;
        
        public ListData(final Object[] data) {
            this.data = data;
        }
        
        @Override
        public synchronized String toString() {
            if (this.canonical == null) {
                final int len = this.data.length;
                final StringBuffer buf = new StringBuffer();
                if (len > 0) {
                    buf.append(this.data[0].toString());
                }
                for (int i = 1; i < len; ++i) {
                    buf.append(' ');
                    buf.append(this.data[i].toString());
                }
                this.canonical = buf.toString();
            }
            return this.canonical;
        }
        
        @Override
        public int getLength() {
            return this.data.length;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof ListData)) {
                return false;
            }
            final Object[] odata = ((ListData)obj).data;
            final int count = this.data.length;
            if (count != odata.length) {
                return false;
            }
            for (int i = 0; i < count; ++i) {
                if (!this.data[i].equals(odata[i])) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hash = 0;
            for (int i = 0; i < this.data.length; ++i) {
                hash ^= this.data[i].hashCode();
            }
            return hash;
        }
        
        @Override
        public boolean contains(final Object item) {
            for (int i = 0; i < this.data.length; ++i) {
                if (item == this.data[i]) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public Object item(final int index) {
            if (index < 0 || index >= this.data.length) {
                return null;
            }
            return this.data[index];
        }
        
        @Override
        public Object get(final int index) {
            if (index >= 0 && index < this.data.length) {
                return this.data[index];
            }
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        
        @Override
        public int size() {
            return this.getLength();
        }
    }
}
