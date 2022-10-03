package com.unboundid.ldap.sdk;

import com.unboundid.util.Debug;
import java.util.List;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Validator;
import javax.net.SocketFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class FailoverServerSet extends ServerSet
{
    private final AtomicBoolean reOrderOnFailover;
    private volatile Long maxFailoverConnectionAge;
    private final ServerSet[] serverSets;
    
    public FailoverServerSet(final String[] addresses, final int[] ports) {
        this(addresses, ports, null, null);
    }
    
    public FailoverServerSet(final String[] addresses, final int[] ports, final LDAPConnectionOptions connectionOptions) {
        this(addresses, ports, null, connectionOptions);
    }
    
    public FailoverServerSet(final String[] addresses, final int[] ports, final SocketFactory socketFactory) {
        this(addresses, ports, socketFactory, null);
    }
    
    public FailoverServerSet(final String[] addresses, final int[] ports, final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions) {
        this(addresses, ports, socketFactory, connectionOptions, null, null);
    }
    
    public FailoverServerSet(final String[] addresses, final int[] ports, final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions, final BindRequest bindRequest, final PostConnectProcessor postConnectProcessor) {
        Validator.ensureNotNull(addresses, ports);
        Validator.ensureTrue(addresses.length > 0, "FailoverServerSet.addresses must not be empty.");
        Validator.ensureTrue(addresses.length == ports.length, "FailoverServerSet addresses and ports arrays must be the same size.");
        this.reOrderOnFailover = new AtomicBoolean(false);
        this.maxFailoverConnectionAge = null;
        SocketFactory sf;
        if (socketFactory == null) {
            sf = SocketFactory.getDefault();
        }
        else {
            sf = socketFactory;
        }
        LDAPConnectionOptions co;
        if (connectionOptions == null) {
            co = new LDAPConnectionOptions();
        }
        else {
            co = connectionOptions;
        }
        this.serverSets = new ServerSet[addresses.length];
        for (int i = 0; i < this.serverSets.length; ++i) {
            this.serverSets[i] = new SingleServerSet(addresses[i], ports[i], sf, co, bindRequest, postConnectProcessor);
        }
    }
    
    public FailoverServerSet(final ServerSet... serverSets) {
        this(StaticUtils.toList(serverSets));
    }
    
    public FailoverServerSet(final List<ServerSet> serverSets) {
        Validator.ensureNotNull(serverSets);
        Validator.ensureFalse(serverSets.isEmpty(), "FailoverServerSet.serverSets must not be empty.");
        serverSets.toArray(this.serverSets = new ServerSet[serverSets.size()]);
        boolean anySupportsAuthentication = false;
        boolean allSupportAuthentication = true;
        boolean anySupportsPostConnectProcessing = false;
        boolean allSupportPostConnectProcessing = true;
        for (final ServerSet serverSet : this.serverSets) {
            if (serverSet.includesAuthentication()) {
                anySupportsAuthentication = true;
            }
            else {
                allSupportAuthentication = false;
            }
            if (serverSet.includesPostConnectProcessing()) {
                anySupportsPostConnectProcessing = true;
            }
            else {
                allSupportPostConnectProcessing = false;
            }
        }
        if (anySupportsAuthentication) {
            Validator.ensureTrue(allSupportAuthentication, "When creating a FailoverServerSet from a collection of server sets, either all of those sets must include authentication, or none of those sets may include authentication.");
        }
        if (anySupportsPostConnectProcessing) {
            Validator.ensureTrue(allSupportPostConnectProcessing, "When creating a FailoverServerSet from a collection of server sets, either all of those sets must include post-connect processing, or none of those sets may include post-connect processing.");
        }
        this.reOrderOnFailover = new AtomicBoolean(false);
        this.maxFailoverConnectionAge = null;
    }
    
    public ServerSet[] getServerSets() {
        return this.serverSets;
    }
    
    public boolean reOrderOnFailover() {
        return this.reOrderOnFailover.get();
    }
    
    public void setReOrderOnFailover(final boolean reOrderOnFailover) {
        this.reOrderOnFailover.set(reOrderOnFailover);
    }
    
    public Long getMaxFailoverConnectionAgeMillis() {
        return this.maxFailoverConnectionAge;
    }
    
    public void setMaxFailoverConnectionAgeMillis(final Long maxFailoverConnectionAge) {
        if (maxFailoverConnectionAge == null) {
            this.maxFailoverConnectionAge = null;
        }
        else if (maxFailoverConnectionAge > 0L) {
            this.maxFailoverConnectionAge = maxFailoverConnectionAge;
        }
        else {
            this.maxFailoverConnectionAge = 0L;
        }
    }
    
    @Override
    public boolean includesAuthentication() {
        return this.serverSets[0].includesAuthentication();
    }
    
    @Override
    public boolean includesPostConnectProcessing() {
        return this.serverSets[0].includesPostConnectProcessing();
    }
    
    @Override
    public LDAPConnection getConnection() throws LDAPException {
        return this.getConnection(null);
    }
    
    @Override
    public LDAPConnection getConnection(final LDAPConnectionPoolHealthCheck healthCheck) throws LDAPException {
        if (this.reOrderOnFailover.get() && this.serverSets.length > 1) {
            synchronized (this) {
                try {
                    return this.serverSets[0].getConnection(healthCheck);
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    int successfulPos = -1;
                    LDAPConnection conn = null;
                    LDAPException lastException = null;
                    int i = 1;
                    while (i < this.serverSets.length) {
                        try {
                            conn = this.serverSets[i].getConnection(healthCheck);
                            successfulPos = i;
                        }
                        catch (final LDAPException le2) {
                            Debug.debugException(le2);
                            lastException = le2;
                            ++i;
                            continue;
                        }
                        break;
                    }
                    if (successfulPos > 0) {
                        int pos = 0;
                        final ServerSet[] setCopy = new ServerSet[this.serverSets.length];
                        for (int j = successfulPos; j < this.serverSets.length; ++j) {
                            setCopy[pos++] = this.serverSets[j];
                        }
                        for (int j = 0; j < successfulPos; ++j) {
                            setCopy[pos++] = this.serverSets[j];
                        }
                        System.arraycopy(setCopy, 0, this.serverSets, 0, setCopy.length);
                        if (this.maxFailoverConnectionAge != null) {
                            conn.setAttachment(LDAPConnectionPool.ATTACHMENT_NAME_MAX_CONNECTION_AGE, this.maxFailoverConnectionAge);
                        }
                        return conn;
                    }
                    throw lastException;
                }
            }
        }
        LDAPException lastException2 = null;
        boolean first = true;
        final ServerSet[] arr$ = this.serverSets;
        final int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            final ServerSet s = arr$[i$];
            try {
                final LDAPConnection conn2 = s.getConnection(healthCheck);
                if (!first && this.maxFailoverConnectionAge != null) {
                    conn2.setAttachment(LDAPConnectionPool.ATTACHMENT_NAME_MAX_CONNECTION_AGE, this.maxFailoverConnectionAge);
                }
                return conn2;
            }
            catch (final LDAPException le3) {
                first = false;
                Debug.debugException(le3);
                lastException2 = le3;
                ++i$;
                continue;
            }
            break;
        }
        throw lastException2;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("FailoverServerSet(serverSets={");
        for (int i = 0; i < this.serverSets.length; ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            this.serverSets[i].toString(buffer);
        }
        buffer.append("})");
    }
}
