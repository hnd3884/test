package org.apache.lucene.queryparser.xml;

import org.apache.lucene.queryparser.xml.builders.BoostingQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.LikeThisQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.BooleanFilterBuilder;
import org.apache.lucene.queryparser.xml.builders.TermsFilterBuilder;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.analysis.Analyzer;

public class CorePlusQueriesParser extends CoreParser
{
    public CorePlusQueriesParser(final Analyzer analyzer, final QueryParser parser) {
        this(null, analyzer, parser);
    }
    
    public CorePlusQueriesParser(final String defaultField, final Analyzer analyzer) {
        this(defaultField, analyzer, null);
    }
    
    protected CorePlusQueriesParser(final String defaultField, final Analyzer analyzer, final QueryParser parser) {
        super(defaultField, analyzer, parser);
        this.filterFactory.addBuilder("TermsFilter", new TermsFilterBuilder(analyzer));
        this.filterFactory.addBuilder("BooleanFilter", new BooleanFilterBuilder(this.filterFactory));
        final String[] fields = { "contents" };
        this.queryFactory.addBuilder("LikeThisQuery", new LikeThisQueryBuilder(analyzer, fields));
        this.queryFactory.addBuilder("BoostingQuery", new BoostingQueryBuilder(this.queryFactory));
    }
}
