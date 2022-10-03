package com.me.mdm.api.core.certificate;

import org.json.JSONException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.paging.PagingUtil;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.core.profiles.ProfilesWrapper;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.certificate.ADCertificateConfigFacade;
import com.me.mdm.api.ApiRequestHandler;

public class ADCertificateServerAPIRequestHandler extends ApiRequestHandler
{
    ADCertificateConfigFacade adCertificateConfigFacade;
    
    public ADCertificateServerAPIRequestHandler() {
        this.adCertificateConfigFacade = new ADCertificateConfigFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            final JSONObject requestJson = ProfilesWrapper.toJSONWithCollectionID(apiRequest);
            final Long customerID = APIUtil.getCustomerID(requestJson);
            final Long collectionID = APIUtil.getResourceID(requestJson, "COLLECTION_ID".toLowerCase());
            final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(requestJson);
            responseDetails.put("RESPONSE", (Object)this.adCertificateConfigFacade.getADCertConfigurations(customerID, collectionID, pagingUtil));
            return responseDetails;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error during get ADCS details", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            final JSONObject requestJson = apiRequest.toJSONObject();
            final Long customerID = APIUtil.getCustomerID(requestJson);
            final JSONObject adCertJSON = JSONUtil.getInstance().changeJSONKeyCase(requestJson.getJSONObject("msg_body"), 1);
            final Long loginID = APIUtil.getLoginID(requestJson);
            responseDetails.put("RESPONSE", (Object)this.adCertificateConfigFacade.addADCertConfiguration(customerID, loginID, adCertJSON));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final JSONException e2) {
            this.logger.log(Level.SEVERE, "Error during ADCS add", (Throwable)e2);
            throw new APIHTTPException("COM0005", new Object[] { e2 });
        }
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "Error during ADCS add", e3);
            throw new APIHTTPException("COM0004", new Object[] { e3 });
        }
    }
}
