package com.me.mdm.server.chrome;

import java.util.Hashtable;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.Row;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.admin.DomainHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.ds.query.UpdateQuery;
import com.me.idps.core.util.DirectoryQueryutil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.resource.MDMResourceDataPopulator;
import java.util.Properties;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.JSONException;
import com.me.idps.core.crud.DomainDataPopulator;
import java.util.HashMap;
import java.util.logging.Level;
import com.me.mdm.chrome.agent.enrollment.ChromeDeviceEnrollmentHandler;
import org.json.JSONObject;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;

public class ChromeManagementHandler extends GoogleForWorkSettings
{
    public static void resetAllSettings(final JSONObject googleDetails) throws JSONException, Exception {
        try {
            new ChromeDeviceEnrollmentHandler(googleDetails).resetAllChromeDevices();
        }
        catch (final Exception e) {
            ChromeManagementHandler.logger.log(Level.SEVERE, "Unable to unmanage Chrome device completely because of license issues. Ignoring and removing integration", e);
        }
        final HashMap domainDetails = new HashMap();
        domainDetails.put("CLIENT_ID", 101);
        domainDetails.put("CUSTOMER_ID", googleDetails.get("CUSTOMER_ID"));
        domainDetails.put("NAME", googleDetails.get("MANAGED_DOMAIN_NAME"));
        domainDetails.put("AD_DOMAIN_NAME", googleDetails.get("MANAGED_DOMAIN_NAME"));
        DomainDataPopulator.getInstance().deleteDomain(domainDetails);
    }
    
    public static JSONObject syncChromeDevices(final Long customerID) throws JSONException {
        final JSONObject resultJSON = new JSONObject();
        try {
            final JSONObject googleESAJSON = GoogleForWorkSettings.getGoogleForWorkSettings(customerID, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT);
            final String enterpriseID = googleESAJSON.getString("ENTERPRISE_ID");
            if (!SyMUtil.isStringEmpty(enterpriseID)) {
                final String templateToken = new EnrollmentTemplateHandler().getTemplateTokenForUserId(googleESAJSON.getLong("ADDED_BY"), 40, customerID);
                googleESAJSON.put("TEMPLATE_TOKEN", (Object)templateToken);
                new ChromeDeviceEnrollmentHandler(googleESAJSON).syncChromeDevices();
            }
            resultJSON.put("Status", (Object)"Success");
        }
        catch (final Exception ex) {
            ChromeManagementHandler.logger.log(Level.SEVERE, "Exception in executing task for syncing chrome devices", ex);
            resultJSON.put("Status", (Object)"Failure");
        }
        return resultJSON;
    }
    
    public static JSONObject persistChromeIntegSettings(final Long customerID, final Long userId, final JSONObject json) throws Exception {
        final JSONObject resultJSON = new JSONObject();
        final JSONObject enrollData = new JSONObject();
        final boolean dirIntegOnly = json.optBoolean("DIR_INTEG", false);
        final String adminEmail = (String)json.get("ADMIN_ACCOUNT_ID");
        final String authCode = (String)json.get("AUTHORIZATION_CODE");
        final String domainName = (String)json.get("DOMAIN_NAME");
        enrollData.put("DomainName", (Object)domainName);
        JSONObject dataJSON = new JSONObject();
        dataJSON.put("Data", (Object)enrollData);
        final JSONObject responseJSON = new ChromeOAuthHandler().validateAuthCode(authCode, domainName, dirIntegOnly);
        final String status = String.valueOf(responseJSON.get("Status"));
        if (status != null && status.equalsIgnoreCase("Success")) {
            dataJSON = responseJSON.getJSONObject("Data");
            final String enterpriseId = String.valueOf(dataJSON.get("EnterpriseId"));
            final Properties resourceProp = new Properties();
            ((Hashtable<String, Long>)resourceProp).put("CUSTOMER_ID", customerID);
            ((Hashtable<String, String>)resourceProp).put("NAME", domainName);
            ((Hashtable<String, String>)resourceProp).put("DOMAIN_NETBIOS_NAME", domainName);
            ((Hashtable<String, String>)resourceProp).put("RESOURCE_TYPE", String.valueOf(1202));
            final DataObject resourceDO = MDMResourceDataPopulator.addOrUpdateMDMResource(resourceProp);
            final Long resourceId = (Long)resourceDO.getFirstValue("Resource", "RESOURCE_ID");
            ((Hashtable<String, Long>)resourceProp).put("RESOURCE_ID", resourceId);
            addOrUpdateChromIntegSettings(resourceProp, userId);
            final JSONObject esaDataJSON = new JSONObject();
            esaDataJSON.put("BUSINESSSTORE_ID", (Object)resourceId);
            esaDataJSON.put("ESA_EMAIL_ID", (Object)"--");
            esaDataJSON.put("ENTERPRISE_ID", (Object)enterpriseId);
            esaDataJSON.put("ESA_CREDENTIAL_JSON_PATH", (Object)"--");
            esaDataJSON.put("DOMAIN_ADMIN_EMAIL_ID", (Object)adminEmail);
            esaDataJSON.put("MANAGED_DOMAIN_NAME", (Object)domainName);
            esaDataJSON.put("ENTERPRISE_TYPE", (Object)ChromeManagementHandler.ENTERPRISE_TYPE_GOOGLE);
            esaDataJSON.put("OAUTH_TYPE", (Object)ChromeManagementHandler.OAUTH_TYPE_BEARER_TOKEN);
            esaDataJSON.put("REFRESH_TOKEN", dataJSON.get("RefreshToken"));
            esaDataJSON.put("CLIENT_ID", dataJSON.get("ClientId"));
            esaDataJSON.put("CLIENT_SECRET", dataJSON.get("ClientSecret"));
            esaDataJSON.put("REDIRECT_URI", dataJSON.get("RedirectURI"));
            GoogleForWorkSettings.addOrUpdateGoogleESADetails(esaDataJSON);
            addOrUpdateChromeMgmtIntegrations(esaDataJSON, customerID);
            addEnrollmentTemplateForUser(userId, customerID);
            ChromeManagementHandler.logger.log(Level.WARNING, "Chrome integration successfully configured:{0} for customerId {1}", new Object[] { esaDataJSON, customerID });
            resultJSON.put("Status", (Object)"Success");
        }
        else {
            resultJSON.put("Status", (Object)"Error");
            resultJSON.put("ErrorMessage", responseJSON.get("ErrorMessage"));
        }
        return resultJSON;
    }
    
    public static boolean updateEnterpriseID(final Long customerID, final String domainName, final String accessToken) throws Exception {
        final String enterpriseID = new ChromeOAuthHandler().getEnterpriseID(accessToken, domainName, false);
        if (!SyMUtil.isStringEmpty(enterpriseID)) {
            final Criteria criteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerID, 0).and(new Criteria(new Column("GoogleESADetails", "ENTERPRISE_ID"), (Object)"--", 0, false)).and(new Criteria(new Column("Resource", "RESOURCE_TYPE"), (Object)1202, 0)).and(new Criteria(new Column("GoogleESADetails", "MANAGED_DOMAIN_NAME"), (Object)domainName, 0, false));
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("GoogleESADetails");
            updateQuery.addJoin(new Join("GoogleESADetails", "Resource", new String[] { "BUSINESSSTORE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            updateQuery.addJoin(new Join("GoogleESADetails", "ChromeMgmtIntegration", new String[] { "BUSINESSSTORE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            updateQuery.setCriteria(criteria);
            updateQuery.setUpdateColumn("ENTERPRISE_ID", (Object)enterpriseID);
            final int numberOfRowsUpdated = DirectoryQueryutil.getInstance().executeUpdateQuery(updateQuery, false);
            if (numberOfRowsUpdated == 1) {
                return true;
            }
        }
        return false;
    }
    
    static void addOrUpdateChromeMgmtIntegrations(final JSONObject dataJSON, final Long customerId) throws JSONException, SyMException, DataAccessException {
        final Long mdmResourceId = dataJSON.getLong("BUSINESSSTORE_ID");
        final JSONObject domainDetails = new JSONObject();
        domainDetails.put("MANAGED_DOMAIN_NAME", (Object)String.valueOf(dataJSON.get("MANAGED_DOMAIN_NAME")));
        domainDetails.put("CUSTOMER_ID", (Object)customerId);
        final Long domainId = registerGSuiteDirectoryAsDomain(domainDetails);
        if (domainId != -1L) {
            addorUpdateDomainToChromeMapping(domainId, mdmResourceId);
        }
    }
    
    private static void addEnrollmentTemplateForUser(final Long userId, final Long customerId) throws JSONException, Exception {
        final Long loginId = DMUserHandler.getLoginIdForUserId(userId);
        final JSONObject enrollmentTemplateJSON = new JSONObject();
        final String domainName = (String)DBUtil.getValueFromDB("AaaLogin", "LOGIN_ID", (Object)loginId, "DOMAINNAME");
        final Properties userInfo = DMUserHandler.getContactInfoProp(userId);
        final String email = userInfo.getProperty("EMAIL_ID");
        enrollmentTemplateJSON.put("DOMAIN_NETBIOS_NAME", (Object)((domainName != null) ? domainName : "MDM"));
        enrollmentTemplateJSON.put("ADDED_USER", (Object)userId);
        enrollmentTemplateJSON.put("LOGIN_ID", (Object)loginId);
        enrollmentTemplateJSON.put("EMAIL_ADDRESS", (Object)email);
        enrollmentTemplateJSON.put("CUSTOMER_ID", (Object)customerId);
        final EnrollmentTemplateHandler handler = new EnrollmentTemplateHandler();
        handler.addorUpdateGSChromeEnrollmentTemplate(new JSONObject(enrollmentTemplateJSON.toString()));
    }
    
    private static Long registerGSuiteDirectoryAsDomain(final JSONObject domainDetails) throws JSONException, SyMException, DataAccessException {
        if (DomainHandler.getInstance().isDomainDetailsAvailableInDB((String)domainDetails.get("MANAGED_DOMAIN_NAME"), (Long)domainDetails.get("CUSTOMER_ID"))) {
            ChromeManagementHandler.logger.log(Level.INFO, "GSuite Domain not added as already some AD exist with same Domain Name {0}", new Object[] { domainDetails });
            return -1L;
        }
        Long domainID = null;
        final Properties newDomainProperties = new Properties();
        ((Hashtable<String, Object>)newDomainProperties).put("NAME", domainDetails.get("MANAGED_DOMAIN_NAME"));
        ((Hashtable<String, Integer>)newDomainProperties).put("CLIENT_ID", 101);
        ((Hashtable<String, Object>)newDomainProperties).put("CUSTOMER_ID", domainDetails.get("CUSTOMER_ID"));
        ((Hashtable<String, String>)newDomainProperties).put("DNS_SUFFIX", "--");
        ((Hashtable<String, String>)newDomainProperties).put("CRD_USERNAME", "NA");
        ((Hashtable<String, String>)newDomainProperties).put("CRD_PASSWORD", "NA");
        ((Hashtable<String, Object>)newDomainProperties).put("AD_DOMAIN_NAME", domainDetails.get("MANAGED_DOMAIN_NAME"));
        ((Hashtable<String, String>)newDomainProperties).put("DC_NAME", "GSuite_Directory::" + domainDetails.get("MANAGED_DOMAIN_NAME"));
        ((Hashtable<String, Boolean>)newDomainProperties).put("IS_AD_DOMAIN", false);
        try {
            final DataObject dmDomainDO = DomainDataPopulator.getInstance().addOrUpdateDMManagedDomain(newDomainProperties);
            domainID = (Long)dmDomainDO.getRow("DMManagedDomain").get("DOMAIN_ID");
        }
        catch (final SyMException ex) {
            Logger.getLogger(ChromeManagementHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        catch (final DataAccessException ex2) {
            Logger.getLogger(ChromeManagementHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex2);
        }
        return domainID;
    }
    
    private static void addorUpdateDomainToChromeMapping(final Long dmDomainId, final Long mdmResourceId) throws DataAccessException {
        final Row newRelRow = new Row("ChromeMgmtToDirectoryRel");
        newRelRow.set("RESOURCE_ID", (Object)mdmResourceId);
        newRelRow.set("DOMAIN_ID", (Object)dmDomainId);
        if (DataAccess.get("ChromeMgmtToDirectoryRel", newRelRow).isEmpty()) {
            final DataObject wriDO = (DataObject)new WritableDataObject();
            wriDO.addRow(newRelRow);
            DataAccess.update(wriDO);
        }
    }
    
    private static void addOrUpdateChromIntegSettings(final Properties prop, final Long userId) throws DataAccessException {
        final Long customerID = ((Hashtable<K, Long>)prop).get("CUSTOMER_ID");
        final String domainName = ((Hashtable<K, String>)prop).get("NAME");
        final Long resourceId = ((Hashtable<K, Long>)prop).get("RESOURCE_ID");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ChromeMgmtIntegration"));
        selectQuery.addJoin(new Join("ChromeMgmtIntegration", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria customerIdCrietria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria bsIdentifierCrietria = new Criteria(new Column("Resource", "NAME"), (Object)domainName, 0);
        selectQuery.addSelectColumn(new Column("ChromeMgmtIntegration", "*"));
        selectQuery.setCriteria(customerIdCrietria.and(bsIdentifierCrietria));
        final DataObject dO = MDMUtil.getPersistence().get(selectQuery);
        Row row = null;
        Boolean isAdded = true;
        if (dO.isEmpty()) {
            row = new Row("ChromeMgmtIntegration");
        }
        else {
            row = dO.getFirstRow("ChromeMgmtIntegration");
            isAdded = false;
        }
        row.set("RESOURCE_ID", (Object)resourceId);
        row.set("ADDED_BY", (Object)userId);
        if (isAdded) {
            dO.addRow(row);
            MDMUtil.getPersistence().add(dO);
        }
        else {
            dO.updateRow(row);
            MDMUtil.getPersistence().update(dO);
        }
    }
}
