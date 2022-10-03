package com.me.mdm.server.doc.policy;

import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import org.json.JSONArray;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class DocPolicyFacade
{
    public static final Logger LOGGER;
    
    public JSONObject getDocPolicyById(final JSONObject apiRequestJSON) {
        JSONObject bodyJSON = null;
        try {
            final Long policyId = APIUtil.getResourceID(apiRequestJSON, "docpolic_id");
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            bodyJSON = DocPolicyHandler.getInstance().getCmDeploymentPolicyById(policyId, customerId, 401);
        }
        catch (final Exception e) {
            DocPolicyFacade.LOGGER.log(Level.SEVERE, "No default entry available", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return bodyJSON;
    }
    
    public JSONArray getDocPolicy(final JSONObject apiRequestJSON) {
        JSONArray bodyJSON = null;
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            final Long userId = APIUtil.getUserID(apiRequestJSON);
            bodyJSON = DocPolicyHandler.getInstance().getCmDeploymentPolicy(customerId);
        }
        catch (final Exception e) {
            DocPolicyFacade.LOGGER.log(Level.SEVERE, "No default entry available", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return bodyJSON;
    }
    
    public JSONObject updateDocPolicy(final JSONObject apiRequestJSON) {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "update-failed";
        JSONObject bodyJSON = null;
        try {
            final Long policyId = APIUtil.getResourceID(apiRequestJSON, "docpolic_id");
            secLog.put((Object)"DOC_POLICY_ID", (Object)policyId);
            final Long userID = APIUtil.getUserID(apiRequestJSON);
            final Long cutId = APIUtil.getCustomerID(apiRequestJSON);
            if (apiRequestJSON.has("msg_body")) {
                final JSONObject msgObj = apiRequestJSON.getJSONObject("msg_body");
                secLog.put((Object)"CONFIG_PARAMS", (Object)msgObj);
                final Integer auto_down = msgObj.getInt("auto_download");
                final Integer documentShare = msgObj.getInt("document_share");
                final Integer clipRestrict = msgObj.getInt("clip_restrict");
                final Integer documentDelete = msgObj.getInt("document_delete");
                final Integer screenShotRestrict = msgObj.getInt("screenshot_restrict");
                final Integer requirePassword = msgObj.getInt("require_password");
                final String configName = msgObj.optString("config_name");
                final String description = msgObj.optString("description");
                final JSONObject cmdepDetails = new JSONObject();
                cmdepDetails.put("AUTO_DOWNLOAD", (Object)auto_down);
                cmdepDetails.put("DOCUMENT_SHARE", (Object)documentShare);
                cmdepDetails.put("CLIP_RESTRICT", (Object)clipRestrict);
                cmdepDetails.put("DOCUMENT_DELETE", (Object)documentDelete);
                cmdepDetails.put("SCREENSHOT_RESTRICT", (Object)screenShotRestrict);
                cmdepDetails.put("REQUIRE_PASSWORD", (Object)requirePassword);
                cmdepDetails.put("DEPLOYMENT_CONFIG_NAME", (Object)configName);
                cmdepDetails.put("DEPLOYMENT_CONFIG_DESCRIPTION", (Object)description);
                final Long confId = DocPolicyHandler.getInstance().addOrUpdateDeploymentPolicy(policyId, cutId, userID, cmdepDetails);
                bodyJSON = new JSONObject();
                bodyJSON.put("DEPLOYMENT_POLICY_ID", (Object)confId);
                remarks = "update-success";
            }
        }
        catch (final APIHTTPException apihttpException) {
            DocPolicyFacade.LOGGER.log(Level.SEVERE, "Api exception", apihttpException);
            throw apihttpException;
        }
        catch (final Exception e) {
            DocPolicyFacade.LOGGER.log(Level.SEVERE, "No default entry available", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "UPDATE_DOC_POLICY", secLog);
        }
        if (bodyJSON == null) {
            DocPolicyFacade.LOGGER.log(Level.SEVERE, "body json is empty");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return bodyJSON;
    }
    
    public JSONObject addDocPolicy(final JSONObject apiRequestJSON) {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "add-failed";
        JSONObject bodyJSON = null;
        try {
            final Long userID = APIUtil.getUserID(apiRequestJSON);
            final Long cutId = APIUtil.getCustomerID(apiRequestJSON);
            if (apiRequestJSON.has("msg_body")) {
                final JSONObject msgObj = apiRequestJSON.getJSONObject("msg_body");
                secLog.put((Object)"CONFIG_PARAMS", (Object)msgObj);
                final Integer auto_down = msgObj.getInt("auto_download");
                final Integer documentShare = msgObj.getInt("document_share");
                final Integer clipRestrict = msgObj.getInt("clip_restrict");
                final Integer documentDelete = msgObj.getInt("document_delete");
                final Integer screenShotRestrict = msgObj.getInt("screenshot_restrict");
                final Integer requirePassword = msgObj.getInt("require_password");
                final String configName = msgObj.optString("config_name");
                final String description = msgObj.optString("description");
                final JSONObject cmdepDetails = new JSONObject();
                cmdepDetails.put("AUTO_DOWNLOAD", (Object)auto_down);
                cmdepDetails.put("DOCUMENT_SHARE", (Object)documentShare);
                cmdepDetails.put("CLIP_RESTRICT", (Object)clipRestrict);
                cmdepDetails.put("DOCUMENT_DELETE", (Object)documentDelete);
                cmdepDetails.put("SCREENSHOT_RESTRICT", (Object)screenShotRestrict);
                cmdepDetails.put("REQUIRE_PASSWORD", (Object)requirePassword);
                cmdepDetails.put("DEPLOYMENT_CONFIG_NAME", (Object)configName);
                cmdepDetails.put("DEPLOYMENT_CONFIG_DESCRIPTION", (Object)description);
                final Long confId = DocPolicyHandler.getInstance().addOrUpdateDeploymentPolicy(null, cutId, userID, cmdepDetails);
                bodyJSON = new JSONObject();
                bodyJSON.put("DEPLOYMENT_POLICY_ID", (Object)confId);
                bodyJSON.put("config_name", (Object)configName);
                secLog.put((Object)"DOC_POLICY_ID", (Object)confId);
                remarks = "add-success";
            }
        }
        catch (final APIHTTPException apihttpException) {
            DocPolicyFacade.LOGGER.log(Level.SEVERE, "Api exception", apihttpException);
            throw apihttpException;
        }
        catch (final Exception e) {
            DocPolicyFacade.LOGGER.log(Level.SEVERE, "No default entry available", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "ADD_DOC_POLICY", secLog);
        }
        if (bodyJSON == null) {
            DocPolicyFacade.LOGGER.log(Level.SEVERE, "body json is empty");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return bodyJSON;
    }
    
    public void deletePolicy(final JSONObject apiRequestJSON) {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "delete-failed";
        try {
            final Long policyId = APIUtil.getResourceID(apiRequestJSON, "docpolic_id");
            secLog.put((Object)"DOC_POLICY_ID", (Object)policyId);
            final Long cutId = APIUtil.getCustomerID(apiRequestJSON);
            DocPolicyHandler.getInstance().deleteDeploymentById(policyId, cutId);
            remarks = "delete-success";
        }
        catch (final APIHTTPException ae) {
            DocPolicyFacade.LOGGER.log(Level.SEVERE, "Api Exception");
            throw ae;
        }
        catch (final Exception e) {
            DocPolicyFacade.LOGGER.log(Level.SEVERE, "No default entry available", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "DELETE_DOC_POLICY", secLog);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMDocLogger");
    }
}
