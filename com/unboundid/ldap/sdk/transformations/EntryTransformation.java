package com.unboundid.ldap.sdk.transformations;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;
import com.unboundid.ldif.LDIFWriterEntryTranslator;
import com.unboundid.ldif.LDIFReaderEntryTranslator;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public interface EntryTransformation extends LDIFReaderEntryTranslator, LDIFWriterEntryTranslator
{
    Entry transformEntry(final Entry p0);
}
