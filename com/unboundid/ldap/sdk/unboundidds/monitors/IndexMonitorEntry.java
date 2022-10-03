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
public final class IndexMonitorEntry extends MonitorEntry
{
    static final String INDEX_MONITOR_OC = "ds-index-monitor-entry";
    private static final String ATTR_INDEX_NAME = "ds-index-name";
    private static final String ATTR_BACKEND_ID = "ds-index-backend-id";
    private static final String ATTR_BASE_DN = "ds-index-backend-base-dn";
    private static final String ATTR_INDEX_ATTR = "ds-index-attribute-type";
    private static final String ATTR_INDEX_TYPE = "ds-index-type";
    private static final String ATTR_INDEX_FILTER = "ds-index-filter";
    private static final String ATTR_INDEX_TRUSTED = "ds-index-trusted";
    private static final String ATTR_ENTRY_LIMIT = "ds-index-entry-limit";
    private static final String ATTR_EXCEEDED_COUNT = "ds-index-exceeded-entry-limit-count-since-db-open";
    private static final String ATTR_SEARCH_KEYS_NEAR_LIMIT = "ds-index-unique-keys-near-entry-limit-accessed-by-search-since-db-open";
    private static final String ATTR_SEARCH_KEYS_OVER_LIMIT = "ds-index-unique-keys-exceeding-entry-limit-accessed-by-search-since-db-open";
    private static final String ATTR_WRITE_KEYS_NEAR_LIMIT = "ds-index-unique-keys-near-entry-limit-accessed-by-write-since-db-open";
    private static final String ATTR_WRITE_KEYS_OVER_LIMIT = "ds-index-unique-keys-exceeding-entry-limit-accessed-by-write-since-db-open";
    private static final String ATTR_MAINTAIN_COUNT = "ds-index-maintain-count";
    private static final String ATTR_FULLY_PRIMED = "ds-index-fully-primed-at-backend-open";
    private static final String ATTR_PRIME_INCOMPLETE_REASON = "ds-index-prime-incomplete-reason";
    private static final String ATTR_PRIME_EXCEPTION = "ds-index-prime-exception";
    private static final String ATTR_PRIMED_KEYS = "ds-index-num-primed-keys-at-backend-open";
    private static final String ATTR_WRITE_COUNT = "ds-index-write-count-since-db-open";
    private static final String ATTR_DELETE_COUNT = "ds-index-remove-count-since-db-open";
    private static final String ATTR_READ_COUNT = "ds-index-read-count-since-db-open";
    private static final String ATTR_READ_FOR_SEARCH_COUNT = "ds-index-read-for-search-count-since-db-open";
    private static final String ATTR_CURSOR_COUNT = "ds-index-open-cursor-count-since-db-open";
    private static final long serialVersionUID = 9182830448328951893L;
    private final Boolean fullyPrimed;
    private final Boolean indexTrusted;
    private final Boolean maintainCount;
    private final Long entryLimit;
    private final Long exceededCount;
    private final Long numCursors;
    private final Long numDeletes;
    private final Long numReads;
    private final Long numReadsForSearch;
    private final Long numWrites;
    private final Long primedKeys;
    private final Long searchKeysNearLimit;
    private final Long searchKeysOverLimit;
    private final Long writeKeysNearLimit;
    private final Long writeKeysOverLimit;
    private final String attributeType;
    private final String backendID;
    private final String baseDN;
    private final String indexFilter;
    private final String indexName;
    private final String indexType;
    private final String primeException;
    private final String primeIncompleteReason;
    
    public IndexMonitorEntry(final Entry entry) {
        super(entry);
        this.fullyPrimed = this.getBoolean("ds-index-fully-primed-at-backend-open");
        this.indexTrusted = this.getBoolean("ds-index-trusted");
        this.maintainCount = this.getBoolean("ds-index-maintain-count");
        this.entryLimit = this.getLong("ds-index-entry-limit");
        this.exceededCount = this.getLong("ds-index-exceeded-entry-limit-count-since-db-open");
        this.numCursors = this.getLong("ds-index-open-cursor-count-since-db-open");
        this.numDeletes = this.getLong("ds-index-remove-count-since-db-open");
        this.numReads = this.getLong("ds-index-read-count-since-db-open");
        this.numReadsForSearch = this.getLong("ds-index-read-for-search-count-since-db-open");
        this.numWrites = this.getLong("ds-index-write-count-since-db-open");
        this.primedKeys = this.getLong("ds-index-num-primed-keys-at-backend-open");
        this.searchKeysNearLimit = this.getLong("ds-index-unique-keys-near-entry-limit-accessed-by-search-since-db-open");
        this.searchKeysOverLimit = this.getLong("ds-index-unique-keys-exceeding-entry-limit-accessed-by-search-since-db-open");
        this.writeKeysNearLimit = this.getLong("ds-index-unique-keys-near-entry-limit-accessed-by-write-since-db-open");
        this.writeKeysOverLimit = this.getLong("ds-index-unique-keys-exceeding-entry-limit-accessed-by-write-since-db-open");
        this.attributeType = this.getString("ds-index-attribute-type");
        this.backendID = this.getString("ds-index-backend-id");
        this.baseDN = this.getString("ds-index-backend-base-dn");
        this.indexFilter = this.getString("ds-index-filter");
        this.indexName = this.getString("ds-index-name");
        this.indexType = this.getString("ds-index-type");
        this.primeException = this.getString("ds-index-prime-exception");
        this.primeIncompleteReason = this.getString("ds-index-prime-incomplete-reason");
    }
    
    public String getIndexName() {
        return this.indexName;
    }
    
    public String getBackendID() {
        return this.backendID;
    }
    
    public String getBaseDN() {
        return this.baseDN;
    }
    
    public String getAttributeType() {
        return this.attributeType;
    }
    
    public String getAttributeIndexType() {
        return this.indexType;
    }
    
    public String getIndexFilter() {
        return this.indexFilter;
    }
    
    public Boolean isIndexTrusted() {
        return this.indexTrusted;
    }
    
    public Long getIndexEntryLimit() {
        return this.entryLimit;
    }
    
    public Long getEntryLimitExceededCountSinceComingOnline() {
        return this.exceededCount;
    }
    
    public Long getUniqueKeysNearEntryLimitAccessedBySearchSinceComingOnline() {
        return this.searchKeysNearLimit;
    }
    
    public Long getUniqueKeysOverEntryLimitAccessedBySearchSinceComingOnline() {
        return this.searchKeysOverLimit;
    }
    
    public Long getUniqueKeysNearEntryLimitAccessedByWriteSinceComingOnline() {
        return this.writeKeysNearLimit;
    }
    
    public Long getUniqueKeysOverEntryLimitAccessedByWriteSinceComingOnline() {
        return this.writeKeysOverLimit;
    }
    
    public Boolean maintainCountForExceededKeys() {
        return this.maintainCount;
    }
    
    public Boolean fullyPrimedWhenBroughtOnline() {
        return this.fullyPrimed;
    }
    
    public String getPrimeIncompleteReason() {
        return this.primeIncompleteReason;
    }
    
    public String getPrimeException() {
        return this.primeException;
    }
    
    public Long getKeysPrimedWhenBroughtOnline() {
        return this.primedKeys;
    }
    
    public Long getKeysWrittenSinceComingOnline() {
        return this.numWrites;
    }
    
    public Long getKeysDeletedSinceComingOnline() {
        return this.numDeletes;
    }
    
    public Long getKeysReadSinceComingOnline() {
        return this.numReads;
    }
    
    public Long getFilterInitiatedReadsSinceComingOnline() {
        return this.numReadsForSearch;
    }
    
    public Long getCursorsCreatedSinceComingOnline() {
        return this.numCursors;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_INDEX_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_INDEX_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(19));
        if (this.indexName != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-name", MonitorMessages.INFO_INDEX_DISPNAME_INDEX_NAME.get(), MonitorMessages.INFO_INDEX_DESC_INDEX_NAME.get(), this.indexName);
        }
        if (this.backendID != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-backend-id", MonitorMessages.INFO_INDEX_DISPNAME_BACKEND_ID.get(), MonitorMessages.INFO_INDEX_DESC_BACKEND_ID.get(), this.backendID);
        }
        if (this.baseDN != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-backend-base-dn", MonitorMessages.INFO_INDEX_DISPNAME_BASE_DN.get(), MonitorMessages.INFO_INDEX_DESC_BASE_DN.get(), this.baseDN);
        }
        if (this.attributeType != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-attribute-type", MonitorMessages.INFO_INDEX_DISPNAME_ATTR_TYPE.get(), MonitorMessages.INFO_INDEX_DESC_ATTR_TYPE.get(), this.attributeType);
        }
        if (this.indexType != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-type", MonitorMessages.INFO_INDEX_DISPNAME_INDEX_TYPE.get(), MonitorMessages.INFO_INDEX_DESC_INDEX_TYPE.get(), this.indexType);
        }
        if (this.indexFilter != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-filter", MonitorMessages.INFO_INDEX_DISPNAME_FILTER.get(), MonitorMessages.INFO_INDEX_DESC_FILTER.get(), this.indexFilter);
        }
        if (this.indexTrusted != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-trusted", MonitorMessages.INFO_INDEX_DISPNAME_TRUSTED.get(), MonitorMessages.INFO_INDEX_DESC_TRUSTED.get(), this.indexTrusted);
        }
        if (this.entryLimit != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-entry-limit", MonitorMessages.INFO_INDEX_DISPNAME_ENTRY_LIMIT.get(), MonitorMessages.INFO_INDEX_DESC_ENTRY_LIMIT.get(), this.entryLimit);
        }
        if (this.exceededCount != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-exceeded-entry-limit-count-since-db-open", MonitorMessages.INFO_INDEX_DISPNAME_EXCEEDED_COUNT.get(), MonitorMessages.INFO_INDEX_DESC_EXCEEDED_COUNT.get(), this.exceededCount);
        }
        if (this.searchKeysNearLimit != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-unique-keys-near-entry-limit-accessed-by-search-since-db-open", MonitorMessages.INFO_INDEX_DISPNAME_SEARCH_KEYS_NEAR_LIMIT.get(), MonitorMessages.INFO_INDEX_DESC_SEARCH_KEYS_NEAR_LIMIT.get(), this.searchKeysNearLimit);
        }
        if (this.searchKeysOverLimit != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-unique-keys-exceeding-entry-limit-accessed-by-search-since-db-open", MonitorMessages.INFO_INDEX_DISPNAME_SEARCH_KEYS_OVER_LIMIT.get(), MonitorMessages.INFO_INDEX_DESC_SEARCH_KEYS_OVER_LIMIT.get(), this.searchKeysOverLimit);
        }
        if (this.writeKeysNearLimit != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-unique-keys-near-entry-limit-accessed-by-write-since-db-open", MonitorMessages.INFO_INDEX_DISPNAME_WRITE_KEYS_NEAR_LIMIT.get(), MonitorMessages.INFO_INDEX_DESC_WRITE_KEYS_NEAR_LIMIT.get(), this.writeKeysNearLimit);
        }
        if (this.writeKeysOverLimit != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-unique-keys-exceeding-entry-limit-accessed-by-write-since-db-open", MonitorMessages.INFO_INDEX_DISPNAME_WRITE_KEYS_OVER_LIMIT.get(), MonitorMessages.INFO_INDEX_DESC_WRITE_KEYS_OVER_LIMIT.get(), this.writeKeysOverLimit);
        }
        if (this.maintainCount != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-maintain-count", MonitorMessages.INFO_INDEX_DISPNAME_MAINTAIN_COUNT.get(), MonitorMessages.INFO_INDEX_DESC_MAINTAIN_COUNT.get(), this.maintainCount);
        }
        if (this.fullyPrimed != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-fully-primed-at-backend-open", MonitorMessages.INFO_INDEX_DISPNAME_FULLY_PRIMED.get(), MonitorMessages.INFO_INDEX_DESC_FULLY_PRIMED.get(), this.fullyPrimed);
        }
        if (this.primeIncompleteReason != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-prime-incomplete-reason", MonitorMessages.INFO_INDEX_DISPNAME_PRIME_INCOMPLETE_REASON.get(), MonitorMessages.INFO_INDEX_DESC_PRIME_INCOMPLETE_REASON.get(), this.primeIncompleteReason);
        }
        if (this.primeException != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-prime-exception", MonitorMessages.INFO_INDEX_DISPNAME_PRIME_EXCEPTION.get(), MonitorMessages.INFO_INDEX_DESC_PRIME_EXCEPTION.get(), this.primeException);
        }
        if (this.primedKeys != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-num-primed-keys-at-backend-open", MonitorMessages.INFO_INDEX_DISPNAME_PRIMED_KEYS.get(), MonitorMessages.INFO_INDEX_DESC_PRIMED_KEYS.get(), this.primedKeys);
        }
        if (this.numWrites != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-write-count-since-db-open", MonitorMessages.INFO_INDEX_DISPNAME_WRITE_COUNT.get(), MonitorMessages.INFO_INDEX_DESC_WRITE_COUNT.get(), this.numWrites);
        }
        if (this.numDeletes != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-remove-count-since-db-open", MonitorMessages.INFO_INDEX_DISPNAME_DELETE_COUNT.get(), MonitorMessages.INFO_INDEX_DESC_DELETE_COUNT.get(), this.numDeletes);
        }
        if (this.numReads != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-read-count-since-db-open", MonitorMessages.INFO_INDEX_DISPNAME_READ_COUNT.get(), MonitorMessages.INFO_INDEX_DESC_READ_COUNT.get(), this.numReads);
        }
        if (this.numReadsForSearch != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-read-for-search-count-since-db-open", MonitorMessages.INFO_INDEX_DISPNAME_FILTER_INITIATED_READ_COUNT.get(), MonitorMessages.INFO_INDEX_DESC_FILTER_INITIATED_READ_COUNT.get(), this.numReadsForSearch);
        }
        if (this.numCursors != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-index-open-cursor-count-since-db-open", MonitorMessages.INFO_INDEX_DISPNAME_CURSOR_COUNT.get(), MonitorMessages.INFO_INDEX_DESC_CURSOR_COUNT.get(), this.numCursors);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
