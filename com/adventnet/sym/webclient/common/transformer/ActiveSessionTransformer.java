package com.adventnet.sym.webclient.common.transformer;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Date;
import com.adventnet.i18n.I18N;
import com.adventnet.authentication.Credential;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class ActiveSessionTransformer extends DefaultTransformer
{
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final String displayColumn = tableContext.getPropertyName();
        String resultVal = "";
        if (displayColumn.equalsIgnoreCase("Action")) {
            final String isDemoMode = String.valueOf(ApiFactoryProvider.getDemoUtilAPI().isDemoMode());
            final HttpServletRequest request = tableContext.getViewContext().getRequest();
            final HttpSession session = request.getSession();
            final Credential credential = (Credential)session.getAttribute("com.adventnet.authentication.Credential");
            final Long currentSession = credential.getSessionId();
            final Long sessionId = Long.parseLong(tableContext.getAssociatedPropertyValue("AaaAccSession.SESSION_ID").toString());
            if (sessionId.equals(currentSession)) {
                resultVal = "<label>" + I18N.getMsg("desktopcentral.common.This_Session", new Object[0]) + "</label>";
            }
            else if (isDemoMode.equalsIgnoreCase("true")) {
                resultVal = "<label><img title='" + I18N.getMsg("dc.common.sign_out", new Object[0]) + "' src='images/delete_disabled_big.gif'/></label>";
            }
            else {
                resultVal = "<a href='javascript:deleteReport(\"" + sessionId.toString() + "\")'><img title='" + I18N.getMsg("dc.common.sign_out", new Object[0]) + "' src='images/delete.png'/></a>";
            }
            columnProperties.put("VALUE", resultVal);
        }
        if (displayColumn.equalsIgnoreCase("AaaAccSession.USER_HOST")) {
            final String location = tableContext.getAssociatedPropertyValue("AaaAccSession.USER_HOST").toString();
            final String ipCheckWorkAround = "::";
            if (location.equalsIgnoreCase(ipCheckWorkAround + "1")) {
                final String ipWorkAround = "127.0";
                final String ipWorkAround2 = "0";
                resultVal = ipWorkAround + "." + ipWorkAround2 + ".1";
            }
            else {
                resultVal = location;
            }
            columnProperties.put("VALUE", resultVal);
        }
        if (displayColumn.equalsIgnoreCase("Duration")) {
            final StringBuilder duration = new StringBuilder();
            final StringBuilder temp = new StringBuilder();
            final Long openTime = Long.parseLong(tableContext.getAssociatedPropertyValue("AaaAccSession.OPENTIME").toString());
            final Long closeTime = Long.parseLong(tableContext.getAssociatedPropertyValue("AaaAccSession.CLOSETIME").toString());
            resultVal = I18N.getMsg("dc.admin.fos.server_up", new Object[0]);
            if (closeTime > 0L) {
                final Long diff = new Date(closeTime).getTime() - new Date(openTime).getTime();
                final Long sec = diff / 1000L % 60L;
                final Long mins = diff / 60000L % 60L;
                final Long hrs = diff / 3600000L % 24L;
                final Long days = diff / 86400000L;
                temp.append((CharSequence)((days > 0L) ? duration.append(days + " days").append(" ") : ""));
                temp.append((CharSequence)((hrs > 0L) ? duration.append(hrs + " hrs").append(" ") : ""));
                temp.append((CharSequence)((mins > 0L) ? duration.append(mins + " min ").append(" ") : ""));
                temp.append((CharSequence)((sec > 0L) ? duration.append(sec + " sec") : ""));
                resultVal = duration.toString();
            }
            columnProperties.put("VALUE", resultVal);
        }
    }
}
