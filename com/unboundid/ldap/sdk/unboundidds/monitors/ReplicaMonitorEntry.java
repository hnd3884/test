package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.Collections;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ReplicaMonitorEntry extends MonitorEntry
{
    static final String REPLICA_MONITOR_OC = "ds-replica-monitor-entry";
    private static final String ATTR_BASE_DN = "base-dn";
    private static final String ATTR_CONNECTED_TO = "connected-to";
    private static final String ATTR_CURRENT_RECEIVE_WINDOW_SIZE = "current-rcv-window";
    private static final String ATTR_CURRENT_SEND_WINDOW_SIZE = "current-send-window";
    private static final String ATTR_GENERATION_ID = "generation-id";
    private static final String ATTR_LOST_CONNECTIONS = "lost-connections";
    private static final String ATTR_MAX_RECEIVE_WINDOW_SIZE = "max-rcv-window";
    private static final String ATTR_MAX_SEND_WINDOW_SIZE = "max-send-window";
    private static final String ATTR_PENDING_UPDATES = "pending-updates";
    private static final String ATTR_RECEIVED_UPDATES = "received-updates";
    private static final String ATTR_REPLICA_ID = "replica-id";
    private static final String ATTR_RESOLVED_MODIFY_CONFLICTS = "resolved-modify-conflicts";
    private static final String ATTR_RESOLVED_NAMING_CONFLICTS = "resolved-naming-conflicts";
    private static final String ATTR_SENT_UPDATES = "sent-updates";
    private static final String ATTR_SSL_ENCRYPTION = "ssl-encryption";
    private static final String ATTR_SUCCESSFUL_REPLAYED = "replayed-updates-ok";
    private static final String ATTR_TOTAL_REPLAYED = "replayed-updates";
    private static final String ATTR_UNRESOLVED_NAMING_CONFLICTS = "unresolved-naming-conflicts";
    private static final long serialVersionUID = -9164207693317460579L;
    private final Boolean useSSL;
    private final Long currentReceiveWindowSize;
    private final Long currentSendWindowSize;
    private final Long lostConnections;
    private final Long maxReceiveWindowSize;
    private final Long maxSendWindowSize;
    private final Long pendingUpdates;
    private final Long receivedUpdates;
    private final Long replayedAfterModifyConflict;
    private final Long replayedAfterNamingConflict;
    private final Long replicationServerPort;
    private final Long sentUpdates;
    private final Long successfullyReplayed;
    private final Long totalReplayed;
    private final Long unresolvedNamingConflicts;
    private final String baseDN;
    private final String generationID;
    private final String replicaID;
    private final String replicationServerAddress;
    
    public ReplicaMonitorEntry(final Entry entry) {
        super(entry);
        this.useSSL = this.getBoolean("ssl-encryption");
        this.lostConnections = this.getLong("lost-connections");
        this.receivedUpdates = this.getLong("received-updates");
        this.sentUpdates = this.getLong("sent-updates");
        this.pendingUpdates = this.getLong("pending-updates");
        this.totalReplayed = this.getLong("replayed-updates");
        this.successfullyReplayed = this.getLong("replayed-updates-ok");
        this.replayedAfterModifyConflict = this.getLong("resolved-modify-conflicts");
        this.replayedAfterNamingConflict = this.getLong("resolved-naming-conflicts");
        this.unresolvedNamingConflicts = this.getLong("unresolved-naming-conflicts");
        this.currentReceiveWindowSize = this.getLong("current-rcv-window");
        this.currentSendWindowSize = this.getLong("current-send-window");
        this.maxReceiveWindowSize = this.getLong("max-rcv-window");
        this.maxSendWindowSize = this.getLong("max-send-window");
        this.baseDN = this.getString("base-dn");
        this.generationID = this.getString("generation-id");
        this.replicaID = this.getString("replica-id");
        String addr = null;
        Long port = null;
        final String connectedTo = this.getString("connected-to");
        if (connectedTo != null) {
            try {
                final int colonPos = connectedTo.indexOf(58);
                if (colonPos > 0) {
                    addr = connectedTo.substring(0, colonPos);
                    port = Long.parseLong(connectedTo.substring(colonPos + 1));
                }
            }
            catch (final Exception e) {
                Debug.debugException(e);
                addr = null;
                port = null;
            }
        }
        this.replicationServerAddress = addr;
        this.replicationServerPort = port;
    }
    
    public String getBaseDN() {
        return this.baseDN;
    }
    
    public String getReplicaID() {
        return this.replicaID;
    }
    
    public String getGenerationID() {
        return this.generationID;
    }
    
    public String getReplicationServerAddress() {
        return this.replicationServerAddress;
    }
    
    public Long getReplicationServerPort() {
        return this.replicationServerPort;
    }
    
    public Boolean useSSL() {
        return this.useSSL;
    }
    
    public Long getLostConnections() {
        return this.lostConnections;
    }
    
    public Long getReceivedUpdates() {
        return this.receivedUpdates;
    }
    
    public Long getSentUpdates() {
        return this.sentUpdates;
    }
    
    public Long getPendingUpdates() {
        return this.pendingUpdates;
    }
    
    public Long getTotalUpdatesReplayed() {
        return this.totalReplayed;
    }
    
    public Long getUpdatesSuccessfullyReplayed() {
        return this.successfullyReplayed;
    }
    
    public Long getUpdatesReplayedAfterModifyConflict() {
        return this.replayedAfterModifyConflict;
    }
    
    public Long getUpdatesReplayedAfterNamingConflict() {
        return this.replayedAfterNamingConflict;
    }
    
    public Long getUnresolvedNamingConflicts() {
        return this.unresolvedNamingConflicts;
    }
    
    public Long getCurrentReceiveWindowSize() {
        return this.currentReceiveWindowSize;
    }
    
    public Long getCurrentSendWindowSize() {
        return this.currentSendWindowSize;
    }
    
    public Long getMaximumReceiveWindowSize() {
        return this.maxReceiveWindowSize;
    }
    
    public Long getMaximumSendWindowSize() {
        return this.maxSendWindowSize;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_REPLICA_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_REPLICA_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(30));
        if (this.baseDN != null) {
            MonitorEntry.addMonitorAttribute(attrs, "base-dn", MonitorMessages.INFO_REPLICA_DISPNAME_BASE_DN.get(), MonitorMessages.INFO_REPLICA_DESC_BASE_DN.get(), this.baseDN);
        }
        if (this.replicaID != null) {
            MonitorEntry.addMonitorAttribute(attrs, "replica-id", MonitorMessages.INFO_REPLICA_DISPNAME_REPLICA_ID.get(), MonitorMessages.INFO_REPLICA_DESC_REPLICA_ID.get(), this.replicaID);
        }
        if (this.generationID != null) {
            MonitorEntry.addMonitorAttribute(attrs, "generation-id", MonitorMessages.INFO_REPLICA_DISPNAME_GENERATION_ID.get(), MonitorMessages.INFO_REPLICA_DESC_GENERATION_ID.get(), this.generationID);
        }
        if (this.replicationServerAddress != null) {
            MonitorEntry.addMonitorAttribute(attrs, "connected-to", MonitorMessages.INFO_REPLICA_DISPNAME_CONNECTED_TO.get(), MonitorMessages.INFO_REPLICA_DESC_CONNECTED_TO.get(), this.replicationServerAddress + ':' + this.replicationServerPort);
        }
        if (this.useSSL != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ssl-encryption", MonitorMessages.INFO_REPLICA_DISPNAME_USE_SSL.get(), MonitorMessages.INFO_REPLICA_DESC_USE_SSL.get(), this.useSSL);
        }
        if (this.lostConnections != null) {
            MonitorEntry.addMonitorAttribute(attrs, "lost-connections", MonitorMessages.INFO_REPLICA_DISPNAME_LOST_CONNECTIONS.get(), MonitorMessages.INFO_REPLICA_DESC_LOST_CONNECTIONS.get(), this.lostConnections);
        }
        if (this.receivedUpdates != null) {
            MonitorEntry.addMonitorAttribute(attrs, "received-updates", MonitorMessages.INFO_REPLICA_DISPNAME_RECEIVED_UPDATES.get(), MonitorMessages.INFO_REPLICA_DESC_RECEIVED_UPDATES.get(), this.receivedUpdates);
        }
        if (this.sentUpdates != null) {
            MonitorEntry.addMonitorAttribute(attrs, "sent-updates", MonitorMessages.INFO_REPLICA_DISPNAME_SENT_UPDATES.get(), MonitorMessages.INFO_REPLICA_DESC_SENT_UPDATES.get(), this.sentUpdates);
        }
        if (this.pendingUpdates != null) {
            MonitorEntry.addMonitorAttribute(attrs, "pending-updates", MonitorMessages.INFO_REPLICA_DISPNAME_PENDING_UPDATES.get(), MonitorMessages.INFO_REPLICA_DESC_PENDING_UPDATES.get(), this.pendingUpdates);
        }
        if (this.totalReplayed != null) {
            MonitorEntry.addMonitorAttribute(attrs, "replayed-updates", MonitorMessages.INFO_REPLICA_DISPNAME_TOTAL_REPLAYED.get(), MonitorMessages.INFO_REPLICA_DESC_TOTAL_REPLAYED.get(), this.totalReplayed);
        }
        if (this.successfullyReplayed != null) {
            MonitorEntry.addMonitorAttribute(attrs, "replayed-updates-ok", MonitorMessages.INFO_REPLICA_DISPNAME_SUCCESSFUL_REPLAYED.get(), MonitorMessages.INFO_REPLICA_DESC_SUCCESSFUL_REPLAYED.get(), this.successfullyReplayed);
        }
        if (this.replayedAfterModifyConflict != null) {
            MonitorEntry.addMonitorAttribute(attrs, "resolved-modify-conflicts", MonitorMessages.INFO_REPLICA_DISPNAME_RESOLVED_MODIFY_CONFLICTS.get(), MonitorMessages.INFO_REPLICA_DESC_RESOLVED_MODIFY_CONFLICTS.get(), this.replayedAfterModifyConflict);
        }
        if (this.replayedAfterNamingConflict != null) {
            MonitorEntry.addMonitorAttribute(attrs, "resolved-naming-conflicts", MonitorMessages.INFO_REPLICA_DISPNAME_RESOLVED_NAMING_CONFLICTS.get(), MonitorMessages.INFO_REPLICA_DESC_RESOLVED_NAMING_CONFLICTS.get(), this.replayedAfterNamingConflict);
        }
        if (this.unresolvedNamingConflicts != null) {
            MonitorEntry.addMonitorAttribute(attrs, "unresolved-naming-conflicts", MonitorMessages.INFO_REPLICA_DISPNAME_UNRESOLVED_NAMING_CONFLICTS.get(), MonitorMessages.INFO_REPLICA_DESC_UNRESOLVED_NAMING_CONFLICTS.get(), this.unresolvedNamingConflicts);
        }
        if (this.currentReceiveWindowSize != null) {
            MonitorEntry.addMonitorAttribute(attrs, "current-rcv-window", MonitorMessages.INFO_REPLICA_DISPNAME_CURRENT_RECEIVE_WINDOW_SIZE.get(), MonitorMessages.INFO_REPLICA_DESC_CURRENT_RECEIVE_WINDOW_SIZE.get(), this.currentReceiveWindowSize);
        }
        if (this.currentSendWindowSize != null) {
            MonitorEntry.addMonitorAttribute(attrs, "current-send-window", MonitorMessages.INFO_REPLICA_DISPNAME_CURRENT_SEND_WINDOW_SIZE.get(), MonitorMessages.INFO_REPLICA_DESC_CURRENT_SEND_WINDOW_SIZE.get(), this.currentSendWindowSize);
        }
        if (this.maxReceiveWindowSize != null) {
            MonitorEntry.addMonitorAttribute(attrs, "max-rcv-window", MonitorMessages.INFO_REPLICA_DISPNAME_MAX_RECEIVE_WINDOW_SIZE.get(), MonitorMessages.INFO_REPLICA_DESC_MAX_RECEIVE_WINDOW_SIZE.get(), this.maxReceiveWindowSize);
        }
        if (this.maxSendWindowSize != null) {
            MonitorEntry.addMonitorAttribute(attrs, "max-send-window", MonitorMessages.INFO_REPLICA_DISPNAME_MAX_SEND_WINDOW_SIZE.get(), MonitorMessages.INFO_REPLICA_DESC_MAX_SEND_WINDOW_SIZE.get(), this.maxSendWindowSize);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
