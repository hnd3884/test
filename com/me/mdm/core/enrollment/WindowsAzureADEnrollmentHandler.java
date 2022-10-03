package com.me.mdm.core.enrollment;

import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.mdm.core.auth.APIKey;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.HashMap;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.logging.Logger;

public class WindowsAzureADEnrollmentHandler extends AdminEnrollmentHandler
{
    public static Logger logger;
    
    public WindowsAzureADEnrollmentHandler() {
        super(32, "WinAzureADDeviceForEnrollment", "WindowsAzureADEnrollmentTemplate");
    }
    
    @Override
    public void addorUpdateAdminEnrollmentTemplate(final JSONObject enrollmentTemplateJSON) throws Exception {
        final EnrollmentTemplateHandler handler = new EnrollmentTemplateHandler();
        handler.addorUpdateWindowsAzureADEnrollmentTemplate(enrollmentTemplateJSON);
    }
    
    @Override
    public boolean isValidEnrollmentTemplate(final Long templateId) throws Exception {
        return true;
    }
    
    @Override
    public int getUnassignedDeviceCount(final Long customerID) throws Exception {
        int unassignedCount = 0;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("WinAzureADDeviceForEnrollment"));
            sQuery.addJoin(new Join("WinAzureADDeviceForEnrollment", "DeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
            final Criteria customerCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "CUSTOMER_ID"), (Object)customerID, 0);
            sQuery.setCriteria(customerCriteria);
            unassignedCount = DBUtil.getRecordActualCount(sQuery, "WinAzureADDeviceForEnrollment", "ENROLLMENT_DEVICE_ID");
        }
        catch (final Exception exp) {
            WindowsAzureADEnrollmentHandler.logger.log(Level.SEVERE, "Exception while obtaining unassignedCount for WindowsAzureADEnrollment", exp);
        }
        return unassignedCount;
    }
    
    public HashMap getAzureADEnrollmentDetails(final Long customerID) throws Exception {
        JSONObject enrollmentTemplateDetails = this.getEnrollmentTemplateDetails(customerID);
        if (!enrollmentTemplateDetails.has("TEMPLATE_TOKEN")) {
            final JSONObject enrollmentTemplateJSON = new JSONObject();
            enrollmentTemplateJSON.put("CUSTOMER_ID", (Object)customerID);
            enrollmentTemplateJSON.put("ADDED_USER", (Object)MDMUtil.getInstance().getCurrentlyLoggedOnUserID());
            this.addorUpdateAdminEnrollmentTemplate(enrollmentTemplateJSON);
            enrollmentTemplateDetails = this.getEnrollmentTemplateDetails(customerID);
        }
        final String templateToken = String.valueOf(enrollmentTemplateDetails.get("TEMPLATE_TOKEN"));
        final APIKey key = MDMApiFactoryProvider.getMdmPurposeAPIKeyGenerator().generateAPIKey(new JSONObject().put("PURPOSE_KEY", 300));
        final HashMap<String, String> azureADAppConfigurationDetails = new HashMap<String, String>();
        final String httpsServerUrl = MDMApiFactoryProvider.getMDMUtilAPI().getServerURLOnTomcatPortForClientAuthSetup();
        final String termsOfUseUrl = httpsServerUrl + "/mdm/client/v1/wptermsofuse/" + customerID + "?" + key.getAsURLParams();
        final JSONObject params = new JSONObject().put("httpsServerBaseURL", (Object)httpsServerUrl).put("customerID", (Object)customerID).put("urlParams", (Object)key.getAsURLParams());
        final String discoveryUrl = MDMApiFactoryProvider.getMDMUtilAPI().getWindowsAzureADDiscoverURL(params);
        azureADAppConfigurationDetails.put("termsOfUseUrl", termsOfUseUrl);
        azureADAppConfigurationDetails.put("discoveryUrl", discoveryUrl);
        azureADAppConfigurationDetails.put("appIDUri", httpsServerUrl);
        final HashMap<String, String> serverComplianceForAzureADEnrollment = new HashMap<String, String>();
        serverComplianceForAzureADEnrollment.put("isThirdPartyCertConfigured", String.valueOf(SSLCertificateUtil.getInstance().isThirdPartySSLInstalled()));
        final HashMap azureADDetailsJSON = new HashMap();
        azureADDetailsJSON.put("azureADAppDetails", azureADAppConfigurationDetails);
        azureADDetailsJSON.put("serverDetails", serverComplianceForAzureADEnrollment);
        return azureADDetailsJSON;
    }
    
    private JSONObject getEnrollmentTemplateDetails(final Long customerID) throws Exception {
        final JSONObject enrollmentTemplateDetails = new JSONObject();
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("WindowsAzureADEnrollmentTemplate"));
        sQuery.addJoin(new Join("WindowsAzureADEnrollmentTemplate", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
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
    
    static {
        WindowsAzureADEnrollmentHandler.logger = Logger.getLogger("MDMEnrollment");
    }
}
