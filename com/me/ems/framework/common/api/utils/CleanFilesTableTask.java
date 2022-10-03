package com.me.ems.framework.common.api.utils;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class CleanFilesTableTask implements SchedulerExecutionInterface
{
    Logger logger;
    
    public CleanFilesTableTask() {
        this.logger = Logger.getLogger(CleanFilesTableTask.class.getName());
    }
    
    @Override
    public void executeTask(final Properties props) {
        this.logger.log(Level.INFO, "Executing CleanFilesTableTask");
        try {
            final long currentTime = SyMUtil.getCurrentTime();
            final ArrayList<Long> fileIDs = new ArrayList<Long>();
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DCFiles"));
            final Criteria timeCriteria = new Criteria(new Column("DCFiles", "EXPIRY_TIME"), (Object)currentTime, 6);
            selectQuery.setCriteria(timeCriteria);
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Iterator dataObjectRows = dataObject.getRows("DCFiles");
                while (dataObjectRows.hasNext()) {
                    final Row row = dataObjectRows.next();
                    final Long expiryTime = (Long)row.get("EXPIRY_TIME");
                    final String filePath = (String)row.get("FILE_SYSTEM_LOCATION");
                    if (expiryTime != -1L) {
                        ApiFactoryProvider.getFileAccessAPI().deleteFile(filePath);
                        fileIDs.add((Long)row.get("FILE_ID"));
                    }
                }
                this.logger.log(Level.INFO, "List of File IDs Deleted in Scheduler are : " + fileIDs.toString());
                DataAccess.delete(new Criteria(Column.getColumn("DCFiles", "FILE_ID"), (Object)fileIDs.toArray(), 8));
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while Executing CleanFilesTableTask ", ex);
        }
    }
}
