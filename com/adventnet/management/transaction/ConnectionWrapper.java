package com.adventnet.management.transaction;

import java.util.LinkedList;
import java.util.Vector;
import java.sql.Connection;

class ConnectionWrapper
{
    Connection con;
    Vector psList;
    Vector statementList;
    LinkedList psCache;
    int con_num;
    boolean isTimedOut;
    private int nestingLevel;
    int STAT_OBJECTS;
    int counter;
    
    ConnectionWrapper() {
        this.isTimedOut = false;
        this.nestingLevel = 0;
        this.STAT_OBJECTS = 0;
        this.counter = 0;
    }
    
    public int getNestingLevel() {
        return this.nestingLevel;
    }
    
    public void updateNestingLevel(final boolean b) {
        if (b) {
            ++this.nestingLevel;
        }
        else {
            --this.nestingLevel;
        }
    }
    
    public void resetNestingLevel() {
        this.nestingLevel = 0;
    }
    
    public Vector getStatementList() {
        return this.statementList;
    }
    
    public void setStatementList(final Vector statementList) {
        this.statementList = statementList;
    }
    
    public synchronized PreparedStatementWrapper getStatement() {
        if (this.counter < this.STAT_OBJECTS - 1) {
            ++this.counter;
        }
        else {
            this.counter = 0;
        }
        return this.statementList.elementAt(this.counter);
    }
    
    public void setStatementCacheSize(final int stat_OBJECTS) {
        this.STAT_OBJECTS = stat_OBJECTS;
    }
}
