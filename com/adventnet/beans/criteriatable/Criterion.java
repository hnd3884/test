package com.adventnet.beans.criteriatable;

public class Criterion implements Operand
{
    private Object attributeObject;
    private String comparator;
    private Object value;
    private boolean negated;
    private boolean groupStartsBeforeThis;
    private boolean groupEndsAfterThis;
    
    public Criterion(final Object o, final String s, final Object o2) {
        this(o, s, o2, false, false);
    }
    
    public Criterion(final Object attributeObject, final String comparator, final Object value, final boolean groupStartsBeforeThis, final boolean groupEndsAfterThis) {
        this.attributeObject = attributeObject;
        this.comparator = comparator;
        this.value = value;
        this.groupStartsBeforeThis = groupStartsBeforeThis;
        this.groupEndsAfterThis = groupEndsAfterThis;
    }
    
    public Object getAttributeObject() {
        return this.attributeObject;
    }
    
    public String getComparator() {
        return this.comparator;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public Operand and(final Operand operand) {
        return new Criteria(this, operand, "AND");
    }
    
    public Operand or(final Operand operand) {
        return new Criteria(this, operand, "OR");
    }
    
    public String toString() {
        String s = "[" + this.attributeObject.toString() + " / " + this.comparator + " / " + this.value + "]";
        if (this.isNegated()) {
            s = "NOT( " + s + " )";
        }
        return s;
    }
    
    public String getString() {
        String s = "[" + this.attributeObject.toString() + " / " + this.comparator + " / " + this.value + "]";
        if (this.isNegated()) {
            s = "NOT(" + s + ")";
        }
        if (this.isGroupStartsBeforeThis()) {
            s = "(" + s;
        }
        if (this.isGroupEndsAfterThis()) {
            s += ")";
        }
        return s;
    }
    
    public Operand negate() {
        final Criterion criterion = (Criterion)this.clone();
        criterion.negated = !criterion.isNegated();
        return criterion;
    }
    
    public boolean isNegated() {
        return this.negated;
    }
    
    public Object clone() {
        Criterion criterion;
        try {
            criterion = (Criterion)super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw new Error(ex.getMessage());
        }
        return criterion;
    }
    
    public boolean isGroupStartsBeforeThis() {
        return this.groupStartsBeforeThis;
    }
    
    public void setGroupStartsBeforeThis(final boolean groupStartsBeforeThis) {
        this.groupStartsBeforeThis = groupStartsBeforeThis;
    }
    
    public void setGroupEndsAfterThis(final boolean groupEndsAfterThis) {
        this.groupEndsAfterThis = groupEndsAfterThis;
    }
    
    public boolean isGroupEndsAfterThis() {
        return this.groupEndsAfterThis;
    }
}
