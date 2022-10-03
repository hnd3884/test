package com.adventnet.sym.webclient.common;

import java.util.HashMap;
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
    
    public void renderHeader(final TransformerContext tableContext) {
        DomainListColumnTransformer.out.log(Level.FINE, "Entered into DomainListColumnTransformer.renderHeader()");
        super.renderHeader(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) {
        DomainListColumnTransformer.out.log(Level.FINE, "Entered into DomainListColumnTransformer.renderCell()");
        try {
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
                domainName = protocal + "://" + hostName + ":" + port;
                columnProperties.put("VALUE", domainName);
            }
            if (I18N.getMsg("dc.common.STATUS", new Object[0]).equals(displyColumn)) {
                final Object data = tableContext.getPropertyValue();
                final Integer value2 = Integer.parseInt(data.toString());
                final String urlId = tableContext.getAssociatedPropertyValue("DCDomainExceptionList.URLID").toString();
                String statusValue = "<img src=\"/images/successful.gif\" width=\"16\" height=\"16\" hspace=\"3\" vspace=\"0\" align=\"absmiddle\" ><span class=\"bodytextbig\" name=\"object_list\" value=\"" + urlId + "\">" + I18N.getMsg("dc.common.SUCCESS", new Object[0]) + "</span>";
                if (value2 == 2) {
                    statusValue = "<img src=\"/images/error_icon.gif\" width=\"16\" height=\"16\" hspace=\"3\" vspace=\"0\" align=\"absmiddle\" ><span class=\"bodytextbig\" name=\"object_list\" value=\"" + urlId + "\">" + I18N.getMsg("dc.common.status.failed", new Object[0]) + "</span>";
                }
                else if (value2 == 3) {
                    statusValue = "<img src=\"/images/s_progressbar.gif\" width=\"16\" height=\"16\" hspace=\"3\" vspace=\"0\" align=\"absmiddle\" ><span class=\"bodytextbig\" name=\"object_list\" value=\"" + urlId + "\">" + I18N.getMsg("dc.db.config.status.in_progress", new Object[0]) + "</span>";
                }
                else if (value2 == 4) {
                    statusValue = "<img src=\"/images/readytodeploy.png\" width=\"16\" height=\"16\" hspace=\"3\" vspace=\"0\" align=\"absmiddle\" ><span class=\"bodytextbig\" name=\"object_list\" value=\"" + urlId + "\">" + I18N.getMsg("dc.common.status.yet_to_start", new Object[0]) + "</span>";
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
