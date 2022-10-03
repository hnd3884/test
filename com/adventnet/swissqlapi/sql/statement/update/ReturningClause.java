package com.adventnet.swissqlapi.sql.statement.update;

import java.util.ArrayList;

public class ReturningClause
{
    public ArrayList lhsExpressionList;
    public String into;
    public ArrayList rhsVariableList;
    public String returning;
    
    public ReturningClause() {
        this.returning = new String();
        this.into = new String();
        this.rhsVariableList = new ArrayList();
        this.lhsExpressionList = new ArrayList();
    }
    
    public void setReturning(final String s) {
        this.returning = s;
    }
    
    public void setInto(final String s) {
        this.into = s;
    }
    
    public void setExpressionList(final ArrayList al) {
        this.lhsExpressionList = al;
    }
    
    public void setrhsVariableList(final ArrayList al) {
        this.rhsVariableList = al;
    }
    
    public String getReturning() {
        return this.returning;
    }
    
    public String getInto() {
        return this.into;
    }
    
    public ArrayList getExpressionList() {
        return this.lhsExpressionList;
    }
    
    public ArrayList getrhsVariableList() {
        return this.rhsVariableList;
    }
    
    @Override
    public String toString() {
        final StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(this.returning.toUpperCase() + " ");
        for (int i = 0, size = this.lhsExpressionList.size(); i < size; ++i) {
            stringbuffer.append(this.lhsExpressionList.get(i).toString() + " ");
        }
        stringbuffer.append(this.into.toUpperCase());
        for (int i = 0, size = this.rhsVariableList.size(); i < size; ++i) {
            stringbuffer.append(" " + this.rhsVariableList.get(i).toString() + " ");
        }
        return stringbuffer.toString();
    }
}
