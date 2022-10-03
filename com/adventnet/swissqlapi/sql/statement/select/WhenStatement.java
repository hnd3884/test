package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.UserObjectContext;

public class WhenStatement
{
    private String WhenClause;
    private WhereExpression WhenCondition;
    private String ThenClause;
    private SelectColumn ThenStatement;
    private UserObjectContext context;
    private CommentClass commentObj;
    
    public WhenStatement() {
        this.context = null;
        this.WhenClause = null;
        this.WhenCondition = null;
        this.ThenClause = null;
        this.ThenStatement = null;
    }
    
    public void setWhenClause(final String s_when_clause) {
        this.WhenClause = s_when_clause;
    }
    
    public void setObjectContext(final UserObjectContext obj) {
        this.context = obj;
    }
    
    public void setWhenCondition(final WhereExpression we_when_condition) {
        this.WhenCondition = we_when_condition;
    }
    
    public void setThenClause(final String s_then_clause) {
        this.ThenClause = s_then_clause;
    }
    
    public void setThenStatement(final SelectColumn sc_then_statement) {
        this.ThenStatement = sc_then_statement;
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public String getWhenClause() {
        return this.WhenClause;
    }
    
    public WhereExpression getWhenCondition() {
        return this.WhenCondition;
    }
    
    public String getThenClause() {
        return this.ThenClause;
    }
    
    public SelectColumn getThenStatement() {
        return this.ThenStatement;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public WhenStatement toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhenStatement when_statement = new WhenStatement();
        when_statement.setWhenClause(this.WhenClause);
        when_statement.setWhenCondition(this.WhenCondition.toOracleSelect(to_sqs, from_sqs));
        when_statement.setThenClause(this.ThenClause);
        when_statement.setThenStatement(this.ThenStatement.toOracleSelect(to_sqs, from_sqs));
        return when_statement;
    }
    
    public WhenStatement toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhenStatement when_statement = new WhenStatement();
        when_statement.setWhenClause(this.WhenClause);
        when_statement.setWhenCondition(this.WhenCondition.toInformixSelect(to_sqs, from_sqs));
        when_statement.setThenClause(this.ThenClause);
        when_statement.setThenStatement(this.ThenStatement.toInformixSelect(to_sqs, from_sqs));
        return when_statement;
    }
    
    public WhenStatement toDB2Select(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhenStatement when_statement = new WhenStatement();
        when_statement.setWhenClause(this.WhenClause);
        when_statement.setWhenCondition(this.WhenCondition.toDB2Select(to_sqs, from_sqs));
        when_statement.setThenClause(this.ThenClause);
        when_statement.setThenStatement(this.ThenStatement.toDB2Select(to_sqs, from_sqs));
        return when_statement;
    }
    
    public WhenStatement toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhenStatement when_statement = new WhenStatement();
        when_statement.setWhenClause(this.WhenClause);
        when_statement.setWhenCondition(this.WhenCondition.toMSSQLServerSelect(to_sqs, from_sqs));
        when_statement.setThenClause(this.ThenClause);
        when_statement.setThenStatement(this.ThenStatement.toMSSQLServerSelect(to_sqs, from_sqs));
        return when_statement;
    }
    
    public WhenStatement toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhenStatement when_statement = new WhenStatement();
        when_statement.setWhenClause(this.WhenClause);
        when_statement.setWhenCondition(this.WhenCondition.toSybaseSelect(to_sqs, from_sqs));
        when_statement.setThenClause(this.ThenClause);
        when_statement.setThenStatement(this.ThenStatement.toSybaseSelect(to_sqs, from_sqs));
        return when_statement;
    }
    
    public WhenStatement toPostgreSQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhenStatement when_statement = new WhenStatement();
        when_statement.setWhenClause(this.WhenClause);
        when_statement.setWhenCondition(this.WhenCondition.toPostgreSQLSelect(to_sqs, from_sqs));
        when_statement.setThenClause(this.ThenClause);
        when_statement.setThenStatement(this.ThenStatement.toPostgreSQLSelect(to_sqs, from_sqs));
        when_statement.setObjectContext(this.context);
        return when_statement;
    }
    
    public WhenStatement toMySQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhenStatement when_statement = new WhenStatement();
        when_statement.setWhenClause(this.WhenClause);
        when_statement.setWhenCondition(this.WhenCondition.toMySQLSelect(to_sqs, from_sqs));
        when_statement.setThenClause(this.ThenClause);
        when_statement.setThenStatement(this.ThenStatement.toMySQLSelect(to_sqs, from_sqs));
        return when_statement;
    }
    
    public WhenStatement toANSISelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhenStatement when_statement = new WhenStatement();
        when_statement.setWhenClause(this.WhenClause);
        when_statement.setWhenCondition(this.WhenCondition.toANSISelect(to_sqs, from_sqs));
        when_statement.setThenClause(this.ThenClause);
        when_statement.setThenStatement(this.ThenStatement.toANSISelect(to_sqs, from_sqs));
        return when_statement;
    }
    
    public WhenStatement toTeradataSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhenStatement when_statement = new WhenStatement();
        when_statement.setWhenClause(this.WhenClause);
        this.WhenCondition.setCaseExpressionBool(true);
        final WhereExpression whenExp = this.WhenCondition.toTeradataSelect(to_sqs, from_sqs);
        if (this.WhenCondition.getRownumClause() != null) {
            whenExp.setRownumClause(this.WhenCondition.getRownumClause());
        }
        when_statement.setWhenCondition(whenExp);
        when_statement.setThenClause(this.ThenClause);
        when_statement.setThenStatement(this.ThenStatement.toTeradataSelect(to_sqs, from_sqs));
        return when_statement;
    }
    
    public WhenStatement toNetezzaSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhenStatement when_statement = new WhenStatement();
        when_statement.setWhenClause(this.WhenClause);
        when_statement.setWhenCondition(this.WhenCondition.toNetezzaSelect(to_sqs, from_sqs));
        when_statement.setThenClause(this.ThenClause);
        when_statement.setThenStatement(this.ThenStatement.toNetezzaSelect(to_sqs, from_sqs));
        return when_statement;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.commentObj != null) {
            sb.append(this.commentObj.toString().trim() + " ");
        }
        sb.append(this.WhenClause);
        this.WhenCondition.setObjectContext(this.context);
        sb.append(" " + this.WhenCondition.toString());
        sb.append(" " + this.ThenClause);
        this.ThenStatement.setObjectContext(this.context);
        sb.append(" " + this.ThenStatement.toString());
        return sb.toString();
    }
    
    public WhenStatement toVectorWiseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhenStatement when_statement = new WhenStatement();
        when_statement.setWhenClause(this.WhenClause);
        when_statement.setWhenCondition(this.WhenCondition.toVectorWiseSelect(to_sqs, from_sqs));
        when_statement.setThenClause(this.ThenClause);
        when_statement.setThenStatement(this.ThenStatement.toVectorWiseSelect(to_sqs, from_sqs));
        return when_statement;
    }
}
