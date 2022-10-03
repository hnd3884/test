package org.apache.catalina.ha.session;

import org.apache.catalina.ha.ClusterManager;
import java.util.Iterator;
import org.apache.catalina.tribes.Member;
import java.util.Date;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import java.io.BufferedOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectInput;
import org.apache.catalina.tribes.io.ReplicationStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.catalina.Manager;
import org.apache.catalina.ha.ClusterMessage;
import org.apache.catalina.Session;
import org.apache.juli.logging.LogFactory;
import java.util.ArrayList;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

public class DeltaManager extends ClusterManagerBase
{
    public final Log log;
    protected static final StringManager sm;
    @Deprecated
    protected static final String managerName = "DeltaManager";
    protected String name;
    private boolean expireSessionsOnShutdown;
    private boolean notifySessionListenersOnReplication;
    private boolean notifyContainerListenersOnReplication;
    private volatile boolean stateTransferred;
    private volatile boolean noContextManagerReceived;
    private int stateTransferTimeout;
    private boolean sendAllSessions;
    private int sendAllSessionsSize;
    private int sendAllSessionsWaitTime;
    private final ArrayList<SessionMessage> receivedMessageQueue;
    private boolean receiverQueue;
    private boolean stateTimestampDrop;
    private volatile long stateTransferCreateSendTime;
    private long sessionReplaceCounter;
    private long counterReceive_EVT_GET_ALL_SESSIONS;
    private long counterReceive_EVT_ALL_SESSION_DATA;
    private long counterReceive_EVT_SESSION_CREATED;
    private long counterReceive_EVT_SESSION_EXPIRED;
    private long counterReceive_EVT_SESSION_ACCESSED;
    private long counterReceive_EVT_SESSION_DELTA;
    private int counterReceive_EVT_ALL_SESSION_TRANSFERCOMPLETE;
    private long counterReceive_EVT_CHANGE_SESSION_ID;
    private long counterReceive_EVT_ALL_SESSION_NOCONTEXTMANAGER;
    private long counterSend_EVT_GET_ALL_SESSIONS;
    private long counterSend_EVT_ALL_SESSION_DATA;
    private long counterSend_EVT_SESSION_CREATED;
    private long counterSend_EVT_SESSION_DELTA;
    private long counterSend_EVT_SESSION_ACCESSED;
    private long counterSend_EVT_SESSION_EXPIRED;
    private int counterSend_EVT_ALL_SESSION_TRANSFERCOMPLETE;
    private long counterSend_EVT_CHANGE_SESSION_ID;
    private int counterNoStateTransferred;
    
    public DeltaManager() {
        this.log = LogFactory.getLog((Class)DeltaManager.class);
        this.name = null;
        this.expireSessionsOnShutdown = false;
        this.notifySessionListenersOnReplication = true;
        this.notifyContainerListenersOnReplication = true;
        this.stateTransferred = false;
        this.noContextManagerReceived = false;
        this.stateTransferTimeout = 60;
        this.sendAllSessions = true;
        this.sendAllSessionsSize = 1000;
        this.sendAllSessionsWaitTime = 2000;
        this.receivedMessageQueue = new ArrayList<SessionMessage>();
        this.receiverQueue = false;
        this.stateTimestampDrop = true;
        this.sessionReplaceCounter = 0L;
        this.counterReceive_EVT_GET_ALL_SESSIONS = 0L;
        this.counterReceive_EVT_ALL_SESSION_DATA = 0L;
        this.counterReceive_EVT_SESSION_CREATED = 0L;
        this.counterReceive_EVT_SESSION_EXPIRED = 0L;
        this.counterReceive_EVT_SESSION_ACCESSED = 0L;
        this.counterReceive_EVT_SESSION_DELTA = 0L;
        this.counterReceive_EVT_ALL_SESSION_TRANSFERCOMPLETE = 0;
        this.counterReceive_EVT_CHANGE_SESSION_ID = 0L;
        this.counterReceive_EVT_ALL_SESSION_NOCONTEXTMANAGER = 0L;
        this.counterSend_EVT_GET_ALL_SESSIONS = 0L;
        this.counterSend_EVT_ALL_SESSION_DATA = 0L;
        this.counterSend_EVT_SESSION_CREATED = 0L;
        this.counterSend_EVT_SESSION_DELTA = 0L;
        this.counterSend_EVT_SESSION_ACCESSED = 0L;
        this.counterSend_EVT_SESSION_EXPIRED = 0L;
        this.counterSend_EVT_ALL_SESSION_TRANSFERCOMPLETE = 0;
        this.counterSend_EVT_CHANGE_SESSION_ID = 0L;
        this.counterNoStateTransferred = 0;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public long getCounterSend_EVT_GET_ALL_SESSIONS() {
        return this.counterSend_EVT_GET_ALL_SESSIONS;
    }
    
    public long getCounterSend_EVT_SESSION_ACCESSED() {
        return this.counterSend_EVT_SESSION_ACCESSED;
    }
    
    public long getCounterSend_EVT_SESSION_CREATED() {
        return this.counterSend_EVT_SESSION_CREATED;
    }
    
    public long getCounterSend_EVT_SESSION_DELTA() {
        return this.counterSend_EVT_SESSION_DELTA;
    }
    
    public long getCounterSend_EVT_SESSION_EXPIRED() {
        return this.counterSend_EVT_SESSION_EXPIRED;
    }
    
    public long getCounterSend_EVT_ALL_SESSION_DATA() {
        return this.counterSend_EVT_ALL_SESSION_DATA;
    }
    
    public int getCounterSend_EVT_ALL_SESSION_TRANSFERCOMPLETE() {
        return this.counterSend_EVT_ALL_SESSION_TRANSFERCOMPLETE;
    }
    
    public long getCounterSend_EVT_CHANGE_SESSION_ID() {
        return this.counterSend_EVT_CHANGE_SESSION_ID;
    }
    
    public long getCounterReceive_EVT_ALL_SESSION_DATA() {
        return this.counterReceive_EVT_ALL_SESSION_DATA;
    }
    
    public long getCounterReceive_EVT_GET_ALL_SESSIONS() {
        return this.counterReceive_EVT_GET_ALL_SESSIONS;
    }
    
    public long getCounterReceive_EVT_SESSION_ACCESSED() {
        return this.counterReceive_EVT_SESSION_ACCESSED;
    }
    
    public long getCounterReceive_EVT_SESSION_CREATED() {
        return this.counterReceive_EVT_SESSION_CREATED;
    }
    
    public long getCounterReceive_EVT_SESSION_DELTA() {
        return this.counterReceive_EVT_SESSION_DELTA;
    }
    
    public long getCounterReceive_EVT_SESSION_EXPIRED() {
        return this.counterReceive_EVT_SESSION_EXPIRED;
    }
    
    public int getCounterReceive_EVT_ALL_SESSION_TRANSFERCOMPLETE() {
        return this.counterReceive_EVT_ALL_SESSION_TRANSFERCOMPLETE;
    }
    
    public long getCounterReceive_EVT_CHANGE_SESSION_ID() {
        return this.counterReceive_EVT_CHANGE_SESSION_ID;
    }
    
    public long getCounterReceive_EVT_ALL_SESSION_NOCONTEXTMANAGER() {
        return this.counterReceive_EVT_ALL_SESSION_NOCONTEXTMANAGER;
    }
    
    public long getProcessingTime() {
        return this.processingTime;
    }
    
    public long getSessionReplaceCounter() {
        return this.sessionReplaceCounter;
    }
    
    @Deprecated
    public int getCounterNoStateTransfered() {
        return this.getCounterNoStateTransferred();
    }
    
    public int getCounterNoStateTransferred() {
        return this.counterNoStateTransferred;
    }
    
    public int getReceivedQueueSize() {
        return this.receivedMessageQueue.size();
    }
    
    public int getStateTransferTimeout() {
        return this.stateTransferTimeout;
    }
    
    public void setStateTransferTimeout(final int timeoutAllSession) {
        this.stateTransferTimeout = timeoutAllSession;
    }
    
    @Deprecated
    public boolean getStateTransfered() {
        return this.getStateTransferred();
    }
    
    @Deprecated
    public void setStateTransfered(final boolean stateTransferred) {
        this.setStateTransferred(stateTransferred);
    }
    
    public boolean getStateTransferred() {
        return this.stateTransferred;
    }
    
    public void setStateTransferred(final boolean stateTransferred) {
        this.stateTransferred = stateTransferred;
    }
    
    public boolean isNoContextManagerReceived() {
        return this.noContextManagerReceived;
    }
    
    public void setNoContextManagerReceived(final boolean noContextManagerReceived) {
        this.noContextManagerReceived = noContextManagerReceived;
    }
    
    public int getSendAllSessionsWaitTime() {
        return this.sendAllSessionsWaitTime;
    }
    
    public void setSendAllSessionsWaitTime(final int sendAllSessionsWaitTime) {
        this.sendAllSessionsWaitTime = sendAllSessionsWaitTime;
    }
    
    public boolean isStateTimestampDrop() {
        return this.stateTimestampDrop;
    }
    
    public void setStateTimestampDrop(final boolean isTimestampDrop) {
        this.stateTimestampDrop = isTimestampDrop;
    }
    
    public boolean isSendAllSessions() {
        return this.sendAllSessions;
    }
    
    public void setSendAllSessions(final boolean sendAllSessions) {
        this.sendAllSessions = sendAllSessions;
    }
    
    public int getSendAllSessionsSize() {
        return this.sendAllSessionsSize;
    }
    
    public void setSendAllSessionsSize(final int sendAllSessionsSize) {
        this.sendAllSessionsSize = sendAllSessionsSize;
    }
    
    public boolean isNotifySessionListenersOnReplication() {
        return this.notifySessionListenersOnReplication;
    }
    
    public void setNotifySessionListenersOnReplication(final boolean notifyListenersCreateSessionOnReplication) {
        this.notifySessionListenersOnReplication = notifyListenersCreateSessionOnReplication;
    }
    
    public boolean isExpireSessionsOnShutdown() {
        return this.expireSessionsOnShutdown;
    }
    
    public void setExpireSessionsOnShutdown(final boolean expireSessionsOnShutdown) {
        this.expireSessionsOnShutdown = expireSessionsOnShutdown;
    }
    
    public boolean isNotifyContainerListenersOnReplication() {
        return this.notifyContainerListenersOnReplication;
    }
    
    public void setNotifyContainerListenersOnReplication(final boolean notifyContainerListenersOnReplication) {
        this.notifyContainerListenersOnReplication = notifyContainerListenersOnReplication;
    }
    
    public Session createSession(final String sessionId) {
        return this.createSession(sessionId, true);
    }
    
    public Session createSession(final String sessionId, final boolean distribute) {
        final DeltaSession session = (DeltaSession)super.createSession(sessionId);
        if (distribute) {
            this.sendCreateSession(session.getId(), session);
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)DeltaManager.sm.getString("deltaManager.createSession.newSession", new Object[] { session.getId(), this.sessions.size() }));
        }
        return (Session)session;
    }
    
    protected void sendCreateSession(final String sessionId, final DeltaSession session) {
        if (this.cluster.getMembers().length > 0) {
            final SessionMessage msg = new SessionMessageImpl(this.getName(), 1, null, sessionId, sessionId + "-" + System.currentTimeMillis());
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)DeltaManager.sm.getString("deltaManager.sendMessage.newSession", new Object[] { this.name, sessionId }));
            }
            msg.setTimestamp(session.getCreationTime());
            ++this.counterSend_EVT_SESSION_CREATED;
            this.send(msg);
        }
    }
    
    protected void send(final SessionMessage msg) {
        if (this.cluster != null) {
            this.cluster.send(msg);
        }
    }
    
    public Session createEmptySession() {
        return (Session)new DeltaSession((Manager)this);
    }
    
    @Deprecated
    protected DeltaSession getNewDeltaSession() {
        return new DeltaSession((Manager)this);
    }
    
    public void changeSessionId(final Session session) {
        this.rotateSessionId(session, true);
    }
    
    public String rotateSessionId(final Session session) {
        return this.rotateSessionId(session, true);
    }
    
    public void changeSessionId(final Session session, final String newId) {
        this.changeSessionId(session, newId, true);
    }
    
    protected void changeSessionId(final Session session, final boolean notify) {
        final String orgSessionID = session.getId();
        super.changeSessionId(session);
        if (notify) {
            this.sendChangeSessionId(session.getId(), orgSessionID);
        }
    }
    
    protected String rotateSessionId(final Session session, final boolean notify) {
        final String orgSessionID = session.getId();
        final String newId = super.rotateSessionId(session);
        if (notify) {
            this.sendChangeSessionId(session.getId(), orgSessionID);
        }
        return newId;
    }
    
    protected void changeSessionId(final Session session, final String newId, final boolean notify) {
        final String orgSessionID = session.getId();
        super.changeSessionId(session, newId);
        if (notify) {
            this.sendChangeSessionId(session.getId(), orgSessionID);
        }
    }
    
    protected void sendChangeSessionId(final String newSessionID, final String orgSessionID) {
        if (this.cluster.getMembers().length > 0) {
            try {
                final byte[] data = this.serializeSessionId(newSessionID);
                final SessionMessage msg = new SessionMessageImpl(this.getName(), 15, data, orgSessionID, orgSessionID + "-" + System.currentTimeMillis());
                msg.setTimestamp(System.currentTimeMillis());
                ++this.counterSend_EVT_CHANGE_SESSION_ID;
                this.send(msg);
            }
            catch (final IOException e) {
                this.log.error((Object)DeltaManager.sm.getString("deltaManager.unableSerializeSessionID", new Object[] { newSessionID }), (Throwable)e);
            }
        }
    }
    
    protected byte[] serializeSessionId(final String sessionId) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeUTF(sessionId);
        oos.flush();
        oos.close();
        return bos.toByteArray();
    }
    
    protected String deserializeSessionId(final byte[] data) throws IOException {
        final ReplicationStream ois = this.getReplicationStream(data);
        final String sessionId = ois.readUTF();
        ois.close();
        return sessionId;
    }
    
    @Deprecated
    protected DeltaRequest deserializeDeltaRequest(final DeltaSession session, final byte[] data) throws ClassNotFoundException, IOException {
        session.lock();
        try {
            final ReplicationStream ois = this.getReplicationStream(data);
            session.getDeltaRequest().readExternal((ObjectInput)ois);
            ois.close();
            return session.getDeltaRequest();
        }
        finally {
            session.unlock();
        }
    }
    
    @Deprecated
    protected byte[] serializeDeltaRequest(final DeltaSession session, final DeltaRequest deltaRequest) throws IOException {
        session.lock();
        try {
            return deltaRequest.serialize();
        }
        finally {
            session.unlock();
        }
    }
    
    protected void deserializeSessions(final byte[] data) throws ClassNotFoundException, IOException {
        try (final ObjectInputStream ois = (ObjectInputStream)this.getReplicationStream(data)) {
            final Integer count = (Integer)ois.readObject();
            for (int n = count, i = 0; i < n; ++i) {
                final DeltaSession session = (DeltaSession)this.createEmptySession();
                session.readObjectData(ois);
                session.setManager((Manager)this);
                session.setValid(true);
                session.setPrimarySession(false);
                session.access();
                session.setAccessCount(0);
                session.resetDeltaRequest();
                if (this.findSession(session.getIdInternal()) == null) {
                    ++this.sessionCounter;
                }
                else {
                    ++this.sessionReplaceCounter;
                    if (this.log.isWarnEnabled()) {
                        this.log.warn((Object)DeltaManager.sm.getString("deltaManager.loading.existing.session", new Object[] { session.getIdInternal() }));
                    }
                }
                this.add((Session)session);
                if (this.notifySessionListenersOnReplication) {
                    session.tellNew();
                }
            }
        }
        catch (final ClassNotFoundException e) {
            this.log.error((Object)DeltaManager.sm.getString("deltaManager.loading.cnfe", new Object[] { e }), (Throwable)e);
            throw e;
        }
        catch (final IOException e2) {
            this.log.error((Object)DeltaManager.sm.getString("deltaManager.loading.ioe", new Object[] { e2 }), (Throwable)e2);
            throw e2;
        }
    }
    
    protected byte[] serializeSessions(final Session[] currentSessions) throws IOException {
        final ByteArrayOutputStream fos = new ByteArrayOutputStream();
        try (final ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos))) {
            oos.writeObject(currentSessions.length);
            for (final Session currentSession : currentSessions) {
                ((DeltaSession)currentSession).writeObjectData(oos);
            }
            oos.flush();
        }
        catch (final IOException e) {
            this.log.error((Object)DeltaManager.sm.getString("deltaManager.unloading.ioe", new Object[] { e }), (Throwable)e);
            throw e;
        }
        return fos.toByteArray();
    }
    
    @Override
    protected synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        try {
            if (this.cluster == null) {
                this.log.error((Object)DeltaManager.sm.getString("deltaManager.noCluster", new Object[] { this.getName() }));
                return;
            }
            if (this.log.isInfoEnabled()) {
                String type = "unknown";
                if (this.cluster.getContainer() instanceof Host) {
                    type = "Host";
                }
                else if (this.cluster.getContainer() instanceof Engine) {
                    type = "Engine";
                }
                this.log.info((Object)DeltaManager.sm.getString("deltaManager.registerCluster", new Object[] { this.getName(), type, this.cluster.getClusterName() }));
            }
            if (this.log.isInfoEnabled()) {
                this.log.info((Object)DeltaManager.sm.getString("deltaManager.startClustering", new Object[] { this.getName() }));
            }
            this.getAllClusterSessions();
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            this.log.error((Object)DeltaManager.sm.getString("deltaManager.managerLoad"), t);
        }
        this.setState(LifecycleState.STARTING);
    }
    
    public synchronized void getAllClusterSessions() {
        if (this.cluster != null && this.cluster.getMembers().length > 0) {
            final long beforeSendTime = System.currentTimeMillis();
            final Member mbr = this.findSessionMasterMember();
            if (mbr == null) {
                return;
            }
            final SessionMessage msg = new SessionMessageImpl(this.getName(), 4, null, "GET-ALL", "GET-ALL-" + this.getName());
            msg.setTimestamp(beforeSendTime);
            this.stateTransferCreateSendTime = beforeSendTime;
            ++this.counterSend_EVT_GET_ALL_SESSIONS;
            this.stateTransferred = false;
            try {
                synchronized (this.receivedMessageQueue) {
                    this.receiverQueue = true;
                }
                this.cluster.send(msg, mbr);
                if (this.log.isInfoEnabled()) {
                    this.log.info((Object)DeltaManager.sm.getString("deltaManager.waitForSessionState", new Object[] { this.getName(), mbr, this.getStateTransferTimeout() }));
                }
                this.waitForSendAllSessions(beforeSendTime);
            }
            finally {
                synchronized (this.receivedMessageQueue) {
                    for (final SessionMessage smsg : this.receivedMessageQueue) {
                        if (!this.stateTimestampDrop) {
                            this.messageReceived(smsg, smsg.getAddress());
                        }
                        else if (smsg.getEventType() != 4 && smsg.getTimestamp() >= this.stateTransferCreateSendTime) {
                            this.messageReceived(smsg, smsg.getAddress());
                        }
                        else {
                            if (!this.log.isWarnEnabled()) {
                                continue;
                            }
                            this.log.warn((Object)DeltaManager.sm.getString("deltaManager.dropMessage", new Object[] { this.getName(), smsg.getEventTypeString(), new Date(this.stateTransferCreateSendTime), new Date(smsg.getTimestamp()) }));
                        }
                    }
                    this.receivedMessageQueue.clear();
                    this.receiverQueue = false;
                }
            }
        }
        else if (this.log.isInfoEnabled()) {
            this.log.info((Object)DeltaManager.sm.getString("deltaManager.noMembers", new Object[] { this.getName() }));
        }
    }
    
    protected Member findSessionMasterMember() {
        Member mbr = null;
        final Member[] mbrs = this.cluster.getMembers();
        if (mbrs.length != 0) {
            mbr = mbrs[0];
        }
        if (mbr == null && this.log.isWarnEnabled()) {
            this.log.warn((Object)DeltaManager.sm.getString("deltaManager.noMasterMember", new Object[] { this.getName(), "" }));
        }
        if (mbr != null && this.log.isDebugEnabled()) {
            this.log.debug((Object)DeltaManager.sm.getString("deltaManager.foundMasterMember", new Object[] { this.getName(), mbr }));
        }
        return mbr;
    }
    
    protected void waitForSendAllSessions(final long beforeSendTime) {
        long reqNow;
        final long reqStart = reqNow = System.currentTimeMillis();
        boolean isTimeout = false;
        if (this.getStateTransferTimeout() > 0) {
            do {
                try {
                    Thread.sleep(100L);
                }
                catch (final Exception ex) {}
                reqNow = System.currentTimeMillis();
                isTimeout = (reqNow - reqStart > 1000L * this.getStateTransferTimeout());
                if (this.getStateTransferred() || isTimeout) {
                    break;
                }
            } while (!this.isNoContextManagerReceived());
        }
        else if (this.getStateTransferTimeout() == -1) {
            do {
                try {
                    Thread.sleep(100L);
                }
                catch (final Exception ex2) {}
            } while (!this.getStateTransferred() && !this.isNoContextManagerReceived());
            reqNow = System.currentTimeMillis();
        }
        if (isTimeout) {
            ++this.counterNoStateTransferred;
            this.log.error((Object)DeltaManager.sm.getString("deltaManager.noSessionState", new Object[] { this.getName(), new Date(beforeSendTime), reqNow - beforeSendTime }));
        }
        else if (this.isNoContextManagerReceived()) {
            if (this.log.isWarnEnabled()) {
                this.log.warn((Object)DeltaManager.sm.getString("deltaManager.noContextManager", new Object[] { this.getName(), new Date(beforeSendTime), reqNow - beforeSendTime }));
            }
        }
        else if (this.log.isInfoEnabled()) {
            this.log.info((Object)DeltaManager.sm.getString("deltaManager.sessionReceived", new Object[] { this.getName(), new Date(beforeSendTime), reqNow - beforeSendTime }));
        }
    }
    
    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)DeltaManager.sm.getString("deltaManager.stopped", new Object[] { this.getName() }));
        }
        this.setState(LifecycleState.STOPPING);
        if (this.log.isInfoEnabled()) {
            this.log.info((Object)DeltaManager.sm.getString("deltaManager.expireSessions", new Object[] { this.getName() }));
        }
        final Session[] arr$;
        final Session[] sessions = arr$ = this.findSessions();
        for (final Session value : arr$) {
            final DeltaSession session = (DeltaSession)value;
            if (session.isValid()) {
                try {
                    session.expire(true, this.isExpireSessionsOnShutdown());
                }
                catch (final Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                }
            }
        }
        super.stopInternal();
    }
    
    public void messageDataReceived(final ClusterMessage cmsg) {
        if (cmsg instanceof SessionMessage) {
            final SessionMessage msg = (SessionMessage)cmsg;
            switch (msg.getEventType()) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 13:
                case 15: {
                    synchronized (this.receivedMessageQueue) {
                        if (this.receiverQueue) {
                            this.receivedMessageQueue.add(msg);
                            return;
                        }
                    }
                    break;
                }
            }
            this.messageReceived(msg, msg.getAddress());
        }
    }
    
    public ClusterMessage requestCompleted(final String sessionId) {
        return this.requestCompleted(sessionId, false);
    }
    
    public ClusterMessage requestCompleted(final String sessionId, final boolean expires) {
        DeltaSession session = null;
        SessionMessage msg = null;
        try {
            session = (DeltaSession)this.findSession(sessionId);
            if (session == null) {
                return null;
            }
            if (session.isDirty()) {
                ++this.counterSend_EVT_SESSION_DELTA;
                msg = new SessionMessageImpl(this.getName(), 13, session.getDiff(), sessionId, sessionId + "-" + System.currentTimeMillis());
            }
        }
        catch (final IOException x) {
            this.log.error((Object)DeltaManager.sm.getString("deltaManager.createMessage.unableCreateDeltaRequest", new Object[] { sessionId }), (Throwable)x);
            return null;
        }
        if (msg == null) {
            if (!expires && !session.isPrimarySession()) {
                ++this.counterSend_EVT_SESSION_ACCESSED;
                msg = new SessionMessageImpl(this.getName(), 3, null, sessionId, sessionId + "-" + System.currentTimeMillis());
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)DeltaManager.sm.getString("deltaManager.createMessage.accessChangePrimary", new Object[] { this.getName(), sessionId }));
                }
            }
        }
        else if (this.log.isDebugEnabled()) {
            this.log.debug((Object)DeltaManager.sm.getString("deltaManager.createMessage.delta", new Object[] { this.getName(), sessionId }));
        }
        if (!expires) {
            session.setPrimarySession(true);
        }
        if (!expires && msg == null) {
            final long replDelta = System.currentTimeMillis() - session.getLastTimeReplicated();
            if (session.getMaxInactiveInterval() >= 0 && replDelta > session.getMaxInactiveInterval() * 1000L) {
                ++this.counterSend_EVT_SESSION_ACCESSED;
                msg = new SessionMessageImpl(this.getName(), 3, null, sessionId, sessionId + "-" + System.currentTimeMillis());
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)DeltaManager.sm.getString("deltaManager.createMessage.access", new Object[] { this.getName(), sessionId }));
                }
            }
        }
        if (msg != null) {
            session.setLastTimeReplicated(System.currentTimeMillis());
            msg.setTimestamp(session.getLastTimeReplicated());
        }
        return msg;
    }
    
    public synchronized void resetStatistics() {
        this.processingTime = 0L;
        this.expiredSessions.set(0L);
        synchronized (this.sessionCreationTiming) {
            this.sessionCreationTiming.clear();
            while (this.sessionCreationTiming.size() < 100) {
                this.sessionCreationTiming.add(null);
            }
        }
        synchronized (this.sessionExpirationTiming) {
            this.sessionExpirationTiming.clear();
            while (this.sessionExpirationTiming.size() < 100) {
                this.sessionExpirationTiming.add(null);
            }
        }
        this.rejectedSessions = 0;
        this.sessionReplaceCounter = 0L;
        this.counterNoStateTransferred = 0;
        this.setMaxActive(this.getActiveSessions());
        this.sessionCounter = this.getActiveSessions();
        this.counterReceive_EVT_ALL_SESSION_DATA = 0L;
        this.counterReceive_EVT_GET_ALL_SESSIONS = 0L;
        this.counterReceive_EVT_SESSION_ACCESSED = 0L;
        this.counterReceive_EVT_SESSION_CREATED = 0L;
        this.counterReceive_EVT_SESSION_DELTA = 0L;
        this.counterReceive_EVT_SESSION_EXPIRED = 0L;
        this.counterReceive_EVT_ALL_SESSION_TRANSFERCOMPLETE = 0;
        this.counterReceive_EVT_CHANGE_SESSION_ID = 0L;
        this.counterSend_EVT_ALL_SESSION_DATA = 0L;
        this.counterSend_EVT_GET_ALL_SESSIONS = 0L;
        this.counterSend_EVT_SESSION_ACCESSED = 0L;
        this.counterSend_EVT_SESSION_CREATED = 0L;
        this.counterSend_EVT_SESSION_DELTA = 0L;
        this.counterSend_EVT_SESSION_EXPIRED = 0L;
        this.counterSend_EVT_ALL_SESSION_TRANSFERCOMPLETE = 0;
        this.counterSend_EVT_CHANGE_SESSION_ID = 0L;
    }
    
    protected void sessionExpired(final String id) {
        if (this.cluster.getMembers().length > 0) {
            ++this.counterSend_EVT_SESSION_EXPIRED;
            final SessionMessage msg = new SessionMessageImpl(this.getName(), 2, null, id, id + "-EXPIRED-MSG");
            msg.setTimestamp(System.currentTimeMillis());
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)DeltaManager.sm.getString("deltaManager.createMessage.expire", new Object[] { this.getName(), id }));
            }
            this.send(msg);
        }
    }
    
    public void expireAllLocalSessions() {
        final long timeNow = System.currentTimeMillis();
        final Session[] sessions = this.findSessions();
        int expireDirect = 0;
        int expireIndirect = 0;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Start expire all sessions " + this.getName() + " at " + timeNow + " sessioncount " + sessions.length));
        }
        for (final Session value : sessions) {
            if (value instanceof DeltaSession) {
                final DeltaSession session = (DeltaSession)value;
                if (session.isPrimarySession()) {
                    if (session.isValid()) {
                        session.expire();
                        ++expireDirect;
                    }
                    else {
                        ++expireIndirect;
                    }
                }
            }
        }
        final long timeEnd = System.currentTimeMillis();
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("End expire sessions " + this.getName() + " expire processingTime " + (timeEnd - timeNow) + " expired direct sessions: " + expireDirect + " expired direct sessions: " + expireIndirect));
        }
    }
    
    public String[] getInvalidatedSessions() {
        return new String[0];
    }
    
    protected void messageReceived(final SessionMessage msg, final Member sender) {
        final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        try {
            final ClassLoader[] loaders = this.getClassLoaders();
            Thread.currentThread().setContextClassLoader(loaders[0]);
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)DeltaManager.sm.getString("deltaManager.receiveMessage.eventType", new Object[] { this.getName(), msg.getEventTypeString(), sender }));
            }
            switch (msg.getEventType()) {
                case 4: {
                    this.handleGET_ALL_SESSIONS(msg, sender);
                    break;
                }
                case 12: {
                    this.handleALL_SESSION_DATA(msg, sender);
                    break;
                }
                case 14: {
                    this.handleALL_SESSION_TRANSFERCOMPLETE(msg, sender);
                    break;
                }
                case 1: {
                    this.handleSESSION_CREATED(msg, sender);
                    break;
                }
                case 2: {
                    this.handleSESSION_EXPIRED(msg, sender);
                    break;
                }
                case 3: {
                    this.handleSESSION_ACCESSED(msg, sender);
                    break;
                }
                case 13: {
                    this.handleSESSION_DELTA(msg, sender);
                    break;
                }
                case 15: {
                    this.handleCHANGE_SESSION_ID(msg, sender);
                    break;
                }
                case 16: {
                    this.handleALL_SESSION_NOCONTEXTMANAGER(msg, sender);
                    break;
                }
            }
        }
        catch (final Exception x) {
            this.log.error((Object)DeltaManager.sm.getString("deltaManager.receiveMessage.error", new Object[] { this.getName() }), (Throwable)x);
        }
        finally {
            Thread.currentThread().setContextClassLoader(contextLoader);
        }
    }
    
    protected void handleALL_SESSION_TRANSFERCOMPLETE(final SessionMessage msg, final Member sender) {
        ++this.counterReceive_EVT_ALL_SESSION_TRANSFERCOMPLETE;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)DeltaManager.sm.getString("deltaManager.receiveMessage.transfercomplete", new Object[] { this.getName(), sender.getHost(), sender.getPort() }));
        }
        this.stateTransferCreateSendTime = msg.getTimestamp();
        this.stateTransferred = true;
    }
    
    protected void handleSESSION_DELTA(final SessionMessage msg, final Member sender) throws IOException, ClassNotFoundException {
        ++this.counterReceive_EVT_SESSION_DELTA;
        final byte[] delta = msg.getSession();
        final DeltaSession session = (DeltaSession)this.findSession(msg.getSessionID());
        if (session == null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)DeltaManager.sm.getString("deltaManager.receiveMessage.delta.unknown", new Object[] { this.getName(), msg.getSessionID() }));
            }
        }
        else {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)DeltaManager.sm.getString("deltaManager.receiveMessage.delta", new Object[] { this.getName(), msg.getSessionID() }));
            }
            session.deserializeAndExecuteDeltaRequest(delta);
        }
    }
    
    protected void handleSESSION_ACCESSED(final SessionMessage msg, final Member sender) throws IOException {
        ++this.counterReceive_EVT_SESSION_ACCESSED;
        final DeltaSession session = (DeltaSession)this.findSession(msg.getSessionID());
        if (session != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)DeltaManager.sm.getString("deltaManager.receiveMessage.accessed", new Object[] { this.getName(), msg.getSessionID() }));
            }
            session.access();
            session.setPrimarySession(false);
            session.endAccess();
        }
    }
    
    protected void handleSESSION_EXPIRED(final SessionMessage msg, final Member sender) throws IOException {
        ++this.counterReceive_EVT_SESSION_EXPIRED;
        final DeltaSession session = (DeltaSession)this.findSession(msg.getSessionID());
        if (session != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)DeltaManager.sm.getString("deltaManager.receiveMessage.expired", new Object[] { this.getName(), msg.getSessionID() }));
            }
            session.expire(this.notifySessionListenersOnReplication, false);
        }
    }
    
    protected void handleSESSION_CREATED(final SessionMessage msg, final Member sender) {
        ++this.counterReceive_EVT_SESSION_CREATED;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)DeltaManager.sm.getString("deltaManager.receiveMessage.createNewSession", new Object[] { this.getName(), msg.getSessionID() }));
        }
        final DeltaSession session = (DeltaSession)this.createEmptySession();
        session.setValid(true);
        session.setPrimarySession(false);
        session.setCreationTime(msg.getTimestamp());
        session.setMaxInactiveInterval(this.getContext().getSessionTimeout() * 60, false);
        session.access();
        session.setId(msg.getSessionID(), this.notifySessionListenersOnReplication);
        session.endAccess();
    }
    
    protected void handleALL_SESSION_DATA(final SessionMessage msg, final Member sender) throws ClassNotFoundException, IOException {
        ++this.counterReceive_EVT_ALL_SESSION_DATA;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)DeltaManager.sm.getString("deltaManager.receiveMessage.allSessionDataBegin", new Object[] { this.getName() }));
        }
        final byte[] data = msg.getSession();
        this.deserializeSessions(data);
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)DeltaManager.sm.getString("deltaManager.receiveMessage.allSessionDataAfter", new Object[] { this.getName() }));
        }
    }
    
    protected void handleGET_ALL_SESSIONS(final SessionMessage msg, final Member sender) throws IOException {
        ++this.counterReceive_EVT_GET_ALL_SESSIONS;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)DeltaManager.sm.getString("deltaManager.receiveMessage.unloadingBegin", new Object[] { this.getName() }));
        }
        final Session[] currentSessions = this.findSessions();
        final long findSessionTimestamp = System.currentTimeMillis();
        if (this.isSendAllSessions()) {
            this.sendSessions(sender, currentSessions, findSessionTimestamp);
        }
        else {
            int remain = currentSessions.length;
            for (int i = 0; i < currentSessions.length; i += this.getSendAllSessionsSize()) {
                final int len = (i + this.getSendAllSessionsSize() > currentSessions.length) ? (currentSessions.length - i) : this.getSendAllSessionsSize();
                final Session[] sendSessions = new Session[len];
                System.arraycopy(currentSessions, i, sendSessions, 0, len);
                this.sendSessions(sender, sendSessions, findSessionTimestamp);
                remain -= len;
                if (this.getSendAllSessionsWaitTime() > 0 && remain > 0) {
                    try {
                        Thread.sleep(this.getSendAllSessionsWaitTime());
                    }
                    catch (final Exception ex) {}
                }
            }
        }
        final SessionMessage newmsg = new SessionMessageImpl(this.name, 14, null, "SESSION-STATE-TRANSFERRED", "SESSION-STATE-TRANSFERRED" + this.getName());
        newmsg.setTimestamp(findSessionTimestamp);
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)DeltaManager.sm.getString("deltaManager.createMessage.allSessionTransferred", new Object[] { this.getName() }));
        }
        ++this.counterSend_EVT_ALL_SESSION_TRANSFERCOMPLETE;
        this.cluster.send(newmsg, sender);
    }
    
    protected void handleCHANGE_SESSION_ID(final SessionMessage msg, final Member sender) throws IOException {
        ++this.counterReceive_EVT_CHANGE_SESSION_ID;
        final DeltaSession session = (DeltaSession)this.findSession(msg.getSessionID());
        if (session != null) {
            final String newSessionID = this.deserializeSessionId(msg.getSession());
            session.setPrimarySession(false);
            this.changeSessionId((Session)session, newSessionID, this.notifySessionListenersOnReplication, this.notifyContainerListenersOnReplication);
        }
    }
    
    protected void handleALL_SESSION_NOCONTEXTMANAGER(final SessionMessage msg, final Member sender) {
        ++this.counterReceive_EVT_ALL_SESSION_NOCONTEXTMANAGER;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)DeltaManager.sm.getString("deltaManager.receiveMessage.noContextManager", new Object[] { this.getName(), sender.getHost(), sender.getPort() }));
        }
        this.noContextManagerReceived = true;
    }
    
    protected void sendSessions(final Member sender, final Session[] currentSessions, final long sendTimestamp) throws IOException {
        final byte[] data = this.serializeSessions(currentSessions);
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)DeltaManager.sm.getString("deltaManager.receiveMessage.unloadingAfter", new Object[] { this.getName() }));
        }
        final SessionMessage newmsg = new SessionMessageImpl(this.name, 12, data, "SESSION-STATE", "SESSION-STATE-" + this.getName());
        newmsg.setTimestamp(sendTimestamp);
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)DeltaManager.sm.getString("deltaManager.createMessage.allSessionData", new Object[] { this.getName() }));
        }
        ++this.counterSend_EVT_ALL_SESSION_DATA;
        this.cluster.send(newmsg, sender);
    }
    
    public ClusterManager cloneFromTemplate() {
        final DeltaManager result = new DeltaManager();
        this.clone(result);
        result.expireSessionsOnShutdown = this.expireSessionsOnShutdown;
        result.notifySessionListenersOnReplication = this.notifySessionListenersOnReplication;
        result.notifyContainerListenersOnReplication = this.notifyContainerListenersOnReplication;
        result.stateTransferTimeout = this.stateTransferTimeout;
        result.sendAllSessions = this.sendAllSessions;
        result.sendAllSessionsSize = this.sendAllSessionsSize;
        result.sendAllSessionsWaitTime = this.sendAllSessionsWaitTime;
        result.stateTimestampDrop = this.stateTimestampDrop;
        return result;
    }
    
    static {
        sm = StringManager.getManager((Class)DeltaManager.class);
    }
}
