package com.me.mdm.server.easmanagement;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.core.UserEvent;
import org.json.simple.JSONObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.sym.server.mdm.core.ManagedUserListener;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class EASListener extends ManagedDeviceListener implements ManagedUserListener
{
    @Override
    public void devicePreRegister(final DeviceEvent oldDeviceEvent) {
        EASListener.mdmlogger.info("Entering EASManagedDeviceListener:devicePreRegister");
        this.devicePreDelete(oldDeviceEvent);
        EASListener.mdmlogger.info("Exiting EASManagedDeviceListener:devicePreRegister");
    }
    
    private HashMap getManagedUserEmailAddressForDevice(final DeviceEvent event) {
        final HashMap enrolledDeviceToManagedUser = ManagedUserHandler.getInstance().getManagedUserDetailsForDevice(event.resourceID);
        EASMgmt.logger.log(Level.INFO, "resourceID = {0} email Address = {1} domain = {2}", new Object[] { event.resourceID, enrolledDeviceToManagedUser.get("EMAIL_ADDRESS"), enrolledDeviceToManagedUser.get("DOMAIN_NETBIOS_NAME") });
        return enrolledDeviceToManagedUser;
    }
    
    @Override
    public void devicePreDelete(final DeviceEvent deviceEvent) {
        final JSONObject exchangeDetails = EASMgmtDataHandler.getInstance().getExchangeServerDetails(false);
        if (exchangeDetails.get((Object)"EAS_SERVER_ID") != null) {
            EASListener.mdmlogger.info("Entering EASManagedDeviceListener:devicePreDelete");
            try {
                final Long deviceResourceID = deviceEvent.resourceID;
                if (deviceResourceID != null) {
                    final HashMap enrolledDeviceDetails = this.getManagedUserEmailAddressForDevice(deviceEvent);
                    final String emailAddress = enrolledDeviceDetails.get("EMAIL_ADDRESS");
                    final String domainName = enrolledDeviceDetails.get("DOMAIN_NETBIOS_NAME");
                    final JSONObject enrollmentPropsJSON = new JSONObject();
                    enrollmentPropsJSON.put((Object)"managed", (Object)false);
                    enrollmentPropsJSON.put((Object)"DOMAIN", (Object)domainName);
                    enrollmentPropsJSON.put((Object)"EMAIL_ADDRESS", (Object)emailAddress);
                    enrollmentPropsJSON.put((Object)"RESOURCE_ID", (Object)deviceResourceID);
                    enrollmentPropsJSON.put((Object)"CUSTOMER_ID", (Object)deviceEvent.customerID);
                    enrollmentPropsJSON.put((Object)"EAS_DEVICE_IDENTIFIER", (Object)EASMgmtDataHandler.getInstance().getMDDeviceInfoEasDeviceIdentifer(deviceResourceID, 300));
                    EASMgmt.getInstance().handleEnrollment(enrollmentPropsJSON);
                }
                else {
                    EASMgmt.logger.log(Level.WARNING, "resource id is received to be null in device listener class.. can't perform coditional access for the enrollment/un-enrollment of this device");
                }
            }
            catch (final Exception ex) {
                EASMgmt.logger.log(Level.SEVERE, null, ex);
            }
            EASListener.mdmlogger.info("Exiting EASManagedDeviceListener:devicePreDelete");
        }
        else {
            EASListener.mdmlogger.info("No EAS Server found!EASServerDetails : " + exchangeDetails.toString());
        }
    }
    
    @Override
    public void deviceManaged(final DeviceEvent deviceEvent) {
        final JSONObject exchangeDetails = EASMgmtDataHandler.getInstance().getExchangeServerDetails(false);
        if (exchangeDetails.get((Object)"EAS_SERVER_ID") != null) {
            EASListener.mdmlogger.info("Entering EASManagedDeviceListener:deviceManaged");
            try {
                final Long managedDeviceResouceID = deviceEvent.resourceID;
                final JSONObject enrollmentPropsJSON = new JSONObject();
                final HashMap enrolledDeviceDetails = this.getManagedUserEmailAddressForDevice(deviceEvent);
                final String emailAddress = enrolledDeviceDetails.get("EMAIL_ADDRESS");
                final String domainName = enrolledDeviceDetails.get("DOMAIN_NETBIOS_NAME");
                enrollmentPropsJSON.put((Object)"managed", (Object)true);
                enrollmentPropsJSON.put((Object)"DOMAIN", (Object)domainName);
                enrollmentPropsJSON.put((Object)"EMAIL_ADDRESS", (Object)emailAddress);
                enrollmentPropsJSON.put((Object)"CUSTOMER_ID", (Object)deviceEvent.customerID);
                enrollmentPropsJSON.put((Object)"RESOURCE_ID", (Object)managedDeviceResouceID);
                enrollmentPropsJSON.put((Object)"EAS_DEVICE_IDENTIFIER", (Object)EASMgmtDataHandler.getInstance().getMDDeviceInfoEasDeviceIdentifer(managedDeviceResouceID, 300));
                EASMgmt.getInstance().handleEnrollment(enrollmentPropsJSON);
            }
            catch (final Exception ex) {
                EASMgmt.logger.log(Level.SEVERE, null, ex);
            }
            EASListener.mdmlogger.info("Exiting EASManagedDeviceListener:deviceManaged");
        }
        else {
            EASListener.mdmlogger.info("No EAS Server found!EASServerDetails : " + exchangeDetails.toString());
        }
    }
    
    @Override
    public void deviceUnmanaged(final DeviceEvent deviceEvent) {
        EASListener.mdmlogger.info("Entering EASManagedDeviceListener:deviceUnmanaged");
        this.devicePreDelete(deviceEvent);
        EASListener.mdmlogger.info("Exiting EASManagedDeviceListener:deviceUnmanaged");
    }
    
    @Override
    public void userAssigned(final DeviceEvent deviceEvent) {
        final JSONObject exchangeDetails = EASMgmtDataHandler.getInstance().getExchangeServerDetails(false);
        if (exchangeDetails.get((Object)"EAS_SERVER_ID") != null) {
            EASListener.mdmlogger.info("Entering EASManagedDeviceListener:userAssigned");
            try {
                final Long deviceResourceID = deviceEvent.resourceID;
                final HashMap enrolledDeviceDetails = this.getManagedUserEmailAddressForDevice(deviceEvent);
                final String emailAddress = enrolledDeviceDetails.get("EMAIL_ADDRESS");
                final String domainName = enrolledDeviceDetails.get("DOMAIN_NETBIOS_NAME");
                final JSONObject enrollmentPropsJSON = new JSONObject();
                enrollmentPropsJSON.put((Object)"managed", (Object)true);
                enrollmentPropsJSON.put((Object)"DOMAIN", (Object)domainName);
                enrollmentPropsJSON.put((Object)"EMAIL_ADDRESS", (Object)emailAddress);
                enrollmentPropsJSON.put((Object)"CUSTOMER_ID", (Object)deviceEvent.customerID);
                enrollmentPropsJSON.put((Object)"RESOURCE_ID", (Object)deviceResourceID);
                enrollmentPropsJSON.put((Object)"EAS_DEVICE_IDENTIFIER", (Object)EASMgmtDataHandler.getInstance().getMDDeviceInfoEasDeviceIdentifer(deviceResourceID, 300));
                EASMgmt.getInstance().handleEnrollment(enrollmentPropsJSON);
            }
            catch (final Exception ex) {
                EASMgmt.logger.log(Level.SEVERE, null, ex);
            }
            EASListener.mdmlogger.info("Exiting EASManagedDeviceListener:userAssigned");
        }
        else {
            EASListener.mdmlogger.info("No EAS Server found!EASServerDetails : " + exchangeDetails.toString());
        }
    }
    
    @Override
    public void userDetailsModified(final UserEvent userEvent) {
        final JSONObject exchangeDetails = EASMgmtDataHandler.getInstance().getExchangeServerDetails(false);
        if (exchangeDetails.get((Object)"EAS_SERVER_ID") != null) {
            EASListener.mdmlogger.info("Entering EASManagedDeviceListener:userDetailsModified");
            try {
                final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUser"));
                sQuery.addJoin(new Join("ManagedUser", "ManagedUserToDevice", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
                sQuery.addJoin(new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                sQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
                final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
                final Criteria userCriteria = new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)userEvent.resourceID, 0);
                final Criteria managedDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
                sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
                sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
                sQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
                sQuery.addSelectColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"));
                sQuery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
                sQuery.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
                sQuery.setCriteria(userCriteria.and(managedDeviceCriteria).and(userNotInTrashCriteria));
                final DataObject dobj = MDMUtil.getPersistence().get(sQuery);
                if (!dobj.isEmpty()) {
                    final Iterator<Row> iter = dobj.getRows("ManagedDevice");
                    while (iter.hasNext()) {
                        final Row row = iter.next();
                        final JSONObject enrollmentPropsJSON = new JSONObject();
                        enrollmentPropsJSON.put((Object)"managed", (Object)true);
                        enrollmentPropsJSON.put((Object)"EMAIL_ADDRESS", dobj.getFirstValue("ManagedUser", "EMAIL_ADDRESS"));
                        enrollmentPropsJSON.put((Object)"DOMAIN", dobj.getFirstValue("Resource", "DOMAIN_NETBIOS_NAME"));
                        enrollmentPropsJSON.put((Object)"CUSTOMER_ID", (Object)userEvent.customerID);
                        enrollmentPropsJSON.put((Object)"RESOURCE_ID", row.get("RESOURCE_ID"));
                        enrollmentPropsJSON.put((Object)"EAS_DEVICE_IDENTIFIER", enrollmentPropsJSON.put((Object)"EAS_DEVICE_IDENTIFIER", (Object)EASMgmtDataHandler.getInstance().getMDDeviceInfoEasDeviceIdentifer((Long)row.get("RESOURCE_ID"), 300)));
                        EASMgmt.getInstance().handleEnrollment(enrollmentPropsJSON);
                    }
                }
            }
            catch (final Exception ex) {
                EASMgmt.logger.log(Level.SEVERE, null, ex);
            }
            EASListener.mdmlogger.info("Exiting EASManagedDeviceListener:userDetailsModified");
        }
        else {
            EASListener.mdmlogger.info("No EAS Server found!EASServerDetails : " + exchangeDetails.toString());
        }
    }
    
    @Override
    public void userTrashed(final UserEvent userEvent) {
    }
    
    @Override
    public void userAdded(final UserEvent userEvent) {
        EASListener.mdmlogger.info("Entering EASManagedDeviceListener:userAdded");
        EASListener.mdmlogger.info("Exiting EASManagedDeviceListener:userAdded");
    }
    
    @Override
    public void userDeleted(final UserEvent userEvent) {
        EASListener.mdmlogger.info("Entering EASManagedDeviceListener:userDeleted");
        EASListener.mdmlogger.info("Exiting EASManagedDeviceListener:userDeleted");
    }
}
