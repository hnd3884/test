package com.me.mdm.server.apps.blocklist.validators;

import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import com.me.mdm.api.controller.IDauthorizer;

public class InventoryAppIDAuthorizer implements IDauthorizer
{
    protected static Logger logger;
    
    @Override
    public void authorize(final String customerIDstr, final Long userID, final String pathParam, final List<Object> idList) throws Exception {
        if (idList != null && !idList.isEmpty()) {
            final Long[] tempArray = this.convertStringListToLongAr(idList);
            final List tempList = new LinkedList(Arrays.asList(tempArray));
            final Long appGroup = tempArray[0];
            switch (pathParam) {
                case "app_id": {
                    InventoryAppIDAuthorizer.logger.log(Level.INFO, "validating the inventory app id {0} for the customer id {1}", new Object[] { appGroup, customerIDstr });
                    final SelectQuery selectQuery = this.getInventoryAppSelectQuery();
                    final Criteria criteria = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroup, 0);
                    final Criteria custCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerIDstr, 0);
                    selectQuery.setCriteria(custCriteria.and(criteria));
                    final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                    if (!dataObject.isEmpty()) {
                        final Iterator<Row> iterator = dataObject.getRows("MdAppGroupDetails");
                        while (iterator.hasNext()) {
                            final Row row = iterator.next();
                            final Long appId = (Long)row.get("APP_GROUP_ID");
                            tempList.remove(appId);
                        }
                    }
                    if (!tempList.isEmpty()) {
                        InventoryAppIDAuthorizer.logger.log(Level.SEVERE, "Exception in AppUpdatePolicyAuthorizer unknown id {0} for path param {1}", new Object[] { tempList, pathParam });
                        throw new APIHTTPException("COM0008", new Object[] { tempList.toString() });
                    }
                    break;
                }
            }
        }
    }
    
    private SelectQuery getInventoryAppSelectQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
        selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "APP_GROUP_ID"));
        return selectQuery;
    }
    
    static {
        InventoryAppIDAuthorizer.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
}
