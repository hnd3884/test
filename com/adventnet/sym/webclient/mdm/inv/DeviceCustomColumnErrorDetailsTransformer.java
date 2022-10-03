package com.adventnet.sym.webclient.mdm.inv;

import java.util.HashMap;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.adventnet.i18n.I18N;
import java.util.Set;
import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.inv.MDCustomDetailsCSVProcessor;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class DeviceCustomColumnErrorDetailsTransformer extends DefaultTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        try {
            final JSONObject columnList = new MDCustomDetailsCSVProcessor().getCustomColumnDetails();
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            final Set<String> keySet = columnList.keySet();
            final String columnName = tableContext.getPropertyName().split("MDCustomDetailsImportInfo.", 0)[1];
            String showColumn = null;
            if (tableContext.getPropertyName().equals("MDCustomDetailsImportInfo.IMEI")) {
                showColumn = CustomerParamsHandler.getInstance().getParameterValue("IsIMEIInCSV", (long)customerID);
                return showColumn != null && !showColumn.isEmpty() && Boolean.parseBoolean(showColumn);
            }
            if (tableContext.getPropertyName().equals("MDCustomDetailsImportInfo.SERIAL_NUMBER")) {
                showColumn = CustomerParamsHandler.getInstance().getParameterValue("isSerialNoInCSV", (long)customerID);
                return showColumn != null && !showColumn.isEmpty() && Boolean.parseBoolean(showColumn);
            }
            if (tableContext.getPropertyName().equals("MDCustomDetailsImportInfo.DEVICE_NAME")) {
                showColumn = CustomerParamsHandler.getInstance().getParameterValue("isDeviceNameInCSV", (long)customerID);
                return showColumn != null && !showColumn.isEmpty() && Boolean.parseBoolean(showColumn);
            }
            if (keySet.contains(columnName)) {
                final String paramName = (String)((JSONObject)columnList.get((Object)columnName)).get((Object)"CUSTOMER_PARAM_NAME");
                showColumn = CustomerParamsHandler.getInstance().getParameterValue(paramName, (long)customerID);
                return showColumn != null && !showColumn.isEmpty() && Boolean.parseBoolean(showColumn);
            }
            return super.checkIfColumnRendererable(tableContext);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            if (tableContext.getPropertyName().equals("MDCustomDetailsImportInfo.ERROR_REMARKS")) {
                columnProperties.put("VALUE", I18N.getMsg((String)tableContext.getPropertyValue(), (Object[])null));
            }
            if (tableContext.getPropertyName().equalsIgnoreCase("ManagedDeviceExtn.PURCHASE_DATE") || tableContext.getPropertyName().equalsIgnoreCase("ManagedDeviceExtn.WARRANTY_EXPIRATION_DATE")) {
                String value = "";
                final String data = (String)tableContext.getPropertyValue();
                if (data != null) {
                    final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    value = sdf.format(new Date(Long.parseLong(value)));
                }
                columnProperties.put("VALUE", value);
            }
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
}
