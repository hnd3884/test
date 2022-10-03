package sun.misc;

import java.util.NoSuchElementException;
import java.util.Enumeration;

public class CompoundEnumeration<E> implements Enumeration<E>
{
    private Enumeration<E>[] enums;
    private int index;
    
    public CompoundEnumeration(final Enumeration<E>[] enums) {
        this.index = 0;
        this.enums = enums;
    }
    
    private boolean next() {
        while (this.index < this.enums.length) {
            if (this.enums[this.index] != null && this.enums[this.index].hasMoreElements()) {
                return true;
            }
            ++this.index;
        }
        return false;
    }
    
    @Override
    public boolean hasMoreElements() {
        return this.next();
    }
    
    @Override
    public E nextElement() {
        if (!this.next()) {
            throw new NoSuchElementException();
        }
        return this.enums[this.index].nextElement();
    }
}
