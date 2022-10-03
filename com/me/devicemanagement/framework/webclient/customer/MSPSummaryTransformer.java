package com.me.devicemanagement.framework.webclient.customer;

import com.adventnet.client.view.web.ViewContext;
import java.util.HashMap;
import java.util.logging.Level;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class MSPSummaryTransformer extends DefaultTransformer
{
    private Logger logger;
    
    public MSPSummaryTransformer() {
        this.logger = Logger.getLogger(MSPSummaryTransformer.class.getName());
    }
    
    public void renderCell(final TransformerContext tableContext) {
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final String columnalais = tableContext.getPropertyName();
            final ViewContext vc = tableContext.getViewContext();
            if (columnalais.equals("InvTechCompleteSummary.MANAGED_COMPUTER") || columnalais.equals("ManagedComputer.MANAGED_COMPUTER") || columnalais.equals("MANAGED_COMPUTERS_COUNT") || columnalais.equals("TOTAL_DEVICES") || columnalais.equals("TOTAL_APPS_INSTALLED")) {
                final String value = "" + columnProperties.get("VALUE");
                if (value == null || value.equalsIgnoreCase("null")) {
                    columnProperties.put("VALUE", "0");
                    columnProperties.put("LINK", null);
                }
                else if (value.equals("0")) {
                    columnProperties.put("LINK", null);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured while rendering cell value in MSPSummaryTransformer ", ex);
            final HashMap columnProperties2 = tableContext.getRenderedAttributes();
            columnProperties2.put("VALUE", "&nbsp;&nbsp;&nbsp;&nbsp;--");
        }
    }
}
