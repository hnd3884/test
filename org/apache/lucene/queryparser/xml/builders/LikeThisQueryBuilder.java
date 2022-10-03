package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.BoostQuery;
import java.util.Set;
import org.apache.lucene.queries.mlt.MoreLikeThisQuery;
import org.w3c.dom.Node;
import org.apache.lucene.queryparser.xml.DOMUtils;
import java.io.IOException;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import java.util.HashSet;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.xml.QueryBuilder;

public class LikeThisQueryBuilder implements QueryBuilder
{
    private static final int DEFAULT_MAX_QUERY_TERMS = 20;
    private static final int DEFAULT_MIN_TERM_FREQUENCY = 1;
    private static final float DEFAULT_PERCENT_TERMS_TO_MATCH = 30.0f;
    private final Analyzer analyzer;
    private final String[] defaultFieldNames;
    
    public LikeThisQueryBuilder(final Analyzer analyzer, final String[] defaultFieldNames) {
        this.analyzer = analyzer;
        this.defaultFieldNames = defaultFieldNames;
    }
    
    @Override
    public Query getQuery(final Element e) throws ParserException {
        final String fieldsList = e.getAttribute("fieldNames");
        String[] fields = this.defaultFieldNames;
        if (fieldsList != null && fieldsList.trim().length() > 0) {
            fields = fieldsList.trim().split(",");
            for (int i = 0; i < fields.length; ++i) {
                fields[i] = fields[i].trim();
            }
        }
        final String stopWords = e.getAttribute("stopWords");
        Set<String> stopWordsSet = null;
        if (stopWords != null && fields != null) {
            stopWordsSet = new HashSet<String>();
            for (final String field : fields) {
                try (final TokenStream ts = this.analyzer.tokenStream(field, stopWords)) {
                    final CharTermAttribute termAtt = (CharTermAttribute)ts.addAttribute((Class)CharTermAttribute.class);
                    ts.reset();
                    while (ts.incrementToken()) {
                        stopWordsSet.add(termAtt.toString());
                    }
                    ts.end();
                }
                catch (final IOException ioe) {
                    throw new ParserException("IoException parsing stop words list in " + this.getClass().getName() + ":" + ioe.getLocalizedMessage());
                }
            }
        }
        final MoreLikeThisQuery mlt = new MoreLikeThisQuery(DOMUtils.getText(e), fields, this.analyzer, fields[0]);
        mlt.setMaxQueryTerms(DOMUtils.getAttribute(e, "maxQueryTerms", 20));
        mlt.setMinTermFrequency(DOMUtils.getAttribute(e, "minTermFrequency", 1));
        mlt.setPercentTermsToMatch(DOMUtils.getAttribute(e, "percentTermsToMatch", 30.0f) / 100.0f);
        mlt.setStopWords((Set)stopWordsSet);
        final int minDocFreq = DOMUtils.getAttribute(e, "minDocFreq", -1);
        if (minDocFreq >= 0) {
            mlt.setMinDocFreq(minDocFreq);
        }
        Query q = (Query)mlt;
        final float boost = DOMUtils.getAttribute(e, "boost", 1.0f);
        if (boost != 1.0f) {
            q = (Query)new BoostQuery((Query)mlt, boost);
        }
        return q;
    }
}
