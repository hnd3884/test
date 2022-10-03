package com.zoho.security.eventfw.config;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DataTemplateConfig
{
    private List<DataFields> dataFields;
    private List<DataFields> builtInDataFields;
    
    public DataTemplateConfig() {
        this.dataFields = new LinkedList<DataFields>();
        this.builtInDataFields = null;
    }
    
    public void addField(final DataFields field) {
        this.dataFields.add(field);
    }
    
    public void addAllFields(final List<DataFields> fields) {
        if (fields != null && fields.size() > 0) {
            this.dataFields.addAll(fields);
        }
    }
    
    public List<DataFields> getDataFields() {
        return this.dataFields;
    }
    
    public void addBuiltInField(final DataFields field) {
        if (this.builtInDataFields == null) {
            this.builtInDataFields = new LinkedList<DataFields>();
        }
        this.builtInDataFields.add(field);
    }
    
    public void addAllBuiltInFields(final List<DataFields> fields) {
        if (fields != null && fields.size() > 0) {
            if (this.builtInDataFields == null) {
                this.builtInDataFields = new LinkedList<DataFields>();
            }
            this.builtInDataFields.addAll(fields);
        }
    }
    
    public List<DataFields> getBuiltInFields() {
        return this.builtInDataFields;
    }
}
