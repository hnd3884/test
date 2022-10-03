package org.apache.lucene.queryparser.flexible.standard.processors;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.config.FieldConfig;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.nodes.BoostQueryNode;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.util.StringUtils;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldableNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;

public class BoostQueryNodeProcessor extends QueryNodeProcessorImpl
{
    @Override
    protected QueryNode postProcessNode(final QueryNode node) throws QueryNodeException {
        if (node instanceof FieldableNode && (node.getParent() == null || !(node.getParent() instanceof FieldableNode))) {
            final FieldableNode fieldNode = (FieldableNode)node;
            final QueryConfigHandler config = this.getQueryConfigHandler();
            if (config != null) {
                final CharSequence field = fieldNode.getField();
                final FieldConfig fieldConfig = config.getFieldConfig(StringUtils.toString(field));
                if (fieldConfig != null) {
                    final Float boost = fieldConfig.get(StandardQueryConfigHandler.ConfigurationKeys.BOOST);
                    if (boost != null) {
                        return new BoostQueryNode(node, boost);
                    }
                }
            }
        }
        return node;
    }
    
    @Override
    protected QueryNode preProcessNode(final QueryNode node) throws QueryNodeException {
        return node;
    }
    
    @Override
    protected List<QueryNode> setChildrenOrder(final List<QueryNode> children) throws QueryNodeException {
        return children;
    }
}
