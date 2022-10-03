package javax.management;

class QualifiedAttributeValueExp extends AttributeValueExp
{
    private static final long serialVersionUID = 8832517277410933254L;
    private String className;
    
    @Deprecated
    public QualifiedAttributeValueExp() {
    }
    
    public QualifiedAttributeValueExp(final String className, final String s) {
        super(s);
        this.className = className;
    }
    
    public String getAttrClassName() {
        return this.className;
    }
    
    @Override
    public ValueExp apply(final ObjectName objectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
        try {
            final String className = QueryEval.getMBeanServer().getObjectInstance(objectName).getClassName();
            if (className.equals(this.className)) {
                return super.apply(objectName);
            }
            throw new InvalidApplicationException((Object)("Class name is " + className + ", should be " + this.className));
        }
        catch (final Exception ex) {
            throw new InvalidApplicationException((Object)("Qualified attribute: " + ex));
        }
    }
    
    @Override
    public String toString() {
        if (this.className != null) {
            return this.className + "." + super.toString();
        }
        return super.toString();
    }
}
