package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.flexible.core.nodes.MatchAllDocsQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.MultiPhraseQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.StandardBooleanQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.SlopQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.RegexpQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.TermRangeQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.PrefixWildcardQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.MatchNoDocsQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.TokenizedPhraseQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.WildcardQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.BoostQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.NumericRangeQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.NumericQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.FuzzyQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.builders.QueryBuilder;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.GroupQueryNode;
import org.apache.lucene.queryparser.flexible.core.builders.QueryTreeBuilder;

public class StandardQueryTreeBuilder extends QueryTreeBuilder implements StandardQueryBuilder
{
    public StandardQueryTreeBuilder() {
        this.setBuilder(GroupQueryNode.class, new GroupQueryNodeBuilder());
        this.setBuilder(FieldQueryNode.class, new FieldQueryNodeBuilder());
        this.setBuilder(BooleanQueryNode.class, new BooleanQueryNodeBuilder());
        this.setBuilder(FuzzyQueryNode.class, new FuzzyQueryNodeBuilder());
        this.setBuilder(NumericQueryNode.class, new DummyQueryNodeBuilder());
        this.setBuilder(NumericRangeQueryNode.class, new NumericRangeQueryNodeBuilder());
        this.setBuilder(BoostQueryNode.class, new BoostQueryNodeBuilder());
        this.setBuilder(ModifierQueryNode.class, new ModifierQueryNodeBuilder());
        this.setBuilder(WildcardQueryNode.class, new WildcardQueryNodeBuilder());
        this.setBuilder(TokenizedPhraseQueryNode.class, new PhraseQueryNodeBuilder());
        this.setBuilder(MatchNoDocsQueryNode.class, new MatchNoDocsQueryNodeBuilder());
        this.setBuilder(PrefixWildcardQueryNode.class, new PrefixWildcardQueryNodeBuilder());
        this.setBuilder(TermRangeQueryNode.class, new TermRangeQueryNodeBuilder());
        this.setBuilder(RegexpQueryNode.class, new RegexpQueryNodeBuilder());
        this.setBuilder(SlopQueryNode.class, new SlopQueryNodeBuilder());
        this.setBuilder(StandardBooleanQueryNode.class, new StandardBooleanQueryNodeBuilder());
        this.setBuilder(MultiPhraseQueryNode.class, new MultiPhraseQueryNodeBuilder());
        this.setBuilder(MatchAllDocsQueryNode.class, new MatchAllDocsQueryNodeBuilder());
    }
    
    @Override
    public Query build(final QueryNode queryNode) throws QueryNodeException {
        return (Query)super.build(queryNode);
    }
}
