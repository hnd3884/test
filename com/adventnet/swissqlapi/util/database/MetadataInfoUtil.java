package com.adventnet.swissqlapi.util.database;

import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.delete.DeleteQueryStatement;
import java.util.Enumeration;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.statement.update.TableExpression;
import java.util.Map;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.update.TableClause;
import com.adventnet.swissqlapi.sql.statement.update.UpdateQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import java.util.Hashtable;

public class MetadataInfoUtil
{
    public static Hashtable tempTableMetadata;
    
    public static String getDatatypeName(final SwisSQLStatement fromSWS, final TableColumn tableColumnObj) {
        Vector oldFromTables = new Vector();
        final Vector fromTables = new Vector();
        if (fromSWS != null) {
            if (fromSWS instanceof SelectQueryStatement) {
                final SelectQueryStatement from_sqs = (SelectQueryStatement)fromSWS;
                if (from_sqs.getFromClause() != null) {
                    oldFromTables = from_sqs.getFromClause().getFromItemList();
                }
            }
            else if (fromSWS instanceof UpdateQueryStatement) {
                final UpdateQueryStatement fromUQS = (UpdateQueryStatement)fromSWS;
                final TableExpression tExpr = fromUQS.getTableExpression();
                if (tExpr != null) {
                    final ArrayList tabClauseList = tExpr.getTableClauseList();
                    if (tabClauseList != null) {
                        for (int i = 0; i < tabClauseList.size(); ++i) {
                            final Object obj = tabClauseList.get(i);
                            if (obj instanceof TableClause) {
                                final TableObject tabObj = ((TableClause)obj).getTableObject();
                                final String tableName = tabObj.getTableName();
                                fromTables.add(tableName);
                            }
                        }
                    }
                }
            }
            else if (tableColumnObj.getTableName() != null) {
                fromTables.add(tableColumnObj.getTableName().toString());
            }
        }
        else if (tableColumnObj.getTableName() != null) {
            fromTables.add(tableColumnObj.getTableName().toString());
        }
        Enumeration enum1 = null;
        if (oldFromTables != null) {
            enum1 = oldFromTables.elements();
        }
        if (enum1 != null) {
            while (enum1.hasMoreElements()) {
                final Object obj2 = enum1.nextElement();
                String tableName2 = null;
                if (obj2 instanceof FromTable && ((FromTable)obj2).getTableName() != null) {
                    tableName2 = ((FromTable)obj2).getTableName().toString();
                }
                if (tableName2 != null && tableName2.trim().length() > 0) {
                    final int index = tableName2.lastIndexOf(".");
                    if (index != -1) {
                        tableName2 = tableName2.substring(index + 1, tableName2.length());
                    }
                    fromTables.add(tableName2);
                }
            }
        }
        try {
            String columnName = null;
            final String tableColumn = tableColumnObj.getColumnName();
            if (tableColumn.trim().length() > 0) {
                final int index = tableColumn.lastIndexOf(".");
                if (index != -1) {
                    columnName = tableColumn.substring(index + 1, tableColumn.length());
                }
                else {
                    columnName = tableColumn;
                }
                if ((columnName.startsWith("\"") || columnName.startsWith("[") || columnName.startsWith("`")) && columnName.length() > 2) {
                    columnName = columnName.substring(1, columnName.length() - 1);
                }
            }
            String tableName3 = tableColumnObj.getTableName();
            if (tableName3 != null) {
                if ((tableName3.startsWith("\"") || tableName3.startsWith("[") || tableName3.startsWith("`")) && tableName3.length() > 2) {
                    tableName3 = tableName3.substring(1, tableName3.length() - 1);
                }
                final Hashtable colDatatypeTable = (Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.dataTypesFromMetaDataHT, tableName3);
                if (colDatatypeTable != null) {
                    final String dataType = (String)CastingUtil.getValueIgnoreCase(colDatatypeTable, columnName);
                    if (dataType != null) {
                        return dataType;
                    }
                }
            }
            final Enumeration enum2 = fromTables.elements();
            if (enum2 != null) {
                while (enum2.hasMoreElements()) {
                    final Object obj3 = enum2.nextElement();
                    String table = obj3.toString().trim().toUpperCase();
                    if ((table.startsWith("\"") || table.startsWith("[") || table.startsWith("`")) && table.length() > 2) {
                        table = table.substring(1, table.length() - 1);
                    }
                    final Hashtable colDatatypeTable2 = (Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.dataTypesFromMetaDataHT, table);
                    if (colDatatypeTable2 != null) {
                        final String dataType2 = (String)CastingUtil.getValueIgnoreCase(colDatatypeTable2, columnName);
                        if (dataType2 != null) {
                            return dataType2;
                        }
                        continue;
                    }
                }
            }
            if (fromSWS == null) {
                final Enumeration enum3 = SwisSQLAPI.dataTypesFromMetaDataHT.keys();
                if (enum3 != null) {
                    while (enum3.hasMoreElements()) {
                        final Hashtable columnDatatypeTable = (Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.dataTypesFromMetaDataHT, enum3.nextElement().toString());
                        if (columnDatatypeTable != null) {
                            final String dataType3 = (String)CastingUtil.getValueIgnoreCase(columnDatatypeTable, columnName);
                            if (dataType3 != null) {
                                return dataType3;
                            }
                            continue;
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static FromTable getTableOfColumn(final SwisSQLStatement fromSWS, final TableColumn tc) {
        if (fromSWS != null) {
            if (fromSWS instanceof SelectQueryStatement) {
                final SelectQueryStatement fromSQS = (SelectQueryStatement)fromSWS;
                Vector oldFromTables = new Vector();
                if (fromSQS.getFromClause() != null) {
                    oldFromTables = fromSQS.getFromClause().getFromItemList();
                }
                return getFromTable(oldFromTables, tc);
            }
            if (fromSWS instanceof UpdateQueryStatement) {
                final UpdateQueryStatement fromUQS = (UpdateQueryStatement)fromSWS;
                String columnName = tc.getColumnName();
                if ((columnName.startsWith("\"") || columnName.startsWith("[") || columnName.startsWith("`")) && columnName.length() > 2) {
                    columnName = columnName.substring(1, columnName.length() - 1);
                }
                final TableExpression tExpr = fromUQS.getTableExpression();
                if (tExpr != null) {
                    final ArrayList tabClauseList = tExpr.getTableClauseList();
                    if (tabClauseList != null) {
                        for (int i = 0; i < tabClauseList.size(); ++i) {
                            final Object obj = tabClauseList.get(i);
                            if (obj instanceof TableClause) {
                                final TableObject tabObj = ((TableClause)obj).getTableObject();
                                String tableName = tabObj.getTableName();
                                if ((tableName.startsWith("\"") || tableName.startsWith("[") || tableName.startsWith("`")) && tableName.length() > 2) {
                                    tableName = tableName.substring(1, tableName.length() - 1);
                                }
                                try {
                                    final Hashtable colDatatypeTable = (Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.dataTypesFromMetaDataHT, tableName);
                                    if (colDatatypeTable != null) {
                                        final String dataType = (String)CastingUtil.getValueIgnoreCase(colDatatypeTable, columnName);
                                        if (dataType != null) {
                                            final FromTable ft = new FromTable();
                                            ft.setTableName(tableName);
                                            final String aliasName = ((TableClause)obj).getAlias();
                                            if (aliasName != null) {
                                                ft.setAliasName(aliasName);
                                            }
                                            return ft;
                                        }
                                    }
                                }
                                catch (final Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                final FromClause fromClause = fromUQS.getFromClause();
                if (fromClause != null) {
                    Vector oldFromTables2 = new Vector();
                    oldFromTables2 = fromClause.getFromItemList();
                    return getFromTable(oldFromTables2, tc);
                }
            }
            else if (fromSWS instanceof DeleteQueryStatement) {
                final ArrayList tableNames = new ArrayList();
                final DeleteQueryStatement fromDQS = (DeleteQueryStatement)fromSWS;
                final TableExpression tExpr = fromDQS.getTableExpression();
                if (tExpr != null) {
                    final ArrayList tabClauseList = tExpr.getTableClauseList();
                    if (tabClauseList != null) {
                        for (int j = 0; j < tabClauseList.size(); ++j) {
                            final Object obj = tabClauseList.get(j);
                            if (obj instanceof TableClause) {
                                final TableObject tabObj = ((TableClause)obj).getTableObject();
                                tableNames.add(tabObj.getTableName());
                            }
                        }
                    }
                }
                final FromClause delFC = fromDQS.getFromClause();
                if (delFC != null) {
                    final Vector fromItems = delFC.getFromItemList();
                    if (fromItems != null) {
                        for (int k = 0; k < fromItems.size(); ++k) {
                            final Object obj2 = fromItems.get(k);
                            if (obj2 instanceof FromTable) {
                                final FromTable ft2 = (FromTable)obj2;
                                final Object ftobj = ft2.getTableName();
                                if (ftobj instanceof String) {
                                    tableNames.add(ftobj);
                                }
                            }
                        }
                    }
                }
                String columnName2 = tc.getColumnName();
                if ((columnName2.startsWith("\"") || columnName2.startsWith("[") || columnName2.startsWith("`")) && columnName2.length() > 2) {
                    columnName2 = columnName2.substring(1, columnName2.length() - 1);
                }
                for (int l = 0; l < tableNames.size(); ++l) {
                    String tableName2 = tableNames.get(l);
                    if ((tableName2.startsWith("\"") || tableName2.startsWith("[") || tableName2.startsWith("`")) && tableName2.length() > 2) {
                        tableName2 = tableName2.substring(1, tableName2.length() - 1);
                    }
                    try {
                        final Hashtable colDatatypeTable2 = (Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.dataTypesFromMetaDataHT, tableName2);
                        if (colDatatypeTable2 != null) {
                            final String dType = (String)CastingUtil.getValueIgnoreCase(colDatatypeTable2, columnName2);
                            if (dType != null) {
                                final FromTable ft3 = new FromTable();
                                ft3.setTableName(tableName2);
                                return ft3;
                            }
                        }
                    }
                    catch (final Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }
        return null;
    }
    
    private static FromTable getFromTable(final Vector oldFromTables, final TableColumn tc) {
        Enumeration enum1 = null;
        if (oldFromTables != null) {
            enum1 = oldFromTables.elements();
        }
        if (enum1 != null) {
            String columnName = null;
            final String tableColumn = tc.getColumnName();
            if (tableColumn.trim().length() > 0) {
                final int index = tableColumn.lastIndexOf(".");
                if (index != -1) {
                    columnName = tableColumn.substring(index + 1, tableColumn.length());
                }
                else {
                    columnName = tableColumn;
                }
            }
            while (enum1.hasMoreElements()) {
                final Object obj = enum1.nextElement();
                String tableName = null;
                if (obj instanceof FromTable && ((FromTable)obj).getTableName() != null) {
                    tableName = ((FromTable)obj).getTableName().toString();
                }
                if (tableName != null && tableName.trim().length() > 0) {
                    final int index2 = tableName.lastIndexOf(".");
                    if (index2 != -1) {
                        tableName = tableName.substring(index2 + 1, tableName.length());
                    }
                }
                try {
                    if (tableName == null) {
                        continue;
                    }
                    if ((tableName.startsWith("\"") || tableName.startsWith("[") || tableName.startsWith("`")) && tableName.length() > 2) {
                        tableName = tableName.substring(1, tableName.length() - 1);
                    }
                    if ((columnName.startsWith("\"") || columnName.startsWith("[") || columnName.startsWith("`")) && columnName.length() > 2) {
                        columnName = columnName.substring(1, columnName.length() - 1);
                    }
                    final Hashtable colDatatypeTable = (Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.dataTypesFromMetaDataHT, tableName);
                    if (colDatatypeTable != null) {
                        final String dataType = (String)CastingUtil.getValueIgnoreCase(colDatatypeTable, columnName);
                        if (dataType != null) {
                            return (FromTable)obj;
                        }
                    }
                    final Hashtable tempColDatatypeTable = (Hashtable)CastingUtil.getValueIgnoreCase(MetadataInfoUtil.tempTableMetadata, tableName);
                    if (tempColDatatypeTable == null) {
                        continue;
                    }
                    final Object tempObj = CastingUtil.getValueIgnoreCase(tempColDatatypeTable, columnName);
                    if (tempObj != null) {
                        return (FromTable)obj;
                    }
                    continue;
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    public static String getFromTable(final ArrayList fromTableList, final TableColumn tc) {
        if (fromTableList != null) {
            String columnName = null;
            final String tableColumn = tc.getColumnName();
            if (tableColumn.trim().length() > 0) {
                final int index = tableColumn.lastIndexOf(".");
                if (index != -1) {
                    columnName = tableColumn.substring(index + 1, tableColumn.length());
                }
                else {
                    columnName = tableColumn;
                }
            }
            for (int i = 0; i < fromTableList.size(); ++i) {
                String tableName = fromTableList.get(i);
                final int index2 = tableName.lastIndexOf(".");
                if (index2 != -1) {
                    tableName = tableName.substring(index2 + 1, tableName.length());
                }
                try {
                    if (tableName != null) {
                        if ((tableName.startsWith("\"") || tableName.startsWith("[") || tableName.startsWith("`")) && tableName.length() > 2) {
                            tableName = tableName.substring(1, tableName.length() - 1);
                        }
                        if ((columnName.startsWith("\"") || columnName.startsWith("[") || columnName.startsWith("`")) && columnName.length() > 2) {
                            columnName = columnName.substring(1, columnName.length() - 1);
                        }
                        final Hashtable colDatatypeTable = (Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.dataTypesFromMetaDataHT, tableName);
                        if (colDatatypeTable != null) {
                            final String dataType = (String)CastingUtil.getValueIgnoreCase(colDatatypeTable, columnName);
                            if (dataType != null) {
                                return tableName;
                            }
                        }
                    }
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    public static FromTable getTableOfColumn(final SwisSQLStatement fromSQS, final String columnName) {
        final TableColumn newTC = new TableColumn();
        newTC.setColumnName(columnName);
        return getTableOfColumn(fromSQS, newTC);
    }
    
    public static String getTargetDataTypeForColumn(final TableColumn tableColumn) {
        if (SwisSQLAPI.targetDataTypesMetaDataHash == null) {
            return null;
        }
        if (tableColumn == null) {
            return null;
        }
        if (tableColumn.getTableName() != null) {
            String tableName = tableColumn.getTableName().toLowerCase();
            String columnName = tableColumn.getColumnName().toLowerCase();
            if ((tableName.startsWith("\"") || tableName.startsWith("[") || tableName.startsWith("`")) && tableName.length() > 2) {
                tableName = tableName.substring(1, tableName.length() - 1);
            }
            if ((columnName.startsWith("\"") || columnName.startsWith("[") || columnName.startsWith("`")) && columnName.length() > 2) {
                columnName = columnName.substring(1, columnName.length() - 1);
            }
            Hashtable columnHash = SwisSQLAPI.targetDataTypesMetaDataHash.get(tableName);
            if (columnHash == null) {
                columnHash = SwisSQLAPI.targetDataTypesMetaDataHash.get(tableName.toUpperCase());
                if (columnHash == null) {
                    return null;
                }
            }
            String dataType = columnHash.get(columnName);
            if (dataType == null) {
                dataType = columnHash.get(columnName.toUpperCase());
            }
            return dataType;
        }
        if (tableColumn.getColumnName() == null) {
            return null;
        }
        if (SwisSQLAPI.variableDatatypeMapping != null) {
            return CastingUtil.getDataType(SwisSQLAPI.variableDatatypeMapping.get(tableColumn.getColumnName()));
        }
        return null;
    }
    
    static {
        MetadataInfoUtil.tempTableMetadata = new Hashtable();
    }
}
