package org.apache.lucene.search;

public interface QueryCache
{
    Weight doCache(final Weight p0, final QueryCachingPolicy p1);
}
