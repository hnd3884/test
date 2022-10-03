package com.me.mdm.server.device.api.service;

import com.me.mdm.server.migration.MspMigrationHandler;
import java.util.List;
import com.me.mdm.server.config.MDMCollectionUtil;
import com.me.mdm.server.customer.MDMCustomerInfoUtil;
import java.util.Collection;
import com.me.mdm.server.device.DeviceFacade;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.server.device.api.model.MspDeviceMigrationModel;
import java.util.logging.Logger;

public class MspDeviceMigrationService
{
    private final Logger logger;
    
    public MspDeviceMigrationService() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void migrateDevice(final MspDeviceMigrationModel mspDeviceMigrationModel) throws Exception {
        try {
            final Long loginId = mspDeviceMigrationModel.getLogInId();
            if (!DMUserHandler.isUserInRole(loginId, "All_Managed_Mobile_Devices")) {
                this.logger.log(Level.INFO, "[MspDeviceMig] User is not Administrator, So MSP migration is not performed and throwing error..");
                throw new APIHTTPException("COM0013", new Object[0]);
            }
            new DeviceFacade().validateIfDevicesExists(mspDeviceMigrationModel.getDeviceIdList(), mspDeviceMigrationModel.getCustomerId());
            if (!MDMCustomerInfoUtil.getInstance().isCustomerIDValidForUser(mspDeviceMigrationModel.getUserId(), mspDeviceMigrationModel.getNewCustomerId())) {
                throw new APIHTTPException("COM0013", new Object[0]);
            }
            if (!mspDeviceMigrationModel.getForceMigrate()) {
                final List<Long> collnIdList = MDMCollectionUtil.getCollectionIdsAssociatedWithResources(mspDeviceMigrationModel.getDeviceIdList());
                if (!collnIdList.isEmpty()) {
                    this.logger.log(Level.INFO, "[MspDeviceMig] Cannot migrate as the Resources have collection associated to them.");
                    throw new APIHTTPException("COM0015", new Object[] { "Associated Profiles/Apps" });
                }
            }
            new MspMigrationHandler().migrateDevice(mspDeviceMigrationModel.getDeviceIdList(), mspDeviceMigrationModel.getCustomerId(), mspDeviceMigrationModel.getNewCustomerId(), mspDeviceMigrationModel.getUserName());
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, "[MspDeviceMig] Exception in MSP device migration.. ", ex);
            throw ex;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "[MspDeviceMig] Exception in MSP device migration.. ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
