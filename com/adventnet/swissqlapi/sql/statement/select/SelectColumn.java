package com.adventnet.swissqlapi.sql.statement.select;

import java.util.Iterator;
import java.util.Set;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.sql.statement.update.TableExpression;
import com.adventnet.swissqlapi.sql.statement.update.TableClause;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.sql.statement.create.CharacterClass;
import com.adventnet.swissqlapi.sql.statement.create.Datatype;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.parser.Token;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import java.util.Hashtable;
import com.adventnet.swissqlapi.sql.statement.update.UpdateQueryStatement;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.Vector;

public class SelectColumn
{
    private String OpenBrace;
    private Vector columnExpression;
    private String CloseBrace;
    private String aliasName;
    private String isAS;
    private String endsWith;
    private UserObjectContext context;
    private String targetDataType;
    private String targetDataTypeWithSize;
    private TableColumn corrTableColumn;
    private boolean insideDecodeFunction;
    private boolean leftTableColProcessed;
    private boolean rightTableColProcessed;
    private boolean isSelectColFromUQS;
    private boolean inArithmeticExpr;
    private CommentClass commentObj;
    private CommentClass commentObjAfterToken;
    private UpdateQueryStatement fromUQS;
    private boolean isDateAddition;
    private Hashtable originalTableNameList;
    private boolean isOrderItem;
    private String parentFunction;
    private String aliasForExpression;
    private boolean teradataUnionCastingDone;
    private String ignoreNulls;
    private boolean reportsMysqlConversion;
    private boolean castToText;
    private boolean outerJoin;
    
    public SelectColumn() {
        this.context = null;
        this.targetDataType = null;
        this.targetDataTypeWithSize = null;
        this.corrTableColumn = null;
        this.insideDecodeFunction = false;
        this.leftTableColProcessed = false;
        this.rightTableColProcessed = false;
        this.isSelectColFromUQS = false;
        this.isDateAddition = false;
        this.originalTableNameList = null;
        this.isOrderItem = false;
        this.parentFunction = null;
        this.teradataUnionCastingDone = false;
        this.reportsMysqlConversion = false;
        this.castToText = true;
        this.outerJoin = false;
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public UserObjectContext getObjectContext() {
        return this.context;
    }
    
    public void setColumnExpression(final Vector v_cn) {
        this.columnExpression = v_cn;
    }
    
    public void addColumnExpressionElement(final Object o) {
        this.columnExpression.addElement(o);
    }
    
    public void setAliasName(final String an) {
        this.aliasName = an;
    }
    
    public void setIsAS(final String as) {
        this.isAS = as;
    }
    
    public void setEndsWith(final String ew) {
        this.endsWith = ew;
    }
    
    public void setOpenBrace(final String s_ob) {
        this.OpenBrace = s_ob;
    }
    
    public String getOpenBrace() {
        return this.OpenBrace;
    }
    
    public void setCloseBrace(final String s_cb) {
        this.CloseBrace = s_cb;
    }
    
    public String getCloseBrace() {
        return this.CloseBrace;
    }
    
    public void setInsideDecodeFunction(final boolean bool) {
        this.insideDecodeFunction = bool;
    }
    
    public void setOriginalTableNamesForUpdateSetClause(final Hashtable tableList) {
        this.originalTableNameList = tableList;
    }
    
    public Hashtable getOriginalTableNamesForUpdateSetClause() {
        return this.originalTableNameList;
    }
    
    public boolean getInsideDecodeFunction() {
        return this.insideDecodeFunction;
    }
    
    public void setInArithmeticExpression(final boolean inArithmeticExpr) {
        this.inArithmeticExpr = inArithmeticExpr;
    }
    
    public boolean getInArithmeticExpression() {
        return this.inArithmeticExpr;
    }
    
    public void setCorrespondingTableColumn(final TableColumn corrTableColumn) {
        this.corrTableColumn = corrTableColumn;
        if (corrTableColumn != null) {
            this.targetDataTypeWithSize = MetadataInfoUtil.getTargetDataTypeForColumn(corrTableColumn);
            this.targetDataType = CastingUtil.getDataType(this.targetDataTypeWithSize);
        }
    }
    
    public TableColumn getCorrespondingTableColumn() {
        return this.corrTableColumn;
    }
    
    public void setTargetDataType(final String targetDataType) {
        this.targetDataType = targetDataType;
    }
    
    public String getTargetDataType() {
        return this.targetDataType;
    }
    
    public void setSelectColFromUQSSetExpression(final boolean isSelectColFromUQS) {
        this.isSelectColFromUQS = isSelectColFromUQS;
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public void setCommentClassAfterToken(final CommentClass commentObj) {
        this.commentObjAfterToken = commentObj;
    }
    
    public void setFromUQS(final UpdateQueryStatement fromUQS) {
        this.fromUQS = fromUQS;
    }
    
    public void setIsOrderItem(final boolean isOrderItem) {
        this.isOrderItem = isOrderItem;
    }
    
    public void setAliasForExpression(final String aliasForExpr) {
        this.aliasForExpression = aliasForExpr;
    }
    
    public void setTeradataUnionCastingDone(final boolean boolVal) {
        this.teradataUnionCastingDone = boolVal;
    }
    
    public void setIgnoreNulls(final String ignoreNulls) {
        this.ignoreNulls = ignoreNulls;
    }
    
    public boolean getSelectColFromUQSSetExpression() {
        return this.isSelectColFromUQS;
    }
    
    public Vector getColumnExpression() {
        return this.columnExpression;
    }
    
    public String getAliasName() {
        return this.aliasName;
    }
    
    public String getIsAS() {
        return this.isAS;
    }
    
    public String getEndsWith() {
        return this.endsWith;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public CommentClass getCommentClassAfterToken() {
        return this.commentObjAfterToken;
    }
    
    public String getAliasForExpression() {
        return this.aliasForExpression;
    }
    
    public void addCommentClassAfterToken(Token commentObj) {
        if (this.commentObjAfterToken != null) {
            final ArrayList specialTokenList = this.commentObjAfterToken.getSpecialToken();
            final int lastIndex = specialTokenList.size();
            while (commentObj.specialToken != null) {
                specialTokenList.add(lastIndex, commentObj.specialToken.image);
                commentObj = commentObj.specialToken;
            }
        }
        else if (commentObj != null && commentObj.specialToken != null) {
            final ArrayList<String> specialTokenList2 = new ArrayList<String>();
            while (commentObj.specialToken != null) {
                specialTokenList2.add(0, commentObj.specialToken.image);
                commentObj = commentObj.specialToken;
            }
            final CommentClass commentObjToBeInserted = new CommentClass();
            commentObjToBeInserted.setSpecialToken(specialTokenList2);
            this.commentObjAfterToken = commentObjToBeInserted;
        }
    }
    
    public boolean isTeradataUnionCastingDone() {
        return this.teradataUnionCastingDone;
    }
    
    public String getIgnoreNulls() {
        return this.ignoreNulls;
    }
    
    public void setReportsMysqlConversion(final boolean reportsMysqlConversionValue) {
        this.reportsMysqlConversion = reportsMysqlConversionValue;
    }
    
    public boolean isReportsMysqlConversion() {
        return this.reportsMysqlConversion;
    }
    
    public void setCastToTextInsideIf(final boolean flag) {
        this.castToText = flag;
    }
    
    public boolean castToTextType() {
        return this.castToText;
    }
    
    public SelectColumn toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectColumn sc = new SelectColumn();
        sc.setCommentClass(this.commentObj);
        String s_ce = new String();
        final Vector v_ce = new Vector();
        final int sql_dialect = 0;
        boolean isDualTable = false;
        if (from_sqs != null) {
            from_sqs.getSQLDialect();
            final FromClause fromClause = from_sqs.getFromClause();
            if (fromClause != null) {
                final FromTable fromTable = fromClause.getFromTablefromTheVector();
                if (fromTable != null && fromTable.getTableName() instanceof String) {
                    String fromTableName = (String)fromTable.getTableName();
                    fromTableName = fromTableName.trim();
                    if (fromTableName.equalsIgnoreCase("DUAL")) {
                        isDualTable = true;
                    }
                }
            }
        }
        String[] keywords = null;
        if (SwisSQLUtils.getKeywords(2) != null) {
            keywords = SwisSQLUtils.getKeywords(2);
        }
        sc.setOpenBrace(this.OpenBrace);
        for (int i = 0; i < this.columnExpression.size(); ++i) {
            if (this.columnExpression.elementAt(i) instanceof TableColumn) {
                final TableColumn tc = this.columnExpression.elementAt(i);
                tc.setObjectContext(this.context);
                if (tc.getColumnName() != null) {
                    String column_Name = tc.getColumnName();
                    if (column_Name.trim().length() > 0 && !isDualTable) {
                        if (!SwisSQLOptions.TSQLQuotedIdentifier && column_Name.trim().startsWith("\"") && column_Name.trim().endsWith("\"")) {
                            String temp = column_Name.substring(1, column_Name.length() - 1);
                            if (temp.indexOf("'") != -1) {
                                temp = temp.replaceAll("'", "\"");
                            }
                            column_Name = "'" + temp + "'";
                        }
                        if (!column_Name.trim().equalsIgnoreCase("USER")) {
                            column_Name = CustomizeUtil.objectNamesToBracedIdentifier(column_Name, keywords, null);
                        }
                    }
                    tc.setColumnName(column_Name);
                }
                v_ce.addElement(tc.toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
                if (this.columnExpression.elementAt(i).getFunctionName() == null) {
                    v_ce.addElement(this.columnExpression.elementAt(i));
                }
                else {
                    v_ce.addElement(this.columnExpression.elementAt(i).toMSSQLServerSelect(to_sqs, from_sqs));
                }
            }
            else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
                v_ce.addElement(this.columnExpression.elementAt(i).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof WhereColumn) {
                v_ce.addElement(this.columnExpression.elementAt(i).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof WhereExpression) {
                v_ce.addElement(this.columnExpression.elementAt(i).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
                v_ce.addElement(this.columnExpression.elementAt(i).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof WhereItem) {
                v_ce.addElement(this.columnExpression.elementAt(i).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
                v_ce.addElement(this.columnExpression.elementAt(i).toMSSQLServerSelect());
            }
            else if (this.columnExpression.elementAt(i) instanceof String) {
                s_ce = this.columnExpression.elementAt(i);
                if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                    v_ce.addElement("CURRENT_TIME");
                }
                else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                    v_ce.addElement("GETDATE()");
                }
                else if (s_ce.equalsIgnoreCase("CURRENT")) {
                    v_ce.addElement("GETDATE()");
                }
                else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                    v_ce.addElement("CURRENT_TIMESTAMP");
                }
                else if (s_ce.equalsIgnoreCase("SYS_GUID")) {
                    v_ce.addElement("NEWID()");
                }
                else if (s_ce.equalsIgnoreCase("SYSDATE")) {
                    v_ce.addElement("GETDATE()");
                }
                else if (s_ce.equalsIgnoreCase("**")) {
                    this.createPowerFunction(v_ce, this.columnExpression, i, true);
                }
                else {
                    if (s_ce.equalsIgnoreCase("=") && sql_dialect == 5) {
                        throw new ConvertException();
                    }
                    if (s_ce.equalsIgnoreCase("IS")) {
                        this.createDecodeFunction(v_ce, this.columnExpression, i);
                        final FunctionCalls fc = v_ce.get(v_ce.size() - 1);
                        v_ce.setElementAt(fc.toMSSQLServerSelect(to_sqs, from_sqs), v_ce.size() - 1);
                    }
                    else if (s_ce.trim().equals(":=")) {
                        v_ce.addElement("=");
                    }
                    else if (s_ce.startsWith(":")) {
                        if (s_ce.substring(0, 2) != null && !s_ce.substring(0, 2).equalsIgnoreCase("::")) {
                            v_ce.addElement("@" + s_ce.substring(1));
                        }
                        else {
                            this.createCastFunction(v_ce, this.columnExpression, i);
                            final Object object = v_ce.get(v_ce.size() - 1);
                            if (object instanceof FunctionCalls) {
                                v_ce.set(v_ce.size() - 1, ((FunctionCalls)object).toMSSQLServerSelect(to_sqs, from_sqs));
                            }
                        }
                    }
                    else if (s_ce.equalsIgnoreCase("/")) {
                        v_ce.addElement("/");
                        if (this.columnExpression.elementAt(i + 1) instanceof SelectColumn) {
                            v_ce.addElement("CONVERT(FLOAT, " + this.columnExpression.elementAt(i + 1).toMSSQLServerSelect(to_sqs, from_sqs) + ")");
                            ++i;
                        }
                        else if (this.columnExpression.elementAt(i + 1) instanceof TableColumn) {
                            v_ce.addElement("CONVERT(FLOAT, " + this.columnExpression.elementAt(i + 1).toMSSQLServerSelect(to_sqs, from_sqs) + ")");
                            ++i;
                        }
                        else if (this.columnExpression.elementAt(i + 1) instanceof FunctionCalls) {
                            v_ce.addElement("CONVERT(FLOAT, " + this.columnExpression.elementAt(i + 1).toMSSQLServerSelect(to_sqs, from_sqs) + ")");
                            ++i;
                        }
                        else if (this.columnExpression.elementAt(i + 1) instanceof CaseStatement) {
                            v_ce.addElement("CONVERT(FLOAT, " + this.columnExpression.elementAt(i + 1).toMSSQLServerSelect(to_sqs, from_sqs) + ")");
                            ++i;
                        }
                        else if (this.columnExpression.elementAt(i + 1) instanceof SelectQueryStatement) {
                            v_ce.addElement(this.columnExpression.elementAt(i + 1).toMSSQLServerSelect());
                            ++i;
                        }
                        else if (this.columnExpression.elementAt(i + 1) instanceof String) {
                            s_ce = this.columnExpression.elementAt(i + 1);
                            if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                                v_ce.addElement("CONVERT(FLOAT, CURRENT_TIME)");
                            }
                            else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                                v_ce.addElement("CONVERT(FLOAT, CURRENT_DATE)");
                            }
                            else if (s_ce.equalsIgnoreCase("CURRENT")) {
                                v_ce.addElement("CONVERT(FLOAT, CURRENT_DATE)");
                            }
                            else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                                v_ce.addElement("CONVERT(FLOAT, CURRENT_TIMESTAMP)");
                            }
                            else if (s_ce.equalsIgnoreCase("SYS_GUID")) {
                                v_ce.addElement("CONVERT(FLOAT, NEWID())");
                            }
                            else if (s_ce.equalsIgnoreCase("SYSDATE")) {
                                v_ce.addElement("CONVERT(FLOAT, GETDATE())");
                            }
                            else if (s_ce.equalsIgnoreCase("**") || s_ce.equalsIgnoreCase("^")) {
                                this.createPowerFunction(v_ce, this.columnExpression, i + 1, true);
                            }
                            else {
                                if (s_ce.equalsIgnoreCase("=") && sql_dialect == 5) {
                                    throw new ConvertException();
                                }
                                if (s_ce.equalsIgnoreCase("IS")) {
                                    throw new ConvertException();
                                }
                                if (s_ce.startsWith(":")) {
                                    v_ce.addElement("@" + s_ce.substring(1));
                                }
                                else {
                                    v_ce.addElement("CONVERT(FLOAT, " + s_ce + ")");
                                }
                            }
                            ++i;
                        }
                        else if (s_ce.equalsIgnoreCase("||")) {
                            v_ce.addElement("+");
                            ++i;
                        }
                        else {
                            v_ce.addElement("CONVERT(FLOAT, " + this.columnExpression.elementAt(i + 1) + ")");
                            ++i;
                        }
                    }
                    else if (s_ce.equalsIgnoreCase("||")) {
                        this.checkConcatenationString(v_ce, this.columnExpression, i, 2);
                        v_ce.addElement("+");
                    }
                    else {
                        v_ce.addElement(s_ce);
                    }
                }
            }
            else {
                v_ce.addElement(this.columnExpression.elementAt(i));
            }
        }
        sc.setColumnExpression(v_ce);
        sc.setCloseBrace(this.CloseBrace);
        sc.setIsAS(this.isAS);
        sc.setEndsWith(this.endsWith);
        sc.setAliasName(this.aliasName);
        return sc;
    }
    
    public SelectColumn toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectColumn sc = new SelectColumn();
        sc.setCommentClass(this.commentObj);
        String s_ce = new String();
        final Vector v_ce = new Vector();
        final int sql_dialect = 0;
        if (from_sqs != null) {
            from_sqs.getSQLDialect();
        }
        sc.setOpenBrace(this.OpenBrace);
        sc.setObjectContext(this.context);
        for (int i = 0; i < this.columnExpression.size(); ++i) {
            if (this.columnExpression.elementAt(i) instanceof TableColumn) {
                this.columnExpression.elementAt(i).setObjectContext(this.context);
                v_ce.addElement(this.columnExpression.elementAt(i).toSybaseSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
                this.columnExpression.elementAt(i).setObjectContext(this.context);
                if (this.columnExpression.elementAt(i).getFunctionName() == null) {
                    v_ce.addElement(this.columnExpression.elementAt(i));
                }
                else {
                    v_ce.addElement(this.columnExpression.elementAt(i).toSybaseSelect(to_sqs, from_sqs));
                }
            }
            else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
                v_ce.addElement(this.columnExpression.elementAt(i).toSybaseSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
                this.columnExpression.elementAt(i).setObjectContext(this.context);
                v_ce.addElement(this.columnExpression.elementAt(i).toSybaseSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
                v_ce.addElement(this.columnExpression.elementAt(i).toSybaseSelect());
            }
            else if (this.columnExpression.elementAt(i) instanceof String) {
                s_ce = this.columnExpression.elementAt(i);
                if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                    v_ce.addElement("CURRENT_TIME");
                }
                else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                    v_ce.addElement("GETDATE()");
                }
                else if (s_ce.equalsIgnoreCase("CURRENT")) {
                    v_ce.addElement("GETDATE()");
                }
                else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                    v_ce.addElement("CURRENT_TIMESTAMP");
                }
                else if (s_ce.equalsIgnoreCase("SYS_GUID")) {
                    v_ce.addElement("NEWID()");
                }
                else if (s_ce.equalsIgnoreCase("SYSDATE")) {
                    v_ce.addElement("GETDATE()");
                }
                else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
                    this.createPowerFunction(v_ce, this.columnExpression, i, true);
                }
                else {
                    if (s_ce.equalsIgnoreCase("=") && sql_dialect == 5) {
                        throw new ConvertException();
                    }
                    if (s_ce.equalsIgnoreCase("IS")) {
                        this.createDecodeFunction(v_ce, this.columnExpression, i);
                        final FunctionCalls fc = v_ce.get(v_ce.size() - 1);
                        v_ce.setElementAt(fc.toSybaseSelect(to_sqs, from_sqs), v_ce.size() - 1);
                    }
                    else if (s_ce.startsWith(":")) {
                        v_ce.addElement("@" + s_ce.substring(1));
                    }
                    else if (s_ce.equalsIgnoreCase("/")) {
                        v_ce.addElement("/");
                        if (this.columnExpression.elementAt(i + 1) instanceof SelectColumn) {
                            v_ce.addElement("CONVERT(FLOAT, " + this.columnExpression.elementAt(i + 1).toSybaseSelect(to_sqs, from_sqs) + ")");
                            ++i;
                        }
                        else if (this.columnExpression.elementAt(i + 1) instanceof TableColumn) {
                            v_ce.addElement("CONVERT(FLOAT, " + this.columnExpression.elementAt(i + 1).toSybaseSelect(to_sqs, from_sqs) + ")");
                            ++i;
                        }
                        else if (this.columnExpression.elementAt(i + 1) instanceof FunctionCalls) {
                            v_ce.addElement("CONVERT(FLOAT, " + this.columnExpression.elementAt(i + 1).toSybaseSelect(to_sqs, from_sqs) + ")");
                            ++i;
                        }
                        else if (this.columnExpression.elementAt(i + 1) instanceof CaseStatement) {
                            v_ce.addElement("CONVERT(FLOAT, " + this.columnExpression.elementAt(i + 1).toSybaseSelect(to_sqs, from_sqs) + ")");
                            ++i;
                        }
                        else if (this.columnExpression.elementAt(i + 1) instanceof SelectQueryStatement) {
                            v_ce.addElement("CONVERT(FLOAT, " + this.columnExpression.elementAt(i + 1).toSybaseSelect() + ")");
                            ++i;
                        }
                        else if (this.columnExpression.elementAt(i + 1) instanceof String) {
                            s_ce = this.columnExpression.elementAt(i + 1);
                            if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                                v_ce.addElement("CONVERT(FLOAT, CURRENT_TIME)");
                            }
                            else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                                v_ce.addElement("CONVERT(FLOAT, CURRENT_DATE)");
                            }
                            else if (s_ce.equalsIgnoreCase("CURRENT")) {
                                v_ce.addElement("CONVERT(FLOAT, CURRENT_DATE)");
                            }
                            else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                                v_ce.addElement("CONVERT(FLOAT, CURRENT_TIMESTAMP)");
                            }
                            else if (s_ce.equalsIgnoreCase("SYS_GUID")) {
                                v_ce.addElement("CONVERT(FLOAT, NEWID())");
                            }
                            else if (s_ce.equalsIgnoreCase("SYSDATE")) {
                                v_ce.addElement("CONVERT(FLOAT, GETDATE())");
                            }
                            else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
                                this.createPowerFunction(v_ce, this.columnExpression, i + 1, true);
                            }
                            else {
                                if (s_ce.equalsIgnoreCase("=") && sql_dialect == 5) {
                                    throw new ConvertException();
                                }
                                if (s_ce.equalsIgnoreCase("IS")) {
                                    throw new ConvertException();
                                }
                                if (s_ce.startsWith(":")) {
                                    v_ce.addElement("@" + s_ce.substring(1));
                                }
                                else {
                                    v_ce.addElement("CONVERT(FLOAT, " + s_ce + ")");
                                }
                            }
                            ++i;
                        }
                        else if (s_ce.equalsIgnoreCase("||")) {
                            v_ce.addElement("+");
                            ++i;
                        }
                        else {
                            v_ce.addElement("CONVERT(FLOAT, " + this.columnExpression.elementAt(i + 1) + ")");
                            ++i;
                        }
                    }
                    else if (s_ce.equalsIgnoreCase("||")) {
                        v_ce.addElement("+");
                    }
                    else {
                        v_ce.addElement(s_ce);
                    }
                }
            }
            else {
                v_ce.addElement(this.columnExpression.elementAt(i));
            }
        }
        sc.setColumnExpression(v_ce);
        sc.setCloseBrace(this.CloseBrace);
        sc.setIsAS(this.isAS);
        sc.setEndsWith(this.endsWith);
        if (from_sqs != null) {
            final SelectStatement fromSS = from_sqs.getSelectStatement();
            if (fromSS != null && this.aliasName != null) {
                final Vector fromSelectItems = fromSS.getSelectItemList();
                if (fromSelectItems != null) {
                    for (int l = 0; l < fromSelectItems.size(); ++l) {
                        final Object obj = fromSelectItems.get(l);
                        if (obj instanceof SelectColumn && !obj.equals(this)) {
                            final Vector colExpr = ((SelectColumn)obj).getColumnExpression();
                            final String currAliasName = ((SelectColumn)obj).getAliasName();
                            if (colExpr != null && colExpr.size() == 1) {
                                final Object obj2 = colExpr.get(0);
                                if (obj2 instanceof TableColumn) {
                                    final String colName = ((TableColumn)obj2).getColumnName();
                                    if (colName != null && colName.equals(this.aliasName) && (currAliasName == null || (currAliasName != null && currAliasName.equals(this.aliasName)))) {
                                        this.aliasName += "_ADV";
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        sc.setAliasName(this.aliasName);
        return sc;
    }
    
    public SelectColumn toTimesTenSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectColumn sc = new SelectColumn();
        sc.setCommentClass(this.commentObj);
        if (this.commentObj != null) {
            this.commentObj.setSQLDialect(10);
        }
        int sql_dialect = 0;
        if (from_sqs != null) {
            sql_dialect = from_sqs.getSQLDialect();
        }
        String s_ce = new String();
        final Vector v_ce = new Vector();
        sc.setOpenBrace(this.OpenBrace);
        this.handleTableColumn(this.columnExpression, from_sqs, 10);
        for (int i = 0; i < this.columnExpression.size(); ++i) {
            if (this.columnExpression.elementAt(i) instanceof TableColumn) {
                final TableColumn tc = this.columnExpression.elementAt(i);
                tc.setObjectContext(this.context);
                v_ce.addElement(tc.toTimesTenSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
                this.columnExpression.elementAt(i).setObjectContext(this.context);
                final FunctionCalls fcs = this.columnExpression.elementAt(i);
                if (fcs.toString().trim().toLowerCase().startsWith("user_name(")) {
                    v_ce.addElement("USER");
                }
                else {
                    final FunctionCalls temp = fcs.toTimesTenSelect(to_sqs, from_sqs);
                    v_ce.addElement(temp);
                    this.columnExpression.setElementAt(temp, i);
                }
            }
            else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
                final SelectColumn temp2 = this.columnExpression.elementAt(i).toTimesTenSelect(to_sqs, from_sqs);
                v_ce.addElement(temp2);
                this.columnExpression.setElementAt(temp2, i);
            }
            else {
                if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
                    throw new ConvertException("\nCASE statements are not supported in TimesTen 5.1.21\n");
                }
                if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
                    final SelectQueryStatement temp3 = this.columnExpression.elementAt(i).toTimesTenSelect();
                    v_ce.addElement(temp3);
                    this.columnExpression.setElementAt(temp3, i);
                }
                else if (this.columnExpression.elementAt(i) instanceof String) {
                    s_ce = this.columnExpression.elementAt(i);
                    if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                        v_ce.addElement("TO_CHAR(SYSDATE,'HH:MI:SS')");
                    }
                    else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                        v_ce.addElement("SYSDATE");
                    }
                    else if (!s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                        if (s_ce.equalsIgnoreCase("CURRENT")) {
                            v_ce.addElement("SYSDATE");
                        }
                        else if (s_ce.equalsIgnoreCase("%")) {
                            this.createModFunction(v_ce, this.columnExpression, i);
                        }
                        else if (s_ce.trim().startsWith("$")) {
                            try {
                                final String dollarString = s_ce.substring(1);
                                final float numericValue = Float.parseFloat(dollarString);
                                v_ce.addElement(dollarString);
                            }
                            catch (final NumberFormatException e) {
                                v_ce.addElement(s_ce);
                            }
                        }
                        else if (!s_ce.equalsIgnoreCase("::")) {
                            if (!s_ce.equalsIgnoreCase("**")) {
                                if (!s_ce.equalsIgnoreCase("IS")) {
                                    if (s_ce.equalsIgnoreCase("=")) {
                                        if (v_ce.elementAt(i - 1) instanceof TableColumn) {
                                            sc.setAliasName(v_ce.elementAt(i - 1).getColumnName());
                                        }
                                        v_ce.setElementAt(" ", i - 1);
                                    }
                                    else if (s_ce.startsWith("@")) {
                                        v_ce.addElement(":" + s_ce.substring(1));
                                    }
                                    else if (s_ce.equalsIgnoreCase("+") || s_ce.equalsIgnoreCase("||")) {
                                        String newStr = null;
                                        if (i - 1 >= 0 && this.columnExpression.elementAt(i - 1).toString().trim().startsWith("'")) {
                                            newStr = this.columnExpression.elementAt(i - 1).toString();
                                            if (newStr.equalsIgnoreCase("''") && SwisSQLOptions.fromSybase) {
                                                newStr = "' '";
                                            }
                                            if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1) instanceof TableColumn) {
                                                v_ce.remove(v_ce.size() - 1);
                                                v_ce.addElement(this.concatFunction(newStr, this.columnExpression.elementAt(i + 1), to_sqs, from_sqs));
                                                this.columnExpression.remove(i + 1);
                                            }
                                            else if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1) instanceof String && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'")) {
                                                if (this.columnExpression.elementAt(i + 1).toString().equalsIgnoreCase("''") && SwisSQLOptions.fromSybase) {
                                                    this.columnExpression.setElementAt("' '", i + 1);
                                                }
                                                v_ce.remove(v_ce.size() - 1);
                                                v_ce.addElement(this.concatFunction(newStr, this.columnExpression.elementAt(i + 1), to_sqs, from_sqs));
                                                this.columnExpression.remove(i + 1);
                                            }
                                            else if (from_sqs != null) {
                                                if (from_sqs.getFromClause() == null && i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'")) {
                                                    if (this.columnExpression.elementAt(i + 1).toString().equalsIgnoreCase("''") && SwisSQLOptions.fromSybase) {
                                                        this.columnExpression.setElementAt("' '", i + 1);
                                                    }
                                                    v_ce.remove(v_ce.size() - 1);
                                                    v_ce.addElement(this.concatFunction(newStr, this.columnExpression.elementAt(i + 1), to_sqs, from_sqs));
                                                    this.columnExpression.remove(i + 1);
                                                }
                                                else {
                                                    v_ce.addElement(s_ce);
                                                }
                                            }
                                            else {
                                                v_ce.addElement(s_ce);
                                            }
                                        }
                                        else if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'")) {
                                            newStr = this.columnExpression.elementAt(i + 1).toString();
                                            if (newStr.equalsIgnoreCase("''") && SwisSQLOptions.fromSybase) {
                                                newStr = "' '";
                                            }
                                            if (v_ce.size() > 0 && v_ce.lastElement() instanceof FunctionCalls && v_ce.get(v_ce.size() - 1).getFunctionName().getColumnName().equalsIgnoreCase("CONCAT")) {
                                                v_ce.addElement(this.concatFunction(v_ce.lastElement(), newStr, to_sqs, from_sqs));
                                                v_ce.remove(v_ce.size() - 2);
                                                this.columnExpression.remove(i + 1);
                                            }
                                            else if (i - 1 >= 0 && this.columnExpression.elementAt(i - 1) instanceof TableColumn) {
                                                v_ce.remove(v_ce.size() - 1);
                                                v_ce.addElement(this.concatFunction(this.columnExpression.elementAt(i - 1), newStr, to_sqs, from_sqs));
                                                this.columnExpression.remove(i + 1);
                                            }
                                            else {
                                                v_ce.addElement(s_ce);
                                            }
                                        }
                                        else if (i - 1 >= 0 && this.columnExpression.elementAt(i - 1) instanceof TableColumn) {
                                            final String dtype = MetadataInfoUtil.getDatatypeName(from_sqs, this.columnExpression.elementAt(i - 1));
                                            if (dtype != null && dtype.toLowerCase().indexOf("char") != -1) {
                                                v_ce.remove(v_ce.size() - 1);
                                                v_ce.addElement(this.concatFunction(this.columnExpression.elementAt(i - 1), this.columnExpression.elementAt(i + 1), to_sqs, from_sqs));
                                                this.columnExpression.remove(i + 1);
                                            }
                                            else {
                                                v_ce.addElement(s_ce);
                                            }
                                        }
                                        else if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1) instanceof TableColumn) {
                                            final String dtype = MetadataInfoUtil.getDatatypeName(from_sqs, this.columnExpression.elementAt(i + 1));
                                            if (v_ce.size() > 0 && v_ce.lastElement() instanceof FunctionCalls && v_ce.get(v_ce.size() - 1).getFunctionName().getColumnName().equalsIgnoreCase("CONCAT")) {
                                                v_ce.addElement(this.concatFunction(v_ce.lastElement(), this.columnExpression.elementAt(i + 1), to_sqs, from_sqs));
                                                v_ce.remove(v_ce.size() - 2);
                                                this.columnExpression.remove(i + 1);
                                            }
                                            else if (dtype != null && dtype.toLowerCase().indexOf("char") != -1) {
                                                v_ce.remove(v_ce.size() - 1);
                                                v_ce.addElement(this.concatFunction(this.columnExpression.elementAt(i - 1), this.columnExpression.elementAt(i + 1), to_sqs, from_sqs));
                                                this.columnExpression.remove(i + 1);
                                            }
                                            else {
                                                v_ce.addElement(s_ce);
                                            }
                                        }
                                        else {
                                            v_ce.addElement(s_ce);
                                        }
                                    }
                                    else {
                                        v_ce.addElement(s_ce);
                                    }
                                }
                            }
                        }
                    }
                }
                else {
                    v_ce.addElement(this.columnExpression.elementAt(i));
                }
            }
        }
        sc.setColumnExpression(v_ce);
        sc.setCloseBrace(this.CloseBrace);
        sc.setIsAS(this.isAS);
        sc.setEndsWith(this.endsWith);
        if (sc != null && sc.getAliasName() == null) {
            if (this.aliasName != null && this.aliasName.charAt(0) == '\'') {
                sc.setAliasName(this.aliasName.replace('\'', '\"'));
            }
            else if (this.aliasName != null && this.aliasName.startsWith("[")) {
                String tempAlias = this.aliasName.substring(1, this.aliasName.length() - 1);
                if (tempAlias.indexOf(" ") != -1) {
                    sc.setAliasName("\"" + tempAlias + "\"");
                }
                else {
                    tempAlias = CustomizeUtil.objectNamesToQuotedIdentifier(tempAlias, SwisSQLUtils.getKeywords(10), null, 10);
                    sc.setAliasName(tempAlias);
                }
            }
            else {
                sc.setAliasName(this.aliasName = CustomizeUtil.objectNamesToQuotedIdentifier(this.aliasName, SwisSQLUtils.getKeywords(10), null, 10));
            }
        }
        return sc;
    }
    
    public SelectColumn toNetezzaSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectColumn sc = new SelectColumn();
        sc.setCommentClass(this.commentObj);
        final int sql_dialect = 0;
        if (from_sqs != null) {
            from_sqs.getSQLDialect();
        }
        String s_ce = new String();
        final Vector v_ce = new Vector();
        sc.setOpenBrace(this.OpenBrace);
        for (int i = 0; i < this.columnExpression.size(); ++i) {
            if (this.columnExpression.elementAt(i) instanceof TableColumn) {
                v_ce.addElement(this.columnExpression.elementAt(i).toNetezzaSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
                v_ce.addElement(this.columnExpression.elementAt(i).toNetezzaSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
                v_ce.addElement(this.columnExpression.elementAt(i).toNetezzaSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
                v_ce.addElement(this.columnExpression.elementAt(i).toNetezzaSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
                v_ce.addElement(this.columnExpression.elementAt(i).toNetezzaSelect());
            }
            else if (this.columnExpression.elementAt(i) instanceof String) {
                s_ce = this.columnExpression.elementAt(i);
                if (s_ce.charAt(0) == '\'') {
                    v_ce.addElement(s_ce.replace('\'', '\''));
                }
                else if (s_ce.equalsIgnoreCase("**")) {
                    this.createPowerFunction(v_ce, this.columnExpression, i, true);
                }
                else {
                    if (s_ce.equalsIgnoreCase("=") && sql_dialect == 5) {
                        throw new ConvertException();
                    }
                    if (s_ce.equalsIgnoreCase("=")) {
                        if (v_ce.elementAt(i - 1) instanceof TableColumn) {
                            sc.setAliasName(v_ce.elementAt(i - 1).getColumnName());
                        }
                        v_ce.setElementAt(" ", i - 1);
                    }
                    else if (s_ce.equalsIgnoreCase("IS")) {
                        this.createDecodeFunction(v_ce, this.columnExpression, i);
                        final FunctionCalls fc = v_ce.get(v_ce.size() - 1);
                        v_ce.setElementAt(fc.toNetezzaSelect(to_sqs, from_sqs), v_ce.size() - 1);
                    }
                    else {
                        v_ce.addElement(s_ce);
                    }
                }
            }
            else {
                v_ce.addElement(this.columnExpression.elementAt(i));
            }
        }
        sc.setColumnExpression(v_ce);
        sc.setCloseBrace(this.CloseBrace);
        sc.setIsAS(this.isAS);
        sc.setEndsWith(this.endsWith);
        if (sc != null && sc.getAliasName() == null) {
            if (this.aliasName != null && this.aliasName.charAt(0) == '\'') {
                sc.setAliasName(this.aliasName.replace('\'', '\"'));
            }
            else if (this.aliasName != null && this.aliasName.charAt(0) == '\"') {
                sc.setAliasName(this.aliasName);
            }
            else if (this.aliasName != null) {
                sc.setAliasName(this.aliasName);
            }
        }
        return sc;
    }
    
    private FunctionCalls concatFunction(Object firstArg, Object secondArg, final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        final FunctionCalls concatFC = new FunctionCalls();
        final Vector fnArgs = new Vector();
        final TableColumn tc = new TableColumn();
        tc.setColumnName("CONCAT");
        if (firstArg instanceof TableColumn) {
            firstArg = ((TableColumn)firstArg).toTimesTenSelect(to_sqs, from_sqs);
        }
        if (secondArg instanceof TableColumn) {
            secondArg = ((TableColumn)secondArg).toTimesTenSelect(to_sqs, from_sqs);
        }
        fnArgs.add(firstArg);
        fnArgs.add(secondArg);
        concatFC.setFunctionName(tc);
        concatFC.setFunctionArguments(fnArgs);
        return concatFC;
    }
    
    public void createPowerFunction(final Vector v_ce, final Vector columnExpression, final int i, final boolean isPower) {
        final SelectColumn sc_firstarg = new SelectColumn();
        final SelectColumn sc_secondarg = new SelectColumn();
        final Vector v_farg = new Vector();
        final TableColumn tc = new TableColumn();
        final FunctionCalls fc = new FunctionCalls();
        final Vector vec_firstarg = new Vector();
        final Vector vec_secondarg = new Vector();
        if (isPower) {
            tc.setColumnName("POWER");
        }
        else {
            tc.setColumnName("POW");
        }
        fc.setFunctionName(tc);
        vec_firstarg.addElement(columnExpression.elementAt(i - 1));
        v_ce.setElementAt(" ", i - 1);
        sc_firstarg.setColumnExpression(vec_firstarg);
        v_farg.addElement(sc_firstarg);
        vec_secondarg.addElement(columnExpression.elementAt(i + 1));
        columnExpression.setElementAt(" ", i + 1);
        sc_secondarg.setColumnExpression(vec_secondarg);
        v_farg.addElement(sc_secondarg);
        fc.setFunctionArguments(v_farg);
        v_ce.addElement(fc);
    }
    
    public void createXOREquivalentFunction(final Vector v_ce, final Vector columnExpression, final int i, final boolean isPower, final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs, final String str) throws ConvertException {
        final SelectColumn sc_firstarg = new SelectColumn();
        final SelectColumn sc_secondarg = new SelectColumn();
        final SelectColumn sc_thirdarg = new SelectColumn();
        final Vector vec_secondarg = new Vector();
        v_ce.addElement("(");
        v_ce.addElement("(");
        if (columnExpression.elementAt(i - 1) instanceof TableColumn) {
            v_ce.addElement(columnExpression.elementAt(i - 1).toOracleSelect(to_sqs, from_sqs));
        }
        else if (columnExpression.elementAt(i - 1) instanceof FunctionCalls) {
            v_ce.addElement(columnExpression.elementAt(i - 1).toOracleSelect(to_sqs, from_sqs));
        }
        else if (columnExpression.elementAt(i - 1) instanceof SelectColumn) {
            v_ce.addElement(columnExpression.elementAt(i - 1).toOracleSelect(to_sqs, from_sqs));
        }
        else if (columnExpression.elementAt(i - 1) instanceof CaseStatement) {
            v_ce.addElement(columnExpression.elementAt(i - 1).toOracleSelect(to_sqs, from_sqs));
        }
        else if (columnExpression.elementAt(i - 1) instanceof SelectQueryStatement) {
            v_ce.addElement(columnExpression.elementAt(i - 1).toOracleSelect());
        }
        else {
            v_ce.addElement(columnExpression.elementAt(i - 1));
        }
        v_ce.addElement("+");
        if (columnExpression.elementAt(i + 1) instanceof TableColumn) {
            v_ce.addElement(columnExpression.elementAt(i + 1).toOracleSelect(to_sqs, from_sqs));
        }
        else if (columnExpression.elementAt(i + 1) instanceof FunctionCalls) {
            v_ce.addElement(columnExpression.elementAt(i + 1).toOracleSelect(to_sqs, from_sqs));
        }
        else if (columnExpression.elementAt(i + 1) instanceof SelectColumn) {
            v_ce.addElement(columnExpression.elementAt(i - 1).toOracleSelect(to_sqs, from_sqs));
        }
        else if (columnExpression.elementAt(i + 1) instanceof CaseStatement) {
            v_ce.addElement(columnExpression.elementAt(i + 1).toOracleSelect(to_sqs, from_sqs));
        }
        else if (columnExpression.elementAt(i + 1) instanceof SelectQueryStatement) {
            v_ce.addElement(columnExpression.elementAt(i + 1).toOracleSelect());
        }
        else {
            v_ce.addElement(columnExpression.elementAt(i + 1));
        }
        v_ce.addElement(")");
        v_ce.addElement("-");
        final Vector v_farg = new Vector();
        final Vector vec_firstarg = new Vector();
        final TableColumn tc = new TableColumn();
        final FunctionCalls fc = new FunctionCalls();
        tc.setColumnName("BITAND");
        fc.setFunctionName(tc);
        if (columnExpression.elementAt(i - 1) instanceof TableColumn) {
            vec_firstarg.addElement(columnExpression.elementAt(i - 1).toOracleSelect(to_sqs, from_sqs));
        }
        else if (columnExpression.elementAt(i - 1) instanceof FunctionCalls) {
            vec_firstarg.addElement(columnExpression.elementAt(i - 1).toOracleSelect(to_sqs, from_sqs));
        }
        else if (columnExpression.elementAt(i - 1) instanceof SelectColumn) {
            vec_firstarg.addElement(columnExpression.elementAt(i - 1).toOracleSelect(to_sqs, from_sqs));
        }
        else if (columnExpression.elementAt(i - 1) instanceof CaseStatement) {
            vec_firstarg.addElement(columnExpression.elementAt(i - 1).toOracleSelect(to_sqs, from_sqs));
        }
        else if (columnExpression.elementAt(i - 1) instanceof SelectQueryStatement) {
            vec_firstarg.addElement(columnExpression.elementAt(i - 1).toOracleSelect());
        }
        else {
            vec_firstarg.addElement(columnExpression.elementAt(i - 1));
        }
        v_ce.setElementAt(" ", i - 1);
        sc_firstarg.setColumnExpression(vec_firstarg);
        v_farg.addElement(sc_firstarg);
        if (columnExpression.elementAt(i + 1) instanceof TableColumn) {
            vec_secondarg.addElement(columnExpression.elementAt(i + 1).toOracleSelect(to_sqs, from_sqs));
        }
        else if (columnExpression.elementAt(i + 1) instanceof FunctionCalls) {
            vec_secondarg.addElement(columnExpression.elementAt(i + 1).toOracleSelect(to_sqs, from_sqs));
        }
        else if (columnExpression.elementAt(i + 1) instanceof SelectColumn) {
            vec_secondarg.addElement(columnExpression.elementAt(i - 1).toOracleSelect(to_sqs, from_sqs));
        }
        else if (columnExpression.elementAt(i + 1) instanceof CaseStatement) {
            vec_secondarg.addElement(columnExpression.elementAt(i + 1).toOracleSelect(to_sqs, from_sqs));
        }
        else if (columnExpression.elementAt(i + 1) instanceof SelectQueryStatement) {
            vec_secondarg.addElement(columnExpression.elementAt(i + 1).toOracleSelect());
        }
        else {
            vec_secondarg.addElement(columnExpression.elementAt(i + 1));
        }
        columnExpression.setElementAt(" ", i + 1);
        sc_secondarg.setColumnExpression(vec_secondarg);
        v_farg.addElement(sc_secondarg);
        fc.setFunctionArguments(v_farg);
        v_ce.addElement(fc);
        if (str.equals("^")) {
            v_ce.addElement("*");
            v_ce.addElement("2");
        }
        v_ce.addElement(")");
    }
    
    public SelectColumn toANSISelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectColumn sc = new SelectColumn();
        sc.setCommentClass(this.commentObj);
        final int sql_dialect = 0;
        if (from_sqs != null) {
            from_sqs.getSQLDialect();
        }
        String s_ce = new String();
        final Vector v_ce = new Vector();
        sc.setOpenBrace(this.OpenBrace);
        for (int i = 0; i < this.columnExpression.size(); ++i) {
            if (this.columnExpression.elementAt(i) instanceof TableColumn) {
                v_ce.addElement(this.columnExpression.elementAt(i).toANSISelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
                if (this.columnExpression.elementAt(i).getFunctionName() == null) {
                    v_ce.addElement(this.columnExpression.elementAt(i));
                }
                else {
                    v_ce.addElement(this.columnExpression.elementAt(i).toANSISelect(to_sqs, from_sqs));
                }
            }
            else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
                v_ce.addElement(this.columnExpression.elementAt(i).toANSISelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
                v_ce.addElement(this.columnExpression.elementAt(i).toANSISelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
                v_ce.addElement(this.columnExpression.elementAt(i).toANSISelect());
            }
            else if (this.columnExpression.elementAt(i) instanceof String) {
                s_ce = this.columnExpression.elementAt(i);
                if (s_ce.charAt(0) == '\'' && SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects && FunctionCalls.functionArgsInSingleQuotesToDouble) {
                    v_ce.addElement(s_ce.replace('\'', '\"'));
                }
                else if (s_ce.equalsIgnoreCase("**")) {
                    this.createPowerFunction(v_ce, this.columnExpression, i, true);
                }
                else {
                    if (s_ce.equalsIgnoreCase("=") && sql_dialect == 5) {
                        throw new ConvertException();
                    }
                    if (s_ce.equalsIgnoreCase("=")) {
                        if (v_ce.elementAt(i - 1) instanceof TableColumn) {
                            sc.setAliasName(v_ce.elementAt(i - 1).getColumnName());
                        }
                        v_ce.setElementAt(" ", i - 1);
                    }
                    else if (s_ce.equalsIgnoreCase("IS")) {
                        this.createDecodeFunction(v_ce, this.columnExpression, i);
                        final FunctionCalls fc = v_ce.get(v_ce.size() - 1);
                        v_ce.setElementAt(fc.toANSISelect(to_sqs, from_sqs), v_ce.size() - 1);
                    }
                    else {
                        v_ce.addElement(s_ce);
                    }
                }
            }
            else {
                v_ce.addElement(this.columnExpression.elementAt(i));
            }
        }
        sc.setColumnExpression(v_ce);
        sc.setCloseBrace(this.CloseBrace);
        sc.setIsAS(this.isAS);
        sc.setEndsWith(this.endsWith);
        if (sc != null && sc.getAliasName() == null) {
            if (this.aliasName != null && this.aliasName.charAt(0) == '\'' && SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
                sc.setAliasName(this.aliasName.replace('\'', '\"'));
            }
            else if (this.aliasName != null && this.aliasName.charAt(0) == '\"') {
                sc.setAliasName(this.aliasName);
            }
            else if (this.aliasName != null && SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
                sc.setAliasName("\"" + this.aliasName + "\"");
            }
            else if (this.aliasName != null) {
                sc.setAliasName(this.aliasName);
            }
        }
        return sc;
    }
    
    public SelectColumn toTeradataSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectColumn sc = new SelectColumn();
        sc.setCommentClass(this.commentObj);
        final String teradataComTkn = "/*%SSTD%";
        if (this.commentObj != null) {
            final String teradataComment = this.commentObj.toString().trim();
            if (teradataComment.indexOf(teradataComTkn) != -1) {
                sc.setCommentClass(this.commentObj);
                this.commentObj.setComment(teradataComment.substring(teradataComTkn.length(), teradataComment.length() - 2));
                this.commentObj.setSQLDialect(12);
            }
        }
        final int sql_dialect = 0;
        if (from_sqs != null) {
            from_sqs.getSQLDialect();
        }
        String s_ce = new String();
        final Vector v_ce = new Vector();
        sc.setOpenBrace(this.OpenBrace);
        for (int i = 0; i < this.columnExpression.size(); ++i) {
            if (this.columnExpression.elementAt(i) instanceof TableColumn) {
                final TableColumn teradataTCN = this.columnExpression.elementAt(i).toTeradataSelect(to_sqs, from_sqs);
                v_ce.addElement(teradataTCN);
                if (to_sqs != null) {
                    to_sqs.addTableColumnToTableColumnList(teradataTCN);
                }
            }
            else {
                if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
                    if (this.columnExpression.elementAt(i).getFunctionName() == null) {
                        v_ce.addElement(this.columnExpression.elementAt(i));
                        continue;
                    }
                    try {
                        v_ce.addElement(this.columnExpression.elementAt(i).toTeradataSelect(to_sqs, from_sqs));
                        continue;
                    }
                    catch (final ConvertException ce) {
                        throw ce;
                    }
                }
                if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i).toTeradataSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
                    v_ce.addElement(this.columnExpression.elementAt(i).toTeradataSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
                    v_ce.addElement(this.columnExpression.elementAt(i).toTeradataSelect());
                }
                else if (this.columnExpression.elementAt(i) instanceof String) {
                    final String teradataCom = "/*%SSTD%";
                    s_ce = this.columnExpression.elementAt(i);
                    if (s_ce.equalsIgnoreCase("**")) {
                        v_ce.addElement(s_ce);
                    }
                    else if (s_ce.equalsIgnoreCase("+") || s_ce.equalsIgnoreCase("-")) {
                        v_ce.addElement(s_ce);
                        if (i > 0 && i < this.columnExpression.size() - 1) {
                            boolean isDateExpr = false;
                            final Object obj = this.columnExpression.get(i - 1);
                            if (obj instanceof TableColumn) {
                                if (SwisSQLUtils.getFunctionReturnType(((TableColumn)obj).getColumnName(), null).equalsIgnoreCase("date")) {
                                    isDateExpr = true;
                                }
                                else if (CastingUtil.getValueIgnoreCase(SwisSQLAPI.columnDatatypes, ((TableColumn)obj).getColumnName()) != null && CastingUtil.getValueIgnoreCase(SwisSQLAPI.columnDatatypes, ((TableColumn)obj).getColumnName()).toString().equalsIgnoreCase("timestamp")) {
                                    isDateExpr = true;
                                }
                            }
                            else if (obj instanceof FunctionCalls) {
                                final FunctionCalls fc = (FunctionCalls)obj;
                                final TableColumn tc = fc.getFunctionName();
                                if (tc != null) {
                                    final Vector args = fc.getFunctionArguments();
                                    if (tc != null && SwisSQLUtils.getFunctionReturnType(tc.getColumnName(), args).equalsIgnoreCase("date")) {
                                        isDateExpr = true;
                                    }
                                }
                            }
                            if (isDateExpr) {
                                final Object nextObj = this.columnExpression.get(i + 1);
                                if (nextObj instanceof String) {
                                    final Vector intervalVector = new Vector();
                                    final int increaseLoopCount = this.convertNumeralsToInterval(intervalVector, this.columnExpression.subList(i + 1, this.columnExpression.size()));
                                    if (intervalVector.size() > 0) {
                                        v_ce.addAll(intervalVector);
                                        i += increaseLoopCount;
                                    }
                                }
                            }
                        }
                    }
                    else {
                        if (s_ce.equalsIgnoreCase("=") && sql_dialect == 5) {
                            throw new ConvertException();
                        }
                        if (s_ce.equalsIgnoreCase("=")) {
                            if (v_ce.elementAt(i - 1) instanceof TableColumn) {
                                sc.setAliasName(v_ce.elementAt(i - 1).getColumnName());
                            }
                            v_ce.setElementAt(" ", i - 1);
                        }
                        else if (s_ce.equalsIgnoreCase("IS")) {
                            this.createDecodeFunction(v_ce, this.columnExpression, i);
                            final FunctionCalls fc2 = v_ce.get(v_ce.size() - 1);
                            v_ce.setElementAt(fc2.toTeradataSelect(to_sqs, from_sqs), v_ce.size() - 1);
                        }
                        else if (s_ce.indexOf(teradataCom) != -1) {
                            v_ce.addElement(s_ce.substring(teradataCom.length(), s_ce.length() - 2));
                        }
                        else if (s_ce.startsWith("--")) {
                            v_ce.add("/*" + s_ce + "*/");
                        }
                        else {
                            v_ce.addElement(s_ce);
                        }
                    }
                }
                else {
                    final Object otherObj = this.columnExpression.elementAt(i);
                    if (otherObj != null && (otherObj.toString().toLowerCase().startsWith("/*+") || otherObj.toString().toUpperCase().indexOf(teradataComTkn) != -1)) {
                        if (otherObj.toString().toUpperCase().indexOf(teradataComTkn) != -1) {
                            v_ce.addElement(otherObj.toString().substring(teradataComTkn.length() + 1, otherObj.toString().length() - 2));
                        }
                        else {
                            if (this.commentObj == null) {
                                this.commentObj = new CommentClass();
                            }
                            final ArrayList specTokenList = new ArrayList();
                            specTokenList.add(otherObj.toString());
                            this.commentObj.setSpecialToken(specTokenList);
                            sc.setCommentClass(this.commentObj);
                        }
                    }
                    else {
                        v_ce.addElement(this.columnExpression.elementAt(i));
                    }
                }
            }
        }
        sc.setColumnExpression(v_ce);
        sc.setCloseBrace(this.CloseBrace);
        sc.setIsAS(this.isAS);
        sc.setEndsWith(this.endsWith);
        sc.setAliasForExpression(this.aliasForExpression);
        if (this.aliasName != null && this.aliasName.startsWith("[")) {
            String tempAlias = this.aliasName.substring(1, this.aliasName.length() - 1);
            if (tempAlias.indexOf(" ") != -1) {
                sc.setAliasName("\"" + tempAlias + "\"");
            }
            else {
                tempAlias = CustomizeUtil.objectNamesToQuotedIdentifier(tempAlias, SwisSQLUtils.getKeywords("teradata"), null, -1);
                sc.setAliasName(tempAlias);
            }
        }
        else if (this.aliasName != null) {
            sc.setAliasName(this.aliasName = CustomizeUtil.objectNamesToQuotedIdentifier(this.aliasName, SwisSQLUtils.getKeywords("teradata"), null, -1));
        }
        return sc;
    }
    
    public SelectColumn toDB2Select(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectColumn sc = new SelectColumn();
        sc.setCommentClass(this.commentObj);
        final int sql_dialect = 0;
        boolean isDate = false;
        boolean openBraceisSet = false;
        boolean concatExpr = false;
        boolean two_or_more_date_column = false;
        int i = 0;
        if (from_sqs != null) {
            from_sqs.getSQLDialect();
        }
        String s_ce = new String();
        final Vector v_ce = new Vector();
        sc.setOpenBrace(this.OpenBrace);
        final ArrayList dateColsWithArithExpr = new ArrayList();
        boolean arithmeticExprAssignedToVar = false;
        boolean arithmeticExpr = false;
        if (this.columnExpression.contains("||")) {
            concatExpr = true;
        }
    Label_0464:
        for (int j = 0; j < this.columnExpression.size(); ++j) {
            if (this.columnExpression.get(j) instanceof String) {
                String str = this.columnExpression.get(j);
                str = str.trim();
                if (str.equals("*") || str.equals("/") || str.equals("+") || str.equals("-")) {
                    if (j != 0) {
                        final Object o = this.columnExpression.get(j - 1);
                        if (o instanceof TableColumn) {
                            final TableColumn tc = (TableColumn)o;
                            final String datatype = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                            if (datatype != null && datatype.toLowerCase().indexOf("char") != -1) {
                                break;
                            }
                        }
                        final Object o2 = this.columnExpression.get(j + 1);
                        if (o2 instanceof TableColumn) {
                            final TableColumn tc2 = (TableColumn)o2;
                            final String datatype2 = MetadataInfoUtil.getDatatypeName(from_sqs, tc2);
                            if (datatype2 != null && datatype2.toLowerCase().indexOf("char") != -1) {
                                break;
                            }
                        }
                        if (str.equals("+")) {
                            for (int k = 0; k < this.columnExpression.size(); ++k) {
                                final Object obj = this.columnExpression.get(k);
                                if (obj instanceof String) {
                                    final String temp = obj.toString();
                                    if (temp.startsWith("'") && temp.endsWith("'")) {
                                        break Label_0464;
                                    }
                                }
                                else if (obj instanceof FunctionCalls) {
                                    final FunctionCalls fc = (FunctionCalls)obj;
                                    final TableColumn tc3 = fc.getFunctionName();
                                    final Vector args = fc.getFunctionArguments();
                                    if (tc3 != null && SwisSQLUtils.getFunctionReturnType(tc3.getColumnName(), args).equalsIgnoreCase("string")) {
                                        break Label_0464;
                                    }
                                }
                            }
                        }
                        arithmeticExpr = true;
                        break;
                    }
                }
            }
        }
        if (this.targetDataType != null) {
            if (this.targetDataType.toLowerCase().startsWith("varchar")) {
                arithmeticExprAssignedToVar = arithmeticExpr;
            }
            if (!concatExpr && arithmeticExprAssignedToVar && !this.inArithmeticExpr) {
                v_ce.addElement("VARCHAR(CHAR(");
            }
        }
        if (concatExpr && arithmeticExpr) {
            v_ce.addElement("CHAR(");
        }
        for (i = 0; i < this.columnExpression.size(); ++i) {
            if (this.columnExpression.elementAt(i) instanceof TableColumn) {
                final TableColumn tc4 = this.columnExpression.elementAt(i);
                if (tc4.getColumnName() != null) {
                    String column_Name = tc4.getColumnName();
                    if (column_Name.equalsIgnoreCase("INCREMENT_BY")) {
                        column_Name = "INCREMENT";
                        tc4.setColumnName(column_Name);
                    }
                    else if (column_Name.equalsIgnoreCase("NEXTVAL")) {
                        final String table_Name_Str = tc4.getTableName();
                        if (table_Name_Str != null && !table_Name_Str.equals("")) {
                            tc4.setTableName("NEXTVAL FOR ");
                            tc4.setColumnName(table_Name_Str);
                        }
                        else {
                            tc4.setColumnName("NEXTVAL");
                        }
                    }
                    else if (column_Name.equalsIgnoreCase("CURRVAL")) {
                        final String table_Name_Str = tc4.getTableName();
                        tc4.setTableName("PREVVAL FOR ");
                        tc4.setColumnName(table_Name_Str);
                    }
                }
                String dataType = MetadataInfoUtil.getDatatypeName(from_sqs, tc4);
                if (dataType == null && SwisSQLAPI.variableDatatypeMapping != null) {
                    dataType = (String)CastingUtil.getValueIgnoreCase(SwisSQLAPI.variableDatatypeMapping, tc4.getResultString());
                }
                if (dataType != null && (dataType.trim().toLowerCase().indexOf("date") != -1 || dataType.trim().toLowerCase().indexOf("timestamp") != -1 || dataType.toLowerCase().indexOf("time") != -1)) {
                    isDate = true;
                    dateColsWithArithExpr.add(tc4);
                    this.addInDateColsIfPrevFnCall(v_ce, dateColsWithArithExpr);
                }
                if (!isDate) {
                    if (arithmeticExpr || this.inArithmeticExpr) {
                        if (this.targetDataType == null) {
                            tc4.setTargetDataType("DOUBLE");
                        }
                        else if (this.targetDataType != null && (!this.targetDataType.equalsIgnoreCase("varchar") || !this.targetDataType.equalsIgnoreCase("char"))) {
                            tc4.setTargetDataType("DOUBLE");
                        }
                        else {
                            tc4.setTargetDataType(this.targetDataType);
                        }
                    }
                    else {
                        tc4.setTargetDataType(this.targetDataType);
                    }
                }
                v_ce.addElement(tc4.toDB2Select(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
                final FunctionCalls fc2 = this.columnExpression.elementAt(i);
                fc2.setTargetDataType(this.targetDataType);
                final TableColumn tc5 = fc2.getFunctionName();
                if (tc5 != null && (tc5.getColumnName().equalsIgnoreCase("TO_DATE") || tc5.getColumnName().equalsIgnoreCase("ORACLE_TO_DATE"))) {
                    isDate = true;
                }
                if ((arithmeticExpr && !isDate) || (this.inArithmeticExpr && !isDate)) {
                    fc2.setInArithmeticExpression(true);
                }
                v_ce.addElement(this.columnExpression.elementAt(i).toDB2Select(to_sqs, from_sqs));
                if (tc5 != null && tc5.getColumnName().equalsIgnoreCase("NVL")) {
                    final String dType = this.getReturnDataTypeForCoalesce(fc2, "NVL", to_sqs, from_sqs);
                    if (dType != null && (dType.equalsIgnoreCase("timestamp") || dType.equalsIgnoreCase("date"))) {
                        isDate = true;
                        dateColsWithArithExpr.add(v_ce.get(v_ce.size() - 1));
                    }
                }
            }
            else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
                if (arithmeticExpr) {
                    this.columnExpression.elementAt(i).setInArithmeticExpression(true);
                }
                this.columnExpression.elementAt(i).setCorrespondingTableColumn(this.corrTableColumn);
                v_ce.addElement(this.columnExpression.elementAt(i).toDB2Select(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
                v_ce.addElement(this.columnExpression.elementAt(i).toDB2Select(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
                v_ce.addElement(this.columnExpression.elementAt(i).toDB2Select());
            }
            else if (this.columnExpression.elementAt(i) instanceof String) {
                s_ce = this.columnExpression.elementAt(i);
                this.rightTableColProcessed = false;
                this.leftTableColProcessed = false;
                if (s_ce.equalsIgnoreCase("||")) {
                    if (!arithmeticExpr) {
                        if (this.columnExpression.elementAt(i - 1) instanceof TableColumn) {
                            final TableColumn tableCol = this.columnExpression.elementAt(i - 1);
                            String sourceDataType = null;
                            if (SwisSQLAPI.variableDatatypeMapping != null) {
                                sourceDataType = CastingUtil.getDataType(SwisSQLAPI.variableDatatypeMapping.get(tableCol.getColumnName()));
                            }
                            if (sourceDataType != null && (sourceDataType.equalsIgnoreCase("CHAR") || sourceDataType.equalsIgnoreCase("VARCHAR"))) {
                                this.leftTableColProcessed = true;
                            }
                        }
                        if (this.columnExpression.elementAt(i + 1) instanceof TableColumn) {
                            final TableColumn tableCol = this.columnExpression.elementAt(i + 1);
                            String sourceDataType = null;
                            if (SwisSQLAPI.variableDatatypeMapping != null) {
                                sourceDataType = CastingUtil.getDataType(SwisSQLAPI.variableDatatypeMapping.get(tableCol.getColumnName()));
                            }
                            if (sourceDataType != null && (sourceDataType.equalsIgnoreCase("CHAR") || sourceDataType.equalsIgnoreCase("VARCHAR"))) {
                                this.rightTableColProcessed = true;
                            }
                        }
                        if (i > 0 && this.columnExpression.elementAt(i - 1) instanceof FunctionCalls) {
                            final FunctionCalls fc2 = this.columnExpression.get(i - 1);
                            if (fc2.getFunctionName() != null && fc2.getFunctionName().getColumnName().equalsIgnoreCase("CHAR")) {
                                if (this.columnExpression.size() > i + 1 && this.columnExpression.elementAt(i + 1) instanceof FunctionCalls) {
                                    final FunctionCalls fc3 = this.columnExpression.get(i + 1);
                                    if (fc3.getFunctionName() == null || !fc3.getFunctionName().getColumnName().equalsIgnoreCase("CHAR") || fc3.getFunctionArguments() == null || fc3.getFunctionArguments().size() != 1) {
                                        final FunctionCalls fc4 = new FunctionCalls();
                                        final TableColumn tc = new TableColumn();
                                        tc.setColumnName("CHAR");
                                        fc4.setFunctionName(tc);
                                        final Vector v1 = new Vector();
                                        final SelectColumn selCol = new SelectColumn();
                                        final Vector selExp = new Vector();
                                        selExp.add(this.columnExpression.get(i + 1));
                                        selCol.setColumnExpression(selExp);
                                        v1.add(selCol);
                                        fc4.setFunctionArguments(v1);
                                        this.columnExpression.set(i + 1, fc4);
                                    }
                                }
                                else if (!this.rightTableColProcessed) {
                                    final FunctionCalls fc3 = new FunctionCalls();
                                    final TableColumn tc6 = new TableColumn();
                                    tc6.setColumnName("CHAR");
                                    fc3.setFunctionName(tc6);
                                    final Vector v2 = new Vector();
                                    final SelectColumn selCol2 = new SelectColumn();
                                    final Vector selExp2 = new Vector();
                                    selExp2.add(this.columnExpression.get(i + 1));
                                    selCol2.setColumnExpression(selExp2);
                                    v2.add(selCol2);
                                    fc3.setFunctionArguments(v2);
                                    this.columnExpression.set(i + 1, fc3);
                                }
                            }
                            else {
                                this.checkConcatenationString(v_ce, this.columnExpression, i, 3);
                            }
                        }
                        else if (i == 1 && v_ce.elementAt(0).toString().trim().equals("CAST(NULL AS INT)")) {
                            v_ce.setElementAt("CAST(NULL AS CHAR)", 0);
                        }
                        else {
                            this.checkConcatenationString(v_ce, this.columnExpression, i, 3);
                        }
                        v_ce.addElement(s_ce);
                    }
                    else {
                        v_ce.addElement(") || CHAR(");
                    }
                }
                else if (s_ce.equalsIgnoreCase("CURRENT")) {
                    v_ce.addElement("CURRENT DATE");
                }
                else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
                    this.createPowerFunction(v_ce, this.columnExpression, i, true);
                }
                else if (s_ce.equalsIgnoreCase("%")) {
                    this.createModFunction(v_ce, this.columnExpression, i);
                }
                else if (s_ce.equalsIgnoreCase("::")) {
                    this.createCastFunction(v_ce, this.columnExpression, i);
                    final Object object = v_ce.get(v_ce.size() - 1);
                    if (object instanceof FunctionCalls) {
                        v_ce.set(v_ce.size() - 1, ((FunctionCalls)object).toDB2Select(to_sqs, from_sqs));
                    }
                }
                else {
                    if (s_ce.equalsIgnoreCase("=") && sql_dialect == 5) {
                        throw new ConvertException();
                    }
                    if (s_ce.equalsIgnoreCase("=")) {
                        if (!this.isSelectColFromUQS) {
                            if (v_ce.elementAt(i - 1) instanceof TableColumn) {
                                sc.setAliasName(v_ce.elementAt(i - 1).getColumnName());
                            }
                            v_ce.setElementAt(" ", i - 1);
                        }
                        else {
                            v_ce.addElement("=");
                        }
                    }
                    else if (s_ce.equalsIgnoreCase("IS")) {
                        this.createDecodeFunction(v_ce, this.columnExpression, i);
                        final FunctionCalls fc2 = v_ce.get(v_ce.size() - 1);
                        v_ce.setElementAt(fc2.toDB2Select(to_sqs, from_sqs), v_ce.size() - 1);
                    }
                    else if (s_ce.equalsIgnoreCase("NULL")) {
                        if (!this.insideDecodeFunction) {
                            if (this.targetDataTypeWithSize != null) {
                                v_ce.addElement("CAST(NULL AS " + this.targetDataTypeWithSize + ")");
                            }
                            else if (this.targetDataType != null) {
                                v_ce.addElement("CAST(NULL AS " + this.targetDataType + ")");
                            }
                            else {
                                v_ce.addElement("CAST(NULL AS INT)");
                            }
                        }
                        else {
                            v_ce.addElement("NULL");
                        }
                    }
                    else {
                        boolean flag = false;
                        if (s_ce.trim().equals("+")) {
                            Object leftExpr = null;
                            if (i - 1 >= 0) {
                                leftExpr = this.columnExpression.elementAt(i - 1);
                            }
                            final Object rightExpr = this.columnExpression.elementAt(i + 1);
                            if (leftExpr instanceof String || rightExpr instanceof String) {
                                if (leftExpr.toString().endsWith("'") || rightExpr.toString().startsWith("'")) {
                                    flag = true;
                                    v_ce.addElement(" CONCAT ");
                                }
                            }
                            else if (leftExpr instanceof FunctionCalls) {
                                final FunctionCalls fc5 = (FunctionCalls)leftExpr;
                                final TableColumn tc2 = fc5.getFunctionName();
                                final Vector args2 = fc5.getFunctionArguments();
                                Object o3 = null;
                                if (args2.size() >= 2) {
                                    o3 = args2.elementAt(1);
                                }
                                if (o3 instanceof Datatype) {
                                    if (o3 instanceof CharacterClass) {
                                        flag = true;
                                        v_ce.addElement(" CONCAT ");
                                    }
                                }
                                else if (tc2 != null && SwisSQLUtils.getFunctionReturnType(tc2.getColumnName(), args2).equalsIgnoreCase("string")) {
                                    flag = true;
                                    v_ce.addElement(" CONCAT ");
                                }
                            }
                            else if (leftExpr instanceof TableColumn) {
                                final TableColumn tc = (TableColumn)leftExpr;
                                String dataType2 = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                                if (dataType2 != null) {
                                    dataType2 = dataType2.toLowerCase();
                                }
                                if (dataType2 != null && dataType2.indexOf("char") != -1) {
                                    flag = true;
                                    v_ce.addElement(" CONCAT ");
                                }
                                else if (rightExpr instanceof FunctionCalls) {
                                    final FunctionCalls fc6 = (FunctionCalls)rightExpr;
                                    final TableColumn tc7 = fc6.getFunctionName();
                                    final Vector args3 = fc6.getFunctionArguments();
                                    if (tc7 != null && SwisSQLUtils.getFunctionReturnType(tc7.getColumnName(), args3).equalsIgnoreCase("string")) {
                                        flag = true;
                                        v_ce.addElement(" CONCAT ");
                                    }
                                }
                            }
                        }
                        if (isDate && s_ce != null && (s_ce.trim().equals("+") || s_ce.trim().equals("-")) && !openBraceisSet) {
                            boolean dateColWithNumCol = false;
                            if (this.columnExpression.elementAt(i + 1) != null && this.columnExpression.elementAt(i + 1) instanceof TableColumn) {
                                String colDataType = MetadataInfoUtil.getDatatypeName(from_sqs, this.columnExpression.elementAt(i + 1));
                                if (colDataType == null && SwisSQLAPI.variableDatatypeMapping != null) {
                                    colDataType = SwisSQLAPI.variableDatatypeMapping.get(this.columnExpression.elementAt(i + 1).getColumnName());
                                }
                                if (colDataType != null && (colDataType.toLowerCase().indexOf("num") != -1 || colDataType.toLowerCase().indexOf("int") != -1 || colDataType.toLowerCase().indexOf("decimal") != -1)) {
                                    dateColWithNumCol = true;
                                }
                            }
                            if (!(this.columnExpression.elementAt(i + 1) instanceof TableColumn)) {
                                if (dateColsWithArithExpr.size() < 2) {
                                    openBraceisSet = true;
                                    v_ce.addElement(s_ce);
                                    v_ce.addElement("(");
                                }
                                else {
                                    two_or_more_date_column = true;
                                    v_ce.addElement(s_ce);
                                }
                            }
                            else if (dateColWithNumCol) {
                                openBraceisSet = true;
                                v_ce.addElement(s_ce);
                                v_ce.addElement("(");
                            }
                            else {
                                two_or_more_date_column = true;
                                v_ce.addElement(s_ce);
                            }
                        }
                        else if (s_ce.trim().equals("/")) {
                            final Object obj2 = v_ce.lastElement();
                            final FunctionCalls fn = new FunctionCalls();
                            final TableColumn tc = new TableColumn();
                            tc.setColumnName("DECIMAL");
                            fn.setFunctionName(tc);
                            final Vector args4 = new Vector();
                            args4.add(obj2);
                            fn.setFunctionArguments(args4);
                            v_ce.setElementAt(fn, v_ce.size() - 1);
                            v_ce.addElement(s_ce);
                        }
                        else if (!flag) {
                            if (this.targetDataType != null && (this.targetDataType.equalsIgnoreCase("decimal") || this.targetDataType.toLowerCase().indexOf("num") != -1 || this.targetDataType.indexOf("int") != -1) && s_ce.trim().startsWith("'")) {
                                v_ce.addElement(CastingUtil.getDB2DataTypeCastedString(null, this.targetDataType, s_ce.substring(1, s_ce.length() - 1)));
                            }
                            else if (!arithmeticExpr && !s_ce.trim().equalsIgnoreCase("-")) {
                                if (!this.inArithmeticExpr) {
                                    v_ce.addElement(CastingUtil.getDB2DataTypeCastedString(null, this.targetDataType, s_ce));
                                }
                                else {
                                    v_ce.addElement(CastingUtil.getDB2DataTypeCastedString(null, "DOUBLE", s_ce));
                                }
                            }
                            else {
                                v_ce.addElement(s_ce);
                            }
                        }
                    }
                }
            }
            else {
                v_ce.addElement(this.columnExpression.elementAt(i));
            }
        }
        if (!concatExpr && arithmeticExprAssignedToVar && !this.inArithmeticExpr) {
            v_ce.addElement("))");
        }
        if (concatExpr && arithmeticExpr) {
            v_ce.addElement(")");
        }
        if (!openBraceisSet && this.columnExpression.contains("-")) {
            for (int m = 0; m < dateColsWithArithExpr.size(); ++m) {
                int l = 0;
                while (l < v_ce.size()) {
                    if (v_ce.get(l).toString().equalsIgnoreCase(dateColsWithArithExpr.get(m).toString())) {
                        if (two_or_more_date_column) {
                            final FunctionCalls newFn = new FunctionCalls();
                            final TableColumn newTC = new TableColumn();
                            final String dtCol = dateColsWithArithExpr.get(m).toString();
                            newTC.setColumnName("DAYS(DATE(" + dtCol + "))");
                            newFn.setFunctionName(newTC);
                            newFn.setOpenBracesForFunctionNameRequired(false);
                            v_ce.removeElementAt(l);
                            v_ce.insertElementAt(newFn, l);
                            break;
                        }
                        final FunctionCalls newFn = new FunctionCalls();
                        final TableColumn newTC = new TableColumn();
                        final String dtCol = dateColsWithArithExpr.get(m).toString();
                        newTC.setColumnName("(YEAR(" + dtCol + ")*365 + MONTH(" + dtCol + ")*31 + DAY(" + dtCol + ") + HOUR(" + dtCol + ")/24.0 + MINUTE(" + dtCol + ")/1440.0 + SECOND(" + dtCol + ")/86400.0)");
                        newFn.setFunctionName(newTC);
                        newFn.setOpenBracesForFunctionNameRequired(false);
                        v_ce.removeElementAt(l);
                        v_ce.insertElementAt(newFn, l);
                        break;
                    }
                    else {
                        ++l;
                    }
                }
            }
        }
        if (openBraceisSet) {
            v_ce.add(") DAYS");
        }
        sc.setColumnExpression(v_ce);
        sc.setCloseBrace(this.CloseBrace);
        sc.setIsAS(this.isAS);
        sc.setEndsWith(this.endsWith);
        if (sc != null && sc.getAliasName() == null) {
            if (this.aliasName != null && this.aliasName.charAt(0) == '\'') {
                sc.setAliasName(this.aliasName.replace('\'', '\"'));
            }
            else {
                sc.setAliasName(this.aliasName);
            }
        }
        return sc;
    }
    
    private void addInDateColsIfPrevFnCall(final Vector v_ce, final ArrayList dateColsWithArithExpr) {
        final int size = v_ce.size();
        if (size > 2) {
            final Object obj = v_ce.get(size - 2);
            if (obj instanceof String) {
                final String operator = (String)obj;
                if (operator.trim().equals("-")) {
                    final Object exprObj = v_ce.get(size - 3);
                    if (exprObj instanceof FunctionCalls) {
                        dateColsWithArithExpr.add(exprObj);
                    }
                }
            }
        }
    }
    
    public void checkConcatenationString(final Vector v_ce, final Vector columnExpression, final int i, final int database) {
        final SelectColumn leftSideCastFunction = new SelectColumn();
        final SelectColumn rightSideCastFunction = new SelectColumn();
        final Vector leftColExp = new Vector();
        final Vector rightColExp = new Vector();
        final Vector leftSideFunctionArguments = new Vector();
        final Vector rightSideFunctionArguments = new Vector();
        final TableColumn leftSideTC = new TableColumn();
        final FunctionCalls leftSideFC = new FunctionCalls();
        final TableColumn rightSideTC = new TableColumn();
        final FunctionCalls rightSideFC = new FunctionCalls();
        final SelectColumn parentLeftSideCastFunction = new SelectColumn();
        final Vector parentLeftColExp = new Vector();
        final Vector parentLeftSideFunctionArguments = new Vector();
        final TableColumn parentLeftSideTC = new TableColumn();
        final FunctionCalls parentLeftSideFC = new FunctionCalls();
        final SelectColumn parentRightSideCastFunction = new SelectColumn();
        final Vector parentRightColExp = new Vector();
        final Vector parentRightSideFunctionArguments = new Vector();
        final TableColumn parentRightSideTC = new TableColumn();
        final FunctionCalls parentRightSideFC = new FunctionCalls();
        if (database == 3) {
            leftSideTC.setColumnName("CHAR");
            parentLeftSideTC.setColumnName("RTRIM");
        }
        else {
            leftSideTC.setColumnName("CAST");
        }
        if (i - 1 <= v_ce.size() - 1) {
            final Object prevObject = v_ce.elementAt(i - 1);
            if ((!this.leftTableColProcessed || database != 3) && prevObject.toString().charAt(0) != '\'' && !prevObject.toString().toLowerCase().startsWith("cast") && !prevObject.toString().toLowerCase().startsWith("char(") && !prevObject.toString().toLowerCase().startsWith("rtrim(char(")) {
                leftColExp.add(prevObject);
                leftSideCastFunction.setColumnExpression(leftColExp);
                leftSideFunctionArguments.add(leftSideCastFunction);
                if (database == 2) {
                    leftSideFunctionArguments.add("VARCHAR");
                }
                leftSideFC.setFunctionArguments(leftSideFunctionArguments);
                leftSideFC.setFunctionName(leftSideTC);
                if (database != 3) {
                    leftSideFC.setAsDatatype("AS");
                }
                if (database == 3) {
                    parentLeftColExp.add(leftSideFC);
                    parentLeftSideCastFunction.setColumnExpression(parentLeftColExp);
                    parentLeftSideFunctionArguments.add(parentLeftSideCastFunction);
                    parentLeftSideFC.setFunctionArguments(parentLeftSideFunctionArguments);
                    parentLeftSideFC.setFunctionName(parentLeftSideTC);
                    v_ce.setElementAt(parentLeftSideFC, i - 1);
                }
                else {
                    v_ce.setElementAt(leftSideFC, i - 1);
                }
            }
        }
        if (database == 3) {
            rightSideTC.setColumnName("CHAR");
            parentRightSideTC.setColumnName("RTRIM");
        }
        else {
            rightSideTC.setColumnName("CAST");
        }
        final Object nextObject = columnExpression.elementAt(i + 1);
        if ((!this.rightTableColProcessed || database != 3) && nextObject.toString().charAt(0) != '\'' && !nextObject.toString().toLowerCase().startsWith("cast") && !nextObject.toString().toLowerCase().startsWith("char(") && !nextObject.toString().toLowerCase().startsWith("rtrim(char(")) {
            rightColExp.add(nextObject);
            rightSideCastFunction.setColumnExpression(rightColExp);
            rightSideFunctionArguments.add(rightSideCastFunction);
            if (database == 2) {
                rightSideFunctionArguments.add("VARCHAR");
            }
            rightSideFC.setFunctionArguments(rightSideFunctionArguments);
            rightSideFC.setFunctionName(rightSideTC);
            if (database != 3) {
                rightSideFC.setAsDatatype("AS");
            }
            if (database == 3) {
                parentRightColExp.add(rightSideFC);
                parentRightSideCastFunction.setColumnExpression(parentRightColExp);
                parentRightSideFunctionArguments.add(parentRightSideCastFunction);
                parentRightSideFC.setFunctionArguments(parentRightSideFunctionArguments);
                parentRightSideFC.setFunctionName(parentRightSideTC);
                columnExpression.setElementAt(parentRightSideFC, i + 1);
            }
            else {
                columnExpression.setElementAt(rightSideFC, i + 1);
            }
        }
    }
    
    public void checkConcatenationString(final Vector v_ce, final Vector columnExpression, final int i, final boolean bool) {
        if (v_ce.elementAt(i - 1) instanceof String) {
            String s_fce = v_ce.elementAt(i - 1);
            if (s_fce.charAt(0) != '\'' && s_fce.indexOf("cast") != 0) {
                s_fce = "CAST ( " + s_fce + " AS VARCHAR ) ";
                v_ce.setElementAt(s_fce, i - 1);
            }
        }
        else if (v_ce.elementAt(i - 1) instanceof FunctionCalls) {
            final FunctionCalls fc = v_ce.elementAt(i - 1);
            if (fc.toString().indexOf("cast") != 0) {
                final String s_fce2 = "CAST ( " + fc.toString() + " AS VARCHAR ) ";
                v_ce.setElementAt(s_fce2, i - 1);
            }
        }
        else if (v_ce.elementAt(i - 1) instanceof TableColumn) {
            final TableColumn tc = v_ce.elementAt(i - 1);
            if (tc.toString().indexOf("cast") != 0 && tc.toString().charAt(0) != '\'') {
                final String s_fce2 = "CAST ( " + tc.toString() + " AS VARCHAR ) ";
                v_ce.setElementAt(s_fce2, i - 1);
            }
        }
        if (columnExpression.elementAt(i + 1) instanceof String) {
            String s_fce = columnExpression.elementAt(i + 1);
            if (s_fce.charAt(0) != '\'' && s_fce.indexOf("cast") != 0) {
                s_fce = "CAST ( " + s_fce + " AS VARCHAR ) ";
                columnExpression.setElementAt(s_fce, i + 1);
            }
        }
        else if (columnExpression.elementAt(i + 1) instanceof FunctionCalls) {
            final FunctionCalls fc = columnExpression.elementAt(i + 1);
            if (fc.toString().indexOf("cast") != 0) {
                final String s_fce2 = "CAST ( " + fc.toString() + " AS VARCHAR ) ";
                columnExpression.setElementAt(s_fce2, i + 1);
            }
        }
        else if (columnExpression.elementAt(i + 1) instanceof TableColumn) {
            final TableColumn tc = columnExpression.elementAt(i + 1);
            if (tc.toString().indexOf("cast") != 0 && tc.toString().charAt(0) != '\'') {
                final String s_fce2 = "CAST ( " + tc.toString() + " AS VARCHAR ) ";
                columnExpression.setElementAt(s_fce2, i + 1);
            }
        }
    }
    
    public SelectColumn toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectColumn sc = new SelectColumn();
        sc.setCommentClass(this.commentObj);
        final int sql_dialect = 0;
        int division = 0;
        if (from_sqs != null) {
            from_sqs.getSQLDialect();
        }
        String s_ce = new String();
        final Vector v_ce = new Vector();
        String[] keywords = null;
        if (SwisSQLUtils.getKeywords(1) != null) {
            keywords = SwisSQLUtils.getKeywords(1);
        }
        sc.setOpenBrace(this.OpenBrace);
        if (!this.isOrderItem && !from_sqs.isOracleLive()) {
            this.handleTableColumn(this.columnExpression, from_sqs, 1);
        }
        for (int cESize = this.columnExpression.size(), i = 0; i < cESize; ++i) {
            if (this.columnExpression.elementAt(i) instanceof TableColumn) {
                TableColumn tc = this.columnExpression.elementAt(i);
                if (this.fromUQS != null) {
                    tc.setFromUQS(this.fromUQS);
                }
                tc.setObjectContext(this.context);
                if (tc.getColumnName() != null) {
                    String column_Name = tc.getColumnName();
                    if (column_Name.equalsIgnoreCase("INCREMENT")) {
                        column_Name = "INCREMENT_BY";
                    }
                    tc.setColumnName(column_Name);
                }
                tc = tc.toOracleSelect(to_sqs, from_sqs);
                if (SwisSQLOptions.SetSybaseDoubleQuotedLiteralsToSingleQuotes && tc.getOwnerName() == null && tc.getTableName() == null) {
                    String colName = tc.getColumnName();
                    if (i != 0 || cESize <= 1 || !(this.columnExpression.elementAt(i + 1) instanceof String)) {
                        if (colName.startsWith("\"")) {
                            colName = colName.replaceAll("'", "\"");
                            colName = "'" + colName.substring(1, colName.length() - 1) + "'";
                            tc.setColumnName(colName);
                        }
                    }
                }
                v_ce.addElement(tc);
            }
            else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
                this.columnExpression.elementAt(i).setObjectContext(this.context);
                final FunctionCalls fcs = this.columnExpression.elementAt(i);
                final TableColumn tcol = fcs.getFunctionName();
                if (tcol != null && tcol.getTableName() != null && tcol.getTableName().equalsIgnoreCase("DBO")) {
                    tcol.setTableName(null);
                }
                if (this.columnExpression.elementAt(i).toString().trim().toLowerCase().startsWith("user_name(")) {
                    final SelectQueryStatement userSQS = new SelectQueryStatement();
                    final SelectStatement userSS = new SelectStatement();
                    userSS.setSelectClause("SELECT");
                    final Vector userSCV = new Vector();
                    final SelectColumn userSC = new SelectColumn();
                    final Vector colExpr = new Vector();
                    final TableColumn userTC = new TableColumn();
                    userTC.setColumnName("USER");
                    colExpr.add(userTC);
                    userSC.setColumnExpression(colExpr);
                    userSCV.add(userSC);
                    userSS.setSelectItemList(userSCV);
                    userSQS.setSelectStatement(userSS);
                    final FunctionCalls userFun = this.columnExpression.elementAt(i);
                    final Vector userFunArgs = userFun.getFunctionArguments();
                    if (userFunArgs != null && userFunArgs.size() == 1) {
                        final WhereExpression userWe = new WhereExpression();
                        final Vector userWIV = new Vector();
                        final WhereItem userWI = new WhereItem();
                        final WhereColumn userLWC = new WhereColumn();
                        final Vector userLWColExpr = new Vector();
                        userLWColExpr.add("UID");
                        userLWC.setColumnExpression(userLWColExpr);
                        userWI.setLeftWhereExp(userLWC);
                        final WhereColumn userRWC = new WhereColumn();
                        final Vector userRWColExpr = new Vector();
                        userRWColExpr.add(userFunArgs.get(0));
                        userRWC.setColumnExpression(userRWColExpr);
                        userWI.setRightWhereExp(userRWC);
                        userWI.setOperator("=");
                        userWIV.add(userWI);
                        userWe.setWhereItem(userWIV);
                        userSQS.setWhereExpression(userWe);
                    }
                    final String temp = userSQS.toOracleString();
                    v_ce.addElement("(");
                    v_ce.addElement(temp);
                    v_ce.addElement(")");
                    this.columnExpression.setElementAt(temp, i);
                }
                else {
                    final FunctionCalls fc = this.columnExpression.elementAt(i);
                    boolean isNtextFunction = false;
                    if (fc.getFunctionName() != null && fc.getFunctionName().getColumnName().equalsIgnoreCase("CONVERT") && fc.getFunctionArguments().get(0) instanceof CharacterClass) {
                        final CharacterClass characterclass = fc.getFunctionArguments().get(0);
                        final String datatypeName = characterclass.getDatatypeName();
                        if (datatypeName.equalsIgnoreCase("NTEXT")) {
                            v_ce.addElement(fc.getFunctionArguments().get(1));
                            isNtextFunction = true;
                        }
                    }
                    if (!isNtextFunction) {
                        final FunctionCalls temp2 = fc.toOracleSelect(to_sqs, from_sqs);
                        v_ce.addElement(temp2);
                    }
                }
            }
            else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
                final SelectColumn temp3 = this.columnExpression.elementAt(i).toOracleSelect(to_sqs, from_sqs);
                v_ce.addElement(temp3);
            }
            else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
                final CaseStatement temp4 = this.columnExpression.elementAt(i).toOracleSelect(to_sqs, from_sqs);
                v_ce.addElement(temp4);
            }
            else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
                final SelectQueryStatement temp5 = this.columnExpression.elementAt(i).toOracleSelect();
                temp5.setObjectContext(this.context);
                v_ce.addElement(temp5);
            }
            else if (this.columnExpression.elementAt(i) instanceof String) {
                s_ce = this.columnExpression.elementAt(i);
                if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).toString().equalsIgnoreCase(".*")) {
                    s_ce = CustomizeUtil.objectNamesToQuotedIdentifier(s_ce, keywords, null, 1);
                }
                if (s_ce != null && ((s_ce.startsWith("[") && s_ce.endsWith("]")) || (s_ce.startsWith("`") && s_ce.endsWith("`")))) {
                    s_ce = s_ce.substring(1, s_ce.length() - 1).trim();
                    if (SwisSQLOptions.retainQuotedIdentifierForOracle || s_ce.indexOf(32) != -1) {
                        s_ce = "\"" + s_ce + "\"";
                    }
                }
                if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                    v_ce.addElement("TO_CHAR(SYSDATE,'HH:MI:SS')");
                }
                else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                    v_ce.addElement("SYSDATE");
                }
                else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                    v_ce.addElement("SYSTIMESTAMP");
                }
                else if (s_ce.equalsIgnoreCase("CURRENT")) {
                    v_ce.addElement("SYSDATE");
                }
                else if (s_ce.equalsIgnoreCase("%")) {
                    this.createModFunction(v_ce, this.columnExpression, i);
                    final Object object = v_ce.get(v_ce.size() - 1);
                    if (object instanceof FunctionCalls) {
                        v_ce.set(v_ce.size() - 1, ((FunctionCalls)object).toOracleSelect(to_sqs, from_sqs));
                    }
                }
                else if (s_ce.equalsIgnoreCase("&")) {
                    String first_arg = "";
                    if (i >= 1) {
                        if (this.columnExpression.elementAt(i - 1) instanceof SelectColumn) {
                            first_arg = this.columnExpression.elementAt(i - 1).toOracleSelect(to_sqs, from_sqs).toString();
                        }
                        else if (this.columnExpression.elementAt(i - 1) instanceof FunctionCalls) {
                            first_arg = this.columnExpression.elementAt(i - 1).toOracleSelect(to_sqs, from_sqs).toString();
                        }
                        else {
                            for (int h = i - 1; h >= 0; --h) {
                                String s = null;
                                if (this.columnExpression.elementAt(h) instanceof TableColumn) {
                                    s = this.columnExpression.elementAt(h).toOracleSelect(to_sqs, from_sqs).toString();
                                }
                                else {
                                    s = this.columnExpression.elementAt(h).toString();
                                }
                                if (s.trim().equals("+") || s.trim().equals("-") || s.trim().equals("*") || s.trim().equals("/") || s.trim().equals("%") || s.trim().equals("**") || s.trim().equals("^") || s.trim().equals("|") || s.trim().equals("&") || s.trim().equals(" ")) {
                                    break;
                                }
                                if (this.columnExpression.elementAt(h) instanceof SelectColumn) {
                                    break;
                                }
                                first_arg += this.columnExpression.elementAt(h).toString();
                            }
                        }
                    }
                    else {
                        for (int h = i - 1; h >= 0; --h) {
                            String s = null;
                            if (this.columnExpression.elementAt(h) instanceof TableColumn) {
                                s = this.columnExpression.elementAt(h).toOracleSelect(to_sqs, from_sqs).toString();
                            }
                            else {
                                s = this.columnExpression.elementAt(h).toString();
                            }
                            if (s.trim().equals("+") || s.trim().equals("-") || s.trim().equals("*") || s.trim().equals("/") || s.trim().equals("%") || s.trim().equals("**") || s.trim().equals("^") || s.trim().equals("|") || s.trim().equals("&") || s.trim().equals(" ")) {
                                break;
                            }
                            if (this.columnExpression.elementAt(h) instanceof SelectColumn) {
                                break;
                            }
                            first_arg += this.columnExpression.elementAt(h).toString();
                        }
                    }
                    final String bitand = this.createBitAnd(first_arg, this.columnExpression, i + 1, to_sqs, from_sqs);
                    v_ce.add(bitand);
                    v_ce.setElementAt(" ", i - 1);
                }
                else if (s_ce.trim().startsWith("$")) {
                    try {
                        final String dollarString = s_ce.substring(1);
                        final float numericValue = Float.parseFloat(dollarString);
                        v_ce.addElement(dollarString);
                    }
                    catch (final NumberFormatException e) {
                        v_ce.addElement(s_ce);
                    }
                }
                else if (s_ce.equalsIgnoreCase("::")) {
                    this.createCastFunction(v_ce, this.columnExpression, i);
                    final Object object = v_ce.get(v_ce.size() - 1);
                    if (object instanceof FunctionCalls) {
                        v_ce.set(v_ce.size() - 1, ((FunctionCalls)object).toOracleSelect(to_sqs, from_sqs));
                    }
                }
                else if (s_ce.equalsIgnoreCase("**")) {
                    this.createPowerFunction(v_ce, this.columnExpression, i, true);
                }
                else if (s_ce.equalsIgnoreCase("|")) {
                    String first_arg = "";
                    if (i >= 1) {
                        if (this.columnExpression.elementAt(i - 1) instanceof SelectColumn) {
                            first_arg = this.columnExpression.elementAt(i - 1).toOracleSelect(to_sqs, from_sqs).toString();
                        }
                        else {
                            for (int h = i - 1; h >= 0; --h) {
                                String s = null;
                                if (this.columnExpression.elementAt(h) instanceof TableColumn) {
                                    s = this.columnExpression.elementAt(h).toOracleSelect(to_sqs, from_sqs).toString();
                                }
                                else {
                                    s = this.columnExpression.elementAt(h).toString();
                                }
                                if (s.trim().equals("+") || s.trim().equals("-") || s.trim().equals("*") || s.trim().equals("/") || s.trim().equals("%") || s.trim().equals("**") || s.trim().equals("^") || s.trim().equals("|") || s.trim().equals("&") || s.trim().equals(" ")) {
                                    break;
                                }
                                if (this.columnExpression.elementAt(h) instanceof SelectColumn) {
                                    break;
                                }
                                first_arg += this.columnExpression.elementAt(h).toString();
                            }
                        }
                    }
                    else {
                        for (int h = i - 1; h >= 0; --h) {
                            String s = null;
                            if (this.columnExpression.elementAt(h) instanceof TableColumn) {
                                s = this.columnExpression.elementAt(h).toOracleSelect(to_sqs, from_sqs).toString();
                            }
                            else {
                                s = this.columnExpression.elementAt(h).toString();
                            }
                            if (s.trim().equals("+") || s.trim().equals("-") || s.trim().equals("*") || s.trim().equals("/") || s.trim().equals("%") || s.trim().equals("**") || s.trim().equals("^") || s.trim().equals("|") || s.trim().equals("&") || s.trim().equals(" ")) {
                                break;
                            }
                            if (this.columnExpression.elementAt(h) instanceof SelectColumn) {
                                break;
                            }
                            first_arg += this.columnExpression.elementAt(h).toString();
                        }
                    }
                    final String bitor = this.createBitOR(first_arg, this.columnExpression, i + 1, to_sqs, from_sqs);
                    v_ce.add(bitor);
                    v_ce.setElementAt(" ", i - 1);
                }
                else if (s_ce.equalsIgnoreCase("~")) {
                    v_ce.setElementAt(" ", i);
                    final Object vv = v_ce.get(i + 1);
                    if (vv instanceof TableColumn) {
                        final String s2 = v_ce.get(i + 1).toOracleSelect(to_sqs, from_sqs).toString();
                        v_ce.setElementAt("((0 - " + s2 + ") + 1)", i + 1);
                    }
                    if (vv instanceof SelectColumn) {
                        final String s2 = v_ce.get(i + 1).toOracleSelect(to_sqs, from_sqs).toString();
                        v_ce.setElementAt("((0 - " + s2 + ") + 1)", i + 1);
                    }
                }
                else if (s_ce.equalsIgnoreCase("^")) {
                    String first_arg = "";
                    if (i >= 1) {
                        if (this.columnExpression.elementAt(i - 1) instanceof SelectColumn) {
                            first_arg = this.columnExpression.elementAt(i - 1).toOracleSelect(to_sqs, from_sqs).toString();
                        }
                        else {
                            for (int h = i - 1; h >= 0; --h) {
                                String s = null;
                                if (this.columnExpression.elementAt(h) instanceof TableColumn) {
                                    s = this.columnExpression.elementAt(h).toOracleSelect(to_sqs, from_sqs).toString();
                                }
                                else {
                                    s = this.columnExpression.elementAt(h).toString();
                                }
                                if (s.trim().equals("+") || s.trim().equals("-") || s.trim().equals("*") || s.trim().equals("/") || s.trim().equals("%") || s.trim().equals("**") || s.trim().equals("^") || s.trim().equals("|") || s.trim().equals("&") || s.trim().equals(" ")) {
                                    break;
                                }
                                if (this.columnExpression.elementAt(h) instanceof SelectColumn) {
                                    break;
                                }
                                first_arg += this.columnExpression.elementAt(h).toString();
                            }
                        }
                    }
                    else {
                        for (int h = i - 1; h >= 0; --h) {
                            String s = null;
                            if (this.columnExpression.elementAt(h) instanceof TableColumn) {
                                s = this.columnExpression.elementAt(h).toOracleSelect(to_sqs, from_sqs).toString();
                            }
                            else {
                                s = this.columnExpression.elementAt(h).toString();
                            }
                            if (s.trim().equals("+") || s.trim().equals("-") || s.trim().equals("*") || s.trim().equals("/") || s.trim().equals("%") || s.trim().equals("**") || s.trim().equals("^") || s.trim().equals("|") || s.trim().equals("&") || s.trim().equals(" ")) {
                                break;
                            }
                            if (this.columnExpression.elementAt(h) instanceof SelectColumn) {
                                break;
                            }
                            first_arg += this.columnExpression.elementAt(h).toString();
                        }
                    }
                    final String bitxor = this.createBitXOR(first_arg, this.columnExpression, i + 1, to_sqs, from_sqs);
                    v_ce.add(bitxor);
                    v_ce.setElementAt(" ", i - 1);
                }
                else if (s_ce.equalsIgnoreCase(">") || s_ce.equalsIgnoreCase("<")) {
                    String first_arg = "";
                    String second_arg = "";
                    if (this.columnExpression.elementAt(i - 1) instanceof SelectColumn) {
                        first_arg = this.columnExpression.elementAt(i - 1).toOracleSelect(to_sqs, from_sqs).toString();
                    }
                    else if (this.columnExpression.elementAt(i - 1) instanceof FunctionCalls) {
                        first_arg = this.columnExpression.elementAt(i - 1).toOracleSelect(to_sqs, from_sqs).toString();
                    }
                    else if (this.columnExpression.elementAt(i - 1) instanceof TableColumn) {
                        first_arg = this.columnExpression.elementAt(i - 1).toOracleSelect(to_sqs, from_sqs).toString();
                    }
                    if (this.columnExpression.elementAt(i + 1) instanceof SelectColumn) {
                        second_arg = this.columnExpression.elementAt(i + 1).toOracleSelect(to_sqs, from_sqs).toString();
                    }
                    else if (this.columnExpression.elementAt(i + 1) instanceof FunctionCalls) {
                        second_arg = this.columnExpression.elementAt(i + 1).toOracleSelect(to_sqs, from_sqs).toString();
                    }
                    else if (this.columnExpression.elementAt(i + 1) instanceof TableColumn) {
                        first_arg = this.columnExpression.elementAt(i + 1).toOracleSelect(to_sqs, from_sqs).toString();
                    }
                    final String caseStmt = "CASE WHEN " + first_arg + " " + s_ce + " " + second_arg + " THEN 1 ELSE 0 END";
                    v_ce.add(caseStmt);
                    v_ce.setElementAt(" ", i - 1);
                    this.columnExpression.clear();
                }
                else if (s_ce.equalsIgnoreCase("IS")) {
                    this.createDecodeFunction(v_ce, this.columnExpression, i);
                }
                else if (s_ce.equalsIgnoreCase("=") && sql_dialect == 5) {
                    this.createDecodeFunction(v_ce, this.columnExpression, i);
                }
                else if (s_ce.equalsIgnoreCase("=")) {
                    if (v_ce.elementAt(i - 1) instanceof TableColumn) {
                        sc.setAliasName(v_ce.elementAt(i - 1).getColumnName());
                    }
                    v_ce.setElementAt(" ", i - 1);
                }
                else if (s_ce.startsWith("@")) {
                    v_ce.addElement(":" + s_ce.substring(1));
                }
                else if (s_ce.startsWith("0x") || s_ce.startsWith("0X")) {
                    final String str = s_ce.substring(2);
                    v_ce.addElement("HEX_TO_NUMBER('" + str + "')");
                }
                else if (s_ce.equalsIgnoreCase("+") && ((i - 1 >= 0 && (this.columnExpression.elementAt(i - 1).toString().trim().startsWith("'-") || this.columnExpression.elementAt(i - 1).toString().trim().endsWith("-'"))) || (i + 1 < this.columnExpression.size() && (this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'-") || this.columnExpression.elementAt(i + 1).toString().trim().endsWith("-'"))))) {
                    v_ce.addElement("||");
                }
                else if (s_ce.equalsIgnoreCase("+")) {
                    String newStr = null;
                    if (i - 1 >= 0 && this.columnExpression.elementAt(i - 1).toString().trim().startsWith("'")) {
                        newStr = this.columnExpression.elementAt(i - 1).toString();
                        if (newStr.equals("''") || StringFunctions.replaceAll("", " ", newStr.substring(1, newStr.length() - 1)).length() == 0) {
                            v_ce.addElement("||");
                        }
                        else if (i + 1 < this.columnExpression.size() && (this.columnExpression.elementAt(i + 1) instanceof TableColumn || this.columnExpression.elementAt(i + 1) instanceof FunctionCalls || this.columnExpression.elementAt(i + 1) instanceof SelectColumn)) {
                            v_ce.addElement("||");
                        }
                        else if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1) instanceof String && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'")) {
                            v_ce.addElement("||");
                        }
                        else if (from_sqs != null) {
                            if (from_sqs.getFromClause() == null && i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'")) {
                                v_ce.addElement("||");
                            }
                            else {
                                v_ce.addElement(s_ce);
                            }
                        }
                        else {
                            v_ce.addElement(s_ce);
                        }
                    }
                    else if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'")) {
                        newStr = this.columnExpression.elementAt(i + 1).toString();
                        if (newStr.equals("''") || StringFunctions.replaceAll("", " ", newStr.substring(1, newStr.length() - 1)).length() == 0) {
                            v_ce.addElement("||");
                        }
                        else if (i - 1 >= 0 && (this.columnExpression.elementAt(i - 1) instanceof TableColumn || this.columnExpression.elementAt(i - 1) instanceof FunctionCalls || this.columnExpression.elementAt(i + 1) instanceof SelectColumn)) {
                            v_ce.addElement("||");
                        }
                        else {
                            v_ce.addElement(s_ce);
                        }
                    }
                    else if (i - 1 >= 0 && this.columnExpression.elementAt(i - 1) instanceof TableColumn) {
                        this.addConcatenationSymbol(from_sqs, v_ce, s_ce, this.columnExpression.elementAt(i - 1));
                    }
                    else if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1) instanceof TableColumn) {
                        this.addConcatenationSymbol(from_sqs, v_ce, s_ce, this.columnExpression.elementAt(i + 1));
                    }
                    else if (i - 1 >= 0 && this.columnExpression.elementAt(i - 1) instanceof FunctionCalls) {
                        this.addConcatenationSymbol(from_sqs, v_ce, s_ce, this.columnExpression.elementAt(i - 1));
                    }
                    else if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1) instanceof FunctionCalls) {
                        this.addConcatenationSymbol(from_sqs, v_ce, s_ce, this.columnExpression.elementAt(i + 1));
                    }
                    else {
                        v_ce.addElement(s_ce);
                    }
                }
                else if (s_ce.equalsIgnoreCase("CONCAT")) {
                    v_ce.addElement("||");
                }
                else if (s_ce.trim().equals("/")) {
                    v_ce.addElement("/");
                    division = 1;
                }
                else if (s_ce.trim().startsWith("--") || s_ce.trim().startsWith("/*")) {
                    final CommentClass cm = new CommentClass();
                    cm.setComment(s_ce);
                    sc.setCommentClass(cm);
                }
                else {
                    v_ce.addElement(s_ce);
                }
            }
            else {
                v_ce.addElement(this.columnExpression.elementAt(i));
            }
            if (division > 0) {
                if (division > 2) {
                    final Object obj = v_ce.get(i);
                    final String val = "-" + obj.toString();
                    v_ce.remove(i);
                    v_ce.remove(i - 1);
                    v_ce.add("");
                    String decimalVal = "(NULLIF(" + val + ",0.0) * 1.0)";
                    if (obj instanceof String) {
                        decimalVal = StringFunctions.getDecimalString(val, decimalVal);
                    }
                    else if (obj instanceof SelectQueryStatement) {
                        decimalVal = "(NULLIF((" + val + "), 0.0) * 1.0)";
                    }
                    v_ce.add(decimalVal);
                    division = 0;
                }
                else if (division > 1) {
                    final Object obj = v_ce.get(i);
                    final String val = obj.toString();
                    if (val.equals("-")) {
                        ++division;
                    }
                    else {
                        v_ce.remove(i);
                        String decimalVal = "(NULLIF(" + val + ",0.0) * 1.0)";
                        if (obj instanceof String) {
                            decimalVal = StringFunctions.getDecimalString(val, decimalVal);
                        }
                        else if (obj instanceof SelectQueryStatement) {
                            decimalVal = "(NULLIF((" + val + "), 0.0) * 1.0)";
                        }
                        v_ce.add(decimalVal);
                        division = 0;
                    }
                }
                else {
                    ++division;
                }
            }
        }
        sc.setColumnExpression(v_ce);
        sc.setCloseBrace(this.CloseBrace);
        sc.setIsAS(this.isAS);
        sc.setEndsWith(this.endsWith);
        if (sc != null && sc.getAliasName() == null) {
            this.aliasName = CustomizeUtil.objectNamesToQuotedIdentifier(this.aliasName, SwisSQLUtils.getKeywords(1), null, 1);
            if (this.aliasName != null && this.aliasName.charAt(0) == '\'') {
                sc.setAliasName(this.aliasName.replace('\'', '\"'));
            }
            else if (this.aliasName != null && this.aliasName.startsWith("[")) {
                final String tempAlias = this.aliasName.substring(1, this.aliasName.length() - 1);
                if (SwisSQLOptions.retainQuotedIdentifierForOracle || tempAlias.indexOf(" ") != -1) {
                    sc.setAliasName("\"" + tempAlias + "\"");
                }
                else {
                    sc.setAliasName(tempAlias);
                }
            }
            else {
                sc.setAliasName(this.aliasName);
            }
        }
        return sc;
    }
    
    public void convertOrdinalNumberToColumn(final SelectQueryStatement from_sqs, final Vector v_ce) {
        final String ordinalNumber = v_ce.elementAt(0);
        if (ordinalNumber.matches("^[1-9][0-9]*")) {
            final Vector tc = from_sqs.getSelectStatement().getSelectItemList().elementAt(Integer.parseInt(ordinalNumber) - 1).getColumnExpression();
            this.setColumnExpression(tc);
        }
    }
    
    private boolean isDecimal(final String input) {
        try {
            final double a = Double.parseDouble(input);
            return true;
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    private void handleTableColumn(final Vector colExpr, final SelectQueryStatement from_sqs, final int target) {
        for (int i = 0; i < colExpr.size(); ++i) {
            if (colExpr.elementAt(i) instanceof TableColumn) {
                if (i + 1 >= colExpr.size() || !colExpr.elementAt(i + 1).toString().equalsIgnoreCase("=") || !colExpr.elementAt(i).toString().startsWith("\"")) {
                    final TableColumn tc = colExpr.elementAt(i);
                    if (tc.getColumnName() != null && tc.getTableName() == null) {
                        String columnName = tc.getColumnName();
                        if (columnName.trim().length() > 0) {
                            if (columnName.equalsIgnoreCase("\"\"")) {
                                if (target == 10) {
                                    if (SwisSQLOptions.fromSybase) {
                                        tc.setColumnName("' '");
                                    }
                                    else {
                                        tc.setColumnName("''");
                                    }
                                }
                                else {
                                    tc.setColumnName("''");
                                }
                            }
                            else if (columnName.startsWith("\"")) {
                                boolean modify = true;
                                FromClause fc = null;
                                if (from_sqs != null) {
                                    fc = from_sqs.getFromClause();
                                }
                                else if (this.fromUQS != null) {
                                    fc = new FromClause();
                                    final Vector newFromItems = new Vector();
                                    fc.setFromItemList(newFromItems);
                                    final TableExpression tExpr = this.fromUQS.getTableExpression();
                                    if (tExpr != null) {
                                        final ArrayList tabClauseList = tExpr.getTableClauseList();
                                        if (tabClauseList != null) {
                                            for (int j = 0; j < tabClauseList.size(); ++j) {
                                                final Object obj = tabClauseList.get(j);
                                                if (obj instanceof TableClause) {
                                                    final TableObject tabObj = ((TableClause)obj).getTableObject();
                                                    final String tableName = tabObj.getTableName();
                                                    final FromTable fromTab = new FromTable();
                                                    fromTab.setTableName(tableName);
                                                    newFromItems.add(fromTab);
                                                }
                                            }
                                        }
                                    }
                                    final FromClause uqsFC = this.fromUQS.getFromClause();
                                    if (uqsFC != null) {
                                        final Vector fromItems = uqsFC.getFromItemList();
                                        if (fromItems != null) {
                                            for (int k = 0; k < fromItems.size(); ++k) {
                                                newFromItems.add(fromItems.get(k));
                                            }
                                        }
                                    }
                                }
                                else {
                                    modify = false;
                                }
                                if (fc != null) {
                                    final String tempCol = columnName.trim().substring(1, columnName.trim().length() - 1);
                                    final Vector fromItems2 = fc.getFromItemList();
                                    if (fromItems2 != null) {
                                        for (int l = 0; l < fromItems2.size(); ++l) {
                                            Object obj2 = fromItems2.get(l);
                                            if (obj2 instanceof FromTable) {
                                                obj2 = ((FromTable)obj2).getTableName();
                                                if (obj2 instanceof String) {
                                                    String tableName2 = (String)obj2;
                                                    final int index = tableName2.lastIndexOf(".");
                                                    if (index != -1) {
                                                        tableName2 = tableName2.substring(index + 1);
                                                    }
                                                    if (tableName2.startsWith("\"") || tableName2.startsWith("[") || tableName2.startsWith("`")) {
                                                        tableName2 = tableName2.substring(1, tableName2.length() - 1);
                                                    }
                                                    final ArrayList colList = (ArrayList)CastingUtil.getValueIgnoreCase(SwisSQLAPI.tableColumnListMetadata, tableName2);
                                                    if (colList == null) {
                                                        break;
                                                    }
                                                    if (CastingUtil.ContainsIgnoreCase(colList, tempCol)) {
                                                        modify = false;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (modify) {
                                    columnName = StringFunctions.replaceFirst("'", "\"", columnName);
                                    columnName = columnName.trim().substring(0, columnName.trim().length() - 1) + "'";
                                    tc.setColumnName(columnName);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void addConcatenationSymbol(final SelectQueryStatement from_sqs, final Vector v_ce, final String s_ce, final TableColumn tc) {
        final String dtype = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
        final String newStr = tc.getColumnName();
        if (dtype != null && dtype.toLowerCase().indexOf("char") != -1) {
            v_ce.addElement("||");
        }
        else if (dtype == null && newStr.startsWith("\"") && newStr.endsWith("\"")) {
            final String name_only = newStr.substring(1, newStr.length() - 1);
            if (name_only.trim().equals("")) {
                v_ce.addElement("||");
            }
            else {
                v_ce.addElement(s_ce);
            }
        }
        else {
            v_ce.addElement(s_ce);
        }
    }
    
    private void addConcatenationSymbol(final SelectQueryStatement from_sqs, final Vector v_ce, final String s_ce, final FunctionCalls fc) {
        final String fnName = fc.getFunctionName().getColumnName().toLowerCase();
        if (fnName.equalsIgnoreCase("suser_sname") || fnName.equalsIgnoreCase("user_name") || fnName.equalsIgnoreCase("to_char") || fnName.equalsIgnoreCase("substr") || fnName.equalsIgnoreCase("app_name") || fnName.equalsIgnoreCase("sys_context")) {
            v_ce.addElement("||");
        }
        else {
            v_ce.addElement(s_ce);
        }
    }
    
    private String createBitAnd(final String first_arg, final Vector sec_arg, final int startIndex, final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        String bitand = "";
        String sec_arg_str = "";
        final Object val = sec_arg.elementAt(startIndex);
        for (int i = startIndex; i < sec_arg.size(); ++i) {
            final Object data = sec_arg.elementAt(i);
            if (data instanceof String) {
                final String str = data.toString();
                if (str.equals("&")) {
                    final String f_arg = "BITAND(" + first_arg + "," + sec_arg_str + ")";
                    sec_arg.setElementAt(" ", i - 1);
                    sec_arg.setElementAt(" ", i);
                    return this.createBitAnd(f_arg, sec_arg, i + 1, to_sqs, from_sqs);
                }
                if (str.equals("|")) {
                    final String f_arg = "BITAND(" + first_arg + "," + sec_arg_str + ")";
                    sec_arg.setElementAt(" ", i - 1);
                    sec_arg.setElementAt(" ", i);
                    return this.createBitOR(f_arg, sec_arg, i + 1, to_sqs, from_sqs);
                }
                if (str.equals("^")) {
                    final String f_arg = "BITAND(" + first_arg + "," + sec_arg_str + ")";
                    sec_arg.setElementAt(" ", i - 1);
                    sec_arg.setElementAt(" ", i);
                    return this.createBitXOR(f_arg, sec_arg, i + 1, to_sqs, from_sqs);
                }
                if (str.equals("~")) {
                    final String f_arg = "BITAND(" + first_arg + "," + sec_arg + ")";
                    sec_arg.setElementAt(" ", i - 1);
                    sec_arg.setElementAt(" ", i);
                    final Object ne = sec_arg.get(i + 1);
                    if (ne instanceof String) {
                        final String ss = ne.toString();
                        return "BITAND(" + first_arg + ",((0-" + ne + ")+1))";
                    }
                    if (ne instanceof TableColumn) {
                        final TableColumn col = ((TableColumn)ne).toOracleSelect(to_sqs, from_sqs);
                        sec_arg_str += col.toString();
                        return "BITAND(" + first_arg + ",((0-" + sec_arg_str + ")+1))";
                    }
                }
                else {
                    if (str.trim().equals("+") || str.trim().equals("-") || str.trim().equals("*") || str.trim().equals("/") || str.trim().equals("%") || str.trim().equals("**") || str.trim().equals("^") || str.trim().equals("|")) {
                        break;
                    }
                    if (str.trim().equals("&")) {
                        break;
                    }
                    sec_arg_str += str;
                }
            }
            else if (data instanceof TableColumn) {
                final TableColumn col2 = ((TableColumn)data).toOracleSelect(to_sqs, from_sqs);
                sec_arg_str += col2.toString();
            }
            else if (data instanceof FunctionCalls) {
                final FunctionCalls col3 = ((FunctionCalls)data).toOracleSelect(to_sqs, from_sqs);
                sec_arg_str += col3.toString();
            }
            else if (data instanceof SelectQueryStatement) {
                final SelectQueryStatement sqs = ((SelectQueryStatement)data).toOracleSelect();
                sec_arg_str += sqs.toString();
            }
            else if (data instanceof CaseStatement) {
                final CaseStatement cs = ((CaseStatement)data).toOracleSelect(to_sqs, from_sqs);
                sec_arg_str += cs.toString();
            }
            else {
                if (!(data instanceof SelectColumn)) {
                    break;
                }
                final SelectColumn sel = ((SelectColumn)data).toOracleSelect(to_sqs, from_sqs);
                sec_arg_str += sel.toString();
            }
        }
        sec_arg.setElementAt(" ", startIndex - 1);
        this.columnExpression.setElementAt(" ", startIndex);
        bitand = bitand + "BITAND(" + first_arg + "," + sec_arg_str + ")";
        return bitand;
    }
    
    private String createBitXOR(final String first_arg, final Vector sec_arg, final int startIndex, final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        String bitxor = "";
        String sec_arg_str = "";
        final Object val = sec_arg.elementAt(startIndex);
        for (int i = startIndex; i < sec_arg.size(); ++i) {
            final Object data = sec_arg.elementAt(i);
            if (data instanceof String) {
                final String str = data.toString();
                if (str.equals("^")) {
                    final String f_arg = "((" + first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + ")*2)";
                    sec_arg.setElementAt(" ", i - 1);
                    sec_arg.setElementAt(" ", i);
                    return this.createBitXOR(f_arg, sec_arg, i + 1, to_sqs, from_sqs);
                }
                if (str.equals("|")) {
                    final String f_arg = "((" + first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + ")*2)";
                    sec_arg.setElementAt(" ", i - 1);
                    sec_arg.setElementAt(" ", i);
                    return this.createBitOR(f_arg, sec_arg, i + 1, to_sqs, from_sqs);
                }
                if (str.equals("&")) {
                    final String f_arg = "((" + first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + ")*2)";
                    sec_arg.setElementAt(" ", i - 1);
                    sec_arg.setElementAt(" ", i);
                    return this.createBitAnd(f_arg, sec_arg, i + 1, to_sqs, from_sqs);
                }
                if (str.equals("~")) {
                    final String f_arg = "BITAND(" + first_arg + "," + sec_arg + ")";
                    sec_arg.setElementAt(" ", i - 1);
                    sec_arg.setElementAt(" ", i);
                    final Object ne = sec_arg.get(i + 1);
                    if (ne instanceof String) {
                        final String ss = ne.toString();
                        sec_arg_str = "((0 - " + ne + ") + 1)";
                        return first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + "))*2";
                    }
                    if (ne instanceof TableColumn) {
                        final TableColumn col = ((TableColumn)ne).toOracleSelect(to_sqs, from_sqs);
                        sec_arg_str = "((0 - " + sec_arg_str + col.toString() + ") + 1";
                        return first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + "))*2";
                    }
                }
                else {
                    if (str.trim().equals("+") || str.trim().equals("-") || str.trim().equals("*") || str.trim().equals("/") || str.trim().equals("%") || str.trim().equals("**") || str.trim().equals("^") || str.trim().equals("|")) {
                        break;
                    }
                    if (str.trim().equals("&")) {
                        break;
                    }
                    sec_arg_str += str;
                }
            }
            else if (data instanceof TableColumn) {
                final TableColumn col2 = ((TableColumn)data).toOracleSelect(to_sqs, from_sqs);
                sec_arg_str += col2.toString();
            }
            else if (data instanceof FunctionCalls) {
                final FunctionCalls col3 = ((FunctionCalls)data).toOracleSelect(to_sqs, from_sqs);
                sec_arg_str += col3.toString();
            }
            else if (data instanceof SelectQueryStatement) {
                final SelectQueryStatement sqs = ((SelectQueryStatement)data).toOracleSelect();
                sec_arg_str += sqs.toString();
            }
            else if (data instanceof CaseStatement) {
                final CaseStatement cs = ((CaseStatement)data).toOracleSelect(to_sqs, from_sqs);
                sec_arg_str += cs.toString();
            }
            else {
                if (!(data instanceof SelectColumn)) {
                    break;
                }
                final SelectColumn sel = ((SelectColumn)data).toOracleSelect(to_sqs, from_sqs);
                sec_arg_str += sel.toString();
            }
        }
        sec_arg.setElementAt(" ", startIndex - 1);
        this.columnExpression.setElementAt(" ", startIndex);
        bitxor = bitxor + "((" + first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + ")*2)";
        return bitxor;
    }
    
    private String createBitOR(final String first_arg, final Vector sec_arg, final int startIndex, final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        String bitor = "";
        String sec_arg_str = "";
        final Object val = sec_arg.elementAt(startIndex);
        for (int i = startIndex; i < sec_arg.size(); ++i) {
            final Object data = sec_arg.elementAt(i);
            if (data instanceof String) {
                final String str = data.toString();
                if (str.equals("|")) {
                    final String f_arg = "((" + first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + "))";
                    sec_arg.setElementAt(" ", i - 1);
                    sec_arg.setElementAt(" ", i);
                    return this.createBitOR(f_arg, sec_arg, i + 1, to_sqs, from_sqs);
                }
                if (str.equals("^")) {
                    final String f_arg = "((" + first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + "))";
                    sec_arg.setElementAt(" ", i - 1);
                    sec_arg.setElementAt(" ", i);
                    return this.createBitXOR(f_arg, sec_arg, i + 1, to_sqs, from_sqs);
                }
                if (str.equals("&")) {
                    final String f_arg = "((" + first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + "))";
                    sec_arg.setElementAt(" ", i - 1);
                    sec_arg.setElementAt(" ", i);
                    return this.createBitAnd(f_arg, sec_arg, i + 1, to_sqs, from_sqs);
                }
                if (str.equals("~")) {
                    sec_arg.setElementAt(" ", i - 1);
                    sec_arg.setElementAt(" ", i);
                    final Object ne = sec_arg.get(i + 1);
                    if (ne instanceof String) {
                        final String ss = ne.toString();
                        sec_arg_str = "((0 - " + ne + ") + 1)";
                        return first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + "))";
                    }
                    if (ne instanceof TableColumn) {
                        final TableColumn col = ((TableColumn)ne).toOracleSelect(to_sqs, from_sqs);
                        sec_arg_str = "((0 - " + sec_arg_str + col.toString() + ") + 1";
                        return first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + "))";
                    }
                }
                else {
                    if (str.trim().equals("+") || str.trim().equals("-") || str.trim().equals("*") || str.trim().equals("/") || str.trim().equals("%") || str.trim().equals("**") || str.trim().equals("^") || str.trim().equals("|")) {
                        break;
                    }
                    if (str.trim().equals("&")) {
                        break;
                    }
                    sec_arg_str += str;
                }
            }
            else if (data instanceof TableColumn) {
                final TableColumn col2 = ((TableColumn)data).toOracleSelect(to_sqs, from_sqs);
                sec_arg_str += col2.toString();
            }
            else if (data instanceof FunctionCalls) {
                final FunctionCalls col3 = ((FunctionCalls)data).toOracleSelect(to_sqs, from_sqs);
                sec_arg_str += col3.toString();
            }
            else if (data instanceof SelectQueryStatement) {
                final SelectQueryStatement sqs = ((SelectQueryStatement)data).toOracleSelect();
                sec_arg_str += sqs.toString();
            }
            else if (data instanceof CaseStatement) {
                final CaseStatement cs = ((CaseStatement)data).toOracleSelect(to_sqs, from_sqs);
                sec_arg_str += cs.toString();
            }
            else {
                if (!(data instanceof SelectColumn)) {
                    break;
                }
                final SelectColumn sel = ((SelectColumn)data).toOracleSelect(to_sqs, from_sqs);
                sec_arg_str += sel.toString();
            }
        }
        sec_arg.setElementAt(" ", startIndex - 1);
        this.columnExpression.setElementAt(" ", startIndex);
        bitor = bitor + "((" + first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + "))";
        return bitor;
    }
    
    public SelectColumn toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectColumn sc = new SelectColumn();
        final int sql_dialect = 0;
        sc.setCommentClass(this.commentObj);
        if (from_sqs != null) {
            from_sqs.getSQLDialect();
        }
        String s_ce = new String();
        final Vector v_ce = new Vector();
        sc.setOpenBrace(this.OpenBrace);
        boolean removeOperatorsWhenNullIsEncountered = false;
        int nullPosition = 0;
        for (int i = 0; i < this.columnExpression.size(); ++i) {
            if (this.columnExpression.elementAt(i) instanceof TableColumn) {
                v_ce.addElement(this.columnExpression.elementAt(i).toInformixSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
                v_ce.addElement(this.columnExpression.elementAt(i).toInformixSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
                v_ce.addElement(this.columnExpression.elementAt(i).toInformixSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
                v_ce.addElement(this.columnExpression.elementAt(i).toInformixSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
                v_ce.addElement(this.columnExpression.elementAt(i).toInformixSelect());
            }
            else if (this.columnExpression.elementAt(i) instanceof String) {
                s_ce = this.columnExpression.elementAt(i);
                if (s_ce.equalsIgnoreCase("||")) {
                    this.checkConcatenationString(v_ce, this.columnExpression, i, 6);
                    v_ce.addElement(s_ce);
                }
                else if (s_ce.equalsIgnoreCase("%")) {
                    this.createModFunction(v_ce, this.columnExpression, i);
                }
                else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
                    this.createPowerFunction(v_ce, this.columnExpression, i, false);
                }
                else if (s_ce.equalsIgnoreCase("IS")) {
                    this.createDecodeFunction(v_ce, this.columnExpression, i);
                }
                else if (s_ce.equalsIgnoreCase("=") && sql_dialect == 5) {
                    this.createDecodeFunction(v_ce, this.columnExpression, i);
                }
                else if (s_ce.equalsIgnoreCase("=")) {
                    if (v_ce.elementAt(i - 1) instanceof TableColumn) {
                        sc.setAliasName(v_ce.elementAt(i - 1).getColumnName());
                    }
                    v_ce.setElementAt(" ", i - 1);
                }
                else if (s_ce.equalsIgnoreCase("NULL")) {
                    removeOperatorsWhenNullIsEncountered = true;
                    v_ce.addElement("CAST(NULL AS INT)");
                    nullPosition = v_ce.size();
                }
                else if (s_ce.trim().startsWith("--") || s_ce.trim().startsWith("/*")) {
                    final CommentClass cm = new CommentClass();
                    cm.setComment(s_ce);
                    sc.setCommentClass(cm);
                }
                else {
                    v_ce.addElement(s_ce);
                }
            }
            else {
                if (this.columnExpression.elementAt(i).toString().trim().startsWith("--") || this.columnExpression.elementAt(i).toString().trim().startsWith("/*")) {
                    sc.getCommentClass().setComment(s_ce);
                }
                v_ce.addElement(this.columnExpression.elementAt(i));
            }
        }
        if (v_ce != null && removeOperatorsWhenNullIsEncountered) {
            while (nullPosition != 0 || nullPosition != v_ce.size()) {
                if (nullPosition > 1) {
                    if (!v_ce.get(nullPosition - 2).equalsIgnoreCase("+") && !v_ce.get(nullPosition - 2).equalsIgnoreCase("-") && !v_ce.get(nullPosition - 2).equalsIgnoreCase("*") && !v_ce.get(nullPosition - 2).equalsIgnoreCase("||") && !v_ce.get(nullPosition - 2).equalsIgnoreCase("/")) {
                        continue;
                    }
                    v_ce.removeElementAt(nullPosition - 2);
                    v_ce.removeElementAt(nullPosition - 3);
                    nullPosition -= 2;
                }
                else {
                    if (nullPosition >= v_ce.size()) {
                        break;
                    }
                    if (!v_ce.get(nullPosition).equalsIgnoreCase("+") && !v_ce.get(nullPosition).equalsIgnoreCase("-") && !v_ce.get(nullPosition).equalsIgnoreCase("*") && !v_ce.get(nullPosition).equalsIgnoreCase("||") && !v_ce.get(nullPosition).equalsIgnoreCase("/")) {
                        continue;
                    }
                    v_ce.removeElementAt(nullPosition + 1);
                    v_ce.removeElementAt(nullPosition);
                }
            }
        }
        sc.setColumnExpression(v_ce);
        sc.setCloseBrace(this.CloseBrace);
        sc.setIsAS(this.isAS);
        sc.setEndsWith(this.endsWith);
        if (sc != null && sc.getAliasName() == null) {
            sc.setAliasName(this.aliasName);
        }
        return sc;
    }
    
    public void createModFunction(final Vector v_ce, final Vector columnExpression, final int i) {
        final SelectColumn sc_firstarg = new SelectColumn();
        final SelectColumn sc_secondarg = new SelectColumn();
        final Vector v_farg = new Vector();
        final TableColumn tc = new TableColumn();
        final FunctionCalls fc = new FunctionCalls();
        final Vector vec_firstarg = new Vector();
        final Vector vec_secondarg = new Vector();
        tc.setColumnName("MOD");
        fc.setFunctionName(tc);
        vec_firstarg.addElement(v_ce.get(v_ce.size() - 2));
        sc_firstarg.setColumnExpression(vec_firstarg);
        v_farg.addElement(sc_firstarg);
        vec_secondarg.addElement(v_ce.get(v_ce.size() - 1));
        sc_secondarg.setColumnExpression(vec_secondarg);
        v_farg.addElement(sc_secondarg);
        fc.setFunctionArguments(v_farg);
        v_ce.addElement(fc);
    }
    
    public void createBitAndFunction(final Vector v_ce, final Vector columnExpression, final int i) {
        final SelectColumn sc_firstarg = new SelectColumn();
        final SelectColumn sc_secondarg = new SelectColumn();
        final Vector v_farg = new Vector();
        final TableColumn tc = new TableColumn();
        final FunctionCalls fc = new FunctionCalls();
        final Vector vec_firstarg = new Vector();
        final Vector vec_secondarg = new Vector();
        tc.setColumnName("BITAND");
        fc.setFunctionName(tc);
        vec_firstarg.addElement(columnExpression.elementAt(i - 1));
        v_ce.setElementAt(" ", i - 1);
        sc_firstarg.setColumnExpression(vec_firstarg);
        v_farg.addElement(sc_firstarg);
        vec_secondarg.addElement(columnExpression.elementAt(i + 1));
        columnExpression.setElementAt(" ", i + 1);
        sc_secondarg.setColumnExpression(vec_secondarg);
        v_farg.addElement(sc_secondarg);
        fc.setFunctionArguments(v_farg);
        v_ce.addElement(fc);
    }
    
    public void createCastFunction(final Vector v_ce, final Vector columnExpression, final int i_count) {
        final SelectColumn firstArgInCastFunction = new SelectColumn();
        final SelectColumn secondArgInCastFunction = new SelectColumn();
        final Vector functionArguments = new Vector();
        final TableColumn tc = new TableColumn();
        final FunctionCalls fc = new FunctionCalls();
        final Vector vec_firstarg = new Vector();
        final Vector vec_secondarg = new Vector();
        tc.setColumnName("CAST");
        fc.setFunctionName(tc);
        vec_firstarg.addElement(columnExpression.elementAt(i_count - 1));
        vec_firstarg.addElement(" AS ");
        vec_firstarg.addElement(columnExpression.elementAt(i_count + 1));
        v_ce.setElementAt(" ", i_count - 1);
        columnExpression.setElementAt(" ", i_count + 1);
        firstArgInCastFunction.setColumnExpression(vec_firstarg);
        functionArguments.addElement(firstArgInCastFunction);
        fc.setFunctionArguments(functionArguments);
        v_ce.addElement(fc);
    }
    
    public void createDecodeFunction(final Vector v_ce, final Vector columnExpression, final int i) {
        final SelectColumn sc_firstarg = new SelectColumn();
        final SelectColumn sc_secondarg = new SelectColumn();
        final SelectColumn sc_thirdarg = new SelectColumn();
        final SelectColumn sc_fourtharg = new SelectColumn();
        final Vector v_farg = new Vector();
        final TableColumn tc = new TableColumn();
        final FunctionCalls fc = new FunctionCalls();
        final Vector vec_firstarg = new Vector();
        final Vector vec_secondarg = new Vector();
        final Vector vec_thirdarg = new Vector();
        final Vector vec_fourtharg = new Vector();
        tc.setColumnName("DECODE");
        fc.setFunctionName(tc);
        vec_firstarg.addElement(columnExpression.elementAt(i - 1));
        v_ce.setElementAt(" ", i - 1);
        sc_firstarg.setColumnExpression(vec_firstarg);
        v_farg.addElement(sc_firstarg);
        boolean isNotNull = false;
        Object obj = columnExpression.elementAt(i + 1);
        final Object obj2 = columnExpression.elementAt(i);
        if (obj2.toString().trim().equalsIgnoreCase("is") && obj.toString().trim().equalsIgnoreCase("not null")) {
            obj = "NULL";
            isNotNull = true;
        }
        vec_secondarg.addElement(obj);
        columnExpression.setElementAt(" ", i + 1);
        sc_secondarg.setColumnExpression(vec_secondarg);
        v_farg.addElement(sc_secondarg);
        if (!isNotNull && obj2.toString().trim().equalsIgnoreCase("is")) {
            vec_thirdarg.addElement("1");
        }
        else {
            vec_thirdarg.addElement("0");
        }
        sc_thirdarg.setColumnExpression(vec_thirdarg);
        v_farg.addElement(sc_thirdarg);
        if (!isNotNull && obj2.toString().trim().equalsIgnoreCase("is")) {
            vec_fourtharg.addElement("0");
        }
        else {
            vec_fourtharg.addElement("1");
        }
        sc_fourtharg.setColumnExpression(vec_fourtharg);
        v_farg.addElement(sc_fourtharg);
        fc.setFunctionArguments(v_farg);
        v_ce.addElement(fc);
    }
    
    public SelectColumn toPostgreSQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectColumn sc = new SelectColumn();
        sc.setCommentClass(null);
        sc.castToText = this.castToText;
        String s_ce = new String();
        final Vector v_ce = new Vector();
        final int sql_dialect = 0;
        int divsion = 0;
        if (from_sqs != null) {
            from_sqs.getSQLDialect();
        }
        sc.setOpenBrace(this.OpenBrace);
        int i = 0;
        int interval = 0;
        while (i < this.columnExpression.size()) {
            if (this.columnExpression.elementAt(i) instanceof TableColumn) {
                final TableColumn tcToBeChanged = this.columnExpression.get(i);
                final String checkForAliasName = tcToBeChanged.getTableName() + ".";
                if (this.originalTableNameList != null) {
                    if (this.originalTableNameList.containsKey(checkForAliasName)) {
                        final TableColumn tc = this.originalTableNameList.get(checkForAliasName);
                        tcToBeChanged.setTableName(tc.getTableName());
                        this.columnExpression.set(i, tcToBeChanged);
                    }
                    else {
                        this.columnExpression.set(i, tcToBeChanged);
                    }
                }
                v_ce.addElement(this.columnExpression.elementAt(i).toPostgreSQLSelect(to_sqs, from_sqs));
                if (this.columnExpression.elementAt(i).toString().equalsIgnoreCase("INTERVAL")) {
                    interval = 1;
                    v_ce.remove(v_ce.size() - 1);
                    v_ce.addElement("INTERVAL");
                    v_ce.addElement(" '1' ");
                }
            }
            else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
                this.columnExpression.elementAt(i).setCastToTextInsideIf(this.castToText);
                final Vector funcArguments = this.columnExpression.get(i).getFunctionArguments();
                if (funcArguments != null && this.originalTableNameList != null) {
                    for (int j = 0; j < funcArguments.size(); ++j) {
                        if (funcArguments.elementAt(j) instanceof SelectColumn) {
                            final SelectColumn sc2 = funcArguments.get(j);
                            sc2.setOriginalTableNamesForUpdateSetClause(this.originalTableNameList);
                        }
                    }
                }
                v_ce.addElement(this.columnExpression.elementAt(i).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof WhereExpression) {
                v_ce.addElement(this.columnExpression.elementAt(i).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof WhereItem) {
                v_ce.addElement(this.columnExpression.elementAt(i).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
                final CaseStatement cs = this.columnExpression.elementAt(i);
                cs.setCastToTextInsideIf(this.castToText);
                final FunctionCalls fc = cs.convertToFunctionCall(to_sqs, from_sqs);
                if (fc == null) {
                    v_ce.addElement(this.columnExpression.elementAt(i).toPostgreSQLSelect(to_sqs, from_sqs));
                }
                else {
                    fc.setCastToTextInsideIf(this.castToText);
                    v_ce.addElement(fc.toPostgreSQLSelect(to_sqs, from_sqs));
                }
            }
            else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
                this.columnExpression.elementAt(i).setOriginalTableNamesForUpdateSetClause(this.originalTableNameList);
                v_ce.addElement(this.columnExpression.elementAt(i).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
                this.columnExpression.elementAt(i).setReportsMeta(from_sqs != null && from_sqs.getReportsMeta());
                v_ce.addElement(this.columnExpression.elementAt(i).toPostgreSQLSelect());
            }
            else if (this.columnExpression.elementAt(i) instanceof Token) {
                final String tokenStr = this.columnExpression.elementAt(i).toString().trim();
                if (tokenStr.startsWith("/*") && tokenStr.endsWith("*/")) {
                    v_ce.addElement("");
                }
            }
            else if (this.columnExpression.elementAt(i) instanceof String) {
                s_ce = this.columnExpression.elementAt(i);
                if (s_ce.equalsIgnoreCase("CURRENT TIME") || s_ce.equalsIgnoreCase("CURTIME")) {
                    v_ce.addElement("CURRENT_TIME");
                }
                else if (s_ce.equalsIgnoreCase("CURRENT DATE") || s_ce.equalsIgnoreCase("CURDATE")) {
                    v_ce.addElement("CURRENT_DATE");
                }
                else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                    v_ce.addElement("CURRENT_TIMESTAMP");
                }
                else if (s_ce.equalsIgnoreCase("CURRENT")) {
                    v_ce.addElement("CURRENT_DATE");
                }
                else if (s_ce.equalsIgnoreCase("**")) {
                    v_ce.addElement("^");
                }
                else if (s_ce.equalsIgnoreCase("=") && sql_dialect == 2) {
                    if (v_ce.elementAt(i - 1) instanceof String) {
                        sc.setAliasName(v_ce.elementAt(i - 1));
                    }
                    else {
                        if (!(v_ce.elementAt(i - 1) instanceof TableColumn)) {
                            throw new ConvertException();
                        }
                        sc.setAliasName(v_ce.elementAt(i - 1).getColumnName());
                    }
                    v_ce.setElementAt(" ", i - 1);
                }
                else if (s_ce.equalsIgnoreCase("NULL") && i > 1) {
                    if (this.columnExpression.elementAt(i - 1) instanceof String && this.columnExpression.elementAt(i - 1).equals("/")) {
                        throw new ConvertException("Conversion failure.. divided by NULL is not supported");
                    }
                    v_ce.addElement(s_ce);
                }
                else if (s_ce.equalsIgnoreCase("IS")) {
                    this.createDecodeFunction(v_ce, this.columnExpression, i);
                    final FunctionCalls fc2 = v_ce.get(v_ce.size() - 1);
                    v_ce.setElementAt(fc2.toPostgreSQLSelect(to_sqs, from_sqs), v_ce.size() - 1);
                }
                else if (s_ce.trim().equals("+")) {
                    if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).toString().trim().startsWith("'") && i - 1 >= 0 && this.columnExpression.elementAt(i - 1).toString().trim().startsWith("'")) {
                        v_ce.addElement("||");
                    }
                    else {
                        v_ce.addElement("+");
                    }
                }
                else if (s_ce.startsWith("0x") || s_ce.startsWith("0X")) {
                    final String str = s_ce.substring(2);
                    v_ce.addElement("'" + str + "'");
                }
                else if (s_ce.trim().equals("/")) {
                    v_ce.addElement("/");
                    divsion = 1;
                }
                else if (s_ce.trim().equals("DIV")) {
                    v_ce.addElement("/");
                }
                else if (s_ce.trim().startsWith("/*") && s_ce.trim().endsWith("*/")) {
                    v_ce.addElement("");
                }
                else {
                    v_ce.addElement(s_ce);
                }
            }
            else {
                v_ce.addElement(this.columnExpression.elementAt(i));
            }
            if (divsion > 0) {
                if (divsion > 2) {
                    final Object obj = v_ce.get(i);
                    final String val = "-" + obj.toString();
                    v_ce.remove(i);
                    v_ce.remove(i - 1);
                    v_ce.add("");
                    String decimalVal = "NULLIF(" + val + ",0.0)";
                    if (obj instanceof String) {
                        decimalVal = StringFunctions.getDecimalString(val, decimalVal);
                    }
                    else if (obj instanceof SelectQueryStatement) {
                        decimalVal = "NULLIF((" + val + "), 0.0)";
                    }
                    v_ce.add(decimalVal);
                    divsion = 0;
                }
                else if (divsion > 1) {
                    final Object obj = v_ce.get(i);
                    final String val = obj.toString();
                    if (val.equals("-")) {
                        ++divsion;
                    }
                    else {
                        v_ce.remove(i);
                        String decimalVal = "NULLIF(" + val + ",0.0)";
                        if (obj instanceof String) {
                            decimalVal = StringFunctions.getDecimalString(val, decimalVal);
                        }
                        else if (obj instanceof SelectQueryStatement) {
                            decimalVal = "NULLIF((" + val + "), 0.0)";
                        }
                        v_ce.add(decimalVal);
                        divsion = 0;
                    }
                }
                else {
                    ++divsion;
                }
            }
            if (interval == 1) {
                i += 2;
                interval = 2;
            }
            else if (interval == 2) {
                --i;
                if (v_ce.get(i + 1).toString().equalsIgnoreCase("week")) {
                    v_ce.remove(i + 1);
                    v_ce.add("day * 7 * ");
                }
                else if (v_ce.get(i + 1).toString().equalsIgnoreCase("quarter")) {
                    v_ce.remove(i + 1);
                    v_ce.add("month * 3 * ");
                }
                else if (v_ce.get(i + 1).toString().equalsIgnoreCase("microsecond")) {
                    v_ce.remove(i + 1);
                    v_ce.add("second / 1000000 * ");
                }
                else {
                    v_ce.add("*");
                }
                interval = 3;
            }
            else if (interval == 3) {
                i += 2;
                interval = 0;
            }
            else {
                ++i;
            }
        }
        sc.setColumnExpression(v_ce);
        sc.setCloseBrace(this.CloseBrace);
        if (this.aliasName != null && this.isAS == null) {
            sc.setIsAS("AS");
        }
        else {
            sc.setIsAS(this.isAS);
        }
        sc.setEndsWith(this.endsWith);
        if (sc != null && sc.getAliasName() == null) {
            if (this.aliasName != null && this.aliasName.charAt(0) == '\'') {
                this.aliasName = this.aliasName.replace('\'', '\"');
            }
            else if (this.aliasName != null && this.aliasName.startsWith("`")) {
                if (this.aliasName.trim().endsWith("`") && this.aliasName.contains("\"")) {
                    final String oldAlias = this.aliasName;
                    try {
                        String alias = this.aliasName.trim().substring(1, this.aliasName.trim().length() - 2);
                        alias = alias.replaceAll("\"", "");
                        alias = "\"" + alias + "\"";
                        this.aliasName = alias;
                    }
                    catch (final Exception e) {
                        this.aliasName = oldAlias.replace('`', '\"');
                    }
                }
                else {
                    this.aliasName = this.aliasName.replace('`', '\"');
                }
            }
            final String aliasNameNew = SelectStatement.checkandRemoveDoubleQuoteForPostgresIdentifier(this.aliasName, from_sqs != null && from_sqs.getReportsMeta());
            if (aliasNameNew != null && !aliasNameNew.isEmpty() && to_sqs != null && from_sqs != null && from_sqs.canHandleHavingWithoutGroupBy()) {
                final String isAsNew = sc.getIsAS();
                final String endsWithNew = sc.getEndsWith();
                sc.setIsAS(null);
                sc.setEndsWith(null);
                final String key = aliasNameNew.toUpperCase().replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
                to_sqs.getAliasVsSelectColExpMap().put(key, sc.toString());
                sc.setIsAS(isAsNew);
                sc.setEndsWith(endsWithNew);
            }
            sc.setAliasName(aliasNameNew);
        }
        return sc;
    }
    
    public SelectColumn toMySQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        return this.toMySQLSelect(to_sqs, from_sqs, false);
    }
    
    public SelectColumn toMySQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs, final boolean fromIfClause) throws ConvertException {
        final SelectColumn sc = new SelectColumn();
        sc.setCommentClass(this.commentObj);
        final IntoStatement into = new IntoStatement();
        String s_ce = new String();
        final Vector v_ce = new Vector();
        boolean intoClause = false;
        boolean concat = false;
        final int sql_dialect = 0;
        sc.setReportsMysqlConversion(true);
        if (from_sqs != null) {
            from_sqs.getSQLDialect();
        }
        sc.setOpenBrace(this.OpenBrace);
        if (this.columnExpression.contains("+") || this.columnExpression.contains("||")) {
            final boolean isNumeric = this.isColumnTypeNumeric(from_sqs, this);
            if (!this.columnExpression.contains("+") || !isNumeric) {
                if (!this.isDateAddition) {
                    v_ce.add("concat(");
                    concat = true;
                }
            }
        }
        for (int i = 0; i < this.columnExpression.size(); ++i) {
            if (this.columnExpression.elementAt(i) instanceof TableColumn) {
                v_ce.addElement(this.columnExpression.elementAt(i).toMySQLSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
                v_ce.addElement(this.columnExpression.elementAt(i).toMySQLSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
                v_ce.addElement(this.columnExpression.elementAt(i).toMySQLSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
                v_ce.addElement(this.columnExpression.elementAt(i).toMySQLSelect(to_sqs, from_sqs, fromIfClause));
            }
            else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
                v_ce.addElement(this.columnExpression.elementAt(i).toMySQLSelect());
            }
            else if (this.columnExpression.elementAt(i) instanceof WhereItem) {
                v_ce.addElement(this.columnExpression.elementAt(i).toMySQLSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof WhereExpression) {
                v_ce.addElement(this.columnExpression.elementAt(i).toMySQLSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof String) {
                s_ce = this.columnExpression.elementAt(i);
                if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                    v_ce.addElement("CURRENT_TIME");
                }
                else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                    v_ce.addElement("CURRENT_DATE");
                }
                else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                    v_ce.addElement("CURRENT_TIMESTAMP");
                }
                else if (s_ce.equalsIgnoreCase("CURRENT")) {
                    v_ce.addElement("CURRENT_DATE");
                }
                else if (s_ce.equalsIgnoreCase("::")) {
                    this.createCastFunction(v_ce, this.columnExpression, i);
                    final Object object = v_ce.get(v_ce.size() - 1);
                    if (object instanceof FunctionCalls) {
                        v_ce.set(v_ce.size() - 1, ((FunctionCalls)object).toMySQLSelect(to_sqs, from_sqs));
                    }
                }
                else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
                    this.createPowerFunction(v_ce, this.columnExpression, i, true);
                }
                else if (s_ce.equalsIgnoreCase("=") && sql_dialect == 2) {
                    if (v_ce.elementAt(i - 1) instanceof String) {
                        sc.setAliasName(v_ce.elementAt(i - 1));
                    }
                    else {
                        if (!(v_ce.elementAt(i - 1) instanceof TableColumn)) {
                            throw new ConvertException();
                        }
                        sc.setAliasName(v_ce.elementAt(i - 1).getColumnName());
                    }
                    v_ce.setElementAt(" ", i - 1);
                }
                else if (s_ce.equalsIgnoreCase("=")) {
                    if (fromIfClause) {
                        v_ce.addElement(s_ce);
                    }
                    else {
                        if (v_ce.elementAt(i - 1) instanceof TableColumn) {
                            final String colName = v_ce.elementAt(i - 1).getColumnName();
                            if (colName.startsWith("@")) {
                                into.setIntoClause("INTO");
                                into.setTableOrFileName(colName);
                                intoClause = true;
                            }
                            else {
                                sc.setAliasName(v_ce.elementAt(i - 1).getColumnName());
                            }
                        }
                        v_ce.setElementAt(" ", i - 1);
                    }
                }
                else if (s_ce.trim().equals("+") || s_ce.trim().equals("||")) {
                    if (s_ce.trim().equals("+") && !concat) {
                        v_ce.addElement("+");
                    }
                    else {
                        v_ce.addElement(",");
                    }
                }
                else {
                    v_ce.addElement(s_ce);
                }
            }
            else {
                v_ce.addElement(this.columnExpression.elementAt(i));
            }
        }
        if (intoClause) {
            v_ce.addElement(into);
        }
        if (concat) {
            for (int i = 1; i < v_ce.size(); ++i) {
                final Object obj = v_ce.get(i);
                if (obj != null && !(obj instanceof String)) {
                    final FunctionCalls fc = new FunctionCalls();
                    final TableColumn tc = new TableColumn();
                    tc.setColumnName("CAST");
                    final CharacterClass charCluse = new CharacterClass();
                    charCluse.setDatatypeName("CHAR");
                    final Vector newFunctionArgs = new Vector();
                    if (obj instanceof SelectColumn) {
                        newFunctionArgs.add(obj);
                    }
                    else {
                        final SelectColumn sc2 = new SelectColumn();
                        final Vector columnExpression = new Vector();
                        columnExpression.add(obj);
                        sc2.setColumnExpression(columnExpression);
                        newFunctionArgs.add(sc2);
                    }
                    newFunctionArgs.add(charCluse);
                    fc.setFunctionName(tc);
                    fc.setFunctionArguments(newFunctionArgs);
                    fc.setAsDatatype("as");
                    v_ce.set(i, fc);
                }
            }
            v_ce.addElement(")");
        }
        sc.setColumnExpression(v_ce);
        sc.setCloseBrace(this.CloseBrace);
        sc.setIsAS(this.isAS);
        sc.setEndsWith(this.endsWith);
        if (sc != null && sc.getAliasName() == null) {
            if (this.aliasName != null && this.aliasName.startsWith("[")) {
                this.aliasName = this.aliasName.replace('[', '`');
                this.aliasName = this.aliasName.replace(']', '`');
            }
            sc.setAliasName(this.aliasName);
        }
        return sc;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.commentObj != null) {
            sb.append(this.commentObj.toString().trim() + " ");
        }
        if (this.OpenBrace != null) {
            sb.append(this.OpenBrace);
        }
        for (int i = 0; i < this.columnExpression.size(); ++i) {
            if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
                sb.append("(" + this.columnExpression.elementAt(i).toString() + ")");
            }
            else {
                if (this.columnExpression.elementAt(i) instanceof TableColumn) {
                    this.columnExpression.elementAt(i).setObjectContext(this.context);
                }
                else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
                    this.columnExpression.elementAt(i).setObjectContext(this.context);
                }
                else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
                    this.columnExpression.elementAt(i).setObjectContext(this.context);
                }
                else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
                    this.columnExpression.elementAt(i).setObjectContext(this.context);
                }
                else if (this.columnExpression.elementAt(i) instanceof String && i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).equals(".*") && this.context != null) {
                    this.columnExpression.setElementAt(this.context.getEquivalent(this.columnExpression.get(i)), i);
                }
                if (i + 1 == this.columnExpression.size()) {
                    sb.append(this.columnExpression.elementAt(i).toString());
                }
                else {
                    String colExprStr = this.columnExpression.elementAt(i).toString() + " ";
                    if (this.columnExpression.elementAt(i).toString().equals("-") && !this.isReportsMysqlConversion()) {
                        colExprStr = colExprStr.trim();
                    }
                    if (i + 1 < this.columnExpression.size() && this.columnExpression.elementAt(i + 1).equals(".*")) {
                        colExprStr = colExprStr.trim();
                    }
                    if (colExprStr.startsWith("--")) {
                        colExprStr = StringFunctions.replaceFirst("/*", "--", colExprStr);
                        colExprStr += "*/";
                    }
                    sb.append(colExprStr);
                }
            }
        }
        if (this.CloseBrace != null) {
            sb.append(this.CloseBrace);
        }
        if (this.commentObjAfterToken != null) {
            sb.append(" " + this.commentObjAfterToken.toString().trim());
        }
        if (this.isAS != null) {
            sb.append(" " + this.isAS);
        }
        if (this.aliasName != null) {
            sb.append(" " + this.aliasName);
        }
        if (this.endsWith != null) {
            sb.append(this.endsWith);
        }
        return sb.toString();
    }
    
    public String getTheCoreSelectItem() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.columnExpression.size(); ++i) {
            sb.append(this.columnExpression.elementAt(i).toString() + " ");
        }
        return sb.toString();
    }
    
    public boolean isAggregateFunction() {
        final boolean conta = this.containsAggregateFunction(this);
        return conta;
    }
    
    public boolean containsAggregateFunction(final SelectColumn sCol) {
        final Vector colExpr = sCol.getColumnExpression();
        for (int i = 0; i < colExpr.size(); ++i) {
            if (colExpr.elementAt(i) instanceof FunctionCalls) {
                final String functionName = colExpr.elementAt(i).getFunctionNameAsAString();
                if (functionName != null) {
                    if (functionName.equalsIgnoreCase("avg") || functionName.equalsIgnoreCase("count") || functionName.equalsIgnoreCase("max") || functionName.equalsIgnoreCase("min") || functionName.equalsIgnoreCase("sum")) {
                        return true;
                    }
                    final Vector funcArgs = colExpr.elementAt(i).getFunctionArguments();
                    if (funcArgs != null) {
                        for (int j = 0; j < funcArgs.size(); ++j) {
                            if (funcArgs.get(j) instanceof SelectColumn && this.containsAggregateFunction(funcArgs.get(j))) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public boolean getOuterJoin() {
        return this.outerJoin;
    }
    
    public void setOuterJoin(final boolean oj) {
        this.outerJoin = oj;
    }
    
    public String generateFunctionForSubQuery(final SelectQueryStatement sqs, final Vector parameters) {
        String functionString = "CREATE OR REPLACE FUNCTION ";
        String alias = "TUSER.HASCHILDREN";
        if (this.aliasName != null) {
            alias = "TUSER." + this.aliasName;
        }
        String parameterString = new String();
        final String singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
        SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
        String sqlStr = sqs.toString();
        SelectQueryStatement.singleQueryConvertedToMultipleQueryList = singleQueryConvertedToMultipleQueryList;
        sqlStr = StringFunctions.replaceAll("\n\t", "\n", sqlStr);
        if (parameters != null && parameters.size() > 0) {
            for (int i = 0; i < parameters.size(); ++i) {
                if (parameters.get(i) instanceof String) {
                    if (i > 0) {
                        parameterString = parameterString + ",\n\t" + parameters.get(i) + " INTEGER";
                    }
                    else {
                        parameterString = parameterString + parameters.get(i) + " INTEGER";
                    }
                }
            }
        }
        functionString = functionString + alias + "\n(\n\t" + parameterString + "\n)" + "\nRETURN INTEGER" + "\nAS\n\t" + "FUNCTION_VAR INTEGER" + ";\nBEGIN\n\t" + sqlStr.trim() + ";\n\tRETURN FUNCTION_VAR;\nEND;\n/";
        return functionString;
    }
    
    public Vector findTheParametersToBePassed(final SelectQueryStatement sqs) {
        final Vector parameters = new Vector();
        final Vector fromItemList = new Vector();
        final SelectStatement ss = sqs.getSelectStatement();
        final Vector selectItemList = ss.getSelectItemList();
        final FromClause fc = sqs.getFromClause();
        final WhereExpression we = sqs.getWhereExpression();
        if (fc != null) {
            final Vector fromTablesList = fc.getFromItemList();
            if (fromTablesList != null) {
                for (int i = 0; i < fromTablesList.size(); ++i) {
                    if (fromTablesList.get(i) instanceof FromTable) {
                        final FromTable ft = fromTablesList.get(i);
                        if (ft.getAliasName() != null) {
                            if (ft.getTableName() instanceof SelectQueryStatement) {
                                final SelectQueryStatement subSQS = (SelectQueryStatement)ft.getTableName();
                                final Vector parametersInsideSubquery = this.findTheParametersToBePassed(subSQS);
                                if (parametersInsideSubquery != null && parametersInsideSubquery.size() > 0) {
                                    for (int j = 0; j < parametersInsideSubquery.size(); ++j) {
                                        if (!parameters.contains(parametersInsideSubquery.get(j))) {
                                            parameters.add(parametersInsideSubquery.get(j));
                                        }
                                    }
                                }
                            }
                            fromItemList.add(ft.getAliasName().trim().toLowerCase());
                        }
                        else {
                            final Object tableName = ft.getTableName();
                            fromItemList.add(tableName.toString().trim().toLowerCase());
                        }
                    }
                }
            }
        }
        if (selectItemList != null) {
            for (int k = 0; k < selectItemList.size(); ++k) {
                if (selectItemList.get(k) instanceof SelectColumn) {
                    final SelectColumn sc = selectItemList.get(k);
                    final Vector columnExpression = sc.getColumnExpression();
                    this.addParametersForTheFunction(parameters, columnExpression, fromItemList);
                }
            }
        }
        this.processWhereExpressionColumns(we, parameters, fromItemList);
        return parameters;
    }
    
    public void processWhereExpressionColumns(final WhereExpression we, final Vector parameters, final Vector fromItemList) {
        if (we != null) {
            final Vector whereItemsList = we.getWhereItems();
            if (whereItemsList != null) {
                for (int i = 0; i < whereItemsList.size(); ++i) {
                    if (whereItemsList.get(i) instanceof WhereItem) {
                        final WhereItem wi = whereItemsList.get(i);
                        final WhereColumn leftWC = wi.getLeftWhereExp();
                        if (leftWC != null) {
                            final Vector leftColExp = leftWC.getColumnExpression();
                            this.addParametersForTheFunction(parameters, leftColExp, fromItemList);
                        }
                        final WhereColumn rightWC = wi.getRightWhereExp();
                        if (rightWC != null) {
                            final Vector rightColExp = rightWC.getColumnExpression();
                            this.addParametersForTheFunction(parameters, rightColExp, fromItemList);
                        }
                    }
                    else if (whereItemsList.get(i) instanceof WhereExpression) {
                        final WhereExpression whereExp = whereItemsList.get(i);
                        this.processWhereExpressionColumns(whereExp, parameters, fromItemList);
                    }
                }
            }
        }
    }
    
    public void addParametersForTheFunction(final Vector parameters, final Vector colExp, final Vector fromItemList) {
        if (colExp != null) {
            for (int i = 0; i < colExp.size(); ++i) {
                if (colExp.get(i) instanceof TableColumn) {
                    final TableColumn tc = colExp.get(i);
                    if (tc.getTableName() != null) {
                        if (fromItemList == null || !fromItemList.contains(tc.getTableName().toLowerCase())) {
                            if (!parameters.contains(tc.toString().trim())) {
                                parameters.add(tc.toString().trim());
                            }
                        }
                    }
                }
                else if (colExp.get(i) instanceof String) {
                    if (colExp.get(i).trim().startsWith("@")) {
                        parameters.add(colExp.get(i));
                    }
                }
                else if (colExp.get(i) instanceof FunctionCalls) {
                    final Vector functionArgs = colExp.get(i).getFunctionArguments();
                    this.addParametersForTheFunction(parameters, functionArgs, fromItemList);
                }
                else if (colExp.get(i) instanceof SelectColumn) {
                    final Vector selectColArgs = colExp.get(i).getColumnExpression();
                    this.addParametersForTheFunction(parameters, selectColArgs, fromItemList);
                }
            }
        }
    }
    
    public String getReturnDataTypeForCoalesce(final FunctionCalls functionCalls, final String builtInFunctionName, final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        final Vector functionArguments = functionCalls.getFunctionArguments();
        if (functionArguments != null) {
            for (int i = 0; i < functionArguments.size(); ++i) {
                if (functionArguments.get(i) instanceof SelectColumn) {
                    final SelectColumn selCol = functionArguments.get(i);
                    try {
                        selCol.toDB2Select(to_sqs, from_sqs);
                    }
                    catch (final Exception e) {
                        e.printStackTrace();
                    }
                    final String str = selCol.toString();
                    final TableColumn tableCol = new TableColumn();
                    tableCol.setColumnName(str);
                    final String dType = MetadataInfoUtil.getTargetDataTypeForColumn(tableCol);
                    if (dType != null) {
                        return dType;
                    }
                    if (this.corrTableColumn != null) {
                        tableCol.setTableName(this.corrTableColumn.getTableName());
                        return MetadataInfoUtil.getDatatypeName(from_sqs, tableCol);
                    }
                }
            }
        }
        return null;
    }
    
    private int convertNumeralsToInterval(final Vector intervalVector, final List subList) {
        int processedElements = 0;
        String previousOperator = "";
        for (int k = 0; k < subList.size(); ++k) {
            final Object obj = subList.get(k);
            if (obj instanceof String) {
                final String objStr = obj.toString().trim();
                if (this.isDecimal(objStr) && (previousOperator.equalsIgnoreCase("") || previousOperator.equalsIgnoreCase("+") || previousOperator.equalsIgnoreCase("-"))) {
                    String intervalStr = SwisSQLUtils.convertDayToInterval(objStr);
                    ++processedElements;
                    if (k != subList.size() - 1 && subList.get(k + 1).toString().equalsIgnoreCase("/") && subList.get(k + 2).toString().trim().equalsIgnoreCase("24")) {
                        intervalStr = intervalStr.replaceFirst("DAY", "HOUR");
                        k += 2;
                        processedElements += 2;
                    }
                    intervalVector.add(intervalStr);
                }
                else {
                    if (objStr.length() != 1) {
                        break;
                    }
                    if (objStr.equalsIgnoreCase("(") || objStr.equalsIgnoreCase(")") || objStr.equalsIgnoreCase("+") || objStr.equalsIgnoreCase("-") || objStr.equalsIgnoreCase("*") || objStr.equalsIgnoreCase("%") || objStr.equalsIgnoreCase("^")) {
                        intervalVector.add(objStr);
                        previousOperator = objStr;
                        ++processedElements;
                    }
                    else if (this.isDecimal(objStr)) {
                        intervalVector.add(objStr);
                        ++processedElements;
                    }
                }
            }
        }
        return processedElements;
    }
    
    public void replaceRownumTableColumn(final Object newColumn) throws ConvertException {
        final Vector colExp = this.columnExpression;
        for (int k = 0; k < colExp.size(); ++k) {
            final Object obj = colExp.get(k);
            if (obj instanceof TableColumn) {
                final TableColumn tcnMod = (TableColumn)obj;
                if (tcnMod.getColumnName().equalsIgnoreCase("rownum")) {
                    colExp.setElementAt(newColumn, k);
                }
            }
            else if (obj instanceof FunctionCalls) {
                final Vector funcArgs = ((FunctionCalls)obj).getFunctionArguments();
                if (funcArgs != null) {
                    for (int j = 0; j < funcArgs.size(); ++j) {
                        if (funcArgs.get(j) instanceof SelectColumn) {
                            funcArgs.get(j).replaceRownumTableColumn(newColumn);
                        }
                        else if (funcArgs.get(j) instanceof TableColumn) {
                            final TableColumn tcnMod2 = funcArgs.get(j);
                            if (tcnMod2.getColumnName().equalsIgnoreCase("rownum")) {
                                funcArgs.setElementAt(newColumn, j);
                            }
                        }
                    }
                }
            }
            else if (obj instanceof SelectColumn) {
                ((SelectColumn)obj).replaceRownumTableColumn(newColumn);
            }
            else if (obj instanceof CaseStatement) {
                final CaseStatement csObj = (CaseStatement)obj;
                csObj.replaceRownumTableColumn(newColumn);
            }
            else if (!(obj instanceof SelectQueryStatement)) {
                if (obj instanceof String) {}
            }
        }
    }
    
    private boolean isColumnTypeNumeric(final SelectQueryStatement from_sqs, final SelectColumn sc) {
        return this.isColumnTypeNumeric(from_sqs, sc, false);
    }
    
    private boolean isColumnTypeNumeric(final SelectQueryStatement from_sqs, final SelectColumn sc, final boolean isVW) {
        return this.isColumnTypeNumeric(from_sqs, sc, isVW, false);
    }
    
    private boolean isColumnTypeNumeric(final SelectQueryStatement from_sqs, final SelectColumn sc, final boolean isVW, final boolean isColExpContainsPlus) {
        boolean isNumeric = false;
        final Vector columnExpression = sc.getColumnExpression();
    Label_0611:
        for (int i = 0; i < columnExpression.size(); ++i) {
            if (columnExpression.elementAt(i) instanceof TableColumn) {
                final TableColumn tc = columnExpression.elementAt(i);
                String tabName = tc.getOrigTableName();
                if (tabName == null && from_sqs != null) {
                    if (tc != null && tc.getColumnName().trim().equalsIgnoreCase("INTERVAL") && columnExpression.contains("+")) {
                        this.isDateAddition = true;
                    }
                    try {
                        final Object fromTableObj = MetadataInfoUtil.getTableOfColumn(from_sqs, tc).getTableName();
                        if (fromTableObj instanceof String) {
                            tabName = fromTableObj.toString();
                        }
                    }
                    catch (final NullPointerException ex) {}
                }
                if (tabName != null) {
                    String dtype = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                    if (dtype != null) {
                        dtype = dtype.trim().toUpperCase();
                        if (dtype.indexOf("INT") != -1 || dtype.indexOf("NUM") != -1) {
                            isNumeric = true;
                            break;
                        }
                        isNumeric = false;
                    }
                    else {
                        isNumeric = true;
                    }
                }
            }
            else if (columnExpression.elementAt(i) instanceof String) {
                String colExpStr = columnExpression.elementAt(i).toString();
                if (colExpStr.startsWith("'")) {
                    colExpStr = colExpStr.substring(1, colExpStr.length() - 1);
                }
                if (!colExpStr.equalsIgnoreCase("+")) {
                    try {
                        Double.parseDouble(colExpStr);
                        isNumeric = true;
                        break;
                    }
                    catch (final NumberFormatException nfe) {
                        isNumeric = false;
                    }
                }
            }
            else if (columnExpression.elementAt(i) instanceof FunctionCalls) {
                if (columnExpression.contains("+") || isColExpContainsPlus) {
                    final FunctionCalls fc = columnExpression.elementAt(i);
                    final String functionName = fc.getFunctionName().toString();
                    if (functionName.equalsIgnoreCase("max") || functionName.equalsIgnoreCase("min") || functionName.equalsIgnoreCase("count") || functionName.equalsIgnoreCase("avg") || functionName.equalsIgnoreCase("sum") || functionName.equalsIgnoreCase("to_number")) {
                        isNumeric = true;
                        break;
                    }
                }
            }
            else if (columnExpression.elementAt(i) instanceof CaseStatement) {
                final CaseStatement cs = columnExpression.elementAt(i);
                final Vector v = cs.getWhenClauseList();
                for (int vi = 0; vi < v.size(); ++vi) {
                    final WhenStatement ws = v.get(vi);
                    final SelectColumn whenSC = ws.getThenStatement();
                    isNumeric = this.isColumnTypeNumeric(from_sqs, whenSC, isVW, isColExpContainsPlus);
                    if (isNumeric) {
                        break Label_0611;
                    }
                }
            }
            else if (columnExpression.elementAt(i) instanceof SelectColumn) {
                final SelectColumn selCol = columnExpression.elementAt(i);
                if (selCol.getColumnExpression().size() == 1 && selCol.getColumnExpression().get(0) instanceof FunctionCalls && isVW) {
                    isNumeric = this.isColumnTypeNumeric(from_sqs, columnExpression.elementAt(i), isVW, true);
                }
                else {
                    isNumeric = this.isColumnTypeNumeric(from_sqs, columnExpression.elementAt(i), isVW, isColExpContainsPlus);
                }
                if (isNumeric) {
                    break;
                }
            }
        }
        return isNumeric;
    }
    
    public SelectColumn toVectorWiseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectColumn sc = new SelectColumn();
        final IntoStatement into = new IntoStatement();
        String s_ce = new String();
        final Vector v_ce = new Vector();
        final boolean intoClause = false;
        boolean concat = false;
        final int sql_dialect = 0;
        int divsion = 0;
        int modulus = 0;
        if (from_sqs != null) {
            from_sqs.getSQLDialect();
        }
        sc.setOpenBrace(this.OpenBrace);
        if (this.columnExpression.contains("+") || this.columnExpression.contains("||")) {
            final boolean isNumeric = true;
            if (!this.columnExpression.contains("+") || !isNumeric) {
                if (!this.isDateAddition) {
                    v_ce.add("concat(");
                    concat = true;
                }
            }
        }
        int i = 0;
        int interval = 0;
        while (i < this.columnExpression.size()) {
            if (this.columnExpression.elementAt(i) instanceof TableColumn) {
                v_ce.addElement(this.columnExpression.elementAt(i).toVectorWiseSelect(to_sqs, from_sqs));
                if (this.columnExpression.elementAt(i).toString().equalsIgnoreCase("INTERVAL")) {
                    interval = 1;
                    v_ce.addElement(" '1' ");
                }
            }
            else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
                v_ce.addElement(this.columnExpression.elementAt(i).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
                v_ce.addElement(this.columnExpression.elementAt(i).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
                v_ce.addElement(this.columnExpression.elementAt(i).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
                v_ce.addElement(this.columnExpression.elementAt(i).toVectorWiseSelect());
            }
            else if (this.columnExpression.elementAt(i) instanceof WhereItem) {
                v_ce.addElement(this.columnExpression.elementAt(i).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof WhereExpression) {
                v_ce.addElement(this.columnExpression.elementAt(i).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else if (this.columnExpression.elementAt(i) instanceof Token) {
                final String tokenStr = this.columnExpression.elementAt(i).toString().trim();
                if (tokenStr.startsWith("/*") && tokenStr.endsWith("*/")) {
                    v_ce.addElement("");
                }
            }
            else if (this.columnExpression.elementAt(i) instanceof String) {
                s_ce = this.columnExpression.elementAt(i);
                if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                    v_ce.addElement("CURRENT_TIME");
                }
                else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                    v_ce.addElement("CURRENT_DATE");
                }
                else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                    v_ce.addElement("CURRENT_TIMESTAMP");
                }
                else if (s_ce.equalsIgnoreCase("CURRENT")) {
                    v_ce.addElement("CURRENT_DATE");
                }
                else if (s_ce.equalsIgnoreCase("::")) {
                    this.createCastFunction(v_ce, this.columnExpression, i);
                    final Object object = v_ce.get(v_ce.size() - 1);
                    if (object instanceof FunctionCalls) {
                        v_ce.set(v_ce.size() - 1, ((FunctionCalls)object).toVectorWiseSelect(to_sqs, from_sqs));
                    }
                }
                else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
                    this.createPowerFunction(v_ce, this.columnExpression, i, true);
                    final Object object = v_ce.get(v_ce.size() - 1);
                    if (object instanceof FunctionCalls) {
                        v_ce.set(v_ce.size() - 1, ((FunctionCalls)object).toVectorWiseSelect(to_sqs, from_sqs));
                    }
                }
                else if (s_ce.equalsIgnoreCase("%")) {
                    modulus = 1;
                }
                else if (s_ce.equalsIgnoreCase("=") && sql_dialect == 2) {
                    if (v_ce.elementAt(i - 1) instanceof String) {
                        sc.setAliasName(SelectStatement.changeBackTip(v_ce.elementAt(i - 1)));
                    }
                    else {
                        if (!(v_ce.elementAt(i - 1) instanceof TableColumn)) {
                            throw new ConvertException();
                        }
                        sc.setAliasName(SelectStatement.changeBackTip(v_ce.elementAt(i - 1).getColumnName()));
                    }
                    v_ce.setElementAt(" ", i - 1);
                }
                else if (s_ce.trim().equals("+") || s_ce.trim().equals("||")) {
                    if (s_ce.trim().equals("+") && !concat) {
                        v_ce.addElement("+");
                    }
                    else {
                        v_ce.addElement(",");
                    }
                }
                else if (s_ce.trim().equals("/")) {
                    v_ce.addElement("/");
                    divsion = 1;
                }
                else if (s_ce.trim().equals("DIV")) {
                    v_ce.addElement("/");
                }
                else if (s_ce.trim().startsWith("/*") && s_ce.trim().endsWith("*/")) {
                    v_ce.addElement("");
                }
                else {
                    v_ce.addElement(s_ce);
                }
            }
            else {
                v_ce.addElement(this.columnExpression.elementAt(i));
            }
            if (divsion > 0) {
                if (divsion > 2) {
                    final Object obj = v_ce.get(i);
                    final String val = "-" + obj.toString();
                    v_ce.remove(i);
                    v_ce.remove(i - 1);
                    v_ce.add("");
                    String decimalVal = "(NULLIF(" + val + ",0.0) * 1.0)";
                    if (obj instanceof String) {
                        decimalVal = StringFunctions.getDecimalString(val, decimalVal);
                    }
                    else if (obj instanceof SelectQueryStatement) {
                        decimalVal = "(NULLIF((" + val + "), 0.0) * 1.0)";
                    }
                    v_ce.add(decimalVal);
                    divsion = 0;
                }
                else if (divsion > 1) {
                    final Object obj = v_ce.get(i);
                    final String val = obj.toString();
                    if (val.equals("-")) {
                        ++divsion;
                    }
                    else {
                        v_ce.remove(i);
                        String decimalVal = "(NULLIF(" + val + ",0.0) * 1.0)";
                        if (obj instanceof String) {
                            decimalVal = StringFunctions.getDecimalString(val, decimalVal);
                        }
                        else if (obj instanceof SelectQueryStatement) {
                            decimalVal = "(NULLIF((" + val + "), 0.0) * 1.0)";
                        }
                        v_ce.add(decimalVal);
                        divsion = 0;
                    }
                }
                else {
                    ++divsion;
                }
            }
            if (modulus > 0) {
                if (modulus > 2) {
                    this.createModFunction(v_ce, this.columnExpression, i);
                    v_ce.remove(i - 2);
                    v_ce.remove(i - 3);
                    modulus = 0;
                }
                else if (modulus > 1) {
                    final Object obj = v_ce.get(i - 1);
                    final String val = obj.toString();
                    if (val.equals("-")) {
                        v_ce.remove(i - 1);
                        ++modulus;
                    }
                    else {
                        this.createModFunction(v_ce, this.columnExpression, i);
                        v_ce.remove(i - 1);
                        v_ce.remove(i - 2);
                        modulus = 0;
                    }
                }
                else {
                    ++modulus;
                }
            }
            if (interval == 1) {
                i += 2;
                interval = 2;
            }
            else if (interval == 2) {
                --i;
                if (v_ce.get(i + 1).toString().equalsIgnoreCase("week")) {
                    v_ce.remove(i + 1);
                    v_ce.add("day * 7 * ");
                }
                else if (v_ce.get(i + 1).toString().equalsIgnoreCase("quarter")) {
                    v_ce.remove(i + 1);
                    v_ce.add("month * 3 * ");
                }
                else if (v_ce.get(i + 1).toString().equalsIgnoreCase("microsecond")) {
                    v_ce.remove(i + 1);
                    v_ce.add("second / 1000000 * ");
                }
                else {
                    v_ce.add("*");
                }
                interval = 3;
            }
            else if (interval == 3) {
                i += 2;
                interval = 0;
            }
            else {
                ++i;
            }
        }
        if (intoClause) {
            v_ce.addElement(into);
        }
        if (concat) {
            for (i = 1; i < v_ce.size(); ++i) {
                final Object obj2 = v_ce.get(i);
                if (obj2 != null && !(obj2 instanceof String)) {
                    final FunctionCalls fc = new FunctionCalls();
                    final TableColumn tc = new TableColumn();
                    tc.setColumnName("CAST");
                    final CharacterClass charCluse = new CharacterClass();
                    charCluse.setDatatypeName("CHAR");
                    final Vector newFunctionArgs = new Vector();
                    if (obj2 instanceof SelectColumn) {
                        newFunctionArgs.add(obj2);
                    }
                    else {
                        final SelectColumn sc2 = new SelectColumn();
                        final Vector columnExpression = new Vector();
                        columnExpression.add(obj2);
                        sc2.setColumnExpression(columnExpression);
                        newFunctionArgs.add(sc2);
                    }
                    newFunctionArgs.add(charCluse);
                    fc.setFunctionName(tc);
                    fc.setFunctionArguments(newFunctionArgs);
                    fc.setAsDatatype("as");
                    v_ce.set(i, fc);
                }
            }
            v_ce.addElement(")");
        }
        sc.setColumnExpression(v_ce);
        sc.setCloseBrace(this.CloseBrace);
        sc.setIsAS(this.isAS);
        sc.setEndsWith(this.endsWith);
        if (sc != null && sc.getAliasName() == null) {
            if (this.aliasName != null && this.aliasName.charAt(0) == '\'') {
                this.aliasName = this.aliasName.replace('\'', '\"');
            }
            else if (this.aliasName != null && this.aliasName.startsWith("[")) {
                this.aliasName = this.aliasName.replace('[', '\"');
                this.aliasName = this.aliasName.replace(']', '\"');
            }
            else if (this.aliasName != null && this.aliasName.startsWith("`")) {
                if (this.aliasName.trim().endsWith("`") && this.aliasName.contains("\"")) {
                    final String oldAlias = this.aliasName;
                    try {
                        String alias = this.aliasName.trim().substring(1, this.aliasName.trim().length() - 2);
                        alias = alias.replaceAll("\"", "");
                        alias = "\"" + alias + "\"";
                        this.aliasName = alias;
                    }
                    catch (final Exception e) {
                        this.aliasName = oldAlias.replace('`', '\"');
                    }
                }
                else {
                    this.aliasName = this.aliasName.replace('`', '\"');
                }
            }
            if (this.aliasName != null && !this.aliasName.isEmpty() && to_sqs != null && from_sqs != null && from_sqs.canHandleHavingWithoutGroupBy()) {
                final String isAsNew = sc.getIsAS();
                final String endsWithNew = sc.getEndsWith();
                sc.setIsAS(null);
                sc.setEndsWith(null);
                final String key = this.aliasName.toUpperCase().replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
                to_sqs.getAliasVsSelectColExpMap().put(key, sc.toString());
                sc.setIsAS(isAsNew);
                sc.setEndsWith(endsWithNew);
            }
            sc.setAliasName(this.aliasName);
        }
        return sc;
    }
    
    public void trackIndexPositionsForCastingNULLString(final int indexPosition, final boolean isFirstSelect, final Set indexPositionsSet) {
        try {
            if (isFirstSelect || indexPositionsSet.contains(indexPosition)) {
                final boolean containsNULLString = this.needsCastingForNULLString();
                if (containsNULLString) {
                    if (isFirstSelect && indexPositionsSet != null) {
                        indexPositionsSet.add(indexPosition);
                    }
                }
                else if (indexPositionsSet != null) {
                    indexPositionsSet.remove(indexPosition);
                }
            }
        }
        catch (final Exception ex) {}
    }
    
    public boolean needsCastingForNULLString() {
        boolean needsCasting = false;
        try {
            final Vector colExp = this.getColumnExpression();
            if (colExp != null && colExp.size() == 1 && colExp.get(0) instanceof String) {
                final String exp = colExp.get(0).toString().replaceAll("\\s", "");
                if (exp.equalsIgnoreCase("NULL")) {
                    needsCasting = true;
                }
            }
        }
        catch (final Exception ex) {}
        return needsCasting;
    }
    
    public boolean needsCastingForStringLiterals() {
        return this.needsCastingForStringLiterals(false);
    }
    
    public boolean needsCastingForStringLiterals(final boolean checkOtherTypes) {
        boolean needsCasting = false;
        try {
            final Vector colExp = this.getColumnExpression();
            if (colExp != null && colExp.size() == 1 && colExp.get(0) instanceof String) {
                final String exp = colExp.get(0).toString().replaceAll("\\s", "");
                if (exp.equals("''") || (exp.startsWith("'") && exp.endsWith("'"))) {
                    needsCasting = true;
                }
            }
        }
        catch (final Exception ex) {}
        return needsCasting;
    }
    
    public void convertSelectColumnToTextDataType() {
        this.convertSelectColumnToTextDataType(true, "CHAR");
    }
    
    public void convertSelectColumnToTextDataType(final String dataType) {
        this.convertSelectColumnToTextDataType(true, dataType);
    }
    
    public boolean needsCastingForStringLiteralsInsideIfFunction() {
        boolean needsCasting = false;
        try {
            final Vector colExp = this.getColumnExpression();
            if (colExp != null && colExp.size() == 1) {
                if (colExp.get(0) instanceof FunctionCalls && colExp.get(0).getFunctionNameAsAString() != null && colExp.get(0).getFunctionNameAsAString().equalsIgnoreCase("if")) {
                    final FunctionCalls fc = colExp.get(0);
                    final Vector fnArgs = fc.getFunctionArguments();
                    if (fnArgs.size() == 3) {
                        final SelectColumn sc1 = fnArgs.get(1);
                        needsCasting = sc1.needsCastingForStringLiteralsInsideIfFunction();
                        if (!needsCasting) {
                            final SelectColumn sc2 = fnArgs.get(2);
                            needsCasting = sc2.needsCastingForStringLiteralsInsideIfFunction();
                        }
                    }
                }
                else {
                    needsCasting = this.needsCastingForStringLiterals(true);
                }
            }
        }
        catch (final StackOverflowError stackOverflowError) {}
        catch (final Exception ex) {}
        return needsCasting;
    }
    
    public void convertSelectColumnArgsToTextDataType(final boolean needsCasting) {
        try {
            if (needsCasting) {
                final Vector colExp = this.getColumnExpression();
                if (colExp != null) {
                    if (colExp.size() == 1 && colExp.get(0) instanceof FunctionCalls && colExp.get(0).getFunctionNameAsAString() != null && colExp.get(0).getFunctionNameAsAString().equalsIgnoreCase("if")) {
                        final FunctionCalls fc = colExp.get(0);
                        final Vector fnArgs = fc.getFunctionArguments();
                        if (fnArgs.size() == 3) {
                            final SelectColumn sc1 = fnArgs.get(1);
                            sc1.setCastToTextInsideIf(true);
                            sc1.convertSelectColumnArgsToTextDataType(needsCasting);
                            sc1.setCastToTextInsideIf(false);
                            final SelectColumn sc2 = fnArgs.get(2);
                            sc2.setCastToTextInsideIf(true);
                            sc2.convertSelectColumnArgsToTextDataType(needsCasting);
                            sc2.setCastToTextInsideIf(false);
                        }
                    }
                    else {
                        this.convertSelectColumnToTextDataType(needsCasting, "CHAR");
                    }
                }
            }
        }
        catch (final Exception ex) {}
    }
    
    public void convertSelectColumnToTextDataType(final boolean needsCasting) {
        this.convertSelectColumnToTextDataType(needsCasting, "CHAR");
    }
    
    public void convertSelectColumnToTextDataType(final boolean needsCasting, final String dataType) {
        try {
            if (needsCasting) {
                final Vector colExp = this.getColumnExpression();
                final Vector castToCharExp = FunctionCalls.castToCharClass(colExp, dataType);
                if (castToCharExp != null) {
                    this.setColumnExpression(castToCharExp);
                }
            }
        }
        catch (final Exception ex) {}
    }
    
    public void convertSelectColumnToTextDataTypeIfChildSelectHasStringLiterals(final int selectItemPosition, final Set indexPositionsSet) {
        try {
            final boolean needsCasting = this.needsCastingForStringLiterals() || indexPositionsSet.contains(selectItemPosition);
            if (needsCasting) {
                this.convertSelectColumnToTextDataType(needsCasting);
                if (indexPositionsSet != null) {
                    indexPositionsSet.add(selectItemPosition);
                }
            }
        }
        catch (final Exception ex) {}
    }
    
    public void convertSelectColumnToNumericDataType(final boolean needsCasting, final String dataType) {
        try {
            if (needsCasting) {
                final Vector colExp = this.getColumnExpression();
                final Vector castToCharExp = FunctionCalls.castToNumericClass(colExp, dataType);
                if (castToCharExp != null) {
                    this.setColumnExpression(castToCharExp);
                }
            }
        }
        catch (final Exception ex) {}
    }
    
    public void convertSelectColumnToNumericDataType(final String dataType) {
        this.convertSelectColumnToNumericDataType(this.needsCastingForNULLString(), dataType);
    }
    
    public void convertWhereExpAloneInsideFunctionTo_IF_Function(final int argumentsSize) {
        try {
            final Vector colExp = this.getColumnExpression();
            boolean containsWhereExp = false;
            if (argumentsSize == 1 && colExp != null) {
                if (colExp.size() == 1 && (colExp.get(0) instanceof WhereExpression || colExp.get(0) instanceof WhereItem)) {
                    containsWhereExp = true;
                }
                else if (colExp.size() == 3 && colExp.get(0) instanceof String && colExp.get(2) instanceof String && (colExp.get(1) instanceof WhereExpression || colExp.get(1) instanceof WhereItem) && colExp.get(0).toString().trim().equals("(") && colExp.get(2).toString().trim().equals(")")) {
                    containsWhereExp = true;
                }
                else {
                    for (final Object argument : colExp) {
                        if (argument instanceof WhereItem) {
                            containsWhereExp = true;
                            break;
                        }
                        if (argument instanceof WhereExpression) {
                            containsWhereExp = true;
                            break;
                        }
                    }
                }
                if (containsWhereExp) {
                    final FunctionCalls fc = new FunctionCalls();
                    final TableColumn functionName = new TableColumn();
                    functionName.setColumnName("IF");
                    fc.setFunctionName(functionName);
                    final Vector subFunctionArgs = new Vector();
                    final SelectColumn firstArgSC = new SelectColumn();
                    final Vector firstArgColExp = new Vector();
                    firstArgColExp.addAll(colExp);
                    firstArgSC.setColumnExpression(firstArgColExp);
                    final SelectColumn secArgSC = new SelectColumn();
                    final Vector secArgExp = new Vector();
                    secArgExp.add("1");
                    secArgSC.setColumnExpression(secArgExp);
                    final SelectColumn thirdArgSC = new SelectColumn();
                    final Vector thirdArgExp = new Vector();
                    thirdArgExp.add("0");
                    thirdArgSC.setColumnExpression(thirdArgExp);
                    subFunctionArgs.add(firstArgSC);
                    subFunctionArgs.add(secArgSC);
                    subFunctionArgs.add(thirdArgSC);
                    fc.setFunctionArguments(subFunctionArgs);
                    final Vector colExpr = new Vector();
                    colExpr.add(fc);
                    this.setColumnExpression(colExpr);
                }
            }
        }
        catch (final Exception ex) {}
    }
}
