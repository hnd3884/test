package com.unboundid.ldif;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import com.unboundid.util.StaticUtils;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AggregateLDIFReaderChangeRecordTranslator implements LDIFReaderChangeRecordTranslator
{
    private final List<LDIFReaderChangeRecordTranslator> translators;
    
    public AggregateLDIFReaderChangeRecordTranslator(final LDIFReaderChangeRecordTranslator... translators) {
        this(StaticUtils.toList(translators));
    }
    
    public AggregateLDIFReaderChangeRecordTranslator(final Collection<? extends LDIFReaderChangeRecordTranslator> translators) {
        if (translators == null) {
            this.translators = Collections.emptyList();
        }
        else {
            this.translators = Collections.unmodifiableList((List<? extends LDIFReaderChangeRecordTranslator>)new ArrayList<LDIFReaderChangeRecordTranslator>(translators));
        }
    }
    
    @Override
    public LDIFChangeRecord translate(final LDIFChangeRecord original, final long firstLineNumber) throws LDIFException {
        if (original == null) {
            return null;
        }
        LDIFChangeRecord r = original;
        for (final LDIFReaderChangeRecordTranslator t : this.translators) {
            r = t.translate(r, firstLineNumber);
            if (r == null) {
                return null;
            }
        }
        return r;
    }
}
