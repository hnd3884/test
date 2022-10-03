package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.IntermediateResponse;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface ChangelogEntryListener
{
    void handleChangelogEntry(final ChangelogEntryIntermediateResponse p0);
    
    void handleMissingChangelogEntries(final MissingChangelogEntriesIntermediateResponse p0);
    
    void handleOtherIntermediateResponse(final IntermediateResponse p0);
}
