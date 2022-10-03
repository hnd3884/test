package com.me.mdm.core.enrollment;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class GSuiteChromeDeviceEnrollmentHandler extends AdminEnrollmentHandler
{
    public static Logger logger;
    
    public GSuiteChromeDeviceEnrollmentHandler() {
        super(40, "GSChromeDeviceForEnrollment", "GSChromeEnrollmentTemplate");
    }
    
    @Override
    public void addorUpdateAdminEnrollmentTemplate(final JSONObject enrollmentTemplateJSON) throws Exception {
        GSuiteChromeDeviceEnrollmentHandler.logger.log(Level.INFO, "Not performing any task for Chrome template token(During New role added , modified , user created ,user modified)");
    }
    
    public static void deleteAdminEnrollmentTemplate(final Long loginID) throws DataAccessException {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("GSChromeEnrollmentTemplate"));
            final Join templateJoin = new Join("GSChromeEnrollmentTemplate", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2);
            sQuery.addJoin(templateJoin);
            sQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final Criteria loginIdCriteria = new Criteria(Column.getColumn("GSChromeEnrollmentTemplate", "LOGIN_ID"), (Object)loginID, 0);
            sQuery.setCriteria(loginIdCriteria);
            DataAccess.delete("GSChromeEnrollmentTemplate", loginIdCriteria);
        }
        catch (final Exception ex) {
            Logger.getLogger(GSuiteChromeDeviceEnrollmentHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public boolean isValidEnrollmentTemplate(final Long templateId) throws Exception {
        return true;
    }
    
    static {
        GSuiteChromeDeviceEnrollmentHandler.logger = Logger.getLogger("MDMEnrollment");
    }
}
