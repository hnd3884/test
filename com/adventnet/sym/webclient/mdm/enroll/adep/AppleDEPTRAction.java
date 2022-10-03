package com.adventnet.sym.webclient.mdm.enroll.adep;

import com.me.mdm.server.adep.DEPEnrollmentUtil;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.client.components.table.web.DMSqlViewRetriever;
import com.adventnet.sym.webclient.mdm.enroll.StagedDeviceViewTRAction;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberSqlViewController;

public class AppleDEPTRAction extends MDMEmberSqlViewController
{
    private final Logger logger;
    
    public AppleDEPTRAction() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        final String viewName = viewCtx.getUniqueId();
        if (viewName.equalsIgnoreCase("depManagedDeviceCSVView")) {
            return this.getDepAssignUserCsvView(viewCtx, variableName);
        }
        return this.getDepDeviceView(viewCtx, variableName);
    }
    
    private String getDepDeviceView(final ViewContext viewCtx, final String variableName) {
        final HttpServletRequest request = viewCtx.getRequest();
        final Long custId = CustomerInfoUtil.getInstance().getCustomerId();
        final String selectedModel = request.getParameter("selectedModel");
        final String multiUser = request.getParameter("isMultiUser");
        StringBuilder associatedValue = new StringBuilder();
        associatedValue.append(super.getVariableValue(viewCtx, variableName));
        final StagedDeviceViewTRAction obj = new StagedDeviceViewTRAction();
        if (variableName.trim().startsWith("DBRANGECRITERIA")) {
            return DMSqlViewRetriever.checkAndGetDBRangeCriteriaAssociatedValue(variableName);
        }
        if (variableName.equals("CRITERIA1")) {
            associatedValue = new StringBuilder();
            try {
                associatedValue.append(this.getCustCriteria(custId)).append(obj.getInStockCriteria()).append(obj.getStatusFilterCriteria(viewCtx)).append(this.getModelCriteria(selectedModel, multiUser)).append(this.getTokenCriteria(viewCtx)).append(obj.setSearchCriteriaForLeftQuery(viewCtx));
                return associatedValue.toString();
            }
            catch (final Exception exp) {
                this.logger.log(Level.SEVERE, "Exception in AppleDEPTRAction: ", exp);
                return this.getCustCriteria(custId);
            }
        }
        if (variableName.equals("CRITERIA2")) {
            associatedValue = new StringBuilder();
            try {
                associatedValue.append(this.getCustCriteria(custId)).append(obj.setEnrolledCriteria()).append(obj.getStatusFilterCriteria(viewCtx)).append(this.getModelCriteria(selectedModel, multiUser)).append(this.getTokenCriteria(viewCtx)).append(obj.setSearchCriteriaForRightQuery(viewCtx));
            }
            catch (final Exception exp) {
                this.logger.log(Level.SEVERE, "Exception in AppleDEPTRAction: ", exp);
                return this.getCustCriteria(custId);
            }
        }
        return associatedValue.toString();
    }
    
    private String getDepAssignUserCsvView(final ViewContext viewCtx, final String variableName) {
        final Long custId = CustomerInfoUtil.getInstance().getCustomerId();
        String associatedValue = super.getVariableValue(viewCtx, variableName);
        if (variableName.equals("CRITERIA1")) {
            final String customerCrit = this.getCustCriteria(custId);
            try {
                associatedValue = customerCrit + " and (1=0)";
            }
            catch (final Exception exp) {
                this.logger.log(Level.SEVERE, "Exception in AppleDEPTRAction: ", exp);
                return customerCrit;
            }
        }
        else if (variableName.equals("CRITERIA2")) {
            final String customerCrit = this.getCustCriteria(custId);
            try {
                final StagedDeviceViewTRAction obj = new StagedDeviceViewTRAction();
                associatedValue = customerCrit + this.getTokenCriteria(viewCtx) + obj.setEnrolledCriteria();
            }
            catch (final Exception exp) {
                this.logger.log(Level.SEVERE, "Exception in AppleDEPTRAction: ", exp);
                return customerCrit;
            }
        }
        return associatedValue;
    }
    
    private String getCustCriteria(final Long custId) {
        return "(Resource.CUSTOMER_ID = " + String.valueOf(custId) + " or DeviceForEnrollment.CUSTOMER_ID = " + String.valueOf(custId) + ")";
    }
    
    private String getTokenCriteria(final ViewContext viewCtx) {
        String criteria = "";
        final HttpServletRequest request = viewCtx.getRequest();
        Long tokenID = null;
        if (request.getParameter("tokenID") != null) {
            tokenID = Long.parseLong(request.getParameter("tokenID"));
            criteria = " and (DEPTokenToGroup.DEP_TOKEN_ID = " + String.valueOf(tokenID) + " or AppleDEPDeviceForEnrollment.DEP_TOKEN_ID = " + String.valueOf(tokenID) + ")";
        }
        return criteria;
    }
    
    private String getModelCriteria(final String selectedModel, final String multiUser) {
        String criteria = "";
        if (!MDMStringUtils.isEmpty(selectedModel) && !selectedModel.equalsIgnoreCase("-1")) {
            final int deviceModel = Integer.parseInt(selectedModel);
            switch (deviceModel) {
                case 1: {
                    criteria = " and (MdModelInfo.MODEL_TYPE = 2";
                    break;
                }
                case 2: {
                    criteria = " and (MdModelInfo.MODEL_TYPE = 1";
                    break;
                }
                case 3: {
                    criteria = " and (MdModelInfo.MODEL_TYPE = 0";
                    break;
                }
                case 4: {
                    criteria = " and (MdModelInfo.MODEL_TYPE = 3 or MdModelInfo.MODEL_TYPE = 4";
                    break;
                }
                case 5: {
                    criteria = " and (MdModelInfo.MODEL_TYPE = 5";
                    break;
                }
            }
            criteria = criteria + " or AppleDEPDeviceForEnrollment.DEVICE_MODEL = " + deviceModel + ")";
            if (deviceModel == 1 && multiUser != null && !multiUser.equalsIgnoreCase("all")) {
                final Boolean isMultiUser = Boolean.parseBoolean(multiUser);
                criteria = criteria + " and (MdDeviceInfo.IS_MULTIUSER = '" + isMultiUser + "')";
            }
        }
        return criteria;
    }
    
    public void updateViewModel(final ViewContext context) throws Exception {
        super.updateViewModel(context);
        context.getRequest().setAttribute("IS_ASSIGN_USER_ENABLED", (Object)MDMRestAPIFactoryProvider.getEnrollmentFacade().isAssignUserEnabled(CustomerInfoUtil.getInstance().getCustomerId()));
        context.getRequest().setAttribute("IS_ASSIGN_USER_FOR_LAPTOP_ENABLED", (Object)MDMRestAPIFactoryProvider.getEnrollmentFacade().isAssignUserForLaptopEnabled(CustomerInfoUtil.getInstance().getCustomerId()));
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
        context.getRequest().setAttribute("DEP_TOKEN_SELF_ENROLL_MAP", (Object)DEPEnrollmentUtil.getSelfEnrollDetailForABMServers(customerId));
        context.getRequest().setAttribute("DEP_TOKEN_TYPE_MAP", (Object)DEPEnrollmentUtil.getTypeForDepTokens(customerId));
    }
}
