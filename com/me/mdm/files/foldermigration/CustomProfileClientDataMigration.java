package com.me.mdm.files.foldermigration;

import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.util.logging.Level;

public class CustomProfileClientDataMigration extends FolderMigrationTask
{
    @Override
    public boolean copyFiles(final Long customerId) {
        this.logger.log(Level.INFO, "Going to migrate custom profiles folder for customer {0}", new Object[] { customerId });
        this.sourcePath = MDMMetaDataUtil.getInstance().getClientDataDir(customerId) + File.separator + "mdm" + File.separator + "customprofiles";
        this.destPath = ProfileUtil.getInstance().getProfilePathWithParentDir(customerId, "customprofiles");
        return super.copyFiles(customerId);
    }
    
    @Override
    public void updateDataBase(final Long customerId) {
        try {
            this.logger.log(Level.INFO, "Custom profiles Folder copy completed:::: Started command file path changes");
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("CustomProfileDetails");
            updateQuery.setCriteria(new Criteria(new Column("CustomProfileDetails", "CUSTOM_PROFILE_PATH"), (Object)("*client-data/" + customerId.toString() + "/mdm/customprofiles*"), 2));
            final Column replaceFunction = (Column)Column.createFunction("REPLACE", new Object[] { Column.getColumn("CustomProfileDetails", "CUSTOM_PROFILE_PATH"), "client-data/" + customerId.toString() + "/mdm/customprofiles", "mdm/profilerepository/" + customerId.toString() + "/customprofiles" });
            replaceFunction.setDataType("CHAR");
            updateQuery.setUpdateColumn("CUSTOM_PROFILE_PATH", (Object)replaceFunction);
            MDMUtil.getPersistence().update(updateQuery);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Cannot update custom profile client ", (Throwable)e);
        }
    }
}
