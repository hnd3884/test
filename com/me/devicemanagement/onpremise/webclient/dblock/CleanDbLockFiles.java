package com.me.devicemanagement.onpremise.webclient.dblock;

import java.util.Iterator;
import com.zoho.framework.utils.FileUtils;
import java.io.File;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;

public class CleanDbLockFiles
{
    public static void SetNotificationOff() {
        try {
            final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            final Criteria c = new Criteria(new Column("DbLockNotification", "LOGIN_ID"), (Object)loginID, 0);
            final DataObject data = SyMUtil.getPersistence().get("DbLockNotification", c);
            if (data.isEmpty()) {
                final Row r = new Row("DbLockNotification");
                r.set("LOGIN_ID", (Object)loginID);
                r.set("LAST_NOTIFIED_TIME", (Object)System.currentTimeMillis());
                final DataObject d = (DataObject)new WritableDataObject();
                d.addRow(r);
                SyMUtil.getPersistence().add(d);
            }
            else {
                data.set("DbLockNotification", "LAST_NOTIFIED_TIME", (Object)System.currentTimeMillis());
                SyMUtil.getPersistence().update(data);
            }
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(CleanDbLockFiles.class.getName()).log(Level.SEVERE, "Exception while updating dblocksettings table..", (Throwable)ex);
        }
        catch (final Exception e) {
            Logger.getLogger(CleanDbLockFiles.class.getName()).log(Level.SEVERE, "Exception while setting Database lock notification off", e);
        }
    }
    
    public static void deleteDblockFiles() {
        try {
            final String path = System.getProperty("server.home") + File.separator + "logs" + File.separator + "Dblocks";
            FileUtils.deleteDir(new File(path));
        }
        catch (final Exception ex) {
            Logger.getLogger(CleanDbLockFiles.class.getName()).log(Level.SEVERE, "Exception while deleting dblock files..", ex);
        }
        try {
            final DataObject DbLockInfoDo = SyMUtil.getPersistence().get("DbLockInfo", (Criteria)null);
            final Column col = Column.getColumn("DbLockInfo", "IS_DELETED");
            final Criteria c = new Criteria(col, (Object)"false", 0);
            final Iterator r = DbLockInfoDo.getRows("DbLockInfo", c);
            while (r.hasNext()) {
                final Row lockinfo = r.next();
                lockinfo.set("IS_DELETED", (Object)"true");
                DbLockInfoDo.updateRow(lockinfo);
            }
            SyMUtil.getPersistence().update(DbLockInfoDo);
        }
        catch (final DataAccessException ex2) {
            Logger.getLogger(CleanDbLockFiles.class.getName()).log(Level.SEVERE, "Exception while updating dblockinfo table", (Throwable)ex2);
        }
    }
}
