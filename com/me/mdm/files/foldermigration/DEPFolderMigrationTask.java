package com.me.mdm.files.foldermigration;

import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.util.logging.Level;

public class DEPFolderMigrationTask extends FolderMigrationTask
{
    @Override
    public boolean copyFiles(final Long customerId) {
        this.logger.log(Level.INFO, "Going to migrate DEP folder for customer {0}", new Object[] { customerId });
        this.sourcePath = MDMMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "mdm" + File.separator + "DEP" + File.separator + customerId;
        this.destPath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataBasePath("DEP") + File.separator + customerId;
        return super.copyFiles(customerId);
    }
}
