package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.search.Query;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.queryparser.flexible.standard.config.NumericConfig;
import org.apache.lucene.queryparser.flexible.messages.Message;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.core.util.StringUtils;
import org.apache.lucene.queryparser.flexible.standard.nodes.NumericQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.NumericRangeQueryNode;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public class NumericRangeQueryNodeBuilder implements StandardQueryBuilder
{
    @Override
    public NumericRangeQuery<? extends Number> build(final QueryNode queryNode) throws QueryNodeException {
        final NumericRangeQueryNode numericRangeNode = (NumericRangeQueryNode)queryNode;
        final NumericQueryNode lowerNumericNode = numericRangeNode.getLowerBound();
        final NumericQueryNode upperNumericNode = numericRangeNode.getUpperBound();
        final Number lowerNumber = lowerNumericNode.getValue();
        final Number upperNumber = upperNumericNode.getValue();
        final NumericConfig numericConfig = numericRangeNode.getNumericConfig();
        final FieldType.NumericType numberType = numericConfig.getType();
        final String field = StringUtils.toString(numericRangeNode.getField());
        final boolean minInclusive = numericRangeNode.isLowerInclusive();
        final boolean maxInclusive = numericRangeNode.isUpperInclusive();
        final int precisionStep = numericConfig.getPrecisionStep();
        switch (numberType) {
            case LONG: {
                return (NumericRangeQuery<? extends Number>)NumericRangeQuery.newLongRange(field, precisionStep, (Long)lowerNumber, (Long)upperNumber, minInclusive, maxInclusive);
            }
            case INT: {
                return (NumericRangeQuery<? extends Number>)NumericRangeQuery.newIntRange(field, precisionStep, (Integer)lowerNumber, (Integer)upperNumber, minInclusive, maxInclusive);
            }
            case FLOAT: {
                return (NumericRangeQuery<? extends Number>)NumericRangeQuery.newFloatRange(field, precisionStep, (Float)lowerNumber, (Float)upperNumber, minInclusive, maxInclusive);
            }
            case DOUBLE: {
                return (NumericRangeQuery<? extends Number>)NumericRangeQuery.newDoubleRange(field, precisionStep, (Double)lowerNumber, (Double)upperNumber, minInclusive, maxInclusive);
            }
            default: {
                throw new QueryNodeException(new MessageImpl(QueryParserMessages.UNSUPPORTED_NUMERIC_DATA_TYPE, new Object[] { numberType }));
            }
        }
    }
}
