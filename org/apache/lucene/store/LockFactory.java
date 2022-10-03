package org.apache.lucene.store;

import java.io.IOException;

public abstract class LockFactory
{
    public abstract Lock obtainLock(final Directory p0, final String p1) throws IOException;
}
