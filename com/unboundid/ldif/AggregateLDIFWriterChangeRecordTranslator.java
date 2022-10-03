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
public final class AggregateLDIFWriterChangeRecordTranslator implements LDIFWriterChangeRecordTranslator
{
    private final List<LDIFWriterChangeRecordTranslator> translators;
    
    public AggregateLDIFWriterChangeRecordTranslator(final LDIFWriterChangeRecordTranslator... translators) {
        this(StaticUtils.toList(translators));
    }
    
    public AggregateLDIFWriterChangeRecordTranslator(final Collection<? extends LDIFWriterChangeRecordTranslator> translators) {
        if (translators == null) {
            this.translators = Collections.emptyList();
        }
        else {
            this.translators = Collections.unmodifiableList((List<? extends LDIFWriterChangeRecordTranslator>)new ArrayList<LDIFWriterChangeRecordTranslator>(translators));
        }
    }
    
    @Override
    public LDIFChangeRecord translateChangeRecordToWrite(final LDIFChangeRecord original) {
        if (original == null) {
            return null;
        }
        LDIFChangeRecord r = original;
        for (final LDIFWriterChangeRecordTranslator t : this.translators) {
            r = t.translateChangeRecordToWrite(r);
            if (r == null) {
                return null;
            }
        }
        return r;
    }
}
