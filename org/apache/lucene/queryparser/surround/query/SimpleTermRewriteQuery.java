package org.apache.lucene.queryparser.surround.query;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.MatchNoDocsQuery;
import java.io.IOException;
import org.apache.lucene.index.Term;
import java.util.List;
import java.util.ArrayList;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.IndexReader;

class SimpleTermRewriteQuery extends RewriteQuery<SimpleTerm>
{
    SimpleTermRewriteQuery(final SimpleTerm srndQuery, final String fieldName, final BasicQueryFactory qf) {
        super(srndQuery, fieldName, qf);
    }
    
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        final List<Query> luceneSubQueries = new ArrayList<Query>();
        ((SimpleTerm)this.srndQuery).visitMatchingTerms(reader, this.fieldName, new SimpleTerm.MatchingTermVisitor() {
            @Override
            public void visitMatchingTerm(final Term term) throws IOException {
                luceneSubQueries.add(SimpleTermRewriteQuery.this.qf.newTermQuery(term));
            }
        });
        return (Query)((luceneSubQueries.size() == 0) ? new MatchNoDocsQuery() : ((luceneSubQueries.size() == 1) ? luceneSubQueries.get(0) : SrndBooleanQuery.makeBooleanQuery(luceneSubQueries, BooleanClause.Occur.SHOULD)));
    }
}
