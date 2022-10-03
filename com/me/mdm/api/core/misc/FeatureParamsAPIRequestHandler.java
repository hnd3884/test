package com.me.mdm.api.core.misc;

import java.util.Iterator;
import java.util.HashMap;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.List;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class FeatureParamsAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            String featureNameCSVString = apiRequest.getParameterList().get("feature_name_list");
            if (featureNameCSVString != null) {
                featureNameCSVString = StringUtils.strip(featureNameCSVString, ", ");
            }
            final JSONObject result = new JSONObject();
            if (featureNameCSVString != null && featureNameCSVString.length() > 0) {
                final HashMap<String, Boolean> mdmfeatureparams = MDMFeatureParamsHandler.getMDMFeatureParamsForFeatureNames(new ArrayList<String>(Arrays.asList(featureNameCSVString.split(","))));
                for (final String key : mdmfeatureparams.keySet()) {
                    result.put(key, (Object)mdmfeatureparams.get(key));
                }
            }
            else {
                final HashMap<String, String> mdmfeatureparams2 = MDMFeatureParamsHandler.getMDMFeatureParameters();
                for (final String key : mdmfeatureparams2.keySet()) {
                    result.put(key, (Object)mdmfeatureparams2.get(key));
                }
            }
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)result);
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "FeatureParamsAPIRequestHandler error", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
