package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;

public class QuotedIdentifierDatatype implements Datatype
{
    private String datatypeName;
    private String openBrace;
    private String closedBrace;
    private String precision;
    private String scale;
    private String varyingType;
    private String size;
    private String arrayStr;
    private String withLocalTimeZone;
    
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
    public void setSize(final String str) {
        this.size = str;
    }
    
    public void setPrecision(final String precision) {
        this.precision = precision;
    }
    
    public void setScale(final String scale) {
        this.scale = scale;
    }
    
    public void setVarying(final String varyingType) {
        this.varyingType = varyingType;
    }
    
    public void setWithLocalTimeZone(final String withLocalTimeZone) {
        this.withLocalTimeZone = withLocalTimeZone;
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
    
    public String getVarying() {
        return this.varyingType;
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
    
    public String getWithLocalTimeZone() {
        return this.withLocalTimeZone;
    }
    
    public String getObjectTypeForTheCorrespondingDataTypeName(final String datatypeVariable) {
        if (datatypeVariable.equalsIgnoreCase("INTEGER") || datatypeVariable.equalsIgnoreCase("INT") || datatypeVariable.equalsIgnoreCase("INT2") || datatypeVariable.equalsIgnoreCase("INT4") || datatypeVariable.equalsIgnoreCase("INT8") || datatypeVariable.equalsIgnoreCase("FLOAT") || datatypeVariable.equalsIgnoreCase("FLOAT4") || datatypeVariable.equalsIgnoreCase("FLOAT8") || datatypeVariable.equalsIgnoreCase("NUMBER") || datatypeVariable.equalsIgnoreCase("NUM") || datatypeVariable.equalsIgnoreCase("NUMERIC") || datatypeVariable.equalsIgnoreCase("DECIMAL") || datatypeVariable.equalsIgnoreCase("SMALLMONEY") || datatypeVariable.equalsIgnoreCase("MONEY") || datatypeVariable.equalsIgnoreCase("REAL") || datatypeVariable.startsWith("DOUBLE") || datatypeVariable.equalsIgnoreCase("BIGINT") || datatypeVariable.equalsIgnoreCase("MEDIUMINT") || datatypeVariable.equalsIgnoreCase("SMALLINT") || datatypeVariable.equalsIgnoreCase("TINYINT")) {
            return "NUMERIC";
        }
        if (datatypeVariable.startsWith("NATIONAL") || datatypeVariable.equalsIgnoreCase("CHAR") || datatypeVariable.equalsIgnoreCase("LONG") || datatypeVariable.equalsIgnoreCase("VARCHAR") || datatypeVariable.equalsIgnoreCase("K_LONG") || datatypeVariable.equalsIgnoreCase("VARCHAR") || datatypeVariable.equalsIgnoreCase("VARCHAR2") || datatypeVariable.equalsIgnoreCase("CHARACTER") || datatypeVariable.equalsIgnoreCase("NCHAR") || datatypeVariable.equalsIgnoreCase("NVARCHAR") || datatypeVariable.equalsIgnoreCase("NVARCHAR2") || datatypeVariable.equalsIgnoreCase("CLOB") || datatypeVariable.equalsIgnoreCase("NCLOB") || datatypeVariable.equalsIgnoreCase("DBCLOB") || datatypeVariable.equalsIgnoreCase("TEXT") || datatypeVariable.equalsIgnoreCase("NTEXT") || datatypeVariable.equalsIgnoreCase("XML")) {
            return "CHAR";
        }
        if (datatypeVariable.equalsIgnoreCase("DATE") || datatypeVariable.equalsIgnoreCase("DATETIME") || datatypeVariable.equalsIgnoreCase("TIMESTAMP") || datatypeVariable.equalsIgnoreCase("TIME") || datatypeVariable.equalsIgnoreCase("SMALLDATETIME") || datatypeVariable.equalsIgnoreCase("DATETIME2") || datatypeVariable.equalsIgnoreCase("DATETIMEOFFSET")) {
            return "DATE";
        }
        if (datatypeVariable.equalsIgnoreCase("BLOB") || datatypeVariable.equalsIgnoreCase("LONGBLOB") || datatypeVariable.equalsIgnoreCase("MEDIUMBLOB") || datatypeVariable.equalsIgnoreCase("TINYBLOB") || datatypeVariable.equalsIgnoreCase("BINARY") || datatypeVariable.equalsIgnoreCase("VARBINARY") || datatypeVariable.equalsIgnoreCase("BIT") || datatypeVariable.equalsIgnoreCase("BFILE") || datatypeVariable.startsWith("LONG") || datatypeVariable.equalsIgnoreCase("LONGTEXT") || datatypeVariable.equalsIgnoreCase("IMAGE") || datatypeVariable.equalsIgnoreCase("MEDIUMTEXT") || datatypeVariable.equalsIgnoreCase("TINYTEXT")) {
            return "BIN";
        }
        return datatypeVariable;
    }
    
    @Override
    public void toANSIString() throws ConvertException {
        String objectName = "";
        this.datatypeSettings();
        if (this.datatypeName != null) {
            objectName = this.getObjectTypeForTheCorrespondingDataTypeName(this.datatypeName);
        }
        if (objectName.equals("NUMERIC")) {
            final NumericClass nc = new NumericClass();
            nc.setDatatypeName(this.datatypeName);
            nc.setPrecision(this.precision);
            nc.setScale(this.scale);
            nc.setOpenBrace(this.openBrace);
            nc.setClosedBrace(this.closedBrace);
            nc.toANSIString();
            this.setDatatypeName(nc.getDatatypeName());
            this.setPrecision(nc.getPrecision());
            this.setScale(nc.getScale());
            this.setOpenBrace(nc.getOpenBrace());
            this.setClosedBrace(nc.getClosedBrace());
        }
        else if (objectName.equals("DATE")) {
            final DateClass dc = new DateClass();
            dc.setDatatypeName(this.datatypeName);
            dc.setSize(this.precision);
            dc.setClosedBrace(this.closedBrace);
            dc.setOpenBrace(this.openBrace);
            dc.toANSIString();
            this.setDatatypeName(dc.getDatatypeName());
            this.setOpenBrace(dc.getOpenBrace());
            this.setClosedBrace(dc.getClosedBrace());
            this.setPrecision(dc.getSize());
        }
        else if (objectName.equals("BIN")) {
            final BinClass bc = new BinClass();
            bc.setDatatypeName(this.datatypeName);
            bc.setSize(this.precision);
            bc.setVarying(this.varyingType);
            bc.setOpenBrace(this.openBrace);
            bc.setClosedBrace(this.closedBrace);
            bc.toANSIString();
            this.setDatatypeName(bc.getDatatypeName());
            this.setPrecision(bc.getSize());
            this.setOpenBrace(bc.getOpenBrace());
            this.setClosedBrace(bc.getClosedBrace());
            this.setVarying(this.varyingType);
        }
        else if (objectName.equals("CHAR")) {
            final CharacterClass cc = new CharacterClass();
            String nationalString = null;
            String datatypeWithoutNational = null;
            if (this.datatypeName.toUpperCase().startsWith("NATIONAL")) {
                nationalString = this.datatypeName.substring(this.datatypeName.toUpperCase().indexOf("NATIONAL"));
                datatypeWithoutNational = this.datatypeName.substring(this.datatypeName.toUpperCase().indexOf("NATIONAL") + 1, this.datatypeName.length());
            }
            if (nationalString != null) {
                cc.setNational(nationalString);
            }
            if (datatypeWithoutNational != null) {
                cc.setDatatypeName(datatypeWithoutNational);
            }
            else {
                cc.setDatatypeName(this.datatypeName);
            }
            if (this.precision != null) {
                cc.setSize(this.precision);
            }
            else if (this.size != null) {
                cc.setSize(this.size);
            }
            cc.setVarying(this.varyingType);
            cc.setOpenBrace(this.openBrace);
            cc.setClosedBrace(this.closedBrace);
            cc.toANSIString();
            final String nationalStringFromCC = cc.getNational();
            if (nationalStringFromCC != null) {
                this.setDatatypeName(nationalStringFromCC + " " + cc.getDatatypeName());
            }
            else {
                this.setDatatypeName(cc.getDatatypeName());
            }
            this.setPrecision(cc.getSize());
            this.setOpenBrace(cc.getOpenBrace());
            this.setClosedBrace(cc.getClosedBrace());
            this.setVarying(cc.getVarying());
            this.setClosedBrace(cc.getClosedBrace());
            this.setOpenBrace(cc.getOpenBrace());
        }
        if (this.getArray() != null) {
            this.setArray(null);
        }
    }
    
    @Override
    public void toTeradataString() throws ConvertException {
        String objectName = "";
        this.datatypeSettings();
        if (this.datatypeName != null) {
            objectName = this.getObjectTypeForTheCorrespondingDataTypeName(this.datatypeName);
        }
        if (objectName.equals("NUMERIC")) {
            final NumericClass nc = new NumericClass();
            nc.setDatatypeName(this.datatypeName);
            nc.setPrecision(this.precision);
            nc.setScale(this.scale);
            nc.setOpenBrace(this.openBrace);
            nc.setClosedBrace(this.closedBrace);
            nc.toTeradataString();
            this.setDatatypeName(nc.getDatatypeName());
            this.setPrecision(nc.getPrecision());
            this.setScale(nc.getScale());
            this.setOpenBrace(nc.getOpenBrace());
            this.setClosedBrace(nc.getClosedBrace());
        }
        else if (objectName.equals("DATE")) {
            final DateClass dc = new DateClass();
            dc.setDatatypeName(this.datatypeName);
            dc.setSize(this.precision);
            dc.setClosedBrace(this.closedBrace);
            dc.setOpenBrace(this.openBrace);
            dc.toTeradataString();
            this.setDatatypeName(dc.getDatatypeName());
            this.setOpenBrace(dc.getOpenBrace());
            this.setClosedBrace(dc.getClosedBrace());
            this.setPrecision(dc.getSize());
        }
        else if (objectName.equals("BIN")) {
            final BinClass bc = new BinClass();
            bc.setDatatypeName(this.datatypeName);
            bc.setSize(this.precision);
            bc.setVarying(this.varyingType);
            bc.setOpenBrace(this.openBrace);
            bc.setClosedBrace(this.closedBrace);
            bc.toTeradataString();
            this.setDatatypeName(bc.getDatatypeName());
            this.setPrecision(bc.getSize());
            this.setOpenBrace(bc.getOpenBrace());
            this.setClosedBrace(bc.getClosedBrace());
            this.setVarying(this.varyingType);
        }
        else if (objectName.equals("CHAR")) {
            final CharacterClass cc = new CharacterClass();
            String nationalString = null;
            String datatypeWithoutNational = null;
            if (this.datatypeName.toUpperCase().startsWith("NATIONAL")) {
                nationalString = this.datatypeName.substring(this.datatypeName.toUpperCase().indexOf("NATIONAL"));
                datatypeWithoutNational = this.datatypeName.substring(this.datatypeName.toUpperCase().indexOf("NATIONAL") + 1, this.datatypeName.length());
            }
            if (nationalString != null) {
                cc.setNational(nationalString);
            }
            if (datatypeWithoutNational != null) {
                cc.setDatatypeName(datatypeWithoutNational);
            }
            else {
                cc.setDatatypeName(this.datatypeName);
            }
            if (this.precision != null) {
                cc.setSize(this.precision);
            }
            else if (this.size != null) {
                cc.setSize(this.size);
            }
            cc.setVarying(this.varyingType);
            cc.setOpenBrace(this.openBrace);
            cc.setClosedBrace(this.closedBrace);
            cc.toTeradataString();
            final String nationalStringFromCC = cc.getNational();
            if (nationalStringFromCC != null) {
                this.setDatatypeName(nationalStringFromCC + " " + cc.getDatatypeName());
            }
            else {
                this.setDatatypeName(cc.getDatatypeName());
            }
            this.setPrecision(cc.getSize());
            this.setOpenBrace(cc.getOpenBrace());
            this.setClosedBrace(cc.getClosedBrace());
            this.setVarying(cc.getVarying());
            this.setClosedBrace(cc.getClosedBrace());
            this.setOpenBrace(cc.getOpenBrace());
        }
        if (this.getArray() != null) {
            this.setArray(null);
        }
    }
    
    @Override
    public void toDB2String() throws ConvertException {
        String objectName = "";
        this.datatypeSettings();
        if (this.datatypeName != null) {
            objectName = this.getObjectTypeForTheCorrespondingDataTypeName(this.datatypeName);
        }
        if (objectName.equals("NUMERIC")) {
            final NumericClass nc = new NumericClass();
            nc.setDatatypeName(this.datatypeName);
            nc.setPrecision(this.precision);
            nc.setScale(this.scale);
            nc.setOpenBrace(this.openBrace);
            nc.setClosedBrace(this.closedBrace);
            nc.toDB2String();
            this.setDatatypeName(nc.getDatatypeName());
            this.setPrecision(nc.getPrecision());
            this.setScale(nc.getScale());
            this.setOpenBrace(nc.getOpenBrace());
            this.setClosedBrace(nc.getClosedBrace());
        }
        else if (objectName.equals("DATE")) {
            final DateClass dc = new DateClass();
            dc.setDatatypeName(this.datatypeName);
            dc.setSize(this.precision);
            dc.toDB2String();
            dc.setOpenBrace(this.openBrace);
            dc.setClosedBrace(this.closedBrace);
            this.setDatatypeName(dc.getDatatypeName());
            this.setPrecision(dc.getSize());
            this.setOpenBrace(dc.getOpenBrace());
            this.setClosedBrace(dc.getClosedBrace());
        }
        else if (objectName.equals("BIN")) {
            final BinClass bc = new BinClass();
            bc.setDatatypeName(this.datatypeName);
            bc.setSize(this.precision);
            bc.setVarying(this.varyingType);
            bc.setOpenBrace(this.openBrace);
            bc.setClosedBrace(this.closedBrace);
            bc.toDB2String();
            this.setDatatypeName(bc.getDatatypeName());
            this.setPrecision(bc.getSize());
            this.setVarying(this.varyingType);
            this.setOpenBrace(bc.getOpenBrace());
            this.setClosedBrace(bc.getClosedBrace());
        }
        else if (objectName.equals("CHAR")) {
            final CharacterClass cc = new CharacterClass();
            String nationalString = null;
            String datatypeWithoutNational = null;
            if (this.datatypeName.toUpperCase().startsWith("NATIONAL")) {
                nationalString = this.datatypeName.substring(this.datatypeName.toUpperCase().indexOf("NATIONAL"));
                datatypeWithoutNational = this.datatypeName.substring(this.datatypeName.toUpperCase().indexOf("NATIONAL") + 1, this.datatypeName.length());
            }
            if (nationalString != null) {
                cc.setNational(nationalString);
            }
            if (datatypeWithoutNational != null) {
                cc.setDatatypeName(datatypeWithoutNational);
            }
            else {
                cc.setDatatypeName(this.datatypeName);
            }
            cc.setSize(this.precision);
            cc.setVarying(this.varyingType);
            cc.setOpenBrace(this.openBrace);
            cc.setClosedBrace(this.closedBrace);
            cc.toDB2String();
            final String nationalStringFromCC = cc.getNational();
            if (nationalStringFromCC != null) {
                this.setDatatypeName(nationalStringFromCC + " " + cc.getDatatypeName());
            }
            else {
                this.setDatatypeName(cc.getDatatypeName());
            }
            this.setPrecision(cc.getSize());
            this.setOpenBrace(cc.getOpenBrace());
            this.setClosedBrace(cc.getClosedBrace());
            this.setVarying(cc.getVarying());
            this.setOpenBrace(cc.getOpenBrace());
            this.setClosedBrace(cc.getClosedBrace());
        }
        if (this.datatypeName != null && this.datatypeName.equalsIgnoreCase("uniqueidentifier")) {
            this.setDatatypeName("CHAR(16) FOR BIT DATA");
            this.setOpenBrace(null);
            this.setPrecision(null);
            this.setClosedBrace(null);
        }
        else if (this.datatypeName != null && this.datatypeName.equalsIgnoreCase("sql_variant")) {
            this.setDatatypeName("VARCHAR");
            this.setOpenBrace("(");
            this.setPrecision("800");
            this.setClosedBrace(")");
        }
        if (this.getArray() != null) {
            this.setArray(null);
        }
    }
    
    @Override
    public void toInformixString() throws ConvertException {
        String objectName = "";
        this.datatypeSettings();
        if (this.datatypeName != null) {
            objectName = this.getObjectTypeForTheCorrespondingDataTypeName(this.datatypeName);
        }
        if (objectName.equals("NUMERIC")) {
            final NumericClass nc = new NumericClass();
            nc.setDatatypeName(this.datatypeName);
            nc.setPrecision(this.precision);
            nc.setScale(this.scale);
            nc.setOpenBrace(this.openBrace);
            nc.setClosedBrace(this.closedBrace);
            nc.toInformixString();
            this.setDatatypeName(nc.getDatatypeName());
            this.setPrecision(nc.getPrecision());
            this.setScale(nc.getScale());
            this.setOpenBrace(nc.getOpenBrace());
            this.setClosedBrace(nc.getClosedBrace());
        }
        else if (objectName.equals("DATE")) {
            final DateClass dc = new DateClass();
            dc.setDatatypeName(this.datatypeName);
            dc.setSize(this.precision);
            dc.setOpenBrace(this.openBrace);
            dc.setClosedBrace(this.closedBrace);
            dc.toInformixString();
            this.setDatatypeName(dc.getDatatypeName());
            this.setPrecision(dc.getSize());
            this.setOpenBrace(dc.getOpenBrace());
            this.setClosedBrace(dc.getClosedBrace());
        }
        else if (objectName.equals("BIN")) {
            final BinClass bc = new BinClass();
            bc.setDatatypeName(this.datatypeName);
            bc.setSize(this.precision);
            bc.setVarying(this.varyingType);
            bc.setOpenBrace(this.openBrace);
            bc.setClosedBrace(this.closedBrace);
            bc.toInformixString();
            this.setDatatypeName(bc.getDatatypeName());
            this.setPrecision(bc.getSize());
            this.setVarying(this.varyingType);
            this.setClosedBrace(bc.getClosedBrace());
            this.setOpenBrace(bc.getOpenBrace());
        }
        else if (objectName.equals("CHAR")) {
            final CharacterClass cc = new CharacterClass();
            String nationalString = null;
            String datatypeWithoutNational = null;
            if (this.datatypeName.toUpperCase().startsWith("NATIONAL")) {
                nationalString = this.datatypeName.substring(this.datatypeName.toUpperCase().indexOf("NATIONAL"));
                datatypeWithoutNational = this.datatypeName.substring(this.datatypeName.toUpperCase().indexOf("NATIONAL") + 1, this.datatypeName.length());
            }
            if (nationalString != null) {
                cc.setNational(nationalString);
            }
            if (datatypeWithoutNational != null) {
                cc.setDatatypeName(datatypeWithoutNational);
            }
            else {
                cc.setDatatypeName(this.datatypeName);
            }
            cc.setSize(this.precision);
            cc.setVarying(this.varyingType);
            cc.setOpenBrace(this.openBrace);
            cc.setClosedBrace(this.closedBrace);
            cc.toInformixString();
            final String nationalStringFromCC = cc.getNational();
            if (nationalStringFromCC != null) {
                this.setDatatypeName(nationalStringFromCC + " " + cc.getDatatypeName());
            }
            else {
                this.setDatatypeName(cc.getDatatypeName());
            }
            this.setPrecision(cc.getSize());
            this.setOpenBrace(cc.getOpenBrace());
            this.setClosedBrace(cc.getClosedBrace());
            this.setVarying(cc.getVarying());
            this.setOpenBrace(cc.getOpenBrace());
            this.setClosedBrace(cc.getClosedBrace());
        }
        if (this.getArray() != null) {
            this.setArray(null);
        }
    }
    
    @Override
    public void toMSSQLServerString() throws ConvertException {
        String objectName = "";
        this.datatypeSettings();
        if (this.datatypeName != null) {
            objectName = this.getObjectTypeForTheCorrespondingDataTypeName(this.datatypeName);
        }
        if (objectName.equals("NUMERIC")) {
            final NumericClass nc = new NumericClass();
            nc.setDatatypeName(this.datatypeName);
            nc.setPrecision(this.precision);
            nc.setScale(this.scale);
            nc.setOpenBrace(this.openBrace);
            nc.setClosedBrace(this.closedBrace);
            nc.toMSSQLServerString();
            this.setDatatypeName(nc.getDatatypeName());
            this.setPrecision(nc.getPrecision());
            this.setScale(nc.getScale());
            this.setOpenBrace(nc.getOpenBrace());
            this.setClosedBrace(nc.getClosedBrace());
        }
        else if (objectName.equals("DATE")) {
            final DateClass dc = new DateClass();
            dc.setDatatypeName(this.datatypeName);
            dc.setSize(this.precision);
            dc.setOpenBrace(this.openBrace);
            dc.setClosedBrace(this.closedBrace);
            dc.toMSSQLServerString();
            this.setDatatypeName(dc.getDatatypeName());
            this.setPrecision(dc.getSize());
            this.setOpenBrace(dc.getOpenBrace());
            this.setClosedBrace(dc.getClosedBrace());
        }
        else if (objectName.equals("BIN")) {
            final BinClass bc = new BinClass();
            bc.setDatatypeName(this.datatypeName);
            bc.setSize(this.precision);
            bc.setVarying(this.varyingType);
            bc.setOpenBrace(this.openBrace);
            bc.setClosedBrace(this.closedBrace);
            bc.toMSSQLServerString();
            this.setDatatypeName(bc.getDatatypeName());
            this.setPrecision(bc.getSize());
            this.setVarying(this.varyingType);
        }
        else if (objectName.equals("CHAR")) {
            final CharacterClass cc = new CharacterClass();
            String nationalString = null;
            String datatypeWithoutNational = null;
            if (this.datatypeName.toUpperCase().startsWith("NATIONAL")) {
                nationalString = this.datatypeName.substring(this.datatypeName.toUpperCase().indexOf("NATIONAL"));
                datatypeWithoutNational = this.datatypeName.substring(this.datatypeName.toUpperCase().indexOf("NATIONAL") + 1, this.datatypeName.length());
            }
            if (nationalString != null) {
                cc.setNational(nationalString);
            }
            if (datatypeWithoutNational != null) {
                cc.setDatatypeName(datatypeWithoutNational);
            }
            else {
                cc.setDatatypeName(this.datatypeName);
            }
            cc.setSize(this.precision);
            cc.setVarying(this.varyingType);
            cc.setOpenBrace(this.openBrace);
            cc.setClosedBrace(this.closedBrace);
            cc.toMSSQLServerString();
            final String nationalStringFromCC = cc.getNational();
            if (nationalStringFromCC != null) {
                this.setDatatypeName(nationalStringFromCC + " " + cc.getDatatypeName());
            }
            else {
                this.setDatatypeName(cc.getDatatypeName());
            }
            this.setPrecision(cc.getSize());
            this.setOpenBrace(cc.getOpenBrace());
            this.setClosedBrace(cc.getClosedBrace());
            this.setVarying(cc.getVarying());
        }
        if (this.getArray() != null) {
            this.setArray(null);
        }
    }
    
    @Override
    public void toMySQLString() throws ConvertException {
        String objectName = "";
        this.datatypeSettings();
        if (this.datatypeName != null) {
            objectName = this.getObjectTypeForTheCorrespondingDataTypeName(this.datatypeName);
        }
        if (objectName.equals("NUMERIC")) {
            final NumericClass nc = new NumericClass();
            nc.setDatatypeName(this.datatypeName);
            nc.setPrecision(this.precision);
            nc.setOpenBrace(this.openBrace);
            nc.setClosedBrace(this.closedBrace);
            nc.setScale(this.scale);
            nc.toMySQLString();
            this.setDatatypeName(nc.getDatatypeName());
            this.setPrecision(nc.getPrecision());
            this.setScale(nc.getScale());
            this.setOpenBrace(nc.getOpenBrace());
            this.setClosedBrace(nc.getClosedBrace());
        }
        else if (objectName.equals("DATE")) {
            final DateClass dc = new DateClass();
            dc.setDatatypeName(this.datatypeName);
            dc.setSize(this.precision);
            dc.setOpenBrace(this.openBrace);
            dc.setClosedBrace(this.closedBrace);
            dc.toMySQLString();
            this.setDatatypeName(dc.getDatatypeName());
            this.setPrecision(dc.getSize());
            this.setOpenBrace(dc.getOpenBrace());
            this.setClosedBrace(dc.getClosedBrace());
        }
        else if (objectName.equals("BIN")) {
            final BinClass bc = new BinClass();
            bc.setDatatypeName(this.datatypeName);
            bc.setSize(this.precision);
            bc.setOpenBrace(this.openBrace);
            bc.setClosedBrace(this.closedBrace);
            bc.setVarying(this.varyingType);
            bc.toMySQLString();
            this.setDatatypeName(bc.getDatatypeName());
            this.setPrecision(bc.getSize());
            this.setOpenBrace(bc.getOpenBrace());
            this.setClosedBrace(bc.getClosedBrace());
            this.setVarying(this.varyingType);
        }
        else if (objectName.equals("CHAR")) {
            final CharacterClass cc = new CharacterClass();
            String nationalString = null;
            String datatypeWithoutNational = null;
            if (this.datatypeName.toUpperCase().startsWith("NATIONAL")) {
                nationalString = this.datatypeName.substring(this.datatypeName.toUpperCase().indexOf("NATIONAL"));
                datatypeWithoutNational = this.datatypeName.substring(this.datatypeName.toUpperCase().indexOf("NATIONAL") + 1, this.datatypeName.length());
            }
            if (nationalString != null) {
                cc.setNational(nationalString);
            }
            if (datatypeWithoutNational != null) {
                cc.setDatatypeName(datatypeWithoutNational);
            }
            else {
                cc.setDatatypeName(this.datatypeName);
            }
            cc.setSize(this.precision);
            cc.setVarying(this.varyingType);
            cc.setOpenBrace(this.openBrace);
            cc.setClosedBrace(this.closedBrace);
            cc.toMySQLString();
            final String nationalStringFromCC = cc.getNational();
            if (nationalStringFromCC != null) {
                this.setDatatypeName(nationalStringFromCC + " " + cc.getDatatypeName());
            }
            else {
                this.setDatatypeName(cc.getDatatypeName());
            }
            this.setPrecision(cc.getSize());
            this.setOpenBrace(cc.getOpenBrace());
            this.setClosedBrace(cc.getClosedBrace());
            this.setVarying(cc.getVarying());
        }
        if (this.getArray() != null) {
            this.setArray(null);
        }
    }
    
    @Override
    public void toOracleString() throws ConvertException {
        String objectName = "";
        this.datatypeSettings();
        if (this.datatypeName != null) {
            objectName = this.getObjectTypeForTheCorrespondingDataTypeName(this.datatypeName);
        }
        if (objectName.equals("NUMERIC")) {
            final NumericClass nc = new NumericClass();
            nc.setDatatypeName(this.datatypeName);
            nc.setPrecision(this.precision);
            nc.setScale(this.scale);
            nc.setOpenBrace(this.openBrace);
            nc.setClosedBrace(this.closedBrace);
            nc.toOracleString();
            this.setDatatypeName(nc.getDatatypeName());
            this.setPrecision(nc.getPrecision());
            this.setScale(nc.getScale());
            this.setOpenBrace(nc.getOpenBrace());
            this.setClosedBrace(nc.getClosedBrace());
        }
        else if (objectName.equals("DATE")) {
            final DateClass dc = new DateClass();
            dc.setDatatypeName(this.datatypeName);
            dc.setSize(this.precision);
            dc.setOpenBrace(this.openBrace);
            dc.setClosedBrace(this.closedBrace);
            dc.toOracleString();
            this.setDatatypeName(dc.getDatatypeName());
            this.setPrecision(dc.getSize());
            this.setOpenBrace(dc.getOpenBrace());
            this.setClosedBrace(dc.getClosedBrace());
            this.setWithLocalTimeZone(dc.getWithLocalTimeZone());
        }
        else if (objectName.equals("BIN")) {
            final BinClass bc = new BinClass();
            bc.setDatatypeName(this.datatypeName);
            if (this.precision != null) {
                bc.setSize(this.precision);
            }
            else if (this.size != null) {
                bc.setSize(this.size);
            }
            bc.setVarying(this.varyingType);
            bc.setOpenBrace(this.openBrace);
            bc.setClosedBrace(this.closedBrace);
            bc.toOracleString();
            this.setDatatypeName(bc.getDatatypeName());
            this.setPrecision(bc.getSize());
            this.setVarying(this.varyingType);
            this.setOpenBrace(bc.getOpenBrace());
            this.setClosedBrace(bc.getClosedBrace());
        }
        else if (objectName.equals("CHAR")) {
            final CharacterClass cc = new CharacterClass();
            String nationalString = null;
            String datatypeWithoutNational = null;
            if (this.datatypeName.toUpperCase().startsWith("NATIONAL")) {
                nationalString = this.datatypeName.substring(this.datatypeName.toUpperCase().indexOf("NATIONAL"));
                datatypeWithoutNational = this.datatypeName.substring(this.datatypeName.toUpperCase().indexOf("NATIONAL") + 1, this.datatypeName.length());
            }
            if (nationalString != null) {
                cc.setNational(nationalString);
            }
            if (datatypeWithoutNational != null) {
                cc.setDatatypeName(datatypeWithoutNational);
            }
            else {
                cc.setDatatypeName(this.datatypeName);
            }
            if (this.precision != null) {
                cc.setSize(this.precision);
            }
            else if (this.size != null) {
                cc.setSize(this.size);
            }
            cc.setVarying(this.varyingType);
            cc.setOpenBrace(this.openBrace);
            cc.setClosedBrace(this.closedBrace);
            cc.toOracleString();
            final String nationalStringFromCC = cc.getNational();
            if (nationalStringFromCC != null) {
                this.setDatatypeName(nationalStringFromCC + " " + cc.getDatatypeName());
            }
            else {
                this.setDatatypeName(cc.getDatatypeName());
            }
            this.setPrecision(cc.getSize());
            this.setOpenBrace(cc.getOpenBrace());
            this.setClosedBrace(cc.getClosedBrace());
            this.setVarying(cc.getVarying());
        }
        if (this.datatypeName != null && this.datatypeName.equalsIgnoreCase("uniqueidentifier")) {
            this.setDatatypeName("CHAR");
            this.setOpenBrace("(");
            this.setPrecision("36");
            this.setClosedBrace(")");
        }
        else if (this.datatypeName != null && this.datatypeName.equalsIgnoreCase("sql_variant")) {
            this.setDatatypeName("SYS.ANYDATA");
            this.setOpenBrace(null);
            this.setPrecision(null);
            this.setClosedBrace(null);
        }
        if (this.getArray() != null) {
            this.setArray(null);
        }
    }
    
    @Override
    public void toPostgreSQLString() throws ConvertException {
        String objectName = "";
        this.datatypeSettings();
        if (this.datatypeName != null) {
            objectName = this.getObjectTypeForTheCorrespondingDataTypeName(this.datatypeName);
        }
        if (objectName.equals("NUMERIC")) {
            final NumericClass nc = new NumericClass();
            nc.setDatatypeName(this.datatypeName);
            nc.setPrecision(this.precision);
            nc.setOpenBrace(this.openBrace);
            nc.setClosedBrace(this.closedBrace);
            nc.setScale(this.scale);
            nc.toPostgreSQLString();
            this.setDatatypeName(nc.getDatatypeName());
            this.setPrecision(nc.getPrecision());
            this.setScale(nc.getScale());
            this.setOpenBrace(nc.getOpenBrace());
            this.setClosedBrace(nc.getClosedBrace());
        }
        else if (objectName.equals("DATE")) {
            final DateClass dc = new DateClass();
            dc.setDatatypeName(this.datatypeName);
            dc.setSize(this.precision);
            dc.setOpenBrace(this.openBrace);
            dc.setClosedBrace(this.closedBrace);
            dc.toPostgreSQLString();
            this.setDatatypeName(dc.getDatatypeName());
            this.setPrecision(dc.getSize());
            this.setOpenBrace(dc.getOpenBrace());
            this.setClosedBrace(dc.getClosedBrace());
        }
        else if (objectName.equals("BIN")) {
            final BinClass bc = new BinClass();
            bc.setDatatypeName(this.datatypeName);
            bc.setSize(this.precision);
            bc.setVarying(this.varyingType);
            bc.toPostgreSQLString();
            this.setDatatypeName(bc.getDatatypeName());
            this.setPrecision(bc.getSize());
            this.setVarying(this.varyingType);
            this.setOpenBrace(bc.getOpenBrace());
            this.setClosedBrace(bc.getClosedBrace());
        }
        else if (objectName.equals("CHAR")) {
            final CharacterClass cc = new CharacterClass();
            String nationalString = null;
            String datatypeWithoutNational = null;
            if (this.datatypeName.toUpperCase().startsWith("NATIONAL")) {
                nationalString = this.datatypeName.substring(this.datatypeName.toUpperCase().indexOf("NATIONAL"));
                datatypeWithoutNational = this.datatypeName.substring(this.datatypeName.toUpperCase().indexOf("NATIONAL") + 1, this.datatypeName.length());
            }
            if (nationalString != null) {
                cc.setNational(nationalString);
            }
            if (datatypeWithoutNational != null) {
                cc.setDatatypeName(datatypeWithoutNational);
            }
            else {
                cc.setDatatypeName(this.datatypeName);
            }
            cc.setSize(this.precision);
            cc.setOpenBrace(this.openBrace);
            cc.setClosedBrace(this.closedBrace);
            cc.setVarying(this.varyingType);
            cc.toPostgreSQLString();
            final String nationalStringFromCC = cc.getNational();
            if (nationalStringFromCC != null) {
                this.setDatatypeName(nationalStringFromCC + " " + cc.getDatatypeName());
            }
            else {
                this.setDatatypeName(cc.getDatatypeName());
            }
            this.setPrecision(cc.getSize());
            this.setOpenBrace(cc.getOpenBrace());
            this.setClosedBrace(cc.getClosedBrace());
            this.setVarying(cc.getVarying());
        }
    }
    
    @Override
    public void toSybaseString() throws ConvertException {
        String objectName = "";
        this.datatypeSettings();
        if (this.datatypeName != null) {
            objectName = this.getObjectTypeForTheCorrespondingDataTypeName(this.datatypeName);
        }
        if (objectName.equals("NUMERIC")) {
            final NumericClass nc = new NumericClass();
            nc.setDatatypeName(this.datatypeName);
            nc.setPrecision(this.precision);
            nc.setScale(this.scale);
            nc.setOpenBrace(this.openBrace);
            nc.setClosedBrace(this.closedBrace);
            nc.toSybaseString();
            this.setDatatypeName(nc.getDatatypeName());
            this.setPrecision(nc.getPrecision());
            this.setScale(nc.getScale());
            this.setOpenBrace(nc.getOpenBrace());
            this.setClosedBrace(nc.getClosedBrace());
        }
        else if (objectName.equals("DATE")) {
            final DateClass dc = new DateClass();
            dc.setDatatypeName(this.datatypeName);
            dc.setSize(this.precision);
            dc.setOpenBrace(this.openBrace);
            dc.setClosedBrace(this.closedBrace);
            dc.toSybaseString();
            this.setDatatypeName(dc.getDatatypeName());
            this.setPrecision(dc.getSize());
            this.setOpenBrace(dc.getOpenBrace());
            this.setClosedBrace(dc.getClosedBrace());
        }
        else if (objectName.equals("BIN")) {
            final BinClass bc = new BinClass();
            bc.setDatatypeName(this.datatypeName);
            bc.setSize(this.precision);
            bc.setVarying(this.varyingType);
            bc.setOpenBrace(this.openBrace);
            bc.setClosedBrace(this.closedBrace);
            bc.toSybaseString();
            this.setDatatypeName(bc.getDatatypeName());
            this.setPrecision(bc.getSize());
            this.setVarying(this.varyingType);
            this.setOpenBrace(bc.getOpenBrace());
            this.setClosedBrace(bc.getClosedBrace());
        }
        else if (objectName.equals("CHAR")) {
            final CharacterClass cc = new CharacterClass();
            String nationalString = null;
            String datatypeWithoutNational = null;
            if (this.datatypeName.toUpperCase().startsWith("NATIONAL")) {
                nationalString = this.datatypeName.substring(this.datatypeName.toUpperCase().indexOf("NATIONAL"));
                datatypeWithoutNational = this.datatypeName.substring(this.datatypeName.toUpperCase().indexOf("NATIONAL") + 1, this.datatypeName.length());
            }
            if (nationalString != null) {
                cc.setNational(nationalString);
            }
            if (datatypeWithoutNational != null) {
                cc.setDatatypeName(datatypeWithoutNational);
            }
            else {
                cc.setDatatypeName(this.datatypeName);
            }
            cc.setSize(this.precision);
            cc.setVarying(this.varyingType);
            cc.setOpenBrace(this.openBrace);
            cc.setClosedBrace(this.closedBrace);
            cc.toSybaseString();
            final String nationalStringFromCC = cc.getNational();
            if (nationalStringFromCC != null) {
                this.setDatatypeName(nationalStringFromCC + " " + cc.getDatatypeName());
            }
            else {
                this.setDatatypeName(cc.getDatatypeName());
            }
            this.setPrecision(cc.getSize());
            this.setOpenBrace(cc.getOpenBrace());
            this.setClosedBrace(cc.getClosedBrace());
            this.setVarying(cc.getVarying());
        }
        if (this.getArray() != null) {
            this.setArray(null);
        }
    }
    
    public Datatype copyObjectValues() {
        final QuotedIdentifierDatatype quotedDatatypeClass = new QuotedIdentifierDatatype();
        quotedDatatypeClass.setClosedBrace(this.closedBrace);
        quotedDatatypeClass.setDatatypeName(this.getDatatypeName());
        quotedDatatypeClass.setOpenBrace(this.openBrace);
        quotedDatatypeClass.setPrecision(this.getPrecision());
        quotedDatatypeClass.setVarying(this.varyingType);
        quotedDatatypeClass.setScale(this.getScale());
        quotedDatatypeClass.setSize(this.getSize());
        quotedDatatypeClass.setArray(this.getArray());
        return quotedDatatypeClass;
    }
    
    private void datatypeSettings() {
        final String str = this.datatypeName;
        if (str != null) {
            int index = str.indexOf("(");
            if (index != -1) {
                this.datatypeName = str.substring(0, index);
                String size = null;
                if (str.indexOf(")") != -1 && index + 1 != str.indexOf(")")) {
                    size = str.substring(index + 1, str.indexOf(")"));
                }
                if (size != null) {
                    this.openBrace = "(";
                    this.closedBrace = ")";
                    index = size.indexOf(",");
                    if (index != -1) {
                        this.precision = size.substring(0, index).trim();
                        this.precision = StringFunctions.replaceAll("", " ", this.precision);
                        this.scale = size.substring(index + 1, size.length()).trim();
                        this.scale = StringFunctions.replaceAll("", " ", this.scale);
                    }
                    else {
                        this.precision = size.trim();
                        this.precision = StringFunctions.replaceAll("", " ", this.precision);
                        if (this.precision.length() <= 0) {
                            this.precision = null;
                            this.openBrace = null;
                            this.closedBrace = null;
                        }
                    }
                }
            }
            else if (str.indexOf(")") != -1) {
                this.openBrace = null;
                this.closedBrace = null;
                this.precision = null;
            }
        }
    }
    
    @Override
    public void toTimesTenString() throws ConvertException {
        String objectName = "";
        this.datatypeSettings();
        if (this.datatypeName != null) {
            objectName = this.getObjectTypeForTheCorrespondingDataTypeName(this.datatypeName);
        }
        if (objectName.equals("NUMERIC")) {
            final NumericClass nc = new NumericClass();
            nc.setDatatypeName(this.datatypeName);
            nc.setPrecision(this.precision);
            nc.setScale(this.scale);
            nc.setOpenBrace(this.openBrace);
            nc.setClosedBrace(this.closedBrace);
            nc.toSybaseString();
            this.setDatatypeName(nc.getDatatypeName());
            this.setPrecision(nc.getPrecision());
            this.setScale(nc.getScale());
            this.setOpenBrace(nc.getOpenBrace());
            this.setClosedBrace(nc.getClosedBrace());
        }
        else if (objectName.equals("DATE")) {
            final DateClass dc = new DateClass();
            dc.setDatatypeName(this.datatypeName);
            dc.setSize(this.precision);
            dc.setOpenBrace(this.openBrace);
            dc.setClosedBrace(this.closedBrace);
            dc.toSybaseString();
            this.setDatatypeName(dc.getDatatypeName());
            this.setPrecision(dc.getSize());
            this.setOpenBrace(dc.getOpenBrace());
            this.setClosedBrace(dc.getClosedBrace());
        }
        else if (objectName.equals("BIN")) {
            final BinClass bc = new BinClass();
            bc.setDatatypeName(this.datatypeName);
            bc.setSize(this.precision);
            bc.setVarying(this.varyingType);
            bc.setOpenBrace(this.openBrace);
            bc.setClosedBrace(this.closedBrace);
            bc.toSybaseString();
            this.setDatatypeName(bc.getDatatypeName());
            this.setPrecision(bc.getSize());
            this.setVarying(this.varyingType);
            this.setOpenBrace(bc.getOpenBrace());
            this.setClosedBrace(bc.getClosedBrace());
        }
        else if (objectName.equals("CHAR")) {
            final CharacterClass cc = new CharacterClass();
            String nationalString = null;
            String datatypeWithoutNational = null;
            if (this.datatypeName.toUpperCase().startsWith("NATIONAL")) {
                nationalString = this.datatypeName.substring(this.datatypeName.toUpperCase().indexOf("NATIONAL"));
                datatypeWithoutNational = this.datatypeName.substring(this.datatypeName.toUpperCase().indexOf("NATIONAL") + 1, this.datatypeName.length());
            }
            if (nationalString != null) {
                cc.setNational(nationalString);
            }
            if (datatypeWithoutNational != null) {
                cc.setDatatypeName(datatypeWithoutNational);
            }
            else {
                cc.setDatatypeName(this.datatypeName);
            }
            cc.setSize(this.precision);
            cc.setVarying(this.varyingType);
            cc.setOpenBrace(this.openBrace);
            cc.setClosedBrace(this.closedBrace);
            cc.toSybaseString();
            final String nationalStringFromCC = cc.getNational();
            if (nationalStringFromCC != null) {
                this.setDatatypeName(nationalStringFromCC + " " + cc.getDatatypeName());
            }
            else {
                this.setDatatypeName(cc.getDatatypeName());
            }
            this.setPrecision(cc.getSize());
            this.setOpenBrace(cc.getOpenBrace());
            this.setClosedBrace(cc.getClosedBrace());
            this.setVarying(cc.getVarying());
        }
        if (this.getArray() != null) {
            this.setArray(null);
        }
    }
    
    @Override
    public void toNetezzaString() throws ConvertException {
        String objectName = "";
        this.datatypeSettings();
        if (this.datatypeName != null) {
            objectName = this.getObjectTypeForTheCorrespondingDataTypeName(this.datatypeName);
        }
        if (objectName.equals("NUMERIC")) {
            final NumericClass nc = new NumericClass();
            nc.setDatatypeName(this.datatypeName);
            nc.setPrecision(this.precision);
            nc.setScale(this.scale);
            nc.setOpenBrace(this.openBrace);
            nc.setClosedBrace(this.closedBrace);
            nc.toNetezzaString();
            this.setDatatypeName(nc.getDatatypeName());
            this.setPrecision(nc.getPrecision());
            this.setScale(nc.getScale());
            this.setOpenBrace(nc.getOpenBrace());
            this.setClosedBrace(nc.getClosedBrace());
        }
        else if (objectName.equals("DATE")) {
            final DateClass dc = new DateClass();
            dc.setDatatypeName(this.datatypeName);
            dc.setSize(this.precision);
            dc.setClosedBrace(this.closedBrace);
            dc.setOpenBrace(this.openBrace);
            dc.toNetezzaString();
            this.setDatatypeName(dc.getDatatypeName());
            this.setOpenBrace(dc.getOpenBrace());
            this.setClosedBrace(dc.getClosedBrace());
            this.setPrecision(dc.getSize());
        }
        else if (objectName.equals("BIN")) {
            final BinClass bc = new BinClass();
            bc.setDatatypeName(this.datatypeName);
            bc.setSize(this.precision);
            bc.setVarying(this.varyingType);
            bc.setOpenBrace(this.openBrace);
            bc.setClosedBrace(this.closedBrace);
            bc.toNetezzaString();
            this.setDatatypeName(bc.getDatatypeName());
            this.setPrecision(bc.getSize());
            this.setOpenBrace(bc.getOpenBrace());
            this.setClosedBrace(bc.getClosedBrace());
            this.setVarying(this.varyingType);
        }
        else if (objectName.equals("CHAR")) {
            final CharacterClass cc = new CharacterClass();
            String nationalString = null;
            String datatypeWithoutNational = null;
            if (this.datatypeName.toUpperCase().startsWith("NATIONAL")) {
                nationalString = this.datatypeName.substring(this.datatypeName.toUpperCase().indexOf("NATIONAL"));
                datatypeWithoutNational = this.datatypeName.substring(this.datatypeName.toUpperCase().indexOf("NATIONAL") + 1, this.datatypeName.length());
            }
            if (nationalString != null) {
                cc.setNational(nationalString);
            }
            if (datatypeWithoutNational != null) {
                cc.setDatatypeName(datatypeWithoutNational);
            }
            else {
                cc.setDatatypeName(this.datatypeName);
            }
            cc.setSize(this.precision);
            cc.setVarying(this.varyingType);
            cc.setOpenBrace(this.openBrace);
            cc.setClosedBrace(this.closedBrace);
            cc.toNetezzaString();
            final String nationalStringFromCC = cc.getNational();
            if (nationalStringFromCC != null) {
                this.setDatatypeName(nationalStringFromCC + " " + cc.getDatatypeName());
            }
            else {
                this.setDatatypeName(cc.getDatatypeName());
            }
            this.setPrecision(cc.getSize());
            this.setOpenBrace(cc.getOpenBrace());
            this.setClosedBrace(cc.getClosedBrace());
            this.setVarying(cc.getVarying());
            this.setClosedBrace(cc.getClosedBrace());
            this.setOpenBrace(cc.getOpenBrace());
        }
        if (this.getArray() != null) {
            this.setArray(null);
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.datatypeName != null) {
            sb.append(this.datatypeName + " ");
        }
        if (this.varyingType != null) {
            sb.append(this.varyingType + " ");
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
        if (this.withLocalTimeZone != null) {
            sb.append(" " + this.withLocalTimeZone + " ");
        }
        return sb.toString();
    }
    
    @Override
    public void setArray(final String arrayStr) {
        this.arrayStr = arrayStr;
    }
    
    @Override
    public String getArray() {
        return this.arrayStr;
    }
}
