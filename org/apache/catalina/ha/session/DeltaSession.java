package org.apache.catalina.ha.session;

import org.apache.juli.logging.LogFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Iterator;
import java.util.List;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.ArrayList;
import java.io.WriteAbortedException;
import java.io.NotSerializableException;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.tribes.io.ReplicationStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import org.apache.catalina.SessionListener;
import org.apache.catalina.ha.ClusterMessage;
import org.apache.catalina.ha.CatalinaCluster;
import java.security.Principal;
import java.io.ObjectInputStream;
import java.io.ObjectInput;
import org.apache.catalina.ha.ClusterManager;
import org.apache.catalina.session.ManagerBase;
import java.io.IOException;
import org.apache.tomcat.util.collections.SynchronizedStack;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.catalina.Manager;
import java.util.concurrent.locks.Lock;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.catalina.tribes.tipis.ReplicatedMapEntry;
import org.apache.catalina.ha.ClusterSession;
import java.io.Externalizable;
import org.apache.catalina.session.StandardSession;

public class DeltaSession extends StandardSession implements Externalizable, ClusterSession, ReplicatedMapEntry
{
    public static final Log log;
    protected static final StringManager sm;
    private transient boolean isPrimarySession;
    private transient DeltaRequest deltaRequest;
    private transient long lastTimeReplicated;
    protected final Lock diffLock;
    private long version;
    
    public DeltaSession() {
        this(null);
    }
    
    public DeltaSession(final Manager manager) {
        super(manager);
        this.isPrimarySession = true;
        this.deltaRequest = null;
        this.lastTimeReplicated = System.currentTimeMillis();
        this.diffLock = new ReentrantReadWriteLock().writeLock();
        final boolean recordAllActions = manager instanceof ClusterManagerBase && ((ClusterManagerBase)manager).isRecordAllActions();
        this.deltaRequest = this.createRequest(this.getIdInternal(), recordAllActions);
    }
    
    private DeltaRequest createRequest() {
        return this.createRequest(null, false);
    }
    
    protected DeltaRequest createRequest(final String sessionId, final boolean recordAllActions) {
        return new DeltaRequest(sessionId, recordAllActions);
    }
    
    public boolean isDirty() {
        return this.getDeltaRequest().getSize() > 0;
    }
    
    public boolean isDiffable() {
        return true;
    }
    
    public byte[] getDiff() throws IOException {
        SynchronizedStack<DeltaRequest> deltaRequestPool = null;
        DeltaRequest newDeltaRequest = null;
        if (this.manager instanceof ClusterManagerBase) {
            deltaRequestPool = ((ClusterManagerBase)this.manager).getDeltaRequestPool();
            newDeltaRequest = (DeltaRequest)deltaRequestPool.pop();
            if (newDeltaRequest == null) {
                newDeltaRequest = this.createRequest(null, ((ClusterManagerBase)this.manager).isRecordAllActions());
            }
        }
        else {
            newDeltaRequest = this.createRequest();
        }
        final DeltaRequest oldDeltaRequest = this.replaceDeltaRequest(newDeltaRequest);
        final byte[] result = oldDeltaRequest.serialize();
        if (deltaRequestPool != null) {
            oldDeltaRequest.reset();
            deltaRequestPool.push((Object)oldDeltaRequest);
        }
        return result;
    }
    
    public ClassLoader[] getClassLoaders() {
        if (this.manager instanceof ClusterManagerBase) {
            return ((ClusterManagerBase)this.manager).getClassLoaders();
        }
        if (this.manager instanceof ManagerBase) {
            final ManagerBase mb = (ManagerBase)this.manager;
            return ClusterManagerBase.getClassLoaders(mb.getContext());
        }
        return null;
    }
    
    public void applyDiff(final byte[] diff, final int offset, final int length) throws IOException, ClassNotFoundException {
        this.lockInternal();
        try (final ObjectInputStream stream = (ObjectInputStream)((ClusterManager)this.getManager()).getReplicationStream(diff, offset, length)) {
            final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
            try {
                final ClassLoader[] loaders = this.getClassLoaders();
                if (loaders != null && loaders.length > 0) {
                    Thread.currentThread().setContextClassLoader(loaders[0]);
                }
                this.getDeltaRequest().readExternal(stream);
                this.getDeltaRequest().execute(this, ((ClusterManager)this.getManager()).isNotifyListenersOnReplication());
            }
            finally {
                Thread.currentThread().setContextClassLoader(contextLoader);
            }
        }
        finally {
            this.unlockInternal();
        }
    }
    
    public void resetDiff() {
        this.resetDeltaRequest();
    }
    
    public void lock() {
    }
    
    public void unlock() {
    }
    
    private void lockInternal() {
        this.diffLock.lock();
    }
    
    private void unlockInternal() {
        this.diffLock.unlock();
    }
    
    public void setOwner(final Object owner) {
        if (owner instanceof ClusterManager && this.getManager() == null) {
            final ClusterManager cm = (ClusterManager)owner;
            this.setManager((Manager)cm);
            this.setValid(true);
            this.setPrimarySession(false);
            this.access();
            this.resetDeltaRequest();
            this.endAccess();
        }
    }
    
    public boolean isAccessReplicate() {
        final long replDelta = System.currentTimeMillis() - this.getLastTimeReplicated();
        return this.maxInactiveInterval >= 0 && replDelta > this.maxInactiveInterval * 1000L;
    }
    
    public void accessEntry() {
        this.access();
        this.setPrimarySession(false);
        this.endAccess();
    }
    
    public boolean isPrimarySession() {
        return this.isPrimarySession;
    }
    
    public void setPrimarySession(final boolean primarySession) {
        this.isPrimarySession = primarySession;
    }
    
    public void setId(final String id, final boolean notify) {
        super.setId(id, notify);
        this.lockInternal();
        try {
            this.deltaRequest.setSessionId(this.getIdInternal());
        }
        finally {
            this.unlockInternal();
        }
    }
    
    public void setId(final String id) {
        this.setId(id, true);
    }
    
    public void setMaxInactiveInterval(final int interval) {
        this.setMaxInactiveInterval(interval, true);
    }
    
    public void setMaxInactiveInterval(final int interval, final boolean addDeltaRequest) {
        super.maxInactiveInterval = interval;
        if (addDeltaRequest) {
            this.lockInternal();
            try {
                this.deltaRequest.setMaxInactiveInterval(interval);
            }
            finally {
                this.unlockInternal();
            }
        }
    }
    
    public void setNew(final boolean isNew) {
        this.setNew(isNew, true);
    }
    
    public void setNew(final boolean isNew, final boolean addDeltaRequest) {
        super.setNew(isNew);
        if (addDeltaRequest) {
            this.lockInternal();
            try {
                this.deltaRequest.setNew(isNew);
            }
            finally {
                this.unlockInternal();
            }
        }
    }
    
    public void setPrincipal(final Principal principal) {
        this.setPrincipal(principal, true);
    }
    
    public void setPrincipal(final Principal principal, final boolean addDeltaRequest) {
        this.lockInternal();
        try {
            super.setPrincipal(principal);
            if (addDeltaRequest) {
                this.deltaRequest.setPrincipal(principal);
            }
        }
        finally {
            this.unlockInternal();
        }
    }
    
    public void setAuthType(final String authType) {
        this.setAuthType(authType, true);
    }
    
    public void setAuthType(final String authType, final boolean addDeltaRequest) {
        this.lockInternal();
        try {
            super.setAuthType(authType);
            if (addDeltaRequest) {
                this.deltaRequest.setAuthType(authType);
            }
        }
        finally {
            this.unlockInternal();
        }
    }
    
    public boolean isValid() {
        if (!this.isValid) {
            return false;
        }
        if (this.expiring) {
            return true;
        }
        if (DeltaSession.ACTIVITY_CHECK && this.accessCount.get() > 0) {
            return true;
        }
        if (this.maxInactiveInterval > 0) {
            final int timeIdle = (int)(this.getIdleTimeInternal() / 1000L);
            if (this.isPrimarySession()) {
                if (timeIdle >= this.maxInactiveInterval) {
                    this.expire(true);
                }
            }
            else if (timeIdle >= 2 * this.maxInactiveInterval) {
                this.expire(true, false);
            }
        }
        return this.isValid;
    }
    
    public void endAccess() {
        super.endAccess();
        if (this.manager instanceof ClusterManagerBase) {
            ((ClusterManagerBase)this.manager).registerSessionAtReplicationValve(this);
        }
    }
    
    public void expire(final boolean notify) {
        this.expire(notify, true);
    }
    
    public void expire(final boolean notify, final boolean notifyCluster) {
        if (!this.isValid) {
            return;
        }
        synchronized (this) {
            if (!this.isValid) {
                return;
            }
            if (this.manager == null) {
                return;
            }
            final String expiredId = this.getIdInternal();
            if (notifyCluster && expiredId != null && this.manager instanceof DeltaManager) {
                final DeltaManager dmanager = (DeltaManager)this.manager;
                final CatalinaCluster cluster = dmanager.getCluster();
                final ClusterMessage msg = dmanager.requestCompleted(expiredId, true);
                if (msg != null) {
                    cluster.send(msg);
                }
            }
            super.expire(notify);
            if (notifyCluster) {
                if (DeltaSession.log.isDebugEnabled()) {
                    DeltaSession.log.debug((Object)DeltaSession.sm.getString("deltaSession.notifying", new Object[] { ((ClusterManager)this.manager).getName(), this.isPrimarySession(), expiredId }));
                }
                if (this.manager instanceof DeltaManager) {
                    ((DeltaManager)this.manager).sessionExpired(expiredId);
                }
            }
        }
    }
    
    public void recycle() {
        this.lockInternal();
        try {
            super.recycle();
            this.deltaRequest.clear();
        }
        finally {
            this.unlockInternal();
        }
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("DeltaSession[");
        sb.append(this.id);
        sb.append(']');
        return sb.toString();
    }
    
    public void addSessionListener(final SessionListener listener) {
        this.addSessionListener(listener, true);
    }
    
    public void addSessionListener(final SessionListener listener, final boolean addDeltaRequest) {
        this.lockInternal();
        try {
            super.addSessionListener(listener);
            if (addDeltaRequest && listener instanceof ReplicatedSessionListener) {
                this.deltaRequest.addSessionListener(listener);
            }
        }
        finally {
            this.unlockInternal();
        }
    }
    
    public void removeSessionListener(final SessionListener listener) {
        this.removeSessionListener(listener, true);
    }
    
    public void removeSessionListener(final SessionListener listener, final boolean addDeltaRequest) {
        this.lockInternal();
        try {
            super.removeSessionListener(listener);
            if (addDeltaRequest && listener instanceof ReplicatedSessionListener) {
                this.deltaRequest.removeSessionListener(listener);
            }
        }
        finally {
            this.unlockInternal();
        }
    }
    
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this.lockInternal();
        try {
            this.readObjectData(in);
        }
        finally {
            this.unlockInternal();
        }
    }
    
    public void readObjectData(final ObjectInputStream stream) throws ClassNotFoundException, IOException {
        this.doReadObject((ObjectInput)stream);
    }
    
    public void readObjectData(final ObjectInput stream) throws ClassNotFoundException, IOException {
        this.doReadObject(stream);
    }
    
    public void writeObjectData(final ObjectOutputStream stream) throws IOException {
        this.writeObjectData((ObjectOutput)stream);
    }
    
    public void writeObjectData(final ObjectOutput stream) throws IOException {
        this.doWriteObject(stream);
    }
    
    public void resetDeltaRequest() {
        this.lockInternal();
        try {
            this.deltaRequest.reset();
            this.deltaRequest.setSessionId(this.getIdInternal());
        }
        finally {
            this.unlockInternal();
        }
    }
    
    public DeltaRequest getDeltaRequest() {
        return this.deltaRequest;
    }
    
    DeltaRequest replaceDeltaRequest(final DeltaRequest deltaRequest) {
        this.lockInternal();
        try {
            final DeltaRequest oldDeltaRequest = this.deltaRequest;
            (this.deltaRequest = deltaRequest).setSessionId(this.getIdInternal());
            return oldDeltaRequest;
        }
        finally {
            this.unlockInternal();
        }
    }
    
    protected void deserializeAndExecuteDeltaRequest(final byte[] delta) throws IOException, ClassNotFoundException {
        if (this.manager instanceof ClusterManagerBase) {
            final SynchronizedStack<DeltaRequest> deltaRequestPool = ((ClusterManagerBase)this.manager).getDeltaRequestPool();
            DeltaRequest newDeltaRequest = (DeltaRequest)deltaRequestPool.pop();
            if (newDeltaRequest == null) {
                newDeltaRequest = this.createRequest(null, ((ClusterManagerBase)this.manager).isRecordAllActions());
            }
            final ReplicationStream ois = ((ClusterManagerBase)this.manager).getReplicationStream(delta);
            newDeltaRequest.readExternal((ObjectInput)ois);
            ois.close();
            DeltaRequest oldDeltaRequest = null;
            this.lockInternal();
            try {
                oldDeltaRequest = this.replaceDeltaRequest(newDeltaRequest);
                newDeltaRequest.execute(this, ((ClusterManagerBase)this.manager).isNotifyListenersOnReplication());
                this.setPrimarySession(false);
            }
            finally {
                this.unlockInternal();
                if (oldDeltaRequest != null) {
                    oldDeltaRequest.reset();
                    deltaRequestPool.push((Object)oldDeltaRequest);
                }
            }
        }
    }
    
    public void removeAttribute(final String name, final boolean notify) {
        this.removeAttribute(name, notify, true);
    }
    
    public void removeAttribute(final String name, final boolean notify, final boolean addDeltaRequest) {
        if (!this.isValid()) {
            throw new IllegalStateException(DeltaSession.sm.getString("standardSession.removeAttribute.ise"));
        }
        this.removeAttributeInternal(name, notify, addDeltaRequest);
    }
    
    public void setAttribute(final String name, final Object value) {
        this.setAttribute(name, value, true, true);
    }
    
    public void setAttribute(final String name, final Object value, final boolean notify, final boolean addDeltaRequest) {
        if (name == null) {
            throw new IllegalArgumentException(DeltaSession.sm.getString("standardSession.setAttribute.namenull"));
        }
        if (value == null) {
            this.removeAttribute(name);
            return;
        }
        this.lockInternal();
        try {
            super.setAttribute(name, value, notify);
            if (addDeltaRequest && !this.exclude(name, value)) {
                this.deltaRequest.setAttribute(name, value);
            }
        }
        finally {
            this.unlockInternal();
        }
    }
    
    protected void doReadObject(final ObjectInputStream stream) throws ClassNotFoundException, IOException {
        this.doReadObject((ObjectInput)stream);
    }
    
    private void doReadObject(final ObjectInput stream) throws ClassNotFoundException, IOException {
        this.authType = null;
        this.creationTime = (long)stream.readObject();
        this.lastAccessedTime = (long)stream.readObject();
        this.maxInactiveInterval = (int)stream.readObject();
        this.isNew = (boolean)stream.readObject();
        this.isValid = (boolean)stream.readObject();
        this.thisAccessedTime = (long)stream.readObject();
        this.version = (long)stream.readObject();
        final boolean hasPrincipal = stream.readBoolean();
        this.principal = null;
        if (hasPrincipal) {
            this.principal = (Principal)stream.readObject();
        }
        this.id = (String)stream.readObject();
        if (DeltaSession.log.isDebugEnabled()) {
            DeltaSession.log.debug((Object)DeltaSession.sm.getString("deltaSession.readSession", new Object[] { this.id }));
        }
        if (this.attributes == null) {
            this.attributes = new ConcurrentHashMap();
        }
        int n = (int)stream.readObject();
        final boolean isValidSave = this.isValid;
        this.isValid = true;
        for (int i = 0; i < n; ++i) {
            final String name = (String)stream.readObject();
            Object value;
            try {
                value = stream.readObject();
            }
            catch (final WriteAbortedException wae) {
                if (wae.getCause() instanceof NotSerializableException) {
                    continue;
                }
                throw wae;
            }
            if (!this.exclude(name, value)) {
                if (null != value) {
                    this.attributes.put(name, value);
                }
            }
        }
        this.isValid = isValidSave;
        n = (int)stream.readObject();
        if (this.listeners == null || n > 0) {
            this.listeners = new ArrayList();
        }
        for (int i = 0; i < n; ++i) {
            final SessionListener listener = (SessionListener)stream.readObject();
            this.listeners.add(listener);
        }
        if (this.notes == null) {
            this.notes = new Hashtable();
        }
        this.activate();
    }
    
    public void writeExternal(final ObjectOutput out) throws IOException {
        this.lockInternal();
        try {
            this.doWriteObject(out);
        }
        finally {
            this.unlockInternal();
        }
    }
    
    protected void doWriteObject(final ObjectOutputStream stream) throws IOException {
        this.doWriteObject((ObjectOutput)stream);
    }
    
    private void doWriteObject(final ObjectOutput stream) throws IOException {
        stream.writeObject(this.creationTime);
        stream.writeObject(this.lastAccessedTime);
        stream.writeObject(this.maxInactiveInterval);
        stream.writeObject(this.isNew);
        stream.writeObject(this.isValid);
        stream.writeObject(this.thisAccessedTime);
        stream.writeObject(this.version);
        stream.writeBoolean(this.getPrincipal() instanceof Serializable);
        if (this.getPrincipal() instanceof Serializable) {
            stream.writeObject(this.getPrincipal());
        }
        stream.writeObject(this.id);
        if (DeltaSession.log.isDebugEnabled()) {
            DeltaSession.log.debug((Object)DeltaSession.sm.getString("deltaSession.writeSession", new Object[] { this.id }));
        }
        final String[] keys = this.keys();
        final List<String> saveNames = new ArrayList<String>();
        final List<Object> saveValues = new ArrayList<Object>();
        for (final String key : keys) {
            Object value = null;
            value = this.attributes.get(key);
            if (value != null && !this.exclude(key, value) && this.isAttributeDistributable(key, value)) {
                saveNames.add(key);
                saveValues.add(value);
            }
        }
        final int n = saveNames.size();
        stream.writeObject(n);
        for (int i = 0; i < n; ++i) {
            stream.writeObject(saveNames.get(i));
            try {
                stream.writeObject(saveValues.get(i));
            }
            catch (final NotSerializableException e) {
                DeltaSession.log.error((Object)DeltaSession.sm.getString("standardSession.notSerializable", new Object[] { saveNames.get(i), this.id }), (Throwable)e);
            }
        }
        final ArrayList<SessionListener> saveListeners = new ArrayList<SessionListener>();
        for (final SessionListener listener : this.listeners) {
            if (listener instanceof ReplicatedSessionListener) {
                saveListeners.add(listener);
            }
        }
        stream.writeObject(saveListeners.size());
        for (final SessionListener listener : saveListeners) {
            stream.writeObject(listener);
        }
    }
    
    protected void removeAttributeInternal(final String name, final boolean notify, final boolean addDeltaRequest) {
        this.lockInternal();
        try {
            final Object value = this.attributes.get(name);
            if (value == null) {
                return;
            }
            super.removeAttributeInternal(name, notify);
            if (addDeltaRequest && !this.exclude(name, (Object)null)) {
                this.deltaRequest.removeAttribute(name);
            }
        }
        finally {
            this.unlockInternal();
        }
    }
    
    public long getLastTimeReplicated() {
        return this.lastTimeReplicated;
    }
    
    public long getVersion() {
        return this.version;
    }
    
    public void setLastTimeReplicated(final long lastTimeReplicated) {
        this.lastTimeReplicated = lastTimeReplicated;
    }
    
    public void setVersion(final long version) {
        this.version = version;
    }
    
    protected void setAccessCount(final int count) {
        if (this.accessCount == null && DeltaSession.ACTIVITY_CHECK) {
            this.accessCount = new AtomicInteger();
        }
        if (this.accessCount != null) {
            super.accessCount.set(count);
        }
    }
    
    static {
        log = LogFactory.getLog((Class)DeltaSession.class);
        sm = StringManager.getManager((Class)DeltaSession.class);
    }
}
