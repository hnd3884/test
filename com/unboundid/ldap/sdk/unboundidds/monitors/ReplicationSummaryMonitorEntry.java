package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Entry;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ReplicationSummaryMonitorEntry extends MonitorEntry
{
    static final String REPLICATION_SUMMARY_MONITOR_OC = "ds-replication-server-summary-monitor-entry";
    private static final String ATTR_BASE_DN = "base-dn";
    private static final String ATTR_REPLICATION_SERVER = "replication-server";
    private static final String ATTR_REPLICA = "replica";
    private static final long serialVersionUID = 3144471025744197014L;
    private final String baseDN;
    private final List<ReplicationSummaryReplica> replicas;
    private final List<ReplicationSummaryReplicationServer> replicationServers;
    
    public ReplicationSummaryMonitorEntry(final Entry entry) {
        super(entry);
        this.baseDN = this.getString("base-dn");
        final List<String> replicaStrings = this.getStrings("replica");
        final ArrayList<ReplicationSummaryReplica> replList = new ArrayList<ReplicationSummaryReplica>(replicaStrings.size());
        for (final String s : replicaStrings) {
            replList.add(new ReplicationSummaryReplica(s));
        }
        this.replicas = Collections.unmodifiableList((List<? extends ReplicationSummaryReplica>)replList);
        final List<String> serverStrings = this.getStrings("replication-server");
        final ArrayList<ReplicationSummaryReplicationServer> serverList = new ArrayList<ReplicationSummaryReplicationServer>(serverStrings.size());
        for (final String s2 : serverStrings) {
            serverList.add(new ReplicationSummaryReplicationServer(s2));
        }
        this.replicationServers = Collections.unmodifiableList((List<? extends ReplicationSummaryReplicationServer>)serverList);
    }
    
    public String getBaseDN() {
        return this.baseDN;
    }
    
    public List<ReplicationSummaryReplica> getReplicas() {
        return this.replicas;
    }
    
    public List<ReplicationSummaryReplicationServer> getReplicationServers() {
        return this.replicationServers;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_REPLICATION_SUMMARY_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_REPLICATION_SUMMARY_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(10));
        if (this.baseDN != null) {
            MonitorEntry.addMonitorAttribute(attrs, "base-dn", MonitorMessages.INFO_REPLICATION_SUMMARY_DISPNAME_BASE_DN.get(), MonitorMessages.INFO_REPLICATION_SUMMARY_DESC_BASE_DN.get(), this.baseDN);
        }
        if (!this.replicas.isEmpty()) {
            final ArrayList<String> replStrings = new ArrayList<String>(this.replicas.size());
            for (final ReplicationSummaryReplica r : this.replicas) {
                replStrings.add(r.toString());
            }
            MonitorEntry.addMonitorAttribute(attrs, "replica", MonitorMessages.INFO_REPLICATION_SUMMARY_DISPNAME_REPLICA.get(), MonitorMessages.INFO_REPLICATION_SUMMARY_DESC_REPLICA.get(), replStrings);
        }
        if (!this.replicationServers.isEmpty()) {
            final ArrayList<String> serverStrings = new ArrayList<String>(this.replicationServers.size());
            for (final ReplicationSummaryReplicationServer s : this.replicationServers) {
                serverStrings.add(s.toString());
            }
            MonitorEntry.addMonitorAttribute(attrs, "replication-server", MonitorMessages.INFO_REPLICATION_SUMMARY_DISPNAME_REPLICATION_SERVER.get(), MonitorMessages.INFO_REPLICATION_SUMMARY_DESC_REPLICATION_SERVER.get(), serverStrings);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
