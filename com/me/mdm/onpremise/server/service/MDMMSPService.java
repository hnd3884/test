package com.me.mdm.onpremise.server.service;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.customer.MDMCustomerInfoUtil;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.mfw.service.Service;

public class MDMMSPService implements Service
{
    private Logger logger;
    
    public MDMMSPService() {
        this.logger = Logger.getLogger("DCServiceLogger");
    }
    
    public void create(final DataObject d) throws Exception {
        this.logger.log(Level.INFO, "_____________________________________________________________________________________________");
        this.logger.log(Level.INFO, "Creating MDM MSP Service...");
        this.logger.log(Level.INFO, "_____________________________________________________________________________________________");
    }
    
    public void start() throws Exception {
        this.logger.log(Level.INFO, "*****************Starting MDM MSP Service*************");
        final Boolean firstStartUp = this.isFirstStartUp();
        this.logger.log(Level.INFO, "First StartUp in MDM MSP:{0}", firstStartUp);
        if (firstStartUp) {
            MDMCustomerInfoUtil.getInstance().addDefaultCustomerForEval();
        }
    }
    
    public void stop() throws Exception {
    }
    
    public void destroy() throws Exception {
    }
    
    private boolean isFirstStartUp() {
        try {
            final SelectQueryImpl query = new SelectQueryImpl(Table.getTable("DCServerUptimeHistory"));
            query.addSelectColumn(Column.getColumn("DCServerUptimeHistory", "*"));
            final DataObject buildHistoryDO = DataAccess.get((SelectQuery)query);
            this.logger.log(Level.INFO, "Inside isFirstStartUp in MDMMSPService");
            if (buildHistoryDO.isEmpty()) {
                return Boolean.TRUE;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception in checking first startup", e);
            return Boolean.FALSE;
        }
        return Boolean.FALSE;
    }
}
