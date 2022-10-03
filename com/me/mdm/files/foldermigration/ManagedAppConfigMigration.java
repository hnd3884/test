package com.me.mdm.files.foldermigration;

import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.util.logging.Level;

public class ManagedAppConfigMigration extends FolderMigrationTask
{
    @Override
    public boolean copyFiles(final Long customerId) {
        this.logger.log(Level.INFO, "Gng to migrate app config template files for customer : {0}", new Object[] { customerId });
        this.sourcePath = MDMMetaDataUtil.getInstance().getClientDataDir(customerId) + File.separator + "mdm" + File.separator + "managedappconfiguration";
        this.destPath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataBasePath("managedappconfiguration") + File.separator + customerId;
        return super.copyFiles(customerId);
    }
    
    @Override
    public void updateDataBase(final Long customerId) {
        try {
            final Column column = new Column("ManagedAppConfigurationData", "APP_CONFIG_ID");
            final Column castToChar = (Column)Column.createFunction("CAST_AS_VARCHAR", new Object[] { column });
            final Column updateColumn = (Column)Column.createFunction("CONCAT", new Object[] { "mdm\\managedappconfiguration\\" + customerId + "\\", castToChar });
            final Column concatCol = (Column)Column.createFunction("CONCAT", new Object[] { updateColumn, "\\app_config.json" });
            concatCol.setDataType("CHAR");
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("ManagedAppConfigurationData");
            updateQuery.setUpdateColumn("APP_CONFIG_PATH", (Object)concatCol);
            updateQuery.setCriteria(new Criteria(new Column("ManagedAppConfigurationData", "APP_CONFIG_PATH"), (Object)("*client-data\\\\" + customerId + "\\\\mdm\\\\*"), 2));
            MDMUtil.getPersistence().update(updateQuery);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Cannot update app config template path ", (Throwable)e);
        }
    }
}
