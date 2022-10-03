package com.me.mdm.server.security.profile;

import java.util.Collection;
import com.adventnet.ds.query.SelectQuery;
import java.util.Iterator;
import java.util.ArrayList;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import java.util.List;
import java.util.HashMap;
import com.me.mdm.server.profiles.ProfilePayloadMapping;

public class PayloadSecretFieldsTableMapping extends ProfilePayloadMapping
{
    private HashMap<String, List<String>> selectColumnsMap;
    private HashMap<String, String> secretColumnsMap;
    private Boolean hasSecretField;
    private Boolean hasCertificateField;
    private List<String> certificateColumns;
    private HashMap<String, List<String>> tableToCertificateColumnsMap;
    
    public PayloadSecretFieldsTableMapping(final String tableName, final HashMap<String, String> secretFieldColumnsMap) {
        this.hasSecretField = Boolean.FALSE;
        this.hasCertificateField = Boolean.FALSE;
        this.cfgDataItemTable = tableName;
        this.tableName = tableName;
        this.cfgColumn = "CONFIG_DATA_ITEM_ID";
        this.secretColumnsMap = secretFieldColumnsMap;
        this.hasSecretField = Boolean.TRUE;
    }
    
    public PayloadSecretFieldsTableMapping(final String tableName, final HashMap<String, String> secretFieldsColumnsMap, final List<Join> secretFieldTableJoins, final HashMap<String, List<String>> selectColumns) {
        this.hasSecretField = Boolean.FALSE;
        this.hasCertificateField = Boolean.FALSE;
        this.cfgDataItemTable = tableName;
        this.tableName = tableName;
        this.cfgColumn = "CONFIG_DATA_ITEM_ID";
        this.joinList = secretFieldTableJoins;
        this.selectColumnsMap = selectColumns;
        this.secretColumnsMap = secretFieldsColumnsMap;
        this.hasSecretField = Boolean.TRUE;
    }
    
    public PayloadSecretFieldsTableMapping(final String tableName, final HashMap<String, String> secretFieldsColumnsMap, final List<Join> secretFieldTableJoins, final HashMap<String, List<String>> selectColumns, final List<String> certificateColumns) {
        this.hasSecretField = Boolean.FALSE;
        this.hasCertificateField = Boolean.FALSE;
        this.cfgDataItemTable = tableName;
        this.tableName = tableName;
        this.cfgColumn = "CONFIG_DATA_ITEM_ID";
        this.joinList = secretFieldTableJoins;
        this.selectColumnsMap = selectColumns;
        if (secretFieldsColumnsMap != null) {
            this.secretColumnsMap = secretFieldsColumnsMap;
            this.hasSecretField = Boolean.TRUE;
        }
        if (certificateColumns != null) {
            this.certificateColumns = certificateColumns;
            this.hasCertificateField = Boolean.TRUE;
            this.tableToCertificateColumnsMap = this.constructTableToCertificateColumnsMap();
        }
    }
    
    @Override
    public List<Column> getColumns() {
        final List<Column> columns = new ArrayList<Column>();
        if (this.selectColumnsMap != null && this.selectColumnsMap.size() > 0) {
            for (final String tableName : this.selectColumnsMap.keySet()) {
                final List<String> columnsList = this.selectColumnsMap.get(tableName);
                for (final String columnName : columnsList) {
                    columns.add(new Column(tableName, columnName));
                }
            }
        }
        else if (this.secretColumnsMap != null && this.secretColumnsMap.size() > 0) {
            for (final String secretField : this.secretColumnsMap.keySet()) {
                final String secretFieldID = this.secretColumnsMap.get(secretField);
                columns.add(new Column(this.cfgDataItemTable, secretField));
                columns.add(new Column(this.cfgDataItemTable, secretFieldID));
            }
            columns.add(new Column(this.cfgDataItemTable, this.cfgColumn));
        }
        return columns;
    }
    
    public void addSelectColumns(final SelectQuery selectQuery) {
        final List<Column> columns = this.getColumns();
        if (!columns.isEmpty()) {
            for (final Column column : columns) {
                selectQuery.addSelectColumn(column);
            }
        }
    }
    
    public HashMap<String, String> getSecretColumnsMap() {
        return this.secretColumnsMap;
    }
    
    public String getSecretFieldTable(final String secretFieldColumn) {
        if (this.selectColumnsMap != null && this.selectColumnsMap.size() > 0) {
            for (final String tableName : this.selectColumnsMap.keySet()) {
                final List<String> columnsList = this.selectColumnsMap.get(tableName);
                if (columnsList.contains(secretFieldColumn)) {
                    return tableName;
                }
            }
            return null;
        }
        return this.cfgDataItemTable;
    }
    
    public HashMap<String, List<String>> getTableToSecretFieldsMap() {
        final HashMap<String, List<String>> tableToSecretFieldsMap = new HashMap<String, List<String>>();
        final List<String> secretFieldsList = new ArrayList<String>(this.secretColumnsMap.keySet());
        if (this.selectColumnsMap != null && this.selectColumnsMap.size() > 0) {
            for (final String tableName : this.selectColumnsMap.keySet()) {
                final List<String> columnsList = this.selectColumnsMap.get(tableName);
                final List<String> tempSecretFieldsList = new ArrayList<String>(secretFieldsList);
                tempSecretFieldsList.retainAll(columnsList);
                if (!tempSecretFieldsList.isEmpty()) {
                    tableToSecretFieldsMap.put(tableName, tempSecretFieldsList);
                }
            }
        }
        else {
            tableToSecretFieldsMap.put(this.cfgDataItemTable, secretFieldsList);
        }
        return tableToSecretFieldsMap;
    }
    
    public HashMap<String, List<String>> constructTableToCertificateColumnsMap() {
        final HashMap<String, List<String>> tableToCertificateColumnsMap = new HashMap<String, List<String>>();
        final List<String> certificateColumns = this.getCertificateColumns();
        if (certificateColumns != null) {
            if (this.selectColumnsMap != null && this.selectColumnsMap.size() > 0) {
                for (final String tableName : this.selectColumnsMap.keySet()) {
                    final List<String> columnsList = this.selectColumnsMap.get(tableName);
                    final List<String> tempCertificateColumnList = new ArrayList<String>(certificateColumns);
                    tempCertificateColumnList.retainAll(columnsList);
                    if (!tempCertificateColumnList.isEmpty()) {
                        tableToCertificateColumnsMap.put(tableName, tempCertificateColumnList);
                    }
                }
            }
            else {
                tableToCertificateColumnsMap.put(this.cfgDataItemTable, certificateColumns);
            }
        }
        return tableToCertificateColumnsMap;
    }
    
    public HashMap<String, List<String>> getTableToCertificateColumnsMap() {
        return this.tableToCertificateColumnsMap;
    }
    
    public List<String> getCertificateListFromMap(final String tableName) {
        return this.tableToCertificateColumnsMap.get(tableName);
    }
    
    public List<String> getTables() {
        final List<String> tables = new ArrayList<String>();
        if (this.selectColumnsMap != null && this.selectColumnsMap.size() > 0) {
            final List<String> tablesList = new ArrayList<String>(this.selectColumnsMap.keySet());
            tables.addAll(tablesList);
        }
        else {
            tables.add(this.cfgDataItemTable);
        }
        return tables;
    }
    
    public Boolean checkIfTableHasCertificateColumn() {
        return this.hasCertificateField;
    }
    
    public Boolean checkIfTableHasSecretField() {
        return this.hasSecretField;
    }
    
    public List<String> getCertificateColumns() {
        return this.certificateColumns;
    }
}
