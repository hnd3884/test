package com.adventnet.swissqlapi.sql.statement.insert;

import java.util.StringTokenizer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import com.adventnet.swissqlapi.sql.statement.create.CreateQueryStatement;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.update.OptionalSpecifier;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.sql.statement.update.TableExpression;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.statement.update.TableClause;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.WithStatement;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.update.ReturningClause;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class InsertQueryStatement implements SwisSQLStatement
{
    private UserObjectContext objectContext;
    private InsertClause insertClause;
    private ValuesClause valuesClause;
    private SetClauseInsert setClause;
    private ReturningClause returningClause;
    private SelectQueryStatement subQuery;
    private SelectQueryStatement selectQueryStatement;
    private CommentClass commentObject;
    private ArrayList insertValList;
    private ArrayList commonTableExprList;
    private String iqsString;
    private String cqsString;
    private String dqsString;
    private String generalComments;
    private String isolationLevel;
    private String withString;
    private ArrayList lockTableList;
    public static ArrayList tablesWithRowIDColumnsList;
    String subQueryOpenBrace;
    String subQueryCloseBrace;
    String multiInsertAll;
    private ArrayList multiTableInsertStmtList;
    private ArrayList conditionalInsertClausesList;
    public WithStatement withStmt;
    private ArrayList multiValuesInsertStmtList;
    private String onDuplicateKeyUpdate;
    private ArrayList onDuplicateKeyUpdateExpList;
    
    public InsertQueryStatement() {
        this.objectContext = null;
        this.insertValList = null;
        this.commonTableExprList = new ArrayList();
        this.isolationLevel = null;
        this.withString = null;
        this.lockTableList = new ArrayList();
        this.subQueryOpenBrace = null;
        this.subQueryCloseBrace = null;
        this.multiInsertAll = null;
        this.multiTableInsertStmtList = new ArrayList();
        this.conditionalInsertClausesList = new ArrayList();
        this.withStmt = null;
        this.multiValuesInsertStmtList = null;
        this.onDuplicateKeyUpdate = null;
        this.onDuplicateKeyUpdateExpList = new ArrayList();
        this.insertClause = null;
        this.valuesClause = null;
        this.setClause = null;
        this.returningClause = null;
        this.subQuery = null;
    }
    
    @Override
    public void setCommentClass(final CommentClass commentObject) {
        this.commentObject = commentObject;
    }
    
    @Override
    public CommentClass getCommentClass() {
        return this.commentObject;
    }
    
    public void setMultipleValuesInsertStmts(final ArrayList insertStmts) {
        this.multiValuesInsertStmtList = insertStmts;
    }
    
    public void setInsertClause(final InsertClause IC) {
        this.insertClause = IC;
    }
    
    public InsertClause getInsertClause() {
        return this.insertClause;
    }
    
    public void setValuesClause(final ValuesClause VC) {
        this.valuesClause = VC;
    }
    
    public void setSetClause(final SetClauseInsert SC) {
        this.setClause = SC;
    }
    
    public void setOnDuplicateKeyUpdate(final String onDuplicateKeyUpdate) {
        this.onDuplicateKeyUpdate = onDuplicateKeyUpdate;
    }
    
    public void setOnDuplicateKeyUpdateExpression(final ArrayList onDuplicateKeyUpdateExpList) {
        this.onDuplicateKeyUpdateExpList = onDuplicateKeyUpdateExpList;
    }
    
    public void setSubQuery(final SelectQueryStatement squery) {
        this.subQuery = squery;
    }
    
    public void setReturningClause(final ReturningClause RC) {
        this.returningClause = RC;
    }
    
    public void setSelectQueryStatement(final SelectQueryStatement sqs) {
        this.selectQueryStatement = sqs;
    }
    
    public void setGeneralComments(final String generalComments) {
        this.generalComments = generalComments;
    }
    
    public void setWithString(final String w) {
        this.withString = w;
    }
    
    public void setIsolationLevel(final String il) {
        this.isolationLevel = il;
    }
    
    public String getIsolationLevel() {
        return this.isolationLevel;
    }
    
    public void addLockTableList(final String lockTableStt) {
        this.lockTableList.add(lockTableStt);
    }
    
    public void setCommonTableExprList(final ArrayList commonTableExprList) {
        this.commonTableExprList = commonTableExprList;
    }
    
    public void setMultiInsertALL(final String all) {
        this.multiInsertAll = all;
    }
    
    public void setMultiTableInsertStmtList(final ArrayList insertStmtList) {
        this.multiTableInsertStmtList = insertStmtList;
    }
    
    public void setMultiTableInsertStmt(final ArrayList insertStmtList) {
        this.multiTableInsertStmtList = insertStmtList;
    }
    
    public void setConditionalInsertClausesList(final ArrayList conditionalInsertList) {
        this.conditionalInsertClausesList = conditionalInsertList;
    }
    
    public SelectQueryStatement getSubQuery() {
        if (this.subQuery != null) {
            return this.subQuery;
        }
        return this.selectQueryStatement;
    }
    
    public ValuesClause getValuesClause() {
        return this.valuesClause;
    }
    
    public SetClauseInsert getSetClause() {
        return this.setClause;
    }
    
    public String getOnDuplicateKeyUpdate() {
        return this.onDuplicateKeyUpdate;
    }
    
    public ArrayList getOnDuplicateKeyUpdateExpression() {
        return this.onDuplicateKeyUpdateExpList;
    }
    
    public ReturningClause getReturningClause() {
        return this.returningClause;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.commentObject != null) {
            final String commentStr = this.commentObject.toString().trim();
            sb.append(commentStr + "\n ");
        }
        for (int k = 0; k < this.lockTableList.size(); ++k) {
            sb.append(this.lockTableList.get(k).toString() + ";\n");
        }
        if (this.singleQueryIntoMultipleQueriesForPLSQL() != null) {
            sb.append(this.singleQueryIntoMultipleQueriesForPLSQL());
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
        }
        if (this.cqsString != null) {
            sb.append(this.cqsString.trim() + ";\n\n");
        }
        if (this.generalComments != null) {
            sb.append(this.generalComments + "\n\n");
        }
        if (this.iqsString != null) {
            sb.append(this.iqsString.trim() + ";\n\n");
        }
        if (this.insertClause != null) {
            if (this.insertClause.getLockStatement() != null) {
                sb.append(this.insertClause.getLockStatement() + "\n");
            }
            if (this.objectContext != null) {
                this.insertClause.setObjectContext(this.objectContext);
            }
            sb.append(this.insertClause.toString() + " \n");
        }
        if (this.valuesClause != null) {
            this.valuesClause.setObjectContext(this.objectContext);
            sb.append(this.valuesClause.toString() + " \n");
        }
        else if (this.setClause != null) {
            this.setClause.setObjectContext(this.objectContext);
            sb.append(this.setClause.toString() + " \n");
        }
        if (this.onDuplicateKeyUpdate != null) {
            sb.append(this.onDuplicateKeyUpdate + " \n");
            if (this.onDuplicateKeyUpdateExpList != null && this.onDuplicateKeyUpdateExpList.size() != 0) {
                for (int size = this.onDuplicateKeyUpdateExpList.size(), i = 0; i < size; ++i) {
                    sb.append(this.onDuplicateKeyUpdateExpList.get(i) + " ");
                }
            }
        }
        if (this.returningClause != null) {
            sb.append(this.returningClause.toString() + " \n");
        }
        if (this.subQuery != null) {
            this.subQuery.setObjectContext(this.objectContext);
            if (this.subQueryOpenBrace != null) {
                sb.append(this.subQueryOpenBrace);
            }
            sb.append(this.subQuery.toString());
            if (this.subQueryCloseBrace != null) {
                sb.append(this.subQueryCloseBrace + " \n");
            }
        }
        if (!this.commonTableExprList.isEmpty()) {
            if (this.withStmt != null) {
                sb.append(this.withStmt.toString());
            }
            else {
                for (int j = 0; j < this.commonTableExprList.size(); ++j) {
                    sb.append(this.commonTableExprList.get(j));
                    if (j + 1 < this.commonTableExprList.size()) {
                        sb.append(",\n");
                    }
                }
            }
            sb.append("\n");
        }
        if (this.selectQueryStatement != null) {
            this.selectQueryStatement.setObjectContext(this.objectContext);
            sb.append(this.selectQueryStatement.toString());
        }
        if (this.dqsString != null) {
            final StringBuffer sb2 = new StringBuffer();
            sb2.append(sb.toString().trim() + ";\n\n" + this.dqsString + "\n\n");
            return sb2.toString();
        }
        if (this.withString != null) {
            sb.append(this.withString + " ");
        }
        if (this.isolationLevel != null) {
            sb.append(this.isolationLevel);
        }
        if (this.multiValuesInsertStmtList != null) {
            for (int j = 0; j < this.multiValuesInsertStmtList.size(); ++j) {
                final Object obj = this.multiValuesInsertStmtList.get(j);
                if (obj instanceof InsertQueryStatement) {
                    sb.append(";\n");
                    final InsertQueryStatement insQueryStmt = (InsertQueryStatement)obj;
                    sb.append(insQueryStmt.toString());
                }
            }
        }
        return sb.toString();
    }
    
    @Override
    public String removeIndent(String s_ri) {
        s_ri = s_ri.replace('\n', ' ');
        s_ri = s_ri.replace('\t', ' ');
        return s_ri;
    }
    
    @Override
    public String toInformixString() throws ConvertException {
        if (this.onDuplicateKeyUpdate != null) {
            throw new ConvertException("On dupicate key update clause is yet to be supported");
        }
        this.withString = null;
        this.isolationLevel = null;
        this.insertClause.toInformix(this);
        if (this.setClause != null) {
            this.setClause.toGeneric(this);
            this.setClause = null;
        }
        if (this.valuesClause != null) {
            final ValuesClause tempValuesClause = this.valuesClause.toInformix();
            this.setValuesClause(tempValuesClause);
        }
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toInformixSelect();
            this.subQueryOpenBrace = null;
            this.subQueryCloseBrace = null;
        }
        if (this.selectQueryStatement != null) {
            this.selectQueryStatement = this.selectQueryStatement.toInformixSelect();
        }
        return this.toString();
    }
    
    @Override
    public String toOracleString() throws ConvertException {
        if (this.multiInsertAll != null) {
            throw new ConvertException("Multi table insert statement yet to be supported");
        }
        if (this.onDuplicateKeyUpdate != null) {
            throw new ConvertException("On dupicate key update clause is yet to be supported");
        }
        this.insertClause.toOracle(this);
        if (this.setClause != null) {
            this.setClause.toGeneric(this);
            this.setClause = null;
        }
        if (this.valuesClause != null) {
            this.valuesClause.setInsertQueryStatement(this);
            final ValuesClause tempValuesClause = this.valuesClause.toOracle();
            this.setValuesClause(tempValuesClause);
        }
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toOracleSelect();
            this.subQueryOpenBrace = "(";
            this.subQueryCloseBrace = ")";
        }
        if (!this.commonTableExprList.isEmpty()) {
            (this.withStmt = new WithStatement()).setWith("WITH");
            final Vector cteList = new Vector();
            for (int i = 0; i < this.commonTableExprList.size(); ++i) {
                final CommonTableExpression cte = this.commonTableExprList.get(i);
                final String withStr = cte.getWith();
                if (withStr != null) {
                    cte.setWith(null);
                }
                cteList.add(cte);
                this.commonTableExprList.set(i, cte);
            }
            this.withStmt.setCommonTableExpressionList(cteList);
            this.withStmt.setWithSQS(this.selectQueryStatement);
            this.setSelectQueryStatement(null);
            this.withStmt.toOracle();
        }
        if (this.selectQueryStatement != null) {
            this.selectQueryStatement = this.selectQueryStatement.toOracleSelect();
        }
        if (this.withString != null && this.isolationLevel != null) {
            String lockStatement = "LOCK TABLE ";
            final ArrayList tableList = this.getInsertClause().getTableExpression().getTableClauseList();
            for (int j = 0; j < tableList.size(); ++j) {
                final Object o = tableList.get(j);
                if (o instanceof TableClause) {
                    final TableClause tc = (TableClause)o;
                    final String s = tc.getTableObject().getTableName();
                    lockStatement = lockStatement + s + " IN ";
                    if (this.isolationLevel.trim().equalsIgnoreCase("RR") || this.isolationLevel.trim().equalsIgnoreCase("RS")) {
                        lockStatement += "EXCLUSIVE MODE";
                        this.addLockTableList(lockStatement);
                    }
                }
            }
        }
        this.withString = null;
        this.isolationLevel = null;
        return this.toString();
    }
    
    @Override
    public String toMySQLString() throws ConvertException {
        this.withString = null;
        this.isolationLevel = null;
        this.insertClause.toMySQL(this);
        if (this.valuesClause != null) {
            final ValuesClause tempValuesClause = this.valuesClause.toMySQL();
            this.setValuesClause(tempValuesClause);
        }
        if (this.subQuery != null) {
            throw new ConvertException();
        }
        return this.toString();
    }
    
    @Override
    public String toMSSQLServerString() throws ConvertException {
        if (this.onDuplicateKeyUpdate != null) {
            throw new ConvertException("On dupicate key update clause is yet to be supported");
        }
        this.withString = null;
        this.isolationLevel = null;
        this.insertClause.toSQLServer(this);
        if (this.setClause != null) {
            this.setClause.toGeneric(this);
            this.setClause = null;
        }
        if (this.valuesClause != null) {
            this.valuesClause.setInsertQueryStatement(this);
            final ValuesClause tempValuesClause = this.valuesClause.toMSSQLServer();
            this.setValuesClause(tempValuesClause);
        }
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toMSSQLServerSelect();
            this.subQueryOpenBrace = null;
            this.subQueryCloseBrace = null;
        }
        if (this.returningClause != null) {
            if (SwisSQLOptions.isReturningClauseConversionNeeded) {
                final SelectQueryStatement sqs = new SelectQueryStatement();
                final SelectStatement ss = new SelectStatement();
                final SelectColumn sc1 = new SelectColumn();
                final ArrayList list = this.returningClause.getrhsVariableList();
                final StringBuffer stringbuffer = new StringBuffer();
                for (int i = 0, size = list.size(); i < size; ++i) {
                    stringbuffer.append(" " + list.get(i).toString() + " ");
                }
                final Vector v = new Vector();
                v.add((Object)stringbuffer + " = SCOPE_IDENTITY()");
                sc1.setColumnExpression(v);
                final Vector items = new Vector();
                items.add(sc1);
                ss.setSelectItemList(items);
                ss.setSelectClause("SELECT");
                sqs.setSelectStatement(ss);
                this.selectQueryStatement = sqs;
            }
            this.returningClause = null;
        }
        if (this.selectQueryStatement != null) {
            this.selectQueryStatement = this.selectQueryStatement.toMSSQLServerSelect();
        }
        return this.toString();
    }
    
    @Override
    public String toSybaseString() throws ConvertException {
        if (this.onDuplicateKeyUpdate != null) {
            throw new ConvertException("On dupicate key update clause is yet to be supported");
        }
        this.withString = null;
        this.isolationLevel = null;
        this.insertClause.toSybase(this);
        if (this.setClause != null) {
            this.setClause.toGeneric(this);
            this.setClause = null;
        }
        if (this.valuesClause != null) {
            final ValuesClause tempValuesClause = this.valuesClause.toSybase();
            this.setValuesClause(tempValuesClause);
        }
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toSybaseSelect();
            this.subQueryOpenBrace = null;
            this.subQueryCloseBrace = null;
        }
        if (this.selectQueryStatement != null) {
            this.selectQueryStatement = this.selectQueryStatement.toSybaseSelect();
        }
        return this.toString();
    }
    
    @Override
    public String toPostgreSQLString() throws ConvertException {
        if (this.onDuplicateKeyUpdate != null) {
            throw new ConvertException("On dupicate key update clause is yet to be supported");
        }
        this.withString = null;
        this.isolationLevel = null;
        this.insertClause.toPostgres(this);
        if (this.setClause != null) {
            this.setClause.toGeneric(this);
            this.setClause = null;
        }
        if (this.valuesClause != null) {
            final ValuesClause tempValuesClause = this.valuesClause.toPostgreSQL();
            this.setValuesClause(tempValuesClause);
        }
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toPostgreSQLSelect();
            this.subQueryOpenBrace = "(";
            this.subQueryCloseBrace = ")";
        }
        if (this.selectQueryStatement != null) {
            this.selectQueryStatement = this.selectQueryStatement.toPostgreSQLSelect();
        }
        return this.toString();
    }
    
    @Override
    public String toDB2String() throws ConvertException {
        if (this.onDuplicateKeyUpdate != null) {
            throw new ConvertException("On dupicate key update clause is yet to be supported");
        }
        this.insertClause.toDB2(this);
        if (this.setClause != null) {
            this.setClause.toGeneric(this);
            this.setClause = null;
        }
        if (this.subQuery != null) {
            final TableExpression te = this.insertClause.getTableExpression();
            final ArrayList tcList = te.getTableClauseList();
            if (tcList != null && tcList.size() == 1 && tcList.get(0) instanceof TableClause) {
                final ArrayList columnList = this.insertClause.getColumnList();
                final TableClause tableClause = tcList.get(0);
                final TableObject to = tableClause.getTableObject();
                if (to != null) {
                    final String tableName = to.getTableName().toLowerCase().trim();
                    if (SwisSQLOptions.addRowidColumnForAllDB2Tables && tableName != null && InsertQueryStatement.tablesWithRowIDColumnsList.contains(tableName)) {
                        final SelectStatement ss = this.subQuery.getSelectStatement();
                        if (ss.getSelectItemList() != null) {
                            final Vector selectList = ss.getSelectItemList();
                            final SelectColumn sc = new SelectColumn();
                            final TableColumn tc = new TableColumn();
                            tc.setColumnName("GENERATE_UNIQUE()");
                            final Vector selectColumnList = new Vector();
                            selectColumnList.add(tc);
                            sc.setColumnExpression(selectColumnList);
                            sc.setEndsWith(",");
                            if (columnList != null && columnList.size() > 0) {
                                columnList.add(1, "RowId");
                                columnList.add(2, ",");
                            }
                            selectList.add(0, sc);
                        }
                    }
                }
            }
            this.subQuery = this.subQuery.toDB2Select();
            this.subQueryOpenBrace = "(";
            this.subQueryCloseBrace = ")";
        }
        if (this.insertClause != null && this.insertClause.getColumnList() != null) {
            final ArrayList columnList2 = this.insertClause.getColumnList();
            this.insertValList = new ArrayList();
            final TableClause tableClause2 = this.insertClause.getTableExpression().getTableClauseList().get(0);
            final String tableName2 = tableClause2.getTableObject().getTableName();
            for (int i = 0; i < columnList2.size(); ++i) {
                final Object obj = columnList2.get(i);
                if (obj instanceof String) {
                    final String colName = (String)obj;
                    if (!colName.trim().equals("(") && !colName.trim().equals(",") && !colName.trim().equals(")")) {
                        final TableColumn tableColumn = new TableColumn();
                        tableColumn.setTableName(tableName2);
                        tableColumn.setColumnName(colName);
                        this.insertValList.add(tableColumn);
                    }
                }
            }
        }
        if (this.valuesClause != null) {
            if (this.insertClause != null && this.insertValList != null && this.insertValList.size() > 0) {
                this.valuesClause.setInsertValList(this.insertValList);
            }
            final TableExpression te = this.insertClause.getTableExpression();
            final ArrayList tcList = te.getTableClauseList();
            if (tcList != null && tcList.size() == 1 && tcList.get(0) instanceof TableClause) {
                final TableClause tableClause3 = tcList.get(0);
                final TableObject to2 = tableClause3.getTableObject();
                if (to2 != null) {
                    final String tableName3 = to2.getTableName().toLowerCase().trim();
                    final ArrayList columnList3 = this.insertClause.getColumnList();
                    if (SwisSQLOptions.addRowidColumnForAllDB2Tables && tableName3 != null && InsertQueryStatement.tablesWithRowIDColumnsList.contains(tableName3)) {
                        final ArrayList valuesList = this.valuesClause.getValuesList();
                        final SelectColumn sc2 = new SelectColumn();
                        final TableColumn tc2 = new TableColumn();
                        tc2.setColumnName("GENERATE_UNIQUE()");
                        final Vector selectColumnList2 = new Vector();
                        selectColumnList2.add(tc2);
                        sc2.setColumnExpression(selectColumnList2);
                        sc2.setEndsWith(",");
                        if (columnList3 != null && columnList3.size() > 0) {
                            columnList3.add(1, "RowId");
                            columnList3.add(2, ",");
                        }
                        valuesList.add(1, sc2);
                    }
                }
            }
            final ValuesClause tempValuesClause = this.valuesClause.toDB2();
            this.setValuesClause(tempValuesClause);
        }
        if (this.selectQueryStatement != null) {
            if (this.insertClause != null && this.insertValList != null && this.insertValList.size() > 0) {
                this.selectQueryStatement.setInsertValList(this.insertValList);
            }
            this.selectQueryStatement = this.selectQueryStatement.toDB2Select();
        }
        return this.toString();
    }
    
    @Override
    public String toANSIString() throws ConvertException {
        if (this.onDuplicateKeyUpdate != null) {
            throw new ConvertException("On dupicate key update clause is yet to be supported");
        }
        this.withString = null;
        this.isolationLevel = null;
        this.insertClause.toANSISQL(this);
        if (this.valuesClause != null) {
            final ValuesClause tempValuesClause = this.valuesClause.toANSI();
            this.setValuesClause(tempValuesClause);
        }
        if (this.setClause != null) {
            this.setClause.toGeneric(this);
            this.setClause = null;
        }
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toANSISelect();
            this.subQueryOpenBrace = "(";
            this.subQueryCloseBrace = ")";
        }
        if (this.selectQueryStatement != null) {
            this.selectQueryStatement = this.selectQueryStatement.toANSISelect();
        }
        return this.toString();
    }
    
    @Override
    public String toTeradataString() throws ConvertException {
        if (this.onDuplicateKeyUpdate != null) {
            throw new ConvertException("On dupicate key update clause is yet to be supported");
        }
        this.withString = null;
        this.isolationLevel = null;
        if (this.commentObject != null) {
            this.commentObject.setSQLDialect(12);
        }
        this.insertClause.toTeradata(this);
        if (this.valuesClause != null) {
            final ValuesClause tempValuesClause = this.valuesClause.toTeradata();
            this.setValuesClause(tempValuesClause);
        }
        if (this.setClause != null) {
            this.setClause.toGeneric(this);
            this.setClause = null;
        }
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toTeradataSelect();
            this.subQueryOpenBrace = "(";
            this.subQueryCloseBrace = ")";
        }
        if (this.selectQueryStatement != null) {
            this.selectQueryStatement.setTopLevel(true);
            this.selectQueryStatement = this.selectQueryStatement.toTeradataSelect();
        }
        return this.toString();
    }
    
    @Override
    public String toTimesTenString() throws ConvertException {
        if (this.onDuplicateKeyUpdate != null) {
            throw new ConvertException("On dupicate key update clause is yet to be supported");
        }
        this.withString = null;
        this.isolationLevel = null;
        this.insertClause.toTimesTen(this);
        if (this.setClause != null) {
            this.setClause = null;
        }
        if (this.valuesClause != null) {
            final ValuesClause tempValuesClause = this.valuesClause.toTimesTen();
            this.setValuesClause(tempValuesClause);
        }
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toTimesTenSelect();
            this.subQueryOpenBrace = "(";
            this.subQueryCloseBrace = ")";
        }
        if (this.selectQueryStatement != null) {
            final TableExpression te = this.insertClause.getTableExpression();
            final ArrayList tblClauseList = te.getTableClauseList();
            final String iqsTableName = tblClauseList.get(0).getTableObject().getTableName();
            final FromClause fc = this.selectQueryStatement.getFromClause();
            if (fc != null) {
                final Vector fromItems = fc.getFromItemList();
                if (fromItems.size() == 1) {
                    final Object obj = fromItems.get(0).getTableName();
                    if (obj instanceof String && obj.toString().equalsIgnoreCase(iqsTableName)) {
                        final CreateQueryStatement cqs = SwisSQLUtils.constructCQS("ADV_IQSTABLE1", this.selectQueryStatement, this);
                        this.cqsString = cqs.toTimesTenString();
                        final InsertQueryStatement newIQS = new InsertQueryStatement();
                        final InsertClause ic = new InsertClause();
                        ic.setInsert("INSERT");
                        final OptionalSpecifier optionalSpecifier = new OptionalSpecifier();
                        optionalSpecifier.setInto("INTO");
                        ic.setOptionalSpecifier(optionalSpecifier);
                        newIQS.setInsertClause(ic);
                        final TableExpression texpr = new TableExpression();
                        final ArrayList newList = new ArrayList();
                        final TableClause tc = new TableClause();
                        final TableObject to = new TableObject();
                        to.setTableName("ADV_IQSTABLE1");
                        tc.setTableObject(to);
                        newList.add(tc);
                        texpr.setTableClauseList(newList);
                        ic.setTableExpression(texpr);
                        final SelectQueryStatement newSQS = new SelectQueryStatement();
                        newSQS.setSelectStatement(this.selectQueryStatement.getSelectStatement());
                        final FromClause newFC = new FromClause();
                        newFC.setFromClause("FROM");
                        final Vector newFromItems = new Vector();
                        final FromTable newFT = new FromTable();
                        newFT.setTableName(obj.toString());
                        newFromItems.add(newFT);
                        newFC.setFromItemList(newFromItems);
                        newSQS.setFromClause(newFC);
                        newIQS.setSelectQueryStatement(newSQS);
                        final Vector sourceSItems = this.selectQueryStatement.getSelectStatement().getSelectItemList();
                        boolean isAliasExists = false;
                        for (int k = 0; k < sourceSItems.size(); ++k) {
                            final Object sourceObj = sourceSItems.get(k);
                            if (sourceObj instanceof SelectColumn && ((SelectColumn)sourceObj).getAliasName() != null) {
                                isAliasExists = true;
                                break;
                            }
                        }
                        if (!isAliasExists) {
                            final Vector newSelItems = new Vector();
                            final Vector colNames = cqs.getColumnNames();
                            final ArrayList colList = new ArrayList();
                            colList.add("(");
                            for (int i = 0; i < colNames.size(); ++i) {
                                final TableColumn tCol = new TableColumn();
                                tCol.setColumnName(colNames.get(i).getColumnName());
                                final SelectColumn sCol = new SelectColumn();
                                final Vector colExpr = new Vector();
                                colExpr.add(tCol);
                                sCol.setColumnExpression(colExpr);
                                if (i != colNames.size() - 1) {
                                    sCol.setEndsWith(",");
                                }
                                newSelItems.add(sCol);
                                colList.add(tCol.getColumnName());
                                if (i != colNames.size() - 1) {
                                    colList.add(",");
                                }
                            }
                            colList.add(")");
                            this.insertClause.setColumnList(colList);
                            this.selectQueryStatement.getSelectStatement().setSelectItemList(newSelItems);
                        }
                        this.iqsString = newIQS.toString();
                        this.dqsString = "DROP TABLE ADV_IQSTABLE1;";
                        fromItems.get(0).setTableName("ADV_IQSTABLE1");
                    }
                }
            }
            this.selectQueryStatement = this.selectQueryStatement.toTimesTenSelect();
        }
        this.setReturningClause(null);
        return this.toString();
    }
    
    @Override
    public String toNetezzaString() throws ConvertException {
        if (this.onDuplicateKeyUpdate != null) {
            throw new ConvertException("On dupicate key update clause is yet to be supported");
        }
        this.withString = null;
        this.isolationLevel = null;
        if (this.multiInsertAll != null && this.multiTableInsertStmtList.size() > 0) {
            final ArrayList convertedStmtList = this.splitMultitableInsertStatement(this.multiTableInsertStmtList);
            final StringBuffer convertedInsertStmtStr = new StringBuffer();
            for (int i = 0; i < convertedStmtList.size(); ++i) {
                final InsertQueryStatement newInsertQuery = convertedStmtList.get(i);
                convertedInsertStmtStr.append(newInsertQuery.toNetezzaString() + "\n");
            }
            return convertedInsertStmtStr.toString();
        }
        if (this.multiInsertAll != null && this.conditionalInsertClausesList.size() > 0) {
            final ArrayList convertedStmtList = new ArrayList();
            for (int j = 0; j < this.conditionalInsertClausesList.size(); ++j) {
                final ConditionalInsertClause conditionalInsertCl = this.conditionalInsertClausesList.get(j);
                final InsertQueryStatement newInsertQuery = conditionalInsertCl.toNetezza(this);
                convertedStmtList.add(newInsertQuery);
            }
            final StringBuffer convertedInsertStmtStr = new StringBuffer();
            for (int i = 0; i < convertedStmtList.size(); ++i) {
                final InsertQueryStatement convInsertQuery = convertedStmtList.get(i);
                convertedInsertStmtStr.append(convInsertQuery.toNetezzaString() + "\n");
            }
            return convertedInsertStmtStr.toString();
        }
        this.insertClause.toNetezza(this);
        if (this.valuesClause != null) {
            final ValuesClause tempValuesClause = this.valuesClause.toNetezza();
            this.setValuesClause(tempValuesClause);
        }
        if (this.setClause != null) {
            this.setClause.toGeneric(this);
            this.setClause = null;
        }
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toNetezzaSelect();
            this.subQueryOpenBrace = "(";
            this.subQueryCloseBrace = ")";
        }
        if (this.selectQueryStatement != null) {
            this.selectQueryStatement = this.selectQueryStatement.toNetezzaSelect();
        }
        if (this.returningClause != null) {
            this.returningClause = null;
        }
        return this.toString();
    }
    
    private String singleQueryIntoMultipleQueriesForPLSQL() {
        return SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
    }
    
    @Override
    public UserObjectContext getObjectContext() {
        return this.objectContext;
    }
    
    @Override
    public void setObjectContext(final UserObjectContext obj) {
        this.objectContext = obj;
    }
    
    private ArrayList splitMultitableInsertStatement(final ArrayList insertStmtList) {
        final ArrayList convertedInsertStmtList = new ArrayList();
        for (int i = 0; i < insertStmtList.size(); ++i) {
            final InsertQueryStatement insertQuery = insertStmtList.get(i);
            final SelectQueryStatement insertSubQuery = new SelectQueryStatement();
            final SelectStatement insertSubQuerySelectStmt = new SelectStatement();
            insertSubQuerySelectStmt.setSelectClause("SELECT");
            final Vector selectItems = new Vector();
            for (int j = 0; j < insertQuery.getValuesClause().getValuesList().size(); ++j) {
                final Object obj = insertQuery.getValuesClause().getValuesList().get(j);
                if (!obj.toString().equalsIgnoreCase("(") && !obj.toString().equalsIgnoreCase(")")) {
                    selectItems.add(obj);
                }
            }
            insertSubQuerySelectStmt.setSelectItemList(selectItems);
            final FromClause fromClause = new FromClause();
            fromClause.setFromClause("FROM");
            final FromTable fromTable = new FromTable();
            fromTable.setTableName(this.getSubQuery());
            final Vector fromItems = new Vector();
            fromItems.add(fromTable);
            fromClause.setFromItemList(fromItems);
            insertSubQuery.setFromClause(fromClause);
            insertSubQuery.setSelectStatement(insertSubQuerySelectStmt);
            insertQuery.setSubQuery(insertSubQuery);
            insertQuery.setValuesClause(null);
            convertedInsertStmtList.add(insertQuery);
        }
        return convertedInsertStmtList;
    }
    
    @Override
    public String toVectorWiseString() throws ConvertException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    static {
        InsertQueryStatement.tablesWithRowIDColumnsList = new ArrayList();
        try {
            final FileInputStream fis = new FileInputStream("conf/TablesHavingRowIdColumns.conf");
            final InputStreamReader isr = new InputStreamReader(fis);
            final BufferedReader br = new BufferedReader(isr);
            String functionString = new String();
            functionString = br.readLine();
            while (functionString != null) {
                if (functionString.trim().equals("") || functionString.trim().startsWith("--")) {
                    functionString = br.readLine();
                }
                else if (functionString.trim().indexOf("--") != -1) {
                    functionString = functionString.trim().substring(0, functionString.trim().indexOf("--"));
                    final StringTokenizer st = new StringTokenizer(functionString.trim());
                    if (st.countTokens() != 1) {
                        continue;
                    }
                    InsertQueryStatement.tablesWithRowIDColumnsList.add(st.nextToken());
                }
                else if (functionString.trim().indexOf("/*") != -1) {
                    if (!functionString.trim().startsWith("/*")) {
                        functionString = functionString.trim().substring(0, functionString.trim().indexOf("/*"));
                        final StringTokenizer st = new StringTokenizer(functionString.trim());
                        if (st.countTokens() == 1) {
                            InsertQueryStatement.tablesWithRowIDColumnsList.add(st.nextToken());
                        }
                    }
                    while (functionString != null && functionString.indexOf("*/") == -1) {
                        functionString = br.readLine();
                    }
                    if (functionString == null || functionString.trim().length() - 2 == functionString.trim().indexOf("*/")) {
                        continue;
                    }
                    functionString = functionString.trim().substring(functionString.trim().indexOf("*/") + 2);
                    InsertQueryStatement.tablesWithRowIDColumnsList.add(functionString.toLowerCase());
                }
                else {
                    InsertQueryStatement.tablesWithRowIDColumnsList.add(functionString.toLowerCase());
                    functionString = br.readLine();
                }
            }
            br.close();
            isr.close();
            fis.close();
        }
        catch (final Exception ex) {}
    }
}
