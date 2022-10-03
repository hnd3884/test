package com.adventnet.sym.webclient.mdm.announcements;

import com.adventnet.client.view.web.ViewContext;
import java.util.HashMap;
import com.me.mdm.webclient.transformer.TransformerUtil;
import org.json.JSONObject;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class MDMDevicesAnnouncementTransformer extends DefaultTransformer
{
    private Logger logger;
    
    public MDMDevicesAnnouncementTransformer() {
        this.logger = Logger.getLogger("AnnouncementHandler");
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalais = tableContext.getPropertyName();
        final int reportType = tableContext.getViewContext().getRenderType();
        final boolean hasConfigurationWritePrivillage = tableContext.getViewContext().getRequest().isUserInRole("MDM_Announcement_Write");
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
            final Integer statusId = (Integer)tableContext.getAssociatedPropertyValue("CollnToResources.STATUS");
            String type = "";
            if (statusId == 18 || statusId == 200 || statusId == 300 || statusId == 1 || statusId == 3 || statusId == 12 || statusId == 16 || statusId == 18 || statusId == 13) {
                statusStr = I18N.getMsg("mdm.announcement.status.yet_to_deliver", new Object[0]);
                type = "info";
            }
            else if (statusId == 2 || statusId == 4 || statusId == 6) {
                statusStr = I18N.getMsg("mdm.announcement.status.delivered", new Object[0]);
                type = "warning";
            }
            else if (statusId == 961) {
                statusStr = I18N.getMsg("mdm.announcement.status.read", new Object[0]);
                type = "success";
            }
            else if (statusId == 962) {
                statusStr = I18N.getMsg("mdm.announcement.status.acknowledged", new Object[0]);
                type = "notify";
            }
            else if (statusId == 7 || statusId == 9 || statusId == 10 || statusId == 11) {
                statusStr = I18N.getMsg("dir.status.failed", new Object[0]);
                type = "failure";
            }
            if (reportType == 4) {
                final JSONObject payload = new JSONObject();
                payload.put("cellValue", (Object)statusStr);
                payload.put("type", (Object)type);
                columnProperties.put("PAYLOAD", payload);
            }
            else {
                columnProperties.put("VALUE", statusStr);
            }
        }
        if (columnalais.equals("CollnToResources.REMARKS")) {
            final Integer statusID = (Integer)tableContext.getAssociatedPropertyValue("CollnToResources.STATUS");
            final Boolean isErr = statusID == 7 || statusID == 11;
            TransformerUtil.renderRemarksAsText(tableContext, columnProperties, (String)data, isErr, isExport);
        }
        if (columnalais.equals("RecentProfileForResource.MARKED_FOR_DELETE")) {
            if (data == Boolean.TRUE) {
                statusStr = I18N.getMsg("mdm.announcement.view.disassociataion", new Object[0]);
            }
            else {
                statusStr = I18N.getMsg("mdm.announcement.view.associataion", new Object[0]);
            }
            columnProperties.put("VALUE", statusStr);
        }
    }
}
