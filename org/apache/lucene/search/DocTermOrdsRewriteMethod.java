package org.apache.lucene.search;

import org.apache.lucene.index.IndexReader;

@Deprecated
public final class DocTermOrdsRewriteMethod extends MultiTermQuery.RewriteMethod
{
    private final DocValuesRewriteMethod rewriteMethod;
    
    public DocTermOrdsRewriteMethod() {
        this.rewriteMethod = new DocValuesRewriteMethod();
    }
    
    @Override
    public Query rewrite(final IndexReader reader, final MultiTermQuery query) {
        return this.rewriteMethod.rewrite(reader, query);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj != null && this.getClass() == obj.getClass());
    }
    
    @Override
    public int hashCode() {
        return 877;
    }
}
