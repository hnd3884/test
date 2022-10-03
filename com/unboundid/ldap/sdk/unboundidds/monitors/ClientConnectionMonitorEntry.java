package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.Collections;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import com.unboundid.ldap.sdk.Entry;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ClientConnectionMonitorEntry extends MonitorEntry
{
    static final String CLIENT_CONNECTION_MONITOR_OC = "ds-client-connection-monitor-entry";
    private static final String ATTR_CONNECTION = "connection";
    private static final long serialVersionUID = -1705824766273147598L;
    private final List<String> connections;
    
    public ClientConnectionMonitorEntry(final Entry entry) {
        super(entry);
        this.connections = this.getStrings("connection");
    }
    
    public List<String> getConnections() {
        return this.connections;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_CLIENT_CONNECTION_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_CLIENT_CONNECTION_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(1));
        if (!this.connections.isEmpty()) {
            MonitorEntry.addMonitorAttribute(attrs, "connection", MonitorMessages.INFO_CLIENT_CONNECTION_DISPNAME_CONNECTION.get(), MonitorMessages.INFO_CLIENT_CONNECTION_DESC_CONNECTION.get(), this.connections);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
