package com.me.mdm.webclient.osupdate;

import java.util.List;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.webclient.transformer.TransformerUtil;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import com.adventnet.client.view.web.ViewContext;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class MDMOSUpdateExecutionStatusTransformer extends DefaultTransformer
{
    private Logger logger;
    
    public MDMOSUpdateExecutionStatusTransformer() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final int reportType = tableContext.getViewContext().getRenderType();
        final String columnalais = tableContext.getPropertyName();
        final ViewContext vc = tableContext.getViewContext();
        final String viewname = vc.getUniqueId();
        if (!columnalais.equals("checkbox") && !columnalais.equals("ManagedDevice.RESOURCE_ID")) {
            return super.checkIfColumnRendererable(tableContext);
        }
        if (reportType != 4) {
            return false;
        }
        final boolean hasWritePrivillage = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_OSUpdateMgmt_Write");
        return hasWritePrivillage;
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final ViewContext viewCtx = tableContext.getViewContext();
        final String viewname = viewCtx.getUniqueId();
        final Object data = tableContext.getPropertyValue();
        final String columnalais = tableContext.getPropertyName();
        final long deviceId = (long)tableContext.getAssociatedPropertyValue("ManagedDevice.RESOURCE_ID");
        String statusStr = "";
        this.logger.log(Level.FINE, "Columnalais : ", columnalais);
        boolean isExport = false;
        final int reportType = tableContext.getViewContext().getRenderType();
        if (reportType != 4) {
            isExport = true;
        }
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
        if (columnalais.equalsIgnoreCase("MdModelInfo.MODEL_TYPE") && data != null) {
            final int modelType = (int)data;
            final String modelTypeName = MDMUtil.getInstance().getModelTypeName(modelType);
            columnProperties.put("VALUE", modelTypeName);
        }
    }
    
    private boolean hasUserDeviceMapping(final ViewContext vc, final Long resourceID) throws Exception {
        final List adminLoginIds = (List)TransformerUtil.getPreValuesForTransformer(vc, "USER_DEVICE_RESOURCE");
        return adminLoginIds != null && adminLoginIds.contains(resourceID);
    }
}
