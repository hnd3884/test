package com.me.mdm.server.enrollment.admin.windows;

import java.util.Hashtable;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Properties;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.server.enrollment.adminenroll.WinLaptopEnrollmentAssignUserCSVProcessor;
import com.me.mdm.core.enrollment.WindowsLaptopEnrollmentHandler;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import com.me.mdm.server.enrollment.admin.AdminEnrollmentDownloadInterface;
import com.me.mdm.server.enrollment.admin.BaseAdminEnrollmentHandler;

public class LaptopEnrollmentAdminHandler extends BaseAdminEnrollmentHandler implements AdminEnrollmentDownloadInterface
{
    public LaptopEnrollmentAdminHandler(final Integer templateType) {
        super(templateType);
    }
    
    @Override
    protected AdminEnrollmentHandler getHandler() {
        return new WindowsLaptopEnrollmentHandler();
    }
    
    @Override
    public String getOperationLabelForTemplate() {
        return new WinLaptopEnrollmentAssignUserCSVProcessor().operationLabel;
    }
    
    @Override
    public JSONObject getEnrollmentDetails(final JSONObject requestJSON) throws Exception {
        final JSONObject json = super.getEnrollmentDetails(requestJSON);
        final Properties contactInfoProps = DMUserHandler.getContactInfoProp(APIUtil.getUserID(requestJSON));
        if (contactInfoProps.containsKey("EMAIL_ID") && !((Hashtable<K, String>)contactInfoProps).get("EMAIL_ID").trim().isEmpty()) {
            final JSONObject additionalContext = new JSONObject();
            additionalContext.put("technicianMailId", ((Hashtable<K, Object>)contactInfoProps).get("EMAIL_ID"));
            json.put("additional_context", (Object)additionalContext);
        }
        return json;
    }
    
    @Override
    public String getFileDownloadPath(final JSONObject requestJSON) throws Exception {
        SyMUtil.updateSyMParameter("Admin_Enrollment_LAPTOP_Tool_Download_Clicked", Boolean.TRUE.toString());
        final String winAdminEnrollPath = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "mdm" + File.separator + "winlaptopenrollment";
        final String tempPath = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "mdm" + File.separator + "temp_" + MDMUtil.getCurrentTimeInMillis();
        final String zipPath = winAdminEnrollPath + File.separator + "WindowsLaptopEnrollment.zip";
        final Long customerID = APIUtil.getCustomerID(requestJSON);
        final Long userId = APIUtil.getUserID(requestJSON);
        final JSONObject tempJSON = new JSONObject();
        tempJSON.put("winAdminEnrollPath", (Object)winAdminEnrollPath);
        tempJSON.put("tempPath", (Object)tempPath);
        tempJSON.put("zipPath", (Object)zipPath);
        tempJSON.put("userID", (Object)userId);
        tempJSON.put("customerID", (Object)customerID);
        new WindowsLaptopEnrollmentHandler().createTemporaryZipFile(tempJSON);
        SyMUtil.updateSyMParameter("LAPTOP_TOOL_DOWNLOADED_ALREADY", Boolean.TRUE.toString());
        MessageProvider.getInstance().hideMessage("DOWNLOAD_LAPTOP_TOOL");
        SyMUtil.updateSyMParameter("Admin_Enrollment_LAPTOP_Tool_Download_Success", Boolean.TRUE.toString());
        return zipPath;
    }
}
