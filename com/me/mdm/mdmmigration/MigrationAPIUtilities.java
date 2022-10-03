package com.me.mdm.mdmmigration;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;

public class MigrationAPIUtilities
{
    private static Logger logger;
    
    public int getServiceID(final Long config_id) {
        int service_id = -1;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("APIServiceConfiguration"));
            selectQuery.addSelectColumn(new Column("APIServiceConfiguration", "*"));
            selectQuery.setCriteria(new Criteria(new Column("APIServiceConfiguration", "CONFIG_ID"), (Object)config_id, 0));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            final Iterator iterator = dataObject.getRows("APIServiceConfiguration");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                service_id = (int)row.get("SERVICE_ID");
            }
        }
        catch (final Exception e) {
            MigrationAPIUtilities.logger.log(Level.SEVERE, "Error while fetching Service ID from APIServiceConfiguration", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return service_id;
    }
    
    static {
        MigrationAPIUtilities.logger = Logger.getLogger("MDMMigrationLogger");
    }
}
