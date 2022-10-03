package com.me.mdm.agent.handlers.android.servletmigration;

import java.util.Arrays;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.List;
import java.util.logging.Level;
import com.me.mdm.agent.handlers.BaseMigrationUtil;

public class AndroidServletMigrationUtil extends BaseMigrationUtil
{
    public AndroidServletMigrationUtil() {
        this.platformType = 2;
    }
    
    @Override
    public void checkAndAddMigrationCommand(final Long resourceID, final Object migrationParam) {
        super.checkAndAddMigrationCommand(resourceID, migrationParam);
    }
    
    @Override
    protected boolean isMigrationRequiredForURL(final Object param, final int cmdRepType) {
        boolean result = false;
        final String url = (String)param;
        if (url.equalsIgnoreCase("/mdm/client/v1/androidcheckin") || url.equalsIgnoreCase("/mdm/client/v1/safecheckin") || url.equalsIgnoreCase("/mdm/client/v1/androidnativeapp")) {
            result = false;
        }
        else {
            result = true;
            this.logger.log(Level.INFO, " Migration needed for this device, going to check MDM agent version");
        }
        return result;
    }
    
    @Override
    protected void addMigrationCommandForDevice(final List resourceIDs, final int commandRepoType) {
        final List readyForMigrationResources = this.filterDevicesReadyForServletMigration(resourceIDs);
        if (!readyForMigrationResources.isEmpty()) {
            final Long commandId = DeviceCommandRepository.getInstance().addCommand("MigrateUrl");
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandId, readyForMigrationResources);
            super.addMigrationCommandForDevice(readyForMigrationResources, 1);
        }
    }
    
    private List filterDevicesReadyForServletMigration(final List resourceIDs) {
        this.logger.log(Level.INFO, "Candidates proposed for servlet migration {0}", resourceIDs);
        List readyForMigrationResources = new ArrayList();
        try {
            final Long servletMigrationReadyAgentVersion = 2300547L;
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "AGENT_VERSION_CODE"));
            final Criteria resourceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceIDs.toArray(), 8);
            final Criteria agentVersionCriteria = new Criteria(Column.getColumn("ManagedDevice", "AGENT_VERSION_CODE"), (Object)servletMigrationReadyAgentVersion, 4);
            sQuery.setCriteria(resourceCriteria.and(agentVersionCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
            final Iterator itr = dataObject.getRows("ManagedDevice");
            readyForMigrationResources = DBUtil.getColumnValuesAsList(itr, "RESOURCE_ID");
            this.logger.log(Level.INFO, "Candidates with agent version eligible for servlet migration {0}", readyForMigrationResources);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Unable to check agent version for servlet migration", (Throwable)e);
        }
        return readyForMigrationResources;
    }
    
    public void addMigrationCommand(final Long resId) {
        this.addMigrationCommandForDevice(Arrays.asList(resId), 1);
    }
    
    public void handleResponse(final Long resId, final String status) {
        if (status.equalsIgnoreCase("Acknowledged")) {
            super.urlMigratedSuccessfullyOndevice(resId, 1);
        }
        else {
            super.migrationFailed(resId, 1);
        }
    }
}
