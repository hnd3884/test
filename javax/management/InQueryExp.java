package javax.management;

class InQueryExp extends QueryEval implements QueryExp
{
    private static final long serialVersionUID = -5801329450358952434L;
    private ValueExp val;
    private ValueExp[] valueList;
    
    public InQueryExp() {
    }
    
    public InQueryExp(final ValueExp val, final ValueExp[] valueList) {
        this.val = val;
        this.valueList = valueList;
    }
    
    public ValueExp getCheckedValue() {
        return this.val;
    }
    
    public ValueExp[] getExplicitValues() {
        return this.valueList;
    }
    
    @Override
    public boolean apply(final ObjectName objectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
        if (this.valueList != null) {
            final ValueExp apply = this.val.apply(objectName);
            final boolean b = apply instanceof NumericValueExp;
            final ValueExp[] valueList = this.valueList;
            for (int length = valueList.length, i = 0; i < length; ++i) {
                final ValueExp apply2 = valueList[i].apply(objectName);
                if (b) {
                    if (((NumericValueExp)apply2).doubleValue() == ((NumericValueExp)apply).doubleValue()) {
                        return true;
                    }
                }
                else if (((StringValueExp)apply2).getValue().equals(((StringValueExp)apply).getValue())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return this.val + " in (" + this.generateValueList() + ")";
    }
    
    private String generateValueList() {
        if (this.valueList == null || this.valueList.length == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(this.valueList[0].toString());
        for (int i = 1; i < this.valueList.length; ++i) {
            sb.append(", ");
            sb.append(this.valueList[i]);
        }
        return sb.toString();
    }
}
