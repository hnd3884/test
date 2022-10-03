package javax.management;

class BetweenQueryExp extends QueryEval implements QueryExp
{
    private static final long serialVersionUID = -2933597532866307444L;
    private ValueExp exp1;
    private ValueExp exp2;
    private ValueExp exp3;
    
    public BetweenQueryExp() {
    }
    
    public BetweenQueryExp(final ValueExp exp1, final ValueExp exp2, final ValueExp exp3) {
        this.exp1 = exp1;
        this.exp2 = exp2;
        this.exp3 = exp3;
    }
    
    public ValueExp getCheckedValue() {
        return this.exp1;
    }
    
    public ValueExp getLowerBound() {
        return this.exp2;
    }
    
    public ValueExp getUpperBound() {
        return this.exp3;
    }
    
    @Override
    public boolean apply(final ObjectName objectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
        final ValueExp apply = this.exp1.apply(objectName);
        final ValueExp apply2 = this.exp2.apply(objectName);
        final ValueExp apply3 = this.exp3.apply(objectName);
        if (!(apply instanceof NumericValueExp)) {
            final String value = ((StringValueExp)apply).getValue();
            final String value2 = ((StringValueExp)apply2).getValue();
            final String value3 = ((StringValueExp)apply3).getValue();
            return value2.compareTo(value) <= 0 && value.compareTo(value3) <= 0;
        }
        if (((NumericValueExp)apply).isLong()) {
            final long longValue = ((NumericValueExp)apply).longValue();
            final long longValue2 = ((NumericValueExp)apply2).longValue();
            final long longValue3 = ((NumericValueExp)apply3).longValue();
            return longValue2 <= longValue && longValue <= longValue3;
        }
        final double doubleValue = ((NumericValueExp)apply).doubleValue();
        final double doubleValue2 = ((NumericValueExp)apply2).doubleValue();
        final double doubleValue3 = ((NumericValueExp)apply3).doubleValue();
        return doubleValue2 <= doubleValue && doubleValue <= doubleValue3;
    }
    
    @Override
    public String toString() {
        return "(" + this.exp1 + ") between (" + this.exp2 + ") and (" + this.exp3 + ")";
    }
}
