package com.me.mdm.webclient.admin.customer;

import java.util.HashMap;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import com.me.devicemanagement.framework.webclient.authorization.RolecheckerTransformer;

public class MDMCustomerSummaryTransformer extends RolecheckerTransformer
{
    public void renderCell(final TransformerContext tableContext) {
        try {
            super.renderCell(tableContext);
            final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
            final String viewName = tableContext.getViewContext().getUniqueId();
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final Object data = tableContext.getPropertyValue();
            final String columnalais = tableContext.getPropertyName();
            if (isExport == null && CustomerInfoUtil.getInstance().isMSP() && "CustomerInfo.CUSTOMER_NAME".equals(columnalais)) {
                columnProperties.put("VALUE", "<a href=\"javascript:viewCustomerDetails('" + tableContext.getAssociatedPropertyValue("CustomerInfo.CUSTOMER_ID") + "')\"+  >" + data + "</a>");
            }
            else {
                columnProperties.put("VALUE", data);
            }
            if (columnalais.equals("CustomerInfo.CUSTOMER_ID") && isExport != null && isExport.equalsIgnoreCase("true")) {
                final String customerName = (String)tableContext.getAssociatedPropertyValue("CustomerInfo.CUSTOMER_NAME");
                columnProperties.put("VALUE", customerName);
            }
            if (columnalais.equals("CustomerInfo.CUSTOMER_NAME") && (isExport == null || isExport.equalsIgnoreCase("false"))) {
                final String customerName = DMIAMEncoder.encodeHTML((String)tableContext.getAssociatedPropertyValue("CustomerInfo.CUSTOMER_NAME"));
                columnProperties.put("VALUE", customerName);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
