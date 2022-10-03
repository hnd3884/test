package com.adventnet.swissqlapi.sql.statement.update;

import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.Hashtable;
import java.util.ArrayList;

public class SetClause
{
    private String set;
    private ArrayList setExpressionList;
    private ArrayList expressionList;
    private Hashtable originalTableNameList;
    private UpdateQueryStatement fromUQS;
    private UserObjectContext context;
    private CommentClass commentObj;
    
    public SetClause() {
        this.context = null;
        this.expressionList = new ArrayList();
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public void setSet(final String s) {
        this.set = s;
    }
    
    public String getSet() {
        return this.set;
    }
    
    public void setExpression(final ArrayList list) {
        this.expressionList = list;
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public ArrayList getExpression() {
        return this.expressionList;
    }
    
    public ArrayList getSetExpressionList() {
        return this.setExpressionList;
    }
    
    public void setOriginalTableName(final Hashtable tableList) {
        this.originalTableNameList = tableList;
    }
    
    public Hashtable getOriginalTableName() {
        return this.originalTableNameList;
    }
    
    public void setSetExpressionList(final ArrayList list) {
        this.setExpressionList = list;
    }
    
    public void setFromUpdateQuerySatetemnt(final UpdateQueryStatement fromUQS) {
        this.fromUQS = fromUQS;
    }
    
    public void toMySQL() throws ConvertException {
        this.setCommentClass(null);
        String updateTableName = null;
        if (this.fromUQS != null && this.fromUQS.getTableExpression() != null && this.fromUQS.getTableExpression().getTableClauseList() != null) {
            final ArrayList tabList = this.fromUQS.getTableExpression().getTableClauseList();
            if (tabList != null && tabList.size() == 1 && tabList.get(0) instanceof TableClause) {
                final TableClause tc = tabList.get(0);
                final TableObject tb = tc.getTableObject();
                updateTableName = tb.getOrigTableName();
            }
        }
        if (this.expressionList == null || this.expressionList.size() == 0) {
            for (int size = this.setExpressionList.size(), i = 0; i < size; i += 2) {
                final SetExpression setExpression = this.setExpressionList.get(i);
                setExpression.toMySQL();
            }
            this.convertSetExpressionListToExpressionList(this.setExpressionList, 5);
        }
        else {
            for (int j = 0; j < this.expressionList.size(); ++j) {
                if (this.expressionList.get(j) instanceof SelectQueryStatement) {
                    this.expressionList.set(j, this.expressionList.get(j).toMySQLSelect());
                }
                else if (this.expressionList.get(j) instanceof SelectColumn) {
                    final SelectColumn sc = this.expressionList.get(j);
                    if (updateTableName != null) {
                        final Vector colExp = sc.getColumnExpression();
                        if (colExp != null) {
                            for (int k = 0; k < colExp.size(); ++k) {
                                if (colExp.get(k) instanceof TableColumn) {
                                    final TableColumn tc2 = colExp.get(k);
                                    tc2.setOrigTableName(updateTableName);
                                }
                            }
                        }
                    }
                    this.expressionList.set(j, sc.toMySQLSelect(null, null));
                }
                if (this.expressionList.get(j) instanceof TableColumn) {
                    final TableColumn tcToBeChanged = this.expressionList.get(j);
                    final String checkForAliasName = tcToBeChanged.getTableName() + ".";
                    if (this.originalTableNameList != null) {
                        if (this.originalTableNameList.containsKey(checkForAliasName)) {
                            final TableColumn tc3 = this.originalTableNameList.get(checkForAliasName);
                            tcToBeChanged.setTableName(tc3.getTableName());
                            tcToBeChanged.setOwnerName(tc3.getOwnerName());
                            this.expressionList.set(j, tcToBeChanged.toMySQLSelect(null, null));
                        }
                        else {
                            this.expressionList.set(j, this.expressionList.get(j).toMySQLSelect(null, null));
                        }
                    }
                    else {
                        this.expressionList.set(j, this.expressionList.get(j).toMySQLSelect(null, null));
                    }
                }
            }
        }
    }
    
    public void toOracle() throws ConvertException {
        if (this.expressionList == null || this.expressionList.size() == 0) {
            for (int size = this.setExpressionList.size(), i = 0; i < size; i += 2) {
                if (this.setExpressionList.get(i) instanceof SetExpression) {
                    final SetExpression setExpression = this.setExpressionList.get(i);
                    setExpression.toOracle();
                }
            }
            this.convertSetExpressionListToExpressionList(this.setExpressionList, 1);
        }
        else {
            boolean datetime = false;
            for (int j = 0; j < this.expressionList.size(); ++j) {
                if (this.expressionList.get(j) instanceof SelectQueryStatement) {
                    this.expressionList.set(j, this.expressionList.get(j).toOracleSelect());
                }
                else if (this.expressionList.get(j) instanceof SelectColumn) {
                    final SelectColumn sc = this.expressionList.get(j);
                    sc.setFromUQS(this.fromUQS);
                    boolean added = false;
                    final Vector colExpr = sc.getColumnExpression();
                    if (colExpr.size() == 1 && colExpr.get(0) instanceof String && colExpr.get(0).toString().trim().startsWith("'")) {
                        final String value = colExpr.get(0).toString().trim();
                        if (datetime) {
                            final String format = SwisSQLUtils.getDateFormat(value, 1);
                            if (format != null) {
                                final FunctionCalls fc = new FunctionCalls();
                                final TableColumn tc = new TableColumn();
                                tc.setColumnName("TO_DATE");
                                final Vector fnArgs = new Vector();
                                if (format.startsWith("'1900")) {
                                    fnArgs.add(format);
                                    fnArgs.add("'YYYY-MM-DD HH24:MI:SS'");
                                }
                                else {
                                    fnArgs.add(value);
                                    fnArgs.add(format);
                                }
                                fc.setFunctionName(tc);
                                fc.setFunctionArguments(fnArgs);
                                colExpr.setElementAt(fc, 0);
                                this.expressionList.set(j, sc);
                                added = true;
                            }
                        }
                    }
                    if (!added) {
                        this.expressionList.set(j, this.expressionList.get(j).toOracleSelect(null, null));
                    }
                }
                if (this.expressionList.get(j) instanceof TableColumn) {
                    final TableColumn tc2 = this.expressionList.get(j);
                    tc2.setFromUQS(this.fromUQS);
                    final String datatype = MetadataInfoUtil.getDatatypeName(this.fromUQS, tc2);
                    datetime = false;
                    if (datatype != null && datatype.toLowerCase().endsWith("datetime")) {
                        datetime = true;
                    }
                    this.expressionList.set(j, tc2.toOracleSelect(null, null));
                }
                if (this.expressionList.get(j) instanceof FunctionCalls) {
                    final FunctionCalls fc2 = this.expressionList.get(j);
                    fc2.getFunctionArguments().add(this.expressionList.get(j - 2));
                    this.expressionList.set(j - 1, " = ");
                    this.expressionList.set(j, this.expressionList.get(j).toOracleSelect(null, null));
                }
            }
        }
    }
    
    public void toMSSQLServer() throws ConvertException {
        this.setCommentClass(null);
        boolean sqsFound = false;
        if (this.expressionList == null || this.expressionList.size() == 0) {
            for (int size = this.setExpressionList.size(), i = 0; i < size; i += 2) {
                final SetExpression setExpression = this.setExpressionList.get(i);
                setExpression.toMSSQLServer();
            }
            this.convertSetExpressionListToExpressionList(this.setExpressionList, 2);
        }
        else {
            int count = 0;
            int tableColumnCount = 0;
            for (int i = 0; i < this.expressionList.size(); ++i) {
                if (this.expressionList.get(i) instanceof SelectQueryStatement) {
                    ++count;
                }
                else if (this.expressionList.get(i) instanceof TableColumn) {
                    ++tableColumnCount;
                }
            }
            if (count == 1) {
                sqsFound = true;
            }
            for (int i = 0; i < this.expressionList.size(); ++i) {
                if (this.expressionList.get(i) instanceof SelectColumn) {
                    this.expressionList.set(i, this.expressionList.get(i).toMSSQLServerSelect(null, null));
                }
                else if (this.expressionList.get(i) instanceof SelectQueryStatement) {
                    boolean subqueryHasAggregateFunction = false;
                    final SelectStatement subSelectStatement = this.expressionList.get(i).getSelectStatement();
                    final Vector subSelectCol = subSelectStatement.getSelectItemList();
                    if (subSelectCol != null) {
                        for (int j = 0; j < subSelectCol.size(); ++j) {
                            if (subSelectCol.get(j) instanceof SelectColumn && (subSelectCol.get(j).isAggregateFunction() || this.selectColumnHasAggrFunction(subSelectCol.get(j).getColumnExpression(), false))) {
                                subqueryHasAggregateFunction = true;
                            }
                        }
                    }
                    if (count == 1 && !subqueryHasAggregateFunction && tableColumnCount <= 1) {
                        if (i >= 3) {
                            final SetExpression setExp = new SetExpression();
                            final ArrayList columnList = new ArrayList();
                            columnList.add("(");
                            columnList.add(this.expressionList.get(i - 3));
                            columnList.add(")");
                            setExp.setColumnList(columnList);
                            setExp.setEqualTo("=");
                            setExp.setSubQuery(this.expressionList.get(i));
                            setExp.setExpressionList(null);
                            final ArrayList setExpressionArrayList = new ArrayList();
                            setExpressionArrayList.add(setExp);
                            this.setSetExpressionList(setExpressionArrayList);
                            setExp.toMSSQLServer();
                            for (int k = i + 1; k > i - 3; --k) {
                                this.expressionList.remove(k);
                            }
                            this.expressionList = setExp.getExpressionList();
                            i -= 3;
                        }
                        else {
                            this.expressionList.set(i, this.expressionList.get(i).toMSSQLServerSelect());
                        }
                    }
                    else {
                        this.expressionList.set(i, this.expressionList.get(i).toMSSQLServerSelect());
                    }
                }
                if (this.expressionList.get(i) instanceof TableColumn) {
                    final TableColumn tcToBeChanged = this.expressionList.get(i);
                    final String checkForAliasName = tcToBeChanged.getTableName() + ".";
                    if (this.originalTableNameList != null && !sqsFound) {
                        if (this.originalTableNameList.containsKey(checkForAliasName)) {
                            final TableColumn tc = this.originalTableNameList.get(checkForAliasName);
                            tcToBeChanged.setTableName(tc.getTableName());
                            tcToBeChanged.setOwnerName(tc.getOwnerName());
                            this.expressionList.set(i, tcToBeChanged.toMSSQLServerSelect(null, null));
                        }
                        else {
                            this.expressionList.set(i, this.expressionList.get(i).toMSSQLServerSelect(null, null));
                        }
                    }
                    else {
                        this.expressionList.set(i, this.expressionList.get(i).toMSSQLServerSelect(null, null));
                    }
                }
            }
        }
    }
    
    public void toSybase() throws ConvertException {
        this.setCommentClass(null);
        boolean sqsFound = false;
        if (this.expressionList == null || this.expressionList.size() == 0) {
            for (int size = this.setExpressionList.size(), i = 0; i < size; i += 2) {
                final SetExpression setExpression = this.setExpressionList.get(i);
                setExpression.toSybase();
            }
            this.convertSetExpressionListToExpressionList(this.setExpressionList, 7);
        }
        else {
            int count = 0;
            for (int j = 0; j < this.expressionList.size(); ++j) {
                if (this.expressionList.get(j) instanceof SelectQueryStatement) {
                    ++count;
                }
            }
            if (count == 1) {
                sqsFound = true;
            }
            for (int j = 0; j < this.expressionList.size(); ++j) {
                if (this.expressionList.get(j) instanceof SelectColumn) {
                    this.expressionList.get(j).setObjectContext(this.context);
                    this.expressionList.set(j, this.expressionList.get(j).toSybaseSelect(null, null));
                }
                else if (this.expressionList.get(j) instanceof SelectQueryStatement) {
                    boolean subqueryHasAggregateFunction = false;
                    final SelectStatement subSelectStatement = this.expressionList.get(j).getSelectStatement();
                    final Vector subSelectCol = subSelectStatement.getSelectItemList();
                    if (subSelectCol != null) {
                        for (int k = 0; k < subSelectCol.size(); ++k) {
                            if (subSelectCol.get(k) instanceof SelectColumn && subSelectCol.get(k).isAggregateFunction()) {
                                subqueryHasAggregateFunction = true;
                            }
                        }
                    }
                    if (count == 1 && !subqueryHasAggregateFunction) {
                        if (j >= 3) {
                            final SetExpression setExp = new SetExpression();
                            final ArrayList columnList = new ArrayList();
                            columnList.add("(");
                            columnList.add(this.expressionList.get(j - 3));
                            columnList.add(")");
                            setExp.setColumnList(columnList);
                            setExp.setEqualTo("=");
                            setExp.setSubQuery(this.expressionList.get(j));
                            setExp.setExpressionList(null);
                            final ArrayList setExpressionArrayList = new ArrayList();
                            setExpressionArrayList.add(setExp);
                            this.setSetExpressionList(setExpressionArrayList);
                            setExp.toSybase();
                            for (int l = j + 1; l > j - 3; --l) {
                                this.expressionList.remove(l);
                            }
                            this.expressionList = setExp.getExpressionList();
                            j -= 3;
                        }
                        else {
                            this.expressionList.set(j, this.expressionList.get(j).toSybaseSelect());
                        }
                    }
                    else {
                        this.expressionList.set(j, this.expressionList.get(j).toSybaseSelect());
                    }
                }
                if (this.expressionList.get(j) instanceof TableColumn) {
                    final TableColumn tcToBeChanged = this.expressionList.get(j);
                    final String checkForAliasName = tcToBeChanged.getTableName() + ".";
                    if (this.originalTableNameList != null && !sqsFound) {
                        if (this.originalTableNameList.containsKey(checkForAliasName)) {
                            final TableColumn tc = this.originalTableNameList.get(checkForAliasName);
                            tcToBeChanged.setTableName(tc.getTableName());
                            tcToBeChanged.setOwnerName(tc.getOwnerName());
                            this.expressionList.set(j, tcToBeChanged.toSybaseSelect(null, null));
                        }
                        else {
                            this.expressionList.set(j, this.expressionList.get(j).toSybaseSelect(null, null));
                        }
                    }
                    else {
                        this.expressionList.set(j, this.expressionList.get(j).toSybaseSelect(null, null));
                    }
                }
            }
        }
    }
    
    public void toPostgreSQL() throws ConvertException {
        this.setCommentClass(null);
        if (this.expressionList == null || this.expressionList.size() == 0) {
            for (int size = this.setExpressionList.size(), i = 0; i < size; i += 2) {
                final SetExpression setExpression = this.setExpressionList.get(i);
                setExpression.toPostgreSQL();
            }
            this.convertSetExpressionListToExpressionList(this.setExpressionList, 4);
        }
        else {
            for (int j = 0; j < this.expressionList.size(); ++j) {
                if (this.expressionList.get(j) instanceof SelectQueryStatement) {
                    this.expressionList.set(j, this.expressionList.get(j).toPostgreSQLSelect());
                }
                else if (this.expressionList.get(j) instanceof SelectColumn) {
                    final SelectColumn sc = this.expressionList.get(j);
                    if (this.originalTableNameList != null) {
                        sc.setOriginalTableNamesForUpdateSetClause(this.originalTableNameList);
                    }
                    this.expressionList.set(j, sc.toPostgreSQLSelect(null, null));
                }
                if (this.expressionList.get(j) instanceof TableColumn) {
                    final TableColumn tcToBeChanged = this.expressionList.get(j);
                    final String checkForAliasName = tcToBeChanged.getTableName() + ".";
                    if (this.originalTableNameList != null) {
                        if (this.originalTableNameList.containsKey(checkForAliasName)) {
                            final TableColumn tc = this.originalTableNameList.get(checkForAliasName);
                            tcToBeChanged.setTableName(tc.getTableName());
                            this.expressionList.set(j, tcToBeChanged.toPostgreSQLSelect(null, null));
                        }
                        else {
                            this.expressionList.set(j, this.expressionList.get(j).toPostgreSQLSelect(null, null));
                        }
                    }
                    else {
                        this.expressionList.set(j, this.expressionList.get(j).toPostgreSQLSelect(null, null));
                    }
                }
            }
        }
    }
    
    public void toDB2() throws ConvertException {
        this.setCommentClass(null);
        if (this.expressionList == null || this.expressionList.size() == 0) {
            for (int size = this.setExpressionList.size(), i = 0; i < size; i += 2) {
                final SetExpression setExpression = this.setExpressionList.get(i);
                setExpression.toDB2();
            }
        }
        else {
            for (int j = 0; j < this.expressionList.size(); ++j) {
                if (this.expressionList.get(j) instanceof SelectQueryStatement) {
                    this.expressionList.set(j, this.expressionList.get(j).toDB2Select());
                }
                else if (this.expressionList.get(j) instanceof SelectColumn) {
                    final SelectColumn sc = this.expressionList.get(j);
                    final Vector colExpr = sc.getColumnExpression();
                    boolean added = false;
                    if (colExpr != null && colExpr.size() == 1) {
                        final Object obj = colExpr.get(0);
                        if (obj instanceof String) {
                            final String str = (String)obj;
                            if (str.equalsIgnoreCase("NULL")) {
                                added = true;
                                this.expressionList.set(j, sc);
                            }
                        }
                    }
                    if (!added) {
                        if (j - 2 >= 0) {
                            final Object obj = this.expressionList.get(j - 2);
                            if (obj instanceof TableColumn) {
                                final TableColumn tableColumn = (TableColumn)obj;
                                final FromTable fromTable = MetadataInfoUtil.getTableOfColumn(this.fromUQS, tableColumn);
                                if (fromTable != null) {
                                    if (fromTable.getTableName() instanceof String) {
                                        tableColumn.setTableName((String)fromTable.getTableName());
                                    }
                                    sc.setCorrespondingTableColumn(tableColumn);
                                }
                            }
                        }
                        this.expressionList.set(j, sc.toDB2Select(null, null));
                    }
                }
                if (this.expressionList.get(j) instanceof TableColumn) {
                    this.expressionList.set(j, this.expressionList.get(j).toDB2Select(null, null));
                    final String dataType = MetadataInfoUtil.getDatatypeName(this.fromUQS, this.expressionList.get(j));
                    if (dataType != null && (dataType.toLowerCase().trim().indexOf("int") != -1 || dataType.toLowerCase().trim().indexOf("num") != -1) && j + 2 < this.expressionList.size()) {
                        final Object obj2 = this.expressionList.get(j + 2);
                        if (obj2 instanceof SelectColumn) {
                            final SelectColumn sc2 = (SelectColumn)obj2;
                            final Vector colExpr2 = sc2.getColumnExpression();
                            if (colExpr2 != null && colExpr2.size() == 1) {
                                final Object scObj = colExpr2.get(0);
                                if (scObj instanceof String) {
                                    final String str2 = (String)scObj;
                                    if (str2.startsWith("'")) {
                                        colExpr2.setElementAt(str2.substring(1, str2.length() - 1), 0);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void toInformix() throws ConvertException {
        this.setCommentClass(null);
        if (this.expressionList == null || this.expressionList.size() == 0) {
            for (int size = this.setExpressionList.size(), i = 0; i < size; i += 2) {
                final SetExpression setExpression = this.setExpressionList.get(i);
                setExpression.toInformix();
            }
        }
        else {
            for (int j = 0; j < this.expressionList.size(); ++j) {
                if (this.expressionList.get(j) instanceof SelectQueryStatement) {
                    this.expressionList.set(j, this.expressionList.get(j).toInformixSelect());
                }
                else if (this.expressionList.get(j) instanceof SelectColumn) {
                    this.expressionList.set(j, this.expressionList.get(j).toInformixSelect(null, null));
                }
                if (this.expressionList.get(j) instanceof TableColumn) {
                    final TableColumn tcToBeChanged = this.expressionList.get(j);
                    final String checkForAliasName = tcToBeChanged.getTableName() + ".";
                    if (this.originalTableNameList != null) {
                        if (this.originalTableNameList.containsKey(checkForAliasName)) {
                            final TableColumn tc = this.originalTableNameList.get(checkForAliasName);
                            tcToBeChanged.setTableName(tc.getTableName());
                            tcToBeChanged.setOwnerName(tc.getOwnerName());
                            this.expressionList.set(j, tcToBeChanged.toInformixSelect(null, null));
                        }
                        else {
                            this.expressionList.set(j, this.expressionList.get(j).toInformixSelect(null, null));
                        }
                    }
                    else {
                        this.expressionList.set(j, this.expressionList.get(j).toInformixSelect(null, null));
                    }
                }
            }
        }
    }
    
    public void toANSISQL() throws ConvertException {
        this.setCommentClass(null);
        if (this.expressionList == null || this.expressionList.size() == 0) {
            for (int size = this.setExpressionList.size(), i = 0; i < size; i += 2) {
                final SetExpression setExpression = this.setExpressionList.get(i);
                setExpression.toANSISQL();
            }
            this.convertSetExpressionListToExpressionList(this.setExpressionList, 8);
        }
        else {
            for (int j = 0; j < this.expressionList.size(); ++j) {
                if (this.expressionList.get(j) instanceof SelectQueryStatement) {
                    this.expressionList.set(j, this.expressionList.get(j).toANSISelect());
                }
                else if (this.expressionList.get(j) instanceof SelectColumn) {
                    this.expressionList.set(j, this.expressionList.get(j).toANSISelect(null, null));
                }
                if (this.expressionList.get(j) instanceof TableColumn) {
                    final TableColumn tcToBeChanged = this.expressionList.get(j);
                    final String checkForAliasName = tcToBeChanged.getTableName() + ".";
                    if (this.originalTableNameList != null) {
                        if (this.originalTableNameList.containsKey(checkForAliasName)) {
                            final TableColumn tc = this.originalTableNameList.get(checkForAliasName);
                            tcToBeChanged.setTableName(tc.getTableName());
                            tcToBeChanged.setOwnerName(tc.getOwnerName());
                            this.expressionList.set(j, tcToBeChanged.toANSISelect(null, null));
                        }
                        else {
                            this.expressionList.set(j, this.expressionList.get(j).toANSISelect(null, null));
                        }
                    }
                    else {
                        this.expressionList.set(j, this.expressionList.get(j).toANSISelect(null, null));
                    }
                }
            }
        }
    }
    
    public void toTeradata() throws ConvertException {
        this.setCommentClass(null);
        if (this.expressionList == null || this.expressionList.size() == 0) {
            for (int size = this.setExpressionList.size(), i = 0; i < size; i += 2) {
                final SetExpression setExpression = this.setExpressionList.get(i);
                setExpression.toTeradata();
            }
            this.convertSetExpressionListToExpressionList(this.setExpressionList, 12);
        }
        else {
            for (int j = 0; j < this.expressionList.size(); ++j) {
                if (this.expressionList.get(j) instanceof SelectQueryStatement) {
                    this.expressionList.set(j, this.expressionList.get(j).toTeradataSelect());
                }
                else if (this.expressionList.get(j) instanceof SelectColumn) {
                    this.expressionList.set(j, this.expressionList.get(j).toTeradataSelect(null, null));
                }
                if (this.expressionList.get(j) instanceof TableColumn) {
                    final TableColumn tcToBeChanged = this.expressionList.get(j);
                    final String checkForAliasName = tcToBeChanged.getTableName() + ".";
                    if (this.originalTableNameList != null) {
                        if (this.originalTableNameList.containsKey(checkForAliasName)) {
                            final TableColumn tc = this.originalTableNameList.get(checkForAliasName);
                            tcToBeChanged.setTableName(tc.getTableName());
                            tcToBeChanged.setOwnerName(tc.getOwnerName());
                            this.expressionList.set(j, tcToBeChanged.toTeradataSelect(null, null));
                        }
                        else {
                            this.expressionList.set(j, this.expressionList.get(j).toTeradataSelect(null, null));
                        }
                    }
                    else {
                        final TableColumn tc = this.expressionList.get(j).toTeradataSelect(null, null);
                        if (tc.getTableName() != null) {
                            tc.setTableName(null);
                        }
                        this.expressionList.set(j, tc);
                    }
                }
            }
        }
    }
    
    public void toTimesTen() throws ConvertException {
        this.setCommentClass(null);
        if (this.expressionList == null || this.expressionList.size() == 0) {
            for (int size = this.setExpressionList.size(), i = 0; i < size; i += 2) {
                if (this.setExpressionList.get(i) instanceof SetExpression) {
                    final SetExpression setExpression = this.setExpressionList.get(i);
                    setExpression.toTimesTen();
                }
            }
            this.convertSetExpressionListToExpressionList(this.setExpressionList, 10);
        }
        else {
            boolean datetime = false;
            boolean timestamp = false;
            boolean unicode = false;
            for (int j = 0; j < this.expressionList.size(); ++j) {
                if (this.expressionList.get(j) instanceof SelectQueryStatement) {
                    throw new ConvertException("\nSubqueries are not supported in UPDATE statement's SET Clause in TimesTen 5.1.21\n");
                }
                if (this.expressionList.get(j) instanceof SelectColumn) {
                    if (this.expressionList.get(j).toString().equalsIgnoreCase("default")) {
                        throw new ConvertException("\nDEFAULT clause not supported in the UPDATE statemnet in TimesTen 5.1.21\n");
                    }
                    boolean added = false;
                    final SelectColumn sc = this.expressionList.get(j);
                    sc.setFromUQS(this.fromUQS);
                    final Vector colExpr = sc.getColumnExpression();
                    if (colExpr.size() == 1 && colExpr.get(0) instanceof String && colExpr.get(0).toString().trim().startsWith("'")) {
                        String value = colExpr.get(0).toString().trim();
                        if (datetime) {
                            String format = SwisSQLUtils.getDateFormat(value, 10);
                            if (format != null && (format.equals("YYYY-MM-DD") || format.equals("HH24:MI:SS"))) {
                                if (timestamp) {
                                    if (format.equals("YYYY-MM-DD")) {
                                        value = value.substring(0, value.length() - 1) + " 00:00:00'";
                                    }
                                    else {
                                        value = "'1900-01-01 " + value.substring(1);
                                    }
                                    colExpr.setElementAt(value, 0);
                                    this.expressionList.set(j, sc);
                                    added = true;
                                }
                                format = null;
                            }
                            if (format != null) {
                                if (format.startsWith("'1900")) {
                                    colExpr.setElementAt(format, 0);
                                    this.expressionList.set(j, sc);
                                    added = true;
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
                                    if (timestamp && time == "") {
                                        value += " 00:00:00";
                                    }
                                    else if (time != "") {
                                        value = value + " " + time;
                                    }
                                    colExpr.setElementAt(value, 0);
                                    this.expressionList.set(j, sc);
                                    added = true;
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
                                    colExpr.setElementAt(fc, 0);
                                    this.expressionList.set(j, sc);
                                    added = true;
                                }
                            }
                        }
                        else if (unicode) {
                            colExpr.setElementAt("N" + value, 0);
                            this.expressionList.set(j, sc);
                            added = true;
                        }
                    }
                    if (!added) {
                        this.expressionList.set(j, this.expressionList.get(j).toTimesTenSelect(null, null));
                    }
                }
                if (this.expressionList.get(j) instanceof TableColumn) {
                    final TableColumn tc2 = this.expressionList.get(j);
                    final String datatype = MetadataInfoUtil.getDatatypeName(this.fromUQS, tc2);
                    datetime = false;
                    timestamp = false;
                    unicode = false;
                    if (datatype != null) {
                        if (datatype.indexOf("date") != -1 || datatype.indexOf("time") != -1) {
                            datetime = true;
                            if (datatype.toLowerCase().indexOf("datetime") != -1) {
                                timestamp = true;
                            }
                        }
                        else if (datatype.indexOf("unichar") != -1 || datatype.indexOf("univarchar") != -1 || datatype.indexOf("nchar") != -1 || datatype.indexOf("nvarchar") != -1) {
                            unicode = true;
                        }
                    }
                    this.expressionList.set(j, tc2.toTimesTenSelect(null, null));
                }
            }
        }
    }
    
    public void toNetezza() throws ConvertException {
        this.setCommentClass(null);
        boolean sqsFound = false;
        if (this.expressionList == null || this.expressionList.size() == 0) {
            for (int size = this.setExpressionList.size(), i = 0; i < size; i += 2) {
                final SetExpression setExpression = this.setExpressionList.get(i);
                setExpression.toNetezzaSQL();
            }
            this.convertSetExpressionListToExpressionList(this.setExpressionList, 2);
        }
        else {
            int count = 0;
            int tableColumnCount = 0;
            for (int i = 0; i < this.expressionList.size(); ++i) {
                if (this.expressionList.get(i) instanceof SelectQueryStatement) {
                    ++count;
                }
                else if (this.expressionList.get(i) instanceof TableColumn) {
                    ++tableColumnCount;
                }
            }
            if (count == 1) {
                sqsFound = true;
            }
            for (int i = 0; i < this.expressionList.size(); ++i) {
                if (this.expressionList.get(i) instanceof SelectColumn) {
                    this.expressionList.set(i, this.expressionList.get(i).toNetezzaSelect(null, null));
                }
                else if (this.expressionList.get(i) instanceof SelectQueryStatement) {
                    boolean subqueryHasAggregateFunction = false;
                    final SelectStatement subSelectStatement = this.expressionList.get(i).getSelectStatement();
                    final Vector subSelectCol = subSelectStatement.getSelectItemList();
                    if (subSelectCol != null) {
                        for (int j = 0; j < subSelectCol.size(); ++j) {
                            if (subSelectCol.get(j) instanceof SelectColumn && (subSelectCol.get(j).isAggregateFunction() || this.selectColumnHasAggrFunction(subSelectCol.get(j).getColumnExpression(), false))) {
                                subqueryHasAggregateFunction = true;
                            }
                        }
                    }
                    if (count == 1 && !subqueryHasAggregateFunction && tableColumnCount <= 1) {
                        if (i >= 3) {
                            final SetExpression setExp = new SetExpression();
                            final ArrayList columnList = new ArrayList();
                            columnList.add("(");
                            columnList.add(this.expressionList.get(i - 3));
                            columnList.add(")");
                            setExp.setColumnList(columnList);
                            setExp.setEqualTo("=");
                            setExp.setSubQuery(this.expressionList.get(i));
                            setExp.setExpressionList(null);
                            final ArrayList setExpressionArrayList = new ArrayList();
                            setExpressionArrayList.add(setExp);
                            this.setSetExpressionList(setExpressionArrayList);
                            setExp.toNetezzaSQL();
                            for (int k = i + 1; k > i - 3; --k) {
                                this.expressionList.remove(k);
                            }
                            this.expressionList = setExp.getExpressionList();
                            i -= 3;
                        }
                        else {
                            this.expressionList.set(i, this.expressionList.get(i).toNetezzaSelect());
                        }
                    }
                    else {
                        this.expressionList.set(i, this.expressionList.get(i).toNetezzaSelect());
                    }
                }
                if (this.expressionList.get(i) instanceof TableColumn) {
                    final TableColumn tcToBeChanged = this.expressionList.get(i);
                    final String checkForAliasName = tcToBeChanged.getTableName() + ".";
                    if (this.originalTableNameList != null && !sqsFound) {
                        if (this.originalTableNameList.containsKey(checkForAliasName)) {
                            final TableColumn tc = this.originalTableNameList.get(checkForAliasName);
                            tcToBeChanged.setTableName(tc.getTableName());
                            tcToBeChanged.setOwnerName(tc.getOwnerName());
                            this.expressionList.set(i, tcToBeChanged.toNetezzaSelect(null, null));
                        }
                        else {
                            this.expressionList.set(i, this.expressionList.get(i).toNetezzaSelect(null, null));
                        }
                    }
                    else {
                        this.expressionList.set(i, this.expressionList.get(i).toNetezzaSelect(null, null));
                    }
                }
            }
        }
    }
    
    private void convertSetExpressionListToExpressionList(final ArrayList setExpressionList, final int database) throws ConvertException {
        for (int size = setExpressionList.size(), i = 0; i < size; ++i) {
            final Object obj = setExpressionList.get(i);
            if (!(obj instanceof SetExpression)) {
                this.expressionList.add(obj.toString());
            }
            else {
                final SetExpression setExpression = setExpressionList.get(i);
                final ArrayList columnList = setExpression.getColumnList();
                final ArrayList valueList = setExpression.getExpressionList();
                if (database == 10 && columnList.size() > 1) {
                    throw new ConvertException("\nSET with multiple LHS COLUMNs is not supported in UPDATE statement in TimesTen 5.1.21\n");
                }
                if (valueList == null) {
                    return;
                }
                if (columnList == null && valueList != null) {
                    if (database != 11) {
                        this.expressionList = valueList;
                        return;
                    }
                    final int j1 = 1;
                    if (size == 1) {
                        this.expressionList = valueList;
                        return;
                    }
                    for (int valueSize = valueList.size(), vS = 0; vS < valueSize; ++vS) {
                        this.expressionList.add(valueList.get(vS));
                    }
                    if (i >= size - 1) {
                        return;
                    }
                    this.expressionList.add(", ");
                }
                else {
                    int i2 = 1;
                    final int size2 = columnList.size();
                    if (size2 == 1) {
                        this.expressionList.add(columnList.get(0).toString());
                        this.expressionList.add(" = ");
                        if (valueList.size() == 1 || valueList.size() == 2) {
                            this.expressionList.add(valueList.get(0).toString());
                        }
                    }
                    else {
                        while (i2 < size2) {
                            this.expressionList.add(columnList.get(i2).toString());
                            this.expressionList.add(" = ");
                            if (valueList.size() == 1 || valueList.size() == 2) {
                                this.expressionList.add(valueList.get(0).toString());
                            }
                            else {
                                this.expressionList.add(valueList.get(i2).toString());
                            }
                            if (i2 < size2 - 2) {
                                this.expressionList.add(", ");
                            }
                            i2 += 2;
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer stringbuffer = new StringBuffer();
        if (this.commentObj != null) {
            stringbuffer.append(this.commentObj.toString().trim() + " ");
        }
        stringbuffer.append(this.set.toUpperCase());
        stringbuffer.append("\t");
        if (this.expressionList == null || this.expressionList.size() == 0) {
            for (int size = this.setExpressionList.size(), i = 0; i < size; ++i) {
                if (this.setExpressionList.get(i) instanceof TableColumn) {
                    this.setExpressionList.get(i).setObjectContext(this.context);
                }
                stringbuffer.append(this.setExpressionList.get(i).toString());
            }
        }
        else {
            ++SelectQueryStatement.beautyTabCount;
            for (int j = 0, size2 = this.expressionList.size(); j < size2; ++j) {
                if (this.expressionList.get(j) instanceof SelectQueryStatement) {
                    stringbuffer.append("\n");
                    SelectQueryStatement.beautyTabCount += 3;
                    this.expressionList.get(j).setObjectContext(this.context);
                    stringbuffer.append(this.expressionList.get(j));
                    SelectQueryStatement.beautyTabCount -= 3;
                    stringbuffer.append("\t\t\t\t");
                    ++j;
                }
                else if (!(this.expressionList.get(j) instanceof String) || this.expressionList.get(j).trim().startsWith("(SELECT ")) {}
                if (this.expressionList.get(j) instanceof TableColumn) {
                    this.expressionList.get(j).setObjectContext(this.context);
                    stringbuffer.append(this.expressionList.get(j));
                }
                else if (this.expressionList.get(j) instanceof FunctionCalls) {
                    this.expressionList.get(j).setObjectContext(this.context);
                    stringbuffer.append(this.expressionList.get(j));
                }
                else if (this.expressionList.get(j) instanceof SelectColumn) {
                    this.expressionList.get(j).setObjectContext(this.context);
                    stringbuffer.append(this.expressionList.get(j));
                }
                else if (this.context != null) {
                    stringbuffer.append(this.context.getEquivalent(this.expressionList.get(j).toString()));
                }
                else {
                    stringbuffer.append(this.expressionList.get(j));
                }
                if (this.expressionList.get(j) instanceof SelectColumn) {
                    this.expressionList.get(j).setObjectContext(this.context);
                    if (this.expressionList.get(j).toString().trim().endsWith(",")) {
                        stringbuffer.append("\n\t");
                    }
                }
                else if (this.expressionList.get(j) instanceof String && this.expressionList.get(j).endsWith(",")) {
                    stringbuffer.append("\t");
                }
            }
            --SelectQueryStatement.beautyTabCount;
        }
        return stringbuffer.toString();
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
}
