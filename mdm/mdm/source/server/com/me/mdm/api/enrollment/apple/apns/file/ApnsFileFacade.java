package mdm.mdm.source.server.com.me.mdm.api.enrollment.apple.apns.file;

import com.me.mdm.server.customer.MDMCustomerInfoUtil;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.files.FileFacade;

public class ApnsFileFacade extends FileFacade
{
    @Override
    protected void validateRolesForMSPCustomer(final JSONObject requestJSON) throws Exception {
        final String[] fileNameSplit = String.valueOf(requestJSON.get("file_name")).split("\\.");
        final String extension = (fileNameSplit.length > 1) ? fileNameSplit[fileNameSplit.length - 1] : null;
        if (!extension.equals("pem") || extension == null) {
            throw new APIHTTPException("FIL0002", new Object[0]);
        }
        final Long customerId = APIUtil.optCustomerID(requestJSON);
        if (customerId < 0L && !this.isAllowedUploadApnsPemFile(APIUtil.getLoginID(requestJSON))) {
            throw new APIHTTPException("COM0022", new Object[0]);
        }
    }
    
    public Boolean isAllowedUploadApnsPemFile(final Long loginId) throws Exception {
        final Boolean validateRolesForCurUser = APIUtil.getNewInstance().checkRolesForCurrentUser(new String[] { "MDM_Settings_Write", "MDM_Enrollment_Admin", "ModernMgmt_Enrollment_Admin" });
        return MDMCustomerInfoUtil.getInstance().validateAccessForAllCustomers(loginId) && validateRolesForCurUser;
    }
}
