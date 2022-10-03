package org.apache.lucene.queryparser.xml;

import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.apache.lucene.search.Query;
import java.io.InputStream;
import org.apache.lucene.queryparser.xml.builders.SpanNotBuilder;
import org.apache.lucene.queryparser.xml.builders.SpanFirstBuilder;
import org.apache.lucene.queryparser.xml.builders.SpanOrTermsBuilder;
import org.apache.lucene.queryparser.xml.builders.SpanOrBuilder;
import org.apache.lucene.queryparser.xml.builders.SpanTermBuilder;
import org.apache.lucene.queryparser.xml.builders.BoostingTermBuilder;
import org.apache.lucene.queryparser.xml.builders.SpanQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.SpanNearBuilder;
import org.apache.lucene.queryparser.xml.builders.SpanQueryBuilderFactory;
import org.apache.lucene.queryparser.xml.builders.CachedFilterBuilder;
import org.apache.lucene.queryparser.xml.builders.ConstantScoreQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.FilteredQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.UserInputQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.DisjunctionMaxQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.RangeQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.NumericRangeQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.MatchAllDocsQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.TermsQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.TermQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.NumericRangeFilterBuilder;
import org.apache.lucene.queryparser.xml.builders.RangeFilterBuilder;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.analysis.Analyzer;

public class CoreParser implements QueryBuilder
{
    protected Analyzer analyzer;
    protected QueryParser parser;
    protected QueryBuilderFactory queryFactory;
    protected FilterBuilderFactory filterFactory;
    public static int maxNumCachedFilters;
    
    public CoreParser(final Analyzer analyzer, final QueryParser parser) {
        this(null, analyzer, parser);
    }
    
    public CoreParser(final String defaultField, final Analyzer analyzer) {
        this(defaultField, analyzer, null);
    }
    
    protected CoreParser(final String defaultField, final Analyzer analyzer, final QueryParser parser) {
        this.analyzer = analyzer;
        this.parser = parser;
        (this.filterFactory = new FilterBuilderFactory()).addBuilder("RangeFilter", new RangeFilterBuilder());
        this.filterFactory.addBuilder("NumericRangeFilter", new NumericRangeFilterBuilder());
        (this.queryFactory = new QueryBuilderFactory()).addBuilder("TermQuery", new TermQueryBuilder());
        this.queryFactory.addBuilder("TermsQuery", new TermsQueryBuilder(analyzer));
        this.queryFactory.addBuilder("MatchAllDocsQuery", new MatchAllDocsQueryBuilder());
        this.queryFactory.addBuilder("BooleanQuery", new BooleanQueryBuilder(this.queryFactory));
        this.queryFactory.addBuilder("NumericRangeQuery", new NumericRangeQueryBuilder());
        this.queryFactory.addBuilder("RangeQuery", new RangeQueryBuilder());
        this.queryFactory.addBuilder("DisjunctionMaxQuery", new DisjunctionMaxQueryBuilder(this.queryFactory));
        if (parser != null) {
            this.queryFactory.addBuilder("UserQuery", new UserInputQueryBuilder(parser));
        }
        else {
            this.queryFactory.addBuilder("UserQuery", new UserInputQueryBuilder(defaultField, analyzer));
        }
        this.queryFactory.addBuilder("FilteredQuery", new FilteredQueryBuilder(this.filterFactory, this.queryFactory));
        this.queryFactory.addBuilder("ConstantScoreQuery", new ConstantScoreQueryBuilder(this.queryFactory));
        this.filterFactory.addBuilder("CachedFilter", new CachedFilterBuilder(this.queryFactory, this.filterFactory, CoreParser.maxNumCachedFilters));
        final SpanQueryBuilderFactory sqof = new SpanQueryBuilderFactory();
        final SpanNearBuilder snb = new SpanNearBuilder(sqof);
        sqof.addBuilder("SpanNear", snb);
        this.queryFactory.addBuilder("SpanNear", snb);
        final BoostingTermBuilder btb = new BoostingTermBuilder();
        sqof.addBuilder("BoostingTermQuery", btb);
        this.queryFactory.addBuilder("BoostingTermQuery", btb);
        final SpanTermBuilder snt = new SpanTermBuilder();
        sqof.addBuilder("SpanTerm", snt);
        this.queryFactory.addBuilder("SpanTerm", snt);
        final SpanOrBuilder sot = new SpanOrBuilder(sqof);
        sqof.addBuilder("SpanOr", sot);
        this.queryFactory.addBuilder("SpanOr", sot);
        final SpanOrTermsBuilder sots = new SpanOrTermsBuilder(analyzer);
        sqof.addBuilder("SpanOrTerms", sots);
        this.queryFactory.addBuilder("SpanOrTerms", sots);
        final SpanFirstBuilder sft = new SpanFirstBuilder(sqof);
        sqof.addBuilder("SpanFirst", sft);
        this.queryFactory.addBuilder("SpanFirst", sft);
        final SpanNotBuilder snot = new SpanNotBuilder(sqof);
        sqof.addBuilder("SpanNot", snot);
        this.queryFactory.addBuilder("SpanNot", snot);
    }
    
    public Query parse(final InputStream xmlStream) throws ParserException {
        return this.getQuery(parseXML(xmlStream).getDocumentElement());
    }
    
    public void addQueryBuilder(final String nodeName, final QueryBuilder builder) {
        this.queryFactory.addBuilder(nodeName, builder);
    }
    
    public void addFilterBuilder(final String nodeName, final FilterBuilder builder) {
        this.filterFactory.addBuilder(nodeName, builder);
    }
    
    private static Document parseXML(final InputStream pXmlFile) throws ParserException {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        }
        catch (final Exception se) {
            throw new ParserException("XML Parser configuration error", se);
        }
        Document doc = null;
        try {
            doc = db.parse(pXmlFile);
        }
        catch (final Exception se2) {
            throw new ParserException("Error parsing XML stream:" + se2, se2);
        }
        return doc;
    }
    
    @Override
    public Query getQuery(final Element e) throws ParserException {
        return this.queryFactory.getQuery(e);
    }
    
    static {
        CoreParser.maxNumCachedFilters = 20;
    }
}
