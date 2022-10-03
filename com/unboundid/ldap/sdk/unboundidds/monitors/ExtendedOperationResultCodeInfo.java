package com.unboundid.ldap.sdk.unboundidds.monitors;

import com.unboundid.ldap.sdk.OperationType;
import java.util.Iterator;
import com.unboundid.ldap.sdk.Entry;
import java.util.Collections;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.Attribute;
import java.util.TreeMap;
import java.util.Map;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ExtendedOperationResultCodeInfo implements Serializable
{
    private static final long serialVersionUID = 2412562905271298484L;
    private final Double failedPercent;
    private final Long failedCount;
    private final Long totalCount;
    private final Map<String, Double> failedPercentsByOID;
    private final Map<String, Long> failedCountsByOID;
    private final Map<String, Long> totalCountsByOID;
    private final Map<String, Map<Integer, ResultCodeInfo>> resultCodeInfoMap;
    private final Map<String, String> requestNamesByOID;
    
    ExtendedOperationResultCodeInfo(final MonitorEntry entry) {
        this.totalCount = entry.getLong("extended-op-total-count");
        this.failedCount = entry.getLong("extended-op-failed-count");
        this.failedPercent = entry.getDouble("extended-op-failed-percent");
        final TreeMap<String, String> names = new TreeMap<String, String>();
        final TreeMap<String, Long> totalCounts = new TreeMap<String, Long>();
        final TreeMap<String, Long> failedCounts = new TreeMap<String, Long>();
        final TreeMap<String, Double> failedPercents = new TreeMap<String, Double>();
        final TreeMap<String, Map<Integer, ResultCodeInfo>> rcMaps = new TreeMap<String, Map<Integer, ResultCodeInfo>>();
        final Entry e = entry.getEntry();
        for (final Attribute a : e.getAttributes()) {
            try {
                final String lowerName = StaticUtils.toLowerCase(a.getName());
                if (!lowerName.startsWith("extended-op-") || !lowerName.endsWith("-total-count")) {
                    continue;
                }
                final String dashedOID = lowerName.substring(12, lowerName.length() - 12);
                final String dottedOID = dashedOID.replace('-', '.');
                final String name = entry.getString("extended-op-" + dashedOID + "-name");
                final long total = a.getValueAsLong();
                final long failed = entry.getLong("extended-op-" + dashedOID + "-failed-count");
                final double failedPct = entry.getDouble("extended-op-" + dashedOID + "-failed-percent");
                names.put(dottedOID, name);
                totalCounts.put(dottedOID, total);
                failedCounts.put(dottedOID, failed);
                failedPercents.put(dottedOID, failedPct);
                rcMaps.put(dottedOID, getRCMap(e, "extended-op-" + dashedOID + "-result-"));
            }
            catch (final Exception ex) {
                Debug.debugException(ex);
            }
        }
        this.requestNamesByOID = Collections.unmodifiableMap((Map<? extends String, ? extends String>)names);
        this.totalCountsByOID = Collections.unmodifiableMap((Map<? extends String, ? extends Long>)totalCounts);
        this.failedCountsByOID = Collections.unmodifiableMap((Map<? extends String, ? extends Long>)failedCounts);
        this.failedPercentsByOID = Collections.unmodifiableMap((Map<? extends String, ? extends Double>)failedPercents);
        this.resultCodeInfoMap = Collections.unmodifiableMap((Map<? extends String, ? extends Map<Integer, ResultCodeInfo>>)rcMaps);
    }
    
    private static Map<Integer, ResultCodeInfo> getRCMap(final Entry entry, final String prefix) {
        final TreeMap<Integer, ResultCodeInfo> m = new TreeMap<Integer, ResultCodeInfo>();
        for (final Attribute a : entry.getAttributes()) {
            try {
                final String lowerName = StaticUtils.toLowerCase(a.getName());
                if (!lowerName.startsWith(prefix) || !lowerName.endsWith("-name")) {
                    continue;
                }
                final int intValue = Integer.parseInt(lowerName.substring(prefix.length(), lowerName.length() - 5));
                final String name = a.getValue();
                final long count = entry.getAttributeValueAsLong(prefix + intValue + "-count");
                final double percent = Double.parseDouble(entry.getAttributeValue(prefix + intValue + "-percent"));
                final double totalResponseTimeMillis = Double.parseDouble(entry.getAttributeValue(prefix + intValue + "-total-response-time-millis"));
                final double averageResponseTimeMillis = Double.parseDouble(entry.getAttributeValue(prefix + intValue + "-average-response-time-millis"));
                m.put(intValue, new ResultCodeInfo(intValue, name, OperationType.EXTENDED, count, percent, totalResponseTimeMillis, averageResponseTimeMillis));
            }
            catch (final Exception ex) {
                Debug.debugException(ex);
            }
        }
        return Collections.unmodifiableMap((Map<? extends Integer, ? extends ResultCodeInfo>)m);
    }
    
    public Long getTotalCount() {
        return this.totalCount;
    }
    
    public Map<String, Long> getTotalCountsByOID() {
        return this.totalCountsByOID;
    }
    
    public Long getFailedCount() {
        return this.failedCount;
    }
    
    public Map<String, Long> getFailedCountsByOID() {
        return this.failedCountsByOID;
    }
    
    public Double getFailedPercent() {
        return this.failedPercent;
    }
    
    public Map<String, Double> getFailedPercentsByOID() {
        return this.failedPercentsByOID;
    }
    
    public Map<String, Map<Integer, ResultCodeInfo>> getResultCodeInfoMap() {
        return this.resultCodeInfoMap;
    }
    
    public Map<String, String> getExtendedRequestNamesByOID() {
        return this.requestNamesByOID;
    }
}
