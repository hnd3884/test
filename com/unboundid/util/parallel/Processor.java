package com.unboundid.util.parallel;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public interface Processor<I, O>
{
    O process(final I p0) throws Throwable;
}
