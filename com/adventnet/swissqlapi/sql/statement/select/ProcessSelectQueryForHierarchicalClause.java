package com.adventnet.swissqlapi.sql.statement.select;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.ArrayList;
import com.adventnet.swissqlapi.SwisSQLAPI;
import java.util.Hashtable;

public class ProcessSelectQueryForHierarchicalClause
{
    public boolean startWithSubQuery;
    public SelectQueryStatement selectQryStment;
    public String subQuery;
    public String curname;
    public String anonFunName;
    public String funcName;
    public String FuncStr;
    public boolean isitRowNum;
    public boolean hasSubQuery;
    public boolean priorChildcolumnboolean;
    Hashtable startWithConnectByHash;
    
    public ProcessSelectQueryForHierarchicalClause() {
        this.startWithSubQuery = false;
        this.selectQryStment = null;
        this.subQuery = null;
        this.curname = null;
        this.anonFunName = "";
        this.funcName = null;
        this.FuncStr = "";
        this.isitRowNum = false;
        this.hasSubQuery = false;
        this.priorChildcolumnboolean = false;
        this.startWithConnectByHash = new Hashtable();
    }
    
    public Hashtable getStartWithConnectByHashtable() {
        return this.startWithConnectByHash;
    }
    
    public void processSelectQueryWhenTreeIsEncountered(final SelectQueryStatement tsqlSelectQueryStatement, final Hashtable hashTable) {
        String tableName = null;
        String aliasName = null;
        String secondAliasName = null;
        String secondtableName = null;
        final FromClause tsqlFromClause = tsqlSelectQueryStatement.getFromClause();
        String fromTableAsQuery = null;
        final Vector tableItems = tsqlFromClause.getFromItemList();
        for (int t = 0; t < tableItems.size(); ++t) {
            if (tableItems.get(t) instanceof FromTable) {
                final FromTable tsqlFromTable = tableItems.get(t);
                final Object tN = tsqlFromTable.getTableName();
                if (tN instanceof SelectQueryStatement) {
                    final SelectQueryStatement sqs = (SelectQueryStatement)tN;
                    final FromClause fc = sqs.getFromClause();
                    final Vector fcList = fc.getFromItemList();
                    if (fcList != null && fcList.size() > 0) {
                        final FromTable fT = fcList.get(0);
                        if (fT != null) {
                            fromTableAsQuery = fT.getTableName().toString();
                        }
                    }
                }
            }
        }
        if (tableItems != null && tableItems.size() > 0) {
            final FromTable tsqlFromTable2 = tableItems.get(0);
            if (fromTableAsQuery != null) {
                tableName = fromTableAsQuery;
            }
            else {
                tableName = tsqlFromTable2.getTableName().toString();
            }
            aliasName = tsqlFromTable2.getAliasName();
            if (tableItems.size() > 1) {
                final FromTable tsqlFromTable3 = tableItems.get(1);
                secondtableName = tsqlFromTable3.getTableName().toString();
                secondAliasName = tsqlFromTable3.getAliasName();
            }
        }
        final String name = hashTable.get("$name$");
        int i = 0;
        int sizeOfstartWithLeftExp = 0;
        String fName = "";
        final Hashtable tSQLTables = SwisSQLAPI.dataTypesFromMetaDataHT;
        final Hashtable tableColumns = SwisSQLAPI.tableColumnListMetadata;
        final ArrayList orderedColumnsList = tableColumns.get(tableName.toUpperCase());
        Hashtable columnHashtable = tSQLTables.get(tableName.toUpperCase());
        if (columnHashtable == null) {
            columnHashtable = tSQLTables.get(tableName);
        }
        Hashtable secColumnHashtable = null;
        if (tableItems.size() > 1 && secondtableName != null) {
            secColumnHashtable = tSQLTables.get(secondtableName.toUpperCase());
            if (secColumnHashtable == null) {
                secColumnHashtable = tSQLTables.get(secondtableName);
            }
        }
        ++i;
        fName = name + i;
        fName += "ConnectBy";
        int j = 3;
        this.anonFunName = "sp_" + tableName + "_hierarchy";
        ++j;
        this.anonFunName = "sp_" + tableName + "_hierarchy" + j;
        String selectColumnsString = "";
        Vector selectColumnsVector = null;
        String selectColumnStringDecl = "";
        String whereColumnNames = "";
        String tabColName = "";
        String whereColumnNamesWithTypes = null;
        String whereColumnNamesWithAlias = "";
        final SelectStatement ss = tsqlSelectQueryStatement.getSelectStatement();
        final Vector ssVec = ss.getSelectItemList();
        final Vector selColVec = new Vector();
        for (int k = 0; k < ssVec.size(); ++k) {
            if (ssVec.elementAt(k) instanceof SelectColumn) {
                final Vector scVec = ssVec.elementAt(k).getColumnExpression();
                for (int y = 0; y < scVec.size(); ++y) {
                    if (scVec.elementAt(y) instanceof TableColumn) {
                        final String hasrownum = scVec.elementAt(y).getColumnName();
                        selColVec.add(hasrownum);
                        if (hasrownum.equalsIgnoreCase("rownum")) {
                            this.isitRowNum = true;
                        }
                    }
                }
            }
        }
        final WhereExpression we = tsqlSelectQueryStatement.getWhereExpression();
        if (we != null) {
            final Vector weVec = we.getWhereItems();
            if (weVec != null) {
                for (int x = 0; x < weVec.size(); ++x) {
                    whereColumnNamesWithAlias = whereColumnNamesWithAlias + " AND " + weVec.elementAt(x).toString();
                }
                if (whereColumnNamesWithAlias.startsWith(" AND")) {
                    whereColumnNamesWithAlias = whereColumnNamesWithAlias.substring(4, whereColumnNamesWithAlias.length());
                }
            }
            for (int a = 0; a < weVec.size(); ++a) {
                if (weVec.elementAt(a) instanceof WhereItem) {
                    final WhereColumn wc = weVec.elementAt(a).getLeftWhereExp();
                    final Vector wcVec = wc.getColumnExpression();
                    for (int b = 0; b < wcVec.size(); ++b) {
                        if (wcVec.elementAt(b) instanceof TableColumn) {
                            final TableColumn tc = wcVec.get(b);
                            tabColName = tc.getColumnName();
                            if (!selColVec.contains(tabColName) && columnHashtable != null) {
                                whereColumnNames = whereColumnNames + "," + tabColName;
                                String temp1 = columnHashtable.get(tabColName);
                                if (temp1 != null && temp1.toLowerCase().trim().startsWith("number")) {
                                    temp1 = temp1.replaceFirst("number", "numeric");
                                }
                                else if (temp1 != null && temp1.toLowerCase().trim().startsWith("varchar2(")) {
                                    temp1 = temp1.replaceFirst("varchar2", "varchar");
                                }
                                whereColumnNamesWithTypes = whereColumnNamesWithTypes + ", " + tabColName + " " + temp1;
                            }
                            if (tc.getTableName() != null) {
                                tc.setTableName(null);
                            }
                        }
                    }
                }
            }
        }
        String join_Var = null;
        if (tableItems.size() > 1) {
            try {
                final FromClause fromclass = tsqlSelectQueryStatement.getFromClause();
                final Vector fromitemlist = fromclass.getFromItemList();
                join_Var = fromitemlist.get(1).toString();
            }
            catch (final Exception e) {
                System.out.println("Exception thrown while obtaining the JOIN variable if the query has multiple tables");
            }
        }
        whereColumnNames = this.removeCommaFromString(whereColumnNames);
        whereColumnNamesWithTypes = this.removeCommaFromString(whereColumnNamesWithTypes);
        selectColumnsVector = this.getSelectColumnsForStartWithConnectBy(tsqlSelectQueryStatement.getSelectStatement());
        selectColumnsString = this.getSelectColumnsForStartWithConnectByString(selectColumnsVector);
        selectColumnStringDecl = this.getSelectColumnsDeclaration(tableName, selectColumnsVector);
        final HierarchicalQueryClause treeStatement = tsqlSelectQueryStatement.getHierarchicalQueryClause();
        final OrderByStatement obstmt = tsqlSelectQueryStatement.getOrderByStatement();
        String orderByColumns = "";
        String Order = "";
        if (obstmt != null) {
            final String siblings = obstmt.getSiblings();
            if (siblings != null) {
                final Vector vec = obstmt.getOrderItemList();
                for (int u = 0; u < vec.size(); ++u) {
                    if (vec.get(u) instanceof OrderItem) {
                        final OrderItem oI = vec.get(u);
                        final SelectColumn sc = oI.getOrderSpecifier();
                        Order = oI.getOrder();
                        if (sc != null) {
                            final Vector colExp = sc.getColumnExpression();
                            if (colExp != null && !colExp.isEmpty()) {
                                for (int a2 = 0; a2 < colExp.size(); ++a2) {
                                    if (colExp.get(a2) instanceof TableColumn) {
                                        final TableColumn tc2 = colExp.get(a2);
                                        final String tabName = tc2.getColumnName();
                                        if (tabName != null) {
                                            if (Order != null) {
                                                orderByColumns = orderByColumns + ", " + tabName + " " + Order;
                                            }
                                            else {
                                                orderByColumns = orderByColumns + ", " + tabName;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                tsqlSelectQueryStatement.setOrderByStatement(null);
            }
        }
        orderByColumns = this.removeCommaFromString(orderByColumns);
        String childColumn = null;
        String column = null;
        String priorColumn = null;
        Object currentVariable = null;
        String dupliPriorColumn = null;
        String dupliPriorLeftColumn = null;
        String dupliPriorRightColumn = null;
        String dupliChildColumn = null;
        String actualPriorColumn = null;
        String actualColumn = null;
        String startWithColumns = "";
        String leftColumnName = null;
        String rightColumnName = null;
        String additionalColName = null;
        String additionalColNameWithoutType = null;
        String secondTabColName = null;
        String startWithLCol = null;
        String startWithRCol = null;
        boolean isLeftColumn = false;
        String startWithColumnsWithTypes = "";
        String Operator = null;
        String additionalConditions = null;
        String priorLHS = null;
        String priorRHS = null;
        String startwithLHS = null;
        String startwithRHS = null;
        if (treeStatement.getStartWithCondition() != null && treeStatement.getConnectByCondition() != null) {
            final WhereExpression startWithExpression = treeStatement.getStartWithCondition();
            final WhereExpression connectByExpression = treeStatement.getConnectByCondition();
            final Vector whereItemList1 = startWithExpression.getWhereItem();
            final Vector whereItemList2 = connectByExpression.getWhereItem();
            final Vector operatorVec = connectByExpression.getOperator();
            if (operatorVec != null && !operatorVec.isEmpty()) {
                Operator = operatorVec.get(0);
            }
            if (whereItemList1 != null && whereItemList1.size() == 1 && whereItemList2 != null && whereItemList2.size() >= 1) {
                if (whereItemList2.size() > 1 && whereItemList2.get(1) instanceof WhereItem) {
                    final WhereItem wI = whereItemList2.get(1);
                    final String wIOperator = wI.getOperator();
                    final WhereColumn wLC = wI.getLeftWhereExp();
                    final WhereColumn wRC = wI.getRightWhereExp();
                    final Vector wcVec2 = wLC.getColumnExpression();
                    final Vector wcRVec = wRC.getColumnExpression();
                    if (wcVec2 != null && !wcVec2.isEmpty() && wcVec2.get(0) instanceof TableColumn) {
                        final TableColumn tc3 = wcVec2.get(0);
                        additionalConditions = tc3.getColumnName();
                    }
                    if (wIOperator != null && additionalConditions != null) {
                        additionalConditions = additionalConditions + " " + wIOperator;
                    }
                    if (wcRVec != null && !wcRVec.isEmpty()) {
                        if (wcRVec.get(0) instanceof TableColumn) {
                            final TableColumn tc3 = wcRVec.get(0);
                            additionalConditions = additionalConditions + " " + tc3.getColumnName();
                        }
                        else if (wcRVec.get(0) instanceof String) {
                            additionalConditions = additionalConditions + " " + wcRVec.get(0);
                        }
                    }
                    if (Operator != null && additionalConditions != null) {
                        additionalConditions = " " + Operator + " " + additionalConditions;
                    }
                }
                if (whereItemList2.get(0) instanceof WhereItem) {
                    if (whereItemList2.get(0).getOperator1() == null && whereItemList2.get(0).getOperator3() != null) {
                        final WhereColumn connectByLeftWhereColumn = whereItemList2.get(0).getLeftWhereExp();
                        final WhereColumn connectByRightWhereColumn = whereItemList2.get(0).getRightWhereExp();
                        if (connectByRightWhereColumn != null) {
                            final Vector connectByRightExp = connectByRightWhereColumn.getColumnExpression();
                            dupliPriorColumn = connectByRightExp.get(0).toString();
                            final TableColumn tcRHS = connectByRightExp.get(0);
                            priorRHS = tcRHS.getColumnName();
                            dupliPriorRightColumn = connectByRightExp.get(0).toString();
                        }
                        if (connectByLeftWhereColumn != null) {
                            final Vector connectByLeftExp = connectByLeftWhereColumn.getColumnExpression();
                            dupliPriorLeftColumn = connectByLeftExp.get(0).toString();
                            final TableColumn tcLHS = connectByLeftExp.get(0);
                            priorLHS = tcLHS.getColumnName();
                        }
                    }
                    if (whereItemList2.get(0).getOperator1() != null && whereItemList2.get(0).getOperator3() == null) {
                        final WhereColumn connectByLeftWhereColumn = whereItemList2.get(0).getLeftWhereExp();
                        final WhereColumn connectByRightWhereColumn = whereItemList2.get(0).getRightWhereExp();
                        if (connectByLeftWhereColumn != null) {
                            final Vector connectByLeftExp = connectByLeftWhereColumn.getColumnExpression();
                            dupliPriorColumn = connectByLeftExp.get(0).toString();
                            final TableColumn tcRHS = connectByLeftExp.get(0);
                            priorRHS = tcRHS.getColumnName();
                            dupliPriorLeftColumn = connectByLeftExp.get(0).toString();
                            if (connectByLeftExp.get(0) instanceof TableColumn && tableItems.size() > 1) {
                                leftColumnName = connectByLeftExp.get(0).getColumnName();
                                final String lTabName = connectByLeftExp.get(0).getTableName();
                                if (secondAliasName != null && lTabName != null && lTabName.equals(secondAliasName) && leftColumnName != null) {
                                    secondTabColName = leftColumnName;
                                    additionalColName = (additionalColNameWithoutType = lTabName + "_" + leftColumnName);
                                    if (secColumnHashtable != null) {
                                        additionalColName = "," + additionalColName + " " + secColumnHashtable.get(leftColumnName);
                                    }
                                }
                            }
                        }
                        if (connectByRightWhereColumn != null) {
                            final Vector connectByRightExp = connectByRightWhereColumn.getColumnExpression();
                            dupliPriorColumn = connectByRightExp.get(0).toString();
                            dupliPriorRightColumn = connectByRightExp.get(0).toString();
                            final TableColumn tcLHS = connectByRightExp.get(0);
                            priorLHS = tcLHS.getColumnName();
                            if (connectByRightExp.get(0) instanceof TableColumn && tableItems.size() > 1) {
                                rightColumnName = connectByRightExp.get(0).getColumnName();
                                final String rTabName = connectByRightExp.get(0).getTableName();
                                if (secondAliasName != null && rTabName != null && rTabName.equals(secondAliasName) && rightColumnName != null) {
                                    secondTabColName = rightColumnName;
                                    additionalColName = (additionalColNameWithoutType = rTabName + "_" + rightColumnName);
                                    additionalColName = additionalColName + " " + secColumnHashtable.get(rightColumnName);
                                }
                            }
                        }
                    }
                }
                if (whereItemList1.get(0) instanceof WhereItem) {
                    final WhereColumn startWithLeftWhereColumn = whereItemList1.get(0).getLeftWhereExp();
                    final WhereColumn startWithRightWhereColumn = whereItemList1.get(0).getRightWhereExp();
                    Vector startWithLeftVector = new Vector();
                    Vector startWithRightVector = new Vector();
                    if (tableItems.size() > 1) {
                        if (startWithLeftWhereColumn != null) {
                            startWithLeftVector = startWithLeftWhereColumn.getColumnExpression();
                            if (startWithLeftVector != null && startWithLeftVector.size() == 1 && startWithLeftVector.get(0) instanceof TableColumn) {
                                startWithLCol = startWithLeftVector.get(0).getColumnName();
                            }
                        }
                        if (startWithRightWhereColumn != null) {
                            startWithRightVector = startWithRightWhereColumn.getColumnExpression();
                            if (startWithRightVector != null && startWithRightVector.size() == 1 && startWithRightVector.get(0) instanceof TableColumn) {
                                startWithRCol = startWithRightVector.get(0).getColumnName();
                            }
                        }
                    }
                    if (whereItemList1.get(0).getRightWhereSubQuery() != null && whereItemList1.get(0).getRightWhereSubQuery() instanceof SelectQueryStatement) {
                        this.startWithSubQuery = true;
                        if (startWithLeftWhereColumn != null) {
                            final Vector startWithLeftExp = startWithLeftWhereColumn.getColumnExpression();
                            if (startWithLeftExp != null && startWithLeftExp.size() == 1 && startWithLeftExp.get(0) instanceof TableColumn) {
                                sizeOfstartWithLeftExp = startWithLeftExp.size();
                                childColumn = startWithLeftExp.get(0).toString();
                                currentVariable = "";
                                this.hasSubQuery = true;
                                this.subQuery = whereItemList1.get(0).getRightWhereSubQuery().toString();
                            }
                            if (startWithLeftExp != null && startWithLeftExp.size() > 1) {
                                sizeOfstartWithLeftExp = startWithLeftExp.size();
                                for (int z = 0; z < startWithLeftExp.size(); ++z) {
                                    if (startWithLeftExp.get(z) instanceof WhereColumn) {
                                        final Vector wcVec3 = startWithLeftExp.get(z).getColumnExpression();
                                        for (int q = 0; q < wcVec3.size(); ++q) {
                                            if (wcVec3.get(q) instanceof TableColumn) {
                                                dupliChildColumn = wcVec3.get(q).toString();
                                                if (dupliChildColumn.equalsIgnoreCase(dupliPriorLeftColumn)) {
                                                    childColumn = dupliChildColumn;
                                                    actualPriorColumn = dupliPriorLeftColumn;
                                                    actualColumn = dupliPriorRightColumn;
                                                    isLeftColumn = true;
                                                }
                                                else if (dupliChildColumn.equalsIgnoreCase(dupliPriorRightColumn)) {
                                                    childColumn = dupliChildColumn;
                                                    actualPriorColumn = dupliPriorRightColumn;
                                                    actualColumn = dupliPriorLeftColumn;
                                                    isLeftColumn = false;
                                                }
                                                if (!dupliChildColumn.equals(",")) {
                                                    startWithColumns = startWithColumns + ", " + dupliChildColumn;
                                                    if (!startWithColumns.equals("")) {
                                                        startWithColumns = this.removeCommaFromString(startWithColumns);
                                                    }
                                                    if (columnHashtable != null) {
                                                        String temp2 = columnHashtable.get(dupliChildColumn.toUpperCase());
                                                        if (temp2 != null && temp2.toLowerCase().trim().startsWith("number")) {
                                                            temp2 = temp2.replaceFirst("number", "numeric");
                                                        }
                                                        else if (temp2 != null && temp2.toLowerCase().trim().startsWith("varchar2(")) {
                                                            temp2 = temp2.replaceFirst("varchar2", "varchar");
                                                        }
                                                        startWithColumnsWithTypes = startWithColumnsWithTypes + ", " + dupliChildColumn + "_adv" + " " + temp2;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                currentVariable = "";
                                this.hasSubQuery = true;
                                this.subQuery = whereItemList1.get(0).getRightWhereSubQuery().toString();
                            }
                        }
                    }
                    else if (startWithLeftWhereColumn != null) {
                        final Vector startWithLeftExp = startWithLeftWhereColumn.getColumnExpression();
                        Vector startWithRightExp = new Vector();
                        if (startWithRightWhereColumn == null) {
                            startWithRightExp.add("null");
                        }
                        else {
                            startWithRightExp = startWithRightWhereColumn.getColumnExpression();
                        }
                        if (startWithLeftExp != null && startWithRightExp != null && startWithLeftExp.size() == 1 && startWithRightExp.size() == 1) {
                            sizeOfstartWithLeftExp = startWithLeftExp.size();
                            if (startWithLeftExp.get(0) instanceof TableColumn && (startWithRightExp.get(0) instanceof TableColumn || startWithRightExp.get(0) instanceof String)) {
                                childColumn = startWithLeftExp.get(0).toString();
                                final TableColumn tcLHS2 = startWithLeftExp.get(0);
                                startwithLHS = tcLHS2.getColumnName();
                                if (startWithRightExp.get(0) instanceof TableColumn) {
                                    currentVariable = startWithRightExp.get(0).getColumnName();
                                    startwithRHS = startWithRightExp.get(0).getColumnName();
                                }
                                else if (startWithRightExp.get(0) instanceof String) {
                                    currentVariable = startWithRightExp.get(0);
                                }
                                else if (startWithLeftExp.get(0) instanceof TableColumn) {
                                    currentVariable = startWithLeftExp.get(0).getColumnName();
                                }
                                else {
                                    currentVariable = startWithLeftExp.get(0);
                                }
                            }
                        }
                        if (startWithLeftExp != null && startWithRightExp != null && startWithLeftExp.size() > 1 && startWithRightExp.size() == 1) {
                            sizeOfstartWithLeftExp = startWithLeftExp.size();
                            for (int w = 0; w < startWithLeftExp.size(); ++w) {
                                if (startWithLeftExp.get(w) instanceof TableColumn && (startWithRightExp.get(0) instanceof TableColumn || startWithRightExp.get(0) instanceof String)) {
                                    dupliChildColumn = startWithLeftExp.get(w).toString();
                                    if (dupliChildColumn.equalsIgnoreCase(dupliPriorLeftColumn) || dupliChildColumn.equalsIgnoreCase(dupliPriorRightColumn)) {
                                        childColumn = dupliChildColumn;
                                        isLeftColumn = dupliChildColumn.equalsIgnoreCase(dupliPriorLeftColumn);
                                        if (startWithRightExp.get(0) instanceof TableColumn) {
                                            currentVariable = startWithRightExp.get(0).getColumnName();
                                        }
                                        else if (startWithRightExp.get(0) instanceof String) {
                                            currentVariable = startWithRightExp.get(0).toString();
                                        }
                                        else if (startWithLeftExp.get(w) instanceof TableColumn) {
                                            currentVariable = startWithLeftExp.get(w).getColumnName();
                                        }
                                        else {
                                            currentVariable = startWithLeftExp.get(w);
                                        }
                                    }
                                    if (!dupliChildColumn.equals(",")) {
                                        startWithColumns = startWithColumns + ", " + dupliChildColumn;
                                        if (columnHashtable != null) {
                                            String temp3 = columnHashtable.get(dupliChildColumn.toUpperCase());
                                            if (temp3 != null && temp3.toLowerCase().trim().startsWith("number")) {
                                                temp3 = temp3.replaceFirst("number", "numeric");
                                            }
                                            else if (temp3 != null && temp3.toLowerCase().trim().startsWith("varchar2(")) {
                                                temp3 = temp3.replaceFirst("varchar2", "varchar");
                                            }
                                            startWithColumnsWithTypes = startWithColumnsWithTypes + ", " + dupliChildColumn + "_adv" + " " + temp3;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (whereItemList2.get(0) instanceof WhereItem) {
                    if (whereItemList2.get(0).getOperator1() == null && whereItemList2.get(0).getOperator3() != null) {
                        final WhereColumn connectByLeftWhereColumn = whereItemList2.get(0).getLeftWhereExp();
                        final WhereColumn connectByRightWhereColumn = whereItemList2.get(0).getRightWhereExp();
                        if (sizeOfstartWithLeftExp == 1) {
                            if (connectByLeftWhereColumn != null) {
                                final Vector connectByLeftExp = connectByLeftWhereColumn.getColumnExpression();
                                column = connectByLeftExp.get(0).toString();
                            }
                            if (connectByRightWhereColumn != null) {
                                final Vector connectByRightExp = connectByRightWhereColumn.getColumnExpression();
                                priorColumn = connectByRightExp.get(0).toString();
                            }
                        }
                        else if (sizeOfstartWithLeftExp > 1) {
                            priorColumn = actualPriorColumn;
                            column = actualColumn;
                        }
                        if (tableName != null && childColumn != null && column != null && priorColumn != null && currentVariable != null) {
                            if (tableItems.size() == 1) {
                                if (sizeOfstartWithLeftExp == 1) {
                                    if (priorColumn.equalsIgnoreCase(childColumn)) {
                                        this.funcName = "sp_" + tableName + "_hierarchy1";
                                    }
                                    else {
                                        this.funcName = "sp_" + tableName + "_hierarchy2";
                                    }
                                }
                                else if (sizeOfstartWithLeftExp > 1) {
                                    if (isLeftColumn) {
                                        this.funcName = "sp_" + tableName + "_hierarchy1";
                                    }
                                    else {
                                        this.funcName = "sp_" + tableName + "_hierarchy2";
                                    }
                                }
                            }
                            else if (tableItems.size() > 1) {
                                this.funcName = "sp_" + tableName + "_hierarchy3";
                            }
                            if (hashTable.containsKey(currentVariable) && hashTable.get(currentVariable).equals("@" + currentVariable)) {
                                currentVariable = hashTable.get(currentVariable);
                            }
                            this.getSelectQueryStmtForStartWithConnectBy(currentVariable, this.funcName, tsqlSelectQueryStatement);
                        }
                    }
                    if (whereItemList2.get(0).getOperator1() != null && whereItemList2.get(0).getOperator3() == null) {
                        final WhereColumn connectByRightWhereColumn2 = whereItemList2.get(0).getRightWhereExp();
                        final WhereColumn connectByLeftWhereColumn2 = whereItemList2.get(0).getLeftWhereExp();
                        if (sizeOfstartWithLeftExp == 1) {
                            if (connectByLeftWhereColumn2 != null) {
                                final Vector connectByLeftExp = connectByLeftWhereColumn2.getColumnExpression();
                                priorColumn = connectByLeftExp.get(0).toString();
                            }
                            if (connectByRightWhereColumn2 != null) {
                                final Vector connectByRightExp = connectByRightWhereColumn2.getColumnExpression();
                                column = connectByRightExp.get(0).toString();
                            }
                        }
                        else if (sizeOfstartWithLeftExp > 1) {
                            priorColumn = actualPriorColumn;
                            column = actualColumn;
                        }
                        if (tableName != null && childColumn != null && column != null && priorColumn != null && currentVariable != null) {
                            if (sizeOfstartWithLeftExp == 1) {
                                if (tableItems.size() == 1) {
                                    if (priorColumn.equalsIgnoreCase(childColumn)) {
                                        this.funcName = "sp_" + tableName + "_hierarchy1";
                                    }
                                    else {
                                        this.funcName = "sp_" + tableName + "_hierarchy2";
                                    }
                                }
                                else if (tableItems.size() > 1) {
                                    this.funcName = "sp_" + tableName + "_hierarchy3";
                                }
                            }
                            else if (sizeOfstartWithLeftExp > 1) {
                                if (isLeftColumn) {
                                    this.funcName = "sp_" + tableName + "_hierarchy1";
                                }
                                else {
                                    this.funcName = "sp_" + tableName + "_hierarchy2";
                                }
                            }
                            if (hashTable.containsKey(currentVariable) && hashTable.get(currentVariable).equals("@" + currentVariable)) {
                                currentVariable = hashTable.get(currentVariable);
                            }
                            this.getSelectQueryStmtForStartWithConnectBy(currentVariable, this.funcName, tsqlSelectQueryStatement);
                            tsqlSelectQueryStatement.setHierarchicalQueryClause(null);
                        }
                    }
                }
            }
        }
        String connectByString = null;
        if (sizeOfstartWithLeftExp == 1) {
            if (tableItems.size() == 1) {
                if (priorColumn.equalsIgnoreCase(childColumn)) {
                    connectByString = this.StartWithConnectByGenerator(currentVariable, false, tableItems.size(), Operator);
                }
                else {
                    connectByString = this.StartWithConnectByGenerator(currentVariable, true, tableItems.size(), Operator);
                }
            }
            else if (tableItems.size() > 1) {
                connectByString = this.StartWithConnectByGenerator(currentVariable, true, tableItems.size(), Operator);
            }
        }
        else if (sizeOfstartWithLeftExp > 1) {
            if (isLeftColumn) {
                connectByString = this.StartWithConnectByGenerator(currentVariable, false, tableItems.size(), Operator);
            }
            else {
                connectByString = this.StartWithConnectByGenerator(currentVariable, true, tableItems.size(), Operator);
            }
        }
        String columnNames = "";
        String allColumns = "";
        String columnTypes = "";
        String toReturns = "";
        String allParams = "";
        String totalParams = "";
        String columnNamesWithAtSymbol = "";
        String totalColumnsWithAtSymbol = "";
        String columnNamesWithAliases = "";
        final Vector columnNamesVec = new Vector();
        if (columnHashtable != null) {
            for (int m = 0; m < orderedColumnsList.size(); ++m) {
                columnNames = orderedColumnsList.get(m);
                columnNamesVec.add(columnNames);
                if (aliasName != null) {
                    columnNamesWithAliases = columnNamesWithAliases + ", " + aliasName + "." + columnNames;
                }
                allColumns = allColumns + "," + columnNames;
                columnTypes = columnHashtable.get(columnNames);
                if (columnTypes != null && columnTypes.toLowerCase().trim().startsWith("number")) {
                    columnTypes = columnTypes.replaceFirst("number", "numeric");
                }
                else if (columnTypes != null && columnTypes.toLowerCase().trim().startsWith("varchar2(")) {
                    columnTypes = columnTypes.replaceFirst("varchar2", "varchar");
                }
                toReturns = toReturns + ", " + columnNames + " " + columnTypes;
                allParams = "DECLARE @" + columnNames + " " + columnTypes + "\n";
                totalParams = totalParams + " " + allParams;
                columnNamesWithAtSymbol = "@" + columnNames;
                totalColumnsWithAtSymbol = totalColumnsWithAtSymbol + "," + columnNamesWithAtSymbol;
            }
        }
        if (priorColumn.indexOf(".") != -1) {
            final String beforedot = priorColumn.substring(0, priorColumn.indexOf("."));
            final String afterdot = priorColumn.substring(priorColumn.indexOf(".") + 1, priorColumn.length());
            if (aliasName != null && beforedot.equalsIgnoreCase(aliasName)) {
                if (priorLHS != null && afterdot.equalsIgnoreCase(priorLHS)) {
                    priorColumn = priorLHS;
                }
                else if (priorRHS != null && afterdot.equalsIgnoreCase(priorRHS)) {
                    priorColumn = priorRHS;
                }
            }
        }
        if (childColumn.indexOf(".") != -1) {
            final String beforedot = childColumn.substring(0, childColumn.indexOf("."));
            final String afterdot = childColumn.substring(childColumn.indexOf(".") + 1, childColumn.length());
            if (aliasName != null && beforedot.equalsIgnoreCase(aliasName) && startwithLHS != null && afterdot.equalsIgnoreCase(startwithLHS)) {
                childColumn = startwithLHS;
            }
        }
        if (secondAliasName != null && secondTabColName != null) {
            columnNamesWithAliases = columnNamesWithAliases + "," + secondAliasName + "." + secondTabColName;
        }
        columnNamesWithAliases = this.removeCommaFromString(columnNamesWithAliases);
        totalColumnsWithAtSymbol = this.removeCommaFromString(totalColumnsWithAtSymbol);
        allColumns = this.removeCommaFromString(allColumns);
        toReturns = this.removeCommaFromString(toReturns);
        if (aliasName != null && tableItems.size() > 1) {
            columnNamesWithAliases = "INSERT INTO @CTEST1 select NULL,NULL," + columnNamesWithAliases + " FROM " + tableName + " " + aliasName + " ";
        }
        if (join_Var != null && tableItems.size() > 1) {
            columnNamesWithAliases += join_Var;
        }
        if (aliasName != null) {
            connectByString = connectByString.replaceAll("TableName_SUBSTITUTE_WITH_ACTUAL_TABLENAME", tableName + " " + aliasName);
        }
        else {
            connectByString = connectByString.replaceAll("TableName_SUBSTITUTE_WITH_ACTUAL_TABLENAME", tableName);
        }
        if (orderByColumns != null && !orderByColumns.equals("")) {
            connectByString = connectByString.replaceAll("ORDER_SIBLINGS_BY_COLUMNS", "ORDER BY " + orderByColumns);
        }
        else {
            connectByString = connectByString.replaceAll("ORDER_SIBLINGS_BY_COLUMNS", "");
        }
        connectByString = connectByString.replaceAll("ChildName_SUBSTITUTE_WITH_ACTUAL_COLUMNNAME", childColumn);
        connectByString = connectByString.replaceAll("Column_SUBSTITUTE_WITH_ACTUAL_COLUMNNAME", column);
        connectByString = connectByString.replaceAll("PriorC_SUBSTITUTE_WITH_ACTUAL_COLUMNNAME", priorColumn);
        connectByString = connectByString.replaceAll("SUBSTITUTE_SELECTCOLUMNS", selectColumnsString);
        if (tableItems.size() == 1) {
            connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_COLUMNSDECL", toReturns);
            connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_ALLCOLUMNS", allColumns);
        }
        else if (tableItems.size() > 1) {
            if (additionalColName != null) {
                connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_COLUMNSDECL", toReturns + additionalColName);
            }
            if (additionalColNameWithoutType != null) {
                connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_ALLCOLUMNS", allColumns + "," + additionalColNameWithoutType);
                connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_ADDITIONAL_COLUMNNAME", additionalColNameWithoutType);
            }
            if (startWithLCol != null) {
                connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_SW_L_COLUMNNAME", startWithLCol);
            }
            if (rightColumnName != null) {
                connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_CB_R_COLUMNNAME", rightColumnName);
            }
            if (columnNamesWithAliases != null) {
                connectByString = connectByString.replaceAll("INSERTING_INTO_CTEST1_WITH_JOINS", columnNamesWithAliases);
            }
        }
        if (Operator != null && additionalConditions != null) {
            connectByString = connectByString.replaceAll("ADDITIONAL_CONDITIONS", additionalConditions);
        }
        connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_TABLENAME", tableName);
        connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_STARTWITH_COLUMNNAME", childColumn);
        connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_PRIOR_COLUMNNAME", priorColumn);
        connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_VARIABLE_DECL", totalParams);
        connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_ALLVARIABLES", totalColumnsWithAtSymbol);
        connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_PRIOR1_COLUMNNAME", column);
        final String funcName1 = "sp_" + tableName;
        if (sizeOfstartWithLeftExp == 1) {
            if (tableItems.size() == 1) {
                if (priorColumn.equalsIgnoreCase(childColumn)) {
                    connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_PROCEDURE_NAME", funcName1 + "_hierarchy1");
                }
                else {
                    connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_PROCEDURE_NAME", funcName1 + "_hierarchy2");
                }
            }
            else if (tableItems.size() > 1) {
                connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_PROCEDURE_NAME", funcName1 + "_hierarchy3");
            }
        }
        else if (sizeOfstartWithLeftExp > 1) {
            if (isLeftColumn) {
                connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_PROCEDURE_NAME", funcName1 + "_hierarchy1");
            }
            else {
                connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_PROCEDURE_NAME", funcName1 + "_hierarchy2");
            }
        }
        String selectedColumns = "";
        String selectedColumnType = "";
        String selectedColumnsWithTypes = "";
        String selectedColumnsNumbersRemoved = "";
        if (!selectColumnsVector.contains("*")) {
            if (selectColumnsString.indexOf(",") == -1 && columnHashtable != null) {
                selectedColumns = selectColumnsString;
                selectedColumnType = columnHashtable.get(selectedColumns.toUpperCase().trim());
                if (selectedColumnType != null && selectedColumnType.toLowerCase().trim().startsWith("number")) {
                    selectedColumnType = selectedColumnType.replaceFirst("number", "numeric");
                }
                else if (selectedColumnType != null && selectedColumnType.toLowerCase().trim().startsWith("varchar2(")) {
                    selectedColumnType = selectedColumnType.replaceFirst("varchar2", "varchar");
                }
                if (!selectedColumns.trim().startsWith("'")) {
                    selectedColumnsWithTypes = selectedColumns + " " + selectedColumnType;
                    selectedColumnsNumbersRemoved = selectedColumnsNumbersRemoved + ", " + selectedColumns;
                }
            }
            else {
                final StringTokenizer stToken = new StringTokenizer(selectColumnsString, ",");
                if (columnHashtable != null) {
                    while (stToken.hasMoreTokens()) {
                        selectedColumns = stToken.nextToken();
                        try {
                            final int integ = Integer.parseInt(selectedColumns.trim());
                        }
                        catch (final Exception NumberFormatException) {
                            selectedColumnType = columnHashtable.get(selectedColumns.toUpperCase().trim());
                            if (selectedColumnType != null && selectedColumnType.toLowerCase().trim().startsWith("number")) {
                                selectedColumnType = selectedColumnType.replaceFirst("number", "numeric");
                            }
                            else if (selectedColumnType != null && selectedColumnType.toLowerCase().trim().startsWith("varchar2(")) {
                                selectedColumnType = selectedColumnType.replaceFirst("varchar2", "varchar");
                            }
                            if ((selectedColumns.equalsIgnoreCase("level") || selectedColumns.toUpperCase().startsWith("level ") || selectedColumns.toLowerCase().startsWith(" level ")) && selectedColumnType == null) {
                                selectedColumns = "level";
                                selectedColumnType = "int";
                            }
                            if (selectedColumns.equalsIgnoreCase(" null")) {
                                continue;
                            }
                            if (selectedColumns.trim().startsWith("'")) {
                                continue;
                            }
                            selectedColumnsWithTypes = selectedColumnsWithTypes + ", " + selectedColumns + " " + selectedColumnType;
                            selectedColumnsNumbersRemoved = selectedColumnsNumbersRemoved + ", " + selectedColumns;
                        }
                    }
                }
            }
        }
        else if (selectColumnsVector.contains("*")) {
            if (columnHashtable != null) {
                final Enumeration enum1 = columnHashtable.keys();
                String nextEle = "";
                while (enum1.hasMoreElements()) {
                    nextEle = enum1.nextElement();
                    selectedColumns = selectedColumns + ", " + nextEle;
                    selectedColumnType = columnHashtable.get(nextEle).toUpperCase().trim();
                    if (selectedColumnType != null && selectedColumnType.toLowerCase().trim().startsWith("number")) {
                        selectedColumnType = selectedColumnType.replaceFirst("number", "numeric");
                    }
                    else if (selectedColumnType != null && selectedColumnType.toLowerCase().trim().startsWith("varchar2(")) {
                        selectedColumnType = selectedColumnType.replaceFirst("varchar2", "varchar");
                    }
                    if (!nextEle.trim().startsWith("'")) {
                        selectedColumnsWithTypes = selectedColumnsWithTypes + ", " + nextEle + " " + selectedColumnType;
                    }
                }
                if (!selectedColumns.equals("")) {
                    selectedColumns = (selectColumnsString = selectedColumns.substring(1));
                }
                if (!selectedColumnsWithTypes.equals("*")) {
                    selectedColumnsWithTypes = selectedColumnsWithTypes.substring(1);
                }
            }
            selectedColumnsNumbersRemoved = selectedColumnsNumbersRemoved + ", " + selectedColumns;
        }
        selectedColumnsWithTypes = this.removeCommaFromString(selectedColumnsWithTypes);
        startWithColumnsWithTypes = this.removeCommaFromString(startWithColumnsWithTypes);
        selectedColumnsNumbersRemoved = this.removeCommaFromString(selectedColumnsNumbersRemoved);
        if ((name == null || name.equals("Anon")) && this.hasSubQuery) {
            this.FuncStr = this.FuncStr + " CREATE FUNCTION " + this.anonFunName + "() \n";
            if (whereColumnNamesWithTypes != null) {
                this.FuncStr = this.FuncStr + "returns @result TABLE(" + selectedColumnsWithTypes + ", " + whereColumnNamesWithTypes + ")" + " \n" + "AS \n";
            }
            else {
                this.FuncStr = this.FuncStr + "returns @result TABLE(" + selectedColumnsWithTypes + ")" + " \n" + "AS \n";
            }
            if (sizeOfstartWithLeftExp > 1) {
                this.FuncStr = this.FuncStr + "BEGIN \n" + "DECLARE  @adv_test TABLE(" + startWithColumnsWithTypes + ") \n";
            }
            else {
                this.FuncStr = this.FuncStr + "BEGIN \n" + "DECLARE @adv_test TABLE(column1 varchar(255)) \n";
            }
            this.FuncStr = this.FuncStr + "INSERT INTO @adv_test " + this.subQuery + " \n" + "DECLARE @adv_storevar varchar(255) \n";
            if (whereColumnNamesWithTypes != null) {
                this.FuncStr = this.FuncStr + "DECLARE  @adv_test1 TABLE(" + selectedColumnsWithTypes + ", " + whereColumnNamesWithTypes + ") \n";
            }
            else {
                this.FuncStr = this.FuncStr + "DECLARE  @adv_test1 TABLE(" + selectedColumnsWithTypes + ") \n";
            }
            if (sizeOfstartWithLeftExp > 1) {
                this.FuncStr = this.FuncStr + "DECLARE adv_cur CURSOR FOR select distinct(" + childColumn + "_adv) " + "from @adv_test \n";
            }
            else {
                this.FuncStr += "DECLARE adv_cur CURSOR FOR select distinct(column1) from @adv_test \n";
            }
            this.FuncStr = this.FuncStr + " OPEN adv_cur \n" + "FETCH  NEXT FROM adv_cur INTO  @adv_storevar \n" + "WHILE ((@@FETCH_STATUS = 0) ) \n" + "BEGIN \n" + "INSERT INTO @adv_test1 SELECT " + selectedColumnsNumbersRemoved + whereColumnNames + " FROM DBO." + this.funcName + "(@adv_storevar) \n" + "FETCH  NEXT FROM adv_cur INTO  @adv_storevar \n" + "END \n" + "CLOSE adv_cur \n" + "deallocate adv_cur \n" + "INSERT into @result(" + selectedColumnsNumbersRemoved + ") select " + selectedColumnsNumbersRemoved + " from @adv_test1 \n" + "RETURN \n" + "END \n" + "GO \n";
            this.startWithConnectByHash.put(this.anonFunName, this.FuncStr);
        }
        this.startWithConnectByHash.put(this.funcName, connectByString);
    }
    
    public String removeCommaFromString(String str) {
        if (str != null) {
            if (str.startsWith(",")) {
                str = str.substring(1, str.length());
            }
            if (str.endsWith(",")) {
                str = str.substring(0, str.length() - 1);
            }
        }
        return str;
    }
    
    public String getTableNameFromSQSTMT(final SelectQueryStatement sqstmt) {
        String nameOfTable = null;
        final FromClause fromclass = sqstmt.getFromClause();
        final Vector fromitemlist = fromclass.getFromItemList();
        final FromTable fromtable = fromitemlist.get(0);
        final Object ob = fromtable.getTableName();
        nameOfTable = ob.toString();
        return nameOfTable;
    }
    
    public boolean isTableNameSQS(final SelectQueryStatement sqstmt) {
        final FromClause fromclass = sqstmt.getFromClause();
        final Vector fromitemlist = fromclass.getFromItemList();
        final FromTable fromtable = fromitemlist.get(0);
        final Object ob = fromtable.getTableName();
        return ob instanceof SelectQueryStatement;
    }
    
    public SelectQueryStatement getSelectQueryStmtForStartWithConnectBy(final Object currentVariable, String functionName, final SelectQueryStatement tsqlSelectQueryStatement) {
        final SelectStatement selectStatement = new SelectStatement();
        selectStatement.setSelectClause("SELECT");
        final Vector newItemList = new Vector();
        functionName = this.funcName;
        if (this.hasSubQuery) {
            functionName = this.anonFunName;
        }
        final int fromIndex = 0;
        final int selectIndex = 0;
        Vector queryColumnsVector = new Vector();
        final String queryColumnsString = "";
        String tName = null;
        final Hashtable tSQLTables = SwisSQLAPI.dataTypesFromMetaDataHT;
        tName = this.getTableNameFromSQSTMT(tsqlSelectQueryStatement);
        if (this.isTableNameSQS(tsqlSelectQueryStatement)) {
            final FromClause fromclass = tsqlSelectQueryStatement.getFromClause();
            final Vector fromitemlist = fromclass.getFromItemList();
            final FromTable fromtable = fromitemlist.get(0);
            final Object ob = fromtable.getTableName();
            if (ob instanceof SelectQueryStatement) {
                final SelectQueryStatement tableAsSQS = (SelectQueryStatement)ob;
                final WhereExpression we = tableAsSQS.getWhereExpression();
                if (we != null) {
                    final WhereExpression mainWhereExp = tsqlSelectQueryStatement.getWhereExpression();
                    if (mainWhereExp != null) {
                        mainWhereExp.addOperator("AND");
                        mainWhereExp.addWhereExpression(we);
                    }
                    else {
                        tsqlSelectQueryStatement.setWhereExpression(we);
                    }
                }
            }
        }
        Hashtable columnHashtable = tSQLTables.get(tName.toUpperCase());
        if (columnHashtable == null) {
            columnHashtable = tSQLTables.get(tName);
        }
        queryColumnsVector = tsqlSelectQueryStatement.getSelectStatement().getSelectItemList();
        if (queryColumnsVector != null && !queryColumnsVector.isEmpty()) {
            for (int g = 0; g < queryColumnsVector.size(); ++g) {
                if (queryColumnsVector.get(g) instanceof SelectColumn) {
                    final Vector whereexp = queryColumnsVector.get(g).getColumnExpression();
                    if (whereexp != null) {
                        for (int h = 0; h < whereexp.size(); ++h) {
                            if (whereexp.get(h) instanceof TableColumn) {
                                final TableColumn tc = whereexp.get(h);
                                if (tc.getTableName() != null) {
                                    tc.setTableName(null);
                                }
                            }
                        }
                    }
                }
            }
        }
        final SelectStatement selstmt = tsqlSelectQueryStatement.getSelectStatement();
        String distinctStr = null;
        if (selstmt.getSelectQualifier() != null) {
            distinctStr = selstmt.getSelectQualifier();
            if (distinctStr.equalsIgnoreCase("DISTINCT")) {
                selectStatement.setSelectQualifier("DISTINCT");
            }
        }
        if (queryColumnsVector.size() == 1) {
            if (queryColumnsVector.get(0) instanceof SelectColumn) {
                final Vector whereEXP = queryColumnsVector.get(0).getColumnExpression();
                if (whereEXP != null && whereEXP.size() == 1) {
                    if (whereEXP.contains("*")) {
                        if (columnHashtable != null) {
                            int size = 0;
                            final Enumeration enum1 = columnHashtable.keys();
                            String nextEle = "";
                            while (enum1.hasMoreElements()) {
                                ++size;
                                final SelectColumn sc = new SelectColumn();
                                final TableColumn tc2 = new TableColumn();
                                final Vector ColEx = new Vector();
                                nextEle = enum1.nextElement();
                                tc2.setColumnName(nextEle);
                                ColEx.add(tc2);
                                sc.setColumnExpression(ColEx);
                                if (size < columnHashtable.size()) {
                                    sc.setEndsWith(",");
                                }
                                newItemList.add(sc);
                            }
                        }
                        selectStatement.setSelectItemList(newItemList);
                    }
                    else if (!whereEXP.contains("*")) {
                        final Vector selVector1 = selstmt.getSelectItemList();
                        selectStatement.setSelectItemList(selVector1);
                    }
                }
            }
        }
        else {
            final Vector selVector2 = selstmt.getSelectItemList();
            selectStatement.setSelectItemList(selVector2);
        }
        tsqlSelectQueryStatement.setSelectStatement(selectStatement);
        final FromClause newFromClause = new FromClause();
        newFromClause.setFromClause("FROM");
        final FunctionCalls functionCall = new FunctionCalls();
        final TableColumn tableColumn = new TableColumn();
        tableColumn.setColumnName(functionName);
        functionCall.setFunctionName(tableColumn);
        final SelectColumn fromSelectColumn = new SelectColumn();
        final Vector fromArgumentsVector = new Vector();
        fromArgumentsVector.add(currentVariable);
        fromSelectColumn.setColumnExpression(fromArgumentsVector);
        final Vector fromClauseSelectItemVector = new Vector();
        fromClauseSelectItemVector.add(fromSelectColumn);
        functionCall.setFunctionArguments(fromClauseSelectItemVector);
        final Vector fromClauseVector = new Vector();
        final FromTable newFromTable = new FromTable();
        newFromTable.setOuterOpenBrace(null);
        newFromTable.setOuterClosedBrace(null);
        newFromTable.setTableName(functionCall);
        fromClauseVector.add(newFromTable);
        newFromClause.setFromItemList(fromClauseVector);
        tsqlSelectQueryStatement.setFromClause(newFromClause);
        tsqlSelectQueryStatement.setHierarchicalQueryClause(null);
        return tsqlSelectQueryStatement;
    }
    
    private Vector getSelectColumnsForStartWithConnectBy(final SelectStatement selectStatement) {
        final Vector toReturnVector = new Vector();
        final Vector selectItemsVector = selectStatement.getSelectItemList();
        for (int i = 0; i < selectItemsVector.size(); ++i) {
            final SelectColumn selectColumn = selectItemsVector.elementAt(i);
            String selectColumnString = selectColumn.toString().trim();
            if (selectColumnString.endsWith(",")) {
                selectColumnString = selectColumnString.substring(0, selectColumnString.length() - 1);
            }
            toReturnVector.addElement(selectColumnString);
        }
        return toReturnVector;
    }
    
    private String getSelectColumnsForStartWithConnectByString(final Vector selectColumnsStringVector) {
        String toReturnSelectString = "";
        for (int i = 0; i < selectColumnsStringVector.size(); ++i) {
            final String selectColumnAsString = selectColumnsStringVector.elementAt(i);
            toReturnSelectString = toReturnSelectString + ", " + selectColumnAsString.toString();
        }
        toReturnSelectString = toReturnSelectString.substring(1);
        return toReturnSelectString;
    }
    
    private String getSelectColumnsDeclaration(final String tableName, final Vector selectColumnsStringVector) {
        String toReturn = "";
        final Hashtable tSQLTables = SwisSQLAPI.dataTypesFromMetaDataHT;
        final Hashtable columnHashtable = tSQLTables.get(tableName.toUpperCase());
        final Hashtable tableColumns = SwisSQLAPI.tableColumnListMetadata;
        final ArrayList orderedColumnsList = tableColumns.get(tableName.toUpperCase());
        if (columnHashtable != null) {
            final Enumeration enum2 = columnHashtable.keys();
            String columnName = "";
            String columnType = "";
            for (int i = 0; i < selectColumnsStringVector.size(); ++i) {
                if (selectColumnsStringVector.elementAt(0).equals("*")) {
                    for (int n = 0; n < orderedColumnsList.size(); ++n) {
                        columnName = orderedColumnsList.get(n);
                        columnType = columnHashtable.get(columnName.toUpperCase());
                        if (columnType != null && columnType.toLowerCase().trim().startsWith("number")) {
                            columnType = columnType.replaceFirst("number", "numeric");
                        }
                        else if (columnType != null && columnType.toLowerCase().trim().startsWith("varchar2(")) {
                            columnType = columnType.replaceFirst("varchar2", "varchar");
                        }
                        toReturn = toReturn + ", " + columnName + " " + columnType;
                    }
                }
                else {
                    columnName = selectColumnsStringVector.elementAt(i);
                    columnName = selectColumnsStringVector.elementAt(i);
                    columnType = columnHashtable.get(columnName.toUpperCase());
                    if (columnType != null && columnType.toLowerCase().trim().startsWith("number")) {
                        columnType = columnType.replaceFirst("number", "numeric");
                    }
                    else if (columnType != null && columnType.toLowerCase().trim().startsWith("varchar2(")) {
                        columnType = columnType.replaceFirst("varchar2", "varchar");
                    }
                    toReturn = toReturn + ", " + columnName + " " + columnType;
                }
            }
        }
        else {
            final String columnType2 = "VARCHAR(100)";
            String columnName = "";
            for (int j = 0; j < selectColumnsStringVector.size(); ++j) {
                columnName = selectColumnsStringVector.elementAt(j);
                toReturn = toReturn + ", " + columnName + " " + columnType2;
            }
        }
        toReturn = toReturn.substring(1);
        return toReturn;
    }
    
    public String StartWithConnectByGenerator(final Object columnVariable, final boolean isPriorChild, final int tableItemSize, final String additionalConditions) {
        final StringBuffer generatedOutput = new StringBuffer();
        try {
            FileInputStream fis = null;
            if (tableItemSize == 1) {
                if (isPriorChild) {
                    if (additionalConditions != null) {
                        fis = new FileInputStream("conf/TreeTraversal_PRIOR_ADDI.conf");
                    }
                    else {
                        fis = new FileInputStream("conf/TreeTraversal.conf");
                    }
                }
                else {
                    fis = new FileInputStream("conf/TreeTraversal1.conf");
                }
            }
            else if (tableItemSize > 1) {
                fis = new FileInputStream("conf/TreeTraversal2.conf");
            }
            final InputStreamReader isr = new InputStreamReader(fis);
            final BufferedReader br = new BufferedReader(isr);
            if (columnVariable != null || this.startWithSubQuery) {
                generatedOutput.append("\n");
                for (String comment = br.readLine(); comment != null; comment = br.readLine()) {
                    generatedOutput.append("\t");
                    generatedOutput.append(comment + "\n");
                }
            }
        }
        catch (final FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
        }
        return generatedOutput.toString();
    }
    
    public String getStartWithValue(final SelectQueryStatement tsqlSelectQueryStatement) {
        String childColumn = null;
        final String column = null;
        final String priorColumn = null;
        String currentVariable = null;
        try {
            final HierarchicalQueryClause treeStatement = tsqlSelectQueryStatement.getHierarchicalQueryClause();
            if (treeStatement.getStartWithCondition() != null && treeStatement.getConnectByCondition() != null) {
                final WhereExpression startWithExpression = treeStatement.getStartWithCondition();
                final WhereExpression connectByExpression = treeStatement.getConnectByCondition();
                final Vector whereItemList1 = startWithExpression.getWhereItem();
                final Vector whereItemList2 = connectByExpression.getWhereItem();
                if (whereItemList1 != null && whereItemList1.size() == 1 && whereItemList2 != null && whereItemList2.size() == 1 && whereItemList1.get(0) instanceof WhereItem) {
                    final WhereColumn startWithLeftWhereColumn = whereItemList1.get(0).getLeftWhereExp();
                    final WhereColumn startWithRightWhereColumn = whereItemList1.get(0).getRightWhereExp();
                    if (whereItemList1.get(0).getRightWhereSubQuery() != null && whereItemList1.get(0).getRightWhereSubQuery() instanceof SelectQueryStatement) {
                        this.startWithSubQuery = true;
                        this.selectQryStment = whereItemList1.get(0).getRightWhereSubQuery();
                        final SelectStatement selectStmnt = this.selectQryStment.getSelectStatement();
                        selectStmnt.setSelectQualifier("DISTINCT");
                        if (startWithLeftWhereColumn != null) {
                            final Vector startWithLeftExp = startWithLeftWhereColumn.getColumnExpression();
                            if (startWithLeftExp != null && startWithLeftExp.size() == 1 && startWithLeftExp.get(0) instanceof TableColumn) {
                                childColumn = startWithLeftExp.get(0).toString();
                            }
                        }
                    }
                    else if (startWithLeftWhereColumn != null) {
                        final Vector startWithLeftExp2 = startWithLeftWhereColumn.getColumnExpression();
                        final Vector startWithRightExp = startWithRightWhereColumn.getColumnExpression();
                        if (startWithLeftExp2 != null && startWithRightExp != null && startWithLeftExp2.size() == 1 && startWithRightExp.size() == 1 && startWithLeftExp2.get(0) instanceof TableColumn && (startWithRightExp.get(0) instanceof TableColumn || startWithRightExp.get(0) instanceof String)) {
                            childColumn = startWithLeftExp2.get(0).toString();
                            if (startWithRightExp.get(0) instanceof TableColumn) {
                                currentVariable = startWithRightExp.get(0).toString();
                            }
                            else if (startWithRightExp.get(0) instanceof String) {
                                currentVariable = startWithRightExp.get(0).toString();
                            }
                            else if (startWithLeftExp2.get(0) instanceof TableColumn) {
                                currentVariable = startWithLeftExp2.get(0).toString();
                            }
                            else {
                                currentVariable = startWithLeftExp2.get(0);
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return currentVariable;
    }
}
