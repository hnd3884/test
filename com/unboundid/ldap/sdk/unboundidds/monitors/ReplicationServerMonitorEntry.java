package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import com.unboundid.util.Debug;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.DN;
import java.util.Map;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ReplicationServerMonitorEntry extends MonitorEntry
{
    static final String REPLICATION_SERVER_MONITOR_OC = "ds-replication-server-monitor-entry";
    private static final String ATTR_BASE_DN = "base-dn";
    private static final String ATTR_BASE_DN_GENERATION_ID = "base-dn-generation-id";
    private static final String ATTR_REPLICATION_SERVER_ID = "replication-server-id";
    private static final String ATTR_REPLICATION_SERVER_PORT = "replication-server-port";
    private static final String ATTR_SSL_AVAILABLE = "ssl-encryption-available";
    private static final long serialVersionUID = 7488640967498574690L;
    private final Boolean sslEncryptionAvailable;
    private final List<String> baseDNs;
    private final Long replicationServerPort;
    private final Map<DN, String> generationIDs;
    private final String replicationServerID;
    
    public ReplicationServerMonitorEntry(final Entry entry) {
        super(entry);
        this.baseDNs = this.getStrings("base-dn");
        this.replicationServerID = this.getString("replication-server-id");
        this.replicationServerPort = this.getLong("replication-server-port");
        this.sslEncryptionAvailable = this.getBoolean("ssl-encryption-available");
        final List<String> baseDNsAndIDs = this.getStrings("base-dn-generation-id");
        final Map<DN, String> idMap = new LinkedHashMap<DN, String>(StaticUtils.computeMapCapacity(baseDNsAndIDs.size()));
        for (final String s : baseDNsAndIDs) {
            try {
                final int lastSpacePos = s.lastIndexOf(32);
                final DN dn = new DN(s.substring(0, lastSpacePos));
                idMap.put(dn, s.substring(lastSpacePos + 1));
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        this.generationIDs = Collections.unmodifiableMap((Map<? extends DN, ? extends String>)idMap);
    }
    
    public List<String> getBaseDNs() {
        return this.baseDNs;
    }
    
    public Map<DN, String> getGenerationIDs() {
        return this.generationIDs;
    }
    
    public String getGenerationID(final String baseDN) {
        try {
            return this.getGenerationID(new DN(baseDN));
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return null;
        }
    }
    
    public String getGenerationID(final DN baseDN) {
        return this.generationIDs.get(baseDN);
    }
    
    public String getReplicationServerID() {
        return this.replicationServerID;
    }
    
    public Long getReplicationServerPort() {
        return this.replicationServerPort;
    }
    
    public Boolean sslEncryptionAvailable() {
        return this.sslEncryptionAvailable;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_REPLICATION_SERVER_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_REPLICATION_SERVER_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(10));
        if (!this.baseDNs.isEmpty()) {
            MonitorEntry.addMonitorAttribute(attrs, "base-dn", MonitorMessages.INFO_REPLICATION_SERVER_DISPNAME_BASE_DN.get(), MonitorMessages.INFO_REPLICATION_SERVER_DESC_BASE_DN.get(), this.baseDNs);
        }
        if (!this.generationIDs.isEmpty()) {
            final ArrayList<String> idStrings = new ArrayList<String>(this.generationIDs.size());
            for (final Map.Entry<DN, String> e : this.generationIDs.entrySet()) {
                idStrings.add(e.getKey().toNormalizedString() + ' ' + e.getValue());
            }
            MonitorEntry.addMonitorAttribute(attrs, "base-dn-generation-id", MonitorMessages.INFO_REPLICATION_SERVER_DISPNAME_BASE_DN_GENERATION_ID.get(), MonitorMessages.INFO_REPLICATION_SERVER_DESC_BASE_DN_GENERATION_ID.get(), idStrings);
        }
        if (this.replicationServerID != null) {
            MonitorEntry.addMonitorAttribute(attrs, "replication-server-id", MonitorMessages.INFO_REPLICATION_SERVER_DISPNAME_REPLICATION_SERVER_ID.get(), MonitorMessages.INFO_REPLICATION_SERVER_DESC_REPLICATION_SERVER_ID.get(), this.replicationServerID);
        }
        if (this.replicationServerPort != null) {
            MonitorEntry.addMonitorAttribute(attrs, "replication-server-port", MonitorMessages.INFO_REPLICATION_SERVER_DISPNAME_REPLICATION_SERVER_PORT.get(), MonitorMessages.INFO_REPLICATION_SERVER_DESC_REPLICATION_SERVER_PORT.get(), this.replicationServerPort);
        }
        if (this.sslEncryptionAvailable != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ssl-encryption-available", MonitorMessages.INFO_REPLICATION_SERVER_DISPNAME_SSL_AVAILABLE.get(), MonitorMessages.INFO_REPLICATION_SERVER_DESC_SSL_AVAILABLE.get(), this.sslEncryptionAvailable);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
