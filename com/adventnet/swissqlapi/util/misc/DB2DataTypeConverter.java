package com.adventnet.swissqlapi.util.misc;

public class DB2DataTypeConverter
{
    public static String convertPLSQLTypeToDB2Type(String datatype) {
        datatype = datatype.toLowerCase();
        if (datatype.startsWith("integer")) {
            datatype = "INTEGER";
        }
        else if (datatype.startsWith("number(38)") || datatype.startsWith("natural") || datatype.startsWith("positive")) {
            datatype = "DECIMAL(31,0)";
        }
        if (datatype.startsWith("numeric")) {
            if (datatype.indexOf(",") != -1) {
                datatype = datatype.replaceFirst("numeric", "DECIMAL");
            }
            else {
                datatype = "DECIMAL(31,0)";
            }
        }
        else if (datatype.startsWith("number") || datatype.startsWith("float")) {
            if (datatype.indexOf(",") != -1) {
                datatype = datatype.replaceFirst("number", "DECIMAL");
                final String lenStr = datatype.substring(datatype.indexOf("(") + 1, datatype.indexOf(","));
                final int len = Integer.parseInt(lenStr);
                if (len > 31) {
                    final String datatypeStr = datatype.substring(0, datatype.indexOf("("));
                    final String tempStr = datatype.substring(datatype.indexOf("(") + 1, datatype.length());
                    String precision = tempStr.substring(tempStr.indexOf(",") + 1, tempStr.indexOf(")"));
                    final int pre = Integer.parseInt(precision);
                    if (pre > 31) {
                        precision = "31";
                    }
                    datatype = datatypeStr + "(" + 31 + "," + precision + ")";
                }
            }
            else if (datatype.equals("float")) {
                datatype = "FLOAT";
            }
            else if (datatype.trim().equalsIgnoreCase("number")) {
                datatype = "DECIMAL(32,0)";
            }
            else {
                final String argumentValue = datatype.substring(datatype.indexOf("(") + 1, datatype.indexOf(")"));
                try {
                    final int numericValueOfArgument = Integer.parseInt(argumentValue);
                    if (numericValueOfArgument < 5) {
                        datatype = "SMALLINT";
                    }
                    else if (numericValueOfArgument < 10) {
                        datatype = "INTEGER";
                    }
                    else if (numericValueOfArgument < 19) {
                        datatype = "BIGINT";
                    }
                    else if (numericValueOfArgument < 32) {
                        datatype = "DECIMAL(" + numericValueOfArgument + ")";
                    }
                    else if (numericValueOfArgument >= 32) {
                        datatype = "DECIMAL(32,0)";
                    }
                }
                catch (final NumberFormatException nfe) {
                    System.out.println("EXCEPTION IN VARIABLEDECLARATION IN DATATYPE : " + datatype);
                    nfe.printStackTrace();
                    datatype = "FLOAT";
                }
            }
        }
        else if (datatype.startsWith("raw")) {
            final String argumentValue = datatype.substring(datatype.indexOf("(") + 1, datatype.indexOf(")"));
            try {
                final int numericValueOfArgument = Integer.parseInt(argumentValue);
                if (numericValueOfArgument <= 254) {
                    datatype = datatype.replaceFirst("raw", "CHAR");
                }
                else if (numericValueOfArgument > 254 && numericValueOfArgument <= 32672) {
                    datatype = datatype.replaceFirst("raw", "VARCHAR");
                }
                else if (numericValueOfArgument > 32672) {
                    datatype = datatype.replaceFirst("raw", "BLOB");
                }
            }
            catch (final NumberFormatException nfe) {
                System.out.println("EXCEPTION IN VARIABLEDECLARATION IN DATATYPE : " + datatype);
                nfe.printStackTrace();
                datatype = "BLOB";
            }
        }
        else if (datatype.startsWith("decimal")) {
            if (datatype.indexOf(",") != -1) {
                datatype = datatype.replaceFirst("decimal", "DECIMAL");
            }
            else {
                datatype = "DECIMAL(31,0)";
            }
        }
        else if (datatype.startsWith("varchar")) {
            if (datatype.indexOf("(") != -1) {
                final String stringBeforeOpenBrace = datatype.substring(0, datatype.indexOf("("));
                final String argumentValue2 = datatype.substring(datatype.indexOf("(") + 1, datatype.indexOf(")"));
                try {
                    final int numericValueOfArgument2 = Integer.parseInt(argumentValue2);
                    if (numericValueOfArgument2 < 32672) {
                        datatype = datatype.replaceFirst(stringBeforeOpenBrace, "VARCHAR");
                    }
                    else {
                        datatype = "VARCHAR(32672)";
                    }
                }
                catch (final Exception exc) {
                    datatype = "VARCHAR(3999)";
                }
            }
            else if (datatype.equalsIgnoreCase("VARCHAR2")) {
                datatype = "VARCHAR(32672)";
            }
        }
        else if (datatype.startsWith("string")) {
            final String stringBeforeOpenBrace = datatype.substring(0, datatype.indexOf("("));
            final String argumentValue2 = datatype.substring(datatype.indexOf("(") + 1, datatype.indexOf(")"));
            try {
                final int numericValueOfArgument2 = Integer.parseInt(argumentValue2);
                if (numericValueOfArgument2 < 4000) {
                    datatype = datatype.replaceFirst(stringBeforeOpenBrace, "VARCHAR");
                }
                else {
                    datatype = "VARCHAR(4000)";
                }
            }
            catch (final Exception exc) {
                datatype = "VARCHAR(3999)";
            }
        }
        else if (datatype.startsWith("char")) {
            final String stringBeforeOpenBrace = datatype.substring(0, datatype.indexOf("("));
            final String argumentValue2 = datatype.substring(datatype.indexOf("(") + 1, datatype.indexOf(")"));
            try {
                final int numericValueOfArgument2 = Integer.parseInt(argumentValue2);
                if (numericValueOfArgument2 <= 254) {
                    datatype = datatype.replaceFirst(stringBeforeOpenBrace, "CHAR");
                }
                else {
                    datatype = "VARCHAR(4000)";
                }
            }
            catch (final Exception exc) {
                datatype = "VARCHAR(3999)";
            }
        }
        else if (datatype.startsWith("national") || datatype.startsWith("nvarchar") || datatype.startsWith("nchar")) {
            final String stringBeforeOpenBrace = datatype.substring(0, datatype.indexOf("("));
            datatype = datatype.replaceFirst(stringBeforeOpenBrace, "VARCHAR");
        }
        else if (datatype.startsWith("bfile") || datatype.startsWith("longraw") || datatype.startsWith("blob")) {
            final String stringBeforeOpenBrace = datatype.substring(0, datatype.indexOf("("));
            datatype = datatype.replaceFirst(stringBeforeOpenBrace, "BLOB");
        }
        else if (datatype.equalsIgnoreCase("DATE")) {
            datatype = "TIMESTAMP";
        }
        return datatype;
    }
}
