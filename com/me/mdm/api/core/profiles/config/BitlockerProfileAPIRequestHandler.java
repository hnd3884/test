package com.me.mdm.api.core.profiles.config;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.adventnet.sym.server.mdm.encryption.windows.bitlocker.BitlockerFacade;
import com.me.mdm.api.ApiRequestHandler;

public class BitlockerProfileAPIRequestHandler extends ApiRequestHandler
{
    private BitlockerFacade bitlockerFacade;
    
    public BitlockerProfileAPIRequestHandler() {
        this.bitlockerFacade = new BitlockerFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.bitlockerFacade.getBitlockerPolicy(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while getting bitlocker profile", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("RESPONSE", (Object)this.bitlockerFacade.addBitlockerPolicy(apiRequest.toJSONObject()));
            responseDetails.put("status", 200);
            return responseDetails;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while adding bitlocker profile", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
