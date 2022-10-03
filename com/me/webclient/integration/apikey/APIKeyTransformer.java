package com.me.webclient.integration.apikey;

import java.util.HashMap;
import com.adventnet.i18n.I18N;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class APIKeyTransformer extends DefaultTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final int reportType = tableContext.getViewContext().getRenderType();
        boolean isExport = false;
        if (reportType != 4) {
            isExport = true;
        }
        final String columnalias = tableContext.getPropertyName();
        return (!columnalias.equalsIgnoreCase("APIKEYINFO.API_KEY") || !isExport) && super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
        final Object data = tableContext.getPropertyValue();
        final int reportType = tableContext.getViewContext().getRenderType();
        boolean isExport = false;
        if (reportType != 4) {
            isExport = true;
        }
        if (columnalais.equals("APIKEYINFO.API_KEY")) {
            String key = (String)data;
            if (ApiFactoryProvider.getDemoUtilAPI().isDemoMode()) {
                final String sampleKey = key.substring(0, key.indexOf(45));
                final String maskFormat = "-****-****-****-************";
                key = sampleKey + maskFormat;
            }
            final JSONObject json = new JSONObject();
            json.put("apiKey", (Object)key);
            columnProperties.put("PAYLOAD", json);
        }
        if (columnalais.equals("APIKEYINFO.API_KEY_ID")) {
            final Long apiKeyId = (Long)data;
            final Long validity = (Long)tableContext.getAssociatedPropertyValue("APIKEYINFO.VALIDITY");
            final String apiServiceId = String.valueOf(tableContext.getAssociatedPropertyValue("INTEGRATIONSERVICE.SERVICE_ID"));
            final String serviceName = String.valueOf(tableContext.getAssociatedPropertyValue("INTEGRATIONSERVICE.NAME"));
            final String productId = String.valueOf(tableContext.getAssociatedPropertyValue("INTEGRATIONPRODUCTSERVICEREL.PRODUCT_ID"));
            final String apiScopes = String.valueOf(tableContext.getAssociatedPropertyValue("APIKEYSCOPE.SCOPE_NAME"));
            String regenKey = I18N.getMsg("mdm.api_key_regenerate", new Object[0]);
            final Long currrentTime = System.currentTimeMillis();
            final boolean isRevokedOrExpired = validity == 0L || validity < currrentTime;
            if (isRevokedOrExpired) {
                regenKey = I18N.getMsg("mdm.api_key_activate", new Object[0]);
            }
            if (!isExport && !serviceName.equalsIgnoreCase("securegatewayserver")) {
                final JSONObject json2 = new JSONObject();
                boolean isIntegrationProduct = true;
                if (productId == "null") {
                    isIntegrationProduct = false;
                }
                json2.put("is_integration_product", (Object)String.valueOf(isIntegrationProduct));
                json2.put("api_key_id", (Object)String.valueOf(apiKeyId));
                json2.put("integration_service_id", (Object)String.valueOf(apiServiceId));
                json2.put("is_revoked_or_expired", isRevokedOrExpired);
                json2.put("service_name", (Object)serviceName);
                json2.put("api_scopes", (Object)apiScopes);
                columnProperties.put("PAYLOAD", json2);
            }
            else {
                columnProperties.put("VALUE", "");
            }
        }
        if (columnalais.equals("APIKEYINFO.VALIDITY")) {
            String apiKeyStatus = I18N.getMsg("dc.admin.fos.server_up", new Object[0]);
            String statusClass = "ucs-table-status-text__success";
            final Long validity2 = (Long)data;
            final Long currrentTime2 = System.currentTimeMillis();
            if (validity2 == 0L) {
                apiKeyStatus = I18N.getMsg("dc.mdm.knox.container.status.inactive", new Object[0]);
                statusClass = "ucs-table-status-text__in-progress";
            }
            else if (validity2 < currrentTime2) {
                apiKeyStatus = I18N.getMsg("dc.db.config.status.Expired", new Object[0]);
                statusClass = "ucs-table-status-text__failed";
            }
            if (!isExport) {
                final JSONObject json3 = new JSONObject();
                json3.put("api_key_status", (Object)apiKeyStatus);
                json3.put("statusClass", (Object)statusClass);
                columnProperties.put("PAYLOAD", json3);
            }
            else {
                columnProperties.put("VALUE", apiKeyStatus);
            }
        }
    }
}
