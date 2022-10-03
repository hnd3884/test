package com.me.devicemanagement.onpremise.webclient.common;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import com.adventnet.authentication.util.AuthUtil;
import java.net.URL;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.authorization.NewViewRolecheckerTransformer;

public class DomainListColumnTransformer extends NewViewRolecheckerTransformer
{
    static String className;
    static Logger out;
    public static final int DOMAIN_ACCESS_FAILED = 2;
    public static final int DOMAIN_ACCESS_SUCCESS = 1;
    public static final int DOMAIN_ACCESS_PROGRESS = 3;
    public static final int DOMAIN_ACCESS_READY = 4;
    
    public void renderHeader(final TransformerContext tableContext) {
        DomainListColumnTransformer.out.log(Level.FINE, "Entered into DomainListColumnTransformer.renderHeader()");
        super.renderHeader(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) {
        DomainListColumnTransformer.out.log(Level.FINE, "Entered into DomainListColumnTransformer.renderCell()");
        try {
            final HttpServletRequest request = tableContext.getViewContext().getRequest();
            final String isExport = request.getParameter("isExport");
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final String displyColumn = tableContext.getDisplayName();
            final String columnAlias = tableContext.getPropertyName();
            if (I18N.getMsg("dc.common.DOMAIN", new Object[0]).equals(displyColumn)) {
                final Object data = tableContext.getPropertyValue();
                final String value = data.toString();
                final URL url = new URL(value);
                final String protocal = url.getProtocol();
                final String hostName = url.getHost();
                final String port = String.valueOf(url.getDefaultPort());
                String domainName = "";
                if (port.equals("80")) {
                    domainName = protocal + "://" + hostName;
                }
                else {
                    domainName = protocal + "://" + hostName + ":" + port;
                }
                columnProperties.put("VALUE", domainName);
            }
            if (I18N.getMsg("dc.common.REMARKS", new Object[0]).equals(displyColumn)) {
                final Object data = tableContext.getPropertyValue();
                String remarks = data.toString();
                final Integer status = Integer.parseInt(tableContext.getAssociatedPropertyValue("DCDomainExceptionList.STATUS").toString());
                final String value2 = tableContext.getAssociatedPropertyValue("DCDomainExceptionList.URLDOMAIN").toString();
                final URL url2 = new URL(value2);
                final String protocal2 = url2.getProtocol();
                final String hostName2 = url2.getHost();
                final String port2 = String.valueOf(url2.getDefaultPort());
                String domainName2 = "";
                if (port2.equals("80")) {
                    domainName2 = hostName2;
                }
                else {
                    domainName2 = hostName2 + ":" + port2;
                }
                if (status == 2) {
                    final String key = "desktopcentral.patch.failed_domain_validation_msg";
                    final Long userID = AuthUtil.getUserCredential().getUserId();
                    remarks = I18NUtil.getMsg(userID, key, new Object[] { domainName2 });
                }
                columnProperties.put("VALUE", remarks);
            }
            if (I18N.getMsg("dc.common.STATUS", new Object[0]).equals(displyColumn)) {
                final boolean isEmberView = "EmberDCDomainExceptionListView".equals(tableContext.getViewContext().getUniqueId());
                final Object data2 = tableContext.getPropertyValue();
                final Integer value3 = Integer.parseInt(data2.toString());
                final String urlId = tableContext.getAssociatedPropertyValue("DCDomainExceptionList.URLID").toString();
                String statusValue = isEmberView ? I18N.getMsg("dc.common.SUCCESS", new Object[0]) : ("<img src=\"/images/successful.gif\" width=\"16\" height=\"16\" hspace=\"3\" vspace=\"0\" align=\"absmiddle\" ><span class=\"bodytextbig\" name=\"object_list\" value=\"" + urlId + "\">" + I18N.getMsg("dc.common.SUCCESS", new Object[0]) + "</span>");
                if (value3 == 2) {
                    statusValue = (isEmberView ? I18N.getMsg("dc.common.status.failed", new Object[0]) : ("<img src=\"/images/error_icon.gif\" width=\"16\" height=\"16\" hspace=\"3\" vspace=\"0\" align=\"absmiddle\" ><span class=\"bodytextbig\" name=\"object_list\" value=\"" + urlId + "\">" + I18N.getMsg("dc.common.status.failed", new Object[0]) + "</span>"));
                }
                else if (value3 == 3) {
                    statusValue = (isEmberView ? I18N.getMsg("dc.db.config.status.in_progress", new Object[0]) : ("<img src=\"/images/s_progressbar.gif\" width=\"16\" height=\"16\" hspace=\"3\" vspace=\"0\" align=\"absmiddle\" ><span class=\"bodytextbig\" name=\"object_list\" value=\"" + urlId + "\">" + I18N.getMsg("dc.db.config.status.in_progress", new Object[0]) + "</span>"));
                }
                else if (value3 == 4) {
                    statusValue = (isEmberView ? I18N.getMsg("dc.common.status.yet_to_start", new Object[0]) : ("<img src=\"/images/readytodeploy.png\" width=\"16\" height=\"16\" hspace=\"3\" vspace=\"0\" align=\"absmiddle\" ><span class=\"bodytextbig\" name=\"object_list\" value=\"" + urlId + "\">" + I18N.getMsg("dc.common.status.yet_to_start", new Object[0]) + "</span>"));
                }
                columnProperties.put("VALUE", statusValue);
            }
        }
        catch (final Exception ex) {
            DomainListColumnTransformer.out.log(Level.WARNING, "Exception occured while rendering cell value for Domains List view ", ex);
            final HashMap columnProperties2 = tableContext.getRenderedAttributes();
            columnProperties2.put("VALUE", "--");
        }
        DomainListColumnTransformer.out.log(Level.FINE, "Finished executing DomainListColumnTransformer.renderCell()");
    }
    
    static {
        DomainListColumnTransformer.className = DomainListColumnTransformer.class.getName();
        DomainListColumnTransformer.out = Logger.getLogger(DomainListColumnTransformer.className);
    }
}
