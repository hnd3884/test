package sun.security.pkcs11;

import java.util.concurrent.ConcurrentLinkedDeque;
import sun.security.pkcs11.wrapper.CK_NOTIFY;
import java.security.ProviderException;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import java.util.concurrent.atomic.AtomicInteger;
import sun.security.util.Debug;

final class SessionManager
{
    private static final int DEFAULT_MAX_SESSIONS = 32;
    private static final Debug debug;
    private final Token token;
    private final int maxSessions;
    private AtomicInteger activeSessions;
    private final Pool objSessions;
    private final Pool opSessions;
    private int maxActiveSessions;
    private Object maxActiveSessionsLock;
    private final long openSessionFlags;
    
    SessionManager(final Token token) {
        this.activeSessions = new AtomicInteger();
        long n;
        if (token.isWriteProtected()) {
            this.openSessionFlags = 4L;
            n = token.tokenInfo.ulMaxSessionCount;
        }
        else {
            this.openSessionFlags = 6L;
            n = token.tokenInfo.ulMaxRwSessionCount;
        }
        if (n == 0L) {
            n = 2147483647L;
        }
        else if (n == -1L || n < 0L) {
            n = 32L;
        }
        this.maxSessions = (int)Math.min(n, 2147483647L);
        this.token = token;
        this.objSessions = new Pool(this);
        this.opSessions = new Pool(this);
        if (SessionManager.debug != null) {
            this.maxActiveSessionsLock = new Object();
        }
    }
    
    boolean lowMaxSessions() {
        return this.maxSessions <= 32;
    }
    
    Session getObjSession() throws PKCS11Exception {
        final Session poll = this.objSessions.poll();
        if (poll != null) {
            return this.ensureValid(poll);
        }
        final Session poll2 = this.opSessions.poll();
        if (poll2 != null) {
            return this.ensureValid(poll2);
        }
        return this.ensureValid(this.openSession());
    }
    
    Session getOpSession() throws PKCS11Exception {
        final Session poll = this.opSessions.poll();
        if (poll != null) {
            return this.ensureValid(poll);
        }
        if (this.maxSessions == Integer.MAX_VALUE || this.activeSessions.get() < this.maxSessions) {
            return this.ensureValid(this.openSession());
        }
        final Session poll2 = this.objSessions.poll();
        if (poll2 != null) {
            return this.ensureValid(poll2);
        }
        throw new ProviderException("Could not obtain session");
    }
    
    private Session ensureValid(final Session session) {
        session.id();
        return session;
    }
    
    Session killSession(final Session session) {
        if (session == null || !this.token.isValid()) {
            return null;
        }
        if (SessionManager.debug != null) {
            System.out.println("Killing session (" + new Exception().getStackTrace()[2].toString() + ") active: " + this.activeSessions.get());
        }
        this.closeSession(session);
        return null;
    }
    
    Session releaseSession(final Session session) {
        if (session == null || !this.token.isValid()) {
            return null;
        }
        if (session.hasObjects()) {
            this.objSessions.release(session);
        }
        else {
            this.opSessions.release(session);
        }
        return null;
    }
    
    void demoteObjSession(final Session session) {
        if (!this.token.isValid()) {
            return;
        }
        if (SessionManager.debug != null) {
            System.out.println("Demoting session, active: " + this.activeSessions.get());
        }
        if (!this.objSessions.remove(session)) {
            return;
        }
        this.opSessions.release(session);
    }
    
    private Session openSession() throws PKCS11Exception {
        if (this.maxSessions != Integer.MAX_VALUE && this.activeSessions.get() >= this.maxSessions) {
            throw new ProviderException("No more sessions available");
        }
        final Session session = new Session(this.token, this.token.p11.C_OpenSession(this.token.provider.slotID, this.openSessionFlags, null, null));
        this.activeSessions.incrementAndGet();
        if (SessionManager.debug != null) {
            synchronized (this.maxActiveSessionsLock) {
                if (this.activeSessions.get() > this.maxActiveSessions) {
                    this.maxActiveSessions = this.activeSessions.get();
                    if (this.maxActiveSessions % 10 == 0) {
                        System.out.println("Open sessions: " + this.maxActiveSessions);
                    }
                }
            }
        }
        return session;
    }
    
    private void closeSession(final Session session) {
        session.close();
        this.activeSessions.decrementAndGet();
    }
    
    static {
        debug = Debug.getInstance("pkcs11");
    }
    
    public static final class Pool
    {
        private final SessionManager mgr;
        private final ConcurrentLinkedDeque<Session> pool;
        
        Pool(final SessionManager mgr) {
            this.mgr = mgr;
            this.pool = new ConcurrentLinkedDeque<Session>();
        }
        
        boolean remove(final Session session) {
            return this.pool.remove(session);
        }
        
        Session poll() {
            return this.pool.pollLast();
        }
        
        void release(final Session session) {
            this.pool.offer(session);
            if (session.hasObjects()) {
                return;
            }
            final int size = this.pool.size();
            if (size < 5) {
                return;
            }
            final long currentTimeMillis = System.currentTimeMillis();
            int n = 0;
            do {
                final Session session2 = this.pool.peek();
                if (session2 == null || session2.isLive(currentTimeMillis)) {
                    break;
                }
                if (!this.pool.remove(session2)) {
                    break;
                }
                ++n;
                this.mgr.closeSession(session2);
            } while (size - n > 1);
            if (SessionManager.debug != null) {
                System.out.println("Closing " + n + " idle sessions, active: " + this.mgr.activeSessions);
            }
        }
    }
}
