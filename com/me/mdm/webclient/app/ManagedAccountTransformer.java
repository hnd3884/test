package com.me.mdm.webclient.app;

import java.util.HashMap;
import org.json.JSONObject;
import com.me.mdm.webclient.i18n.MDMI18N;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.apps.android.afw.AFWAccountErrorHandler;
import com.adventnet.i18n.I18N;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class ManagedAccountTransformer extends DefaultTransformer
{
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final Object data = tableContext.getPropertyValue();
        final String columnalais = tableContext.getPropertyName();
        if (columnalais.equals("AFWAccountStatus.ACCOUNT_STATUS")) {
            final Integer statusId = (Integer)data;
            String status = "";
            if (statusId == null) {
                status = I18N.getMsg("mdm.afw.account.status.not.initated", new Object[0]);
            }
            else if (statusId == 1) {
                status = I18N.getMsg("dir.status.in.progress", new Object[0]);
            }
            else if (statusId == 2) {
                status = I18N.getMsg("dir.status.succeeded", new Object[0]);
            }
            else if (statusId == 3) {
                status = I18N.getMsg("dc.mdm.reports.safetynet_failed", new Object[0]);
            }
            else if (statusId == 4) {
                status = I18N.getMsg("dc.db.mdm.status.initiated", new Object[0]);
            }
            else if (statusId == 5) {
                status = I18N.getMsg("mdm.afw.account.status.not.initated", new Object[0]);
            }
            columnProperties.put("VALUE", status);
        }
        if (columnalais.equals("AFWAccountStatus.REMARKS")) {
            Integer errorCode = (Integer)tableContext.getAssociatedPropertyValue("AFWAccountStatus.ERROR_CODE");
            Integer status2 = (Integer)tableContext.getAssociatedPropertyValue("AFWAccountStatus.ACCOUNT_STATUS");
            String remarks = (String)data;
            if (status2 == null) {
                status2 = 5;
                errorCode = -1;
            }
            final boolean isRetryNeeded = new AFWAccountErrorHandler().getIfRetryAllowed(errorCode, status2);
            if (MDMStringUtils.isEmpty(remarks)) {
                remarks = new AFWAccountErrorHandler().getRemarksForErrorCode(status2, errorCode, -1L);
            }
            remarks = MDMI18N.getMsg(remarks, false, false);
            final JSONObject payload = new JSONObject();
            payload.put("isRetryNeeded", isRetryNeeded);
            payload.put("remarks", (Object)remarks);
            columnProperties.put("PAYLOAD", payload);
            if (remarks == null || remarks == "") {
                columnProperties.put("VALUE", "--");
            }
            else {
                columnProperties.put("VALUE", remarks);
            }
        }
    }
}
