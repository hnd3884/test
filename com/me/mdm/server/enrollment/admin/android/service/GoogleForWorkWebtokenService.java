package com.me.mdm.server.enrollment.admin.android.service;

import com.me.mdm.core.enrollment.AndroidZTEnrollmentHandler;
import com.me.mdm.server.apps.android.afw.GooglePlayEnterpriseBusinessStore;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import org.json.JSONObject;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.me.mdm.server.apps.android.afw.GoogleAPIErrorHandler;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.enrollment.admin.android.model.GoogleWebtokenEnrollmentResponseModel;
import com.me.mdm.server.enrollment.admin.android.model.GoogleWebtokenEnrollmentModel;
import java.util.logging.Logger;

public class GoogleForWorkWebtokenService
{
    public Logger logger;
    
    public GoogleForWorkWebtokenService() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public GoogleWebtokenEnrollmentResponseModel getGoogleForWorkEnrollmentWebToken(final GoogleWebtokenEnrollmentModel googleWebtokenEnrollmentModel) {
        GoogleWebtokenEnrollmentResponseModel googleWebtokenEnrollmentResponseModel = new GoogleWebtokenEnrollmentResponseModel();
        try {
            this.validateMandatoryParamsForWebtoken(googleWebtokenEnrollmentModel);
            googleWebtokenEnrollmentResponseModel = this.constructWebtokenResponse(googleWebtokenEnrollmentResponseModel, googleWebtokenEnrollmentModel);
        }
        catch (final APIHTTPException ae) {
            this.logger.log(Level.WARNING, "APIHTTPException while getting the web token ", ae);
            throw ae;
        }
        catch (final GoogleJsonResponseException ex) {
            this.logger.log(Level.WARNING, "GoogleJsonResponseException while getting the web token ", (Throwable)ex);
            final JSONObject errorResponseJSON = GoogleAPIErrorHandler.getErrorResponseJSON(ex);
            final String apiErrorCode = errorResponseJSON.optString("apiErrorCode");
            if (apiErrorCode != null && apiErrorCode.equals("APP0026")) {
                throw new APIHTTPException(apiErrorCode, new Object[0]);
            }
            throw new APIHTTPException("COM0004", new Object[] { ex.getMessage() });
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while getting the web token ", e);
            throw new APIHTTPException("COM0004", new Object[] { e.getMessage() });
        }
        return googleWebtokenEnrollmentResponseModel;
    }
    
    private void validateMandatoryParamsForWebtoken(final GoogleWebtokenEnrollmentModel googleWebtokenEnrollmentModel) throws Exception {
        final Long customerId = googleWebtokenEnrollmentModel.getCustomerId();
        final String parent = googleWebtokenEnrollmentModel.getParent();
        final Boolean isEnterpriseCreated = GoogleForWorkSettings.isGoogleForWorkSettingsConfigured(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
        if (parent == null || parent.isEmpty()) {
            throw new APIHTTPException("COM0005", new Object[] { "Missing mandatory param : parent" });
        }
        if (!parent.startsWith("https://")) {
            throw new APIHTTPException("COM0005", new Object[] { "Field : parent, must start with https://" });
        }
        if (!isEnterpriseCreated) {
            throw new APIHTTPException("COM0015", new Object[] { "Android for Work not yet configured" });
        }
    }
    
    private String getWebtokenBasedOnEnrollmentType(final GoogleWebtokenEnrollmentModel googleWebtokenEnrollmentModel) throws Exception {
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put("parent", (Object)googleWebtokenEnrollmentModel.getParent());
        switch (googleWebtokenEnrollmentModel.getTemplateType()) {
            case 23: {
                requestJSON.put("zeroTouch", (Object)Boolean.TRUE);
                break;
            }
        }
        final GooglePlayEnterpriseBusinessStore ebs = new GooglePlayEnterpriseBusinessStore(GoogleForWorkSettings.getGoogleForWorkSettings(googleWebtokenEnrollmentModel.getCustomerId(), GoogleForWorkSettings.SERVICE_TYPE_AFW));
        return ebs.generateWebToken(requestJSON);
    }
    
    private String getDPCExtrasBasedOnEnrollmentType(final GoogleWebtokenEnrollmentModel googleWebtokenEnrollmentModel) throws Exception {
        switch (googleWebtokenEnrollmentModel.getTemplateType()) {
            case 23: {
                final AndroidZTEnrollmentHandler ztEnrollmentHandler = new AndroidZTEnrollmentHandler();
                return ztEnrollmentHandler.getZTEnrollmentProfile(googleWebtokenEnrollmentModel.getUserId(), googleWebtokenEnrollmentModel.getCustomerId());
            }
            default: {
                return null;
            }
        }
    }
    
    private GoogleWebtokenEnrollmentResponseModel constructWebtokenResponse(final GoogleWebtokenEnrollmentResponseModel googleWebtokenEnrollmentResponseModel, final GoogleWebtokenEnrollmentModel googleWebtokenEnrollmentModel) throws Exception {
        googleWebtokenEnrollmentResponseModel.setWebtoken(this.getWebtokenBasedOnEnrollmentType(googleWebtokenEnrollmentModel));
        switch (googleWebtokenEnrollmentModel.getTemplateType()) {
            case 23: {
                googleWebtokenEnrollmentResponseModel.setDpcId("com.manageengine.mdm.android");
                googleWebtokenEnrollmentResponseModel.setDpcExtras(this.getDPCExtrasBasedOnEnrollmentType(googleWebtokenEnrollmentModel));
                break;
            }
        }
        return googleWebtokenEnrollmentResponseModel;
    }
}
