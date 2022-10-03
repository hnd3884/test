package com.me.mdm.files.foldermigration;

import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Logger;

public class ClientDataFolderMigration
{
    Logger logger;
    
    public ClientDataFolderMigration() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void copyProfileTask() {
        try {
            final Long[] customerIdsFromDB;
            final Long[] customers = customerIdsFromDB = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            for (final Long customerId : customerIdsFromDB) {
                new ProfileClientDataMigration().migrateFiles(customerId);
                new ComplianceClientDataMigration().migrateFiles(customerId);
                new CustomProfileClientDataMigration().migrateFiles(customerId);
                new CredentialClientDataMigrationTask().migrateFiles(customerId);
                new DEPFolderMigrationTask().migrateFiles(customerId);
                new AppConfigTemplateMigration().migrateFiles(customerId);
                new ManagedAppConfigMigration().migrateFiles(customerId);
            }
            new APNsFolderMigration().migrateFiles(null);
            new APNsBackUpFolderMigration().migrateFiles(null);
            MDMUtil.deleteSyMParameter("migrateClientDataFolder");
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Cannot update file ", e);
        }
    }
}
