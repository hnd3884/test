package com.unboundid.ldap.sdk.transformations;

import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;
import com.unboundid.ldif.LDIFWriterChangeRecordTranslator;
import com.unboundid.ldif.LDIFReaderChangeRecordTranslator;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public interface LDIFChangeRecordTransformation extends LDIFReaderChangeRecordTranslator, LDIFWriterChangeRecordTranslator
{
    LDIFChangeRecord transformChangeRecord(final LDIFChangeRecord p0);
}
