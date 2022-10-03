package com.me.mdm.server.profiles.config;

import java.util.Iterator;
import com.adventnet.persistence.Row;
import org.json.JSONObject;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;

public class MDMSSOPolicyConfigHandler extends DefaultConfigHandler
{
    @Override
    public JSONArray DOToAPIJSON(final DataObject dataObject, final String configName) throws APIHTTPException {
        try {
            final JSONArray response = new JSONArray();
            final JSONObject base = super.DOToAPIJSON(dataObject, configName, "SSOAccountPolicy").getJSONObject(0);
            final JSONObject kerberos = super.DOToAPIJSON(dataObject, configName, "SSOKerberosAccount").getJSONObject(0);
            final JSONArray certificates = super.DOToAPIJSON(dataObject, configName, "SSOToCertificateRel");
            JSONObject result = JSONUtil.mergeJSONObjects(base, kerberos);
            if (certificates != null && certificates.length() > 0) {
                final JSONObject certificate = certificates.getJSONObject(0);
                result = JSONUtil.mergeJSONObjects(result, certificate);
            }
            else {
                result.put("client_cert_id", -1);
            }
            result.put("url_details", (Object)this.getURLDetails(dataObject));
            result.put("allowed_apps", (Object)this.getAppDetails(dataObject));
            response.put((Object)result);
            return response;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, "exception in MDMSSOPolicyConfigHandler", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private JSONArray getAppDetails(final DataObject dataObject) throws JSONException, DataAccessException {
        final JSONArray appArray = new JSONArray();
        final Iterator iterator = dataObject.getRows("SSOApps");
        while (iterator.hasNext()) {
            final JSONObject appDetails = new JSONObject();
            final Row row = iterator.next();
            appDetails.put("identifier", (Object)row.get("APP_IDENTIFIER"));
            appDetails.put("group_display_name", (Object)row.get("APP_NAME"));
            appArray.put((Object)appDetails);
        }
        return appArray;
    }
    
    private JSONArray getURLDetails(final DataObject dataObject) throws JSONException, DataAccessException {
        final JSONArray urlDetailsArray = new JSONArray();
        final Iterator it = dataObject.getRows("ManagedWebDomainURLDetails");
        while (it.hasNext()) {
            final JSONObject urlDetails = new JSONObject();
            final Row urlDetailsRow = it.next();
            urlDetails.put("url", urlDetailsRow.get("URL"));
            urlDetailsArray.put((Object)urlDetails);
        }
        return urlDetailsArray;
    }
}
