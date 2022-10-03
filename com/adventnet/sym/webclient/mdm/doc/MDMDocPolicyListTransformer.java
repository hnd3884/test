package com.adventnet.sym.webclient.mdm.doc;

import java.util.HashMap;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.web.TransformerContext;
import com.me.mdm.server.doc.DocMgmt;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class MDMDocPolicyListTransformer extends DefaultTransformer
{
    private Logger logger;
    
    public MDMDocPolicyListTransformer() {
        this.logger = DocMgmt.logger;
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        if (columnalias.equalsIgnoreCase("DeploymentConfig.DEPLOYMENT_CONFIG_ID")) {
            final ViewContext vc = tableContext.getViewContext();
            final HttpServletRequest request = vc.getRequest();
            final String isExport = request.getParameter("isExport");
            final int reportType = tableContext.getViewContext().getRenderType();
            boolean export = false;
            if (reportType != 4) {
                export = true;
            }
            if (export || (isExport != null && isExport.equalsIgnoreCase("true"))) {
                return false;
            }
        }
        if (columnalias.equalsIgnoreCase("DeploymentConfig.DEPLOYMENT_CONFIG_ID")) {
            final ViewContext vc = tableContext.getViewContext();
            final HttpServletRequest request = vc.getRequest();
            return request.isUserInRole("MDM_ContentMgmt_Admin");
        }
        return super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final int reportType = tableContext.getViewContext().getRenderType();
            final String columnalias = tableContext.getPropertyName();
            boolean export = false;
            if (reportType != 4) {
                export = true;
            }
            if (columnalias.equalsIgnoreCase("CMDeploymentPolicySummary.DOCS_COUNT")) {
                Integer docCount = (Integer)tableContext.getAssociatedPropertyValue("CMDeploymentPolicySummary.DOCS_COUNT");
                if (docCount == null) {
                    docCount = 0;
                }
                if (!export) {
                    final JSONObject payload = new JSONObject();
                    payload.put("sharedDocCount", (Object)docCount);
                    columnProperties.put("PAYLOAD", payload);
                }
                else {
                    columnProperties.put("VALUE", docCount);
                }
            }
            if (columnalias.equalsIgnoreCase("DeploymentConfig.DEPLOYMENT_CONFIG_DESCRIPTION")) {
                String description = (String)tableContext.getAssociatedPropertyValue("DeploymentConfig.DEPLOYMENT_CONFIG_DESCRIPTION");
                if (SyMUtil.isStringEmpty(description)) {
                    description = "--";
                }
                columnProperties.put("VALUE", description);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in renderCell", e);
        }
    }
}
