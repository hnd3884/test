package com.adventnet.ds.query;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class CaseExpression extends Column
{
    public static final String DUMMY_COLUMN = "<DUMMY_COLUMN>";
    private Object elseVal;
    private List<WhenExpr> whenExpressions;
    private Criteria expr;
    private int valType;
    private boolean useAddWhen_WO_Criteria;
    private boolean useAddWhen_With_Criteria;
    
    public CaseExpression(final String columnAlias) {
        super(null, "<DUMMY_COLUMN>");
        this.elseVal = null;
        this.whenExpressions = new ArrayList<WhenExpr>();
        this.expr = null;
        this.valType = -1;
        this.useAddWhen_WO_Criteria = false;
        this.useAddWhen_With_Criteria = false;
        this.setColumnAlias(columnAlias);
        this.useAddWhen_With_Criteria = true;
    }
    
    public CaseExpression(final String tableAlias, final String columnName, final String columnAlias) {
        super(tableAlias, columnName, columnAlias);
        this.elseVal = null;
        this.whenExpressions = new ArrayList<WhenExpr>();
        this.expr = null;
        this.valType = -1;
        this.useAddWhen_WO_Criteria = false;
        this.useAddWhen_With_Criteria = false;
        this.useAddWhen_WO_Criteria = true;
    }
    
    public CaseExpression(final Criteria expr, final String columnAlias) {
        super(null, "<DUMMY_COLUMN>");
        this.elseVal = null;
        this.whenExpressions = new ArrayList<WhenExpr>();
        this.expr = null;
        this.valType = -1;
        this.useAddWhen_WO_Criteria = false;
        this.useAddWhen_With_Criteria = false;
        this.expr = expr;
        this.setColumnAlias(columnAlias);
        this.useAddWhen_With_Criteria = true;
    }
    
    public void addWhen(final Object expr, final Object value) throws IllegalArgumentException {
        if (this.useAddWhen_With_Criteria) {
            throw new IllegalArgumentException("Conditional WHEN statement not allowed for CASE with Criteria");
        }
        this.addExpr(expr, value);
    }
    
    public void addWhen(final Criteria expr, final Object value) throws IllegalArgumentException {
        if (this.expr != null || this.useAddWhen_WO_Criteria) {
            throw new IllegalArgumentException("Conditional WHEN statement not allowed for CASE with Criteria");
        }
        this.addExpr(expr, value);
    }
    
    public void elseVal(final Object resultVal) throws IllegalArgumentException {
        if (this.whenExpressions == null || this.whenExpressions.isEmpty()) {
            throw new IllegalArgumentException("Else can be added only after WHEN");
        }
        this.validateValue(resultVal);
        this.elseVal = resultVal;
    }
    
    public Object getElseVal() {
        return this.elseVal;
    }
    
    @Override
    public int getType() {
        return this.valType;
    }
    
    public List<WhenExpr> getWhenExpressions() {
        return this.whenExpressions;
    }
    
    public Criteria getExpr() {
        return this.expr;
    }
    
    @Override
    public String toString() {
        final StringBuilder buff = new StringBuilder("CASE ");
        final String columnName = this.getColumnName();
        if (!columnName.equals("<DUMMY_COLUMN>")) {
            buff.append(columnName);
        }
        buff.append("(");
        for (final WhenExpr whenExpr : this.whenExpressions) {
            buff.append("WHEN ");
            buff.append(whenExpr.expr);
            buff.append(" THEN ");
            buff.append(whenExpr.value);
        }
        if (this.elseVal != null) {
            buff.append(" ELSE ");
            buff.append(this.elseVal);
        }
        buff.append(" ) AS ");
        buff.append(this.getColumnAlias());
        return buff.toString();
    }
    
    private void addExpr(final Object expr, final Object value) throws IllegalArgumentException {
        this.validateValue(value);
        this.whenExpressions.add(new WhenExpr(expr, value));
    }
    
    private void validateValue(final Object value) throws IllegalArgumentException {
        if (value != null) {
            if (!(value instanceof Number) && !(value instanceof String) && !(value instanceof Boolean) && !(value instanceof Column)) {
                throw new IllegalArgumentException("Only Numeric and String values allowed");
            }
            final int newValType = this.findValType(value);
            if (this.valType == -1) {
                this.valType = newValType;
            }
            else if (this.valType != newValType) {
                throw new IllegalArgumentException("Result values should be of same type");
            }
        }
    }
    
    private int findValType(final Object value) throws IllegalArgumentException {
        if (value instanceof String) {
            return 12;
        }
        if (value instanceof Integer) {
            return 4;
        }
        if (value instanceof Long) {
            return -5;
        }
        if (value instanceof Float) {
            return 6;
        }
        if (value instanceof Double) {
            return 8;
        }
        if (value instanceof Boolean) {
            return 16;
        }
        if (value instanceof Column) {
            return ((Column)value).getType();
        }
        throw new IllegalArgumentException("Unknown value type specified " + value.getClass().getName());
    }
    
    public static class WhenExpr
    {
        Object expr;
        Object value;
        
        WhenExpr(final Object expr, final Object value) {
            this.expr = expr;
            this.value = value;
        }
        
        public Object getExpr() {
            return this.expr;
        }
        
        public Object getValue() {
            return this.value;
        }
    }
}
