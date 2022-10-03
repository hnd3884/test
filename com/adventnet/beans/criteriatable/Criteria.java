package com.adventnet.beans.criteriatable;

public class Criteria implements Operand
{
    public static final String AND = "AND";
    public static final String OR = "OR";
    public static final String NOT = "NOT";
    private Operand leftOperand;
    private String operator;
    private Operand rightOperand;
    private boolean negated;
    private boolean groupStartsBeforeThis;
    private boolean groupEndsAfterThis;
    
    public Criteria(final Operand operand, final Operand operand2, final String s) {
        this(operand, operand2, s, false, false);
    }
    
    public Criteria(final Operand leftOperand, final Operand rightOperand, final String operator, final boolean groupStartsBeforeThis, final boolean groupEndsAfterThis) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.operator = operator;
        this.groupStartsBeforeThis = groupStartsBeforeThis;
        this.groupEndsAfterThis = groupEndsAfterThis;
    }
    
    public Operand getLeftOperand() {
        return this.leftOperand;
    }
    
    public Operand getRightOperand() {
        return this.rightOperand;
    }
    
    public String getOperator() {
        return this.operator;
    }
    
    public Operand and(final Operand operand) {
        return new Criteria(this, operand, "AND");
    }
    
    public Operand or(final Operand operand) {
        return new Criteria(this, operand, "OR");
    }
    
    public String toString() {
        String string = "";
        if (this.leftOperand instanceof Criteria) {
            string += "(";
        }
        String s;
        if (this.leftOperand != null) {
            s = string + this.leftOperand.toString();
        }
        else {
            s = string + " [NULL] ";
        }
        if (this.leftOperand instanceof Criteria) {
            s += ")";
        }
        String s2;
        if (this.operator != null) {
            s2 = s + " " + this.operator + " ";
        }
        else {
            s2 = s + " [NULL] ";
        }
        if (this.rightOperand instanceof Criteria) {
            s2 += "(";
        }
        String s3;
        if (this.rightOperand != null) {
            s3 = s2 + this.rightOperand.toString();
        }
        else {
            s3 = s2 + " [NULL] ";
        }
        if (this.rightOperand instanceof Criteria) {
            s3 += ")";
        }
        if (this.isNegated()) {
            s3 = "NOT(" + s3 + ")";
        }
        return s3;
    }
    
    public String getString() {
        final String s = "";
        String s2;
        if (this.leftOperand != null) {
            s2 = s + this.leftOperand.getString();
        }
        else {
            s2 = s + " [NULL] ";
        }
        String s3;
        if (this.operator != null) {
            s3 = s2 + " " + this.operator + " ";
        }
        else {
            s3 = s2 + " [NULL] ";
        }
        String s4;
        if (this.rightOperand != null) {
            s4 = s3 + this.rightOperand.getString();
        }
        else {
            s4 = s3 + " [NULL] ";
        }
        if (this.isNegated()) {
            s4 = "NOT(" + s4 + ")";
        }
        if (this.isGroupStartsBeforeThis()) {
            s4 = "(" + s4;
        }
        if (this.isGroupEndsAfterThis()) {
            s4 += ")";
        }
        return s4;
    }
    
    public Operand negate() {
        final Criteria criteria = (Criteria)this.clone();
        criteria.negated = !criteria.isNegated();
        return criteria;
    }
    
    public boolean isNegated() {
        return this.negated;
    }
    
    public void setGroupStartsBeforeThis(final boolean groupStartsBeforeThis) {
        this.groupStartsBeforeThis = groupStartsBeforeThis;
    }
    
    public void setGroupEndsAfterThis(final boolean groupEndsAfterThis) {
        this.groupEndsAfterThis = groupEndsAfterThis;
    }
    
    public boolean isGroupStartsBeforeThis() {
        return this.groupStartsBeforeThis;
    }
    
    public boolean isGroupEndsAfterThis() {
        return this.groupEndsAfterThis;
    }
    
    public Object clone() {
        Criteria criteria;
        try {
            criteria = (Criteria)super.clone();
            if (criteria.leftOperand instanceof Criteria) {
                criteria.leftOperand = (Operand)((Criteria)criteria.leftOperand).clone();
            }
            else if (criteria.leftOperand instanceof Criterion) {
                criteria.leftOperand = (Operand)((Criterion)criteria.leftOperand).clone();
            }
            if (criteria.rightOperand instanceof Criteria) {
                criteria.rightOperand = (Operand)((Criteria)criteria.rightOperand).clone();
            }
            else if (criteria.rightOperand instanceof Criterion) {
                criteria.rightOperand = (Operand)((Criterion)criteria.rightOperand).clone();
            }
            criteria.operator = new String(this.operator);
        }
        catch (final CloneNotSupportedException ex) {
            throw new Error(ex.getMessage());
        }
        return criteria;
    }
}
