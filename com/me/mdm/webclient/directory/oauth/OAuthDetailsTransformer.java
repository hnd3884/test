package com.me.mdm.webclient.directory.oauth;

import java.util.HashMap;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.webclient.directory.MDMAllDomainsViewTransformer;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class OAuthDetailsTransformer extends DefaultTransformer
{
    private Logger logger;
    
    public OAuthDetailsTransformer() {
        this.logger = Logger.getLogger("OauthLogger");
    }
    
    public void renderCell(final TransformerContext tableContext) {
        try {
            final Object data = tableContext.getPropertyValue();
            final String columnalais = tableContext.getPropertyName();
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final boolean export = tableContext.getViewContext().getRenderType() != 4;
            if (columnalais.equals("OauthMetadata.DOMAIN_TYPE")) {
                new MDMAllDomainsViewTransformer().getDomainType(data, columnProperties);
            }
            if (columnalais.equals("OauthMetadata.OAUTH_CLIENT_ID")) {
                String valueStr = String.valueOf(data);
                if (SyMUtil.isStringEmpty(valueStr)) {
                    valueStr = "--";
                }
                if (export) {
                    columnProperties.put("VALUE", valueStr);
                }
                else {
                    final JSONObject payload = new JSONObject();
                    payload.put("data", (Object)valueStr);
                    payload.put("hoverText", (Object)valueStr);
                    columnProperties.put("PAYLOAD", payload);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in renderCell", e);
        }
    }
}
