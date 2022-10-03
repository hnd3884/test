package mssql.googlecode.concurrentlinkedhashmap;

public interface Weigher<V>
{
    int weightOf(final V p0);
}
