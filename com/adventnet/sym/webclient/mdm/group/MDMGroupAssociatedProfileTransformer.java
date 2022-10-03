package com.adventnet.sym.webclient.mdm.group;

import java.util.HashMap;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class MDMGroupAssociatedProfileTransformer extends DefaultTransformer
{
    private Logger logger;
    
    public MDMGroupAssociatedProfileTransformer() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalais = tableContext.getPropertyName();
        final int reportType = tableContext.getViewContext().getRenderType();
        final boolean configWrite = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_Configurations_Write");
        final boolean configModernMgmtWrite = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("ModernMgmt_Configurations_Write");
        final boolean hasConfigurationWritePrivillage = configWrite || configModernMgmtWrite;
        if (!columnalais.equals("checkbox") && !columnalais.equals("Action")) {
            return super.checkIfColumnRendererable(tableContext);
        }
        if (reportType != 4) {
            return Boolean.FALSE;
        }
        return hasConfigurationWritePrivillage;
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final Object data = tableContext.getPropertyValue();
        final String columnalais = tableContext.getPropertyName();
        final int reportType = tableContext.getViewContext().getRenderType();
        String statusStr = "--";
        if (columnalais.equals("GroupToProfileHistory.COLLECTION_STATUS")) {
            final String statusLabel = (String)tableContext.getAssociatedPropertyValue("ConfigStatusDefn.Label");
            statusStr = I18N.getMsg(statusLabel, new Object[0]);
            columnProperties.put("VALUE", statusStr);
        }
        if (columnalais.equals("GroupToProfileHistory.REMARKS") && data != null) {
            columnProperties.put("VALUE", I18N.getMsg((String)data, new Object[0]));
        }
        if (columnalais.equals("RecentProfileForGroup.MARKED_FOR_DELETE")) {
            if (data == Boolean.TRUE) {
                statusStr = I18N.getMsg("dc.mdm.group.view.profile_disassociataion", new Object[0]);
            }
            else {
                statusStr = I18N.getMsg("dc.mdm.group.view.profile_associataion", new Object[0]);
            }
            columnProperties.put("VALUE", statusStr);
        }
    }
}
