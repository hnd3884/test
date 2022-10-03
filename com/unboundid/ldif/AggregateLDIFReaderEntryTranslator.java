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
public final class AggregateLDIFReaderEntryTranslator implements LDIFReaderEntryTranslator
{
    private final List<LDIFReaderEntryTranslator> translators;
    
    public AggregateLDIFReaderEntryTranslator(final LDIFReaderEntryTranslator... translators) {
        this(StaticUtils.toList(translators));
    }
    
    public AggregateLDIFReaderEntryTranslator(final Collection<? extends LDIFReaderEntryTranslator> translators) {
        if (translators == null) {
            this.translators = Collections.emptyList();
        }
        else {
            this.translators = Collections.unmodifiableList((List<? extends LDIFReaderEntryTranslator>)new ArrayList<LDIFReaderEntryTranslator>(translators));
        }
    }
    
    @Override
    public Entry translate(final Entry original, final long firstLineNumber) throws LDIFException {
        if (original == null) {
            return null;
        }
        Entry e = original;
        for (final LDIFReaderEntryTranslator t : this.translators) {
            e = t.translate(e, firstLineNumber);
            if (e == null) {
                return null;
            }
        }
        return e;
    }
}
