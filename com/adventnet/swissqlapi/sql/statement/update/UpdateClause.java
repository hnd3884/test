package com.adventnet.swissqlapi.sql.statement.update;

public class UpdateClause
{
    private String update;
    private OptionalSpecifier optionalSpecifier;
    
    public UpdateClause() {
        this.update = null;
        this.optionalSpecifier = null;
    }
    
    public void setUpdate(final String s) {
        this.update = s;
    }
    
    public String getUpdate() {
        return this.update;
    }
    
    public void setOptionalSpecifier(final OptionalSpecifier os) {
        this.optionalSpecifier = os;
    }
    
    public OptionalSpecifier getOptionalSpecifier() {
        return this.optionalSpecifier;
    }
    
    private void toGeneric() {
        this.optionalSpecifier = null;
    }
    
    public void toOracle() {
        this.toGeneric();
    }
    
    public void toDB2() {
        this.toGeneric();
    }
    
    public void toInformix() {
        this.toGeneric();
    }
    
    public void toSQLServer() {
        this.toGeneric();
    }
    
    public void toSybase() {
        this.toGeneric();
    }
    
    public void toMySQL() {
    }
    
    public void toPostgreSQL() {
        this.toGeneric();
    }
    
    public void toANSISQL() {
        if (this.optionalSpecifier != null) {
            this.optionalSpecifier = null;
        }
    }
    
    public void toTimesTen() {
        this.toGeneric();
    }
    
    public void toNetezza() {
        this.toGeneric();
    }
    
    public void toTeradata() {
        this.toGeneric();
    }
    
    @Override
    public String toString() {
        final StringBuffer stringbuffer = new StringBuffer();
        if (this.update != null) {
            stringbuffer.append(this.update.toUpperCase());
            stringbuffer.append(" ");
        }
        if (this.optionalSpecifier != null) {
            stringbuffer.append(this.optionalSpecifier.toString());
            stringbuffer.append(" ");
        }
        return stringbuffer.toString();
    }
}
