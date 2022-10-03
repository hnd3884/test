package com.zoho.security.wafad;

import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.iam.security.SecurityUtil;
import java.util.HashMap;
import com.zoho.security.attackdiscovery.AttackDiscovery;
import java.util.Map;
import java.util.logging.Logger;
import com.zoho.security.attackdiscovery.AttackDiscoveryMetric;

public class WAFAttackDiscoveryMetricRecorder implements AttackDiscoveryMetric
{
    private static final Logger LOGGER;
    private static final ThreadLocal<Map<String, WAFAttackDiscoveryStats>> WAF_ATTACK_DISCOVERY_METRIC_THREAD_LOCAL;
    public static AttackDiscoveryMetric wafAttackDiscoveryMetric;
    private static boolean recordOnWAFAgent;
    
    public static void enableForWAFAgent() {
        WAFAttackDiscoveryMetricRecorder.recordOnWAFAgent = true;
        enable();
    }
    
    public static void enable() {
        WAFAttackDiscoveryMetricRecorder.wafAttackDiscoveryMetric = (AttackDiscoveryMetric)new WAFAttackDiscoveryMetricRecorder();
    }
    
    public static void disable() {
        WAFAttackDiscoveryMetricRecorder.wafAttackDiscoveryMetric = null;
    }
    
    public static boolean isEnabled() {
        return WAFAttackDiscoveryMetricRecorder.wafAttackDiscoveryMetric != null;
    }
    
    public void record(final Class<? extends AttackDiscovery> attackDiscoveryClass, final String monitoringClass, final String monitoringMethod, final long executionTime, final boolean isDiscovered) {
        if (WAFAttackDiscoveryMetricRecorder.recordOnWAFAgent && isCurrentRequestNull()) {
            return;
        }
        final String wafAttackDiscoveryStatsKey = attackDiscoveryClass.getName() + "::" + monitoringClass + "::" + monitoringMethod;
        Map<String, WAFAttackDiscoveryStats> wafAttackDiscoveryStats = WAFAttackDiscoveryMetricRecorder.WAF_ATTACK_DISCOVERY_METRIC_THREAD_LOCAL.get();
        if (wafAttackDiscoveryStats == null) {
            WAFAttackDiscoveryMetricRecorder.WAF_ATTACK_DISCOVERY_METRIC_THREAD_LOCAL.set(wafAttackDiscoveryStats = new HashMap<String, WAFAttackDiscoveryStats>());
        }
        WAFAttackDiscoveryStats stats = wafAttackDiscoveryStats.get(wafAttackDiscoveryStatsKey);
        if (stats == null) {
            wafAttackDiscoveryStats.put(wafAttackDiscoveryStatsKey, stats = new WAFAttackDiscoveryStats());
        }
        stats.add(executionTime, isDiscovered);
    }
    
    private static boolean isCurrentRequestNull() {
        return SecurityUtil.getCurrentRequest() == null;
    }
    
    public static void logMetrics() {
        if (WAFAttackDiscoveryMetricRecorder.wafAttackDiscoveryMetric == null) {
            return;
        }
        final Map<String, WAFAttackDiscoveryStats> wafAttackDiscoveryStats = WAFAttackDiscoveryMetricRecorder.WAF_ATTACK_DISCOVERY_METRIC_THREAD_LOCAL.get();
        if (wafAttackDiscoveryStats == null) {
            return;
        }
        WAFAttackDiscoveryMetricRecorder.WAF_ATTACK_DISCOVERY_METRIC_THREAD_LOCAL.remove();
        if (wafAttackDiscoveryStats.isEmpty()) {
            return;
        }
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<String, WAFAttackDiscoveryStats> entry : wafAttackDiscoveryStats.entrySet()) {
            if (sb.length() == 0) {
                sb.append("WAF Attack Discovery Stats ::: ");
            }
            else {
                sb.append(", ");
            }
            sb.append(entry.getKey());
            sb.append(" -> ");
            sb.append(entry.getValue().toString());
        }
        WAFAttackDiscoveryMetricRecorder.LOGGER.log(Level.SEVERE, sb.toString());
    }
    
    static {
        LOGGER = Logger.getLogger(WAFAttackDiscoveryMetricRecorder.class.getName());
        WAF_ATTACK_DISCOVERY_METRIC_THREAD_LOCAL = new ThreadLocal<Map<String, WAFAttackDiscoveryStats>>();
    }
    
    private class WAFAttackDiscoveryStats
    {
        long totalExecutionTime;
        long totalAccessCount;
        long totalDiscoveredCount;
        
        void add(final long executionTime, final boolean isDiscovered) {
            if (isDiscovered) {
                ++this.totalDiscoveredCount;
            }
            this.totalExecutionTime += executionTime;
            ++this.totalAccessCount;
        }
        
        @Override
        public String toString() {
            return "Total Execution Time: " + this.totalExecutionTime + " ms Total Access Count: " + this.totalAccessCount + " Total Discovered Count: " + this.totalDiscoveredCount;
        }
    }
}
