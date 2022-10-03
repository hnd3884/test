package com.me.mdm.files.foldermigration;

import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.logging.Level;

public class ProfileClientDataMigration extends FolderMigrationTask
{
    @Override
    public void updateDataBase(final Long customerId) {
        try {
            this.logger.log(Level.INFO, "Profile Folder copy completed:::: Started command file path changes");
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MdCommands");
            updateQuery.setCriteria(new Criteria(new Column("MdCommands", "COMMAND_DATA_FILE_PATH"), (Object)("*client-data\\\\" + customerId.toString() + "\\\\mdm\\\\profiles*"), 2));
            final Column replaceFunction = (Column)Column.createFunction("REPLACE", new Object[] { Column.getColumn("MdCommands", "COMMAND_DATA_FILE_PATH"), "client-data\\" + customerId.toString() + "\\mdm\\profiles", "mdm\\profilerepository\\" + customerId.toString() + "\\profiles" });
            replaceFunction.setDataType("CHAR");
            updateQuery.setUpdateColumn("COMMAND_DATA_FILE_PATH", (Object)replaceFunction);
            MDMUtil.getPersistence().update(updateQuery);
            final UpdateQuery updateQuery2 = (UpdateQuery)new UpdateQueryImpl("CollectionMetaData");
            updateQuery2.setCriteria(new Criteria(new Column("CollectionMetaData", "COLLECTION_FILE_PATH"), (Object)("*client-data\\\\" + customerId.toString() + "\\\\mdm\\\\profiles*"), 2));
            final Column replaceFunction2 = (Column)Column.createFunction("REPLACE", new Object[] { Column.getColumn("CollectionMetaData", "COLLECTION_FILE_PATH"), "client-data\\" + customerId.toString() + "\\mdm\\profiles", "mdm\\profilerepository\\" + customerId.toString() + "\\profiles" });
            replaceFunction2.setDataType("CHAR");
            updateQuery2.setUpdateColumn("COLLECTION_FILE_PATH", (Object)replaceFunction2);
            MDMUtil.getPersistence().update(updateQuery2);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Cannot update profile client ", (Throwable)e);
        }
    }
    
    @Override
    public boolean copyFiles(final Long customerId) {
        this.logger.log(Level.INFO, "Going to migrate profiles folder for customer {0}", new Object[] { customerId });
        this.sourcePath = MDMMetaDataUtil.getInstance().getProfilePathWithParentDir(customerId, "profiles");
        this.destPath = ProfileUtil.getInstance().getProfilePathWithParentDir(customerId, "profiles");
        return super.copyFiles(customerId);
    }
}
