package org.apache.lucene.search;

@Deprecated
public interface FilterCache
{
    Filter doCache(final Filter p0, final FilterCachingPolicy p1);
}
