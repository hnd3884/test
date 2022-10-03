package com.adventnet.swissqlapi.sql.statement.update;

import java.util.ArrayList;

public class OracleSpecificClass
{
    public ArrayList expressionList;
    public String into;
    public ArrayList variableList;
    public String returning;
    
    public OracleSpecificClass() {
        this.returning = new String();
        this.into = new String();
        this.variableList = new ArrayList();
        this.expressionList = new ArrayList();
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.returning);
        sb.append(this.expressionList);
        return sb.toString();
    }
}
