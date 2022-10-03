package com.me.mdm.webclient.audit;

import java.util.HashMap;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.i18n.I18N;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.audit.EventViewerColumnTransformer;

public class MDMEventViewerColumnTransformer extends EventViewerColumnTransformer
{
    private Logger logger;
    
    public MDMEventViewerColumnTransformer() {
        this.logger = Logger.getLogger(MDMEventViewerColumnTransformer.class.getName());
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        return !columnalias.equalsIgnoreCase("EventCode.EVENT_MODULE_LABEL") && (columnalias.equalsIgnoreCase("EventCode.SUB_MODULE_LABEL") || super.checkIfColumnRendererable(tableContext));
    }
    
    public void renderCell(final TransformerContext tableContext) {
        try {
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            Object data = tableContext.getPropertyValue();
            final String columnalais = tableContext.getPropertyName();
            final String viewname = tableContext.getViewContext().getUniqueId();
            final int reportType = tableContext.getViewContext().getRenderType();
            if (columnalais.equals("EventCode.EVENT_TYPE")) {
                String value = I18N.getMsg("dc.common.NOT_AVAILABLE", new Object[0]);
                if (reportType != 4) {
                    if (data != null && data.equals(new Integer(1))) {
                        value = I18N.getMsg("desktopcentral.patch.editStoreLocation.ERROR_MSG", new Object[0]);
                    }
                    else if (data != null && data.equals(new Integer(2))) {
                        value = I18N.getMsg("dc.common.INFORMATION", new Object[0]);
                    }
                    else if (data != null && data.equals(new Integer(3))) {
                        value = I18N.getMsg("dc.common.WARNING", new Object[0]);
                    }
                    else {
                        value = I18N.getMsg("dc.common.NOT_AVAILABLE", new Object[0]);
                    }
                    columnProperties.put("VALUE", value);
                }
            }
            else if (columnalais.equals("EventCode.SUB_MODULE_LABEL")) {
                Object payloadData = data;
                final String module = (String)tableContext.getAssociatedPropertyValue("EventCode.EVENT_MODULE_LABEL");
                if (module.equalsIgnoreCase("dc.mdm.MDM")) {
                    payloadData = data;
                    data = I18N.getMsg((String)data, new Object[0]);
                    columnProperties.put("VALUE", data);
                }
                else {
                    payloadData = module;
                    data = I18N.getMsg(module, new Object[0]);
                    columnProperties.put("VALUE", data);
                }
                if (viewname.equalsIgnoreCase("MDMAllEventViewDash")) {
                    final JSONObject payload = new JSONObject();
                    payload.put("payloadData", payloadData);
                    columnProperties.put("PAYLOAD", payload);
                }
            }
            else {
                if (columnalais.equals("EventCode.EVENT_MODULE_LABEL") && viewname.equalsIgnoreCase("MDMAllEventViewDash") && viewname.equalsIgnoreCase("MDMAllEventView")) {
                    final String module = (String)data;
                    String imgName;
                    if (module.equals("dc.common.USER_MANAGEMENT")) {
                        imgName = "dUser.png";
                    }
                    else if (module.equals("dc.mdm.MDM")) {
                        imgName = "dMDM.png";
                    }
                    else {
                        imgName = "dInfo.png";
                    }
                    final String imgSrc = "<img src='/images/" + imgName + "' class='moduleImg'/>";
                    columnProperties.put("VALUE", imgSrc);
                    return;
                }
                super.renderCell(tableContext);
                if (columnalais.equals("EventLog.EVENT_REMARKS")) {
                    final String value = columnProperties.get("VALUE").toString();
                    final JSONObject payload2 = new JSONObject();
                    payload2.put("remarks", (Object)value);
                    columnProperties.put("PAYLOAD", payload2);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured while rendering cell value ", ex);
        }
    }
}
