package com.adventnet.sym.webclient.mdm.group;

import java.util.HashMap;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class DeviceToCustomGroupImportErrorDetailsTransformer extends DefaultTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        try {
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            if (tableContext.getPropertyName().equals("DeviceToCustomGroupImportInfo.IMEI")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("DeviceToCustomGroupImport_IsIMEIInCSV", (long)customerID));
            }
            if (tableContext.getPropertyName().equals("DeviceToCustomGroupImportInfo.SERIAL_NUMBER")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("DeviceToCustomGroupImport_IsSerialNoInCSV", (long)customerID));
            }
            if (tableContext.getPropertyName().equals("DeviceToCustomGroupImportInfo.EMAIL_ADDRESS")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("DeviceToCustomGroupImport_IsEmailInCSV", (long)customerID));
            }
            if (tableContext.getPropertyName().equals("DeviceToCustomGroupImportInfo.GROUP_NAME")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("DeviceToCustomGroupImport_IsGroupNameInCSV", (long)customerID));
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
            if (tableContext.getPropertyName().equals("DeviceToCustomGroupImportInfo.ERROR_REMARKS")) {
                final HashMap columnProperties = tableContext.getRenderedAttributes();
                columnProperties.put("VALUE", I18N.getMsg((String)tableContext.getPropertyValue(), new Object[0]));
            }
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
}
