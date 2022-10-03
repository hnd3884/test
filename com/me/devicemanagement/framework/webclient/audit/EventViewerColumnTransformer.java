package com.me.devicemanagement.framework.webclient.audit;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import java.util.HashMap;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Date;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.authorization.RolecheckerTransformer;

public class EventViewerColumnTransformer extends RolecheckerTransformer
{
    private Logger logger;
    private static final int CSV = 5;
    
    public EventViewerColumnTransformer() {
        this.logger = Logger.getLogger(EventViewerColumnTransformer.class.getName());
    }
    
    @Override
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        return !columnalias.equalsIgnoreCase("EventCode.SUB_MODULE_LABEL") && (!columnalias.equalsIgnoreCase("EventLog.EVENT_SOURCE_HOSTNAME") || !CustomerInfoUtil.isSAS) && super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) {
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            Object data = tableContext.getPropertyValue();
            final String columnalais = tableContext.getPropertyName();
            final int reportType = tableContext.getViewContext().getRenderType();
            final String viewname = tableContext.getViewContext().getUniqueId();
            String value = I18N.getMsg("dc.common.NOT_AVAILABLE", new Object[0]);
            final ViewContext viewContext = tableContext.getViewContext();
            final HttpServletRequest request = viewContext.getRequest();
            if (columnalais.equals("EventCode.EVENT_TYPE")) {
                if (data != null && data.equals(new Integer(1))) {
                    value = I18N.getMsg("desktopcentral.patch.editStoreLocation.ERROR_MSG", new Object[0]);
                    if (reportType != 3 && reportType != 5) {
                        value = "<img src=\"/images/failureicon.gif\" width=\"16\" height=\"16\" align=\"middle\" alt=\"" + value + "\" title=\"" + value + "\">";
                    }
                }
                else if (data != null && data.equals(new Integer(2))) {
                    value = I18N.getMsg("dc.common.INFORMATION", new Object[0]);
                    if (reportType != 3 && reportType != 5) {
                        value = "<img src=\"/images/info.png\" width=\"16\" height=\"16\" align=\"middle\" alt=\"" + value + "\" title=\"" + value + "\">";
                    }
                }
                else if (data != null && data.equals(new Integer(3))) {
                    value = I18N.getMsg("dc.common.WARNING", new Object[0]);
                    if (reportType != 3 && reportType != 5) {
                        value = "<img src=\"/images/alert-small.gif\" width=\"16\" height=\"16\" align=\"middle\" alt=\"" + value + "\" title=\"" + value + "\">";
                    }
                }
                else {
                    value = I18N.getMsg("dc.common.NOT_AVAILABLE", new Object[0]);
                }
                columnProperties.put("VALUE", value);
            }
            else if (columnalais.equals("EventCode.EVENT_MODULE_LABEL")) {
                data = I18N.getMsg((String)data, new Object[0]);
                final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
                if (isMSP && data != null && "SoM".equals(data.toString())) {
                    final String somReplaceString = I18N.getMsg("dc.common.Agent", new Object[0]);
                    columnProperties.put("VALUE", somReplaceString);
                }
                columnProperties.put("VALUE", data);
            }
            if (columnalais.equalsIgnoreCase("EventLog.EVENT_REMARKS")) {
                final String eventModule = (String)tableContext.getAssociatedPropertyValue("EventCode.EVENT_MODULE");
                final String remarks = (String)tableContext.getAssociatedPropertyValue("EventLog.EVENT_REMARKS");
                final String remarksArgs = (String)tableContext.getAssociatedPropertyValue("EventLog.EVENT_REMARKS_ARGS");
                final String transformedRemarks = I18NUtil.transformRemarks(remarks, remarksArgs);
                final Object trimLengthValue = tableContext.getColumnConfigRow().getOriginalValue("TRIM_LENGTH");
                final int transformedRemarksLength = transformedRemarks.length();
                boolean bothAreGreater = false;
                if (trimLengthValue != null) {
                    bothAreGreater = (transformedRemarksLength > Integer.parseInt(trimLengthValue.toString()) && remarks.length() > Integer.parseInt(trimLengthValue.toString()));
                }
                final String isExport = request.getParameter("isExport");
                if (isExport == null || isExport.equalsIgnoreCase("false")) {
                    final String eventRemarksArgs = (String)tableContext.getAssociatedPropertyValue("EventLog.EVENT_REMARKS_ARGS");
                    String encodedRemarksArgs = null;
                    if (eventRemarksArgs != null) {
                        final String[] tempRemarksArgs = eventRemarksArgs.split("@@@");
                        encodedRemarksArgs = DMIAMEncoder.encodeHTML(tempRemarksArgs[0]);
                        for (int i = 1; i < tempRemarksArgs.length; ++i) {
                            encodedRemarksArgs = encodedRemarksArgs + "@@@" + DMIAMEncoder.encodeHTML(tempRemarksArgs[i]);
                        }
                    }
                    else {
                        encodedRemarksArgs = eventRemarksArgs;
                    }
                    final String encodedTransformedRemarks = I18NUtil.transformRemarks(remarks, encodedRemarksArgs);
                    if (trimLengthValue != null && bothAreGreater) {
                        final int trimLength = Integer.parseInt(trimLengthValue.toString());
                        final String trimmedValue = encodedTransformedRemarks.substring(0, trimLength).concat("...");
                        columnProperties.put("TRIMMED_VALUE", trimmedValue);
                        columnProperties.put("VALUE", "");
                        columnProperties.put("ACTUAL_VALUE", transformedRemarks);
                    }
                    else {
                        columnProperties.put("TRIMMED_VALUE", "");
                        columnProperties.put("VALUE", encodedTransformedRemarks);
                    }
                }
                else if (trimLengthValue != null && bothAreGreater) {
                    final int trimLength2 = Integer.parseInt(trimLengthValue.toString());
                    final String trimmedValue2 = transformedRemarks.substring(0, trimLength2).concat("...");
                    columnProperties.put("TRIMMED_VALUE", trimmedValue2);
                    columnProperties.put("VALUE", "");
                    columnProperties.put("ACTUAL_VALUE", transformedRemarks);
                }
                else {
                    columnProperties.put("TRIMMED_VALUE", "");
                    columnProperties.put("VALUE", transformedRemarks);
                }
            }
            else if (columnalais.equalsIgnoreCase("EventLog.EVENT_TIMESTAMP")) {
                value = "--";
                if (data != null) {
                    final Long time = (Long)data;
                    value = SyMUtil.getDateTimeString(new Date(time));
                }
                columnProperties.put("VALUE", value);
            }
            else if (columnalais.equalsIgnoreCase("EventLog.LOGON_USER_NAME")) {
                value = "--";
                if (data != null) {
                    value = (String)data;
                    if (value.indexOf(64) != -1 && CustomerInfoUtil.isSAS) {
                        value = ApiFactoryProvider.getAuthUtilAccessAPI().getUsernameFromEmail(value);
                    }
                }
                columnProperties.put("VALUE", value);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured while rendering cell value ", ex);
        }
    }
}
