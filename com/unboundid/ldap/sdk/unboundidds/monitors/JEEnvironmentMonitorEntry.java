package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import com.unboundid.ldap.sdk.Attribute;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.Entry;
import java.util.Map;
import java.util.Date;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class JEEnvironmentMonitorEntry extends MonitorEntry
{
    static final String JE_ENVIRONMENT_MONITOR_OC = "ds-je-environment-monitor-entry";
    private static final String ATTR_ACTIVE_TXNS = "active-transaction-count";
    private static final String ATTR_AVERAGE_CHECKPOINT_DURATION_MILLIS = "average-checkpoint-duration-millis";
    private static final String ATTR_BACKEND_ID = "backend-id";
    private static final String ATTR_CACHE_PCT_FULL = "db-cache-percent-full";
    private static final String ATTR_CHECKPOINT_IN_PROGRESS = "checkpoint-in-progress";
    private static final String ATTR_CLEANER_BACKLOG = "cleaner-backlog";
    private static final String ATTR_CURRENT_CACHE_SIZE = "current-db-cache-size";
    private static final String ATTR_DB_DIRECTORY = "db-directory";
    private static final String ATTR_DB_ON_DISK_SIZE = "db-on-disk-size";
    private static final String ATTR_JE_VERSION = "je-version";
    private static final String ATTR_LAST_CHECKPOINT_DURATION_MILLIS = "last-checkpoint-duration-millis";
    private static final String ATTR_LAST_CHECKPOINT_START_TIME = "last-checkpoint-start-time";
    private static final String ATTR_LAST_CHECKPOINT_STOP_TIME = "last-checkpoint-stop-time";
    @Deprecated
    private static final String ATTR_LAST_CHECKPOINT_TIME = "last-checkpoint-time";
    private static final String ATTR_MAX_CACHE_SIZE = "max-db-cache-size";
    private static final String ATTR_MILLIS_SINCE_LAST_CHECKPOINT = "millis-since-last-checkpoint";
    private static final String ATTR_NODES_EVICTED = "nodes-evicted";
    private static final String ATTR_NUM_CHECKPOINTS = "num-checkpoints";
    private static final String ATTR_NUM_READ_LOCKS = "read-locks-held";
    private static final String ATTR_TOTAL_CHECKPOINT_DURATION_MILLIS = "total-checkpoint-duration-millis";
    private static final String ATTR_NUM_WAITING_TXNS = "transactions-waiting-on-locks";
    private static final String ATTR_NUM_WRITE_LOCKS = "write-locks-held";
    private static final String ATTR_RANDOM_READS = "random-read-count";
    private static final String ATTR_RANDOM_WRITES = "random-write-count";
    private static final String ATTR_SEQUENTIAL_READS = "sequential-read-count";
    private static final String ATTR_SEQUENTIAL_WRITES = "sequential-write-count";
    private static final String ATTR_PREFIX_ENV_STAT = "je-env-stat-";
    private static final String ATTR_PREFIX_LOCK_STAT = "je-lock-stat-";
    private static final String ATTR_PREFIX_TXN_STAT = "je-txn-stat-";
    private static final String PROPERTY_ENV_STATS = "je-env-stats";
    private static final String PROPERTY_LOCK_STATS = "je-lock-stats";
    private static final String PROPERTY_TXN_STATS = "je-txn-stats";
    private static final long serialVersionUID = 2557783119454069632L;
    private final Boolean checkpointInProgress;
    private final Date lastCheckpointStartTime;
    private final Date lastCheckpointStopTime;
    @Deprecated
    private final Date lastCheckpointTime;
    private final Long activeTransactionCount;
    private final Long averageCheckpointDurationMillis;
    private final Long cleanerBacklog;
    private final Long currentDBCacheSize;
    private final Long dbCachePercentFull;
    private final Long dbOnDiskSize;
    private final Long lastCheckpointDurationMillis;
    private final Long maxDBCacheSize;
    private final Long millisSinceLastCheckpoint;
    private final Long nodesEvicted;
    private final Long numCheckpoints;
    private final Long randomReads;
    private final Long randomWrites;
    private final Long readLocksHeld;
    private final Long sequentialReads;
    private final Long sequentialWrites;
    private final Long totalCheckpointDurationMillis;
    private final Long transactionsWaitingOnLocks;
    private final Long writeLocksHeld;
    private final Map<String, String> envStats;
    private final Map<String, String> lockStats;
    private final Map<String, String> txnStats;
    private final String backendID;
    private final String dbDirectory;
    private final String jeVersion;
    
    public JEEnvironmentMonitorEntry(final Entry entry) {
        super(entry);
        this.activeTransactionCount = this.getLong("active-transaction-count");
        this.cleanerBacklog = this.getLong("cleaner-backlog");
        this.currentDBCacheSize = this.getLong("current-db-cache-size");
        this.dbCachePercentFull = this.getLong("db-cache-percent-full");
        this.dbOnDiskSize = this.getLong("db-on-disk-size");
        this.maxDBCacheSize = this.getLong("max-db-cache-size");
        this.nodesEvicted = this.getLong("nodes-evicted");
        this.randomReads = this.getLong("random-read-count");
        this.randomWrites = this.getLong("random-write-count");
        this.readLocksHeld = this.getLong("read-locks-held");
        this.sequentialReads = this.getLong("sequential-read-count");
        this.sequentialWrites = this.getLong("sequential-write-count");
        this.transactionsWaitingOnLocks = this.getLong("transactions-waiting-on-locks");
        this.writeLocksHeld = this.getLong("write-locks-held");
        this.backendID = this.getString("backend-id");
        this.dbDirectory = this.getString("db-directory");
        this.jeVersion = this.getString("je-version");
        this.checkpointInProgress = this.getBoolean("checkpoint-in-progress");
        this.lastCheckpointStartTime = this.getDate("last-checkpoint-start-time");
        this.lastCheckpointStopTime = this.getDate("last-checkpoint-stop-time");
        this.lastCheckpointTime = this.getDate("last-checkpoint-time");
        this.averageCheckpointDurationMillis = this.getLong("average-checkpoint-duration-millis");
        this.lastCheckpointDurationMillis = this.getLong("last-checkpoint-duration-millis");
        this.millisSinceLastCheckpoint = this.getLong("millis-since-last-checkpoint");
        this.numCheckpoints = this.getLong("num-checkpoints");
        this.totalCheckpointDurationMillis = this.getLong("total-checkpoint-duration-millis");
        final LinkedHashMap<String, String> tmpEnvStats = new LinkedHashMap<String, String>(StaticUtils.computeMapCapacity(20));
        final LinkedHashMap<String, String> tmpLockStats = new LinkedHashMap<String, String>(StaticUtils.computeMapCapacity(20));
        final LinkedHashMap<String, String> tmpTxnStats = new LinkedHashMap<String, String>(StaticUtils.computeMapCapacity(20));
        for (final Attribute a : entry.getAttributes()) {
            final String name = StaticUtils.toLowerCase(a.getName());
            if (name.startsWith("je-env-stat-")) {
                tmpEnvStats.put(StaticUtils.toLowerCase(name.substring("je-env-stat-".length())), a.getValue());
            }
            else if (name.startsWith("je-lock-stat-")) {
                tmpLockStats.put(StaticUtils.toLowerCase(name.substring("je-lock-stat-".length())), a.getValue());
            }
            else {
                if (!name.startsWith("je-txn-stat-")) {
                    continue;
                }
                tmpTxnStats.put(StaticUtils.toLowerCase(name.substring("je-txn-stat-".length())), a.getValue());
            }
        }
        this.envStats = Collections.unmodifiableMap((Map<? extends String, ? extends String>)tmpEnvStats);
        this.lockStats = Collections.unmodifiableMap((Map<? extends String, ? extends String>)tmpLockStats);
        this.txnStats = Collections.unmodifiableMap((Map<? extends String, ? extends String>)tmpTxnStats);
    }
    
    public String getBackendID() {
        return this.backendID;
    }
    
    public String getJEVersion() {
        return this.jeVersion;
    }
    
    public String getDBDirectory() {
        return this.dbDirectory;
    }
    
    public Long getDBOnDiskSize() {
        return this.dbOnDiskSize;
    }
    
    public Long getCurrentDBCacheSize() {
        return this.currentDBCacheSize;
    }
    
    public Long getMaxDBCacheSize() {
        return this.maxDBCacheSize;
    }
    
    public Long getDBCachePercentFull() {
        return this.dbCachePercentFull;
    }
    
    public Boolean checkpointInProgress() {
        return this.checkpointInProgress;
    }
    
    public Long getNumCheckpoints() {
        return this.numCheckpoints;
    }
    
    public Long getTotalCheckpointDurationMillis() {
        return this.totalCheckpointDurationMillis;
    }
    
    public Long getAverageCheckpointDurationMillis() {
        return this.averageCheckpointDurationMillis;
    }
    
    public Long getLastCheckpointDurationMillis() {
        return this.lastCheckpointDurationMillis;
    }
    
    public Date getLastCheckpointStartTime() {
        return this.lastCheckpointStartTime;
    }
    
    public Date getLastCheckpointStopTime() {
        return this.lastCheckpointStopTime;
    }
    
    @Deprecated
    public Date getLastCheckpointTime() {
        return this.lastCheckpointTime;
    }
    
    public Long getMillisSinceLastCheckpoint() {
        return this.millisSinceLastCheckpoint;
    }
    
    public Long getCleanerBacklog() {
        return this.cleanerBacklog;
    }
    
    public Long getNodesEvicted() {
        return this.nodesEvicted;
    }
    
    public Long getRandomReads() {
        return this.randomReads;
    }
    
    public Long getRandomWrites() {
        return this.randomWrites;
    }
    
    public Long getSequentialReads() {
        return this.sequentialReads;
    }
    
    public Long getSequentialWrites() {
        return this.sequentialWrites;
    }
    
    public Long getActiveTransactionCount() {
        return this.activeTransactionCount;
    }
    
    public Long getReadLocksHeld() {
        return this.readLocksHeld;
    }
    
    public Long getWriteLocksHeld() {
        return this.writeLocksHeld;
    }
    
    public Long getTransactionsWaitingOnLocks() {
        return this.transactionsWaitingOnLocks;
    }
    
    public Map<String, String> getEnvironmentStats() {
        return this.envStats;
    }
    
    public String getEnvironmentStat(final String statName) {
        return this.envStats.get(StaticUtils.toLowerCase(statName));
    }
    
    public Map<String, String> getLockStats() {
        return this.lockStats;
    }
    
    public String getLockStat(final String statName) {
        return this.lockStats.get(StaticUtils.toLowerCase(statName));
    }
    
    public Map<String, String> getTransactionStats() {
        return this.txnStats;
    }
    
    public String getTransactionStat(final String statName) {
        return this.txnStats.get(StaticUtils.toLowerCase(statName));
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_JE_ENVIRONMENT_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_JE_ENVIRONMENT_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(20));
        if (this.backendID != null) {
            MonitorEntry.addMonitorAttribute(attrs, "backend-id", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_BACKEND_ID.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_BACKEND_ID.get(), this.backendID);
        }
        if (this.jeVersion != null) {
            MonitorEntry.addMonitorAttribute(attrs, "je-version", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_JE_VERSION.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_JE_VERSION.get(), this.jeVersion);
        }
        if (this.dbDirectory != null) {
            MonitorEntry.addMonitorAttribute(attrs, "db-directory", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_DB_DIRECTORY.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_DB_DIRECTORY.get(), this.dbDirectory);
        }
        if (this.dbOnDiskSize != null) {
            MonitorEntry.addMonitorAttribute(attrs, "db-on-disk-size", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_DB_ON_DISK_SIZE.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_DB_ON_DISK_SIZE.get(), this.dbOnDiskSize);
        }
        if (this.currentDBCacheSize != null) {
            MonitorEntry.addMonitorAttribute(attrs, "current-db-cache-size", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_CURRENT_CACHE_SIZE.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_CURRENT_CACHE_SIZE.get(), this.currentDBCacheSize);
        }
        if (this.maxDBCacheSize != null) {
            MonitorEntry.addMonitorAttribute(attrs, "max-db-cache-size", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_MAX_CACHE_SIZE.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_MAX_CACHE_SIZE.get(), this.maxDBCacheSize);
        }
        if (this.dbCachePercentFull != null) {
            MonitorEntry.addMonitorAttribute(attrs, "db-cache-percent-full", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_CACHE_PCT_FULL.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_CACHE_PCT_FULL.get(), this.dbCachePercentFull);
        }
        if (this.checkpointInProgress != null) {
            MonitorEntry.addMonitorAttribute(attrs, "checkpoint-in-progress", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_CP_IN_PROGRESS.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_CP_IN_PROGRESS.get(), this.checkpointInProgress);
        }
        if (this.numCheckpoints != null) {
            MonitorEntry.addMonitorAttribute(attrs, "num-checkpoints", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_NUM_CP.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_NUM_CP.get(), this.numCheckpoints);
        }
        if (this.totalCheckpointDurationMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "total-checkpoint-duration-millis", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_TOTAL_CP_DURATION.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_TOTAL_CP_DURATION.get(), this.totalCheckpointDurationMillis);
        }
        if (this.averageCheckpointDurationMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "average-checkpoint-duration-millis", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_AVG_CP_DURATION.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_AVG_CP_DURATION.get(), this.averageCheckpointDurationMillis);
        }
        if (this.lastCheckpointDurationMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-checkpoint-duration-millis", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_LAST_CP_DURATION.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_LAST_CP_DURATION.get(), this.lastCheckpointDurationMillis);
        }
        if (this.lastCheckpointStartTime != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-checkpoint-start-time", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_LAST_CP_START_TIME.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_LAST_CP_START_TIME.get(), this.lastCheckpointStartTime);
        }
        if (this.lastCheckpointStopTime != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-checkpoint-stop-time", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_LAST_CP_STOP_TIME.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_LAST_CP_STOP_TIME.get(), this.lastCheckpointStopTime);
        }
        if (this.millisSinceLastCheckpoint != null) {
            MonitorEntry.addMonitorAttribute(attrs, "millis-since-last-checkpoint", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_MILLIS_SINCE_CP.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_MILLIS_SINCE_CP.get(), this.millisSinceLastCheckpoint);
        }
        if (this.cleanerBacklog != null) {
            MonitorEntry.addMonitorAttribute(attrs, "cleaner-backlog", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_CLEANER_BACKLOG.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_CLEANER_BACKLOG.get(), this.cleanerBacklog);
        }
        if (this.nodesEvicted != null) {
            MonitorEntry.addMonitorAttribute(attrs, "nodes-evicted", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_NODES_EVICTED.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_NODES_EVICTED.get(), this.nodesEvicted);
        }
        if (this.randomReads != null) {
            MonitorEntry.addMonitorAttribute(attrs, "random-read-count", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_RANDOM_READS.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_RANDOM_READS.get(), this.randomReads);
        }
        if (this.randomWrites != null) {
            MonitorEntry.addMonitorAttribute(attrs, "random-write-count", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_RANDOM_WRITES.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_RANDOM_WRITES.get(), this.randomWrites);
        }
        if (this.sequentialReads != null) {
            MonitorEntry.addMonitorAttribute(attrs, "sequential-read-count", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_SEQUENTIAL_READS.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_SEQUENTIAL_READS.get(), this.sequentialReads);
        }
        if (this.sequentialWrites != null) {
            MonitorEntry.addMonitorAttribute(attrs, "sequential-write-count", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_SEQUENTIAL_WRITES.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_SEQUENTIAL_WRITES.get(), this.sequentialWrites);
        }
        if (this.activeTransactionCount != null) {
            MonitorEntry.addMonitorAttribute(attrs, "active-transaction-count", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_ACTIVE_TXNS.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_ACTIVE_TXNS.get(), this.activeTransactionCount);
        }
        if (this.readLocksHeld != null) {
            MonitorEntry.addMonitorAttribute(attrs, "read-locks-held", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_READ_LOCKS.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_READ_LOCKS.get(), this.readLocksHeld);
        }
        if (this.writeLocksHeld != null) {
            MonitorEntry.addMonitorAttribute(attrs, "write-locks-held", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_WRITE_LOCKS.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_WRITE_LOCKS.get(), this.writeLocksHeld);
        }
        if (this.transactionsWaitingOnLocks != null) {
            MonitorEntry.addMonitorAttribute(attrs, "transactions-waiting-on-locks", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_TXNS_WAITING_ON_LOCKS.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_TXNS_WAITING_ON_LOCKS.get(), this.transactionsWaitingOnLocks);
        }
        if (!this.envStats.isEmpty()) {
            final ArrayList<String> values = new ArrayList<String>(this.envStats.size());
            for (final Map.Entry<String, String> e : this.envStats.entrySet()) {
                values.add(e.getKey() + '=' + e.getValue());
            }
            MonitorEntry.addMonitorAttribute(attrs, "je-env-stats", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_ENV_STATS.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_ENV_STATS.get(), values);
        }
        if (!this.lockStats.isEmpty()) {
            final ArrayList<String> values = new ArrayList<String>(this.lockStats.size());
            for (final Map.Entry<String, String> e : this.lockStats.entrySet()) {
                values.add(e.getKey() + '=' + e.getValue());
            }
            MonitorEntry.addMonitorAttribute(attrs, "je-lock-stats", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_LOCK_STATS.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_LOCK_STATS.get(), values);
        }
        if (!this.txnStats.isEmpty()) {
            final ArrayList<String> values = new ArrayList<String>(this.txnStats.size());
            for (final Map.Entry<String, String> e : this.txnStats.entrySet()) {
                values.add(e.getKey() + '=' + e.getValue());
            }
            MonitorEntry.addMonitorAttribute(attrs, "je-txn-stats", MonitorMessages.INFO_JE_ENVIRONMENT_DISPNAME_TXN_STATS.get(), MonitorMessages.INFO_JE_ENVIRONMENT_DESC_TXN_STATS.get(), values);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
