package org.apache.lucene.queryparser.xml;

import org.apache.lucene.queryparser.xml.builders.FuzzyLikeThisQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.DuplicateFilterBuilder;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.analysis.Analyzer;

public class CorePlusExtensionsParser extends CorePlusQueriesParser
{
    public CorePlusExtensionsParser(final Analyzer analyzer, final QueryParser parser) {
        this(null, analyzer, parser);
    }
    
    public CorePlusExtensionsParser(final String defaultField, final Analyzer analyzer) {
        this(defaultField, analyzer, null);
    }
    
    private CorePlusExtensionsParser(final String defaultField, final Analyzer analyzer, final QueryParser parser) {
        super(defaultField, analyzer, parser);
        this.filterFactory.addBuilder("DuplicateFilter", new DuplicateFilterBuilder());
        this.queryFactory.addBuilder("FuzzyLikeThisQuery", new FuzzyLikeThisQueryBuilder(analyzer));
    }
}
