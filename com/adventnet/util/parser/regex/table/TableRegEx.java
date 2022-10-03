package com.adventnet.util.parser.regex.table;

import java.util.ArrayList;

public class TableRegEx
{
    private String name;
    private String expression;
    private ArrayList parameterList;
    private String reference;
    private int startLine;
    private int endLine;
    
    public TableRegEx() {
        this.name = null;
        this.expression = null;
        this.parameterList = null;
        this.reference = null;
        this.startLine = 0;
        this.endLine = 0;
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
    
    public String getReference() {
        return this.reference;
    }
    
    public void setReference(final String reference) {
        this.reference = reference;
    }
    
    public int getStartLine() {
        return this.startLine;
    }
    
    public void setStartLine(final int startLine) {
        this.startLine = startLine;
    }
    
    public int getEndLine() {
        return this.endLine;
    }
    
    public void setEndLine(final int endLine) {
        this.endLine = endLine;
    }
}
