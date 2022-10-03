package com.unboundid.ldif;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public interface LDIFReaderChangeRecordTranslator
{
    LDIFChangeRecord translate(final LDIFChangeRecord p0, final long p1) throws LDIFException;
}
