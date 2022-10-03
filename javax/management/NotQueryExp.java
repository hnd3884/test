package javax.management;

class NotQueryExp extends QueryEval implements QueryExp
{
    private static final long serialVersionUID = 5269643775896723397L;
    private QueryExp exp;
    
    public NotQueryExp() {
    }
    
    public NotQueryExp(final QueryExp exp) {
        this.exp = exp;
    }
    
    public QueryExp getNegatedExp() {
        return this.exp;
    }
    
    @Override
    public boolean apply(final ObjectName objectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
        return !this.exp.apply(objectName);
    }
    
    @Override
    public String toString() {
        return "not (" + this.exp + ")";
    }
}
