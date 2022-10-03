package com.adventnet.swissqlapi.sql.statement.select;

import java.util.Vector;
import com.adventnet.swissqlapi.sql.exception.ConvertException;

public class FetchClause
{
    public String FetchFirstClause;
    public String FetchCount;
    public String RowOnlyClause;
    private String fetchCountVariable;
    private String fetchOffsetCount;
    
    public void setFetchFirstClause(final String s_ffc) {
        this.FetchFirstClause = s_ffc;
    }
    
    public void setFetchCount(final String s_fc) {
        this.FetchCount = s_fc;
    }
    
    public void setFetchOffSetCount(final String s_fc) {
        this.fetchOffsetCount = s_fc;
    }
    
    public void setFetchCountVariable(final String variable) {
        this.fetchCountVariable = variable;
    }
    
    public void setRowOnlyClause(final String s_roc) {
        this.RowOnlyClause = s_roc;
    }
    
    public String getFetchFirstClause() {
        return this.FetchFirstClause;
    }
    
    public String getFetchCount() {
        return this.FetchCount;
    }
    
    public String getFetchCountVariable() {
        return this.fetchCountVariable;
    }
    
    public String getRowOnlyClause() {
        return this.RowOnlyClause;
    }
    
    public String getFetchOffSetCount(final String s_fc) {
        return this.fetchOffsetCount;
    }
    
    public FetchClause toDB2Select(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final FetchClause fc = new FetchClause();
        fc.setFetchFirstClause(this.FetchFirstClause);
        fc.setFetchCount(this.FetchCount);
        fc.setRowOnlyClause(this.RowOnlyClause);
        return fc;
    }
    
    public FetchClause toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (to_sqs.getSelectStatement().getSelectRowSpecifier() != null) {
            throw new ConvertException();
        }
        to_sqs.getSelectStatement().setSelectRowSpecifier("TOP");
        if (this.FetchCount == null) {
            this.FetchCount = "1";
        }
        to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(this.FetchCount));
        return null;
    }
    
    public FetchClause toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (to_sqs.getSelectStatement().getSelectRowSpecifier() != null) {
            throw new ConvertException();
        }
        to_sqs.getSelectStatement().setSelectRowSpecifier("TOP");
        if (this.FetchCount == null) {
            this.FetchCount = "1";
        }
        to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(this.FetchCount));
        return null;
    }
    
    public FetchClause toPostgreSQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final LimitClause lc = new LimitClause();
        if (to_sqs.getLimitClause() != null) {
            throw new ConvertException();
        }
        lc.setLimitClause("LIMIT");
        if (this.FetchCount == null) {
            this.FetchCount = "1";
        }
        lc.setLimitValue(this.FetchCount);
        to_sqs.setLimitClause(lc);
        return null;
    }
    
    public FetchClause toMySQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final LimitClause lc = new LimitClause();
        if (to_sqs.getLimitClause() != null) {
            throw new ConvertException();
        }
        lc.setLimitClause("LIMIT");
        if (this.FetchCount == null) {
            this.FetchCount = "1";
        }
        lc.setLimitValue(this.FetchCount);
        to_sqs.setLimitClause(lc);
        return null;
    }
    
    public FetchClause toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final LimitClause lc = new LimitClause();
        if (from_sqs.getLimitClause() != null || from_sqs.getSelectStatement().getSelectRowSpecifier() != null) {
            throw new ConvertException();
        }
        final WhereExpression f_we = from_sqs.getWhereExpression();
        final WhereItem wi = new WhereItem();
        Vector v_temp = new Vector();
        WhereColumn wc_temp = new WhereColumn();
        v_temp.addElement("ROWNUM");
        wc_temp.setColumnExpression(v_temp);
        wi.setLeftWhereExp(wc_temp);
        wi.setOperator("<");
        v_temp = new Vector();
        wc_temp = new WhereColumn();
        if (this.FetchCount == null) {
            this.FetchCount = "1";
        }
        v_temp.addElement(Integer.toString(Integer.parseInt(this.FetchCount) + 1));
        wc_temp.setColumnExpression(v_temp);
        wi.setRightWhereExp(wc_temp);
        if (f_we != null && f_we.getCheckWhere()) {
            to_sqs.getWhereExpression().addOperator("AND");
            to_sqs.getWhereExpression().addWhereItem(wi);
        }
        else if (f_we != null) {
            to_sqs.setWhereExpression(f_we.toOracleSelect(to_sqs, from_sqs));
            to_sqs.getWhereExpression().addOperator("AND");
            to_sqs.getWhereExpression().addWhereItem(wi);
        }
        else {
            final WhereExpression we = new WhereExpression();
            we.addWhereItem(wi);
            if (to_sqs != null && to_sqs.getWhereExpression() != null) {
                to_sqs.getWhereExpression().addOperator("AND");
                to_sqs.getWhereExpression().addWhereExpression(we);
            }
            else {
                to_sqs.setWhereExpression(we);
            }
        }
        return null;
    }
    
    public FetchClause toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (to_sqs.getSelectStatement().getSelectRowSpecifier() != null) {
            throw new ConvertException();
        }
        to_sqs.getSelectStatement().setInformixRowSpecifier("FIRST");
        if (this.FetchCount == null) {
            this.FetchCount = "1";
        }
        to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(this.FetchCount));
        return null;
    }
    
    public FetchClause toTimesTenSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (to_sqs.getSelectStatement().getSelectRowSpecifier() != null) {
            throw new ConvertException();
        }
        to_sqs.getSelectStatement().setSelectRowSpecifier("FIRST");
        if (this.FetchCount == null) {
            this.FetchCount = "1";
        }
        to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(this.FetchCount));
        return null;
    }
    
    public FetchClause toNetezzaSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final LimitClause lc = new LimitClause();
        if (to_sqs.getLimitClause() != null) {
            throw new ConvertException();
        }
        lc.setLimitClause("LIMIT");
        if (this.FetchCount == null) {
            this.FetchCount = "1";
        }
        lc.setLimitValue(this.FetchCount);
        to_sqs.setLimitClause(lc);
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.fetchOffsetCount == null) {
            sb.append(this.FetchFirstClause.toUpperCase());
            if (this.fetchCountVariable != null) {
                sb.append(" (" + this.fetchCountVariable + ")");
            }
            else if (this.FetchCount != null) {
                sb.append(" " + this.FetchCount.toUpperCase());
            }
            sb.append(" " + this.RowOnlyClause.toUpperCase());
        }
        else {
            sb.append("OFFSET");
            sb.append(" " + this.fetchOffsetCount);
            sb.append(" " + this.FetchFirstClause);
            sb.append(" " + this.FetchCount);
            sb.append(" " + this.RowOnlyClause);
        }
        return sb.toString();
    }
}
