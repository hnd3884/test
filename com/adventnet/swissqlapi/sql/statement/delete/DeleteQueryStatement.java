package com.adventnet.swissqlapi.sql.statement.delete;

import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.update.OptionalSpecifier;
import com.adventnet.swissqlapi.sql.statement.select.RownumClause;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.update.TableClause;
import com.adventnet.swissqlapi.sql.parser.ParseException;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.update.OracleSpecificClass;
import com.adventnet.swissqlapi.sql.statement.update.WhereCurrentClause;
import com.adventnet.swissqlapi.sql.statement.update.OptionalHintClause;
import com.adventnet.swissqlapi.sql.statement.update.HintClause;
import com.adventnet.swissqlapi.sql.statement.update.ReturningClause;
import com.adventnet.swissqlapi.sql.statement.update.TableExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class DeleteQueryStatement implements SwisSQLStatement
{
    private UserObjectContext objectContext;
    private DeleteClause deleteClause;
    private FromClause fromClause;
    private WhereExpression whereExpression;
    private TableExpression tableExp;
    private DeleteLimitClause deleteLimitClause;
    private ReturningClause returningClause;
    private HintClause hintClause;
    private OptionalHintClause optionalHintClause;
    private WhereCurrentClause whereCurrentClause;
    private OracleSpecificClass OracleSpecificInstance;
    private CommentClass commentObject;
    private OrderByStatement orderByStatement;
    private String isolationLevel;
    private String withString;
    private ArrayList lockTableList;
    private ArrayList usingTableList;
    
    public DeleteQueryStatement() {
        this.objectContext = null;
        this.isolationLevel = null;
        this.withString = null;
        this.lockTableList = new ArrayList();
        this.deleteClause = null;
        this.fromClause = null;
        this.whereExpression = null;
        this.tableExp = null;
        this.deleteLimitClause = null;
    }
    
    @Override
    public void setCommentClass(final CommentClass commentObject) {
        this.commentObject = commentObject;
    }
    
    @Override
    public CommentClass getCommentClass() {
        return this.commentObject;
    }
    
    public void setDeleteClause(final DeleteClause dc) {
        this.deleteClause = dc;
    }
    
    public void setFromClause(final FromClause fromClause) {
        this.fromClause = fromClause;
    }
    
    public FromClause getFromClause() {
        return this.fromClause;
    }
    
    public void setWhereClause(final WhereExpression we) {
        (this.whereExpression = we).setFromDeleteQueryStatement(true);
    }
    
    public void setWhereCurrentClause(final WhereCurrentClause whereCurrentClause) {
        this.whereCurrentClause = whereCurrentClause;
    }
    
    public void setOrderByStatement(final OrderByStatement obs) {
        this.orderByStatement = obs;
    }
    
    public void setUsingTableList(final ArrayList usingTableList) {
        this.usingTableList = usingTableList;
    }
    
    public ArrayList getUsingTableList() {
        return this.usingTableList;
    }
    
    public OrderByStatement getOrderByStatement() {
        return this.orderByStatement;
    }
    
    public WhereExpression getWhereExpression() {
        return this.whereExpression;
    }
    
    public void setTableExpression(final TableExpression tableexpression) {
        this.tableExp = tableexpression;
    }
    
    public TableExpression getTableExpression() {
        return this.tableExp;
    }
    
    public void setDeleteLimitClause(final DeleteLimitClause l) {
        this.deleteLimitClause = l;
    }
    
    public DeleteLimitClause getLimitClause() {
        return this.deleteLimitClause;
    }
    
    public void setHintClause(final HintClause hc) {
        this.hintClause = hc;
    }
    
    public HintClause getHintClause() {
        return this.hintClause;
    }
    
    public void setOracleSpecificInstance(final OracleSpecificClass oraclespecificclass) {
        this.OracleSpecificInstance = oraclespecificclass;
    }
    
    public void setOptionalHintClause(final OptionalHintClause ohc) {
        this.optionalHintClause = ohc;
    }
    
    public OptionalHintClause getOptionalHintClause() {
        return this.optionalHintClause;
    }
    
    public void setReturningClause(final ReturningClause rc) {
        this.returningClause = rc;
    }
    
    public void setWithString(final String w) {
        this.withString = w;
    }
    
    public String getWithString() {
        return this.withString;
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
    
    public WhereCurrentClause getWhereCurrentClause() {
        return this.whereCurrentClause;
    }
    
    public DeleteClause getDeleteClause() {
        return this.deleteClause;
    }
    
    @Override
    public String toString() {
        final StringBuffer stringbuffer = new StringBuffer();
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
        stringbuffer.append(this.deleteClause.toString() + " ");
        if (this.tableExp != null) {
            if (this.objectContext != null) {
                this.tableExp.setObjectContext(this.objectContext);
            }
            stringbuffer.append(this.tableExp.toString() + " \n");
        }
        if (this.hintClause != null && this.hintClause.toString() != null && !this.hintClause.toString().trim().equals("")) {
            stringbuffer.append(this.hintClause.toString() + " \n");
        }
        if (this.fromClause != null) {
            if (this.objectContext != null) {
                this.fromClause.setObjectContext(this.objectContext);
            }
            stringbuffer.append(this.fromClause.toString() + " \n");
        }
        if (this.whereCurrentClause != null) {
            stringbuffer.append(this.whereCurrentClause.toString() + " \n");
        }
        if (this.whereExpression != null) {
            if (!this.whereExpression.toString().trim().equals("")) {
                stringbuffer.append("WHERE ");
                this.whereExpression.setObjectContext(this.objectContext);
                if (this.whereExpression.toString().indexOf("AND") == 0) {
                    stringbuffer.append(StringFunctions.replaceFirst(" ", "AND", this.whereExpression.toString()));
                }
                else {
                    stringbuffer.append(" " + this.whereExpression.toString());
                }
            }
            stringbuffer.append(" \n");
        }
        if (this.orderByStatement != null) {
            stringbuffer.append(this.orderByStatement.toString() + "\n");
        }
        if (this.deleteLimitClause != null) {
            stringbuffer.append(this.deleteLimitClause.toString() + " \n");
        }
        if (this.optionalHintClause != null) {
            stringbuffer.append(this.optionalHintClause.toString() + " \n");
        }
        if (this.returningClause != null) {
            stringbuffer.append(this.returningClause.toString() + "\n");
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
        this.deleteClause.toInformix();
        this.tableExp.toInformix();
        if (this.hintClause != null) {
            this.hintClause.toSQLServer();
        }
        this.convertAliasNameToTableName();
        if (this.whereExpression != null) {
            this.whereExpression = this.whereExpression.toInformixSelect(null, null);
        }
        this.returningClause = null;
        this.deleteLimitClause = null;
        this.orderByStatement = null;
        return this.toString();
    }
    
    @Override
    public String toOracleString() throws ConvertException {
        final StringBuffer stringbuffer = new StringBuffer();
        this.deleteClause.toOracle();
        this.tableExp.toOracle();
        if (this.hintClause != null) {
            this.hintClause.toOracle();
        }
        if (this.whereExpression != null) {
            this.setDQSForWhereColumn(this.whereExpression);
            this.whereExpression = this.whereExpression.toOracleSelect(null, null);
        }
        if (this.fromClause != null) {
            try {
                this.fromClause.convertToSubQuery(this, 1, this.fromClause);
            }
            catch (final ParseException pe) {
                System.err.println("Exception in SQLQuery : " + this.toString());
                pe.printStackTrace();
                throw new ConvertException("conversion failure ");
            }
        }
        this.optionalHintClause = null;
        if (this.deleteLimitClause != null) {
            this.deleteLimitClause.toOracleRowNum(this);
        }
        this.deleteLimitClause = null;
        this.orderByStatement = null;
        if (this.withString != null && this.isolationLevel != null) {
            String lockStatement = "LOCK TABLE ";
            final ArrayList tableList = this.getTableExpression().getTableClauseList();
            for (int i = 0; i < tableList.size(); ++i) {
                final Object o = tableList.get(i);
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
    public String toMSSQLServerString() throws ConvertException {
        this.withString = null;
        this.isolationLevel = null;
        this.deleteClause.toSQLServer();
        this.tableExp.toMSSQLServer();
        if (this.hintClause != null) {
            this.hintClause.toSQLServer();
        }
        this.convertAliasNameToTableName();
        if (this.whereExpression != null) {
            this.whereExpression = this.whereExpression.toMSSQLServerSelect(null, null);
        }
        this.returningClause = null;
        this.deleteLimitClause = null;
        this.orderByStatement = null;
        final ArrayList tableList = this.tableExp.getTableClauseList();
        if (tableList != null) {
            for (int i = 0; i < tableList.size(); ++i) {
                if (tableList.get(i) instanceof TableClause) {
                    tableList.get(i).setAlias("");
                }
            }
        }
        return this.toString();
    }
    
    @Override
    public String toSybaseString() throws ConvertException {
        this.withString = null;
        this.isolationLevel = null;
        this.deleteClause.toSybase();
        this.tableExp.toSybase();
        if (this.hintClause != null) {
            this.hintClause.toSQLServer();
        }
        if (this.whereExpression != null) {
            this.whereExpression.setObjectContext(this.objectContext);
            this.whereExpression = this.whereExpression.toSybaseSelect(null, null);
        }
        this.returningClause = null;
        this.deleteLimitClause = null;
        this.orderByStatement = null;
        return this.toString();
    }
    
    @Override
    public String toDB2String() throws ConvertException {
        this.deleteClause.toDB2();
        this.tableExp.toDB2();
        if (this.hintClause != null) {
            this.hintClause = null;
        }
        if (this.whereExpression != null) {
            final ArrayList tableNamesList = new ArrayList();
            if (this.tableExp != null) {
                final ArrayList tableClauseList = this.tableExp.getTableClauseList();
                for (int i = 0; i < tableClauseList.size(); ++i) {
                    if (tableClauseList.get(i) != null && tableClauseList.get(i) instanceof TableClause) {
                        final TableObject tableObject = tableClauseList.get(i).getTableObject();
                        tableNamesList.add(tableObject.getTableName());
                    }
                }
                this.whereExpression.setFromTableList(tableNamesList);
            }
            final WhereExpression we = new WhereExpression();
            final WhereItem wi = new WhereItem();
            final WhereColumn wc1 = new WhereColumn();
            final WhereColumn wc2 = new WhereColumn();
            final Vector whereColVector1 = new Vector();
            final Vector whereColVector2 = new Vector();
            final Vector whereItemVector = new Vector();
            final RownumClause rc = this.whereExpression.getRownumClause();
            if (rc != null) {
                whereColVector1.add("ROWNUM");
                wc1.setColumnExpression(whereColVector1);
                wi.setLeftWhereExp(wc1);
                wi.setOperator(rc.getOperator());
                whereColVector2.add(rc.getRownumValue());
                wc2.setColumnExpression(whereColVector2);
                wi.setRightWhereExp(wc2);
                whereItemVector.add(wi);
                we.setWhereItem(whereItemVector);
            }
            this.whereExpression = this.whereExpression.toDB2Select(null, null);
            if (rc != null) {
                final SelectQueryStatement sqs = new SelectQueryStatement();
                final SelectStatement ss = new SelectStatement();
                final Vector selectListVector = new Vector();
                final SelectColumn sc1 = new SelectColumn();
                final Vector selectColumnVector1 = new Vector();
                final SelectColumn sc2 = new SelectColumn();
                final Vector selectColumnVector2 = new Vector();
                final FromTable ft = new FromTable();
                final FromClause fc = new FromClause();
                final Vector fromClauseVector = new Vector();
                final TableExpression tabExpr = new TableExpression();
                selectColumnVector1.add("ROW_NUMBER() OVER()");
                sc1.setColumnExpression(selectColumnVector1);
                sc1.setIsAS("AS");
                sc1.setAliasName("ROWNUM");
                sc1.setEndsWith(",");
                selectListVector.add(sc1);
                if (tableNamesList.size() > 0) {
                    selectColumnVector2.add(tableNamesList.get(0) + ".*");
                    sc2.setColumnExpression(selectColumnVector2);
                    selectListVector.add(sc2);
                    ft.setTableName(tableNamesList.get(0));
                    fromClauseVector.add(ft);
                    fc.setFromClause("FROM");
                    fc.setFromItemList(fromClauseVector);
                    sqs.setFromClause(fc);
                }
                ss.setSelectItemList(selectListVector);
                ss.setSelectClause("SELECT");
                sqs.setSelectStatement(ss);
                sqs.setWhereExpression(this.whereExpression);
                tabExpr.setSubQuery(sqs);
                this.setTableExpression(tabExpr);
                this.setWhereClause(we);
            }
        }
        if (this.fromClause != null) {
            try {
                this.fromClause.convertToSubQuery(this, 3, this.fromClause);
            }
            catch (final ParseException pe) {
                throw new ConvertException("conversion failure");
            }
        }
        this.optionalHintClause = null;
        this.returningClause = null;
        this.deleteLimitClause = null;
        this.orderByStatement = null;
        return this.toString();
    }
    
    private boolean isNumber(final String str) {
        try {
            final int i = Integer.parseInt(str);
            return true;
        }
        catch (final NumberFormatException nf) {
            System.out.println("Limit Dimension is not an Integer  : " + nf.getMessage());
            return false;
        }
    }
    
    @Override
    public String toMySQLString() throws ConvertException {
        this.withString = null;
        this.isolationLevel = null;
        final DeleteClause dd = this.deleteClause;
        this.deleteClause.toMySQL();
        if (this.tableExp != null && this.tableExp.getTableClauseList() != null && this.tableExp.getTableClauseList().get(0) instanceof TableClause) {
            final TableClause tc = this.tableExp.getTableClauseList().get(0);
            final TableObject tb = tc.getTableObject();
            if (tb.getTableName().equalsIgnoreCase("TOP") && tc.getAlias() != null && this.isNumber(tc.getAlias())) {
                final DeleteLimitClause dLimitClause = new DeleteLimitClause();
                dLimitClause.setLimit("LIMIT");
                dLimitClause.setDimension(tc.getAlias());
                this.setDeleteLimitClause(dLimitClause);
                this.tableExp.getTableClauseList().set(0, " ");
            }
        }
        if (this.tableExp != null) {
            if (this.tableExp.getTableClauseList() != null && this.tableExp.getTableClauseList().size() == 1) {
                this.convertAliasNameToTableName();
            }
            this.tableExp.toMySQL();
            final ArrayList tables = this.tableExp.getTableClauseList();
            if (tables != null && tables.size() > 1 && this.deleteClause.getOptionalSpecifier() != null && this.deleteClause.getOptionalSpecifier().getFrom() == null) {
                this.deleteClause.setOptionalSpecifier(null);
            }
        }
        if (this.fromClause != null) {
            if (this.deleteClause.getOptionalSpecifier() != null && this.deleteClause.getOptionalSpecifier().getFrom() != null) {
                this.deleteClause.getOptionalSpecifier().setFrom(null);
            }
            this.fromClause.toMySQLSelect(null, null);
        }
        this.convertRowNumClauseToDeleteLimitClause();
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
        this.deleteClause.toPostgreSQL();
        this.convertAliasNameToTableName();
        this.tableExp.toPostgreSQL();
        if (this.whereExpression != null) {
            this.whereExpression = this.whereExpression.toPostgreSQLSelect(null, null);
        }
        this.optionalHintClause = null;
        this.returningClause = null;
        this.deleteLimitClause = null;
        this.orderByStatement = null;
        return this.toString();
    }
    
    private void setDQSForWhereColumn(final WhereExpression we) {
        final Vector wis = we.getWhereItems();
        if (wis != null) {
            for (int i = 0; i < wis.size(); ++i) {
                final Object obj = wis.get(i);
                if (obj instanceof WhereItem) {
                    final WhereItem wi = (WhereItem)obj;
                    final WhereColumn lwc = wi.getLeftWhereExp();
                    if (lwc != null) {
                        lwc.setFromDQS(this);
                    }
                    final WhereColumn rwc = wi.getRightWhereExp();
                    if (rwc != null) {
                        rwc.setFromDQS(this);
                    }
                }
            }
        }
    }
    
    private String singleQueryIntoMultipleQueriesForPLSQL() {
        return SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
    }
    
    private void convertRowNumClauseToDeleteLimitClause() throws ConvertException {
        RownumClause rownumClause = null;
        if (this.whereExpression != null) {
            rownumClause = this.whereExpression.getRownumClause();
        }
        if (rownumClause != null) {
            final DeleteLimitClause dlimitClause = new DeleteLimitClause();
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
            dlimitClause.setLimit("LIMIT");
            if (rownumClause.getOperator().equals("<=")) {
                dlimitClause.setDimension(rownumValue);
            }
            else {
                dlimitClause.setDimension(Integer.parseInt(rownumValue) - 1 + "");
            }
            if (this.getLimitClause() != null) {
                throw new ConvertException();
            }
            this.setDeleteLimitClause(dlimitClause);
            this.whereExpression.setRownumClause(null);
        }
    }
    
    @Override
    public String toANSIString() throws ConvertException {
        this.withString = null;
        this.isolationLevel = null;
        this.deleteClause.toANSISQL();
        this.tableExp.toANSISQL();
        if (this.whereExpression != null) {
            this.whereExpression = this.whereExpression.toANSISelect(null, null);
        }
        if (this.fromClause != null) {
            try {
                this.fromClause.convertToSubQuery(this, 8, this.fromClause);
            }
            catch (final ParseException pe) {
                throw new ConvertException("conversion failure");
            }
        }
        this.optionalHintClause = null;
        this.returningClause = null;
        this.deleteLimitClause = null;
        this.orderByStatement = null;
        return this.toString();
    }
    
    @Override
    public String toTeradataString() throws ConvertException {
        this.withString = null;
        this.isolationLevel = null;
        if (this.commentObject != null) {
            this.commentObject.setSQLDialect(12);
        }
        this.deleteClause.toTeradata();
        this.tableExp.toTeradata();
        if (this.whereExpression != null) {
            this.whereExpression = this.whereExpression.toTeradataSelect(null, null);
        }
        if (this.fromClause != null) {
            try {
                this.fromClause.convertToSubQuery(this, 12, this.fromClause);
            }
            catch (final ParseException pe) {
                throw new ConvertException("conversion failure");
            }
        }
        this.optionalHintClause = null;
        this.returningClause = null;
        this.deleteLimitClause = null;
        this.orderByStatement = null;
        return this.toString();
    }
    
    @Override
    public String toTimesTenString() throws ConvertException {
        this.withString = null;
        this.isolationLevel = null;
        final StringBuffer stringbuffer = new StringBuffer();
        this.deleteClause.toTimesTen();
        this.tableExp.toTimesTen();
        if (this.whereExpression != null) {
            this.setDQSForWhereColumn(this.whereExpression);
            this.whereExpression = this.whereExpression.toTimesTenSelect(null, null);
        }
        if (this.fromClause != null) {
            final WhereItem wi = new WhereItem();
            wi.setOperator("EXISTS");
            final SelectQueryStatement sqs = new SelectQueryStatement();
            final SelectStatement ss = new SelectStatement();
            ss.setSelectClause("SELECT");
            final Vector sItems = new Vector();
            final SelectColumn sc = new SelectColumn();
            final Vector colExpr = new Vector();
            colExpr.add("1");
            sc.setColumnExpression(colExpr);
            sItems.add(sc);
            ss.setSelectItemList(sItems);
            sqs.setSelectStatement(ss);
            sqs.setFromClause(this.getClonedFromClause(this.fromClause).toTimesTenSelect(null, null));
            sqs.setWhereExpression(this.getClonedWhereExpression(this.whereExpression).toTimesTenSelect(null, null));
            wi.setRightWhereSubQuery(sqs);
            final Vector wItems = new Vector();
            wItems.add(wi);
            (this.whereExpression = new WhereExpression()).setWhereItem(wItems);
            this.fromClause = null;
        }
        this.optionalHintClause = null;
        this.deleteLimitClause = null;
        this.orderByStatement = null;
        this.returningClause = null;
        return this.toString();
    }
    
    @Override
    public String toNetezzaString() throws ConvertException {
        this.withString = null;
        this.isolationLevel = null;
        this.deleteClause.toNetezza();
        final SelectQueryStatement tempSubQuery = this.tableExp.getSubQuery();
        if (tempSubQuery != null && tempSubQuery.getWhereExpression() != null) {
            if (this.whereExpression != null) {
                this.whereExpression.addWhereExpression(tempSubQuery.getWhereExpression());
                this.whereExpression.addOperator("AND");
            }
            else {
                this.whereExpression = tempSubQuery.getWhereExpression();
            }
        }
        this.tableExp.toNetezza();
        if (this.whereExpression != null) {
            this.whereExpression = this.whereExpression.toNetezzaSelect(null, null);
        }
        if (this.fromClause != null) {
            try {
                this.fromClause.convertToSubQuery(this, 11, this.fromClause);
            }
            catch (final ParseException pe) {
                throw new ConvertException("conversion failure");
            }
        }
        this.optionalHintClause = null;
        this.returningClause = null;
        this.deleteLimitClause = null;
        this.orderByStatement = null;
        return this.toString();
    }
    
    private WhereExpression getClonedWhereExpression(final WhereExpression whereExpression) {
        final WhereExpression clonedWhereExpression = new WhereExpression();
        final Vector whereItemList = new Vector();
        final Vector clonedWhereItems = new Vector();
        clonedWhereExpression.setOperator((Vector)whereExpression.getOperator().clone());
        final Vector whereItems = whereExpression.getWhereItems();
        for (int i = 0; i < whereItems.size(); ++i) {
            if (whereItems.elementAt(i) instanceof WhereItem) {
                final WhereItem whereItem = (WhereItem)whereItems.elementAt(i).clone();
                whereItemList.addElement(whereItem);
            }
            else if (whereItems.elementAt(i) instanceof WhereExpression) {
                whereItemList.addElement(this.getClonedWhereExpression(whereItems.elementAt(i)));
            }
        }
        clonedWhereExpression.setWhereItem(whereItemList);
        return clonedWhereExpression;
    }
    
    public FromClause getClonedFromClause(final FromClause fc) {
        final FromClause clonedFC = new FromClause();
        clonedFC.setFromClause("FROM");
        final Vector clonedFromItems = new Vector();
        final Vector fromItems = fc.getFromItemList();
        for (int i = 0; i < fromItems.size(); ++i) {
            final FromTable ft = (FromTable)fromItems.get(i).clone();
            clonedFromItems.add(ft);
        }
        clonedFC.setFromItemList(clonedFromItems);
        return clonedFC;
    }
    
    public void convertAliasNameToTableName() throws ConvertException {
        final ArrayList al_tcl = this.tableExp.getTableClauseList();
        if (al_tcl.get(0) instanceof TableClause) {
            final TableClause tc = al_tcl.get(0);
            final String s_an = tc.getAlias();
            final String s_tn = tc.getTableObject().toString();
            if (this.whereExpression != null) {
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
                final SelectQueryStatement sqs = wi.getRightWhereSubQuery();
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
                                this.changeFunctionCallArgs(v_lce.elementAt(i_icount), s_an, s_tn);
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
                            else if (v_lce.elementAt(i_icount) instanceof FunctionCalls) {
                                this.changeFunctionCallArgs(v_lce.elementAt(i_icount), s_an, s_tn);
                            }
                        }
                    }
                }
                if (sqs != null) {
                    final FromClause fc = sqs.getFromClause();
                    if (fc != null) {
                        final Vector fromItems = fc.getFromItemList();
                        if (fromItems != null) {
                            int j;
                            for (j = 0; j < fromItems.size(); ++j) {
                                final Object obj = fromItems.get(j);
                                if (obj instanceof FromTable) {
                                    final FromTable ft = (FromTable)obj;
                                    final String alias = ft.getAliasName();
                                    final Object fromTable = ft.getTableName();
                                    if (alias != null && alias.equalsIgnoreCase(s_an)) {
                                        break;
                                    }
                                    if (fromTable instanceof String && ((String)fromTable).equalsIgnoreCase(s_an)) {
                                        break;
                                    }
                                }
                            }
                            if (j != fromItems.size()) {
                                continue;
                            }
                        }
                    }
                    final WhereExpression subWE = sqs.getWhereExpression();
                    if (subWE != null) {
                        this.changeWhereColumn(subWE, s_an, s_tn);
                    }
                }
            }
            else if (v_wi.elementAt(i_count) instanceof WhereExpression) {
                this.changeWhereColumn(v_wi.elementAt(i_count), s_an, s_tn);
            }
        }
    }
    
    private void changeFunctionCallArgs(final FunctionCalls fnObj, final String s_an, final String s_tn) {
        final Vector fnArgs = fnObj.getFunctionArguments();
        if (fnArgs != null) {
            for (int k = 0; k < fnArgs.size(); ++k) {
                if (fnArgs.get(k) instanceof SelectColumn) {
                    final SelectColumn sc = fnArgs.get(k);
                    final Vector colExpr = sc.getColumnExpression();
                    if (colExpr != null) {
                        for (int n = 0; n < colExpr.size(); ++n) {
                            final Object tcObj = colExpr.get(n);
                            if (tcObj instanceof TableColumn) {
                                final TableColumn tc = (TableColumn)tcObj;
                                final String tablename = tc.getTableName();
                                if (tablename != null && tablename.equalsIgnoreCase(s_an)) {
                                    tc.setTableName(s_tn);
                                }
                            }
                        }
                    }
                }
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
