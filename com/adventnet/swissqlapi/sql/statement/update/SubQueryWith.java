package com.adventnet.swissqlapi.sql.statement.update;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;

public class SubQueryWith
{
    private SelectQueryStatement subQuery;
    private WithClause withClause;
    
    public SubQueryWith() {
        this.subQuery = new SelectQueryStatement();
        this.withClause = new WithClause();
    }
    
    public void setSubQuery(final SelectQueryStatement s) {
        this.subQuery = s;
    }
    
    public void setWithClause(final WithClause wc) {
        this.withClause = wc;
    }
    
    public void toMySQL() throws ConvertException {
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toMySQLSelect();
        }
    }
    
    public void toOracle() throws ConvertException {
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toOracleSelect();
        }
    }
    
    public void toMSSQLServer() throws ConvertException {
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toMSSQLServerSelect();
        }
    }
    
    public void toSybase() throws ConvertException {
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toSybaseSelect();
        }
    }
    
    public void toPostgreSQL() throws ConvertException {
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toPostgreSQLSelect();
        }
    }
    
    public void toDB2() throws ConvertException {
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toDB2Select();
        }
    }
    
    public void toInformix() throws ConvertException {
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toInformixSelect();
        }
    }
    
    public void toANSISQL() throws ConvertException {
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toANSISelect();
        }
    }
    
    public void toTeradata() throws ConvertException {
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toTeradataSelect();
        }
    }
    
    public void toNetezza() throws ConvertException {
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toNetezzaSelect();
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.subQuery != null) {
            sb.append(this.subQuery.toString());
        }
        if (this.withClause != null) {
            sb.append(this.withClause.toString());
        }
        return sb.toString();
    }
}
