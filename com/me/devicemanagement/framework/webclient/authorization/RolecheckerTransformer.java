package com.me.devicemanagement.framework.webclient.authorization;

import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class RolecheckerTransformer extends DefaultTransformer
{
    private static Logger logger;
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final ViewContext vc = tableContext.getViewContext();
        final HttpServletRequest request = vc.getRequest();
        String selectedTab = "";
        selectedTab = SYMClientUtil.getInstance().getValueFromSessionOrRequest(request, "selectedTab") + "";
        selectedTab = ((selectedTab.equalsIgnoreCase("Reports") || selectedTab.equalsIgnoreCase("Report")) ? "Report" : selectedTab);
        final String columnalias = tableContext.getPropertyName();
        final String isExport = request.getParameter("isExport");
        if (isExport != null && isExport.equalsIgnoreCase("true")) {
            if (columnalias.equalsIgnoreCase("SWPackage.PACKAGE_ID") || columnalias.equalsIgnoreCase("checkbox_column") || columnalias.equalsIgnoreCase("checkbox") || columnalias.equalsIgnoreCase("icons_column") || columnalias.equalsIgnoreCase("Action") || columnalias.equalsIgnoreCase("action_column") || columnalias.equalsIgnoreCase("TaskDetails.TASK_ID") || columnalias.equalsIgnoreCase("CmptsAddedForShutdownTask.COMPUTER_ID") || columnalias.equalsIgnoreCase("collid")) {
                return false;
            }
            if (columnalias.equalsIgnoreCase("ach.remarks") || columnalias.equalsIgnoreCase("WakeOnNowResource.RESOURCE_ID") || columnalias.equalsIgnoreCase("Resource.RESOURCE_ID") || columnalias.equalsIgnoreCase("BranchOfficeDetails.BRANCH_OFFICE_ID") || columnalias.equalsIgnoreCase("Resource.IS_INACTIVE") || columnalias.equalsIgnoreCase("DCSelectedComputer.SELECTED_COMPUTER_ID") || columnalias.equalsIgnoreCase("CustomGroup.RESOURCE_ID") || columnalias.equalsIgnoreCase("CustomerInfo.CUSTOMER_ID") || columnalias.equalsIgnoreCase("BranchOffice_Download_Link") || columnalias.equalsIgnoreCase("CfgDataToCollection.COLLECTION_ID") || columnalias.equalsIgnoreCase("Collection.COLLECTION_ID") || columnalias.equalsIgnoreCase(" ") || columnalias.equalsIgnoreCase("SomAdCompAdded.SOM_AD_COMP_ID") || columnalias.equalsIgnoreCase("SomAdCompDeleted.RESOURCE_ID") || columnalias.equalsIgnoreCase("BranchOfficeBoundaryInfo.BOUNDARY_ID")) {
                return false;
            }
        }
        if (columnalias.equalsIgnoreCase("SWPackage.PACKAGE_ID") || columnalias.equalsIgnoreCase("checkbox_column") || columnalias.equalsIgnoreCase("checkbox") || columnalias.equalsIgnoreCase("icons_column") || columnalias.equalsIgnoreCase("Action") || columnalias.equalsIgnoreCase("action_column") || columnalias.equalsIgnoreCase("TaskDetails.TASK_ID") || columnalias.equalsIgnoreCase("CmptsAddedForShutdownTask.COMPUTER_ID") || columnalias.equalsIgnoreCase("collid") || columnalias.equalsIgnoreCase("ach.remarks") || columnalias.equalsIgnoreCase("WakeOnNowResource.RESOURCE_ID") || columnalias.equalsIgnoreCase("BranchOfficeDetails.AGENT_ARC_LOC") || columnalias.equalsIgnoreCase("BranchOfficeDetails.BRANCH_OFFICE_ID") || columnalias.equals("BranchOffice_Download_Link") || columnalias.equalsIgnoreCase("BranchOfficeBoundaryInfo.BOUNDARY_ID")) {
            boolean temp = request.isUserInRole(selectedTab + "_Write");
            if (selectedTab.equalsIgnoreCase("admin")) {
                temp = request.isUserInRole("CA_Write");
            }
            if (selectedTab.equalsIgnoreCase("inventory")) {
                temp = (request.isUserInRole("Inventory_Admin") || request.isUserInRole("CA_Write"));
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
            return temp;
        }
        return super.checkIfColumnRendererable(tableContext);
    }
    
    public void initCellRendering(final TransformerContext context) throws Exception {
        RolecheckerTransformer.logger.log(Level.FINE, "initCellRendering called");
        super.initCellRendering(context);
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        RolecheckerTransformer.logger.log(Level.FINE, "renderHeader called");
        super.renderHeader(tableContext);
    }
    
    static {
        RolecheckerTransformer.logger = Logger.getLogger(RolecheckerTransformer.class.getName());
    }
}
