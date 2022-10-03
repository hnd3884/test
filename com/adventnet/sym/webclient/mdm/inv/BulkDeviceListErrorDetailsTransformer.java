package com.adventnet.sym.webclient.mdm.inv;

import java.util.HashMap;
import com.adventnet.i18n.I18N;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class BulkDeviceListErrorDetailsTransformer extends DefaultTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        try {
            return super.checkIfColumnRendererable(tableContext);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        try {
            super.renderCell(tableContext);
            if (tableContext.getPropertyName().equals("DeviceListImportInfo.ERROR_REMARKS")) {
                final HashMap columnProperties = tableContext.getRenderedAttributes();
                columnProperties.put("VALUE", I18N.getMsg((String)tableContext.getPropertyValue(), new Object[0]));
            }
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
}
