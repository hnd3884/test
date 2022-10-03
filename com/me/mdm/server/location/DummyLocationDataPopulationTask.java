package com.me.mdm.server.location;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.privacy.PrivacySettingsHandler;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Logger;

public class DummyLocationDataPopulationTask
{
    public Logger logger;
    double[] latitude;
    double[] longitude;
    
    public DummyLocationDataPopulationTask() {
        this.logger = Logger.getLogger("MDMLogger");
        this.latitude = new double[] { 12.831443004076565, 10.056040637171373, 37.68609692360853, 11.541777617646504, 11.155984753129934, 8.719967804731468 };
        this.longitude = new double[] { 80.04931817203243, 78.19218591090255, -121.8938500885669, 78.15636483580182, 77.04395849927839, 77.77005591089211 };
    }
    
    public void executeTask() {
        try {
            final Long[] customerIDs = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            for (int i = 0; i < customerIDs.length; ++i) {
                final long customerID = customerIDs[i];
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
                selectQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                selectQuery.addJoin(new Join("ManagedDevice", "LocationDeviceStatus", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
                final Criteria locationStatusEnabledCri = new Criteria(Column.getColumn("LocationDeviceStatus", "IS_ENABLED"), (Object)true, 0);
                final Criteria managedCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
                final Criteria custCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
                selectQuery.setCriteria(locationStatusEnabledCri.and(managedCri).and(custCri));
                selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
                final DataObject dataObject = DataAccess.get(selectQuery);
                final Iterator rows = dataObject.getRows("ManagedDevice");
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    final long deviceId = (long)row.get("RESOURCE_ID");
                    final JSONObject privacySettings = new PrivacySettingsHandler().getPrivacyDetails(ManagedDeviceHandler.getInstance().getDeviceOwnership(deviceId), customerID);
                    if (privacySettings.getInt("fetch_location") == 2) {
                        continue;
                    }
                    for (int j = 0; j < 5; ++j) {
                        final JSONObject locationObj = new JSONObject();
                        locationObj.put("Latitude", this.latitude[j]);
                        locationObj.put("Longitude", this.longitude[j]);
                        locationObj.put("LocationUpdationTime", System.currentTimeMillis() + j * 3600000);
                        LocationDataHandler.getInstance().deviceLocationUpdates(deviceId, locationObj);
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "exception in DummyLocationDataPopulationTask", e);
        }
    }
}
