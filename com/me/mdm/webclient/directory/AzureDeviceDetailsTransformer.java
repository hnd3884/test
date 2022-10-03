package com.me.mdm.webclient.directory;

import java.util.HashMap;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.util.Utils;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class AzureDeviceDetailsTransformer extends DefaultTransformer
{
    private static Logger logger;
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) {
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final String columnalais = tableContext.getPropertyName();
            final Object data = tableContext.getPropertyValue();
            final int reportType = tableContext.getViewContext().getRenderType();
            final String deviceOS = (String)tableContext.getAssociatedPropertyValue("DIROBJREGSTRVAL_DEVICE_OS_TYPE_VALUE");
            if (columnalais.equals("DIROBJREGSTRVAL_LAST_LOGON_VALUE")) {
                final Long loginTime = Long.valueOf((String)data);
                String value = "--";
                if (loginTime != null) {
                    value = Utils.getEventTime(loginTime);
                }
                columnProperties.put("VALUE", value);
            }
            if (columnalais.equals("DIROBJREGSTRVAL_DEVICE_STATUS_VALUE")) {
                final int status = (int)data;
                if (data != null) {
                    if (status == 3) {
                        columnProperties.put("VALUE", I18N.getMsg("mdm.azure.complaint", new Object[0]));
                    }
                    else if (status >= 0 && status < 3) {
                        columnProperties.put("VALUE", I18N.getMsg("mdm.azure.not.complaint", new Object[0]));
                    }
                    else {
                        columnProperties.put("VALUE", "--");
                    }
                }
            }
            if (columnalais.equals("ManagedDevice.MANAGED_STATUS")) {
                String status2 = I18N.getMsg("mdm.cea.managed.status.not.enrolled", new Object[0]);
                if (data != null) {
                    if ((int)data == 2) {
                        status2 = I18N.getMsg("dc.mdm.enrolled", new Object[0]);
                    }
                }
                else if (!deviceOS.equalsIgnoreCase("windows")) {
                    status2 = I18N.getMsg("mdm.agent.efrp.not_applicable", new Object[0]);
                }
                columnProperties.put("VALUE", status2);
            }
        }
        catch (final Exception e) {
            AzureDeviceDetailsTransformer.logger.log(Level.WARNING, "Exception occoured in renderCell", e);
        }
    }
    
    static {
        AzureDeviceDetailsTransformer.logger = Logger.getLogger(AzureDeviceDetailsTransformer.class.getName());
    }
}
