package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.Iterator;
import com.unboundid.util.Debug;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Collections;
import com.unboundid.ldap.sdk.Entry;
import java.util.Map;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import com.unboundid.util.NotMutable;

@NotMutable
@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class ProcessingTimeHistogramMonitorEntry extends MonitorEntry
{
    static final String PROCESSING_TIME_HISTOGRAM_MONITOR_OC = "ds-processing-time-histogram-monitor-entry";
    private static final String ATTR_ADD_TOTAL_COUNT = "addOpsTotalCount";
    private static final String ATTR_ADD_AVERAGE_RESPONSE_TIME_MS = "addOpsAverageResponseTimeMillis";
    private static final String ATTR_ADD_AGGREGATE_PERCENT = "addOpsAggregatePercent";
    private static final String ATTR_ADD_COUNT = "addOpsCount";
    private static final String ATTR_ADD_PERCENT = "addOpsPercent";
    private static final String ATTR_ALL_TOTAL_COUNT = "allOpsTotalCount";
    private static final String ATTR_ALL_AVERAGE_RESPONSE_TIME_MS = "allOpsAverageResponseTimeMillis";
    private static final String ATTR_ALL_AGGREGATE_PERCENT = "allOpsAggregatePercent";
    private static final String ATTR_ALL_COUNT = "allOpsCount";
    private static final String ATTR_ALL_PERCENT = "allOpsPercent";
    private static final String ATTR_BIND_TOTAL_COUNT = "bindOpsTotalCount";
    private static final String ATTR_BIND_AVERAGE_RESPONSE_TIME_MS = "bindOpsAverageResponseTimeMillis";
    private static final String ATTR_BIND_AGGREGATE_PERCENT = "bindOpsAggregatePercent";
    private static final String ATTR_BIND_COUNT = "bindOpsCount";
    private static final String ATTR_BIND_PERCENT = "bindOpsPercent";
    private static final String ATTR_COMPARE_TOTAL_COUNT = "compareOpsTotalCount";
    private static final String ATTR_COMPARE_AVERAGE_RESPONSE_TIME_MS = "compareOpsAverageResponseTimeMillis";
    private static final String ATTR_COMPARE_AGGREGATE_PERCENT = "compareOpsAggregatePercent";
    private static final String ATTR_COMPARE_COUNT = "compareOpsCount";
    private static final String ATTR_COMPARE_PERCENT = "compareOpsPercent";
    private static final String ATTR_DELETE_TOTAL_COUNT = "deleteOpsTotalCount";
    private static final String ATTR_DELETE_AVERAGE_RESPONSE_TIME_MS = "deleteOpsAverageResponseTimeMillis";
    private static final String ATTR_DELETE_AGGREGATE_PERCENT = "deleteOpsAggregatePercent";
    private static final String ATTR_DELETE_COUNT = "deleteOpsCount";
    private static final String ATTR_DELETE_PERCENT = "deleteOpsPercent";
    private static final String ATTR_EXTENDED_TOTAL_COUNT = "extendedOpsTotalCount";
    private static final String ATTR_EXTENDED_AVERAGE_RESPONSE_TIME_MS = "extendedOpsAverageResponseTimeMillis";
    private static final String ATTR_EXTENDED_AGGREGATE_PERCENT = "extendedOpsAggregatePercent";
    private static final String ATTR_EXTENDED_COUNT = "extendedOpsCount";
    private static final String ATTR_EXTENDED_PERCENT = "extendedOpsPercent";
    private static final String ATTR_MODIFY_TOTAL_COUNT = "modifyOpsTotalCount";
    private static final String ATTR_MODIFY_AVERAGE_RESPONSE_TIME_MS = "modifyOpsAverageResponseTimeMillis";
    private static final String ATTR_MODIFY_AGGREGATE_PERCENT = "modifyOpsAggregatePercent";
    private static final String ATTR_MODIFY_COUNT = "modifyOpsCount";
    private static final String ATTR_MODIFY_PERCENT = "modifyOpsPercent";
    private static final String ATTR_MODIFY_DN_TOTAL_COUNT = "modifyDNOpsTotalCount";
    private static final String ATTR_MODIFY_DN_AVERAGE_RESPONSE_TIME_MS = "modifyDNOpsAverageResponseTimeMillis";
    private static final String ATTR_MODIFY_DN_AGGREGATE_PERCENT = "modifyDNOpsAggregatePercent";
    private static final String ATTR_MODIFY_DN_COUNT = "modifyDNOpsCount";
    private static final String ATTR_MODIFY_DN_PERCENT = "modifyDNOpsPercent";
    private static final String ATTR_SEARCH_TOTAL_COUNT = "searchOpsTotalCount";
    private static final String ATTR_SEARCH_AVERAGE_RESPONSE_TIME_MS = "searchOpsAverageResponseTimeMillis";
    private static final String ATTR_SEARCH_AGGREGATE_PERCENT = "searchOpsAggregatePercent";
    private static final String ATTR_SEARCH_COUNT = "searchOpsCount";
    private static final String ATTR_SEARCH_PERCENT = "searchOpsPercent";
    private static final long serialVersionUID = -2498009928344820276L;
    private final Map<Long, Double> addOpsPercent;
    private final Map<Long, Double> addOpsAggregatePercent;
    private final Map<Long, Double> allOpsPercent;
    private final Map<Long, Double> allOpsAggregatePercent;
    private final Map<Long, Double> bindOpsPercent;
    private final Map<Long, Double> bindOpsAggregatePercent;
    private final Map<Long, Double> compareOpsPercent;
    private final Map<Long, Double> compareOpsAggregatePercent;
    private final Map<Long, Double> deleteOpsPercent;
    private final Map<Long, Double> deleteOpsAggregatePercent;
    private final Map<Long, Double> extendedOpsPercent;
    private final Map<Long, Double> extendedOpsAggregatePercent;
    private final Map<Long, Double> modifyOpsPercent;
    private final Map<Long, Double> modifyOpsAggregatePercent;
    private final Map<Long, Double> modifyDNOpsPercent;
    private final Map<Long, Double> modifyDNOpsAggregatePercent;
    private final Map<Long, Double> searchOpsPercent;
    private final Map<Long, Double> searchOpsAggregatePercent;
    private final Map<Long, Long> addOpsCount;
    private final Map<Long, Long> allOpsCount;
    private final Map<Long, Long> bindOpsCount;
    private final Map<Long, Long> compareOpsCount;
    private final Map<Long, Long> deleteOpsCount;
    private final Map<Long, Long> extendedOpsCount;
    private final Map<Long, Long> modifyOpsCount;
    private final Map<Long, Long> modifyDNOpsCount;
    private final Map<Long, Long> searchOpsCount;
    private final Long addOpsTotalCount;
    private final Long allOpsTotalCount;
    private final Long bindOpsTotalCount;
    private final Long compareOpsTotalCount;
    private final Long deleteOpsTotalCount;
    private final Long extendedOpsTotalCount;
    private final Long modifyOpsTotalCount;
    private final Long modifyDNOpsTotalCount;
    private final Long searchOpsTotalCount;
    private final Double addOpsAvgResponseTimeMillis;
    private final Double allOpsAvgResponseTimeMillis;
    private final Double bindOpsAvgResponseTimeMillis;
    private final Double compareOpsAvgResponseTimeMillis;
    private final Double deleteOpsAvgResponseTimeMillis;
    private final Double extendedOpsAvgResponseTimeMillis;
    private final Double modifyOpsAvgResponseTimeMillis;
    private final Double modifyDNOpsAvgResponseTimeMillis;
    private final Double searchOpsAvgResponseTimeMillis;
    
    public ProcessingTimeHistogramMonitorEntry(final Entry entry) {
        super(entry);
        this.allOpsTotalCount = this.getLong("allOpsTotalCount");
        this.allOpsAvgResponseTimeMillis = this.getDouble("allOpsAverageResponseTimeMillis");
        this.allOpsCount = parseCountAttribute(entry, "allOpsCount");
        this.allOpsPercent = parsePercentAttribute(entry, "allOpsPercent");
        this.allOpsAggregatePercent = parsePercentAttribute(entry, "allOpsAggregatePercent");
        this.addOpsTotalCount = this.getLong("addOpsTotalCount");
        this.addOpsAvgResponseTimeMillis = this.getDouble("addOpsAverageResponseTimeMillis");
        this.addOpsCount = parseCountAttribute(entry, "addOpsCount");
        this.addOpsPercent = parsePercentAttribute(entry, "addOpsPercent");
        this.addOpsAggregatePercent = parsePercentAttribute(entry, "addOpsAggregatePercent");
        this.bindOpsTotalCount = this.getLong("bindOpsTotalCount");
        this.bindOpsAvgResponseTimeMillis = this.getDouble("bindOpsAverageResponseTimeMillis");
        this.bindOpsCount = parseCountAttribute(entry, "bindOpsCount");
        this.bindOpsPercent = parsePercentAttribute(entry, "bindOpsPercent");
        this.bindOpsAggregatePercent = parsePercentAttribute(entry, "bindOpsAggregatePercent");
        this.compareOpsTotalCount = this.getLong("compareOpsTotalCount");
        this.compareOpsAvgResponseTimeMillis = this.getDouble("compareOpsAverageResponseTimeMillis");
        this.compareOpsCount = parseCountAttribute(entry, "compareOpsCount");
        this.compareOpsPercent = parsePercentAttribute(entry, "compareOpsPercent");
        this.compareOpsAggregatePercent = parsePercentAttribute(entry, "compareOpsAggregatePercent");
        this.deleteOpsTotalCount = this.getLong("deleteOpsTotalCount");
        this.deleteOpsAvgResponseTimeMillis = this.getDouble("deleteOpsAverageResponseTimeMillis");
        this.deleteOpsCount = parseCountAttribute(entry, "deleteOpsCount");
        this.deleteOpsPercent = parsePercentAttribute(entry, "deleteOpsPercent");
        this.deleteOpsAggregatePercent = parsePercentAttribute(entry, "deleteOpsAggregatePercent");
        this.extendedOpsTotalCount = this.getLong("extendedOpsTotalCount");
        this.extendedOpsAvgResponseTimeMillis = this.getDouble("extendedOpsAverageResponseTimeMillis");
        this.extendedOpsCount = parseCountAttribute(entry, "extendedOpsCount");
        this.extendedOpsPercent = parsePercentAttribute(entry, "extendedOpsPercent");
        this.extendedOpsAggregatePercent = parsePercentAttribute(entry, "extendedOpsAggregatePercent");
        this.modifyOpsTotalCount = this.getLong("modifyOpsTotalCount");
        this.modifyOpsAvgResponseTimeMillis = this.getDouble("modifyOpsAverageResponseTimeMillis");
        this.modifyOpsCount = parseCountAttribute(entry, "modifyOpsCount");
        this.modifyOpsPercent = parsePercentAttribute(entry, "modifyOpsPercent");
        this.modifyOpsAggregatePercent = parsePercentAttribute(entry, "modifyOpsAggregatePercent");
        this.modifyDNOpsTotalCount = this.getLong("modifyDNOpsTotalCount");
        this.modifyDNOpsAvgResponseTimeMillis = this.getDouble("modifyDNOpsAverageResponseTimeMillis");
        this.modifyDNOpsCount = parseCountAttribute(entry, "modifyDNOpsCount");
        this.modifyDNOpsPercent = parsePercentAttribute(entry, "modifyDNOpsPercent");
        this.modifyDNOpsAggregatePercent = parsePercentAttribute(entry, "modifyDNOpsAggregatePercent");
        this.searchOpsTotalCount = this.getLong("searchOpsTotalCount");
        this.searchOpsAvgResponseTimeMillis = this.getDouble("searchOpsAverageResponseTimeMillis");
        this.searchOpsCount = parseCountAttribute(entry, "searchOpsCount");
        this.searchOpsPercent = parsePercentAttribute(entry, "searchOpsPercent");
        this.searchOpsAggregatePercent = parsePercentAttribute(entry, "searchOpsAggregatePercent");
    }
    
    private static Map<Long, Long> parseCountAttribute(final Entry entry, final String name) {
        final String[] values = entry.getAttributeValues(name);
        if (values == null || values.length == 0) {
            return Collections.emptyMap();
        }
        try {
            final LinkedHashMap<Long, Long> map = new LinkedHashMap<Long, Long>(StaticUtils.computeMapCapacity(50));
            int colonPos = values[0].indexOf(58);
            map.put(0L, Long.parseLong(values[0].substring(colonPos + 1).trim()));
            for (int i = 1; i < values.length; ++i) {
                int msPos = values[i].indexOf("ms ");
                long lowerBound;
                if (msPos < 0) {
                    msPos = values[i].indexOf("ms:");
                    lowerBound = Long.parseLong(values[i].substring(9, msPos));
                }
                else {
                    lowerBound = Long.parseLong(values[i].substring(8, msPos));
                }
                colonPos = values[i].indexOf(58, msPos);
                map.put(lowerBound, Long.parseLong(values[i].substring(colonPos + 1).trim()));
            }
            return Collections.unmodifiableMap((Map<? extends Long, ? extends Long>)map);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return Collections.emptyMap();
        }
    }
    
    private static Map<Long, Double> parsePercentAttribute(final Entry entry, final String name) {
        final String[] values = entry.getAttributeValues(name);
        if (values == null || values.length == 0) {
            return Collections.emptyMap();
        }
        try {
            final LinkedHashMap<Long, Double> map = new LinkedHashMap<Long, Double>(StaticUtils.computeMapCapacity(50));
            boolean atLeastFound = false;
            long lastUpperBound = 0L;
            for (final String s : values) {
                final int colonPos = s.indexOf(58);
                final int pctPos = s.indexOf(37, colonPos);
                final double percent = Double.parseDouble(s.substring(colonPos + 1, pctPos));
                final int msPos = s.indexOf("ms");
                if (s.startsWith("Less than ")) {
                    map.put(lastUpperBound, percent);
                    lastUpperBound = Long.parseLong(s.substring(10, msPos));
                }
                else if (s.startsWith("Between ")) {
                    final long lowerBound = Long.parseLong(s.substring(8, msPos));
                    map.put(lowerBound, percent);
                    final int secondMSPos = s.indexOf("ms:", msPos + 1);
                    lastUpperBound = Long.parseLong(s.substring(msPos + 7, secondMSPos));
                }
                else {
                    atLeastFound = true;
                    final long lowerBound = Long.parseLong(s.substring(9, msPos));
                    map.put(lowerBound, percent);
                }
            }
            if (!atLeastFound) {
                map.put(lastUpperBound, 100.0);
            }
            return Collections.unmodifiableMap((Map<? extends Long, ? extends Double>)map);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return Collections.emptyMap();
        }
    }
    
    public final Long getAllOpsTotalCount() {
        return this.allOpsTotalCount;
    }
    
    public final Double getAllOpsAverageResponseTimeMillis() {
        return this.allOpsAvgResponseTimeMillis;
    }
    
    public final Map<Long, Long> getAllOpsCount() {
        return this.allOpsCount;
    }
    
    public final Map<Long, Double> getAllOpsPercent() {
        return this.allOpsPercent;
    }
    
    public final Map<Long, Double> getAllOpsAggregatePercent() {
        return this.allOpsAggregatePercent;
    }
    
    public final Long getAddOpsTotalCount() {
        return this.addOpsTotalCount;
    }
    
    public final Double getAddOpsAverageResponseTimeMillis() {
        return this.addOpsAvgResponseTimeMillis;
    }
    
    public final Map<Long, Long> getAddOpsCount() {
        return this.addOpsCount;
    }
    
    public final Map<Long, Double> getAddOpsPercent() {
        return this.addOpsPercent;
    }
    
    public final Map<Long, Double> getAddOpsAggregatePercent() {
        return this.addOpsAggregatePercent;
    }
    
    public final Long getBindOpsTotalCount() {
        return this.bindOpsTotalCount;
    }
    
    public final Double getBindOpsAverageResponseTimeMillis() {
        return this.bindOpsAvgResponseTimeMillis;
    }
    
    public final Map<Long, Long> getBindOpsCount() {
        return this.bindOpsCount;
    }
    
    public final Map<Long, Double> getBindOpsPercent() {
        return this.bindOpsPercent;
    }
    
    public final Map<Long, Double> getBindOpsAggregatePercent() {
        return this.bindOpsAggregatePercent;
    }
    
    public final Long getCompareOpsTotalCount() {
        return this.compareOpsTotalCount;
    }
    
    public final Double getCompareOpsAverageResponseTimeMillis() {
        return this.compareOpsAvgResponseTimeMillis;
    }
    
    public final Map<Long, Long> getCompareOpsCount() {
        return this.compareOpsCount;
    }
    
    public final Map<Long, Double> getCompareOpsPercent() {
        return this.compareOpsPercent;
    }
    
    public final Map<Long, Double> getCompareOpsAggregatePercent() {
        return this.compareOpsAggregatePercent;
    }
    
    public final Long getDeleteOpsTotalCount() {
        return this.deleteOpsTotalCount;
    }
    
    public final Double getDeleteOpsAverageResponseTimeMillis() {
        return this.deleteOpsAvgResponseTimeMillis;
    }
    
    public final Map<Long, Long> getDeleteOpsCount() {
        return this.deleteOpsCount;
    }
    
    public final Map<Long, Double> getDeleteOpsPercent() {
        return this.deleteOpsPercent;
    }
    
    public final Map<Long, Double> getDeleteOpsAggregatePercent() {
        return this.deleteOpsAggregatePercent;
    }
    
    public final Long getExtendedOpsTotalCount() {
        return this.extendedOpsTotalCount;
    }
    
    public final Double getExtendedOpsAverageResponseTimeMillis() {
        return this.extendedOpsAvgResponseTimeMillis;
    }
    
    public final Map<Long, Long> getExtendedOpsCount() {
        return this.extendedOpsCount;
    }
    
    public final Map<Long, Double> getExtendedOpsPercent() {
        return this.extendedOpsPercent;
    }
    
    public final Map<Long, Double> getExtendedOpsAggregatePercent() {
        return this.extendedOpsAggregatePercent;
    }
    
    public final Long getModifyOpsTotalCount() {
        return this.modifyOpsTotalCount;
    }
    
    public final Double getModifyOpsAverageResponseTimeMillis() {
        return this.modifyOpsAvgResponseTimeMillis;
    }
    
    public final Map<Long, Long> getModifyOpsCount() {
        return this.modifyOpsCount;
    }
    
    public final Map<Long, Double> getModifyOpsPercent() {
        return this.modifyOpsPercent;
    }
    
    public final Map<Long, Double> getModifyOpsAggregatePercent() {
        return this.modifyOpsAggregatePercent;
    }
    
    public final Map<Long, Long> getModifyDNOpsCount() {
        return this.modifyDNOpsCount;
    }
    
    public final Long getModifyDNOpsTotalCount() {
        return this.modifyDNOpsTotalCount;
    }
    
    public final Double getModifyDNOpsAverageResponseTimeMillis() {
        return this.modifyDNOpsAvgResponseTimeMillis;
    }
    
    public final Map<Long, Double> getModifyDNOpsPercent() {
        return this.modifyDNOpsPercent;
    }
    
    public final Map<Long, Double> getModifyDNOpsAggregatePercent() {
        return this.modifyDNOpsAggregatePercent;
    }
    
    public final Long getSearchOpsTotalCount() {
        return this.searchOpsTotalCount;
    }
    
    public final Double getSearchOpsAverageResponseTimeMillis() {
        return this.searchOpsAvgResponseTimeMillis;
    }
    
    public final Map<Long, Long> getSearchOpsCount() {
        return this.searchOpsCount;
    }
    
    public final Map<Long, Double> getSearchOpsPercent() {
        return this.searchOpsPercent;
    }
    
    public final Map<Long, Double> getSearchOpsAggregatePercent() {
        return this.searchOpsAggregatePercent;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_PROCESSING_TIME_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_PROCESSING_TIME_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(50));
        if (this.allOpsTotalCount != null) {
            MonitorEntry.addMonitorAttribute(attrs, "allOpsTotalCount", MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_ALL_TOTAL_COUNT.get(), MonitorMessages.INFO_PROCESSING_TIME_DESC_ALL_TOTAL_COUNT.get(), this.allOpsTotalCount);
        }
        if (this.allOpsAvgResponseTimeMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "allOpsAverageResponseTimeMillis", MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_ALL_TOTAL_TIME.get(), MonitorMessages.INFO_PROCESSING_TIME_DESC_ALL_TOTAL_TIME.get(), this.allOpsAvgResponseTimeMillis);
        }
        if (!this.allOpsCount.isEmpty()) {
            final Iterator<Long> iterator = this.allOpsCount.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "allOpsCount-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_ALL_COUNT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_ALL_COUNT.get(lastValue, value), this.allOpsCount.get(lastValue));
                lastValue = value;
                if (!iterator.hasNext()) {
                    MonitorEntry.addMonitorAttribute(attrs, "allOpsCount-" + lastValue, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_ALL_COUNT_LAST.get(lastValue), MonitorMessages.INFO_PROCESSING_TIME_DESC_ALL_COUNT_LAST.get(lastValue), this.allOpsCount.get(lastValue));
                }
            }
        }
        if (!this.allOpsPercent.isEmpty()) {
            final Iterator<Long> iterator = this.allOpsPercent.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "allOpsPct-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_ALL_PCT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_ALL_PCT.get(lastValue, value), this.allOpsPercent.get(lastValue));
                lastValue = value;
                if (!iterator.hasNext()) {
                    MonitorEntry.addMonitorAttribute(attrs, "allOpsPct-" + lastValue, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_ALL_PCT_LAST.get(lastValue), MonitorMessages.INFO_PROCESSING_TIME_DESC_ALL_PCT_LAST.get(lastValue), this.allOpsPercent.get(lastValue));
                }
            }
        }
        if (!this.allOpsAggregatePercent.isEmpty()) {
            final Iterator<Long> iterator = this.allOpsAggregatePercent.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "allOpsAggrPct-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_ALL_AGGR_PCT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_ALL_AGGR_PCT.get(lastValue, value), this.allOpsAggregatePercent.get(lastValue));
                lastValue = value;
            }
        }
        if (this.addOpsTotalCount != null) {
            MonitorEntry.addMonitorAttribute(attrs, "addOpsTotalCount", MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_ADD_TOTAL_COUNT.get(), MonitorMessages.INFO_PROCESSING_TIME_DESC_ADD_TOTAL_COUNT.get(), this.addOpsTotalCount);
        }
        if (this.addOpsAvgResponseTimeMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "addOpsAverageResponseTimeMillis", MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_ADD_TOTAL_TIME.get(), MonitorMessages.INFO_PROCESSING_TIME_DESC_ADD_TOTAL_TIME.get(), this.addOpsAvgResponseTimeMillis);
        }
        if (!this.addOpsCount.isEmpty()) {
            final Iterator<Long> iterator = this.addOpsCount.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "addOpsCount-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_ADD_COUNT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_ADD_COUNT.get(lastValue, value), this.addOpsCount.get(lastValue));
                lastValue = value;
                if (!iterator.hasNext()) {
                    MonitorEntry.addMonitorAttribute(attrs, "addOpsCount-" + lastValue, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_ADD_COUNT_LAST.get(lastValue), MonitorMessages.INFO_PROCESSING_TIME_DESC_ADD_COUNT_LAST.get(lastValue), this.addOpsCount.get(lastValue));
                }
            }
        }
        if (!this.addOpsPercent.isEmpty()) {
            final Iterator<Long> iterator = this.addOpsPercent.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "addOpsPct-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_ADD_PCT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_ADD_PCT.get(lastValue, value), this.addOpsPercent.get(lastValue));
                lastValue = value;
                if (!iterator.hasNext()) {
                    MonitorEntry.addMonitorAttribute(attrs, "addOpsPct-" + lastValue, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_ADD_PCT_LAST.get(lastValue), MonitorMessages.INFO_PROCESSING_TIME_DESC_ADD_PCT_LAST.get(lastValue), this.addOpsPercent.get(lastValue));
                }
            }
        }
        if (!this.addOpsAggregatePercent.isEmpty()) {
            final Iterator<Long> iterator = this.addOpsAggregatePercent.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "addOpsAggrPct-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_ADD_AGGR_PCT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_ADD_AGGR_PCT.get(lastValue, value), this.addOpsAggregatePercent.get(lastValue));
                lastValue = value;
            }
        }
        if (this.bindOpsTotalCount != null) {
            MonitorEntry.addMonitorAttribute(attrs, "bindOpsTotalCount", MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_BIND_TOTAL_COUNT.get(), MonitorMessages.INFO_PROCESSING_TIME_DESC_BIND_TOTAL_COUNT.get(), this.bindOpsTotalCount);
        }
        if (this.bindOpsAvgResponseTimeMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "bindOpsAverageResponseTimeMillis", MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_BIND_TOTAL_TIME.get(), MonitorMessages.INFO_PROCESSING_TIME_DESC_BIND_TOTAL_TIME.get(), this.bindOpsAvgResponseTimeMillis);
        }
        if (!this.bindOpsCount.isEmpty()) {
            final Iterator<Long> iterator = this.bindOpsCount.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "bindOpsCount-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_BIND_COUNT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_BIND_COUNT.get(lastValue, value), this.bindOpsCount.get(lastValue));
                lastValue = value;
                if (!iterator.hasNext()) {
                    MonitorEntry.addMonitorAttribute(attrs, "bindOpsCount-" + lastValue, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_BIND_COUNT_LAST.get(lastValue), MonitorMessages.INFO_PROCESSING_TIME_DESC_BIND_COUNT_LAST.get(lastValue), this.bindOpsCount.get(lastValue));
                }
            }
        }
        if (!this.bindOpsPercent.isEmpty()) {
            final Iterator<Long> iterator = this.bindOpsPercent.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "bindOpsPct-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_BIND_PCT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_BIND_PCT.get(lastValue, value), this.bindOpsPercent.get(lastValue));
                lastValue = value;
                if (!iterator.hasNext()) {
                    MonitorEntry.addMonitorAttribute(attrs, "bindOpsPct-" + lastValue, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_BIND_PCT_LAST.get(lastValue), MonitorMessages.INFO_PROCESSING_TIME_DESC_BIND_PCT_LAST.get(lastValue), this.bindOpsPercent.get(lastValue));
                }
            }
        }
        if (!this.bindOpsAggregatePercent.isEmpty()) {
            final Iterator<Long> iterator = this.bindOpsAggregatePercent.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "bindOpsAggrPct-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_BIND_AGGR_PCT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_BIND_AGGR_PCT.get(lastValue, value), this.bindOpsAggregatePercent.get(lastValue));
                lastValue = value;
            }
        }
        if (this.compareOpsTotalCount != null) {
            MonitorEntry.addMonitorAttribute(attrs, "compareOpsTotalCount", MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_COMPARE_TOTAL_COUNT.get(), MonitorMessages.INFO_PROCESSING_TIME_DESC_COMPARE_TOTAL_COUNT.get(), this.compareOpsTotalCount);
        }
        if (this.compareOpsAvgResponseTimeMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "compareOpsAverageResponseTimeMillis", MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_COMPARE_TOTAL_TIME.get(), MonitorMessages.INFO_PROCESSING_TIME_DESC_COMPARE_TOTAL_TIME.get(), this.compareOpsAvgResponseTimeMillis);
        }
        if (!this.compareOpsCount.isEmpty()) {
            final Iterator<Long> iterator = this.compareOpsCount.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "compareOpsCount-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_COMPARE_COUNT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_COMPARE_COUNT.get(lastValue, value), this.compareOpsCount.get(lastValue));
                lastValue = value;
                if (!iterator.hasNext()) {
                    MonitorEntry.addMonitorAttribute(attrs, "compareOpsCount-" + lastValue, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_COMPARE_COUNT_LAST.get(lastValue), MonitorMessages.INFO_PROCESSING_TIME_DESC_COMPARE_COUNT_LAST.get(lastValue), this.compareOpsCount.get(lastValue));
                }
            }
        }
        if (!this.compareOpsPercent.isEmpty()) {
            final Iterator<Long> iterator = this.compareOpsPercent.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "compareOpsPct-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_COMPARE_PCT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_COMPARE_PCT.get(lastValue, value), this.compareOpsPercent.get(lastValue));
                lastValue = value;
                if (!iterator.hasNext()) {
                    MonitorEntry.addMonitorAttribute(attrs, "compareOpsPct-" + lastValue, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_COMPARE_PCT_LAST.get(lastValue), MonitorMessages.INFO_PROCESSING_TIME_DESC_COMPARE_PCT_LAST.get(lastValue), this.compareOpsPercent.get(lastValue));
                }
            }
        }
        if (!this.compareOpsAggregatePercent.isEmpty()) {
            final Iterator<Long> iterator = this.compareOpsAggregatePercent.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "compareOpsAggrPct-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_COMPARE_AGGR_PCT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_COMPARE_AGGR_PCT.get(lastValue, value), this.compareOpsAggregatePercent.get(lastValue));
                lastValue = value;
            }
        }
        if (this.deleteOpsTotalCount != null) {
            MonitorEntry.addMonitorAttribute(attrs, "deleteOpsTotalCount", MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_DELETE_TOTAL_COUNT.get(), MonitorMessages.INFO_PROCESSING_TIME_DESC_DELETE_TOTAL_COUNT.get(), this.deleteOpsTotalCount);
        }
        if (this.deleteOpsAvgResponseTimeMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "deleteOpsAverageResponseTimeMillis", MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_DELETE_TOTAL_TIME.get(), MonitorMessages.INFO_PROCESSING_TIME_DESC_DELETE_TOTAL_TIME.get(), this.deleteOpsAvgResponseTimeMillis);
        }
        if (!this.deleteOpsCount.isEmpty()) {
            final Iterator<Long> iterator = this.deleteOpsCount.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "deleteOpsCount-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_DELETE_COUNT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_DELETE_COUNT.get(lastValue, value), this.deleteOpsCount.get(lastValue));
                lastValue = value;
                if (!iterator.hasNext()) {
                    MonitorEntry.addMonitorAttribute(attrs, "deleteOpsCount-" + lastValue, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_DELETE_COUNT_LAST.get(lastValue), MonitorMessages.INFO_PROCESSING_TIME_DESC_DELETE_COUNT_LAST.get(lastValue), this.deleteOpsCount.get(lastValue));
                }
            }
        }
        if (!this.deleteOpsPercent.isEmpty()) {
            final Iterator<Long> iterator = this.deleteOpsPercent.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "deleteOpsPct-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_DELETE_PCT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_DELETE_PCT.get(lastValue, value), this.deleteOpsPercent.get(lastValue));
                lastValue = value;
                if (!iterator.hasNext()) {
                    MonitorEntry.addMonitorAttribute(attrs, "deleteOpsPct-" + lastValue, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_DELETE_PCT_LAST.get(lastValue), MonitorMessages.INFO_PROCESSING_TIME_DESC_DELETE_PCT_LAST.get(lastValue), this.deleteOpsPercent.get(lastValue));
                }
            }
        }
        if (!this.deleteOpsAggregatePercent.isEmpty()) {
            final Iterator<Long> iterator = this.deleteOpsAggregatePercent.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "deleteOpsAggrPct-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_DELETE_AGGR_PCT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_DELETE_AGGR_PCT.get(lastValue, value), this.deleteOpsAggregatePercent.get(lastValue));
                lastValue = value;
            }
        }
        if (this.extendedOpsTotalCount != null) {
            MonitorEntry.addMonitorAttribute(attrs, "extendedOpsTotalCount", MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_EXTENDED_TOTAL_COUNT.get(), MonitorMessages.INFO_PROCESSING_TIME_DESC_EXTENDED_TOTAL_COUNT.get(), this.extendedOpsTotalCount);
        }
        if (this.extendedOpsAvgResponseTimeMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "extendedOpsAverageResponseTimeMillis", MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_EXTENDED_TOTAL_TIME.get(), MonitorMessages.INFO_PROCESSING_TIME_DESC_EXTENDED_TOTAL_TIME.get(), this.extendedOpsAvgResponseTimeMillis);
        }
        if (!this.extendedOpsCount.isEmpty()) {
            final Iterator<Long> iterator = this.extendedOpsCount.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "extendedOpsCount-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_EXTENDED_COUNT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_EXTENDED_COUNT.get(lastValue, value), this.extendedOpsCount.get(lastValue));
                lastValue = value;
                if (!iterator.hasNext()) {
                    MonitorEntry.addMonitorAttribute(attrs, "extendedOpsCount-" + lastValue, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_EXTENDED_COUNT_LAST.get(lastValue), MonitorMessages.INFO_PROCESSING_TIME_DESC_EXTENDED_COUNT_LAST.get(lastValue), this.extendedOpsCount.get(lastValue));
                }
            }
        }
        if (!this.extendedOpsPercent.isEmpty()) {
            final Iterator<Long> iterator = this.extendedOpsPercent.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "extendedOpsPct-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_EXTENDED_PCT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_EXTENDED_PCT.get(lastValue, value), this.extendedOpsPercent.get(lastValue));
                lastValue = value;
                if (!iterator.hasNext()) {
                    MonitorEntry.addMonitorAttribute(attrs, "extendedOpsPct-" + lastValue, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_EXTENDED_PCT_LAST.get(lastValue), MonitorMessages.INFO_PROCESSING_TIME_DESC_EXTENDED_PCT_LAST.get(lastValue), this.extendedOpsPercent.get(lastValue));
                }
            }
        }
        if (!this.extendedOpsAggregatePercent.isEmpty()) {
            final Iterator<Long> iterator = this.extendedOpsAggregatePercent.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "extendedOpsAggrPct-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_EXTENDED_AGGR_PCT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_EXTENDED_AGGR_PCT.get(lastValue, value), this.extendedOpsAggregatePercent.get(lastValue));
                lastValue = value;
            }
        }
        if (this.modifyOpsTotalCount != null) {
            MonitorEntry.addMonitorAttribute(attrs, "modifyOpsTotalCount", MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_MODIFY_TOTAL_COUNT.get(), MonitorMessages.INFO_PROCESSING_TIME_DESC_MODIFY_TOTAL_COUNT.get(), this.modifyOpsTotalCount);
        }
        if (this.modifyOpsAvgResponseTimeMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "modifyOpsAverageResponseTimeMillis", MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_MODIFY_TOTAL_TIME.get(), MonitorMessages.INFO_PROCESSING_TIME_DESC_MODIFY_TOTAL_TIME.get(), this.modifyOpsAvgResponseTimeMillis);
        }
        if (!this.modifyOpsCount.isEmpty()) {
            final Iterator<Long> iterator = this.modifyOpsCount.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "modifyOpsCount-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_MODIFY_COUNT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_MODIFY_COUNT.get(lastValue, value), this.modifyOpsCount.get(lastValue));
                lastValue = value;
                if (!iterator.hasNext()) {
                    MonitorEntry.addMonitorAttribute(attrs, "modifyOpsCount-" + lastValue, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_MODIFY_COUNT_LAST.get(lastValue), MonitorMessages.INFO_PROCESSING_TIME_DESC_MODIFY_COUNT_LAST.get(lastValue), this.modifyOpsCount.get(lastValue));
                }
            }
        }
        if (!this.modifyOpsPercent.isEmpty()) {
            final Iterator<Long> iterator = this.modifyOpsPercent.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "modifyOpsPct-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_MODIFY_PCT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_MODIFY_PCT.get(lastValue, value), this.modifyOpsPercent.get(lastValue));
                lastValue = value;
                if (!iterator.hasNext()) {
                    MonitorEntry.addMonitorAttribute(attrs, "modifyOpsPct-" + lastValue, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_MODIFY_PCT_LAST.get(lastValue), MonitorMessages.INFO_PROCESSING_TIME_DESC_MODIFY_PCT_LAST.get(lastValue), this.modifyOpsPercent.get(lastValue));
                }
            }
        }
        if (!this.modifyOpsAggregatePercent.isEmpty()) {
            final Iterator<Long> iterator = this.modifyOpsAggregatePercent.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "modifyOpsAggrPct-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_MODIFY_AGGR_PCT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_MODIFY_AGGR_PCT.get(lastValue, value), this.modifyOpsAggregatePercent.get(lastValue));
                lastValue = value;
            }
        }
        if (this.modifyDNOpsTotalCount != null) {
            MonitorEntry.addMonitorAttribute(attrs, "modifyDNOpsTotalCount", MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_MODIFY_DN_TOTAL_COUNT.get(), MonitorMessages.INFO_PROCESSING_TIME_DESC_MODIFY_DN_TOTAL_COUNT.get(), this.modifyDNOpsTotalCount);
        }
        if (this.modifyDNOpsAvgResponseTimeMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "modifyDNOpsAverageResponseTimeMillis", MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_MODIFY_DN_TOTAL_TIME.get(), MonitorMessages.INFO_PROCESSING_TIME_DESC_MODIFY_DN_TOTAL_TIME.get(), this.modifyDNOpsAvgResponseTimeMillis);
        }
        if (!this.modifyDNOpsCount.isEmpty()) {
            final Iterator<Long> iterator = this.modifyDNOpsCount.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "modifyDNOpsCount-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_MODIFY_DN_COUNT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_MODIFY_DN_COUNT.get(lastValue, value), this.modifyDNOpsCount.get(lastValue));
                lastValue = value;
                if (!iterator.hasNext()) {
                    MonitorEntry.addMonitorAttribute(attrs, "modifyDNOpsCount-" + lastValue, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_MODIFY_DN_COUNT_LAST.get(lastValue), MonitorMessages.INFO_PROCESSING_TIME_DESC_MODIFY_DN_COUNT_LAST.get(lastValue), this.modifyDNOpsCount.get(lastValue));
                }
            }
        }
        if (!this.modifyDNOpsPercent.isEmpty()) {
            final Iterator<Long> iterator = this.modifyDNOpsPercent.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "modifyDNOpsPct-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_MODIFY_DN_PCT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_MODIFY_DN_PCT.get(lastValue, value), this.modifyDNOpsPercent.get(lastValue));
                lastValue = value;
                if (!iterator.hasNext()) {
                    MonitorEntry.addMonitorAttribute(attrs, "modifyDNOpsPct-" + lastValue, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_MODIFY_DN_PCT_LAST.get(lastValue), MonitorMessages.INFO_PROCESSING_TIME_DESC_MODIFY_DN_PCT_LAST.get(lastValue), this.modifyDNOpsPercent.get(lastValue));
                }
            }
        }
        if (!this.modifyDNOpsAggregatePercent.isEmpty()) {
            final Iterator<Long> iterator = this.modifyDNOpsAggregatePercent.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "modifyDNOpsAggrPct-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_MODIFY_DN_AGGR_PCT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_MODIFY_DN_AGGR_PCT.get(lastValue, value), this.modifyDNOpsAggregatePercent.get(lastValue));
                lastValue = value;
            }
        }
        if (this.searchOpsTotalCount != null) {
            MonitorEntry.addMonitorAttribute(attrs, "searchOpsTotalCount", MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_SEARCH_TOTAL_COUNT.get(), MonitorMessages.INFO_PROCESSING_TIME_DESC_SEARCH_TOTAL_COUNT.get(), this.searchOpsTotalCount);
        }
        if (this.searchOpsAvgResponseTimeMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "searchOpsAverageResponseTimeMillis", MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_SEARCH_TOTAL_TIME.get(), MonitorMessages.INFO_PROCESSING_TIME_DESC_SEARCH_TOTAL_TIME.get(), this.searchOpsAvgResponseTimeMillis);
        }
        if (!this.searchOpsCount.isEmpty()) {
            final Iterator<Long> iterator = this.searchOpsCount.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "searchOpsCount-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_SEARCH_COUNT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_SEARCH_COUNT.get(lastValue, value), this.searchOpsCount.get(lastValue));
                lastValue = value;
                if (!iterator.hasNext()) {
                    MonitorEntry.addMonitorAttribute(attrs, "searchOpsCount-" + lastValue, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_SEARCH_COUNT_LAST.get(lastValue), MonitorMessages.INFO_PROCESSING_TIME_DESC_SEARCH_COUNT_LAST.get(lastValue), this.searchOpsCount.get(lastValue));
                }
            }
        }
        if (!this.searchOpsPercent.isEmpty()) {
            final Iterator<Long> iterator = this.searchOpsPercent.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "searchOpsPct-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_SEARCH_PCT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_SEARCH_PCT.get(lastValue, value), this.searchOpsPercent.get(lastValue));
                lastValue = value;
                if (!iterator.hasNext()) {
                    MonitorEntry.addMonitorAttribute(attrs, "searchOpsPct-" + lastValue, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_SEARCH_PCT_LAST.get(lastValue), MonitorMessages.INFO_PROCESSING_TIME_DESC_SEARCH_PCT_LAST.get(lastValue), this.searchOpsPercent.get(lastValue));
                }
            }
        }
        if (!this.searchOpsAggregatePercent.isEmpty()) {
            final Iterator<Long> iterator = this.searchOpsAggregatePercent.keySet().iterator();
            Long lastValue = iterator.next();
            while (iterator.hasNext()) {
                final Long value = iterator.next();
                MonitorEntry.addMonitorAttribute(attrs, "searchOpsAggrPct-" + lastValue + '-' + value, MonitorMessages.INFO_PROCESSING_TIME_DISPNAME_SEARCH_AGGR_PCT.get(lastValue, value), MonitorMessages.INFO_PROCESSING_TIME_DESC_SEARCH_AGGR_PCT.get(lastValue, value), this.searchOpsAggregatePercent.get(lastValue));
                lastValue = value;
            }
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
