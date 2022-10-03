package com.me.mdm.core.enrollment;

import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.core.auth.APIKey;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.HashMap;
import org.json.JSONObject;

public class WindowsModernMgmtEnrollmentHandler extends AdminEnrollmentHandler
{
    public WindowsModernMgmtEnrollmentHandler() {
        super(33, "WinModernMgmtDeviceForEnrollment", "WindowsModernMgmtEnrollmentTemplate");
    }
    
    @Override
    public void addorUpdateAdminEnrollmentTemplate(final JSONObject enrollmentTemplateJSON) throws Exception {
        final EnrollmentTemplateHandler handler = new EnrollmentTemplateHandler();
        handler.addorUpdateWindowsModernMgmtTemplate(enrollmentTemplateJSON);
    }
    
    @Override
    public boolean isValidEnrollmentTemplate(final Long templateId) throws Exception {
        return true;
    }
    
    public HashMap getModernMgmtEnrollmentDetails(final Long customerID, final Long userID) throws Exception {
        final HashMap hashMap = new HashMap();
        JSONObject enrollmentTemplateDetails = this.getEnrollmentTemplateDetails(customerID);
        if (!enrollmentTemplateDetails.has("TEMPLATE_TOKEN")) {
            final JSONObject enrollmentTemplateJSON = new JSONObject();
            enrollmentTemplateJSON.put("CUSTOMER_ID", (Object)customerID);
            enrollmentTemplateJSON.put("ADDED_USER", (Object)userID);
            this.addorUpdateAdminEnrollmentTemplate(enrollmentTemplateJSON);
            enrollmentTemplateDetails = this.getEnrollmentTemplateDetails(customerID);
        }
        final JSONObject jsonObject = new JSONObject().put("PURPOSE_KEY", 301);
        jsonObject.put("CUSTOMER_ID", (Object)customerID);
        final APIKey key = MDMApiFactoryProvider.getMdmPurposeAPIKeyGenerator().generateAPIKey(jsonObject);
        hashMap.put("encapiKey", key.getKeyValue());
        hashMap.put("TEMPLATE_TOKEN", enrollmentTemplateDetails.get("TEMPLATE_TOKEN"));
        return hashMap;
    }
    
    private JSONObject getEnrollmentTemplateDetails(final Long customerID) throws Exception {
        final JSONObject enrollmentTemplateDetails = new JSONObject();
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("WindowsModernMgmtEnrollmentTemplate"));
        sQuery.addJoin(new Join("WindowsModernMgmtEnrollmentTemplate", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        sQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "*"));
        final Criteria templateTypeCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)this.templateType, 0);
        final Criteria customerIDCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerID, 0);
        sQuery.setCriteria(templateTypeCriteria.and(customerIDCriteria));
        final DataObject enrollmentTemplateDO = MDMUtil.getPersistence().get(sQuery);
        if (!enrollmentTemplateDO.isEmpty()) {
            final Row enrollmentTemplateRow = enrollmentTemplateDO.getFirstRow("EnrollmentTemplate");
            for (final Object columnName : enrollmentTemplateRow.getColumns()) {
                enrollmentTemplateDetails.put((String)columnName, enrollmentTemplateRow.get((String)columnName));
            }
        }
        return enrollmentTemplateDetails;
    }
}
