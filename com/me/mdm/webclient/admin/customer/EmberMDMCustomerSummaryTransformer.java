package com.me.mdm.webclient.admin.customer;

import java.util.HashMap;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class EmberMDMCustomerSummaryTransformer extends DefaultTransformer
{
    protected static Logger logger;
    
    public void renderCell(final TransformerContext tableContext) {
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final Object data = tableContext.getPropertyValue();
            final String columnalais = tableContext.getPropertyName();
            final int reportType = tableContext.getViewContext().getRenderType();
            boolean isExport = false;
            if (reportType != 4) {
                isExport = true;
            }
            if (columnalais.equals("CustomerInfo.CUSTOMER_NAME")) {
                if (isExport) {
                    columnProperties.put("VALUE", data);
                }
                else {
                    final Long customerId = (Long)tableContext.getAssociatedPropertyValue("CustomerInfo.CUSTOMER_ID");
                    final JSONObject json = new JSONObject();
                    json.put("displayValue", data);
                    json.put("viewContent", (Object)"home");
                    json.put("customer_id", (Object)customerId.toString());
                    columnProperties.put("PAYLOAD", json);
                }
            }
            if (columnalais.equals("CustomerInfo.CUSTOMER_ID")) {
                if (isExport) {
                    final String customerName = (String)tableContext.getAssociatedPropertyValue("CustomerInfo.CUSTOMER_NAME");
                    columnProperties.put("VALUE", customerName);
                }
                else {
                    final Long customerID = (Long)tableContext.getAssociatedPropertyValue("CustomerInfo.CUSTOMER_ID");
                    final String customerName2 = (String)tableContext.getAssociatedPropertyValue("CustomerInfo.CUSTOMER_NAME");
                    Long deviceCount = 0L;
                    if (tableContext.getAssociatedPropertyValue("ManagedDevice.DEVICE_COUNT") != null) {
                        deviceCount = Long.valueOf(tableContext.getAssociatedPropertyValue("ManagedDevice.DEVICE_COUNT").toString());
                    }
                    final JSONObject json2 = new JSONObject();
                    json2.put("customer_id", (Object)customerID.toString());
                    json2.put("customer_name", (Object)customerName2);
                    json2.put("device_count", (Object)deviceCount.toString());
                    columnProperties.put("PAYLOAD", json2);
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            EmberMDMCustomerSummaryTransformer.logger.log(Level.SEVERE, "Exception while creating payload for Customer Table");
        }
    }
    
    static {
        EmberMDMCustomerSummaryTransformer.logger = Logger.getLogger("MDMApiLogger");
    }
}
