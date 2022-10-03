package com.me.mdm.core.enrollment;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

public class MultipleMgmtEnrollmentHandler extends AdminEnrollmentHandler
{
    public MultipleMgmtEnrollmentHandler() {
        super(-1, "DeviceForEnrollment", null);
    }
    
    @Override
    public void addorUpdateAdminEnrollmentTemplate(final JSONObject enrollmentTemplateJSON) throws Exception {
        Logger.getLogger("MDMEnrollment").log(Level.INFO, "Going to populate enrollment template in for MultipleMgmt..");
        final EnrollmentTemplateHandler handler = new EnrollmentTemplateHandler();
        handler.addorUpdateMultipleMgmtEnrollmentTemplate(enrollmentTemplateJSON);
    }
    
    @Override
    public boolean isValidEnrollmentTemplate(final Long templateId) throws Exception {
        return true;
    }
}
