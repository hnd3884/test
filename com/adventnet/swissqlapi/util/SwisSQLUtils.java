package com.adventnet.swissqlapi.util;

import java.util.Collection;
import java.util.Arrays;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import java.util.HashMap;
import java.util.List;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import java.util.StringTokenizer;
import com.adventnet.swissqlapi.sql.statement.create.BinClass;
import com.adventnet.swissqlapi.sql.statement.create.NumericClass;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.sql.statement.create.CharacterClass;
import com.adventnet.swissqlapi.sql.statement.create.Datatype;
import java.util.Iterator;
import java.util.Set;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import com.adventnet.swissqlapi.sql.statement.insert.InsertQueryStatement;
import java.util.Map;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.sql.statement.create.CreateQueryStatement;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.ArrayList;
import java.util.Hashtable;

public class SwisSQLUtils
{
    public static Hashtable objectNameMapping;
    private static String[] oracleSystemFunctionsArray;
    private static String[] mysqlSystemFunctionsArray;
    private static String[] sqlServerSystemFunctionsArray;
    private static String[] sybaseSystemFunctionsArray;
    private static String[] db2SystemFunctionsArray;
    private static String[] postgresqlSystemFunctionsArray;
    private static String[] teradataSystemFunctionsArray;
    private static String[] oracleKeywordsArray;
    private static String[] sqlServerKeywordsArray;
    private static String[] timestenReservedWordsArray;
    private static String[] netezzaReservedWordsArray;
    private static String[] postgresqlReservedWordsArray;
    private static String[] teradataReservedWordsArray;
    private static String[] oracleDateFormat;
    private static String[] oracleTimeZonesArray;
    private static final ArrayList oracleTimeZones;
    private static ArrayList functionsReturningDate;
    private static ArrayList functionsReturningTimestamp;
    public static ArrayList swissqlMessageList;
    
    public static String[] getSystemFunctions(final int dialecttype) {
        if (dialecttype == 2) {
            return SwisSQLUtils.sqlServerSystemFunctionsArray;
        }
        if (dialecttype == 7) {
            return SwisSQLUtils.sybaseSystemFunctionsArray;
        }
        if (dialecttype == 1) {
            return SwisSQLUtils.oracleSystemFunctionsArray;
        }
        if (dialecttype == 5) {
            return SwisSQLUtils.mysqlSystemFunctionsArray;
        }
        if (dialecttype == 3) {
            return SwisSQLUtils.db2SystemFunctionsArray;
        }
        if (dialecttype == 4) {
            return SwisSQLUtils.postgresqlSystemFunctionsArray;
        }
        if (dialecttype == 12) {
            return SwisSQLUtils.teradataSystemFunctionsArray;
        }
        return null;
    }
    
    public static String[] getKeywords(final int dialecttype) {
        if (dialecttype == 1) {
            return SwisSQLUtils.oracleKeywordsArray;
        }
        if (dialecttype == 2) {
            return SwisSQLUtils.sqlServerKeywordsArray;
        }
        if (dialecttype == 10) {
            return SwisSQLUtils.timestenReservedWordsArray;
        }
        if (dialecttype == 11) {
            return SwisSQLUtils.netezzaReservedWordsArray;
        }
        if (dialecttype == 12) {
            return SwisSQLUtils.teradataReservedWordsArray;
        }
        if (dialecttype == 4) {
            return SwisSQLUtils.postgresqlReservedWordsArray;
        }
        return null;
    }
    
    public static String[] getKeywords(final String database) {
        if (database.equalsIgnoreCase("teradata")) {
            return SwisSQLUtils.teradataReservedWordsArray;
        }
        return null;
    }
    
    public static ArrayList getOracleTimeZones() {
        return SwisSQLUtils.oracleTimeZones;
    }
    
    public static CreateQueryStatement constructCQS(String tableName, final SelectQueryStatement from_sqs, final SwisSQLStatement to_sqs) {
        final CreateQueryStatement cqs = new CreateQueryStatement();
        cqs.setCreate("CREATE");
        cqs.setTableOrView("TABLE");
        cqs.setClosedBraces(")");
        cqs.setOpenBraces("(");
        if (tableName != null) {
            final TableObject to = new TableObject();
            to.setTableName(tableName);
            cqs.setTableObject(to);
        }
        final SelectStatement fromSS = from_sqs.getSelectStatement();
        final Vector sItems = fromSS.getSelectItemList();
        final Vector ccVector = new Vector();
        final FromClause fc = from_sqs.getFromClause();
        if (fc != null) {
            final Vector fromItems = fc.getFromItemList();
            for (int i = 0; i < fromItems.size(); ++i) {
                final Object obj = fromItems.get(i);
                if (obj instanceof FromTable) {
                    final Object tableObj = ((FromTable)obj).getTableName();
                    if (tableObj instanceof String) {
                        tableName = tableObj.toString();
                        if (tableName.indexOf(".") != -1) {
                            tableName = tableName.substring(tableName.lastIndexOf(".") + 1, tableName.length());
                        }
                        final Hashtable colDatatypeTable = (Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.dataTypesFromMetaDataHT, tableName);
                        if (colDatatypeTable == null) {
                            if (to_sqs instanceof SelectQueryStatement) {
                                ((SelectQueryStatement)to_sqs).setGeneralComments("/* SwisSQL Message : Metadata of the source database required for accurate conversion */");
                            }
                            else if (to_sqs instanceof InsertQueryStatement) {
                                ((InsertQueryStatement)to_sqs).setGeneralComments("/* SwisSQL Message : Metadata of the source database required for accurate conversion */");
                            }
                        }
                        else if (sItems.size() == 1 && sItems.get(0).toString().equals("*") && tableName != null) {
                            final Set keys = colDatatypeTable.keySet();
                            for (final Object col : keys) {
                                final CreateColumn cc = new CreateColumn();
                                cc.setColumnName(col.toString());
                                final Datatype datatype = constructDatatype((String)CastingUtil.getValueIgnoreCase(colDatatypeTable, col.toString()));
                                if (datatype == null) {
                                    if (to_sqs instanceof SelectQueryStatement) {
                                        ((SelectQueryStatement)to_sqs).setGeneralComments("/* SwisSQL Message : Metadata of the source database required for accurate conversion */");
                                    }
                                    else if (to_sqs instanceof InsertQueryStatement) {
                                        ((InsertQueryStatement)to_sqs).setGeneralComments("/* SwisSQL Message : Metadata of the source database required for accurate conversion */");
                                    }
                                }
                                cc.setDatatype(datatype);
                                ccVector.add(cc);
                            }
                        }
                        else {
                            for (int j = 0; j < sItems.size(); ++j) {
                                final Object scObj = sItems.get(j);
                                final int colCount = 0;
                                if (scObj instanceof SelectColumn) {
                                    final SelectColumn sc = (SelectColumn)scObj;
                                    final Vector colExpr = sc.getColumnExpression();
                                    if (colExpr.size() == 1 && colExpr.get(0) instanceof TableColumn) {
                                        final CreateColumn cc2 = new CreateColumn();
                                        final String colName = colExpr.get(0).getColumnName();
                                        if (sc.getAliasName() == null) {
                                            cc2.setColumnName(colName);
                                        }
                                        else {
                                            cc2.setColumnName(sc.getAliasName());
                                        }
                                        final Datatype datatype2 = constructDatatype((String)CastingUtil.getValueIgnoreCase(colDatatypeTable, colName));
                                        if (datatype2 == null) {
                                            if (to_sqs instanceof SelectQueryStatement) {
                                                ((SelectQueryStatement)to_sqs).setGeneralComments("/* SwisSQL Message : Metadata of the source database required for accurate conversion */");
                                            }
                                            else if (to_sqs instanceof InsertQueryStatement) {
                                                ((InsertQueryStatement)to_sqs).setGeneralComments("/* SwisSQL Message : Metadata of the source database required for accurate conversion */");
                                            }
                                        }
                                        cc2.setDatatype(datatype2);
                                        ccVector.add(cc2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        cqs.setColumnNames(ccVector);
        return cqs;
    }
    
    public static Datatype constructDatatype(String datatypeName) {
        String size = null;
        if (datatypeName != null) {
            final int index1 = datatypeName.indexOf("(");
            String tempType = datatypeName.toLowerCase();
            if (index1 != -1) {
                tempType = datatypeName.toLowerCase().substring(0, index1);
            }
            if (CreateColumn.getUserDefinedDatatypes().containsKey(tempType)) {
                datatypeName = CreateColumn.getUserDefinedDatatypes().get(tempType);
            }
            final int index2 = datatypeName.indexOf("(");
            if (index2 != -1) {
                size = datatypeName.substring(index2 + 1, datatypeName.length() - 1);
                datatypeName = datatypeName.substring(0, index2);
            }
            if (datatypeName.toLowerCase().indexOf("char") != -1 || datatypeName.toLowerCase().indexOf("clob") != -1 || datatypeName.equalsIgnoreCase("text")) {
                final CharacterClass cc = new CharacterClass();
                cc.setDatatypeName(datatypeName.toUpperCase());
                if (size != null) {
                    cc.setOpenBrace("(");
                    cc.setSize(size);
                    cc.setClosedBrace(")");
                }
                return cc;
            }
            if (datatypeName.toLowerCase().indexOf("time") != -1 || datatypeName.toLowerCase().indexOf("date") != -1) {
                final DateClass dc = new DateClass();
                dc.setDatatypeName(datatypeName.toUpperCase());
                return dc;
            }
            if (datatypeName.toLowerCase().indexOf("int") != -1 || datatypeName.toLowerCase().indexOf("number") != -1 || datatypeName.toLowerCase().indexOf("double") != -1 || datatypeName.equalsIgnoreCase("real") || datatypeName.equalsIgnoreCase("float") || datatypeName.toLowerCase().indexOf("dec") != -1 || datatypeName.equalsIgnoreCase("numeric") || datatypeName.toLowerCase().indexOf("money") != -1) {
                final NumericClass nc = new NumericClass();
                nc.setDatatypeName(datatypeName.toUpperCase());
                if (size != null) {
                    nc.setOpenBrace("(");
                    nc.setSize(size);
                    nc.setClosedBrace(")");
                }
                if (datatypeName.toLowerCase().indexOf("int") != -1 || datatypeName.toLowerCase().indexOf("double") != -1 || datatypeName.equalsIgnoreCase("real") || datatypeName.equalsIgnoreCase("float")) {
                    nc.setOpenBrace(null);
                    nc.setSize(null);
                    nc.setClosedBrace(null);
                }
                return nc;
            }
            if (datatypeName.toLowerCase().indexOf("blob") != -1 || datatypeName.toLowerCase().indexOf("binary") != -1 || datatypeName.equalsIgnoreCase("bit") || datatypeName.equalsIgnoreCase("image") || datatypeName.toLowerCase().indexOf("bool") != -1 || datatypeName.equalsIgnoreCase("raw") || datatypeName.equalsIgnoreCase("longtext") || datatypeName.equalsIgnoreCase("mediumtext") || datatypeName.equalsIgnoreCase("tinytext")) {
                final BinClass bc = new BinClass();
                bc.setDatatypeName(datatypeName.toUpperCase());
                if (size != null) {
                    bc.setOpenBrace("(");
                    bc.setSize(size);
                    bc.setClosedBrace(")");
                }
                return bc;
            }
        }
        return null;
    }
    
    public static String getDateFormat(String dateTimeLiteralValue, final int targetdb) {
        if (dateTimeLiteralValue.equals("''")) {
            return "'1900-01-01 00:00:00'";
        }
        if (!dateTimeLiteralValue.startsWith("'")) {
            return null;
        }
        dateTimeLiteralValue = dateTimeLiteralValue.substring(1, dateTimeLiteralValue.length() - 1);
        if (dateTimeLiteralValue.trim().length() == 0) {
            return "'1900-01-01 00:00:00'";
        }
        String dateformat = "";
        boolean secondsWithColon = false;
        String dateLiteralValue = dateTimeLiteralValue;
        String timeLiteralValue = "";
        int space = 0;
        if ((space = dateTimeLiteralValue.indexOf(" ")) != -1 && space >= 5) {
            dateLiteralValue = dateTimeLiteralValue.substring(0, space);
            timeLiteralValue = dateTimeLiteralValue.substring(space + 1);
        }
        else if (dateTimeLiteralValue.indexOf(":") != -1 || dateTimeLiteralValue.toLowerCase().indexOf("am") != -1 || dateTimeLiteralValue.toLowerCase().indexOf("pm") != -1) {
            timeLiteralValue = dateTimeLiteralValue;
            dateLiteralValue = "";
            final String tempStr = timeLiteralValue.toLowerCase();
            if (tempStr.indexOf("jan") != -1 || tempStr.indexOf("feb") != -1 || tempStr.indexOf("mar") != -1 || tempStr.indexOf("apr") != -1 || tempStr.indexOf("may") != -1 || tempStr.indexOf("jun") != -1 || tempStr.indexOf("jul") != -1 || tempStr.indexOf("aug") != -1 || tempStr.indexOf("sep") != -1 || tempStr.indexOf("oct") != -1 || tempStr.indexOf("nov") != -1 || tempStr.indexOf("dec") != -1) {
                int index = tempStr.indexOf(" ", 10);
                if (index != -1) {
                    dateLiteralValue = timeLiteralValue.substring(0, index);
                    timeLiteralValue = timeLiteralValue.substring(index + 1);
                }
                else {
                    index = tempStr.indexOf(" ", 6);
                    dateLiteralValue = timeLiteralValue.substring(0, index);
                    timeLiteralValue = timeLiteralValue.substring(index + 1);
                }
            }
        }
        String tempStr = dateLiteralValue.toLowerCase();
        if (tempStr.indexOf("jan") == -1 && tempStr.indexOf("feb") == -1 && tempStr.indexOf("mar") == -1 && tempStr.indexOf("apr") == -1 && tempStr.indexOf("may") == -1 && tempStr.indexOf("jun") == -1 && tempStr.indexOf("jul") == -1 && tempStr.indexOf("aug") == -1 && tempStr.indexOf("sep") == -1 && tempStr.indexOf("oct") == -1 && tempStr.indexOf("nov") == -1 && tempStr.indexOf("dec") == -1) {
            final int len = dateLiteralValue.length();
            String seperator = "";
            if (len == 10 || len == 8 || len == 9) {
                int index2 = dateLiteralValue.indexOf("-");
                if (index2 != -1) {
                    seperator = "-";
                }
                else if ((index2 = dateLiteralValue.indexOf("/")) != -1) {
                    seperator = "/";
                }
                else if ((index2 = dateLiteralValue.indexOf(".")) != -1) {
                    seperator = ".";
                }
                if (index2 == 2 && seperator != "") {
                    if (len == 10 || len == 9) {
                        dateformat = dateformat + "MM" + seperator + "DD" + seperator + "YYYY";
                    }
                    else if (len == 8) {
                        dateformat = dateformat + "MM" + seperator + "DD" + seperator + "YY";
                    }
                }
                else if (index2 == 1 && seperator != "") {
                    dateformat = dateformat + "MM" + seperator + "DD" + seperator + "YYYY";
                }
                else if (index2 == 4 && seperator != "") {
                    dateformat = dateformat + "YYYY" + seperator + "MM" + seperator + "DD";
                }
            }
            if (seperator == "") {
                if (len == 8) {
                    if (targetdb == 10) {
                        return "'" + dateTimeLiteralValue + "'";
                    }
                    dateformat += "YYYYMMDD";
                }
                else if (len == 6) {
                    if (targetdb == 10) {
                        return "'" + dateTimeLiteralValue + "'";
                    }
                    dateformat += "YYMMDD";
                }
            }
        }
        else {
            while (tempStr.indexOf("  ") != -1) {
                tempStr = tempStr.replaceAll("  ", " ");
            }
            final StringTokenizer st = new StringTokenizer(tempStr, " ");
            boolean start = true;
            while (st.hasMoreTokens()) {
                if (!start) {
                    dateformat += " ";
                }
                final String token = st.nextToken();
                if (token.length() == 3) {
                    if (token.indexOf(",") != -1) {
                        dateformat += "DD,";
                    }
                    else {
                        dateformat += "MON";
                    }
                }
                else if (token.length() == 4) {
                    if (token.indexOf(",") != -1) {
                        dateformat += "MON,";
                    }
                    else {
                        dateformat += "YYYY";
                    }
                }
                else if (token.length() == 2) {
                    if (dateformat.indexOf("DD") != -1) {
                        dateformat += "YY";
                    }
                    else {
                        dateformat += "DD";
                    }
                }
                else if (token.length() == 1 && !token.equalsIgnoreCase(",")) {
                    dateformat += "DD";
                }
                start = false;
            }
            if (dateformat == "" && tempStr.length() > 0 && tempStr.indexOf(" ") == -1 && tempStr.indexOf("-") == 2 && tempStr.lastIndexOf("-") == 6) {
                if (tempStr.length() == 11) {
                    dateformat = "DD-MON-YYYY";
                }
                else {
                    dateformat = "DD-MON-YY";
                }
            }
        }
        final int len = timeLiteralValue.length();
        if (len > 0) {
            if (dateformat != "") {
                dateformat += " ";
            }
            final String[] time = timeLiteralValue.split(":");
            int index2 = 0;
            if (time.length > 1) {
                if (time.length >= 3) {
                    dateformat += "HH24:MI:SS";
                    if (time.length > 3) {
                        secondsWithColon = true;
                        if (targetdb == 1) {
                            final String ms = time[3];
                            dateformat = dateformat + ":FF" + ms.length();
                        }
                    }
                    else if (targetdb == 1) {
                        String ms = time[2];
                        int index3 = -1;
                        if ((index3 = ms.indexOf(".")) != -1) {
                            ms = ms.substring(index3 + 1);
                            dateformat = dateformat + ".FF" + ms.length();
                        }
                    }
                }
                else if ((index2 = timeLiteralValue.toLowerCase().indexOf("am")) != -1 || (index2 = timeLiteralValue.toLowerCase().indexOf("pm")) != -1) {
                    if (timeLiteralValue.toLowerCase().charAt(index2) == 'a') {
                        dateformat += "HH:MIAM";
                    }
                    else {
                        dateformat += "HH:MIPM";
                    }
                }
                else {
                    dateformat += "HH24:MI";
                }
            }
            else if ((index2 = timeLiteralValue.toLowerCase().indexOf("am")) != -1 || (index2 = timeLiteralValue.toLowerCase().indexOf("pm")) != -1) {
                dateformat += "HH";
                final String temp = timeLiteralValue.substring(1);
                if (CustomizeUtil.isStartsWithNum(temp)) {
                    if (index2 != 2) {
                        for (int spacesToAdd = index2 - 2, i = 0; i < spacesToAdd; ++i) {
                            dateformat += " ";
                        }
                    }
                }
                else if (len != 3) {
                    for (int spacesToAdd = index2 - 1, i = 0; i < spacesToAdd; ++i) {
                        dateformat += " ";
                    }
                }
                if (timeLiteralValue.toLowerCase().charAt(index2) == 'a') {
                    dateformat += "AM";
                }
                else {
                    dateformat += "PM";
                }
            }
        }
        if (dateformat == "") {
            return null;
        }
        if (targetdb == 10) {
            if ((dateformat.equals("YYYY-MM-DD") || dateformat.equals("YYYY-MM-DD HH24:MI:SS") || dateformat.equals("HH24:MI:SS")) && !secondsWithColon) {
                if (dateformat.equals("YYYY-MM-DD") || dateformat.equals("HH24:MI:SS")) {
                    return dateformat;
                }
                return null;
            }
        }
        else if (targetdb == 1 && (dateformat.equals("DD-MON-YYYY") || dateformat.equals("DD-MON-YY"))) {
            return null;
        }
        return "'" + dateformat + "'";
    }
    
    public static HashMap truncateNames(final List data, final int validLength) {
        final HashMap returnTruncatedMap = new HashMap();
        final int size = data.size();
        int genValue = 0;
        for (int i = 0; i < size; ++i) {
            if (data.get(i) != null) {
                String val = data.get(i);
                boolean addQuotes = false;
                if (val.startsWith("\"") && val.endsWith("\"")) {
                    val = val.substring(1, val.length() - 1);
                    addQuotes = true;
                }
                if (val.length() > validLength) {
                    String temp = val.substring(0, validLength);
                    boolean present = false;
                    final Set set = returnTruncatedMap.keySet();
                    for (final Object next : set) {
                        String obj = returnTruncatedMap.get(next);
                        if (obj.startsWith("\"") && obj.endsWith("\"")) {
                            obj = obj.substring(1, obj.length() - 1);
                        }
                        if (obj.equals(temp) && !next.equals(val)) {
                            present = true;
                            break;
                        }
                    }
                    if (!present) {
                        for (int j = 0; j < size; ++j) {
                            String validCol = data.get(j);
                            if (validCol.startsWith("\"") && validCol.endsWith("\"")) {
                                validCol = validCol.substring(1, validCol.length() - 1);
                            }
                            if (validCol.length() == validLength && validCol.equalsIgnoreCase(temp)) {
                                present = true;
                                break;
                            }
                        }
                    }
                    if (present) {
                        final String intStr = "" + genValue;
                        final int intlen = intStr.length();
                        final String intStr2 = "" + (genValue + 1);
                        if (intStr2.length() > intlen) {
                            String cc = temp.substring(0, validLength - (intlen + 2));
                            cc = cc + "_" + (genValue + 1);
                            if (addQuotes) {
                                cc = "\"" + cc + "\"";
                            }
                            returnTruncatedMap.put(data.get(i), cc);
                            ++genValue;
                        }
                        else {
                            String cc = temp.substring(0, validLength - (intlen + 1));
                            cc = cc + "_" + (genValue + 1);
                            if (addQuotes) {
                                cc = "\"" + cc + "\"";
                            }
                            returnTruncatedMap.put(data.get(i), cc);
                            ++genValue;
                        }
                    }
                    else {
                        if (addQuotes) {
                            temp = "\"" + temp + "\"";
                        }
                        returnTruncatedMap.put(data.get(i), temp);
                    }
                }
            }
        }
        if (SwisSQLAPI.enableObjectMapping) {
            SwisSQLUtils.objectNameMapping.putAll(returnTruncatedMap);
        }
        return returnTruncatedMap;
    }
    
    public static boolean isAggregateFunction(final SelectColumn scol) {
        if (scol != null) {
            final Vector colExprn = scol.getColumnExpression();
            for (int s = 0; s < colExprn.size(); ++s) {
                final Object sObj = colExprn.get(s);
                if (sObj instanceof FunctionCalls) {
                    final FunctionCalls fc = (FunctionCalls)sObj;
                    final TableColumn tfc = fc.getFunctionName();
                    if (tfc != null) {
                        final String fnName = tfc.getColumnName();
                        if (fnName.equalsIgnoreCase("min") || fnName.equalsIgnoreCase("max") || fnName.equalsIgnoreCase("count") || fnName.equalsIgnoreCase("avg") || fnName.equalsIgnoreCase("sum")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static void checkAndReplaceGroupByItem(final SelectColumn sc, final SelectQueryStatement from_sqs) {
        final Vector scColExpr = sc.getColumnExpression();
        if (scColExpr.elementAt(0) instanceof TableColumn) {
            final TableColumn scTableColumn = scColExpr.elementAt(0);
            if (scTableColumn.getColumnName().toLowerCase().equalsIgnoreCase("date_trunc")) {
                final Vector from_sqsSelectItem = from_sqs.getSelectStatement().getSelectItemList();
                for (int j = 0; j < from_sqsSelectItem.size(); ++j) {
                    if (from_sqsSelectItem.get(j) instanceof SelectColumn) {
                        final Vector from_sqsSelectItemColExpr = from_sqsSelectItem.get(j).getColumnExpression();
                        for (int jv = 0; jv < from_sqsSelectItemColExpr.size(); ++jv) {
                            if (from_sqsSelectItemColExpr.get(jv) instanceof TableColumn) {
                                final TableColumn from_sqsSelectItemColExprTC = from_sqsSelectItemColExpr.get(jv);
                                if (from_sqsSelectItemColExprTC.getColumnName().toLowerCase().equalsIgnoreCase("date_trunc")) {
                                    sc.setColumnExpression(scColExpr);
                                }
                            }
                            if (from_sqsSelectItemColExpr.get(jv) instanceof FunctionCalls) {
                                final FunctionCalls from_sqsSelectItemColExprFC = from_sqsSelectItemColExpr.get(jv);
                                if (from_sqsSelectItemColExprFC.getFunctionNameAsAString().toLowerCase().equalsIgnoreCase("date_trunc")) {
                                    sc.setColumnExpression(from_sqsSelectItemColExpr);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static String getObjectNameFromMapping(final String origObjName) {
        final Object obj = origObjName;
        final String targetObjName = SwisSQLUtils.objectNameMapping.get(obj);
        if (targetObjName != null) {
            targetObjName.trim();
        }
        return targetObjName;
    }
    
    public static void setObjectNameForMapping(final String origName, final String targetName) {
        if (SwisSQLAPI.enableObjectMapping) {
            SwisSQLUtils.objectNameMapping.put(origName, targetName);
        }
    }
    
    public static String[] getOracleDateFormats() {
        return SwisSQLUtils.oracleDateFormat;
    }
    
    public static String getFunctionReturnType(final String functionName, final Vector functionArgs) {
        if (functionName != null) {
            if (functionName.equalsIgnoreCase("substring") || functionName.equalsIgnoreCase("char") || functionName.equalsIgnoreCase("lower") || functionName.equalsIgnoreCase("ltrim") || functionName.equalsIgnoreCase("replicate") || functionName.equalsIgnoreCase("right") || functionName.equalsIgnoreCase("rtrim") || functionName.equalsIgnoreCase("space") || functionName.equalsIgnoreCase("stuff") || functionName.equalsIgnoreCase("upper")) {
                return "string";
            }
            if (functionName.equalsIgnoreCase("convert")) {
                if (functionArgs != null && functionArgs.size() > 0 && functionArgs.get(0) instanceof CharacterClass) {
                    return "string";
                }
            }
            else {
                if (CastingUtil.ContainsIgnoreCase(SwisSQLUtils.functionsReturningTimestamp, functionName)) {
                    return "timestamp";
                }
                if (CastingUtil.ContainsIgnoreCase(SwisSQLUtils.functionsReturningDate, functionName)) {
                    return "date";
                }
                if (functionName.equalsIgnoreCase("round") || functionName.equalsIgnoreCase("trunc")) {
                    if (functionArgs != null && functionArgs.size() > 1) {
                        String fnArg = functionArgs.get(1).toString();
                        if (fnArg.startsWith("'") && fnArg.endsWith("'")) {
                            fnArg = fnArg.substring(1, fnArg.length() - 1);
                        }
                        for (int i = 0; i < SwisSQLUtils.oracleDateFormat.length; ++i) {
                            final String dateFmt = SwisSQLUtils.oracleDateFormat[i];
                            if (fnArg.equalsIgnoreCase(dateFmt)) {
                                return "date";
                            }
                        }
                    }
                }
                else if (functionName.equalsIgnoreCase("cast") && functionArgs != null) {
                    for (int v = 0; v < functionArgs.size(); ++v) {
                        if (functionArgs.get(v) instanceof DateClass) {
                            final DateClass dcType = functionArgs.get(v);
                            if (dcType.getDatatypeName() != null && dcType.getDatatypeName().equalsIgnoreCase("date")) {
                                return "date";
                            }
                            if (dcType.getDatatypeName() != null && dcType.getDatatypeName().equalsIgnoreCase("timestamp")) {
                                return "timestamp";
                            }
                        }
                        else if (functionArgs.get(v) instanceof SelectColumn) {
                            final Vector scColExp = functionArgs.get(v).getColumnExpression();
                            for (int v2 = 0; v2 < scColExp.size(); ++v2) {
                                if (scColExp.get(v2) instanceof DateClass) {
                                    final DateClass dcType2 = scColExp.get(v2);
                                    if (dcType2.getDatatypeName() != null && dcType2.getDatatypeName().equalsIgnoreCase("date")) {
                                        return "date";
                                    }
                                    if (dcType2.getDatatypeName() != null && dcType2.getDatatypeName().equalsIgnoreCase("timestamp")) {
                                        return "timestamp";
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return "none";
    }
    
    public static String convertDayToInterval(final String day) {
        String intervalStr = "";
        final int dayInSeconds = 86400;
        try {
            final int dayValue = (int)(Double.parseDouble(day) * 100.0);
            if (dayValue <= 0) {
                return day;
            }
            if (dayValue >= 100) {
                return "INTERVAL '" + dayValue / 100 + "' DAY";
            }
            final int dayVal = 86400 * dayValue / 100;
            final int hours = dayVal / 3600;
            final int minutes = dayVal % 3600 / 60;
            final int seconds = dayVal % 3600 % 60 / 60;
            String minuteStr = "" + minutes;
            if (minuteStr.length() == 1) {
                minuteStr = "0" + minuteStr;
            }
            String secondStr = "" + seconds;
            if (secondStr.length() == 1) {
                secondStr = "0" + secondStr;
            }
            intervalStr = "INTERVAL '" + hours + ":" + minuteStr + ":" + secondStr + "' HOUR TO SECOND";
        }
        catch (final NumberFormatException ex) {}
        return intervalStr;
    }
    
    public static TableColumn getMappedFunctionName(final HashMap functionMapping, final TableColumn functionName) {
        if (functionMapping != null && functionName != null) {
            String mappedFunctionName;
            final String originalFunctionName = mappedFunctionName = functionName.getColumnName();
            if (functionMapping.containsKey(originalFunctionName.toUpperCase())) {
                mappedFunctionName = functionMapping.get(originalFunctionName.toUpperCase()).toString();
            }
            functionName.setColumnName(mappedFunctionName);
        }
        return functionName;
    }
    
    static {
        SwisSQLUtils.objectNameMapping = new Hashtable();
        SwisSQLUtils.oracleSystemFunctionsArray = new String[] { "ABS", "ACOS", "APP_NAME", "ASCII", "ASIN", "ATAN", "ATN2", "AVG", "CAST", "CEILING", "CHAR", "CHARINDEX", "CHECKSUM", "COALESCE", "COL_LENGTH", "COL_NAME", "CONTAINS", "CONVERT", "COS", "COT", "COUNT", "DATALENGTH", "DATEADD", "DATEDIFF", "DATENAME", "DATEPART", "DIFFERENCE", "DEGREES", "DAY", "EXP", "FLOOR", "GETDATE", "GETUTCDATE", "ISDATE", "ISNULL", "ISNUMERIC", "LEFT", "LEN", "LOG", "LOG10", "LOWER", "LTRIM", "MAX", "MIN", "MONTH", "NEWID", "NULLIF", "OBJECT_ID", "PATINDEX", "QUOTENAME", "RAND", "REPLACE", "RADIANS", "REPLICATE", "REVERSE", "RIGHT", "ROUND", "RTRIM", "SOUNDEX", "SPACE", "STR", "SUBSTRING", "STUFF", "CASE", "USER_NAME", "UPPER", "UNICODE" };
        SwisSQLUtils.mysqlSystemFunctionsArray = new String[] { "ABS", "ACOS", "APP_NAME", "ASCII", "ASIN", "ATAN", "ATN2", "AVG", "CAST", "CEILING", "CHAR", "CHARINDEX", "CHECKSUM", "COALESCE", "COL_LENGTH", "COL_NAME", "CONTAINS", "CONVERT", "COS", "COT", "COUNT", "DATALENGTH", "DATEADD", "DATEDIFF", "DATENAME", "DATEPART", "DIFFERENCE", "DEGREES", "DAY", "EXP", "FLOOR", "GETDATE", "GETUTCDATE", "ISDATE", "ISNULL", "ISNUMERIC", "LEFT", "LEN", "LOG", "LOG10", "LOWER", "LTRIM", "MAX", "MIN", "MONTH", "NEWID", "NULLIF", "OBJECT_ID", "PATINDEX", "QUOTENAME", "RAND", "REPLACE", "RADIANS", "REPLICATE", "REVERSE", "RIGHT", "ROUND", "RTRIM", "SOUNDEX", "SPACE", "STR", "SUBSTRING", "STUFF", "CASE", "USER_NAME", "UPPER", "UNICODE" };
        SwisSQLUtils.sqlServerSystemFunctionsArray = new String[] { "abs", "acos", "app_name", "ascii", "asin", "atan", "atn2", "avg", "cast", "ceiling", "char", "charindex", "checksum", "coalesce", "col_length", "col_name", "contains", "convert", "cos", "cot", "count", "datalength", "dateadd", "datediff", "datename", "datepart", "difference", "degrees", "day", "exp", "floor", "getdate", "getutcdate", "isdate", "isnull", "isnumeric", "left", "len", "log", "log10", "lower", "ltrim", "max", "min", "month", "newid", "nullif", "object_id", "patindex", "quotename", "rand", "replace", "radians", "replicate", "reverse", "right", "round", "rtrim", "soundex", "space", "str", "substring", "stuff", "case", "user_name", "upper", "unicode" };
        SwisSQLUtils.sybaseSystemFunctionsArray = new String[] { "abs", "acos", "app_name", "ascii", "asin", "atan", "atn2", "avg", "cast", "ceiling", "char", "charindex", "checksum", "coalesce", "col_length", "col_name", "contains", "convert", "cos", "cot", "count", "datalength", "dateadd", "datediff", "datename", "datepart", "day", "degrees", "exp", "floor", "getdate", "isdate", "isnull", "isnumeric", "left", "len", "log", "log10", "lower", "ltrim", "max", "min", "month", "newid", "nullif", "object_id", "patindex", "radians", "rand", "replace", "replicate", "reverse", "right", "round", "rtrim", "space", "str", "substring", "stuff", "case", "user_name", "upper" };
        SwisSQLUtils.db2SystemFunctionsArray = new String[] { "ABS", "ASCII", "DAYNAME", "ACOS", "CHAR", "DAYOFWEEK", "ASIN", "CONCAT", "DAYOFYEAR", "ATAN", "DIFFERENCE", "HOUR", "ATAN2", "INSERT", "MINUTE", "CEILING", "LCASE", "MONTH", "COS", "LEFT", "MONTHNAME", "COT", "LENGTH", "MONTHNAME", "DEGREES", "LOCATE", "QUARTER", "EXP", "LTRIM", "SECOND", "FLOOR", "REPEAT", "TIMESTAMPDIFF", "LOG", "REPLACE", "WEEK", "LOG10", "RIGHT", "YEAR", "MOD", "RTRIM", "POWER", "SOUNDEX", "RADIANS", "SPACE", "RAND", "SUBSTRING", "ROUND", "UCASE", "SIGN", "SIN", "SQRT", "TAN", "TRUNCATE" };
        SwisSQLUtils.postgresqlSystemFunctionsArray = new String[] { "ABS", "ASCII", "DATABASE", "CURDATE", "ACOS", "CHAR", "IFNULL", "CURTIME", "ASIN", "CONCAT", "USER", "DAYNAME", "ATAN", "LCASE", "DAYOFMONTH", "ATAN2", "LEFT", "DAYOFWEEK", "CEILING", "LENGTH", "DAYOFYEAR", "COS", "LTRIM", "HOUR", "COT", "REPEAT", "MINUTE", "DEGREES", "REPLACE", "MONTH", "EXP", "RTRIM", "MONTHNAME", "FLOOR", "SPACE", "NOW", "LOG", "SUBSTRING", "QUARTER", "LOG10", "UCASE", "SECOND", "MOD", "WEEK", "PI", "YEAR", "POWER", "RADIANS", "RAND", "ROUND", "SIGN", "SIN", "SQRT", "TAN", "TRUNCATE" };
        SwisSQLUtils.teradataSystemFunctionsArray = new String[] { "CURRENT_DATE", "CURRENT_TIMESTAMP", "CURRENT_TIME", "DATABASE", "DATE", "PROFILE", "ROLE", "SESSION", "TIME", "USER" };
        SwisSQLUtils.oracleKeywordsArray = new String[] { "ACCESS", "ADD", "ALL", "ALTER", "AND", "ANY", "AS", "ASC", "AUDIT", "BETWEEN", "BY", "CHAR", "CHECK", "CLUSTER", "COLUMN", "COMMENT", "COMPRESS", "CONNECT", "CREATE", "CURRENT", "DATE", "DECIMAL", "DEFAULT", "DELETE", "DESC", "DISTINCT", "DROP", "ELSE", "EXCLUSIVE", "EXISTS", "FILE", "FLOAT", "FOR", "FROM", "GRANT", "GROUP", "HAVING", "IDENTIFIED", "IMMEDIATE", "IN", "INCREMENT", "INDEX", "INITIAL", "INSERT", "INTEGER", "INTERSECT", "INTO", "IS", "LEVEL", "LIKE", "LOCK", "LONG", "MAXEXTENTS", "MINUS", "MLSLABEL", "MODE", "MODIFY", "NOAUDIT", "NOCOMPRESS", "NOT", "NOWAIT", "NULL", "NUMBER", "OF", "OFFLINE", "ON", "ONLINE", "OPTION", "OR", "ORDER", "PCTFREE", "PRIOR", "PRIVILEGES", "PUBLIC", "RAW", "RENAME", "RESOURCE", "REVOKE", "ROW", "ROWID", "ROWNUM", "ROWS", "SELECT", "SESSION", "SET", "SHARE", "SIZE", "SMALLINT", "START", "SUCCESSFUL", "SYNONYM", "SYSDATE", "TABLE", "THEN", "TO", "TRIGGER", "UID", "UNION", "UNIQUE", "UPDATE", "USER", "VALIDATE", "VALUES", "VARCHAR", "VARCHAR2", "VIEW", "WHENEVER", "WHERE", "WITH" };
        SwisSQLUtils.sqlServerKeywordsArray = new String[] { "ADD", "EXCEPT", "PERCENT", "ALL", "EXEC", "PLAN", "ALTER", "EXECUTE", "PRECISION", "AND", "EXISTS", "PRIMARY", "ANY", "EXIT", "PRINT", "AS", "FETCH", "PROC", "ASC", "FILE", "PROCEDURE", "AUTHORIZATION", "FILLFACTOR", "PUBLIC", "BACKUP", "FOR", "RAISERROR", "BEGIN", "FOREIGN", "READ", "BETWEEN", "FREETEXT", "READTEXT", "BREAK", "FREETEXTTABLE", "RECONFIGURE", "BROWSE", "FROM", "REFERENCES", "BULK", "FULL", "REPLICATION", "BY", "FUNCTION", "RESTORE", "CASCADE", "GOTO", "RESTRICT", "CASE", "GRANT", "RETURN", "CHECK", "GROUP", "REVOKE", "CHECKPOINT", "HAVING", "RIGHT", "CLOSE", "HOLDLOCK", "ROLLBACK", "CLUSTERED", "IDENTITY", "ROWCOUNT", "COALESCE", "IDENTITY_INSERT", "ROWGUIDCOL", "COLLATE", "IDENTITYCOL", "RULE", "COLUMN", "IF", "SAVE", "COMMIT", "IN", "SCHEMA", "COMPUTE", "INDEX", "SELECT", "CONSTRAINT", "INNER", "SESSION_USER", "CONTAINS", "INSERT", "SET", "CONTAINSTABLE", "INTERSECT", "SETUSER", "CONTINUE", "INTO", "SHUTDOWN", "CONVERT", "IS", "SOME", "CREATE", "JOIN", "STATISTICS", "CROSS", "KEY", "SYSTEM_USER", "CURRENT", "KILL", "TABLE", "CURRENT_DATE", "LEFT", "TEXTSIZE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "LIKE", "THEN", "LINENO", "TO", "CURRENT_USER", "LOAD", "TOP", "CURSOR", "NATIONAL", "TRAN", "DATABASE", "NOCHECK", "TRANSACTION", "DBCC", "NONCLUSTERED", "TRIGGER", "DEALLOCATE", "NOT", "TRUNCATE", "DECLARE", "NULL", "TSEQUAL", "DEFAULT", "NULLIF", "UNION", "DELETE", "OF", "UNIQUE", "DENY", "OFF", "UPDATE", "DESC", "OFFSETS", "UPDATETEXT", "DISK", "ON", "USE", "DISTINCT", "OPEN", "USER", "DISTRIBUTED", "OPENDATASOURCE", "VALUES", "DOUBLE", "OPENQUERY", "VARYING", "DROP", "OPENROWSET", "VIEW", "DUMMY", "OPENXML", "WAITFOR", "DUMP", "OPTION", "WHEN", "ELSE", "OR", "WHERE", "END", "ORDER", "WHILE", "ERRLVL", "OUTER", "WITH", "ESCAPE", "OVER", "WRITETEXT" };
        SwisSQLUtils.timestenReservedWordsArray = new String[] { "ABS", "ACTION", "ADD", "ADDMONTHS", "ALL", "ALLOWABLE", "ALTER", "AND", "ANY", "AS", "ASC", "ASYNCHRONOUS", "AUTHORIZATION", "AUTOREFRESH", "AVG", "BEGIN", "BETWEEN", "BIGINT", "BIGINTS", "BINARY", "BITAND", "BITNEG", "BITOR", "BULK", "BY", "CACHE", "CACHEONLY", "CALL", "CASCADE", "CHAR", "CHARACTER", "CHECK", "COLON", "COLUMN", "COMMA", "COMMIT", "COMPRESS", "CONCAT", "CONFLICTS", "CONNECT", "CONSTRAINT", "COUNT", "CREATE", "CS", "CURRENT", "CURRENT_SCHEMA", "CURRENT_USER", "CURRENTDATE", "CURRENTDATETIME", "CURRENTTIME", "CYCLE", "DATASTORE", "DATASTORE_OWNER", "DATE", "DATETIME", "DAY", "DDL", "DEBUG", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELETE", "DELETE_FT", "DESC", "DIGIT", "DISABLE", "DISTINCT", "DOT", "DOUBLE", "DROP", "DURABLE", "DURATION", "ELEMENT", "ENCRYPTED", "EQU", "ESCAPE", "EVERY", "EXCEPTION", "EXCLOR", "EXISTS", "EXIT", "EXTERNALLY", "FAILTHRESHOLD", "FIRST", "FLOAT", "FLUSH", "FOR", "FOREIGN", "FRACTION", "FROM", "FULL", "GARBAGE", "GEQ", "GETDATE", "GRANT", "GROUP", "GRT", "HASH", "HAVING", "HEXSTRING", "HOUR", "ID", "IDENT", "IDENTIFIED", "IN", "INCREMENT", "INCREMENTAL", "INDEX", "INDICATOR", "INLINE", "INSERT", "INSERTONLY", "INSTANCE", "INT", "INTEGER", "INTERVAL", "INTO", "IS", "KEY", "LATENCY", "LBRACE", "LEQ", "LES", "LIKE", "LIMIT", "LOAD", "LOCAL", "LONG", "LOWER", "LPAREN", "MASTER", "MATERIALIZED", "MAX", "MAXVALUE", "MILLISECONDS", "MIN", "MINUS", "MINUTE", "MINUTES", "MINVALUE", "MOD", "MODE", "MONTH", "MULTI", "NAME", "NATIONAL", "NCHAR", "NEQ", "NO", "NONDURABLE", "NOT", "NOTIMPLEMENTED", "NQUOTESTR", "NULL", "NUMERIC", "NVARCHAR", "NVL", "OF", "OFF", "ON", "OR", "ORACLE", "ORACLEQUERY", "ORDER", "OUT_OF_LINE", "OUTERJOIN", "PAGES", "PAUSED", "PLUS", "PORT", "PRECISION", "PRIMARY", "PRIVATE", "PRIVILEGES", "PROPAGATE", "PROPAGATOR", "PUBLIC", "PUBLICREAD", "PUBLICROW", "QUIT", "QUOTESTR", "RBRACE", "RC", "READONLY", "REAL", "REALS", "RECEIPT", "REFERENCES", "REFRESH", "RELEASE", "REPLICATION", "REPORT", "REQUEST", "REQUIRED", "RESTRICT", "RESUME", "RETURN", "REVOKE", "ROLLBACK", "ROW", "ROWS", "RPAREN", "RR", "RTRIM", "RU", "SCHEMA", "SECOND", "SECONDS", "SECTION", "SELECT", "SELF", "SEMI", "SEQCACHE", "SEQCACHEONLY", "SEQUENCE", "SERVICES", "SESSION", "SESSION_USER", "SET", "SLASH", "SMALLINT", "SOME", "STAR", "START", "STATE", "STOPPED", "STORE", "SUBSCRIBER", "SUM", "SYNCHRONOUS", "SYSDATE", "SYSTEM", "SYSTEM_USER", "TABLE", "TIME", "TIMEOUT", "TIMESTAMP", "TINYINT", "TO", "TO_CHAR", "TO_DATE", "TOCHAR", "TODATE", "TOINTEGER", "TRAFFIC", "TRANSMIT", "TWOSAFE", "UNION", "UNIQUE", "UNLOAD", "UPDATE", "UPPER", "USER", "USERMANAGED", "VALUES", "VARBINARY", "VARCHAR", "VARYING", "VIEW", "WAIT", "WHEN", "WHERE", "WITH", "WORK", "WRITE", "WRITETHROUGH", "YEAR" };
        SwisSQLUtils.netezzaReservedWordsArray = new String[] { "ABORT", "ADMIN", "AGGREGATE", "ALIGN", "ALL", "ALLOCATE", "ANALYSE", "ANALYZE", "AND", "ANY", "AS", "ASC", "BETWEEN", "BINARY", "BIT", "BOTH", "CASE", "CHAR", "CHARACTER", "DEC", "DECIMAL", "DEFAULT", "DEFERRABLE", "DESC", "DISTINCT", "DISTRIBUTE", "DO", "ELSE", "END", "EXCEPT", "EXCLUDE", "EXISTS", "EXPLAIN", "EXPRESS", "EXTEND", "FALSE", "LEADING", "LEFT", "LIKE", "LIMIT", "LISTEN", "LOAD", "LOCAL", "LOCK", "MINUS", "MOVE", "NATURAL", "NCHAR", "NEW", "NOT", "NOTNULL", "NULL", "NULLS", "NUMERIC", "RESET", "REUSE", "RIGHT", "ROWS", "ROWSETLIMIT", "RULE", "SEARCH", "SELECT", "SESSION_USER", "SETOF", "SHOW", "SOME", "SYSTEM", "THEN", "TIES", "TIME", "TIMESTAMP", "CHECK", "CLUSTER", "COLLATE", "COLLATION", "COLUMN", "CONSTRAINT", "COPY", "CROSS", "CURRENT", "CURRENT_RUSER", "CURRENT_USERID", "CURRENT_USEROID", "DEALLOCATE", "FIRST", "FLOAT", "FOLLOWING", "FOR", "FOREIGN", "FROM", "FULL", "FUNCTION", "GENSTATS", "GLOBAL", "GROUP", "HAVING", "ILIKE", "IN", "INDEX", "INITIALLY", "INNER", "INOUT", "INTERSECT", "INTERVAL", "INTO", "OFF", "OFFSET", "OLD", "ON", "ONLINE", "ONLY", "OR", "ORDER", "OTHERS", "OUT", "OUTER", "OVER", "OVERLAPS", "PARTITION", "POSITION", "PRECEDING", "PRECISION", "PRESERVE", "PRIMARY", "RESET", "REUSE", "TO", "TRAILING", "TRANSACTION", "TRUE", "UNBOUNDED", "UNION", "UNIQUE", "USING", "VACUUM", "VARCHAR", "VERBOSE", "WHEN", "WHERE", "WITH", "WRITE" };
        SwisSQLUtils.postgresqlReservedWordsArray = new String[] { "ALL", "ANALYSE", "ANALYZE", "AND", "ANY", "ARRAY", "AS", "ASC", "ASYMMETRIC", "AUTHORIZATION", "BETWEEN", "BINARY", "BOTH", "CASE", "CAST", "CHECK", "COLLATE", "COLUMN", "CONSTRAINT", "CREATE", "CROSS", "CURRENT_CATALOG", "CURRENT_DATE", "CURRENT_ROLE", "CURRENT_SCHEMA", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "DEFAULT", "DEFERRABLE", "DESC", "DISTINCT", "DO", "ELSE", "END", "EXCEPT", "FETCH", "FOR", "FOREIGN", "FREEZE", "FROM", "FULL", "GRANT", "GROUP", "HAVING", "ILIKE", "IN", "INITIALLY", "INNER", "INTERSECT", "INTO", "IS", "ISNULL", "JOIN", "LEADING", "LEFT", "LIKE", "LIMIT", "LOCALTIME", "LOCALTIMESTAMP", "NATURAL", "NEW", "NOT", "NOTNULL", "NULL", "OFF", "OFFSET", "OLD", "ON", "ONLY", "OR", "ORDER", "OUTER", "OVERLAPS", "PLACING", "PRIMARY", "REFERENCES", "RETURNING", "RIGHT", "SELECT", "SESSION_USER", "SIMILAR", "SOME", "SYMMETRIC", "TABLE", "THEN", "TO", "TRAILING", "UNION", "UNIQUE", "USER", "USING", "VARIADIC", "VERBOSE", "WHEN", "WHERE", "WITH" };
        SwisSQLUtils.teradataReservedWordsArray = new String[] { "A", "ABORT", "ABORTSESSION", "ABS", "ABSOLUTE", "ACCESS", "ACCESS_LOCK", "ACCOUNT", "ACOS", "ACOSH", "ACTION", "ADA", "ADD", "ADD_MONTHS", "ADMIN", "AFTER", "AG", "AGGREGATE", "ALIAS", "ALL", "ALLOCATE", "ALLOCATION", "ALLPARAMS", "ALTER", "ALWAYS", "AMP", "ANALYSIS", "AND", "ANSIDATE", "ANY", "ARCHIVE", "ARE", "ARGLPAREN", "ARRAY", "AS", "ASC", "ASCII", "ASENSITIVE", "ASIN", "ASINH", "ASSERTION", "ASSIGNMENT", "ASYMMETRIC", "AT", "ATAN", "ATAN2", "ATANH", "ATOMIC", "ATTR", "ATTRIBUTE", "ATTRIBUTES", "ATTRS", "AUTHORIZATION", "AVE", "AVERAGE", "AVG", "BEFORE", "BEGIN", "BERNOULLI", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOOLEAN", "BOTH", "BREADTH", "BT", "BUT", "BY", "BYTE", "BYTEINT", "BYTES", "C", "CALL", "CALLED", "CALLER", "CARDINALITY", "CASCADE", "CASCADED", "CASE", "CASE_N", "CASESPECIFIC", "CAST", "CATALOG", "CATALOG_NAME", "CD", "CEIL", "CEILING", "CHAIN", "CHANGERATE", "CHAR", "CHAR_LENGTH", "CHAR2HEXINT", "CHARACTER", "CHARACTER_LENGTH", "CHARACTER_SET_CATALOG", "CHARACTER_SET_NAME", "CHARACTER_SET_SCHEMA", "CHARACTERISTICS", "CHARACTERS", "CHARS", "CHARSET_COLL", "CHECK", "CHECKED", "CHECKPOINT", "CHECKSUM", "CLASS", "CLASS_ORIGIN", "CLIENT", "CLOB", "CLOSE", "CLUSTER", "CM", "COALESCE", "COBOL", "COLLATE", "COLLATION", "COLLATION_CATALOG", "COLLATION_NAME", "COLLATION_SCHEMA", "COLLECT", "COLUMN", "COLUMN_NAME", "COLUMNS", "COLUMNSPERINDEX", "COLUMNSPERJOININDEX", "COMMAND_FUNCTION", "COMMAND_FUNCTION_CODE", "COMMENT", "COMMIT", "COMMITTED", "COMPARABLE", "COMPARISON", "COMPILE", "COMPRESS", "CONDITION", "CONDITION_NUMBER", "CONNECT", "CONNECTION", "CONNECTION_NAME", "CONSTRAINT", "CONSTRAINT_CATALOG", "CONSTRAINT_NAME", "CONSTRAINT_SCHEMA", "CONSTRAINTS", "CONSTRUCTOR", "CONSUME", "CONTAINS", "CONTINUE", "CONVERT", "CONVERT_TABLE_HEADER", "CORR", "CORRESPONDING", "COS", "COSH", "COSTS", "COUNT", "COVAR_POP", "COVAR_SAMP", "CPP", "CPUTIME", "CPUTIMENORM", "CREATE", "CROSS", "CS", "CSUM", "CT", "CUBE", "CUME_DIST", "CURRENT", "CURRENT_DATE", "CURRENT_DEFAULT_TRANSFORM_GROUP", "CURRENT_PATH", "CURRENT_ROLE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_TRANSFORM_GROUP_FOR_TYPE", "CURRENT_USER", "CURSOR", "CURSOR_NAME", "CV", "CYCLE", "DATA", "DATABASE", "DATABLOCKSIZE", "DATE", "DATEFORM", "DATETIME_INTERVAL_CODE", "DATETIME_INTERVAL_PRECISION", "DAY", "DBC", "DEALLOCATE", "DEBUG", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DEFAULTS", "DEFERRABLE", "DEFERRED", "DEFINED", "DEFINER", "DEGREE", "DEGREES", "DEL", "DELETE", "DEMOGRAPHICS", "DENIALS", "DENSE_RANK", "DEPTH", "DEREF", "DERIVED", "DESC", "DESCRIBE", "DESCRIPTOR", "DETERMINISTIC", "DIAGNOSTIC", "DIAGNOSTICS", "DIGITS", "DISABLED", "DISCONNECT", "DISPATCH", "DISTINCT", "DO", "DOMAIN", "DOUBLE", "DR", "DROP", "DUAL", "DUMP", "DYNAMIC", "DYNAMIC_FUNCTION", "DYNAMIC_FUNCTION_CODE", "EACH", "EBCDIC", "ECHO", "ELAPSEDSEC", "ELAPSEDTIME", "ELEMENT", "ELSE", "ELSEIF", "ENABLED", "ENCRYPT", "END", "END-EXEC", "EQ", "EQUALS", "ERROR", "ERRORFILES", "ERRORS", "ERRORTABLES", "ESCAPE", "ET", "EVERY", "EXCEPT", "EXCEPTION", "EXCL", "EXCLUDE", "EXCLUDING", "EXCLUSIVE", "EXEC", "EXECUTE", "EXISTING", "EXISTS", "EXIT", "EXP", "EXPIRE", "EXPLAIN", "EXTERNAL", "EXTRACT", "FALLBACK", "FALSE", "FASTEXPORT", "FETCH", "FILTER", "FINAL", "FIRST", "FLOAT", "FLOOR", "FOLLOWING", "FOR", "FOREIGN", "FORMAT", "FORTRAN", "FOUND", "FREE", "FREESPACE", "FROM", "FULL", "FUNCTION", "FUSION", "G", "GE", "GENERAL", "GENERATED", "GET", "GIVE", "GLOBAL", "GO", "GOTO", "GRANT", "GRANTED", "GRAPHIC", "GROUP", "GROUPING", "GT", "HANDLER", "HASH", "HASHAMP", "HASHBAKAMP", "HASHBUCKET", "HASHROW", "HAVING", "HELP", "HIERARCHY", "HIGH", "HOLD", "HOST", "HOUR", "IDENTITY", "IF", "IFP", "IMMEDIATE", "IMPLEMENTATION", "IN", "INCLUDING", "INCONSISTENT", "INCREMENT", "INDEX", "INDEXESPERTABLE", "INDEXMAINTMODE", "INDICATOR", "INIT", "INITIALLY", "INITIATE", "INNER", "INOUT", "INPUT", "INS", "INSENSITIVE", "INSERT", "INSTANCE", "INSTANTIABLE", "INSTEAD", "INT", "INTEGER", "INTEGERDATE", "INTERFACE", "INTERNAL", "INTERSECT", "INTERSECTION", "INTERVAL", "INTO", "INVOKER", "IOCOUNT", "IS", "ISOLATION", "ITERATE", "JAR", "JAVA", "JIS_COLL", "JOIN", "JOURNAL", "K", "KANJI1", "KANJISJIS", "KBYTE", "KBYTES", "KEEP", "KEY", "KEY_MEMBER", "KEY_TYPE", "KILOBYTES", "KURTOSIS", "LANGUAGE", "LARGE", "LAST", "LATERAL", "LATIN", "LE", "LEADING", "LEAVE", "LEFT", "LENGTH", "LEVEL", "LIKE", "LIMIT", "LN", "LOADING", "LOCAL", "LOCALTIME", "LOCALTIMESTAMP", "LOCATOR", "LOCK", "LOCKEDUSEREXPIRE", "LOCKING", "LOG", "LOGGING", "LOGON", "LONG", "LOOP", "LOW", "LOWER", "LT", "M", "MACRO", "MAP", "MATCH", "MATCHED", "MAVG", "MAX", "MAXCHAR", "MAXIMUM", "MAXLOGONATTEMPTS", "MAXVALUE", "MCHARACTERS", "MDIFF", "MEDIUM", "MEMBER", "MERGE", "MESSAGE_LENGTH", "MESSAGE_OCTET_LENGTH", "MESSAGE_TEXT", "METHOD", "MIN", "MINCHAR", "MINDEX", "MINIMUM", "MINUS", "MINUTE", "MINVALUE", "MLINREG", "MLOAD", "MOD", "MODE", "MODIFIED", "MODIFIES", "MODIFY", "MODULE", "MONITOR", "MONRESOURCE", "MONSESSION", "MONTH", "MORE", "MSUBSTR", "MSUM", "MULTINATIONAL", "MULTISET", "MUMPS", "NAME", "NAMED", "NAMES", "NATIONAL", "NATURAL", "NCHAR", "NCLOB", "NE", "NESTING", "NEW", "NEW_TABLE", "NEXT", "NO", "NONE", "NONOPTCOST", "NONOPTINIT", "NORMALIZE", "NORMALIZED", "NOT", "NOWAIT", "NULL", "NULLABLE", "NULLIF", "NULLIFZERO", "NULLS", "NUMBER", "NUMERIC", "OA", "OBJECT", "OBJECTS", "OCTET_LENGTH", "OCTETS", "OF", "OFF", "OLD", "OLD_TABLE", "ON", "ONLINE", "ONLY", "OPEN", "OPTION", "OPTIONS", "OR", "ORDER", "ORDERED_ANALYTIC", "ORDERING", "ORDINALITY", "OTHERS", "OUT", "OUTER", "OUTPUT", "OVER", "OVERLAPS", "OVERLAY", "OVERLAYS", "OVERRIDE", "OVERRIDING", "PAD", "PARAMETER", "PARAMETER_MODE", "PARAMETER_NAME", "PARAMETER_ORDINAL_POSITION", "PARAMETER_SPECIFIC_CATALOG", "PARAMETER_SPECIFIC_NAME", "PARAMETER_SPECIFIC_SCHEMA", "PARAMID", "PARTIAL", "PARTITION", "PARTITIONED", "PARTITION#L1", "PARTITION#L2", "PARTITION#L3", "PARTITION#L4", "PARTITION#L5", "PARTITION#L6", "PARTITION#L7", "PARTITION#L8", "PARTITION#L9", "PARTITION#L10", "PARTITION#L11", "PARTITION#L12", "PARTITION#L13", "PARTITION#L14", "PARTITION#L15", "PASCAL", "PASSWORD", "PATH", "PERCENT", "PERCENT_RANK", "PERCENTILE_CONT", "PERCENTILE_DISC", "PERM", "PERMANENT", "PLACING", "PLI", "POSITION", "POWER", "PRECEDING", "PRECISION", "PREPARE", "PRESERVE", "PRIMARY", "PRINT", "PRIOR", "PRIVATE", "PRIVILEGES", "PROCEDURE", "PROFILE", "PROTECTED", "PROTECTION", "PUBLIC", "QUALIFIED", "QUALIFY", "QUANTILE", "QUEUE", "QUERY", "QUERY_BAND", "RADIANS", "RANDOM", "RANDOMIZED", "RANGE", "RANGE#L1", "RANGE#L2", "RANGE#L3", "RANGE#L4", "RANGE#L5", "RANGE#L6", "RANGE#L7", "RANGE#L8", "RANGE#L9", "RANGE#L10", "RANGE#L11", "RANGE#L12", "RANGE#L13", "RANGE#L14", "RANGE#L15", "RANGE_N", "RANK", "READ", "READS", "REAL", "RECALC", "RECURSIVE", "REF", "REFERENCES", "REFERENCING", "REGR_AVGX", "REGR_AVGY", "REGR_COUNT", "REGR_INTERCEPT", "REGR_R2", "REGR_SLOPE", "REGR_SXX", "REGR_SXY", "REGR_SYY", "RELATIVE", "RELEASE", "RENAME", "REPEAT", "REPEATABLE", "REPLACE", "REPLACEMENT", "REPLCONTROL", "REPLICATION", "REQUEST", "RESTART", "RESTORE", "RESTRICT", "RESTRICTWORDS", "RESULT", "RESUME", "RET", "RETAIN", "RETRIEVE", "RETURN", "RETURNED_CARDINALITY", "RETURNED_LENGTH", "RETURNED_OCTET_LENGTH", "RETURNED_SQLSTATE", "RETURNS", "REUSE", "REVALIDATE", "REVOKE", "RIGHT", "RIGHTS", "ROLE", "ROLLBACK", "ROLLFORWARD", "ROLLUP", "ROUTINE", "ROUTINE_CATALOG", "ROUTINE_NAME", "ROUTINE_SCHEMA", "ROW", "ROW_COUNT", "ROW_NUMBER", "ROWID", "ROWS", "RU", "SAMPLE", "SAMPLEID", "SAMPLES", "SAVEPOINT", "SCALE", "SCHEMA", "SCHEMA_NAME", "SCOPE", "SCOPE_CATALOG", "SCOPE_NAME", "SCOPE_SCHEMA", "SCROLL", "SEARCH", "SEARCHSPACE", "SECOND", "SECTION", "SECURITY", "SEED", "SEL", "SELECT", "SELF", "SENSITIVE", "SEQUENCE", "SERIALIZABLE", "SERVER_NAME", "SESSION", "SESSION_USER", "SET", "SETRESRATE", "SETS", "SETSESSRATE", "SHARE", "SHOW", "SIMILAR", "SIMPLE", "SIN", "SINH", "SIZE", "SKEW", "SMALLINT", "SOME", "SOUNDEX", "SOURCE", "SPACE", "SPECCHAR", "SPECIFIC", "SPECIFIC_NAME", "SPECIFICTYPE", "SPL", "SPOOL", "SQL", "SQLDATA", "SQLEXCEPTION", "SQLSTATE", "SQLTEXT", "SQLWARNING", "SQRT", "SR", "SS", "START", "STARTUP", "STAT", "STATE", "STATEMENT", "STATIC", "STATISTICS", "STATS", "STDDEV_POP", "STDDEV_SAMP", "STEPINFO", "STRING_CS", "STRUCTURE", "STYLE", "SUBCLASS_ORIGIN", "SUBLIST", "SUBMULTISET", "SUBSCRIBER", "SUBSTR", "SUBSTRING", "SUM", "SUMMARY", "SUMMARYONLY", "SUSPEND", "SYMMETRIC", "SYSTEM", "SYSTEM_USER", "SYSTEMTEST", "TABLE", "TABLE_NAME", "TABLESAMPLE", "TAN", "TANH", "TARGET", "TBL_CS", "TD_GENERAL", "TD_INTERNAL", "TEMPORARY", "TERMINATE", "TEXT", "THAN", "THEN", "THRESHOLD", "TIES", "TIME", "TIMESTAMP", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TITLE", "TO", "TOP", "TPA", "TOP_LEVEL_COUNT", "TRACE", "TRAILING", "TRANSACTION", "TRANSACTION_ACTIVE", "TRANSACTIONS_COMMITTED", "TRANSACTIONS_ROLLED_BACK", "TRANSFORM", "TRANSFORMS", "TRANSLATE", "TRANSLATE_CHK", "TRANSLATION", "TREAT", "TRIGGER", "TRIGGER_CATALOG", "TRIGGER_NAME", "TRIGGER_SCHEMA", "TRIM", "TRUE", "TYPE", "UC", "UDTCASTAS", "UDTCASTLPAREN", "UDTMETHOD", "UDTTYPE", "UDTUSAGE", "UESCAPE", "UNBOUNDED", "UNCOMMITTED", "UNDEFINED", "UNDER", "UNDO", "UNICODE", "UNION", "UNIQUE", "UNKNOWN", "UNNAMED", "UNNEST", "UNTIL", "UPD", "UPDATE", "UPPER", "UPPERCASE", "USAGE", "USE", "USER", "USER_DEFINED_TYPE_CATALOG", "USER_DEFINED_TYPE_CODE", "USER_DEFINED_TYPE_NAME", "USER_DEFINED_TYPE_SCHEMA", "USING", "VALUE", "VALUES", "VAR_POP", "VAR_SAMP", "VARBYTE", "VARCHAR", "VARGRAPHIC", "VARYING", "VIEW", "VOLATILE", "WAIT", "WARNING", "WHEN", "WHENEVER", "WHERE", "WHILE", "WIDTH_BUCKET", "WINDOW", "WITH", "WITHIN", "WITHOUT", "WORK", "WRITE", "YEAR", "ZEROIFNULL", "ZONE", "CTCONTROL", "EXPAND", "EXPANDING", "GLOP", "RESIGNAL", "SIGNAL", "UNTIL_CHANGED", "VARIANT_TYPE", "XMLPLAN" };
        SwisSQLUtils.oracleDateFormat = new String[] { "CC", "SCC", "SYYYY", "YYYY", "YEAR", "SYEAR", "YYY", "YY", "Y", "IYYY", "IY", "I", "Q", "MONTH", "MON", "MM", "RM", "WW", "IW", "W", "DDD", "DD", "J", "DAY", "DY", "D", "HH", "HH12", "HH24", "MI" };
        SwisSQLUtils.oracleTimeZonesArray = new String[] { "GMT", "CET", "CST", "CST6CDT", "CUBA", "EET", "EST", "EST5EDT", "EGYPT", "EIRE", "GB", "GB-EIRE", "GREENWICH", "HST", "HONGKONG", "ICELAND", "IRAN", "ISRAEL", "JAMAICA", "JAPAN", "KWAJALEIN", "LIBYA", "MET", "MST", "MST7MDT", "NZ", "NZ_CHAT", "NAVAJO", "PRC", "PST", "PST8PDT", "POLAND", "PORTUGAL", "ROC", "ROK", "SINGAPORE", "TURKEY", "UTC", "W_SU", "WET" };
        oracleTimeZones = new ArrayList((Collection<? extends E>)Arrays.asList(SwisSQLUtils.oracleTimeZonesArray));
        SwisSQLUtils.functionsReturningDate = new ArrayList((Collection<? extends E>)Arrays.asList("TO_DATE", "ADD_MONTHS", "LAST_DAY", "NEXT_DAY", "SYSDATE", "CURRENT_DATE"));
        SwisSQLUtils.functionsReturningTimestamp = new ArrayList((Collection<? extends E>)Arrays.asList("TO_TIMESTAMP", "SYSTIMESTAMP", "CURRENT_TIMESTAMP"));
        SwisSQLUtils.swissqlMessageList = new ArrayList();
    }
}
