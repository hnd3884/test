package com.adventnet.sym.webclient.mdm.inv;

import com.adventnet.i18n.I18N;
import java.util.HashMap;
import org.json.JSONObject;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.client.components.web.TransformerContext;
import java.util.Arrays;
import java.util.List;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class DeviceUserViewTransformer extends DefaultTransformer
{
    public List yesNoStatusCellColumns;
    
    public DeviceUserViewTransformer() {
        this.yesNoStatusCellColumns = Arrays.asList("MDDeviceUserAccounts.DATA_SYNCED", "MDDeviceUserAccounts.HAS_SECURE_TOKEN", "MDDeviceUserAccounts.IS_LOGGED_IN", "MDDeviceUserAccounts.IS_MOBILE_ACCOUNT");
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
        final Object data = tableContext.getPropertyValue();
        final int reportType = tableContext.getViewContext().getRenderType();
        if (columnalais.equalsIgnoreCase("MDDeviceUserAccounts.DATA_QUOTA") || columnalais.equalsIgnoreCase("MDDeviceUserAccounts.DATA_USED")) {
            if (!MDMStringUtils.isEmpty(String.valueOf(data))) {
                final String convertedBytes = SYMClientUtil.convertBytesToGBorMB((long)Long.valueOf((String)data));
                columnProperties.put("VALUE", convertedBytes);
            }
        }
        else if (this.yesNoStatusCellColumns.contains(columnalais)) {
            String value = this.getColumnValue(columnalais, false);
            String svgImage = "cancel-filled";
            String svgFill = "#e55e5e";
            final Boolean checkOnlyNull = false;
            if ((data != null && data.toString() == "true" && data != "--") || (checkOnlyNull && data != null && data != "--")) {
                value = this.getColumnValue(columnalais, true);
                svgImage = "green-tick";
                svgFill = "";
            }
            if (reportType == 4) {
                final JSONObject payload = new JSONObject();
                payload.put("cellData", (Object)value);
                payload.put("svgImage", (Object)svgImage);
                payload.put("svgFill", (Object)svgFill);
                columnProperties.put("PAYLOAD", payload);
            }
            else {
                columnProperties.put("VALUE", value);
            }
        }
    }
    
    private String getColumnValue(final String columnalias, final boolean value) throws Exception {
        if (value && columnalias.equalsIgnoreCase("MDDeviceUserAccounts.IS_LOGGED_IN")) {
            return I18N.getMsg("mdm.inv.shared.logged_in", new Object[0]);
        }
        if (!value && columnalias.equalsIgnoreCase("MDDeviceUserAccounts.IS_LOGGED_IN")) {
            return I18N.getMsg("mdm.inv.shared.logged_out", new Object[0]);
        }
        if (value) {
            return I18N.getMsg("dc.mdm.enroll.dep.yes", new Object[0]);
        }
        return I18N.getMsg("dc.mdm.enroll.dep.no", new Object[0]);
    }
}
