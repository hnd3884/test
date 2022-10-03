package sun.util;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Set;
import java.util.Enumeration;

public class ResourceBundleEnumeration implements Enumeration<String>
{
    Set<String> set;
    Iterator<String> iterator;
    Enumeration<String> enumeration;
    String next;
    
    public ResourceBundleEnumeration(final Set<String> set, final Enumeration<String> enumeration) {
        this.next = null;
        this.set = set;
        this.iterator = set.iterator();
        this.enumeration = enumeration;
    }
    
    @Override
    public boolean hasMoreElements() {
        if (this.next == null) {
            if (this.iterator.hasNext()) {
                this.next = this.iterator.next();
            }
            else if (this.enumeration != null) {
                while (this.next == null && this.enumeration.hasMoreElements()) {
                    this.next = this.enumeration.nextElement();
                    if (this.set.contains(this.next)) {
                        this.next = null;
                    }
                }
            }
        }
        return this.next != null;
    }
    
    @Override
    public String nextElement() {
        if (this.hasMoreElements()) {
            final String next = this.next;
            this.next = null;
            return next;
        }
        throw new NoSuchElementException();
    }
}
