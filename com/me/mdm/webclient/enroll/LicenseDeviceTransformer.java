package com.me.mdm.webclient.enroll;

import java.util.HashMap;
import java.util.logging.Level;
import org.apache.commons.lang.StringEscapeUtils;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.EnrollmentRequestMigratedTransformer;

public class LicenseDeviceTransformer extends EnrollmentRequestMigratedTransformer
{
    public Logger logger;
    
    public LicenseDeviceTransformer() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public void renderCell(final TransformerContext tableContext) {
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final String columnalais = tableContext.getPropertyName();
            Object data = tableContext.getPropertyValue();
            if (columnalais.equals("ManagedDeviceExtn.NAME")) {
                final String deviceName = (String)data;
                if (deviceName == null) {
                    columnProperties.put("VALUE", "--");
                }
                else {
                    columnProperties.put("VALUE", deviceName);
                }
            }
            if (columnalais.equalsIgnoreCase("ManagedUser.EMAIL_ADDRESS")) {
                if (data == null || ((String)data).isEmpty()) {
                    data = "--";
                }
                else {
                    columnProperties.put("VALUE", StringEscapeUtils.escapeHtml((String)data));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured while rendering cell value in LicenseDeviceTransformer ", ex);
            final HashMap columnProperties2 = tableContext.getRenderedAttributes();
            columnProperties2.put("VALUE", "--");
        }
    }
}
