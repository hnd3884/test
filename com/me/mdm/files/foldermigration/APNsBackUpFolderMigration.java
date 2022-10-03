package com.me.mdm.files.foldermigration;

import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.util.logging.Level;

public class APNsBackUpFolderMigration extends FolderMigrationTask
{
    @Override
    public boolean copyFiles(final Long customerId) {
        this.logger.log(Level.INFO, "Going to migrate APNs backup folder for customer {0}", new Object[] { customerId });
        this.sourcePath = MDMMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "mdm" + File.separator + "apnsCertificate" + File.separator + "backup";
        this.destPath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataBasePath("apnsCertificate") + File.separator + "backup";
        return super.copyFiles(customerId);
    }
}
