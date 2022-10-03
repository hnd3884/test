package com.unboundid.ldif;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public interface LDIFReaderEntryTranslator
{
    Entry translate(final Entry p0, final long p1) throws LDIFException;
}
