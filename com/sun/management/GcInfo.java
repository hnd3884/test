package com.sun.management;

import java.util.Collection;
import javax.management.openmbean.CompositeType;
import java.util.Collections;
import sun.management.GcInfoCompositeData;
import java.util.HashMap;
import sun.management.GcInfoBuilder;
import java.lang.management.MemoryUsage;
import java.util.Map;
import jdk.Exported;
import javax.management.openmbean.CompositeDataView;
import javax.management.openmbean.CompositeData;

@Exported
public class GcInfo implements CompositeData, CompositeDataView
{
    private final long index;
    private final long startTime;
    private final long endTime;
    private final Map<String, MemoryUsage> usageBeforeGc;
    private final Map<String, MemoryUsage> usageAfterGc;
    private final Object[] extAttributes;
    private final CompositeData cdata;
    private final GcInfoBuilder builder;
    
    private GcInfo(final GcInfoBuilder builder, final long index, final long startTime, final long endTime, final MemoryUsage[] array, final MemoryUsage[] array2, final Object[] extAttributes) {
        this.builder = builder;
        this.index = index;
        this.startTime = startTime;
        this.endTime = endTime;
        final String[] poolNames = builder.getPoolNames();
        this.usageBeforeGc = new HashMap<String, MemoryUsage>(poolNames.length);
        this.usageAfterGc = new HashMap<String, MemoryUsage>(poolNames.length);
        for (int i = 0; i < poolNames.length; ++i) {
            this.usageBeforeGc.put(poolNames[i], array[i]);
            this.usageAfterGc.put(poolNames[i], array2[i]);
        }
        this.extAttributes = extAttributes;
        this.cdata = new GcInfoCompositeData(this, builder, extAttributes);
    }
    
    private GcInfo(final CompositeData cdata) {
        GcInfoCompositeData.validateCompositeData(cdata);
        this.index = GcInfoCompositeData.getId(cdata);
        this.startTime = GcInfoCompositeData.getStartTime(cdata);
        this.endTime = GcInfoCompositeData.getEndTime(cdata);
        this.usageBeforeGc = GcInfoCompositeData.getMemoryUsageBeforeGc(cdata);
        this.usageAfterGc = GcInfoCompositeData.getMemoryUsageAfterGc(cdata);
        this.extAttributes = null;
        this.builder = null;
        this.cdata = cdata;
    }
    
    public long getId() {
        return this.index;
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public long getEndTime() {
        return this.endTime;
    }
    
    public long getDuration() {
        return this.endTime - this.startTime;
    }
    
    public Map<String, MemoryUsage> getMemoryUsageBeforeGc() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends MemoryUsage>)this.usageBeforeGc);
    }
    
    public Map<String, MemoryUsage> getMemoryUsageAfterGc() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends MemoryUsage>)this.usageAfterGc);
    }
    
    public static GcInfo from(final CompositeData compositeData) {
        if (compositeData == null) {
            return null;
        }
        if (compositeData instanceof GcInfoCompositeData) {
            return ((GcInfoCompositeData)compositeData).getGcInfo();
        }
        return new GcInfo(compositeData);
    }
    
    @Override
    public boolean containsKey(final String s) {
        return this.cdata.containsKey(s);
    }
    
    @Override
    public boolean containsValue(final Object o) {
        return this.cdata.containsValue(o);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this.cdata.equals(o);
    }
    
    @Override
    public Object get(final String s) {
        return this.cdata.get(s);
    }
    
    @Override
    public Object[] getAll(final String[] array) {
        return this.cdata.getAll(array);
    }
    
    @Override
    public CompositeType getCompositeType() {
        return this.cdata.getCompositeType();
    }
    
    @Override
    public int hashCode() {
        return this.cdata.hashCode();
    }
    
    @Override
    public String toString() {
        return this.cdata.toString();
    }
    
    @Override
    public Collection values() {
        return this.cdata.values();
    }
    
    @Override
    public CompositeData toCompositeData(final CompositeType compositeType) {
        return this.cdata;
    }
}
