package com.adventnet.db.persistence.metadata;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;

public class UniqueValueGeneration implements Serializable
{
    private static final long serialVersionUID = 974417217341705752L;
    private String generatorName;
    private String nameColumn;
    private boolean isInstanceSpecificSeqGenEnabled;
    private int generatorType;
    private String generatorClass;
    public static final int TABLE_SEQGEN = 1;
    public static final int TEMPLATE_SEQGEN = 2;
    
    public UniqueValueGeneration() {
        this.isInstanceSpecificSeqGenEnabled = false;
        this.generatorType = 1;
    }
    
    public String getGeneratorName() {
        return this.generatorName;
    }
    
    public void setGeneratorName(final String v) {
        this.generatorName = v;
    }
    
    public void setGeneratorType(final int type) {
        this.generatorType = type;
    }
    
    public int getGeneratorType() {
        return this.generatorType;
    }
    
    public String getGeneratorNameForTemplateInstance(final String templateInstanceName, final String colName) {
        return templateInstanceName + "." + colName;
    }
    
    @Override
    public int hashCode() {
        return this.generatorName.hashCode() + ((this.nameColumn != null) ? (2 * this.nameColumn.hashCode()) : 0);
    }
    
    @Override
    public boolean equals(final Object uvg) {
        if (uvg instanceof UniqueValueGeneration) {
            final UniqueValueGeneration u = (UniqueValueGeneration)uvg;
            return u.hashCode() == this.hashCode();
        }
        return false;
    }
    
    public void setNameColumn(final String nameCol) {
        this.nameColumn = nameCol;
    }
    
    public String getNameColumn() {
        return this.nameColumn;
    }
    
    public void setGeneratorClass(final String generatorClass) {
        this.generatorClass = generatorClass;
    }
    
    public String getGeneratorClass() {
        return this.generatorClass;
    }
    
    public void setInstanceSpecificSequenceGenerator(final boolean isEnabled) {
        this.isInstanceSpecificSeqGenEnabled = isEnabled;
    }
    
    public boolean isInstanceSpecificSequenceGeneratorEnabled() {
        return this.isInstanceSpecificSeqGenEnabled;
    }
    
    public void validate() {
        if (this.generatorType == 1 && this.isInstanceSpecificSeqGenEnabled) {
            throw new IllegalArgumentException("Instance-specific sequence-generator cannot be enabled for Non-template table. Generator Name: " + this.generatorName);
        }
    }
    
    public JSONObject toJSON() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("generatorname", (Object)this.generatorName);
        jsonObject.put("generatorclass", (Object)this.generatorClass);
        return jsonObject;
    }
}
