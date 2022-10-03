package com.me.mdm.files.foldermigration;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.util.logging.Level;

public class CredentialClientDataMigrationTask extends FolderMigrationTask
{
    @Override
    public boolean copyFiles(final Long customerId) {
        this.logger.log(Level.INFO, "Going to migrate credentials folder for customer {0}", new Object[] { customerId });
        this.sourcePath = MDMMetaDataUtil.getInstance().getClientDataDir(customerId) + File.separator + "mdm" + File.separator + "credential";
        this.destPath = MDMUtil.getCredentialCertificateFolder(customerId);
        return super.copyFiles(customerId);
    }
}
