package javax.management;

public class StringValueExp implements ValueExp
{
    private static final long serialVersionUID = -3256390509806284044L;
    private String val;
    
    public StringValueExp() {
    }
    
    public StringValueExp(final String val) {
        this.val = val;
    }
    
    public String getValue() {
        return this.val;
    }
    
    @Override
    public String toString() {
        return "'" + this.val.replace("'", "''") + "'";
    }
    
    @Deprecated
    @Override
    public void setMBeanServer(final MBeanServer mBeanServer) {
    }
    
    @Override
    public ValueExp apply(final ObjectName objectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
        return this;
    }
}
