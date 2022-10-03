package com.adventnet.ds.adapter.mds;

import java.util.Iterator;
import com.adventnet.ds.DataSourceManager;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.mfw.service.Service;

public class MDSService implements Service
{
    private static final Logger LOGGER;
    
    public void create(final DataObject serviceDO) throws Exception {
        if (PersistenceInitializer.isColdStart()) {
            DBThreadLocal.set("default");
            DataAccess.add(PersistenceInitializer.getDefaultDSDO());
        }
        final DataObject mdsDO = DataAccess.getForPersonality("DataSource", new Criteria(Column.getColumn("DataSource", "DSNAME"), "default", 1));
        final Iterator dsRows = mdsDO.getRows("DataSource");
        while (dsRows.hasNext()) {
            final Row dsRow = dsRows.next();
            final DataObject dsDO = mdsDO.getDataObject(mdsDO.getTableNames(), dsRow);
            if (dsDO.containsTable("DBAdapter")) {
                if (PersistenceInitializer.onSAS()) {
                    final boolean isActive = (boolean)dsRow.get("ISACTIVE");
                    final String dsName = (String)dsRow.get(2);
                    try {
                        DataSourceManager.addDataSource(dsDO);
                        if (isActive) {
                            continue;
                        }
                        DataSourceManager.updateDSStatus(dsName, true);
                    }
                    catch (final Exception e) {
                        MDSService.LOGGER.fine("[WARNING] Invalid parameters specified for datasource " + dsRow.get("DSNAME"));
                        if (!isActive) {
                            continue;
                        }
                        DataSourceManager.updateDSStatus(dsName, false);
                    }
                }
                else {
                    DataSourceManager.addDataSource(dsDO);
                }
            }
        }
        DataAccess.update(mdsDO);
    }
    
    public void start() throws Exception {
    }
    
    public void stop() throws Exception {
    }
    
    public void destroy() throws Exception {
    }
    
    static {
        LOGGER = Logger.getLogger(MDSService.class.getName());
    }
}
