package com.adventnet.sym.webclient.mdm.config;

import java.util.HashMap;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class AppUpdatePolicyToGroupColumnTransformer extends DefaultTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnAlias = tableContext.getPropertyName();
        if (columnAlias.equalsIgnoreCase("checkbox") || columnAlias.equalsIgnoreCase("Resource.RESOURCE_ID")) {
            final boolean hasAppMgmtWritePrivillage = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_AppMgmt_Write") || ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("ModernMgmt_AppMgmt_Write");
            return hasAppMgmtWritePrivillage;
        }
        return super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final String columnAlias = tableContext.getPropertyName();
        final Object data = tableContext.getPropertyValue();
        final Long appUpdatePolicyId = (Long)tableContext.getAssociatedPropertyValue("RecentProfileForGroup.PROFILE_ID");
        final Long resourceId = (Long)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_ID");
        if (columnAlias.equalsIgnoreCase("checkbox") || columnAlias.equalsIgnoreCase("Resource.RESOURCE_ID") || columnAlias.equalsIgnoreCase("Resource.NAME")) {
            final JSONObject payloadData = new JSONObject();
            payloadData.put("appUpdatePolicyId", (Object)appUpdatePolicyId.toString());
            payloadData.put("groupId", (Object)resourceId.toString());
            payloadData.put("cellValue", (Object)data.toString());
            columnProperties.put("PAYLOAD", payloadData);
        }
    }
}
