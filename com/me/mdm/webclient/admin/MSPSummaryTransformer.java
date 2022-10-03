package com.me.mdm.webclient.admin;

import com.adventnet.client.view.web.ViewContext;
import java.util.HashMap;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
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
            final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final String columnalais = tableContext.getPropertyName();
            final ViewContext vc = tableContext.getViewContext();
            if (columnalais.equals("DeviceLimitToCustomerMapping.NO_OF_DEVICES")) {
                final String columnValue = "" + columnProperties.get("VALUE");
                final String licenseType = LicenseProvider.getInstance().getLicenseType();
                if (columnValue.equals("0")) {
                    if (licenseType.equals("F")) {
                        columnProperties.put("VALUE", "--");
                    }
                    else {
                        final String replaceValue = I18N.getMsg("dc.admin.common.no_limit", new Object[0]);
                        columnProperties.put("VALUE", replaceValue);
                    }
                }
            }
            if (columnalais.equals("ManagedDevice.RESOURCE_ID") || columnalais.equals("TOTAL_DEVICES") || columnalais.equals("ManagedDevice.DEVICE_COUNT")) {
                long managedDevices = 0L;
                if (columnProperties.get("VALUE") != null) {
                    managedDevices = Long.valueOf(columnProperties.get("VALUE").toString());
                }
                if (isExport != null && isExport.equalsIgnoreCase("true")) {
                    columnProperties.put("VALUE", "" + managedDevices);
                }
                else {
                    final Long allocatedDevices = Long.valueOf(tableContext.getAssociatedPropertyValue("DeviceLimitToCustomerMapping.NO_OF_DEVICES").toString());
                    String limitmoverOvertext = "";
                    if (managedDevices == 0L) {
                        columnProperties.put("VALUE", I18N.getMsg("mdm.enroll.azuread.getting_started_step2", new Object[0]));
                    }
                    else if (allocatedDevices != 0L) {
                        final Long diffPercent = managedDevices * 100L / allocatedDevices;
                        if (diffPercent >= 75L && diffPercent < 100L) {
                            limitmoverOvertext = I18N.getMsg("dc.mdm.msp.license_about_to_reach_tooltip", new Object[0]);
                            columnProperties.put("VALUE", "<div style=\"color:orange;\" onmouseover=\"return overlib('" + limitmoverOvertext + "', WIDTH , '150',FGCOLOR, '#faf8de' , BGCOLOR, '#d9ca66');\"  onmouseout=\"return nd()\">" + managedDevices + "</div>");
                        }
                        else if (diffPercent == 100L) {
                            limitmoverOvertext = I18N.getMsg("dc.mdm.msp.license_allocated_reached_tooltip", new Object[0]);
                            columnProperties.put("VALUE", "<div style=\"color:red;\" onmouseover=\"return overlib('" + limitmoverOvertext + "', WIDTH , '150',FGCOLOR, '#faf8de' , BGCOLOR, '#d9ca66');\"  onmouseout=\"return nd()\">" + managedDevices + "</div>");
                        }
                        else {
                            columnProperties.put("VALUE", "" + managedDevices);
                        }
                    }
                    else {
                        columnProperties.put("VALUE", "" + managedDevices);
                    }
                }
            }
            if (columnalais.equals("TOTAL_DEVICES") || columnalais.equals("ManagedDevice.RESOURCE_ID")) {
                final String value = "" + columnProperties.get("VALUE");
                if (value == null || value.equalsIgnoreCase("null")) {
                    columnProperties.put("VALUE", "0");
                    columnProperties.put("LINK", null);
                }
                else if (value.equals("0")) {
                    columnProperties.put("LINK", null);
                }
            }
            if (columnalais.equals("TOTAL_APPS_INSTALLED")) {
                final String data = "" + columnProperties.get("VALUE");
                String actionStr = "";
                if (data == null || data.equals("0") || data.equalsIgnoreCase("null")) {
                    actionStr = "0";
                }
                else {
                    actionStr = actionStr + "<a href=\"#/uems/mdm/inventory/apps\" class=\"bodytext\">" + data + "</a>";
                }
                columnProperties.put("VALUE", actionStr);
            }
            if (columnalais.equals("CustomerInfo.CUSTOMER_NAME")) {
                final String data = "" + columnProperties.get("VALUE");
                String actionStr = "";
                if (data == null || data.equals("0") || data.equalsIgnoreCase("null")) {
                    actionStr = "0";
                }
                else {
                    actionStr = actionStr + "<a href=\"#/uems/mdm/home\" class=\"bodytext\">" + data + "</a>";
                }
                columnProperties.put("VALUE", actionStr);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured while rendering cell value in MDM MSPSummaryTransformer ", ex);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            columnProperties.put("VALUE", "&nbsp;&nbsp;&nbsp;&nbsp;--");
        }
    }
}
