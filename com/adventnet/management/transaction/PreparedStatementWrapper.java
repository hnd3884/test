package com.adventnet.management.transaction;

import java.sql.Statement;
import java.sql.PreparedStatement;

public class PreparedStatementWrapper
{
    private PreparedStatement prstmt;
    private Statement stmt;
    private boolean status;
    private int id;
    private long timeOfConn;
    private String sqlStat;
    
    public PreparedStatement getPreparedStatement() {
        return this.prstmt;
    }
    
    void setPreparedStatement(final PreparedStatement prstmt) {
        this.prstmt = prstmt;
    }
    
    Statement getStatement() {
        return this.stmt;
    }
    
    void setStatement(final Statement stmt) {
        this.stmt = stmt;
    }
    
    boolean getStatus() {
        return this.status;
    }
    
    void setStatus(final boolean status) {
        this.status = status;
    }
    
    public int getId() {
        return this.id;
    }
    
    void setId(final int id) {
        this.id = id;
    }
    
    long getTimeOfConnection() {
        return this.timeOfConn;
    }
    
    void setTimeOfConnection(final long timeOfConn) {
        this.timeOfConn = timeOfConn;
    }
    
    public String getSqlString() {
        return this.sqlStat;
    }
    
    void setSqlString(final String sqlStat) {
        this.sqlStat = sqlStat;
    }
    
    synchronized boolean setAsUsed(final long n) {
        if (!this.getStatus() || System.currentTimeMillis() - this.getTimeOfConnection() > n) {
            this.setStatus(true);
            return false;
        }
        return true;
    }
}
