package com.adventnet.swissqlapi.sql.statement.update;

import com.adventnet.swissqlapi.sql.UserObjectContext;

public class WithClause
{
    private String with;
    private String readOnly;
    private String checkOption;
    private String constraint;
    private String constraintName;
    private UserObjectContext context;
    
    public WithClause() {
        this.context = null;
        this.with = new String();
        this.readOnly = new String();
        this.checkOption = new String();
        this.constraint = new String();
        this.constraintName = new String();
    }
    
    public void setWith(final String s) {
        this.with = s;
    }
    
    public void setReadOnly(final String s) {
        this.readOnly = s;
    }
    
    public void setCheckOption(final String s) {
        this.checkOption = s;
    }
    
    public void setConstraint(final String s) {
        this.constraint = s;
    }
    
    public void setConstraintName(final String s) {
        this.constraintName = s;
    }
    
    public void setObjectContext(final UserObjectContext obj) {
        this.context = obj;
    }
    
    public String getWith() {
        return this.with;
    }
    
    public String getReadOnly() {
        return this.readOnly;
    }
    
    public String getCheckOption() {
        return this.checkOption;
    }
    
    public String getConstraint() {
        return this.constraint;
    }
    
    public String getConstraintName() {
        return this.constraintName;
    }
    
    @Override
    public String toString() {
        final StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(" ");
        if (this.with != null) {
            stringbuffer.append(this.with.toUpperCase());
            stringbuffer.append(" ");
        }
        if (this.readOnly != null) {
            stringbuffer.append(this.readOnly.toUpperCase());
            stringbuffer.append(" ");
        }
        if (this.checkOption != null) {
            stringbuffer.append(this.checkOption.toUpperCase());
            stringbuffer.append(" ");
        }
        if (this.constraint != null) {
            stringbuffer.append(this.constraint.toUpperCase());
            stringbuffer.append(" ");
        }
        if (this.constraintName != null) {
            if (this.context != null) {
                stringbuffer.append(this.context.getEquivalent(this.constraintName));
            }
            else {
                stringbuffer.append(this.constraintName);
            }
            stringbuffer.append(" ");
        }
        return stringbuffer.toString();
    }
}
