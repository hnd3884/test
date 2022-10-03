package com.me.mdm.server.certificate;

import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import java.util.List;
import com.adventnet.persistence.Row;
import com.me.mdm.server.profiles.ProfilePayloadMapping;

public class CertificateMapping extends ProfilePayloadMapping
{
    public CertificateMapping(final String tableName, final String columnName) {
        this.columnName = columnName;
        this.tableName = tableName;
        this.baseTable = "Certificates";
        this.baseTableColumn = "CERTIFICATE_RESOURCE_ID";
        this.cfgDataItemTable = tableName;
        this.unConfigurePayload = Boolean.FALSE;
        this.cfgColumn = "CONFIG_DATA_ITEM_ID";
    }
    
    public CertificateMapping(final String tableName, final String columnName, final Boolean unConfigurePayload) {
        this.columnName = columnName;
        this.tableName = tableName;
        this.baseTable = "Certificates";
        this.baseTableColumn = "CERTIFICATE_RESOURCE_ID";
        this.cfgDataItemTable = tableName;
        this.unConfigurePayload = unConfigurePayload;
        this.cfgColumn = "CONFIG_DATA_ITEM_ID";
    }
    
    public CertificateMapping(final String tableName, final String columnName, final String cfgDataItemTable, final String cfgColumn) {
        this.columnName = columnName;
        this.tableName = tableName;
        this.cfgDataItemTable = cfgDataItemTable;
        this.cfgColumn = cfgColumn;
        this.baseTable = "Certificates";
        this.baseTableColumn = "CERTIFICATE_RESOURCE_ID";
        this.unConfigurePayload = Boolean.FALSE;
    }
    
    @Override
    public Long getNewCertValue(final Long newCertValue) {
        if (newCertValue != null && newCertValue != -1L) {
            return newCertValue;
        }
        return null;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof CertificateMapping)) {
            return super.equals(obj);
        }
        final CertificateMapping certificateMapping = (CertificateMapping)obj;
        if (certificateMapping.tableName.equalsIgnoreCase(this.tableName) && certificateMapping.columnName.equalsIgnoreCase(this.columnName)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public void updateCertId(final Row row, final Long value) {
        row.set(this.columnName, (Object)value);
    }
    
    @Override
    public String getTableName() {
        return this.tableName;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    @Override
    public Criteria getCriteria(final List certids) {
        return new Criteria(Column.getColumn(this.tableName, this.columnName), (Object)certids.toArray(), 8);
    }
    
    @Override
    public Criteria getCriteria(final Object value) {
        return new Criteria(Column.getColumn(this.tableName, this.columnName), value, 0);
    }
    
    @Override
    public Criteria getNotEmptyCriteria() {
        return new Criteria(Column.getColumn(this.tableName, this.columnName), (Object)(-1L), 1).and(new Criteria(Column.getColumn(this.tableName, this.columnName), (Object)null, 1));
    }
    
    @Override
    public Boolean isUnConfigurePayload() {
        return this.unConfigurePayload;
    }
    
    @Override
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
}
