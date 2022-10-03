package com.adventnet.swissqlapi.sql.statement.insert;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Hashtable;
import java.util.Map;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.sql.statement.update.TableClause;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.statement.update.TableExpression;
import com.adventnet.swissqlapi.sql.statement.update.OptionalSpecifier;

public class InsertClause
{
    private String insert;
    private OptionalSpecifier optionalSpecifier;
    private TableExpression tblExp;
    private ArrayList columnList;
    private String with;
    private String lock;
    private String lockStatement;
    public static boolean isOracleDEFColTruncated;
    private UserObjectContext context;
    private CommentClass commentObj;
    
    public InsertClause() {
        this.context = null;
        this.insert = null;
        this.optionalSpecifier = null;
        this.tblExp = null;
        this.columnList = null;
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public void setInsert(final String s) {
        this.insert = s;
    }
    
    public void setOptionalSpecifier(final OptionalSpecifier ps) {
        this.optionalSpecifier = ps;
    }
    
    public void setTableExpression(final TableExpression tec) {
        this.tblExp = tec;
    }
    
    public void setColumnList(final ArrayList v) {
        this.columnList = v;
    }
    
    public void setWith(final String with) {
        this.with = with;
    }
    
    public void setLock(final String lock) {
        this.lock = lock;
    }
    
    public void setLockStatement(final String lockStatement) {
        this.lockStatement = lockStatement;
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public ArrayList getColumnList() {
        return this.columnList;
    }
    
    public OptionalSpecifier getOptionalSpecifier() {
        return this.optionalSpecifier;
    }
    
    public TableExpression getTableExpression() {
        return this.tblExp;
    }
    
    public String getWith() {
        return this.with;
    }
    
    public String getLock() {
        return this.lock;
    }
    
    public String getLockStatement() {
        return this.lockStatement;
    }
    
    public void toOracle(final InsertQueryStatement q) throws ConvertException {
        String[] keywords = null;
        if (SwisSQLUtils.getKeywords(1) != null) {
            keywords = SwisSQLUtils.getKeywords(1);
        }
        (this.optionalSpecifier = new OptionalSpecifier()).setInto("INTO");
        this.tblExp.toOracle();
        final ArrayList oracleColTruncForDEFAULT = new ArrayList();
        ArrayList vcList = new ArrayList();
        final ValuesClause vc = q.getValuesClause();
        final ArrayList tablesList = this.tblExp.getTableClauseList();
        String tableName = null;
        if (tablesList != null && tablesList.size() > 0) {
            final Object obj = tablesList.get(0);
            if (obj instanceof TableClause) {
                final TableClause tc = (TableClause)obj;
                final TableObject to = tc.getTableObject();
                tableName = to.getTableName();
                tableName = CustomizeUtil.objectNamesToQuotedIdentifier(tableName, keywords, null, 1);
                to.setTableName(tableName);
            }
        }
        if (this.with != null && this.lock != null) {
            if (this.lock.equalsIgnoreCase("TABLOCK") || this.lock.equalsIgnoreCase("UPDLOCK")) {
                this.lock = "SHARE";
            }
            if (this.lock.equalsIgnoreCase("TABLOCKX")) {
                this.lock = "EXCLUSIVE";
            }
            if (!this.lock.equalsIgnoreCase("NOLOCK") && !this.lock.equalsIgnoreCase("ROWLOCK") && !this.lock.equalsIgnoreCase("XLOCK")) {
                final String lockTableStatement = "LOCK TABLE " + tableName + " IN " + this.lock + " MODE;";
                if (SwisSQLOptions.handleLOCK_HINTSforOracle) {
                    this.setLockStatement(lockTableStatement);
                }
            }
        }
        if (vc != null) {
            vcList = vc.getValuesList();
        }
        if (vcList != null && vcList.size() > 3) {
            for (int j = 0; j < vcList.size(); ++j) {
                if (vcList.get(j).toString().trim().toLowerCase().equals("default")) {
                    oracleColTruncForDEFAULT.add(new Integer(j));
                    if (j != vcList.size() - 2) {
                        oracleColTruncForDEFAULT.add(new Integer(j + 1));
                    }
                    else {
                        oracleColTruncForDEFAULT.add(new Integer(j - 1));
                    }
                }
            }
        }
        final ArrayList dateTypeColumnIndex = new ArrayList();
        final ArrayList dateTypeColumnNames = new ArrayList();
        if (tableName != null) {
            final ArrayList colList = (ArrayList)CastingUtil.getValueIgnoreCase(SwisSQLAPI.tableColumnListMetadata, tableName);
            final Hashtable colDatatypeTable = (Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.dataTypesFromMetaDataHT, tableName);
            if (colList != null && colDatatypeTable != null) {
                for (int i = 0; i < colList.size(); ++i) {
                    final String columnName = colList.get(i);
                    final String dataType = (String)CastingUtil.getValueIgnoreCase(colDatatypeTable, columnName);
                    if (dataType != null && (dataType.toLowerCase().indexOf("datetime") != -1 || dataType.toLowerCase().indexOf("date") != -1)) {
                        if (this.columnList != null) {
                            dateTypeColumnNames.add(columnName.toLowerCase());
                        }
                        else {
                            dateTypeColumnIndex.add(new Integer(i + 1));
                        }
                    }
                }
            }
        }
        if (vcList != null) {
            int valueCount = 0;
            for (int k = 0; k < vcList.size(); ++k) {
                final Object selObj = vcList.get(k);
                if (selObj instanceof SelectColumn && ((SelectColumn)selObj).getColumnExpression().size() == 1) {
                    final String value = selObj.toString().trim();
                    if (!value.equals(",") && !value.equals("(") && !value.equals(")")) {
                        ++valueCount;
                    }
                    String insertColumnName = null;
                    if (this.columnList != null && vcList.size() == this.columnList.size()) {
                        insertColumnName = this.columnList.get(k).toString().trim().toLowerCase();
                        if (insertColumnName.startsWith("\"") || insertColumnName.startsWith("[")) {
                            insertColumnName = insertColumnName.substring(1, insertColumnName.length() - 1);
                        }
                    }
                    if (value.startsWith("'") && (dateTypeColumnIndex.contains(new Integer(valueCount)) || (insertColumnName != null && dateTypeColumnNames.contains(insertColumnName)))) {
                        final Object obj2 = vcList.get(k);
                        if (obj2 instanceof SelectColumn) {
                            final String format = SwisSQLUtils.getDateFormat(value, 1);
                            final FunctionCalls fc = new FunctionCalls();
                            final TableColumn tc2 = new TableColumn();
                            tc2.setColumnName("TO_DATE");
                            final Vector fnArgs = new Vector();
                            if (format != null) {
                                if (format.startsWith("'1900")) {
                                    fnArgs.add(format);
                                    fnArgs.add("'YYYY-MM-DD HH24:MI:SS'");
                                }
                                else {
                                    fnArgs.add(value);
                                    fnArgs.add(format);
                                }
                                fc.setFunctionName(tc2);
                                fc.setFunctionArguments(fnArgs);
                                ((SelectColumn)obj2).getColumnExpression().setElementAt(fc, 0);
                            }
                        }
                    }
                }
            }
        }
        if (this.columnList != null) {
            final int siz = this.columnList.size();
            for (int l = 0; l < this.columnList.size(); ++l) {
                if (this.columnList.get(l) instanceof String) {
                    String columnName2 = this.columnList.get(l);
                    if (!columnName2.trim().equals(",") && !columnName2.trim().startsWith("/*") && !columnName2.trim().startsWith("--")) {
                        columnName2 = CustomizeUtil.objectNamesToQuotedIdentifier(columnName2, keywords, null, 1);
                    }
                    if ((columnName2.startsWith("[") && columnName2.endsWith("]")) || (columnName2.startsWith("`") && columnName2.endsWith("`"))) {
                        columnName2 = columnName2.substring(1, columnName2.length() - 1);
                    }
                    if (!columnName2.startsWith("\"") && !columnName2.trim().startsWith("/*") && !columnName2.trim().startsWith("--") && (SwisSQLOptions.retainQuotedIdentifierForOracle || columnName2.indexOf(32) != -1)) {
                        columnName2 = "\"" + columnName2 + "\"";
                    }
                    boolean addQuotes = false;
                    if (columnName2.startsWith("\"") && columnName2.endsWith("\"")) {
                        columnName2 = columnName2.substring(1, columnName2.length() - 1);
                        addQuotes = true;
                    }
                    if (columnName2.length() > 30 && !columnName2.trim().startsWith("/*") && !columnName2.trim().startsWith("--")) {
                        columnName2 = columnName2.substring(0, 30);
                    }
                    if (addQuotes) {
                        columnName2 = "\"" + columnName2 + "\"";
                    }
                    if (!oracleColTruncForDEFAULT.contains(new Integer(l))) {
                        this.columnList.set(l, columnName2);
                    }
                    else {
                        InsertClause.isOracleDEFColTruncated = true;
                        this.columnList.set(l, null);
                    }
                }
            }
            if (InsertClause.isOracleDEFColTruncated) {
                for (int l = 0; l < this.columnList.size(); ++l) {
                    this.columnList.remove(null);
                    if (l == this.columnList.size() - 2 && this.columnList.get(l) != null) {
                        final String str = this.columnList.get(l);
                        if (str.equals(",")) {
                            this.columnList.remove(l);
                        }
                    }
                }
            }
        }
        if (q.getSubQuery() != null) {
            int valueCount = 0;
            final ArrayList dateColumnIndexList = new ArrayList();
            if (this.columnList != null) {
                for (int i = 0; i < this.columnList.size(); ++i) {
                    if (this.columnList.get(i) instanceof String) {
                        final String columnName = this.columnList.get(i).toString();
                        if (!columnName.equals(",") && !columnName.equals("(") && !columnName.equals(")")) {
                            ++valueCount;
                        }
                        if (dateTypeColumnNames.contains(columnName.toLowerCase())) {
                            dateColumnIndexList.add(new Integer(valueCount));
                        }
                    }
                }
            }
            final SelectQueryStatement local_Stmt = q.getSubQuery();
            final Vector selectQueryColumns = local_Stmt.getSelectStatement().getSelectItemList();
            for (int m = 0; m < selectQueryColumns.size(); ++m) {
                if (dateColumnIndexList.contains(new Integer(m + 1))) {
                    final Object obj2 = selectQueryColumns.get(m);
                    if (obj2 instanceof SelectColumn) {
                        String value2 = obj2.toString();
                        if (value2.startsWith("'")) {
                            if (value2.indexOf(",") == value2.length() - 1) {
                                value2 = value2.substring(0, value2.length() - 1);
                            }
                            final String format2 = SwisSQLUtils.getDateFormat(value2, 1);
                            final FunctionCalls fc2 = new FunctionCalls();
                            final TableColumn tc3 = new TableColumn();
                            tc3.setColumnName("TO_DATE");
                            final Vector fnArgs2 = new Vector();
                            if (format2 != null) {
                                if (format2.startsWith("'1900")) {
                                    fnArgs2.add(format2);
                                    fnArgs2.add("'YYYY-MM-DD HH24:MI:SS'");
                                }
                                else {
                                    fnArgs2.add(value2);
                                    fnArgs2.add(format2);
                                }
                                fc2.setFunctionName(tc3);
                                fc2.setFunctionArguments(fnArgs2);
                                ((SelectColumn)obj2).getColumnExpression().setElementAt(fc2, 0);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void toSQLServer(final InsertQueryStatement q) throws ConvertException {
        this.setCommentClass(null);
        if (this.optionalSpecifier.toString().indexOf("INTO") != -1) {
            (this.optionalSpecifier = new OptionalSpecifier()).setInto("INTO");
        }
        else {
            this.optionalSpecifier = null;
        }
        this.tblExp.toMSSQLServer();
        if (this.columnList != null) {
            for (int i = 0; i < this.columnList.size(); ++i) {
                if (this.columnList.get(i) instanceof String) {
                    String columnName = this.columnList.get(i);
                    if (!columnName.trim().equals(",") && !columnName.trim().startsWith("/*") && !columnName.trim().startsWith("--")) {
                        columnName = CustomizeUtil.objectNamesToBracedIdentifier(columnName, SwisSQLUtils.getKeywords(2), null);
                    }
                    if (columnName.startsWith("`") && columnName.endsWith("`")) {
                        columnName = columnName.substring(1, columnName.length() - 1);
                        if (columnName.indexOf(32) != -1) {
                            columnName = "\"" + columnName + "\"";
                        }
                    }
                    this.columnList.set(i, columnName);
                }
            }
        }
    }
    
    public void toSybase(final InsertQueryStatement q) throws ConvertException {
        this.setCommentClass(null);
        if (this.optionalSpecifier.toString().indexOf("INTO") != -1) {
            (this.optionalSpecifier = new OptionalSpecifier()).setInto("INTO");
        }
        else {
            this.optionalSpecifier = null;
        }
        this.tblExp.toSybase();
        if (this.columnList != null) {
            for (int i = 0; i < this.columnList.size(); ++i) {
                if (this.columnList.get(i) instanceof String) {
                    String columnName = this.columnList.get(i);
                    if (columnName.startsWith("`") && columnName.endsWith("`")) {
                        columnName = columnName.substring(1, columnName.length() - 1);
                        if (columnName.indexOf(32) != -1) {
                            columnName = "\"" + columnName + "\"";
                        }
                    }
                    this.columnList.set(i, columnName);
                }
            }
        }
    }
    
    public void toDB2(final InsertQueryStatement q) throws ConvertException {
        this.setCommentClass(null);
        (this.optionalSpecifier = new OptionalSpecifier()).setInto("INTO");
        this.tblExp.setTableNameforAliasNameInDB2Insert(true);
        this.tblExp.toDB2();
        if (this.columnList != null) {
            for (int i = 0; i < this.columnList.size(); ++i) {
                if (this.columnList.get(i) instanceof String) {
                    String columnName = this.columnList.get(i);
                    if ((columnName.startsWith("[") && columnName.endsWith("]")) || (columnName.startsWith("`") && columnName.endsWith("`"))) {
                        columnName = columnName.substring(1, columnName.length() - 1);
                    }
                    if (columnName.indexOf(32) != -1 && !columnName.trim().startsWith("/*") && !columnName.trim().startsWith("--")) {
                        columnName = "\"" + columnName + "\"";
                    }
                    this.columnList.set(i, columnName);
                }
            }
        }
    }
    
    public void toPostgres(final InsertQueryStatement q) throws ConvertException {
        this.setCommentClass(null);
        (this.optionalSpecifier = new OptionalSpecifier()).setInto("INTO");
        this.tblExp.toPostgreSQL();
    }
    
    public void toANSISQL(final InsertQueryStatement q) throws ConvertException {
        this.setCommentClass(null);
        (this.optionalSpecifier = new OptionalSpecifier()).setInto("INTO");
        this.tblExp.toANSISQL();
        if (this.columnList != null) {
            for (int i = 0; i < this.columnList.size(); ++i) {
                if (this.columnList.get(i) instanceof String) {
                    String columnName = this.columnList.get(i);
                    if ((columnName.startsWith("[") && columnName.endsWith("]")) || (columnName.startsWith("`") && columnName.endsWith("`"))) {
                        columnName = columnName.substring(1, columnName.length() - 1);
                    }
                    if (columnName.indexOf(32) != -1 && !columnName.trim().startsWith("/*") && !columnName.trim().startsWith("--")) {
                        columnName = "\"" + columnName + "\"";
                    }
                    this.columnList.set(i, columnName);
                }
            }
        }
    }
    
    public void toTeradata(final InsertQueryStatement q) throws ConvertException {
        this.setCommentClass(null);
        (this.optionalSpecifier = new OptionalSpecifier()).setInto("INTO");
        this.tblExp.toTeradata();
        if (this.columnList != null) {
            for (int i = 0; i < this.columnList.size(); ++i) {
                if (this.columnList.get(i) instanceof String) {
                    String columnName = this.columnList.get(i);
                    if ((columnName.startsWith("[") && columnName.endsWith("]")) || (columnName.startsWith("`") && columnName.endsWith("`"))) {
                        columnName = columnName.substring(1, columnName.length() - 1);
                    }
                    if (columnName.indexOf(32) != -1 && !columnName.trim().startsWith("/*") && !columnName.trim().startsWith("--")) {
                        columnName = "\"" + columnName + "\"";
                    }
                    if (!columnName.equalsIgnoreCase("(") && !columnName.equalsIgnoreCase(")") && !columnName.equalsIgnoreCase(",") && !columnName.trim().startsWith("/*") && !columnName.trim().startsWith("--")) {
                        columnName = CustomizeUtil.objectNamesToQuotedIdentifier(columnName, SwisSQLUtils.getKeywords("teradata"), null, -1);
                    }
                    this.columnList.set(i, columnName);
                }
            }
        }
    }
    
    public void toMySQL(final InsertQueryStatement q) throws ConvertException {
        this.setCommentClass(null);
        this.tblExp.toMySQL();
        if (this.columnList != null) {
            for (int i = 0; i < this.columnList.size(); ++i) {
                if (this.columnList.get(i) instanceof String) {
                    String columnName = this.columnList.get(i);
                    if (columnName.startsWith("[") && columnName.endsWith("]")) {
                        columnName = columnName.substring(1, columnName.length() - 1);
                        if (columnName.indexOf(32) != -1) {
                            columnName = "\"" + columnName + "\"";
                        }
                    }
                    this.columnList.set(i, columnName);
                }
            }
        }
    }
    
    public void toInformix(final InsertQueryStatement q) throws ConvertException {
        this.setCommentClass(null);
        (this.optionalSpecifier = new OptionalSpecifier()).setInto("INTO");
        this.tblExp.toInformix();
    }
    
    public void toTimesTen(final InsertQueryStatement q) throws ConvertException {
        this.setCommentClass(null);
        (this.optionalSpecifier = new OptionalSpecifier()).setInto("INTO");
        this.tblExp.toTimesTen();
        ArrayList vcList = new ArrayList();
        final ValuesClause vc = q.getValuesClause();
        final ArrayList tblList = this.tblExp.getTableClauseList();
        String tableName = null;
        if (tblList != null) {
            final Object obj = tblList.get(0);
            if (obj instanceof TableClause) {
                tableName = ((TableClause)obj).getTableObject().getTableName();
            }
        }
        final ArrayList dateTypeColumnIndex = new ArrayList();
        final ArrayList dateTypeColumnNames = new ArrayList();
        final ArrayList dateTimeColumnIndex = new ArrayList();
        final ArrayList dateTimeColumnNames = new ArrayList();
        final ArrayList unicodeColumnIndex = new ArrayList();
        final ArrayList unicodeColumnNames = new ArrayList();
        if (tableName != null) {
            final ArrayList colList = (ArrayList)CastingUtil.getValueIgnoreCase(SwisSQLAPI.tableColumnListMetadata, tableName);
            final Hashtable colDatatypeTable = (Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.dataTypesFromMetaDataHT, tableName);
            if (colList != null && colDatatypeTable != null) {
                for (int i = 0; i < colList.size(); ++i) {
                    final String columnName = colList.get(i);
                    final String dataType = (String)CastingUtil.getValueIgnoreCase(colDatatypeTable, columnName);
                    if (dataType != null) {
                        if (dataType.toLowerCase().indexOf("date") != -1 || dataType.toLowerCase().indexOf("time") != -1) {
                            if (this.columnList != null) {
                                dateTypeColumnNames.add(columnName.toLowerCase());
                            }
                            else {
                                dateTypeColumnIndex.add(new Integer(i + 1));
                            }
                            if (dataType.toLowerCase().indexOf("datetime") != -1) {
                                if (this.columnList != null) {
                                    dateTimeColumnNames.add(columnName.toLowerCase());
                                }
                                else {
                                    dateTimeColumnIndex.add(new Integer(i + 1));
                                }
                            }
                        }
                        else if (dataType.indexOf("unichar") != -1 || dataType.indexOf("univarchar") != -1 || dataType.indexOf("nchar") != -1 || dataType.indexOf("nvarchar") != -1) {
                            if (this.columnList != null) {
                                unicodeColumnNames.add(columnName.toLowerCase());
                            }
                            else {
                                unicodeColumnIndex.add(new Integer(i + 1));
                            }
                        }
                    }
                }
            }
        }
        if (this.columnList != null) {
            final int siz = this.columnList.size();
            for (int j = 0; j < this.columnList.size(); ++j) {
                if (this.columnList.get(j) instanceof String) {
                    String columnName2 = this.columnList.get(j);
                    if ((columnName2.startsWith("[") && columnName2.endsWith("]")) || (columnName2.startsWith("`") && columnName2.endsWith("`"))) {
                        columnName2 = columnName2.substring(1, columnName2.length() - 1);
                        this.columnList.set(j, columnName2);
                    }
                    if (!columnName2.startsWith("\"") && columnName2.indexOf(32) != -1) {
                        columnName2 = "\"" + columnName2 + "\"";
                        this.columnList.set(j, columnName2);
                    }
                }
            }
        }
        if (vc != null) {
            vcList = vc.getValuesList();
        }
        if (vcList != null) {
            int valueCount = 0;
            for (int k = 0; k < vcList.size(); ++k) {
                if (vcList.get(k).toString().trim().toLowerCase().equals("default")) {
                    throw new ConvertException("\n DEFAULT values are not supported in TimesTen 5.1.21\n");
                }
                String value = vcList.get(k).toString().trim();
                if (!value.equals(",") && !value.equals("(") && !value.equals(")")) {
                    ++valueCount;
                }
                if (value.startsWith("'") && (dateTypeColumnIndex.contains(new Integer(valueCount)) || (this.columnList != null && dateTypeColumnNames.contains(this.columnList.get(k).toString().trim().toLowerCase())))) {
                    final Object obj2 = vcList.get(k);
                    if (obj2 instanceof SelectColumn) {
                        String format = SwisSQLUtils.getDateFormat(value, 10);
                        if (format != null && (format.equals("YYYY-MM-DD") || format.equals("HH24:MI:SS"))) {
                            if (dateTimeColumnIndex.contains(new Integer(valueCount)) || (this.columnList != null && dateTypeColumnNames.contains(this.columnList.get(k).toString().trim().toLowerCase()))) {
                                if (format.equals("YYYY-MM-DD")) {
                                    value = value.substring(0, value.length() - 1) + " 00:00:00'";
                                }
                                else {
                                    value = "'1900-01-01 " + value.substring(1);
                                }
                                ((SelectColumn)obj2).getColumnExpression().setElementAt(value, 0);
                            }
                            format = null;
                        }
                        if (format != null) {
                            if (format.startsWith("'1900")) {
                                ((SelectColumn)obj2).getColumnExpression().setElementAt(format, 0);
                            }
                            else if (format.equals(value)) {
                                value = value.substring(1, value.length() - 1);
                                String time = "";
                                int index = 0;
                                if ((index = value.indexOf(" ")) != -1) {
                                    time = value.substring(index + 1);
                                    value = value.substring(0, index);
                                }
                                final int len = value.length();
                                if (len == 8) {
                                    value = value.substring(0, 4) + "-" + value.substring(4, 6) + "-" + value.substring(6);
                                }
                                else if (len == 6) {
                                    String yearStr = value.substring(0, 2);
                                    final int year = Integer.parseInt(yearStr);
                                    if (year < 50) {
                                        yearStr = "20" + yearStr;
                                    }
                                    else {
                                        yearStr = "19" + yearStr;
                                    }
                                    value = yearStr + "-" + value.substring(2, 4) + "-" + value.substring(4);
                                }
                                if ((dateTimeColumnIndex.contains(new Integer(valueCount)) || (this.columnList != null && dateTypeColumnNames.contains(this.columnList.get(k).toString().trim().toLowerCase()))) && time == "") {
                                    value += " 00:00:00";
                                }
                                else if (time != "") {
                                    value = value + " " + time;
                                }
                                ((SelectColumn)obj2).getColumnExpression().setElementAt("'" + value + "'", 0);
                            }
                            else {
                                final FunctionCalls fc = new FunctionCalls();
                                final TableColumn tc = new TableColumn();
                                tc.setColumnName("TO_DATE");
                                final Vector fnArgs = new Vector();
                                fnArgs.add(value);
                                fnArgs.add(format);
                                fc.setFunctionName(tc);
                                fc.setFunctionArguments(fnArgs);
                                ((SelectColumn)obj2).getColumnExpression().setElementAt(fc, 0);
                            }
                        }
                    }
                }
                else if (value.startsWith("'") && (unicodeColumnIndex.contains(new Integer(valueCount)) || (this.columnList != null && unicodeColumnNames.contains(this.columnList.get(k).toString().trim().toLowerCase())))) {
                    final Object obj2 = vcList.get(k);
                    if (obj2 instanceof SelectColumn) {
                        final Vector colExpr = ((SelectColumn)obj2).getColumnExpression();
                        if (colExpr.size() == 1) {
                            colExpr.setElementAt("N" + value, 0);
                        }
                    }
                }
            }
        }
    }
    
    public void toNetezza(final InsertQueryStatement q) throws ConvertException {
        this.setCommentClass(null);
        (this.optionalSpecifier = new OptionalSpecifier()).setInto("INTO");
        final SelectQueryStatement tempSubQuery = this.tblExp.getSubQuery();
        if (this.columnList == null && tempSubQuery != null) {
            final ArrayList newColumnList = new ArrayList();
            final SelectQueryStatement tblExpSubQuery = tempSubQuery;
            final Vector tblExpSubQuerySelectItems = tblExpSubQuery.getSelectStatement().getSelectItemList();
            newColumnList.add("(");
            for (int i = 0; i < tblExpSubQuerySelectItems.size(); ++i) {
                if (tblExpSubQuerySelectItems.elementAt(i) instanceof SelectColumn) {
                    newColumnList.add(tblExpSubQuerySelectItems.elementAt(i).toNetezzaSelect(tblExpSubQuery, tblExpSubQuery));
                }
                else {
                    newColumnList.add(tblExpSubQuerySelectItems.elementAt(i));
                }
            }
            newColumnList.add(")");
            this.setColumnList(newColumnList);
        }
        this.tblExp.toNetezza();
        if (this.columnList != null) {
            for (int j = 0; j < this.columnList.size(); ++j) {
                if (this.columnList.get(j) instanceof String) {
                    String columnName = this.columnList.get(j);
                    if ((columnName.startsWith("[") && columnName.endsWith("]")) || (columnName.startsWith("`") && columnName.endsWith("`"))) {
                        columnName = columnName.substring(1, columnName.length() - 1);
                    }
                    if (columnName.indexOf(32) != -1 && !columnName.trim().startsWith("/*") && !columnName.trim().startsWith("--")) {
                        columnName = "\"" + columnName + "\"";
                    }
                    if (!columnName.equalsIgnoreCase("(") && !columnName.equalsIgnoreCase(")") && !columnName.equalsIgnoreCase(",") && !columnName.trim().startsWith("/*") && !columnName.trim().startsWith("--")) {
                        columnName = CustomizeUtil.objectNamesToQuotedIdentifier(columnName, SwisSQLUtils.getKeywords(11), null, 11);
                    }
                    this.columnList.set(j, columnName);
                }
            }
        }
        if (this.columnList == null && tempSubQuery != null && this.tblExp.getSubQuery() == null) {
            final SelectQueryStatement tblExpSubQuery2 = tempSubQuery;
            final Vector tblExpSubQuerySelectItems2 = tblExpSubQuery2.getSelectStatement().getSelectItemList();
            for (int k = 0; k < tblExpSubQuerySelectItems2.size(); ++k) {
                if (tblExpSubQuerySelectItems2.elementAt(k) instanceof SelectColumn) {
                    this.columnList.add(tblExpSubQuerySelectItems2.elementAt(k).toNetezzaSelect(tblExpSubQuery2, tblExpSubQuery2));
                }
                else {
                    this.columnList.add(tblExpSubQuerySelectItems2.elementAt(k));
                }
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.insert.toUpperCase());
        sb.append(" ");
        if (this.commentObj != null) {
            sb.append(this.commentObj.toString().trim() + " ");
        }
        if (this.optionalSpecifier != null) {
            sb.append(this.optionalSpecifier.toString());
            sb.append(" ");
        }
        if (this.tblExp != null) {
            if (this.context != null) {
                this.tblExp.setObjectContext(this.context);
            }
            sb.append(this.tblExp.toString());
            sb.append(" ");
        }
        if (this.columnList != null) {
            final int size = this.columnList.size();
            SelectQueryStatement.beautyTabCount += 2;
            for (int i = 0; i < size; ++i) {
                String isCommaOrOpenBrace = "";
                if (this.columnList.get(i) instanceof String) {
                    isCommaOrOpenBrace = this.columnList.get(i);
                }
                if (isCommaOrOpenBrace.trim().equals("(")) {
                    sb.append("\n");
                    for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
                        sb.append("\t");
                    }
                }
                if (this.context != null) {
                    final String temp = this.context.getEquivalent(this.columnList.get(i)).toString();
                    sb.append(temp + " ");
                }
                else {
                    sb.append(this.columnList.get(i) + " ");
                }
                if (isCommaOrOpenBrace.equals(",")) {
                    sb.append("\n");
                    for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
                        sb.append("\t");
                    }
                }
            }
            SelectQueryStatement.beautyTabCount -= 2;
        }
        return sb.toString();
    }
    
    static {
        InsertClause.isOracleDEFColTruncated = false;
    }
}
