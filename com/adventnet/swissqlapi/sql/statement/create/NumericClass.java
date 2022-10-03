package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;

public class NumericClass implements Datatype
{
    private String datatypeName;
    private String openBrace;
    private String closedBrace;
    private String precision;
    private String scale;
    private String arrayStr;
    private boolean isTenroxRequirement;
    
    public NumericClass() {
        this.isTenroxRequirement = false;
    }
    
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
        if (size != null && size.indexOf(",") != -1) {
            this.setPrecision(size.substring(0, size.indexOf(",")));
            this.setScale(size.substring(size.indexOf(",") + 1));
        }
        else {
            this.setPrecision(size);
        }
    }
    
    public void setPrecision(final String precision) {
        this.precision = precision;
    }
    
    public void setScale(final String scale) {
        this.scale = scale;
    }
    
    @Override
    public String getDatatypeName() {
        return this.datatypeName;
    }
    
    public String getPrecision() {
        return this.precision;
    }
    
    public String getScale() {
        return this.scale;
    }
    
    @Override
    public String getOpenBrace() {
        return this.openBrace;
    }
    
    @Override
    public String getClosedBrace() {
        return this.closedBrace;
    }
    
    @Override
    public String getSize() {
        return null;
    }
    
    @Override
    public void toInformixString() {
        if (this.getDatatypeName() != null) {
            final String changeIfxDatatype = this.getDatatypeName();
            if (changeIfxDatatype.equalsIgnoreCase("TINYINT")) {
                this.setDatatypeName("SMALLINT");
            }
            else if (changeIfxDatatype.equalsIgnoreCase("FLOAT") || changeIfxDatatype.equalsIgnoreCase("FLOAT8") || changeIfxDatatype.equalsIgnoreCase("FLOAT4")) {
                if (this.getPrecision() != null) {
                    if (Integer.parseInt(this.getPrecision()) < 8 || changeIfxDatatype.equalsIgnoreCase("FLOAT4")) {
                        this.setDatatypeName("SMALLFLOAT");
                    }
                    else {
                        this.setDatatypeName("FLOAT");
                    }
                    this.setPrecision(null);
                    this.setOpenBrace(null);
                    this.setClosedBrace(null);
                }
                else {
                    this.setDatatypeName("FLOAT");
                }
            }
            else if (changeIfxDatatype.equalsIgnoreCase("BIGINT")) {
                this.setDatatypeName("INTEGER");
            }
            else if (changeIfxDatatype.equalsIgnoreCase("MEDIUMINT")) {
                this.setDatatypeName("INTEGER");
            }
            else if (changeIfxDatatype.equalsIgnoreCase("MONEY")) {
                this.setDatatypeName("MONEY");
                this.setOpenBrace("(");
                this.setPrecision("19");
                this.setScale("4");
                this.setClosedBrace(")");
            }
            else if (changeIfxDatatype.equalsIgnoreCase("SMALLMONEY")) {
                this.setDatatypeName("MONEY");
                this.setOpenBrace("(");
                this.setPrecision("10");
                this.setScale("4");
                this.setClosedBrace(")");
            }
            else if (changeIfxDatatype.equalsIgnoreCase("NUMBER") || changeIfxDatatype.equalsIgnoreCase("NUM")) {
                this.setDatatypeName("NUMERIC");
            }
            else if (changeIfxDatatype.equalsIgnoreCase("DOUBLE")) {
                this.setDatatypeName("DOUBLE PRECISION");
            }
            else if (changeIfxDatatype.equalsIgnoreCase("FIXED")) {
                this.setDatatypeName("NUMERIC");
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
            if (SwisSQLOptions.fromAccess && (changeDB2Datatype.equalsIgnoreCase("INTEGER") || changeDB2Datatype.equalsIgnoreCase("REAL") || changeDB2Datatype.equalsIgnoreCase("SMALLINT"))) {
                this.setDatatypeName(changeDB2Datatype);
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (this.getPrecision() != null && (changeDB2Datatype.equalsIgnoreCase("INT") || changeDB2Datatype.equalsIgnoreCase("INTEGER") || changeDB2Datatype.equalsIgnoreCase("INT2") || changeDB2Datatype.equalsIgnoreCase("INT4") || changeDB2Datatype.equalsIgnoreCase("MEDIUMINT") || changeDB2Datatype.equalsIgnoreCase("TINYINT") || changeDB2Datatype.equalsIgnoreCase("SMALLINT"))) {
                this.setDatatypeName("INTEGER");
                this.setPrecision(null);
                this.setOpenBrace(null);
                this.setClosedBrace(null);
            }
            else if (SwisSQLOptions.fromAccess && changeDB2Datatype.equalsIgnoreCase("BYTE")) {
                this.setDatatypeName("SMALLINT");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (SwisSQLOptions.fromAccess && changeDB2Datatype.equalsIgnoreCase("COUNTER")) {
                this.setDatatypeName("INT GENERATED BY DEFAULT AS IDENTITY(START WITH 1 INCREMENT BY 1)");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (SwisSQLOptions.fromAccess && changeDB2Datatype.equalsIgnoreCase("CURRENCY")) {
                this.setDatatypeName("DECIMAL");
            }
            else if (this.getPrecision() != null && this.getScale() != null && changeDB2Datatype.equalsIgnoreCase("FLOAT")) {
                this.setDatatypeName("DECIMAL");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("BIGINT")) {
                this.setDatatypeName("BIGINT");
                this.setPrecision(null);
                this.setOpenBrace(null);
                this.setClosedBrace(null);
            }
            else if (changeDB2Datatype.equalsIgnoreCase("MEDIUMINT")) {
                this.setDatatypeName("INTEGER");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("TINYINT")) {
                this.setDatatypeName("SMALLINT");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("NUMBER")) {
                this.setDatatypeName("NUM");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("INT2")) {
                this.setDatatypeName("SMALLINT");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("INT4")) {
                this.setDatatypeName("INTEGER");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("INT8")) {
                this.setDatatypeName("INTEGER");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("FLOAT4")) {
                this.setDatatypeName("FLOAT");
                this.setOpenBrace("(");
                this.setPrecision("24");
                this.setClosedBrace(")");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("FLOAT8")) {
                this.setDatatypeName("FLOAT");
                this.setOpenBrace("(");
                this.setPrecision("48");
                this.setClosedBrace(")");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("MONEY")) {
                this.setDatatypeName("DECIMAL");
                this.setOpenBrace("(");
                this.setPrecision("19");
                this.setScale("4");
                this.setClosedBrace(")");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("SMALLMONEY")) {
                this.setDatatypeName("DECIMAL");
                this.setOpenBrace("(");
                this.setPrecision("10");
                this.setScale("4");
                this.setClosedBrace(")");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("FIXED")) {
                this.setDatatypeName("DECIMAL");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("DOUBLE")) {
                this.setOpenBrace(null);
                this.setPrecision(null);
                this.setScale(null);
                this.setClosedBrace(null);
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toOracleString() {
        if (this.getDatatypeName() != null) {
            final String changeOracleDatatype = this.getDatatypeName();
            if (SwisSQLOptions.fromAccess && (changeOracleDatatype.equalsIgnoreCase("INTEGER") || changeOracleDatatype.equalsIgnoreCase("REAL") || changeOracleDatatype.equalsIgnoreCase("SMALLINT"))) {
                this.setDatatypeName(changeOracleDatatype);
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (SwisSQLOptions.fromAccess && changeOracleDatatype.equalsIgnoreCase("BYTE")) {
                this.setDatatypeName("SMALLINT");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (SwisSQLOptions.fromAccess && changeOracleDatatype.equalsIgnoreCase("COUNTER")) {
                this.setDatatypeName("INT");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (SwisSQLOptions.fromAccess && changeOracleDatatype.equalsIgnoreCase("GUID")) {
                this.setDatatypeName("CHAR(36)");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (SwisSQLOptions.fromAccess && changeOracleDatatype.equalsIgnoreCase("CURRENCY")) {
                this.setDatatypeName("DECIMAL");
            }
            else if (this.getPrecision() != null && (changeOracleDatatype.equalsIgnoreCase("INT") || changeOracleDatatype.equalsIgnoreCase("INTEGER") || changeOracleDatatype.equalsIgnoreCase("INT2") || changeOracleDatatype.equalsIgnoreCase("INT4") || changeOracleDatatype.equalsIgnoreCase("MEDIUMINT") || changeOracleDatatype.equalsIgnoreCase("TINYINT") || changeOracleDatatype.equalsIgnoreCase("BIGINT") || changeOracleDatatype.equalsIgnoreCase("SMALLINT"))) {
                this.setPrecisionScale();
                this.setScale(null);
                this.setDatatypeName("NUMBER");
            }
            else if (this.getPrecision() != null && this.getScale() != null && changeOracleDatatype.equalsIgnoreCase("FLOAT")) {
                this.setDatatypeName("DECIMAL");
            }
            else if (changeOracleDatatype.equalsIgnoreCase("BIGINT")) {
                this.setDatatypeName("INTEGER");
            }
            else if (changeOracleDatatype.equalsIgnoreCase("MEDIUMINT")) {
                this.setDatatypeName("INTEGER");
            }
            else if (changeOracleDatatype.equalsIgnoreCase("TINYINT")) {
                if (this.isTenroxRequirement) {
                    this.setDatatypeName("NUMBER");
                    this.setOpenBrace("(");
                    this.setClosedBrace(")");
                    this.setPrecision("11");
                }
                else {
                    this.setDatatypeName("SMALLINT");
                }
            }
            else if (changeOracleDatatype.equalsIgnoreCase("NUM")) {
                this.setDatatypeName("NUMBER");
            }
            else if (changeOracleDatatype.equalsIgnoreCase("INT2")) {
                this.setDatatypeName("SMALLINT");
            }
            else if (changeOracleDatatype.equalsIgnoreCase("INT4")) {
                if (this.isTenroxRequirement) {
                    this.setDatatypeName("NUMBER");
                    this.setOpenBrace("(");
                    this.setClosedBrace(")");
                    this.setPrecision("11");
                }
                else {
                    this.setDatatypeName("INTEGER");
                }
            }
            else if (changeOracleDatatype.equalsIgnoreCase("INT8")) {
                if (this.isTenroxRequirement) {
                    this.setDatatypeName("NUMBER");
                    this.setOpenBrace("(");
                    this.setClosedBrace(")");
                    this.setPrecision("11");
                }
                else {
                    this.setDatatypeName("INTEGER");
                }
            }
            else if (changeOracleDatatype.equalsIgnoreCase("FLOAT4")) {
                if (this.isTenroxRequirement) {
                    this.setDatatypeName("NUMBER");
                    this.setOpenBrace("(");
                    this.setPrecision("11");
                    this.setScale("2");
                    this.setClosedBrace(")");
                }
                else {
                    this.setDatatypeName("FLOAT");
                    this.setOpenBrace("(");
                    this.setPrecision("24");
                    this.setClosedBrace(")");
                }
            }
            else if (changeOracleDatatype.equalsIgnoreCase("FLOAT8")) {
                if (this.isTenroxRequirement) {
                    this.setDatatypeName("NUMBER");
                    this.setOpenBrace("(");
                    this.setPrecision("11");
                    this.setScale("2");
                    this.setClosedBrace(")");
                }
                else {
                    this.setDatatypeName("FLOAT");
                    this.setOpenBrace("(");
                    this.setPrecision("48");
                    this.setClosedBrace(")");
                }
            }
            else if (changeOracleDatatype.equalsIgnoreCase("DEC")) {
                if (this.isTenroxRequirement) {
                    this.setDatatypeName("NUMBER");
                    this.setOpenBrace("(");
                    this.setPrecision("11");
                    this.setScale("2");
                    this.setClosedBrace(")");
                }
                else {
                    this.setDatatypeName("DECIMAL");
                }
            }
            else if (changeOracleDatatype.equalsIgnoreCase("DOUBLE")) {
                this.setDatatypeName("DOUBLE PRECISION");
                this.setOpenBrace(null);
                this.setPrecision(null);
                this.setScale(null);
                this.setClosedBrace(null);
            }
            else if (changeOracleDatatype.equalsIgnoreCase("MONEY")) {
                if (this.isTenroxRequirement) {
                    this.setDatatypeName("NUMBER");
                    this.setOpenBrace("(");
                    this.setPrecision("11");
                    this.setScale("2");
                    this.setClosedBrace(")");
                }
                else {
                    this.setDatatypeName("DECIMAL");
                    this.setOpenBrace("(");
                    this.setPrecision("19");
                    this.setScale("4");
                    this.setClosedBrace(")");
                }
            }
            else if (changeOracleDatatype.equalsIgnoreCase("SMALLMONEY")) {
                if (this.isTenroxRequirement) {
                    this.setDatatypeName("NUMBER");
                    this.setOpenBrace("(");
                    this.setPrecision("11");
                    this.setScale("2");
                    this.setClosedBrace(")");
                }
                else {
                    this.setDatatypeName("DECIMAL");
                    this.setOpenBrace("(");
                    this.setPrecision("10");
                    this.setScale("4");
                    this.setClosedBrace(")");
                }
            }
            else if (changeOracleDatatype.equalsIgnoreCase("INT") || changeOracleDatatype.equalsIgnoreCase("SMALLINT") || changeOracleDatatype.equalsIgnoreCase("INTEGER")) {
                if (this.isTenroxRequirement) {
                    this.setDatatypeName("NUMBER");
                    this.setOpenBrace("(");
                    this.setClosedBrace(")");
                    this.setPrecision("11");
                }
                else {
                    this.setDatatypeName(changeOracleDatatype);
                }
            }
            else if (changeOracleDatatype.equalsIgnoreCase("FLOAT") || changeOracleDatatype.equalsIgnoreCase("DECIMAL")) {
                if (this.isTenroxRequirement) {
                    this.setDatatypeName("NUMBER");
                    this.setOpenBrace("(");
                    this.setPrecision("11");
                    this.setScale("2");
                    this.setClosedBrace(")");
                }
                else {
                    this.setDatatypeName(changeOracleDatatype);
                }
            }
            else if (changeOracleDatatype.equalsIgnoreCase("FIXED")) {
                this.setDatatypeName("DECIMAL");
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toMSSQLServerString() {
        if (this.getDatatypeName() != null) {
            final String changeSQLServerDatatype = this.getDatatypeName();
            if (SwisSQLOptions.fromAccess && (changeSQLServerDatatype.equalsIgnoreCase("INTEGER") || changeSQLServerDatatype.equalsIgnoreCase("REAL") || changeSQLServerDatatype.equalsIgnoreCase("SMALLINT"))) {
                this.setDatatypeName(changeSQLServerDatatype);
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (this.getPrecision() != null && (changeSQLServerDatatype.equalsIgnoreCase("INT") || changeSQLServerDatatype.equalsIgnoreCase("INTEGER") || changeSQLServerDatatype.equalsIgnoreCase("INT2") || changeSQLServerDatatype.equalsIgnoreCase("INT4") || changeSQLServerDatatype.equalsIgnoreCase("MEDIUMINT") || changeSQLServerDatatype.equalsIgnoreCase("TINYINT") || changeSQLServerDatatype.equalsIgnoreCase("BIGINT") || changeSQLServerDatatype.equalsIgnoreCase("SMALLINT"))) {
                this.setPrecisionScale();
                this.setDatatypeName("NUMERIC");
            }
            else if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("BYTE")) {
                this.setDatatypeName("TINYINT");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("COUNTER")) {
                this.setDatatypeName("INT IDENTITY");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("GUID")) {
                this.setDatatypeName("UNIQUEIDENTIFIER");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("CURRENCY")) {
                this.setDatatypeName("MONEY");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setPrecision(null);
                this.setClosedBrace(null);
                this.setScale(null);
            }
            else if (this.getPrecision() != null && this.getScale() != null && changeSQLServerDatatype.equalsIgnoreCase("FLOAT")) {
                this.setDatatypeName("DECIMAL");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("INTEGER")) {
                this.setDatatypeName("BIGINT");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("MEDIUMINT")) {
                this.setDatatypeName("INT");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("NUMBER") || changeSQLServerDatatype.equalsIgnoreCase("NUM")) {
                this.setPrecisionScale();
                this.setDatatypeName("NUMERIC");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("INT2")) {
                this.setDatatypeName("SMALLINT");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("INT4")) {
                this.setDatatypeName("INT");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("INT8")) {
                this.setDatatypeName("BIGINT");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("FLOAT4")) {
                this.setDatatypeName("FLOAT");
                this.setOpenBrace("(");
                this.setPrecision("24");
                this.setClosedBrace(")");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("FLOAT8")) {
                this.setDatatypeName("FLOAT");
                this.setOpenBrace("(");
                this.setPrecision("48");
                this.setClosedBrace(")");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("DEC")) {
                this.setDatatypeName("DECIMAL");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("DOUBLE")) {
                this.setDatatypeName("FLOAT");
                this.setOpenBrace("(");
                this.setPrecision("53");
                this.setScale(null);
                this.setClosedBrace(")");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("FIXED")) {
                this.setDatatypeName("DECIMAL");
            }
        }
    }
    
    @Override
    public void toSybaseString() {
        if (this.getDatatypeName() != null) {
            final String changeSQLServerDatatype = this.getDatatypeName();
            if (SwisSQLOptions.fromAccess && (changeSQLServerDatatype.equalsIgnoreCase("INTEGER") || changeSQLServerDatatype.equalsIgnoreCase("REAL") || changeSQLServerDatatype.equalsIgnoreCase("SMALLINT"))) {
                this.setDatatypeName(changeSQLServerDatatype);
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (this.getPrecision() != null && (changeSQLServerDatatype.equalsIgnoreCase("INT") || changeSQLServerDatatype.equalsIgnoreCase("INTEGER") || changeSQLServerDatatype.equalsIgnoreCase("INT2") || changeSQLServerDatatype.equalsIgnoreCase("INT4") || changeSQLServerDatatype.equalsIgnoreCase("MEDIUMINT") || changeSQLServerDatatype.equalsIgnoreCase("TINYINT") || changeSQLServerDatatype.equalsIgnoreCase("BIGINT") || changeSQLServerDatatype.equalsIgnoreCase("SMALLINT"))) {
                this.setPrecisionScale();
                this.setDatatypeName("NUMERIC");
            }
            else if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("BYTE")) {
                this.setDatatypeName("TINYINT");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("COUNTER")) {
                this.setDatatypeName("NUMERIC IDENTITY");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("CURRENCY")) {
                this.setDatatypeName("MONEY");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setPrecision(null);
                this.setClosedBrace(null);
                this.setScale(null);
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("INTEGER") || changeSQLServerDatatype.equalsIgnoreCase("MEDIUMINT") || changeSQLServerDatatype.equalsIgnoreCase("INT4")) {
                this.setDatatypeName("INT");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("BIGINT")) {
                this.setDatatypeName("DECIMAL");
                this.setOpenBrace("(");
                this.setClosedBrace(")");
                this.setPrecision("19");
                this.setScale("0");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("NUMBER") || changeSQLServerDatatype.equalsIgnoreCase("NUM")) {
                if (this.getPrecision() != null && Integer.parseInt(this.getPrecision()) > 38) {
                    this.setPrecision("38");
                }
                this.setDatatypeName("NUMERIC");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("INT2")) {
                this.setDatatypeName("SMALLINT");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("INT8")) {
                this.setDatatypeName("BIGINT");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("FLOAT4")) {
                this.setDatatypeName("FLOAT");
                this.setOpenBrace("(");
                this.setPrecision("24");
                this.setClosedBrace(")");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("FLOAT8")) {
                this.setDatatypeName("FLOAT");
                this.setOpenBrace("(");
                this.setPrecision("48");
                this.setClosedBrace(")");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("FLOAT")) {
                this.setScale(null);
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("DEC")) {
                this.setDatatypeName("DECIMAL");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("DOUBLE")) {
                this.setDatatypeName("DOUBLE PRECISION");
                this.setOpenBrace(null);
                this.setPrecision(null);
                this.setScale(null);
                this.setClosedBrace(null);
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("FIXED")) {
                this.setDatatypeName("DECIMAL");
            }
        }
    }
    
    @Override
    public void toPostgreSQLString() {
        if (this.getDatatypeName() != null) {
            final String changePostgreSQLDatatype = this.getDatatypeName();
            if (changePostgreSQLDatatype.equalsIgnoreCase("INTEGER") || changePostgreSQLDatatype.equalsIgnoreCase("BIGINT") || changePostgreSQLDatatype.equalsIgnoreCase("INT")) {
                this.setDatatypeName("INT8");
                if (this.getPrecision() != null || this.getSize() != null) {
                    this.removeSizeForPostgreSQLDatatypes();
                }
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("MEDIUMINT")) {
                this.setDatatypeName("INT4");
                if (this.getPrecision() != null || this.getSize() != null) {
                    this.removeSizeForPostgreSQLDatatypes();
                }
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("NUMBER") || changePostgreSQLDatatype.equalsIgnoreCase("NUM")) {
                this.setDatatypeName("NUMERIC");
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("TINYINT") || changePostgreSQLDatatype.equalsIgnoreCase("SMALLINT")) {
                this.setDatatypeName("INT2");
                if (this.getPrecision() != null || this.getSize() != null) {
                    this.removeSizeForPostgreSQLDatatypes();
                }
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("FLOAT")) {
                this.setDatatypeName("FLOAT");
                if (this.getScale() != null) {
                    this.setScale(null);
                }
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("SMALLMONEY")) {
                this.setDatatypeName("NUMERIC");
                this.setOpenBrace("(");
                this.setPrecision("19");
                this.setScale("4");
                this.setClosedBrace(")");
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("MONEY")) {
                this.setDatatypeName("NUMERIC");
                this.setOpenBrace("(");
                this.setPrecision("19");
                this.setScale("4");
                this.setClosedBrace(")");
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("DOUBLE")) {
                this.setDatatypeName("DOUBLE PRECISION");
                if (this.getPrecision() != null || this.getSize() != null) {
                    this.removeSizeForPostgreSQLDatatypes();
                }
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("FIXED")) {
                this.setDatatypeName("NUMERIC");
            }
        }
    }
    
    public void removeSizeForPostgreSQLDatatypes() {
        this.setPrecision(null);
        this.setScale(null);
        this.setSize(null);
        this.setOpenBrace(null);
        this.setClosedBrace(null);
    }
    
    @Override
    public void toMySQLString() {
        if (this.getDatatypeName() != null) {
            final String changeMySQLDatatype = this.getDatatypeName();
            if (SwisSQLOptions.fromAccess && changeMySQLDatatype.equalsIgnoreCase("BYTE")) {
                this.setDatatypeName("TINYINT UNSIGNED");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (SwisSQLOptions.fromAccess && changeMySQLDatatype.equalsIgnoreCase("REAL")) {
                this.setDatatypeName("FLOAT");
            }
            else if (SwisSQLOptions.fromAccess && changeMySQLDatatype.equalsIgnoreCase("CURRENCY")) {
                this.setDatatypeName("DECIMAL");
                this.setOpenBrace("(");
                this.setSize("20");
                this.setScale("4");
                this.setClosedBrace(")");
            }
            else if (SwisSQLOptions.fromAccess && changeMySQLDatatype.equalsIgnoreCase("COUNTER")) {
                this.setDatatypeName("INT AUTO_INCREMENT");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (SwisSQLOptions.fromAccess && changeMySQLDatatype.equalsIgnoreCase("DOUBLE")) {
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (changeMySQLDatatype.equalsIgnoreCase("INT2")) {
                this.setDatatypeName("SMALLINT");
            }
            else if (changeMySQLDatatype.equalsIgnoreCase("INT4")) {
                this.setDatatypeName("INTEGER");
            }
            else if (changeMySQLDatatype.equalsIgnoreCase("INT8")) {
                this.setDatatypeName("INTEGER");
            }
            else if (changeMySQLDatatype.equalsIgnoreCase("FLOAT4")) {
                this.setDatatypeName("FLOAT");
                this.setOpenBrace("(");
                this.setPrecision("24");
                this.setClosedBrace(")");
            }
            if (changeMySQLDatatype.equalsIgnoreCase("NUMBER") || changeMySQLDatatype.equalsIgnoreCase("NUM")) {
                this.setDatatypeName("NUMERIC");
            }
            else if (changeMySQLDatatype.equalsIgnoreCase("FLOAT8")) {
                this.setDatatypeName("FLOAT");
                this.setOpenBrace("(");
                this.setPrecision("48");
                this.setClosedBrace(")");
            }
            else if (changeMySQLDatatype.equalsIgnoreCase("MONEY")) {
                this.setDatatypeName("DECIMAL");
                this.setOpenBrace("(");
                this.setPrecision("19");
                this.setScale("4");
                this.setClosedBrace(")");
            }
            else if (changeMySQLDatatype.equalsIgnoreCase("SMALLMONEY")) {
                this.setDatatypeName("DECIMAL");
                this.setOpenBrace("(");
                this.setPrecision("10");
                this.setScale("4");
                this.setClosedBrace(")");
            }
            else if (changeMySQLDatatype.equalsIgnoreCase("FIXED")) {
                this.setDatatypeName("DECIMAL");
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
            if (changeANSIDatatype.equalsIgnoreCase("BIGINT")) {
                this.setDatatypeName("INTEGER");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("MEDIUMINT")) {
                this.setDatatypeName("INTEGER");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("TINYINT")) {
                this.setDatatypeName("SMALLINT");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("INT2")) {
                this.setDatatypeName("SMALLINT");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("DOUBLE")) {
                this.setDatatypeName("DOUBLE PRECISION");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("INT4")) {
                this.setDatatypeName("INTEGER");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("INT8")) {
                this.setDatatypeName("INTEGER");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("FLOAT4")) {
                this.setDatatypeName("FLOAT");
                this.setOpenBrace("(");
                this.setPrecision("24");
                this.setClosedBrace(")");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("FLOAT8")) {
                this.setDatatypeName("FLOAT");
                this.setOpenBrace("(");
                this.setPrecision("48");
                this.setClosedBrace(")");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("NUMBER") || changeANSIDatatype.equalsIgnoreCase("NUM")) {
                this.setDatatypeName("NUMERIC");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("DEC")) {
                this.setDatatypeName("DECIMAL");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("DOUBLE")) {
                this.setDatatypeName("DOUBLE PRECISION");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("MONEY")) {
                this.setDatatypeName("DECIMAL");
                this.setOpenBrace("(");
                this.setPrecision("19");
                this.setScale("4");
                this.setClosedBrace(")");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("SMALLMONEY")) {
                this.setDatatypeName("DECIMAL");
                this.setOpenBrace("(");
                this.setPrecision("10");
                this.setScale("4");
                this.setClosedBrace(")");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("FIXED")) {
                this.setDatatypeName("DECIMAL");
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
            if (this.getPrecision() != null && (sourceType.equalsIgnoreCase("INT") || sourceType.equalsIgnoreCase("INTEGER") || sourceType.equalsIgnoreCase("INT2") || sourceType.equalsIgnoreCase("INT4") || sourceType.equalsIgnoreCase("MEDIUMINT") || sourceType.equalsIgnoreCase("TINYINT") || sourceType.equalsIgnoreCase("BIGINT") || sourceType.equalsIgnoreCase("SMALLINT"))) {
                this.setDatatypeName("NUMERIC");
            }
            else if (sourceType.equalsIgnoreCase("MEDIUMINT") || sourceType.equalsIgnoreCase("INT4")) {
                this.setDatatypeName("INT");
            }
            else if (sourceType.equalsIgnoreCase("NUMBER") || sourceType.equalsIgnoreCase("NUM")) {
                if (this.getPrecision() != null && Integer.parseInt(this.getPrecision()) > 40) {
                    this.setPrecision("40");
                }
                this.setDatatypeName("NUMERIC");
            }
            else if (sourceType.equalsIgnoreCase("INT2")) {
                this.setDatatypeName("SMALLINT");
            }
            else if (sourceType.equalsIgnoreCase("INT8")) {
                this.setDatatypeName("BIGINT");
            }
            else if (sourceType.equalsIgnoreCase("FLOAT")) {
                final String precision_str = this.getPrecision();
                int precision_int = -1;
                try {
                    precision_int = Integer.parseInt(precision_str);
                    if (precision_int < 24) {
                        this.setPrecision("24");
                    }
                    else {
                        this.setPrecision("53");
                    }
                }
                catch (final Exception ex) {}
            }
            else if (sourceType.equalsIgnoreCase("FLOAT4")) {
                this.setDatatypeName("FLOAT");
                this.setOpenBrace("(");
                this.setPrecision("24");
                this.setClosedBrace(")");
            }
            else if (sourceType.equalsIgnoreCase("FLOAT8")) {
                this.setDatatypeName("FLOAT");
                this.setOpenBrace("(");
                this.setPrecision("53");
                this.setClosedBrace(")");
            }
            else if (sourceType.equalsIgnoreCase("FIXED")) {
                this.setDatatypeName("DECIMAL");
            }
            else if (sourceType.equalsIgnoreCase("MONEY")) {
                this.setDatatypeName("DECIMAL");
                this.setOpenBrace("(");
                this.setPrecision("19");
                this.setScale("4");
                this.setClosedBrace(")");
            }
            else if (sourceType.equalsIgnoreCase("SMALLMONEY")) {
                this.setDatatypeName("DECIMAL");
                this.setOpenBrace("(");
                this.setPrecision("10");
                this.setScale("4");
                this.setClosedBrace(")");
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
            if (changeNetezzaDatatype.equalsIgnoreCase("BIGINT")) {
                this.setDatatypeName("BIGINT");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("MEDIUMINT")) {
                this.setDatatypeName("INTEGER");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("TINYINT")) {
                this.setDatatypeName("SMALLINT");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("INT2")) {
                this.setDatatypeName("SMALLINT");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("DOUBLE")) {
                this.setDatatypeName("DOUBLE PRECISION");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("INT4") || changeNetezzaDatatype.equalsIgnoreCase("INT") || changeNetezzaDatatype.equalsIgnoreCase("INTEGER")) {
                this.setDatatypeName("INTEGER");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("INT8")) {
                this.setDatatypeName("INTEGER");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("FLOAT4")) {
                this.setDatatypeName("FLOAT");
                this.setOpenBrace("(");
                this.setPrecision("24");
                this.setClosedBrace(")");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("FLOAT8")) {
                this.setDatatypeName("FLOAT");
                this.setOpenBrace("(");
                this.setPrecision("48");
                this.setClosedBrace(")");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("NUMBER") || changeNetezzaDatatype.equalsIgnoreCase("NUM") || changeNetezzaDatatype.equalsIgnoreCase("NUMERIC")) {
                this.setDatatypeName("NUMERIC");
                this.handleNumericTypeForNetezza();
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("DEC") || changeNetezzaDatatype.equalsIgnoreCase("DECIMAL")) {
                this.setDatatypeName("NUMERIC");
                this.handleNumericTypeForNetezza();
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("DOUBLE")) {
                this.setDatatypeName("DOUBLE PRECISION");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("MONEY")) {
                this.setDatatypeName("NUMERIC");
                this.setOpenBrace("(");
                this.setPrecision("19");
                this.setScale("4");
                this.setClosedBrace(")");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("SMALLMONEY")) {
                this.setDatatypeName("NUMERIC");
                this.setOpenBrace("(");
                this.setPrecision("10");
                this.setScale("4");
                this.setClosedBrace(")");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("FIXED")) {
                this.setDatatypeName("NUMERIC");
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
            if (changeTeradataDatatype.equalsIgnoreCase("BIGINT")) {
                this.setDatatypeName("INTEGER");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("MEDIUMINT")) {
                this.setDatatypeName("INTEGER");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("TINYINT")) {
                this.setDatatypeName("SMALLINT");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("INT2")) {
                this.setDatatypeName("SMALLINT");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("DOUBLE")) {
                this.setDatatypeName("DOUBLE PRECISION");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("INT4")) {
                this.setDatatypeName("INTEGER");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("INT8")) {
                this.setDatatypeName("INTEGER");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("FLOAT4")) {
                this.setDatatypeName("FLOAT");
                this.setOpenBrace("(");
                this.setPrecision("24");
                this.setClosedBrace(")");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("FLOAT8")) {
                this.setDatatypeName("FLOAT");
                this.setOpenBrace("(");
                this.setPrecision("48");
                this.setClosedBrace(")");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("NUMBER") || changeTeradataDatatype.equalsIgnoreCase("NUM") || changeTeradataDatatype.equalsIgnoreCase("NUMERIC")) {
                this.setDatatypeName("DECIMAL");
                if (this.getPrecision() == null && this.getScale() == null) {
                    this.setOpenBrace("(");
                    this.setPrecision("38");
                    this.setScale("14");
                    this.setClosedBrace(")");
                }
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("DEC")) {
                this.setDatatypeName("DECIMAL");
                if (SwisSQLAPI.convertToTeradata && this.getPrecision() == null && this.getScale() == null) {
                    this.setOpenBrace("(");
                    this.setPrecision("38");
                    this.setScale("16");
                    this.setClosedBrace(")");
                }
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("DOUBLE")) {
                this.setDatatypeName("DOUBLE PRECISION");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("MONEY")) {
                this.setDatatypeName("DECIMAL");
                this.setOpenBrace("(");
                this.setPrecision("19");
                this.setScale("4");
                this.setClosedBrace(")");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("SMALLMONEY")) {
                this.setDatatypeName("DECIMAL");
                this.setOpenBrace("(");
                this.setPrecision("10");
                this.setScale("4");
                this.setClosedBrace(")");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("FIXED")) {
                this.setDatatypeName("DECIMAL");
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    public Datatype copyObjectValues() {
        final NumericClass newNumericClass = new NumericClass();
        newNumericClass.setClosedBrace(this.closedBrace);
        newNumericClass.setDatatypeName(this.getDatatypeName());
        newNumericClass.setOpenBrace(this.openBrace);
        newNumericClass.setPrecision(this.getPrecision());
        newNumericClass.setScale(this.getScale());
        return newNumericClass;
    }
    
    public void setPrecisionScale() {
        if (this.getPrecision() != null) {
            try {
                final int prec = Integer.parseInt(this.precision);
                if (prec >= 0 && prec <= 9 && this.scale != null) {
                    try {
                        final int scl = Integer.parseInt(this.scale);
                        if (scl > 4) {
                            this.scale = "4";
                        }
                    }
                    catch (final NumberFormatException ex) {}
                }
                if (prec >= 10 && prec <= 19 && this.scale != null) {
                    try {
                        final int scl = Integer.parseInt(this.scale);
                        if (scl > 8) {
                            this.scale = "8";
                        }
                    }
                    catch (final NumberFormatException ex2) {}
                }
                if (prec >= 20 && prec <= 28 && this.scale != null) {
                    try {
                        final int scl = Integer.parseInt(this.scale);
                        if (scl > 12) {
                            this.scale = "12";
                        }
                    }
                    catch (final NumberFormatException ex3) {}
                }
                if (prec >= 29 && prec <= 38 && this.scale != null) {
                    try {
                        final int scl = Integer.parseInt(this.scale);
                        if (scl > 16) {
                            this.scale = "16";
                        }
                    }
                    catch (final NumberFormatException ex4) {}
                }
                if (prec >= 38) {
                    this.setPrecision("38");
                    if (this.scale != null) {
                        try {
                            final int scl = Integer.parseInt(this.scale);
                            if (scl > 16) {
                                this.scale = "16";
                            }
                        }
                        catch (final NumberFormatException ex5) {}
                    }
                }
            }
            catch (final NumberFormatException ex6) {}
        }
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
        if (this.precision != null) {
            sb.append(this.precision);
        }
        if (this.scale != null) {
            sb.append(", " + this.scale);
        }
        if (this.closedBrace != null) {
            sb.append(this.closedBrace);
        }
        if (this.arrayStr != null) {
            sb.append(this.arrayStr);
        }
        return sb.toString();
    }
    
    private void handleNumericTypeForNetezza() {
        try {
            if (this.getPrecision() != null && this.getScale() != null) {
                final int precVal = Integer.parseInt(this.getPrecision());
                final int scaleVal = Integer.parseInt(this.getScale());
                if (scaleVal == 0) {
                    if (precVal <= 9) {
                        this.setDatatypeName("INTEGER");
                        this.setPrecision(null);
                        this.setOpenBrace(null);
                        this.setScale(null);
                        this.setClosedBrace(null);
                    }
                    else if (precVal > 9 && precVal <= 18) {
                        this.setDatatypeName("BIGINT");
                        this.setPrecision(null);
                        this.setOpenBrace(null);
                        this.setScale(null);
                        this.setClosedBrace(null);
                    }
                    else if (precVal > 38) {
                        this.setPrecision("38");
                    }
                }
                else if (precVal > 38) {
                    this.setPrecision("38");
                }
            }
            else if (this.getPrecision() != null && this.getScale() == null) {
                final int precVal = Integer.parseInt(this.getPrecision());
                if (precVal <= 9) {
                    this.setDatatypeName("INTEGER");
                    this.setPrecision(null);
                    this.setOpenBrace(null);
                    this.setScale(null);
                    this.setClosedBrace(null);
                }
                else if (precVal > 9 && precVal <= 18) {
                    this.setDatatypeName("BIGINT");
                    this.setPrecision(null);
                    this.setOpenBrace(null);
                    this.setScale(null);
                    this.setClosedBrace(null);
                }
                else if (precVal > 38) {
                    this.setPrecision("38");
                }
            }
        }
        catch (final NumberFormatException nfe) {
            nfe.printStackTrace();
        }
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
