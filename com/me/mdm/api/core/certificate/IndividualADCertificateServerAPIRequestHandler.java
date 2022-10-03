package com.me.mdm.api.core.certificate;

import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.certificate.ADCertificateConfigFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualADCertificateServerAPIRequestHandler extends ApiRequestHandler
{
    ADCertificateConfigFacade adCertificateConfigFacade;
    
    public IndividualADCertificateServerAPIRequestHandler() {
        this.adCertificateConfigFacade = new ADCertificateConfigFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            apiRequest.urlStartKey = "servers";
            final JSONObject requestJson = apiRequest.toJSONObject();
            final APIUtil apiUtil = APIUtil.getNewInstance();
            final Long customerID = APIUtil.getCustomerID(requestJson);
            final Long adConfigID = APIUtil.getResourceID(requestJson, "server_id");
            responseDetails.put("RESPONSE", (Object)this.adCertificateConfigFacade.getADCertServer(customerID, adConfigID));
            return responseDetails;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error during get individual ADCS detail", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            apiRequest.urlStartKey = "servers";
            final JSONObject requestJson = apiRequest.toJSONObject();
            final Long loginID = APIUtil.getLoginID(requestJson);
            final Long userID = APIUtil.getUserID(requestJson);
            final Long customerID = APIUtil.getCustomerID(requestJson);
            final Long adConfigID = APIUtil.getResourceID(requestJson, "server_id");
            final Boolean isRedistributionNeed = requestJson.getJSONObject("msg_body").optBoolean("redistribute_profiles", (boolean)Boolean.FALSE);
            final JSONObject adCertJson = JSONUtil.getInstance().changeJSONKeyCase(requestJson.getJSONObject("msg_body"), 1);
            responseDetails.put("RESPONSE", (Object)this.adCertificateConfigFacade.modifyADCertServerDetails(loginID, userID, customerID, adConfigID, isRedistributionNeed, adCertJson));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Error during modify ADCS ", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Error during modify ADCS ", e2);
            throw new APIHTTPException("COM0004", (Object[])null);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 202);
            apiRequest.urlStartKey = "servers";
            final JSONObject requestJson = apiRequest.toJSONObject();
            final Long loginID = APIUtil.getLoginID(requestJson);
            final Long userID = APIUtil.getUserID(requestJson);
            final Long customerID = APIUtil.getCustomerID(requestJson);
            final Long adConfigID = APIUtil.getResourceID(requestJson, "server_id");
            final Boolean isRedistributionNeed = requestJson.getJSONObject("msg_body").optBoolean("redistribute_profiles", (boolean)Boolean.FALSE);
            responseDetails.put("RESPONSE", (Object)this.adCertificateConfigFacade.deleteADCertificateServer(loginID, userID, customerID, adConfigID, isRedistributionNeed));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Error during delete ADCS ", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Error during delete ADCS ", e2);
            throw new APIHTTPException("COM0004", (Object[])null);
        }
    }
}
