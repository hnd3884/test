package com.me.mdm.webclient.config;

import java.util.HashMap;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class WebContentFilterUrlImportErrorDetailsTransformer extends DefaultTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        try {
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            if (tableContext.getPropertyName().equals("URLDetailsImportInfo.URL")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("url", (long)customerID));
            }
            if (tableContext.getPropertyName().equals("URLDetailsImportInfo.BOOKMARK_TITLE")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("bookmarkTitle", (long)customerID));
            }
            if (tableContext.getPropertyName().equals("URLDetailsImportInfo.BOOKMARK_PATH")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("bookmarkPath", (long)customerID));
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
            if (tableContext.getPropertyName().equals("URLDetailsImportInfo.ERROR_REMARKS")) {
                final HashMap columnProperties = tableContext.getRenderedAttributes();
                final String remarks_args = (String)tableContext.getAssociatedPropertyValue("URLDetailsImportInfo.ERROR_REMARKS");
                columnProperties.put("VALUE", I18N.getMsg((String)tableContext.getPropertyValue(), new Object[] { remarks_args }));
            }
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
}
