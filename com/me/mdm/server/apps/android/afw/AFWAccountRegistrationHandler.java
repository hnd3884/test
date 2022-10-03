package com.me.mdm.server.apps.android.afw;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.apps.android.afw.usermgmt.EMMManagedUsersDirectory;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueues;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueUtil;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import com.me.mdm.server.apps.android.afw.appmgmt.GooglePlayBusinessAppHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import com.me.mdm.server.apps.businessstore.android.AndroidStoreHandler;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import java.util.logging.Level;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.net.URLEncoder;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AFWAccountRegistrationHandler extends GoogleForWorkSettings
{
    Logger logger;
    
    public AFWAccountRegistrationHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public String getSignupUrl(final Long customerId, final String callbackURL) throws JSONException, Exception {
        final JSONObject requestData = new JSONObject();
        requestData.put("MsgRequestType", (Object)"GenerateSignupURL");
        JSONObject dataJSON = new JSONObject();
        dataJSON.put("CallbackURL", (Object)URLEncoder.encode(callbackURL, "UTF-8"));
        requestData.put("Data", (Object)dataJSON);
        final JSONObject responseJSON = MDMApiFactoryProvider.getSecureKeyProviderAPI().processAFWRegistrationRequest(requestData);
        this.logger.log(Level.INFO, "Response message from creator : {0}", responseJSON);
        if (responseJSON.get("Status").equals("Success")) {
            dataJSON = responseJSON.getJSONObject("Data");
            final String signupUrl = String.valueOf(dataJSON.get("SignupURL"));
            final String completionToken = String.valueOf(dataJSON.get("CompletionToken"));
            CustomerParamsHandler.getInstance().addOrUpdateParameter("CompletionToken", completionToken, (long)customerId);
            return signupUrl;
        }
        dataJSON = responseJSON.getJSONObject("Data");
        final String errorMsg = String.valueOf(dataJSON.get("ErrorMsg"));
        throw new RuntimeException(errorMsg);
    }
    
    public Long completeSignup(final Long customerId, final Long userId, final String enterpriseToken) throws JSONException, Exception {
        final JSONObject requestData = new JSONObject();
        requestData.put("MsgRequestType", (Object)"CompleteSignup");
        JSONObject dataJSON = new JSONObject();
        dataJSON.put("EnterpriseToken", (Object)enterpriseToken);
        dataJSON.put("CompletionToken", (Object)CustomerParamsHandler.getInstance().getParameterValue("CompletionToken", (long)customerId));
        requestData.put("Data", (Object)dataJSON);
        final JSONObject responseJSON = MDMApiFactoryProvider.getSecureKeyProviderAPI().processAFWRegistrationRequest(requestData);
        this.logger.log(Level.INFO, "Data sent to creator for processAFWRegistrationRequest-CompleteSignup: {0}", responseJSON);
        if (!responseJSON.get("Status").equals("Success")) {
            dataJSON = responseJSON.getJSONObject("Data");
            final String errorMsg = String.valueOf(dataJSON.get("ErrorMsg"));
            throw new RuntimeException(errorMsg);
        }
        dataJSON = responseJSON.getJSONObject("Data");
        final String enterpriseId = String.valueOf(dataJSON.get("EnterpriseId"));
        final String enterpriseName = String.valueOf(dataJSON.get("EnterpriseName"));
        final String adminId = String.valueOf(dataJSON.get("AdminEmail"));
        final JSONObject serviceAccountJSON = this.getServiceAccount(enterpriseId);
        final String esaId = String.valueOf(serviceAccountJSON.get("client_email"));
        if (this.setAccount(enterpriseId, esaId)) {
            final JSONObject businessStoreData = new JSONObject();
            final Long businessstore_id = new AndroidStoreHandler(null, customerId).addOrUpdateManagedStore(enterpriseName, userId);
            final String jsonPermanentPath = GoogleForWorkSettings.ESA_CREDENTIAL_JSON_FILE_PATH + customerId + File.separator + businessstore_id + File.separator + "credential.json";
            ApiFactoryProvider.getFileAccessAPI().writeFile(jsonPermanentPath, serviceAccountJSON.toString().getBytes());
            final JSONObject esaDataJSON = new JSONObject();
            esaDataJSON.put("BUSINESSSTORE_ID", (Object)businessstore_id);
            esaDataJSON.put("ESA_EMAIL_ID", (Object)esaId);
            esaDataJSON.put("ENTERPRISE_ID", (Object)enterpriseId);
            esaDataJSON.put("ESA_CREDENTIAL_JSON_PATH", (Object)jsonPermanentPath);
            esaDataJSON.put("DOMAIN_ADMIN_EMAIL_ID", (Object)adminId);
            esaDataJSON.put("MANAGED_DOMAIN_NAME", (Object)enterpriseName);
            esaDataJSON.put("ENTERPRISE_TYPE", (Object)AFWAccountRegistrationHandler.ENTERPRISE_TYPE_EMM);
            esaDataJSON.put("OAUTH_TYPE", (Object)AFWAccountRegistrationHandler.OAUTH_TYPE_ESA);
            GoogleForWorkSettings.addOrUpdateGoogleESADetails(esaDataJSON);
            this.logger.log(Level.INFO, "AFW without Gsuite configured :: Business store ID : {0}", businessstore_id);
            this.handleAlreadyEnrolledDevices(customerId);
            new GooglePlayBusinessAppHandler().syncGooglePlay(customerId, 2, 3);
            return businessstore_id;
        }
        throw new RuntimeException("Service Account creation fails, Contact support with logs!");
    }
    
    private JSONObject getServiceAccount(final String enterpriseId) throws Exception {
        final JSONObject requestData = new JSONObject();
        requestData.put("MsgRequestType", (Object)"GetServiceAccount");
        JSONObject dataJSON = new JSONObject();
        dataJSON.put("EnterpriseId", (Object)enterpriseId);
        requestData.put("Data", (Object)dataJSON);
        final JSONObject responseJSON = MDMApiFactoryProvider.getSecureKeyProviderAPI().processAFWRegistrationRequest(requestData);
        this.logger.log(Level.INFO, "Response message from creator : {0}", responseJSON);
        if (responseJSON.get("Status").equals("Success")) {
            dataJSON = responseJSON.getJSONObject("Data");
            final String serviceAccount = String.valueOf(dataJSON.get("ServiceAccount"));
            return new JSONObject(serviceAccount);
        }
        dataJSON = responseJSON.getJSONObject("Data");
        final String errorMsg = String.valueOf(dataJSON.get("ErrorMsg"));
        throw new RuntimeException(errorMsg);
    }
    
    private boolean setAccount(final String enterpriseId, final String esaEmailId) throws Exception {
        final JSONObject requestData = new JSONObject();
        requestData.put("MsgRequestType", (Object)"SetAccount");
        JSONObject dataJSON = new JSONObject();
        dataJSON.put("EnterpriseId", (Object)enterpriseId);
        dataJSON.put("ServiceAccountEmail", (Object)esaEmailId);
        requestData.put("Data", (Object)dataJSON);
        final JSONObject responseJSON = MDMApiFactoryProvider.getSecureKeyProviderAPI().processAFWRegistrationRequest(requestData);
        this.logger.log(Level.INFO, "Response message from creator : {0}", responseJSON);
        if (responseJSON.get("Status").equals("Success")) {
            return true;
        }
        dataJSON = responseJSON.getJSONObject("Data");
        final String errorMsg = String.valueOf(dataJSON.get("ErrorMsg"));
        throw new RuntimeException(errorMsg);
    }
    
    private void handleAlreadyEnrolledDevices(final Long customerId) {
        try {
            final JSONObject queueObject = new JSONObject();
            queueObject.put("DeviceType", (Object)"allDevicesOnConfigure");
            final CommonQueueData afwQueueData = new CommonQueueData();
            afwQueueData.setCustomerId(customerId);
            afwQueueData.setTaskName("AddBulkAFWCommandTask");
            afwQueueData.setClassName("com.me.mdm.server.apps.android.afw.AddBulkAFWCommandTask");
            afwQueueData.setJsonQueueData(queueObject);
            CommonQueueUtil.getInstance().addToQueue(afwQueueData, CommonQueues.MDM_APP_MGMT);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while adding data to queue ", e);
        }
    }
    
    public String generateAFWAccountToken(final String deviceUDID, final Long customerId) throws Exception {
        if (GoogleForWorkSettings.isAFWSettingsConfigured(customerId)) {
            final JSONObject playstoreDetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
            final EMMManagedUsersDirectory emmUsersDirectory = new EMMManagedUsersDirectory();
            emmUsersDirectory.initialize(playstoreDetails);
            final Long bsId = playstoreDetails.getLong("BUSINESSSTORE_ID");
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("BusinessStoreUsers"));
            final Criteria storeIdCriteria = new Criteria(Column.getColumn("BusinessStoreUsers", "BUSINESSSTORE_ID"), (Object)bsId, 0);
            final Criteria udidCriteria = new Criteria(Column.getColumn("BusinessStoreUsers", "BS_MDM_ID"), (Object)("udid#" + deviceUDID), 0);
            sQuery.setCriteria(storeIdCriteria.and(udidCriteria));
            sQuery.addSelectColumn(new Column("BusinessStoreUsers", "*"));
            final DataObject dataObject = DataAccess.get(sQuery);
            final String userId = dataObject.getFirstValue("BusinessStoreUsers", "BS_STORE_ID").toString();
            return emmUsersDirectory.getUserAccountAuthToken(userId);
        }
        return "";
    }
}
