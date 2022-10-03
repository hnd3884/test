package org.apache.lucene.queryparser.flexible.standard.nodes;

import org.apache.lucene.queryparser.flexible.messages.Message;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.config.NumericConfig;

public class NumericRangeQueryNode extends AbstractRangeQueryNode<NumericQueryNode>
{
    public NumericConfig numericConfig;
    
    public NumericRangeQueryNode(final NumericQueryNode lower, final NumericQueryNode upper, final boolean lowerInclusive, final boolean upperInclusive, final NumericConfig numericConfig) throws QueryNodeException {
        this.setBounds(lower, upper, lowerInclusive, upperInclusive, numericConfig);
    }
    
    private static FieldType.NumericType getNumericDataType(final Number number) throws QueryNodeException {
        if (number instanceof Long) {
            return FieldType.NumericType.LONG;
        }
        if (number instanceof Integer) {
            return FieldType.NumericType.INT;
        }
        if (number instanceof Double) {
            return FieldType.NumericType.DOUBLE;
        }
        if (number instanceof Float) {
            return FieldType.NumericType.FLOAT;
        }
        throw new QueryNodeException(new MessageImpl(QueryParserMessages.NUMBER_CLASS_NOT_SUPPORTED_BY_NUMERIC_RANGE_QUERY, new Object[] { number.getClass() }));
    }
    
    public void setBounds(final NumericQueryNode lower, final NumericQueryNode upper, final boolean lowerInclusive, final boolean upperInclusive, final NumericConfig numericConfig) throws QueryNodeException {
        if (numericConfig == null) {
            throw new IllegalArgumentException("numericConfig cannot be null!");
        }
        FieldType.NumericType lowerNumberType;
        if (lower != null && lower.getValue() != null) {
            lowerNumberType = getNumericDataType(lower.getValue());
        }
        else {
            lowerNumberType = null;
        }
        FieldType.NumericType upperNumberType;
        if (upper != null && upper.getValue() != null) {
            upperNumberType = getNumericDataType(upper.getValue());
        }
        else {
            upperNumberType = null;
        }
        if (lowerNumberType != null && !lowerNumberType.equals((Object)numericConfig.getType())) {
            throw new IllegalArgumentException("lower value's type should be the same as numericConfig type: " + lowerNumberType + " != " + numericConfig.getType());
        }
        if (upperNumberType != null && !upperNumberType.equals((Object)numericConfig.getType())) {
            throw new IllegalArgumentException("upper value's type should be the same as numericConfig type: " + upperNumberType + " != " + numericConfig.getType());
        }
        super.setBounds(lower, upper, lowerInclusive, upperInclusive);
        this.numericConfig = numericConfig;
    }
    
    public NumericConfig getNumericConfig() {
        return this.numericConfig;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("<numericRange lowerInclusive='");
        sb.append(this.isLowerInclusive()).append("' upperInclusive='").append(this.isUpperInclusive()).append("' precisionStep='" + this.numericConfig.getPrecisionStep()).append("' type='" + this.numericConfig.getType()).append("'>\n");
        sb.append(((AbstractRangeQueryNode<Object>)this).getLowerBound()).append('\n');
        sb.append(((AbstractRangeQueryNode<Object>)this).getUpperBound()).append('\n');
        sb.append("</numericRange>");
        return sb.toString();
    }
}
