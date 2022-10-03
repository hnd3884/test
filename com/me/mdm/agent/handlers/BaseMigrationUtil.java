package com.me.mdm.agent.handlers;

import com.me.mdm.agent.handlers.android.servletmigration.AndroidServletMigrationUtil;
import com.me.mdm.agent.handlers.windows.WindowsMigrationUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

public abstract class BaseMigrationUtil
{
    public static final int YET_TO_APPLY = 0;
    public static final int COMMAND_ADDED = 1;
    public static final int INITATED = 2;
    public static final int AGENT_FAILED = 3;
    public static final int FAILED = 4;
    public static final int SUCCESS = 5;
    private static final String ADDED_LIST = "ADDED_LIST";
    private static final String SUCCESS_LIST = "SUCCESS_LIST";
    public Logger logger;
    public int platformType;
    
    public BaseMigrationUtil() {
        this.logger = Logger.getLogger("MigrationEventLogger");
    }
    
    public void migrationInitated(final Long resourceID, final int commandRepType) {
        this.updateMigrationStatus(resourceID, commandRepType, 2);
    }
    
    public void migrationFailed(final Long resourceID, final int commandRepType) {
        this.updateMigrationStatus(resourceID, commandRepType, 3);
    }
    
    public void urlMigratedSuccessfullyOndevice(final Long resourceID, final int commandRepType) {
        this.updateMigrationStatus(resourceID, commandRepType, 5);
    }
    
    public void checkAndAddMigrationCommand(final Long resourceID, final Object migrationParam) {
        this.checkAndAddMigrationCommand(resourceID, migrationParam, 1);
    }
    
    private void updateMigrationStatus(final Long resourceID, final int cmdRepType, final int status) {
        final List list = new ArrayList();
        list.add(resourceID);
        this.updateMigrationStatus(list, cmdRepType, status);
    }
    
    private void updateMigrationStatus(final List resourceIDs, final int cmdRepType, final int status) {
        final SelectQueryImpl selectQuery = new SelectQueryImpl(new Table("MigrationStatus"));
        final Criteria criteria = new Criteria(Column.getColumn("MigrationStatus", "RESOURCE_ID"), (Object)resourceIDs.toArray(), 8);
        final Criteria cmdRepCriteria = new Criteria(Column.getColumn("MigrationStatus", "COMMAND_REPOSITORY_TYPE"), (Object)cmdRepType, 0);
        selectQuery.setCriteria(criteria.and(cmdRepCriteria));
        selectQuery.addSelectColumn(Column.getColumn("MigrationStatus", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MigrationStatus", "COMMAND_REPOSITORY_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("MigrationStatus", "COMMAND_STATUS"));
        try {
            final Long currentMillis = System.currentTimeMillis();
            final Iterator iterator = resourceIDs.iterator();
            final DataObject dataObject = MDMUtil.getPersistenceLite().get((SelectQuery)selectQuery);
            while (iterator.hasNext()) {
                final Long resID = iterator.next();
                final Row migRow = dataObject.getRow("MigrationStatus", new Criteria(Column.getColumn("MigrationStatus", "RESOURCE_ID"), (Object)resID, 0));
                if (migRow != null && status != (int)migRow.get("COMMAND_STATUS")) {
                    final int oldstatus = (int)migRow.get("COMMAND_STATUS");
                    migRow.set("COMMAND_STATUS", (Object)status);
                    migRow.set("UPDATED_AT", (Object)currentMillis);
                    final Row row = new Row("MigrationHistory");
                    row.set("ADDED_AT", (Object)currentMillis);
                    row.set("RESOURCE_ID", (Object)resID);
                    row.set("COMMAND_REPOSITORY_TYPE", (Object)cmdRepType);
                    row.set("COMMAND_STATUS", (Object)status);
                    dataObject.addRow(row);
                    dataObject.updateRow(migRow);
                    this.logger.log(Level.INFO, "[Migration][Status] : changed status of migration : <{0}>", row.getAsJSON());
                    this.logger.log(Level.INFO, "[Migration][Status][Parsable] : {0} {1} {2} {3}", new Object[] { row.get("RESOURCE_ID"), row.get("COMMAND_REPOSITORY_TYPE"), ThrottlingHandler.getStatusAsString(oldstatus), ThrottlingHandler.getStatusAsString(status) });
                }
            }
            MDMUtil.getPersistenceLite().update(dataObject);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "[Migration][Error] Failed to update Migration status", e);
        }
    }
    
    public void checkAndAddMigrationCommand(final Long resourceID, final Object migrationParam, final int cmdRepType) {
        try {
            Boolean isCacheModified = Boolean.FALSE;
            HashMap cacheParentHash = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache("MDM_MIGRATION_CACHE", 2);
            if (cacheParentHash == null) {
                cacheParentHash = new HashMap();
                isCacheModified = Boolean.TRUE;
            }
            HashMap resMap = cacheParentHash.get(cmdRepType);
            if (resMap == null) {
                resMap = new HashMap();
                isCacheModified = Boolean.TRUE;
            }
            if (this.isMigrationRequiredForURL(migrationParam, cmdRepType)) {
                List list = resMap.get("ADDED_LIST");
                if (list == null) {
                    list = new ArrayList();
                    isCacheModified = Boolean.TRUE;
                }
                if (!list.contains(resourceID)) {
                    new ThrottlingHandler().addForMigrationIfNotAdded(resourceID, cmdRepType);
                    list.add(resourceID);
                    isCacheModified = Boolean.TRUE;
                }
                resMap.put("ADDED_LIST", list);
                cacheParentHash.put(cmdRepType, resMap);
            }
            else {
                List list = resMap.get("SUCCESS_LIST");
                if (list == null) {
                    list = new ArrayList();
                    isCacheModified = Boolean.TRUE;
                }
                if (!list.contains(resourceID)) {
                    this.urlMigratedSuccessfullyOndevice(resourceID, cmdRepType);
                    list.add(resourceID);
                    isCacheModified = Boolean.TRUE;
                }
                resMap.put("SUCCESS_LIST", list);
                cacheParentHash.put(cmdRepType, resMap);
            }
            if (isCacheModified) {
                ApiFactoryProvider.getCacheAccessAPI().putCache("MDM_MIGRATION_CACHE", (Object)cacheParentHash, 2);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "[Migration][error] : Error While adding command for migration", e);
        }
    }
    
    protected void addMigrationCommandForDevice(final List resourceIDs, final int commandRepoType) {
        this.updateMigrationStatus(resourceIDs, commandRepoType, 1);
    }
    
    protected boolean isMigrationRequiredForURL(final Object param, final int cmdRepType) {
        final String url = (String)param;
        Boolean migrationRequired = Boolean.FALSE;
        if (url != null && !url.contains("encapiKey")) {
            migrationRequired = Boolean.TRUE;
        }
        return migrationRequired;
    }
    
    public static BaseMigrationUtil getInstance(final int platformType) {
        if (platformType == 3) {
            return new WindowsMigrationUtil();
        }
        if (platformType == 2) {
            return new AndroidServletMigrationUtil();
        }
        return null;
    }
}
