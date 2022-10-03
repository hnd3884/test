package com.adventnet.sym.webclient.mdm.enroll.adep;

import java.util.HashMap;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class BulkDEPErrorDetailsTransformer extends DefaultTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        try {
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            if (tableContext.getPropertyName().equals("BulkDEPImportInfo.SERIAL_NUMBER")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("BulkDEP_IsSerialNumberInCSV", (long)customerID));
            }
            if (tableContext.getPropertyName().equals("BulkDEPImportInfo.USER_NAME")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("BulkDEP_IsUserNameInCSV", (long)customerID));
            }
            if (tableContext.getPropertyName().equals("BulkDEPImportInfo.DOMAIN_NAME")) {
                return !CustomerInfoUtil.getInstance().isMSP() && Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("BulkDEP_IsDomainNameInCSV", (long)customerID));
            }
            if (tableContext.getPropertyName().equals("BulkDEPImportInfo.EMAIL_ADDRESS")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("BulkDEP_IsEmailInCSV", (long)customerID));
            }
            if (tableContext.getPropertyName().equals("BulkDEPImportInfo.GROUP_NAME")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("BulkDEP_IsGroupNameInCSV", (long)customerID));
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
            if (tableContext.getPropertyName().equals("BulkDEPImportInfo.ERROR_REMARKS")) {
                final HashMap columnProperties = tableContext.getRenderedAttributes();
                final String remarks_args = (String)tableContext.getAssociatedPropertyValue("BulkDEPImportInfo.ERROR_REMARKS_ARGS");
                columnProperties.put("VALUE", I18N.getMsg((String)tableContext.getPropertyValue(), new Object[] { remarks_args }));
            }
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
}
