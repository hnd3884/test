package jdk.jfr.internal;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;
import java.security.AccessControlContext;

public abstract class Control
{
    private final AccessControlContext context;
    private static final int CACHE_SIZE = 5;
    private final Set<?>[] cachedUnions;
    private final String[] cachedValues;
    private String defaultValue;
    private String lastValue;
    
    public Control(final AccessControlContext context) {
        this.cachedUnions = new HashSet[5];
        this.cachedValues = new String[5];
        Objects.requireNonNull(context);
        this.context = context;
    }
    
    public Control(final String defaultValue) {
        this.cachedUnions = new HashSet[5];
        this.cachedValues = new String[5];
        this.defaultValue = defaultValue;
        this.context = null;
    }
    
    public abstract String combine(final Set<String> p0);
    
    public abstract void setValue(final String p0);
    
    public abstract String getValue();
    
    final void apply(final Set<String> set) {
        this.setValueSafe(this.findCombineSafe(set));
    }
    
    final void setDefault() {
        if (this.defaultValue == null) {
            this.defaultValue = this.getValueSafe();
        }
        this.apply(this.defaultValue);
    }
    
    final String getValueSafe() {
        if (this.context == null) {
            return this.getValue();
        }
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                try {
                    return Control.this.getValue();
                }
                catch (final Throwable t) {
                    Logger.log(LogTag.JFR_SETTING, LogLevel.WARN, "Exception occured when trying to get value for " + this.getClass());
                    return (Control.this.defaultValue != null) ? Control.this.defaultValue : "";
                }
            }
        }, this.context);
    }
    
    private void apply(final String valueSafe) {
        if (this.lastValue != null && Objects.equals(valueSafe, this.lastValue)) {
            return;
        }
        this.setValueSafe(valueSafe);
    }
    
    final void setValueSafe(final String s) {
        if (this.context == null) {
            try {
                this.setValue(s);
            }
            catch (final Throwable t) {
                Logger.log(LogTag.JFR_SETTING, LogLevel.WARN, "Exception occured when setting value \"" + s + "\" for " + this.getClass());
            }
        }
        else {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    try {
                        Control.this.setValue(s);
                    }
                    catch (final Throwable t) {
                        Logger.log(LogTag.JFR_SETTING, LogLevel.WARN, "Exception occured when setting value \"" + s + "\" for " + this.getClass());
                    }
                    return null;
                }
            }, this.context);
        }
        this.lastValue = s;
    }
    
    private String combineSafe(final Set<String> set) {
        if (this.context == null) {
            return this.combine(set);
        }
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                try {
                    Control.this.combine(Collections.unmodifiableSet((Set<? extends String>)set));
                }
                catch (final Throwable t) {
                    Logger.log(LogTag.JFR_SETTING, LogLevel.WARN, "Exception occured when combining " + set + " for " + this.getClass());
                }
                return null;
            }
        }, this.context);
    }
    
    private final String findCombineSafe(final Set<String> set) {
        if (set.size() == 1) {
            return set.iterator().next();
        }
        for (int i = 0; i < 5; ++i) {
            if (Objects.equals(this.cachedUnions[i], set)) {
                return this.cachedValues[i];
            }
        }
        final String combineSafe = this.combineSafe(set);
        for (int j = 0; j < 4; ++j) {
            this.cachedUnions[j + 1] = this.cachedUnions[j];
            this.cachedValues[j + 1] = this.cachedValues[j];
        }
        this.cachedValues[0] = combineSafe;
        this.cachedUnions[0] = set;
        return combineSafe;
    }
    
    final String getDefaultValue() {
        return this.defaultValue;
    }
    
    final String getLastValue() {
        return this.lastValue;
    }
    
    public final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    
    private final void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        throw new IOException("Object cannot be serialized");
    }
    
    private final void readObject(final ObjectInputStream objectInputStream) throws IOException {
        throw new IOException("Class cannot be deserialized");
    }
}
