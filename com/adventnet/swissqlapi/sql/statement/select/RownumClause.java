package com.adventnet.swissqlapi.sql.statement.select;

public class RownumClause
{
    private String rownumClause;
    private String operator;
    private Object rownumValue;
    
    public void setRownumClause(final String s_rnc) {
        this.rownumClause = s_rnc;
    }
    
    public void setOperator(final String s_op) {
        this.operator = s_op;
    }
    
    public void setRownumValue(final Object o_rnv) {
        this.rownumValue = o_rnv;
    }
    
    public String getRownumClause() {
        return this.rownumClause;
    }
    
    public String getOperator() {
        return this.operator;
    }
    
    public Object getRownumValue() {
        return this.rownumValue;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.rownumClause);
        sb.append(" " + this.operator.toUpperCase());
        sb.append(" " + this.rownumValue.toString());
        return sb.toString();
    }
}
