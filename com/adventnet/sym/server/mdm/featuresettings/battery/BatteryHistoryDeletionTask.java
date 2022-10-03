package com.adventnet.sym.server.mdm.featuresettings.battery;

import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.Hashtable;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import java.util.logging.Logger;

public class BatteryHistoryDeletionTask
{
    private static Logger logger;
    
    public static void deleteBatteryDetails() throws Exception {
        try {
            final Hashtable ht = DateTimeUtil.determine_From_To_Times("today");
            if (ht != null) {
                final Long[] customerIdsFromDB;
                final Long[] customersList = customerIdsFromDB = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
                for (final Long customer : customerIdsFromDB) {
                    int num_of_days = 7;
                    final DataObject batterySettingsDO = MDMBatterySettingsDBHandler.getMDDeviceBatterySettingsDO(customer, false);
                    if (!batterySettingsDO.isEmpty()) {
                        final Row settingsRow = batterySettingsDO.getRow("MdDeviceBatterySettings");
                        num_of_days = (int)settingsRow.get("HISTORY_DELETION_INTERVAL");
                    }
                    BatteryHistoryDeletionTask.logger.log(Level.INFO, "Beginning the deletion of battery details older than 7 days for customer: {0}", new Object[] { customer });
                    final Long today = ht.get("date1");
                    final Long lastDate = today - num_of_days * 24 * 60 * 60 * 1000L;
                    final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdDeviceBatteryDetails");
                    final Join deviceJoin = new Join("MdDeviceBatteryDetails", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
                    final Criteria customerCriteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customer, 0);
                    deleteQuery.addJoin(deviceJoin);
                    deleteQuery.setCriteria(customerCriteria.and(new Criteria(new Column("MdDeviceBatteryDetails", "DEVICE_UTC_TIME"), (Object)lastDate, 6)));
                    final int detailsDeleted = DataAccess.delete(deleteQuery);
                    BatteryHistoryDeletionTask.logger.log(Level.INFO, "Successfully deleted {0} old battery details for customer: {1}", new Object[] { detailsDeleted, customer });
                }
            }
        }
        catch (final Exception e) {
            BatteryHistoryDeletionTask.logger.log(Level.SEVERE, "Exception while deleting old battery details", e);
        }
    }
    
    static {
        BatteryHistoryDeletionTask.logger = Logger.getLogger("MDMLogger");
    }
}
