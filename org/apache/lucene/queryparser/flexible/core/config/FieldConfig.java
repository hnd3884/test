package org.apache.lucene.queryparser.flexible.core.config;

public class FieldConfig extends AbstractQueryConfig
{
    private String fieldName;
    
    public FieldConfig(final String fieldName) {
        if (fieldName == null) {
            throw new IllegalArgumentException("field name should not be null!");
        }
        this.fieldName = fieldName;
    }
    
    public String getField() {
        return this.fieldName;
    }
    
    @Override
    public String toString() {
        return "<fieldconfig name=\"" + this.fieldName + "\" configurations=\"" + super.toString() + "\"/>";
    }
}
