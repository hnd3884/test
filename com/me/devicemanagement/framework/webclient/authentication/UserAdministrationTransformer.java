package com.me.devicemanagement.framework.webclient.authentication;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.HashMap;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import java.util.logging.Level;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class UserAdministrationTransformer extends DefaultTransformer
{
    private static Logger logger;
    
    public void renderCell(final TransformerContext tableContext) {
        UserAdministrationTransformer.logger.log(Level.FINE, "Entering UserAdministrationTransformer...");
        try {
            super.renderCell(tableContext);
            final String columnalais = tableContext.getPropertyName();
            UserAdministrationTransformer.logger.log(Level.FINE, "Columnalais : " + columnalais);
            final HttpServletRequest srvRequest = tableContext.getViewContext().getRequest();
            final String isExport = srvRequest.getParameter("isExport");
            final Locale locale = srvRequest.getLocale();
            final Long userID = SYMClientUtil.getCurrentlyLoggedInUserID(srvRequest);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            if (columnalais.equals("AaaUser.FIRST_NAME") && (isExport == null || isExport.equalsIgnoreCase("false"))) {
                final String appName = (String)tableContext.getAssociatedPropertyValue("DCAaaLogin.APPNAME");
                if (appName != null) {
                    final String userName = (String)tableContext.getPropertyValue();
                    final String i18n = I18N.getMsg("desktopcentral.common.title.message.sdp.user_msg", new Object[0]);
                    final String value = "<img src=\"images/sdp_user.gif\" class=\"menuItemImage\" title=\"" + i18n + "\">&nbsp;" + DMIAMEncoder.encodeHTMLAttribute(userName);
                    columnProperties.put("VALUE", value);
                }
                else {
                    final String userName = DMIAMEncoder.encodeHTML((String)tableContext.getAssociatedPropertyValue("AaaUser.FIRST_NAME"));
                    columnProperties.put("VALUE", userName);
                }
            }
            else if (columnalais.equals("CUSTOMERINFO.CUSTOMER_NAME")) {
                final String roleName = (String)tableContext.getAssociatedPropertyValue("UMRole.UM_ROLE_NAME");
                if ("Administrator".endsWith(roleName)) {
                    final String i18n2 = I18NUtil.getString("desktopcentral.common.tabComponanat.all_customers", locale, userID);
                    columnProperties.put("TRIMMED_VALUE", "");
                    columnProperties.put("VALUE", i18n2);
                }
            }
            else if (columnalais.equals("UMRole.UM_ROLE_DESCRIPTION")) {
                final String value2 = I18N.getMsg((String)tableContext.getPropertyValue(), new Object[0]);
                columnProperties.put("VALUE", DMIAMEncoder.encodeHTMLAttribute(value2));
            }
            if (columnalais.equals("AaaLogin.DOMAINNAME")) {
                final Object val = columnProperties.get("VALUE");
                final String value3 = I18N.getMsg(val + "", new Object[0]);
                columnProperties.put("VALUE", value3);
            }
            else if (columnalais.equals("AaaContactInfo.EMAILID") && (isExport == null || isExport.equalsIgnoreCase("false"))) {
                final String value2 = DMIAMEncoder.encodeHTML((String)tableContext.getPropertyValue());
                columnProperties.put("VALUE", value2);
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
        UserAdministrationTransformer.logger = Logger.getLogger("UserManagementLogger");
    }
}
