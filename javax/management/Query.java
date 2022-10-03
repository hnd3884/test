package javax.management;

public class Query
{
    public static final int GT = 0;
    public static final int LT = 1;
    public static final int GE = 2;
    public static final int LE = 3;
    public static final int EQ = 4;
    public static final int PLUS = 0;
    public static final int MINUS = 1;
    public static final int TIMES = 2;
    public static final int DIV = 3;
    
    public static QueryExp and(final QueryExp queryExp, final QueryExp queryExp2) {
        return new AndQueryExp(queryExp, queryExp2);
    }
    
    public static QueryExp or(final QueryExp queryExp, final QueryExp queryExp2) {
        return new OrQueryExp(queryExp, queryExp2);
    }
    
    public static QueryExp gt(final ValueExp valueExp, final ValueExp valueExp2) {
        return new BinaryRelQueryExp(0, valueExp, valueExp2);
    }
    
    public static QueryExp geq(final ValueExp valueExp, final ValueExp valueExp2) {
        return new BinaryRelQueryExp(2, valueExp, valueExp2);
    }
    
    public static QueryExp leq(final ValueExp valueExp, final ValueExp valueExp2) {
        return new BinaryRelQueryExp(3, valueExp, valueExp2);
    }
    
    public static QueryExp lt(final ValueExp valueExp, final ValueExp valueExp2) {
        return new BinaryRelQueryExp(1, valueExp, valueExp2);
    }
    
    public static QueryExp eq(final ValueExp valueExp, final ValueExp valueExp2) {
        return new BinaryRelQueryExp(4, valueExp, valueExp2);
    }
    
    public static QueryExp between(final ValueExp valueExp, final ValueExp valueExp2, final ValueExp valueExp3) {
        return new BetweenQueryExp(valueExp, valueExp2, valueExp3);
    }
    
    public static QueryExp match(final AttributeValueExp attributeValueExp, final StringValueExp stringValueExp) {
        return new MatchQueryExp(attributeValueExp, stringValueExp);
    }
    
    public static AttributeValueExp attr(final String s) {
        return new AttributeValueExp(s);
    }
    
    public static AttributeValueExp attr(final String s, final String s2) {
        return new QualifiedAttributeValueExp(s, s2);
    }
    
    public static AttributeValueExp classattr() {
        return new ClassAttributeValueExp();
    }
    
    public static QueryExp not(final QueryExp queryExp) {
        return new NotQueryExp(queryExp);
    }
    
    public static QueryExp in(final ValueExp valueExp, final ValueExp[] array) {
        return new InQueryExp(valueExp, array);
    }
    
    public static StringValueExp value(final String s) {
        return new StringValueExp(s);
    }
    
    public static ValueExp value(final Number n) {
        return new NumericValueExp(n);
    }
    
    public static ValueExp value(final int n) {
        return new NumericValueExp((long)n);
    }
    
    public static ValueExp value(final long n) {
        return new NumericValueExp(n);
    }
    
    public static ValueExp value(final float n) {
        return new NumericValueExp((double)n);
    }
    
    public static ValueExp value(final double n) {
        return new NumericValueExp(n);
    }
    
    public static ValueExp value(final boolean b) {
        return new BooleanValueExp(b);
    }
    
    public static ValueExp plus(final ValueExp valueExp, final ValueExp valueExp2) {
        return new BinaryOpValueExp(0, valueExp, valueExp2);
    }
    
    public static ValueExp times(final ValueExp valueExp, final ValueExp valueExp2) {
        return new BinaryOpValueExp(2, valueExp, valueExp2);
    }
    
    public static ValueExp minus(final ValueExp valueExp, final ValueExp valueExp2) {
        return new BinaryOpValueExp(1, valueExp, valueExp2);
    }
    
    public static ValueExp div(final ValueExp valueExp, final ValueExp valueExp2) {
        return new BinaryOpValueExp(3, valueExp, valueExp2);
    }
    
    public static QueryExp initialSubString(final AttributeValueExp attributeValueExp, final StringValueExp stringValueExp) {
        return new MatchQueryExp(attributeValueExp, new StringValueExp(escapeString(stringValueExp.getValue()) + "*"));
    }
    
    public static QueryExp anySubString(final AttributeValueExp attributeValueExp, final StringValueExp stringValueExp) {
        return new MatchQueryExp(attributeValueExp, new StringValueExp("*" + escapeString(stringValueExp.getValue()) + "*"));
    }
    
    public static QueryExp finalSubString(final AttributeValueExp attributeValueExp, final StringValueExp stringValueExp) {
        return new MatchQueryExp(attributeValueExp, new StringValueExp("*" + escapeString(stringValueExp.getValue())));
    }
    
    public static QueryExp isInstanceOf(final StringValueExp stringValueExp) {
        return new InstanceOfQueryExp(stringValueExp);
    }
    
    private static String escapeString(String s) {
        if (s == null) {
            return null;
        }
        s = s.replace("\\", "\\\\");
        s = s.replace("*", "\\*");
        s = s.replace("?", "\\?");
        s = s.replace("[", "\\[");
        return s;
    }
}
