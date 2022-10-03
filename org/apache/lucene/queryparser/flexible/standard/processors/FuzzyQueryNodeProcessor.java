package org.apache.lucene.queryparser.flexible.standard.processors;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.config.FuzzyConfig;
import org.apache.lucene.queryparser.flexible.core.nodes.FuzzyQueryNode;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;

public class FuzzyQueryNodeProcessor extends QueryNodeProcessorImpl
{
    @Override
    protected QueryNode postProcessNode(final QueryNode node) throws QueryNodeException {
        return node;
    }
    
    @Override
    protected QueryNode preProcessNode(final QueryNode node) throws QueryNodeException {
        if (node instanceof FuzzyQueryNode) {
            final FuzzyQueryNode fuzzyNode = (FuzzyQueryNode)node;
            final QueryConfigHandler config = this.getQueryConfigHandler();
            FuzzyConfig fuzzyConfig = null;
            if (config != null && (fuzzyConfig = config.get(StandardQueryConfigHandler.ConfigurationKeys.FUZZY_CONFIG)) != null) {
                fuzzyNode.setPrefixLength(fuzzyConfig.getPrefixLength());
                if (fuzzyNode.getSimilarity() < 0.0f) {
                    fuzzyNode.setSimilarity(fuzzyConfig.getMinSimilarity());
                }
            }
            else if (fuzzyNode.getSimilarity() < 0.0f) {
                throw new IllegalArgumentException("No FUZZY_CONFIG set in the config");
            }
        }
        return node;
    }
    
    @Override
    protected List<QueryNode> setChildrenOrder(final List<QueryNode> children) throws QueryNodeException {
        return children;
    }
}
