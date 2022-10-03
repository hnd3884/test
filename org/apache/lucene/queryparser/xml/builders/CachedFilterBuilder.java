package org.apache.lucene.queryparser.xml.builders;

import java.util.Map;
import java.util.LinkedHashMap;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.QueryBuilder;
import org.apache.lucene.search.CachingWrapperQuery;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.search.Filter;
import org.w3c.dom.Element;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.xml.FilterBuilderFactory;
import org.apache.lucene.queryparser.xml.QueryBuilderFactory;
import org.apache.lucene.queryparser.xml.FilterBuilder;

@Deprecated
public class CachedFilterBuilder implements FilterBuilder
{
    private final QueryBuilderFactory queryFactory;
    private final FilterBuilderFactory filterFactory;
    private LRUCache<Object, Query> filterCache;
    private final int cacheSize;
    
    public CachedFilterBuilder(final QueryBuilderFactory queryFactory, final FilterBuilderFactory filterFactory, final int cacheSize) {
        this.queryFactory = queryFactory;
        this.filterFactory = filterFactory;
        this.cacheSize = cacheSize;
    }
    
    @Override
    public synchronized Filter getFilter(final Element e) throws ParserException {
        final Element childElement = DOMUtils.getFirstChildOrFail(e);
        if (this.filterCache == null) {
            this.filterCache = new LRUCache<Object, Query>(this.cacheSize);
        }
        final QueryBuilder qb = this.queryFactory.getQueryBuilder(childElement.getNodeName());
        Object cacheKey = null;
        Query q = null;
        Filter f = null;
        if (qb != null) {
            q = (Query)(cacheKey = qb.getQuery(childElement));
        }
        else {
            f = (Filter)(cacheKey = this.filterFactory.getFilter(childElement));
        }
        Query cachedFilter = this.filterCache.get(cacheKey);
        if (cachedFilter != null) {
            return (Filter)new QueryWrapperFilter(cachedFilter);
        }
        if (qb != null) {
            cachedFilter = (Query)new QueryWrapperFilter(q);
        }
        else {
            cachedFilter = (Query)new CachingWrapperQuery((Query)f);
        }
        this.filterCache.put(cacheKey, cachedFilter);
        return (Filter)new QueryWrapperFilter(cachedFilter);
    }
    
    static class LRUCache<K, V> extends LinkedHashMap<K, V>
    {
        protected int maxsize;
        
        public LRUCache(final int maxsize) {
            super(maxsize * 4 / 3 + 1, 0.75f, true);
            this.maxsize = maxsize;
        }
        
        @Override
        protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
            return this.size() > this.maxsize;
        }
    }
}
