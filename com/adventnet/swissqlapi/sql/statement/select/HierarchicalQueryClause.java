package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.exception.ConvertException;

public class HierarchicalQueryClause
{
    private String StartWithClause;
    private WhereExpression StartWithCondition;
    private String ConnectByClause;
    private WhereExpression ConnectByCondition;
    
    public HierarchicalQueryClause() {
        this.StartWithClause = new String();
        this.StartWithCondition = new WhereExpression();
        this.ConnectByClause = new String();
        this.ConnectByCondition = new WhereExpression();
    }
    
    public void setStartWithClause(final String s_swc) {
        this.StartWithClause = s_swc;
    }
    
    public void setStartWithCondition(final WhereExpression we_swc) {
        this.StartWithCondition = we_swc;
    }
    
    public void setConnectByClause(final String s_cbc) {
        this.ConnectByClause = s_cbc;
    }
    
    public void setConnectByCondition(final WhereExpression we_cbc) {
        this.ConnectByCondition = we_cbc;
    }
    
    public String getStartWithClause() {
        return this.StartWithClause;
    }
    
    public WhereExpression getStartWithCondition() {
        return this.StartWithCondition;
    }
    
    public String getConnectByClause() {
        return this.ConnectByClause;
    }
    
    public WhereExpression getConnectByCondition() {
        return this.ConnectByCondition;
    }
    
    public HierarchicalQueryClause toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final HierarchicalQueryClause hqc = new HierarchicalQueryClause();
        hqc.setStartWithClause(this.StartWithClause);
        hqc.setConnectByClause(this.ConnectByClause);
        hqc.setStartWithCondition(this.StartWithCondition.toOracleSelect(to_sqs, from_sqs));
        hqc.setConnectByCondition(this.ConnectByCondition.toOracleSelect(to_sqs, from_sqs));
        return hqc;
    }
    
    public HierarchicalQueryClause toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final HierarchicalQueryClause hqc = new HierarchicalQueryClause();
        hqc.setStartWithClause(this.StartWithClause);
        hqc.setConnectByClause(this.ConnectByClause);
        hqc.setStartWithCondition(this.StartWithCondition.toInformixSelect(to_sqs, from_sqs));
        hqc.setConnectByCondition(this.ConnectByCondition.toInformixSelect(to_sqs, from_sqs));
        return hqc;
    }
    
    public HierarchicalQueryClause toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final HierarchicalQueryClause hqc = new HierarchicalQueryClause();
        hqc.setStartWithClause(this.StartWithClause);
        hqc.setConnectByClause(this.ConnectByClause);
        hqc.setStartWithCondition(this.StartWithCondition.toMSSQLServerSelect(to_sqs, from_sqs));
        hqc.setConnectByCondition(this.ConnectByCondition.toMSSQLServerSelect(to_sqs, from_sqs));
        return hqc;
    }
    
    public HierarchicalQueryClause toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final HierarchicalQueryClause hqc = new HierarchicalQueryClause();
        hqc.setStartWithClause(this.StartWithClause);
        hqc.setConnectByClause(this.ConnectByClause);
        hqc.setStartWithCondition(this.StartWithCondition.toSybaseSelect(to_sqs, from_sqs));
        hqc.setConnectByCondition(this.ConnectByCondition.toSybaseSelect(to_sqs, from_sqs));
        return hqc;
    }
    
    public HierarchicalQueryClause toTimesTenSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        throw new ConvertException("\nHierarchial Query Clause(START WITH...CONNECT BY PRIOR) is not supported in TimesTen 5.1.21\n");
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.StartWithClause != null) {
            sb.append(this.StartWithClause.toUpperCase());
        }
        if (this.StartWithCondition != null) {
            sb.append(" " + this.StartWithCondition.toString());
        }
        if (this.ConnectByClause != null) {
            sb.append(" " + this.ConnectByClause.toUpperCase());
        }
        if (this.ConnectByCondition != null) {
            sb.append(" " + this.ConnectByCondition.toString());
        }
        return sb.toString();
    }
}
