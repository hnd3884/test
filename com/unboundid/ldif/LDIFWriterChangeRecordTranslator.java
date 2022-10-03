package com.unboundid.ldif;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public interface LDIFWriterChangeRecordTranslator
{
    LDIFChangeRecord translateChangeRecordToWrite(final LDIFChangeRecord p0);
}
