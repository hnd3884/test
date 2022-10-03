package com.me.mdm.webclient.admin;

import java.util.HashMap;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class EmberMSPSummaryTransformer extends DefaultTransformer
{
    private Logger logger;
    
    public EmberMSPSummaryTransformer() {
        this.logger = Logger.getLogger(EmberMSPSummaryTransformer.class.getName());
    }
    
    public void renderCell(final TransformerContext tableContext) {
        try {
            final int reportType = tableContext.getViewContext().getRenderType();
            final Object data = tableContext.getPropertyValue();
            boolean isExport = false;
            if (reportType != 4) {
                isExport = true;
            }
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final String columnalais = tableContext.getPropertyName();
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
            if (columnalais.equals("ManagedDevice.DEVICE_COUNT") || columnalais.equals("TOTAL_DEVICES")) {
                long managedDevices = 0L;
                if (columnProperties.get("VALUE") != null) {
                    managedDevices = Long.valueOf(columnProperties.get("VALUE").toString());
                }
                if (isExport) {
                    columnProperties.put("VALUE", "" + managedDevices);
                }
                else {
                    final Long allocatedDevices = Long.valueOf(tableContext.getAssociatedPropertyValue("DeviceLimitToCustomerMapping.NO_OF_DEVICES").toString());
                    String licenseStatus = "normal";
                    if (managedDevices > 0L && allocatedDevices != 0L) {
                        final Long diffPercent = managedDevices * 100L / allocatedDevices;
                        if (diffPercent >= 75L && diffPercent < 100L) {
                            licenseStatus = "about_to_reach";
                        }
                        else if (diffPercent == 100L) {
                            licenseStatus = "reached_limit";
                        }
                    }
                    final Long customerId = (Long)tableContext.getAssociatedPropertyValue("CustomerInfo.CUSTOMER_ID");
                    final JSONObject json = new JSONObject();
                    json.put("viewContent", (Object)"devices");
                    json.put("managed_devices_count", (Object)String.valueOf(managedDevices));
                    json.put("license_status", (Object)licenseStatus);
                    json.put("customer_id", (Object)customerId.toString());
                    columnProperties.put("PAYLOAD", json);
                }
            }
            if (columnalais.equals("TOTAL_APPS_INSTALLED")) {
                long appCount = 0L;
                if (data != null) {
                    appCount = Long.valueOf(data.toString());
                }
                if (isExport) {
                    columnProperties.put("VALUE", "" + appCount);
                }
                else {
                    final Long customerId2 = (Long)tableContext.getAssociatedPropertyValue("CustomerInfo.CUSTOMER_ID");
                    final JSONObject json2 = new JSONObject();
                    json2.put("viewContent", (Object)"apps");
                    json2.put("displayValue", (Object)String.valueOf(appCount));
                    json2.put("customer_id", (Object)customerId2.toString());
                    columnProperties.put("PAYLOAD", json2);
                }
            }
            if (columnalais.equals("ManagedDevice.TOTAL_APPS_INSTALLED")) {
                long appCount = 0L;
                if (data != null) {
                    appCount = Long.valueOf(data.toString());
                }
                if (isExport) {
                    columnProperties.put("VALUE", "" + appCount);
                }
                else {
                    final Long customerId2 = (Long)tableContext.getAssociatedPropertyValue("CustomerInfo.CUSTOMER_ID");
                    final JSONObject json2 = new JSONObject();
                    json2.put("viewContent", (Object)"apps");
                    json2.put("displayValue", (Object)String.valueOf(appCount));
                    json2.put("customer_id", (Object)customerId2.toString());
                    columnProperties.put("PAYLOAD", json2);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured while rendering cell value in MDM EmberMSPSummaryTransformer ", ex);
            final HashMap columnProperties2 = tableContext.getRenderedAttributes();
            columnProperties2.put("VALUE", "&nbsp;&nbsp;&nbsp;&nbsp;--");
        }
    }
}
