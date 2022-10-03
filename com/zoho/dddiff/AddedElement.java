package com.zoho.dddiff;

import org.w3c.dom.Element;

public class AddedElement
{
    private Element element;
    private String tableName;
    private String ddName;
    private DataDictionaryDiff.ElementType type;
    
    AddedElement(final Element obj, final String tableName, final String ddName, final DataDictionaryDiff.ElementType type) {
        this.element = obj;
        this.tableName = tableName;
        this.ddName = ddName;
        this.type = type;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public String getDDName() {
        return this.ddName;
    }
    
    public Element getElement() {
        return this.element;
    }
    
    public DataDictionaryDiff.ElementType getType() {
        return this.type;
    }
}
