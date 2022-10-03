package javax.management;

public class AttributeValueExp implements ValueExp
{
    private static final long serialVersionUID = -7768025046539163385L;
    private String attr;
    
    @Deprecated
    public AttributeValueExp() {
    }
    
    public AttributeValueExp(final String attr) {
        this.attr = attr;
    }
    
    public String getAttributeName() {
        return this.attr;
    }
    
    @Override
    public ValueExp apply(final ObjectName objectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
        final Object attribute = this.getAttribute(objectName);
        if (attribute instanceof Number) {
            return new NumericValueExp((Number)attribute);
        }
        if (attribute instanceof String) {
            return new StringValueExp((String)attribute);
        }
        if (attribute instanceof Boolean) {
            return new BooleanValueExp((Boolean)attribute);
        }
        throw new BadAttributeValueExpException(attribute);
    }
    
    @Override
    public String toString() {
        return this.attr;
    }
    
    @Deprecated
    @Override
    public void setMBeanServer(final MBeanServer mBeanServer) {
    }
    
    protected Object getAttribute(final ObjectName objectName) {
        try {
            return QueryEval.getMBeanServer().getAttribute(objectName, this.attr);
        }
        catch (final Exception ex) {
            return null;
        }
    }
}
