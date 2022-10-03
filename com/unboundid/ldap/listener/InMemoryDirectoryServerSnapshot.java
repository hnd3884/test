package com.unboundid.ldap.listener;

import java.util.Collections;
import java.util.TreeMap;
import com.unboundid.ldap.sdk.ReadOnlyEntry;
import com.unboundid.ldap.sdk.DN;
import java.util.Map;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class InMemoryDirectoryServerSnapshot implements Serializable
{
    private static final long serialVersionUID = 4691579754615787705L;
    private final long firstChangeNumber;
    private final long lastChangeNumber;
    private final Map<DN, ReadOnlyEntry> entryMap;
    
    InMemoryDirectoryServerSnapshot(final Map<DN, ReadOnlyEntry> m, final long firstChangeNumber, final long lastChangeNumber) {
        this.firstChangeNumber = firstChangeNumber;
        this.lastChangeNumber = lastChangeNumber;
        this.entryMap = Collections.unmodifiableMap((Map<? extends DN, ? extends ReadOnlyEntry>)new TreeMap<DN, ReadOnlyEntry>(m));
    }
    
    public Map<DN, ReadOnlyEntry> getEntryMap() {
        return this.entryMap;
    }
    
    public long getFirstChangeNumber() {
        return this.firstChangeNumber;
    }
    
    public long getLastChangeNumber() {
        return this.lastChangeNumber;
    }
}
