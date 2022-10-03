package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.config.SwisSQLOptions;

public class BinClass implements Datatype
{
    private String datatypeName;
    private String openBrace;
    private String closedBrace;
    private String size;
    private String varyingType;
    private String notlogged;
    private String arrayStr;
    
    @Override
    public void setDatatypeName(final String datatypeName) {
        this.datatypeName = datatypeName;
    }
    
    public void setVarying(final String varyingType) {
        this.varyingType = varyingType;
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
    
    @Override
    public String getDatatypeName() {
        return this.datatypeName;
    }
    
    @Override
    public String getSize() {
        return this.size;
    }
    
    public String getVarying() {
        return this.varyingType;
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
    
    @Override
    public void toDB2String() {
        if (this.getVarying() != null) {
            this.setVarying(null);
        }
        if (this.getDatatypeName() != null) {
            final String changeDB2Datatype = this.getDatatypeName();
            if (changeDB2Datatype.equalsIgnoreCase("BLOB")) {
                if (this.size != null) {
                    if (!this.size.toUpperCase().endsWith("G")) {
                        if (this.size.toUpperCase().endsWith("M")) {
                            this.size = this.size.substring(0, this.size.length() - 1);
                            final int precisionValue = Integer.parseInt(this.size);
                            if (precisionValue >= 1024 && precisionValue < 2000) {
                                this.setSize("1G");
                                this.setNotLogged(" NOT LOGGED");
                            }
                            else if (precisionValue >= 2000) {
                                this.setSize("2G");
                                this.setNotLogged(" NOT LOGGED");
                            }
                            else {
                                this.setSize(this.size + "M");
                            }
                        }
                    }
                    else {
                        final String newSize = this.size.substring(0, this.size.length() - 1);
                        final int value = Integer.parseInt(newSize);
                        if (value == 1) {
                            this.setSize("1G");
                        }
                        else if (value == 2) {
                            this.setSize("2G");
                        }
                        this.setNotLogged(" NOT LOGGED");
                    }
                }
                else {
                    this.setDatatypeName("BLOB");
                    this.setOpenBrace("(");
                    this.setSize("2G");
                    this.setClosedBrace(")");
                    this.setNotLogged(" NOT LOGGED");
                }
                if (this.notlogged != null) {
                    this.setNotLogged(" NOT LOGGED");
                }
            }
            else if (changeDB2Datatype.equalsIgnoreCase("LONGTEXT")) {
                this.setDatatypeName("CLOB");
                this.setOpenBrace("(");
                this.setSize("2G");
                this.setClosedBrace(")");
                this.setNotLogged(" NOT LOGGED");
            }
            else if (SwisSQLOptions.fromAccess && changeDB2Datatype.equalsIgnoreCase("BIT")) {
                this.setDatatypeName("NUM");
                this.setOpenBrace("(");
                this.setSize("1");
                this.setClosedBrace(")");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("LONGBLOB") || changeDB2Datatype.equalsIgnoreCase("BFILE") || changeDB2Datatype.equalsIgnoreCase("LONG RAW") || changeDB2Datatype.equalsIgnoreCase("IMAGE") || changeDB2Datatype.equalsIgnoreCase("BYTEA")) {
                this.setDatatypeName("BLOB");
                this.setOpenBrace("(");
                this.setSize("2G");
                this.setClosedBrace(")");
                this.setNotLogged(" NOT LOGGED");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("RAW")) {
                this.setDatatypeName("BLOB");
                this.setOpenBrace("(");
                this.setSize("2000");
                this.setClosedBrace(")");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("MEDIUMBLOB")) {
                this.setDatatypeName("BLOB");
                this.setOpenBrace("(");
                this.setSize("16777215");
                this.setClosedBrace(")");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("MEDIUMTEXT")) {
                this.setDatatypeName("CLOB");
                this.setOpenBrace("(");
                this.setSize("16777215");
                this.setClosedBrace(")");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("TINYBLOB") || changeDB2Datatype.equalsIgnoreCase("TINYTEXT")) {
                this.setDatatypeName("CHAR");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("BINARY")) {
                this.setDatatypeName("BLOB");
                this.setOpenBrace("(");
                this.setSize("8000");
                this.setClosedBrace(")");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("VARBINARY")) {
                this.setDatatypeName("BLOB");
                this.setOpenBrace("(");
                if (this.getSize() != null && this.getSize().equalsIgnoreCase("MAX")) {
                    this.setSize("2147483647");
                }
                else {
                    this.setSize("8000");
                }
                this.setClosedBrace(")");
            }
            else if (changeDB2Datatype.equalsIgnoreCase("BIT")) {
                this.setDatatypeName("NUM(1)");
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toOracleString() {
        if (this.notlogged != null) {
            this.setNotLogged(null);
        }
        if (this.getVarying() != null) {
            this.setVarying(null);
        }
        if (this.getDatatypeName() != null) {
            final String changeOracleDatatype = this.getDatatypeName();
            if (changeOracleDatatype.equalsIgnoreCase("BLOB")) {
                if (this.getSize() != null) {
                    this.setOpenBrace(null);
                    this.setSize(null);
                    this.setClosedBrace(null);
                }
            }
            else if (SwisSQLOptions.fromAccess && changeOracleDatatype.equalsIgnoreCase("BIT")) {
                this.setDatatypeName("NUMBER");
                this.setOpenBrace("(");
                this.setSize("1");
                this.setClosedBrace(")");
            }
            else if (changeOracleDatatype.equalsIgnoreCase("LONGBLOB") || changeOracleDatatype.equalsIgnoreCase("LONGTEXT")) {
                this.setDatatypeName("BLOB");
            }
            else if (changeOracleDatatype.equalsIgnoreCase("MEDIUMBLOB") || changeOracleDatatype.equalsIgnoreCase("MEDIUMTEXT")) {
                this.setDatatypeName("LONG");
            }
            else if (changeOracleDatatype.equalsIgnoreCase("TINYBLOB") || changeOracleDatatype.equalsIgnoreCase("TINYTEXT")) {
                this.setDatatypeName("CHAR");
            }
            else if (changeOracleDatatype.equalsIgnoreCase("BINARY") || changeOracleDatatype.equalsIgnoreCase("VARBINARY")) {
                if (this.getOpenBrace() == null) {
                    this.setDatatypeName("RAW");
                    this.setOpenBrace("(");
                    this.setSize("1");
                    this.setClosedBrace(")");
                }
                if (this.getSize() != null) {
                    if (this.getSize().equalsIgnoreCase("MAX")) {
                        this.setDatatypeName("BLOB");
                        this.setOpenBrace(null);
                        this.setSize(null);
                        this.setClosedBrace(null);
                    }
                    else {
                        final int siz = Integer.parseInt(this.getSize());
                        if (siz <= 2000) {
                            this.setDatatypeName("RAW");
                        }
                        else {
                            this.setDatatypeName("BLOB");
                            this.setOpenBrace(null);
                            this.setSize(null);
                            this.setClosedBrace(null);
                        }
                    }
                }
            }
            else if (changeOracleDatatype.equalsIgnoreCase("IMAGE") || changeOracleDatatype.equalsIgnoreCase("BYTEA")) {
                this.setDatatypeName("BLOB");
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (changeOracleDatatype.equalsIgnoreCase("BIT")) {
                this.setDatatypeName("NUMBER");
                this.setSize("1");
                this.setOpenBrace("(");
                this.setClosedBrace(")");
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toMSSQLServerString() {
        if (this.notlogged != null) {
            this.setNotLogged(null);
        }
        if (this.getVarying() != null) {
            this.setVarying(null);
        }
        if (this.getDatatypeName() != null) {
            final String changeSQLServerDatatype = this.getDatatypeName();
            if (changeSQLServerDatatype.equalsIgnoreCase("BLOB") || changeSQLServerDatatype.equalsIgnoreCase("MEDIUMBLOB") || changeSQLServerDatatype.equalsIgnoreCase("LONGBLOB") || changeSQLServerDatatype.equalsIgnoreCase("TINYBLOB") || changeSQLServerDatatype.equalsIgnoreCase("LONG RAW") || changeSQLServerDatatype.equalsIgnoreCase("BYTEA")) {
                if (this.getSize() != null) {
                    this.setOpenBrace(null);
                    this.setSize(null);
                    this.setClosedBrace(null);
                }
                this.setDatatypeName("IMAGE");
            }
            else if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("BIT")) {
                this.setDatatypeName(changeSQLServerDatatype);
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("LONGTEXT") || changeSQLServerDatatype.equalsIgnoreCase("MEDIUMTEXT") || changeSQLServerDatatype.equalsIgnoreCase("BFILE")) {
                if (this.getSize() != null) {
                    this.setOpenBrace(null);
                    this.setSize(null);
                    this.setClosedBrace(null);
                }
                this.setDatatypeName("TEXT");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("RAW")) {
                this.setDatatypeName("VARBINARY");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("TINYTEXT")) {
                this.setDatatypeName("CHAR");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("BOOLEAN")) {
                this.setDatatypeName("BOOLEAN");
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toSybaseString() {
        if (this.notlogged != null) {
            this.setNotLogged(null);
        }
        if (this.getVarying() != null) {
            this.setVarying(null);
        }
        if (this.getDatatypeName() != null) {
            final String changeSQLServerDatatype = this.getDatatypeName();
            if (changeSQLServerDatatype.equalsIgnoreCase("BLOB") || changeSQLServerDatatype.equalsIgnoreCase("LONGBLOB") || changeSQLServerDatatype.equalsIgnoreCase("MEDIUMBLOB") || changeSQLServerDatatype.equalsIgnoreCase("ORACLEBLOB") || changeSQLServerDatatype.equalsIgnoreCase("LONG RAW") || changeSQLServerDatatype.equalsIgnoreCase("TINYBLOB") || changeSQLServerDatatype.equalsIgnoreCase("BYTEA")) {
                if (this.getSize() != null) {
                    this.setOpenBrace(null);
                    this.setSize(null);
                    this.setClosedBrace(null);
                }
                this.setDatatypeName("IMAGE");
            }
            else if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("BIT")) {
                this.setDatatypeName(changeSQLServerDatatype);
                this.setOpenBrace(null);
                this.setSize(null);
                this.setClosedBrace(null);
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("LONGTEXT") || changeSQLServerDatatype.equalsIgnoreCase("MEDIUMTEXT") || changeSQLServerDatatype.equalsIgnoreCase("BFILE")) {
                if (this.getSize() != null) {
                    this.setOpenBrace(null);
                    this.setSize(null);
                    this.setClosedBrace(null);
                }
                this.setDatatypeName("TEXT");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("RAW")) {
                this.setDatatypeName("VARBINARY");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("TINYTEXT")) {
                this.setDatatypeName("CHAR");
                this.setOpenBrace("(");
                this.setSize("255");
                this.setClosedBrace(")");
            }
            else if (changeSQLServerDatatype.equalsIgnoreCase("BOOLEAN")) {
                this.setDatatypeName("BIT");
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toPostgreSQLString() {
        if (this.notlogged != null) {
            this.setNotLogged(null);
        }
        if (this.getVarying() != null) {
            this.setVarying(null);
        }
        if (this.getSize() != null) {
            this.setOpenBrace(null);
            this.setSize(null);
            this.setClosedBrace(null);
        }
        if (this.getDatatypeName() != null) {
            final String changePostgreSQLDatatype = this.getDatatypeName();
            if (changePostgreSQLDatatype.equalsIgnoreCase("BLOB") || changePostgreSQLDatatype.equalsIgnoreCase("MEDIUMBLOB") || changePostgreSQLDatatype.equalsIgnoreCase("LONGBLOB") || changePostgreSQLDatatype.equalsIgnoreCase("TINYBLOB") || changePostgreSQLDatatype.equalsIgnoreCase("LONG RAW") || changePostgreSQLDatatype.equalsIgnoreCase("RAW") || changePostgreSQLDatatype.equalsIgnoreCase("BINARY") || changePostgreSQLDatatype.equalsIgnoreCase("VARBINARY") || changePostgreSQLDatatype.equalsIgnoreCase("IMAGE") || changePostgreSQLDatatype.equalsIgnoreCase("BFILE")) {
                if (this.getSize() != null) {
                    this.setOpenBrace(null);
                    this.setSize(null);
                    this.setClosedBrace(null);
                }
                this.setDatatypeName("BYTEA");
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("LONGTEXT") || changePostgreSQLDatatype.equalsIgnoreCase("MEDIUMTEXT") || changePostgreSQLDatatype.equalsIgnoreCase("CLOB") || changePostgreSQLDatatype.equalsIgnoreCase("TINYTEXT")) {
                if (this.getSize() != null) {
                    this.setOpenBrace(null);
                    this.setSize(null);
                    this.setClosedBrace(null);
                }
                this.setDatatypeName("TEXT");
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("BIT")) {
                this.setDatatypeName("smallint");
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("BOOLEAN")) {
                this.setDatatypeName("BOOL");
            }
            else if (changePostgreSQLDatatype.equalsIgnoreCase("BYTEA")) {
                this.setDatatypeName("BYTEA");
            }
            else {
                this.setDatatypeName("TEXT");
            }
        }
    }
    
    @Override
    public void toMySQLString() {
        if (this.notlogged != null) {
            this.setNotLogged(null);
        }
        if (this.getVarying() != null) {
            this.setVarying(null);
        }
        if (this.getSize() != null) {
            this.setOpenBrace(null);
            this.setSize(null);
            this.setClosedBrace(null);
        }
        if (this.getDatatypeName() != null) {
            final String changeMySQLDatatype = this.getDatatypeName();
            if (SwisSQLOptions.fromAccess && changeMySQLDatatype.equalsIgnoreCase("BIT")) {
                this.setDatatypeName("INTEGER");
                this.setOpenBrace("(");
                this.setSize("1");
                this.setClosedBrace(")");
            }
            else if (changeMySQLDatatype.equalsIgnoreCase("LONG RAW") || changeMySQLDatatype.equalsIgnoreCase("RAW") || changeMySQLDatatype.equalsIgnoreCase("BINARY") || changeMySQLDatatype.equalsIgnoreCase("VARBINARY") || changeMySQLDatatype.equalsIgnoreCase("IMAGE") || changeMySQLDatatype.equalsIgnoreCase("BYTEA")) {
                this.setDatatypeName("LONGBLOB");
            }
            else if (changeMySQLDatatype.equalsIgnoreCase("BIT")) {
                this.setDatatypeName("TINYINT");
            }
            else if (changeMySQLDatatype.equalsIgnoreCase("BFILE")) {
                this.setDatatypeName("LONGBLOB");
            }
            else if (changeMySQLDatatype.equalsIgnoreCase("BOOLEAN")) {
                this.setDatatypeName("TINYINT");
                this.setOpenBrace("(");
                this.setSize("1");
                this.setClosedBrace(")");
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toANSIString() {
        if (this.notlogged != null) {
            this.setNotLogged(null);
        }
        if (this.getVarying() != null) {
            this.setVarying(null);
        }
        if (this.getDatatypeName() != null) {
            final String changeANSIDatatype = this.getDatatypeName();
            if (changeANSIDatatype.equalsIgnoreCase("BLOB")) {
                if (this.getSize() != null) {
                    this.setOpenBrace(null);
                    this.setSize(null);
                    this.setClosedBrace(null);
                }
            }
            else if (changeANSIDatatype.equalsIgnoreCase("LONGBLOB") || changeANSIDatatype.equalsIgnoreCase("LONGTEXT") || changeANSIDatatype.equalsIgnoreCase("IMAGE") || changeANSIDatatype.equalsIgnoreCase("BYTEA")) {
                this.setDatatypeName("BLOB");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("MEDIUMBLOB") || changeANSIDatatype.equalsIgnoreCase("MEDIUMTEXT")) {
                this.setDatatypeName("LONG");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("TINYBLOB") || changeANSIDatatype.equalsIgnoreCase("TINYTEXT")) {
                this.setDatatypeName("CHAR");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("LONG RAW") || changeANSIDatatype.equalsIgnoreCase("RAW") || changeANSIDatatype.equalsIgnoreCase("BINARY") || changeANSIDatatype.equalsIgnoreCase("VARBINARY")) {
                this.setDatatypeName("BLOB");
            }
            else if (changeANSIDatatype.equalsIgnoreCase("BFILE")) {
                this.setDatatypeName("BLOB");
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toInformixString() {
        if (this.notlogged != null) {
            this.setNotLogged(null);
        }
        if (this.getVarying() != null) {
            this.setVarying(null);
        }
        if (this.getDatatypeName() != null) {
            final String changeIfxDatatype = this.getDatatypeName();
            if (changeIfxDatatype.equalsIgnoreCase("BIT")) {
                this.setDatatypeName("SMALLINT");
            }
            else if (changeIfxDatatype.equalsIgnoreCase("BINARY") || changeIfxDatatype.equalsIgnoreCase("VARBINARY")) {
                this.setDatatypeName("BYTE");
                this.setSize(null);
                this.setOpenBrace(null);
                this.setClosedBrace(null);
            }
            else if (changeIfxDatatype.equalsIgnoreCase("LONGBLOB") || changeIfxDatatype.equalsIgnoreCase("LONGTEXT") || changeIfxDatatype.equalsIgnoreCase("BFILE") || changeIfxDatatype.equalsIgnoreCase("BLOB") || changeIfxDatatype.equalsIgnoreCase("LONG RAW") || changeIfxDatatype.equalsIgnoreCase("IMAGE") || changeIfxDatatype.equalsIgnoreCase("BYTEA")) {
                this.setDatatypeName("BLOB");
                this.setClosedBrace(null);
                this.setOpenBrace(null);
                this.setSize(null);
            }
            else if (changeIfxDatatype.equalsIgnoreCase("TINYBLOB") || changeIfxDatatype.equalsIgnoreCase("TINYTEXT")) {
                this.setDatatypeName("CHAR");
            }
            else if (changeIfxDatatype.equalsIgnoreCase("MEDIUMBLOB") || changeIfxDatatype.equalsIgnoreCase("MEDIUMTEXT") || changeIfxDatatype.equalsIgnoreCase("RAW")) {
                this.setDatatypeName("TEXT");
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toTimesTenString() {
        if (this.notlogged != null) {
            this.setNotLogged(null);
        }
        if (this.getVarying() != null) {
            this.setVarying(null);
        }
        if (this.getDatatypeName() != null) {
            final String sourceType = this.getDatatypeName();
            if (sourceType.equalsIgnoreCase("BLOB") || sourceType.equalsIgnoreCase("LONGBLOB") || sourceType.equalsIgnoreCase("MEDIUMBLOB") || sourceType.equalsIgnoreCase("ORACLEBLOB") || sourceType.equalsIgnoreCase("LONG RAW") || sourceType.equalsIgnoreCase("BFILE") || sourceType.equalsIgnoreCase("BYTEA")) {
                this.setDatatypeName("VARBINARY");
                this.setOpenBrace("(");
                this.setSize("4194304");
                this.setClosedBrace(")");
            }
            else if (sourceType.equalsIgnoreCase("LONGTEXT") || sourceType.equalsIgnoreCase("MEDIUMTEXT")) {
                this.setDatatypeName("VARCHAR");
                this.setOpenBrace("(");
                this.setSize("4194304");
                this.setClosedBrace(")");
            }
            else if (sourceType.equalsIgnoreCase("RAW") || sourceType.equalsIgnoreCase("TINYBLOB")) {
                this.setDatatypeName("BINARY");
                if (this.size == null) {
                    this.setOpenBrace("(");
                    this.setSize("1");
                    this.setClosedBrace(")");
                }
            }
            else if (sourceType.equalsIgnoreCase("TINYTEXT")) {
                this.setDatatypeName("CHAR");
                this.setOpenBrace("(");
                this.setSize("255");
                this.setClosedBrace(")");
            }
            else if (sourceType.equalsIgnoreCase("BOOLEAN") || sourceType.equalsIgnoreCase("BIT")) {
                this.setDatatypeName("TINYINT");
            }
            else if (sourceType.equalsIgnoreCase("BINARY")) {
                if (this.size == null) {
                    this.setOpenBrace("(");
                    this.setSize("1");
                    this.setClosedBrace(")");
                }
                else if (Integer.parseInt(this.size) > 8300) {
                    this.setOpenBrace("(");
                    this.setSize("8300");
                    this.setClosedBrace(")");
                }
            }
            else if (sourceType.equalsIgnoreCase("VARBINARY")) {
                if (this.size == null) {
                    this.setOpenBrace("(");
                    this.setSize("1");
                    this.setClosedBrace(")");
                }
                else if (Integer.parseInt(this.size) > 4194304) {
                    this.setOpenBrace("(");
                    this.setSize("4194304");
                    this.setClosedBrace(")");
                }
            }
            else if (sourceType.equalsIgnoreCase("IMAGE")) {
                this.setDatatypeName("VARBINARY");
                this.setOpenBrace("(");
                this.setSize("4194304");
                this.setClosedBrace(")");
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toNetezzaString() {
        final String varcharMaxSize = "32760";
        if (this.notlogged != null) {
            this.setNotLogged(null);
        }
        if (this.getVarying() != null) {
            this.setVarying(null);
        }
        if (this.getDatatypeName() != null) {
            final String changeNetezzaDatatype = this.getDatatypeName();
            if (changeNetezzaDatatype.equalsIgnoreCase("BLOB")) {
                this.setDatatypeName("VARCHAR");
                this.setOpenBrace("(");
                this.setSize(varcharMaxSize);
                this.setClosedBrace(")");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("LONGBLOB") || changeNetezzaDatatype.equalsIgnoreCase("LONGTEXT") || changeNetezzaDatatype.equalsIgnoreCase("IMAGE") || changeNetezzaDatatype.equalsIgnoreCase("BYTEA")) {
                this.setDatatypeName("VARCHAR");
                this.setOpenBrace("(");
                this.setSize(varcharMaxSize);
                this.setClosedBrace(")");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("MEDIUMBLOB") || changeNetezzaDatatype.equalsIgnoreCase("MEDIUMTEXT")) {
                this.setDatatypeName("VARCHAR");
                this.setOpenBrace("(");
                this.setSize(varcharMaxSize);
                this.setClosedBrace(")");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("TINYBLOB") || changeNetezzaDatatype.equalsIgnoreCase("TINYTEXT")) {
                this.setDatatypeName("CHAR");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("LONG RAW") || changeNetezzaDatatype.equalsIgnoreCase("RAW") || changeNetezzaDatatype.equalsIgnoreCase("BINARY") || changeNetezzaDatatype.equalsIgnoreCase("VARBINARY")) {
                this.setDatatypeName("VARCHAR");
                this.setOpenBrace("(");
                this.setSize(varcharMaxSize);
                this.setClosedBrace(")");
            }
            else if (changeNetezzaDatatype.equalsIgnoreCase("BFILE")) {
                this.setDatatypeName("VARCHAR");
                this.setOpenBrace("(");
                this.setSize(varcharMaxSize);
                this.setClosedBrace(")");
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    @Override
    public void toTeradataString() {
        if (this.notlogged != null) {
            this.setNotLogged(null);
        }
        if (this.getVarying() != null) {
            this.setVarying(null);
        }
        if (this.getDatatypeName() != null) {
            final String changeTeradataDatatype = this.getDatatypeName();
            if (changeTeradataDatatype.equalsIgnoreCase("BLOB")) {
                if (this.getSize() != null) {
                    this.setOpenBrace(null);
                    this.setSize(null);
                    this.setClosedBrace(null);
                }
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("LONGBLOB") || changeTeradataDatatype.equalsIgnoreCase("LONGTEXT") || changeTeradataDatatype.equalsIgnoreCase("IMAGE") || changeTeradataDatatype.equalsIgnoreCase("BYTEA")) {
                this.setDatatypeName("BLOB");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("MEDIUMBLOB") || changeTeradataDatatype.equalsIgnoreCase("MEDIUMTEXT")) {
                this.setDatatypeName("LONG");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("TINYBLOB") || changeTeradataDatatype.equalsIgnoreCase("TINYTEXT")) {
                this.setDatatypeName("CHAR");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("LONG RAW") || changeTeradataDatatype.equalsIgnoreCase("RAW") || changeTeradataDatatype.equalsIgnoreCase("BINARY") || changeTeradataDatatype.equalsIgnoreCase("VARBINARY")) {
                this.setDatatypeName("BLOB");
            }
            else if (changeTeradataDatatype.equalsIgnoreCase("BFILE")) {
                this.setDatatypeName("BLOB");
            }
            if (this.getArray() != null) {
                this.setArray(null);
            }
        }
    }
    
    public Datatype copyObjectValues() {
        final BinClass newBinClass = new BinClass();
        newBinClass.setClosedBrace(this.closedBrace);
        newBinClass.setDatatypeName(this.getDatatypeName());
        newBinClass.setOpenBrace(this.openBrace);
        newBinClass.setSize(this.getSize());
        newBinClass.setVarying(this.getVarying());
        newBinClass.setNotLogged(this.getNotLogged());
        newBinClass.setArray(this.getArray());
        return newBinClass;
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
        if (this.size != null) {
            sb.append(this.size);
        }
        if (this.closedBrace != null) {
            sb.append(this.closedBrace);
        }
        if (this.notlogged != null) {
            sb.append(this.notlogged);
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
