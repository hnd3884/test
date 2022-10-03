package sun.security.pkcs11;

import java.util.SortedSet;
import java.util.Collections;
import java.util.TreeSet;
import java.security.ProviderException;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import java.util.Set;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.PhantomReference;

final class SessionRef extends PhantomReference<Session> implements Comparable<SessionRef>
{
    private static ReferenceQueue<Session> refQueue;
    private static Set<SessionRef> refList;
    private long id;
    private Token token;
    
    static ReferenceQueue<Session> referenceQueue() {
        return SessionRef.refQueue;
    }
    
    static int totalCount() {
        return SessionRef.refList.size();
    }
    
    private static void drainRefQueueBounded() {
        while (true) {
            final SessionRef sessionRef = (SessionRef)SessionRef.refQueue.poll();
            if (sessionRef == null) {
                break;
            }
            sessionRef.dispose();
        }
    }
    
    SessionRef(final Session session, final long id, final Token token) {
        super(session, SessionRef.refQueue);
        this.id = id;
        this.token = token;
        SessionRef.refList.add(this);
        drainRefQueueBounded();
    }
    
    void dispose() {
        SessionRef.refList.remove(this);
        try {
            if (this.token.isPresent(this.id)) {
                this.token.p11.C_CloseSession(this.id);
            }
        }
        catch (final PKCS11Exception ex) {}
        catch (final ProviderException ex2) {}
        finally {
            this.clear();
        }
    }
    
    @Override
    public int compareTo(final SessionRef sessionRef) {
        if (this.id == sessionRef.id) {
            return 0;
        }
        return (this.id < sessionRef.id) ? -1 : 1;
    }
    
    static {
        SessionRef.refQueue = new ReferenceQueue<Session>();
        SessionRef.refList = (Set<SessionRef>)Collections.synchronizedSortedSet(new TreeSet<Object>());
    }
}
