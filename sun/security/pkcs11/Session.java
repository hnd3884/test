package sun.security.pkcs11;

import java.security.ProviderException;
import java.util.concurrent.atomic.AtomicInteger;

final class Session implements Comparable<Session>
{
    private static final long MAX_IDLE_TIME = 180000L;
    final Token token;
    private final long id;
    private final AtomicInteger createdObjects;
    private long lastAccess;
    private final SessionRef sessionRef;
    
    Session(final Token token, final long id) {
        this.token = token;
        this.id = id;
        this.createdObjects = new AtomicInteger();
        this.id();
        this.sessionRef = new SessionRef(this, id, token);
    }
    
    @Override
    public int compareTo(final Session session) {
        if (this.lastAccess == session.lastAccess) {
            return 0;
        }
        return (this.lastAccess < session.lastAccess) ? -1 : 1;
    }
    
    boolean isLive(final long n) {
        return n - this.lastAccess < 180000L;
    }
    
    long idInternal() {
        return this.id;
    }
    
    long id() {
        if (!this.token.isPresent(this.id)) {
            throw new ProviderException("Token has been removed");
        }
        this.lastAccess = System.currentTimeMillis();
        return this.id;
    }
    
    void addObject() {
        this.createdObjects.incrementAndGet();
    }
    
    void removeObject() {
        final int decrementAndGet = this.createdObjects.decrementAndGet();
        if (decrementAndGet == 0) {
            this.token.sessionManager.demoteObjSession(this);
        }
        else if (decrementAndGet < 0) {
            throw new ProviderException("Internal error: objects created " + decrementAndGet);
        }
    }
    
    boolean hasObjects() {
        return this.createdObjects.get() != 0;
    }
    
    void close() {
        if (this.hasObjects()) {
            throw new ProviderException("Internal error: close session with active objects");
        }
        this.sessionRef.dispose();
    }
}
