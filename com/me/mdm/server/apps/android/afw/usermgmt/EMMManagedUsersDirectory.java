package com.me.mdm.server.apps.android.afw.usermgmt;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.googleapis.json.GoogleJsonError;
import org.json.JSONException;
import java.util.Arrays;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.io.IOException;
import com.google.api.services.androidenterprise.model.AuthenticationToken;
import com.google.api.client.googleapis.batch.BatchRequest;
import org.json.JSONArray;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.services.androidenterprise.model.User;
import java.util.logging.Level;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpRequestInitializer;
import com.me.mdm.server.apps.android.afw.GoogleAPINetworkManager;
import org.json.JSONObject;
import com.google.api.services.androidenterprise.AndroidEnterprise;
import java.util.List;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import java.util.logging.Logger;
import com.me.mdm.server.apps.businessstore.UserDirectory;

public class EMMManagedUsersDirectory implements UserDirectory
{
    private static final String APPLICATION_NAME = "ManageEngine EMM/1.0";
    private String enterpriseID;
    private Logger logger;
    private static final JsonFactory JSON_FACTORY;
    HttpTransport http_Transport;
    private static final List<String> ANDROID_ENTERPRISE_SCOPES;
    AndroidEnterprise androidEnterprise;
    JSONObject responseJSON;
    JSONObject successJSON;
    JSONObject failureJSON;
    
    public EMMManagedUsersDirectory() {
        this.enterpriseID = null;
        this.logger = Logger.getLogger("MDMLogger");
        this.http_Transport = null;
        this.androidEnterprise = null;
        this.responseJSON = null;
        this.successJSON = null;
        this.failureJSON = null;
    }
    
    @Deprecated
    public void initialize(final JSONObject jsonObject) throws Exception {
        this.http_Transport = GoogleAPINetworkManager.getGoogleAPINetworkManager().getHttpTransportWithProxyConfigured();
        this.enterpriseID = jsonObject.optString("ENTERPRISE_ID");
        final String accessToken = jsonObject.optString("accessToken");
        final GoogleCredential credential = this.getGoogleCredential(jsonObject);
        if (accessToken != null && !accessToken.isEmpty()) {
            credential.setAccessToken(accessToken);
        }
        this.androidEnterprise = new AndroidEnterprise.Builder(this.http_Transport, EMMManagedUsersDirectory.JSON_FACTORY, (HttpRequestInitializer)credential).setApplicationName("ManageEngine EMM/1.0").build();
    }
    
    private GoogleCredential getGoogleCredential(final JSONObject jsonObject) throws Exception {
        final String credentialFilePath = String.valueOf(jsonObject.get("ESA_CREDENTIAL_JSON_PATH"));
        final InputStream inStream = ApiFactoryProvider.getFileAccessAPI().getInputStream(credentialFilePath);
        final GoogleCredential credential = GoogleCredential.fromStream(inStream, this.http_Transport, EMMManagedUsersDirectory.JSON_FACTORY).createScoped((Collection)Collections.singleton("https://www.googleapis.com/auth/androidenterprise"));
        return credential;
    }
    
    @Override
    public JSONObject getUsers(final JSONObject jsonObject) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public JSONObject addUser(final JSONObject jsonObject) {
        final JSONObject userReponseJSON = new JSONObject();
        try {
            this.successJSON = new JSONObject();
            this.failureJSON = new JSONObject();
            final JSONArray users = jsonObject.getJSONArray("users");
            this.logger.log(Level.INFO, "Bussiness store user insert starts- {0}", users);
            BatchRequest batchRequest = this.androidEnterprise.batch();
            int batchCount = 1;
            for (int i = 0; i < users.length(); ++i) {
                final JSONObject userJSON = users.getJSONObject(i);
                final User user = new User().setAccountIdentifier(String.valueOf(userJSON.get("UserIdentifier"))).setAccountType("deviceAccount").setManagementType("emmManaged");
                this.androidEnterprise.users().insert(this.enterpriseID, user).queue(batchRequest, (JsonBatchCallback)new InsertUsersCallBack(String.valueOf(userJSON.get("UserIdentifier"))));
                this.logger.log(Level.INFO, "Going to insert bussiness store user with userid {0}", userJSON.optString("UserIdentifier"));
                if (batchRequest.size() == 40) {
                    this.logger.log(Level.INFO, "insert users by batch of 40-Batch {0}", batchCount);
                    batchRequest.execute();
                    batchRequest = this.androidEnterprise.batch();
                    Thread.sleep(3000L);
                    ++batchCount;
                }
            }
            if (batchRequest.size() > 0) {
                this.logger.log(Level.INFO, "insert users from rest of the batch {0}-Batch {1}", new Object[] { batchRequest.size(), batchCount });
                batchRequest.execute();
            }
            userReponseJSON.put("SuccessList", (Object)this.successJSON);
            userReponseJSON.put("FailureList", (Object)this.failureJSON);
            this.logger.log(Level.INFO, "Insert bussiness store user completed");
        }
        catch (final Exception ex) {
            Logger.getLogger(EMMManagedUsersDirectory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userReponseJSON;
    }
    
    @Override
    public JSONObject updateUser(final JSONObject jsonObject) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public JSONObject deleteUser(final JSONObject jsonObject) {
        try {
            final JSONArray users = jsonObject.getJSONArray("users");
            this.logger.log(Level.INFO, "Bussiness store users delete starts- {0}", users);
            this.responseJSON = new JSONObject();
            BatchRequest batchRequest = this.androidEnterprise.batch();
            int batchCount = 1;
            for (int i = 0; i < users.length(); ++i) {
                final JSONObject userJSON = users.getJSONObject(i);
                this.androidEnterprise.users().delete(this.enterpriseID, String.valueOf(userJSON.get("BS_STORE_ID"))).queue(batchRequest, (JsonBatchCallback)new DeleteUsersCallBack(String.valueOf(userJSON.get("BS_STORE_ID"))));
                this.logger.log(Level.INFO, "Going to delete bussiness store user with userid {0}", String.valueOf(userJSON.get("BS_STORE_ID")));
                if (batchRequest.size() == 40) {
                    this.logger.log(Level.INFO, "deleteUser by batch of 40-Batch {0}", batchCount);
                    batchRequest.execute();
                    batchRequest = this.androidEnterprise.batch();
                    Thread.sleep(3000L);
                    ++batchCount;
                }
            }
            if (batchRequest.size() > 0) {
                this.logger.log(Level.INFO, "deleteUser from rest of the batch {0}-Batch {1}", new Object[] { batchRequest.size(), batchCount });
                batchRequest.execute();
            }
            this.logger.log(Level.INFO, "Delete bussiness store user completed");
            return this.responseJSON;
        }
        catch (final Exception ex) {
            Logger.getLogger(EMMManagedUsersDirectory.class.getName()).log(Level.SEVERE, null, ex);
            return this.responseJSON;
        }
    }
    
    public String getUserAccountAuthToken(final String userId) throws IOException {
        final AuthenticationToken token = (AuthenticationToken)this.androidEnterprise.users().generateAuthenticationToken(this.enterpriseID, userId).execute();
        return token.getToken();
    }
    
    static {
        JSON_FACTORY = (JsonFactory)JacksonFactory.getDefaultInstance();
        ANDROID_ENTERPRISE_SCOPES = Arrays.asList("https://www.googleapis.com/auth/androidenterprise");
    }
    
    private class InsertUsersCallBack extends JsonBatchCallback<User>
    {
        String mdmUserId;
        
        public InsertUsersCallBack(final String userIdentifier) throws JSONException {
            this.mdmUserId = null;
            this.mdmUserId = userIdentifier;
        }
        
        public void onFailure(final GoogleJsonError gje, final HttpHeaders hh) throws IOException {
            String error = "";
            try {
                EMMManagedUsersDirectory.this.failureJSON.put(this.mdmUserId, (Object)gje.toPrettyString());
                error = gje.toPrettyString();
            }
            catch (final JSONException ex) {
                EMMManagedUsersDirectory.this.logger.log(Level.SEVERE, (Throwable)ex, () -> "Failed to insert user in store:" + googleJsonError.toString() + " mdmUserId " + this.mdmUserId);
            }
            EMMManagedUsersDirectory.this.logger.log(Level.SEVERE, "Failed to insert user in store:{0} mdmUserId {1}", new Object[] { error, this.mdmUserId });
        }
        
        public void onSuccess(final User user, final HttpHeaders hh) throws IOException {
            try {
                final String storeUserId = user.getId();
                final JSONObject userDetails = new JSONObject();
                userDetails.put("BS_STORE_ID", (Object)storeUserId);
                userDetails.put("BS_MDM_ID", (Object)this.mdmUserId);
                EMMManagedUsersDirectory.this.successJSON.put(this.mdmUserId, (Object)userDetails);
                EMMManagedUsersDirectory.this.logger.log(Level.INFO, "Business store user inserted {0}", userDetails);
            }
            catch (final JSONException exp) {
                EMMManagedUsersDirectory.this.logger.log(Level.SEVERE, "Exception in fetching inserted business store user details :", (Throwable)exp);
            }
        }
    }
    
    private class DeleteUsersCallBack extends JsonBatchCallback<Void>
    {
        String storeUserId;
        
        public DeleteUsersCallBack(final String storeUserId) throws JSONException {
            this.storeUserId = null;
            this.storeUserId = storeUserId;
        }
        
        public void onFailure(final GoogleJsonError gje, final HttpHeaders hh) throws IOException {
            EMMManagedUsersDirectory.this.logger.log(Level.INFO, "Failed to delete user in store:{0}", gje.toPrettyString());
        }
        
        public void onSuccess(final Void t, final HttpHeaders hh) throws IOException {
            try {
                final JSONObject userDetails = new JSONObject();
                userDetails.put("BS_STORE_ID", (Object)this.storeUserId);
                EMMManagedUsersDirectory.this.responseJSON.put(this.storeUserId, (Object)userDetails);
                EMMManagedUsersDirectory.this.logger.log(Level.INFO, "Business store user deleted '{'0'}'{0}", EMMManagedUsersDirectory.this.responseJSON);
            }
            catch (final Exception exp) {
                EMMManagedUsersDirectory.this.logger.log(Level.INFO, "Failed to insert user in store:{0}", exp);
            }
        }
    }
}
