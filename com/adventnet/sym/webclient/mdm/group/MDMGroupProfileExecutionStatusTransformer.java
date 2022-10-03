package com.adventnet.sym.webclient.mdm.group;

import com.adventnet.client.view.web.ViewContext;
import java.util.HashMap;
import com.me.mdm.webclient.transformer.TransformerUtil;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class MDMGroupProfileExecutionStatusTransformer extends DefaultTransformer
{
    private Logger logger;
    
    public MDMGroupProfileExecutionStatusTransformer() {
        this.logger = Logger.getLogger(MDMGroupProfileExecutionStatusTransformer.class.getName());
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
        final ViewContext viewCtx = tableContext.getViewContext();
        final String viewname = viewCtx.getUniqueId();
        final Object data = tableContext.getPropertyValue();
        final String columnalais = tableContext.getPropertyName();
        final int reportType = tableContext.getViewContext().getRenderType();
        String statusStr = "--";
        boolean isExport = Boolean.FALSE;
        if (reportType != 4) {
            isExport = Boolean.TRUE;
        }
        this.logger.log(Level.FINE, "Columnalais : ", columnalais);
        if (columnalais.equals("CollnToResources.STATUS")) {
            final String statusLabel = (String)tableContext.getAssociatedPropertyValue("ConfigStatusDefn.StatusLabel");
            statusStr = I18N.getMsg(statusLabel, new Object[0]);
            columnProperties.put("VALUE", statusStr);
        }
        if (columnalais.equals("CollnToResources.REMARKS")) {
            final Integer statusID = (Integer)tableContext.getAssociatedPropertyValue("CollnToResources.STATUS");
            final Boolean isErr = statusID == 7 || statusID == 11;
            TransformerUtil.renderRemarksAsText(tableContext, columnProperties, (String)data, isErr, isExport);
        }
        if (columnalais.equals("RecentProfileForResource.MARKED_FOR_DELETE")) {
            if (viewname.equalsIgnoreCase("mdmDevicesAssociatedwithProfile")) {
                if (data == Boolean.TRUE) {
                    statusStr = I18N.getMsg("dc.mdm.group.view.profile_disassociataion", new Object[0]);
                }
                else {
                    statusStr = I18N.getMsg("dc.mdm.group.view.profile_associataion", new Object[0]);
                }
            }
            columnProperties.put("VALUE", statusStr);
        }
    }
}
