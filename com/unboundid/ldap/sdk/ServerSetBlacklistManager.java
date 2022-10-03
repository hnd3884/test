package com.unboundid.ldap.sdk;

import java.util.Iterator;
import com.unboundid.util.Debug;
import java.util.TimerTask;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Validator;
import javax.net.SocketFactory;
import com.unboundid.util.ObjectPair;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicReference;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class ServerSetBlacklistManager
{
    private final AtomicReference<Timer> timerReference;
    private final BindRequest bindRequest;
    private final LDAPConnectionOptions connectionOptions;
    private final long checkIntervalMillis;
    private final Map<ObjectPair<String, Integer>, LDAPConnectionPoolHealthCheck> blacklistedServers;
    private final PostConnectProcessor postConnectProcessor;
    private final SocketFactory socketFactory;
    private final String serverSetString;
    
    ServerSetBlacklistManager(final ServerSet serverSet, final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions, final BindRequest bindRequest, final PostConnectProcessor postConnectProcessor, final long checkIntervalMillis) {
        Validator.ensureTrue(checkIntervalMillis > 0L, "ServerSetBlacklistManager.checkIntervalMillis must be greater than zero.");
        this.checkIntervalMillis = checkIntervalMillis;
        this.serverSetString = serverSet.toString();
        if (socketFactory == null) {
            this.socketFactory = SocketFactory.getDefault();
        }
        else {
            this.socketFactory = socketFactory;
        }
        if (connectionOptions == null) {
            this.connectionOptions = new LDAPConnectionOptions();
        }
        else {
            this.connectionOptions = connectionOptions;
        }
        this.bindRequest = bindRequest;
        this.postConnectProcessor = postConnectProcessor;
        this.blacklistedServers = new ConcurrentHashMap<ObjectPair<String, Integer>, LDAPConnectionPoolHealthCheck>(StaticUtils.computeMapCapacity(10));
        this.timerReference = new AtomicReference<Timer>();
    }
    
    boolean isEmpty() {
        if (this.blacklistedServers.isEmpty()) {
            return true;
        }
        this.ensureTimerIsRunning();
        return false;
    }
    
    int size() {
        if (this.blacklistedServers.isEmpty()) {
            return 0;
        }
        this.ensureTimerIsRunning();
        return this.blacklistedServers.size();
    }
    
    Set<ObjectPair<String, Integer>> getBlacklistedServers() {
        if (!this.blacklistedServers.isEmpty()) {
            this.ensureTimerIsRunning();
        }
        return new HashSet<ObjectPair<String, Integer>>(this.blacklistedServers.keySet());
    }
    
    boolean isBlacklisted(final String host, final int port) {
        if (this.blacklistedServers.isEmpty()) {
            return false;
        }
        this.ensureTimerIsRunning();
        return this.blacklistedServers.containsKey(new ObjectPair(host, port));
    }
    
    boolean isBlacklisted(final ObjectPair<String, Integer> hostPort) {
        if (this.blacklistedServers.isEmpty()) {
            return false;
        }
        this.ensureTimerIsRunning();
        return this.blacklistedServers.containsKey(hostPort);
    }
    
    void addToBlacklist(final String host, final int port, final LDAPConnectionPoolHealthCheck healthCheck) {
        this.addToBlacklist(new ObjectPair<String, Integer>(host, port), healthCheck);
    }
    
    void addToBlacklist(final ObjectPair<String, Integer> hostPort, final LDAPConnectionPoolHealthCheck healthCheck) {
        if (healthCheck == null) {
            this.blacklistedServers.put(hostPort, new LDAPConnectionPoolHealthCheck());
        }
        else {
            this.blacklistedServers.put(hostPort, healthCheck);
        }
        this.ensureTimerIsRunning();
    }
    
    void removeFromBlacklist(final String host, final int port) {
        this.removeFromBlacklist(new ObjectPair<String, Integer>(host, port));
    }
    
    void removeFromBlacklist(final ObjectPair<String, Integer> hostPort) {
        this.blacklistedServers.remove(hostPort);
        if (!this.blacklistedServers.isEmpty()) {
            this.ensureTimerIsRunning();
        }
    }
    
    void clear() {
        this.blacklistedServers.clear();
    }
    
    private synchronized void ensureTimerIsRunning() {
        Timer timer = this.timerReference.get();
        if (timer == null) {
            timer = new Timer("ServerSet Blacklist Manager Timer for " + this.serverSetString, true);
            this.timerReference.set(timer);
            timer.scheduleAtFixedRate(new ServerSetBlacklistManagerTimerTask(this), this.checkIntervalMillis, this.checkIntervalMillis);
        }
    }
    
    void checkBlacklistedServers() {
        final Iterator<Map.Entry<ObjectPair<String, Integer>, LDAPConnectionPoolHealthCheck>> iterator = this.blacklistedServers.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<ObjectPair<String, Integer>, LDAPConnectionPoolHealthCheck> e = iterator.next();
            final ObjectPair<String, Integer> hostPort = e.getKey();
            final LDAPConnectionPoolHealthCheck healthCheck = e.getValue();
            try (final LDAPConnection conn = new LDAPConnection(this.socketFactory, this.connectionOptions, hostPort.getFirst(), hostPort.getSecond())) {
                ServerSet.doBindPostConnectAndHealthCheckProcessing(conn, this.bindRequest, this.postConnectProcessor, healthCheck);
                iterator.remove();
            }
            catch (final Exception ex) {
                Debug.debugException(ex);
            }
        }
        if (this.blacklistedServers.isEmpty()) {
            synchronized (this) {
                if (this.blacklistedServers.isEmpty()) {
                    final Timer timer = this.timerReference.getAndSet(null);
                    if (timer != null) {
                        timer.cancel();
                        timer.purge();
                    }
                }
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    void toString(final StringBuilder buffer) {
        buffer.append("ServerSetBlacklistManager(serverSet='");
        buffer.append(this.serverSetString);
        buffer.append("', blacklistedServers={");
        final Iterator<ObjectPair<String, Integer>> iterator = this.blacklistedServers.keySet().iterator();
        while (iterator.hasNext()) {
            final ObjectPair<String, Integer> hostPort = iterator.next();
            buffer.append('\'');
            buffer.append(hostPort.getFirst());
            buffer.append(':');
            buffer.append(hostPort.getSecond());
            buffer.append('\'');
            if (iterator.hasNext()) {
                buffer.append(',');
            }
        }
        buffer.append("}, checkIntervalMillis=");
        buffer.append(this.checkIntervalMillis);
        buffer.append(')');
    }
}
