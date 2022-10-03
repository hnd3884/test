package com.me.mdm.core.enrollment;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

public class MacModernMgmtEnrollmentHandler extends AdminEnrollmentHandler
{
    public MacModernMgmtEnrollmentHandler() {
        super(12, "MacModernMgmtDeviceForEnrollment", "MacModernMgmtEnrollmentTemplate");
    }
    
    @Override
    public void addorUpdateAdminEnrollmentTemplate(final JSONObject enrollmentTemplateJSON) throws Exception {
        Logger.getLogger("MDMModernMgmtLogger").log(Level.INFO, "START :: Going to populate enrollment template in for ModernMgmt [ This will be called only when Modern Mgmt device is assigned user for firsttime]..");
        final EnrollmentTemplateHandler handler = new EnrollmentTemplateHandler();
        handler.addorUpdateModernMgmtMacEnrollmentTemplate(enrollmentTemplateJSON);
        Logger.getLogger("MDMModernMgmtLogger").log(Level.INFO, "END :: Going to populate enrollment template in for ModernMgmt..");
    }
    
    @Override
    public boolean isValidEnrollmentTemplate(final Long templateId) throws Exception {
        return true;
    }
}
