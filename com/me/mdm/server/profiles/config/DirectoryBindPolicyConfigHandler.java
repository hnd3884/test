package com.me.mdm.server.profiles.config;

import java.util.Iterator;
import org.json.JSONObject;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;

public class DirectoryBindPolicyConfigHandler extends DefaultConfigHandler
{
    @Override
    public JSONArray DOToAPIJSON(final DataObject dataObject, final String configName) throws APIHTTPException {
        try {
            final JSONArray result = super.DOToAPIJSON(dataObject, configName);
            final JSONObject directorybindpolicy = result.getJSONObject(0);
            if (!dataObject.isEmpty()) {
                final JSONArray policyArray = new JSONArray();
                final Iterator it = dataObject.getRows("ADBindPrivilegeGroup");
                while (it.hasNext()) {
                    final Row adminRow = it.next();
                    policyArray.put(adminRow.get("GROUP_NAME"));
                }
                directorybindpolicy.put("ADMINPRIVILEGEADGROUP", (Object)policyArray);
                final Row ouRow = dataObject.getRow("ADBindOU");
                directorybindpolicy.put("ADBINDOU", ouRow.get("OU"));
            }
            return result;
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in DOToAPIJSON DirectoryBindPolicy", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
