package com.me.mdm.server.apps.android.afw;

import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.apps.android.afw.appmgmt.GooglePlayBusinessAppHandler;
import com.me.mdm.server.apps.android.afw.layoutmgmt.StoreLayoutManager;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.auth.oauth2.TokenResponseException;
import java.net.UnknownHostException;
import com.me.mdm.webclient.i18n.MDMI18N;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.directory.Directory;
import java.util.Arrays;
import java.security.PrivateKey;
import com.google.api.client.auth.oauth2.Credential;
import java.io.IOException;
import java.security.GeneralSecurityException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.androidenterprise.AndroidEnterprise;
import java.util.Collection;
import java.util.Collections;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import com.me.mdm.server.apps.businessstore.android.AndroidStoreHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.security.spec.InvalidKeySpecException;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.chrome.ChromeManagementHandler;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class GoogleForWorkSettings
{
    protected static final String ESA_CREDENTIAL_JSON_FILE_PATH;
    public static final Integer ENTERPRISE_TYPE_GOOGLE;
    public static final Integer ENTERPRISE_TYPE_EMM;
    public static final Integer SERVICE_TYPE_AFW;
    public static final Integer SERVICE_TYPE_CHROME_MGMT;
    public static final Integer OAUTH_TYPE_ESA;
    public static final Integer OAUTH_TYPE_BEARER_TOKEN;
    protected static Logger logger;
    
    public static JSONObject getGoogleForWorkSettings(final Long customerId, final int serviceType) throws DataAccessException, JSONException {
        GoogleForWorkSettings.logger.log(Level.INFO, "Fetching Google ESA settings for customer {0} ,service Type {1}", new Object[] { customerId, serviceType });
        final JSONObject esaDetails = new JSONObject();
        try {
            final DataObject dO = getAFWSettingsDO(customerId, serviceType);
            if (dO.isEmpty()) {
                esaDetails.put("isConfigured", false);
            }
            else {
                if (dO.size("GoogleESADetails") > 1) {
                    GoogleForWorkSettings.logger.log(Level.WARNING, "More than one GOOGLEESADETAILS row obtained");
                }
                Row row = dO.getFirstRow("GoogleESADetails");
                esaDetails.put("isConfigured", true);
                esaDetails.put("CUSTOMER_ID", (Object)customerId);
                esaDetails.put("BUSINESSSTORE_ID", row.get("BUSINESSSTORE_ID"));
                esaDetails.put("DOMAIN_ADMIN_EMAIL_ID", row.get("DOMAIN_ADMIN_EMAIL_ID"));
                esaDetails.put("ENTERPRISE_ID", row.get("ENTERPRISE_ID"));
                esaDetails.put("ESA_CREDENTIAL_JSON_PATH", row.get("ESA_CREDENTIAL_JSON_PATH"));
                esaDetails.put("ESA_EMAIL_ID", row.get("ESA_EMAIL_ID"));
                esaDetails.put("MANAGED_DOMAIN_NAME", row.get("MANAGED_DOMAIN_NAME"));
                esaDetails.put("ENTERPRISE_TYPE", row.get("ENTERPRISE_TYPE"));
                esaDetails.put("OAUTH_TYPE", row.get("OAUTH_TYPE"));
                if (serviceType == GoogleForWorkSettings.SERVICE_TYPE_AFW) {
                    final Row managedBSrow = dO.getFirstRow("ManagedBusinessStore");
                    esaDetails.put("BUSINESSSTORE_ADDED_BY", managedBSrow.get("BUSINESSSTORE_ADDED_BY"));
                    esaDetails.put("ADDED_BY", managedBSrow.get("BUSINESSSTORE_ADDED_BY"));
                }
                else {
                    final Row managedBSrow = dO.getFirstRow("ChromeMgmtIntegration");
                    esaDetails.put("ADDED_BY", managedBSrow.get("ADDED_BY"));
                }
                if (dO.containsTable("GoogleBearerTokenDetails")) {
                    final JSONObject bearerJSON = new JSONObject();
                    row = dO.getFirstRow("GoogleBearerTokenDetails");
                    bearerJSON.put("GOOGLE_AUTH_ID", row.get("GOOGLE_AUTH_ID"));
                    bearerJSON.put("GOOGLE_PROJECT_ID", row.get("GOOGLE_PROJECT_ID"));
                    bearerJSON.put("REFRESH_TOKEN", row.get("REFRESH_TOKEN"));
                    row = dO.getFirstRow("GoogleOAuthProjectDetails");
                    bearerJSON.put("GOOGLE_PROJECT_ID", row.get("GOOGLE_PROJECT_ID"));
                    bearerJSON.put("CLIENT_ID", row.get("CLIENT_ID"));
                    bearerJSON.put("CLIENT_SECRET", row.get("CLIENT_SECRET"));
                    bearerJSON.put("REDIRECT_URI", row.get("REDIRECT_URI"));
                    esaDetails.put("GoogleBearerTokenDetails", (Object)bearerJSON);
                }
            }
        }
        catch (final DataAccessException e) {
            throw e;
        }
        catch (final JSONException e2) {
            throw e2;
        }
        catch (final RuntimeException e3) {
            throw e3;
        }
        return esaDetails;
    }
    
    public static JSONObject getAfWSettingsForResource(final Long resourceId) {
        try {
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId);
            return getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
        }
        catch (final Exception e) {
            GoogleForWorkSettings.logger.log(Level.SEVERE, "Exception when getting AfW settings", e);
            return new JSONObject();
        }
    }
    
    public static JSONObject getGoogleForWorkSettings(final Long customerID) throws DataAccessException, JSONException {
        final JSONObject json = new JSONObject();
        json.put(GoogleForWorkSettings.SERVICE_TYPE_AFW.toString(), (Object)getGoogleForWorkSettings(customerID, GoogleForWorkSettings.SERVICE_TYPE_AFW));
        json.put(GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT.toString(), (Object)getGoogleForWorkSettings(customerID, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT));
        return json;
    }
    
    public static boolean isGoogleForWorkSettingsConfigured(final Long customerId, final int serviceType) throws DataAccessException {
        final DataObject dO = getAFWSettingsDO(customerId, serviceType);
        return !dO.isEmpty();
    }
    
    public static boolean isAFWSettingsConfigured(final Long customerId) throws DataAccessException {
        final DataObject dO = getAFWSettingsDO(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
        return !dO.isEmpty();
    }
    
    public static boolean isEMMTypeAFWConfigured(final Long customerId) throws DataAccessException, JSONException {
        final DataObject dO = getAFWSettingsDO(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
        if (!dO.isEmpty()) {
            final int enterpriseType = (int)dO.getFirstRow("GoogleESADetails").get("ENTERPRISE_TYPE");
            return enterpriseType == GoogleForWorkSettings.ENTERPRISE_TYPE_EMM;
        }
        return false;
    }
    
    private static DataObject getAFWSettingsDO(final Long customerId, final int serviceType) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("GoogleESADetails"));
        final Join resJoin = new Join("GoogleESADetails", "Resource", new String[] { "BUSINESSSTORE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join bearerTokenJoin = new Join("GoogleESADetails", "GoogleBearerTokenDetails", new String[] { "BUSINESSSTORE_ID" }, new String[] { "GOOGLE_AUTH_ID" }, 1);
        final Join oauthProjectJoin = new Join("GoogleBearerTokenDetails", "GoogleOAuthProjectDetails", new String[] { "GOOGLE_PROJECT_ID" }, new String[] { "GOOGLE_PROJECT_ID" }, 1);
        Join serviceSpcTableJoin = null;
        if (serviceType == GoogleForWorkSettings.SERVICE_TYPE_AFW) {
            serviceSpcTableJoin = new Join("GoogleESADetails", "ManagedBusinessStore", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2);
            sQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"));
            sQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ADDED_BY"));
        }
        else if (serviceType == GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT) {
            serviceSpcTableJoin = new Join("GoogleESADetails", "ChromeMgmtIntegration", new String[] { "BUSINESSSTORE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            sQuery.addSelectColumn(Column.getColumn("ChromeMgmtIntegration", "RESOURCE_ID"));
            sQuery.addSelectColumn(Column.getColumn("ChromeMgmtIntegration", "ADDED_BY"));
        }
        final Criteria customerIdCrietria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        sQuery.addJoin(resJoin);
        sQuery.addJoin(serviceSpcTableJoin);
        sQuery.addJoin(bearerTokenJoin);
        sQuery.addJoin(oauthProjectJoin);
        sQuery.setCriteria(customerIdCrietria);
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "BUSINESSSTORE_ID"));
        sQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "DOMAIN_ADMIN_EMAIL_ID"));
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "ENTERPRISE_ID"));
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "ESA_CREDENTIAL_JSON_PATH"));
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "ESA_EMAIL_ID"));
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "MANAGED_DOMAIN_NAME"));
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "ENTERPRISE_TYPE"));
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "OAUTH_TYPE"));
        sQuery.addSelectColumn(Column.getColumn("GoogleBearerTokenDetails", "GOOGLE_AUTH_ID"));
        sQuery.addSelectColumn(Column.getColumn("GoogleBearerTokenDetails", "GOOGLE_PROJECT_ID"));
        sQuery.addSelectColumn(Column.getColumn("GoogleBearerTokenDetails", "REFRESH_TOKEN"));
        sQuery.addSelectColumn(Column.getColumn("GoogleOAuthProjectDetails", "GOOGLE_PROJECT_ID"));
        sQuery.addSelectColumn(Column.getColumn("GoogleOAuthProjectDetails", "CLIENT_ID"));
        sQuery.addSelectColumn(Column.getColumn("GoogleOAuthProjectDetails", "CLIENT_SECRET"));
        sQuery.addSelectColumn(Column.getColumn("GoogleOAuthProjectDetails", "REDIRECT_URI"));
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        return dO;
    }
    
    public static JSONObject persistSettings(final Long customerID, final Long userId, final JSONObject json) throws Exception {
        JSONObject resultJSON = new JSONObject();
        final int serviceType = json.getInt("SERVICE_TYPE");
        try {
            if (serviceType == GoogleForWorkSettings.SERVICE_TYPE_AFW) {
                resultJSON = persistAfWSettings(customerID, userId, json);
            }
            else {
                resultJSON = ChromeManagementHandler.persistChromeIntegSettings(customerID, userId, json);
            }
        }
        catch (final InvalidKeySpecException ex) {
            resultJSON.put("Status", (Object)"Error");
            resultJSON.put("ErrorMessage", (Object)"dc.mdm.android.pfw.enroll.upload_correct_esajson");
            resultJSON.put("ErrorMessagevalue", (Object)I18N.getMsg("dc.mdm.android.pfw.enroll.upload_correct_esajson", new Object[0]));
            GoogleForWorkSettings.logger.log(Level.WARNING, "Exception while saving Google Settings", ex);
            trackAfWConfigFailure(customerID, "InvalidESAJSON");
        }
        catch (final JSONException ex2) {
            resultJSON.put("Status", (Object)"Error");
            resultJSON.put("ErrorMessage", (Object)"dc.mdm.android.pfw.enroll.upload_correct_json");
            resultJSON.put("ErrorMessagevalue", (Object)I18N.getMsg("dc.mdm.android.pfw.enroll.upload_correct_json", new Object[0]));
            GoogleForWorkSettings.logger.log(Level.WARNING, "Exception while saving Google Settings", (Throwable)ex2);
            trackAfWConfigFailure(customerID, "InvalidESAJSON");
        }
        final String status = String.valueOf(resultJSON.get("Status"));
        if (status != null && status.equalsIgnoreCase("Success")) {
            if (serviceType == GoogleForWorkSettings.SERVICE_TYPE_AFW) {
                resultJSON.put("serviceType", (Object)GoogleForWorkSettings.SERVICE_TYPE_AFW);
            }
            else {
                resultJSON.put("serviceType", (Object)GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT);
            }
        }
        return resultJSON;
    }
    
    private static JSONObject persistAfWSettings(final Long customerID, final Long userId, final JSONObject json) throws Exception {
        JSONObject resultJSON = new JSONObject();
        final JSONObject enrollData = new JSONObject();
        final String adminEmail = (String)json.get("ADMIN_ACCOUNT_ID");
        final String domainName = (String)json.get("DOMAIN_NAME");
        final String bindingToken = (String)json.get("BINDING_TOKEN");
        final String jsonFileTempPath = (String)json.get("ESA_CREDENTIAL_JSON");
        final JSONObject credentialJSON = JSONUtil.getInstance().getJSONFromFile(jsonFileTempPath);
        final String esaEmailId = (String)credentialJSON.get("client_email");
        enrollData.put("DomainName", (Object)domainName);
        enrollData.put("ESAId", (Object)esaEmailId);
        enrollData.put("BindingToken", (Object)bindingToken);
        final String privateKeyPem = (String)credentialJSON.get("private_key");
        JSONObject dataJSON = new JSONObject();
        dataJSON.put("Data", (Object)enrollData);
        final JSONObject adminAccountValidation = validateGoogleAdminAccount(esaEmailId, domainName, adminEmail, CertificateUtils.loadPrivateKey(new InputStreamReader(new ByteArrayInputStream(privateKeyPem.getBytes(StandardCharsets.UTF_8)))));
        if (String.valueOf(adminAccountValidation.get("Status")).equals("Success")) {
            final JSONObject responseJSON = MDMApiFactoryProvider.getSecureKeyProviderAPI().enrollESA(dataJSON);
            final String status = String.valueOf(responseJSON.get("Status"));
            if (status != null && status.equalsIgnoreCase("Success")) {
                dataJSON = responseJSON.getJSONObject("Data");
                final String enterpriseId = String.valueOf(dataJSON.get("EnterpriseId"));
                if (validateGooglePlaySettings(jsonFileTempPath, enterpriseId)) {
                    final Long businessstoreId = new AndroidStoreHandler(null, customerID).addOrUpdateManagedStore(domainName, userId);
                    final String jsonPermanentPath = GoogleForWorkSettings.ESA_CREDENTIAL_JSON_FILE_PATH + customerID + File.separator + businessstoreId + File.separator + "credential.json";
                    ApiFactoryProvider.getFileAccessAPI().copyFile(jsonFileTempPath, jsonPermanentPath);
                    final JSONObject esaDataJSON = new JSONObject();
                    esaDataJSON.put("BUSINESSSTORE_ID", (Object)businessstoreId);
                    esaDataJSON.put("ESA_EMAIL_ID", (Object)esaEmailId);
                    esaDataJSON.put("ENTERPRISE_ID", (Object)enterpriseId);
                    esaDataJSON.put("ESA_CREDENTIAL_JSON_PATH", (Object)jsonPermanentPath);
                    esaDataJSON.put("DOMAIN_ADMIN_EMAIL_ID", (Object)adminEmail);
                    esaDataJSON.put("MANAGED_DOMAIN_NAME", (Object)domainName);
                    esaDataJSON.put("ENTERPRISE_TYPE", (Object)GoogleForWorkSettings.ENTERPRISE_TYPE_GOOGLE);
                    esaDataJSON.put("OAUTH_TYPE", (Object)GoogleForWorkSettings.OAUTH_TYPE_ESA);
                    addOrUpdateGoogleESADetails(esaDataJSON);
                    GoogleForWorkSettings.logger.log(Level.WARNING, "Google For Work successfully configured:{0} for customerId {1}", new Object[] { esaDataJSON, customerID });
                    resultJSON.put("Status", (Object)"Success");
                }
                else {
                    resultJSON.put("Status", (Object)"Error");
                    resultJSON.put("ErrorMessage", (Object)"Provided JSON file is not valid");
                    trackAfWConfigFailure(customerID, "InvalidESAJSON");
                }
            }
            else {
                dataJSON = responseJSON.getJSONObject("Data");
                resultJSON.put("Status", (Object)"Error");
                final String errorMsg = String.valueOf(dataJSON.get("ErrorMsg"));
                resultJSON = setTextForErrorMsg(resultJSON, errorMsg);
                GoogleForWorkSettings.logger.log(Level.INFO, "Wrong AfW settings:{0}", errorMsg);
                trackAfWConfigFailure(customerID, "IncorrectToken");
            }
        }
        else {
            resultJSON.put("Status", (Object)"Error");
            resultJSON.put("ErrorMessage", (Object)String.valueOf(adminAccountValidation.get("ErrorMessage")));
            trackAfWConfigFailure(customerID, resultJSON.optString("Reason", "UnknownError"));
        }
        return resultJSON;
    }
    
    public static void resetSettings(final Long customerId, final int serviceType) throws Exception {
        final JSONObject googleDetails = getGoogleForWorkSettings(customerId, serviceType);
        if (serviceType == GoogleForWorkSettings.SERVICE_TYPE_AFW) {
            resetAfWSettings(googleDetails);
        }
        else {
            resetChromeIntegSettings(googleDetails);
        }
        final DeleteQuery dQuery = (DeleteQuery)new DeleteQueryImpl("MDMResource");
        final Join resJoin = new Join("MDMResource", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join easDetails = new Join("MDMResource", "GoogleESADetails", new String[] { "RESOURCE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2);
        Join serviceSpcJoin;
        if (serviceType == GoogleForWorkSettings.SERVICE_TYPE_AFW) {
            serviceSpcJoin = new Join("MDMResource", "ManagedBusinessStore", new String[] { "RESOURCE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2);
        }
        else {
            serviceSpcJoin = new Join("MDMResource", "ChromeMgmtIntegration", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        }
        final Criteria customerIdCrietria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        dQuery.addJoin(easDetails);
        dQuery.addJoin(resJoin);
        dQuery.addJoin(serviceSpcJoin);
        dQuery.setCriteria(customerIdCrietria);
        DataAccess.delete(dQuery);
        Logger.getLogger("MDMLogger").log(Level.WARNING, "Google settings unbound for customer Id {0} for serviceType {1}", new Object[] { customerId, serviceType });
    }
    
    public static void addOrUpdateGoogleESADetails(final JSONObject data) throws JSONException, DataAccessException {
        final String domainName = String.valueOf(data.get("MANAGED_DOMAIN_NAME"));
        final String enterpriseId = String.valueOf(data.get("ENTERPRISE_ID"));
        final String esaId = String.valueOf(data.get("ESA_EMAIL_ID"));
        final String esaCertPath = String.valueOf(data.get("ESA_CREDENTIAL_JSON_PATH"));
        final String adminEmailId = String.valueOf(data.get("DOMAIN_ADMIN_EMAIL_ID"));
        final Long businessstore_id = data.getLong("BUSINESSSTORE_ID");
        final Integer enterpriseType = data.getInt("ENTERPRISE_TYPE");
        final Integer oauthType = data.getInt("OAUTH_TYPE");
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("GoogleESADetails"));
        final Criteria customerCriteria = new Criteria(new Column("GoogleESADetails", "BUSINESSSTORE_ID"), (Object)businessstore_id, 0);
        sQuery.setCriteria(customerCriteria);
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "BUSINESSSTORE_ID"));
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "DOMAIN_ADMIN_EMAIL_ID"));
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "ENTERPRISE_ID"));
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "ESA_CREDENTIAL_JSON_PATH"));
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "ESA_EMAIL_ID"));
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "MANAGED_DOMAIN_NAME"));
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "ENTERPRISE_TYPE"));
        sQuery.addSelectColumn(Column.getColumn("GoogleESADetails", "OAUTH_TYPE"));
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        if (dO.isEmpty()) {
            final Row row = new Row("GoogleESADetails");
            row.set("BUSINESSSTORE_ID", (Object)businessstore_id);
            row.set("DOMAIN_ADMIN_EMAIL_ID", (Object)adminEmailId);
            row.set("ENTERPRISE_ID", (Object)enterpriseId);
            row.set("ESA_CREDENTIAL_JSON_PATH", (Object)esaCertPath);
            row.set("ESA_EMAIL_ID", (Object)esaId);
            row.set("MANAGED_DOMAIN_NAME", (Object)domainName);
            row.set("ENTERPRISE_TYPE", (Object)enterpriseType);
            row.set("OAUTH_TYPE", (Object)oauthType);
            dO.addRow(row);
        }
        else {
            final Row row = dO.getFirstRow("GoogleESADetails");
            row.set("BUSINESSSTORE_ID", (Object)businessstore_id);
            row.set("DOMAIN_ADMIN_EMAIL_ID", (Object)adminEmailId);
            row.set("ENTERPRISE_ID", (Object)enterpriseId);
            row.set("ESA_CREDENTIAL_JSON_PATH", (Object)esaCertPath);
            row.set("ESA_EMAIL_ID", (Object)esaId);
            row.set("MANAGED_DOMAIN_NAME", (Object)domainName);
            row.set("ENTERPRISE_TYPE", (Object)enterpriseType);
            row.set("OAUTH_TYPE", (Object)oauthType);
            dO.updateRow(row);
        }
        MDMUtil.getPersistence().update(dO);
        if (oauthType == GoogleForWorkSettings.OAUTH_TYPE_BEARER_TOKEN) {
            addOrUpdateGoogleBearerToken(businessstore_id, data);
        }
    }
    
    private static void addOrUpdateGoogleBearerToken(final Long googleEsaId, final JSONObject data) throws JSONException, DataAccessException {
        final String refreshToken = String.valueOf(data.get("REFRESH_TOKEN"));
        final Long googleOauthProjectId = addOrUpdateGoogleProjectDetails(data);
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("GoogleBearerTokenDetails"));
        final Criteria googleESADetailsCriteria = new Criteria(new Column("GoogleBearerTokenDetails", "GOOGLE_AUTH_ID"), (Object)googleEsaId, 0);
        sQuery.setCriteria(googleESADetailsCriteria);
        sQuery.addSelectColumn(Column.getColumn("GoogleBearerTokenDetails", "GOOGLE_PROJECT_ID"));
        sQuery.addSelectColumn(Column.getColumn("GoogleBearerTokenDetails", "GOOGLE_AUTH_ID"));
        sQuery.addSelectColumn(Column.getColumn("GoogleBearerTokenDetails", "REFRESH_TOKEN"));
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        if (dO.isEmpty()) {
            final Row row = new Row("GoogleBearerTokenDetails");
            row.set("GOOGLE_PROJECT_ID", (Object)googleOauthProjectId);
            row.set("GOOGLE_AUTH_ID", (Object)googleEsaId);
            row.set("REFRESH_TOKEN", (Object)refreshToken);
            dO.addRow(row);
        }
        else {
            final Row row = dO.getFirstRow("GoogleBearerTokenDetails");
            row.set("GOOGLE_PROJECT_ID", (Object)googleOauthProjectId);
            row.set("GOOGLE_AUTH_ID", (Object)googleEsaId);
            row.set("REFRESH_TOKEN", (Object)refreshToken);
            dO.updateRow(row);
        }
        MDMUtil.getPersistence().update(dO);
    }
    
    private static Long addOrUpdateGoogleProjectDetails(final JSONObject data) throws JSONException, DataAccessException {
        final String clientId = String.valueOf(data.get("CLIENT_ID"));
        final String clientSecret = String.valueOf(data.get("CLIENT_SECRET"));
        final String redirectURI = String.valueOf(data.get("REDIRECT_URI"));
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("GoogleOAuthProjectDetails"));
        final Criteria clientIdCriteria = new Criteria(new Column("GoogleOAuthProjectDetails", "CLIENT_ID"), (Object)clientId, 0);
        final Criteria clientSecretCriteria = new Criteria(new Column("GoogleOAuthProjectDetails", "CLIENT_SECRET"), (Object)clientSecret, 0);
        final Criteria redirectUriCriteria = new Criteria(new Column("GoogleOAuthProjectDetails", "REDIRECT_URI"), (Object)redirectURI, 0);
        sQuery.setCriteria(clientIdCriteria.and(clientSecretCriteria).and(redirectUriCriteria));
        sQuery.addSelectColumn(Column.getColumn("GoogleOAuthProjectDetails", "GOOGLE_PROJECT_ID"));
        sQuery.addSelectColumn(Column.getColumn("GoogleOAuthProjectDetails", "CLIENT_ID"));
        sQuery.addSelectColumn(Column.getColumn("GoogleOAuthProjectDetails", "CLIENT_SECRET"));
        sQuery.addSelectColumn(Column.getColumn("GoogleOAuthProjectDetails", "REDIRECT_URI"));
        DataObject dO = MDMUtil.getPersistence().get(sQuery);
        if (dO.isEmpty()) {
            final Row row = new Row("GoogleOAuthProjectDetails");
            row.set("CLIENT_ID", (Object)clientId);
            row.set("CLIENT_SECRET", (Object)clientSecret);
            row.set("REDIRECT_URI", (Object)redirectURI);
            dO.addRow(row);
        }
        else {
            final Row row = dO.getFirstRow("GoogleOAuthProjectDetails");
            row.set("CLIENT_ID", (Object)clientId);
            row.set("CLIENT_SECRET", (Object)clientSecret);
            row.set("REDIRECT_URI", (Object)redirectURI);
            dO.updateRow(row);
        }
        dO = MDMUtil.getPersistence().update(dO);
        return (Long)dO.getFirstValue("GoogleOAuthProjectDetails", "GOOGLE_PROJECT_ID");
    }
    
    private static boolean validateGooglePlaySettings(final String credentialPath, final String enterpriseId) {
        try {
            final InputStream inStream = ApiFactoryProvider.getFileAccessAPI().readFile(credentialPath);
            final Credential credential = (Credential)GoogleCredential.fromStream(inStream, GoogleAPINetworkManager.getGoogleAPINetworkManager().getHttpTransportWithProxyConfigured(), (JsonFactory)JacksonFactory.getDefaultInstance()).createScoped((Collection)Collections.singleton("https://www.googleapis.com/auth/androidenterprise"));
            final AndroidEnterprise androidEnterprise = new AndroidEnterprise.Builder(GoogleAPINetworkManager.getGoogleAPINetworkManager().getHttpTransportWithProxyConfigured(), (JsonFactory)JacksonFactory.getDefaultInstance(), (HttpRequestInitializer)credential).setApplicationName("Validate User Input").build();
            Logger.getLogger(GoogleForWorkSettings.class.getName()).log(Level.INFO, "Going to intitate the validation for Google Enterprise API access {0} {1}", new Object[] { enterpriseId, credential });
            androidEnterprise.enterprises().get(enterpriseId).execute();
            return true;
        }
        catch (final GeneralSecurityException ex) {
            GoogleForWorkSettings.logger.log(Level.SEVERE, null, ex);
        }
        catch (final IOException ex2) {
            GoogleForWorkSettings.logger.log(Level.SEVERE, null, ex2);
        }
        catch (final Exception ex3) {
            GoogleForWorkSettings.logger.log(Level.SEVERE, null, ex3);
        }
        return false;
    }
    
    private static JSONObject validateGoogleAdminAccount(final String esaEMailAddress, final String domainName, final String serviceAccountUser, final PrivateKey privateKey) throws Exception {
        JSONObject resultJSON = new JSONObject();
        try {
            final GoogleCredential credential = new GoogleCredential.Builder().setJsonFactory((JsonFactory)JacksonFactory.getDefaultInstance()).setServiceAccountId(esaEMailAddress).setServiceAccountPrivateKey(privateKey).setTransport(GoogleAPINetworkManager.getGoogleAPINetworkManager().getHttpTransportWithProxyConfigured()).setServiceAccountScopes((Collection)Arrays.asList("https://www.googleapis.com/auth/admin.directory.user")).setServiceAccountUser(serviceAccountUser).build();
            final Directory directory = new Directory.Builder(GoogleAPINetworkManager.getGoogleAPINetworkManager().getHttpTransportWithProxyConfigured(), (JsonFactory)JacksonFactory.getDefaultInstance(), (HttpRequestInitializer)credential).setApplicationName("Validate User Input").build();
            final Directory.Users.List list = directory.users().list();
            list.setDomain(domainName);
            list.setMaxResults(Integer.valueOf(10));
            list.execute();
            resultJSON.put("Status", (Object)"Success");
        }
        catch (final GoogleJsonResponseException ex) {
            resultJSON.put("Status", (Object)"Error");
            final GoogleJsonError details = ex.getDetails();
            resultJSON = setTextForErrorMsg(resultJSON, details.getMessage());
            Logger.getLogger("MDMLogger").log(Level.WARNING, "Exception while saving Google Settings", (Throwable)ex);
        }
        catch (final UnknownHostException ex2) {
            resultJSON.put("Status", (Object)"Error");
            resultJSON.put("ErrorMessage", (Object)MDMI18N.getMsg("mdm.afw.check_connectivity", new Object[] { ex2.getMessage() }));
            resultJSON.put("Reason", (Object)"CannotReachReqDomain");
            Logger.getLogger("MDMLogger").log(Level.WARNING, "Exception while saving Google Settings", ex2);
        }
        catch (final TokenResponseException ex3) {
            resultJSON.put("Status", (Object)"Error");
            resultJSON = setTextForErrorMsg(resultJSON, ex3.getDetails().getErrorDescription());
            Logger.getLogger("MDMLogger").log(Level.WARNING, "Exception while saving Google Settings", (Throwable)ex3);
        }
        catch (final Exception ex4) {
            resultJSON.put("Status", (Object)"Error");
            resultJSON.put("ErrorMessage", (Object)"Unknown error! Contact support.");
            resultJSON.put("Reason", (Object)"InternalError");
            Logger.getLogger("MDMLogger").log(Level.WARNING, "Exception while saving Google Settings", ex4);
        }
        return resultJSON;
    }
    
    public static PrivateKey getPrivateKeyFromCredentialJSONFile(final String credentialJSONFilePath) throws Exception {
        final JSONObject credentialJSON = JSONUtil.getInstance().getJSONFromFile(credentialJSONFilePath);
        return CertificateUtils.loadPrivateKey(new InputStreamReader(new ByteArrayInputStream(String.valueOf(credentialJSON.get("private_key")).getBytes(StandardCharsets.UTF_8))));
    }
    
    private static JSONObject setTextForErrorMsg(final JSONObject resultJSON, final String errorMsg) {
        String errorText = errorMsg;
        String reason = errorMsg;
        try {
            if (errorMsg.contains("Check the Parameters")) {
                errorText = "mdm.afw.incorrect_token";
                reason = "IncorrectToken";
            }
            else if (errorMsg.contains("Not Authorized to access")) {
                errorText = "dc.mdm.android.pfw.enroll.invalid_adminid";
                reason = "NotAnAdmin";
            }
            else if (errorMsg.contains("Invalid email")) {
                errorText = "mdm.afw.verify_email";
                reason = "InvalidEmail";
            }
            resultJSON.put("ErrorMessage", (Object)errorText);
            resultJSON.put("Reason", (Object)reason);
        }
        catch (final Exception ex) {
            GoogleForWorkSettings.logger.log(Level.SEVERE, "Excpetion in I18N parsing:", ex);
        }
        return resultJSON;
    }
    
    private static void trackAfWConfigFailure(final Long customerID, final String reason) {
        try {
            final MEMDMTrackParamManager meTracker = MEMDMTrackParamManager.getInstance();
            meTracker.incrementTrackValue(customerID, "Android_Module", "afwConfigAttempts");
            meTracker.addOrUpdateTrackParam(customerID, "Android_Module", "afwLastFailureReason", reason);
        }
        catch (final JSONException ex) {
            Logger.getLogger(GoogleForWorkSettings.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
    }
    
    public static Boolean isGoogleSettingsConfigured(final JSONObject googleSettings, final Integer serviceType) {
        try {
            return getSettingsForService(googleSettings, serviceType).getBoolean("isConfigured");
        }
        catch (final JSONException ex) {
            Logger.getLogger(GoogleForWorkSettings.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            throw new RuntimeException("Given JSON does not contains details for contain requested service type");
        }
    }
    
    public static JSONObject getGSuiteIntegDetails(final JSONObject googleSettings) {
        try {
            if (isGoogleSettingsConfigured(googleSettings, GoogleForWorkSettings.SERVICE_TYPE_AFW)) {
                final int type = googleSettings.getJSONObject(GoogleForWorkSettings.SERVICE_TYPE_AFW.toString()).getInt("ENTERPRISE_TYPE");
                if (type == GoogleForWorkSettings.ENTERPRISE_TYPE_GOOGLE) {
                    return createGSuiteIntegDetails(googleSettings.getJSONObject(GoogleForWorkSettings.SERVICE_TYPE_AFW.toString()));
                }
            }
            if (isGoogleSettingsConfigured(googleSettings, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT)) {
                return createGSuiteIntegDetails(googleSettings.getJSONObject(GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT.toString()));
            }
        }
        catch (final JSONException ex) {
            Logger.getLogger(GoogleForWorkSettings.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        return null;
    }
    
    private static JSONObject createGSuiteIntegDetails(final JSONObject googleSettings) throws JSONException {
        final JSONObject json = new JSONObject();
        json.put("DomainName", googleSettings.get("MANAGED_DOMAIN_NAME"));
        json.put("DomainAdminAccount", googleSettings.get("DOMAIN_ADMIN_EMAIL_ID"));
        json.put("ServiceAccountId", googleSettings.get("ESA_EMAIL_ID"));
        json.put("EnterpriseID", googleSettings.get("ENTERPRISE_ID"));
        json.put("ESA_CREDENTIAL_JSON", (Object)"");
        return json;
    }
    
    public static JSONObject getSettingsForService(final JSONObject googleSettings, final Integer serviceType) throws JSONException {
        return googleSettings.getJSONObject(serviceType.toString());
    }
    
    private static void resetAfWSettings(final JSONObject googleESADetails) throws Exception {
        final Long customerId = googleESADetails.getLong("CUSTOMER_ID");
        new StoreLayoutManager().deleteStoreLayout(googleESADetails.getLong("BUSINESSSTORE_ID"));
        new AFWAccountStatusHandler().resetAllAfWAccountStatus(customerId);
        new GooglePlayBusinessAppHandler().unApproveAllPortalApps(customerId);
    }
    
    private static void resetChromeIntegSettings(final JSONObject googleDetails) throws Exception {
        ChromeManagementHandler.resetAllSettings(googleDetails);
        ManagedDeviceHandler.getInstance().unmanageAllDevices((Long)googleDetails.get("CUSTOMER_ID"), 4);
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("DeviceForEnrollment");
        deleteQuery.addJoin(new Join("DeviceForEnrollment", "GSChromeDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
        MDMUtil.getPersistenceLite().delete(deleteQuery);
    }
    
    public static JSONObject setUpGoogleForWork(final Long customerID, final JSONObject formData) throws Exception {
        JSONObject resultJSON = new JSONObject();
        final int serviceType = formData.getInt("SERVICE_TYPE");
        try {
            resultJSON = persistSettings(customerID, ApiFactoryProvider.getAuthUtilAccessAPI().getUserID(), formData);
            final String status = resultJSON.optString("Status");
            if (!MDMStringUtils.isEmpty(status) && !status.toLowerCase().contains("error")) {
                if (serviceType == GoogleForWorkSettings.SERVICE_TYPE_AFW) {
                    new GooglePlayBusinessAppHandler().syncGooglePlay(customerID, 2, 3);
                }
                else {
                    ChromeManagementHandler.syncChromeDevices(customerID);
                }
            }
        }
        catch (final Exception ex) {
            GoogleForWorkSettings.logger.log(Level.WARNING, "Exception while configuring Google Play Settings", ex);
            resultJSON.put("Status", (Object)"Error");
            resultJSON.put("ErrorMessage", (Object)"Unknown Error! Contact Support with Logs");
        }
        finally {
            if (serviceType == GoogleForWorkSettings.SERVICE_TYPE_AFW) {
                ApiFactoryProvider.getFileAccessAPI().deleteFile(String.valueOf(formData.get("ESA_CREDENTIAL_JSON")));
            }
        }
        return resultJSON;
    }
    
    static {
        ESA_CREDENTIAL_JSON_FILE_PATH = DCMetaDataUtil.getInstance().getClientDataParentDir() + "\\mdm\\android\\afw\\";
        ENTERPRISE_TYPE_GOOGLE = 1;
        ENTERPRISE_TYPE_EMM = 2;
        SERVICE_TYPE_AFW = 1;
        SERVICE_TYPE_CHROME_MGMT = 2;
        OAUTH_TYPE_ESA = 1;
        OAUTH_TYPE_BEARER_TOKEN = 2;
        GoogleForWorkSettings.logger = Logger.getLogger("MDMLogger");
    }
}
