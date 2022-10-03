package org.apache.catalina.session;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Lifecycle;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import org.apache.catalina.security.SecurityUtil;
import org.apache.catalina.Manager;
import java.io.IOException;
import java.util.HashMap;
import org.apache.juli.logging.LogFactory;
import org.apache.catalina.Session;
import java.util.Map;
import org.apache.catalina.Store;
import org.apache.juli.logging.Log;
import org.apache.catalina.StoreManager;

public abstract class PersistentManagerBase extends ManagerBase implements StoreManager
{
    private final Log log;
    private static final String name = "PersistentManagerBase";
    private static final String PERSISTED_LAST_ACCESSED_TIME = "org.apache.catalina.session.PersistentManagerBase.persistedLastAccessedTime";
    protected Store store;
    protected boolean saveOnRestart;
    protected int maxIdleBackup;
    protected int minIdleSwap;
    protected int maxIdleSwap;
    private final Map<String, Object> sessionSwapInLocks;
    private final ThreadLocal<Session> sessionToSwapIn;
    
    public PersistentManagerBase() {
        this.log = LogFactory.getLog((Class)PersistentManagerBase.class);
        this.store = null;
        this.saveOnRestart = true;
        this.maxIdleBackup = -1;
        this.minIdleSwap = -1;
        this.maxIdleSwap = -1;
        this.sessionSwapInLocks = new HashMap<String, Object>();
        this.sessionToSwapIn = new ThreadLocal<Session>();
    }
    
    public int getMaxIdleBackup() {
        return this.maxIdleBackup;
    }
    
    public void setMaxIdleBackup(final int backup) {
        if (backup == this.maxIdleBackup) {
            return;
        }
        final int oldBackup = this.maxIdleBackup;
        this.maxIdleBackup = backup;
        this.support.firePropertyChange("maxIdleBackup", oldBackup, (Object)this.maxIdleBackup);
    }
    
    public int getMaxIdleSwap() {
        return this.maxIdleSwap;
    }
    
    public void setMaxIdleSwap(final int max) {
        if (max == this.maxIdleSwap) {
            return;
        }
        final int oldMaxIdleSwap = this.maxIdleSwap;
        this.maxIdleSwap = max;
        this.support.firePropertyChange("maxIdleSwap", oldMaxIdleSwap, (Object)this.maxIdleSwap);
    }
    
    public int getMinIdleSwap() {
        return this.minIdleSwap;
    }
    
    public void setMinIdleSwap(final int min) {
        if (this.minIdleSwap == min) {
            return;
        }
        final int oldMinIdleSwap = this.minIdleSwap;
        this.minIdleSwap = min;
        this.support.firePropertyChange("minIdleSwap", oldMinIdleSwap, (Object)this.minIdleSwap);
    }
    
    public boolean isLoaded(final String id) {
        try {
            if (super.findSession(id) != null) {
                return true;
            }
        }
        catch (final IOException e) {
            this.log.error((Object)PersistentManagerBase.sm.getString("persistentManager.isLoadedError", new Object[] { id }), (Throwable)e);
        }
        return false;
    }
    
    @Override
    public String getName() {
        return "PersistentManagerBase";
    }
    
    public void setStore(final Store store) {
        (this.store = store).setManager(this);
    }
    
    @Override
    public Store getStore() {
        return this.store;
    }
    
    public boolean getSaveOnRestart() {
        return this.saveOnRestart;
    }
    
    public void setSaveOnRestart(final boolean saveOnRestart) {
        if (saveOnRestart == this.saveOnRestart) {
            return;
        }
        final boolean oldSaveOnRestart = this.saveOnRestart;
        this.saveOnRestart = saveOnRestart;
        this.support.firePropertyChange("saveOnRestart", oldSaveOnRestart, (Object)this.saveOnRestart);
    }
    
    public void clearStore() {
        if (this.store == null) {
            return;
        }
        try {
            if (SecurityUtil.isPackageProtectionEnabled()) {
                try {
                    AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedStoreClear());
                }
                catch (final PrivilegedActionException e) {
                    this.log.error((Object)PersistentManagerBase.sm.getString("persistentManager.storeClearError"), (Throwable)e.getException());
                }
            }
            else {
                this.store.clear();
            }
        }
        catch (final IOException e2) {
            this.log.error((Object)PersistentManagerBase.sm.getString("persistentManager.storeClearError"), (Throwable)e2);
        }
    }
    
    @Override
    public void processExpires() {
        final long timeNow = System.currentTimeMillis();
        final Session[] sessions = this.findSessions();
        int expireHere = 0;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Start expire sessions " + this.getName() + " at " + timeNow + " sessioncount " + sessions.length));
        }
        for (final Session session : sessions) {
            if (!session.isValid()) {
                this.expiredSessions.incrementAndGet();
                ++expireHere;
            }
        }
        this.processPersistenceChecks();
        if (this.getStore() instanceof StoreBase) {
            ((StoreBase)this.getStore()).processExpires();
        }
        final long timeEnd = System.currentTimeMillis();
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("End expire sessions " + this.getName() + " processingTime " + (timeEnd - timeNow) + " expired sessions: " + expireHere));
        }
        this.processingTime += timeEnd - timeNow;
    }
    
    public void processPersistenceChecks() {
        this.processMaxIdleSwaps();
        this.processMaxActiveSwaps();
        this.processMaxIdleBackups();
    }
    
    @Override
    public Session findSession(final String id) throws IOException {
        Session session = super.findSession(id);
        if (session != null) {
            synchronized (session) {
                session = super.findSession(session.getIdInternal());
                if (session != null) {
                    session.access();
                    session.endAccess();
                }
            }
        }
        if (session != null) {
            return session;
        }
        session = this.swapIn(id);
        return session;
    }
    
    @Override
    public void removeSuper(final Session session) {
        super.remove(session, false);
    }
    
    @Override
    public void load() {
        this.sessions.clear();
        if (this.store == null) {
            return;
        }
        String[] ids = null;
        try {
            Label_0076: {
                if (SecurityUtil.isPackageProtectionEnabled()) {
                    try {
                        ids = AccessController.doPrivileged((PrivilegedExceptionAction<String[]>)new PrivilegedStoreKeys());
                        break Label_0076;
                    }
                    catch (final PrivilegedActionException e) {
                        this.log.error((Object)PersistentManagerBase.sm.getString("persistentManager.storeLoadKeysError"), (Throwable)e.getException());
                        return;
                    }
                }
                ids = this.store.keys();
            }
        }
        catch (final IOException e2) {
            this.log.error((Object)PersistentManagerBase.sm.getString("persistentManager.storeLoadKeysError"), (Throwable)e2);
            return;
        }
        final int n = ids.length;
        if (n == 0) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)PersistentManagerBase.sm.getString("persistentManager.loading", new Object[] { String.valueOf(n) }));
        }
        for (final String id : ids) {
            try {
                this.swapIn(id);
            }
            catch (final IOException e3) {
                this.log.error((Object)PersistentManagerBase.sm.getString("persistentManager.storeLoadError"), (Throwable)e3);
            }
        }
    }
    
    @Override
    public void remove(final Session session, final boolean update) {
        super.remove(session, update);
        if (this.store != null) {
            this.removeSession(session.getIdInternal());
        }
    }
    
    protected void removeSession(final String id) {
        try {
            if (SecurityUtil.isPackageProtectionEnabled()) {
                try {
                    AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedStoreRemove(id));
                }
                catch (final PrivilegedActionException e) {
                    this.log.error((Object)PersistentManagerBase.sm.getString("persistentManager.removeError"), (Throwable)e.getException());
                }
            }
            else {
                this.store.remove(id);
            }
        }
        catch (final IOException e2) {
            this.log.error((Object)PersistentManagerBase.sm.getString("persistentManager.removeError"), (Throwable)e2);
        }
    }
    
    @Override
    public void unload() {
        if (this.store == null) {
            return;
        }
        final Session[] sessions = this.findSessions();
        final int n = sessions.length;
        if (n == 0) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)PersistentManagerBase.sm.getString("persistentManager.unloading", new Object[] { String.valueOf(n) }));
        }
        for (final Session session : sessions) {
            try {
                this.swapOut(session);
            }
            catch (final IOException ex) {}
        }
    }
    
    @Override
    public int getActiveSessionsFull() {
        int result = this.getActiveSessions();
        try {
            result += this.getStore().getSize();
        }
        catch (final IOException ioe) {
            this.log.warn((Object)PersistentManagerBase.sm.getString("persistentManager.storeSizeException"));
        }
        return result;
    }
    
    @Override
    public Set<String> getSessionIdsFull() {
        final Set<String> sessionIds = new HashSet<String>(this.sessions.keySet());
        try {
            sessionIds.addAll(Arrays.asList(this.getStore().keys()));
        }
        catch (final IOException e) {
            this.log.warn((Object)PersistentManagerBase.sm.getString("persistentManager.storeKeysException"));
        }
        return sessionIds;
    }
    
    protected Session swapIn(final String id) throws IOException {
        if (this.store == null) {
            return null;
        }
        Object swapInLock = null;
        synchronized (this) {
            swapInLock = this.sessionSwapInLocks.get(id);
            if (swapInLock == null) {
                swapInLock = new Object();
                this.sessionSwapInLocks.put(id, swapInLock);
            }
        }
        Session session = null;
        synchronized (swapInLock) {
            session = this.sessions.get(id);
            if (session == null) {
                final Session currentSwapInSession = this.sessionToSwapIn.get();
                try {
                    if (currentSwapInSession == null || !id.equals(currentSwapInSession.getId())) {
                        session = this.loadSessionFromStore(id);
                        this.sessionToSwapIn.set(session);
                        if (session != null && !session.isValid()) {
                            this.log.error((Object)PersistentManagerBase.sm.getString("persistentManager.swapInInvalid", new Object[] { id }));
                            session.expire();
                            this.removeSession(id);
                            session = null;
                        }
                        if (session != null) {
                            this.reactivateLoadedSession(id, session);
                        }
                    }
                }
                finally {
                    this.sessionToSwapIn.remove();
                }
            }
        }
        synchronized (this) {
            this.sessionSwapInLocks.remove(id);
        }
        return session;
    }
    
    private void reactivateLoadedSession(final String id, final Session session) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)PersistentManagerBase.sm.getString("persistentManager.swapIn", new Object[] { id }));
        }
        session.setManager(this);
        ((StandardSession)session).tellNew();
        this.add(session);
        ((StandardSession)session).activate();
        session.access();
        session.endAccess();
    }
    
    private Session loadSessionFromStore(final String id) throws IOException {
        try {
            if (SecurityUtil.isPackageProtectionEnabled()) {
                return this.securedStoreLoad(id);
            }
            return this.store.load(id);
        }
        catch (final ClassNotFoundException e) {
            final String msg = PersistentManagerBase.sm.getString("persistentManager.deserializeError", new Object[] { id });
            this.log.error((Object)msg, (Throwable)e);
            throw new IllegalStateException(msg, e);
        }
    }
    
    private Session securedStoreLoad(final String id) throws IOException, ClassNotFoundException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Session>)new PrivilegedStoreLoad(id));
        }
        catch (final PrivilegedActionException ex) {
            final Exception e = ex.getException();
            this.log.error((Object)PersistentManagerBase.sm.getString("persistentManager.swapInException", new Object[] { id }), (Throwable)e);
            if (e instanceof IOException) {
                throw (IOException)e;
            }
            if (e instanceof ClassNotFoundException) {
                throw (ClassNotFoundException)e;
            }
            return null;
        }
    }
    
    protected void swapOut(final Session session) throws IOException {
        if (this.store == null || !session.isValid()) {
            return;
        }
        ((StandardSession)session).passivate();
        this.writeSession(session);
        super.remove(session, true);
        session.recycle();
    }
    
    protected void writeSession(final Session session) throws IOException {
        if (this.store == null || !session.isValid()) {
            return;
        }
        try {
            if (SecurityUtil.isPackageProtectionEnabled()) {
                try {
                    AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedStoreSave(session));
                }
                catch (final PrivilegedActionException ex) {
                    final Exception exception = ex.getException();
                    if (exception instanceof IOException) {
                        throw (IOException)exception;
                    }
                    this.log.error((Object)PersistentManagerBase.sm.getString("persistentManager.serializeError", new Object[] { session.getIdInternal(), exception }));
                }
            }
            else {
                this.store.save(session);
            }
        }
        catch (final IOException e) {
            this.log.error((Object)PersistentManagerBase.sm.getString("persistentManager.serializeError", new Object[] { session.getIdInternal(), e }));
            throw e;
        }
    }
    
    @Override
    protected synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        if (this.store == null) {
            this.log.error((Object)"No Store configured, persistence disabled");
        }
        else if (this.store instanceof Lifecycle) {
            ((Lifecycle)this.store).start();
        }
        this.setState(LifecycleState.STARTING);
    }
    
    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)"Stopping");
        }
        this.setState(LifecycleState.STOPPING);
        if (this.getStore() != null && this.saveOnRestart) {
            this.unload();
        }
        else {
            final Session[] arr$;
            final Session[] sessions = arr$ = this.findSessions();
            for (final Session value : arr$) {
                final StandardSession session = (StandardSession)value;
                if (session.isValid()) {
                    session.expire();
                }
            }
        }
        if (this.getStore() instanceof Lifecycle) {
            ((Lifecycle)this.getStore()).stop();
        }
        super.stopInternal();
    }
    
    protected void processMaxIdleSwaps() {
        if (!this.getState().isAvailable() || this.maxIdleSwap < 0) {
            return;
        }
        final Session[] sessions = this.findSessions();
        if (this.maxIdleSwap >= 0) {
            for (final Session value : sessions) {
                final StandardSession session = (StandardSession)value;
                synchronized (session) {
                    if (session.isValid()) {
                        final int timeIdle = (int)(session.getIdleTimeInternal() / 1000L);
                        if (timeIdle >= this.maxIdleSwap && timeIdle >= this.minIdleSwap) {
                            if (session.accessCount == null || session.accessCount.get() <= 0) {
                                if (this.log.isDebugEnabled()) {
                                    this.log.debug((Object)PersistentManagerBase.sm.getString("persistentManager.swapMaxIdle", new Object[] { session.getIdInternal(), timeIdle }));
                                }
                                try {
                                    this.swapOut(session);
                                }
                                catch (final IOException ex) {}
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected void processMaxActiveSwaps() {
        if (!this.getState().isAvailable() || this.minIdleSwap < 0 || this.getMaxActiveSessions() < 0) {
            return;
        }
        final Session[] sessions = this.findSessions();
        final int limit = (int)(this.getMaxActiveSessions() * 0.9);
        if (limit >= sessions.length) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)PersistentManagerBase.sm.getString("persistentManager.tooManyActive", new Object[] { sessions.length }));
        }
        for (int toswap = sessions.length - limit, i = 0; i < sessions.length && toswap > 0; ++i) {
            final StandardSession session = (StandardSession)sessions[i];
            synchronized (session) {
                final int timeIdle = (int)(session.getIdleTimeInternal() / 1000L);
                if (timeIdle >= this.minIdleSwap) {
                    if (session.accessCount == null || session.accessCount.get() <= 0) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug((Object)PersistentManagerBase.sm.getString("persistentManager.swapTooManyActive", new Object[] { session.getIdInternal(), timeIdle }));
                        }
                        try {
                            this.swapOut(session);
                        }
                        catch (final IOException ex) {}
                        --toswap;
                    }
                }
            }
        }
    }
    
    protected void processMaxIdleBackups() {
        if (!this.getState().isAvailable() || this.maxIdleBackup < 0) {
            return;
        }
        final Session[] sessions = this.findSessions();
        if (this.maxIdleBackup >= 0) {
            for (final Session value : sessions) {
                final StandardSession session = (StandardSession)value;
                synchronized (session) {
                    if (session.isValid()) {
                        final long lastAccessedTime = session.getLastAccessedTimeInternal();
                        final Long persistedLastAccessedTime = (Long)session.getNote("org.apache.catalina.session.PersistentManagerBase.persistedLastAccessedTime");
                        if (persistedLastAccessedTime == null || lastAccessedTime != persistedLastAccessedTime) {
                            final int timeIdle = (int)(session.getIdleTimeInternal() / 1000L);
                            if (timeIdle >= this.maxIdleBackup) {
                                if (this.log.isDebugEnabled()) {
                                    this.log.debug((Object)PersistentManagerBase.sm.getString("persistentManager.backupMaxIdle", new Object[] { session.getIdInternal(), timeIdle }));
                                }
                                try {
                                    this.writeSession(session);
                                }
                                catch (final IOException ex) {}
                                session.setNote("org.apache.catalina.session.PersistentManagerBase.persistedLastAccessedTime", lastAccessedTime);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private class PrivilegedStoreClear implements PrivilegedExceptionAction<Void>
    {
        PrivilegedStoreClear() {
        }
        
        @Override
        public Void run() throws Exception {
            PersistentManagerBase.this.store.clear();
            return null;
        }
    }
    
    private class PrivilegedStoreRemove implements PrivilegedExceptionAction<Void>
    {
        private String id;
        
        PrivilegedStoreRemove(final String id) {
            this.id = id;
        }
        
        @Override
        public Void run() throws Exception {
            PersistentManagerBase.this.store.remove(this.id);
            return null;
        }
    }
    
    private class PrivilegedStoreLoad implements PrivilegedExceptionAction<Session>
    {
        private String id;
        
        PrivilegedStoreLoad(final String id) {
            this.id = id;
        }
        
        @Override
        public Session run() throws Exception {
            return PersistentManagerBase.this.store.load(this.id);
        }
    }
    
    private class PrivilegedStoreSave implements PrivilegedExceptionAction<Void>
    {
        private Session session;
        
        PrivilegedStoreSave(final Session session) {
            this.session = session;
        }
        
        @Override
        public Void run() throws Exception {
            PersistentManagerBase.this.store.save(this.session);
            return null;
        }
    }
    
    private class PrivilegedStoreKeys implements PrivilegedExceptionAction<String[]>
    {
        PrivilegedStoreKeys() {
        }
        
        @Override
        public String[] run() throws Exception {
            return PersistentManagerBase.this.store.keys();
        }
    }
}
