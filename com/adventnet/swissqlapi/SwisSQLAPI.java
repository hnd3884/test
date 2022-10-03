package com.adventnet.swissqlapi;

import java.util.HashSet;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Collection;
import java.util.Set;
import com.adventnet.swissqlapi.sql.statement.select.SelectInvolvedTables;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.io.DataInputStream;
import java.io.File;
import java.util.ArrayList;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.sql.Connection;
import com.adventnet.swissqlapi.config.metadata.MetaDataProperties;
import java.sql.SQLException;
import java.util.Vector;
import com.adventnet.swissqlapi.util.database.MetaDataUtility;
import com.adventnet.swissqlapi.config.datatypes.DatatypeMapping;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.create.CreateQueryStatement;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.parser.ParseException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import com.adventnet.swissqlapi.util.misc.BuiltInFunctionDetails;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.HashMap;
import java.util.Hashtable;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.parser.ALLSQL;

public class SwisSQLAPI
{
    private ALLSQL vembuParser;
    private boolean constructed;
    private SwisSQLStatement currentSwisSQLStatement;
    private boolean reInitToBeDone;
    private Hashtable dbCumDatatypeMapping;
    private HashMap objectNames;
    public static boolean ANSIJOIN_ForOracle;
    public static Hashtable dataTypesFromMetaDataHT;
    public static Hashtable columnDatatypes;
    public static Hashtable tableColumnListMetadata;
    public static Hashtable targetDataTypesMetaDataHash;
    public static Hashtable identityMapping;
    public static Hashtable primaryKeyMetaData;
    public static final int GIVENSQL = 0;
    public static final int ORACLE = 1;
    public static final int MSSQLSERVER = 2;
    public static final int DB2 = 3;
    public static final int POSTGRESQL = 4;
    public static final int MYSQL = 5;
    public static final int INFORMIX = 6;
    public static final int SYBASE = 7;
    public static final int ANSISQL = 8;
    public static final int COMMON = 9;
    public static final int TIMESTEN = 10;
    public static final int NETEZZA = 11;
    public static final int TERADATA = 12;
    public static final int VECTORWISE = 13;
    public static boolean MSSQLSERVER_THETA;
    public static boolean convert_OracleThetaJOIN_To_ANSIJOIN;
    public static HashMap variableDatatypeMapping;
    public static UserObjectContext objectContext;
    private Hashtable dbCumDatatypeMappingFile;
    private Hashtable dbCumDatatypeMappingStream;
    public static boolean convertCaseToDecode;
    public static boolean quotedOracleIdentifier;
    public static boolean enableObjectMapping;
    public static boolean convertToTeradata;
    public static BuiltInFunctionDetails builtInFunctionDetails;
    public static boolean tozohodb;
    public static HashMap objectsOwnerName;
    public static HashMap targetDBMappedFunctionNames;
    public static String targetDBFunctionMappingFile;
    public boolean isAmazonRedShift;
    public boolean isMSAzure;
    public boolean isOracleLive;
    public boolean canUseIfFunction;
    public boolean canUseUDFFunctionsForNumeric;
    public boolean canUseUDFFunctionsForText;
    public boolean canUseUDFFunctionsForDateTime;
    public boolean canHandleStringLiteralsForNumeric;
    public boolean canHandleStringLiteralsForDateTime;
    public boolean canHandleNullsInsideINClause;
    public boolean canCastStringLiteralToText;
    public int removalOptionForOrderAndFetchClause;
    public boolean canUseDistinctFromForNullSafeEqualsOperator;
    public boolean canHandleHavingWithoutGroupBy;
    public boolean canUseUDFFunctionsForStrToDate;
    public boolean canHandleFunctionArgumentsCountMismatch;
    public static boolean truncateTableNameForDB2;
    public static boolean truncateTableNameForOracle;
    public static int truncateTableCount;
    public static int truncateIndexCount;
    public static int truncateConstraintCount;
    public static ThreadLocal involvedTablesTL;
    public static ThreadLocal removeLimitAndFetchClause;
    public static ThreadLocal indexPositionsForCastingTL;
    public static ThreadLocal holidayTable;
    public static Map fnWhiteListMap;
    public static ThreadLocal indexPositionsForNULLTL;
    
    public static void setMSSQLServerThetaConversion(final boolean flag) {
        SwisSQLAPI.MSSQLSERVER_THETA = flag;
    }
    
    public static void setQuotedOracleIdentifier(final boolean flag) {
        SwisSQLAPI.quotedOracleIdentifier = flag;
    }
    
    public SwisSQLAPI() {
        this.vembuParser = null;
        this.constructed = false;
        this.currentSwisSQLStatement = null;
        this.reInitToBeDone = true;
        this.dbCumDatatypeMapping = new Hashtable();
        this.objectNames = new HashMap();
        this.dbCumDatatypeMappingFile = new Hashtable();
        this.dbCumDatatypeMappingStream = new Hashtable();
        this.isAmazonRedShift = false;
        this.isMSAzure = false;
        this.isOracleLive = false;
        this.canUseIfFunction = false;
        this.canUseUDFFunctionsForNumeric = false;
        this.canUseUDFFunctionsForText = false;
        this.canUseUDFFunctionsForDateTime = false;
        this.canHandleStringLiteralsForNumeric = false;
        this.canHandleStringLiteralsForDateTime = false;
        this.canHandleNullsInsideINClause = false;
        this.canCastStringLiteralToText = false;
        this.removalOptionForOrderAndFetchClause = -1;
        this.canUseDistinctFromForNullSafeEqualsOperator = true;
        this.canHandleHavingWithoutGroupBy = false;
        this.canUseUDFFunctionsForStrToDate = false;
        this.canHandleFunctionArgumentsCountMismatch = false;
        this.resetStaticVariables();
    }
    
    public SwisSQLAPI(final String sql) {
        this.vembuParser = null;
        this.constructed = false;
        this.currentSwisSQLStatement = null;
        this.reInitToBeDone = true;
        this.dbCumDatatypeMapping = new Hashtable();
        this.objectNames = new HashMap();
        this.dbCumDatatypeMappingFile = new Hashtable();
        this.dbCumDatatypeMappingStream = new Hashtable();
        this.isAmazonRedShift = false;
        this.isMSAzure = false;
        this.isOracleLive = false;
        this.canUseIfFunction = false;
        this.canUseUDFFunctionsForNumeric = false;
        this.canUseUDFFunctionsForText = false;
        this.canUseUDFFunctionsForDateTime = false;
        this.canHandleStringLiteralsForNumeric = false;
        this.canHandleStringLiteralsForDateTime = false;
        this.canHandleNullsInsideINClause = false;
        this.canCastStringLiteralToText = false;
        this.removalOptionForOrderAndFetchClause = -1;
        this.canUseDistinctFromForNullSafeEqualsOperator = true;
        this.canHandleHavingWithoutGroupBy = false;
        this.canUseUDFFunctionsForStrToDate = false;
        this.canHandleFunctionArgumentsCountMismatch = false;
        if (!this.constructed) {
            this.vembuParser = new ALLSQL(new StringReader(sql));
            this.reInitToBeDone = false;
            this.constructed = true;
        }
        else {
            this.vembuParser.ReInit(new StringReader(sql));
            this.reInitToBeDone = false;
        }
        this.resetStaticVariables();
    }
    
    public SwisSQLAPI(final InputStream stream) {
        this.vembuParser = null;
        this.constructed = false;
        this.currentSwisSQLStatement = null;
        this.reInitToBeDone = true;
        this.dbCumDatatypeMapping = new Hashtable();
        this.objectNames = new HashMap();
        this.dbCumDatatypeMappingFile = new Hashtable();
        this.dbCumDatatypeMappingStream = new Hashtable();
        this.isAmazonRedShift = false;
        this.isMSAzure = false;
        this.isOracleLive = false;
        this.canUseIfFunction = false;
        this.canUseUDFFunctionsForNumeric = false;
        this.canUseUDFFunctionsForText = false;
        this.canUseUDFFunctionsForDateTime = false;
        this.canHandleStringLiteralsForNumeric = false;
        this.canHandleStringLiteralsForDateTime = false;
        this.canHandleNullsInsideINClause = false;
        this.canCastStringLiteralToText = false;
        this.removalOptionForOrderAndFetchClause = -1;
        this.canUseDistinctFromForNullSafeEqualsOperator = true;
        this.canHandleHavingWithoutGroupBy = false;
        this.canUseUDFFunctionsForStrToDate = false;
        this.canHandleFunctionArgumentsCountMismatch = false;
        if (!this.constructed) {
            this.vembuParser = new ALLSQL(stream);
            this.reInitToBeDone = false;
            this.constructed = true;
        }
        else {
            this.vembuParser.ReInit(stream);
            this.reInitToBeDone = false;
        }
        this.resetStaticVariables();
    }
    
    public SwisSQLAPI(final Reader stream) {
        this.vembuParser = null;
        this.constructed = false;
        this.currentSwisSQLStatement = null;
        this.reInitToBeDone = true;
        this.dbCumDatatypeMapping = new Hashtable();
        this.objectNames = new HashMap();
        this.dbCumDatatypeMappingFile = new Hashtable();
        this.dbCumDatatypeMappingStream = new Hashtable();
        this.isAmazonRedShift = false;
        this.isMSAzure = false;
        this.isOracleLive = false;
        this.canUseIfFunction = false;
        this.canUseUDFFunctionsForNumeric = false;
        this.canUseUDFFunctionsForText = false;
        this.canUseUDFFunctionsForDateTime = false;
        this.canHandleStringLiteralsForNumeric = false;
        this.canHandleStringLiteralsForDateTime = false;
        this.canHandleNullsInsideINClause = false;
        this.canCastStringLiteralToText = false;
        this.removalOptionForOrderAndFetchClause = -1;
        this.canUseDistinctFromForNullSafeEqualsOperator = true;
        this.canHandleHavingWithoutGroupBy = false;
        this.canUseUDFFunctionsForStrToDate = false;
        this.canHandleFunctionArgumentsCountMismatch = false;
        if (!this.constructed) {
            this.vembuParser = new ALLSQL(stream);
            this.reInitToBeDone = false;
            this.constructed = true;
        }
        else {
            this.vembuParser.ReInit(stream);
            this.reInitToBeDone = false;
        }
        this.resetStaticVariables();
    }
    
    public HashMap getObjectNames() {
        return this.objectNames;
    }
    
    public void setAmazonRedShiftFlag(final boolean isAmazonRedShift) {
        this.isAmazonRedShift = isAmazonRedShift;
    }
    
    public void setMSAzureFlag(final boolean isMSAzure) {
        this.isMSAzure = isMSAzure;
    }
    
    public void setOracleLiveFlag(final boolean isOracleLive) {
        this.isOracleLive = isOracleLive;
    }
    
    public void setCanUseIfFunctionForCaseWhenExp(final boolean canUseIfFunction) {
        this.canUseIfFunction = canUseIfFunction;
    }
    
    public void setCanUseUDFFunctionsForNumeric(final boolean canUseUDFFunction) {
        this.canUseUDFFunctionsForNumeric = canUseUDFFunction;
    }
    
    public void setCanUseUDFFunctionsForText(final boolean canUseUDFFunction) {
        this.canUseUDFFunctionsForText = canUseUDFFunction;
    }
    
    public void setCanUseUDFFunctionsForDateTime(final boolean canUseUDFFunction) {
        this.canUseUDFFunctionsForDateTime = canUseUDFFunction;
    }
    
    public void setCanHandleStringLiteralsForNumeric(final boolean canHandleStringLiteralsForNumeric) {
        this.canHandleStringLiteralsForNumeric = canHandleStringLiteralsForNumeric;
    }
    
    public void setCanHandleStringLiteralsForDateTime(final boolean canHandleStringLiteralsForDateTime) {
        this.canHandleStringLiteralsForDateTime = canHandleStringLiteralsForDateTime;
    }
    
    public void setCanHandleNullsInsideINClause(final boolean canHandleNullsInsideINClause) {
        this.canHandleNullsInsideINClause = canHandleNullsInsideINClause;
    }
    
    public void setCanCastStringLiteralToText(final boolean canCastStringLiteralToText) {
        this.canCastStringLiteralToText = canCastStringLiteralToText;
    }
    
    public void setRemovalOptionForOrderAndFetchClauses(final int option) {
        this.removalOptionForOrderAndFetchClause = option;
    }
    
    public void setCanUseDistinctFromForNullSafeEqualsOperator(final boolean canUseDistinctFromForNullSafeEqualsOperator) {
        this.canUseDistinctFromForNullSafeEqualsOperator = canUseDistinctFromForNullSafeEqualsOperator;
    }
    
    public void setCanHandleHavingWithoutGroupBy(final boolean canHandleHavingWithoutGroupBy) {
        this.canHandleHavingWithoutGroupBy = canHandleHavingWithoutGroupBy;
    }
    
    public void setCanUseUDFFunctionsForStrToDate(final boolean canUseUDFFunctionsForStrToDate) {
        this.canUseUDFFunctionsForStrToDate = canUseUDFFunctionsForStrToDate;
    }
    
    public void setCanHandleFunctionArgumentsCountMismatch(final boolean canHandleFnArgCount) {
        this.canHandleFunctionArgumentsCountMismatch = canHandleFnArgCount;
    }
    
    public synchronized void setSQLString(String sql) {
        if (sql != null && sql.trim().startsWith("(") && sql.trim().endsWith(")")) {
            sql = sql.trim().substring(1, sql.trim().length() - 1);
        }
        this.setFormulaOrSQLString(sql);
    }
    
    public synchronized void setFormulaString(final String sql) {
        this.setFormulaOrSQLString(sql);
    }
    
    public synchronized void setFormulaOrSQLString(final String sql) {
        if (!this.constructed) {
            this.vembuParser = new ALLSQL(new StringReader(sql));
            this.reInitToBeDone = false;
            this.constructed = true;
        }
        else {
            this.vembuParser.ReInit(new StringReader(sql));
            this.reInitToBeDone = false;
        }
        this.resetStaticVariables();
    }
    
    public synchronized void setSQLInputStream(final InputStream stream) {
        if (!this.constructed) {
            this.vembuParser = new ALLSQL(stream);
            this.reInitToBeDone = false;
            this.constructed = true;
        }
        else {
            this.vembuParser.ReInit(stream);
            this.reInitToBeDone = false;
        }
        this.resetStaticVariables();
    }
    
    public synchronized void setSQLReader(final Reader stream) {
        if (!this.constructed) {
            this.vembuParser = new ALLSQL(stream);
            this.reInitToBeDone = false;
            this.constructed = true;
        }
        else {
            this.vembuParser.ReInit(stream);
            this.reInitToBeDone = false;
        }
        this.resetStaticVariables();
    }
    
    public synchronized String convert(final int dialect) throws ParseException, ConvertException {
        return this.convert(dialect, false);
    }
    
    public synchronized String convert(final int dialect, final boolean enable_indent) throws ParseException, ConvertException {
        return this.convert(dialect, enable_indent, false);
    }
    
    public synchronized String convert(final int dialect, final boolean enable_indent, final boolean meta) throws ParseException, ConvertException {
        String s_sql = null;
        if (!this.reInitToBeDone) {
            this.currentSwisSQLStatement = this.vembuParser.CompilationUnit();
            this.reInitToBeDone = true;
        }
        if (this.currentSwisSQLStatement == null) {
            return "";
        }
        boolean setDatatypeMapping = false;
        if (this.currentSwisSQLStatement instanceof CreateQueryStatement || this.currentSwisSQLStatement instanceof SelectQueryStatement) {
            setDatatypeMapping = true;
            if (this.currentSwisSQLStatement instanceof SelectQueryStatement) {
                ((SelectQueryStatement)this.currentSwisSQLStatement).setTopLevel(true);
                ((SelectQueryStatement)this.currentSwisSQLStatement).setReportsMeta(meta);
            }
        }
        if (SwisSQLAPI.targetDBFunctionMappingFile != null) {
            this.loadFunctionNameMapping(SwisSQLAPI.targetDBFunctionMappingFile);
        }
        switch (dialect) {
            case 0: {
                s_sql = this.currentSwisSQLStatement.toString();
                break;
            }
            case 1: {
                if (setDatatypeMapping) {
                    this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 1);
                }
                if (this.isOracleLive && this.currentSwisSQLStatement instanceof SelectQueryStatement) {
                    ((SelectQueryStatement)this.currentSwisSQLStatement).setOracleLiveFlag(this.isOracleLive);
                }
                s_sql = this.currentSwisSQLStatement.toOracleString();
                break;
            }
            case 2: {
                if (setDatatypeMapping) {
                    this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 2);
                }
                if (this.isMSAzure && this.currentSwisSQLStatement instanceof SelectQueryStatement) {
                    ((SelectQueryStatement)this.currentSwisSQLStatement).setMSAzureFlag(this.isMSAzure);
                }
                s_sql = this.currentSwisSQLStatement.toMSSQLServerString();
                break;
            }
            case 3: {
                if (setDatatypeMapping) {
                    this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 3);
                }
                s_sql = this.currentSwisSQLStatement.toDB2String();
                break;
            }
            case 4: {
                if (setDatatypeMapping) {
                    this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 4);
                }
                if (this.currentSwisSQLStatement instanceof SelectQueryStatement) {
                    final SelectQueryStatement sqs = (SelectQueryStatement)this.currentSwisSQLStatement;
                    if (this.isAmazonRedShift) {
                        sqs.setAmazonRedShiftFlag(this.isAmazonRedShift);
                    }
                    if (this.canUseIfFunction) {
                        sqs.setCanUseIFFunctionForPGCaseWhenExp(this.canUseIfFunction);
                    }
                    if (this.canUseUDFFunctionsForNumeric) {
                        sqs.setCanUseUDFFunctionsForNumeric(this.canUseUDFFunctionsForNumeric);
                    }
                    if (this.canUseUDFFunctionsForText) {
                        sqs.setCanUseUDFFunctionsForText(this.canUseUDFFunctionsForText);
                    }
                    if (this.canUseUDFFunctionsForDateTime) {
                        sqs.setCanUseUDFFunctionsForDateTime(this.canUseUDFFunctionsForDateTime);
                    }
                    if (this.canHandleStringLiteralsForNumeric) {
                        sqs.setCanHandleStringLiteralsForNumeric(this.canHandleStringLiteralsForNumeric);
                    }
                    if (this.canHandleStringLiteralsForDateTime) {
                        sqs.setCanHandleStringLiteralsForDateTime(this.canHandleStringLiteralsForDateTime);
                    }
                    if (this.canCastStringLiteralToText) {
                        sqs.setCanCastStringLiteralToText(this.canCastStringLiteralToText);
                    }
                    if (this.removalOptionForOrderAndFetchClause >= 0) {
                        sqs.setRemovalOptionForOrderAndFetchClauses(this.removalOptionForOrderAndFetchClause);
                    }
                    sqs.setCanHandleHavingWithoutGroupBy(this.canHandleHavingWithoutGroupBy);
                    sqs.setCanUseUDFFunctionsForStrToDate(this.canUseUDFFunctionsForStrToDate);
                }
                clearIndexPositions();
                clearNULLIndexPositions();
                s_sql = this.currentSwisSQLStatement.toPostgreSQLString();
                removeIndexPositionsTL();
                removeNULLIndexPositionsTL();
                break;
            }
            case 5: {
                if (setDatatypeMapping) {
                    this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 5);
                }
                if (this.currentSwisSQLStatement instanceof SelectQueryStatement) {
                    final SelectQueryStatement sqs = (SelectQueryStatement)this.currentSwisSQLStatement;
                    sqs.setCanHandleFunctionArgumentsCountMismatch(this.canHandleFunctionArgumentsCountMismatch);
                }
                s_sql = this.currentSwisSQLStatement.toMySQLString();
                break;
            }
            case 6: {
                if (setDatatypeMapping) {
                    this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 6);
                }
                s_sql = this.currentSwisSQLStatement.toInformixString();
                break;
            }
            case 7: {
                if (setDatatypeMapping) {
                    this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 7);
                }
                s_sql = this.currentSwisSQLStatement.toSybaseString();
                break;
            }
            case 8: {
                if (setDatatypeMapping) {
                    this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 8);
                }
                s_sql = this.currentSwisSQLStatement.toANSIString();
                break;
            }
            case 10: {
                if (setDatatypeMapping) {
                    this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 10);
                }
                s_sql = this.currentSwisSQLStatement.toTimesTenString();
                break;
            }
            case 11: {
                if (setDatatypeMapping) {
                    this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 11);
                }
                s_sql = this.currentSwisSQLStatement.toNetezzaString();
                break;
            }
            case 12: {
                if (setDatatypeMapping) {
                    this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 12);
                }
                s_sql = this.currentSwisSQLStatement.toTeradataString();
                break;
            }
            case 13: {
                if (setDatatypeMapping) {
                    this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 13);
                }
                if (this.currentSwisSQLStatement instanceof SelectQueryStatement) {
                    final SelectQueryStatement sqs = (SelectQueryStatement)this.currentSwisSQLStatement;
                    if (this.canHandleStringLiteralsForNumeric) {
                        sqs.setCanHandleStringLiteralsForNumeric(this.canHandleStringLiteralsForNumeric);
                    }
                    if (this.canHandleStringLiteralsForDateTime) {
                        sqs.setCanHandleStringLiteralsForDateTime(this.canHandleStringLiteralsForDateTime);
                    }
                    if (this.canHandleNullsInsideINClause) {
                        sqs.setCanHandleNullsInsideINClause(this.canHandleNullsInsideINClause);
                    }
                    if (this.canCastStringLiteralToText) {
                        sqs.setCanCastStringLiteralToText(this.canCastStringLiteralToText);
                    }
                    if (this.removalOptionForOrderAndFetchClause >= 0) {
                        sqs.setRemovalOptionForOrderAndFetchClauses(this.removalOptionForOrderAndFetchClause);
                    }
                    sqs.setCanUseDistinctFromForNullSafeEqualsOperator(this.canUseDistinctFromForNullSafeEqualsOperator);
                    sqs.setCanHandleHavingWithoutGroupBy(this.canHandleHavingWithoutGroupBy);
                }
                clearIndexPositions();
                clearNULLIndexPositions();
                s_sql = this.currentSwisSQLStatement.toVectorWiseString();
                removeIndexPositionsTL();
                removeNULLIndexPositionsTL();
                break;
            }
            default: {
                s_sql = "Not Supported Dialect";
                break;
            }
        }
        if (enable_indent) {
            return s_sql;
        }
        return this.currentSwisSQLStatement.removeIndent(s_sql);
    }
    
    private void setDatatypeMappingForSQLDialect(final SwisSQLStatement sqs, final int dialect) {
        DatatypeMapping mapping = this.dbCumDatatypeMapping.get(new Integer(dialect));
        if (mapping == null) {
            mapping = this.dbCumDatatypeMapping.get(new Integer(9));
        }
        if (sqs instanceof CreateQueryStatement) {
            ((CreateQueryStatement)sqs).setDatatypeMapping(mapping);
        }
        else if (sqs instanceof SelectQueryStatement) {
            ((SelectQueryStatement)sqs).setDatatypeMapping(mapping);
        }
    }
    
    public synchronized SwisSQLStatement parse() throws ParseException, ConvertException {
        final SwisSQLStatement sss = this.vembuParser.CompilationUnit();
        this.objectNames = this.vembuParser.getObjectNames();
        return sss;
    }
    
    public synchronized SwisSQLStatement parseFormulas() throws ParseException, ConvertException {
        final SwisSQLStatement sss = this.vembuParser.CompilationUnitForFormulas();
        this.objectNames = this.vembuParser.getObjectNames();
        return sss;
    }
    
    private void resetStaticVariables() {
        SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
        SelectQueryStatement.beautyTabCount = 0;
        SelectQueryStatement.getListOfWithStatements().clear();
        this.resetTruncateVariables();
    }
    
    private void resetTruncateVariables() {
        if (SwisSQLAPI.truncateTableCount > 99) {
            SwisSQLAPI.truncateTableCount = 0;
        }
        if (SwisSQLAPI.truncateIndexCount > 99) {
            SwisSQLAPI.truncateIndexCount = 0;
        }
        if (SwisSQLAPI.truncateConstraintCount > 99) {
            SwisSQLAPI.truncateConstraintCount = 0;
        }
    }
    
    public void getMetaData() {
        try {
            final MetaDataUtility mUtil = new MetaDataUtility();
            final Vector outputStrings = new Vector();
            mUtil.getMetaData(outputStrings);
            this.loadMetaData(mUtil.getDestinationFile());
        }
        catch (final SQLException sqle) {
            System.out.println(" Error Code : " + sqle.getErrorCode() + ". Please verify the Connection URL. Proceeding without loading metdata...");
        }
        catch (final Exception e) {
            System.out.println(" Problem in fetching/loading metadata. Proceeding without loading metdata...");
        }
    }
    
    public void getMetaData(final MetaDataProperties property) {
        try {
            final MetaDataUtility mUtil = new MetaDataUtility(property);
            final Vector outputStrings = new Vector();
            mUtil.getMetaData(outputStrings);
            this.loadMetaData(mUtil.getDestinationFile());
        }
        catch (final SQLException sqle) {
            System.out.println(" Error Code : " + sqle.getErrorCode() + ". Please verify the Connection URL. Proceeding without loading metdata...");
        }
        catch (final Exception e) {
            System.out.println(" Problem in fetching/loading metadata. Proceeding without loading metdata...");
        }
    }
    
    public void getMetaData(final Connection con, final MetaDataProperties property) {
        try {
            final MetaDataUtility mUtil = new MetaDataUtility(con, property);
            final Vector outputStrings = new Vector();
            mUtil.getMetaData(outputStrings);
            this.loadMetaData(mUtil.getDestinationFile());
        }
        catch (final SQLException sqle) {
            System.out.println(" Error Code : " + sqle.getErrorCode() + ". Please verify the Connection URL. Proceeding without loading metdata...");
            sqle.printStackTrace();
        }
        catch (final Exception e) {
            System.out.println(" Problem in fetching/loading metadata. Proceeding without loading metdata...");
        }
    }
    
    public void loadMetaData(final String fileName) {
        try {
            final FileInputStream fis = new FileInputStream(fileName);
            final InputStreamReader isr = new InputStreamReader(fis);
            this.loadMetaData(isr);
            isr.close();
            fis.close();
        }
        catch (final FileNotFoundException fnfe) {
            System.out.println(" LoadMetaData : File not found " + fileName + ". Proceeding with default handling...");
        }
        catch (final IOException ioe) {
            System.out.println(" LoadMetaData : GetMetaData yet to be done. Proceeding with default handling...");
        }
        catch (final Exception e) {
            System.out.println(" LoadMetaData : GetMetaData yet to be done. Proceeding with default handling...");
        }
    }
    
    public void loadMetaData(final InputStreamReader isr) throws IOException {
        SwisSQLAPI.dataTypesFromMetaDataHT = new Hashtable();
        SwisSQLAPI.tableColumnListMetadata = new Hashtable();
        SwisSQLAPI.primaryKeyMetaData = new Hashtable();
        final BufferedReader br = new BufferedReader(isr);
        for (String metadataString = br.readLine(); metadataString != null; metadataString = br.readLine()) {
            final String tablename = StringFunctions.getLastStrToken(metadataString, "=:").trim();
            metadataString = br.readLine();
            final String columnname = StringFunctions.getLastStrToken(metadataString, "=:").trim();
            metadataString = br.readLine();
            final String orgTypename = StringFunctions.getLastStrToken(metadataString, "=:").toLowerCase().trim();
            metadataString = br.readLine();
            String primaryKey = null;
            if (metadataString != null) {
                primaryKey = StringFunctions.getLastStrToken(metadataString, "=:").trim();
            }
            Hashtable orgTemp = new Hashtable();
            ArrayList columnList = new ArrayList();
            if (SwisSQLAPI.dataTypesFromMetaDataHT.containsKey(tablename)) {
                orgTemp = SwisSQLAPI.dataTypesFromMetaDataHT.get(tablename);
                orgTemp.put(columnname, orgTypename);
                columnList = SwisSQLAPI.tableColumnListMetadata.get(tablename);
                columnList.add(columnname);
            }
            else {
                orgTemp.put(columnname, orgTypename);
                SwisSQLAPI.dataTypesFromMetaDataHT.put(tablename, orgTemp);
                columnList.add(columnname);
                SwisSQLAPI.tableColumnListMetadata.put(tablename, columnList);
            }
            if (primaryKey != null && primaryKey.equals("1")) {
                if (!SwisSQLAPI.primaryKeyMetaData.containsKey(tablename)) {
                    final ArrayList tempCols = new ArrayList();
                    tempCols.add(columnname);
                    SwisSQLAPI.primaryKeyMetaData.put(tablename, tempCols);
                }
                else {
                    final ArrayList tempCols = SwisSQLAPI.primaryKeyMetaData.get(tablename);
                    if (!tempCols.contains(columnname)) {
                        tempCols.add(columnname);
                    }
                }
            }
            if (metadataString != null && metadataString.indexOf("PRIMARY_KEY=:") != -1) {}
        }
    }
    
    public void setDatatypeMapping(final int toDB, final DatatypeMapping mapping) {
        this.dbCumDatatypeMapping.put(new Integer(toDB), mapping);
    }
    
    public void setDatatypeMapping(final int toDB, final String datatypeMappingFileName) {
        this.dbCumDatatypeMappingFile.put(new Integer(toDB), datatypeMappingFileName);
        try {
            this.setDatatypeMappingWithMappingObject(new Integer(toDB));
        }
        catch (final IOException ioe) {
            System.out.println(" Problem in loading datatype mapping from a file/Input stream. Proceeding with default handling ... ");
        }
        catch (final Exception e) {
            System.out.println(" Problem in loading datatype mapping from a file/Input stream. Proceeding with default handling ...");
            e.printStackTrace();
        }
    }
    
    public void setDatatypeMapping(final int toDB, final InputStreamReader isr) {
        this.dbCumDatatypeMappingStream.put(new Integer(toDB), isr);
        try {
            this.setDatatypeMappingWithMappingObject(new Integer(toDB));
        }
        catch (final IOException ioe) {
            System.out.println(" Problem in loading datatype mapping from a file/Input stream. Proceeding with default handling ... ");
        }
        catch (final Exception e) {
            System.out.println(" Problem in loading datatype mapping from a file/Input stream. Proceeding with default handling ...");
            e.printStackTrace();
        }
    }
    
    public static void setObjectsOwnerName(final int targetDatabase, final String ownerName) {
        SwisSQLAPI.objectsOwnerName.put(new Integer(targetDatabase), ownerName);
    }
    
    public static void setIdentityMapping(final String fileName) {
        try {
            final File f = new File(fileName);
            final FileInputStream fstream = new FileInputStream(f);
            final DataInputStream in = new DataInputStream(fstream);
            final InputStreamReader isr = new InputStreamReader(in);
            setIdentityMapping(isr);
            isr.close();
            fstream.close();
            in.close();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void setIdentityMapping(final InputStreamReader isr) {
        final BufferedReader br = new BufferedReader(isr);
        try {
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (!strLine.startsWith("#") && strLine.indexOf("=") != -1 && strLine.indexOf(".") != -1 && !strLine.startsWith("/*") && !strLine.startsWith("--")) {
                    final String[] datatype = strLine.trim().split("=");
                    final String temp = datatype[0].trim().toString();
                    final String identity = datatype[1].trim().toString();
                    if (temp.indexOf(".") == -1) {
                        continue;
                    }
                    SwisSQLAPI.identityMapping.put(temp, identity);
                }
            }
            br.close();
        }
        catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void setDatatypeMappingWithMappingObject(final Integer toDB) throws IOException, Exception {
        if (this.dbCumDatatypeMappingStream.size() > 0) {
            final Enumeration enum1 = this.dbCumDatatypeMappingStream.keys();
            if (enum1 != null) {
                while (enum1.hasMoreElements()) {
                    final Object obj = enum1.nextElement();
                    if (obj.equals(toDB)) {
                        final BufferedReader br = new BufferedReader(this.dbCumDatatypeMappingStream.get(obj));
                        DatatypeMapping mapping = null;
                        if (br != null) {
                            String mappingString = br.readLine();
                            mapping = new DatatypeMapping();
                            while (mappingString != null) {
                                if (mappingString.indexOf("=") != -1 && !mappingString.trim().startsWith("#")) {
                                    final StringTokenizer stringTokenizer = new StringTokenizer(mappingString, "=");
                                    final String tabCol = stringTokenizer.nextToken().trim();
                                    final String mapped = stringTokenizer.nextToken().trim();
                                    if (tabCol.indexOf(".") == -1) {
                                        mapping.addGlobalDatatypeMapping(tabCol, mapped);
                                    }
                                    else {
                                        final StringTokenizer tabCols = new StringTokenizer(tabCol, ".");
                                        final String tab = tabCols.nextToken().trim();
                                        final String col = tabCols.nextToken().trim();
                                        mapping.addTableSpecificDatatypeMapping(tab, col, mapped);
                                    }
                                }
                                mappingString = br.readLine();
                            }
                            br.close();
                        }
                        if (this.dbCumDatatypeMapping.containsKey(obj)) {
                            this.dbCumDatatypeMapping.remove(obj);
                        }
                        this.dbCumDatatypeMapping.put(obj, mapping);
                    }
                }
            }
        }
        if (this.dbCumDatatypeMappingFile.size() > 0) {
            final Enumeration enum1 = this.dbCumDatatypeMappingFile.keys();
            if (enum1 != null) {
                while (enum1.hasMoreElements()) {
                    final DatatypeMapping mapping2 = new DatatypeMapping();
                    final Object obj2 = enum1.nextElement();
                    if (obj2.equals(toDB)) {
                        final String mappingFileName = this.dbCumDatatypeMappingFile.get(obj2).toString();
                        final File file = new File(mappingFileName);
                        if (file.exists()) {
                            final FileInputStream str = new FileInputStream(file);
                            final Properties props = new Properties();
                            props.load(str);
                            final Enumeration enumProp = props.keys();
                            if (enumProp == null) {
                                continue;
                            }
                            while (enumProp.hasMoreElements()) {
                                final String key = enumProp.nextElement();
                                final String val = ((Hashtable<K, String>)props).get(key);
                                if (key.indexOf(".") == -1) {
                                    mapping2.addGlobalDatatypeMapping(key, val);
                                }
                                else {
                                    final StringTokenizer tabCol2 = new StringTokenizer(key, ".");
                                    final String tab2 = tabCol2.nextToken().trim();
                                    final String col2 = tabCol2.nextToken().trim();
                                    mapping2.addTableSpecificDatatypeMapping(tab2, col2, val);
                                }
                            }
                            if (this.dbCumDatatypeMapping.containsKey(obj2)) {
                                this.dbCumDatatypeMapping.remove(obj2);
                            }
                            this.dbCumDatatypeMapping.put(obj2, mapping2);
                        }
                        else {
                            System.out.println(mappingFileName + " file is not found ...");
                        }
                    }
                }
            }
        }
    }
    
    public void loadObjectNameMapping(final String objectNameMappingFileName) {
        try {
            final FileInputStream fis = new FileInputStream(objectNameMappingFileName);
            final InputStreamReader isr = new InputStreamReader(fis);
            this.loadObjectNameMapping(isr);
            isr.close();
            fis.close();
        }
        catch (final FileNotFoundException fnfe) {
            System.out.println(" objectName mapping file not found " + objectNameMappingFileName + ". Proceeding with default handling...");
        }
        catch (final Exception e) {
            System.out.println(" Exception in loading the object name mapping . Proceeding with default handling...");
        }
    }
    
    public void loadObjectNameMapping(final InputStreamReader objectNameMappingStreamReader) {
        try {
            final BufferedReader br = new BufferedReader(objectNameMappingStreamReader);
            for (String mappingString = br.readLine(); mappingString != null; mappingString = br.readLine()) {
                final StringTokenizer stringTokenizer = new StringTokenizer(mappingString, "=:");
                String sourceObjName = "";
                String targetObjName = "";
                if (stringTokenizer.hasMoreTokens()) {
                    sourceObjName = stringTokenizer.nextToken().trim();
                    if (stringTokenizer.hasMoreTokens()) {
                        targetObjName = stringTokenizer.nextToken().trim();
                    }
                    SwisSQLUtils.objectNameMapping.put(sourceObjName, targetObjName);
                }
            }
        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public void writeObjectNameMappingToFile(final String objectNameMappingFileName) {
        if (SwisSQLUtils.objectNameMapping.size() == 0) {
            return;
        }
        final File conf = new File(objectNameMappingFileName);
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(conf);
            bw = new BufferedWriter(fw);
            final Enumeration keysEnum = SwisSQLUtils.objectNameMapping.keys();
            final Enumeration valuesEnum = SwisSQLUtils.objectNameMapping.elements();
            while (keysEnum.hasMoreElements() && valuesEnum.hasMoreElements()) {
                final String mapping = keysEnum.nextElement().toString() + ":=" + valuesEnum.nextElement().toString();
                bw.write(mapping);
                bw.newLine();
            }
        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
            try {
                if (bw != null) {
                    bw.close();
                }
                if (fw != null) {
                    fw.close();
                }
            }
            catch (final IOException ioe) {
                ioe.printStackTrace();
            }
        }
        finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (fw != null) {
                    fw.close();
                }
            }
            catch (final IOException ioe2) {
                ioe2.printStackTrace();
            }
        }
    }
    
    public SwisSQLStatement getCurrentSwisSQLStatement() {
        return this.currentSwisSQLStatement;
    }
    
    public void loadColumnDatatype(final String fileName) throws IOException {
        try {
            final FileInputStream fis = new FileInputStream(fileName);
            final InputStreamReader isr = new InputStreamReader(fis);
            this.loadColumnDatatype(isr);
            isr.close();
            fis.close();
        }
        catch (final FileNotFoundException fnfe) {
            System.out.println(" loadColumnDatatype : File not found " + fileName + ". Proceeding with default handling...");
        }
        catch (final IOException ioe) {
            System.out.println(" loadColumnDatatype : IOException. Proceeding with default handling...");
        }
        catch (final Exception e) {
            System.out.println(" loadColumnDatatype : Exception in loading column datatypes. Proceeding with default handling...");
        }
    }
    
    public void loadColumnDatatype(final InputStreamReader isr) throws IOException {
        SwisSQLAPI.columnDatatypes = new Hashtable();
        final BufferedReader br = new BufferedReader(isr);
        for (String metadataString = br.readLine(); metadataString != null; metadataString = br.readLine()) {
            final String[] split = metadataString.split("=:");
            final String columnname = split[0];
            final String datatype = split[1];
            SwisSQLAPI.columnDatatypes.put(columnname, datatype);
        }
    }
    
    public static void setTargetDBFunctionMappingFile(final String filename) {
        SwisSQLAPI.targetDBFunctionMappingFile = filename;
    }
    
    public static void addFunctionNameMapping(final String originalFunctionName, final String mappedFunctionName) {
        SwisSQLAPI.targetDBMappedFunctionNames.put(originalFunctionName.trim().toUpperCase(), mappedFunctionName.trim());
    }
    
    public void loadFunctionNameMapping(final String filename) {
        try {
            final FileInputStream functionMap = new FileInputStream(filename);
            final InputStreamReader isr = new InputStreamReader(functionMap);
            this.loadFunctionNameMapping(isr);
            isr.close();
            functionMap.close();
        }
        catch (final FileNotFoundException fnf) {
            System.out.println("LoadFunctionMapping : File " + filename + " Not Found : Proceeding with Default configurations");
        }
        catch (final IOException ioe) {
            System.out.println("LoadFunctionMapping : IO Exception Occured : Proceeding with default configurations");
        }
        catch (final Exception e) {
            System.out.println("LoadFunctionMapping : Exception while loading function mappings" + e.getMessage());
        }
    }
    
    public void loadFunctionNameMapping(final InputStreamReader isr) throws IOException {
        SwisSQLAPI.targetDBMappedFunctionNames = new HashMap();
        final BufferedReader reader = new BufferedReader(isr);
        for (String functionMap = reader.readLine(); functionMap != null; functionMap = reader.readLine()) {
            if (!functionMap.trim().equals("") && !functionMap.startsWith("#")) {
                final String[] functionNames = functionMap.split(":=");
                if (functionNames.length == 2) {
                    addFunctionNameMapping(functionNames[0], functionNames[1]);
                }
            }
        }
    }
    
    public String getBuildID() {
        return "5.0_OCT_09_2009";
    }
    
    public void setInvolvedTablesNeeded() {
        final SelectInvolvedTables tl = SwisSQLAPI.involvedTablesTL.get();
        tl.involvedTables.clear();
        tl.isNeeded = true;
    }
    
    public Set getInvolvedTables() {
        final SelectInvolvedTables tl = SwisSQLAPI.involvedTablesTL.get();
        return tl.involvedTables;
    }
    
    public void removeTL() {
        SwisSQLAPI.involvedTablesTL.remove();
    }
    
    public static void updateRemoveLimitAndFetchClauseStatus(final boolean status) {
        SwisSQLAPI.removeLimitAndFetchClause.set(status);
    }
    
    public static boolean canClearLimitAndFetchClauses() {
        return SwisSQLAPI.removeLimitAndFetchClause.get();
    }
    
    public static void clearIndexPositions() {
        final Set tl = SwisSQLAPI.indexPositionsForCastingTL.get();
        tl.clear();
    }
    
    public static void setIndexPositionsForCasting(final int index) {
        final Set tl = SwisSQLAPI.indexPositionsForCastingTL.get();
        tl.add(index);
    }
    
    public static void setIndexPositionsForCasting(final Set indexList) {
        final Set tl = SwisSQLAPI.indexPositionsForCastingTL.get();
        tl.addAll(indexList);
    }
    
    public static Set getIndexPositionsForCasting() {
        return SwisSQLAPI.indexPositionsForCastingTL.get();
    }
    
    public static void removeIndexPositionsTL() {
        SwisSQLAPI.indexPositionsForCastingTL.remove();
    }
    
    public void setholidayTable(final String Table_name, final String Col_name) {
        final LinkedHashSet tl = SwisSQLAPI.holidayTable.get();
        tl.add(Table_name);
        tl.add(Col_name);
    }
    
    public LinkedHashSet getholidayTable() {
        return SwisSQLAPI.holidayTable.get();
    }
    
    public static void setFnWhiteList(final Object[][] fnList) {
        Map fnWhiteListMap_unm = new HashMap();
        for (int i = 0; i < fnList.length; ++i) {
            fnWhiteListMap_unm.put(fnList[i][0], fnList[i]);
        }
        fnWhiteListMap_unm = (SwisSQLAPI.fnWhiteListMap = Collections.unmodifiableMap((Map<?, ?>)fnWhiteListMap_unm));
    }
    
    public static void clearNULLIndexPositions() {
        final Set tl = SwisSQLAPI.indexPositionsForNULLTL.get();
        tl.clear();
    }
    
    public static void setNULLIndexPositionsForCasting(final int index) {
        final Set tl = SwisSQLAPI.indexPositionsForNULLTL.get();
        tl.add(index);
    }
    
    public static void removeNULLIndexPositionsForCasting(final int index) {
        final Set tl = SwisSQLAPI.indexPositionsForNULLTL.get();
        tl.remove(index);
    }
    
    public static Set getNULLIndexPositionsForCasting() {
        return SwisSQLAPI.indexPositionsForNULLTL.get();
    }
    
    public static void removeNULLIndexPositionsTL() {
        SwisSQLAPI.indexPositionsForNULLTL.remove();
    }
    
    public static boolean containsIndexForNULL(final int indexPosition) {
        return SwisSQLAPI.indexPositionsForNULLTL.get().contains(indexPosition);
    }
    
    public void updateSwisOptionsToSQS(final SelectQueryStatement sqs) {
        if (sqs != null) {
            sqs.setAmazonRedShiftFlag(this.isAmazonRedShift);
            sqs.setMSAzureFlag(this.isMSAzure);
            sqs.setOracleLiveFlag(this.isOracleLive);
            sqs.setCanUseUDFFunctionsForText(this.canUseUDFFunctionsForText);
            sqs.setCanUseUDFFunctionsForNumeric(this.canUseUDFFunctionsForNumeric);
            sqs.setCanUseUDFFunctionsForDateTime(this.canUseUDFFunctionsForDateTime);
            sqs.setCanUseIFFunctionForPGCaseWhenExp(this.canUseIfFunction);
            sqs.setCanCastStringLiteralToText(this.canCastStringLiteralToText);
            sqs.setCanHandleStringLiteralsForNumeric(this.canHandleStringLiteralsForNumeric);
            sqs.setCanHandleStringLiteralsForDateTime(this.canHandleStringLiteralsForDateTime);
            sqs.setCanHandleNullsInsideINClause(this.canHandleNullsInsideINClause);
            sqs.setRemovalOptionForOrderAndFetchClauses(this.removalOptionForOrderAndFetchClause);
            sqs.setCanUseDistinctFromForNullSafeEqualsOperator(this.canUseDistinctFromForNullSafeEqualsOperator);
            sqs.setCanHandleHavingWithoutGroupBy(this.canHandleHavingWithoutGroupBy);
            sqs.setCanUseUDFFunctionsForStrToDate(this.canUseUDFFunctionsForStrToDate);
            sqs.setCanHandleFunctionArgumentsCountMismatch(this.canHandleFunctionArgumentsCountMismatch);
        }
    }
    
    static {
        SwisSQLAPI.ANSIJOIN_ForOracle = false;
        SwisSQLAPI.dataTypesFromMetaDataHT = new Hashtable();
        SwisSQLAPI.columnDatatypes = new Hashtable();
        SwisSQLAPI.tableColumnListMetadata = new Hashtable();
        SwisSQLAPI.targetDataTypesMetaDataHash = new Hashtable();
        SwisSQLAPI.identityMapping = new Hashtable();
        SwisSQLAPI.primaryKeyMetaData = new Hashtable();
        SwisSQLAPI.MSSQLSERVER_THETA = false;
        SwisSQLAPI.convert_OracleThetaJOIN_To_ANSIJOIN = false;
        SwisSQLAPI.variableDatatypeMapping = null;
        SwisSQLAPI.objectContext = null;
        SwisSQLAPI.convertCaseToDecode = true;
        SwisSQLAPI.quotedOracleIdentifier = false;
        SwisSQLAPI.enableObjectMapping = false;
        SwisSQLAPI.convertToTeradata = true;
        SwisSQLAPI.tozohodb = false;
        SwisSQLAPI.objectsOwnerName = new HashMap();
        SwisSQLAPI.targetDBMappedFunctionNames = new HashMap();
        SwisSQLAPI.targetDBFunctionMappingFile = null;
        SwisSQLAPI.truncateTableNameForDB2 = true;
        SwisSQLAPI.truncateTableNameForOracle = true;
        SwisSQLAPI.truncateTableCount = 0;
        SwisSQLAPI.truncateIndexCount = 0;
        SwisSQLAPI.truncateConstraintCount = 0;
        SwisSQLAPI.involvedTablesTL = new ThreadLocal() {
            public SelectInvolvedTables initialValue() {
                return new SelectInvolvedTables();
            }
        };
        SwisSQLAPI.removeLimitAndFetchClause = new ThreadLocal() {
            public Boolean initialValue() {
                return false;
            }
        };
        SwisSQLAPI.indexPositionsForCastingTL = new ThreadLocal() {
            public Set initialValue() {
                return new HashSet();
            }
        };
        SwisSQLAPI.holidayTable = new ThreadLocal() {
            public LinkedHashSet initialValue() {
                return new LinkedHashSet();
            }
        };
        SwisSQLAPI.indexPositionsForNULLTL = new ThreadLocal() {
            public Set initialValue() {
                return new HashSet();
            }
        };
    }
}
