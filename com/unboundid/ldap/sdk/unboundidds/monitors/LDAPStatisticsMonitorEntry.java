package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.Collections;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPStatisticsMonitorEntry extends MonitorEntry
{
    static final String LDAP_STATISTICS_MONITOR_OC = "ds-ldap-statistics-monitor-entry";
    private static final String ATTR_ABANDON_REQUESTS = "abandonRequests";
    private static final String ATTR_ADD_REQUESTS = "addRequests";
    private static final String ATTR_ADD_RESPONSES = "addResponses";
    private static final String ATTR_BIND_REQUESTS = "bindRequests";
    private static final String ATTR_BIND_RESPONSES = "bindResponses";
    private static final String ATTR_BYTES_READ = "bytesRead";
    private static final String ATTR_BYTES_WRITTEN = "bytesWritten";
    private static final String ATTR_COMPARE_REQUESTS = "compareRequests";
    private static final String ATTR_COMPARE_RESPONSES = "compareResponses";
    private static final String ATTR_CONNECTIONS_CLOSED = "connectionsClosed";
    private static final String ATTR_CONNECTIONS_ESTABLISHED = "connectionsEstablished";
    private static final String ATTR_DELETE_REQUESTS = "deleteRequests";
    private static final String ATTR_DELETE_RESPONSES = "deleteResponses";
    private static final String ATTR_EXTENDED_REQUESTS = "extendedRequests";
    private static final String ATTR_EXTENDED_RESPONSES = "extendedResponses";
    private static final String ATTR_LDAP_MESSAGES_READ = "ldapMessagesRead";
    private static final String ATTR_LDAP_MESSAGES_WRITTEN = "ldapMessagesWritten";
    private static final String ATTR_MODIFY_REQUESTS = "modifyRequests";
    private static final String ATTR_MODIFY_RESPONSES = "modifyResponses";
    private static final String ATTR_MODIFY_DN_REQUESTS = "modifyDNRequests";
    private static final String ATTR_MODIFY_DN_RESPONSES = "modifyDNResponses";
    private static final String ATTR_OPS_ABANDONED = "operationsAbandoned";
    private static final String ATTR_OPS_COMPLETED = "operationsCompleted";
    private static final String ATTR_OPS_INITIATED = "operationsInitiated";
    private static final String ATTR_SEARCH_REQUESTS = "searchRequests";
    private static final String ATTR_SEARCH_RESULT_DONE_RESPONSES = "searchResultsDone";
    private static final String ATTR_SEARCH_RESULT_ENTRY_RESPONSES = "searchResultEntries";
    private static final String ATTR_SEARCH_RESULT_REFERENCE_RESPONSES = "searchResultReferences";
    private static final String ATTR_UNBIND_REQUESTS = "unbindRequests";
    private static final long serialVersionUID = 4869341619766489249L;
    private final Long abandonRequests;
    private final Long addRequests;
    private final Long addResponses;
    private final Long bindRequests;
    private final Long bindResponses;
    private final Long bytesRead;
    private final Long bytesWritten;
    private final Long compareRequests;
    private final Long compareResponses;
    private final Long connectionsClosed;
    private final Long connectionsEstablished;
    private final Long deleteRequests;
    private final Long deleteResponses;
    private final Long extendedRequests;
    private final Long extendedResponses;
    private final Long ldapMessagesRead;
    private final Long ldapMessagesWritten;
    private final Long modifyRequests;
    private final Long modifyResponses;
    private final Long modifyDNRequests;
    private final Long modifyDNResponses;
    private final Long opsAbandoned;
    private final Long opsCompleted;
    private final Long opsInitiated;
    private final Long searchRequests;
    private final Long searchDoneResponses;
    private final Long searchEntryResponses;
    private final Long searchReferenceResponses;
    private final Long unbindRequests;
    
    public LDAPStatisticsMonitorEntry(final Entry entry) {
        super(entry);
        this.abandonRequests = this.getLong("abandonRequests");
        this.addRequests = this.getLong("addRequests");
        this.addResponses = this.getLong("addResponses");
        this.bindRequests = this.getLong("bindRequests");
        this.bindResponses = this.getLong("bindResponses");
        this.bytesRead = this.getLong("bytesRead");
        this.bytesWritten = this.getLong("bytesWritten");
        this.compareRequests = this.getLong("compareRequests");
        this.compareResponses = this.getLong("compareResponses");
        this.connectionsClosed = this.getLong("connectionsClosed");
        this.connectionsEstablished = this.getLong("connectionsEstablished");
        this.deleteRequests = this.getLong("deleteRequests");
        this.deleteResponses = this.getLong("deleteResponses");
        this.extendedRequests = this.getLong("extendedRequests");
        this.extendedResponses = this.getLong("extendedResponses");
        this.ldapMessagesRead = this.getLong("ldapMessagesRead");
        this.ldapMessagesWritten = this.getLong("ldapMessagesWritten");
        this.modifyRequests = this.getLong("modifyRequests");
        this.modifyResponses = this.getLong("modifyResponses");
        this.modifyDNRequests = this.getLong("modifyDNRequests");
        this.modifyDNResponses = this.getLong("modifyDNResponses");
        this.opsAbandoned = this.getLong("operationsAbandoned");
        this.opsCompleted = this.getLong("operationsCompleted");
        this.opsInitiated = this.getLong("operationsInitiated");
        this.searchRequests = this.getLong("searchRequests");
        this.searchDoneResponses = this.getLong("searchResultsDone");
        this.searchEntryResponses = this.getLong("searchResultEntries");
        this.searchReferenceResponses = this.getLong("searchResultReferences");
        this.unbindRequests = this.getLong("unbindRequests");
    }
    
    public Long getConnectionsEstablished() {
        return this.connectionsEstablished;
    }
    
    public Long getConnectionsClosed() {
        return this.connectionsClosed;
    }
    
    public Long getOperationsInitiated() {
        return this.opsInitiated;
    }
    
    public Long getOperationsCompleted() {
        return this.opsCompleted;
    }
    
    public Long getOperationsAbandoned() {
        return this.opsAbandoned;
    }
    
    public Long getBytesRead() {
        return this.bytesRead;
    }
    
    public Long getBytesWritten() {
        return this.bytesWritten;
    }
    
    public Long getLDAPMessagesRead() {
        return this.ldapMessagesRead;
    }
    
    public Long getLDAPMessagesWritten() {
        return this.ldapMessagesWritten;
    }
    
    public Long getAbandonRequests() {
        return this.abandonRequests;
    }
    
    public Long getAddRequests() {
        return this.addRequests;
    }
    
    public Long getAddResponses() {
        return this.addResponses;
    }
    
    public Long getBindRequests() {
        return this.bindRequests;
    }
    
    public Long getBindResponses() {
        return this.bindResponses;
    }
    
    public Long getCompareRequests() {
        return this.compareRequests;
    }
    
    public Long getCompareResponses() {
        return this.compareResponses;
    }
    
    public Long getDeleteRequests() {
        return this.deleteRequests;
    }
    
    public Long getDeleteResponses() {
        return this.deleteResponses;
    }
    
    public Long getExtendedRequests() {
        return this.extendedRequests;
    }
    
    public Long getExtendedResponses() {
        return this.extendedResponses;
    }
    
    public Long getModifyRequests() {
        return this.modifyRequests;
    }
    
    public Long getModifyResponses() {
        return this.modifyResponses;
    }
    
    public Long getModifyDNRequests() {
        return this.modifyDNRequests;
    }
    
    public Long getModifyDNResponses() {
        return this.modifyDNResponses;
    }
    
    public Long getSearchRequests() {
        return this.searchRequests;
    }
    
    public Long getSearchResultEntries() {
        return this.searchEntryResponses;
    }
    
    public Long getSearchResultReferences() {
        return this.searchReferenceResponses;
    }
    
    public Long getSearchDoneResponses() {
        return this.searchDoneResponses;
    }
    
    public Long getUnbindRequests() {
        return this.unbindRequests;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_LDAP_STATS_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_LDAP_STATS_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(50));
        if (this.connectionsEstablished != null) {
            MonitorEntry.addMonitorAttribute(attrs, "connectionsEstablished", MonitorMessages.INFO_LDAP_STATS_DISPNAME_CONNECTIONS_ESTABLISHED.get(), MonitorMessages.INFO_LDAP_STATS_DESC_CONNECTIONS_ESTABLISHED.get(), this.connectionsEstablished);
        }
        if (this.connectionsClosed != null) {
            MonitorEntry.addMonitorAttribute(attrs, "connectionsClosed", MonitorMessages.INFO_LDAP_STATS_DISPNAME_CONNECTIONS_CLOSED.get(), MonitorMessages.INFO_LDAP_STATS_DESC_CONNECTIONS_CLOSED.get(), this.connectionsClosed);
        }
        if (this.bytesRead != null) {
            MonitorEntry.addMonitorAttribute(attrs, "bytesRead", MonitorMessages.INFO_LDAP_STATS_DISPNAME_BYTES_READ.get(), MonitorMessages.INFO_LDAP_STATS_DESC_BYTES_READ.get(), this.bytesRead);
        }
        if (this.bytesWritten != null) {
            MonitorEntry.addMonitorAttribute(attrs, "bytesWritten", MonitorMessages.INFO_LDAP_STATS_DISPNAME_BYTES_WRITTEN.get(), MonitorMessages.INFO_LDAP_STATS_DESC_BYTES_WRITTEN.get(), this.bytesWritten);
        }
        if (this.ldapMessagesRead != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ldapMessagesRead", MonitorMessages.INFO_LDAP_STATS_DISPNAME_LDAP_MESSAGES_READ.get(), MonitorMessages.INFO_LDAP_STATS_DESC_LDAP_MESSAGES_READ.get(), this.ldapMessagesRead);
        }
        if (this.ldapMessagesWritten != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ldapMessagesWritten", MonitorMessages.INFO_LDAP_STATS_DISPNAME_LDAP_MESSAGES_WRITTEN.get(), MonitorMessages.INFO_LDAP_STATS_DESC_LDAP_MESSAGES_WRITTEN.get(), this.ldapMessagesWritten);
        }
        if (this.opsInitiated != null) {
            MonitorEntry.addMonitorAttribute(attrs, "operationsInitiated", MonitorMessages.INFO_LDAP_STATS_DISPNAME_OPS_INITIATED.get(), MonitorMessages.INFO_LDAP_STATS_DESC_OPS_INITIATED.get(), this.opsInitiated);
        }
        if (this.opsCompleted != null) {
            MonitorEntry.addMonitorAttribute(attrs, "operationsCompleted", MonitorMessages.INFO_LDAP_STATS_DISPNAME_OPS_COMPLETED.get(), MonitorMessages.INFO_LDAP_STATS_DESC_OPS_COMPLETED.get(), this.opsCompleted);
        }
        if (this.opsAbandoned != null) {
            MonitorEntry.addMonitorAttribute(attrs, "operationsAbandoned", MonitorMessages.INFO_LDAP_STATS_DISPNAME_OPS_ABANDONED.get(), MonitorMessages.INFO_LDAP_STATS_DESC_OPS_ABANDONED.get(), this.opsAbandoned);
        }
        if (this.abandonRequests != null) {
            MonitorEntry.addMonitorAttribute(attrs, "abandonRequests", MonitorMessages.INFO_LDAP_STATS_DISPNAME_ABANDON_REQUESTS.get(), MonitorMessages.INFO_LDAP_STATS_DESC_ABANDON_REQUESTS.get(), this.abandonRequests);
        }
        if (this.addRequests != null) {
            MonitorEntry.addMonitorAttribute(attrs, "addRequests", MonitorMessages.INFO_LDAP_STATS_DISPNAME_ADD_REQUESTS.get(), MonitorMessages.INFO_LDAP_STATS_DESC_ADD_REQUESTS.get(), this.addRequests);
        }
        if (this.addResponses != null) {
            MonitorEntry.addMonitorAttribute(attrs, "addResponses", MonitorMessages.INFO_LDAP_STATS_DISPNAME_ADD_RESPONSES.get(), MonitorMessages.INFO_LDAP_STATS_DESC_ADD_RESPONSES.get(), this.addResponses);
        }
        if (this.bindRequests != null) {
            MonitorEntry.addMonitorAttribute(attrs, "bindRequests", MonitorMessages.INFO_LDAP_STATS_DISPNAME_BIND_REQUESTS.get(), MonitorMessages.INFO_LDAP_STATS_DESC_BIND_REQUESTS.get(), this.bindRequests);
        }
        if (this.bindResponses != null) {
            MonitorEntry.addMonitorAttribute(attrs, "bindResponses", MonitorMessages.INFO_LDAP_STATS_DISPNAME_BIND_RESPONSES.get(), MonitorMessages.INFO_LDAP_STATS_DESC_BIND_RESPONSES.get(), this.bindResponses);
        }
        if (this.compareRequests != null) {
            MonitorEntry.addMonitorAttribute(attrs, "compareRequests", MonitorMessages.INFO_LDAP_STATS_DISPNAME_COMPARE_REQUESTS.get(), MonitorMessages.INFO_LDAP_STATS_DESC_COMPARE_REQUESTS.get(), this.compareRequests);
        }
        if (this.compareResponses != null) {
            MonitorEntry.addMonitorAttribute(attrs, "compareResponses", MonitorMessages.INFO_LDAP_STATS_DISPNAME_COMPARE_RESPONSES.get(), MonitorMessages.INFO_LDAP_STATS_DESC_COMPARE_RESPONSES.get(), this.compareResponses);
        }
        if (this.deleteRequests != null) {
            MonitorEntry.addMonitorAttribute(attrs, "deleteRequests", MonitorMessages.INFO_LDAP_STATS_DISPNAME_DELETE_REQUESTS.get(), MonitorMessages.INFO_LDAP_STATS_DESC_DELETE_REQUESTS.get(), this.deleteRequests);
        }
        if (this.deleteResponses != null) {
            MonitorEntry.addMonitorAttribute(attrs, "deleteResponses", MonitorMessages.INFO_LDAP_STATS_DISPNAME_DELETE_RESPONSES.get(), MonitorMessages.INFO_LDAP_STATS_DESC_DELETE_RESPONSES.get(), this.deleteResponses);
        }
        if (this.extendedRequests != null) {
            MonitorEntry.addMonitorAttribute(attrs, "extendedRequests", MonitorMessages.INFO_LDAP_STATS_DISPNAME_EXTENDED_REQUESTS.get(), MonitorMessages.INFO_LDAP_STATS_DESC_EXTENDED_REQUESTS.get(), this.extendedRequests);
        }
        if (this.extendedResponses != null) {
            MonitorEntry.addMonitorAttribute(attrs, "extendedResponses", MonitorMessages.INFO_LDAP_STATS_DISPNAME_EXTENDED_RESPONSES.get(), MonitorMessages.INFO_LDAP_STATS_DESC_EXTENDED_RESPONSES.get(), this.extendedResponses);
        }
        if (this.modifyRequests != null) {
            MonitorEntry.addMonitorAttribute(attrs, "modifyRequests", MonitorMessages.INFO_LDAP_STATS_DISPNAME_MODIFY_REQUESTS.get(), MonitorMessages.INFO_LDAP_STATS_DESC_MODIFY_REQUESTS.get(), this.modifyRequests);
        }
        if (this.modifyResponses != null) {
            MonitorEntry.addMonitorAttribute(attrs, "modifyResponses", MonitorMessages.INFO_LDAP_STATS_DISPNAME_MODIFY_RESPONSES.get(), MonitorMessages.INFO_LDAP_STATS_DESC_MODIFY_RESPONSES.get(), this.modifyResponses);
        }
        if (this.modifyDNRequests != null) {
            MonitorEntry.addMonitorAttribute(attrs, "modifyDNRequests", MonitorMessages.INFO_LDAP_STATS_DISPNAME_MODIFY_DN_REQUESTS.get(), MonitorMessages.INFO_LDAP_STATS_DESC_MODIFY_DN_REQUESTS.get(), this.modifyDNRequests);
        }
        if (this.modifyDNResponses != null) {
            MonitorEntry.addMonitorAttribute(attrs, "modifyDNResponses", MonitorMessages.INFO_LDAP_STATS_DISPNAME_MODIFY_DN_RESPONSES.get(), MonitorMessages.INFO_LDAP_STATS_DESC_MODIFY_DN_RESPONSES.get(), this.modifyDNResponses);
        }
        if (this.searchRequests != null) {
            MonitorEntry.addMonitorAttribute(attrs, "searchRequests", MonitorMessages.INFO_LDAP_STATS_DISPNAME_SEARCH_REQUESTS.get(), MonitorMessages.INFO_LDAP_STATS_DESC_SEARCH_REQUESTS.get(), this.searchRequests);
        }
        if (this.searchEntryResponses != null) {
            MonitorEntry.addMonitorAttribute(attrs, "searchResultEntries", MonitorMessages.INFO_LDAP_STATS_DISPNAME_SEARCH_ENTRY_RESPONSES.get(), MonitorMessages.INFO_LDAP_STATS_DESC_SEARCH_ENTRY_RESPONSES.get(), this.searchEntryResponses);
        }
        if (this.searchReferenceResponses != null) {
            MonitorEntry.addMonitorAttribute(attrs, "searchResultReferences", MonitorMessages.INFO_LDAP_STATS_DISPNAME_SEARCH_REFERENCE_RESPONSES.get(), MonitorMessages.INFO_LDAP_STATS_DESC_SEARCH_REFERENCE_RESPONSES.get(), this.searchReferenceResponses);
        }
        if (this.searchDoneResponses != null) {
            MonitorEntry.addMonitorAttribute(attrs, "searchResultsDone", MonitorMessages.INFO_LDAP_STATS_DISPNAME_SEARCH_DONE_RESPONSES.get(), MonitorMessages.INFO_LDAP_STATS_DESC_SEARCH_DONE_RESPONSES.get(), this.searchDoneResponses);
        }
        if (this.unbindRequests != null) {
            MonitorEntry.addMonitorAttribute(attrs, "unbindRequests", MonitorMessages.INFO_LDAP_STATS_DISPNAME_UNBIND_REQUESTS.get(), MonitorMessages.INFO_LDAP_STATS_DESC_UNBIND_REQUESTS.get(), this.unbindRequests);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
