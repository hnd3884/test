package javax.management;

class InstanceOfQueryExp extends QueryEval implements QueryExp
{
    private static final long serialVersionUID = -1081892073854801359L;
    private StringValueExp classNameValue;
    
    public InstanceOfQueryExp(final StringValueExp classNameValue) {
        if (classNameValue == null) {
            throw new IllegalArgumentException("Null class name.");
        }
        this.classNameValue = classNameValue;
    }
    
    public StringValueExp getClassNameValue() {
        return this.classNameValue;
    }
    
    @Override
    public boolean apply(final ObjectName objectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
        StringValueExp stringValueExp;
        try {
            stringValueExp = (StringValueExp)this.classNameValue.apply(objectName);
        }
        catch (final ClassCastException ex) {
            final BadStringOperationException ex2 = new BadStringOperationException(ex.toString());
            ex2.initCause(ex);
            throw ex2;
        }
        try {
            return QueryEval.getMBeanServer().isInstanceOf(objectName, stringValueExp.getValue());
        }
        catch (final InstanceNotFoundException ex3) {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return "InstanceOf " + this.classNameValue.toString();
    }
}
