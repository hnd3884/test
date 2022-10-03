package com.adventnet.sym.webclient.mdm.inv;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import java.util.HashMap;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.authorization.RolecheckerTransformer;

public class CertificateListViewTransformer extends RolecheckerTransformer
{
    private Logger logger;
    
    public CertificateListViewTransformer() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        final String head = tableContext.getDisplayName();
        headerProperties.put("VALUE", head);
    }
    
    public void renderCell(final TransformerContext tableContext) {
        this.logger.log(Level.INFO, "Entering into {0}.renderCell", this.getClass().getName());
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final String data = tableContext.getPropertyValue() + "";
            if (data != null && !data.equals("--")) {
                columnProperties.put("VALUE", MDMUtil.getDate((long)new Long(data)));
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception Occurred in {0}.renderCell : {1}", new Object[] { this.getClass().getName(), ex.getMessage() });
        }
    }
}
