package javax.management;

class BooleanValueExp extends QueryEval implements ValueExp
{
    private static final long serialVersionUID = 7754922052666594581L;
    private boolean val;
    
    BooleanValueExp(final boolean val) {
        this.val = false;
        this.val = val;
    }
    
    BooleanValueExp(final Boolean b) {
        this.val = false;
        this.val = b;
    }
    
    public Boolean getValue() {
        return this.val;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.val);
    }
    
    @Override
    public ValueExp apply(final ObjectName objectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
        return this;
    }
    
    @Deprecated
    @Override
    public void setMBeanServer(final MBeanServer mBeanServer) {
        super.setMBeanServer(mBeanServer);
    }
}
