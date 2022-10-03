package com.me.mdm.files;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Logger;

public class ExpiredAPIFileCleanupTask
{
    private Logger logger;
    
    public ExpiredAPIFileCleanupTask() {
        this.logger = Logger.getLogger("MDMAPILogger");
    }
    
    public void executeTask() {
        try {
            final DataObject DO = MDMUtil.getReadOnlyPersistence().get("DMFiles", new Criteria(Column.getColumn("DMFiles", "EXPIRY_TIME"), (Object)MDMUtil.getCurrentTimeInMillis(), 6));
            if (!DO.isEmpty()) {
                for (final String fileName : DBUtil.getColumnValuesAsList(DO.getRows("DMFiles"), "FILE_SYSTEM_LOCATION")) {
                    if (!fileName.contains("MDM/docrepository")) {
                        this.logger.log(Level.INFO, "Deleting file {0}", fileName);
                        final File file = new File(fileName);
                        ApiFactoryProvider.getFileAccessAPI().deleteDirectory((file.getParent() != null) ? file.getParent() : fileName);
                    }
                    else {
                        this.logger.log(Level.INFO, "DMFiles deletion issue skipping content Mgmt file deletion - {0}", fileName);
                    }
                }
                DO.deleteRows("DMFiles", (Criteria)null);
                MDMUtil.getPersistence().update(DO);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(ExpiredAPIFileCleanupTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
