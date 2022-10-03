package com.me.devicemanagement.framework.webclient.authorization;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class NewViewRolecheckerTransformer extends DefaultTransformer
{
    private static Logger logger;
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final ViewContext vc = tableContext.getViewContext();
        final String viewName = vc.getUniqueId();
        final HttpServletRequest request = vc.getRequest();
        String selectedTab = "";
        CustomerInfoUtil.getInstance();
        if (CustomerInfoUtil.isSAS()) {
            return true;
        }
        try {
            selectedTab = (String)WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, "selectedTab");
        }
        catch (final Exception e) {
            NewViewRolecheckerTransformer.logger.log(Level.SEVERE, "Exception occurred : " + e);
        }
        if (selectedTab == null || selectedTab == "" || selectedTab == "null") {
            selectedTab = request.getAttribute("selectedTab") + "";
        }
        selectedTab = ((selectedTab.equalsIgnoreCase("Reports") || selectedTab.equalsIgnoreCase("Report")) ? "Report" : selectedTab);
        final String columnalias = tableContext.getPropertyName();
        final String isExport = request.getParameter("isExport");
        if (isExport != null && isExport.equalsIgnoreCase("true")) {
            final String a = "temp";
            if (columnalias.equalsIgnoreCase("Action") || columnalias.equalsIgnoreCase("checkbox_column")) {
                return false;
            }
        }
        if (columnalias.equalsIgnoreCase("Action")) {
            boolean temp = request.isUserInRole(selectedTab + "_Write");
            if (!temp) {
                temp = request.isUserInRole("Tool_RDS_Write");
            }
            if (!temp && viewName.equalsIgnoreCase("NetworkMCView")) {
                temp = request.isUserInRole("VulnerabilityMgmt_Write");
            }
            if (!temp && selectedTab.equalsIgnoreCase("Tools")) {
                String isPMTools = null;
                if (request.getAttribute("isPMTools") != null) {
                    isPMTools = (String)request.getAttribute("isPMTools");
                }
                else if (request.getParameter("isPMTools") != null) {
                    isPMTools = request.getParameter("isPMTools");
                }
                if (isPMTools != null && !isPMTools.equalsIgnoreCase("")) {
                    temp = request.isUserInRole("Tools_PM_Write");
                }
            }
            if (selectedTab.equalsIgnoreCase("Report") && !temp) {
                temp = request.isUserInRole("PatchMgmt_Write");
            }
            if (selectedTab.equalsIgnoreCase("Report") && !temp) {
                temp = request.isUserInRole("MDM_Report_Write");
            }
            if (selectedTab.equalsIgnoreCase("Report") && !temp) {
                temp = request.isUserInRole("Inventory_Write");
            }
            return temp;
        }
        return super.checkIfColumnRendererable(tableContext);
    }
    
    static {
        NewViewRolecheckerTransformer.logger = Logger.getLogger(RolecheckerTransformer.class.getName());
    }
}
