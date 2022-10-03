package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.config.SwisSQLOptions;

public class DateClass implements Datatype
{
    private String datatypeName;
    private String openBrace;
    private String closedBrace;
    private String size;
    private String withLocalTimeZone;
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
    
    public void setWithLocalTimeZone(final String withLocalTimeZone) {
        this.withLocalTimeZone = withLocalTimeZone;
    }
    
    @Override
    public void setSize(final String size) {
        this.size = size;
    }
    
    @Override
    public String getDatatypeName() {
        return this.datatypeName;
    }
    
    @Override
    public String getSize() {
        return this.size;
    }
    
    public String getWithLocalTimeZone() {
        return this.withLocalTimeZone;
    }
    
    @Override
    public String getClosedBrace() {
        return this.closedBrace;
    }
    
    @Override
    public String getOpenBrace() {
        return this.openBrace;
    }
    
    @Override
    public void toInformixString() {
        if (this.getDatatypeName() != null) {
            final String changeIfxDatatype = this.getDatatypeName();
            if (changeIfxDatatype.equalsIgnoreCase("DATETIME")) {
                this.setDatatypeName("DATETIME YEAR TO FRACTION");
            }
            else if (changeIfxDatatype.equalsIgnoreCase("SMALLDATETIME")) {
                this.setDatatypeName("DATETIME YEAR TO DAY");
            }
            if (changeIfxDatatype.equalsIgnoreCase("TIMESTAMP")) {
                this.setDatatypeName("DATETIME YEAR TO FRACTION");
                if (this.getSize() != null) {
                    this.setOpenBrace(null);
                    this.setSize(null);
                    this.setClosedBrace(null);
                }
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toDB2String() {
        if (this.getDatatypeName() != null) {
            final String changeDB2Datatype = this.getDatatypeName();
            if (changeDB2Datatype.equalsIgnoreCase("TIMESTAMP") && this.getSize() != null) {
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            if (SwisSQLOptions.fromAccess && changeDB2Datatype.equalsIgnoreCase("DATETIME")) {
                this.setDatatypeName("TIMESTAMP");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (changeDB2Datatype.equalsIgnoreCase("DATETIME")) {
                this.setDatatypeName("TIMESTAMP");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("SMALLDATETIME")) {
                this.setDatatypeName("TIMESTAMP");
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toOracleString() {
        final String changeOracleDatatype = this.getDatatypeName();
        if (SwisSQLOptions.fromAccess && changeOracleDatatype.equalsIgnoreCase("DATETIME")) {
            this.setDatatypeName("DATE");
            this.setOpenBrace(null);
            this.setSize(null);
            this.setClosedBrace(null);
        }
        if (this.getDatatypeName() != null) {
            if (this.getDatatypeName().equalsIgnoreCase("time") || this.getDatatypeName().equalsIgnoreCase("timestamp")) {
                this.setDatatypeName("TIMESTAMP");
            }
            else if (this.getDatatypeName().equalsIgnoreCase("datetime2")) {
                this.setDatatypeName("TIMESTAMP");
            }
            else if (this.getDatatypeName().equalsIgnoreCase("datetimeoffset")) {
                this.setDatatypeName("TIMESTAMP");
                this.setWithLocalTimeZone("WITH TIME ZONE");
            }
            else {
                this.setDatatypeName("DATE");
                if (this.getSize() != null) {
                    this.setOpenBrace(null);
                    this.setSize(null);
                    this.setClosedBrace(null);
                }
                if (this.getArray() != null) {
                    this.setArray(null);
                }
            }
        }
    }
    
    @Override
    public void toMSSQLServerString() {
        if (this.getDatatypeName() != null) {
            final String changeSQLServerDatatype = this.getDatatypeName();
            if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("DATETIME")) {
                this.setDatatypeName(changeSQLServerDatatype);
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("TIMESTAMP")) {
                if (SwisSQLOptions.fromSybase || SwisSQLOptions.fromSQLServer) {
                    this.setDatatypeName("TIMESTAMP");
                }
                else {
                    this.setDatatypeName("DATETIME");
                }
                if (this.getSize() != null) {
                    this.setOpenBrace(null);
                    this.setSize(null);
                    this.setClosedBrace(null);
                }
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("DATE")) {
                this.setDatatypeName("DATETIME");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("TIME")) {
                this.setDatatypeName("DATETIME");
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toSybaseString() {
        if (this.getDatatypeName() != null) {
            final String changeSybaseDatatype = this.getDatatypeName();
            if (SwisSQLOptions.fromAccess && changeSybaseDatatype.equalsIgnoreCase("DATETIME")) {
                this.setDatatypeName(changeSybaseDatatype);
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (changeSybaseDatatype.equalsIgnoreCase("TIMESTAMP")) {
                this.setDatatypeName("DATETIME");
                if (this.getSize() != null) {
                    this.setOpenBrace(null);
                    this.setSize(null);
                    this.setClosedBrace(null);
                }
            }
            else if (changeSybaseDatatype.equalsIgnoreCase("DATE")) {
                this.setDatatypeName("DATETIME");
            }
            else if (changeSybaseDatatype.equalsIgnoreCase("TIME")) {
                this.setDatatypeName("DATETIME");
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toPostgreSQLString() {
        if (this.getDatatypeName() != null) {
            final String changePostgreSQLDatatype = this.getDatatypeName();
            if (changePostgreSQLDatatype.equalsIgnoreCase("TIMESTAMP")) {
                this.setDatatypeName("TIMESTAMP");
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("DATETIME")) {
                this.setDatatypeName("TIMESTAMP");
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("SMALLDATETIME")) {
                this.setDatatypeName("TIMESTAMP");
            }
        }
    }
    
    @Override
    public void toMySQLString() {
        if (this.getDatatypeName() != null) {
            final String changeSQLServerDatatype = this.getDatatypeName();
            if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("DATETIME")) {
                this.setDatatypeName(changeSQLServerDatatype);
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("DATE")) {
                this.setDatatypeName("DATETIME");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("TIME")) {
                this.setDatatypeName("DATETIME");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("SMALLDATETIME")) {
                this.setDatatypeName("DATETIME");
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toANSIString() {
        if (this.getDatatypeName() != null) {
            final String changeANSIDatatype = this.getDatatypeName();
            if (changeANSIDatatype.equalsIgnoreCase("TIMESTAMP")) {
                if (this.getSize() != null) {
                    this.setOpenBrace(null);
                    this.setSize(null);
                    this.setClosedBrace(null);
                }
            }
            else if (changeANSIDatatype.equalsIgnoreCase("DATETIME")) {
                this.setDatatypeName("TIMESTAMP");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("SMALLDATETIME")) {
                this.setDatatypeName("TIMESTAMP");
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toTimesTenString() {
        if (this.getDatatypeName() != null) {
            final String sourceType = this.getDatatypeName();
            if (sourceType.equalsIgnoreCase("DATETIME")) {
                this.setDatatypeName("TIMESTAMP");
            }
            else if (sourceType.equalsIgnoreCase("SMALLDATETIME")) {
                this.setDatatypeName("TIMESTAMP");
            }
            else if (sourceType.equalsIgnoreCase("TIMESTAMP")) {
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
                this.setWithLocalTimeZone(null);
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toNetezzaString() {
        if (this.getDatatypeName() != null) {
            final String changeNetezzaDatatype = this.getDatatypeName();
            if (changeNetezzaDatatype.equalsIgnoreCase("TIMESTAMP")) {
                if (this.getSize() != null) {
                    this.setOpenBrace(null);
                    this.setSize(null);
                    this.setClosedBrace(null);
                    this.setWithLocalTimeZone(null);
                }
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("DATETIME")) {
                this.setDatatypeName("TIMESTAMP");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("SMALLDATETIME")) {
                this.setDatatypeName("TIMESTAMP");
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toTeradataString() {
        if (this.getDatatypeName() != null) {
            final String changeTeradataDatatype = this.getDatatypeName();
            if (changeTeradataDatatype.equalsIgnoreCase("DATETIME")) {
                this.setDatatypeName("TIMESTAMP");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("SMALLDATETIME")) {
                this.setDatatypeName("TIMESTAMP");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("DATE")) {
                this.setDatatypeName("TIMESTAMP");
                this.setOpenBrace("(");
                this.setSize("0");
                this.setClosedBrace(")");
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    public Datatype copyObjectValues() {
        final DateClass newDateClass = new DateClass();
        newDateClass.setClosedBrace(this.closedBrace);
        newDateClass.setDatatypeName(this.getDatatypeName());
        newDateClass.setOpenBrace(this.openBrace);
        newDateClass.setSize(this.getSize());
        newDateClass.setArray(this.getArray());
        return newDateClass;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
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
        if (this.withLocalTimeZone != null) {
            sb.append(" " + this.withLocalTimeZone + " ");
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
