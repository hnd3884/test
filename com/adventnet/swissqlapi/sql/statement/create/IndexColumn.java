package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;

public class IndexColumn
{
    private SelectColumn selectColumn;
    private String ascOrDesc;
    private UserObjectContext context;
    
    public IndexColumn() {
        this.context = null;
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public void setIndexColumnName(final SelectColumn selectColumn) {
        this.selectColumn = selectColumn;
    }
    
    public void setAscOrDesc(final String ascOrDesc) {
        this.ascOrDesc = ascOrDesc;
    }
    
    public SelectColumn getIndexColumnName() {
        return this.selectColumn;
    }
    
    public String getAscOrDesc() {
        return this.ascOrDesc;
    }
    
    public IndexColumn toANSI() throws ConvertException {
        final IndexColumn tempIndexColumn = this.copyObjectvalues();
        if (tempIndexColumn.getIndexColumnName() != null) {
            final SelectColumn tempSelectColumn = tempIndexColumn.getIndexColumnName();
            final SelectColumn ansiSelectColumn = tempSelectColumn.toANSISelect(null, null);
            tempIndexColumn.setIndexColumnName(ansiSelectColumn);
        }
        tempIndexColumn.setAscOrDesc(null);
        return tempIndexColumn;
    }
    
    public IndexColumn toDB2() throws ConvertException {
        final IndexColumn tempIndexColumn = this.copyObjectvalues();
        if (tempIndexColumn.getIndexColumnName() != null) {
            final SelectColumn tempSelectColumn = tempIndexColumn.getIndexColumnName();
            final SelectColumn db2SelectColumn = tempSelectColumn.toDB2Select(null, null);
            tempIndexColumn.setIndexColumnName(db2SelectColumn);
        }
        tempIndexColumn.setAscOrDesc(null);
        return tempIndexColumn;
    }
    
    public IndexColumn toInformix() throws ConvertException {
        final IndexColumn tempIndexColumn = this.copyObjectvalues();
        if (tempIndexColumn.getIndexColumnName() != null) {
            final SelectColumn tempSelectColumn = tempIndexColumn.getIndexColumnName();
            final SelectColumn informixSelectColumn = tempSelectColumn.toInformixSelect(null, null);
            tempIndexColumn.setIndexColumnName(informixSelectColumn);
        }
        tempIndexColumn.setAscOrDesc(null);
        return tempIndexColumn;
    }
    
    public IndexColumn toMSSQLServer() throws ConvertException {
        final IndexColumn tempIndexColumn = this.copyObjectvalues();
        if (tempIndexColumn.getIndexColumnName() != null) {
            if (tempIndexColumn.toString().trim().equalsIgnoreCase("date")) {
                tempIndexColumn.setIndexColumnName(tempIndexColumn.getIndexColumnName());
            }
            else if (tempIndexColumn.toString().trim().equalsIgnoreCase("user")) {
                final SelectColumn tempSelectColumn = new SelectColumn();
                final Vector tempSelectColumnVector = new Vector();
                tempSelectColumnVector.add("[user]");
                tempSelectColumn.setColumnExpression(tempSelectColumnVector);
                tempIndexColumn.setIndexColumnName(tempSelectColumn);
            }
            else {
                final SelectColumn tempSelectColumn = tempIndexColumn.getIndexColumnName();
                final SelectColumn msSQLServerSelectColumn = tempSelectColumn.toMSSQLServerSelect(null, null);
                tempIndexColumn.setIndexColumnName(msSQLServerSelectColumn);
            }
        }
        if (tempIndexColumn.getAscOrDesc() != null) {
            final String tempAscOrDesc = tempIndexColumn.getAscOrDesc();
            tempIndexColumn.setAscOrDesc(tempAscOrDesc);
        }
        return tempIndexColumn;
    }
    
    public IndexColumn toSybase() throws ConvertException {
        final IndexColumn tempIndexColumn = this.copyObjectvalues();
        if (tempIndexColumn.getIndexColumnName() != null) {
            final SelectColumn tempSelectColumn = tempIndexColumn.getIndexColumnName();
            final SelectColumn sybaseSelectColumn = tempSelectColumn.toSybaseSelect(null, null);
            tempIndexColumn.setIndexColumnName(sybaseSelectColumn);
        }
        if (tempIndexColumn.getAscOrDesc() != null) {
            final String tempAscOrDesc = tempIndexColumn.getAscOrDesc();
            tempIndexColumn.setAscOrDesc(tempAscOrDesc);
        }
        return tempIndexColumn;
    }
    
    public IndexColumn toMySQL() throws ConvertException {
        final IndexColumn tempIndexColumn = this.copyObjectvalues();
        if (tempIndexColumn.getIndexColumnName() != null) {
            final SelectColumn tempSelectColumn = tempIndexColumn.getIndexColumnName();
            final Vector colExp = tempSelectColumn.getColumnExpression();
            boolean ignoreConversion = false;
            if (colExp != null) {
                for (int colExpSize = colExp.size(), i = 0; i < colExpSize; ++i) {
                    final Object obj = colExp.get(i);
                    if (obj instanceof TableColumn) {
                        final TableColumn tabCol = (TableColumn)obj;
                        String colName = tabCol.getColumnName();
                        if (colName != null && colName.trim().equalsIgnoreCase("DATE")) {
                            ignoreConversion = true;
                            colName = "`" + colName + "`";
                            tabCol.setColumnName(colName);
                        }
                    }
                }
            }
            if (!ignoreConversion) {
                final SelectColumn mySQLSelectColumn = tempSelectColumn.toMySQLSelect(null, null);
                tempIndexColumn.setIndexColumnName(mySQLSelectColumn);
            }
        }
        tempIndexColumn.setAscOrDesc(null);
        return tempIndexColumn;
    }
    
    public IndexColumn toOracle() throws ConvertException {
        final IndexColumn tempIndexColumn = this.copyObjectvalues();
        if (tempIndexColumn.getIndexColumnName() != null) {
            final SelectColumn tempSelectColumn = tempIndexColumn.getIndexColumnName();
            final SelectColumn oracleSelectColumn = tempSelectColumn.toOracleSelect(null, null);
            final Vector colExpr = oracleSelectColumn.getColumnExpression();
            for (int i = 0; i < colExpr.size(); ++i) {
                final Object obj = colExpr.get(i);
                if (obj instanceof TableColumn) {
                    final TableColumn tc = (TableColumn)obj;
                    final String tableName = tc.getTableName();
                    String colName = tc.getColumnName();
                    if (tableName == null && colName.startsWith("\"") && colName.endsWith("\"")) {
                        colName = colName.substring(1, colName.length() - 1);
                        if (colName.length() > 30) {
                            colName = "\"" + colName.substring(0, 30) + "\"";
                            tc.setColumnName(colName);
                        }
                    }
                }
            }
            tempIndexColumn.setIndexColumnName(oracleSelectColumn);
        }
        if (tempIndexColumn.getAscOrDesc() != null) {
            final String tempAscOrDesc = tempIndexColumn.getAscOrDesc();
            tempIndexColumn.setAscOrDesc(tempAscOrDesc);
        }
        return tempIndexColumn;
    }
    
    public IndexColumn toPostgreSQL() throws ConvertException {
        final IndexColumn tempIndexColumn = this.copyObjectvalues();
        if (tempIndexColumn.getIndexColumnName() != null) {
            final SelectColumn tempSelectColumn = tempIndexColumn.getIndexColumnName();
            final SelectColumn postgreSelectColumn = tempSelectColumn.toPostgreSQLSelect(null, null);
            tempIndexColumn.setIndexColumnName(postgreSelectColumn);
        }
        tempIndexColumn.setAscOrDesc(null);
        return tempIndexColumn;
    }
    
    public IndexColumn toTimesTen() throws ConvertException {
        final IndexColumn tempIndexColumn = this.copyObjectvalues();
        if (tempIndexColumn.getIndexColumnName() instanceof SelectColumn) {
            final Vector colexp = tempIndexColumn.getIndexColumnName().getColumnExpression();
            if (colexp.size() > 1) {
                throw new ConvertException("\n\nIndex creation on 'Column Expressions' is not supported in TimesTen 5.1.21\n");
            }
            if (colexp.get(0) instanceof FunctionCalls) {
                throw new ConvertException("\n\nIndex creation on 'Function Calls' is not supported in TimesTen 5.1.21\n");
            }
        }
        if (tempIndexColumn.getIndexColumnName() != null) {
            tempIndexColumn.getIndexColumnName();
        }
        if (tempIndexColumn.getAscOrDesc() != null) {
            final String tempAscOrDesc = tempIndexColumn.getAscOrDesc();
            tempIndexColumn.setAscOrDesc(tempAscOrDesc);
        }
        return tempIndexColumn;
    }
    
    public IndexColumn toNetezza() throws ConvertException {
        final IndexColumn tempIndexColumn = this.copyObjectvalues();
        if (tempIndexColumn.getIndexColumnName() != null) {
            final SelectColumn tempSelectColumn = tempIndexColumn.getIndexColumnName();
            final SelectColumn netezzaSelectColumn = tempSelectColumn.toNetezzaSelect(null, null);
            tempIndexColumn.setIndexColumnName(netezzaSelectColumn);
        }
        tempIndexColumn.setAscOrDesc(null);
        return tempIndexColumn;
    }
    
    public IndexColumn toTeradata() throws ConvertException {
        final IndexColumn tempIndexColumn = this.copyObjectvalues();
        if (tempIndexColumn.getIndexColumnName() != null) {
            final SelectColumn tempSelectColumn = tempIndexColumn.getIndexColumnName();
            final SelectColumn TeradataSelectColumn = tempSelectColumn.toTeradataSelect(null, null);
            tempIndexColumn.setIndexColumnName(TeradataSelectColumn);
        }
        tempIndexColumn.setAscOrDesc(null);
        return tempIndexColumn;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.selectColumn != null) {
            this.selectColumn.setObjectContext(this.context);
            sb.append(this.selectColumn + " ");
        }
        if (this.ascOrDesc != null) {
            sb.append(this.ascOrDesc.toUpperCase() + " ");
        }
        return sb.toString();
    }
    
    public IndexColumn copyObjectvalues() {
        final IndexColumn dupIndexColumn = new IndexColumn();
        dupIndexColumn.setIndexColumnName(this.selectColumn);
        dupIndexColumn.setAscOrDesc(this.ascOrDesc);
        dupIndexColumn.setObjectContext(this.context);
        return dupIndexColumn;
    }
}
