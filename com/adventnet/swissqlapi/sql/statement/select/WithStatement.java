package com.adventnet.swissqlapi.sql.statement.select;

import java.util.Enumeration;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.insert.CommonTableExpression;
import java.util.Hashtable;
import java.util.Vector;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class WithStatement implements SwisSQLStatement
{
    private CommentClass commentObject;
    private UserObjectContext objectContext;
    String with;
    ArrayList columnList;
    SelectQueryStatement withSQS;
    Vector commonTableExprList;
    SelectQueryStatement derivedTableQuery;
    Hashtable cteAliasQueryMap;
    
    public WithStatement() {
        this.objectContext = null;
        this.columnList = new ArrayList();
        this.withSQS = null;
        this.commonTableExprList = null;
        this.derivedTableQuery = null;
        this.cteAliasQueryMap = new Hashtable();
    }
    
    public void setWith(final String withStr) {
        this.with = withStr;
    }
    
    public void setWithSQS(final SelectQueryStatement sqs) {
        this.withSQS = sqs;
    }
    
    public void setCommonTableExpressionList(final Vector cteList) {
        this.commonTableExprList = cteList;
    }
    
    public String getWith() {
        return this.with;
    }
    
    public SelectQueryStatement getWithSQS() {
        return this.withSQS;
    }
    
    public Vector getCommonTableExpressionList() {
        return this.commonTableExprList;
    }
    
    public SwisSQLStatement toOracle() throws ConvertException {
        final WithStatement ws = this.copyObjectValues();
        final String withTemp = ws.getWith();
        if (withTemp != null) {
            ws.setWith(null);
        }
        final Vector commonTableExpressionList = ws.getCommonTableExpressionList();
        final Vector convertedCommonTableExpressionList = new Vector();
        SelectQueryStatement withSelectStmt = ws.getWithSQS();
        withSelectStmt = withSelectStmt.toOracleSelect();
        final Vector withSelectStmtFromItems = withSelectStmt.getFromClause().getFromItemList();
        final Hashtable addSelectStmt = new Hashtable();
        final Vector aliasNamesList = new Vector();
        for (int i = 0; i < commonTableExpressionList.size(); ++i) {
            final CommonTableExpression cte = commonTableExpressionList.get(i);
            final String viewNameTemp = cte.getViewName().toString();
            final ArrayList newColumnList = new ArrayList();
            final ArrayList columnList = cte.getColumnList();
            if (columnList != null) {
                for (int colListInd = 0; colListInd < columnList.size(); ++colListInd) {
                    if (!columnList.get(colListInd).toString().equalsIgnoreCase("(") && !columnList.get(colListInd).toString().equalsIgnoreCase(",") && !columnList.get(colListInd).toString().equalsIgnoreCase(")")) {
                        newColumnList.add(columnList.get(colListInd));
                    }
                }
            }
            final Vector cteSQSSelectColList = cte.getSelectQueryStatement().getSelectStatement().getSelectItemList();
            if (cteSQSSelectColList.size() == newColumnList.size()) {
                for (int k = 0; k < cteSQSSelectColList.size(); ++k) {
                    if (cteSQSSelectColList.get(k) instanceof SelectColumn) {
                        final SelectColumn cteSelCol = cteSQSSelectColList.get(k);
                        cteSelCol.setIsAS("AS");
                        cteSelCol.setAliasName(newColumnList.get(k).toString());
                    }
                }
            }
            for (int j = 0; j < withSelectStmtFromItems.size(); ++j) {
                final FromTable fromTable = withSelectStmtFromItems.get(j);
                if (fromTable.getTableName() instanceof String) {
                    final Vector v = new Vector();
                    final String fromTableName = fromTable.getTableName().toString();
                    SelectQueryStatement ss = new SelectQueryStatement();
                    final String hashKey = cte.getViewName().toString();
                    if (addSelectStmt.containsKey(hashKey.trim())) {
                        ss = addSelectStmt.get(hashKey.trim());
                    }
                    else {
                        ss = cte.getSelectQueryStatement().toOracleSelect();
                        addSelectStmt.put(hashKey.trim(), ss);
                    }
                    final String temp = cte.getSelectQueryStatement().getFromClause().getFirstElement().toString();
                    aliasNamesList.add(cte.getViewName().getTableName());
                    if (fromTableName.equalsIgnoreCase(viewNameTemp) && commonTableExpressionList.size() == 1) {
                        fromTable.setTableName(ss);
                        fromTable.setAliasName(fromTableName);
                    }
                    if (commonTableExpressionList.size() > 1 && fromTableName.equalsIgnoreCase(viewNameTemp)) {
                        final Vector ssFromItems = ss.getFromClause().getFromItemList();
                        for (int ssIndex = 0; ssIndex < ssFromItems.size(); ++ssIndex) {
                            for (int aliasIndex = 0; aliasIndex < aliasNamesList.size(); ++aliasIndex) {
                                final FromTable ssFromTable = ssFromItems.get(ssIndex);
                                if (ssFromTable.getTableName().toString().trim().equalsIgnoreCase(aliasNamesList.get(aliasIndex).toString().trim())) {
                                    ssFromTable.setTableName(addSelectStmt.get(aliasNamesList.get(aliasIndex).toString().trim()));
                                    ssFromTable.setAliasName(aliasNamesList.get(aliasIndex).toString().trim());
                                    final SelectQueryStatement ss2 = (SelectQueryStatement)ssFromTable.getTableName();
                                    final Vector ssFromTableFromItems = ss2.getFromClause().getFromItemList();
                                    for (int x = 0; x < ssFromTableFromItems.size(); ++x) {
                                        for (int y = 0; y < aliasNamesList.size(); ++y) {
                                            if (aliasNamesList.get(y).toString().trim().equalsIgnoreCase(ssFromTableFromItems.get(x).toString().trim())) {
                                                final FromTable ss1FromTable = ssFromTableFromItems.get(x);
                                                ss1FromTable.setTableName(addSelectStmt.get(ssFromTableFromItems.get(x).toString().trim()));
                                                ss1FromTable.setAliasName(aliasNamesList.get(y).toString());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        fromTable.setTableName(ss);
                        fromTable.setAliasName(fromTableName);
                    }
                }
            }
        }
        return this.derivedTableQuery = withSelectStmt;
    }
    
    public SwisSQLStatement toMSSQLServer() throws ConvertException {
        final WithStatement ws = this.copyObjectValues();
        final String withTemp = ws.getWith();
        if (withTemp != null) {
            ws.setWith(null);
        }
        final Vector commonTableExpressionList = ws.getCommonTableExpressionList();
        final Vector convertedCommonTableExpressionList = new Vector();
        SelectQueryStatement withSelectStmt = ws.getWithSQS();
        withSelectStmt = withSelectStmt.toMSSQLServerSelect();
        final Vector withSelectStmtFromItems = withSelectStmt.getFromClause().getFromItemList();
        final Hashtable addSelectStmt = new Hashtable();
        final Vector aliasNamesList = new Vector();
        for (int i = 0; i < commonTableExpressionList.size(); ++i) {
            final CommonTableExpression cte = commonTableExpressionList.get(i);
            final String viewNameTemp = cte.getViewName().toString();
            final ArrayList newColumnList = new ArrayList();
            final ArrayList columnList = cte.getColumnList();
            if (columnList != null) {
                for (int colListInd = 0; colListInd < columnList.size(); ++colListInd) {
                    if (!columnList.get(colListInd).toString().equalsIgnoreCase("(") && !columnList.get(colListInd).toString().equalsIgnoreCase(",") && !columnList.get(colListInd).toString().equalsIgnoreCase(")")) {
                        newColumnList.add(columnList.get(colListInd));
                    }
                }
            }
            final Vector cteSQSSelectColList = cte.getSelectQueryStatement().getSelectStatement().getSelectItemList();
            if (cteSQSSelectColList.size() == newColumnList.size()) {
                for (int k = 0; k < cteSQSSelectColList.size(); ++k) {
                    if (cteSQSSelectColList.get(k) instanceof SelectColumn) {
                        final SelectColumn cteSelCol = cteSQSSelectColList.get(k);
                        cteSelCol.setIsAS("AS");
                        cteSelCol.setAliasName(newColumnList.get(k).toString());
                    }
                }
            }
            for (int j = 0; j < withSelectStmtFromItems.size(); ++j) {
                final FromTable fromTable = withSelectStmtFromItems.get(j);
                if (fromTable.getTableName() instanceof String) {
                    final Vector v = new Vector();
                    final String fromTableName = fromTable.getTableName().toString();
                    final SelectQueryStatement ss = cte.getSelectQueryStatement().toMSSQLServerSelect();
                    final String hashKey = cte.getViewName().toString();
                    final String temp = cte.getSelectQueryStatement().getFromClause().getFirstElement().toString();
                    addSelectStmt.put(hashKey.trim(), ss);
                    aliasNamesList.add(cte.getViewName().getTableName());
                    if (fromTableName.equalsIgnoreCase(viewNameTemp) && commonTableExpressionList.size() == 1) {
                        fromTable.setTableName(ss);
                        fromTable.setAliasName(fromTableName);
                    }
                    if (commonTableExpressionList.size() > 1 && fromTableName.equalsIgnoreCase(viewNameTemp)) {
                        final Vector ssFromItems = ss.getFromClause().getFromItemList();
                        for (int ssIndex = 0; ssIndex < ssFromItems.size(); ++ssIndex) {
                            for (int aliasIndex = 0; aliasIndex < aliasNamesList.size(); ++aliasIndex) {
                                final FromTable ssFromTable = ssFromItems.get(ssIndex);
                                if (ssFromTable.getTableName().toString().trim().equalsIgnoreCase(aliasNamesList.get(aliasIndex).toString().trim())) {
                                    ssFromTable.setTableName(addSelectStmt.get(aliasNamesList.get(aliasIndex).toString().trim()));
                                    ssFromTable.setAliasName(aliasNamesList.get(aliasIndex).toString().trim());
                                    final SelectQueryStatement ss2 = (SelectQueryStatement)ssFromTable.getTableName();
                                    final Vector ssFromTableFromItems = ss2.getFromClause().getFromItemList();
                                    for (int x = 0; x < ssFromTableFromItems.size(); ++x) {
                                        for (int y = 0; y < aliasNamesList.size(); ++y) {
                                            if (aliasNamesList.get(y).toString().trim().equalsIgnoreCase(ssFromTableFromItems.get(x).toString().trim())) {
                                                final FromTable ss1FromTable = ssFromTableFromItems.get(x);
                                                ss1FromTable.setTableName(addSelectStmt.get(ssFromTableFromItems.get(x).toString().trim()));
                                                ss1FromTable.setAliasName(aliasNamesList.get(y).toString());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        fromTable.setTableName(ss);
                        fromTable.setAliasName(fromTableName);
                    }
                }
            }
        }
        return this.derivedTableQuery = withSelectStmt;
    }
    
    public SwisSQLStatement toSybase() throws ConvertException {
        final WithStatement ws = this.copyObjectValues();
        final String withTemp = ws.getWith();
        if (withTemp != null) {
            ws.setWith(null);
        }
        final Vector commonTableExpressionList = ws.getCommonTableExpressionList();
        final Vector convertedCommonTableExpressionList = new Vector();
        SelectQueryStatement withSelectStmt = ws.getWithSQS();
        withSelectStmt = withSelectStmt.toSybaseSelect();
        final Vector withSelectStmtFromItems = withSelectStmt.getFromClause().getFromItemList();
        final Hashtable addSelectStmt = new Hashtable();
        final Vector aliasNamesList = new Vector();
        for (int i = 0; i < commonTableExpressionList.size(); ++i) {
            final CommonTableExpression cte = commonTableExpressionList.get(i);
            final String viewNameTemp = cte.getViewName().toString();
            final ArrayList newColumnList = new ArrayList();
            final ArrayList columnList = cte.getColumnList();
            if (columnList != null) {
                for (int colListInd = 0; colListInd < columnList.size(); ++colListInd) {
                    if (!columnList.get(colListInd).toString().equalsIgnoreCase("(") && !columnList.get(colListInd).toString().equalsIgnoreCase(",") && !columnList.get(colListInd).toString().equalsIgnoreCase(")")) {
                        newColumnList.add(columnList.get(colListInd));
                    }
                }
            }
            final Vector cteSQSSelectColList = cte.getSelectQueryStatement().getSelectStatement().getSelectItemList();
            if (cteSQSSelectColList.size() == newColumnList.size()) {
                for (int k = 0; k < cteSQSSelectColList.size(); ++k) {
                    if (cteSQSSelectColList.get(k) instanceof SelectColumn) {
                        final SelectColumn cteSelCol = cteSQSSelectColList.get(k);
                        cteSelCol.setIsAS("AS");
                        cteSelCol.setAliasName(newColumnList.get(k).toString());
                    }
                }
            }
            for (int j = 0; j < withSelectStmtFromItems.size(); ++j) {
                final FromTable fromTable = withSelectStmtFromItems.get(j);
                if (fromTable.getTableName() instanceof String) {
                    final Vector v = new Vector();
                    final String fromTableName = fromTable.getTableName().toString();
                    final SelectQueryStatement ss = cte.getSelectQueryStatement().toSybaseSelect();
                    final String hashKey = cte.getViewName().toString();
                    final String temp = cte.getSelectQueryStatement().getFromClause().getFirstElement().toString();
                    addSelectStmt.put(hashKey.trim(), ss);
                    aliasNamesList.add(cte.getViewName().getTableName());
                    if (fromTableName.equalsIgnoreCase(viewNameTemp) && commonTableExpressionList.size() == 1) {
                        fromTable.setTableName(ss);
                        fromTable.setAliasName(fromTableName);
                    }
                    if (commonTableExpressionList.size() > 1 && fromTableName.equalsIgnoreCase(viewNameTemp)) {
                        final Vector ssFromItems = ss.getFromClause().getFromItemList();
                        for (int ssIndex = 0; ssIndex < ssFromItems.size(); ++ssIndex) {
                            for (int aliasIndex = 0; aliasIndex < aliasNamesList.size(); ++aliasIndex) {
                                final FromTable ssFromTable = ssFromItems.get(ssIndex);
                                if (ssFromTable.getTableName().toString().trim().equalsIgnoreCase(aliasNamesList.get(aliasIndex).toString().trim())) {
                                    ssFromTable.setTableName(addSelectStmt.get(aliasNamesList.get(aliasIndex).toString().trim()));
                                    ssFromTable.setAliasName(aliasNamesList.get(aliasIndex).toString().trim());
                                    final SelectQueryStatement ss2 = (SelectQueryStatement)ssFromTable.getTableName();
                                    final Vector ssFromTableFromItems = ss2.getFromClause().getFromItemList();
                                    for (int x = 0; x < ssFromTableFromItems.size(); ++x) {
                                        for (int y = 0; y < aliasNamesList.size(); ++y) {
                                            if (aliasNamesList.get(y).toString().trim().equalsIgnoreCase(ssFromTableFromItems.get(x).toString().trim())) {
                                                final FromTable ss1FromTable = ssFromTableFromItems.get(x);
                                                ss1FromTable.setTableName(addSelectStmt.get(ssFromTableFromItems.get(x).toString().trim()));
                                                ss1FromTable.setAliasName(aliasNamesList.get(y).toString());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        fromTable.setTableName(ss);
                        fromTable.setAliasName(fromTableName);
                    }
                }
            }
        }
        return this.derivedTableQuery = withSelectStmt;
    }
    
    public SwisSQLStatement toDB2() throws ConvertException {
        return this;
    }
    
    public SwisSQLStatement toMySQL() throws ConvertException {
        final WithStatement ws = this.copyObjectValues();
        final String withTemp = ws.getWith();
        if (withTemp != null) {
            ws.setWith(null);
        }
        final Vector commonTableExpressionList = ws.getCommonTableExpressionList();
        final Vector convertedCommonTableExpressionList = new Vector();
        SelectQueryStatement withSelectStmt = ws.getWithSQS();
        withSelectStmt = withSelectStmt.toMySQLSelect();
        final Vector withSelectStmtFromItems = withSelectStmt.getFromClause().getFromItemList();
        final Hashtable addSelectStmt = new Hashtable();
        final Vector aliasNamesList = new Vector();
        for (int i = 0; i < commonTableExpressionList.size(); ++i) {
            final CommonTableExpression cte = commonTableExpressionList.get(i);
            final String viewNameTemp = cte.getViewName().toString();
            final ArrayList newColumnList = new ArrayList();
            final ArrayList columnList = cte.getColumnList();
            if (columnList != null) {
                for (int colListInd = 0; colListInd < columnList.size(); ++colListInd) {
                    if (!columnList.get(colListInd).toString().equalsIgnoreCase("(") && !columnList.get(colListInd).toString().equalsIgnoreCase(",") && !columnList.get(colListInd).toString().equalsIgnoreCase(")")) {
                        newColumnList.add(columnList.get(colListInd));
                    }
                }
            }
            final Vector cteSQSSelectColList = cte.getSelectQueryStatement().getSelectStatement().getSelectItemList();
            if (cteSQSSelectColList.size() == newColumnList.size()) {
                for (int k = 0; k < cteSQSSelectColList.size(); ++k) {
                    if (cteSQSSelectColList.get(k) instanceof SelectColumn) {
                        final SelectColumn cteSelCol = cteSQSSelectColList.get(k);
                        cteSelCol.setIsAS("AS");
                        cteSelCol.setAliasName(newColumnList.get(k).toString());
                    }
                }
            }
            for (int j = 0; j < withSelectStmtFromItems.size(); ++j) {
                final FromTable fromTable = withSelectStmtFromItems.get(j);
                if (fromTable.getTableName() instanceof String) {
                    final Vector v = new Vector();
                    final String fromTableName = fromTable.getTableName().toString();
                    final SelectQueryStatement ss = cte.getSelectQueryStatement().toMySQLSelect();
                    final String hashKey = cte.getViewName().toString();
                    final String temp = cte.getSelectQueryStatement().getFromClause().getFirstElement().toString();
                    addSelectStmt.put(hashKey.trim(), ss);
                    aliasNamesList.add(cte.getViewName().getTableName());
                    if (fromTableName.equalsIgnoreCase(viewNameTemp) && commonTableExpressionList.size() == 1) {
                        fromTable.setTableName(ss);
                        fromTable.setAliasName(fromTableName);
                    }
                    if (commonTableExpressionList.size() > 1 && fromTableName.equalsIgnoreCase(viewNameTemp)) {
                        final Vector ssFromItems = ss.getFromClause().getFromItemList();
                        for (int ssIndex = 0; ssIndex < ssFromItems.size(); ++ssIndex) {
                            for (int aliasIndex = 0; aliasIndex < aliasNamesList.size(); ++aliasIndex) {
                                final FromTable ssFromTable = ssFromItems.get(ssIndex);
                                if (ssFromTable.getTableName().toString().trim().equalsIgnoreCase(aliasNamesList.get(aliasIndex).toString().trim())) {
                                    ssFromTable.setTableName(addSelectStmt.get(aliasNamesList.get(aliasIndex).toString().trim()));
                                    ssFromTable.setAliasName(aliasNamesList.get(aliasIndex).toString().trim());
                                    final SelectQueryStatement ss2 = (SelectQueryStatement)ssFromTable.getTableName();
                                    final Vector ssFromTableFromItems = ss2.getFromClause().getFromItemList();
                                    for (int x = 0; x < ssFromTableFromItems.size(); ++x) {
                                        for (int y = 0; y < aliasNamesList.size(); ++y) {
                                            if (aliasNamesList.get(y).toString().trim().equalsIgnoreCase(ssFromTableFromItems.get(x).toString().trim())) {
                                                final FromTable ss1FromTable = ssFromTableFromItems.get(x);
                                                ss1FromTable.setTableName(addSelectStmt.get(ssFromTableFromItems.get(x).toString().trim()));
                                                ss1FromTable.setAliasName(aliasNamesList.get(y).toString());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        fromTable.setTableName(ss);
                        fromTable.setAliasName(fromTableName);
                    }
                }
            }
        }
        return this.derivedTableQuery = withSelectStmt;
    }
    
    public SwisSQLStatement toInformix() throws ConvertException {
        return this;
    }
    
    public SwisSQLStatement toPostgreSQL() throws ConvertException {
        final WithStatement ws = this.copyObjectValues();
        final String withTemp = ws.getWith();
        if (withTemp != null) {
            ws.setWith(null);
        }
        final Vector commonTableExpressionList = ws.getCommonTableExpressionList();
        final Vector convertedCommonTableExpressionList = new Vector();
        SelectQueryStatement withSelectStmt = ws.getWithSQS();
        withSelectStmt = withSelectStmt.toPostgreSQLSelect();
        final Vector withSelectStmtFromItems = withSelectStmt.getFromClause().getFromItemList();
        final Hashtable addSelectStmt = new Hashtable();
        final Vector aliasNamesList = new Vector();
        for (int i = 0; i < commonTableExpressionList.size(); ++i) {
            final CommonTableExpression cte = commonTableExpressionList.get(i);
            final String viewNameTemp = cte.getViewName().toString();
            final ArrayList newColumnList = new ArrayList();
            final ArrayList columnList = cte.getColumnList();
            if (columnList != null) {
                for (int colListInd = 0; colListInd < columnList.size(); ++colListInd) {
                    if (!columnList.get(colListInd).toString().equalsIgnoreCase("(") && !columnList.get(colListInd).toString().equalsIgnoreCase(",") && !columnList.get(colListInd).toString().equalsIgnoreCase(")")) {
                        newColumnList.add(columnList.get(colListInd));
                    }
                }
            }
            final Vector cteSQSSelectColList = cte.getSelectQueryStatement().getSelectStatement().getSelectItemList();
            if (cteSQSSelectColList.size() == newColumnList.size()) {
                for (int k = 0; k < cteSQSSelectColList.size(); ++k) {
                    if (cteSQSSelectColList.get(k) instanceof SelectColumn) {
                        final SelectColumn cteSelCol = cteSQSSelectColList.get(k);
                        cteSelCol.setIsAS("AS");
                        cteSelCol.setAliasName(newColumnList.get(k).toString());
                    }
                }
            }
            for (int j = 0; j < withSelectStmtFromItems.size(); ++j) {
                final FromTable fromTable = withSelectStmtFromItems.get(j);
                if (fromTable.getTableName() instanceof String) {
                    final Vector v = new Vector();
                    final String fromTableName = fromTable.getTableName().toString();
                    final SelectQueryStatement ss = cte.getSelectQueryStatement().toPostgreSQLSelect();
                    final String hashKey = cte.getViewName().toString();
                    final String temp = cte.getSelectQueryStatement().getFromClause().getFirstElement().toString();
                    addSelectStmt.put(hashKey.trim(), ss);
                    aliasNamesList.add(cte.getViewName().getTableName());
                    if (fromTableName.equalsIgnoreCase(viewNameTemp) && commonTableExpressionList.size() == 1) {
                        fromTable.setTableName(ss);
                        fromTable.setAliasName(fromTableName);
                    }
                    if (commonTableExpressionList.size() > 1 && fromTableName.equalsIgnoreCase(viewNameTemp)) {
                        final Vector ssFromItems = ss.getFromClause().getFromItemList();
                        for (int ssIndex = 0; ssIndex < ssFromItems.size(); ++ssIndex) {
                            for (int aliasIndex = 0; aliasIndex < aliasNamesList.size(); ++aliasIndex) {
                                final FromTable ssFromTable = ssFromItems.get(ssIndex);
                                if (ssFromTable.getTableName().toString().trim().equalsIgnoreCase(aliasNamesList.get(aliasIndex).toString().trim())) {
                                    ssFromTable.setTableName(addSelectStmt.get(aliasNamesList.get(aliasIndex).toString().trim()));
                                    ssFromTable.setAliasName(aliasNamesList.get(aliasIndex).toString().trim());
                                    final SelectQueryStatement ss2 = (SelectQueryStatement)ssFromTable.getTableName();
                                    final Vector ssFromTableFromItems = ss2.getFromClause().getFromItemList();
                                    for (int x = 0; x < ssFromTableFromItems.size(); ++x) {
                                        for (int y = 0; y < aliasNamesList.size(); ++y) {
                                            if (aliasNamesList.get(y).toString().trim().equalsIgnoreCase(ssFromTableFromItems.get(x).toString().trim())) {
                                                final FromTable ss1FromTable = ssFromTableFromItems.get(x);
                                                ss1FromTable.setTableName(addSelectStmt.get(ssFromTableFromItems.get(x).toString().trim()));
                                                ss1FromTable.setAliasName(aliasNamesList.get(y).toString());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        fromTable.setTableName(ss);
                        fromTable.setAliasName(fromTableName);
                    }
                }
            }
        }
        return this.derivedTableQuery = withSelectStmt;
    }
    
    public SwisSQLStatement toANSISQL() throws ConvertException {
        final WithStatement ws = this.copyObjectValues();
        final Vector commonTableExpressionList = ws.getCommonTableExpressionList();
        final Vector convertedCommonTableExpressionList = new Vector();
        for (int i = 0; i < commonTableExpressionList.size(); ++i) {
            CommonTableExpression cte = commonTableExpressionList.get(i);
            cte = cte.toANSISQL(ws, ws);
            convertedCommonTableExpressionList.add(cte);
        }
        ws.setCommonTableExpressionList(convertedCommonTableExpressionList);
        SelectQueryStatement withSelectSQS = ws.getWithSQS();
        withSelectSQS = withSelectSQS.toANSISelect();
        return ws;
    }
    
    public SwisSQLStatement toTeradata() throws ConvertException {
        final WithStatement ws = this.copyObjectValues();
        final Vector commonTableExpressionList = ws.getCommonTableExpressionList();
        final Vector convertedCommonTableExpressionList = new Vector();
        for (int i = 0; i < commonTableExpressionList.size(); ++i) {
            final CommonTableExpression cte = commonTableExpressionList.get(i);
            convertedCommonTableExpressionList.add(cte);
        }
        ws.setCommonTableExpressionList(convertedCommonTableExpressionList);
        SelectQueryStatement withSelectSQS = ws.getWithSQS();
        withSelectSQS = withSelectSQS.toTeradataSelect();
        return ws;
    }
    
    public SwisSQLStatement toTimesTen() throws ConvertException {
        return this;
    }
    
    public SwisSQLStatement toNetezza() throws ConvertException {
        final WithStatement ws = this.copyObjectValues();
        final String withTemp = ws.getWith();
        if (withTemp != null) {
            ws.setWith(null);
        }
        final Vector commonTableExpressionList = ws.getCommonTableExpressionList();
        for (int cteListSize = commonTableExpressionList.size(), i = 0; i < cteListSize; ++i) {
            final CommonTableExpression cte = commonTableExpressionList.get(i);
            final TableObject aliasTO = cte.getViewName();
            aliasTO.toNetezza();
            final ArrayList newColumnList = new ArrayList();
            final ArrayList columnList = cte.getColumnList();
            if (columnList != null) {
                for (int colListInd = 0; colListInd < columnList.size(); ++colListInd) {
                    if (!columnList.get(colListInd).toString().equalsIgnoreCase("(") && !columnList.get(colListInd).toString().equalsIgnoreCase(",") && !columnList.get(colListInd).toString().equalsIgnoreCase(")")) {
                        newColumnList.add(columnList.get(colListInd));
                    }
                }
            }
            final Vector cteSQSSelectColList = cte.getSelectQueryStatement().getSelectStatement().getSelectItemList();
            if (cteSQSSelectColList.size() == newColumnList.size()) {
                for (int k = 0; k < cteSQSSelectColList.size(); ++k) {
                    if (cteSQSSelectColList.get(k) instanceof SelectColumn) {
                        final SelectColumn cteSelCol = cteSQSSelectColList.get(k);
                        cteSelCol.setIsAS("AS");
                        cteSelCol.setAliasName(newColumnList.get(k).toString());
                    }
                }
            }
            this.cteAliasQueryMap.put(aliasTO.toString().toUpperCase(), cte.getSelectQueryStatement().toNetezzaSelect());
        }
        final Enumeration cteEnum = this.cteAliasQueryMap.elements();
        while (cteEnum.hasMoreElements()) {
            SelectQueryStatement cteSQS = cteEnum.nextElement();
            cteSQS = this.convertCTEAliastoDerivedTable(cteSQS);
        }
        final SelectQueryStatement withSelectStmt = ws.getWithSQS().toNetezzaSelect();
        final SelectQueryStatement newWithSelect = this.convertCTEAliastoDerivedTable(withSelectStmt);
        return this.derivedTableQuery = withSelectStmt;
    }
    
    private SelectQueryStatement convertCTEAliastoDerivedTable(final SelectQueryStatement sQuery) {
        final SelectQueryStatement localQuery = sQuery;
        final Vector localQueryFromItems = localQuery.getFromClause().getFromItemList();
        for (int j = 0; j < localQueryFromItems.size(); ++j) {
            final FromTable fromTable = localQueryFromItems.get(j);
            if (fromTable.getTableName() instanceof String) {
                final Vector v = new Vector();
                final String fromTableName = fromTable.getTableName().toString();
                if (this.cteAliasQueryMap.containsKey(fromTableName.toUpperCase())) {
                    fromTable.setTableName(this.cteAliasQueryMap.get(fromTableName.toUpperCase()));
                    fromTable.setAliasName(fromTableName);
                }
            }
            else if (fromTable.getTableName() instanceof SelectQueryStatement) {
                final SelectQueryStatement subQuery = this.convertCTEAliastoDerivedTable((SelectQueryStatement)fromTable.getTableName());
                fromTable.setTableName(subQuery);
            }
        }
        return localQuery;
    }
    
    private WithStatement copyObjectValues() {
        final WithStatement ws = new WithStatement();
        ws.setWith(this.getWith());
        ws.setCommonTableExpressionList(this.getCommonTableExpressionList());
        ws.setWithSQS(this.getWithSQS());
        return ws;
    }
    
    @Override
    public String toString() {
        if (this.derivedTableQuery != null) {
            return this.derivedTableQuery.toString();
        }
        final StringBuffer sb = new StringBuffer();
        if (this.with != null) {
            sb.append(this.with.toUpperCase() + "\n");
        }
        if (this.commonTableExprList != null) {
            for (int i = 0; i < this.commonTableExprList.size(); ++i) {
                sb.append(this.commonTableExprList.get(i).toString());
                if (i != this.commonTableExprList.size() - 1) {
                    sb.append(" ,\n");
                }
            }
        }
        if (this.withSQS != null) {
            sb.append("\n" + this.withSQS.toString() + " ");
        }
        return sb.toString();
    }
    
    @Override
    public String toOracleString() throws ConvertException {
        return this.toOracle().toString();
    }
    
    @Override
    public String toMSSQLServerString() throws ConvertException {
        return this.toMSSQLServer().toString();
    }
    
    @Override
    public String toSybaseString() throws ConvertException {
        return this.toSybase().toString();
    }
    
    @Override
    public String toDB2String() throws ConvertException {
        return this.toDB2().toString();
    }
    
    @Override
    public String toPostgreSQLString() throws ConvertException {
        return this.toPostgreSQL().toString();
    }
    
    @Override
    public String toMySQLString() throws ConvertException {
        return this.toMySQL().toString();
    }
    
    @Override
    public String toANSIString() throws ConvertException {
        return this.toANSISQL().toString();
    }
    
    @Override
    public String toTeradataString() throws ConvertException {
        return this.toTeradata().toString();
    }
    
    @Override
    public String toInformixString() throws ConvertException {
        return this.toInformix().toString();
    }
    
    @Override
    public String toTimesTenString() throws ConvertException {
        return this.toTimesTen().toString();
    }
    
    @Override
    public String toNetezzaString() throws ConvertException {
        return this.toNetezza().toString();
    }
    
    @Override
    public void setCommentClass(final CommentClass commentObject) {
        this.commentObject = commentObject;
    }
    
    @Override
    public CommentClass getCommentClass() {
        return this.commentObject;
    }
    
    @Override
    public UserObjectContext getObjectContext() {
        return this.objectContext;
    }
    
    @Override
    public void setObjectContext(final UserObjectContext obj) {
        this.objectContext = obj;
    }
    
    @Override
    public String removeIndent(final String formattedSqlString) {
        String s_ri = formattedSqlString;
        s_ri = s_ri.replace('\n', ' ');
        s_ri = s_ri.replace('\t', ' ');
        return s_ri;
    }
    
    public SwisSQLStatement toVectorWise() throws ConvertException {
        final WithStatement ws = this.copyObjectValues();
        final String withTemp = ws.getWith();
        if (withTemp != null) {
            ws.setWith(null);
        }
        final Vector commonTableExpressionList = ws.getCommonTableExpressionList();
        final Vector convertedCommonTableExpressionList = new Vector();
        SelectQueryStatement withSelectStmt = ws.getWithSQS();
        withSelectStmt = withSelectStmt.toMySQLSelect();
        final Vector withSelectStmtFromItems = withSelectStmt.getFromClause().getFromItemList();
        final Hashtable addSelectStmt = new Hashtable();
        final Vector aliasNamesList = new Vector();
        for (int i = 0; i < commonTableExpressionList.size(); ++i) {
            final CommonTableExpression cte = commonTableExpressionList.get(i);
            final String viewNameTemp = cte.getViewName().toString();
            final ArrayList newColumnList = new ArrayList();
            final ArrayList columnList = cte.getColumnList();
            if (columnList != null) {
                for (int colListInd = 0; colListInd < columnList.size(); ++colListInd) {
                    if (!columnList.get(colListInd).toString().equalsIgnoreCase("(") && !columnList.get(colListInd).toString().equalsIgnoreCase(",") && !columnList.get(colListInd).toString().equalsIgnoreCase(")")) {
                        newColumnList.add(columnList.get(colListInd));
                    }
                }
            }
            final Vector cteSQSSelectColList = cte.getSelectQueryStatement().getSelectStatement().getSelectItemList();
            if (cteSQSSelectColList.size() == newColumnList.size()) {
                for (int k = 0; k < cteSQSSelectColList.size(); ++k) {
                    if (cteSQSSelectColList.get(k) instanceof SelectColumn) {
                        final SelectColumn cteSelCol = cteSQSSelectColList.get(k);
                        cteSelCol.setIsAS("AS");
                        cteSelCol.setAliasName(newColumnList.get(k).toString());
                    }
                }
            }
            for (int j = 0; j < withSelectStmtFromItems.size(); ++j) {
                final FromTable fromTable = withSelectStmtFromItems.get(j);
                if (fromTable.getTableName() instanceof String) {
                    final Vector v = new Vector();
                    final String fromTableName = fromTable.getTableName().toString();
                    final SelectQueryStatement ss = cte.getSelectQueryStatement().toMySQLSelect();
                    final String hashKey = cte.getViewName().toString();
                    final String temp = cte.getSelectQueryStatement().getFromClause().getFirstElement().toString();
                    addSelectStmt.put(hashKey.trim(), ss);
                    aliasNamesList.add(cte.getViewName().getTableName());
                    if (fromTableName.equalsIgnoreCase(viewNameTemp) && commonTableExpressionList.size() == 1) {
                        fromTable.setTableName(ss);
                        fromTable.setAliasName(fromTableName);
                    }
                    if (commonTableExpressionList.size() > 1 && fromTableName.equalsIgnoreCase(viewNameTemp)) {
                        final Vector ssFromItems = ss.getFromClause().getFromItemList();
                        for (int ssIndex = 0; ssIndex < ssFromItems.size(); ++ssIndex) {
                            for (int aliasIndex = 0; aliasIndex < aliasNamesList.size(); ++aliasIndex) {
                                final FromTable ssFromTable = ssFromItems.get(ssIndex);
                                if (ssFromTable.getTableName().toString().trim().equalsIgnoreCase(aliasNamesList.get(aliasIndex).toString().trim())) {
                                    ssFromTable.setTableName(addSelectStmt.get(aliasNamesList.get(aliasIndex).toString().trim()));
                                    ssFromTable.setAliasName(aliasNamesList.get(aliasIndex).toString().trim());
                                    final SelectQueryStatement ss2 = (SelectQueryStatement)ssFromTable.getTableName();
                                    final Vector ssFromTableFromItems = ss2.getFromClause().getFromItemList();
                                    for (int x = 0; x < ssFromTableFromItems.size(); ++x) {
                                        for (int y = 0; y < aliasNamesList.size(); ++y) {
                                            if (aliasNamesList.get(y).toString().trim().equalsIgnoreCase(ssFromTableFromItems.get(x).toString().trim())) {
                                                final FromTable ss1FromTable = ssFromTableFromItems.get(x);
                                                ss1FromTable.setTableName(addSelectStmt.get(ssFromTableFromItems.get(x).toString().trim()));
                                                ss1FromTable.setAliasName(aliasNamesList.get(y).toString());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        fromTable.setTableName(ss);
                        fromTable.setAliasName(fromTableName);
                    }
                }
            }
        }
        return this.derivedTableQuery = withSelectStmt;
    }
    
    @Override
    public String toVectorWiseString() throws ConvertException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
