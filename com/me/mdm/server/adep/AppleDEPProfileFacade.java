package com.me.mdm.server.adep;

import java.util.HashMap;
import com.me.mdm.server.resource.MDMResourceDataProvider;
import org.json.JSONArray;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import com.me.mdm.server.adep.mac.AccountConfiguration;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.encryption.MDMPKCS12CertificateHandler;
import com.me.mdm.server.certificate.api.util.SupervisionIdentityUtil;
import java.util.logging.Level;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AppleDEPProfileFacade
{
    private static AppleDEPProfileFacade facadeObj;
    public static Logger logger;
    
    public static AppleDEPProfileFacade getInstance() {
        if (AppleDEPProfileFacade.facadeObj == null) {
            AppleDEPProfileFacade.facadeObj = new AppleDEPProfileFacade();
        }
        return AppleDEPProfileFacade.facadeObj;
    }
    
    public JSONObject getDEPProfile(final JSONObject profileDetailsApiJson) throws Exception {
        try {
            final Long tokenId = Long.valueOf(String.valueOf(profileDetailsApiJson.getJSONObject("msg_header").getJSONObject("resource_identifier").get("appledepserver_id")));
            final Long customerId = APIUtil.getCustomerID(profileDetailsApiJson);
            ABMAuthTokenFacade.validateIfDepTokenExists(tokenId, customerId);
            final AppleDEPProfileHandler profileHandler = AppleDEPProfileHandler.getInstance(tokenId, customerId);
            JSONObject profileDetails = profileHandler.getDEPProfileDetails();
            profileDetails = this.parseResponseDataForDEPProfile(profileDetails);
            return profileDetails;
        }
        catch (final Exception ex) {
            AppleDEPProfileFacade.logger.log(Level.SEVERE, "Exception while get ABM Enrollment Profile..", ex);
            throw ex;
        }
    }
    
    public JSONObject createOrModifyDEPProfile(final JSONObject profileDetailsApiJson) throws Exception {
        final Long tokenId = Long.valueOf(String.valueOf(profileDetailsApiJson.getJSONObject("msg_header").getJSONObject("resource_identifier").get("appledepserver_id")));
        final Long customerId = APIUtil.getCustomerID(profileDetailsApiJson);
        ABMAuthTokenFacade.validateIfDepTokenExists(tokenId, customerId);
        final JSONObject response = new JSONObject();
        response.put("server_id", (Object)tokenId);
        response.put("server_name", (Object)DEPEnrollmentUtil.getDEPServerName(tokenId));
        try {
            AppleDEPProfileFacade.logger.log(Level.INFO, "Going to create or modify DEP profile.. token id - ", tokenId);
            final JSONObject depJson = this.parseRequestDataForDEPProfile(profileDetailsApiJson);
            final AppleDEPProfileHandler profileHandler = AppleDEPProfileHandler.getInstance(tokenId, customerId);
            depJson.put("SERVER_NAME", response.get("server_name"));
            final Long certificateID = SupervisionIdentityUtil.getInstance().getSupervisionCertificateId(customerId);
            if (certificateID == null) {
                MDMPKCS12CertificateHandler.getInstance().createSupervisionIdentityCertificate(customerId);
            }
            profileHandler.createProfile(depJson);
            response.put("success", true);
            return response;
        }
        catch (final SyMException ex) {
            AppleDEPProfileFacade.logger.log(Level.SEVERE, "Exception while create ABM Enrollment Profile.. Sym exp..", (Throwable)ex);
            response.put("success", false);
            final JSONObject errorDetails = ADEPServerSyncHandler.getInstance(tokenId, customerId).getErrorDetails();
            if (errorDetails != null) {
                response.put("error", (Object)errorDetails);
            }
            return response;
        }
        catch (final Exception ex2) {
            AppleDEPProfileFacade.logger.log(Level.SEVERE, "Exception while create ABM Enrollment Profile..", ex2);
            throw ex2;
        }
    }
    
    private JSONObject parseRequestDataForDEPProfile(JSONObject profileDetailsJson) throws Exception {
        try {
            final Long tokenId = Long.valueOf(String.valueOf(profileDetailsJson.getJSONObject("msg_header").getJSONObject("resource_identifier").get("appledepserver_id")));
            final Long customerId = APIUtil.getCustomerID(profileDetailsJson);
            final Long userId = APIUtil.getUserID(profileDetailsJson);
            final JSONObject apiJsonBody = profileDetailsJson.has("msg_body") ? profileDetailsJson.optJSONObject("msg_body") : new JSONObject();
            final JSONObject depJson = new JSONObject();
            depJson.put("ADDED_USER", (Object)userId);
            depJson.put("CUSTOMER_ID", (Object)customerId);
            depJson.put("DEP_TOKEN_ID", (Object)tokenId);
            if (apiJsonBody.has("profile")) {
                profileDetailsJson = apiJsonBody.getJSONObject("profile");
                final boolean isSelfEnroll = profileDetailsJson.optBoolean("ENABLE_SELF_ENROLL".toLowerCase());
                if (isSelfEnroll && !MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().isActiveDirectoryOrZohoAccountAuthApplicable(customerId)) {
                    throw new APIHTTPException("ENR0105", new Object[0]);
                }
                depJson.put("ENABLE_SELF_ENROLL", isSelfEnroll);
                depJson.put("ACTIVATION_BY", profileDetailsJson.optInt("activation_by", 1));
                final List<String> skipArray = JSONUtil.getInstance().convertStringJSONArrayTOList(profileDetailsJson.optJSONArray("skip"));
                for (final String eachSetting : skipArray) {
                    depJson.put(AppleDEPProfileHandler.getSkipSettingKey(eachSetting), true);
                }
                depJson.put("AUTO_ADVANCE_TV", profileDetailsJson.optBoolean("AUTO_ADVANCE_TV".toLowerCase()));
                depJson.put("IS_MULTIUSER", profileDetailsJson.optBoolean("IS_MULTIUSER".toLowerCase()));
            }
            if (apiJsonBody.has("is_multiuser")) {
                depJson.put("IS_MULTIUSER", apiJsonBody.getBoolean("is_multiuser"));
                if (apiJsonBody.has("shared_device_config")) {
                    depJson.put("IS_ACCOUNT_CONFIG_ENABLED", true);
                    depJson.put("shared_device_config", (Object)apiJsonBody.optJSONObject("shared_device_config"));
                }
            }
            if (apiJsonBody.has("account_config")) {
                try {
                    final JSONArray temp = apiJsonBody.getJSONArray("account_config");
                    profileDetailsJson = temp.getJSONObject(0);
                    depJson.put("IS_ACCOUNT_CONFIG_ENABLED", true);
                    depJson.put("SHORT_NAME", profileDetailsJson.get("short_name"));
                    depJson.put("FULL_NAME", profileDetailsJson.get("full_name"));
                    depJson.put("HIDDEN", profileDetailsJson.get("is_hidden"));
                    depJson.put("SKIP_ACC_CREATION", profileDetailsJson.getBoolean("skip_account_creation"));
                    depJson.put("SET_REGULAR_ACCOUNT", profileDetailsJson.optBoolean("is_regular_account", false));
                    if (!AccountConfiguration.getInstance().isAccountConfigEnabledForDEP(tokenId) || profileDetailsJson.has("password")) {
                        depJson.put("password", profileDetailsJson.get("password"));
                    }
                }
                catch (final JSONException e) {
                    throw new APIHTTPException("COM0009", new Object[0]);
                }
            }
            if (apiJsonBody.has("group")) {
                final JSONArray groupIds = apiJsonBody.getJSONArray("group");
                if (groupIds.length() > 0) {
                    depJson.put("GROUP_RESOURCE_ID", (Object)groupIds);
                }
            }
            return depJson;
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    private JSONObject parseResponseDataForDEPProfile(final JSONObject profileDetailsJson) throws Exception {
        try {
            final JSONObject responseJson = new JSONObject();
            JSONArray skipArray = new JSONArray();
            final JSONObject profileJson = new JSONObject();
            final JSONArray accConficJA = new JSONArray();
            final JSONObject userJson = new JSONObject();
            final JSONArray groupArray = new JSONArray();
            profileJson.put("PROFILE_UUID", profileDetailsJson.get("PROFILE_UUID"));
            profileJson.put("ENABLE_SELF_ENROLL", profileDetailsJson.get("ENABLE_SELF_ENROLL"));
            profileJson.put("AUTO_ADVANCE_TV", profileDetailsJson.get("AUTO_ADVANCE_TV"));
            profileJson.put("ACTIVATION_BY", profileDetailsJson.getInt("ACTIVATION_BY"));
            responseJson.put("is_multiuser", profileDetailsJson.getBoolean("IS_MULTIUSER"));
            skipArray = AppleDEPProfileHandler.getSkipSettingsArray(profileDetailsJson);
            if (skipArray.length() > 0) {
                profileJson.put("skip", (Object)skipArray);
            }
            userJson.put("id", profileDetailsJson.get("ADDED_USER"));
            userJson.put("name", profileDetailsJson.get("USER_NAME"));
            profileJson.put("added_user", (Object)userJson);
            final Boolean isAwatingConfiguration = (Boolean)profileDetailsJson.get("IS_ACCOUNT_CONFIG_ENABLED");
            if (isAwatingConfiguration && profileDetailsJson.has("SHORT_NAME")) {
                final JSONObject accountConfigJson = new JSONObject();
                accountConfigJson.put("short_name", profileDetailsJson.get("SHORT_NAME"));
                accountConfigJson.put("full_name", profileDetailsJson.get("FULL_NAME"));
                accountConfigJson.put("is_hidden", profileDetailsJson.get("HIDDEN"));
                accountConfigJson.put("skip_account_creation", profileDetailsJson.get("SKIP_ACC_CREATION"));
                accountConfigJson.put("is_regular_account", profileDetailsJson.get("SET_REGULAR_ACCOUNT"));
                accConficJA.put((Object)accountConfigJson);
                responseJson.put("account_config", (Object)accConficJA);
            }
            final Boolean isMultiUser = (Boolean)profileDetailsJson.get("IS_MULTIUSER");
            if (isMultiUser) {
                responseJson.put("shared_device_config", (Object)profileDetailsJson.optJSONObject("shared_device_config"));
                profileDetailsJson.remove("shared_device_config");
            }
            if (profileDetailsJson.has("GROUP_RESOURCE_ID")) {
                final List<Long> groupIdsList = JSONUtil.getInstance().convertLongJSONArrayTOList(profileDetailsJson.getJSONArray("GROUP_RESOURCE_ID"));
                final HashMap groupIdVsNamesMap = MDMResourceDataProvider.getResourceNames(groupIdsList);
                for (final Long eachGrpId : groupIdsList) {
                    final JSONObject tempJson = new JSONObject();
                    tempJson.put("id", (Object)eachGrpId);
                    tempJson.put("name", groupIdVsNamesMap.get(eachGrpId));
                    groupArray.put((Object)tempJson);
                }
            }
            responseJson.put("group", (Object)groupArray);
            responseJson.put("profile", (Object)profileJson);
            return responseJson;
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    static {
        AppleDEPProfileFacade.facadeObj = null;
        AppleDEPProfileFacade.logger = Logger.getLogger("MDMEnrollment");
    }
}
