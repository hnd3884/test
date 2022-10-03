package com.me.mdm.server.profiles;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.util.HashMap;
import com.adventnet.ds.query.Join;
import java.util.List;

public class ProfilePayloadMapping
{
    protected String tableName;
    protected String columnName;
    protected String baseTable;
    protected String baseTableColumn;
    protected String cfgDataItemTable;
    protected String cfgColumn;
    protected Boolean unConfigurePayload;
    protected List<Join> joinList;
    protected HashMap criteriaTableColumns;
    protected Boolean modifyClonedDO;
    protected Boolean deleteRow;
    
    public ProfilePayloadMapping() {
        this.modifyClonedDO = false;
        this.deleteRow = false;
    }
    
    public Join getJoin(final int joinType) {
        return new Join(this.baseTable, this.tableName, new String[] { this.baseTableColumn }, new String[] { this.columnName }, joinType);
    }
    
    public void addCfgDataItemJoin(final SelectQuery selectQuery, final int joinType) {
        if (this.joinList != null && this.joinList.size() > 0) {
            final List<Join> selectQueryJoin = selectQuery.getJoins();
            for (final Join join : this.joinList) {
                if (!selectQueryJoin.contains(join)) {
                    selectQuery.addJoin(join);
                }
            }
        }
        else {
            if (!selectQuery.getTableList().contains(Table.getTable(this.cfgDataItemTable))) {
                selectQuery.addJoin(new Join("ConfigDataItem", this.cfgDataItemTable, new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, joinType));
            }
            if (!this.cfgDataItemTable.equals(this.tableName) && !selectQuery.getTableList().contains(Table.getTable(this.tableName))) {
                selectQuery.addJoin(new Join(this.cfgDataItemTable, this.tableName, new String[] { this.cfgColumn }, new String[] { this.cfgColumn }, joinType));
            }
        }
    }
    
    public void updateCertId(final Row row, final Long value) {
        row.set(this.columnName, (Object)value);
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public List<String> getTableNames() {
        List<String> tableNames = new ArrayList<String>();
        if (this.criteriaTableColumns != null && this.criteriaTableColumns.size() > 0) {
            tableNames = new ArrayList<String>(this.criteriaTableColumns.keySet());
        }
        else {
            tableNames.add(this.getTableName());
        }
        return tableNames;
    }
    
    public String getColumnName(final String tableName) {
        if (this.criteriaTableColumns != null && this.criteriaTableColumns.size() > 0) {
            return this.criteriaTableColumns.get(tableName);
        }
        return this.columnName;
    }
    
    public List<Column> getColumns() {
        final List<Column> columns = new ArrayList<Column>();
        if (this.criteriaTableColumns != null && this.criteriaTableColumns.size() > 0) {
            for (final String tableName : this.criteriaTableColumns.keySet()) {
                final String columnName = this.criteriaTableColumns.get(tableName);
                columns.add(new Column(tableName, columnName));
            }
        }
        else {
            columns.add(new Column(this.tableName, this.columnName));
        }
        return columns;
    }
    
    public Criteria getCriteria(final List certids) {
        if (this.criteriaTableColumns != null && this.criteriaTableColumns.size() > 0) {
            Criteria criteria = null;
            for (final String tableName : this.criteriaTableColumns.keySet()) {
                final String columnName = this.criteriaTableColumns.get(tableName);
                final Criteria tableCriteria = new Criteria(new Column(tableName, columnName), (Object)certids.toArray(), 8);
                if (criteria == null) {
                    criteria = tableCriteria;
                }
                else {
                    criteria = criteria.or(tableCriteria);
                }
            }
            return criteria;
        }
        return new Criteria(Column.getColumn(this.tableName, this.columnName), (Object)certids.toArray(), 8);
    }
    
    public Criteria getCriteria(final Object value) {
        if (this.criteriaTableColumns != null && this.criteriaTableColumns.size() > 0) {
            Criteria criteria = null;
            for (final String tableName : this.criteriaTableColumns.keySet()) {
                final String columnName = this.criteriaTableColumns.get(tableName);
                final Criteria tableCriteria = new Criteria(new Column(tableName, columnName), value, 0);
                if (criteria == null) {
                    criteria = tableCriteria;
                }
                else {
                    criteria = criteria.or(tableCriteria);
                }
            }
            return criteria;
        }
        return new Criteria(Column.getColumn(this.tableName, this.columnName), value, 0);
    }
    
    public Criteria getNotEmptyCriteria() {
        return new Criteria(Column.getColumn(this.tableName, this.columnName), (Object)(-1L), 1).and(new Criteria(Column.getColumn(this.tableName, this.columnName), (Object)null, 1));
    }
    
    public Boolean isUnConfigurePayload() {
        return this.unConfigurePayload;
    }
    
    public HashMap getConfigDataItemID(final DataObject dataObject) throws DataAccessException {
        final HashMap cfgDataItemIDs = new HashMap();
        final String cfgTable = this.cfgColumn.equals("CONFIG_DATA_ITEM_ID") ? this.tableName : this.cfgDataItemTable;
        if (dataObject.containsTable(cfgTable)) {
            final Iterator iterator = dataObject.getRows(cfgTable);
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long configDataItemID = (Long)row.get("CONFIG_DATA_ITEM_ID");
                Long certID = null;
                if (row.getTableName().equals(this.tableName)) {
                    certID = (Long)row.get(this.columnName);
                }
                else {
                    final Row certRow = dataObject.getRow(this.tableName, new Criteria(Column.getColumn(this.tableName, this.cfgColumn), row.get(this.cfgColumn), 0));
                    certID = (Long)certRow.get(this.columnName);
                }
                if (certID != null && certID != -1L) {
                    List list = cfgDataItemIDs.get(certID);
                    if (list == null) {
                        list = new ArrayList();
                    }
                    list.add(configDataItemID);
                    cfgDataItemIDs.put(certID, list);
                }
            }
        }
        return cfgDataItemIDs;
    }
    
    public Long getNewCertValue(final Long newCertValue) {
        if (newCertValue != -1L) {
            return newCertValue;
        }
        return -1L;
    }
    
    public void modifyClonedDO(final DataObject clonedDO) throws DataAccessException {
    }
    
    public Boolean isDeleteRow() {
        return this.deleteRow;
    }
    
    public boolean isModifyClonedDO() {
        return this.modifyClonedDO;
    }
    
    public int customDeleteRow(final DataObject dataObject, final Row row, final Integer configId) throws Exception {
        return 0;
    }
}
