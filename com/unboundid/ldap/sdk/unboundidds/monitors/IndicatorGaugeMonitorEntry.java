package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Collections;
import com.unboundid.ldap.sdk.Entry;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class IndicatorGaugeMonitorEntry extends GaugeMonitorEntry
{
    static final String INDICATOR_GAUGE_MONITOR_OC = "ds-indicator-gauge-monitor-entry";
    private static final long serialVersionUID = 6487368235968435879L;
    private final List<String> observedValues;
    private final String currentValue;
    private final String previousValue;
    
    public IndicatorGaugeMonitorEntry(final Entry entry) {
        super(entry);
        this.currentValue = this.getString("value");
        this.previousValue = this.getString("previous-value");
        final String observedValuesStr = this.getString("observed-values");
        if (observedValuesStr == null) {
            this.observedValues = Collections.emptyList();
        }
        else {
            final ArrayList<String> valueList = new ArrayList<String>(10);
            final StringTokenizer tokenizer = new StringTokenizer(observedValuesStr, ",");
            while (tokenizer.hasMoreTokens()) {
                valueList.add(tokenizer.nextToken());
            }
            this.observedValues = Collections.unmodifiableList((List<? extends String>)valueList);
        }
    }
    
    public String getCurrentValue() {
        return this.currentValue;
    }
    
    public String getPreviousValue() {
        return this.previousValue;
    }
    
    public List<String> getObservedValues() {
        return this.observedValues;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_INDICATOR_GAUGE_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_INDICATOR_GAUGE_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final Map<String, MonitorAttribute> superAttributes = super.getMonitorAttributes();
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(superAttributes.size() + 3));
        attrs.putAll((Map<?, ?>)superAttributes);
        if (this.currentValue != null) {
            MonitorEntry.addMonitorAttribute(attrs, "value", MonitorMessages.INFO_INDICATOR_GAUGE_DISPNAME_CURRENT_VALUE.get(), MonitorMessages.INFO_INDICATOR_GAUGE_DESC_CURRENT_VALUE.get(), this.currentValue);
        }
        if (this.previousValue != null) {
            MonitorEntry.addMonitorAttribute(attrs, "previous-value", MonitorMessages.INFO_INDICATOR_GAUGE_DISPNAME_PREVIOUS_VALUE.get(), MonitorMessages.INFO_INDICATOR_GAUGE_DESC_PREVIOUS_VALUE.get(), this.previousValue);
        }
        if (!this.observedValues.isEmpty()) {
            MonitorEntry.addMonitorAttribute(attrs, "observed-values", MonitorMessages.INFO_INDICATOR_GAUGE_DISPNAME_OBSERVED_VALUES.get(), MonitorMessages.INFO_INDICATOR_GAUGE_DESC_OBSERVED_VALUES.get(), this.observedValues);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
