package com.adventnet.swissqlapi.sql.statement.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class CommitStatement implements SwisSQLStatement
{
    private String commit;
    private String transaction;
    private String transactionNameVariable;
    private String work;
    private String rollback;
    
    public void setCommit(final String commit) {
        this.commit = commit;
    }
    
    public void setTransaction(final String transaction) {
        this.transaction = transaction;
    }
    
    public void setTransactionNameVariable(final String transactionNameVariable) {
        this.transactionNameVariable = transactionNameVariable;
    }
    
    public void setWork(final String work) {
        this.work = work;
    }
    
    public void setRollback(final String rollback) {
        this.rollback = rollback;
    }
    
    public String getCommit() {
        return this.commit;
    }
    
    public String getTransaction() {
        return this.transaction;
    }
    
    public String getTransactionNameVariable() {
        return this.transactionNameVariable;
    }
    
    public String getWork() {
        return this.work;
    }
    
    public String getRollback() {
        return this.rollback;
    }
    
    @Override
    public CommentClass getCommentClass() {
        return null;
    }
    
    @Override
    public UserObjectContext getObjectContext() {
        return null;
    }
    
    @Override
    public String removeIndent(final String str) {
        return str;
    }
    
    @Override
    public void setCommentClass(final CommentClass commentClass) {
    }
    
    @Override
    public void setObjectContext(final UserObjectContext userObjectContext) {
    }
    
    @Override
    public String toANSIString() throws ConvertException {
        return this.toANSICommit().toString();
    }
    
    @Override
    public String toTeradataString() throws ConvertException {
        return this.toTeradataCommit().toString();
    }
    
    @Override
    public String toDB2String() throws ConvertException {
        return this.toDB2Commit().toString();
    }
    
    @Override
    public String toInformixString() throws ConvertException {
        return this.toInformixCommit().toString();
    }
    
    @Override
    public String toMSSQLServerString() throws ConvertException {
        return this.toSQLServerCommit().toString();
    }
    
    @Override
    public String toMySQLString() throws ConvertException {
        return this.toMysqlCommit().toString();
    }
    
    @Override
    public String toOracleString() throws ConvertException {
        return this.toOracleCommit().toString();
    }
    
    @Override
    public String toPostgreSQLString() throws ConvertException {
        return this.toPostgresCommit().toString();
    }
    
    @Override
    public String toSybaseString() throws ConvertException {
        return this.toSybaseCommit().toString();
    }
    
    @Override
    public String toTimesTenString() throws ConvertException {
        return this.toTimesTenCommit().toString();
    }
    
    @Override
    public String toNetezzaString() throws ConvertException {
        return this.toNetezzaCommit().toString();
    }
    
    public CommitStatement toOracleCommit() throws ConvertException {
        final CommitStatement commitStmt = new CommitStatement();
        if (this.commit != null) {
            commitStmt.setCommit(this.commit);
        }
        commitStmt.setTransaction(null);
        commitStmt.setTransactionNameVariable(null);
        if (this.work != null) {
            commitStmt.setWork(this.work);
        }
        return commitStmt;
    }
    
    public CommitStatement toSQLServerCommit() throws ConvertException {
        final CommitStatement commitStmt = new CommitStatement();
        if (this.commit != null) {
            commitStmt.setCommit(this.commit);
        }
        commitStmt.setTransaction(this.transaction);
        commitStmt.setTransactionNameVariable(this.transactionNameVariable);
        if (this.work != null) {
            commitStmt.setWork(this.work);
        }
        return commitStmt;
    }
    
    public CommitStatement toSybaseCommit() throws ConvertException {
        final CommitStatement commitStmt = new CommitStatement();
        if (this.commit != null) {
            commitStmt.setCommit(this.commit);
        }
        commitStmt.setTransaction(this.transaction);
        commitStmt.setTransactionNameVariable(this.transactionNameVariable);
        if (this.work != null) {
            commitStmt.setWork(this.work);
        }
        return commitStmt;
    }
    
    public CommitStatement toDB2Commit() throws ConvertException {
        final CommitStatement commitStmt = new CommitStatement();
        if (this.commit != null) {
            commitStmt.setCommit(this.commit);
        }
        commitStmt.setTransaction(null);
        commitStmt.setTransactionNameVariable(null);
        if (this.work != null) {
            commitStmt.setWork(this.work);
        }
        return commitStmt;
    }
    
    public CommitStatement toPostgresCommit() throws ConvertException {
        final CommitStatement commitStmt = new CommitStatement();
        if (this.commit != null) {
            commitStmt.setCommit(this.commit);
        }
        commitStmt.setTransaction(null);
        commitStmt.setTransactionNameVariable(null);
        if (this.work != null) {
            commitStmt.setWork(this.work);
        }
        return commitStmt;
    }
    
    public CommitStatement toInformixCommit() throws ConvertException {
        final CommitStatement commitStmt = new CommitStatement();
        if (this.commit != null) {
            commitStmt.setCommit(this.commit);
        }
        commitStmt.setTransaction(null);
        commitStmt.setTransactionNameVariable(null);
        if (this.work != null) {
            commitStmt.setWork(this.work);
        }
        return commitStmt;
    }
    
    public CommitStatement toANSICommit() throws ConvertException {
        final CommitStatement commitStmt = new CommitStatement();
        if (this.commit != null) {
            commitStmt.setCommit(this.commit);
        }
        commitStmt.setTransaction(null);
        commitStmt.setTransactionNameVariable(null);
        if (this.work != null) {
            commitStmt.setWork(this.work);
        }
        return commitStmt;
    }
    
    public CommitStatement toTeradataCommit() throws ConvertException {
        final CommitStatement commitStmt = new CommitStatement();
        if (this.commit != null) {
            commitStmt.setCommit(this.commit);
        }
        commitStmt.setTransaction(null);
        commitStmt.setTransactionNameVariable(null);
        if (this.work != null) {
            commitStmt.setWork(this.work);
        }
        return commitStmt;
    }
    
    public CommitStatement toMysqlCommit() throws ConvertException {
        final CommitStatement commitStmt = new CommitStatement();
        if (this.commit != null) {
            commitStmt.setCommit(this.commit);
        }
        commitStmt.setTransaction(null);
        commitStmt.setTransactionNameVariable(null);
        if (this.work != null) {
            commitStmt.setWork(this.work);
        }
        return commitStmt;
    }
    
    public CommitStatement toTimesTenCommit() throws ConvertException {
        final CommitStatement commitStmt = new CommitStatement();
        if (this.commit != null) {
            commitStmt.setCommit(this.commit);
        }
        commitStmt.setTransaction(this.transaction);
        commitStmt.setTransactionNameVariable(this.transactionNameVariable);
        if (this.work != null) {
            commitStmt.setWork(this.work);
        }
        return commitStmt;
    }
    
    public CommitStatement toNetezzaCommit() throws ConvertException {
        final CommitStatement commitStmt = new CommitStatement();
        if (this.commit != null) {
            commitStmt.setCommit(this.commit);
        }
        if (this.rollback != null) {
            commitStmt.setRollback(this.rollback);
        }
        commitStmt.setTransaction(null);
        commitStmt.setTransactionNameVariable(null);
        if (this.work != null) {
            commitStmt.setWork(this.work);
        }
        return commitStmt;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final String indentString = "\n";
        if (this.commit != null) {
            sb.append(indentString + this.commit.toUpperCase());
        }
        if (this.rollback != null) {
            sb.append(indentString + this.rollback.toUpperCase());
        }
        if (this.transaction != null) {
            sb.append(" " + this.transaction.toUpperCase());
        }
        if (this.transactionNameVariable != null) {
            sb.append(" " + this.transactionNameVariable);
        }
        if (this.work != null) {
            sb.append(" " + this.work.toUpperCase());
        }
        return sb.toString();
    }
    
    @Override
    public String toVectorWiseString() throws ConvertException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
