package javax.management;

class BinaryRelQueryExp extends QueryEval implements QueryExp
{
    private static final long serialVersionUID = -5690656271650491000L;
    private int relOp;
    private ValueExp exp1;
    private ValueExp exp2;
    
    public BinaryRelQueryExp() {
    }
    
    public BinaryRelQueryExp(final int relOp, final ValueExp exp1, final ValueExp exp2) {
        this.relOp = relOp;
        this.exp1 = exp1;
        this.exp2 = exp2;
    }
    
    public int getOperator() {
        return this.relOp;
    }
    
    public ValueExp getLeftValue() {
        return this.exp1;
    }
    
    public ValueExp getRightValue() {
        return this.exp2;
    }
    
    @Override
    public boolean apply(final ObjectName objectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
        final ValueExp apply = this.exp1.apply(objectName);
        final ValueExp apply2 = this.exp2.apply(objectName);
        final boolean b = apply instanceof NumericValueExp;
        final boolean b2 = apply instanceof BooleanValueExp;
        if (b) {
            if (((NumericValueExp)apply).isLong()) {
                final long longValue = ((NumericValueExp)apply).longValue();
                final long longValue2 = ((NumericValueExp)apply2).longValue();
                switch (this.relOp) {
                    case 0: {
                        return longValue > longValue2;
                    }
                    case 1: {
                        return longValue < longValue2;
                    }
                    case 2: {
                        return longValue >= longValue2;
                    }
                    case 3: {
                        return longValue <= longValue2;
                    }
                    case 4: {
                        return longValue == longValue2;
                    }
                }
            }
            else {
                final double doubleValue = ((NumericValueExp)apply).doubleValue();
                final double doubleValue2 = ((NumericValueExp)apply2).doubleValue();
                switch (this.relOp) {
                    case 0: {
                        return doubleValue > doubleValue2;
                    }
                    case 1: {
                        return doubleValue < doubleValue2;
                    }
                    case 2: {
                        return doubleValue >= doubleValue2;
                    }
                    case 3: {
                        return doubleValue <= doubleValue2;
                    }
                    case 4: {
                        return doubleValue == doubleValue2;
                    }
                }
            }
        }
        else if (b2) {
            final boolean booleanValue = ((BooleanValueExp)apply).getValue();
            final boolean booleanValue2 = ((BooleanValueExp)apply2).getValue();
            switch (this.relOp) {
                case 0: {
                    return booleanValue && !booleanValue2;
                }
                case 1: {
                    return !booleanValue && booleanValue2;
                }
                case 2: {
                    return booleanValue || !booleanValue2;
                }
                case 3: {
                    return !booleanValue || booleanValue2;
                }
                case 4: {
                    return booleanValue == booleanValue2;
                }
            }
        }
        else {
            final String value = ((StringValueExp)apply).getValue();
            final String value2 = ((StringValueExp)apply2).getValue();
            switch (this.relOp) {
                case 0: {
                    return value.compareTo(value2) > 0;
                }
                case 1: {
                    return value.compareTo(value2) < 0;
                }
                case 2: {
                    return value.compareTo(value2) >= 0;
                }
                case 3: {
                    return value.compareTo(value2) <= 0;
                }
                case 4: {
                    return value.compareTo(value2) == 0;
                }
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "(" + this.exp1 + ") " + this.relOpString() + " (" + this.exp2 + ")";
    }
    
    private String relOpString() {
        switch (this.relOp) {
            case 0: {
                return ">";
            }
            case 1: {
                return "<";
            }
            case 2: {
                return ">=";
            }
            case 3: {
                return "<=";
            }
            case 4: {
                return "=";
            }
            default: {
                return "=";
            }
        }
    }
}
