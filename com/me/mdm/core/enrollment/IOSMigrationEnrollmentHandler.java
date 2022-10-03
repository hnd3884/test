package com.me.mdm.core.enrollment;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.Row;
import com.me.mdm.core.auth.APIKey;
import com.me.mdm.core.auth.MDMAPIKeyGeneratorAPI;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.core.auth.MDMUserAPIKeyGenerator;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import org.json.JSONObject;

public class IOSMigrationEnrollmentHandler extends AdminEnrollmentHandler
{
    public IOSMigrationEnrollmentHandler() {
        super(50, "MigrationDeviceForEnrollment", "IOSMigrationEnrollmentTemplate");
    }
    
    @Override
    public void addorUpdateAdminEnrollmentTemplate(final JSONObject enrollmentTemplateJSON) throws Exception {
        final EnrollmentTemplateHandler handler = new EnrollmentTemplateHandler();
        handler.addorUpdateIOSMigrationEnrollmentTemplate(enrollmentTemplateJSON);
    }
    
    public JSONObject getProfileDownloadURL(final Long userID, final Long customerID) {
        final JSONObject downloadUrls = new JSONObject();
        try {
            String serviceConfigUrl = MDMEnrollmentUtil.getInstance().getServerBaseURL() + "/mdm/client/v1/ios/MDMServiceConfig" + "?templateToken=" + new EnrollmentTemplateHandler().getTemplateTokenForUserId(userID, this.templateType, customerID);
            final MDMAPIKeyGeneratorAPI generator = MDMUserAPIKeyGenerator.getInstance();
            String enrollmentUrl = MDMEnrollmentUtil.getInstance().getServerBaseURL() + "/mdm/client/v1/ios/ac/" + new EnrollmentTemplateHandler().getTemplateTokenForUserId(userID, this.templateType, customerID);
            if (generator != null) {
                final JSONObject json = new JSONObject();
                json.put("LOGIN_ID", (Object)DMUserHandler.getLoginIdForUserId(userID));
                json.put("TEMPLATE_TYPE", this.templateType);
                final APIKey key = generator.generateAPIKey(json);
                enrollmentUrl = enrollmentUrl + "?" + key.getAsURLParams();
                serviceConfigUrl = serviceConfigUrl + "&" + key.getAsURLParams();
            }
            downloadUrls.put("enrollmentUrl", (Object)enrollmentUrl);
            downloadUrls.put("serviceConfigUrl", (Object)serviceConfigUrl);
        }
        catch (final Exception ex) {
            Logger.getLogger(IOSMigrationEnrollmentHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return downloadUrls;
    }
    
    @Override
    public boolean isValidEnrollmentTemplate(final Long templateId) throws Exception {
        boolean isValid = false;
        final Row enrollmentTableRow = new Row("EnrollmentTemplate");
        enrollmentTableRow.set("TEMPLATE_ID", (Object)templateId);
        final DataObject dataObject = DataAccess.get("EnrollmentTemplate", enrollmentTableRow);
        final Iterator iterator = dataObject.getRows("EnrollmentTemplate");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            isValid = row.get("TEMPLATE_TYPE").toString().equals("50");
        }
        return isValid;
    }
}
