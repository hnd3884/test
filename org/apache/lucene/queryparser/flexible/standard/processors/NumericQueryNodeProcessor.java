package org.apache.lucene.queryparser.flexible.standard.processors;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import java.text.NumberFormat;
import org.apache.lucene.queryparser.flexible.core.config.FieldConfig;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.nodes.NumericRangeQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.NumericQueryNode;
import java.text.ParseException;
import org.apache.lucene.queryparser.flexible.messages.Message;
import org.apache.lucene.queryparser.flexible.core.QueryNodeParseException;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.config.NumericConfig;
import org.apache.lucene.queryparser.flexible.core.nodes.RangeQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;

public class NumericQueryNodeProcessor extends QueryNodeProcessorImpl
{
    @Override
    protected QueryNode postProcessNode(final QueryNode node) throws QueryNodeException {
        if (node instanceof FieldQueryNode && !(node.getParent() instanceof RangeQueryNode)) {
            final QueryConfigHandler config = this.getQueryConfigHandler();
            if (config != null) {
                final FieldQueryNode fieldNode = (FieldQueryNode)node;
                final FieldConfig fieldConfig = config.getFieldConfig(fieldNode.getFieldAsString());
                if (fieldConfig != null) {
                    final NumericConfig numericConfig = fieldConfig.get(StandardQueryConfigHandler.ConfigurationKeys.NUMERIC_CONFIG);
                    if (numericConfig != null) {
                        final NumberFormat numberFormat = numericConfig.getNumberFormat();
                        final String text = fieldNode.getTextAsString();
                        Number number = null;
                        if (text.length() > 0) {
                            try {
                                number = numberFormat.parse(text);
                            }
                            catch (final ParseException e) {
                                throw new QueryNodeParseException(new MessageImpl(QueryParserMessages.COULD_NOT_PARSE_NUMBER, new Object[] { fieldNode.getTextAsString(), numberFormat.getClass().getCanonicalName() }), e);
                            }
                            switch (numericConfig.getType()) {
                                case LONG: {
                                    number = number.longValue();
                                    break;
                                }
                                case INT: {
                                    number = number.intValue();
                                    break;
                                }
                                case DOUBLE: {
                                    number = number.doubleValue();
                                    break;
                                }
                                case FLOAT: {
                                    number = number.floatValue();
                                    break;
                                }
                            }
                            final NumericQueryNode lowerNode = new NumericQueryNode(fieldNode.getField(), number, numberFormat);
                            final NumericQueryNode upperNode = new NumericQueryNode(fieldNode.getField(), number, numberFormat);
                            return new NumericRangeQueryNode(lowerNode, upperNode, true, true, numericConfig);
                        }
                        throw new QueryNodeParseException(new MessageImpl(QueryParserMessages.NUMERIC_CANNOT_BE_EMPTY, new Object[] { fieldNode.getFieldAsString() }));
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
