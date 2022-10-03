package sun.security.pkcs11;

import java.util.Collections;
import java.util.HashSet;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import java.security.ProviderException;
import java.util.Set;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.PhantomReference;

final class SessionKeyRef extends PhantomReference<P11Key>
{
    private static ReferenceQueue<P11Key> refQueue;
    private static Set<SessionKeyRef> refSet;
    private long keyID;
    private Session session;
    private boolean wrapperKeyUsed;
    
    static ReferenceQueue<P11Key> referenceQueue() {
        return SessionKeyRef.refQueue;
    }
    
    private static void drainRefQueueBounded() {
        while (true) {
            final SessionKeyRef sessionKeyRef = (SessionKeyRef)SessionKeyRef.refQueue.poll();
            if (sessionKeyRef == null) {
                break;
            }
            sessionKeyRef.dispose();
        }
    }
    
    SessionKeyRef(final P11Key p11Key, final long n, final boolean wrapperKeyUsed, final Session session) {
        super(p11Key, SessionKeyRef.refQueue);
        if (session == null) {
            throw new ProviderException("key must be associated with a session");
        }
        this.registerNativeKey(n, session);
        this.wrapperKeyUsed = wrapperKeyUsed;
        SessionKeyRef.refSet.add(this);
        drainRefQueueBounded();
    }
    
    void registerNativeKey(final long n, final Session session) {
        assert n != 0L;
        assert session != null;
        this.updateNativeKey(n, session);
    }
    
    void removeNativeKey() {
        assert this.session != null;
        this.updateNativeKey(0L, null);
    }
    
    private void updateNativeKey(final long keyID, final Session session) {
        if (keyID == 0L) {
            assert session == null;
            final Token token = this.session.token;
            if (token.isValid()) {
                Session opSession = null;
                try {
                    opSession = token.getOpSession();
                    token.p11.C_DestroyObject(opSession.id(), this.keyID);
                }
                catch (final PKCS11Exception ex) {}
                finally {
                    token.releaseSession(opSession);
                }
            }
            this.session.removeObject();
        }
        else {
            session.addObject();
        }
        this.keyID = keyID;
        this.session = session;
    }
    
    void dispose() {
        if (this.wrapperKeyUsed) {
            NativeKeyHolder.decWrapperKeyRef();
        }
        if (this.keyID != 0L) {
            this.removeNativeKey();
        }
        SessionKeyRef.refSet.remove(this);
        this.clear();
    }
    
    static {
        SessionKeyRef.refQueue = new ReferenceQueue<P11Key>();
        SessionKeyRef.refSet = Collections.synchronizedSet(new HashSet<SessionKeyRef>());
    }
}
