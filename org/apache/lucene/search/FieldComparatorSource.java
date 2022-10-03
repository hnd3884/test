package org.apache.lucene.search;

import java.io.IOException;

public abstract class FieldComparatorSource
{
    public abstract FieldComparator<?> newComparator(final String p0, final int p1, final int p2, final boolean p3) throws IOException;
}
