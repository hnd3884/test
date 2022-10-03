package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.w3c.dom.NodeList;
import org.apache.lucene.search.BoostQuery;
import org.w3c.dom.Node;
import org.apache.lucene.sandbox.queries.FuzzyLikeThisQuery;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.xml.QueryBuilder;

public class FuzzyLikeThisQueryBuilder implements QueryBuilder
{
    private static final int DEFAULT_MAX_NUM_TERMS = 50;
    private static final float DEFAULT_MIN_SIMILARITY = 2.0f;
    private static final int DEFAULT_PREFIX_LENGTH = 1;
    private static final boolean DEFAULT_IGNORE_TF = false;
    private final Analyzer analyzer;
    
    public FuzzyLikeThisQueryBuilder(final Analyzer analyzer) {
        this.analyzer = analyzer;
    }
    
    @Override
    public Query getQuery(final Element e) throws ParserException {
        final NodeList nl = e.getElementsByTagName("Field");
        final int maxNumTerms = DOMUtils.getAttribute(e, "maxNumTerms", 50);
        final FuzzyLikeThisQuery fbq = new FuzzyLikeThisQuery(maxNumTerms, this.analyzer);
        fbq.setIgnoreTF(DOMUtils.getAttribute(e, "ignoreTF", false));
        for (int i = 0; i < nl.getLength(); ++i) {
            final Element fieldElem = (Element)nl.item(i);
            final float minSimilarity = DOMUtils.getAttribute(fieldElem, "minSimilarity", 2.0f);
            final int prefixLength = DOMUtils.getAttribute(fieldElem, "prefixLength", 1);
            final String fieldName = DOMUtils.getAttributeWithInheritance(fieldElem, "fieldName");
            final String value = DOMUtils.getText(fieldElem);
            fbq.addTerms(value, fieldName, minSimilarity, prefixLength);
        }
        Query q = (Query)fbq;
        final float boost = DOMUtils.getAttribute(e, "boost", 1.0f);
        if (boost != 1.0f) {
            q = (Query)new BoostQuery((Query)fbq, boost);
        }
        return q;
    }
}
