package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.Vector;

public class HavingStatement
{
    private String HavingClause;
    private Vector HavingItems;
    private UserObjectContext context;
    private CommentClass commentObj;
    
    public HavingStatement() {
        this.context = null;
    }
    
    public void setHavingClause(final String s_hc) {
        this.HavingClause = s_hc;
    }
    
    public void addHavingItems(final Object o_hi) {
        this.HavingItems.addElement(o_hi);
    }
    
    public void setObjectContext(final UserObjectContext obj) {
        this.context = obj;
    }
    
    public void setHavingItems(final Vector v_hi) {
        this.HavingItems = v_hi;
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public String getHavingClause() {
        return this.HavingClause;
    }
    
    public Vector getHavingItems() {
        return this.HavingItems;
    }
    
    public HavingStatement toANSISelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final int ansi = 6;
        HavingStatement hs = new HavingStatement();
        final Vector v_hi = new Vector();
        if (from_sqs.getGroupByStatement() == null) {
            for (int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
                if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
                    final WhereExpression we = this.HavingItems.elementAt(i_count);
                    this.processWhereExpressionInHaving(to_sqs, we, ansi);
                    if (to_sqs.getWhereExpression() != null) {
                        final WhereExpression to_SQSWhereExp = to_sqs.getWhereExpression();
                        to_SQSWhereExp.addWhereExpression(we);
                        to_SQSWhereExp.addOperator("AND");
                    }
                    else {
                        to_sqs.setWhereExpression(we);
                    }
                }
                hs = null;
            }
        }
        else {
            hs.setHavingClause(this.HavingClause);
            for (int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
                if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
                    v_hi.addElement(this.HavingItems.elementAt(i_count).toANSISelect(to_sqs, from_sqs));
                }
            }
            hs.setHavingItems(v_hi);
        }
        return hs;
    }
    
    public HavingStatement toTeradataSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final int ansi = 6;
        HavingStatement hs = new HavingStatement();
        final Vector v_hi = new Vector();
        if (from_sqs.getGroupByStatement() == null && this.HavingClause != null && this.HavingClause.trim().equalsIgnoreCase("having")) {
            for (int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
                if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
                    final WhereExpression we = this.HavingItems.elementAt(i_count);
                    this.processWhereExpressionInHaving(to_sqs, we, ansi);
                    if (to_sqs.getWhereExpression() != null) {
                        final WhereExpression to_SQSWhereExp = to_sqs.getWhereExpression();
                        to_SQSWhereExp.addWhereExpression(we);
                        to_SQSWhereExp.addOperator("AND");
                    }
                    else {
                        to_sqs.setWhereExpression(we);
                    }
                }
                hs = null;
            }
        }
        else if (this.HavingClause != null && this.HavingClause.trim().equalsIgnoreCase("QUALIFY")) {
            hs.setHavingClause(this.HavingClause);
            for (int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
                if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
                    v_hi.addElement(this.HavingItems.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs));
                }
            }
            hs.setHavingItems(v_hi);
        }
        else {
            hs.setHavingClause(this.HavingClause);
            for (int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
                if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
                    v_hi.addElement(this.HavingItems.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs));
                }
            }
            hs.setHavingItems(v_hi);
        }
        return hs;
    }
    
    public HavingStatement toDB2Select(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        HavingStatement hs = new HavingStatement();
        final Vector v_hi = new Vector();
        final int db2 = 2;
        if (from_sqs.getGroupByStatement() == null) {
            for (int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
                if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
                    final WhereExpression we = this.HavingItems.elementAt(i_count);
                    this.processWhereExpressionInHaving(to_sqs, we, db2);
                    if (to_sqs.getWhereExpression() != null) {
                        final WhereExpression to_SQSWhereExp = to_sqs.getWhereExpression();
                        to_SQSWhereExp.addWhereExpression(we);
                        to_SQSWhereExp.addOperator("AND");
                    }
                    else {
                        to_sqs.setWhereExpression(we);
                    }
                }
                hs = null;
            }
        }
        else {
            hs.setHavingClause(this.HavingClause);
            for (int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
                if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
                    v_hi.addElement(this.HavingItems.elementAt(i_count).toDB2Select(to_sqs, from_sqs));
                }
            }
            hs.setHavingItems(v_hi);
        }
        return hs;
    }
    
    public HavingStatement toMySQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final HavingStatement hs = new HavingStatement();
        final Vector v_hi = new Vector();
        hs.setHavingClause(this.HavingClause);
        for (int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
                v_hi.addElement(this.HavingItems.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
        }
        hs.setHavingItems(v_hi);
        return hs;
    }
    
    public HavingStatement toPostgreSQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final HavingStatement hs = new HavingStatement();
        final Vector v_hi = new Vector();
        final int postgreSQL = 4;
        hs.setHavingClause(this.HavingClause);
        for (int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
                v_hi.addElement(this.HavingItems.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
        }
        hs.setHavingItems(v_hi);
        return hs;
    }
    
    public HavingStatement toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        HavingStatement hs = new HavingStatement();
        final Vector v_hi = new Vector();
        final int msSQLServer = 1;
        if (from_sqs.getGroupByStatement() == null) {
            for (int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
                if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
                    WhereExpression we = this.HavingItems.elementAt(i_count);
                    this.processWhereExpressionInHaving(to_sqs, we, msSQLServer);
                    if (to_sqs.getWhereExpression() != null) {
                        final WhereExpression to_SQSWhereExp = to_sqs.getWhereExpression();
                        we = we.toMSSQLServerSelect(to_sqs, from_sqs);
                        to_SQSWhereExp.addWhereExpression(we);
                        to_SQSWhereExp.addOperator("AND");
                    }
                    else {
                        to_sqs.setWhereExpression(we);
                    }
                }
                hs = null;
            }
        }
        else {
            hs.setHavingClause(this.HavingClause);
            for (int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
                if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
                    v_hi.addElement(this.HavingItems.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
                }
            }
            hs.setHavingItems(v_hi);
        }
        return hs;
    }
    
    public HavingStatement toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final HavingStatement hs = new HavingStatement();
        final Vector v_hi = new Vector();
        hs.setHavingClause(this.HavingClause);
        for (int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
                v_hi.addElement(this.HavingItems.elementAt(i_count).toSybaseSelect(to_sqs, from_sqs));
            }
        }
        hs.setHavingItems(v_hi);
        return hs;
    }
    
    public HavingStatement toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        HavingStatement hs = new HavingStatement();
        hs.setCommentClass(this.commentObj);
        final Vector v_hi = new Vector();
        final int oracle = 0;
        if (from_sqs.getGroupByStatement() == null) {
            for (int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
                if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
                    final WhereExpression we = this.HavingItems.elementAt(i_count);
                    this.processWhereExpressionInHaving(to_sqs, we, oracle);
                    if (to_sqs.getWhereExpression() != null) {
                        final WhereExpression to_SQSWhereExp = to_sqs.getWhereExpression();
                        to_SQSWhereExp.addWhereExpression(we);
                        to_SQSWhereExp.addOperator("AND");
                    }
                    else {
                        to_sqs.setWhereExpression(we);
                    }
                }
                hs = null;
            }
        }
        else {
            hs.setHavingClause(this.HavingClause);
            for (int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
                if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
                    v_hi.addElement(this.HavingItems.elementAt(i_count).toOracleSelect(to_sqs, from_sqs));
                }
            }
            hs.setHavingItems(v_hi);
        }
        return hs;
    }
    
    public HavingStatement toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        HavingStatement hs = new HavingStatement();
        final Vector v_hi = new Vector();
        final int informix = 3;
        if (from_sqs.getGroupByStatement() == null) {
            for (int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
                if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
                    final WhereExpression we = this.HavingItems.elementAt(i_count);
                    this.processWhereExpressionInHaving(to_sqs, we, informix);
                    if (to_sqs.getWhereExpression() != null) {
                        final WhereExpression to_SQSWhereExp = to_sqs.getWhereExpression();
                        to_SQSWhereExp.addWhereExpression(we);
                        to_SQSWhereExp.addOperator("AND");
                    }
                    else {
                        to_sqs.setWhereExpression(we);
                    }
                }
                hs = null;
            }
        }
        else {
            hs.setHavingClause(this.HavingClause);
            for (int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
                if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
                    v_hi.addElement(this.HavingItems.elementAt(i_count).toInformixSelect(to_sqs, from_sqs));
                }
            }
            hs.setHavingItems(v_hi);
        }
        return hs;
    }
    
    public HavingStatement toTimesTenSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        HavingStatement hs = new HavingStatement();
        final Vector v_hi = new Vector();
        if (from_sqs.getGroupByStatement() == null) {
            for (int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
                if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
                    final WhereExpression we = this.HavingItems.elementAt(i_count);
                    this.processWhereExpressionInHaving(to_sqs, we, 10);
                    if (to_sqs.getWhereExpression() != null) {
                        final WhereExpression to_SQSWhereExp = to_sqs.getWhereExpression();
                        to_SQSWhereExp.addWhereExpression(we);
                        to_SQSWhereExp.addOperator("AND");
                    }
                    else {
                        to_sqs.setWhereExpression(we);
                    }
                }
                hs = null;
            }
        }
        else {
            hs.setHavingClause(this.HavingClause);
            for (int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
                if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
                    v_hi.addElement(this.HavingItems.elementAt(i_count).toTimesTenSelect(to_sqs, from_sqs));
                }
            }
            hs.setHavingItems(v_hi);
        }
        return hs;
    }
    
    public HavingStatement toNetezzaSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final int ansi = 6;
        HavingStatement hs = new HavingStatement();
        final Vector v_hi = new Vector();
        if (from_sqs.getGroupByStatement() == null) {
            for (int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
                if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
                    final WhereExpression we = this.HavingItems.elementAt(i_count);
                    this.processWhereExpressionInHaving(to_sqs, we, ansi);
                    if (to_sqs.getWhereExpression() != null) {
                        final WhereExpression to_SQSWhereExp = to_sqs.getWhereExpression();
                        to_SQSWhereExp.addWhereExpression(we);
                        to_SQSWhereExp.addOperator("AND");
                    }
                    else {
                        to_sqs.setWhereExpression(we);
                    }
                }
                hs = null;
            }
        }
        else {
            hs.setHavingClause(this.HavingClause);
            for (int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
                if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
                    v_hi.addElement(this.HavingItems.elementAt(i_count).toNetezzaSelect(to_sqs, from_sqs));
                }
            }
            hs.setHavingItems(v_hi);
        }
        return hs;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.commentObj != null) {
            sb.append(this.commentObj.toString().trim() + " ");
        }
        sb.append(this.HavingClause.toUpperCase() + " ");
        for (int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
                this.HavingItems.elementAt(i_count).setObjectContext(this.context);
            }
            else if (this.HavingItems.elementAt(i_count) instanceof SelectQueryStatement) {
                this.HavingItems.elementAt(i_count).setObjectContext(this.context);
            }
            else if (this.HavingItems.elementAt(i_count) instanceof SelectColumn) {
                this.HavingItems.elementAt(i_count).setObjectContext(this.context);
            }
            else if (this.HavingItems.elementAt(i_count) instanceof FunctionCalls) {
                this.HavingItems.elementAt(i_count).setObjectContext(this.context);
            }
            sb.append(this.HavingItems.elementAt(i_count).toString() + " ");
        }
        return sb.toString();
    }
    
    public void convertAggregateFunctionIntoSubQuery(final Vector columnExpression, final SelectQueryStatement toSQS, final int database) throws ConvertException {
        for (int i = 0; i < columnExpression.size(); ++i) {
            if (columnExpression.elementAt(i) instanceof FunctionCalls) {
                final FunctionCalls fnCall = columnExpression.elementAt(i);
                final String functionName = fnCall.getFunctionNameAsAString();
                if (functionName != null) {
                    if (functionName.equalsIgnoreCase("avg") || functionName.equalsIgnoreCase("count") || functionName.equalsIgnoreCase("max") || functionName.equalsIgnoreCase("min") || functionName.equalsIgnoreCase("sum")) {
                        final SelectQueryStatement sqs = new SelectQueryStatement();
                        final SelectStatement ss = new SelectStatement();
                        final Vector selectItems = new Vector();
                        final SelectColumn sc = new SelectColumn();
                        final Vector aggrFunctionExpr = new Vector();
                        sqs.setCloseBrace(")");
                        sqs.setOpenBrace("(");
                        aggrFunctionExpr.add(columnExpression.elementAt(i));
                        sc.setColumnExpression(aggrFunctionExpr);
                        selectItems.add(sc);
                        ss.setSelectClause("SELECT");
                        ss.setSelectItemList(selectItems);
                        sqs.setSelectStatement(ss);
                        if (toSQS != null && toSQS.getFromClause() != null) {
                            final FromClause toFromClause = toSQS.getFromClause();
                            final FromClause fc = new FromClause();
                            fc.setFromClause(toFromClause.getFromClause());
                            final Vector vembuFromItems = toFromClause.getFromItemList();
                            if (vembuFromItems != null) {
                                final Vector newFromItemList = new Vector();
                                for (int j = 0; j < vembuFromItems.size(); ++j) {
                                    if (vembuFromItems.get(j) instanceof FromTable) {
                                        newFromItemList.add(vembuFromItems.get(j));
                                        break;
                                    }
                                }
                                fc.setFromItemList(newFromItemList);
                            }
                            sqs.setFromClause(fc);
                        }
                        columnExpression.set(i, sqs);
                        if (database == 5) {
                            throw new ConvertException("Conversion Failure.. Invalid Query..");
                        }
                    }
                    else if (database == 1) {
                        final Vector fnArgs = fnCall.getFunctionArguments();
                        if (fnArgs != null) {
                            for (int k = 0; k < fnArgs.size(); ++k) {
                                final Object obj = fnArgs.get(k);
                                if (obj instanceof SelectColumn) {
                                    this.convertAggregateFunctionIntoSubQuery(((SelectColumn)obj).getColumnExpression(), toSQS, database);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void processWhereExpressionInHaving(final SelectQueryStatement sqs, final WhereExpression we, final int database) throws ConvertException {
        if (we != null) {
            for (int i = 0; i < we.getWhereItems().size(); ++i) {
                if (we.getWhereItems().get(i) instanceof WhereExpression) {
                    final WhereExpression newWE = we.getWhereItems().get(i);
                    this.processWhereExpressionInHaving(sqs, newWE, database);
                }
                if (we.getWhereItems().get(i) instanceof WhereItem) {
                    final WhereItem wi = we.getWhereItems().get(i);
                    final WhereColumn leftSideCol = wi.getLeftWhereExp();
                    if (leftSideCol != null) {
                        final Vector leftExpr = leftSideCol.getColumnExpression();
                        if (leftExpr != null) {
                            this.convertAggregateFunctionIntoSubQuery(leftExpr, sqs, database);
                        }
                    }
                    final WhereColumn rightSideCol = wi.getRightWhereExp();
                    if (rightSideCol != null) {
                        final Vector rightExpr = rightSideCol.getColumnExpression();
                        if (rightExpr != null) {
                            this.convertAggregateFunctionIntoSubQuery(rightExpr, sqs, database);
                        }
                    }
                }
            }
        }
    }
    
    public HavingStatement toVectorWiseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final HavingStatement hs = new HavingStatement();
        final Vector v_hi = new Vector();
        hs.setHavingClause(this.HavingClause);
        for (int i_count = 0; i_count < this.HavingItems.size(); ++i_count) {
            if (this.HavingItems.elementAt(i_count) instanceof WhereExpression) {
                v_hi.addElement(this.HavingItems.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
            }
        }
        hs.setHavingItems(v_hi);
        return hs;
    }
}
