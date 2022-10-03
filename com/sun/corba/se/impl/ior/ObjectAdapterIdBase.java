package com.sun.corba.se.impl.ior;

import org.omg.CORBA_2_3.portable.OutputStream;
import java.util.Iterator;
import com.sun.corba.se.spi.ior.ObjectAdapterId;

abstract class ObjectAdapterIdBase implements ObjectAdapterId
{
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ObjectAdapterId)) {
            return false;
        }
        final ObjectAdapterId objectAdapterId = (ObjectAdapterId)o;
        final Iterator iterator = this.iterator();
        final Iterator iterator2 = objectAdapterId.iterator();
        while (iterator.hasNext() && iterator2.hasNext()) {
            if (!iterator.next().equals(iterator2.next())) {
                return false;
            }
        }
        return iterator.hasNext() == iterator2.hasNext();
    }
    
    @Override
    public int hashCode() {
        int n = 17;
        final Iterator iterator = this.iterator();
        while (iterator.hasNext()) {
            n = 37 * n + ((String)iterator.next()).hashCode();
        }
        return n;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("ObjectAdapterID[");
        final Iterator iterator = this.iterator();
        int n = 1;
        while (iterator.hasNext()) {
            final String s = iterator.next();
            if (n != 0) {
                n = 0;
            }
            else {
                sb.append("/");
            }
            sb.append(s);
        }
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public void write(final OutputStream outputStream) {
        outputStream.write_long(this.getNumLevels());
        final Iterator iterator = this.iterator();
        while (iterator.hasNext()) {
            outputStream.write_string((String)iterator.next());
        }
    }
}
