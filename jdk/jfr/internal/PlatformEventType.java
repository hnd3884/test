package jdk.jfr.internal;

import java.util.Iterator;
import java.util.Objects;
import java.util.ArrayList;
import jdk.jfr.SettingDescriptor;
import java.util.List;

public final class PlatformEventType extends Type
{
    private final boolean isJVM;
    private final boolean isJDK;
    private final boolean isMethodSampling;
    private final List<SettingDescriptor> settings;
    private final boolean dynamicSettings;
    private final int stackTraceOffset;
    private boolean enabled;
    private boolean stackTraceEnabled;
    private long thresholdTicks;
    private long period;
    private boolean hasHook;
    private boolean beginChunk;
    private boolean endChunk;
    private boolean hasStackTrace;
    private boolean hasDuration;
    private boolean hasPeriod;
    private boolean hasCutoff;
    private boolean isInstrumented;
    private boolean markForInstrumentation;
    private boolean registered;
    private boolean commitable;
    
    PlatformEventType(final String s, final long n, final boolean isJDK, final boolean dynamicSettings) {
        super(s, Type.SUPER_TYPE_EVENT, n);
        this.settings = new ArrayList<SettingDescriptor>(5);
        this.enabled = false;
        this.stackTraceEnabled = true;
        this.thresholdTicks = 0L;
        this.period = 0L;
        this.hasStackTrace = true;
        this.hasDuration = true;
        this.hasPeriod = true;
        this.hasCutoff = false;
        this.registered = true;
        this.commitable = (this.enabled && this.registered);
        this.dynamicSettings = dynamicSettings;
        this.isJVM = Type.isDefinedByJVM(n);
        this.isMethodSampling = (s.equals("jdk.ExecutionSample") || s.equals("jdk.NativeMethodSample"));
        this.isJDK = isJDK;
        this.stackTraceOffset = stackTraceOffset(s, isJDK);
    }
    
    private static int stackTraceOffset(final String s, final boolean b) {
        if (b) {
            if (s.equals("jdk.JavaExceptionThrow")) {
                return 5;
            }
            if (s.equals("jdk.JavaErrorThrow")) {
                return 5;
            }
        }
        return 4;
    }
    
    public void add(final SettingDescriptor settingDescriptor) {
        Objects.requireNonNull(settingDescriptor);
        this.settings.add(settingDescriptor);
    }
    
    public List<SettingDescriptor> getSettings() {
        if (this.dynamicSettings) {
            final ArrayList list = new ArrayList(this.settings.size());
            for (final SettingDescriptor settingDescriptor : this.settings) {
                if (Utils.isSettingVisible(settingDescriptor.getTypeId(), this.hasHook)) {
                    list.add(settingDescriptor);
                }
            }
            return list;
        }
        return this.settings;
    }
    
    public List<SettingDescriptor> getAllSettings() {
        return this.settings;
    }
    
    public void setHasStackTrace(final boolean hasStackTrace) {
        this.hasStackTrace = hasStackTrace;
    }
    
    public void setHasDuration(final boolean hasDuration) {
        this.hasDuration = hasDuration;
    }
    
    public void setHasCutoff(final boolean hasCutoff) {
        this.hasCutoff = hasCutoff;
    }
    
    public void setCutoff(final long n) {
        if (this.isJVM) {
            JVM.getJVM().setCutoff(this.getId(), Utils.nanosToTicks(n));
        }
    }
    
    public void setHasPeriod(final boolean hasPeriod) {
        this.hasPeriod = hasPeriod;
    }
    
    public boolean hasStackTrace() {
        return this.hasStackTrace;
    }
    
    public boolean hasDuration() {
        return this.hasDuration;
    }
    
    public boolean hasPeriod() {
        return this.hasPeriod;
    }
    
    public boolean hasCutoff() {
        return this.hasCutoff;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public boolean isJVM() {
        return this.isJVM;
    }
    
    public boolean isJDK() {
        return this.isJDK;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
        this.updateCommitable();
        if (this.isJVM) {
            if (this.isMethodSampling) {
                JVM.getJVM().setMethodSamplingInterval(this.getId(), enabled ? this.period : 0L);
            }
            else {
                JVM.getJVM().setEnabled(this.getId(), enabled);
            }
        }
    }
    
    public void setPeriod(final long period, final boolean beginChunk, final boolean endChunk) {
        if (this.isMethodSampling) {
            JVM.getJVM().setMethodSamplingInterval(this.getId(), this.enabled ? period : 0L);
        }
        this.beginChunk = beginChunk;
        this.endChunk = endChunk;
        this.period = period;
    }
    
    public void setStackTraceEnabled(final boolean stackTraceEnabled) {
        this.stackTraceEnabled = stackTraceEnabled;
        if (this.isJVM) {
            JVM.getJVM().setStackTraceEnabled(this.getId(), stackTraceEnabled);
        }
    }
    
    public void setThreshold(final long n) {
        this.thresholdTicks = Utils.nanosToTicks(n);
        if (this.isJVM) {
            JVM.getJVM().setThreshold(this.getId(), this.thresholdTicks);
        }
    }
    
    public boolean isEveryChunk() {
        return this.period == 0L;
    }
    
    public boolean getStackTraceEnabled() {
        return this.stackTraceEnabled;
    }
    
    public long getThresholdTicks() {
        return this.thresholdTicks;
    }
    
    public long getPeriod() {
        return this.period;
    }
    
    public boolean hasEventHook() {
        return this.hasHook;
    }
    
    public void setEventHook(final boolean hasHook) {
        this.hasHook = hasHook;
    }
    
    public boolean isBeginChunk() {
        return this.beginChunk;
    }
    
    public boolean isEndChunk() {
        return this.endChunk;
    }
    
    public boolean isInstrumented() {
        return this.isInstrumented;
    }
    
    public void setInstrumented() {
        this.isInstrumented = true;
    }
    
    public void markForInstrumentation(final boolean markForInstrumentation) {
        this.markForInstrumentation = markForInstrumentation;
    }
    
    public boolean isMarkedForInstrumentation() {
        return this.markForInstrumentation;
    }
    
    public boolean setRegistered(final boolean registered) {
        if (this.registered != registered) {
            this.registered = registered;
            this.updateCommitable();
            final LogTag logTag = (this.isJVM() || this.isJDK()) ? LogTag.JFR_SYSTEM_EVENT : LogTag.JFR_EVENT;
            if (registered) {
                Logger.log(logTag, LogLevel.INFO, "Registered " + this.getLogName());
            }
            else {
                Logger.log(logTag, LogLevel.INFO, "Unregistered " + this.getLogName());
            }
            if (!registered) {
                MetadataRepository.getInstance().setUnregistered();
            }
            return true;
        }
        return false;
    }
    
    private void updateCommitable() {
        this.commitable = (this.enabled && this.registered);
    }
    
    public final boolean isRegistered() {
        return this.registered;
    }
    
    public boolean isCommitable() {
        return this.commitable;
    }
    
    public int getStackTraceOffset() {
        return this.stackTraceOffset;
    }
}
