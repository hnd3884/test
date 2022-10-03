package com.adventnet.util.parser.regex.scalar;

import java.util.ArrayList;

public class ScalarRegEx
{
    private String name;
    private String expression;
    private ArrayList parameterList;
    
    public ScalarRegEx() {
        this.name = null;
        this.expression = null;
        this.parameterList = null;
        this.parameterList = new ArrayList();
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getExpression() {
        return this.expression;
    }
    
    public void setExpression(final String expression) {
        this.expression = expression;
    }
    
    public ArrayList getParameterList() {
        return this.parameterList;
    }
    
    public void setParameterList(final ArrayList parameterList) {
        this.parameterList = parameterList;
    }
}
