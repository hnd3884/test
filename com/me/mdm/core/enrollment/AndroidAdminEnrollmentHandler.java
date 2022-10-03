package com.me.mdm.core.enrollment;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AndroidAdminEnrollmentHandler extends AdminEnrollmentHandler
{
    public static final int DIRECT_DOWNLOAD = 1;
    public static final int PLAY_STORE_DOWNLOAD = 2;
    public static Logger logger;
    
    public AndroidAdminEnrollmentHandler() {
        super(20, "AndroidNFCDeviceForEnrollment", "AndroidAdminEnrollmentTemplate");
    }
    
    @Override
    public void addorUpdateAdminEnrollmentTemplate(final JSONObject enrollmentTemplateJSON) throws Exception {
        final EnrollmentTemplateHandler handler = new EnrollmentTemplateHandler();
        handler.addorUpdateAndroidAdminEnrollmentTemplate(enrollmentTemplateJSON);
    }
    
    public static void deleteAdminEnrollmentTemplate(final Long loginID) throws DataAccessException {
        DataAccess.delete("AndroidAdminEnrollmentTemplate", new Criteria(Column.getColumn("AndroidAdminEnrollmentTemplate", "LOGIN_ID"), (Object)loginID, 0));
    }
    
    @Override
    public boolean isValidEnrollmentTemplate(final Long templateId) throws Exception {
        return true;
    }
    
    static {
        AndroidAdminEnrollmentHandler.logger = Logger.getLogger("MDMEnrollment");
    }
}
