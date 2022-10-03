package com.me.devicemanagement.framework.webclient.authentication;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.DataObject;
import java.util.HashMap;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import java.util.logging.Level;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class EmberUserAdministrationColumnTransformer extends DefaultTransformer
{
    private static Logger logger;
    
    public void renderCell(final TransformerContext tableContext) {
        EmberUserAdministrationColumnTransformer.logger.log(Level.FINE, "Entering UserAdministrationTransformer...");
        try {
            super.renderCell(tableContext);
            final String columnalais = tableContext.getPropertyName();
            EmberUserAdministrationColumnTransformer.logger.log(Level.FINE, "Columnalais : " + columnalais);
            final HttpServletRequest srvRequest = tableContext.getViewContext().getRequest();
            final String isExport = srvRequest.getParameter("isExport");
            final Locale locale = srvRequest.getLocale();
            final Long userID = SYMClientUtil.getCurrentlyLoggedInUserID(srvRequest);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            if (columnalais.equals("AaaUser.FIRST_NAME") && (isExport == null || isExport.equalsIgnoreCase("false"))) {
                final String appName = (String)tableContext.getAssociatedPropertyValue("DCAaaLogin.APPNAME");
                String userName;
                if (CustomerInfoUtil.getInstance().isMSP() && CustomerInfoUtil.isDC()) {
                    userName = (String)tableContext.getAssociatedPropertyValue("AaaLogin.NAME");
                }
                else {
                    userName = (String)tableContext.getPropertyValue();
                }
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("userName", (Object)userName);
                final boolean isSDPUser = appName != null;
                jsonObject.put("isSDPUser", isSDPUser);
                columnProperties.put("PAYLOAD", jsonObject);
                columnProperties.put("VALUE", userName);
            }
            else if (columnalais.equals("CUSTOMERINFO.CUSTOMER_NAME")) {
                final String roleName = (String)tableContext.getAssociatedPropertyValue("UMRole.UM_ROLE_NAME");
                if ("Administrator".endsWith(roleName)) {
                    final String i18n = I18NUtil.getString("desktopcentral.common.tabComponanat.all_customers", locale, userID);
                    columnProperties.put("TRIMMED_VALUE", "");
                    columnProperties.put("VALUE", i18n);
                }
            }
            else if (columnalais.equals("UMRole.UM_ROLE_DESCRIPTION")) {
                final String value = I18N.getMsg((String)tableContext.getPropertyValue(), new Object[0]);
                final JSONObject jsonObject2 = new JSONObject();
                jsonObject2.put("description", (Object)value);
                columnProperties.put("PAYLOAD", jsonObject2);
                columnProperties.put("VALUE", value);
            }
            if (columnalais.equals("AaaLogin.DOMAINNAME")) {
                final Object val = columnProperties.get("VALUE");
                final String value2 = I18N.getMsg(val + "", new Object[0]);
                columnProperties.put("VALUE", value2);
            }
            else if (columnalais.equals("AaaContactInfo.EMAILID") && (isExport == null || isExport.equalsIgnoreCase("false"))) {
                columnProperties.put("VALUE", tableContext.getPropertyValue());
            }
            if (columnalais.equals("Action")) {
                CustomerInfoUtil.getInstance();
                if (CustomerInfoUtil.isSASAndMSP()) {
                    final DataObject dataObject = tableContext.getColumnConfiguration();
                    final String emberProperties = "{\"tableCellView\":\"admin/global-settings/user-administration/users/view-components/menu-action-cell\"}";
                    dataObject.set("EmberColumnConfiguration", "PROPERTIES", (Object)emberProperties);
                    dataObject.getFirstValue("EmberColumnConfiguration", "PROPERTIES");
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String displyColumn = tableContext.getPropertyName();
        final HttpServletRequest request = tableContext.getViewContext().getRequest();
        final String isExport = request.getParameter("isExport");
        return (isExport == null || !isExport.equalsIgnoreCase("true") || !displyColumn.equals("Action")) && (!displyColumn.equals("DCSpiceworksUserMapping.SPICE_USER_NAME") || SyMUtil.getSyMParameter("isSpiceworksEnabled") == null || SyMUtil.getSyMParameter("isSpiceworksEnabled").equalsIgnoreCase("enabled")) && super.checkIfColumnRendererable(tableContext);
    }
    
    static {
        EmberUserAdministrationColumnTransformer.logger = Logger.getLogger("UserManagementLogger");
    }
}
