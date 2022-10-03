package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.BoostQuery;
import org.w3c.dom.Node;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.xml.QueryBuilder;

public class UserInputQueryBuilder implements QueryBuilder
{
    private QueryParser unSafeParser;
    private Analyzer analyzer;
    private String defaultField;
    
    public UserInputQueryBuilder(final QueryParser parser) {
        this.unSafeParser = parser;
    }
    
    public UserInputQueryBuilder(final String defaultField, final Analyzer analyzer) {
        this.analyzer = analyzer;
        this.defaultField = defaultField;
    }
    
    @Override
    public Query getQuery(final Element e) throws ParserException {
        final String text = DOMUtils.getText(e);
        try {
            Query q = null;
            if (this.unSafeParser != null) {
                synchronized (this.unSafeParser) {
                    q = this.unSafeParser.parse(text);
                }
            }
            else {
                final String fieldName = DOMUtils.getAttribute(e, "fieldName", this.defaultField);
                final QueryParser parser = this.createQueryParser(fieldName, this.analyzer);
                q = parser.parse(text);
            }
            final float boost = DOMUtils.getAttribute(e, "boost", 1.0f);
            return (Query)new BoostQuery(q, boost);
        }
        catch (final ParseException e2) {
            throw new ParserException(e2.getMessage());
        }
    }
    
    protected QueryParser createQueryParser(final String fieldName, final Analyzer analyzer) {
        return new QueryParser(fieldName, analyzer);
    }
}
