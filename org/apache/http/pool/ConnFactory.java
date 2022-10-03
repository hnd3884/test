package org.apache.http.pool;

import java.io.IOException;

public interface ConnFactory<T, C>
{
    C create(final T p0) throws IOException;
}
