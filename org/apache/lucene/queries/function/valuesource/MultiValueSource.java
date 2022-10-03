package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.queries.function.ValueSource;

public abstract class MultiValueSource extends ValueSource
{
    public abstract int dimension();
}
