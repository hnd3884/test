package com.adventnet.sym.webclient.mdm.doc;

import java.util.HashMap;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.client.components.web.TransformerContext;
import com.me.mdm.server.doc.DocMgmt;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class MDMGroupDocumentTransformer extends DefaultTransformer
{
    private Logger logger;
    
    public MDMGroupDocumentTransformer() {
        this.logger = DocMgmt.logger;
    }
    
    public void renderCell(final TransformerContext tableContext) {
        try {
            final ViewContext vc = tableContext.getViewContext();
            final int reportType = vc.getRenderType();
            final boolean export = reportType != 4;
            final String columnalais = tableContext.getPropertyName();
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            if (columnalais.equals("DeploymentConfig.DEPLOYMENT_CONFIG_NAME")) {
                String policyStr = String.valueOf(tableContext.getPropertyValue());
                if (SyMUtil.isStringEmpty(policyStr)) {
                    policyStr = "--";
                }
                if (export) {
                    columnProperties.put("VALUE", policyStr);
                }
                else {
                    final JSONObject payload = new JSONObject();
                    payload.put("data", (Object)policyStr);
                    payload.put("hoverText", (Object)policyStr);
                    columnProperties.put("PAYLOAD", payload);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in renderCell", e);
        }
    }
}
