package com.me.mdm.server.location;

import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.settings.location.LocationSettingsDataHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class DeleteUnwantedLocationTask implements SchedulerExecutionInterface
{
    Logger logger;
    
    public DeleteUnwantedLocationTask() {
        this.logger = Logger.getLogger("MDMLocationLogger");
    }
    
    public void executeTask(final Properties props) {
        this.logger.log(Level.INFO, "inside DeleteUnwantedLocationTask execute task ");
        try {
            final String customerID = props.getProperty("Customer_id");
            if (customerID.equalsIgnoreCase("all")) {
                this.logger.log(Level.INFO, " DeleteUnwantedLocationTask execute task - for all customers");
                final Long[] customerIds = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
                for (int i = 0; i < customerIds.length; ++i) {
                    this.deleteUnwantedLocations(customerIds[i]);
                }
            }
            else if (customerID != null) {
                final Long customerIdLong = Long.parseLong(customerID);
                this.deleteUnwantedLocations(customerIdLong);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error in deleting locations", e);
        }
    }
    
    private void deleteUnwantedLocations(final Long customerID) {
        this.logger.log(Level.INFO, "inside DeleteUnwantedLocationTask deleteUnwantedLocations ");
        this.deleteAllLocationsOfNotApplicableDevices(customerID);
        final boolean historyEnabled = LocationSettingsDataHandler.getInstance().isLocationHistoryEnabled(customerID);
        if (!historyEnabled) {
            this.deleteAllResourceLocationHistory(customerID);
        }
    }
    
    private void deleteAllLocationsOfNotApplicableDevices(final Long customerID) {
        try {
            this.logger.log(Level.INFO, "inside DeleteUnwantedLocationTask deleteAllLocationsOfNotApplicableDevices ");
            final int startIndex = 0;
            final int batchCount = 10000;
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceLocationDetails"));
            final Criteria custCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            selectQuery.addJoin(new Join("MdDeviceLocationDetails", "Resource", new String[] { "DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("MdDeviceLocationDetails", "LostModeTrackInfo", new String[] { "DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            selectQuery.addJoin(new Join("MdDeviceLocationDetails", "LocationDeviceStatus", new String[] { "DEVICE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1));
            final Criteria notInLostMode = new Criteria(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"), (Object)new Integer[] { 2, 6 }, 9).or(new Criteria(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"), (Object)null, 0));
            final Criteria notApplicable = new Criteria(Column.getColumn("LocationDeviceStatus", "IS_ENABLED"), (Object)false, 0);
            selectQuery.setCriteria(notInLostMode.and(notApplicable).and(custCri));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"));
            selectQuery.addSortColumn(new SortColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID", true));
            while (true) {
                selectQuery.setRange(new Range(startIndex, batchCount));
                final DataObject dataObject = DataAccess.get(selectQuery);
                if (dataObject.isEmpty()) {
                    break;
                }
                final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdDeviceLocationDetails");
                final Criteria criteria = new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"), (Object)this.getLocationDetailsId(dataObject), 8);
                deleteQuery.setCriteria(criteria);
                DataAccess.delete(deleteQuery);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error in deleting location history", e);
        }
    }
    
    private void deleteAllResourceLocationHistory(final Long customerID) {
        this.logger.log(Level.INFO, "inside DeleteUnwantedLocationTask deleteAllResourceLocationHistory ");
        try {
            final int startIndex = 0;
            final int batchCount = 10000;
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceLocationDetails"));
            final Criteria historyLocCri = new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"), (Object)Column.getColumn("DeviceRecentLocation", "LOCATION_DETAIL_ID"), 1);
            final Criteria custCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            selectQuery.addJoin(new Join("MdDeviceLocationDetails", "DeviceRecentLocation", new String[] { "DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            selectQuery.addJoin(new Join("MdDeviceLocationDetails", "Resource", new String[] { "DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.setCriteria(historyLocCri.and(custCri));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"));
            selectQuery.addSortColumn(new SortColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID", true));
            while (true) {
                final long start = System.currentTimeMillis();
                selectQuery.setRange(new Range(startIndex, batchCount));
                final DataObject dataObject = DataAccess.get(selectQuery);
                if (dataObject.isEmpty()) {
                    break;
                }
                final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdDeviceLocationDetails");
                final Criteria criteria = new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"), (Object)this.getLocationDetailsId(dataObject), 8);
                deleteQuery.setCriteria(criteria);
                DataAccess.delete(deleteQuery);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error in deleting location history", e);
        }
    }
    
    private long[] getLocationDetailsId(final DataObject dataObject) {
        final long[] locationDetailId = new long[dataObject.size("MdDeviceLocationDetails")];
        try {
            final Iterator rows = dataObject.getRows("MdDeviceLocationDetails");
            int counter = 0;
            while (rows.hasNext()) {
                final Row row = rows.next();
                locationDetailId[counter] = (long)row.get("LOCATION_DETAIL_ID");
                ++counter;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error in getLocationDetailsId ", e);
        }
        return locationDetailId;
    }
}
