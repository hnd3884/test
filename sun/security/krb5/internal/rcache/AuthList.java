package sun.security.krb5.internal.rcache;

import java.util.Iterator;
import java.util.ListIterator;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.KerberosTime;
import java.util.LinkedList;

public class AuthList
{
    private final LinkedList<AuthTimeWithHash> entries;
    private final int lifespan;
    private volatile int oldestTime;
    
    public AuthList(final int lifespan) {
        this.oldestTime = Integer.MIN_VALUE;
        this.lifespan = lifespan;
        this.entries = new LinkedList<AuthTimeWithHash>();
    }
    
    public synchronized void put(final AuthTimeWithHash authTimeWithHash, final KerberosTime kerberosTime) throws KrbApErrException {
        if (this.entries.isEmpty()) {
            this.entries.addFirst(authTimeWithHash);
            this.oldestTime = authTimeWithHash.ctime;
            return;
        }
        final int compareTo = this.entries.getFirst().compareTo(authTimeWithHash);
        if (compareTo < 0) {
            this.entries.addFirst(authTimeWithHash);
        }
        else {
            if (compareTo == 0) {
                throw new KrbApErrException(34);
            }
            final ListIterator<AuthTimeWithHash> listIterator = this.entries.listIterator(1);
            boolean b = false;
            while (listIterator.hasNext()) {
                final AuthTimeWithHash authTimeWithHash2 = listIterator.next();
                final int compareTo2 = authTimeWithHash2.compareTo(authTimeWithHash);
                if (compareTo2 < 0) {
                    this.entries.add(this.entries.indexOf(authTimeWithHash2), authTimeWithHash);
                    b = true;
                    break;
                }
                if (compareTo2 == 0) {
                    throw new KrbApErrException(34);
                }
            }
            if (!b) {
                this.entries.addLast(authTimeWithHash);
            }
        }
        final long n = kerberosTime.getSeconds() - this.lifespan;
        if (this.oldestTime > n - 5L) {
            return;
        }
        while (!this.entries.isEmpty()) {
            final AuthTimeWithHash authTimeWithHash3 = this.entries.removeLast();
            if (authTimeWithHash3.ctime >= n) {
                this.entries.addLast(authTimeWithHash3);
                this.oldestTime = authTimeWithHash3.ctime;
                return;
            }
        }
        this.oldestTime = Integer.MIN_VALUE;
    }
    
    public boolean isEmpty() {
        return this.entries.isEmpty();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final Iterator<AuthTimeWithHash> descendingIterator = this.entries.descendingIterator();
        int size = this.entries.size();
        while (descendingIterator.hasNext()) {
            sb.append('#').append(size--).append(": ").append(descendingIterator.next().toString()).append('\n');
        }
        return sb.toString();
    }
}
