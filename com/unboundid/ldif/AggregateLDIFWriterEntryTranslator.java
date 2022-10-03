package com.unboundid.ldif;

import java.util.Iterator;
import com.unboundid.ldap.sdk.Entry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import com.unboundid.util.StaticUtils;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AggregateLDIFWriterEntryTranslator implements LDIFWriterEntryTranslator
{
    private final List<LDIFWriterEntryTranslator> translators;
    
    public AggregateLDIFWriterEntryTranslator(final LDIFWriterEntryTranslator... translators) {
        this(StaticUtils.toList(translators));
    }
    
    public AggregateLDIFWriterEntryTranslator(final Collection<? extends LDIFWriterEntryTranslator> translators) {
        if (translators == null) {
            this.translators = Collections.emptyList();
        }
        else {
            this.translators = Collections.unmodifiableList((List<? extends LDIFWriterEntryTranslator>)new ArrayList<LDIFWriterEntryTranslator>(translators));
        }
    }
    
    @Override
    public Entry translateEntryToWrite(final Entry original) {
        if (original == null) {
            return null;
        }
        Entry e = original;
        for (final LDIFWriterEntryTranslator t : this.translators) {
            e = t.translateEntryToWrite(e);
            if (e == null) {
                return null;
            }
        }
        return e;
    }
}
