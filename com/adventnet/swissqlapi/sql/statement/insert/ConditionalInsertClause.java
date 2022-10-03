package com.adventnet.swissqlapi.sql.statement.insert;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;

public class ConditionalInsertClause
{
    WhereExpression whenExp;
    InsertQueryStatement iQuery;
    
    public ConditionalInsertClause() {
        this.whenExp = null;
        this.iQuery = null;
    }
    
    public void setWhenExpression(final WhereExpression we) {
        this.whenExp = we;
    }
    
    public void setInsertStmt(final InsertQueryStatement multiInsertStmt) {
        this.iQuery = multiInsertStmt;
    }
    
    public InsertQueryStatement toNetezza(final InsertQueryStatement insertQueryStmt) throws ConvertException {
        final InsertQueryStatement insertQuery = new InsertQueryStatement();
        insertQuery.setInsertClause(this.iQuery.getInsertClause());
        final SelectQueryStatement insertSubQuery = new SelectQueryStatement();
        final SelectStatement insertSubQuerySelectStmt = new SelectStatement();
        insertSubQuerySelectStmt.setSelectClause("SELECT");
        final Vector selectItems = new Vector();
        if (this.iQuery != null && this.iQuery.getValuesClause() != null) {
            for (int j = 0; j < this.iQuery.getValuesClause().getValuesList().size(); ++j) {
                final Object obj = this.iQuery.getValuesClause().getValuesList().get(j);
                if (!obj.toString().equalsIgnoreCase("(") && !obj.toString().equalsIgnoreCase(")")) {
                    selectItems.add(obj);
                }
            }
        }
        if (selectItems.size() <= 0) {
            selectItems.add("*");
        }
        insertSubQuerySelectStmt.setSelectItemList(selectItems);
        final FromClause fromClause = new FromClause();
        fromClause.setFromClause("FROM");
        final FromTable fromTable = new FromTable();
        fromTable.setTableName(insertQueryStmt.getSubQuery());
        final Vector fromItems = new Vector();
        fromItems.add(fromTable);
        fromClause.setFromItemList(fromItems);
        insertSubQuery.setFromClause(fromClause);
        insertSubQuery.setSelectStatement(insertSubQuerySelectStmt);
        insertSubQuery.setWhereExpression(this.whenExp);
        insertQuery.setSubQuery(insertSubQuery);
        insertQuery.setValuesClause(null);
        return insertQuery;
    }
}
