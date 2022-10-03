package com.me.mdm.server.profiles.config;

import java.util.Iterator;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;

public class DefaultKioskConfigHandler extends DefaultConfigHandler
{
    protected void addWebClipsRel(final JSONObject configObject, final DataObject dataObject, final JSONArray templateConfigProperties) throws Exception {
        final Iterator webClipIterator = dataObject.getRows("WebClipPolicies");
        final JSONArray webAppsArray = new JSONArray();
        final JSONObject webClipTemplate = this.getSubConfigProperties(templateConfigProperties, "WebClipPolicies");
        final JSONArray webClipsProperties = webClipTemplate.getJSONArray("properties");
        while (webClipIterator.hasNext()) {
            final JSONObject webClipJSON = new JSONObject();
            final Row webClipRow = webClipIterator.next();
            final Long webClipPolicyId = (Long)webClipRow.get("WEBCLIP_POLICY_ID");
            webClipJSON.put(this.getSubConfigProperties(webClipsProperties, "WEBCLIP_POLICY_ID").getString("alias"), (Object)webClipPolicyId);
            final String webClipLabel = (String)webClipRow.get("WEBCLIP_NAME");
            webClipJSON.put(this.getSubConfigProperties(webClipsProperties, "WEBCLIP_NAME").getString("alias"), (Object)webClipLabel);
            webAppsArray.put((Object)webClipJSON);
        }
        configObject.put(webClipTemplate.getString("alias"), (Object)webAppsArray);
    }
}
