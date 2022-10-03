package com.adventnet.sym.server.mdm.gdpr;

import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import java.util.HashMap;
import org.json.JSONObject;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class ConsentViewTransformer extends DefaultTransformer
{
    private static Logger logger;
    
    public void renderCell(final TransformerContext tableContext) {
        ConsentViewTransformer.logger.log(Level.FINE, "Entering ConsentViewTransformer...");
        try {
            super.renderCell(tableContext);
            final String columnalais = tableContext.getPropertyName();
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            if (columnalais.equals("ConsentStatus.STATUS")) {
                final Integer val = (Integer)tableContext.getAssociatedPropertyValue("ConsentStatus.STATUS");
                columnProperties.put("VALUE", this.getHtmlCodeForStatus(val));
            }
            else if (columnalais.equals("EventLog.EVENT_ID")) {
                Integer val = (Integer)tableContext.getAssociatedPropertyValue("EventLog.EVENT_ID");
                val = ((val == 2201) ? 1 : 2);
                columnProperties.put("VALUE", this.getHtmlCodeForStatus(val));
            }
            else if (columnalais.equals("AaaUser.FIRST_NAME")) {
                String val2 = (String)tableContext.getAssociatedPropertyValue("AaaUser.FIRST_NAME");
                if (val2.equalsIgnoreCase("DC-SYSTEM-USER")) {
                    val2 = "--";
                }
                columnProperties.put("VALUE", val2);
            }
            else if (columnalais.equals("Consent.CONSENT_DESCRIPTION")) {
                final String val2 = (String)tableContext.getAssociatedPropertyValue("Consent.CONSENT_DESCRIPTION");
                final String i18n = I18N.getMsg(val2, new Object[0]);
                columnProperties.put("VALUE", i18n);
            }
            else if (columnalais.equals("Action")) {
                final Integer status = (Integer)tableContext.getAssociatedPropertyValue("ConsentStatus.STATUS");
                final String consentname = (String)tableContext.getAssociatedPropertyValue("Consent.CONSENT_DESCRIPTION");
                final Long consentid = (Long)tableContext.getAssociatedPropertyValue("Consent.CONSENT_ID");
                final JSONObject json = new JSONObject();
                json.put("consentid", (Object)consentid);
                json.put("consentstatus", (Object)status);
                json.put("consentname", (Object)I18N.getMsg(consentname, new Object[0]));
                json.put("consentgroupid", (Object)I18N.getMsg((String)tableContext.getAssociatedPropertyValue("ConsentGroupTable.CONSENT_GROUP_NAME"), new Object[0]));
                json.put("consentcategory", tableContext.getAssociatedPropertyValue("Consent.CONSENT_CATEGORY"));
                columnProperties.put("PAYLOAD", json);
            }
        }
        catch (final Exception e) {
            ConsentViewTransformer.logger.log(Level.SEVERE, "Error while rendering Cell for Consent View Transformer ", e);
        }
    }
    
    private String getHtmlCodeForStatus(final Integer val) {
        String value = "";
        try {
            if (val == 1) {
                final String i18n = I18N.getMsg("dc.inv.sw.ALLOWED", new Object[0]);
                value = "<div style='color:#83b93c;'><img id='MessageIcon' src='/images/tick_green.png' style='width: 13px;height: 13px;' align='absmiddle'/> " + DMIAMEncoder.encodeHTMLAttribute(i18n) + "</div>";
            }
            else {
                final String i18n = I18N.getMsg("mdm.privacy.denied", new Object[0]);
                value = "<div style='color:#ed262d;'><img id='MessageIcon' src='/images/suspend.png' style='width: 13px;height: 13px;' align='absmiddle' /> " + DMIAMEncoder.encodeHTMLAttribute(i18n) + "</div>";
            }
        }
        catch (final Exception e) {
            ConsentViewTransformer.logger.log(Level.SEVERE, "Error in getting I18N key ", e);
        }
        return value;
    }
    
    static {
        ConsentViewTransformer.logger = Logger.getLogger(ConsentViewTransformer.class.getName());
    }
}
