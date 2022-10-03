package com.me.mdm.server.profiles.config;

import java.util.Iterator;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import org.json.JSONObject;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;

public class AndroidEFRPConfigHandler extends DefaultConfigHandler
{
    @Override
    public JSONArray DOToAPIJSON(final DataObject dataObject, final String configName) throws APIHTTPException {
        try {
            final JSONArray result = super.DOToAPIJSON(dataObject, configName);
            final JSONObject efrp = result.getJSONObject(0);
            if (!dataObject.isEmpty()) {
                final JSONArray efrpDetArray = new JSONArray();
                final Iterator it = dataObject.getRows("EFRPAccDetails");
                while (it.hasNext()) {
                    final JSONObject emailDetails = new JSONObject();
                    final Row emailRow = it.next();
                    emailDetails.put("email_user_id", emailRow.get("EMAIL_USER_ID"));
                    emailDetails.put("email_id", emailRow.get("EMAIL_ID"));
                    efrpDetArray.put((Object)emailDetails);
                }
                efrp.put("efrp_details", (Object)efrpDetArray);
            }
            return result;
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in DOToAPIJSON AndroidEFRPConfigHandler", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
