package com.me.mdm.server.enrollment.admin;

import org.json.JSONArray;
import com.me.mdm.api.error.APIHTTPException;
import java.util.Arrays;
import java.util.List;
import com.me.mdm.server.enrollment.EnrollmentFacade;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.server.enrollment.admin.migration.IOSMigrationEnrollmentHandler;
import com.me.mdm.server.enrollment.admin.windows.WindowsModernMgmtEnrollmentHandler;
import com.me.mdm.server.enrollment.admin.apple.MacAdminEnrollmentHandler;
import com.me.mdm.server.enrollment.admin.chrome.ChromeAdminEnrollmentHandler;
import com.me.mdm.server.enrollment.admin.windows.AzureADEnrollmentHandler;
import com.me.mdm.server.enrollment.admin.windows.LaptopEnrollmentAdminHandler;
import com.me.mdm.server.enrollment.admin.android.AndroidZTEnrollmnetHandler;
import com.me.mdm.server.enrollment.admin.android.AndroidQREnrollmentHandler;
import com.me.mdm.server.enrollment.admin.windows.WindowsICDAdminHandler;
import com.me.mdm.server.enrollment.admin.android.KnoxAdminEnrollmentHandler;
import com.me.mdm.server.enrollment.admin.android.AndroidNFCEnrollmentHandler;
import com.me.mdm.server.enrollment.admin.apple.AppleConfiguratorEnrollmentHandler;
import java.util.Map;
import com.me.devicemanagement.framework.server.csv.CSVImportStatusHandler;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;

public abstract class BaseAdminEnrollmentHandler implements AdminEnrollmentInterface
{
    Integer templateType;
    
    public BaseAdminEnrollmentHandler(final Integer templateType) {
        this.templateType = templateType;
    }
    
    protected abstract AdminEnrollmentHandler getHandler();
    
    @Override
    public JSONObject getEnrollmentDetails(final JSONObject requestJSON) throws Exception {
        return this.getAdminEnrollStatus(APIUtil.getCustomerID(requestJSON));
    }
    
    public JSONObject getAdminEnrollStatus(final Long customerID) throws Exception {
        final JSONObject json = new JSONObject();
        final AdminEnrollmentHandler adminEnrollmentHandler = this.getHandler();
        json.put("adminEnrolledDeviceCount", adminEnrollmentHandler.getAdminEnrolledDeviceCount(customerID));
        json.put("unAssignedDeviceCount", adminEnrollmentHandler.getUnassignedDeviceCount(customerID));
        json.put("unEnrolledDeviceCount", adminEnrollmentHandler.getUnEnrolledDeviceCount(customerID));
        return json;
    }
    
    public abstract String getOperationLabelForTemplate();
    
    public JSONObject getImportStatus(final JSONObject requestJSON) throws Exception {
        final String operation = this.getOperationLabelForTemplate();
        final org.json.simple.JSONObject props = CSVImportStatusHandler.getInstance().getImportStatus(APIUtil.getCustomerID(requestJSON), operation);
        final JSONObject response = new JSONObject((Map)props);
        return response;
    }
    
    public static BaseAdminEnrollmentHandler getInstance(final Integer templateType) {
        switch (templateType) {
            case 11: {
                return new AppleConfiguratorEnrollmentHandler(templateType);
            }
            case 20: {
                return new AndroidNFCEnrollmentHandler(templateType);
            }
            case 21: {
                return new KnoxAdminEnrollmentHandler(templateType);
            }
            case 30: {
                return new WindowsICDAdminHandler(templateType);
            }
            case 22: {
                return new AndroidQREnrollmentHandler(templateType);
            }
            case 23: {
                return new AndroidZTEnrollmnetHandler(templateType);
            }
            case 31: {
                return new LaptopEnrollmentAdminHandler(templateType);
            }
            case 32: {
                return new AzureADEnrollmentHandler(templateType);
            }
            case 40: {
                return new ChromeAdminEnrollmentHandler(templateType);
            }
            case 12: {
                return new MacAdminEnrollmentHandler(templateType);
            }
            case -1: {
                return new MultipleEnrollmentHandler(templateType);
            }
            case 33: {
                return new WindowsModernMgmtEnrollmentHandler(templateType);
            }
            case 50: {
                return new IOSMigrationEnrollmentHandler(templateType);
            }
            default: {
                return null;
            }
        }
    }
    
    public static JSONObject removeDevice(final JSONObject requestJSON) throws Exception {
        final Long userID = APIUtil.getUserID(requestJSON);
        final JSONArray deviceForEnrollmentList = requestJSON.getJSONObject("msg_body").getJSONArray("device_ids");
        final String userName = DMUserHandler.getUserNameFromUserID(userID);
        final ArrayList deviceList = new ArrayList();
        for (int i = 0; i < deviceForEnrollmentList.length(); ++i) {
            deviceList.add(deviceForEnrollmentList.get(i));
        }
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        if (!new EnrollmentFacade().validateEnrollmentDeviceId(deviceList, customerId)) {
            throw new APIHTTPException("COM0008", new Object[] { Arrays.toString(deviceList.toArray()) });
        }
        final Boolean successfullyRemoved = AdminEnrollmentHandler.removeDevice(deviceList, userName, APIUtil.getCustomerID(requestJSON));
        final JSONObject response = new JSONObject();
        response.put("success", (Object)successfullyRemoved);
        if (successfullyRemoved) {
            response.put("devices", (Object)deviceForEnrollmentList);
        }
        return response;
    }
}
