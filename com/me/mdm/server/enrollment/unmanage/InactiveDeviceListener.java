package com.me.mdm.server.enrollment.unmanage;

import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.customgroup.MDMCustomGroupUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.mdm.server.enrollment.EnrollmentSettingsHandler;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class InactiveDeviceListener extends ManagedDeviceListener
{
    private static final Logger logger;
    
    @Override
    public void deviceUnmanaged(final DeviceEvent deviceEvent) {
        InactiveDeviceListener.mdmlogger.info("Entering InactiveDeviceListener:deviceUnmanaged");
        try {
            final JSONObject json = EnrollmentSettingsHandler.getInstance().getCommonEnrollmentSettings(deviceEvent.customerID);
            if (json.optBoolean("NOTIFY_DEVICE_UNMANAGED", (boolean)Boolean.FALSE)) {
                EnrollmentSettingsHandler.getInstance().sendUnmanagedDeviceNotififcationMail(this.getMailAttributeDetails(deviceEvent));
            }
        }
        catch (final Exception ex) {
            InactiveDeviceListener.logger.log(Level.SEVERE, null, ex);
        }
        InactiveDeviceListener.mdmlogger.info("Exiting InactiveDeviceListener:deviceUnmanaged");
    }
    
    private JSONObject getMailAttributeDetails(final DeviceEvent deviceEvent) {
        final JSONObject mailVariables = new JSONObject();
        mailVariables.put("customerID", (Object)deviceEvent.customerID);
        mailVariables.put("platformType", (Object)MDMUtil.getInstance().getPlatformName(deviceEvent.platformType));
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
            final Join deviceInfoJoin = new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join modelInfoJoin = new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2);
            final Join extnJoin = new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1);
            final Join userToDeviceMappingJoin = new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1);
            final Join mappingToUserJoin = new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1);
            final Join userToResourceJoin = new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 1);
            selectQuery.addJoin(deviceInfoJoin);
            selectQuery.addJoin(modelInfoJoin);
            selectQuery.addJoin(extnJoin);
            selectQuery.addJoin(userToDeviceMappingJoin);
            selectQuery.addJoin(mappingToUserJoin);
            selectQuery.addJoin(userToResourceJoin);
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "MODEL_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID", "MANAGEDDEVICEEXTN.MANAGED_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME", "MANAGEDDEVICEEXTN.NAME"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
            final Criteria managedDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceEvent.resourceID, 0);
            final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
            selectQuery.setCriteria(managedDeviceCriteria.and(userNotInTrashCriteria));
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (ds.next()) {
                final Long resourceID = (Long)ds.getValue("RESOURCE_ID");
                final String serialNumber = (String)ds.getValue("SERIAL_NUMBER");
                final String modelName = (String)ds.getValue("MODEL_NAME");
                final String deviceName = (String)ds.getValue("MANAGEDDEVICEEXTN.NAME");
                final String mail = (String)ds.getValue("EMAIL_ADDRESS");
                final String userName = (String)ds.getValue("NAME");
                String grpNameList = new MDMCustomGroupUtil().getGroupNamesWithResourceID().get(resourceID);
                grpNameList = ((grpNameList == null) ? "--" : grpNameList);
                mailVariables.put("groups", (Object)grpNameList);
                mailVariables.put("RESOURCE_ID", (Object)resourceID);
                mailVariables.put("SERIAL_NUMBER", (Object)serialNumber);
                mailVariables.put("MODEL_NAME", (Object)modelName);
                mailVariables.put("device_name", (Object)deviceName);
                mailVariables.put("EMAIL_ADDRESS", (Object)mail);
                mailVariables.put("NAME", (Object)userName);
            }
        }
        catch (final Exception e) {
            InactiveDeviceListener.logger.log(Level.SEVERE, "Exception while fetching getMailAttributeDetails", e);
        }
        return mailVariables;
    }
    
    static {
        logger = Logger.getLogger("MDMEnrollment");
    }
}
