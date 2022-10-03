package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.Iterator;
import java.util.Collections;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import com.unboundid.ldap.sdk.OperationType;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ResultCodeMonitorEntry extends MonitorEntry
{
    static final String RESULT_CODE_MONITOR_OC = "ds-ldap-result-codes-monitor-entry";
    private static final long serialVersionUID = -963682306039266913L;
    private final ExtendedOperationResultCodeInfo extendedOperationResultCodeInfo;
    private final OperationResultCodeInfo addOperationResultCodeInfo;
    private final OperationResultCodeInfo allOperationsResultCodeInfo;
    private final OperationResultCodeInfo bindOperationResultCodeInfo;
    private final OperationResultCodeInfo compareOperationResultCodeInfo;
    private final OperationResultCodeInfo deleteOperationResultCodeInfo;
    private final OperationResultCodeInfo modifyOperationResultCodeInfo;
    private final OperationResultCodeInfo modifyDNOperationResultCodeInfo;
    private final OperationResultCodeInfo searchOperationResultCodeInfo;
    
    public ResultCodeMonitorEntry(final Entry entry) {
        super(entry);
        this.allOperationsResultCodeInfo = new OperationResultCodeInfo(this, null, "all-ops-");
        this.addOperationResultCodeInfo = new OperationResultCodeInfo(this, OperationType.ADD, "add-op-");
        this.bindOperationResultCodeInfo = new OperationResultCodeInfo(this, OperationType.BIND, "bind-op-");
        this.compareOperationResultCodeInfo = new OperationResultCodeInfo(this, OperationType.COMPARE, "compare-op-");
        this.deleteOperationResultCodeInfo = new OperationResultCodeInfo(this, OperationType.DELETE, "delete-op-");
        this.extendedOperationResultCodeInfo = new ExtendedOperationResultCodeInfo(this);
        this.modifyOperationResultCodeInfo = new OperationResultCodeInfo(this, OperationType.MODIFY, "modify-op-");
        this.modifyDNOperationResultCodeInfo = new OperationResultCodeInfo(this, OperationType.MODIFY_DN, "modifydn-op-");
        this.searchOperationResultCodeInfo = new OperationResultCodeInfo(this, OperationType.SEARCH, "search-op-");
    }
    
    public OperationResultCodeInfo getAllOperationsResultCodeInfo() {
        return this.allOperationsResultCodeInfo;
    }
    
    public OperationResultCodeInfo getAddOperationResultCodeInfo() {
        return this.addOperationResultCodeInfo;
    }
    
    public OperationResultCodeInfo getBindOperationResultCodeInfo() {
        return this.bindOperationResultCodeInfo;
    }
    
    public OperationResultCodeInfo getCompareOperationResultCodeInfo() {
        return this.compareOperationResultCodeInfo;
    }
    
    public OperationResultCodeInfo getDeleteOperationResultCodeInfo() {
        return this.deleteOperationResultCodeInfo;
    }
    
    public ExtendedOperationResultCodeInfo getExtendedOperationResultCodeInfo() {
        return this.extendedOperationResultCodeInfo;
    }
    
    public OperationResultCodeInfo getModifyOperationResultCodeInfo() {
        return this.modifyOperationResultCodeInfo;
    }
    
    public OperationResultCodeInfo getModifyDNOperationResultCodeInfo() {
        return this.modifyDNOperationResultCodeInfo;
    }
    
    public OperationResultCodeInfo getSearchOperationResultCodeInfo() {
        return this.searchOperationResultCodeInfo;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_RESULT_CODE_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_RESULT_CODE_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(100));
        addAttrs(attrs, this.allOperationsResultCodeInfo, "all-ops-");
        addAttrs(attrs, this.addOperationResultCodeInfo, "add-op-");
        addAttrs(attrs, this.bindOperationResultCodeInfo, "bind-op-");
        addAttrs(attrs, this.compareOperationResultCodeInfo, "compare-op-");
        addAttrs(attrs, this.deleteOperationResultCodeInfo, "delete-op-");
        addAttrs(attrs, this.extendedOperationResultCodeInfo);
        addAttrs(attrs, this.modifyOperationResultCodeInfo, "modify-op-");
        addAttrs(attrs, this.modifyDNOperationResultCodeInfo, "modifydn-op-");
        addAttrs(attrs, this.searchOperationResultCodeInfo, "search-op-");
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
    
    private static void addAttrs(final LinkedHashMap<String, MonitorAttribute> attrs, final OperationResultCodeInfo resultCodeInfo, final String attrPrefix) {
        String opName = null;
        if (resultCodeInfo.getOperationType() == null) {
            opName = MonitorMessages.INFO_RESULT_CODE_OP_NAME_ALL.get();
        }
        else {
            switch (resultCodeInfo.getOperationType()) {
                case ADD: {
                    opName = MonitorMessages.INFO_RESULT_CODE_OP_NAME_ADD.get();
                    break;
                }
                case BIND: {
                    opName = MonitorMessages.INFO_RESULT_CODE_OP_NAME_BIND.get();
                    break;
                }
                case COMPARE: {
                    opName = MonitorMessages.INFO_RESULT_CODE_OP_NAME_COMPARE.get();
                    break;
                }
                case DELETE: {
                    opName = MonitorMessages.INFO_RESULT_CODE_OP_NAME_DELETE.get();
                    break;
                }
                case MODIFY: {
                    opName = MonitorMessages.INFO_RESULT_CODE_OP_NAME_MODIFY.get();
                    break;
                }
                case MODIFY_DN: {
                    opName = MonitorMessages.INFO_RESULT_CODE_OP_NAME_MODIFY_DN.get();
                    break;
                }
                case SEARCH: {
                    opName = MonitorMessages.INFO_RESULT_CODE_OP_NAME_SEARCH.get();
                    break;
                }
                default: {
                    opName = "Unknown";
                    break;
                }
            }
        }
        final String lowerOpName = StaticUtils.toLowerCase(opName);
        final Long totalCount = resultCodeInfo.getTotalCount();
        if (totalCount != null) {
            MonitorEntry.addMonitorAttribute(attrs, attrPrefix + "total-count", MonitorMessages.INFO_RESULT_CODE_DISPNAME_TOTAL_COUNT.get(opName), MonitorMessages.INFO_RESULT_CODE_DESC_TOTAL_COUNT.get(lowerOpName), totalCount);
        }
        final Long failedCount = resultCodeInfo.getFailedCount();
        if (failedCount != null) {
            MonitorEntry.addMonitorAttribute(attrs, attrPrefix + "failed-count", MonitorMessages.INFO_RESULT_CODE_DISPNAME_FAILED_COUNT.get(opName), MonitorMessages.INFO_RESULT_CODE_DESC_FAILED_COUNT.get(lowerOpName), failedCount);
        }
        final Double failedPercent = resultCodeInfo.getFailedPercent();
        if (failedPercent != null) {
            MonitorEntry.addMonitorAttribute(attrs, attrPrefix + "failed-percent", MonitorMessages.INFO_RESULT_CODE_DISPNAME_FAILED_PERCENT.get(opName), MonitorMessages.INFO_RESULT_CODE_DESC_FAILED_PERCENT.get(lowerOpName), failedPercent);
        }
        for (final ResultCodeInfo i : resultCodeInfo.getResultCodeInfoMap().values()) {
            MonitorEntry.addMonitorAttribute(attrs, attrPrefix + i.intValue() + "-name", MonitorMessages.INFO_RESULT_CODE_DISPNAME_RC_NAME.get(opName, i.intValue()), MonitorMessages.INFO_RESULT_CODE_DESC_RC_NAME.get(lowerOpName, i.intValue()), i.getName());
            MonitorEntry.addMonitorAttribute(attrs, attrPrefix + i.intValue() + "-count", MonitorMessages.INFO_RESULT_CODE_DISPNAME_RC_COUNT.get(opName, i.intValue()), MonitorMessages.INFO_RESULT_CODE_DESC_RC_COUNT.get(lowerOpName, i.intValue()), i.getCount());
            MonitorEntry.addMonitorAttribute(attrs, attrPrefix + i.intValue() + "-percent", MonitorMessages.INFO_RESULT_CODE_DISPNAME_RC_PERCENT.get(opName, i.intValue()), MonitorMessages.INFO_RESULT_CODE_DESC_RC_PERCENT.get(lowerOpName, i.intValue()), i.getPercent());
            MonitorEntry.addMonitorAttribute(attrs, attrPrefix + i.intValue() + "-average-response-time-millis", MonitorMessages.INFO_RESULT_CODE_DISPNAME_RC_AVG_RT.get(opName, i.intValue()), MonitorMessages.INFO_RESULT_CODE_DESC_RC_AVG_RT.get(lowerOpName, i.intValue()), i.getAverageResponseTimeMillis());
            MonitorEntry.addMonitorAttribute(attrs, attrPrefix + i.intValue() + "-total-response-time-millis", MonitorMessages.INFO_RESULT_CODE_DISPNAME_RC_TOTAL_RT.get(opName, i.intValue()), MonitorMessages.INFO_RESULT_CODE_DESC_RC_TOTAL_RT.get(lowerOpName, i.intValue()), i.getTotalResponseTimeMillis());
        }
    }
    
    private static void addAttrs(final LinkedHashMap<String, MonitorAttribute> attrs, final ExtendedOperationResultCodeInfo resultCodeInfo) {
        final String opName = MonitorMessages.INFO_RESULT_CODE_OP_NAME_EXTENDED.get();
        final String lowerOpName = StaticUtils.toLowerCase(opName);
        final Long totalCount = resultCodeInfo.getTotalCount();
        if (totalCount != null) {
            MonitorEntry.addMonitorAttribute(attrs, "extended-op-total-count", MonitorMessages.INFO_RESULT_CODE_DISPNAME_TOTAL_COUNT.get(opName), MonitorMessages.INFO_RESULT_CODE_DESC_TOTAL_COUNT.get(lowerOpName), totalCount);
        }
        final Long failedCount = resultCodeInfo.getFailedCount();
        if (failedCount != null) {
            MonitorEntry.addMonitorAttribute(attrs, "extended-op-failed-count", MonitorMessages.INFO_RESULT_CODE_DISPNAME_FAILED_COUNT.get(opName), MonitorMessages.INFO_RESULT_CODE_DESC_FAILED_COUNT.get(lowerOpName), failedCount);
        }
        final Double failedPercent = resultCodeInfo.getFailedPercent();
        if (failedPercent != null) {
            MonitorEntry.addMonitorAttribute(attrs, "extended-op-failed-percent", MonitorMessages.INFO_RESULT_CODE_DISPNAME_FAILED_PERCENT.get(opName), MonitorMessages.INFO_RESULT_CODE_DESC_FAILED_PERCENT.get(lowerOpName), failedPercent);
        }
        for (final String oid : resultCodeInfo.getExtendedRequestNamesByOID().keySet()) {
            final String prefix = "extended-op-" + oid.replace('.', '-') + '-';
            final String name = resultCodeInfo.getExtendedRequestNamesByOID().get(oid);
            if (name != null) {
                MonitorEntry.addMonitorAttribute(attrs, prefix + "name", MonitorMessages.INFO_RESULT_CODE_DISPNAME_EXTOP_NAME.get(oid), MonitorMessages.INFO_RESULT_CODE_DESC_EXTOP_NAME.get(oid), name);
            }
            final Long total = resultCodeInfo.getTotalCountsByOID().get(oid);
            if (total != null) {
                MonitorEntry.addMonitorAttribute(attrs, prefix + "total-count", MonitorMessages.INFO_RESULT_CODE_DISPNAME_EXTOP_TOTAL_COUNT.get(oid), MonitorMessages.INFO_RESULT_CODE_DESC_EXTOP_TOTAL_COUNT.get(oid), total);
            }
            final Long failed = resultCodeInfo.getFailedCountsByOID().get(oid);
            if (failed != null) {
                MonitorEntry.addMonitorAttribute(attrs, prefix + "failed-count", MonitorMessages.INFO_RESULT_CODE_DISPNAME_EXTOP_FAILED_COUNT.get(oid), MonitorMessages.INFO_RESULT_CODE_DESC_EXTOP_FAILED_COUNT.get(oid), failed);
            }
            final Double percent = resultCodeInfo.getFailedPercentsByOID().get(oid);
            if (percent != null) {
                MonitorEntry.addMonitorAttribute(attrs, prefix + "failed-percent", MonitorMessages.INFO_RESULT_CODE_DISPNAME_EXTOP_FAILED_PERCENT.get(oid), MonitorMessages.INFO_RESULT_CODE_DESC_EXTOP_FAILED_PERCENT.get(oid), percent);
            }
            final Map<Integer, ResultCodeInfo> rcInfoMap = resultCodeInfo.getResultCodeInfoMap().get(oid);
            if (rcInfoMap != null) {
                for (final ResultCodeInfo rcInfo : rcInfoMap.values()) {
                    final int intValue = rcInfo.intValue();
                    final String rcPrefix = prefix + intValue + '-';
                    MonitorEntry.addMonitorAttribute(attrs, rcPrefix + "name", MonitorMessages.INFO_RESULT_CODE_DISPNAME_EXTOP_RC_NAME.get(oid, intValue), MonitorMessages.INFO_RESULT_CODE_DESC_EXTOP_RC_NAME.get(oid, intValue), rcInfo.getName());
                    MonitorEntry.addMonitorAttribute(attrs, rcPrefix + "count", MonitorMessages.INFO_RESULT_CODE_DISPNAME_EXTOP_RC_COUNT.get(oid, intValue), MonitorMessages.INFO_RESULT_CODE_DESC_EXTOP_RC_COUNT.get(oid, intValue), rcInfo.getCount());
                    MonitorEntry.addMonitorAttribute(attrs, rcPrefix + "percent", MonitorMessages.INFO_RESULT_CODE_DISPNAME_EXTOP_RC_PERCENT.get(oid, intValue), MonitorMessages.INFO_RESULT_CODE_DESC_EXTOP_RC_PERCENT.get(oid, intValue), rcInfo.getPercent());
                    MonitorEntry.addMonitorAttribute(attrs, rcPrefix + "average-response-time-millis", MonitorMessages.INFO_RESULT_CODE_DISPNAME_EXTOP_RC_AVG_RT.get(oid, intValue), MonitorMessages.INFO_RESULT_CODE_DESC_EXTOP_RC_AVG_RT.get(oid, intValue), rcInfo.getAverageResponseTimeMillis());
                    MonitorEntry.addMonitorAttribute(attrs, rcPrefix + "total-response-time-millis", MonitorMessages.INFO_RESULT_CODE_DISPNAME_EXTOP_RC_TOTAL_RT.get(oid, intValue), MonitorMessages.INFO_RESULT_CODE_DESC_EXTOP_RC_TOTAL_RT.get(oid, intValue), rcInfo.getTotalResponseTimeMillis());
                }
            }
        }
    }
}
