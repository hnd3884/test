package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.BoostQuery;
import java.io.IOException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.xml.QueryBuilder;

public class TermsQueryBuilder implements QueryBuilder
{
    private final Analyzer analyzer;
    
    public TermsQueryBuilder(final Analyzer analyzer) {
        this.analyzer = analyzer;
    }
    
    @Override
    public Query getQuery(final Element e) throws ParserException {
        final String fieldName = DOMUtils.getAttributeWithInheritanceOrFail(e, "fieldName");
        final String text = DOMUtils.getNonBlankTextOrFail(e);
        final BooleanQuery.Builder bq = new BooleanQuery.Builder();
        bq.setDisableCoord(DOMUtils.getAttribute(e, "disableCoord", false));
        bq.setMinimumNumberShouldMatch(DOMUtils.getAttribute(e, "minimumNumberShouldMatch", 0));
        try (final TokenStream ts = this.analyzer.tokenStream(fieldName, text)) {
            final TermToBytesRefAttribute termAtt = (TermToBytesRefAttribute)ts.addAttribute((Class)TermToBytesRefAttribute.class);
            Term term = null;
            ts.reset();
            while (ts.incrementToken()) {
                term = new Term(fieldName, BytesRef.deepCopyOf(termAtt.getBytesRef()));
                bq.add(new BooleanClause((Query)new TermQuery(term), BooleanClause.Occur.SHOULD));
            }
            ts.end();
        }
        catch (final IOException ioe) {
            throw new RuntimeException("Error constructing terms from index:" + ioe);
        }
        final Query q = (Query)bq.build();
        final float boost = DOMUtils.getAttribute(e, "boost", 1.0f);
        return (Query)new BoostQuery(q, boost);
    }
}
