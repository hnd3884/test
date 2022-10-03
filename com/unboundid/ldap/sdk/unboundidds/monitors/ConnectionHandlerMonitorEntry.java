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
public final class ConnectionHandlerMonitorEntry extends MonitorEntry
{
    static final String CONNECTION_HANDLER_MONITOR_OC = "ds-connectionhandler-monitor-entry";
    private static final String ATTR_CONNECTION = "ds-connectionhandler-connection";
    private static final String ATTR_LISTENER = "ds-connectionhandler-listener";
    private static final String ATTR_NUM_CONNECTIONS = "ds-connectionhandler-num-connections";
    private static final String ATTR_PROTOCOL = "ds-connectionhandler-protocol";
    private static final long serialVersionUID = -2922139631867367609L;
    private final List<String> connections;
    private final List<String> listeners;
    private final Long numConnections;
    private final String protocol;
    
    public ConnectionHandlerMonitorEntry(final Entry entry) {
        super(entry);
        this.connections = this.getStrings("ds-connectionhandler-connection");
        this.listeners = this.getStrings("ds-connectionhandler-listener");
        this.numConnections = this.getLong("ds-connectionhandler-num-connections");
        this.protocol = this.getString("ds-connectionhandler-protocol");
    }
    
    public List<String> getConnections() {
        return this.connections;
    }
    
    public List<String> getListeners() {
        return this.listeners;
    }
    
    public Long getNumConnections() {
        return this.numConnections;
    }
    
    public String getProtocol() {
        return this.protocol;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_CONNECTION_HANDLER_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_CONNECTION_HANDLER_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(4));
        if (this.protocol != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-connectionhandler-protocol", MonitorMessages.INFO_CONNECTION_HANDLER_DISPNAME_PROTOCOL.get(), MonitorMessages.INFO_CONNECTION_HANDLER_DESC_PROTOCOL.get(), this.protocol);
        }
        if (!this.listeners.isEmpty()) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-connectionhandler-listener", MonitorMessages.INFO_CONNECTION_HANDLER_DISPNAME_LISTENER.get(), MonitorMessages.INFO_CONNECTION_HANDLER_DESC_LISTENER.get(), this.listeners);
        }
        if (this.numConnections != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-connectionhandler-num-connections", MonitorMessages.INFO_CONNECTION_HANDLER_DISPNAME_NUM_CONNECTIONS.get(), MonitorMessages.INFO_CONNECTION_HANDLER_DESC_NUM_CONNECTIONS.get(), this.numConnections);
        }
        if (!this.connections.isEmpty()) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-connectionhandler-connection", MonitorMessages.INFO_CONNECTION_HANDLER_DISPNAME_CONNECTION.get(), MonitorMessages.INFO_CONNECTION_HANDLER_DESC_CONNECTION.get(), this.connections);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
