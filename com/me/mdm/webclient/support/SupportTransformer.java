package com.me.mdm.webclient.support;

import java.util.HashMap;
import com.me.devicemanagement.framework.server.util.Utils;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserHandler;
import org.json.JSONObject;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class SupportTransformer extends DefaultTransformer
{
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
        if (columnalais.equals("BUILD_TYPE")) {
            final String columnValue = tableContext.getAssociatedPropertyValue("BUILD_TYPE").toString();
            final String description = tableContext.getAssociatedPropertyValue("REMARKS").toString();
            final String buildno = tableContext.getAssociatedPropertyValue("BUILD_NUMBER").toString();
            final JSONObject json = new JSONObject();
            json.put("build_number", (Object)buildno);
            json.put("build_type", (Object)columnValue);
            if (Integer.parseInt(columnValue) == 3) {
                json.put("description", (Object)description.substring(description.indexOf("PatchDescription") + 17, description.indexOf("type=JarPPM") - 2));
            }
            columnProperties.put("PAYLOAD", json);
        }
        else if (columnalais.equals("BUILD_DETECTED_AT")) {
            final String columnValue = tableContext.getAssociatedPropertyValue("BUILD_DETECTED_AT").toString();
            columnProperties.put("VALUE", Utils.longdateToString(Long.parseLong(columnValue), DMOnPremiseUserHandler.getUserTimeFormat()));
        }
    }
}
