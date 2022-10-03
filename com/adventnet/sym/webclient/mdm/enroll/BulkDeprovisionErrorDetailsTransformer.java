package com.adventnet.sym.webclient.mdm.enroll;

import java.util.HashMap;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class BulkDeprovisionErrorDetailsTransformer extends DefaultTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        try {
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            if (tableContext.getPropertyName().equals("BulkDeprovisionImportInfo.UDID")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("IsUDIDInCSV", (long)customerID));
            }
            if (tableContext.getPropertyName().equals("BulkDeprovisionImportInfo.SERIAL_NUMBER")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("IsSerialNoInCSV", (long)customerID));
            }
            if (tableContext.getPropertyName().equals("BulkDeprovisionImportInfo.IMEI")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("IsIMEIInCSV", (long)customerID));
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
            if (tableContext.getPropertyName().equals("BulkDeprovisionImportInfo.ERROR_REMARKS")) {
                final HashMap columnProperties = tableContext.getRenderedAttributes();
                final String remarks_args = (String)tableContext.getAssociatedPropertyValue("BulkDeprovisionImportInfo.ERROR_REMARKS_ARGS");
                columnProperties.put("VALUE", I18N.getMsg((String)tableContext.getPropertyValue(), new Object[] { remarks_args }));
            }
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
}
