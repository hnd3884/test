package com.adventnet.swissqlapi.sql.statement.update;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;

public class UpdateLimitClause
{
    private String limit;
    private String dimension;
    
    public void setLimit(final String s) {
        this.limit = s;
    }
    
    public void setDimension(final String s) {
        this.dimension = s;
    }
    
    public String getLimit() {
        return this.limit;
    }
    
    public String getDimension() {
        return this.dimension;
    }
    
    public void toOracleRowNum(final UpdateQueryStatement uqs) {
        final WhereItem newWhereItem = new WhereItem();
        final WhereColumn lWhereColumn = new WhereColumn();
        final Vector lcolExp = new Vector();
        lcolExp.addElement("ROWNUM");
        lWhereColumn.setColumnExpression(lcolExp);
        newWhereItem.setLeftWhereExp(lWhereColumn);
        newWhereItem.setOperator("<");
        final WhereColumn rWhereColumn = new WhereColumn();
        final Vector rcolExp = new Vector();
        rcolExp.addElement(Integer.parseInt(this.dimension) + 1 + "");
        rWhereColumn.setColumnExpression(rcolExp);
        newWhereItem.setRightWhereExp(rWhereColumn);
        if (uqs.getWhereExpression() != null) {
            uqs.getWhereExpression().addOperator(" AND ");
            uqs.getWhereExpression().addWhereItem(newWhereItem);
        }
        else {
            final WhereExpression newWhereExpression = new WhereExpression();
            newWhereExpression.addWhereItem(newWhereItem);
            uqs.setWhereClause(newWhereExpression);
        }
    }
    
    public void toMySQLSelect() throws ConvertException {
    }
    
    public void toOracleSelect() throws ConvertException {
    }
    
    public void toMSSQLServerSelect() throws ConvertException {
    }
    
    public void toSybaseSelect() throws ConvertException {
    }
    
    public void toPostgreSQLSelect() throws ConvertException {
    }
    
    public void toDB2Select() throws ConvertException {
    }
    
    public void toInformixSelect() throws ConvertException {
    }
    
    @Override
    public String toString() {
        final StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(" " + this.limit.toUpperCase() + " ");
        stringbuffer.append(" " + this.dimension + " ");
        return stringbuffer.toString();
    }
}
