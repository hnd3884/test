package io.opencensus.trace;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import io.opencensus.internal.Utils;
import java.util.List;
import javax.annotation.Nullable;
import java.util.Iterator;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class Tracestate
{
    private static final int KEY_MAX_SIZE = 256;
    private static final int VALUE_MAX_SIZE = 256;
    private static final int MAX_KEY_VALUE_PAIRS = 32;
    
    @Nullable
    public String get(final String key) {
        for (final Entry entry : this.getEntries()) {
            if (entry.getKey().equals(key)) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    public abstract List<Entry> getEntries();
    
    public static Builder builder() {
        return new Builder(Builder.EMPTY);
    }
    
    public Builder toBuilder() {
        return new Builder(this);
    }
    
    private static boolean validateKey(final String key) {
        if (key.length() > 256 || key.isEmpty() || key.charAt(0) < 'a' || key.charAt(0) > 'z') {
            return false;
        }
        for (int i = 1; i < key.length(); ++i) {
            final char c = key.charAt(i);
            if ((c < 'a' || c > 'z') && (c < '0' || c > '9') && c != '_' && c != '-' && c != '*' && c != '/') {
                return false;
            }
        }
        return true;
    }
    
    private static boolean validateValue(final String value) {
        if (value.length() > 256 || value.charAt(value.length() - 1) == ' ') {
            return false;
        }
        for (int i = 0; i < value.length(); ++i) {
            final char c = value.charAt(i);
            if (c == ',' || c == '=' || c < ' ' || c > '~') {
                return false;
            }
        }
        return true;
    }
    
    private static Tracestate create(final List<Entry> entries) {
        Utils.checkState(entries.size() <= 32, "Invalid size");
        return new AutoValue_Tracestate(Collections.unmodifiableList((List<? extends Entry>)entries));
    }
    
    Tracestate() {
    }
    
    public static final class Builder
    {
        private final Tracestate parent;
        @Nullable
        private ArrayList<Entry> entries;
        private static final Tracestate EMPTY;
        
        private Builder(final Tracestate parent) {
            Utils.checkNotNull(parent, "parent");
            this.parent = parent;
            this.entries = null;
        }
        
        public Builder set(final String key, final String value) {
            final Entry entry = Entry.create(key, value);
            if (this.entries == null) {
                this.entries = new ArrayList<Entry>(this.parent.getEntries());
            }
            for (int i = 0; i < this.entries.size(); ++i) {
                if (this.entries.get(i).getKey().equals(entry.getKey())) {
                    this.entries.remove(i);
                    break;
                }
            }
            this.entries.add(0, entry);
            return this;
        }
        
        public Builder remove(final String key) {
            Utils.checkNotNull(key, "key");
            if (this.entries == null) {
                this.entries = new ArrayList<Entry>(this.parent.getEntries());
            }
            for (int i = 0; i < this.entries.size(); ++i) {
                if (this.entries.get(i).getKey().equals(key)) {
                    this.entries.remove(i);
                    break;
                }
            }
            return this;
        }
        
        public Tracestate build() {
            if (this.entries == null) {
                return this.parent;
            }
            return create(this.entries);
        }
        
        static {
            EMPTY = create(Collections.emptyList());
        }
    }
    
    @Immutable
    public abstract static class Entry
    {
        public static Entry create(final String key, final String value) {
            Utils.checkNotNull(key, "key");
            Utils.checkNotNull(value, "value");
            Utils.checkArgument(validateKey(key), "Invalid key %s", key);
            Utils.checkArgument(validateValue(value), "Invalid value %s", value);
            return new AutoValue_Tracestate_Entry(key, value);
        }
        
        public abstract String getKey();
        
        public abstract String getValue();
        
        Entry() {
        }
    }
}
