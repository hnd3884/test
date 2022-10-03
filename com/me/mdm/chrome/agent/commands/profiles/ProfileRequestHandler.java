package com.me.mdm.chrome.agent.commands.profiles;

import java.util.Iterator;
import com.me.mdm.chrome.agent.db.MDMAgentParamsTableHandler;
import com.me.mdm.chrome.agent.Context;
import org.json.JSONException;
import java.io.IOException;
import com.me.mdm.chrome.agent.GoogleChromeApiErrorHandler;
import com.me.mdm.chrome.agent.ChromeDeviceManager;
import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONObject;
import com.me.mdm.chrome.agent.core.Response;
import com.me.mdm.chrome.agent.core.Request;
import java.util.logging.Logger;
import com.me.mdm.chrome.agent.core.ProcessRequestHandler;

public class ProfileRequestHandler extends ProcessRequestHandler
{
    public Logger logger;
    private String remarks;
    private String remarksNotApplicable;
    static ProfileRequestHandler profileRequestHdr;
    
    public ProfileRequestHandler() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
        this.remarks = null;
        this.remarksNotApplicable = null;
    }
    
    @Override
    public void processRequest(final Request request, final Response response) {
        if (request.requestType.equalsIgnoreCase("InstallProfile")) {
            this.processInstallProfile(request, response);
        }
        else {
            this.processRemoveProfile(request, response);
        }
    }
    
    protected void processInstallProfile(final Request request, final Response response) {
        try {
            final JSONObject joProfileDetails = (JSONObject)request.requestData;
            this.logger.info("Profile Details--->" + joProfileDetails);
            final Context context = request.getContainer().getContext();
            JSONArray jaPayLoads = new JSONArray();
            jaPayLoads = joProfileDetails.getJSONArray("PayloadContent");
            final String payloadIdentifier = String.valueOf(joProfileDetails.get("PayloadIdentifier"));
            final String payloadDisplayName = joProfileDetails.optString("PayloadDisplayName", "");
            if (this.isProfileAlreadyInstalled(context, payloadIdentifier)) {
                this.logger.log(Level.INFO, "Going to modify the Profile : {0}", payloadDisplayName);
                this.processModifyProfile(request, response);
            }
            else {
                this.logger.log(Level.INFO, "Going to install the Profile : {0}", payloadDisplayName);
                final ONCPayload existingONCPayload = ChromeDeviceManager.getInstance().getONCPayload(context);
                boolean hasONCPayload = false;
                for (int i = 0; i < jaPayLoads.length(); ++i) {
                    final JSONObject joPayLoad = jaPayLoads.getJSONObject(i);
                    final PayloadRequest payloadRequest = this.preparePayloadRequest(joPayLoad);
                    final PayloadResponse payloadResponse = this.preparePayloadResponse(payloadRequest);
                    final PayloadRequestHandler handler = this.getPayloadRequestHandler(context, payloadRequest.getPayloadType());
                    payloadRequest.existingONCPayload = existingONCPayload;
                    if (handler == null) {
                        this.logger.log(Level.WARNING, "No handler found, unsupported payload {0}", payloadRequest.getPayloadType());
                        this.updateNotApplicablePayloadRemarks(payloadRequest.getPayloadType());
                    }
                    else {
                        if (!handler.checkPayloadCompatible(request, response, payloadRequest, payloadResponse)) {
                            this.logger.log(Level.WARNING, "Payload Type is not compatbile {0}", payloadRequest.getPayloadType());
                            this.updateNotApplicablePayloadRemarks(payloadRequest.getPayloadType());
                            if (response.getRemarks() != null) {
                                this.updateRemarks(response.getRemarks());
                            }
                        }
                        else {
                            handler.processInstallPayload(request, response, payloadRequest, payloadResponse);
                            if (payloadResponse.isONCPayload()) {
                                hasONCPayload = true;
                            }
                            if (response.getRemarks() != null) {
                                this.updateRemarks(response.getRemarks());
                            }
                        }
                        if (payloadResponse.getPayloadStatus().equalsIgnoreCase("Error")) {
                            this.logger.log(Level.INFO, "Error Occured while installing the payload  : ", payloadRequest.getPayloadType());
                            response.setErrorCode(payloadResponse.getErrorCode());
                            response.setErrorMessage(payloadResponse.getErrorMsg());
                            handler.restorePayload(context);
                            break;
                        }
                        if (payloadResponse.getPayloadStatus().equalsIgnoreCase("NotNow")) {
                            this.logger.log(Level.INFO, "Device currently not ready for payload {0}", payloadRequest.getPayloadType());
                            response.setStatus("NotNow");
                            handler.restorePayload(context);
                            break;
                        }
                        handler.updateDB(context, payloadRequest);
                    }
                }
                final String totalRemarks = this.getTotalRemarks();
                if (totalRemarks != null) {
                    response.setRemarks(totalRemarks);
                }
                if (hasONCPayload) {
                    existingONCPayload.publishONCProfile();
                }
            }
            if (response.getStatus().equalsIgnoreCase("Acknowledged")) {
                this.logger.info("persisting payload");
                this.persistProfileRequestData(context, joProfileDetails);
            }
        }
        catch (final IOException | JSONException exp) {
            this.logger.log(Level.WARNING, "Exception in handling process install profile", exp);
            final JSONObject errorJSON = GoogleChromeApiErrorHandler.getErrorResponseJSON(exp, true);
            response.setErrorCode(errorJSON.optInt("errorCode", 70010));
            response.setStatus("Error");
            response.setErrorMessage(errorJSON.optString("errorMsg", exp.getMessage()));
        }
    }
    
    private void updateNotApplicablePayloadRemarks(final String payloadType) {
        if (this.remarksNotApplicable == null) {
            this.remarksNotApplicable = "Profiles not applicable for this device: ";
        }
        else {
            this.remarksNotApplicable = this.remarksNotApplicable.concat(", ");
        }
        this.remarksNotApplicable = this.remarksNotApplicable.concat(payloadType);
    }
    
    private void updateRemarks(final String payloadRemarks) {
        if (this.remarks == null) {
            this.remarks = "";
            this.remarks = this.remarks.concat(payloadRemarks);
        }
        else {
            this.remarks = this.remarks.concat("; ").concat(payloadRemarks);
        }
    }
    
    private String getTotalRemarks() {
        String totalRemarks = "";
        if (this.remarksNotApplicable != null) {
            totalRemarks = this.remarksNotApplicable;
        }
        if (this.remarks != null && !this.remarks.isEmpty() && this.remarksNotApplicable != null) {
            totalRemarks = totalRemarks.concat("; ");
        }
        return this.remarks = (totalRemarks.isEmpty() ? null : totalRemarks);
    }
    
    protected void processModifyProfile(final Request request, final Response response) {
        try {
            final JSONObject joProfileDetails = (JSONObject)request.requestData;
            final Context context = request.getContainer().getContext();
            JSONArray jaOldPayLoads = new JSONArray();
            final String payloadIdentifier = String.valueOf(joProfileDetails.get("PayloadIdentifier"));
            jaOldPayLoads = this.getPayloadDataFromIdentifier(context, payloadIdentifier);
            if (jaOldPayLoads == null) {
                this.logger.info("ProfileRequestHandler: old payloads is null");
                return;
            }
            JSONArray jaNewPayLoads = new JSONArray();
            jaNewPayLoads = joProfileDetails.getJSONArray("PayloadContent");
            if (jaNewPayLoads == null) {
                this.logger.info("ProfileRequestHandler:  new payloads is null");
                return;
            }
            ONCPayload existingONCPayload = ChromeDeviceManager.getInstance().getONCPayload(context);
            boolean hasONCPayload = false;
            for (int i = 0; i < jaNewPayLoads.length(); ++i) {
                final JSONObject joNewPayLoad = jaNewPayLoads.getJSONObject(i);
                final PayloadRequest payloadRequest = this.preparePayloadRequest(joNewPayLoad);
                final PayloadResponse payloadResponse = this.preparePayloadResponse(payloadRequest);
                final PayloadRequestHandler handler = this.getPayloadRequestHandler(context, payloadRequest.getPayloadType());
                payloadRequest.existingONCPayload = existingONCPayload;
                if (handler == null) {
                    this.logger.log(Level.WARNING, "No handler found, unsupported payload {0}", payloadRequest.getPayloadType());
                    this.updateNotApplicablePayloadRemarks(payloadRequest.getPayloadType());
                }
                else {
                    if (!handler.checkPayloadCompatible(request, response, payloadRequest, payloadResponse)) {
                        this.logger.log(Level.WARNING, "Payload Type is not compatbile {0}", payloadRequest.getPayloadType());
                        this.updateNotApplicablePayloadRemarks(payloadRequest.getPayloadType());
                        if (response.getRemarks() != null) {
                            this.updateRemarks(response.getRemarks());
                        }
                    }
                    else {
                        handler.processInstallPayload(request, response, payloadRequest, payloadResponse);
                        if (payloadResponse.isONCPayload()) {
                            hasONCPayload = true;
                        }
                        if (response.getRemarks() != null) {
                            this.updateRemarks(response.getRemarks());
                        }
                    }
                    if (payloadResponse.getPayloadStatus().equalsIgnoreCase("Error")) {
                        this.logger.info("Error Occured while installing the payload " + payloadRequest.getPayloadType());
                        response.setErrorCode(payloadResponse.getErrorCode());
                        response.setErrorMessage(payloadResponse.getErrorMsg());
                        handler.restorePayload(context);
                        break;
                    }
                    if (payloadResponse.getPayloadStatus().equalsIgnoreCase("NotNow")) {
                        this.logger.info("Device currently not ready for payload " + payloadRequest.getPayloadType());
                        response.setStatus("NotNow");
                        handler.restorePayload(context);
                        break;
                    }
                    handler.updateDB(context, payloadRequest);
                }
            }
            final String totalRemarks = this.getTotalRemarks();
            if (totalRemarks != null) {
                response.setRemarks(totalRemarks);
            }
            if (hasONCPayload) {
                existingONCPayload.publishONCProfile();
            }
            existingONCPayload = ChromeDeviceManager.getInstance().getONCPayload(context);
            hasONCPayload = false;
            for (int j = 0; j < jaOldPayLoads.length(); ++j) {
                final JSONObject joOldPayLoad = jaOldPayLoads.getJSONObject(j);
                final String oldPayloadIdentifier = String.valueOf(joOldPayLoad.get("PayloadIdentifier"));
                boolean isRemoveRequired = true;
                for (int k = 0; k < jaNewPayLoads.length(); ++k) {
                    final JSONObject joNewPayLoad2 = jaNewPayLoads.getJSONObject(k);
                    final String newPayloadIdentifier = String.valueOf(joNewPayLoad2.get("PayloadIdentifier"));
                    if (newPayloadIdentifier.equalsIgnoreCase(oldPayloadIdentifier)) {
                        this.logger.info("ProfileModify : Same payload is modified.");
                        isRemoveRequired = false;
                        break;
                    }
                }
                if (isRemoveRequired) {
                    this.logger.info("ProcessModifyProfile Remove required");
                    final PayloadRequest payloadRequest2 = this.preparePayloadRequest(joOldPayLoad);
                    final PayloadResponse payloadResponse2 = this.preparePayloadResponse(payloadRequest2);
                    final PayloadRequestHandler handler2 = this.getPayloadRequestHandler(context, payloadRequest2.getPayloadType());
                    payloadRequest2.existingONCPayload = existingONCPayload;
                    if (handler2 != null) {
                        handler2.processRemovePayload(request, response, payloadRequest2, payloadResponse2);
                        if (payloadResponse2.getPayloadStatus().equalsIgnoreCase("Error")) {
                            response.setStatus("Error");
                            response.setErrorCode(payloadResponse2.getErrorCode());
                            response.setErrorMessage(payloadResponse2.getErrorMsg());
                            break;
                        }
                        handler2.removeDB(context, payloadRequest2);
                        if (payloadResponse2.isONCPayload()) {
                            hasONCPayload = true;
                        }
                    }
                    else {
                        this.logger.log(Level.WARNING, "Payload Type is not supported for modify Remove{0}", payloadRequest2.getPayloadType());
                    }
                }
            }
            if (hasONCPayload) {
                existingONCPayload.publishONCProfile();
            }
            if (response.getStatus().equalsIgnoreCase("Acknowledged")) {
                this.persistProfileRequestData(context, joProfileDetails);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in handling process install profile ", exp);
            final JSONObject errorJSON = GoogleChromeApiErrorHandler.getErrorResponseJSON(exp, true);
            response.setErrorCode(errorJSON.optInt("errorCode", 70010));
            response.setStatus("Error");
            response.setErrorMessage(errorJSON.optString("errorMsg", exp.getMessage()));
        }
    }
    
    protected void processRemoveProfile(final Request request, final Response response) {
        try {
            final JSONObject joProfileDetails = (JSONObject)request.requestData;
            final Context context = request.getContainer().getContext();
            JSONArray jaPayLoads = new JSONArray();
            final String payloadIdentifier = String.valueOf(joProfileDetails.get("PayloadIdentifier"));
            jaPayLoads = this.getPayloadDataFromIdentifier(context, payloadIdentifier);
            final String payloadDisplayName = this.getPayloadDisplayNameByIdFromDB(context, payloadIdentifier);
            if (payloadDisplayName != null) {
                this.logger.info("Going to remove the Profile : " + payloadDisplayName);
            }
            else {
                this.logger.info("There is no profile in the device with identifier: " + payloadIdentifier);
            }
            final ONCPayload existingONCPayload = ChromeDeviceManager.getInstance().getONCPayload(context);
            boolean hasONCPayload = false;
            if (jaPayLoads != null) {
                for (int i = 0; i < jaPayLoads.length(); ++i) {
                    final JSONObject joPayLoad = jaPayLoads.getJSONObject(i);
                    final PayloadRequest payloadRequest = this.preparePayloadRequest(joPayLoad);
                    final PayloadResponse payloadResponse = this.preparePayloadResponse(payloadRequest);
                    final PayloadRequestHandler handler = this.getPayloadRequestHandler(context, payloadRequest.getPayloadType());
                    payloadRequest.existingONCPayload = existingONCPayload;
                    if (handler != null && handler.checkPayloadCompatible(request, response, payloadRequest, payloadResponse)) {
                        handler.processRemovePayload(request, response, payloadRequest, payloadResponse);
                        if (payloadResponse.getPayloadStatus().equalsIgnoreCase("Error")) {
                            this.logger.info("ProfileRequestHandler: Error while removing profile payload: " + payloadResponse.getErrorCode() + ": " + payloadResponse.getErrorMsg());
                        }
                        else if (payloadResponse.getPayloadStatus().equalsIgnoreCase("NotNow")) {
                            this.logger.info("ProfileRequestHandler: Device currently not ready for payload " + payloadRequest.getPayloadType());
                            response.setStatus("NotNow");
                        }
                        else {
                            handler.removeDB(context, payloadRequest);
                        }
                        if (payloadResponse.isONCPayload()) {
                            hasONCPayload = true;
                        }
                    }
                    else {
                        this.logger.log(Level.WARNING, "Payload Type is not supported for Remove{0}", payloadRequest.getPayloadType());
                    }
                }
                if (hasONCPayload) {
                    existingONCPayload.publishONCProfile();
                }
            }
            else {
                this.logger.info("ProfileRequestHandler Profile : " + payloadDisplayName + " is already removed");
                response.setRemarks("Profile Already Installed");
            }
            if (response.getStatus().equalsIgnoreCase("Acknowledged")) {
                this.RemoveProfileRequestData(context, payloadIdentifier);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in handling process Remove profile{0}", exp.getMessage());
            final JSONObject errorJSON = GoogleChromeApiErrorHandler.getErrorResponseJSON(exp, false);
            response.setErrorCode(errorJSON.optInt("errorCode", 70010));
            response.setStatus("Error");
            response.setErrorMessage(errorJSON.optString("errorMsg", exp.getMessage()));
        }
    }
    
    private PayloadResponse preparePayloadResponse(final PayloadRequest ploadReq) {
        final PayloadResponse ploadResp = new PayloadResponse();
        try {
            ploadResp.setPayloadType(ploadReq.getPayloadType());
            ploadResp.setErrorCode(0);
            this.logger.info("prepareResponse : response " + ploadResp.toString());
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "prepareResponse : Exception ocurred : {0}", exp.getMessage());
        }
        return ploadResp;
    }
    
    private PayloadRequest preparePayloadRequest(final JSONObject joPayLoad) {
        final PayloadRequest ploadReq = new PayloadRequest();
        try {
            ploadReq.payloadType = String.valueOf(joPayLoad.get("PayloadType"));
            ploadReq.payloadIdentifier = String.valueOf(joPayLoad.get("PayloadIdentifier"));
            ploadReq.payloadData = joPayLoad;
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception ocurred in preparePayload: {0}", exp.getMessage());
        }
        return ploadReq;
    }
    
    protected PayloadRequestHandler getPayloadRequestHandler(final Context context, final String payloadType) {
        PayloadRequestHandler handler = null;
        handler = ChromeDeviceManager.getInstance().getPayloadRequestHandler(context, payloadType);
        this.logger.info("PayloadRequestHandler: " + handler);
        return handler;
    }
    
    private void persistProfileRequestData(final Context context, final JSONObject joNewPayLoads) {
        try {
            final String payloadIdentifier = String.valueOf(joNewPayLoads.get("PayloadIdentifier"));
            JSONObject joPayLoads = new JSONObject();
            if (payloadIdentifier != null) {
                final String oldPayloads = new MDMAgentParamsTableHandler(context).getStringValue("PayloadConfigList");
                if (oldPayloads != null) {
                    joPayLoads = new JSONObject(oldPayloads);
                }
                joPayLoads.put(payloadIdentifier, (Object)joNewPayLoads);
                new MDMAgentParamsTableHandler(context).addStringValue("PayloadConfigList", joPayLoads.toString());
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception Occurred while getting persistProfileRequestData{0}", exp.getMessage());
        }
    }
    
    public JSONArray getPayloadDataFromIdentifier(final Context context, final String identifier) {
        JSONArray jaPayloadData = new JSONArray();
        try {
            final String executedProfiles = new MDMAgentParamsTableHandler(context).getStringValue("PayloadConfigList");
            if (executedProfiles != null) {
                final JSONObject Payloads = new JSONObject(executedProfiles).getJSONObject(identifier);
                jaPayloadData = Payloads.getJSONArray("PayloadContent");
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception Occurred while getting persistProfileRequestData{0}", exp.getMessage());
        }
        return jaPayloadData;
    }
    
    private String getPayloadDisplayNameByIdFromDB(final Context context, final String identifier) {
        final String executedProfiles = new MDMAgentParamsTableHandler(context).getStringValue("PayloadConfigList");
        String payloadName = null;
        if (executedProfiles != null) {
            try {
                final JSONObject payload = new JSONObject(executedProfiles).getJSONObject(identifier);
                if (payload != null) {
                    payloadName = payload.optString("PayloadDisplayName", (String)null);
                }
            }
            catch (final JSONException ex) {
                this.logger.log(Level.WARNING, "Exception while constructing JSON Object from String from DB", (Throwable)ex);
            }
        }
        return payloadName;
    }
    
    public boolean isProfileAlreadyInstalled(final Context context, final String identifier) {
        boolean isAlreadyInstalled = false;
        try {
            final String installedProfiles = new MDMAgentParamsTableHandler(context).getStringValue("PayloadConfigList");
            if (installedProfiles != null) {
                final JSONObject Payloads = new JSONObject(installedProfiles).optJSONObject(identifier);
                if (Payloads != null) {
                    isAlreadyInstalled = true;
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception Occurred while getting ProfileRequestData", exp);
        }
        return isAlreadyInstalled;
    }
    
    private void RemoveProfileRequestData(final Context context, final String identifier) {
        try {
            final String executedProfiles = new MDMAgentParamsTableHandler(context).getStringValue("PayloadConfigList");
            if (executedProfiles != null) {
                new MDMAgentParamsTableHandler(context).removeValue("PayloadConfigList");
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception Occurred while removing ProfileRequestData{0}", exp.getMessage());
        }
    }
    
    public void removeAllProfiles(final Request request, final Response response) {
        final String profileConfig = new MDMAgentParamsTableHandler(request.getContainer().getContext()).getStringValue("PayloadConfigList");
        JSONObject profilePayloads = null;
        final ProcessRequestHandler handler = new ProfileRequestHandler();
        try {
            if (profileConfig != null) {
                profilePayloads = new JSONObject(profileConfig);
                final Iterator profileIterator = profilePayloads.keys();
                while (profileIterator.hasNext()) {
                    final JSONObject profile = new JSONObject();
                    profile.put("PayloadIdentifier", (Object)profileIterator.next());
                    request.requestData = profile;
                    request.requestType = "RemoveProfile";
                    handler.processRequest(request, response);
                }
                this.logger.info("Successfully Remove all the profiles ");
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception Occurred while getting removeallProfiles{0}", exp.getMessage());
        }
    }
    
    static {
        ProfileRequestHandler.profileRequestHdr = null;
    }
}
