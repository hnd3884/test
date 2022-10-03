package org.apache.lucene.queryparser.flexible.standard.processors;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.nodes.SlopQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.MultiPhraseQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.TokenizedPhraseQueryNode;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;

public class DefaultPhraseSlopQueryNodeProcessor extends QueryNodeProcessorImpl
{
    private boolean processChildren;
    private int defaultPhraseSlop;
    
    public DefaultPhraseSlopQueryNodeProcessor() {
        this.processChildren = true;
    }
    
    @Override
    public QueryNode process(final QueryNode queryTree) throws QueryNodeException {
        final QueryConfigHandler queryConfig = this.getQueryConfigHandler();
        if (queryConfig != null) {
            final Integer defaultPhraseSlop = queryConfig.get(StandardQueryConfigHandler.ConfigurationKeys.PHRASE_SLOP);
            if (defaultPhraseSlop != null) {
                this.defaultPhraseSlop = defaultPhraseSlop;
                return super.process(queryTree);
            }
        }
        return queryTree;
    }
    
    @Override
    protected QueryNode postProcessNode(final QueryNode node) throws QueryNodeException {
        if (node instanceof TokenizedPhraseQueryNode || node instanceof MultiPhraseQueryNode) {
            return new SlopQueryNode(node, this.defaultPhraseSlop);
        }
        return node;
    }
    
    @Override
    protected QueryNode preProcessNode(final QueryNode node) throws QueryNodeException {
        if (node instanceof SlopQueryNode) {
            this.processChildren = false;
        }
        return node;
    }
    
    @Override
    protected void processChildren(final QueryNode queryTree) throws QueryNodeException {
        if (this.processChildren) {
            super.processChildren(queryTree);
        }
        else {
            this.processChildren = true;
        }
    }
    
    @Override
    protected List<QueryNode> setChildrenOrder(final List<QueryNode> children) throws QueryNodeException {
        return children;
    }
}
