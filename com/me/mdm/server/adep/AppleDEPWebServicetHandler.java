package com.me.mdm.server.adep;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.security.cert.X509Certificate;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import com.me.mdm.server.certificate.api.util.SupervisionIdentityUtil;
import org.json.JSONArray;
import com.dd.plist.Base64;
import com.me.mdm.certificate.CertificateHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.UUID;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AppleDEPWebServicetHandler extends AppleDEPHandler
{
    public static Logger logger;
    
    public static AppleDEPWebServicetHandler getInstance(final Long tokenId) {
        return new AppleDEPWebServicetHandler(tokenId);
    }
    
    public static AppleDEPWebServicetHandler getInstance(final Long tokenId, final Long custoemrID) {
        return new AppleDEPWebServicetHandler(tokenId, custoemrID);
    }
    
    private AppleDEPWebServicetHandler(final Long tokenID) {
        super(tokenID);
    }
    
    private AppleDEPWebServicetHandler(final Long tokenId, final Long customerID) {
        super(tokenId, customerID);
    }
    
    public String defineDEPProfile(final JSONObject templateJSON) throws Exception {
        final JSONObject profileJSON = this.createDEPProfileJSON(templateJSON);
        return this.generateProfileJSON(profileJSON);
    }
    
    private JSONObject createDEPProfileJSON(final JSONObject templateJSON) throws Exception {
        final JSONObject profileJSON = new JSONObject();
        profileJSON.put("profile_name", templateJSON.opt("TEMPLATE_NAME"));
        final String sServerBaseURL = MDMApiFactoryProvider.getMDMUtilAPI().getServerURLOnTomcatPortForClientAuthSetup();
        profileJSON.put("url", (Object)(sServerBaseURL + templateJSON.opt("MDM_URL")));
        profileJSON.put("allow_pairing", true);
        profileJSON.put("is_supervised", true);
        profileJSON.put("is_mandatory", true);
        profileJSON.put("is_mdm_removable", false);
        profileJSON.put("await_device_configured", templateJSON.optBoolean("ENABLE_AWAIT_CONFIG", (boolean)Boolean.FALSE));
        profileJSON.put("org_magic", (Object)UUID.randomUUID().toString().toUpperCase());
        if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("DoNotUseDepWebView")) {
            profileJSON.put("configuration_web_url", (Object)(sServerBaseURL + templateJSON.opt("MDM_URL")));
        }
        if (ApiFactoryProvider.getServerSettingsAPI().getCertificateType() != 2) {
            final X509Certificate certificate = CertificateHandler.getInstance().getAppropriateCertificate();
            if (certificate != null) {
                final byte[] encoded = certificate.getEncoded();
                final String encodedBytes = Base64.encodeBytes(encoded);
                final JSONArray jarray = new JSONArray();
                jarray.put((Object)encodedBytes);
                profileJSON.put("anchor_certs", (Object)jarray);
            }
        }
        final Long supervisionIdentityCertID = SupervisionIdentityUtil.getInstance().getSupervisionCertificateId(this.customerId);
        if (supervisionIdentityCertID != null) {
            final X509Certificate supervisionCert = ProfileCertificateUtil.getInstance().getCertificate(this.customerId, supervisionIdentityCertID);
            final JSONArray jarray2 = new JSONArray();
            final byte[] encoded2 = supervisionCert.getEncoded();
            final String encodedBytes2 = Base64.encodeBytes(encoded2);
            jarray2.put((Object)encodedBytes2);
            profileJSON.put("supervising_host_certs", (Object)jarray2);
        }
        final JSONArray skipArray = AppleDEPProfileHandler.getSkipSettingsArray(templateJSON);
        profileJSON.put("skip_setup_items", (Object)skipArray);
        if (templateJSON.getBoolean("IS_MULTIUSER")) {
            profileJSON.put("is_multi_user", templateJSON.getBoolean("IS_MULTIUSER"));
        }
        return profileJSON;
    }
    
    private String generateProfileJSON(final JSONObject profileJSON) throws Exception {
        final AppleDEPServerRequestHandler reqHandler = new AppleDEPServerRequestHandler();
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put("DEPServiceRequestName", (Object)"DefineProfile");
        requestJSON.put("DEPServiceRequestData", (Object)profileJSON);
        requestJSON.put("CustomerId", (Object)this.customerId);
        requestJSON.put("DEP_TOKEN_ID", (Object)this.tokenId);
        final JSONObject responseJSON = reqHandler.processRequestForDEPToken(requestJSON);
        final String status = String.valueOf(responseJSON.get("DEPServiceStatus"));
        if (status.equalsIgnoreCase("Acknowledged")) {
            final JSONObject responseDataJSON = responseJSON.optJSONObject("DEPServiceResponseData");
            final String profileUUID = String.valueOf(responseDataJSON.get("profile_uuid"));
            return profileUUID;
        }
        throw new SyMException(responseJSON.optJSONObject("DEPServiceError").optInt("DEPServerErrorCode"), responseJSON.optJSONObject("DEPServiceError").optString("DEPServerErrorMsg"), (Throwable)null);
    }
    
    public void updateCursor(final String cursor) throws DataAccessException {
        final UpdateQuery u = (UpdateQuery)new UpdateQueryImpl("DEPTokenDetails");
        Criteria c = new Criteria(new Column("DEPTokenDetails", "DEP_TOKEN_ID"), (Object)this.tokenId, 0);
        c = c.and(new Criteria(new Column("DEPTokenDetails", "CUSTOMER_ID"), (Object)this.customerId, 0));
        u.setCriteria(c);
        u.setUpdateColumn("CURSOR", (Object)cursor);
        MDMUtil.getPersistence().update(u);
    }
    
    public JSONObject getDeviceJSON(final String cursor, final boolean fetch) throws Exception {
        final AppleDEPServerRequestHandler reqHandler = new AppleDEPServerRequestHandler();
        final JSONObject requestJSON = new JSONObject();
        if (fetch) {
            requestJSON.put("DEPServiceRequestName", (Object)"FetchDevices");
        }
        else {
            requestJSON.put("DEPServiceRequestName", (Object)"SyncDevices");
        }
        requestJSON.put("CustomerId", (Object)this.customerId);
        if (cursor != null) {
            final JSONObject sycDeviceJSON = new JSONObject();
            sycDeviceJSON.put("cursor", (Object)cursor);
            requestJSON.put("DEPServiceRequestData", (Object)sycDeviceJSON);
        }
        requestJSON.put("DEP_TOKEN_ID", (Object)this.tokenId);
        final JSONObject responseJSON = reqHandler.processRequestForDEPToken(requestJSON);
        String status = null;
        if (responseJSON.get("DEPServiceStatus") != null) {
            status = String.valueOf(responseJSON.get("DEPServiceStatus"));
        }
        if (status.equalsIgnoreCase("Acknowledged")) {
            final JSONObject deviceJson = responseJSON.optJSONObject("DEPServiceResponseData");
            this.updateCursor(deviceJson.getString("cursor"));
            return deviceJson;
        }
        throw new SyMException(responseJSON.optJSONObject("DEPServiceError").optInt("DEPServerErrorCode"), responseJSON.optJSONObject("DEPServiceError").optString("DEPServerErrorMsg"), (Throwable)null);
    }
    
    public void assignDEPDevice(final JSONObject profileJSON) throws Exception {
        final AppleDEPServerRequestHandler reqHandler = new AppleDEPServerRequestHandler();
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put("DEPServiceRequestName", (Object)"AssignProfile");
        requestJSON.put("DEPServiceRequestData", (Object)profileJSON);
        requestJSON.put("CustomerId", (Object)this.customerId);
        requestJSON.put("DEP_TOKEN_ID", (Object)this.tokenId);
        final JSONObject responseJSON = reqHandler.processRequestForDEPToken(requestJSON);
        final String status = String.valueOf(responseJSON.get("DEPServiceStatus"));
        if (!status.equalsIgnoreCase("Acknowledged")) {
            throw new SyMException(responseJSON.optJSONObject("DEPServiceError").optInt("DEPServerErrorCode"), responseJSON.optJSONObject("DEPServiceError").optString("DEPServerErrorMsg"), (Throwable)null);
        }
    }
    
    public JSONObject getAccountDetailsJSON() throws Exception {
        final AppleDEPServerRequestHandler reqHandler = new AppleDEPServerRequestHandler();
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put("DEPServiceRequestName", (Object)"Account");
        requestJSON.put("CustomerId", (Object)this.customerId);
        requestJSON.put("DEP_TOKEN_ID", (Object)this.tokenId);
        final JSONObject responseJSON = reqHandler.processRequestForDEPToken(requestJSON);
        final String status = String.valueOf(responseJSON.get("DEPServiceStatus"));
        if (status.equalsIgnoreCase("Acknowledged")) {
            final JSONObject accountJSON = responseJSON.optJSONObject("DEPServiceResponseData");
            return accountJSON;
        }
        throw new SyMException(responseJSON.optJSONObject("DEPServiceError").optInt("DEPServerErrorCode"), responseJSON.optJSONObject("DEPServiceError").optString("DEPServerErrorMsg"), (Throwable)null);
    }
    
    public JSONObject getDeviceDetails(final JSONObject devicesJSON) throws Exception {
        final AppleDEPServerRequestHandler reqHandler = new AppleDEPServerRequestHandler();
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put("DEPServiceRequestName", (Object)"Devices");
        requestJSON.put("DEPServiceRequestData", (Object)devicesJSON);
        requestJSON.put("CustomerId", (Object)this.customerId);
        requestJSON.put("DEP_TOKEN_ID", (Object)this.tokenId);
        final JSONObject responseJSON = reqHandler.processRequestForDEPToken(requestJSON);
        final String status = String.valueOf(responseJSON.get("DEPServiceStatus"));
        if (status.equalsIgnoreCase("Acknowledged")) {
            final JSONObject deviceJSON = responseJSON.optJSONObject("DEPServiceResponseData");
            return deviceJSON;
        }
        throw new SyMException(responseJSON.optJSONObject("DEPServiceError").optInt("DEPServerErrorCode"), responseJSON.optJSONObject("DEPServiceError").optString("DEPServerErrorMsg"), (Throwable)null);
    }
    
    public void removeDEPProfile(final JSONObject profileJSON) throws Exception {
        final AppleDEPServerRequestHandler reqHandler = new AppleDEPServerRequestHandler();
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put("DEPServiceRequestName", (Object)"RemoveProfile");
        requestJSON.put("DEPServiceRequestData", (Object)profileJSON);
        requestJSON.put("CustomerId", (Object)this.customerId);
        requestJSON.put("DEP_TOKEN_ID", (Object)this.tokenId);
        final JSONObject responseJSON = reqHandler.processRequestForDEPToken(requestJSON);
        Logger.getLogger("MDMEnrollment").log(Level.INFO, "Remove Profile for DEP devices response: {0}", responseJSON.toString());
        if (responseJSON.has("DEPServiceError")) {
            throw new APIHTTPException("ABM021", new Object[0]);
        }
    }
    
    static {
        AppleDEPWebServicetHandler.logger = Logger.getLogger("MDMEnrollment");
    }
}
