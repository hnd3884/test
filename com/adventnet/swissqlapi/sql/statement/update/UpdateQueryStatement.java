package com.adventnet.swissqlapi.sql.statement.update;

import java.util.Collection;
import com.adventnet.swissqlapi.sql.statement.select.CaseStatement;
import com.adventnet.swissqlapi.sql.statement.select.RownumClause;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import java.util.Hashtable;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class UpdateQueryStatement implements SwisSQLStatement
{
    private UserObjectContext objectContext;
    private UpdateClause updateClause;
    private TableExpression tableExp;
    private HintClause hintClause;
    private SetClause setClause;
    private FromClause fromClause;
    private WhereCurrentClause whereCurrentClause;
    private WhereExpression whereExpression;
    private ReturningClause returningClause;
    private OrderByStatement orderByStatement;
    private UpdateLimitClause updateLimitClause;
    private OptionalHintClause optionalHintClause;
    private OracleSpecificClass OracleSpecificInstance;
    private CommentClass commentObject;
    private String isolationLevel;
    private String withString;
    private ArrayList lockTableList;
    private int timesTenFirst;
    private ArrayList messageArray;
    private Hashtable withTableColumnandIndex;
    private boolean selectQueryForUpdateStmt;
    
    public int getTimesTenFirst() {
        return this.timesTenFirst;
    }
    
    public void setTimesTenFirst(final int i) {
        this.timesTenFirst = i;
    }
    
    @Override
    public void setCommentClass(final CommentClass commentObject) {
        this.commentObject = commentObject;
    }
    
    @Override
    public CommentClass getCommentClass() {
        return this.commentObject;
    }
    
    public UpdateQueryStatement(final UpdateQueryStatement q) {
        this.objectContext = null;
        this.isolationLevel = null;
        this.withString = null;
        this.lockTableList = new ArrayList();
        this.timesTenFirst = -1;
        this.messageArray = new ArrayList();
        this.withTableColumnandIndex = null;
        this.selectQueryForUpdateStmt = false;
        this.updateClause = q.updateClause;
        this.tableExp = q.tableExp;
        this.hintClause = q.hintClause;
        this.setClause = q.setClause;
        this.fromClause = q.fromClause;
        this.whereExpression = q.whereExpression;
        this.returningClause = q.returningClause;
        this.orderByStatement = q.orderByStatement;
        this.updateLimitClause = q.updateLimitClause;
        this.optionalHintClause = q.optionalHintClause;
        this.OracleSpecificInstance = q.OracleSpecificInstance;
    }
    
    public UpdateQueryStatement() {
        this.objectContext = null;
        this.isolationLevel = null;
        this.withString = null;
        this.lockTableList = new ArrayList();
        this.timesTenFirst = -1;
        this.messageArray = new ArrayList();
        this.withTableColumnandIndex = null;
        this.selectQueryForUpdateStmt = false;
        this.updateClause = null;
        this.fromClause = null;
        this.setClause = null;
        this.tableExp = null;
        this.updateLimitClause = null;
        this.whereExpression = null;
        this.hintClause = null;
    }
    
    public void setUpdateClause(final UpdateClause updateclause) {
        this.updateClause = updateclause;
    }
    
    public void setHintClause(final HintClause hc) {
        this.hintClause = hc;
    }
    
    public void setWhereCurrentClause(final WhereCurrentClause wcc) {
        this.whereCurrentClause = wcc;
    }
    
    public String getWithString() {
        return this.withString;
    }
    
    public OrderByStatement getOrderByStatement() {
        return this.orderByStatement;
    }
    
    public OptionalHintClause getOptionalHintClause() {
        return this.optionalHintClause;
    }
    
    public HintClause getHintClause() {
        return this.hintClause;
    }
    
    public void setWhereClause(final WhereExpression we) {
        this.whereExpression = we;
    }
    
    public WhereExpression getWhereExpression() {
        return this.whereExpression;
    }
    
    public void setSetClause(final SetClause setclause) {
        this.setClause = setclause;
    }
    
    public void setTableExpression(final TableExpression tableexpression) {
        this.tableExp = tableexpression;
    }
    
    public TableExpression getTableExpression() {
        return this.tableExp;
    }
    
    public void setOrderByStatement(final OrderByStatement obs) {
        this.orderByStatement = obs;
    }
    
    public void setReturningClause(final ReturningClause rc) {
        this.returningClause = rc;
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
    
    public ReturningClause getReturningClause() {
        return this.returningClause;
    }
    
    public void setUpdateLimitClause(final UpdateLimitClause limitclause) {
        this.updateLimitClause = limitclause;
    }
    
    public void setOracleSpecificInstance(final OracleSpecificClass oraclespecificclass) {
        this.OracleSpecificInstance = oraclespecificclass;
    }
    
    public void setFromClause(final FromClause fc) {
        this.fromClause = fc;
    }
    
    public void setOptionalHintClause(final OptionalHintClause ohc) {
        this.optionalHintClause = ohc;
    }
    
    public UpdateLimitClause getUpdateLimitClause() {
        return this.updateLimitClause;
    }
    
    public FromClause getFromClause() {
        return this.fromClause;
    }
    
    public WhereCurrentClause getWhereCurrentClause() {
        return this.whereCurrentClause;
    }
    
    public SetClause getSetClause() {
        return this.setClause;
    }
    
    public UpdateClause getUpdateClause() {
        return this.updateClause;
    }
    
    public boolean columnsHaveAggregateFunction() {
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuffer stringbuffer = new StringBuffer();
        if (!SwisSQLUtils.swissqlMessageList.isEmpty()) {
            stringbuffer.append("/* SwisSQL Messages :\n");
            for (int i = 0; i < SwisSQLUtils.swissqlMessageList.size(); ++i) {
                stringbuffer.append(SwisSQLUtils.swissqlMessageList.get(i).toString() + "\n");
            }
            stringbuffer.append("*/\n");
            SwisSQLUtils.swissqlMessageList.clear();
        }
        if (this.commentObject != null) {
            stringbuffer.append(this.commentObject.toString() + "\n");
        }
        for (int k = 0; k < this.lockTableList.size(); ++k) {
            stringbuffer.append(this.lockTableList.get(k).toString() + ";\n");
        }
        if (this.singleQueryIntoMultipleQueriesForPLSQL() != null) {
            stringbuffer.append(this.singleQueryIntoMultipleQueriesForPLSQL());
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
        }
        if (this.updateClause != null) {
            stringbuffer.append(this.updateClause.toString() + " ");
        }
        if (this.getTimesTenFirst() != -1) {
            stringbuffer.append("FIRST " + (this.getTimesTenFirst() - 1) + " ");
        }
        if (this.tableExp != null) {
            if (this.objectContext != null) {
                this.tableExp.setObjectContext(this.objectContext);
            }
            stringbuffer.append(this.tableExp.toString() + " \n");
        }
        if (this.hintClause != null && this.hintClause.toString() != null) {
            stringbuffer.append(this.hintClause.toString() + " \n");
        }
        if (this.setClause != null) {
            if (this.objectContext != null) {
                this.setClause.setObjectContext(this.objectContext);
            }
            stringbuffer.append(this.setClause.toString() + " \n");
        }
        if (this.fromClause != null) {
            this.fromClause.setObjectContext(this.objectContext);
            stringbuffer.append(this.fromClause.toString() + " \n");
        }
        if (this.whereCurrentClause != null) {
            stringbuffer.append(this.whereCurrentClause.toString() + " \n");
        }
        if (this.whereExpression != null) {
            if (!this.whereExpression.toString().trim().equals("")) {
                if (this.whereExpression.getConcatenation() != null) {
                    stringbuffer.append("+ ");
                }
                stringbuffer.append("WHERE ");
                this.whereExpression.setObjectContext(this.objectContext);
                if (this.whereExpression.toString().trim().indexOf("AND") == 0) {
                    stringbuffer.append(StringFunctions.replaceFirst(" ", "AND", this.whereExpression.toString()));
                }
                else {
                    stringbuffer.append(" " + this.whereExpression.toString());
                }
            }
            stringbuffer.append(" \n");
        }
        if (this.orderByStatement != null) {
            stringbuffer.append(this.orderByStatement.toString() + " \n");
        }
        if (this.updateLimitClause != null) {
            stringbuffer.append(this.updateLimitClause.toString() + " \n");
        }
        if (this.optionalHintClause != null) {
            stringbuffer.append(this.optionalHintClause.toString() + " \n");
        }
        if (this.returningClause != null) {
            stringbuffer.append(this.returningClause.toString());
        }
        if (this.withString != null) {
            stringbuffer.append(this.withString + " ");
        }
        if (this.isolationLevel != null) {
            stringbuffer.append(this.isolationLevel);
        }
        return stringbuffer.toString();
    }
    
    @Override
    public String removeIndent(String s_ri) {
        s_ri = s_ri.replace('\n', ' ');
        s_ri = s_ri.replace('\t', ' ');
        return s_ri;
    }
    
    @Override
    public String toInformixString() throws ConvertException {
        this.withString = null;
        this.isolationLevel = null;
        this.updateClause.toInformix();
        if (this.setClause != null) {
            if (this.tableExp.getTableClauseList() != null) {
                final ArrayList tableList = this.tableExp.getTableClauseList();
                for (int i = 0; i < tableList.size(); ++i) {
                    final Hashtable withTableColumnandIndex = new Hashtable();
                    if (tableList.get(i).getAlias() != null) {
                        final TableColumn tc = new TableColumn();
                        final TableObject setTableObject = tableList.get(i).getTableObject();
                        final String setOriginalTableNameForAlias = setTableObject.getTableName();
                        tc.setTableName(setOriginalTableNameForAlias);
                        tc.setOwnerName(setTableObject.getUser());
                        final String keyString = tableList.get(i).getAlias() + ".";
                        withTableColumnandIndex.put(keyString, tc);
                        this.setClause.setOriginalTableName(withTableColumnandIndex);
                    }
                }
            }
            this.setClause.toInformix();
        }
        if (this.hintClause != null) {
            this.hintClause.toInformix();
        }
        if (this.hintClause != null) {
            this.hintClause = null;
        }
        if (this.whereExpression != null) {
            this.whereExpression = this.whereExpression.toInformixSelect(null, null);
        }
        if (this.fromClause != null) {
            this.fromClause.convertToSubQuery(this, 6, this.getFromClause());
        }
        this.convertAliasNameToTableName();
        this.tableExp.toInformix();
        this.optionalHintClause = null;
        this.returningClause = null;
        this.updateLimitClause = null;
        return this.toString();
    }
    
    @Override
    public String toOracleString() throws ConvertException {
        final StringBuffer stringbuffer = new StringBuffer();
        boolean convertToSubQueryNotReqd = false;
        this.updateClause.toOracle();
        this.tableExp.toOracle();
        if (this.setClause != null) {
            if (this.setClause.getExpression() != null) {
                final ArrayList expList = this.setClause.getExpression();
                convertToSubQueryNotReqd = this.processSetClauseIntoASingleSubQuery(expList);
            }
            this.setClause.setFromUpdateQuerySatetemnt(this);
            this.setClause.toOracle();
        }
        if (this.hintClause != null) {
            this.hintClause.toOracle();
        }
        if (this.whereExpression != null) {
            this.setUQSForWhereColumn(this.whereExpression);
            this.whereExpression = this.whereExpression.toOracleSelect(null, null);
        }
        if (this.fromClause != null) {
            this.fromClause.setBaseFromClauseFound(true);
            if (convertToSubQueryNotReqd) {
                this.setFromClause(null);
            }
            else {
                final SetClause sc = this.getSetClause();
                final ArrayList list = sc.getExpression();
                final Vector selectItemVector = new Vector();
                final Vector colVector = new Vector();
                final ArrayList setClauseList = new ArrayList();
                final SetClause newSetClause = new SetClause();
                if (list != null && list.toString().toLowerCase().indexOf("select") == -1) {
                    colVector.add(list.get(0));
                    for (int i = 1; i < list.size(); ++i) {
                        if (list.get(i).toString().trim().equals("=")) {
                            selectItemVector.add(list.get(++i));
                            if (i + 2 < list.size()) {
                                selectItemVector.add(",");
                            }
                        }
                        if (++i < list.size() && list.get(i).toString().equals(",")) {
                            i += 2;
                            colVector.add(list.get(i));
                        }
                    }
                    setClauseList.add("(");
                    for (int i = 0; i < colVector.size(); ++i) {
                        if (colVector.get(i) instanceof TableColumn) {
                            colVector.get(i).setTableName(null);
                        }
                        setClauseList.add(colVector.get(i));
                        if (i + 1 < colVector.size()) {
                            setClauseList.add(",");
                        }
                    }
                    setClauseList.add(")");
                    setClauseList.add("=");
                    setClauseList.add("(");
                    final SelectQueryStatement fromSQS = new SelectQueryStatement();
                    final SelectStatement fromSS = new SelectStatement();
                    fromSS.setSelectClause("SELECT");
                    fromSS.setSelectItemList(selectItemVector);
                    fromSQS.setSelectStatement(fromSS);
                    fromSQS.setWhereExpression(this.getWhereExpression());
                    fromSQS.setFromClause(this.getFromClause());
                    final SelectQueryStatement convertedSQS = fromSQS.toOracleSelect();
                    if (this.objectContext != null) {
                        this.setObjectContextForSQS(convertedSQS);
                    }
                    setClauseList.add(convertedSQS);
                    setClauseList.add(")");
                    newSetClause.setSetExpressionList(setClauseList);
                    newSetClause.setSet("SET");
                    this.setSetClause(newSetClause);
                    this.setFromClause(null);
                    this.setWhereClause(null);
                }
                final ArrayList expList2 = this.setClause.getExpression();
                if (this.selectQueryForUpdateStmt) {
                    expList2.add(0, "(");
                    expList2.add(expList2.size() - 1, ")");
                    expList2.add(expList2.size() - 1, "=");
                    expList2.add(expList2.size() - 1, "(");
                    expList2.add(")");
                    for (int j = 0; j < expList2.size(); ++j) {
                        final Object obj = expList2.get(j);
                        if (obj instanceof SelectQueryStatement) {
                            if (this.objectContext != null) {
                                this.setObjectContextForSQS((SelectQueryStatement)obj);
                            }
                        }
                        else if (obj instanceof TableColumn) {
                            ((TableColumn)obj).setTableName(null);
                        }
                    }
                    this.setClause.setExpression(expList2);
                    this.setFromClause(null);
                    this.setWhereClause(null);
                }
            }
        }
        this.optionalHintClause = null;
        if (this.updateLimitClause != null) {
            this.updateLimitClause.toOracleRowNum(this);
        }
        this.updateLimitClause = null;
        if (this.withString != null && this.isolationLevel != null) {
            String lockStatement = "LOCK TABLE ";
            final ArrayList tableList = this.getTableExpression().getTableClauseList();
            for (int k = 0; k < tableList.size(); ++k) {
                final Object o = tableList.get(k);
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
    
    private void setObjectContextForSQS(final SelectQueryStatement convertedSQS) {
        convertedSQS.setObjectContext(this.objectContext);
        if (convertedSQS.getFromClause() != null) {
            convertedSQS.getFromClause().setObjectContext(this.objectContext);
            final Vector fromItems = convertedSQS.getFromClause().getFromItemList();
            if (fromItems != null) {
                for (int i = 0; i < fromItems.size(); ++i) {
                    final Object obj = fromItems.get(i);
                    if (obj instanceof FromTable) {
                        fromItems.get(i).setObjectContext(this.objectContext);
                    }
                    else if (obj instanceof FromClause) {
                        fromItems.get(i).setObjectContext(this.objectContext);
                    }
                    else if (obj instanceof SelectQueryStatement) {
                        fromItems.get(i).setObjectContext(this.objectContext);
                    }
                }
            }
        }
        if (convertedSQS.getWhereExpression() != null) {
            convertedSQS.getWhereExpression().setObjectContext(this.objectContext);
            final Vector wi = convertedSQS.getWhereExpression().getWhereItems();
            if (wi != null) {
                for (int i = 0; i < wi.size(); ++i) {
                    final Object obj = wi.get(i);
                    if (obj instanceof WhereItem) {
                        ((WhereItem)obj).setObjectContext(this.objectContext);
                        WhereColumn wc = ((WhereItem)obj).getLeftWhereExp();
                        if (wc != null) {
                            wc.setObjectContext(this.objectContext);
                        }
                        wc = ((WhereItem)obj).getRightWhereExp();
                        if (wc != null) {
                            wc.setObjectContext(this.objectContext);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public String toMSSQLServerString() throws ConvertException {
        this.withString = null;
        this.isolationLevel = null;
        this.updateClause.toSQLServer();
        boolean whereExprConverted = false;
        if (this.setClause != null) {
            if (this.tableExp.getTableClauseList() != null) {
                final ArrayList tableList = this.tableExp.getTableClauseList();
                for (int i = 0; i < tableList.size(); ++i) {
                    final Hashtable withTableColumnandIndex = new Hashtable();
                    tableList.get(i).setToMSSQLServer(true);
                    if (tableList.get(i).getAlias() != null) {
                        final TableColumn tc = new TableColumn();
                        final TableObject setTableObject = tableList.get(i).getTableObject();
                        final String setOriginalTableNameForAlias = setTableObject.getTableName();
                        tc.setTableName(setOriginalTableNameForAlias);
                        tc.setOwnerName(setTableObject.getUser());
                        String keyString = tableList.get(i).getAlias() + ".";
                        withTableColumnandIndex.put(keyString, tc);
                        if (!tableList.get(i).getAlias().trim().equals("")) {
                            if (keyString.startsWith(".")) {
                                keyString = keyString.substring(1, keyString.length());
                            }
                            if (keyString.endsWith(".")) {
                                keyString = keyString.substring(0, keyString.length() - 1);
                            }
                            final FromClause fc1 = new FromClause();
                            final FromTable ft1 = new FromTable();
                            ft1.setAliasName(keyString);
                            ft1.setTableName(setOriginalTableNameForAlias);
                            fc1.addFromItem(ft1);
                            fc1.setFromClause("FROM");
                            this.setFromClause(fc1);
                        }
                    }
                }
            }
            this.setClause.toMSSQLServer();
            if (this.setClause.getExpression() != null) {
                boolean subqueryHasAggregateFunction = false;
                final ArrayList expressionList = this.setClause.getExpression();
                if (expressionList != null) {
                    for (int ii = 0; ii < expressionList.size(); ++ii) {
                        if (expressionList.get(ii) instanceof SelectQueryStatement) {
                            final SelectQueryStatement subQuery = expressionList.get(ii);
                            final SelectStatement subSelectStatement = subQuery.getSelectStatement();
                            final FromClause sqlFromClause = subQuery.getFromClause();
                            final Vector fromList = sqlFromClause.getFromItemList();
                            final Vector subSelectCol = subSelectStatement.getSelectItemList();
                            for (int j = 0; j < subSelectCol.size(); ++j) {
                                if (subSelectCol.get(j) instanceof SelectColumn && (subSelectCol.get(j).isAggregateFunction() || this.selectColumnHasAggrFunction(subSelectCol.get(j).getColumnExpression(), false))) {
                                    subqueryHasAggregateFunction = true;
                                    if (fromList != null) {
                                        for (int k = 0; k < fromList.size(); ++k) {
                                            if (fromList.get(k) instanceof FromTable) {
                                                final FromTable sqlFromTable = fromList.get(k);
                                                if (sqlFromTable.getAliasName() == null && this.tableExp.getTableClauseList() != null && this.tableExp.getTableClauseList().size() > 0 && this.tableExp.getTableClauseList().get(0) instanceof TableClause) {
                                                    final TableClause sqlTableClause = this.tableExp.getTableClauseList().get(0);
                                                    final String tableName = sqlTableClause.getTableObject().toString();
                                                    final String aliasName = sqlTableClause.getAlias();
                                                    if (sqlFromTable.getTableName().toString().trim().equalsIgnoreCase(tableName.trim())) {
                                                        sqlFromTable.setAliasName(aliasName);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (this.setClause.getSetExpressionList() != null) {
                boolean subqueryHasAggregateFunction = false;
                final SetExpression se = this.setClause.getSetExpressionList().get(0);
                if (se.getSubQuery() != null) {
                    final SelectQueryStatement subQuery2 = se.getSubQuery();
                    final SelectStatement subSelectStatement2 = subQuery2.getSelectStatement();
                    final FromClause sqlFromClause2 = subQuery2.getFromClause();
                    final Vector fromList2 = sqlFromClause2.getFromItemList();
                    final Vector subSelectCol2 = subSelectStatement2.getSelectItemList();
                    if (subSelectCol2 != null) {
                        for (int l = 0; l < subSelectCol2.size(); ++l) {
                            if (subSelectCol2.get(l) instanceof SelectColumn && subSelectCol2.get(l).isAggregateFunction()) {
                                subqueryHasAggregateFunction = true;
                                if (fromList2 != null) {
                                    for (int m = 0; m < fromList2.size(); ++m) {
                                        if (fromList2.get(m) instanceof FromTable) {
                                            final FromTable sqlFromTable2 = fromList2.get(m);
                                            if (sqlFromTable2.getAliasName() == null && this.tableExp.getTableClauseList() != null && this.tableExp.getTableClauseList().size() > 0 && this.tableExp.getTableClauseList().get(0) instanceof TableClause) {
                                                final TableClause sqlTableClause2 = this.tableExp.getTableClauseList().get(0);
                                                final String tableName2 = sqlTableClause2.getTableObject().toString();
                                                final String aliasName2 = sqlTableClause2.getAlias();
                                                if (sqlFromTable2.getTableName().toString().trim().equalsIgnoreCase(tableName2.trim())) {
                                                    sqlFromTable2.setAliasName(aliasName2);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!subqueryHasAggregateFunction) {
                        final Vector fromItemList = subQuery2.getFromClause().getFromItemList();
                        final FromTable ft2 = new FromTable();
                        final ArrayList tableClauseList = this.tableExp.getTableClauseList();
                        final TableClause tableClause = tableClauseList.get(0);
                        String tableToBeAdded = null;
                        String aliasToBeAdded = null;
                        tableToBeAdded = tableClause.getTableObject().toString();
                        aliasToBeAdded = tableClause.getAlias();
                        boolean addTableName = true;
                        int i2 = 0;
                        while (i2 < fromItemList.size()) {
                            final String existingTableName = fromItemList.get(i2).getTableName().toString();
                            final String existingAliasName = fromItemList.get(i2).getAliasName();
                            if (existingTableName.equalsIgnoreCase(tableToBeAdded)) {
                                addTableName = false;
                                if (existingAliasName == null) {
                                    fromItemList.get(i2).setAliasName(aliasToBeAdded);
                                    break;
                                }
                                break;
                            }
                            else {
                                if (existingAliasName != null && existingAliasName.equalsIgnoreCase(aliasToBeAdded)) {
                                    addTableName = false;
                                    break;
                                }
                                ++i2;
                            }
                        }
                        if (addTableName) {
                            ft2.setTableName(tableToBeAdded);
                            ft2.setAliasName(aliasToBeAdded);
                            fromItemList.insertElementAt(ft2, 0);
                        }
                        if (this.getFromClause() == null) {
                            this.setFromClause(subQuery2.getFromClause());
                        }
                        else {
                            final Vector updateFromItemList = this.fromClause.getFromItemList();
                            if (subQuery2.getFromClause() != null) {
                                final Vector subQueryFromList = subQuery2.getFromClause().getFromItemList();
                                for (int i3 = 0; i3 < subQueryFromList.size(); ++i3) {
                                    boolean alreadyExistsInFromClause = false;
                                    for (int index = 0; index < updateFromItemList.size(); ++index) {
                                        final String multipleQueryString = SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
                                        if (updateFromItemList.get(index).toString().equalsIgnoreCase(subQueryFromList.get(i3).toString())) {
                                            alreadyExistsInFromClause = true;
                                            break;
                                        }
                                        SelectQueryStatement.singleQueryConvertedToMultipleQueryList = multipleQueryString;
                                    }
                                    if (!alreadyExistsInFromClause) {
                                        updateFromItemList.add(subQueryFromList.get(i3));
                                    }
                                }
                            }
                        }
                        if (this.getWhereExpression() == null) {
                            this.setWhereClause(subQuery2.getWhereExpression());
                            whereExprConverted = true;
                        }
                        else {
                            final WhereExpression wExp = subQuery2.getWhereExpression();
                            if (wExp != null) {
                                final Vector where_items = wExp.getWhereItems();
                                for (int i3 = 0; i3 < where_items.size(); ++i3) {
                                    if (where_items.get(i3) instanceof WhereItem) {
                                        final WhereItem wi = where_items.get(i3);
                                        final WhereColumn lwc = wi.getLeftWhereExp();
                                        if (lwc != null) {
                                            final Vector where_col_expression = lwc.getColumnExpression();
                                            if (where_col_expression != null && where_col_expression.get(0) instanceof TableColumn) {
                                                final TableColumn tc2 = where_col_expression.get(0);
                                                final FromTable ftable = MetadataInfoUtil.getTableOfColumn(subQuery2, tc2);
                                                if (ftable != null) {
                                                    tc2.setTableName(ftable.toString());
                                                }
                                            }
                                        }
                                        final WhereColumn rwc = wi.getRightWhereExp();
                                        if (rwc != null) {
                                            final Vector where_col_expression2 = rwc.getColumnExpression();
                                            if (where_col_expression2 != null && where_col_expression2.get(0) instanceof TableColumn) {
                                                final TableColumn tc3 = where_col_expression2.get(0);
                                                final FromTable ftable2 = MetadataInfoUtil.getTableOfColumn(subQuery2, tc3);
                                                if (ftable2 != null) {
                                                    tc3.setTableName(ftable2.toString());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            wExp.addWhereExpression(this.getWhereExpression().toMSSQLServerSelect(null, null));
                            wExp.addOperator("AND");
                            this.setWhereClause(wExp);
                            whereExprConverted = true;
                        }
                        this.setOrderByStatement(subQuery2.getOrderByStatement());
                    }
                }
            }
        }
        if (this.hintClause != null) {
            this.hintClause.toSQLServer();
        }
        if (this.fromClause != null) {
            this.fromClause = this.fromClause.toMSSQLServerSelect(null, null);
        }
        this.convertAliasNameToTableName();
        this.tableExp.toMSSQLServer();
        if (this.whereExpression != null && !whereExprConverted) {
            if (!SwisSQLAPI.MSSQLSERVER_THETA) {
                final SelectQueryStatement sqs1 = new SelectQueryStatement();
                sqs1.setFromClause(this.fromClause);
                sqs1.setWhereExpression(this.whereExpression);
                this.whereExpression = this.whereExpression.toMSSQLServerSelect(sqs1, sqs1);
                this.fromClause = sqs1.getFromClause();
            }
            else {
                this.whereExpression = this.whereExpression.toMSSQLServerSelect(null, null);
            }
        }
        this.returningClause = null;
        this.updateLimitClause = null;
        return this.toString();
    }
    
    @Override
    public String toSybaseString() throws ConvertException {
        this.withString = null;
        this.isolationLevel = null;
        this.updateClause.toSQLServer();
        boolean whereExprConverted = false;
        if (this.setClause != null) {
            if (this.tableExp.getTableClauseList() != null) {
                final ArrayList tableList = this.tableExp.getTableClauseList();
                for (int i = 0; i < tableList.size(); ++i) {
                    final Hashtable withTableColumnandIndex = new Hashtable();
                    if (tableList.get(i).getAlias() != null) {
                        final TableColumn tc = new TableColumn();
                        tc.setObjectContext(this.objectContext);
                        final TableObject setTableObject = tableList.get(i).getTableObject();
                        final String setOriginalTableNameForAlias = setTableObject.getTableName();
                        tc.setTableName(setOriginalTableNameForAlias);
                        tc.setOwnerName(setTableObject.getUser());
                        String keyString = tableList.get(i).getAlias() + ".";
                        withTableColumnandIndex.put(keyString, tc);
                        if (!tableList.get(i).getAlias().trim().equals("")) {
                            if (keyString.startsWith(".")) {
                                keyString = keyString.substring(1, keyString.length());
                            }
                            if (keyString.endsWith(".")) {
                                keyString = keyString.substring(0, keyString.length() - 1);
                            }
                            final FromClause fc1 = new FromClause();
                            final FromTable ft1 = new FromTable();
                            ft1.setAliasName(keyString);
                            ft1.setTableName(setOriginalTableNameForAlias);
                            ft1.setObjectContext(this.objectContext);
                            fc1.addFromItem(ft1);
                            fc1.setFromClause("FROM");
                            fc1.setObjectContext(this.objectContext);
                            this.setFromClause(fc1);
                        }
                    }
                }
            }
            this.setClause.setObjectContext(this.objectContext);
            this.setClause.toSybase();
            if (this.setClause.getExpression() != null) {
                boolean subqueryHasAggregateFunction = false;
                final ArrayList expressionList = this.setClause.getExpression();
                if (expressionList != null) {
                    for (int ii = 0; ii < expressionList.size(); ++ii) {
                        if (expressionList.get(ii) instanceof SelectQueryStatement) {
                            final SelectQueryStatement subQuery = expressionList.get(ii);
                            final SelectStatement subSelectStatement = subQuery.getSelectStatement();
                            final FromClause sqlFromClause = subQuery.getFromClause();
                            final Vector fromList = sqlFromClause.getFromItemList();
                            final Vector subSelectCol = subSelectStatement.getSelectItemList();
                            for (int j = 0; j < subSelectCol.size(); ++j) {
                                if (subSelectCol.get(j) instanceof SelectColumn && subSelectCol.get(j).isAggregateFunction()) {
                                    subqueryHasAggregateFunction = true;
                                    if (fromList != null) {
                                        for (int k = 0; k < fromList.size(); ++k) {
                                            if (fromList.get(k) instanceof FromTable) {
                                                final FromTable sqlFromTable = fromList.get(k);
                                                if (sqlFromTable.getAliasName() == null && this.tableExp.getTableClauseList() != null && this.tableExp.getTableClauseList().size() > 0 && this.tableExp.getTableClauseList().get(0) instanceof TableClause) {
                                                    final TableClause sqlTableClause = this.tableExp.getTableClauseList().get(0);
                                                    final String tableName = sqlTableClause.getTableObject().toString();
                                                    final String aliasName = sqlTableClause.getAlias();
                                                    if (sqlFromTable.getTableName().toString().trim().equalsIgnoreCase(tableName.trim())) {
                                                        sqlFromTable.setAliasName(aliasName);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (this.setClause.getSetExpressionList() != null) {
                boolean subqueryHasAggregateFunction = false;
                final SetExpression se = this.setClause.getSetExpressionList().get(0);
                if (se.getSubQuery() != null) {
                    final SelectQueryStatement subQuery2 = se.getSubQuery();
                    final SelectStatement subSelectStatement2 = subQuery2.getSelectStatement();
                    final FromClause sqlFromClause2 = subQuery2.getFromClause();
                    final Vector fromList2 = sqlFromClause2.getFromItemList();
                    final Vector subSelectCol2 = subSelectStatement2.getSelectItemList();
                    if (subSelectCol2 != null) {
                        for (int l = 0; l < subSelectCol2.size(); ++l) {
                            if (subSelectCol2.get(l) instanceof SelectColumn && subSelectCol2.get(l).isAggregateFunction()) {
                                subqueryHasAggregateFunction = true;
                                if (fromList2 != null) {
                                    for (int m = 0; m < fromList2.size(); ++m) {
                                        if (fromList2.get(m) instanceof FromTable) {
                                            final FromTable sqlFromTable2 = fromList2.get(m);
                                            if (sqlFromTable2.getAliasName() == null && this.tableExp.getTableClauseList() != null && this.tableExp.getTableClauseList().size() > 0 && this.tableExp.getTableClauseList().get(0) instanceof TableClause) {
                                                final TableClause sqlTableClause2 = this.tableExp.getTableClauseList().get(0);
                                                final String tableName2 = sqlTableClause2.getTableObject().toString();
                                                final String aliasName2 = sqlTableClause2.getAlias();
                                                if (sqlFromTable2.getTableName().toString().trim().equalsIgnoreCase(tableName2.trim())) {
                                                    sqlFromTable2.setAliasName(aliasName2);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!subqueryHasAggregateFunction) {
                        final Vector fromItemList = subQuery2.getFromClause().getFromItemList();
                        final FromTable ft2 = new FromTable();
                        ft2.setObjectContext(this.objectContext);
                        final ArrayList tableClauseList = this.tableExp.getTableClauseList();
                        final TableClause tableClause = tableClauseList.get(0);
                        String tableToBeAdded = null;
                        String aliasToBeAdded = null;
                        tableToBeAdded = tableClause.getTableObject().toString();
                        aliasToBeAdded = tableClause.getAlias();
                        boolean addTableName = true;
                        int i2 = 0;
                        while (i2 < fromItemList.size()) {
                            final String existingTableName = fromItemList.get(i2).getTableName().toString();
                            final String existingAliasName = fromItemList.get(i2).getAliasName();
                            if (existingTableName.equalsIgnoreCase(tableToBeAdded)) {
                                addTableName = false;
                                if (existingAliasName == null) {
                                    fromItemList.get(i2).setAliasName(aliasToBeAdded);
                                    break;
                                }
                                break;
                            }
                            else {
                                if (existingAliasName != null && existingAliasName.equalsIgnoreCase(aliasToBeAdded)) {
                                    addTableName = false;
                                    break;
                                }
                                ++i2;
                            }
                        }
                        if (addTableName) {
                            ft2.setTableName(tableToBeAdded);
                            ft2.setAliasName(aliasToBeAdded);
                            fromItemList.insertElementAt(ft2, 0);
                        }
                        this.setFromClause(subQuery2.getFromClause());
                        if (this.getWhereExpression() == null) {
                            if (subQuery2.getWhereExpression() != null) {
                                subQuery2.getWhereExpression().setObjectContext(this.objectContext);
                            }
                            this.setWhereClause(subQuery2.getWhereExpression());
                            whereExprConverted = true;
                        }
                        else {
                            final WhereExpression wExp = subQuery2.getWhereExpression();
                            wExp.addWhereExpression(this.getWhereExpression().toSybaseSelect(null, null));
                            wExp.addOperator("AND");
                            wExp.setObjectContext(this.objectContext);
                            this.setWhereClause(wExp);
                            whereExprConverted = true;
                        }
                        this.setOrderByStatement(subQuery2.getOrderByStatement());
                    }
                }
            }
        }
        if (this.hintClause != null) {
            this.hintClause.toSQLServer();
        }
        if (this.fromClause != null) {
            this.fromClause = this.fromClause.toSybaseSelect(null, null);
        }
        this.convertAliasNameToTableName();
        this.tableExp.toSybase();
        if (this.whereExpression != null && !whereExprConverted) {
            this.whereExpression.setObjectContext(this.objectContext);
            this.whereExpression = this.whereExpression.toSybaseSelect(null, null);
        }
        this.returningClause = null;
        this.updateLimitClause = null;
        return this.toString();
    }
    
    @Override
    public String toDB2String() throws ConvertException {
        this.updateClause.toDB2();
        this.tableExp.toDB2();
        if (this.hintClause != null) {
            this.hintClause.toDB2();
        }
        if (this.setClause != null) {
            this.setClause.setFromUpdateQuerySatetemnt(this);
            this.setClause.toDB2();
        }
        if (this.hintClause != null) {
            this.hintClause = null;
        }
        if (this.whereExpression != null) {
            this.whereExpression = this.whereExpression.toDB2Select(null, null);
        }
        if (this.fromClause != null) {
            this.fromClause.convertToSubQuery(this, 3, this.getFromClause());
        }
        this.optionalHintClause = null;
        this.returningClause = null;
        this.updateLimitClause = null;
        return this.toString();
    }
    
    @Override
    public String toMySQLString() throws ConvertException {
        this.withString = null;
        this.isolationLevel = null;
        this.updateClause.toMySQL();
        if (this.hintClause != null) {
            this.hintClause.toMySQL();
        }
        if (this.setClause != null) {
            this.setClause.setFromUpdateQuerySatetemnt(this);
            if (this.tableExp.getTableClauseList() != null) {
                final ArrayList tableList = this.tableExp.getTableClauseList();
                for (int i = 0; i < tableList.size(); ++i) {
                    final Hashtable withTableColumnandIndex = new Hashtable();
                    if (tableList.get(i) instanceof TableClause && tableList.get(i).getAlias() != null) {
                        final TableColumn tc = new TableColumn();
                        final TableObject setTableObject = tableList.get(i).getTableObject();
                        final String setOriginalTableNameForAlias = setTableObject.getTableName();
                        tc.setTableName(setOriginalTableNameForAlias);
                        tc.setOwnerName(setTableObject.getUser());
                        final String keyString = tableList.get(i).getAlias() + ".";
                        withTableColumnandIndex.put(keyString, tc);
                        this.setClause.setOriginalTableName(withTableColumnandIndex);
                    }
                }
            }
            this.setClause.toMySQL();
        }
        if (this.fromClause != null) {
            throw new ConvertException("from clause has to be changed into subquery");
        }
        TableExpression.isUpdateStatement = true;
        this.tableExp.toMySQL();
        TableExpression.isUpdateStatement = false;
        this.convertRownumToUpdateLimitClause();
        if (this.whereExpression != null) {
            this.whereExpression = this.whereExpression.toMySQLSelect(null, null);
        }
        this.optionalHintClause = null;
        this.returningClause = null;
        return this.toString();
    }
    
    @Override
    public String toPostgreSQLString() throws ConvertException {
        this.withString = null;
        this.isolationLevel = null;
        this.updateClause.toPostgreSQL();
        if (this.hintClause != null) {
            this.hintClause.toPostgreSQL();
        }
        if (this.setClause != null) {
            if (this.tableExp.getTableClauseList() != null) {
                final ArrayList tableList = this.tableExp.getTableClauseList();
                this.withTableColumnandIndex = new Hashtable();
                for (int i = 0; i < tableList.size(); ++i) {
                    if (tableList.get(i).getAlias() != null) {
                        final TableColumn tc = new TableColumn();
                        final TableObject setTableObject = tableList.get(i).getTableObject();
                        final String setOriginalTableNameForAlias = setTableObject.getTableName();
                        tc.setTableName(setOriginalTableNameForAlias);
                        tc.setOwnerName(setTableObject.getUser());
                        final String keyString = tableList.get(i).getAlias() + ".";
                        this.withTableColumnandIndex.put(keyString, tc);
                        this.setClause.setOriginalTableName(this.withTableColumnandIndex);
                    }
                }
            }
            this.setClause.toPostgreSQL();
        }
        if (this.fromClause != null) {
            this.fromClause = this.fromClause.toPostgreSQLSelect(null, null);
        }
        this.convertAliasNameToTableName();
        this.tableExp.toPostgreSQL();
        this.convertRownumToUpdateLimitClause();
        if (this.whereExpression != null) {
            this.whereExpression = this.whereExpression.toPostgreSQLSelect(null, null);
        }
        this.optionalHintClause = null;
        this.returningClause = null;
        return this.toString();
    }
    
    private void convertRownumToUpdateLimitClause() throws ConvertException {
        RownumClause rownumClause = null;
        if (this.whereExpression != null) {
            rownumClause = this.whereExpression.getRownumClause();
        }
        if (rownumClause != null) {
            final UpdateLimitClause ulimitClause = new UpdateLimitClause();
            String rownumValue = "0";
            if (rownumClause.getRownumValue() instanceof SelectQueryStatement) {
                throw new ConvertException("Conversion failure.. Subquery can't be converted");
            }
            if (rownumClause.getRownumValue() instanceof SelectColumn) {
                final SelectColumn sc = (SelectColumn)rownumClause.getRownumValue();
                final Vector colExp = sc.getColumnExpression();
                for (int i = 0; i < colExp.size(); ++i) {
                    if (colExp.elementAt(i) instanceof FunctionCalls) {
                        throw new ConvertException("Conversion failure.. Function calls can't be converted");
                    }
                    if (colExp.elementAt(i) instanceof TableColumn) {
                        throw new ConvertException("Conversion failure.. Identifier can't be converted");
                    }
                    if (!(colExp.elementAt(i) instanceof String) || colExp.size() != 1) {
                        throw new ConvertException("Conversion failure.. Expression can't be converted");
                    }
                    rownumValue = colExp.elementAt(i);
                }
            }
            ulimitClause.setLimit("LIMIT");
            if (rownumClause.getOperator().equals("<=")) {
                ulimitClause.setDimension(rownumValue);
            }
            else {
                ulimitClause.setDimension(Integer.parseInt(rownumValue) - 1 + "");
            }
            if (this.getUpdateLimitClause() != null) {
                throw new ConvertException();
            }
            this.setUpdateLimitClause(ulimitClause);
            this.whereExpression.setRownumClause(null);
        }
    }
    
    @Override
    public String toANSIString() throws ConvertException {
        this.withString = null;
        this.isolationLevel = null;
        this.updateClause.toANSISQL();
        if (this.setClause != null) {
            if (this.tableExp.getTableClauseList() != null) {
                final ArrayList tableList = this.tableExp.getTableClauseList();
                for (int i = 0; i < tableList.size(); ++i) {
                    final Hashtable withTableColumnandIndex = new Hashtable();
                    if (tableList.get(i).getAlias() != null) {
                        final TableColumn tc = new TableColumn();
                        final TableObject setTableObject = tableList.get(i).getTableObject();
                        final String setOriginalTableNameForAlias = setTableObject.getTableName();
                        tc.setTableName(setOriginalTableNameForAlias);
                        final String keyString = tableList.get(i).getAlias() + ".";
                        withTableColumnandIndex.put(keyString, tc);
                        this.setClause.setOriginalTableName(withTableColumnandIndex);
                    }
                }
            }
            this.setClause.toANSISQL();
        }
        if (this.whereExpression != null) {
            this.whereExpression = this.whereExpression.toANSISelect(null, null);
        }
        if (this.fromClause != null) {
            this.fromClause.convertToSubQuery(this, 8, this.getFromClause());
        }
        this.convertAliasNameToTableName();
        this.tableExp.toANSISQL();
        this.optionalHintClause = null;
        this.returningClause = null;
        return this.toString();
    }
    
    @Override
    public String toTeradataString() throws ConvertException {
        this.withString = null;
        this.isolationLevel = null;
        if (this.commentObject != null) {
            this.commentObject.setSQLDialect(12);
        }
        this.updateClause.toTeradata();
        if (this.setClause != null) {
            if (this.tableExp.getTableClauseList() != null) {
                final ArrayList tableList = this.tableExp.getTableClauseList();
                for (int i = 0; i < tableList.size(); ++i) {
                    final Hashtable withTableColumnandIndex = new Hashtable();
                    if (tableList.get(i).getAlias() != null) {
                        final TableColumn tc = new TableColumn();
                        final TableObject setTableObject = tableList.get(i).getTableObject();
                        final String setOriginalTableNameForAlias = setTableObject.getTableName();
                        tc.setTableName(setOriginalTableNameForAlias);
                        final String keyString = tableList.get(i).getAlias() + ".";
                        withTableColumnandIndex.put(keyString, tc);
                        this.setClause.setOriginalTableName(withTableColumnandIndex);
                    }
                }
            }
            this.setClause.toTeradata();
        }
        if (this.whereExpression != null) {
            this.whereExpression = this.whereExpression.toTeradataSelect(null, null);
        }
        if (this.fromClause != null) {
            this.fromClause.convertToSubQuery(this, 12, this.getFromClause());
        }
        this.convertAliasNameToTableName();
        this.tableExp.toTeradata();
        this.optionalHintClause = null;
        this.returningClause = null;
        return this.toString();
    }
    
    @Override
    public String toTimesTenString() throws ConvertException {
        this.withString = null;
        this.isolationLevel = null;
        final StringBuffer stringbuffer = new StringBuffer();
        this.updateClause.toTimesTen();
        this.tableExp.toTimesTen();
        if (this.setClause != null) {
            this.setClause.setFromUpdateQuerySatetemnt(this);
            this.setClause.toTimesTen();
        }
        if (this.whereExpression != null) {
            this.setUQSForWhereColumnInTimesTen(this.whereExpression);
            this.whereExpression = this.whereExpression.toTimesTenSelect(null, null);
        }
        if (this.fromClause != null) {
            throw new ConvertException("\nFROM clause is not supported in UPDATE statement in TimesTen 5.1.21\n");
        }
        this.optionalHintClause = null;
        this.updateLimitClause = null;
        this.returningClause = null;
        return this.toString();
    }
    
    @Override
    public String toNetezzaString() throws ConvertException {
        this.withString = null;
        this.isolationLevel = null;
        this.updateClause.toNetezza();
        boolean whereExprConverted = false;
        if (this.setClause != null) {
            if (this.tableExp.getTableClauseList() != null) {
                final ArrayList tableList = this.tableExp.getTableClauseList();
                for (int i = 0; i < tableList.size(); ++i) {
                    final Hashtable withTableColumnandIndex = new Hashtable();
                    if (tableList.get(i).getAlias() != null) {
                        final TableColumn tc = new TableColumn();
                        final TableObject setTableObject = tableList.get(i).getTableObject();
                        final String setOriginalTableNameForAlias = setTableObject.getTableName();
                        tc.setTableName(setOriginalTableNameForAlias);
                        tc.setOwnerName(setTableObject.getUser());
                        String keyString = tableList.get(i).getAlias() + ".";
                        withTableColumnandIndex.put(keyString, tc);
                        if (!tableList.get(i).getAlias().trim().equals("")) {
                            if (keyString.startsWith(".")) {
                                keyString = keyString.substring(1, keyString.length());
                            }
                            if (keyString.endsWith(".")) {
                                keyString = keyString.substring(0, keyString.length() - 1);
                            }
                            final FromClause fc1 = new FromClause();
                            final FromTable ft1 = new FromTable();
                            ft1.setAliasName(keyString);
                            ft1.setTableName(setOriginalTableNameForAlias);
                            fc1.addFromItem(ft1);
                            fc1.setFromClause("FROM");
                            this.setFromClause(fc1);
                        }
                    }
                }
            }
            this.setClause.toNetezza();
            if (this.setClause.getExpression() != null) {
                boolean subqueryHasAggregateFunction = false;
                final ArrayList expressionList = this.setClause.getExpression();
                if (expressionList != null) {
                    for (int ii = 0; ii < expressionList.size(); ++ii) {
                        if (expressionList.get(ii) instanceof SelectQueryStatement) {
                            final SelectQueryStatement subQuery = expressionList.get(ii);
                            final SelectStatement subSelectStatement = subQuery.getSelectStatement();
                            final FromClause sqlFromClause = subQuery.getFromClause();
                            final Vector fromList = sqlFromClause.getFromItemList();
                            final Vector subSelectCol = subSelectStatement.getSelectItemList();
                            for (int j = 0; j < subSelectCol.size(); ++j) {
                                if (subSelectCol.get(j) instanceof SelectColumn && (subSelectCol.get(j).isAggregateFunction() || this.selectColumnHasAggrFunction(subSelectCol.get(j).getColumnExpression(), false))) {
                                    subqueryHasAggregateFunction = true;
                                    if (fromList != null) {
                                        for (int k = 0; k < fromList.size(); ++k) {
                                            if (fromList.get(k) instanceof FromTable) {
                                                final FromTable sqlFromTable = fromList.get(k);
                                                if (sqlFromTable.getAliasName() == null && this.tableExp.getTableClauseList() != null && this.tableExp.getTableClauseList().size() > 0 && this.tableExp.getTableClauseList().get(0) instanceof TableClause) {
                                                    final TableClause sqlTableClause = this.tableExp.getTableClauseList().get(0);
                                                    final String tableName = sqlTableClause.getTableObject().toString();
                                                    final String aliasName = sqlTableClause.getAlias();
                                                    System.out.println("The alais of table clause is" + aliasName);
                                                    if (sqlFromTable.getTableName().toString().trim().equalsIgnoreCase(tableName.trim())) {
                                                        sqlFromTable.setAliasName(aliasName);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (this.setClause.getSetExpressionList() != null) {
                boolean subqueryHasAggregateFunction = false;
                final SetExpression se = this.setClause.getSetExpressionList().get(0);
                if (se.getSubQuery() != null) {
                    final SelectQueryStatement subQuery2 = se.getSubQuery();
                    final SelectStatement subSelectStatement2 = subQuery2.getSelectStatement();
                    final FromClause sqlFromClause2 = subQuery2.getFromClause();
                    final Vector fromList2 = sqlFromClause2.getFromItemList();
                    final Vector subSelectCol2 = subSelectStatement2.getSelectItemList();
                    if (subSelectCol2 != null) {
                        for (int l = 0; l < subSelectCol2.size(); ++l) {
                            if (subSelectCol2.get(l) instanceof SelectColumn && subSelectCol2.get(l).isAggregateFunction()) {
                                subqueryHasAggregateFunction = true;
                                if (fromList2 != null) {
                                    for (int m = 0; m < fromList2.size(); ++m) {
                                        if (fromList2.get(m) instanceof FromTable) {
                                            final FromTable sqlFromTable2 = fromList2.get(m);
                                            if (sqlFromTable2.getAliasName() == null && this.tableExp.getTableClauseList() != null && this.tableExp.getTableClauseList().size() > 0 && this.tableExp.getTableClauseList().get(0) instanceof TableClause) {
                                                final TableClause sqlTableClause2 = this.tableExp.getTableClauseList().get(0);
                                                final String tableName2 = sqlTableClause2.getTableObject().toString();
                                                final String aliasName2 = sqlTableClause2.getAlias();
                                                if (sqlFromTable2.getTableName().toString().trim().equalsIgnoreCase(tableName2.trim())) {
                                                    sqlFromTable2.setAliasName(aliasName2);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!subqueryHasAggregateFunction) {
                        final Vector fromItemList = subQuery2.getFromClause().getFromItemList();
                        final FromTable ft2 = new FromTable();
                        final ArrayList tableClauseList = this.tableExp.getTableClauseList();
                        final TableClause tableClause = tableClauseList.get(0);
                        String tableToBeAdded = null;
                        String aliasToBeAdded = null;
                        tableToBeAdded = tableClause.getTableObject().toString();
                        aliasToBeAdded = tableClause.getAlias();
                        boolean addTableName = true;
                        int i2 = 0;
                        while (i2 < fromItemList.size()) {
                            final String existingTableName = fromItemList.get(i2).getTableName().toString();
                            final String existingAliasName = fromItemList.get(i2).getAliasName();
                            if (existingTableName.equalsIgnoreCase(tableToBeAdded)) {
                                addTableName = false;
                                if (existingAliasName == null) {
                                    fromItemList.get(i2).setAliasName(aliasToBeAdded);
                                    break;
                                }
                                break;
                            }
                            else {
                                if (existingAliasName != null && existingAliasName.equalsIgnoreCase(aliasToBeAdded)) {
                                    addTableName = false;
                                    break;
                                }
                                ++i2;
                            }
                        }
                        if (addTableName) {
                            ft2.setTableName(tableToBeAdded);
                            ft2.setAliasName(aliasToBeAdded);
                            fromItemList.insertElementAt(ft2, 0);
                        }
                        if (this.getFromClause() == null) {
                            this.setFromClause(subQuery2.getFromClause());
                        }
                        else {
                            final Vector updateFromItemList = this.fromClause.getFromItemList();
                            if (subQuery2.getFromClause() != null) {
                                final Vector subQueryFromList = subQuery2.getFromClause().getFromItemList();
                                for (int i3 = 0; i3 < subQueryFromList.size(); ++i3) {
                                    boolean alreadyExistsInFromClause = false;
                                    for (int index = 0; index < updateFromItemList.size(); ++index) {
                                        final String multipleQueryString = SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
                                        if (updateFromItemList.get(index).toString().equalsIgnoreCase(subQueryFromList.get(i3).toString())) {
                                            alreadyExistsInFromClause = true;
                                            break;
                                        }
                                        SelectQueryStatement.singleQueryConvertedToMultipleQueryList = multipleQueryString;
                                    }
                                    if (!alreadyExistsInFromClause) {
                                        updateFromItemList.add(subQueryFromList.get(i3));
                                    }
                                }
                            }
                        }
                        if (this.getWhereExpression() == null) {
                            this.setWhereClause(subQuery2.getWhereExpression());
                            whereExprConverted = true;
                        }
                        else {
                            final WhereExpression wExp = subQuery2.getWhereExpression();
                            if (wExp != null) {
                                final Vector where_items = wExp.getWhereItems();
                                for (int i3 = 0; i3 < where_items.size(); ++i3) {
                                    if (where_items.get(i3) instanceof WhereItem) {
                                        final WhereItem wi = where_items.get(i3);
                                        final WhereColumn lwc = wi.getLeftWhereExp();
                                        if (lwc != null) {
                                            final Vector where_col_expression = lwc.getColumnExpression();
                                            if (where_col_expression != null && where_col_expression.get(0) instanceof TableColumn) {
                                                final TableColumn tc2 = where_col_expression.get(0);
                                                final FromTable ftable = MetadataInfoUtil.getTableOfColumn(subQuery2, tc2);
                                                if (ftable != null) {
                                                    tc2.setTableName(ftable.toString());
                                                }
                                            }
                                        }
                                        final WhereColumn rwc = wi.getRightWhereExp();
                                        if (rwc != null) {
                                            final Vector where_col_expression2 = rwc.getColumnExpression();
                                            if (where_col_expression2 != null && where_col_expression2.get(0) instanceof TableColumn) {
                                                final TableColumn tc3 = where_col_expression2.get(0);
                                                final FromTable ftable2 = MetadataInfoUtil.getTableOfColumn(subQuery2, tc3);
                                                if (ftable2 != null) {
                                                    tc3.setTableName(ftable2.toString());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            wExp.addWhereExpression(this.getWhereExpression().toNetezzaSelect(null, null));
                            wExp.addOperator("AND");
                            this.setWhereClause(wExp);
                            whereExprConverted = true;
                        }
                        this.setOrderByStatement(subQuery2.getOrderByStatement());
                    }
                }
            }
        }
        if (this.hintClause != null) {
            this.hintClause.toSQLServer();
        }
        if (this.fromClause != null) {
            this.fromClause = this.fromClause.toNetezzaSelect(null, null);
        }
        this.convertAliasNameToTableName();
        this.tableExp.toNetezza();
        if (this.whereExpression != null && !whereExprConverted) {
            if (!SwisSQLAPI.MSSQLSERVER_THETA) {
                final SelectQueryStatement sqs1 = new SelectQueryStatement();
                sqs1.setFromClause(this.fromClause);
                sqs1.setWhereExpression(this.whereExpression);
                this.whereExpression = this.whereExpression.toNetezzaSelect(sqs1, sqs1);
                this.fromClause = sqs1.getFromClause();
            }
            else {
                this.whereExpression = this.whereExpression.toNetezzaSelect(null, null);
            }
        }
        this.returningClause = null;
        this.updateLimitClause = null;
        return this.toString();
    }
    
    private void setUQSForWhereColumnInTimesTen(final WhereExpression we) throws ConvertException {
        final Vector wis = we.getWhereItems();
        if (wis != null) {
            for (int i = 0; i < wis.size(); ++i) {
                final Object obj = wis.get(i);
                if (obj instanceof WhereItem) {
                    final WhereItem wi = (WhereItem)obj;
                    if (wi.getRownumClause() != null) {
                        wis.remove(i);
                        we.getOperator().remove(i - 1);
                        final RownumClause rnc = wi.getRownumClause();
                        final String rownum_operator = rnc.getOperator();
                        if (!rownum_operator.trim().equalsIgnoreCase("<")) {
                            throw new ConvertException("\nROWNUM conditions with lessthan('<') is supported.\n");
                        }
                        if (rnc.getRownumValue() != null) {
                            int rownum_value = -1;
                            try {
                                final Object rownum_value_obj = rnc.getRownumValue();
                                rownum_value = Integer.parseInt(rownum_value_obj + "");
                            }
                            catch (final Exception ex) {}
                            this.setTimesTenFirst(rownum_value);
                        }
                        --i;
                    }
                    else {
                        final WhereColumn lwc = wi.getLeftWhereExp();
                        if (lwc != null) {
                            lwc.setFromUQS(this);
                        }
                        final WhereColumn rwc = wi.getRightWhereExp();
                        if (rwc != null) {
                            rwc.setFromUQS(this);
                        }
                    }
                }
            }
        }
    }
    
    private void setUQSForWhereColumn(final WhereExpression we) {
        final Vector wis = we.getWhereItems();
        if (wis != null) {
            for (int i = 0; i < wis.size(); ++i) {
                final Object obj = wis.get(i);
                if (obj instanceof WhereItem) {
                    final WhereItem wi = (WhereItem)obj;
                    final WhereColumn lwc = wi.getLeftWhereExp();
                    if (lwc != null) {
                        lwc.setFromUQS(this);
                    }
                    final WhereColumn rwc = wi.getRightWhereExp();
                    if (rwc != null) {
                        rwc.setFromUQS(this);
                    }
                }
            }
        }
    }
    
    public void convertAliasNameToTableName() throws ConvertException {
        final ArrayList al_tcl = this.tableExp.getTableClauseList();
        if (al_tcl != null && al_tcl.get(0) instanceof TableClause) {
            final TableClause tc = al_tcl.get(0);
            final String s_an = tc.getAlias();
            final String s_tn = tc.getTableObject().toString();
            if (this.whereExpression != null && this.fromClause == null) {
                this.changeWhereColumn(this.whereExpression, s_an, s_tn);
            }
        }
    }
    
    public void changeWhereColumn(final WhereExpression we, final String s_an, final String s_tn) {
        final Vector v_wi = we.getWhereItems();
        for (int i_count = 0; i_count < v_wi.size(); ++i_count) {
            if (v_wi.elementAt(i_count) instanceof WhereItem) {
                final WhereItem wi = v_wi.elementAt(i_count);
                final WhereColumn lwc = wi.getLeftWhereExp();
                final WhereColumn rwc = wi.getRightWhereExp();
                if (lwc != null) {
                    final Vector v_lce = lwc.getColumnExpression();
                    if (v_lce != null) {
                        for (int i_icount = 0; i_icount < v_lce.size(); ++i_icount) {
                            if (v_lce.elementAt(i_icount) instanceof TableColumn) {
                                final TableColumn tc = v_lce.elementAt(i_icount);
                                final String s_tablename = tc.getTableName();
                                if (s_tablename != null && s_tablename.equalsIgnoreCase(s_an)) {
                                    tc.setTableName(s_tn);
                                }
                            }
                            else if (v_lce.elementAt(i_icount) instanceof FunctionCalls) {
                                final FunctionCalls fc = v_lce.elementAt(i_icount);
                                final Vector functionArguments = fc.getFunctionArguments();
                                if (functionArguments != null) {
                                    for (int i = 0; i < functionArguments.size(); ++i) {
                                        if (functionArguments.get(i) instanceof SelectColumn) {
                                            final SelectColumn sc = functionArguments.get(i);
                                            sc.setOriginalTableNamesForUpdateSetClause(this.withTableColumnandIndex);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (rwc != null) {
                    final Vector v_lce = rwc.getColumnExpression();
                    if (v_lce != null) {
                        for (int i_icount = 0; i_icount < v_lce.size(); ++i_icount) {
                            if (v_lce.elementAt(i_icount) instanceof TableColumn) {
                                final TableColumn tc = v_lce.elementAt(i_icount);
                                final String s_tablename = tc.getTableName();
                                if (s_tablename != null && s_tablename.equalsIgnoreCase(s_an)) {
                                    tc.setTableName(s_tn);
                                }
                            }
                        }
                    }
                }
            }
            else if (v_wi.elementAt(i_count) instanceof WhereExpression) {
                this.changeWhereColumn(v_wi.elementAt(i_count), s_an, s_tn);
            }
        }
    }
    
    private String singleQueryIntoMultipleQueriesForPLSQL() {
        return SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
    }
    
    private boolean selectColumnHasAggrFunction(final Vector colExp, final boolean inputVal) {
        boolean bool = inputVal;
        if (colExp != null) {
            for (int i = 0; i < colExp.size(); ++i) {
                if (colExp.get(i) instanceof SelectColumn) {
                    if (colExp.get(i).isAggregateFunction()) {
                        return true;
                    }
                    final Vector selColExp = colExp.get(i).getColumnExpression();
                    bool = this.selectColumnHasAggrFunction(selColExp, bool);
                }
                else if (colExp.get(i) instanceof FunctionCalls) {
                    final Vector FunctionArgs = colExp.get(i).getFunctionArguments();
                    bool = this.selectColumnHasAggrFunction(FunctionArgs, bool);
                }
            }
        }
        return bool;
    }
    
    private boolean processSetClauseIntoASingleSubQuery(final ArrayList expList) {
        final boolean bool = false;
        final Vector aliasOrTableList = new Vector();
        final Vector selectColumnVector = new Vector();
        for (int index = 0; index < expList.size(); ++index) {
            if (expList.get(index) instanceof SelectColumn && index > 1 && expList.get(index - 1) instanceof String && expList.get(index - 1).toString().trim().equals("=")) {
                final SelectColumn sc = expList.get(index);
                selectColumnVector.add(sc);
                if (sc.getColumnExpression() != null) {
                    final Vector colExp = sc.getColumnExpression();
                    this.addAllTheElementsInColumnExpression(colExp, aliasOrTableList, bool);
                }
            }
        }
        if (aliasOrTableList.size() > 0) {
            this.selectQueryForUpdateStmt = true;
            final ArrayList columnList = new ArrayList();
            expList.add(this.convertSetClauseExpressionIntoASingleSubQuery(selectColumnVector, aliasOrTableList));
            for (int index2 = 0; index2 < expList.size(); ++index2) {
                if (expList.get(index2) instanceof SelectColumn && index2 > 1 && expList.get(index2 - 1) instanceof String && expList.get(index2 - 1).toString().trim().equals("=")) {
                    expList.remove(index2 - 1);
                    columnList.add(expList.remove(index2 - 1));
                }
            }
        }
        if (this.fromClause != null && this.fromClause.getFromItemList() != null) {
            final Vector fromItems = this.fromClause.getFromItemList();
            final Vector tableOrAliasList = new Vector();
            for (int j = 0; j < fromItems.size(); ++j) {
                if (fromItems.get(j) instanceof FromTable) {
                    final FromTable ft = fromItems.get(j);
                    if (ft.getAliasName() != null) {
                        tableOrAliasList.add(ft.getAliasName());
                    }
                    else if (ft.getTableName() != null && ft.getTableName() instanceof String) {
                        tableOrAliasList.add(ft.getTableName().toString());
                    }
                }
                else if (fromItems.get(j) instanceof FromClause) {
                    this.addAllTheFromItemsInFromItemList(fromItems, tableOrAliasList);
                }
            }
            if (bool) {
                this.removeWhereItemsAfterAllProcess(this.whereExpression, tableOrAliasList);
            }
        }
        return bool;
    }
    
    private void addAllTheFromItemsInFromItemList(final Vector fromItems, final Vector tableOrAliasList) {
        for (int i = 0; i < fromItems.size(); ++i) {
            if (fromItems.get(i) instanceof FromTable) {
                final FromTable ft = fromItems.get(i);
                if (ft.getAliasName() != null) {
                    tableOrAliasList.add(ft.getAliasName());
                }
                else if (ft.getTableName() != null && ft.getTableName() instanceof String) {
                    tableOrAliasList.add(ft.getTableName().toString());
                }
            }
            else if (fromItems.get(i) instanceof FromClause) {
                final Vector newFromItemList = fromItems.get(i).getFromItemList();
                this.addAllTheFromItemsInFromItemList(newFromItemList, tableOrAliasList);
            }
        }
    }
    
    private void addAllTheElementsInColumnExpression(final Vector colExp, final Vector aliasOrTableList, boolean bool) {
        for (int i = 0; i < colExp.size(); ++i) {
            if (colExp.get(i) instanceof TableColumn) {
                final TableColumn tc = colExp.get(i);
                if (this.fromClause != null && this.fromClause.getFromItemList() != null && tc.getTableName() != null) {
                    final Vector fromItems = this.fromClause.getFromItemList();
                    for (int j = 0; j < fromItems.size(); ++j) {
                        if (fromItems.get(j) instanceof FromTable) {
                            final FromTable ft = fromItems.get(j);
                            if (ft.getAliasName() != null && ft.getAliasName().equalsIgnoreCase(tc.getTableName())) {
                                bool = true;
                                aliasOrTableList.add(ft.getAliasName());
                            }
                            else if (ft.getTableName() != null && ft.getTableName() instanceof String && ft.getTableName().toString().equalsIgnoreCase(tc.getTableName())) {
                                bool = true;
                                aliasOrTableList.add(ft.getTableName().toString());
                            }
                        }
                        else if (fromItems.get(j) instanceof FromClause) {
                            final Vector newFromItems = fromItems.get(j).getFromItemList();
                            this.addAllTheFromItemsInFromItemList(newFromItems, aliasOrTableList);
                        }
                    }
                }
            }
            else if (colExp.get(i) instanceof CaseStatement) {
                if (this.fromClause != null && this.fromClause.getFromItemList() != null) {
                    final Vector fromItems2 = this.fromClause.getFromItemList();
                    for (int k = 0; k < fromItems2.size(); ++k) {
                        if (fromItems2.get(k) instanceof FromTable) {
                            final FromTable ft2 = fromItems2.get(k);
                            if (ft2.getAliasName() != null) {
                                aliasOrTableList.add(ft2.getAliasName());
                            }
                            else if (ft2.getTableName() != null && ft2.getTableName() instanceof String) {
                                aliasOrTableList.add(ft2.getTableName().toString());
                            }
                        }
                    }
                    bool = true;
                }
            }
            else if (colExp.get(i) instanceof FunctionCalls) {
                if (this.fromClause != null && this.fromClause.getFromItemList() != null) {
                    final Vector fromItems2 = this.fromClause.getFromItemList();
                    for (int k = 0; k < fromItems2.size(); ++k) {
                        if (fromItems2.get(k) instanceof FromTable) {
                            final FromTable ft2 = fromItems2.get(k);
                            if (ft2.getAliasName() != null) {
                                aliasOrTableList.add(ft2.getAliasName());
                            }
                            else if (ft2.getTableName() != null && ft2.getTableName() instanceof String) {
                                aliasOrTableList.add(ft2.getTableName().toString());
                            }
                        }
                    }
                    bool = true;
                }
            }
            else if (colExp.get(i) instanceof SelectColumn) {
                final SelectColumn sc = colExp.get(i);
                final Vector scVector = sc.getColumnExpression();
                this.addAllTheElementsInColumnExpression(scVector, aliasOrTableList, bool);
            }
        }
    }
    
    private void removeTheFromTableForUpdate(final Vector fromItems) {
        String tableName = "";
        final TableExpression te = this.tableExp;
        final ArrayList tableClauseList = te.getTableClauseList();
        if (tableClauseList != null && tableClauseList.size() > 0 && tableClauseList.get(0) instanceof TableClause) {
            tableName = tableClauseList.get(0).getTableObject().getTableName();
        }
        for (int i = 0; i < fromItems.size(); ++i) {
            if (fromItems.get(i) instanceof FromTable) {
                final FromTable ft = fromItems.get(i);
                if (ft.getAliasName() != null && ft.getAliasName().equalsIgnoreCase(tableName)) {
                    fromItems.remove(i);
                }
                else if (ft.getTableName() != null && ft.getTableName() instanceof String && ft.getTableName().toString().equalsIgnoreCase(tableName)) {
                    fromItems.remove(i);
                }
            }
            else if (fromItems.get(i) instanceof FromClause) {
                final Vector newFCItems = fromItems.get(i).getFromItemList();
                this.removeTheFromTableForUpdate(newFCItems);
            }
        }
    }
    
    public SelectQueryStatement convertSetClauseExpressionIntoASingleSubQuery(final Vector selectColumns, final Vector tableOrAliasList) {
        final SelectQueryStatement sqs = new SelectQueryStatement();
        final SelectStatement ss = new SelectStatement();
        ss.setSelectClause("SELECT");
        for (int i = 0; i < selectColumns.size(); ++i) {
            final SelectColumn sc = selectColumns.get(i);
            if (i < selectColumns.size() - 1) {
                sc.setEndsWith(",");
            }
        }
        ss.setSelectItemList(selectColumns);
        sqs.setSelectStatement(ss);
        final FromClause fc = new FromClause();
        fc.setFromClause("FROM");
        Vector fromItems = new Vector();
        fromItems = this.fromClause.getFromItemList();
        fc.setFromItemList(fromItems);
        sqs.setFromClause(fc);
        if (this.whereExpression != null && this.whereExpression.getWhereItems() != null) {
            final WhereExpression we = new WhereExpression();
            sqs.setWhereExpression(this.whereExpression);
        }
        return sqs;
    }
    
    public void processWhereExpressionForConvertingForSingleSubQuery(final WhereExpression we, final WhereExpression whereExpression, final Vector tableOrAliasList) {
        final Vector whereExpressionList = whereExpression.getWhereItems();
        final Vector operators = whereExpression.getOperator();
        for (int i = 0; i < whereExpressionList.size(); ++i) {
            if (whereExpressionList.get(i) instanceof WhereItem) {
                final WhereItem wi = whereExpressionList.get(i);
                boolean isremoved = false;
                if (wi.getLeftWhereExp() != null) {
                    final WhereColumn lwc = wi.getLeftWhereExp();
                    if (lwc.getColumnExpression() != null) {
                        for (int j = 0; j < lwc.getColumnExpression().size(); ++j) {
                            if (lwc.getColumnExpression().get(j) instanceof TableColumn) {
                                for (int k = 0; k < tableOrAliasList.size(); ++k) {
                                    final String tableOrAliasReferenceName = tableOrAliasList.get(k).toString();
                                    final TableColumn tc = lwc.getColumnExpression().get(j);
                                    if (tc.getTableName() != null && tc.getTableName().equalsIgnoreCase(tableOrAliasReferenceName) && !isremoved) {
                                        isremoved = true;
                                        if (we.getWhereItems() != null && !we.getWhereItems().isEmpty()) {
                                            if (i < whereExpressionList.size()) {
                                                we.addWhereItem(whereExpressionList.get(i));
                                            }
                                            if (i != 0) {
                                                final int icount = i;
                                                if (operators.get(i - 1).toString().equalsIgnoreCase("&AND")) {
                                                    we.addOperator("AND");
                                                }
                                                else if (operators.get(i - 1).toString().equalsIgnoreCase("&OR")) {
                                                    we.addOperator("OR");
                                                }
                                                else {
                                                    we.addOperator(operators.get(i - 1).toString());
                                                }
                                            }
                                            else {
                                                we.addOperator(operators.get(i).toString());
                                            }
                                        }
                                        else {
                                            we.addOperator(operators.get(i).toString());
                                            we.addWhereItem(whereExpressionList.get(i));
                                        }
                                        if (i != 0) {
                                            final String op = operators.get(i - 1);
                                            if (!op.equals("&AND")) {
                                                operators.setElementAt("&AND", i - 1);
                                            }
                                            else if (operators.size() > i) {
                                                operators.setElementAt("&AND", i);
                                            }
                                        }
                                        else if (operators.size() > i) {
                                            operators.setElementAt("", i);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (wi.getRightWhereExp() != null && !isremoved) {
                    final WhereColumn rwc = wi.getRightWhereExp();
                    if (rwc.getColumnExpression() != null) {
                        for (int j = 0; j < rwc.getColumnExpression().size(); ++j) {
                            if (rwc.getColumnExpression().get(j) instanceof TableColumn) {
                                for (int k = 0; k < tableOrAliasList.size(); ++k) {
                                    final String tableOrAliasReferenceName = tableOrAliasList.get(k).toString();
                                    final TableColumn tc = rwc.getColumnExpression().get(j);
                                    if (tc.getTableName() != null && tc.getTableName().equalsIgnoreCase(tableOrAliasReferenceName) && !isremoved) {
                                        if (we.getWhereItems() != null && !we.getWhereItems().isEmpty()) {
                                            if (i < whereExpressionList.size()) {
                                                we.addWhereItem(whereExpressionList.get(i));
                                            }
                                            isremoved = true;
                                            if (i != 0) {
                                                if (operators.get(i - 1).toString().equalsIgnoreCase("&AND")) {
                                                    we.addOperator("AND");
                                                }
                                                else if (operators.get(i - 1).toString().equalsIgnoreCase("&OR")) {
                                                    we.addOperator("OR");
                                                }
                                                else {
                                                    we.addOperator(operators.get(i - 1).toString());
                                                }
                                            }
                                            else {
                                                we.addOperator(operators.get(i).toString());
                                            }
                                        }
                                        else {
                                            we.addWhereItem(whereExpressionList.get(i));
                                        }
                                        if (whereExpressionList.size() > i) {}
                                        if (i != 0) {
                                            final String op = operators.get(i - 1);
                                            if (!op.equals("&AND")) {
                                                operators.setElementAt("&AND", i - 1);
                                            }
                                            else if (operators.size() > i) {
                                                operators.setElementAt("&AND", i);
                                            }
                                        }
                                        else if (operators.size() > i) {
                                            operators.setElementAt("&AND", i);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else if (whereExpressionList.get(i) instanceof WhereExpression) {
                final WhereExpression subWhereExp = whereExpressionList.get(i);
                final WhereExpression newWE = new WhereExpression();
                newWE.setOpenBrace(subWhereExp.getOpenBrace());
                newWE.setCloseBrace(subWhereExp.getCloseBrace());
                this.processWhereExpressionForConvertingForSingleSubQuery(newWE, subWhereExp, tableOrAliasList);
                for (int countNum = 0; countNum < subWhereExp.getOperator().size(); ++countNum) {}
                we.addWhereExpression(newWE);
            }
        }
    }
    
    private boolean processSetClauseExpressionIntoSubQuery(final ArrayList expList) {
        boolean bool = false;
        for (int index = 0; index < expList.size(); ++index) {
            if (expList.get(index) instanceof SelectColumn && index > 1 && expList.get(index - 1) instanceof String && expList.get(index - 1).toString().trim().equals("=")) {
                final SelectColumn sc = expList.get(index);
                if (sc.getColumnExpression() != null) {
                    final Vector colExp = sc.getColumnExpression();
                    for (int i = 0; i < colExp.size(); ++i) {
                        if (colExp.get(i) instanceof TableColumn) {
                            final TableColumn tc = colExp.get(i);
                            if (this.fromClause != null && this.fromClause.getFromItemList() != null && tc.getTableName() != null) {
                                final Vector fromItems = this.fromClause.getFromItemList();
                                final Vector aliasOrTableList = new Vector();
                                for (int j = 0; j < fromItems.size(); ++j) {
                                    if (fromItems.get(j) instanceof FromTable) {
                                        final FromTable ft = fromItems.get(j);
                                        if (ft.getAliasName() != null) {
                                            aliasOrTableList.add(ft.getAliasName());
                                        }
                                        else if (ft.getTableName() != null && ft.getTableName() instanceof String) {
                                            aliasOrTableList.add(ft.getTableName().toString());
                                        }
                                    }
                                }
                                expList.set(index, this.convertSetClauseExpressionIntoSubQuery(sc, aliasOrTableList));
                                expList.add(index, "(");
                                expList.add(index + 2, ")");
                                bool = true;
                            }
                        }
                        else if (colExp.get(i) instanceof CaseStatement) {
                            if (this.fromClause != null && this.fromClause.getFromItemList() != null) {
                                final Vector fromItems2 = this.fromClause.getFromItemList();
                                final Vector aliasOrTableList2 = new Vector();
                                for (int k = 0; k < fromItems2.size(); ++k) {
                                    if (fromItems2.get(k) instanceof FromTable) {
                                        final FromTable ft2 = fromItems2.get(k);
                                        if (ft2.getAliasName() != null) {
                                            aliasOrTableList2.add(ft2.getAliasName());
                                        }
                                        else if (ft2.getTableName() != null && ft2.getTableName() instanceof String) {
                                            aliasOrTableList2.add(ft2.getTableName().toString());
                                        }
                                    }
                                }
                                expList.set(index, this.convertSetClauseExpressionIntoSubQuery(sc, aliasOrTableList2));
                                expList.add(index, "(");
                                expList.add(index + 2, ")");
                                bool = true;
                            }
                        }
                        else if (colExp.get(i) instanceof FunctionCalls && this.fromClause != null && this.fromClause.getFromItemList() != null) {
                            final Vector fromItems2 = this.fromClause.getFromItemList();
                            final Vector aliasOrTableList2 = new Vector();
                            for (int k = 0; k < fromItems2.size(); ++k) {
                                if (fromItems2.get(k) instanceof FromTable) {
                                    final FromTable ft2 = fromItems2.get(k);
                                    if (ft2.getAliasName() != null) {
                                        aliasOrTableList2.add(ft2.getAliasName());
                                    }
                                    else if (ft2.getTableName() != null && ft2.getTableName() instanceof String) {
                                        aliasOrTableList2.add(ft2.getTableName().toString());
                                    }
                                }
                            }
                            expList.set(index, this.convertSetClauseExpressionIntoSubQuery(sc, aliasOrTableList2));
                            expList.add(index, "(");
                            expList.add(index + 2, ")");
                            bool = true;
                        }
                    }
                }
            }
        }
        if (this.fromClause != null && this.fromClause.getFromItemList() != null) {
            final Vector fromItems3 = this.fromClause.getFromItemList();
            final Vector tableOrAliasList = new Vector();
            for (int l = 0; l < fromItems3.size(); ++l) {
                if (fromItems3.get(l) instanceof FromTable) {
                    final FromTable ft3 = fromItems3.get(l);
                    if (ft3.getAliasName() != null) {
                        tableOrAliasList.add(ft3.getAliasName());
                    }
                    else if (ft3.getTableName() != null && ft3.getTableName() instanceof String) {
                        tableOrAliasList.add(ft3.getTableName().toString());
                    }
                }
            }
            if (bool) {
                this.removeWhereItemsAfterAllProcess(this.whereExpression, tableOrAliasList);
            }
        }
        return bool;
    }
    
    public void removeWhereItemsAfterAllProcess(final WhereExpression we, final Vector tableOrAliasList) {
        if (we != null) {
            final Vector whereExpressionList = we.getWhereItems();
            for (int i = 0; i < whereExpressionList.size(); ++i) {
                if (whereExpressionList.get(i) instanceof WhereItem) {
                    final WhereItem wi = whereExpressionList.get(i);
                    boolean isremoved = false;
                    if (wi.getLeftWhereExp() != null) {
                        final WhereColumn lwc = wi.getLeftWhereExp();
                        if (lwc.getColumnExpression() != null) {
                            for (int j = 0; j < lwc.getColumnExpression().size(); ++j) {
                                if (lwc.getColumnExpression().get(j) instanceof TableColumn) {
                                    for (int k = 0; k < tableOrAliasList.size(); ++k) {
                                        final String tableOrAliasReferenceName = tableOrAliasList.get(k).toString();
                                        final TableColumn tc = lwc.getColumnExpression().get(j);
                                        if (tc.getTableName() != null && tc.getTableName().trim().equalsIgnoreCase(tableOrAliasReferenceName.trim())) {
                                            isremoved = true;
                                            if (we.getWhereItems() != null && !we.getWhereItems().isEmpty()) {
                                                whereExpressionList.set(i, null);
                                            }
                                            else {
                                                whereExpressionList.set(i, null);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (wi.getRightWhereExp() != null && !isremoved) {
                        final WhereColumn rwc = wi.getRightWhereExp();
                        if (rwc.getColumnExpression() != null) {
                            for (int j = 0; j < rwc.getColumnExpression().size(); ++j) {
                                if (rwc.getColumnExpression().get(j) instanceof TableColumn) {
                                    for (int k = 0; k < tableOrAliasList.size(); ++k) {
                                        final String tableOrAliasReferenceName = tableOrAliasList.get(k).toString();
                                        final TableColumn tc = rwc.getColumnExpression().get(j);
                                        if (tc.getTableName() != null && tc.getTableName().trim().equalsIgnoreCase(tableOrAliasReferenceName.trim())) {
                                            if (we.getWhereItems() != null && !we.getWhereItems().isEmpty()) {
                                                whereExpressionList.set(i, null);
                                            }
                                            else {
                                                whereExpressionList.set(i, null);
                                            }
                                            if (whereExpressionList.size() > i) {
                                                whereExpressionList.set(i, null);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else if (whereExpressionList.get(i) instanceof WhereExpression) {
                    WhereExpression subWhereExp = new WhereExpression();
                    subWhereExp = whereExpressionList.get(i);
                    this.removeWhereItemsAfterAllProcess(subWhereExp, tableOrAliasList);
                }
            }
        }
    }
    
    public SelectQueryStatement convertSetClauseExpressionIntoSubQuery(final SelectColumn sc, final Vector tableOrAliasList) {
        final SelectQueryStatement sqs = new SelectQueryStatement();
        final SelectStatement ss = new SelectStatement();
        ss.setSelectClause("SELECT");
        final Vector selectColumns = new Vector();
        selectColumns.add(sc);
        ss.setSelectItemList(selectColumns);
        sqs.setSelectStatement(ss);
        final FromClause fc = new FromClause();
        fc.setFromClause("FROM");
        final Vector fromItems = new Vector();
        fromItems.addAll(this.fromClause.getFromItemList());
        fc.setFromItemList(fromItems);
        sqs.setFromClause(fc);
        if (this.whereExpression != null && this.whereExpression.getWhereItems() != null) {
            final WhereExpression we = new WhereExpression();
            this.processWhereExpressionForConvertingToSubQuery(we, this.whereExpression, tableOrAliasList);
            sqs.setWhereExpression(we);
        }
        return sqs;
    }
    
    public void processWhereExpressionForConvertingToSubQuery(final WhereExpression we, final WhereExpression whereExpression, final Vector tableOrAliasList) {
        final Vector whereExpressionList = whereExpression.getWhereItems();
        final Vector operators = whereExpression.getOperator();
        for (int i = 0; i < whereExpressionList.size(); ++i) {
            if (whereExpressionList.get(i) instanceof WhereItem) {
                final WhereItem wi = whereExpressionList.get(i);
                boolean isremoved = false;
                if (wi.getLeftWhereExp() != null) {
                    final WhereColumn lwc = wi.getLeftWhereExp();
                    if (lwc.getColumnExpression() != null) {
                        for (int j = 0; j < lwc.getColumnExpression().size(); ++j) {
                            if (lwc.getColumnExpression().get(j) instanceof TableColumn) {
                                for (int k = 0; k < tableOrAliasList.size(); ++k) {
                                    final String tableOrAliasReferenceName = tableOrAliasList.get(k).toString();
                                    final TableColumn tc = lwc.getColumnExpression().get(j);
                                    if (tc.getTableName() != null && tc.getTableName().equalsIgnoreCase(tableOrAliasReferenceName)) {
                                        isremoved = true;
                                        if (we.getWhereItems() != null && !we.getWhereItems().isEmpty()) {
                                            we.addWhereItem(whereExpressionList.get(i));
                                            if (i != 0) {
                                                final int icount = i;
                                                if (operators.get(i - 1).toString().equalsIgnoreCase("&AND")) {
                                                    we.addOperator("AND");
                                                }
                                                else if (operators.get(i - 1).toString().equalsIgnoreCase("&OR")) {
                                                    we.addOperator("AND");
                                                }
                                                else {
                                                    we.addOperator(operators.get(i - 1).toString());
                                                }
                                            }
                                            else {
                                                we.addOperator("AND");
                                            }
                                        }
                                        else {
                                            we.addWhereItem(whereExpressionList.get(i));
                                        }
                                        if (i != 0) {
                                            final String op = operators.get(i - 1);
                                            if (!op.equals("&AND")) {
                                                operators.setElementAt("&AND", i - 1);
                                            }
                                            else if (operators.size() > i) {
                                                operators.setElementAt("&AND", i);
                                            }
                                        }
                                        else if (operators.size() > i) {
                                            operators.setElementAt("&AND", i);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (wi.getRightWhereExp() != null && !isremoved) {
                    final WhereColumn rwc = wi.getRightWhereExp();
                    if (rwc.getColumnExpression() != null) {
                        for (int j = 0; j < rwc.getColumnExpression().size(); ++j) {
                            if (rwc.getColumnExpression().get(j) instanceof TableColumn) {
                                for (int k = 0; k < tableOrAliasList.size(); ++k) {
                                    final String tableOrAliasReferenceName = tableOrAliasList.get(k).toString();
                                    final TableColumn tc = rwc.getColumnExpression().get(j);
                                    if (tc.getTableName() != null && tc.getTableName().equalsIgnoreCase(tableOrAliasReferenceName)) {
                                        if (we.getWhereItems() != null && !we.getWhereItems().isEmpty()) {
                                            we.addWhereItem(whereExpressionList.get(i));
                                            if (i != 0) {
                                                if (operators.get(i - 1).toString().equalsIgnoreCase("&AND")) {
                                                    we.addOperator("AND");
                                                }
                                                else if (operators.get(i - 1).toString().equalsIgnoreCase("&OR")) {
                                                    we.addOperator("OR");
                                                }
                                                else {
                                                    we.addOperator("AND");
                                                }
                                            }
                                            else {
                                                we.addOperator("AND");
                                            }
                                        }
                                        else {
                                            we.addWhereItem(whereExpressionList.get(i));
                                        }
                                        if (i != 0) {
                                            final String op = operators.get(i - 1);
                                            if (!op.equals("&AND")) {
                                                operators.setElementAt("&AND", i - 1);
                                            }
                                            else if (operators.size() > i) {
                                                operators.setElementAt("&AND", i);
                                            }
                                        }
                                        else if (operators.size() > i) {
                                            operators.setElementAt("&AND", i);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else if (whereExpressionList.get(i) instanceof WhereExpression) {
                final WhereExpression subWhereExp = whereExpressionList.get(i);
                final WhereExpression newWE = new WhereExpression();
                newWE.setOpenBrace(subWhereExp.getOpenBrace());
                newWE.setCloseBrace(subWhereExp.getCloseBrace());
                this.processWhereExpressionForConvertingToSubQuery(newWE, subWhereExp, tableOrAliasList);
                for (int countNum = 0; countNum < subWhereExp.getOperator().size(); ++countNum) {
                    newWE.addOperator(subWhereExp.getOperator().get(countNum));
                }
                we.addWhereExpression(newWE);
            }
        }
    }
    
    @Override
    public UserObjectContext getObjectContext() {
        return this.objectContext;
    }
    
    @Override
    public void setObjectContext(final UserObjectContext obj) {
        this.objectContext = obj;
    }
    
    @Override
    public String toVectorWiseString() throws ConvertException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
