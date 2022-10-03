package javax.management;

class AndQueryExp extends QueryEval implements QueryExp
{
    private static final long serialVersionUID = -1081892073854801359L;
    private QueryExp exp1;
    private QueryExp exp2;
    
    public AndQueryExp() {
    }
    
    public AndQueryExp(final QueryExp exp1, final QueryExp exp2) {
        this.exp1 = exp1;
        this.exp2 = exp2;
    }
    
    public QueryExp getLeftExp() {
        return this.exp1;
    }
    
    public QueryExp getRightExp() {
        return this.exp2;
    }
    
    @Override
    public boolean apply(final ObjectName objectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
        return this.exp1.apply(objectName) && this.exp2.apply(objectName);
    }
    
    @Override
    public String toString() {
        return "(" + this.exp1 + ") and (" + this.exp2 + ")";
    }
}
