package com.adventnet.sym.webclient.mdm.announcements;

import java.util.HashMap;
import org.json.JSONObject;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class MDMGroupsAnnouncementTransformer extends DefaultTransformer
{
    private Logger logger;
    
    public MDMGroupsAnnouncementTransformer() {
        this.logger = Logger.getLogger("AnnouncementHandler");
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalais = tableContext.getPropertyName();
        final int reportType = tableContext.getViewContext().getRenderType();
        final boolean hasWritePrivillage = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_Announcement_Write");
        if (!columnalais.equals("checkbox") && !columnalais.equals("Action")) {
            return super.checkIfColumnRendererable(tableContext);
        }
        if (reportType != 4) {
            return Boolean.FALSE;
        }
        return hasWritePrivillage;
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        Object data = tableContext.getPropertyValue();
        final String columnalais = tableContext.getPropertyName();
        final int reportType = tableContext.getViewContext().getRenderType();
        String statusStr = "--";
        if (columnalais.equals("GroupToProfileHistory.COLLECTION_STATUS")) {
            final Integer statusId = (Integer)tableContext.getAssociatedPropertyValue("GroupToProfileHistory.COLLECTION_STATUS");
            String type = "";
            if (statusId == 2) {
                statusStr = I18N.getMsg("mdm.announcement.status.yet_to_deliver", new Object[0]);
                type = "info";
            }
            else if (statusId == 3) {
                statusStr = I18N.getMsg("dir.status.in.progress", new Object[0]);
                type = "warning";
            }
            else if (statusId == 4) {
                statusStr = I18N.getMsg("mdm.content.success", new Object[0]);
                type = "success";
            }
            else if (statusId == 11) {
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
        if (columnalais.equals("GroupToProfileHistory.REMARKS")) {
            if (data != null && !((String)data).isEmpty()) {
                columnProperties.put("VALUE", I18N.getMsg((String)data, new Object[0]));
            }
            else {
                columnProperties.put("VALUE", "--");
            }
        }
        if (columnalais.equals("RecentProfileForGroup.MARKED_FOR_DELETE")) {
            if (data == Boolean.TRUE) {
                statusStr = I18N.getMsg("mdm.announcement.view.disassociataion", new Object[0]);
            }
            else {
                statusStr = I18N.getMsg("mdm.announcement.view.associataion", new Object[0]);
            }
            columnProperties.put("VALUE", statusStr);
        }
        if (columnalais.equals("MEMBER_COUNT")) {
            if (((String)data).isEmpty() || data == null) {
                data = 0;
            }
            columnProperties.put("VALUE", data);
        }
    }
}
