package com.unboundid.ldap.sdk.unboundidds;

import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.ReadOnlyEntry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface MoveSubtreeListener
{
    ReadOnlyEntry doPreAddProcessing(final ReadOnlyEntry p0);
    
    void doPostAddProcessing(final ReadOnlyEntry p0);
    
    void doPreDeleteProcessing(final DN p0);
    
    void doPostDeleteProcessing(final DN p0);
}
