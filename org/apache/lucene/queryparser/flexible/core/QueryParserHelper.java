package org.apache.lucene.queryparser.flexible.core;

import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.builders.QueryBuilder;
import org.apache.lucene.queryparser.flexible.core.parser.SyntaxParser;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessor;

public class QueryParserHelper
{
    private QueryNodeProcessor processor;
    private SyntaxParser syntaxParser;
    private QueryBuilder builder;
    private QueryConfigHandler config;
    
    public QueryParserHelper(final QueryConfigHandler queryConfigHandler, final SyntaxParser syntaxParser, final QueryNodeProcessor processor, final QueryBuilder builder) {
        this.syntaxParser = syntaxParser;
        this.config = queryConfigHandler;
        this.processor = processor;
        this.builder = builder;
        if (processor != null) {
            processor.setQueryConfigHandler(queryConfigHandler);
        }
    }
    
    public QueryNodeProcessor getQueryNodeProcessor() {
        return this.processor;
    }
    
    public void setQueryNodeProcessor(final QueryNodeProcessor processor) {
        (this.processor = processor).setQueryConfigHandler(this.getQueryConfigHandler());
    }
    
    public void setSyntaxParser(final SyntaxParser syntaxParser) {
        if (syntaxParser == null) {
            throw new IllegalArgumentException("textParser should not be null!");
        }
        this.syntaxParser = syntaxParser;
    }
    
    public void setQueryBuilder(final QueryBuilder queryBuilder) {
        if (queryBuilder == null) {
            throw new IllegalArgumentException("queryBuilder should not be null!");
        }
        this.builder = queryBuilder;
    }
    
    public QueryConfigHandler getQueryConfigHandler() {
        return this.config;
    }
    
    public QueryBuilder getQueryBuilder() {
        return this.builder;
    }
    
    public SyntaxParser getSyntaxParser() {
        return this.syntaxParser;
    }
    
    public void setQueryConfigHandler(final QueryConfigHandler config) {
        this.config = config;
        final QueryNodeProcessor processor = this.getQueryNodeProcessor();
        if (processor != null) {
            processor.setQueryConfigHandler(config);
        }
    }
    
    public Object parse(final String query, final String defaultField) throws QueryNodeException {
        QueryNode queryTree = this.getSyntaxParser().parse(query, defaultField);
        final QueryNodeProcessor processor = this.getQueryNodeProcessor();
        if (processor != null) {
            queryTree = processor.process(queryTree);
        }
        return this.getQueryBuilder().build(queryTree);
    }
}
