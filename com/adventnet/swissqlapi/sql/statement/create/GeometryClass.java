package com.adventnet.swissqlapi.sql.statement.create;

public class GeometryClass implements Datatype
{
    private String datatypeName;
    private String openBrace;
    private String closedBrace;
    private String size;
    private String schemaName;
    private String arrayStr;
    
    @Override
    public void setDatatypeName(final String datatypeName) {
        this.datatypeName = datatypeName;
    }
    
    @Override
    public void setOpenBrace(final String openBrace) {
        this.openBrace = openBrace;
    }
    
    @Override
    public void setClosedBrace(final String closedBrace) {
        this.closedBrace = closedBrace;
    }
    
    @Override
    public void setSize(final String size) {
        this.size = size;
    }
    
    public void setSchemaName(final String schemaName) {
        this.schemaName = schemaName;
    }
    
    @Override
    public String getDatatypeName() {
        return this.datatypeName;
    }
    
    @Override
    public String getSize() {
        return this.size;
    }
    
    @Override
    public String getOpenBrace() {
        return this.openBrace;
    }
    
    @Override
    public String getClosedBrace() {
        return this.closedBrace;
    }
    
    public String getSchemaName() {
        return this.schemaName;
    }
    
    @Override
    public void toInformixString() {
    }
    
    @Override
    public void toDB2String() {
        if (this.datatypeName != null && this.datatypeName.equalsIgnoreCase("sdo_geometry")) {
            this.setDatatypeName("ST_GEOMETRY");
            this.setSchemaName("DB2GSE");
        }
    }
    
    @Override
    public void toOracleString() {
    }
    
    @Override
    public void toMSSQLServerString() {
    }
    
    @Override
    public void toSybaseString() {
    }
    
    @Override
    public void toPostgreSQLString() {
    }
    
    @Override
    public void toMySQLString() {
    }
    
    @Override
    public void toANSIString() {
    }
    
    @Override
    public void toTimesTenString() {
    }
    
    @Override
    public void toNetezzaString() {
    }
    
    @Override
    public void toTeradataString() {
    }
    
    public Datatype copyObjectValues() {
        final GeometryClass geometryClass = new GeometryClass();
        geometryClass.setClosedBrace(this.closedBrace);
        geometryClass.setDatatypeName(this.getDatatypeName());
        geometryClass.setOpenBrace(this.openBrace);
        geometryClass.setSize(this.getSize());
        geometryClass.setSchemaName(this.getSchemaName());
        return geometryClass;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.schemaName != null) {
            sb.append(this.schemaName + ".");
        }
        if (this.datatypeName != null) {
            sb.append(this.datatypeName + " ");
        }
        if (this.openBrace != null) {
            sb.append(this.openBrace);
        }
        if (this.size != null) {
            sb.append(this.size);
        }
        if (this.closedBrace != null) {
            sb.append(this.closedBrace);
        }
        if (this.arrayStr != null) {
            sb.append(this.arrayStr);
        }
        return sb.toString();
    }
    
    @Override
    public void setArray(String arrayStr) {
        arrayStr = arrayStr;
    }
    
    @Override
    public String getArray() {
        return this.arrayStr;
    }
}
