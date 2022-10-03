package com.adventnet.webclient.components.table;

public class ViewColumn
{
    private String columnName;
    private int index;
    private Object userObject;
    private String rendererClass;
    
    public ViewColumn(final String columnName, final int index, final Object userObject, final String rendererClass) {
        this.columnName = null;
        this.index = 0;
        this.userObject = null;
        this.rendererClass = null;
        this.columnName = columnName;
        this.index = index;
        this.userObject = userObject;
        this.rendererClass = rendererClass;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public Object getUserObject() {
        return this.userObject;
    }
    
    public String getRendererClass() {
        return this.rendererClass;
    }
    
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("View Column [ ");
        buffer.append(" Column Name = ");
        buffer.append(this.columnName);
        buffer.append("; Index = ");
        buffer.append(this.index);
        buffer.append("; User Object = ");
        buffer.append(this.userObject);
        buffer.append("; Renderer Class = ");
        buffer.append(this.rendererClass);
        buffer.append(" ]");
        return buffer.toString();
    }
}
