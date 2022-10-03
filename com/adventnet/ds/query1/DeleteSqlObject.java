package com.adventnet.ds.query1;

import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.parser.ParseException;
import java.util.Iterator;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.update.TableClause;
import com.adventnet.persistence.PersistenceUtil;
import java.util.Collection;
import com.adventnet.swissqlapi.SwisSQLAPI;
import java.util.HashMap;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import java.util.Map;
import java.util.List;
import com.adventnet.swissqlapi.sql.statement.delete.DeleteQueryStatement;
import java.util.logging.Logger;

public class DeleteSqlObject
{
    private static final Logger LOGGER;
    private DeleteQueryStatement dqs;
    private List<String> tableNames;
    private Map<String, FromTable> fromTableMap;
    private Map sasMap;
    private static final String OPEN_BRACE = "(";
    private static final String CLOSE_BRACE = ")";
    
    public static void main(final String[] args) throws Exception {
        final DeleteSqlObject ds = new DeleteSqlObject(args[0]);
        if (ds != null) {
            DeleteSqlObject.LOGGER.info("OVER");
        }
    }
    
    public DeleteSqlObject(final String sql) throws ParseException, ConvertException, DataAccessException {
        this.dqs = null;
        this.tableNames = new ArrayList<String>();
        this.fromTableMap = new HashMap<String, FromTable>();
        this.sasMap = null;
        this.dqs = (DeleteQueryStatement)new SwisSQLAPI(sql).parse();
        if (this.dqs.getFromClause() != null) {
            final Vector<FromTable> fromTables = this.dqs.getFromClause().getFromItemList();
            for (final FromTable fromTable : fromTables) {
                this.rightjoinCheck(fromTable);
                final Object tableName = fromTable.getTableName();
                if (tableName instanceof String) {
                    this.fromTableMap.put((String)tableName, fromTable);
                }
            }
            this.tableNames = PersistenceUtil.sortTables(new ArrayList(this.fromTableMap.keySet()));
        }
        else {
            final List tables = this.dqs.getTableExpression().getTableClauseList();
            final List<String> tmpList = new ArrayList<String>();
            for (final Object table : tables) {
                if (table instanceof TableClause) {
                    tmpList.add(((TableClause)table).getTableObject().getTableName());
                }
            }
            this.tableNames = PersistenceUtil.sortTables(tmpList);
        }
    }
    
    public List<String> getTableNames() {
        return this.tableNames;
    }
    
    public void setSasMap(final Map sasMap) {
        this.sasMap = sasMap;
    }
    
    public void doScoping() {
        if (this.sasMap == null) {
            return;
        }
        final WhereExpression scopeWhereExp = this.brace(this.formSasWhereExp());
        final WhereExpression whereExp = this.dqs.getWhereExpression();
        if (whereExp == null && scopeWhereExp != null) {
            this.dqs.setWhereClause(scopeWhereExp);
        }
        else {
            final WhereExpression newWhereExp = new WhereExpression();
            newWhereExp.addWhereExpression(this.brace(whereExp));
            if (scopeWhereExp != null) {
                newWhereExp.addOperator("AND");
                newWhereExp.addWhereExpression(scopeWhereExp);
            }
            this.dqs.setWhereClause(newWhereExp);
        }
    }
    
    public String getSQL(final String dialect) throws ConvertException {
        if (dialect == null) {
            throw new IllegalArgumentException("dialect paramter cannot be null");
        }
        if (dialect.contains("mysql")) {
            return this.dqs.toMySQLString();
        }
        if (dialect.contains("oracle")) {
            return this.dqs.toOracleString();
        }
        if (dialect.contains("mssql")) {
            return this.dqs.toMSSQLServerString();
        }
        if (dialect.contains("postgres")) {
            return this.dqs.toPostgreSQLString();
        }
        throw new UnsupportedOperationException("This dialect " + dialect + " is not supported");
    }
    
    private WhereExpression formSasWhereExp() {
        final ColumnDefinition sasColumn = this.sasMap.get("scopeColumn");
        if (sasColumn == null) {
            DeleteSqlObject.LOGGER.severe("sas column is null. So SAS Scoping criteria will not be append");
            return null;
        }
        final TableColumn table = this.formTableColumn(this.fromTableMap.get(sasColumn.getTableName()), sasColumn);
        final WhereExpression whereExp = new WhereExpression();
        whereExp.addWhereItem(this.formWhereItem(table, ">=", this.sasMap.get("sas_start_id").toString()));
        whereExp.addOperator("AND");
        whereExp.addWhereItem(this.formWhereItem(table, "<=", this.sasMap.get("sas_end_id").toString()));
        return whereExp;
    }
    
    private WhereItem formWhereItem(final TableColumn table, final String operator, final String value) {
        final WhereColumn lhs = new WhereColumn();
        lhs.setColumnExpression((Vector)this.singletonVector(table));
        final WhereColumn rhs = new WhereColumn();
        rhs.setColumnExpression((Vector)this.singletonVector(value));
        final WhereItem whereItem = new WhereItem();
        whereItem.setLeftWhereExp(lhs);
        whereItem.setOperator(operator);
        whereItem.setRightWhereExp(rhs);
        return whereItem;
    }
    
    public TableColumn formTableColumn(final FromTable fromTable, final ColumnDefinition columnDefn) {
        final TableColumn tableColumn = new TableColumn();
        String tableName = columnDefn.getTableName();
        if (fromTable != null && fromTable.getAliasName() != null) {
            tableName = fromTable.getAliasName();
        }
        tableColumn.setTableName(tableName);
        tableColumn.setColumnName(columnDefn.getColumnName());
        return tableColumn;
    }
    
    public <T> Vector<T> singletonVector(final T obj) {
        final Vector<T> toRet = new Vector<T>(1);
        toRet.add(obj);
        return toRet;
    }
    
    private void rightjoinCheck(final FromTable fromTable) {
        final String join = fromTable.getJoinClause();
        if (join != null && join.toLowerCase().contains("right")) {
            throw new IllegalArgumentException("Delete Query having 'right join' is not supported");
        }
    }
    
    private WhereExpression brace(final WhereExpression whereExp) {
        if (whereExp == null) {
            return null;
        }
        whereExp.setOpenBrace("(");
        whereExp.setCloseBrace(")");
        return whereExp;
    }
    
    static {
        LOGGER = Logger.getLogger(DeleteSqlObject.class.getName());
    }
}
