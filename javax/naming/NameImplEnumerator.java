package javax.naming;

import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.Enumeration;

final class NameImplEnumerator implements Enumeration<String>
{
    Vector<String> vector;
    int count;
    int limit;
    
    NameImplEnumerator(final Vector<String> vector, final int count, final int limit) {
        this.vector = vector;
        this.count = count;
        this.limit = limit;
    }
    
    @Override
    public boolean hasMoreElements() {
        return this.count < this.limit;
    }
    
    @Override
    public String nextElement() {
        if (this.count < this.limit) {
            return this.vector.elementAt(this.count++);
        }
        throw new NoSuchElementException("NameImplEnumerator");
    }
}
