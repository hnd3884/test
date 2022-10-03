package com.unboundid.util.args;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public abstract class ArgumentValueValidator
{
    public abstract void validateArgumentValue(final Argument p0, final String p1) throws ArgumentException;
}
