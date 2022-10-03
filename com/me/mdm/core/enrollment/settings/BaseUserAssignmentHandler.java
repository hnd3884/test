package com.me.mdm.core.enrollment.settings;

import java.util.Iterator;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.core.enrollment.DeviceForEnrollmentHandler;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BaseUserAssignmentHandler
{
    public Logger logger;
    List removalimeiList;
    List removalserialList;
    List removaleasList;
    List removaludidList;
    
    public BaseUserAssignmentHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
        this.removalimeiList = new ArrayList();
        this.removalserialList = new ArrayList();
        this.removaleasList = new ArrayList();
        this.removaludidList = new ArrayList();
    }
    
    public JSONObject delegateUserAssignmentRule(final JSONArray deviceProps, final JSONObject rules) {
        final JSONObject jsonObject = new JSONObject();
        this.logger.log(Level.INFO, "Delegated Rule Set {0} was applied to {1}", new Object[] { rules, deviceProps });
        return jsonObject;
    }
    
    public JSONArray getAuthenticatedUser(final JSONArray deviceProps, final Long customerID) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceForEnrollment"));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
        selectQuery.addJoin(new Join("DeviceEnrollmentToRequest", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        selectQuery.addJoin(new Join("EnrollmentTemplateToRequest", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        final Criteria devCriteria = new DeviceForEnrollmentHandler().getDeviceForEnrollmentCriteria(deviceProps);
        final Criteria customerCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.setCriteria(devCriteria.and(customerCriteria));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "*"));
        final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
        final JSONArray resourcesJSON = new JSONArray();
        while (dmDataSetWrapper.next()) {
            final JSONObject resourceJSON = new JSONObject();
            resourceJSON.put("SerialNumber", dmDataSetWrapper.getValue("SERIAL_NUMBER"));
            resourceJSON.put("IMEI", dmDataSetWrapper.getValue("IMEI"));
            resourceJSON.put("UDID", dmDataSetWrapper.getValue("UDID"));
            resourceJSON.put("EASID", dmDataSetWrapper.getValue("EAS_DEVICE_IDENTIFIER"));
            resourceJSON.put("UserName", dmDataSetWrapper.getValue("NAME"));
            resourceJSON.put("EmailAddr", dmDataSetWrapper.getValue("EMAIL_ADDRESS"));
            resourceJSON.put("DomainName", dmDataSetWrapper.getValue("DOMAIN_NETBIOS_NAME"));
            resourcesJSON.put((Object)resourceJSON);
        }
        return resourcesJSON;
    }
    
    public void userRuleMatched(final JSONObject deviceProps, final JSONObject userProps, final Long customerID) throws Exception {
        this.logger.log(Level.INFO, "A delegated user assignment has picked a user {0} for the device {1}", new Object[] { userProps, deviceProps });
        final Long DFEId = new DeviceForEnrollmentHandler().getDeviceForEnrollmentId(deviceProps);
        if (DFEId != null) {
            this.logger.log(Level.INFO, "found DFE entry for the device {0}", DFEId);
            final JSONObject jsonObject = new EnrollmentTemplateHandler().getDeviceForEnrollmentIDDetails(DFEId, customerID);
            final int templateType = jsonObject.getInt("TEMPLATE_TYPE");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("OnBoardingRule"));
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            new UserRuleHandler().getUserRuleJoin(selectQuery);
            new DeviceTypeHandler().getDeviceTypeJoin(selectQuery);
            new DeviceNameHandler().getDeviceNameJoin(selectQuery);
            new GroupAssignmentHandler().getGroupJoin(selectQuery);
            selectQuery.addJoin(new Join("OnBoardingRule", "OnBoardingSettings", new String[] { "ON_BOARD_RULE_ID" }, new String[] { "ON_BOARD_RULE_ID" }, 2));
            selectQuery.addJoin(new Join("OnBoardingSettings", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
            selectQuery.setCriteria(new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)templateType, 0).and(new Criteria(Column.getColumn("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerID, 0)));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            deviceProps.put("UserName", userProps.get("NAME"));
            deviceProps.put("DomainName", userProps.get("DOMAIN_NETBIOS_NAME"));
            deviceProps.put("EmailAddr", (Object)userProps.optString("EMAIL_ADDRESS"));
            final JSONArray deviceArray = new JSONArray();
            deviceArray.put((Object)deviceProps);
            if (!dataObject.isEmpty()) {
                final Long userAssignID = this.getUserAssignmentID(dataObject, deviceArray);
                this.logger.log(Level.INFO, "Going to perform User Assignment ");
                new UserAssignmentRuleHandler().completeUserAssignmentForDevice(deviceArray, dataObject, userAssignID);
                this.logger.log(Level.INFO, "Delegated user assignment was completed");
            }
        }
    }
    
    protected Long getUserAssignmentID(final DataObject dataObject, final JSONArray deviceJSON) throws DataAccessException {
        final Row row = dataObject.getFirstRow("OnBoardingRule");
        return (Long)row.get("ON_BOARD_RULE_ID");
    }
    
    protected JSONArray removeDevicesNotToBeAssigned(final JSONArray resourceProps, final Long customerID, final DataObject ruleDO) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceForEnrollment"));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToUser", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
        final Criteria devCriteria = new DeviceForEnrollmentHandler().getDeviceForEnrollmentCriteria(resourceProps);
        final Criteria customerCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.setCriteria(devCriteria.and(customerCriteria));
        selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "*"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        final Iterator iterator = dataObject.getRows("DeviceForEnrollment");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            String val = (String)row.get("SERIAL_NUMBER");
            if (val != null) {
                this.removalserialList.add(val);
            }
            val = (String)row.get("IMEI");
            if (val != null) {
                this.removalimeiList.add(val);
            }
            val = (String)row.get("EAS_DEVICE_IDENTIFIER");
            if (val != null) {
                this.removaleasList.add(val);
            }
            val = (String)row.get("UDID");
            if (val != null) {
                this.removaludidList.add(val);
            }
        }
        final JSONArray modifiedList = new JSONArray();
        for (int i = 0; i < resourceProps.length(); ++i) {
            final JSONObject jsonObject = resourceProps.getJSONObject(i);
            final String serialNumber = jsonObject.optString("SerialNumber", (String)null);
            final String easID = jsonObject.optString("EASID", (String)null);
            final String udid = jsonObject.optString("UDID", (String)null);
            final String imei = jsonObject.optString("IMEI", (String)null);
            Boolean alreadyAssigned = Boolean.FALSE;
            if (serialNumber != null && this.removalserialList.contains(serialNumber)) {
                alreadyAssigned = Boolean.TRUE;
            }
            if (easID != null && this.removaleasList.contains(easID)) {
                alreadyAssigned = Boolean.TRUE;
            }
            if (udid != null && this.removaludidList.contains(udid)) {
                alreadyAssigned = Boolean.TRUE;
            }
            if (imei != null && this.removalimeiList.contains(imei)) {
                alreadyAssigned = Boolean.TRUE;
            }
            final JSONObject additionalContext = jsonObject.optJSONObject("additional_context");
            if (!alreadyAssigned && additionalContext != null && additionalContext.has("device_model")) {
                modifiedList.put((Object)jsonObject);
            }
        }
        return modifiedList;
    }
    
    public static BaseUserAssignmentHandler getInstance(final Integer templateType) {
        return new BaseUserAssignmentHandler();
    }
    
    public static BaseUserAssignmentHandler getInstance(final Long templateID) {
        final Integer templateType = EnrollmentTemplateHandler.getTemplateType(templateID);
        return getInstance(templateType);
    }
    
    public static BaseUserAssignmentHandler getInstance(final JSONObject deviceProps, final Long customerID) throws Exception {
        final Long dfeID = new DeviceForEnrollmentHandler().getDeviceForEnrollmentId(deviceProps);
        if (dfeID != null) {
            final JSONObject jsonObject = new EnrollmentTemplateHandler().getDeviceForEnrollmentIDDetails(dfeID, customerID);
            final int templateType = jsonObject.getInt("TEMPLATE_TYPE");
            return getInstance(templateType);
        }
        return null;
    }
}
