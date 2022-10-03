package com.adventnet.sym.webclient.mdm.enroll.adep;

import java.util.HashMap;
import com.me.mdm.server.adep.ADEPServerSyncHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.util.Utils;
import org.apache.commons.lang.StringEscapeUtils;
import java.net.URLEncoder;
import org.json.JSONObject;
import com.me.mdm.core.enrollment.DEPAdminEnrollmentHandler;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class AppleDEPAccountsTransformer extends DefaultTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        try {
            final ViewContext viewCtx = tableContext.getViewContext();
            final HttpServletRequest request = viewCtx.getRequest();
            final String columnalias = tableContext.getPropertyName();
            final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
            if (columnalias.equals("Action")) {
                return isExport == null || !isExport.equalsIgnoreCase("true");
            }
            return super.checkIfColumnRendererable(tableContext);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final DEPAdminEnrollmentHandler handler = new DEPAdminEnrollmentHandler();
        final String columnalais = tableContext.getPropertyName();
        final Long customerID = (Long)tableContext.getAssociatedPropertyValue("DEPTokenDetails.CUSTOMER_ID");
        final Long tokenID = (Long)tableContext.getAssociatedPropertyValue("DEPTokenDetails.DEP_TOKEN_ID");
        final int reportType = tableContext.getViewContext().getRenderType();
        if (columnalais.equals("DEPAccountDetails.ORG_EMAIL")) {
            final String email = (String)tableContext.getAssociatedPropertyValue("DEPAccountDetails.ORG_EMAIL");
            columnProperties.put("VALUE", email);
        }
        if (columnalais.equals("Action")) {
            String actionStr = "";
            final JSONObject json = new JSONObject();
            json.put("CUSTOMER_ID", (Object)String.valueOf(customerID));
            json.put("TOKEN_ID", (Object)String.valueOf(tokenID));
            String serverName = (String)tableContext.getAssociatedPropertyValue("DEPAccountDetails.SERVER_NAME");
            serverName = ((serverName != null) ? URLEncoder.encode(serverName, "UTF-8") : serverName);
            json.put("serverName", (Object)serverName);
            actionStr = "<span style=\"white-space:nowrap;cursor: pointer;\" onclick=\"" + StringEscapeUtils.escapeHtml("DEPOpenActionList('" + json.toString() + "')") + "\"><img src=\"/images/action_dropdown.png\" width=\"20\" height=\"16\" hspace=\"3\" vspace=\"0\" align=\"absmiddle\"></span>";
            columnProperties.put("VALUE", actionStr);
        }
        if (columnalais.equals("DEPAccountDetails.SERVER_NAME")) {
            final String value;
            final String serverName2 = value = (String)tableContext.getAssociatedPropertyValue("DEPAccountDetails.SERVER_NAME");
            final JSONObject payload = new JSONObject();
            if (reportType != 4) {
                columnProperties.put("VALUE", value);
            }
            else {
                payload.put("tokenID", (Object)tokenID);
                payload.put("serverName", (Object)serverName2);
                columnProperties.put("PAYLOAD", payload);
            }
        }
        if (columnalais.equals("DEPTokenDetails.TOKEN_ADDED_TIME")) {
            final Long addedTime = (Long)tableContext.getAssociatedPropertyValue("DEPTokenDetails.TOKEN_ADDED_TIME");
            String value = "--";
            if (addedTime != -1L) {
                value = Utils.getEventTime(addedTime);
            }
            columnProperties.put("VALUE", value);
        }
        if (columnalais.equals("DEPTokenDetails.ACCESS_TOKEN_EXPIRY_DATE")) {
            final Long expiryTime = (Long)tableContext.getAssociatedPropertyValue("DEPTokenDetails.ACCESS_TOKEN_EXPIRY_DATE");
            String value = "--";
            final JSONObject payload = new JSONObject();
            if (expiryTime != -1L) {
                value = Utils.getEventTime(expiryTime);
            }
            payload.put("cellText", (Object)value);
            if (expiryTime <= MDMUtil.getCurrentTimeInMillis()) {
                payload.put("cellClass", (Object)"font-danger");
            }
            else if (expiryTime <= MDMUtil.getCurrentTimeInMillis() + 1296000000L) {
                payload.put("cellClass", (Object)"font-warning");
            }
            if (reportType != 4) {
                columnProperties.put("VALUE", value);
            }
            else {
                columnProperties.put("PAYLOAD", payload);
            }
        }
        if (columnalais.equals("Pending_Enrollment")) {
            final int unAssignedCount = handler.getUnenrolledDeviceCount(customerID, tokenID);
            final String value = String.valueOf(unAssignedCount);
            columnProperties.put("VALUE", value);
        }
        if (columnalais.equals("AppleDEPServerSyncStatus.LAST_SUCCESSFUL_SYNC_TIME")) {
            final JSONObject depSyncDetails = ADEPServerSyncHandler.getInstance(tokenID, customerID).getDEPServerSyncDetails();
            final String successSyncTime = (String)depSyncDetails.get("successSyncTimeString");
            columnProperties.put("VALUE", successSyncTime);
        }
        if (columnalais.equals("Enrolled")) {
            final int enrolledDEPDeviceCount = handler.getAdminEnrolledDeviceCount(customerID, tokenID);
            final String value = String.valueOf(enrolledDEPDeviceCount);
            columnProperties.put("VALUE", value);
        }
        if (columnalais.equals("Total_Devices")) {
            final int enrolledDEPDeviceCount = handler.getAdminEnrolledDeviceCount(customerID, tokenID);
            final int unAssignedCount2 = handler.getUnenrolledDeviceCount(customerID, tokenID);
            final int totalDevices = enrolledDEPDeviceCount + unAssignedCount2;
            final String value2 = String.valueOf(totalDevices);
            columnProperties.put("VALUE", value2);
        }
    }
}
