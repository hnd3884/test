package com.me.webclient.admin;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import java.util.HashMap;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.util.Utils;
import java.util.Hashtable;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.authentication.UserAdministrationTransformer;

public class MDMPUserAdministrationTransformer extends UserAdministrationTransformer
{
    private static Logger logger;
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) {
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final String displayColumn = tableContext.getPropertyName();
            final ViewContext viewContext = tableContext.getViewContext();
            final HttpServletRequest srvRequest = viewContext.getRequest();
            if (displayColumn.equalsIgnoreCase("LastLogonTime")) {
                final Hashtable<String, String> userLogonDetail = (Hashtable<String, String>)srvRequest.getAttribute("UserLastLogon");
                final String userName = (String)tableContext.getAssociatedPropertyValue("AaaUser.FIRST_NAME");
                final String value = userLogonDetail.get(userName);
                columnProperties.put("VALUE", (value == null) ? "--" : Utils.getEventTime(Long.valueOf(value)));
            }
            if (displayColumn.equals("AaaLogin.DOMAINNAME")) {
                final String value2 = columnProperties.get("VALUE").toString();
                columnProperties.put("VALUE", value2.equalsIgnoreCase("-") ? "Local" : value2);
            }
            else if (displayColumn.equalsIgnoreCase("UMRole.UM_ROLE_DESCRIPTION")) {
                final String value2 = I18N.getMsg((String)tableContext.getPropertyValue(), new Object[0]);
                columnProperties.put("VALUE", value2);
            }
        }
        catch (final Exception e) {
            MDMPUserAdministrationTransformer.logger.log(Level.SEVERE, "Exception in renderCell while viewing user administration view", e);
        }
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        return super.checkIfColumnRendererable(tableContext);
    }
    
    static {
        MDMPUserAdministrationTransformer.logger = Logger.getLogger("MDMPUserAdministrationTransformer");
    }
}
