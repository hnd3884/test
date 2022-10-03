package com.adventnet.swissqlapi.sql.statement.update;

import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.ArrayList;

public class TableExpression
{
    private ArrayList tableClauseList;
    private SelectQueryStatement subQuery;
    private SampleClause sampleClause;
    private String remoteTable;
    private WithClause withClause;
    private TableCollectionExpression tblCollExp;
    private boolean tableNameforAliasName;
    private UserObjectContext context;
    private String starInTableExp;
    public static boolean isUpdateStatement;
    
    public TableExpression() {
        this.tableNameforAliasName = false;
        this.context = null;
        this.starInTableExp = null;
        this.tableClauseList = null;
        this.subQuery = null;
        this.sampleClause = null;
        this.withClause = null;
        this.tblCollExp = null;
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public void setTableClauseList(final ArrayList list) {
        this.tableClauseList = list;
    }
    
    public ArrayList getTableClauseList() {
        return this.tableClauseList;
    }
    
    public void setSubQuery(final SelectQueryStatement s) {
        this.subQuery = s;
    }
    
    public void setSampleClause(final SampleClause sampleclause) {
        this.sampleClause = sampleclause;
    }
    
    public void setWithClause(final WithClause withclause) {
        this.withClause = withclause;
    }
    
    public void setRemoteTable(final String s) {
        this.remoteTable = s;
    }
    
    public void setTableCollectionExpression(final TableCollectionExpression tablecollectionexpression) {
        this.tblCollExp = tablecollectionexpression;
    }
    
    public void setTableNameforAliasNameInDB2Insert(final boolean tableNameForAliasName) {
        this.tableNameforAliasName = tableNameForAliasName;
    }
    
    public void setStarInTableExp(final String star) {
        this.starInTableExp = star;
    }
    
    public String getStarInTableExp() {
        return this.starInTableExp;
    }
    
    public SelectQueryStatement getSubQuery() {
        return this.subQuery;
    }
    
    public SampleClause getSampleClause() {
        return this.sampleClause;
    }
    
    public WithClause getWithClause() {
        return this.withClause;
    }
    
    public String getRemoteTable() {
        return this.remoteTable;
    }
    
    public TableCollectionExpression getTableCollectionExpression() {
        return this.tblCollExp;
    }
    
    public void toGeneric() {
        if (this.subQuery == null) {
            if (this.tableClauseList != null && this.tableClauseList.get(0) != null && this.tableClauseList.get(0) instanceof TableClause) {
                final TableClause tableClause = this.tableClauseList.get(0);
                if (tableClause.getAlias() != null) {
                    return;
                }
            }
        }
        else {
            final FromClause fromClause = this.subQuery.getFromClause();
            final Vector fromItemList = fromClause.getFromItemList();
            final FromTable fromTable = fromItemList.elementAt(0);
            final String tableName = (String)fromTable.getTableName();
            (this.tableClauseList = new ArrayList()).add(tableName);
            this.subQuery = null;
        }
        this.sampleClause = null;
        this.remoteTable = null;
        this.withClause = null;
        this.tblCollExp = null;
    }
    
    public void toMySQL() throws ConvertException {
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toMySQLSelect();
        }
        if (this.tableClauseList != null) {
            for (int size = this.tableClauseList.size(), i = 0; i < size; ++i) {
                if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause && !TableExpression.isUpdateStatement) {
                    this.tableClauseList.get(i).toMySQL();
                    this.tableClauseList.get(i).setAlias("");
                }
            }
        }
        this.toGeneric();
    }
    
    public void toOracle() throws ConvertException {
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toOracleSelect();
        }
        if (this.tableClauseList != null) {
            for (int size = this.tableClauseList.size(), i = 0; i < size; ++i) {
                if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause) {
                    this.tableClauseList.get(i).toOracle();
                }
            }
        }
        this.toGeneric();
    }
    
    public void toMSSQLServer() throws ConvertException {
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toMSSQLServerSelect();
        }
        if (this.tableClauseList != null) {
            for (int size = this.tableClauseList.size(), i = 0; i < size; ++i) {
                if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause) {
                    this.tableClauseList.get(i).toMSSQLServer();
                }
            }
        }
        this.toGeneric();
    }
    
    public void toSybase() throws ConvertException {
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toSybaseSelect();
        }
        if (this.tableClauseList != null) {
            for (int size = this.tableClauseList.size(), i = 0; i < size; ++i) {
                if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause) {
                    this.tableClauseList.get(i).toSybase();
                    this.tableClauseList.get(i).setAlias("");
                }
            }
        }
        this.toGeneric();
    }
    
    public void toPostgreSQL() throws ConvertException {
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toPostgreSQLSelect();
        }
        if (this.tableClauseList != null) {
            for (int size = this.tableClauseList.size(), i = 0; i < size; ++i) {
                if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause) {
                    this.tableClauseList.get(i).toPostgreSQL();
                    this.tableClauseList.get(i).setAlias("");
                }
            }
        }
        this.toGeneric();
    }
    
    public void toDB2() throws ConvertException {
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toDB2Select();
        }
        if (this.tableClauseList != null) {
            for (int size = this.tableClauseList.size(), i = 0; i < size; ++i) {
                if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause) {
                    this.tableClauseList.get(i).toDB2();
                    if (this.tableNameforAliasName) {
                        this.tableClauseList.get(i).setAlias("");
                    }
                    this.tableNameforAliasName = false;
                }
            }
        }
        this.toGeneric();
    }
    
    public void toInformix() throws ConvertException {
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toInformixSelect();
        }
        if (this.tableClauseList != null) {
            for (int size = this.tableClauseList.size(), i = 0; i < size; ++i) {
                if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause) {
                    this.tableClauseList.get(i).toInformix();
                    this.tableClauseList.get(i).setAlias("");
                }
            }
        }
        this.toGeneric();
    }
    
    public void toANSISQL() throws ConvertException {
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toANSISelect();
        }
        if (this.tableClauseList != null) {
            for (int size = this.tableClauseList.size(), i = 0; i < size; ++i) {
                if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause) {
                    this.tableClauseList.get(i).toANSISQL();
                    this.tableClauseList.get(i).setAlias("");
                }
            }
        }
        this.toGeneric();
    }
    
    public void toTeradata() throws ConvertException {
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toTeradataSelect();
        }
        if (this.tableClauseList != null) {
            for (int size = this.tableClauseList.size(), i = 0; i < size; ++i) {
                if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause) {
                    this.tableClauseList.get(i).toTeradata();
                    this.tableClauseList.get(i).setAlias("");
                }
            }
        }
        this.toGeneric();
    }
    
    public void toTimesTen() throws ConvertException {
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toTimesTenSelect();
        }
        if (this.tableClauseList != null) {
            for (int size = this.tableClauseList.size(), i = 0; i < size; ++i) {
                if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause) {
                    this.tableClauseList.get(i).toTimesTen();
                }
            }
        }
        this.toGeneric();
    }
    
    public void toNetezza() throws ConvertException {
        if (this.subQuery != null) {
            this.subQuery = this.subQuery.toNetezzaSelect();
        }
        if (this.tableClauseList != null) {
            for (int size = this.tableClauseList.size(), i = 0; i < size; ++i) {
                if (this.tableClauseList.get(i) != null && this.tableClauseList.get(i) instanceof TableClause) {
                    this.tableClauseList.get(i).toNetezza();
                    this.tableClauseList.get(i).setAlias("");
                }
            }
        }
        if (this.subQuery == null) {
            if (this.tableClauseList != null && this.tableClauseList.get(0) != null && this.tableClauseList.get(0) instanceof TableClause) {
                final TableClause tableClause = this.tableClauseList.get(0);
                if (tableClause.getAlias() != null) {
                    return;
                }
            }
        }
        else {
            final FromClause fromClause = this.subQuery.getFromClause();
            final Vector fromItemList = fromClause.getFromItemList();
            final FromTable fromTable = fromItemList.elementAt(0);
            this.tableClauseList = new ArrayList();
            if (fromTable.getTableName() instanceof String) {
                final String tableName = (String)fromTable.getTableName();
                this.tableClauseList.add(tableName);
            }
            else if (fromTable.getTableName() instanceof SelectQueryStatement) {
                SwisSQLUtils.swissqlMessageList.add("Netezza does not support subqueries in the UPDATE clause of UPDATE statements.");
                this.tableClauseList.add(fromTable.getTableName());
            }
            else {
                this.tableClauseList.add(fromTable.getTableName());
            }
            this.subQuery = null;
        }
        this.sampleClause = null;
        this.remoteTable = null;
        this.withClause = null;
        this.tblCollExp = null;
    }
    
    @Override
    public String toString() {
        final StringBuffer stringbuffer = new StringBuffer();
        if (this.tableClauseList != null) {
            for (int size = this.tableClauseList.size(), i = 0; i < size; ++i) {
                if (this.tableClauseList.get(i) instanceof TableClause) {
                    this.tableClauseList.get(i).setObjectContext(this.context);
                }
                stringbuffer.append(this.tableClauseList.get(i).toString() + " ");
            }
        }
        if (this.subQuery != null) {
            stringbuffer.append("(" + this.subQuery.toString() + ")");
        }
        if (this.sampleClause != null) {
            stringbuffer.append(this.sampleClause.toString());
        }
        if (this.remoteTable != null) {
            stringbuffer.append(this.remoteTable.toString());
        }
        if (this.withClause != null) {
            this.withClause.setObjectContext(this.context);
            stringbuffer.append(this.withClause.toString());
        }
        if (this.tblCollExp != null) {
            stringbuffer.append(this.tblCollExp.toString());
        }
        return stringbuffer.toString();
    }
    
    static {
        TableExpression.isUpdateStatement = false;
    }
}
