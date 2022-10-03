package com.adventnet.swissqlapi.sql.statement.select;

public class XMLStatement
{
    private String forString;
    private String xmlString;
    private String xmlType;
    private String xmlData;
    private String elements;
    
    public void setFor(final String forString) {
        this.forString = forString;
    }
    
    public void setXML(final String xmlString) {
        this.xmlString = xmlString;
    }
    
    public void setXMLType(final String xmlType) {
        this.xmlType = xmlType;
    }
    
    public void setXMLData(final String xmlData) {
        this.xmlData = xmlData;
    }
    
    public void setElements(final String elements) {
        this.elements = elements;
    }
    
    public String getFor() {
        return this.forString;
    }
    
    public String getXML() {
        return this.xmlString;
    }
    
    public String getXMLType() {
        return this.xmlType;
    }
    
    public String getElements() {
        return this.elements;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.forString != null) {
            sb.append(this.forString + " ");
        }
        if (this.xmlString != null) {
            sb.append(this.xmlString + " ");
        }
        if (this.xmlType != null) {
            sb.append(this.xmlType + " ");
        }
        if (this.xmlData != null) {
            sb.append(", " + this.xmlData);
        }
        if (this.elements != null) {
            sb.append(", " + this.elements);
        }
        return sb.toString();
    }
}
