package sun.security.acl;

import java.util.NoSuchElementException;
import java.util.Hashtable;
import java.security.acl.Acl;
import java.security.acl.AclEntry;
import java.util.Enumeration;

final class AclEnumerator implements Enumeration<AclEntry>
{
    Acl acl;
    Enumeration<AclEntry> u1;
    Enumeration<AclEntry> u2;
    Enumeration<AclEntry> g1;
    Enumeration<AclEntry> g2;
    
    AclEnumerator(final Acl acl, final Hashtable<?, AclEntry> hashtable, final Hashtable<?, AclEntry> hashtable2, final Hashtable<?, AclEntry> hashtable3, final Hashtable<?, AclEntry> hashtable4) {
        this.acl = acl;
        this.u1 = hashtable.elements();
        this.u2 = hashtable3.elements();
        this.g1 = hashtable2.elements();
        this.g2 = hashtable4.elements();
    }
    
    @Override
    public boolean hasMoreElements() {
        return this.u1.hasMoreElements() || this.u2.hasMoreElements() || this.g1.hasMoreElements() || this.g2.hasMoreElements();
    }
    
    @Override
    public AclEntry nextElement() {
        synchronized (this.acl) {
            if (this.u1.hasMoreElements()) {
                return this.u1.nextElement();
            }
            if (this.u2.hasMoreElements()) {
                return this.u2.nextElement();
            }
            if (this.g1.hasMoreElements()) {
                return this.g1.nextElement();
            }
            if (this.g2.hasMoreElements()) {
                return this.g2.nextElement();
            }
        }
        throw new NoSuchElementException("Acl Enumerator");
    }
}
