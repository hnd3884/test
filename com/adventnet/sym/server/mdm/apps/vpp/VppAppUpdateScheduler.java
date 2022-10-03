package com.adventnet.sym.server.mdm.apps.vpp;

import java.util.List;
import com.me.mdm.server.apps.ios.vpp.VPPTokenDataHandler;
import com.me.mdm.server.apps.businessstore.ios.IOSSyncAppsHandler;
import org.json.JSONObject;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import com.me.mdm.server.apps.businessstore.BusinessStoreSyncConstants;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import java.util.logging.Logger;

public class VppAppUpdateScheduler
{
    public Logger logger;
    
    public VppAppUpdateScheduler() {
        this.logger = Logger.getLogger("MDMVPPAppsMgmtLogger");
    }
    
    public void executeVPPTask() {
        try {
            final DataObject vppTokenDO = this.getVppConfiguredCustomers();
            if (!vppTokenDO.isEmpty()) {
                final Iterator item = vppTokenDO.getRows("Resource");
                while (item.hasNext()) {
                    final Row vppTokenRow = item.next();
                    final Long customerId = (Long)vppTokenRow.get("CUSTOMER_ID");
                    this.executeVPPTask(customerId);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Excetion while updating VPP details ", e);
        }
    }
    
    private DataObject getVppConfiguredCustomers() throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        sQuery.addJoin(new Join("Resource", "ManagedBusinessStore", new String[] { "RESOURCE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
        sQuery.addJoin(new Join("ManagedBusinessStore", "MdBusinessStoreToVppRel", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
        sQuery.addJoin(new Join("MdBusinessStoreToVppRel", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
        sQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
        final SelectQuery selectQuery1 = (SelectQuery)new SelectQueryImpl(Table.getTable("TaskToCustomerRel"));
        selectQuery1.addSelectColumn(Column.getColumn("TaskToCustomerRel", "CUSTOMER_ID"));
        selectQuery1.addJoin(new Join("TaskToCustomerRel", "TaskDetails", new String[] { "TASK_ID" }, new String[] { "TASK_ID" }, 2));
        selectQuery1.setCriteria(new Criteria(Column.getColumn("TaskDetails", "TYPE"), (Object)7200, 0));
        final Column derivedColumn = (Column)new DerivedColumn("derived", selectQuery1);
        final Criteria criteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)derivedColumn, 9);
        sQuery.setCriteria(criteria);
        final DataObject DO = MDMUtil.getPersistence().get(sQuery);
        return DO;
    }
    
    public void executeVPPTask(final Long customerId) {
        try {
            final List<Long> businessStoreIDs = MDBusinessStoreUtil.getBusinessStoreIDs(customerId, BusinessStoreSyncConstants.BS_SERVICE_VPP);
            if (!businessStoreIDs.isEmpty()) {
                this.logger.log(Level.INFO, "Start of VPP sync for customer id {0}", customerId);
                for (int i = 0; i < businessStoreIDs.size(); ++i) {
                    final Long businessStoreID = businessStoreIDs.get(i);
                    this.logger.log(Level.INFO, "Start of VPP sync for businessStoreId {0}", businessStoreID);
                    final JSONObject params = new JSONObject();
                    final IOSSyncAppsHandler syncAppsHandler = new IOSSyncAppsHandler(businessStoreID, customerId);
                    params.put("userID", (Object)VPPTokenDataHandler.getInstance().getVppTokenAddedByUserID(businessStoreID));
                    params.put("isDailySync", (Object)Boolean.TRUE);
                    syncAppsHandler.syncApps(params);
                }
                this.logger.log(Level.INFO, "End of VPP sync for businessStoreID {0}", customerId);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while performing VPP App Update Sync ", e);
        }
    }
}
