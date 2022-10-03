package com.unboundid.ldap.sdk.transformations;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ExcludeAllEntriesTransformation implements EntryTransformation, Serializable
{
    private static final long serialVersionUID = 8203086326365856962L;
    
    @Override
    public Entry transformEntry(final Entry entry) {
        return null;
    }
    
    @Override
    public Entry translate(final Entry original, final long firstLineNumber) {
        return null;
    }
    
    @Override
    public Entry translateEntryToWrite(final Entry original) {
        return null;
    }
}
