package org.apache.lucene.queryparser.flexible.standard.processors;

import org.apache.lucene.queryparser.flexible.core.processors.RemoveDeletedQueryNodesProcessor;
import org.apache.lucene.queryparser.flexible.core.processors.NoChildOptimizationQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorPipeline;

public class StandardQueryNodeProcessorPipeline extends QueryNodeProcessorPipeline
{
    public StandardQueryNodeProcessorPipeline(final QueryConfigHandler queryConfig) {
        super(queryConfig);
        this.add((QueryNodeProcessor)new WildcardQueryNodeProcessor());
        this.add((QueryNodeProcessor)new MultiFieldQueryNodeProcessor());
        this.add((QueryNodeProcessor)new FuzzyQueryNodeProcessor());
        this.add((QueryNodeProcessor)new MatchAllDocsQueryNodeProcessor());
        this.add((QueryNodeProcessor)new OpenRangeQueryNodeProcessor());
        this.add((QueryNodeProcessor)new NumericQueryNodeProcessor());
        this.add((QueryNodeProcessor)new NumericRangeQueryNodeProcessor());
        this.add((QueryNodeProcessor)new LowercaseExpandedTermsQueryNodeProcessor());
        this.add((QueryNodeProcessor)new TermRangeQueryNodeProcessor());
        this.add((QueryNodeProcessor)new AllowLeadingWildcardProcessor());
        this.add((QueryNodeProcessor)new AnalyzerQueryNodeProcessor());
        this.add((QueryNodeProcessor)new PhraseSlopQueryNodeProcessor());
        this.add((QueryNodeProcessor)new BooleanQuery2ModifierNodeProcessor());
        this.add((QueryNodeProcessor)new NoChildOptimizationQueryNodeProcessor());
        this.add((QueryNodeProcessor)new RemoveDeletedQueryNodesProcessor());
        this.add((QueryNodeProcessor)new RemoveEmptyNonLeafQueryNodeProcessor());
        this.add((QueryNodeProcessor)new BooleanSingleChildOptimizationQueryNodeProcessor());
        this.add((QueryNodeProcessor)new DefaultPhraseSlopQueryNodeProcessor());
        this.add((QueryNodeProcessor)new BoostQueryNodeProcessor());
        this.add((QueryNodeProcessor)new MultiTermRewriteMethodProcessor());
    }
}
