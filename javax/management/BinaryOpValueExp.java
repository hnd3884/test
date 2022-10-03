package javax.management;

class BinaryOpValueExp extends QueryEval implements ValueExp
{
    private static final long serialVersionUID = 1216286847881456786L;
    private int op;
    private ValueExp exp1;
    private ValueExp exp2;
    
    public BinaryOpValueExp() {
    }
    
    public BinaryOpValueExp(final int op, final ValueExp exp1, final ValueExp exp2) {
        this.op = op;
        this.exp1 = exp1;
        this.exp2 = exp2;
    }
    
    public int getOperator() {
        return this.op;
    }
    
    public ValueExp getLeftValue() {
        return this.exp1;
    }
    
    public ValueExp getRightValue() {
        return this.exp2;
    }
    
    @Override
    public ValueExp apply(final ObjectName objectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
        final ValueExp apply = this.exp1.apply(objectName);
        final ValueExp apply2 = this.exp2.apply(objectName);
        if (apply instanceof NumericValueExp) {
            if (((NumericValueExp)apply).isLong()) {
                final long longValue = ((NumericValueExp)apply).longValue();
                final long longValue2 = ((NumericValueExp)apply2).longValue();
                switch (this.op) {
                    case 0: {
                        return Query.value(longValue + longValue2);
                    }
                    case 2: {
                        return Query.value(longValue * longValue2);
                    }
                    case 1: {
                        return Query.value(longValue - longValue2);
                    }
                    case 3: {
                        return Query.value(longValue / longValue2);
                    }
                }
            }
            else {
                final double doubleValue = ((NumericValueExp)apply).doubleValue();
                final double doubleValue2 = ((NumericValueExp)apply2).doubleValue();
                switch (this.op) {
                    case 0: {
                        return Query.value(doubleValue + doubleValue2);
                    }
                    case 2: {
                        return Query.value(doubleValue * doubleValue2);
                    }
                    case 1: {
                        return Query.value(doubleValue - doubleValue2);
                    }
                    case 3: {
                        return Query.value(doubleValue / doubleValue2);
                    }
                }
            }
            throw new BadBinaryOpValueExpException(this);
        }
        final String value = ((StringValueExp)apply).getValue();
        final String value2 = ((StringValueExp)apply2).getValue();
        switch (this.op) {
            case 0: {
                return new StringValueExp(value + value2);
            }
            default: {
                throw new BadStringOperationException(this.opString());
            }
        }
    }
    
    @Override
    public String toString() {
        try {
            return this.parens(this.exp1, true) + " " + this.opString() + " " + this.parens(this.exp2, false);
        }
        catch (final BadBinaryOpValueExpException ex) {
            return "invalid expression";
        }
    }
    
    private String parens(final ValueExp valueExp, final boolean b) throws BadBinaryOpValueExpException {
        int n;
        if (valueExp instanceof BinaryOpValueExp) {
            final int op = ((BinaryOpValueExp)valueExp).op;
            if (b) {
                n = ((this.precedence(op) >= this.precedence(this.op)) ? 1 : 0);
            }
            else {
                n = ((this.precedence(op) > this.precedence(this.op)) ? 1 : 0);
            }
        }
        else {
            n = 1;
        }
        if (n != 0) {
            return valueExp.toString();
        }
        return "(" + valueExp + ")";
    }
    
    private int precedence(final int n) throws BadBinaryOpValueExpException {
        switch (n) {
            case 0:
            case 1: {
                return 0;
            }
            case 2:
            case 3: {
                return 1;
            }
            default: {
                throw new BadBinaryOpValueExpException(this);
            }
        }
    }
    
    private String opString() throws BadBinaryOpValueExpException {
        switch (this.op) {
            case 0: {
                return "+";
            }
            case 2: {
                return "*";
            }
            case 1: {
                return "-";
            }
            case 3: {
                return "/";
            }
            default: {
                throw new BadBinaryOpValueExpException(this);
            }
        }
    }
    
    @Deprecated
    @Override
    public void setMBeanServer(final MBeanServer mBeanServer) {
        super.setMBeanServer(mBeanServer);
    }
}
