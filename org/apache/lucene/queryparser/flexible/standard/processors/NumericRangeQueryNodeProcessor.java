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
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.config.NumericConfig;
import org.apache.lucene.queryparser.flexible.core.util.StringUtils;
import org.apache.lucene.queryparser.flexible.standard.nodes.TermRangeQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;

public class NumericRangeQueryNodeProcessor extends QueryNodeProcessorImpl
{
    @Override
    protected QueryNode postProcessNode(final QueryNode node) throws QueryNodeException {
        if (node instanceof TermRangeQueryNode) {
            final QueryConfigHandler config = this.getQueryConfigHandler();
            if (config != null) {
                final TermRangeQueryNode termRangeNode = (TermRangeQueryNode)node;
                final FieldConfig fieldConfig = config.getFieldConfig(StringUtils.toString(termRangeNode.getField()));
                if (fieldConfig != null) {
                    final NumericConfig numericConfig = fieldConfig.get(StandardQueryConfigHandler.ConfigurationKeys.NUMERIC_CONFIG);
                    if (numericConfig != null) {
                        final FieldQueryNode lower = termRangeNode.getLowerBound();
                        final FieldQueryNode upper = termRangeNode.getUpperBound();
                        final String lowerText = lower.getTextAsString();
                        final String upperText = upper.getTextAsString();
                        final NumberFormat numberFormat = numericConfig.getNumberFormat();
                        Number lowerNumber = null;
                        Number upperNumber = null;
                        if (lowerText.length() > 0) {
                            try {
                                lowerNumber = numberFormat.parse(lowerText);
                            }
                            catch (final ParseException e) {
                                throw new QueryNodeParseException(new MessageImpl(QueryParserMessages.COULD_NOT_PARSE_NUMBER, new Object[] { lower.getTextAsString(), numberFormat.getClass().getCanonicalName() }), e);
                            }
                        }
                        if (upperText.length() > 0) {
                            try {
                                upperNumber = numberFormat.parse(upperText);
                            }
                            catch (final ParseException e) {
                                throw new QueryNodeParseException(new MessageImpl(QueryParserMessages.COULD_NOT_PARSE_NUMBER, new Object[] { upper.getTextAsString(), numberFormat.getClass().getCanonicalName() }), e);
                            }
                        }
                        switch (numericConfig.getType()) {
                            case LONG: {
                                if (upperNumber != null) {
                                    upperNumber = upperNumber.longValue();
                                }
                                if (lowerNumber != null) {
                                    lowerNumber = lowerNumber.longValue();
                                    break;
                                }
                                break;
                            }
                            case INT: {
                                if (upperNumber != null) {
                                    upperNumber = upperNumber.intValue();
                                }
                                if (lowerNumber != null) {
                                    lowerNumber = lowerNumber.intValue();
                                    break;
                                }
                                break;
                            }
                            case DOUBLE: {
                                if (upperNumber != null) {
                                    upperNumber = upperNumber.doubleValue();
                                }
                                if (lowerNumber != null) {
                                    lowerNumber = lowerNumber.doubleValue();
                                    break;
                                }
                                break;
                            }
                            case FLOAT: {
                                if (upperNumber != null) {
                                    upperNumber = upperNumber.floatValue();
                                }
                                if (lowerNumber != null) {
                                    lowerNumber = lowerNumber.floatValue();
                                    break;
                                }
                                break;
                            }
                        }
                        final NumericQueryNode lowerNode = new NumericQueryNode(termRangeNode.getField(), lowerNumber, numberFormat);
                        final NumericQueryNode upperNode = new NumericQueryNode(termRangeNode.getField(), upperNumber, numberFormat);
                        final boolean lowerInclusive = termRangeNode.isLowerInclusive();
                        final boolean upperInclusive = termRangeNode.isUpperInclusive();
                        return new NumericRangeQueryNode(lowerNode, upperNode, lowerInclusive, upperInclusive, numericConfig);
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
