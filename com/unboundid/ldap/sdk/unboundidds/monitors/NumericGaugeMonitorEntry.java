package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import com.unboundid.util.Debug;
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
public final class NumericGaugeMonitorEntry extends GaugeMonitorEntry
{
    static final String NUMERIC_GAUGE_MONITOR_OC = "ds-numeric-gauge-monitor-entry";
    private static final long serialVersionUID = 2049893927290436280L;
    private final Double currentValue;
    private final Double maximumValue;
    private final Double minimumValue;
    private final Double previousValue;
    private final List<Double> observedValues;
    
    public NumericGaugeMonitorEntry(final Entry entry) {
        super(entry);
        this.currentValue = this.getDouble("value");
        this.previousValue = this.getDouble("previous-value");
        this.minimumValue = this.getDouble("value-minimum");
        this.maximumValue = this.getDouble("value-maximum");
        final String observedStr = this.getString("observed-values");
        if (observedStr == null || observedStr.isEmpty()) {
            this.observedValues = Collections.emptyList();
        }
        else {
            final ArrayList<Double> values = new ArrayList<Double>(10);
            try {
                final StringTokenizer tokenizer = new StringTokenizer(observedStr, ",");
                while (tokenizer.hasMoreTokens()) {
                    values.add(Double.parseDouble(tokenizer.nextToken()));
                }
            }
            catch (final Exception e) {
                Debug.debugException(e);
                values.clear();
            }
            this.observedValues = Collections.unmodifiableList((List<? extends Double>)values);
        }
    }
    
    public Double getCurrentValue() {
        return this.currentValue;
    }
    
    public Double getPreviousValue() {
        return this.previousValue;
    }
    
    public Double getMinimumValue() {
        return this.minimumValue;
    }
    
    public Double getMaximumValue() {
        return this.maximumValue;
    }
    
    public List<Double> getObservedValues() {
        return this.observedValues;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_NUMERIC_GAUGE_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_NUMERIC_GAUGE_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final Map<String, MonitorAttribute> superAttributes = super.getMonitorAttributes();
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(superAttributes.size() + 5));
        attrs.putAll((Map<?, ?>)superAttributes);
        if (this.currentValue != null) {
            MonitorEntry.addMonitorAttribute(attrs, "value", MonitorMessages.INFO_NUMERIC_GAUGE_DISPNAME_CURRENT_VALUE.get(), MonitorMessages.INFO_NUMERIC_GAUGE_DESC_CURRENT_VALUE.get(), this.currentValue);
        }
        if (this.previousValue != null) {
            MonitorEntry.addMonitorAttribute(attrs, "previous-value", MonitorMessages.INFO_NUMERIC_GAUGE_DISPNAME_PREVIOUS_VALUE.get(), MonitorMessages.INFO_NUMERIC_GAUGE_DESC_PREVIOUS_VALUE.get(), this.previousValue);
        }
        if (this.minimumValue != null) {
            MonitorEntry.addMonitorAttribute(attrs, "value-minimum", MonitorMessages.INFO_NUMERIC_GAUGE_DISPNAME_MINIMUM_VALUE.get(), MonitorMessages.INFO_NUMERIC_GAUGE_DESC_MINIMUM_VALUE.get(), this.minimumValue);
        }
        if (this.maximumValue != null) {
            MonitorEntry.addMonitorAttribute(attrs, "value-maximum", MonitorMessages.INFO_NUMERIC_GAUGE_DISPNAME_MAXIMUM_VALUE.get(), MonitorMessages.INFO_NUMERIC_GAUGE_DESC_MAXIMUM_VALUE.get(), this.maximumValue);
        }
        if (!this.observedValues.isEmpty()) {
            final Double[] values = new Double[this.observedValues.size()];
            this.observedValues.toArray(values);
            attrs.put("observed-values", new MonitorAttribute("observed-values", MonitorMessages.INFO_NUMERIC_GAUGE_DISPNAME_OBSERVED_VALUES.get(), MonitorMessages.INFO_NUMERIC_GAUGE_DESC_OBSERVED_VALUES.get(), values));
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
