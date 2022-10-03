package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.Iterator;
import com.unboundid.ldap.sdk.Entry;
import java.util.Collections;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.Attribute;
import java.util.TreeMap;
import com.unboundid.ldap.sdk.OperationType;
import java.util.Map;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class OperationResultCodeInfo implements Serializable
{
    private static final long serialVersionUID = 4688688688915878084L;
    private final Double failedPercent;
    private final Long failedCount;
    private final Long totalCount;
    private final Map<Integer, ResultCodeInfo> resultCodeInfoMap;
    private final OperationType operationType;
    
    OperationResultCodeInfo(final MonitorEntry entry, final OperationType operationType, final String opTypeAttrPrefix) {
        this.operationType = operationType;
        this.totalCount = entry.getLong(opTypeAttrPrefix + "total-count");
        this.failedCount = entry.getLong(opTypeAttrPrefix + "failed-count");
        this.failedPercent = entry.getDouble(opTypeAttrPrefix + "failed-percent");
        final String rcPrefix = opTypeAttrPrefix + "result-";
        final TreeMap<Integer, ResultCodeInfo> rcMap = new TreeMap<Integer, ResultCodeInfo>();
        final Entry e = entry.getEntry();
        for (final Attribute a : e.getAttributes()) {
            try {
                final String lowerName = StaticUtils.toLowerCase(a.getName());
                if (!lowerName.startsWith(rcPrefix) || !lowerName.endsWith("-name")) {
                    continue;
                }
                final String name = a.getValue();
                final int intValue = Integer.parseInt(lowerName.substring(rcPrefix.length(), lowerName.length() - 5));
                final long count = entry.getLong(rcPrefix + intValue + "-count");
                final double percent = entry.getDouble(rcPrefix + intValue + "-percent");
                final double totalResponseTimeMillis = entry.getDouble(rcPrefix + intValue + "-total-response-time-millis");
                final double averageResponseTimeMillis = entry.getDouble(rcPrefix + intValue + "-average-response-time-millis");
                rcMap.put(intValue, new ResultCodeInfo(intValue, name, operationType, count, percent, totalResponseTimeMillis, averageResponseTimeMillis));
            }
            catch (final Exception ex) {
                Debug.debugException(ex);
            }
        }
        this.resultCodeInfoMap = Collections.unmodifiableMap((Map<? extends Integer, ? extends ResultCodeInfo>)rcMap);
    }
    
    public OperationType getOperationType() {
        return this.operationType;
    }
    
    public Long getTotalCount() {
        return this.totalCount;
    }
    
    public Long getFailedCount() {
        return this.failedCount;
    }
    
    public Double getFailedPercent() {
        return this.failedPercent;
    }
    
    public Map<Integer, ResultCodeInfo> getResultCodeInfoMap() {
        return this.resultCodeInfoMap;
    }
}
