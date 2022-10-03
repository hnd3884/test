package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.functions.misc.decode;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.sql.statement.update.TableExpression;
import com.adventnet.swissqlapi.sql.statement.update.TableClause;
import com.adventnet.swissqlapi.sql.parser.Token;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import java.util.StringTokenizer;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.delete.DeleteQueryStatement;
import com.adventnet.swissqlapi.sql.statement.update.UpdateQueryStatement;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.Vector;

public class WhereColumn
{
    private String OpenBrace;
    private Vector columnExpression;
    private String CloseBrace;
    private UserObjectContext context;
    private TableColumn corrTableColumn;
    private String targetDataType;
    private boolean lhs;
    private ArrayList fromTableList;
    private String tableName;
    private String sourceDataType;
    private UpdateQueryStatement fromUQS;
    private DeleteQueryStatement fromDQS;
    private CommentClass commentObj;
    private CommentClass commentObjAfterToken;
    private String stmtTableName;
    
    public WhereColumn() {
        this.context = null;
        this.corrTableColumn = null;
        this.targetDataType = null;
        this.lhs = false;
        this.fromTableList = null;
        this.tableName = null;
        this.sourceDataType = null;
    }
    
    public void setColumnExpression(final Vector v_cn) {
        this.columnExpression = v_cn;
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public void setOpenBrace(final String s_ob) {
        this.OpenBrace = s_ob;
    }
    
    public void setCloseBrace(final String s_cb) {
        this.CloseBrace = s_cb;
    }
    
    public void setFromUQS(final UpdateQueryStatement fromUQS) {
        this.fromUQS = fromUQS;
    }
    
    public void setFromDQS(final DeleteQueryStatement fromDQS) {
        this.fromDQS = fromDQS;
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public void setCommentClassAfterToken(final CommentClass commentObj) {
        this.commentObjAfterToken = commentObj;
    }
    
    public void setStmtTableName(final String stmtTableName) {
        this.stmtTableName = stmtTableName;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public CommentClass getCommentClassAfterToken() {
        return this.commentObjAfterToken;
    }
    
    public Vector getColumnExpression() {
        return this.columnExpression;
    }
    
    public String getOpenBrace() {
        return this.OpenBrace;
    }
    
    public String getCloseBrace() {
        return this.CloseBrace;
    }
    
    public void setSourceDataType(final String sourceDataType) {
        this.sourceDataType = sourceDataType;
    }
    
    public String getSourceDataType() {
        return this.sourceDataType;
    }
    
    public void setTargetDataType(final String targetDataType) {
        this.targetDataType = targetDataType;
    }
    
    public String getTargetDataType() {
        return this.targetDataType;
    }
    
    public void setLHSExpr(final boolean lhs) {
        this.lhs = lhs;
    }
    
    public boolean isLHSExpr() {
        return this.lhs;
    }
    
    public TableColumn getCorrTableColumn() {
        return this.corrTableColumn;
    }
    
    public void setFromTableList(final ArrayList fromTableList) {
        this.fromTableList = fromTableList;
    }
    
    public WhereColumn toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhereColumn wc = new WhereColumn();
        String s_ce = new String();
        final Vector v_ce = new Vector();
        wc.setOpenBrace(this.OpenBrace);
        if (this.columnExpression != null) {
            for (int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
                if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
                    final TableColumn tc = this.columnExpression.elementAt(i_count);
                    tc.setObjectContext(this.context);
                    if (tc.getColumnName() != null) {
                        String column_Name = tc.getColumnName();
                        if (column_Name.trim().length() > 0 && !SwisSQLOptions.TSQLQuotedIdentifier && column_Name.trim().startsWith("\"") && column_Name.trim().endsWith("\"")) {
                            final String temp = column_Name.substring(1, column_Name.length() - 1);
                            column_Name = "'" + temp + "'";
                        }
                        tc.setColumnName(column_Name);
                    }
                    v_ce.addElement(tc.toMSSQLServerSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
                    if (this.columnExpression.elementAt(i_count).getFunctionName() == null) {
                        v_ce.addElement(this.columnExpression.elementAt(i_count));
                    }
                    else {
                        v_ce.addElement(this.columnExpression.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
                    }
                }
                else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toMSSQLServerSelect());
                }
                else if (this.columnExpression.elementAt(i_count) instanceof String) {
                    s_ce = this.columnExpression.elementAt(i_count);
                    if (SwisSQLOptions.removeDBSchemaQualifier && s_ce.indexOf(".") != -1 && s_ce.indexOf(".") != s_ce.lastIndexOf(".")) {
                        s_ce = s_ce.substring(s_ce.indexOf(".") + 1);
                    }
                    if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                        v_ce.addElement("CURRENT_TIME");
                    }
                    else if (s_ce.equalsIgnoreCase("SYS_GUID")) {
                        v_ce.addElement("NEWID()");
                    }
                    else if (s_ce.equalsIgnoreCase("SYSDATE")) {
                        v_ce.addElement("GETDATE()");
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
                    else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
                        this.createPowerFunction(v_ce, this.columnExpression, i_count);
                    }
                    else if (s_ce.equalsIgnoreCase("::")) {
                        this.createCastFunction(v_ce, this.columnExpression, i_count);
                    }
                    else if (s_ce.startsWith(":")) {
                        v_ce.addElement("@" + s_ce.substring(1));
                    }
                    else if (s_ce.equalsIgnoreCase("||")) {
                        v_ce.addElement("+");
                    }
                    else {
                        v_ce.addElement(s_ce);
                    }
                }
                else {
                    v_ce.addElement(this.columnExpression.elementAt(i_count));
                }
            }
            wc.setColumnExpression(v_ce);
        }
        wc.setCloseBrace(this.CloseBrace);
        return wc;
    }
    
    public WhereColumn toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhereColumn wc = new WhereColumn();
        String s_ce = new String();
        final Vector v_ce = new Vector();
        wc.setOpenBrace(this.OpenBrace);
        if (this.columnExpression != null) {
            for (int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
                if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toSybaseSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
                    this.columnExpression.elementAt(i_count).setObjectContext(this.context);
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toSybaseSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toSybaseSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
                    this.columnExpression.elementAt(i_count).setObjectContext(this.context);
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toSybaseSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
                    this.columnExpression.elementAt(i_count).setObjectContext(this.context);
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toSybaseSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
                    this.columnExpression.elementAt(i_count).setObjectContext(this.context);
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toSybaseSelect());
                }
                else if (this.columnExpression.elementAt(i_count) instanceof String) {
                    s_ce = this.columnExpression.elementAt(i_count);
                    if (s_ce.charAt(0) == '\"') {
                        v_ce.addElement(s_ce.replace('\"', '\''));
                    }
                    else if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                        v_ce.addElement("CURRENT_TIME");
                    }
                    else if (s_ce.equalsIgnoreCase("SYS_GUID")) {
                        v_ce.addElement("NEWID()");
                    }
                    else if (s_ce.equalsIgnoreCase("SYSDATE")) {
                        v_ce.addElement("GETDATE()");
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
                    else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
                        this.createPowerFunction(v_ce, this.columnExpression, i_count);
                    }
                    else if (s_ce.equalsIgnoreCase("::")) {
                        this.createCastFunction(v_ce, this.columnExpression, i_count);
                    }
                    else if (s_ce.startsWith(":")) {
                        v_ce.addElement("@" + s_ce.substring(1));
                    }
                    else if (s_ce.equalsIgnoreCase("||")) {
                        v_ce.addElement("+");
                    }
                    else {
                        v_ce.addElement(s_ce);
                    }
                }
                else {
                    v_ce.addElement(this.columnExpression.elementAt(i_count));
                }
            }
            wc.setColumnExpression(v_ce);
        }
        wc.setCloseBrace(this.CloseBrace);
        wc.setObjectContext(this.context);
        return wc;
    }
    
    public void createPowerFunction(final Vector v_ce, final Vector columnExpression, final int i_count) {
        final SelectColumn wc_firstarg = new SelectColumn();
        final SelectColumn wc_secondarg = new SelectColumn();
        final Vector v_farg = new Vector();
        final TableColumn tc = new TableColumn();
        final FunctionCalls fc = new FunctionCalls();
        final Vector vec_firstarg = new Vector();
        final Vector vec_secondarg = new Vector();
        tc.setColumnName("POWER");
        fc.setFunctionName(tc);
        vec_firstarg.addElement(columnExpression.elementAt(i_count - 1));
        v_ce.setElementAt(" ", i_count - 1);
        wc_firstarg.setColumnExpression(vec_firstarg);
        v_farg.addElement(wc_firstarg);
        vec_secondarg.addElement(columnExpression.elementAt(i_count + 1));
        columnExpression.setElementAt(" ", i_count + 1);
        wc_secondarg.setColumnExpression(vec_secondarg);
        v_farg.addElement(wc_secondarg);
        fc.setFunctionArguments(v_farg);
        v_ce.addElement(fc);
    }
    
    public WhereColumn toANSISelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhereColumn wc = new WhereColumn();
        String s_ce = new String();
        final Vector v_ce = new Vector();
        wc.setOpenBrace(this.OpenBrace);
        if (this.columnExpression != null) {
            for (int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
                if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toANSISelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toANSISelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toANSISelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toANSISelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toANSISelect());
                }
                else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toANSISelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof String) {
                    s_ce = this.columnExpression.elementAt(i_count);
                    if (s_ce.equalsIgnoreCase("**")) {
                        this.createPowerFunction(v_ce, this.columnExpression, i_count);
                    }
                    else if (!s_ce.trim().startsWith("\"")) {
                        if (s_ce.indexOf(".") != -1) {
                            final Vector tokenVector = new Vector();
                            final StringTokenizer st = new StringTokenizer(s_ce, ".");
                            while (st.hasMoreTokens()) {
                                tokenVector.add(st.nextToken());
                            }
                            if (tokenVector.size() == 2) {
                                final String table_Column = "\"" + tokenVector.get(0) + "\"" + "." + "\"" + tokenVector.get(1) + "\"";
                                v_ce.addElement(table_Column);
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
                else {
                    v_ce.addElement(this.columnExpression.elementAt(i_count));
                }
            }
            wc.setColumnExpression(v_ce);
        }
        wc.setCloseBrace(this.CloseBrace);
        return wc;
    }
    
    public WhereColumn toTeradataSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhereColumn wc = new WhereColumn();
        String s_ce = new String();
        final Vector v_ce = new Vector();
        wc.setOpenBrace(this.OpenBrace);
        if (this.columnExpression != null) {
            for (int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
                if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
                    FunctionCalls wcFunc = this.columnExpression.elementAt(i_count);
                    if (wcFunc.getFunctionName() == null) {
                        v_ce.addElement(this.columnExpression.elementAt(i_count));
                    }
                    else {
                        try {
                            wcFunc = wcFunc.toTeradataSelect(to_sqs, from_sqs);
                            v_ce.addElement(wcFunc);
                        }
                        catch (final ConvertException ce) {
                            throw ce;
                        }
                    }
                }
                else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toTeradataSelect());
                }
                else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof String) {
                    s_ce = this.columnExpression.elementAt(i_count);
                    if (s_ce.equalsIgnoreCase("**")) {
                        v_ce.addElement(s_ce);
                    }
                    else if (s_ce.startsWith("/*") || s_ce.startsWith("--")) {
                        v_ce.addElement(s_ce);
                    }
                    else if (s_ce.equalsIgnoreCase("+") || s_ce.equalsIgnoreCase("-")) {
                        v_ce.addElement(s_ce);
                        if (i_count > 0 && i_count < this.columnExpression.size() - 1) {
                            boolean isDateExpr = false;
                            final Object obj = this.columnExpression.get(i_count - 1);
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
                                final Object nextObj = this.columnExpression.get(i_count + 1);
                                if (nextObj instanceof String) {
                                    final Vector intervalVector = new Vector();
                                    final int increaseLoopCount = this.convertNumeralsToInterval(intervalVector, this.columnExpression.subList(i_count + 1, this.columnExpression.size()));
                                    if (intervalVector.size() > 0) {
                                        v_ce.addAll(intervalVector);
                                        i_count += increaseLoopCount;
                                    }
                                }
                            }
                        }
                    }
                    else if (s_ce.indexOf(".") != -1 && s_ce.charAt(0) != '\'' && s_ce.charAt(0) != '.') {
                        final String[] elements = s_ce.split("\\.");
                        final int esize = elements.length;
                        if (esize > 0) {
                            final StringBuffer newS_ce = new StringBuffer();
                            for (int es = 0; es < esize; ++es) {
                                String elem = elements[es];
                                if (!CustomizeUtil.isStartsWithNum(elem)) {
                                    elem = CustomizeUtil.objectNamesToQuotedIdentifier(elem, SwisSQLUtils.getKeywords("teradata"), null, -1);
                                }
                                if (elem.equalsIgnoreCase("dual") || elem.equalsIgnoreCase("sys.dual")) {
                                    elem = "\"DUAL\"";
                                }
                                if (es == 0) {
                                    newS_ce.append(elem);
                                }
                                else {
                                    newS_ce.append("." + elem);
                                }
                            }
                            v_ce.addElement(newS_ce.toString());
                            this.columnExpression.setElementAt(newS_ce.toString(), i_count);
                        }
                        else {
                            v_ce.addElement(CustomizeUtil.objectNamesToQuotedIdentifier(s_ce, SwisSQLUtils.getKeywords("teradata"), null, -1));
                        }
                    }
                    else {
                        v_ce.addElement(s_ce);
                    }
                }
                else {
                    v_ce.addElement(this.columnExpression.elementAt(i_count));
                }
            }
            wc.setColumnExpression(v_ce);
        }
        wc.setCloseBrace(this.CloseBrace);
        return wc;
    }
    
    public WhereColumn toDB2Select(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhereColumn wc = new WhereColumn();
        String s_ce = new String();
        final Vector v_ce = new Vector();
        wc.setOpenBrace(this.OpenBrace);
        if (this.columnExpression != null) {
            for (int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
                if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
                    final TableColumn tc = this.columnExpression.elementAt(i_count);
                    if (tc.getColumnName() != null) {
                        String column_Name = tc.getColumnName();
                        if (column_Name.equalsIgnoreCase("SEQUENCE_NAME")) {
                            column_Name = "SEQNAME";
                        }
                        tc.setColumnName(column_Name);
                    }
                    if (this.isLHSExpr()) {
                        final TableColumn tcTemp = new TableColumn();
                        tcTemp.setColumnName(tc.getColumnName());
                        if (tc.getTableName() == null) {
                            if (this.stmtTableName != null) {
                                tcTemp.setTableName(this.stmtTableName);
                                tc.setTableName(this.stmtTableName);
                            }
                            else {
                                tcTemp.setTableName(MetadataInfoUtil.getFromTable(this.fromTableList, tc));
                            }
                        }
                        else {
                            tcTemp.setTableName(tc.getTableName());
                        }
                        if (this.sourceDataType == null) {
                            this.sourceDataType = MetadataInfoUtil.getTargetDataTypeForColumn(tcTemp);
                        }
                    }
                    else {
                        tc.setTargetDataType(CastingUtil.getDataType(this.targetDataType));
                    }
                    final TableColumn newTC = tc.toDB2Select(to_sqs, from_sqs);
                    if (newTC != null) {
                        if (this.sourceDataType != null && !this.sourceDataType.equalsIgnoreCase("")) {
                            newTC.setSourceDataType(this.sourceDataType);
                        }
                        if (this.stmtTableName != null) {
                            newTC.setTableName(null);
                        }
                    }
                    v_ce.addElement(newTC);
                }
                else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toDB2Select(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toDB2Select(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toDB2Select(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toDB2Select());
                }
                else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toDB2Select(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof String) {
                    s_ce = this.columnExpression.elementAt(i_count);
                    if (s_ce.charAt(0) == '\"') {
                        v_ce.addElement(s_ce.replace('\"', '\''));
                    }
                    else if (s_ce.startsWith("'") && (s_ce.indexOf("-") != -1 || s_ce.indexOf("/") != -1)) {
                        if (s_ce.indexOf("-") != -1 || s_ce.indexOf("/") != -1) {
                            if (s_ce.indexOf("-") != -1) {
                                v_ce.addElement(this.convertToOracleDateFormat(s_ce, "-", true));
                            }
                            else if (s_ce.indexOf("/") != -1) {
                                v_ce.addElement(this.convertToOracleDateFormat(s_ce, "/", true));
                            }
                        }
                    }
                    else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
                        this.createPowerFunction(v_ce, this.columnExpression, i_count);
                    }
                    else if (s_ce.equalsIgnoreCase("%")) {
                        this.createModFunction(v_ce, this.columnExpression, i_count);
                    }
                    else if (s_ce.equalsIgnoreCase("::")) {
                        this.createCastFunction(v_ce, this.columnExpression, i_count);
                    }
                    else if (s_ce.equalsIgnoreCase("CURRENT")) {
                        v_ce.addElement("CURRENT DATE");
                    }
                    else if (s_ce.equalsIgnoreCase("NULL")) {
                        v_ce.addElement("CAST(NULL AS INT)");
                    }
                    else if (s_ce.trim().equals("/")) {
                        final Object obj = v_ce.lastElement();
                        final FunctionCalls fn = new FunctionCalls();
                        final TableColumn tc2 = new TableColumn();
                        tc2.setColumnName("CAST");
                        fn.setFunctionName(tc2);
                        final Vector args = new Vector();
                        args.add(obj + " AS DECIMAL");
                        fn.setFunctionArguments(args);
                        v_ce.setElementAt(fn, v_ce.size() - 1);
                        v_ce.addElement(s_ce);
                    }
                    else if (s_ce.trim().equals("+") && i_count != 0 && i_count != this.columnExpression.size() - 1) {
                        final Object o1 = this.columnExpression.get(i_count - 1);
                        final Object o2 = this.columnExpression.get(i_count + 1);
                        if ((o1.toString().startsWith("'") && o1.toString().endsWith("'")) || (o2.toString().startsWith("'") && o2.toString().endsWith("'"))) {
                            v_ce.add("CONCAT");
                        }
                        else if (o1 instanceof TableColumn && o2 instanceof TableColumn) {
                            final String datatype1 = MetadataInfoUtil.getDatatypeName(null, (TableColumn)o1);
                            final String datatype2 = MetadataInfoUtil.getDatatypeName(null, (TableColumn)o2);
                            if (datatype1 != null && datatype2 != null) {
                                if (datatype1.trim().toLowerCase().startsWith("varchar") || datatype2.trim().toLowerCase().startsWith("varchar")) {
                                    v_ce.add("CONCAT");
                                }
                                if (datatype1.trim().toLowerCase().startsWith("char") || datatype2.trim().toLowerCase().startsWith("char")) {
                                    v_ce.add("CONCAT");
                                }
                            }
                            else {
                                v_ce.add(s_ce);
                            }
                        }
                        else {
                            v_ce.add(s_ce);
                        }
                    }
                    else {
                        v_ce.addElement(s_ce);
                        final int size = v_ce.size();
                        if (size > 2) {
                            final Object obj2 = v_ce.get(size - 2);
                            if (obj2 instanceof String) {
                                final String operator = (String)obj2;
                                if (operator.trim().equals("+") || operator.trim().equals("-")) {
                                    final Object exprObj = v_ce.get(size - 3);
                                    if (exprObj instanceof TableColumn) {
                                        final TableColumn tc3 = (TableColumn)exprObj;
                                        final String dataType = MetadataInfoUtil.getDatatypeName(from_sqs, tc3);
                                        if (dataType != null && (dataType.equalsIgnoreCase("TIMESTAMP") || dataType.equalsIgnoreCase("DATE"))) {
                                            v_ce.addElement("DAYS");
                                        }
                                        else if (SwisSQLAPI.variableDatatypeMapping != null) {
                                            final String varDataType = SwisSQLAPI.variableDatatypeMapping.get(tc3.getColumnName());
                                            if (varDataType != null && (varDataType.equalsIgnoreCase("TIMESTAMP") || varDataType.equalsIgnoreCase("DATE"))) {
                                                v_ce.addElement("DAYS");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else {
                    v_ce.addElement(this.columnExpression.elementAt(i_count));
                }
            }
            wc.setColumnExpression(v_ce);
        }
        wc.setCloseBrace(this.CloseBrace);
        return wc;
    }
    
    public WhereColumn toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhereColumn wc = new WhereColumn();
        String s_ce = new String();
        final Vector v_ce = new Vector();
        wc.setOpenBrace(this.OpenBrace);
        wc.setCommentClass(this.commentObj);
        if (this.columnExpression != null) {
            for (int i = 0; i < this.columnExpression.size(); ++i) {
                if (this.columnExpression.elementAt(i) instanceof TableColumn) {
                    TableColumn tc = this.columnExpression.elementAt(i);
                    if (this.fromUQS != null) {
                        tc.setFromUQS(this.fromUQS);
                    }
                    else if (this.fromDQS != null) {
                        tc.setFromDQS(this.fromDQS);
                    }
                    if (!this.isLHSExpr()) {
                        this.handleTableColumn(tc, from_sqs, 1);
                    }
                    if (tc.getColumnName() != null) {
                        String column_Name = tc.getColumnName();
                        if (column_Name.equalsIgnoreCase("SEQNAME")) {
                            column_Name = "SEQUENCE_NAME";
                        }
                        tc.setColumnName(column_Name);
                    }
                    tc = tc.toOracleSelect(to_sqs, from_sqs);
                    if (SwisSQLOptions.SetSybaseDoubleQuotedLiteralsToSingleQuotes && tc.getOwnerName() == null && tc.getTableName() == null) {
                        String colName = tc.getColumnName();
                        if (colName.startsWith("\"")) {
                            colName = colName.replaceAll("'", "\"");
                            colName = "'" + colName.substring(1, colName.length() - 1) + "'";
                            tc.setColumnName(colName);
                        }
                    }
                    v_ce.addElement(tc);
                }
                else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
                    final FunctionCalls temp = this.columnExpression.elementAt(i).toOracleSelect(to_sqs, from_sqs);
                    v_ce.addElement(temp);
                    this.columnExpression.setElementAt(temp, i);
                }
                else if (this.columnExpression.elementAt(i) instanceof SelectColumn) {
                    final SelectColumn temp2 = this.columnExpression.elementAt(i).toOracleSelect(to_sqs, from_sqs);
                    v_ce.addElement(temp2);
                    this.columnExpression.setElementAt(temp2, i);
                }
                else if (this.columnExpression.elementAt(i) instanceof CaseStatement) {
                    final CaseStatement temp3 = this.columnExpression.elementAt(i).toOracleSelect(to_sqs, from_sqs);
                    v_ce.addElement(temp3);
                    this.columnExpression.setElementAt(temp3, i);
                }
                else if (this.columnExpression.elementAt(i) instanceof SelectQueryStatement) {
                    final SelectQueryStatement temp4 = this.columnExpression.elementAt(i).toOracleSelect();
                    v_ce.addElement(temp4);
                    this.columnExpression.setElementAt(temp4, i);
                }
                else if (this.columnExpression.elementAt(i) instanceof WhereColumn) {
                    final WhereColumn temp5 = this.columnExpression.elementAt(i).toOracleSelect(to_sqs, from_sqs);
                    v_ce.addElement(temp5);
                    this.columnExpression.setElementAt(temp5, i);
                }
                else if (this.columnExpression.elementAt(i) instanceof String) {
                    s_ce = this.columnExpression.elementAt(i);
                    if (s_ce.charAt(0) == '\"') {
                        v_ce.addElement(s_ce.replace('\"', '\''));
                    }
                    else if (s_ce.startsWith("'") && (s_ce.indexOf("-") != -1 || s_ce.indexOf("/") != -1)) {
                        if (s_ce.indexOf("-") != -1 || s_ce.indexOf("/") != -1) {
                            if (s_ce.indexOf("-") != -1) {
                                v_ce.addElement(this.convertToOracleDateFormat(s_ce, "-", false));
                            }
                            else if (s_ce.indexOf("/") != -1) {
                                v_ce.addElement(this.convertToOracleDateFormat(s_ce, "/", false));
                            }
                        }
                    }
                    else if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
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
                    else if (s_ce.equalsIgnoreCase("::")) {
                        this.createCastFunction(v_ce, this.columnExpression, i);
                    }
                    else if (s_ce.equalsIgnoreCase("**")) {
                        this.createPowerFunction(v_ce, this.columnExpression, i);
                    }
                    else if (s_ce.trim().equalsIgnoreCase("+")) {
                        String newStr = null;
                        if (i - 1 >= 0 && this.columnExpression.elementAt(i - 1).toString().trim().startsWith("'")) {
                            newStr = this.columnExpression.elementAt(i - 1).toString();
                            if (newStr.equals("''") || StringFunctions.replaceAll("", " ", newStr.substring(1, newStr.length() - 1)).length() == 0) {
                                v_ce.addElement("||");
                            }
                            else if (i + 1 < this.columnExpression.size() && (this.columnExpression.elementAt(i + 1) instanceof TableColumn || this.columnExpression.elementAt(i + 1) instanceof FunctionCalls)) {
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
                            else if (i - 1 >= 0 && (this.columnExpression.elementAt(i - 1) instanceof TableColumn || this.columnExpression.elementAt(i - 1) instanceof FunctionCalls)) {
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
                        else {
                            v_ce.addElement(s_ce);
                        }
                    }
                    else if (s_ce.startsWith("@")) {
                        v_ce.addElement(":" + s_ce.substring(1));
                    }
                    else if (s_ce.startsWith("0x") || s_ce.startsWith("0X")) {
                        final String str = s_ce.substring(2);
                        v_ce.addElement("HEX_TO_NUMBER('" + str + "')");
                    }
                    else {
                        v_ce.addElement(s_ce);
                    }
                }
                else {
                    v_ce.addElement(this.columnExpression.elementAt(i));
                }
            }
            wc.setColumnExpression(v_ce);
        }
        wc.setCloseBrace(this.CloseBrace);
        return wc;
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
                    final String f_arg = "BITAND(" + first_arg + "," + sec_arg + ")";
                    sec_arg.setElementAt(" ", i - 1);
                    sec_arg.setElementAt(" ", i);
                    final Object ne = sec_arg.get(i + 1);
                    if (ne instanceof String) {
                        final String ss = ne.toString();
                        sec_arg_str = "((0 - " + ne + ") + 1)";
                        return "((" + first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + "))";
                    }
                    if (ne instanceof TableColumn) {
                        final TableColumn col = ((TableColumn)ne).toOracleSelect(to_sqs, from_sqs);
                        sec_arg_str = "((0 - " + sec_arg_str + col.toString() + ") + 1";
                        return "((" + first_arg + "+" + sec_arg_str + ")" + " - BITAND(" + first_arg + "," + sec_arg_str + "))";
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
    
    private String convertToOracleDateFormat(final String originalString, final String hiphenOrSlash, final boolean convertDateFormatForDB2) {
        final StringTokenizer st = new StringTokenizer(originalString, hiphenOrSlash);
        final int count = st.countTokens();
        final Vector tokenVector = new Vector();
        if (count == 3) {
            while (st.hasMoreTokens()) {
                tokenVector.add(st.nextToken());
            }
            String str1 = tokenVector.get(0);
            String str2 = tokenVector.get(1);
            String str3 = tokenVector.get(2);
            boolean validDate = true;
            if (str1.startsWith("'")) {
                str1 = str1.substring(1);
            }
            if (str3.endsWith("'")) {
                str3 = str3.substring(0, str3.length() - 1);
            }
            try {
                Integer.parseInt(str3);
            }
            catch (final NumberFormatException e) {
                validDate = false;
            }
            try {
                Integer.parseInt(str1);
            }
            catch (final NumberFormatException e) {
                validDate = false;
            }
            String convertedSting = "";
            if ((str1.length() != 2 || str2.length() != 2 || str3.length() != 2 || !validDate) && (str1.length() != 2 || str2.length() != 2 || str3.length() != 4 || !validDate) && (str1.length() != 2 || str2.length() != 3 || str3.length() != 4 || !validDate) && (str1.length() != 2 || str2.length() <= 3 || str3.length() != 4 || !validDate)) {
                if (str1.length() != 2 || str2.length() <= 3 || str3.length() != 2 || !validDate) {
                    if ((str1.length() != 4 || str2.length() != 3 || str3.length() != 2 || !validDate) && (str1.length() != 4 || str2.length() <= 3 || str3.length() != 2 || !validDate)) {
                        if (str1.length() != 4 || str2.length() != 2 || str3.length() != 2 || !validDate) {
                            convertedSting = str1 + hiphenOrSlash + str2 + hiphenOrSlash + str3;
                            return '\'' + convertedSting + '\'';
                        }
                    }
                    try {
                        final int monthValue = Integer.parseInt(str2);
                        if (monthValue > 12 && convertDateFormatForDB2) {
                            final String temp = str2;
                            str2 = str3;
                            str3 = temp;
                        }
                    }
                    catch (final NumberFormatException e2) {
                        if (convertDateFormatForDB2) {
                            str2 = this.convertMonthsToEquivalentMonthValue(str2);
                        }
                    }
                    convertedSting = str1 + hiphenOrSlash + str2 + hiphenOrSlash + str3;
                    return '\'' + convertedSting + '\'';
                }
            }
            try {
                final int monthValue = Integer.parseInt(str2);
                if (monthValue > 12 && convertDateFormatForDB2) {
                    final String temp = str2;
                    str2 = str1;
                    str1 = temp;
                }
            }
            catch (final NumberFormatException e2) {
                if (convertDateFormatForDB2) {
                    str2 = this.convertMonthsToEquivalentMonthValue(str2);
                }
            }
            convertedSting = str3 + hiphenOrSlash + str2 + hiphenOrSlash + str1;
            return '\'' + convertedSting + '\'';
        }
        return originalString;
    }
    
    private String convertMonthsToEquivalentMonthValue(String monthName) {
        monthName = monthName.trim().toUpperCase();
        if (monthName.equals("JAN") || monthName.equals("JANUARY")) {
            return "01";
        }
        if (monthName.equals("FEB") || monthName.equals("FEBRUARY")) {
            return "02";
        }
        if (monthName.equals("MAR") || monthName.equals("MARCH")) {
            return "03";
        }
        if (monthName.equals("APR") || monthName.equals("APRIL")) {
            return "04";
        }
        if (monthName.equals("MAY")) {
            return "05";
        }
        if (monthName.equals("JUN") || monthName.equals("JUNE")) {
            return "06";
        }
        if (monthName.equals("JUL") || monthName.equals("JULY")) {
            return "07";
        }
        if (monthName.equals("AUG") || monthName.equals("AUGUST")) {
            return "08";
        }
        if (monthName.equals("SEP") || monthName.equals("SEPTEMBER")) {
            return "09";
        }
        if (monthName.equals("OCT") || monthName.equals("OCTOBER")) {
            return "10";
        }
        if (monthName.equals("NOV") || monthName.equals("NOVEMBER")) {
            return "11";
        }
        if (monthName.equals("DEC") || monthName.equals("DECEMBER")) {
            return "12";
        }
        return monthName;
    }
    
    public WhereColumn toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhereColumn wc = new WhereColumn();
        String s_ce = new String();
        final Vector v_ce = new Vector();
        wc.setOpenBrace(this.OpenBrace);
        if (this.columnExpression != null) {
            for (int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
                if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toInformixSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toInformixSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toInformixSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toInformixSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toInformixSelect());
                }
                else if (this.columnExpression.elementAt(i_count) instanceof String) {
                    s_ce = this.columnExpression.elementAt(i_count);
                    if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                        v_ce.addElement("CURRENT");
                    }
                    else if (s_ce.startsWith("'") && (s_ce.indexOf("-") != -1 || s_ce.indexOf("/") != -1)) {
                        if (s_ce.indexOf("-") != -1 || s_ce.indexOf("/") != -1) {
                            if (s_ce.indexOf("-") != -1) {
                                v_ce.addElement(this.convertToOracleDateFormat(s_ce, "-", false));
                            }
                            else if (s_ce.indexOf("/") != -1) {
                                v_ce.addElement(this.convertToOracleDateFormat(s_ce, "/", false));
                            }
                        }
                    }
                    else if (s_ce.equalsIgnoreCase("%")) {
                        this.createModFunction(v_ce, this.columnExpression, i_count);
                    }
                    else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
                        this.createPowerFunction(v_ce, this.columnExpression, i_count);
                    }
                    else {
                        v_ce.addElement(s_ce);
                    }
                }
                else {
                    v_ce.addElement(this.columnExpression.elementAt(i_count));
                }
            }
            wc.setColumnExpression(v_ce);
        }
        wc.setCloseBrace(this.CloseBrace);
        return wc;
    }
    
    public void createModFunction(final Vector v_ce, final Vector columnExpression, final int i_count) {
        final SelectColumn wc_firstarg = new SelectColumn();
        final SelectColumn wc_secondarg = new SelectColumn();
        final Vector v_farg = new Vector();
        final TableColumn tc = new TableColumn();
        final FunctionCalls fc = new FunctionCalls();
        final Vector vec_firstarg = new Vector();
        final Vector vec_secondarg = new Vector();
        tc.setColumnName("MOD");
        fc.setFunctionName(tc);
        vec_firstarg.addElement(columnExpression.elementAt(i_count - 1));
        v_ce.setElementAt(" ", i_count - 1);
        wc_firstarg.setColumnExpression(vec_firstarg);
        v_farg.addElement(wc_firstarg);
        vec_secondarg.addElement(columnExpression.elementAt(i_count + 1));
        columnExpression.setElementAt(" ", i_count + 1);
        wc_secondarg.setColumnExpression(vec_secondarg);
        v_farg.addElement(wc_secondarg);
        fc.setFunctionArguments(v_farg);
        v_ce.addElement(fc);
    }
    
    public void createCastFunction(final Vector v_ce, final Vector columnExpression, final int i_count) {
        final SelectColumn wc_firstarg = new SelectColumn();
        final SelectColumn wc_secondarg = new SelectColumn();
        final Vector v_farg = new Vector();
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
        wc_firstarg.setColumnExpression(vec_firstarg);
        v_farg.addElement(wc_firstarg);
        fc.setFunctionArguments(v_farg);
        v_ce.addElement(fc);
    }
    
    public WhereColumn toPostgreSQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhereColumn wc = new WhereColumn();
        String s_ce = new String();
        final Vector v_ce = new Vector();
        int division = 0;
        wc.setOpenBrace(this.OpenBrace);
        if (this.columnExpression != null) {
            for (int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
                if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
                    this.columnExpression.elementAt(i_count).setCastToTextInsideIf(false);
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
                    final CaseStatement cs = this.columnExpression.elementAt(i_count);
                    cs.setCastToTextInsideIf(false);
                    final FunctionCalls fc = cs.convertToFunctionCall(to_sqs, from_sqs);
                    if (fc == null) {
                        v_ce.addElement(this.columnExpression.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
                    }
                    else {
                        fc.setCastToTextInsideIf(false);
                        v_ce.addElement(fc.toPostgreSQLSelect(to_sqs, from_sqs));
                    }
                }
                else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toPostgreSQLSelect());
                }
                else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
                    this.columnExpression.elementAt(i_count).setCastToTextInsideIf(false);
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof Token) {
                    final String tokenStr = this.columnExpression.elementAt(i_count).toString().trim();
                    if (tokenStr.startsWith("/*") && tokenStr.endsWith("*/")) {
                        v_ce.addElement("");
                    }
                }
                else if (this.columnExpression.elementAt(i_count) instanceof String) {
                    s_ce = this.columnExpression.elementAt(i_count);
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
                    else if (s_ce.equalsIgnoreCase("**")) {
                        v_ce.addElement("^");
                    }
                    else if (s_ce.trim().equals("/")) {
                        v_ce.addElement("/");
                        division = 1;
                    }
                    else if (s_ce.trim().equals("DIV")) {
                        v_ce.addElement("/");
                    }
                    else if (s_ce.trim().startsWith("'") && s_ce.trim().endsWith("'") && from_sqs != null && !from_sqs.isAmazonRedShift()) {
                        Object element = s_ce;
                        final boolean canHandleStringLiterals = from_sqs.canHandleStringLiteralsForDateTime();
                        final boolean canHandleStringLiteralsForNumeric = from_sqs.canHandleStringLiteralsForNumeric();
                        final boolean isDateTimeValue = StringFunctions.isDateTimeValue(s_ce);
                        final boolean isNumericValue = StringFunctions.isNumericValue(s_ce);
                        if (canHandleStringLiterals) {
                            s_ce = StringFunctions.convertToAnsiDateFormatIfDateLiteralString(s_ce, false);
                        }
                        if (s_ce.equalsIgnoreCase("NULL")) {
                            element = s_ce;
                        }
                        else {
                            try {
                                if (canHandleStringLiteralsForNumeric && isNumericValue) {
                                    element = s_ce.trim().replaceAll("'", "");
                                }
                                else if (from_sqs.canCastStringLiteralToText() && !isDateTimeValue) {
                                    final FunctionCalls castFn = FunctionCalls.castToCharFunctionCall(s_ce);
                                    if (castFn != null) {
                                        element = castFn.toPostgreSQLSelect(to_sqs, from_sqs);
                                    }
                                }
                                else {
                                    element = s_ce;
                                }
                            }
                            catch (final Exception e) {
                                element = s_ce;
                            }
                        }
                        v_ce.addElement(element);
                    }
                    else if (s_ce.trim().startsWith("/*") && s_ce.trim().endsWith("*/")) {
                        v_ce.addElement("");
                    }
                    else {
                        v_ce.addElement(s_ce);
                    }
                }
                else {
                    v_ce.addElement(this.columnExpression.elementAt(i_count));
                }
                if (division > 0) {
                    if (division > 2) {
                        final Object obj = v_ce.get(i_count);
                        final String val = "-" + obj.toString();
                        v_ce.remove(i_count);
                        v_ce.remove(i_count - 1);
                        v_ce.add("");
                        String decimalVal = "NULLIF(" + val + ",0.0)";
                        if (obj instanceof String) {
                            decimalVal = StringFunctions.getDecimalString(val, decimalVal);
                        }
                        else if (obj instanceof SelectQueryStatement) {
                            decimalVal = "NULLIF((" + val + "), 0.0)";
                        }
                        v_ce.add(decimalVal);
                        division = 0;
                    }
                    else if (division > 1) {
                        final Object obj = v_ce.get(i_count);
                        final String val = obj.toString();
                        if (val.equals("-")) {
                            ++division;
                        }
                        else {
                            v_ce.remove(i_count);
                            String decimalVal = "NULLIF(" + val + ",0.0)";
                            if (obj instanceof String) {
                                decimalVal = StringFunctions.getDecimalString(val, decimalVal);
                            }
                            else if (obj instanceof SelectQueryStatement) {
                                decimalVal = "NULLIF((" + val + "), 0.0)";
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
            wc.setColumnExpression(v_ce);
        }
        wc.setCloseBrace(this.CloseBrace);
        return wc;
    }
    
    public WhereColumn toMySQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhereColumn wc = new WhereColumn();
        String s_ce = new String();
        final Vector v_ce = new Vector();
        wc.setOpenBrace(this.OpenBrace);
        if (this.columnExpression != null) {
            for (int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
                if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toMySQLSelect());
                }
                else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof String) {
                    s_ce = this.columnExpression.elementAt(i_count);
                    if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                        v_ce.addElement("CURRENT_TIME");
                    }
                    else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                        v_ce.addElement("CURRENT_DATE");
                    }
                    else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                        v_ce.addElement("CURRENT_TIMESTAMP");
                    }
                    else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
                        this.createPowerFunction(v_ce, this.columnExpression, i_count);
                    }
                    else if (s_ce.equalsIgnoreCase("::")) {
                        this.createCastFunction(v_ce, this.columnExpression, i_count);
                    }
                    else if (s_ce.indexOf(".") != -1 && s_ce.indexOf(".") == s_ce.lastIndexOf(".") && !s_ce.startsWith("'") && !this.isDecimal(s_ce)) {
                        final String s_ceTableName = s_ce.substring(0, s_ce.lastIndexOf("."));
                        final String s_ceColName = s_ce.substring(s_ce.lastIndexOf(".") + 1);
                        final TableColumn tc = new TableColumn();
                        tc.setColumnName(s_ceColName);
                        tc.setTableName(s_ceTableName);
                        v_ce.addElement(tc.toMySQLSelect(to_sqs, from_sqs));
                    }
                    else {
                        v_ce.addElement(s_ce);
                    }
                }
                else {
                    v_ce.addElement(this.columnExpression.elementAt(i_count));
                }
            }
            wc.setColumnExpression(v_ce);
        }
        wc.setCloseBrace(this.CloseBrace);
        return wc;
    }
    
    public WhereColumn toTimesTenSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhereColumn wc = new WhereColumn();
        String s_ce = new String();
        final Vector v_ce = new Vector();
        wc.setOpenBrace(this.OpenBrace);
        if (this.columnExpression != null) {
            for (int i = 0; i < this.columnExpression.size(); ++i) {
                if (this.columnExpression.elementAt(i) instanceof TableColumn) {
                    final TableColumn tc = this.columnExpression.elementAt(i);
                    if (!this.isLHSExpr()) {
                        this.handleTableColumn(tc, from_sqs, 10);
                    }
                    v_ce.addElement(tc.toTimesTenSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i) instanceof FunctionCalls) {
                    final FunctionCalls temp = this.columnExpression.elementAt(i).toTimesTenSelect(to_sqs, from_sqs);
                    v_ce.addElement(temp);
                    this.columnExpression.setElementAt(temp, i);
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
                    else if (this.columnExpression.elementAt(i) instanceof WhereColumn) {
                        final WhereColumn temp4 = this.columnExpression.elementAt(i).toTimesTenSelect(to_sqs, from_sqs);
                        v_ce.addElement(temp4);
                        this.columnExpression.setElementAt(temp4, i);
                    }
                    else if (this.columnExpression.elementAt(i) instanceof String) {
                        s_ce = this.columnExpression.elementAt(i);
                        if (s_ce.charAt(0) == '\"') {
                            v_ce.addElement(s_ce.replace('\"', '\''));
                        }
                        else if (s_ce.startsWith("'") && (s_ce.indexOf("-") != -1 || s_ce.indexOf("/") != -1)) {
                            if (s_ce.indexOf("-") != -1 || s_ce.indexOf("/") != -1) {
                                if (s_ce.indexOf("-") != -1) {
                                    v_ce.addElement(this.convertToOracleDateFormat(s_ce, "-", false));
                                }
                                else if (s_ce.indexOf("/") != -1) {
                                    v_ce.addElement(this.convertToOracleDateFormat(s_ce, "/", false));
                                }
                            }
                        }
                        else if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
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
                            else if (s_ce.equalsIgnoreCase("&")) {
                                String first_arg = "";
                                if (i >= 1) {
                                    if (this.columnExpression.elementAt(i - 1) instanceof SelectColumn) {
                                        first_arg = this.columnExpression.elementAt(i - 1).toTimesTenSelect(to_sqs, from_sqs).toString();
                                    }
                                    else if (this.columnExpression.elementAt(i - 1) instanceof FunctionCalls) {
                                        first_arg = this.columnExpression.elementAt(i - 1).toTimesTenSelect(to_sqs, from_sqs).toString();
                                    }
                                    else {
                                        for (int h = i - 1; h >= 0; --h) {
                                            String s = null;
                                            if (this.columnExpression.elementAt(h) instanceof TableColumn) {
                                                s = this.columnExpression.elementAt(h).toTimesTenSelect(to_sqs, from_sqs).toString();
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
                                            s = this.columnExpression.elementAt(h).toTimesTenSelect(to_sqs, from_sqs).toString();
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
                            else if (s_ce.equalsIgnoreCase("|")) {
                                String first_arg = "";
                                if (i >= 1) {
                                    if (this.columnExpression.elementAt(i - 1) instanceof SelectColumn) {
                                        first_arg = this.columnExpression.elementAt(i - 1).toTimesTenSelect(to_sqs, from_sqs).toString();
                                    }
                                    else {
                                        for (int h = i - 1; h >= 0; --h) {
                                            String s = null;
                                            if (this.columnExpression.elementAt(h) instanceof TableColumn) {
                                                s = this.columnExpression.elementAt(h).toTimesTenSelect(to_sqs, from_sqs).toString();
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
                                            s = this.columnExpression.elementAt(h).toTimesTenSelect(to_sqs, from_sqs).toString();
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
                            else if (s_ce.equalsIgnoreCase("^")) {
                                String first_arg = "";
                                if (i >= 1) {
                                    if (this.columnExpression.elementAt(i - 1) instanceof SelectColumn) {
                                        first_arg = this.columnExpression.elementAt(i - 1).toTimesTenSelect(to_sqs, from_sqs).toString();
                                    }
                                    else {
                                        for (int h = i - 1; h >= 0; --h) {
                                            String s = null;
                                            if (this.columnExpression.elementAt(h) instanceof TableColumn) {
                                                s = this.columnExpression.elementAt(h).toTimesTenSelect(to_sqs, from_sqs).toString();
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
                                            s = this.columnExpression.elementAt(h).toTimesTenSelect(to_sqs, from_sqs).toString();
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
                            else if (!s_ce.equalsIgnoreCase("::")) {
                                if (!s_ce.equalsIgnoreCase("**")) {
                                    if (s_ce.startsWith("@")) {
                                        v_ce.addElement(":" + s_ce.substring(1));
                                    }
                                    else if (s_ce.startsWith("0x") || s_ce.startsWith("0X")) {
                                        final String str = s_ce.substring(2);
                                        v_ce.addElement("HEX_TO_NUMBER('" + str + "')");
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
            }
            wc.setColumnExpression(v_ce);
        }
        wc.setCloseBrace(this.CloseBrace);
        return wc;
    }
    
    public WhereColumn toNetezzaSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhereColumn wc = new WhereColumn();
        String s_ce = new String();
        final Vector v_ce = new Vector();
        wc.setOpenBrace(this.OpenBrace);
        if (this.columnExpression != null) {
            for (int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
                if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toNetezzaSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toNetezzaSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toNetezzaSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toNetezzaSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toNetezzaSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toNetezzaSelect());
                }
                else if (this.columnExpression.elementAt(i_count) instanceof String) {
                    s_ce = this.columnExpression.elementAt(i_count);
                    if (s_ce.charAt(0) == '\"') {
                        v_ce.addElement(s_ce.replace('\"', '\''));
                    }
                    else if (s_ce.equalsIgnoreCase("**")) {
                        this.createPowerFunction(v_ce, this.columnExpression, i_count);
                    }
                    else {
                        v_ce.addElement(s_ce);
                    }
                }
                else {
                    v_ce.addElement(this.columnExpression.elementAt(i_count));
                }
            }
            wc.setColumnExpression(v_ce);
        }
        wc.setCloseBrace(this.CloseBrace);
        return wc;
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
    
    private void handleTableColumn(final TableColumn tc, final SelectQueryStatement from_sqs, final int target) {
        String columnName = tc.getColumnName();
        if (columnName != null && tc.getTableName() == null && columnName.trim().length() > 0) {
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
                            for (int i = 0; i < fromItems.size(); ++i) {
                                newFromItems.add(fromItems.get(i));
                            }
                        }
                    }
                }
                else if (this.fromDQS != null) {
                    fc = new FromClause();
                    final Vector newFromItems = new Vector();
                    fc.setFromItemList(newFromItems);
                    final TableExpression tExpr = this.fromDQS.getTableExpression();
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
                    final FromClause delFC = this.fromDQS.getFromClause();
                    if (delFC != null) {
                        final Vector fromItems = delFC.getFromItemList();
                        if (fromItems != null) {
                            for (int i = 0; i < fromItems.size(); ++i) {
                                newFromItems.add(fromItems.get(i));
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
                        for (int k = 0; k < fromItems2.size(); ++k) {
                            Object obj2 = fromItems2.get(k);
                            if (obj2 instanceof FromTable) {
                                obj2 = ((FromTable)obj2).getTableName();
                                if (obj2 instanceof String) {
                                    String tableName2 = (String)obj2;
                                    final int index = tableName2.lastIndexOf(".");
                                    if (index != -1) {
                                        tableName2.substring(index + 1);
                                    }
                                    if (tableName2.startsWith("\"") || tableName2.startsWith("[") || tableName2.startsWith("`")) {
                                        tableName2 = tableName2.substring(1, tableName2.length() - 1);
                                    }
                                    final ArrayList colList = (ArrayList)CastingUtil.getValueIgnoreCase(SwisSQLAPI.tableColumnListMetadata, tableName2);
                                    if (colList == null) {
                                        modify = false;
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
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.commentObj != null) {
            sb.append(this.commentObj.toString().trim() + " ");
        }
        if (this.OpenBrace != null) {
            sb.append(this.OpenBrace);
        }
        for (int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
            if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
                this.columnExpression.elementAt(i_count).setObjectContext(this.context);
            }
            else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
                this.columnExpression.elementAt(i_count).setObjectContext(this.context);
            }
            else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
                this.columnExpression.elementAt(i_count).setObjectContext(this.context);
            }
            if (this.columnExpression.elementAt(i_count) != null) {
                sb.append(this.columnExpression.elementAt(i_count).toString() + " ");
            }
        }
        if (this.CloseBrace != null) {
            sb.append(this.CloseBrace);
        }
        if (this.commentObjAfterToken != null) {
            sb.append(" " + this.commentObjAfterToken.toString().trim());
        }
        return sb.toString();
    }
    
    public String getTableAlias() {
        if (this.getColumnExpression() != null && this.getColumnExpression().size() > 0) {
            for (int i = 0; i < this.getColumnExpression().size(); ++i) {
                if (this.getColumnExpression().get(i) instanceof TableColumn) {
                    final TableColumn tc = this.getColumnExpression().get(i);
                    return tc.getTableName();
                }
                if (this.getColumnExpression().get(i) instanceof FunctionCalls) {
                    final Vector functionArguments = this.getColumnExpression().get(i).getFunctionArguments();
                    if (functionArguments != null) {
                        for (int j = 0; j < functionArguments.size(); ++j) {
                            if (functionArguments.get(j) instanceof SelectColumn) {
                                final String tableName = this.getTableAlias(functionArguments.get(j));
                                if (tableName != null && !tableName.equals("")) {
                                    return tableName;
                                }
                            }
                        }
                    }
                    else if (functionArguments == null && this.getColumnExpression().get(i) instanceof decode) {
                        final FunctionCalls fnc = this.getColumnExpression().get(i);
                        if (fnc.toString().trim().toLowerCase().startsWith("case") && fnc instanceof decode) {
                            final CaseStatement cs = ((decode)fnc).getCaseStatement();
                            final WhereItem cswi = cs.getCaseCondition().getWhereItem().get(0);
                            final SelectColumn cssc = cswi.getLeftWhereExp().getColumnExpression().get(0);
                            final String tableName2 = this.getTableAlias(cssc);
                            if (tableName2 != null && !tableName2.equals("")) {
                                return tableName2;
                            }
                        }
                    }
                }
                else if (this.getColumnExpression().get(i) instanceof String) {
                    final String tableAliaswhere = this.getColumnExpression().get(i);
                    if (tableAliaswhere.startsWith("'")) {
                        return "";
                    }
                    if (tableAliaswhere.indexOf(".") != -1) {
                        if (tableAliaswhere.indexOf(46) != tableAliaswhere.lastIndexOf(46) && SwisSQLOptions.removeDBSchemaQualifier) {
                            return tableAliaswhere.substring(tableAliaswhere.indexOf(46) + 1, tableAliaswhere.lastIndexOf("."));
                        }
                        return tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                    }
                }
            }
        }
        return null;
    }
    
    private String getTableAlias(final SelectColumn sc) {
        if (sc != null && sc.getColumnExpression() != null && sc.getColumnExpression().size() > 0) {
            for (int i = 0; i < sc.getColumnExpression().size(); ++i) {
                if (sc.getColumnExpression().get(i) instanceof TableColumn) {
                    final TableColumn tc = sc.getColumnExpression().get(i);
                    return tc.getTableName();
                }
                if (sc.getColumnExpression().get(i) instanceof FunctionCalls) {
                    final Vector functionArguments = sc.getColumnExpression().get(i).getFunctionArguments();
                    if (functionArguments != null) {
                        for (int j = 0; j < functionArguments.size(); ++j) {
                            if (functionArguments.get(j) instanceof SelectColumn) {
                                final String tableName = this.getTableAlias(functionArguments.get(j));
                                if (tableName != null) {
                                    return tableName;
                                }
                            }
                        }
                    }
                }
                else if (sc.getColumnExpression().get(i) instanceof String) {
                    final String tableAliaswhere = sc.getColumnExpression().get(i);
                    if (tableAliaswhere.startsWith("'")) {
                        return "";
                    }
                    if (tableAliaswhere.indexOf(".") != -1) {
                        if (tableAliaswhere.indexOf(46) != tableAliaswhere.lastIndexOf(46) && SwisSQLOptions.removeDBSchemaQualifier) {
                            return tableAliaswhere.substring(tableAliaswhere.indexOf(46) + 1, tableAliaswhere.lastIndexOf("."));
                        }
                        return tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
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
        for (int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
            final Object obj = this.columnExpression.elementAt(i_count);
            if (obj instanceof TableColumn) {
                final TableColumn tcnMod = (TableColumn)obj;
                if (tcnMod.getColumnName().equalsIgnoreCase("rownum")) {
                    this.columnExpression.setElementAt(newColumn, i_count);
                }
            }
            else if (obj instanceof FunctionCalls) {
                final FunctionCalls wcFunc = (FunctionCalls)obj;
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
            else if (obj instanceof WhereColumn) {
                ((WhereColumn)obj).replaceRownumTableColumn(newColumn);
            }
            else if (obj instanceof CaseStatement) {
                ((CaseStatement)obj).replaceRownumTableColumn(newColumn);
            }
            else if (!(obj instanceof SelectQueryStatement)) {
                if (obj instanceof SelectColumn) {
                    ((SelectColumn)obj).replaceRownumTableColumn(newColumn);
                }
                else if (obj instanceof String) {}
            }
        }
    }
    
    public WhereColumn toVectorWiseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhereColumn wc = new WhereColumn();
        String s_ce = new String();
        final Vector v_ce = new Vector();
        int division = 0;
        wc.setOpenBrace(this.OpenBrace);
        if (this.columnExpression != null) {
            for (int i_count = 0; i_count < this.columnExpression.size(); ++i_count) {
                if (this.columnExpression.elementAt(i_count) instanceof TableColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof FunctionCalls) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof WhereColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof CaseStatement) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof SelectQueryStatement) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toVectorWiseSelect());
                }
                else if (this.columnExpression.elementAt(i_count) instanceof SelectColumn) {
                    v_ce.addElement(this.columnExpression.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else if (this.columnExpression.elementAt(i_count) instanceof Token) {
                    final String tokenStr = this.columnExpression.elementAt(i_count).toString().trim();
                    if (tokenStr.startsWith("/*") && tokenStr.endsWith("*/")) {
                        v_ce.addElement("");
                    }
                }
                else if (this.columnExpression.elementAt(i_count) instanceof String) {
                    s_ce = this.columnExpression.elementAt(i_count);
                    if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
                        v_ce.addElement("CURRENT_TIME");
                    }
                    else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
                        v_ce.addElement("CURRENT_DATE");
                    }
                    else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                        v_ce.addElement("CURRENT_TIMESTAMP");
                    }
                    else if (s_ce.equalsIgnoreCase("**") | s_ce.equalsIgnoreCase("^")) {
                        this.createPowerFunction(v_ce, this.columnExpression, i_count);
                        final Object object = v_ce.get(v_ce.size() - 1);
                        if (object instanceof FunctionCalls) {
                            v_ce.set(v_ce.size() - 1, ((FunctionCalls)object).toVectorWiseSelect(to_sqs, from_sqs));
                        }
                    }
                    else if (s_ce.equalsIgnoreCase("::")) {
                        this.createCastFunction(v_ce, this.columnExpression, i_count);
                        final Object object = v_ce.get(v_ce.size() - 1);
                        if (object instanceof FunctionCalls) {
                            v_ce.set(v_ce.size() - 1, ((FunctionCalls)object).toVectorWiseSelect(to_sqs, from_sqs));
                        }
                    }
                    else if (s_ce.indexOf(".") != -1 && s_ce.indexOf(".") == s_ce.lastIndexOf(".") && !s_ce.startsWith("'") && !this.isDecimal(s_ce)) {
                        final String s_ceTableName = s_ce.substring(0, s_ce.lastIndexOf("."));
                        final String s_ceColName = s_ce.substring(s_ce.lastIndexOf(".") + 1);
                        final TableColumn tc = new TableColumn();
                        tc.setColumnName(s_ceColName);
                        tc.setTableName(s_ceTableName);
                        v_ce.addElement(tc.toVectorWiseSelect(to_sqs, from_sqs));
                    }
                    else if (s_ce.trim().equals("%")) {
                        this.createModFunction(v_ce, this.columnExpression, i_count);
                        final Object object = v_ce.get(v_ce.size() - 1);
                        if (object instanceof FunctionCalls) {
                            v_ce.set(v_ce.size() - 1, ((FunctionCalls)object).toVectorWiseSelect(to_sqs, from_sqs));
                        }
                    }
                    else if (s_ce.trim().equals("/")) {
                        v_ce.addElement("/");
                        division = 1;
                    }
                    else if (s_ce.trim().equals("DIV")) {
                        v_ce.addElement("/");
                    }
                    else if (s_ce.trim().startsWith("/*") && s_ce.trim().endsWith("*/")) {
                        v_ce.addElement("");
                    }
                    else {
                        final boolean canHandleStringLiterals = from_sqs != null && from_sqs.canHandleStringLiteralsForDateTime();
                        String element = s_ce;
                        if (canHandleStringLiterals) {
                            element = StringFunctions.convertToAnsiDateFormatIfDateLiteralString(s_ce, true);
                        }
                        v_ce.addElement(element);
                    }
                }
                else {
                    v_ce.addElement(this.columnExpression.elementAt(i_count));
                }
                if (division > 0) {
                    if (division > 2) {
                        final Object obj = v_ce.get(i_count);
                        final String val = "-" + obj.toString();
                        v_ce.remove(i_count);
                        v_ce.remove(i_count - 1);
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
                        final Object obj = v_ce.get(i_count);
                        final String val = obj.toString();
                        if (val.equals("-")) {
                            ++division;
                        }
                        else {
                            v_ce.remove(i_count);
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
            wc.setColumnExpression(v_ce);
        }
        wc.setCloseBrace(this.CloseBrace);
        return wc;
    }
}
