package com.me.mdm.server.security;

import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.logging.Level;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;

public class MDMIOSSecurityUtil extends MDMBaseSecurityUtil
{
    @Override
    public void notifyAgents(final Long customerId, final String commandUUID) {
        switch (commandUUID) {
            case "SyncAgentSettings": {
                final List<Long> resourceList = ManagedDeviceHandler.getInstance().getIosManagedDevicesForCustomer(customerId);
                DeviceCommandRepository.getInstance().addSyncAgentSettingsCommand(resourceList, 2);
                break;
            }
        }
    }
    
    public void notifyAgentsForCheckSumSettings(final Long customerId) {
        final List<Long> resourceList = ManagedDeviceHandler.getInstance().getIosManagedDevicesForCustomer(customerId);
        DeviceCommandRepository.getInstance().addSyncAgentSettingsCommand(resourceList, 2);
    }
    
    @Override
    public void toggleCheckSumValidation(final Long customerId, final boolean enable) {
        this.logger.log(Level.INFO, "Going to set checksum validation to {0}", new Object[] { String.valueOf(enable) });
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("IOSAgentSettings");
        updateQuery.setCriteria(new Criteria(new Column("IOSAgentSettings", "CUSTOMER_ID"), (Object)customerId, 0));
        updateQuery.setUpdateColumn("VALIDATE_CHECKSUM", (Object)enable);
        try {
            MDMUtil.getPersistence().update(updateQuery);
            this.notifyAgents(customerId, "SyncAgentSettings");
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Cannot update checksum validation ", (Throwable)e);
        }
    }
}
