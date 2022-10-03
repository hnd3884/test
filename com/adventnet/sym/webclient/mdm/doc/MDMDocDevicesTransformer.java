package com.adventnet.sym.webclient.mdm.doc;

import java.text.DateFormat;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.web.TransformerContext;
import com.me.mdm.server.doc.DocMgmt;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class MDMDocDevicesTransformer extends DefaultTransformer
{
    private Logger logger;
    
    public MDMDocDevicesTransformer() {
        this.logger = DocMgmt.logger;
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final int reportType = tableContext.getViewContext().getRenderType();
        boolean export = false;
        if (reportType != 4) {
            export = true;
        }
        if (columnalias.equalsIgnoreCase("checkbox") || columnalias.equalsIgnoreCase("checkbox_column") || columnalias.equalsIgnoreCase("ManagedDevice.RESOURCE_ID")) {
            final ViewContext vc = tableContext.getViewContext();
            final HttpServletRequest request = vc.getRequest();
            final String isExport = request.getParameter("isExport");
            if (isExport != null && isExport.equalsIgnoreCase("true")) {
                return false;
            }
            if (export) {
                return false;
            }
        }
        if (columnalias.equalsIgnoreCase("ManagedDevice.RESOURCE_ID")) {
            final ViewContext vc = tableContext.getViewContext();
            final HttpServletRequest request = vc.getRequest();
            return request.isUserInRole("MDM_ContentMgmt_Write");
        }
        return super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) {
        this.logger.log(Level.FINE, "Entering DocDeviceTransformer...");
        try {
            final Object data = tableContext.getPropertyValue();
            final ViewContext vc = tableContext.getViewContext();
            final String columnalais = tableContext.getPropertyName();
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final String viewname = vc.getUniqueId();
            final int reportType = vc.getRenderType();
            final String isExportString = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
            boolean export = false;
            if (reportType != 4) {
                export = true;
            }
            if (reportType != 4 && columnalais.equals("ManagedDevice.PLATFORM_TYPE") && viewname.equalsIgnoreCase("mdmDocDevices")) {
                String platformName = I18N.getMsg("dc.common.UNKNOWN", new Object[0]);
                final Integer platformType = (Integer)data;
                if (platformType == 1) {
                    platformName = I18N.getMsg("dc.mdm.ios", new Object[0]);
                }
                else if (platformType == 2) {
                    platformName = I18N.getMsg("dc.mdm.android", new Object[0]);
                }
                else if (platformType == 3) {
                    platformName = I18N.getMsg("dc.common.WINDOWS", new Object[0]);
                }
                else if (platformType == 4) {
                    platformName = I18N.getMsg("mdm.common.chrome", new Object[0]);
                }
                columnProperties.put("VALUE", platformName);
            }
            if (columnalais.equals("ManagedDevice.PLATFORM_TYPE") && !export) {
                String platformName = "";
                final Integer platformType = (Integer)data;
                platformName = MDMUtil.getInstance().getPlatformColumnValue(platformType, isExportString);
                columnProperties.put("VALUE", platformName);
            }
            if (columnalais.equals("DocumentManagedDeviceInfo.AGENT_APPLIED_TIME")) {
                String value = "--";
                try {
                    final Date currentDate = new Date((long)data);
                    final DateFormat df = new SimpleDateFormat("MMM dd hh:mm a");
                    if (!data.toString().equals("-1") && data != null) {
                        value = df.format(currentDate);
                    }
                }
                catch (final Exception ex) {
                    this.logger.log(Level.WARNING, "Exception occoured in Agent applied time", ex);
                }
                columnProperties.put("VALUE", value);
            }
            if (columnalais.equals("DocumentManagedDeviceInfo.ASSOCIATED_AT")) {
                final Date currentDate2 = new Date((long)data);
                final DateFormat df2 = new SimpleDateFormat("MMM dd hh:mm a");
                columnProperties.put("VALUE", df2.format(currentDate2));
            }
            if (columnalais.equals("DocumentManagedDeviceInfo.STATUS_ID")) {
                final Integer statusId = (Integer)tableContext.getAssociatedPropertyValue("ConfigStatusDefn.STATUS_ID");
                final String statusLabel = (String)tableContext.getAssociatedPropertyValue("ConfigStatusDefn.StatusLabel");
                if (viewname.equalsIgnoreCase("mdmDocDevices")) {
                    if (!export) {
                        final JSONObject payload = new JSONObject();
                        payload.put("statusID", (Object)statusId.toString());
                        payload.put("statusLabel", (Object)I18N.getMsg(statusLabel, new Object[0]));
                        columnProperties.put("PAYLOAD", payload);
                    }
                    else {
                        columnProperties.put("VALUE", statusId);
                    }
                }
            }
            if (columnalais.equals("Remarks")) {
                final Integer statusId = (Integer)tableContext.getAssociatedPropertyValue("ConfigStatusDefn.STATUS_ID");
                final Integer platformType = (Integer)tableContext.getAssociatedPropertyValue("ManagedDevice.PLATFORM_TYPE");
                final Long deviceAgentVersion = (Long)tableContext.getAssociatedPropertyValue("ManagedDevice.AGENT_VERSION_CODE");
                final String deviceAgentVersionStr = (String)tableContext.getAssociatedPropertyValue("ManagedDevice.AGENT_VERSION");
                String remarkLabel = "--";
                if (statusId == 12) {
                    remarkLabel = I18N.getMsg("mdm.doc.app.device.remarks.document.notviewed", new Object[0]);
                    final String helpUrl = (platformType == 1) ? "$(mdmUrl)/how-to/automate-mdm-app-installation-ios-devices.html?$(traceurl)&pgSrc=$(pageSource)" : "$(mdmUrl)/help/app_management/windows_app_management.html#distribute_memdm";
                    final String content = MDMUtil.replaceProductUrlLoaderValuesinText(helpUrl, viewname);
                    if (deviceAgentVersion == -1L) {
                        if (platformType == 1 || (platformType == 3 && MDMStringUtils.isEmpty(deviceAgentVersionStr))) {
                            if (!export) {
                                remarkLabel = I18N.getMsg("mdm.doc.app.device.remarks.app_install", new Object[0]) + " " + I18N.getMsg("mdm.automate.insatallation", new Object[] { content });
                            }
                            else {
                                remarkLabel = I18N.getMsg("mdm.doc.app.device.remarks.app_install", new Object[0]);
                            }
                        }
                    }
                    else if (platformType == 1) {
                        if (deviceAgentVersion < 1416L) {
                            if (!export) {
                                remarkLabel = I18N.getMsg("mdm.doc.app.device.remarks.older_version", new Object[0]) + " " + I18N.getMsg("mdm.automate.updation", new Object[] { content });
                            }
                            else {
                                remarkLabel = I18N.getMsg("mdm.doc.app.device.remarks.older_version", new Object[0]);
                            }
                        }
                    }
                    else if (platformType == 2 && deviceAgentVersion < 2300244L) {
                        if (!export) {
                            remarkLabel = I18N.getMsg("mdm.doc.app.device.remarks.older_version", new Object[0]) + " " + I18N.getMsg("mdm.automate.updation", new Object[] { content });
                        }
                        else {
                            remarkLabel = I18N.getMsg("mdm.doc.app.device.remarks.older_version", new Object[0]);
                        }
                    }
                }
                else if (statusId == 200) {
                    remarkLabel = I18N.getMsg("mdm.doc.app.device.remarks.yet_to_update", new Object[0]);
                }
                else if (statusId == 6) {
                    remarkLabel = I18N.getMsg("mdm.doc.app.device.remarks.succeeded", new Object[0]);
                }
                if (export) {
                    columnProperties.put("VALUE", remarkLabel);
                }
                else {
                    final JSONObject payload2 = new JSONObject();
                    payload2.put("remarkLabel", (Object)remarkLabel);
                    columnProperties.put("PAYLOAD", payload2);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in renderCell", e);
        }
    }
}
