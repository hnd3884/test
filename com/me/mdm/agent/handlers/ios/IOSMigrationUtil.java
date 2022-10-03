package com.me.mdm.agent.handlers.ios;

import org.json.JSONObject;
import com.me.mdm.server.ios.error.IOSErrorStatusHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.List;
import com.me.mdm.agent.handlers.BaseMigrationUtil;

public class IOSMigrationUtil extends BaseMigrationUtil
{
    static IOSMigrationUtil iosMigrationUtil;
    
    public static IOSMigrationUtil getInstance() {
        if (IOSMigrationUtil.iosMigrationUtil == null) {
            IOSMigrationUtil.iosMigrationUtil = new IOSMigrationUtil();
        }
        return IOSMigrationUtil.iosMigrationUtil;
    }
    
    public IOSMigrationUtil() {
        this.platformType = 1;
    }
    
    @Override
    protected void addMigrationCommandForDevice(final List resourceIDs, final int commandRepoType) {
        this.logger.log(Level.INFO, "[Migration][Migrate][IOSMigrationUtil] : CommandRepoType - {0} Picked Candidates {1}", new Object[] { commandRepoType, Arrays.toString(resourceIDs.toArray()) });
        if (commandRepoType == 1) {
            DeviceCommandRepository.getInstance().addDefaultAppCatalogCommand(resourceIDs, "DefaultAppCatalogWebClipsMigrate");
        }
        else if (commandRepoType == 2) {
            final DeviceCommandRepository dcr = DeviceCommandRepository.getInstance();
            final Long cmdId = dcr.addCommand("MDMDefaultApplicationConfigMigrate");
            dcr.assignCommandToDevices(cmdId, resourceIDs, 1);
        }
        super.addMigrationCommandForDevice(resourceIDs, commandRepoType);
    }
    
    public void processAppConfigResponse(final String strData, final Long resourceID) {
        this.logger.log(Level.INFO, "[Migration][IOSMigrationUtil] : Process App Config Response.. ResourceID - {0},  ", new Object[] { resourceID });
        try {
            final JSONObject jo = new IOSErrorStatusHandler().getIOSSettingError(strData);
            if (jo.get("Status").equals("Acknowledged") || (jo.get("Status").equals("Error") && jo.getInt("ErrorCode") == 21009)) {
                this.urlMigratedSuccessfullyOndevice(resourceID, 2);
                this.logger.log(Level.INFO, "[Migration][IOSMigrationUtil] : App URL Successfully Migrated on Device.. ResourceID - {0},  ", new Object[] { resourceID });
            }
            else {
                final String errStatus = jo.optString("Status", "");
                final String errCode = jo.optString("ErrorCode", "");
                this.logger.log(Level.WARNING, "[Migration][IOSMigrationUtil] : App URL Migration failed on Device.. ResourceID - {0}, Error Status: {1} ,  Error Code: {2}", new Object[] { resourceID, errStatus, errCode });
                this.migrationFailed(resourceID, 2);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in processAppConfigResponse.. ", ex);
        }
    }
    
    static {
        IOSMigrationUtil.iosMigrationUtil = null;
    }
}
