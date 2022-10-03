package org.apache.lucene.search;

import org.apache.lucene.index.Term;

@Deprecated
public class PrefixFilter extends MultiTermQueryWrapperFilter<PrefixQuery>
{
    public PrefixFilter(final Term prefix) {
        super(new PrefixQuery(prefix));
    }
    
    public Term getPrefix() {
        return ((PrefixQuery)this.query).getPrefix();
    }
    
    @Override
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("PrefixFilter(");
        buffer.append(this.getPrefix().toString());
        buffer.append(")");
        return buffer.toString();
    }
}
