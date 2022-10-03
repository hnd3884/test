package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import java.util.ArrayList;

public class CharacterClass implements Datatype
{
    private String datatypeName;
    private String openBrace;
    private String closedBrace;
    private String size;
    private String nationalType;
    private String varyingType;
    private String doBinarySearch;
    private String notlogged;
    private String ascii;
    private String unicode;
    private String SAPbyte;
    private ArrayList enumValues;
    private ArrayList setValues;
    private String arrayStr;
    private String caseSpecificPhrase;
    
    public void setNational(final String nationalType) {
        this.nationalType = nationalType;
    }
    
    public void setVarying(final String varyingType) {
        this.varyingType = varyingType;
    }
    
    public void setBinary(final String doBinarySearch) {
        this.doBinarySearch = doBinarySearch;
    }
    
    @Override
    public void setDatatypeName(final String datatypeName) {
        this.datatypeName = datatypeName;
    }
    
    public void setAscii(final String ascii) {
        this.ascii = ascii;
    }
    
    public void setUnicode(final String unicode) {
        this.unicode = unicode;
    }
    
    public void setByte(final String SAPbyte) {
        this.SAPbyte = SAPbyte;
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
    
    public void setNotLogged(final String notlogged) {
        this.notlogged = notlogged;
    }
    
    public void setEnumValues(final ArrayList enumValues) {
        this.enumValues = enumValues;
    }
    
    public void setSetValues(final ArrayList setValues) {
        this.setValues = setValues;
    }
    
    public void setCaseSpecificPhrase(final String caseSpecPhrase) {
        this.caseSpecificPhrase = caseSpecPhrase;
    }
    
    @Override
    public String getDatatypeName() {
        return this.datatypeName;
    }
    
    @Override
    public String getSize() {
        return this.size;
    }
    
    public String getNational() {
        return this.nationalType;
    }
    
    public String getVarying() {
        return this.varyingType;
    }
    
    public String getBinary() {
        return this.doBinarySearch;
    }
    
    @Override
    public String getOpenBrace() {
        return this.openBrace;
    }
    
    @Override
    public String getClosedBrace() {
        return this.closedBrace;
    }
    
    public String getNotLogged() {
        return this.notlogged;
    }
    
    public String getAscii() {
        return this.ascii;
    }
    
    public String getUnicode() {
        return this.unicode;
    }
    
    public String getByte() {
        return this.SAPbyte;
    }
    
    public ArrayList getEnumValues() {
        return this.enumValues;
    }
    
    public ArrayList getSetValues() {
        return this.setValues;
    }
    
    public String getCaseSpecificPhrase() {
        return this.caseSpecificPhrase;
    }
    
    @Override
    public void toInformixString() {
        this.setNotLogged(null);
        if (this.getDatatypeName() != null) {
            if (this.getBinary() != null) {
                this.setBinary(null);
            }
            final String changeIfxDatatype = this.getDatatypeName();
            if (this.getNational() != null || this.getUnicode() != null) {
                if (changeIfxDatatype.equalsIgnoreCase("CHAR") || changeIfxDatatype.equalsIgnoreCase("CHARACTER")) {
                    if (this.getVarying() != null) {
                        this.setDatatypeName("NVARCHAR");
                        this.setVarying(null);
                    }
                    else {
                        this.setDatatypeName("NCHAR");
                    }
                    this.setNational(null);
                }
                else if (changeIfxDatatype.equalsIgnoreCase("VARCHAR") && this.getUnicode() != null) {
                    this.setDatatypeName("NVARCHAR");
                }
            }
            else if (changeIfxDatatype.equalsIgnoreCase("NCHAR")) {
                if (this.getVarying() != null) {
                    this.setDatatypeName("NVARCHAR");
                    this.setVarying(null);
                }
            }
            else if (changeIfxDatatype.equalsIgnoreCase("VARCHAR2")) {
                this.setDatatypeName("VARCHAR");
            }
            else if (changeIfxDatatype.equalsIgnoreCase("NVARCHAR2")) {
                this.setDatatypeName("NVARCHAR");
            }
            else if (changeIfxDatatype.equalsIgnoreCase("NTEXT")) {
                this.setDatatypeName("TEXT");
            }
            else if (changeIfxDatatype.equalsIgnoreCase("BPCHAR")) {
                this.setDatatypeName("CHAR");
            }
            else if (changeIfxDatatype.equalsIgnoreCase("LONG") || changeIfxDatatype.equalsIgnoreCase("NCLOB") || changeIfxDatatype.equalsIgnoreCase("DBCLOB")) {
                this.setDatatypeName("TEXT");
            }
            else if (changeIfxDatatype.equalsIgnoreCase("ENUM")) {
                this.setDatatypeName("VARCHAR");
                this.setOpenBrace("(");
                this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                this.setClosedBrace(")");
            }
            if (this.getAscii() != null) {
                this.setAscii(null);
            }
            if (this.getUnicode() != null) {
                this.setUnicode(null);
            }
            if (this.getByte() != null) {
                this.setByte(null);
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
            if (this.getNational() != null) {
                this.setNational(null);
            }
            if (this.getBinary() != null) {
                this.setBinary("FOR BIT DATA");
            }
            if (this.notlogged != null) {
                this.setNotLogged(" NOT LOGGED");
            }
            if (changeDB2Datatype.equalsIgnoreCase("CLOB") || changeDB2Datatype.equalsIgnoreCase("NCLOB") || changeDB2Datatype.equalsIgnoreCase("TEXT") || changeDB2Datatype.equalsIgnoreCase("LONG")) {
                this.setDatatypeName("CLOB");
                if (this.getSize() == null) {
                    this.setOpenBrace("(");
                    this.setSize("2G");
                    this.setClosedBrace(")");
                    this.setNotLogged(" NOT LOGGED");
                }
                else {
                    try {
                        final int precisionValue = Integer.parseInt(this.getSize());
                        if (precisionValue >= 1073741823) {
                            this.setSize("2G");
                            this.setNotLogged(" NOT LOGGED");
                        }
                    }
                    catch (final NumberFormatException ex) {}
                }
            }
            else if (SwisSQLOptions.fromAccess && changeDB2Datatype.equalsIgnoreCase("LONGCHAR")) {
                this.setDatatypeName("CLOB (2G) NOT LOGGED");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (changeDB2Datatype.equalsIgnoreCase("NTEXT")) {
                this.setDatatypeName("CLOB");
                this.setOpenBrace("(");
                this.setSize("1073741823");
                this.setClosedBrace(")");
                this.setNotLogged(" NOT LOGGED");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("CHAR") || changeDB2Datatype.equalsIgnoreCase("CHARACTER")) {
                final String sizeStr = this.getSize();
                if (sizeStr != null) {
                    final int size = Integer.parseInt(sizeStr);
                    if (size > 254) {
                        this.setDatatypeName("VARCHAR");
                    }
                }
                if (this.getVarying() != null) {
                    this.setDatatypeName("VARCHAR");
                    this.setVarying(null);
                }
                if (this.getUnicode() != null) {
                    this.setDatatypeName("GRAPHIC");
                }
            }
            else if (changeDB2Datatype.equalsIgnoreCase("NVARCHAR") || changeDB2Datatype.equalsIgnoreCase("NVARCHAR2") || changeDB2Datatype.equalsIgnoreCase("VARCHAR2")) {
                this.setDatatypeName("VARCHAR");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("NCHAR")) {
                final String sizeStr = this.getSize();
                if (sizeStr != null) {
                    final int size = Integer.parseInt(sizeStr);
                    if (size > 254) {
                        this.setDatatypeName("VARCHAR");
                    }
                    else {
                        this.setDatatypeName("CHAR");
                    }
                }
                else {
                    this.setDatatypeName("CHAR");
                }
            }
            else if (changeDB2Datatype.equalsIgnoreCase("BPCHAR")) {
                this.setDatatypeName("CHAR");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("\"CHAR\"")) {
                this.setDatatypeName("CHAR");
                this.setOpenBrace("(");
                this.setSize("1");
                this.setClosedBrace(")");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("VARCHAR") && this.getUnicode() != null) {
                this.setDatatypeName("VARGRAPHIC");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("ENUM")) {
                this.setDatatypeName("VARCHAR");
                this.setOpenBrace("(");
                this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                this.setClosedBrace(")");
            }
            if (this.getAscii() != null) {
                this.setAscii(null);
            }
            if (this.getUnicode() != null) {
                this.setUnicode(null);
            }
            if (this.getByte() != null) {
                this.setByte(null);
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toOracleString() {
        this.setNotLogged(null);
        if (this.getDatatypeName() != null) {
            final String changeOracleDatatype = this.getDatatypeName();
            if (changeOracleDatatype.equalsIgnoreCase("CLOB") || changeOracleDatatype.equalsIgnoreCase("DBCLOB")) {
                this.setDatatypeName("CLOB");
                if (this.getSize() != null) {
                    this.setOpenBrace(null);
                    this.setSize(null);
                    this.setClosedBrace(null);
                    this.setDatatypeName("LONG");
                }
            }
            else if (changeOracleDatatype.equalsIgnoreCase("XML")) {
                this.setDatatypeName("CLOB");
            }
            else if (changeOracleDatatype.equalsIgnoreCase("TEXT")) {
                this.setDatatypeName("CLOB");
            }
            else if (SwisSQLOptions.fromAccess && changeOracleDatatype.equalsIgnoreCase("LONGCHAR")) {
                this.setDatatypeName("CLOB");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (changeOracleDatatype.equalsIgnoreCase("NTEXT")) {
                this.setDatatypeName("NCLOB");
            }
            else if (changeOracleDatatype.equalsIgnoreCase("VARCHAR")) {
                if (this.getUnicode() != null) {
                    this.setDatatypeName("NVARCHAR2");
                    if (this.size != null && Integer.parseInt(this.size) > 2000) {
                        this.setSize("2000");
                    }
                }
                else if (this.getBinary() != null) {
                    this.setDatatypeName("RAW");
                    if (this.size != null && Integer.parseInt(this.size) > 2000) {
                        this.setDatatypeName("LONG RAW");
                        this.setSize(null);
                        this.setClosedBrace(null);
                        this.setOpenBrace(null);
                    }
                }
                else {
                    this.setDatatypeName("VARCHAR2");
                }
                try {
                    if (this.size != null && Integer.parseInt(this.size) > 4000) {
                        this.setDatatypeName("CLOB");
                        this.setOpenBrace(null);
                        this.setClosedBrace(null);
                        this.setSize(null);
                    }
                }
                catch (final NumberFormatException nfe) {
                    if (this.size != null && this.size.equalsIgnoreCase("max")) {
                        this.setDatatypeName("CLOB");
                        this.setOpenBrace(null);
                        this.setClosedBrace(null);
                        this.setSize(null);
                    }
                }
            }
            else if (changeOracleDatatype.equalsIgnoreCase("NVARCHAR")) {
                this.setDatatypeName("NVARCHAR2");
                try {
                    if (this.size != null && Integer.parseInt(this.size) > 2000) {
                        this.setDatatypeName("NCLOB");
                        this.setOpenBrace(null);
                        this.setClosedBrace(null);
                        this.setSize(null);
                    }
                }
                catch (final NumberFormatException nfe) {
                    if (this.size != null && this.size.equalsIgnoreCase("max")) {
                        this.setDatatypeName("NCLOB");
                        this.setOpenBrace(null);
                        this.setClosedBrace(null);
                        this.setSize(null);
                    }
                }
            }
            else if (changeOracleDatatype.equalsIgnoreCase("LONG VARCHAR")) {
                this.setDatatypeName("LONG");
                if (this.getBinary() != null) {
                    this.setDatatypeName("LONG RAW");
                }
            }
            else if (changeOracleDatatype.equalsIgnoreCase("\"CHAR\"")) {
                this.setDatatypeName("CHAR");
                this.setOpenBrace("(");
                this.setSize("1");
                this.setClosedBrace(")");
            }
            else if (changeOracleDatatype.equalsIgnoreCase("BPCHAR")) {
                this.setDatatypeName("CHAR");
            }
            else if (changeOracleDatatype.equalsIgnoreCase("CHAR") && this.getOpenBrace() != null && this.getUnicode() != null) {
                this.setDatatypeName("NCHAR");
            }
            else if (changeOracleDatatype.equalsIgnoreCase("CHAR") || changeOracleDatatype.equalsIgnoreCase("CHARACTER")) {
                if (this.size != null && Integer.parseInt(this.size) > 2000) {
                    this.setDatatypeName("CLOB");
                    this.setOpenBrace(null);
                    this.setClosedBrace(null);
                    this.setSize(null);
                    this.setVarying(null);
                }
            }
            else if (changeOracleDatatype.equalsIgnoreCase("NCHAR")) {
                if (this.size != null && Integer.parseInt(this.size) > 2000) {
                    this.setDatatypeName("NCLOB");
                    this.setOpenBrace(null);
                    this.setClosedBrace(null);
                    this.setSize(null);
                    this.setVarying(null);
                }
            }
            else if (changeOracleDatatype.equalsIgnoreCase("ENUM")) {
                this.setDatatypeName("VARCHAR2");
                this.setOpenBrace("(");
                this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                this.setClosedBrace(")");
            }
            else if ((changeOracleDatatype.equalsIgnoreCase("CHAR") || changeOracleDatatype.equalsIgnoreCase("CHARACTER")) && this.getBinary() != null) {
                this.setDatatypeName("RAW");
                if (this.size == null) {
                    this.setOpenBrace("(");
                    this.setSize("1");
                    this.setClosedBrace(")");
                }
            }
            else if (changeOracleDatatype.equalsIgnoreCase("LONG") && this.getUnicode() != null) {
                this.setDatatypeName("CLOB");
            }
            if (this.getAscii() != null) {
                this.setAscii(null);
            }
            if (this.getUnicode() != null) {
                this.setUnicode(null);
            }
            if (this.getByte() != null) {
                this.setByte(null);
            }
            if (this.getBinary() != null) {
                this.setBinary(null);
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toMSSQLServerString() {
        this.setNotLogged(null);
        if (this.getDatatypeName() != null) {
            final String changeSQLServerDatatype = this.getDatatypeName();
            if (this.getBinary() != null) {
                this.setBinary(null);
            }
            if (this.getNational() != null || this.getUnicode() != null) {
                if (changeSQLServerDatatype.equalsIgnoreCase("CHAR") || changeSQLServerDatatype.equalsIgnoreCase("CHARACTER")) {
                    this.setDatatypeName("NCHAR");
                    this.setNational(null);
                }
                else if (changeSQLServerDatatype.equalsIgnoreCase("VARCHAR") && this.getUnicode() != null) {
                    this.setDatatypeName("NVARCHAR");
                }
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("VARCHAR2")) {
                this.setDatatypeName("VARCHAR");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("NVARCHAR2")) {
                this.setDatatypeName("NVARCHAR");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("CHARACTER")) {
                this.setDatatypeName("CHAR");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("CLOB") || changeSQLServerDatatype.equalsIgnoreCase("DBCLOB") || changeSQLServerDatatype.equalsIgnoreCase("LONG") || changeSQLServerDatatype.equalsIgnoreCase("LONG VARCHAR")) {
                this.setDatatypeName("TEXT");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("LONGCHAR")) {
                this.setDatatypeName("TEXT");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("NCLOB")) {
                this.setDatatypeName("NTEXT");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("\"CHAR\"")) {
                this.setDatatypeName("CHAR");
                this.setOpenBrace("(");
                this.setSize("1");
                this.setClosedBrace(")");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("BPCHAR")) {
                this.setDatatypeName("CHAR");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("GRAPHIC")) {
                this.setDatatypeName("NCHAR");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("ENUM")) {
                this.setDatatypeName("VARCHAR");
                this.setOpenBrace("(");
                this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                this.setClosedBrace(")");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("SET")) {
                this.setDatatypeName("SET");
                this.setOpenBrace("(");
                String s = this.getSetValues().toString();
                s = s.substring(1, s.length() - 1);
                this.setSize(s);
                this.setClosedBrace(")");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("LONG") && this.getUnicode() != null) {
                this.setDatatypeName("TEXT");
            }
            if (this.getAscii() != null) {
                this.setAscii(null);
            }
            if (this.getUnicode() != null) {
                this.setUnicode(null);
            }
            if (this.getByte() != null) {
                this.setByte(null);
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toSybaseString() {
        this.setNotLogged(null);
        if (this.getDatatypeName() != null) {
            final String changeSQLServerDatatype = this.getDatatypeName();
            if (this.getBinary() != null) {
                this.setBinary(null);
            }
            else if ((this.getNational() != null && this.getVarying() != null) || this.getUnicode() != null) {
                if (changeSQLServerDatatype.equalsIgnoreCase("CHAR") || changeSQLServerDatatype.equalsIgnoreCase("CHARACTER") || changeSQLServerDatatype.equalsIgnoreCase("NCHAR")) {
                    this.setDatatypeName("NVARCHAR");
                    this.setNational(null);
                    this.setVarying(null);
                }
                else if (changeSQLServerDatatype.equalsIgnoreCase("VARCHAR") && this.getUnicode() != null) {
                    this.setDatatypeName("NVARCHAR");
                }
            }
            else if (this.getNational() != null) {
                if (changeSQLServerDatatype.equalsIgnoreCase("CHAR") || changeSQLServerDatatype.equalsIgnoreCase("CHARACTER")) {
                    this.setDatatypeName("NCHAR");
                    this.setNational(null);
                }
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("VARCHAR2")) {
                this.setDatatypeName("VARCHAR");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("NVARCHAR2")) {
                this.setDatatypeName("NVARCHAR");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("CHARACTER")) {
                this.setDatatypeName("CHAR");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("CLOB") || changeSQLServerDatatype.equalsIgnoreCase("NCLOB") || changeSQLServerDatatype.equalsIgnoreCase("DBCLOB") || changeSQLServerDatatype.equalsIgnoreCase("LONG") || changeSQLServerDatatype.equalsIgnoreCase("LONG VARCHAR") || changeSQLServerDatatype.equalsIgnoreCase("NTEXT")) {
                this.setDatatypeName("TEXT");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("LONGCHAR")) {
                this.setDatatypeName("TEXT");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("\"CHAR\"")) {
                this.setDatatypeName("CHAR");
                this.setOpenBrace("(");
                this.setSize("1");
                this.setClosedBrace(")");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("BPCHAR")) {
                this.setDatatypeName("CHAR");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("ENUM")) {
                this.setDatatypeName("VARCHAR");
                this.setOpenBrace("(");
                this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                this.setClosedBrace(")");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("LONG") && this.getUnicode() != null) {
                this.setDatatypeName("TEXT");
            }
            if (this.getAscii() != null) {
                this.setAscii(null);
            }
            if (this.getUnicode() != null) {
                this.setUnicode(null);
            }
            if (this.getByte() != null) {
                this.setByte(null);
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toPostgreSQLString() {
        this.setNotLogged(null);
        if (this.getDatatypeName() != null) {
            final String changePostgreSQLDatatype = this.getDatatypeName();
            if (this.getBinary() != null) {
                this.setBinary(null);
                this.setDatatypeName("BYTEA");
                this.setSize(null);
                this.setClosedBrace(null);
                this.setOpenBrace(null);
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("CHARACTER")) {
                if (this.getVarying() != null) {
                    this.setDatatypeName("VARCHAR");
                    this.setVarying(null);
                }
                else {
                    this.setDatatypeName("CHAR");
                }
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("VARCHAR2")) {
                this.setDatatypeName("VARCHAR");
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("NVARCHAR2") || changePostgreSQLDatatype.equalsIgnoreCase("NVARCHAR")) {
                this.setDatatypeName("CHAR");
                this.setNational("NATIONAL");
                this.setVarying("VARYING");
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("NCHAR")) {
                this.setDatatypeName("CHAR");
                this.setNational("NATIONAL");
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("CLOB") || changePostgreSQLDatatype.equalsIgnoreCase("NCLOB") || changePostgreSQLDatatype.equalsIgnoreCase("DBCLOB") || changePostgreSQLDatatype.equalsIgnoreCase("LONG") || changePostgreSQLDatatype.equalsIgnoreCase("NTEXT") || changePostgreSQLDatatype.equalsIgnoreCase("LONG VARCHAR")) {
                this.setDatatypeName("TEXT");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("CHAR") && this.getUnicode() != null) {
                this.setDatatypeName("CHAR");
                this.setNational("NATIONAL");
                this.setVarying("VARYING");
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("BPCHAR")) {
                this.setDatatypeName("BPCHAR");
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("VARCHAR") && this.getUnicode() != null) {
                this.setDatatypeName("VARCHAR");
                this.setNational("NATIONAL");
                this.setVarying("VARYING");
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("ENUM")) {
                this.setDatatypeName("VARCHAR");
                this.setOpenBrace("(");
                this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                this.setClosedBrace(")");
            }
            if (this.getAscii() != null) {
                this.setAscii(null);
            }
            if (this.getUnicode() != null) {
                this.setUnicode(null);
            }
            if (this.getByte() != null) {
                this.setByte(null);
            }
        }
    }
    
    @Override
    public void toMySQLString() {
        this.setNotLogged(null);
        if (this.getDatatypeName() != null) {
            final String changeMySQLDatatype = this.getDatatypeName();
            if (SwisSQLOptions.fromAccess && changeMySQLDatatype.equalsIgnoreCase("LONGCHAR")) {
                this.setDatatypeName("TEXT");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (changeMySQLDatatype.equalsIgnoreCase("VARCHAR2")) {
                this.setDatatypeName("VARCHAR");
            }
            if (this.getBinary() != null) {
                this.setBinary("BINARY");
            }
            else if (changeMySQLDatatype.equalsIgnoreCase("NVARCHAR2") || changeMySQLDatatype.equalsIgnoreCase("NVARCHAR")) {
                this.setNational("NATIONAL");
                this.setDatatypeName("VARCHAR");
            }
            else if (changeMySQLDatatype.equalsIgnoreCase("NCHAR")) {
                this.setNational("NATIONAL");
                this.setDatatypeName("CHAR");
            }
            else if (changeMySQLDatatype.equalsIgnoreCase("CLOB") || changeMySQLDatatype.equalsIgnoreCase("NCLOB") || changeMySQLDatatype.equalsIgnoreCase("DBCLOB") || changeMySQLDatatype.equalsIgnoreCase("LONG") || changeMySQLDatatype.equalsIgnoreCase("LONG VARCHAR") || changeMySQLDatatype.equalsIgnoreCase("NTEXT")) {
                this.setDatatypeName("LONGTEXT");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (changeMySQLDatatype.equalsIgnoreCase("\"CHAR\"")) {
                this.setDatatypeName("CHAR");
                this.setOpenBrace("(");
                this.setSize("1");
                this.setClosedBrace(")");
            }
            else if (changeMySQLDatatype.equalsIgnoreCase("BPCHAR")) {
                this.setDatatypeName("CHAR");
            }
            else if (changeMySQLDatatype.equalsIgnoreCase("VARCHAR") && this.getUnicode() != null) {
                this.setNational("NATIONAL");
                this.setDatatypeName("VARCHAR");
            }
            else if (changeMySQLDatatype.equalsIgnoreCase("CHAR") && this.getUnicode() != null) {
                this.setNational("NATIONAL");
                this.setDatatypeName("CHAR");
            }
            if (this.getAscii() != null) {
                this.setAscii(null);
            }
            if (this.getUnicode() != null) {
                this.setUnicode(null);
            }
            if (this.getByte() != null) {
                this.setByte(null);
            }
            if ((this.getDatatypeName().equalsIgnoreCase("VARCHAR") || this.getDatatypeName().equalsIgnoreCase("CHAR") || this.getDatatypeName().equalsIgnoreCase("NCHAR") || this.getDatatypeName().equalsIgnoreCase("NVARCHAR") || this.getDatatypeName().equalsIgnoreCase("CHARACTER")) && this.size != null) {
                final int temp = Integer.parseInt(this.size);
                if (this.getVarying() != null && temp < 255) {
                    this.setDatatypeName("VARCHAR");
                }
                if (temp > 255) {
                    this.setDatatypeName("text");
                    this.setSize(null);
                    this.setOpenBrace(null);
                    this.setClosedBrace(null);
                    this.setNational(null);
                }
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toANSIString() {
        this.setNotLogged(null);
        if (this.getDatatypeName() != null) {
            final String changeANSIDatatype = this.getDatatypeName();
            if (this.getBinary() != null) {
                this.setBinary(null);
            }
            if (changeANSIDatatype.equalsIgnoreCase("CLOB") || changeANSIDatatype.equalsIgnoreCase("DBCLOB")) {
                this.setDatatypeName("CLOB");
                if (this.getSize() != null) {
                    this.setOpenBrace(null);
                    this.setSize(null);
                    this.setClosedBrace(null);
                }
            }
            else if (changeANSIDatatype.equalsIgnoreCase("TEXT") || changeANSIDatatype.equalsIgnoreCase("NTEXT")) {
                this.setDatatypeName("LONG");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("VARCHAR2")) {
                this.setDatatypeName("VARCHAR");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("NVARCHAR2")) {
                this.setDatatypeName("NVARCHAR");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("NCHAR")) {
                this.setDatatypeName("CHAR");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("LONG VARCHAR")) {
                this.setDatatypeName("LONG");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("\"CHAR\"")) {
                this.setDatatypeName("CHAR");
                this.setOpenBrace("(");
                this.setSize("1");
                this.setClosedBrace(")");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("BPCHAR")) {
                this.setDatatypeName("CHAR");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("VARCHAR") && this.getUnicode() != null) {
                this.setDatatypeName("NVARCHAR");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("CHAR") && this.getUnicode() != null) {
                this.setDatatypeName("NCHAR");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("ENUM")) {
                this.setDatatypeName("VARCHAR");
                this.setOpenBrace("(");
                this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                this.setClosedBrace(")");
            }
            if (this.getAscii() != null) {
                this.setAscii(null);
            }
            if (this.getUnicode() != null) {
                this.setUnicode(null);
            }
            if (this.getByte() != null) {
                this.setByte(null);
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toTimesTenString() {
        this.setNotLogged(null);
        if (this.getDatatypeName() != null) {
            final String sourceType = this.getDatatypeName();
            if (this.getBinary() != null) {
                this.setBinary(null);
            }
            if (sourceType.equalsIgnoreCase("NCHAR") && this.getVarying() != null) {
                this.setDatatypeName("NVARCHAR");
            }
            else if (sourceType.equalsIgnoreCase("CHAR") || sourceType.equalsIgnoreCase("CHARACTER")) {
                if (this.getNational() != null && this.getVarying() != null) {
                    this.setDatatypeName("NVARCHAR");
                    if (this.size == null) {
                        this.setOpenBrace("(");
                        this.setClosedBrace(")");
                        this.setSize("1");
                    }
                }
                if (this.getNational() != null && this.getVarying() == null) {
                    this.setDatatypeName("NCHAR");
                }
                if (this.getNational() == null && this.getVarying() != null) {
                    this.setDatatypeName("VARCHAR");
                }
            }
            this.setNational(null);
            this.setVarying(null);
            if (sourceType.equalsIgnoreCase("VARCHAR2")) {
                this.setDatatypeName("VARCHAR");
            }
            else if (sourceType.equalsIgnoreCase("NVARCHAR2")) {
                this.setDatatypeName("NVARCHAR");
            }
            else if (sourceType.equalsIgnoreCase("CLOB") || sourceType.equalsIgnoreCase("DBCLOB") || sourceType.equalsIgnoreCase("LONG") || sourceType.equalsIgnoreCase("LONG VARCHAR") || sourceType.equalsIgnoreCase("NTEXT") || sourceType.equalsIgnoreCase("TEXT")) {
                this.setDatatypeName("VARCHAR");
                this.setOpenBrace("(");
                this.setSize("4194304");
                this.setClosedBrace(")");
            }
            else if (sourceType.equalsIgnoreCase("NCLOB")) {
                this.setDatatypeName("NVARCHAR");
                this.setOpenBrace("(");
                this.setSize("2097152");
                this.setClosedBrace(")");
            }
            else if (sourceType.equalsIgnoreCase("\"CHAR\"")) {
                this.setDatatypeName("CHAR");
            }
            else if (sourceType.equalsIgnoreCase("BPCHAR")) {
                this.setDatatypeName("CHAR");
            }
            else if (sourceType.equalsIgnoreCase("UNICHAR")) {
                this.setDatatypeName("NCHAR");
                this.setOpenBrace("(");
                this.setSize("4150");
                this.setClosedBrace(")");
            }
            else if (sourceType.equalsIgnoreCase("UNIVARCHAR")) {
                this.setDatatypeName("NVARCHAR");
                this.setOpenBrace("(");
                this.setSize("2097152");
                this.setClosedBrace(")");
            }
            else if (sourceType.equalsIgnoreCase("ENUM")) {
                this.setDatatypeName("VARCHAR");
                this.setOpenBrace("(");
                this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                this.setClosedBrace(")");
            }
            else if ((sourceType.equalsIgnoreCase("VARCHAR") || sourceType.equalsIgnoreCase("NVARCHAR")) && this.size == null) {
                this.setOpenBrace("(");
                this.setClosedBrace(")");
                this.setSize("1");
            }
            else if (sourceType.equalsIgnoreCase("CHAR") && this.size != null) {
                if (Integer.parseInt(this.size) > 8300) {
                    this.setSize("8300");
                }
            }
            else if (sourceType.equalsIgnoreCase("VARCHAR") && this.size != null) {
                if (Integer.parseInt(this.size) > 4194304) {
                    this.setSize("4194304");
                }
            }
            else if (sourceType.equalsIgnoreCase("NVARCHAR") && this.size != null) {
                if (Integer.parseInt(this.size) > 2097152) {
                    this.setSize("2097152");
                }
            }
            else if (sourceType.equalsIgnoreCase("NCHAR") && this.size != null && Integer.parseInt(this.size) > 4150) {
                this.setSize("4150");
            }
            if (this.getAscii() != null) {
                this.setAscii(null);
            }
            if (this.getUnicode() != null) {
                this.setUnicode(null);
            }
            if (this.getByte() != null) {
                this.setByte(null);
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toNetezzaString() {
        final String varcharMaxSize = "32760";
        final String nvarcharMaxSize = "8192";
        this.setNotLogged(null);
        if (this.getDatatypeName() != null) {
            final String changeNetezzaDatatype = this.getDatatypeName();
            if (this.getBinary() != null) {
                this.setBinary(null);
            }
            if (changeNetezzaDatatype.equalsIgnoreCase("CLOB") || changeNetezzaDatatype.equalsIgnoreCase("DBCLOB")) {
                this.setDatatypeName("VARCHAR");
                this.setOpenBrace("(");
                this.setSize(varcharMaxSize);
                this.setClosedBrace(")");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("TEXT") || changeNetezzaDatatype.equalsIgnoreCase("NTEXT")) {
                this.setDatatypeName("VARCHAR");
                this.setOpenBrace("(");
                this.setSize(varcharMaxSize);
                this.setClosedBrace(")");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("VARCHAR2")) {
                this.setDatatypeName("VARCHAR");
                try {
                    if (this.getSize() != null && Integer.parseInt(this.getSize()) < 17) {
                        this.setDatatypeName("CHAR");
                    }
                }
                catch (final NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("NVARCHAR2")) {
                this.setDatatypeName("NVARCHAR");
                try {
                    final int nvarcharSize = Integer.parseInt(this.getSize());
                    if (nvarcharSize > 8192) {
                        this.setSize(nvarcharMaxSize);
                    }
                }
                catch (final NumberFormatException ex) {}
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("NCHAR")) {
                this.setDatatypeName("NVARCHAR");
                try {
                    final int nvarcharSize = Integer.parseInt(this.getSize());
                    if (nvarcharSize > 8192) {
                        this.setSize(nvarcharMaxSize);
                    }
                }
                catch (final NumberFormatException ex2) {}
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("LONG") || changeNetezzaDatatype.equalsIgnoreCase("LONG VARCHAR")) {
                this.setDatatypeName("VARCHAR");
                this.setOpenBrace("(");
                this.setSize(varcharMaxSize);
                this.setClosedBrace(")");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("\"CHAR\"")) {
                this.setDatatypeName("CHAR");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("BPCHAR")) {
                this.setDatatypeName("CHAR");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("VARCHAR") && this.getUnicode() != null) {
                this.setDatatypeName("VARCHAR");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("CHAR") && this.getUnicode() != null) {
                this.setDatatypeName("VARCHAR");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("ENUM")) {
                this.setDatatypeName("VARCHAR");
                this.setOpenBrace("(");
                this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                this.setClosedBrace(")");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("NCLOB")) {
                this.setDatatypeName("NVARCHAR");
                this.setOpenBrace("(");
                this.setSize(nvarcharMaxSize);
                this.setClosedBrace(")");
            }
            if (this.getAscii() != null) {
                this.setAscii(null);
            }
            if (this.getUnicode() != null) {
                this.setUnicode(null);
            }
            if (this.getByte() != null) {
                this.setByte(null);
            }
        }
    }
    
    @Override
    public void toTeradataString() {
        this.setNotLogged(null);
        if (this.getDatatypeName() != null) {
            final String changeTeradataDatatype = this.getDatatypeName();
            if (this.getBinary() != null) {
                this.setBinary(null);
            }
            if (changeTeradataDatatype.equalsIgnoreCase("CLOB") || changeTeradataDatatype.equalsIgnoreCase("DBCLOB")) {
                this.setDatatypeName("CLOB");
                if (this.getSize() != null) {
                    this.setOpenBrace(null);
                    this.setSize(null);
                    this.setClosedBrace(null);
                }
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("TEXT") || changeTeradataDatatype.equalsIgnoreCase("NTEXT")) {
                this.setDatatypeName("LONG");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("VARCHAR2")) {
                this.setDatatypeName("VARCHAR");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("NVARCHAR2")) {
                this.setDatatypeName("NVARCHAR");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("NCHAR")) {
                this.setDatatypeName("CHAR");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("LONG VARCHAR")) {
                this.setDatatypeName("LONG");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("\"CHAR\"")) {
                this.setDatatypeName("CHAR");
                this.setOpenBrace("(");
                this.setSize("1");
                this.setClosedBrace(")");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("BPCHAR")) {
                this.setDatatypeName("CHAR");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("VARCHAR") && this.getUnicode() != null) {
                this.setDatatypeName("NVARCHAR");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("CHAR") && this.getUnicode() != null) {
                this.setDatatypeName("NCHAR");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("ENUM")) {
                this.setDatatypeName("VARCHAR");
                this.setOpenBrace("(");
                this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                this.setClosedBrace(")");
            }
            if (this.getAscii() != null) {
                this.setAscii(null);
            }
            if (this.getUnicode() != null) {
                this.setUnicode(null);
            }
            if (this.getByte() != null) {
                this.setByte(null);
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    public Datatype copyObjectValues() {
        final CharacterClass newCharacterClass = new CharacterClass();
        newCharacterClass.setClosedBrace(this.closedBrace);
        newCharacterClass.setDatatypeName(this.getDatatypeName());
        newCharacterClass.setOpenBrace(this.openBrace);
        newCharacterClass.setSize(this.getSize());
        newCharacterClass.setNational(this.getNational());
        newCharacterClass.setVarying(this.getVarying());
        newCharacterClass.setBinary(this.getBinary());
        newCharacterClass.setAscii(this.getAscii());
        newCharacterClass.setUnicode(this.getUnicode());
        newCharacterClass.setByte(this.getByte());
        newCharacterClass.setEnumValues(this.getEnumValues());
        newCharacterClass.setSetValues(this.getSetValues());
        newCharacterClass.setNotLogged(this.getNotLogged());
        newCharacterClass.setArray(this.getArray());
        return newCharacterClass;
    }
    
    private String maxLengthFromEnumValues(final ArrayList enumValues) {
        int max = enumValues.get(0).toString().length();
        for (int i = 1; i < enumValues.size(); ++i) {
            final int current = enumValues.get(i).toString().length();
            if (max < current) {
                max = current;
            }
        }
        return new Integer(max).toString();
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.nationalType != null) {
            sb.append(this.nationalType + " ");
        }
        if (this.datatypeName != null) {
            sb.append(this.datatypeName + " ");
        }
        if (this.varyingType != null) {
            sb.append(this.varyingType + " ");
        }
        if (this.openBrace != null) {
            sb.append(this.openBrace);
        }
        if (this.size != null) {
            sb.append(this.size);
        }
        if (this.enumValues != null) {
            for (int i = 0; i < this.enumValues.size(); ++i) {
                sb.append(this.enumValues.get(i).toString());
                if (i != this.enumValues.size() - 1) {
                    sb.append(",");
                }
            }
        }
        if (this.closedBrace != null) {
            sb.append(this.closedBrace);
        }
        if (this.notlogged != null) {
            sb.append(this.notlogged);
        }
        if (this.doBinarySearch != null) {
            sb.append(" " + this.doBinarySearch + " ");
        }
        if (this.ascii != null) {
            sb.append(" " + this.ascii + " ");
        }
        if (this.unicode != null) {
            sb.append(" " + this.unicode + " ");
        }
        if (this.SAPbyte != null) {
            sb.append(" " + this.SAPbyte + " ");
        }
        if (this.arrayStr != null) {
            sb.append(this.arrayStr);
        }
        if (this.caseSpecificPhrase != null) {
            sb.append(" " + this.caseSpecificPhrase + " ");
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
