package com.adventnet.sym.webclient.mdm.enroll;

import com.me.mdm.server.adep.DEPEnrollmentUtil;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberSqlViewController;

public class StagedDeviceViewTRAction extends MDMEmberSqlViewController
{
    public Logger logger;
    public static final int MODEL_TYPE_LAPTOP_OR_DESKTOP = 6;
    
    public StagedDeviceViewTRAction() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        viewCtx.getRequest().setAttribute("stagedviewtoolID", (Object)"40081");
        String selectedTab = viewCtx.getRequest().getParameter("selectedTab");
        selectedTab = ((selectedTab != null) ? selectedTab : "staged");
        viewCtx.getRequest().setAttribute("selectedTabInput", (Object)selectedTab);
        final Boolean isDeviceProvisioningUser = ManagedDeviceHandler.getInstance().isDeviceProvisioningUser();
        viewCtx.getRequest().setAttribute("isDeviceProvisioningUser", (Object)isDeviceProvisioningUser);
        String associatedValue = super.getVariableValue(viewCtx, variableName);
        if (variableName.equals("CRITERIA1")) {
            final Long custId = CustomerInfoUtil.getInstance().getCustomerId();
            final String customerCrit = "(Resource.CUSTOMER_ID = " + String.valueOf(custId) + " or DeviceForEnrollment.CUSTOMER_ID = " + String.valueOf(custId) + ")";
            try {
                associatedValue = customerCrit + this.getStatusFilterCriteria(viewCtx) + this.getPlatformFilterCriteria(viewCtx) + this.getTemplateTypeFilterCriteria(viewCtx) + this.getModelTypeCriteria(viewCtx) + this.getInStockCriteria() + this.setSearchCriteriaForLeftQuery(viewCtx);
            }
            catch (final SyMException exp) {
                this.logger.log(Level.SEVERE, "Search text not matching safestring: ", (Throwable)exp);
                return customerCrit;
            }
        }
        if (variableName.equals("CRITERIA2")) {
            final Long custId = CustomerInfoUtil.getInstance().getCustomerId();
            final String customerCrit = "(Resource.CUSTOMER_ID = " + String.valueOf(custId) + " or DeviceForEnrollment.CUSTOMER_ID = " + String.valueOf(custId) + ")";
            try {
                associatedValue = customerCrit + this.getStatusFilterCriteria(viewCtx) + this.getPlatformFilterCriteria(viewCtx) + this.getTemplateTypeFilterCriteria(viewCtx) + this.getModelTypeCriteria(viewCtx) + this.setSearchCriteriaForRightQuery(viewCtx) + this.setModernMgmtCriteria(viewCtx) + this.setEnrolledCriteria();
            }
            catch (final SyMException exp) {
                this.logger.log(Level.SEVERE, "Search text not matching safestring: ", (Throwable)exp);
                return customerCrit;
            }
        }
        return associatedValue;
    }
    
    private String getDeprovisionCommentCriteria() {
        DMDataSetWrapper ds = null;
        String maxDeprovisionTime = "(";
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeprovisionHistory"));
            selectQuery.addSelectColumn(Column.getColumn("DeprovisionHistory", "RESOURCE_ID"));
            final Column latestDeprovisionTime = Column.getColumn("DeprovisionHistory", "DEPROVISION_TIME").maximum();
            latestDeprovisionTime.setColumnAlias("LATEST_TIME");
            selectQuery.addSelectColumn(latestDeprovisionTime);
            selectQuery.addGroupByColumn(Column.getColumn("DeprovisionHistory", "RESOURCE_ID"));
            ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (ds.next()) {
                if (!maxDeprovisionTime.equals("(")) {
                    maxDeprovisionTime += "or ";
                }
                maxDeprovisionTime = maxDeprovisionTime + "ManagedDevice.RESOURCE_ID=" + ds.getValue("RESOURCE_ID") + " and DeprovisionHistory.DEPROVISION_TIME=" + ds.getValue("LATEST_TIME");
            }
            if (!maxDeprovisionTime.equals("(")) {
                maxDeprovisionTime += " or DeprovisionHistory.DEPROVISION_TIME is null)";
                return "and" + maxDeprovisionTime;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error in setting deprovision criteria", e);
        }
        return "";
    }
    
    public String setEnrolledCriteria() {
        return " and ((ManagedDevice.MANAGED_STATUS is null and ((DeviceEnrollmentRequest.REQUEST_STATUS is null or DeviceEnrollmentRequest.REQUEST_STATUS!=0) or (DeviceEnrollmentRequest.REQUEST_STATUS=0 and DeviceEnrollmentRequest.ENROLLMENT_TYPE in (3,4)))) or (ManagedDevice.MANAGED_STATUS in (5,6))) ";
    }
    
    public String getInStockCriteria() {
        return " and (ManagedDevice.MANAGED_STATUS=9 or ManagedDevice.MANAGED_STATUS=10)";
    }
    
    public String getStatusFilterCriteria(final ViewContext viewCtx) {
        String status = viewCtx.getRequest().getParameter("selectedStatus");
        viewCtx.getRequest().setAttribute("selectedStatus", (Object)status);
        status = ((status == null || status.equalsIgnoreCase("")) ? "-1" : status);
        switch (Integer.parseInt(status)) {
            case -1: {
                status = "";
                break;
            }
            case 12: {
                status = " and (DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID is null  and  DeviceEnrollmentToUser.MANAGED_USER_ID is null)";
                break;
            }
            case 9: {
                status = " and (ManagedDevice.MANAGED_STATUS=9)";
                break;
            }
            case 10: {
                status = " and (ManagedDevice.MANAGED_STATUS=10)";
                break;
            }
            case 5: {
                status = " and (ManagedDevice.MANAGED_STATUS=5)";
                break;
            }
            case 0: {
                status = " and (DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID is null and  DeviceEnrollmentToUser.MANAGED_USER_ID is not null)";
                break;
            }
        }
        return status;
    }
    
    private String getPlatformFilterCriteria(final ViewContext viewCtx) {
        String platform = viewCtx.getRequest().getParameter("platformType");
        platform = ((platform == null || platform.equalsIgnoreCase("")) ? "-1" : platform);
        viewCtx.getRequest().setAttribute("platformType", (Object)Integer.parseInt(platform));
        switch (Integer.parseInt(platform)) {
            case -1: {
                platform = "";
                break;
            }
            case 0: {
                platform = " and (DeviceEnrollmentRequest.PLATFORM_TYPE = 0)";
                break;
            }
            case 1: {
                platform = " and ((DeviceEnrollmentRequest.PLATFORM_TYPE = 1 and (MdModelInfo.MODEL_TYPE != 3 and MdModelInfo.MODEL_TYPE != 4)) or (AppleDEPDeviceForEnrollment.ENROLLMENT_DEVICE_ID is not null and AppleDEPDeviceForEnrollment.DEVICE_MODEL!=4) or (AppleConfigDeviceForEnrollment.ENROLLMENT_DEVICE_ID is not null))";
                break;
            }
            case 2: {
                platform = " and (DeviceEnrollmentRequest.PLATFORM_TYPE = 2 or AndroidNFCDeviceForEnrollment.ENROLLMENT_DEVICE_ID=DeviceForEnrollment.ENROLLMENT_DEVICE_ID or KNOXMobileDeviceForEnrollment.ENROLLMENT_DEVICE_ID is not null or AndroidQRDeviceForEnrollment.ENROLLMENT_DEVICE_ID=DeviceForEnrollment.ENROLLMENT_DEVICE_ID or AndroidZTDeviceForEnrollment.ENROLLMENT_DEVICE_ID=DeviceForEnrollment.ENROLLMENT_DEVICE_ID)";
                break;
            }
            case 3: {
                platform = " and (DeviceEnrollmentRequest.PLATFORM_TYPE = 3 or WinAzureADDeviceForEnrollment.ENROLLMENT_DEVICE_ID=DeviceForEnrollment.ENROLLMENT_DEVICE_ID or WindowsLaptopDeviceForEnrollment.ENROLLMENT_DEVICE_ID is not null or WinModernMgmtDeviceForEnrollment.ENROLLMENT_DEVICE_ID is not null or WindowsICDDeviceForEnrollment.ENROLLMENT_DEVICE_ID=DeviceForEnrollment.ENROLLMENT_DEVICE_ID)";
                break;
            }
            case 4: {
                platform = " and (DeviceEnrollmentRequest.PLATFORM_TYPE = 4 or GSChromeDeviceForEnrollment.ENROLLMENT_DEVICE_ID=DeviceForEnrollment.ENROLLMENT_DEVICE_ID)";
                break;
            }
            case 6: {
                platform = " and ((DeviceEnrollmentRequest.PLATFORM_TYPE = 1 and (MdModelInfo.MODEL_TYPE = 3 or MdModelInfo.MODEL_TYPE = 4)) or (AppleDEPDeviceForEnrollment.ENROLLMENT_DEVICE_ID is not null and AppleDEPDeviceForEnrollment.DEVICE_MODEL=4) or (MacModernMgmtDeviceForEnrollment.ENROLLMENT_DEVICE_ID is not null))";
                break;
            }
            case 7: {
                platform = " and ((DeviceEnrollmentRequest.PLATFORM_TYPE = 1 and (MdModelInfo.MODEL_TYPE = 5) or (AppleDEPDeviceForEnrollment.ENROLLMENT_DEVICE_ID is not null and AppleDEPDeviceForEnrollment.DEVICE_MODEL=5)))";
                break;
            }
        }
        return platform;
    }
    
    private String getTemplateTypeFilterCriteria(final ViewContext viewCtx) {
        String templateType = viewCtx.getRequest().getParameter("templateType");
        templateType = ((templateType == null || templateType.equalsIgnoreCase("")) ? "-1" : templateType);
        viewCtx.getRequest().setAttribute("templateType", (Object)Integer.parseInt(templateType));
        switch (Integer.parseInt(templateType)) {
            case 20: {
                templateType = " and (AndroidNFCDeviceForEnrollment.ENROLLMENT_DEVICE_ID=DeviceForEnrollment.ENROLLMENT_DEVICE_ID or EnrollmentTemplate.TEMPLATE_TYPE =" + templateType + ")";
                break;
            }
            case 11: {
                templateType = " and (AppleConfigDeviceForEnrollment.ENROLLMENT_DEVICE_ID=DeviceForEnrollment.ENROLLMENT_DEVICE_ID or EnrollmentTemplate.TEMPLATE_TYPE =" + templateType + ")";
                break;
            }
            case 21: {
                templateType = " and (KNOXMobileDeviceForEnrollment.ENROLLMENT_DEVICE_ID=DeviceForEnrollment.ENROLLMENT_DEVICE_ID or EnrollmentTemplate.TEMPLATE_TYPE =" + templateType + ")";
                break;
            }
            case 30: {
                templateType = " and (WindowsICDDeviceForEnrollment.ENROLLMENT_DEVICE_ID=DeviceForEnrollment.ENROLLMENT_DEVICE_ID or EnrollmentTemplate.TEMPLATE_TYPE =" + templateType + ")";
                break;
            }
            case 22: {
                templateType = " and (AndroidQRDeviceForEnrollment.ENROLLMENT_DEVICE_ID=DeviceForEnrollment.ENROLLMENT_DEVICE_ID or EnrollmentTemplate.TEMPLATE_TYPE =" + templateType + ")";
                break;
            }
            case 31: {
                templateType = " and (WindowsLaptopDeviceForEnrollment.ENROLLMENT_DEVICE_ID=DeviceForEnrollment.ENROLLMENT_DEVICE_ID or EnrollmentTemplate.TEMPLATE_TYPE =" + templateType + ")";
                break;
            }
            case 23: {
                templateType = " and (AndroidZTDeviceForEnrollment.ENROLLMENT_DEVICE_ID=DeviceForEnrollment.ENROLLMENT_DEVICE_ID or EnrollmentTemplate.TEMPLATE_TYPE =" + templateType + ")";
                break;
            }
            case 32: {
                templateType = " and (WinAzureADDeviceForEnrollment.ENROLLMENT_DEVICE_ID=DeviceForEnrollment.ENROLLMENT_DEVICE_ID or EnrollmentTemplate.TEMPLATE_TYPE =" + templateType + ")";
                break;
            }
            case 33: {
                templateType = " and (WinModernMgmtDeviceForEnrollment.ENROLLMENT_DEVICE_ID=DeviceForEnrollment.ENROLLMENT_DEVICE_ID or EnrollmentTemplate.TEMPLATE_TYPE =" + templateType + ")";
                break;
            }
            case 40: {
                templateType = " and (GSChromeDeviceForEnrollment.ENROLLMENT_DEVICE_ID=DeviceForEnrollment.ENROLLMENT_DEVICE_ID or EnrollmentTemplate.TEMPLATE_TYPE =" + templateType + ")";
                break;
            }
            case 10: {
                templateType = " and (AppleDEPDeviceForEnrollment.ENROLLMENT_DEVICE_ID=DeviceForEnrollment.ENROLLMENT_DEVICE_ID or EnrollmentTemplate.TEMPLATE_TYPE =" + templateType + ")";
                break;
            }
            case 12: {
                templateType = " and (MacModernMgmtDeviceForEnrollment.ENROLLMENT_DEVICE_ID=DeviceForEnrollment.ENROLLMENT_DEVICE_ID or EnrollmentTemplate.TEMPLATE_TYPE =" + templateType + ")";
                break;
            }
            case 50: {
                templateType = " and (MigrationDeviceForEnrollment.ENROLLMENT_DEVICE_ID=DeviceForEnrollment.ENROLLMENT_DEVICE_ID or EnrollmentTemplate.TEMPLATE_TYPE =" + templateType + ")";
                break;
            }
            default: {
                templateType = "";
                break;
            }
        }
        return templateType;
    }
    
    public String setSearchCriteriaForLeftQuery(final ViewContext viewCtx) throws SyMException {
        String criteria = "";
        final String[] searchcol = MDMApiFactoryProvider.getMDMTableViewAPI().getViewContextParameterValues(viewCtx, "SEARCH_COLUMN");
        if (searchcol != null && searchcol.length != 0) {
            final String[] searchval = MDMApiFactoryProvider.getMDMTableViewAPI().getViewContextParameterValues(viewCtx, "SEARCH_VALUE");
            for (int i = 0; i < searchcol.length; ++i) {
                String colVal = searchval[i];
                colVal = DMIAMEncoder.encodeSQLForNonPatternContext(colVal);
                final String s = searchcol[i];
                switch (s) {
                    case "MdDeviceInfo.SERIAL_NUMBER": {
                        criteria = criteria + " and ((DeviceForEnrollment.SERIAL_NUMBER like '%" + colVal + "%') or (MdDeviceInfo.SERIAL_NUMBER like '%" + colVal + "%'))";
                        break;
                    }
                    case "MdDeviceInfo.IMEI": {
                        criteria = criteria + " and ((DeviceForEnrollment.IMEI like '%" + colVal + "%') or (MdDeviceInfo.IMEI like '%" + colVal + "%'))";
                        break;
                    }
                    case "ManagedUser.DISPLAY_NAME": {
                        criteria = criteria + " and (ManagedUser.DISPLAY_NAME like '%" + colVal + "%')";
                        break;
                    }
                    case "ManagedUser.EMAIL_ADDRESS": {
                        criteria = criteria + " and (ManagedUser.EMAIL_ADDRESS like '%" + colVal + "%')";
                        break;
                    }
                    case "AaaUser.FIRST_NAME": {
                        criteria = criteria + " and (AaaUser.FIRST_NAME like '%" + colVal + "%')";
                        break;
                    }
                    case "ManagedDeviceExtn.NAME": {
                        criteria = criteria + " and ((ManagedDeviceExtn.NAME like '%" + colVal + "%') or (DeviceEnrollmentProps.ASSIGNED_DEVICE_NAME like '%" + colVal + "%'))";
                        break;
                    }
                    case "MdModelInfo.MODEL_NAME": {
                        criteria = criteria + " and (MdModelInfo.MODEL_NAME like '%" + colVal + "%')";
                        break;
                    }
                    case "DeprovisionHistory.COMMENT": {
                        criteria = criteria + " and (DeprovisionHistory.COMMENT like '%" + colVal + "%')";
                        break;
                    }
                    case "AppleDEPDeviceForEnrollment.ASSIGNED_USER": {
                        criteria = criteria + " and (AppleDEPDeviceForEnrollment.ASSIGNED_USER like '%" + colVal + "%')";
                        break;
                    }
                }
            }
        }
        return criteria;
    }
    
    public String setSearchCriteriaForRightQuery(final ViewContext viewCtx) throws SyMException {
        String criteria = "";
        final String[] searchcol = MDMApiFactoryProvider.getMDMTableViewAPI().getViewContextParameterValues(viewCtx, "SEARCH_COLUMN");
        if (searchcol != null && searchcol.length != 0) {
            final String[] searchval = MDMApiFactoryProvider.getMDMTableViewAPI().getViewContextParameterValues(viewCtx, "SEARCH_VALUE");
            for (int i = 0; i < searchcol.length; ++i) {
                String colVal = searchval[i];
                colVal = DMIAMEncoder.encodeSQLForNonPatternContext(colVal);
                final String s = searchcol[i];
                switch (s) {
                    case "MdDeviceInfo.SERIAL_NUMBER": {
                        criteria = criteria + " and ((DeviceForEnrollment.SERIAL_NUMBER like '%" + colVal + "%') or (MdDeviceInfo.SERIAL_NUMBER like '%" + colVal + "%'))";
                        break;
                    }
                    case "MdDeviceInfo.IMEI": {
                        criteria = criteria + " and ((DeviceForEnrollment.IMEI like '%" + colVal + "%') or (MdDeviceInfo.IMEI like '%" + colVal + "%'))";
                        break;
                    }
                    case "ManagedUser.DISPLAY_NAME": {
                        criteria = criteria + " and (ManagedUser.DISPLAY_NAME like '%" + colVal + "%')";
                        break;
                    }
                    case "ManagedUser.EMAIL_ADDRESS": {
                        criteria = criteria + " and (ManagedUser.EMAIL_ADDRESS like '%" + colVal + "%')";
                        break;
                    }
                    case "AaaUser.FIRST_NAME": {
                        criteria = criteria + " and (AaaUser.FIRST_NAME like '%" + colVal + "%')";
                        break;
                    }
                    case "ManagedDeviceExtn.NAME": {
                        criteria = criteria + " and ((ManagedDeviceExtn.NAME like '%" + colVal + "%') or (DeviceEnrollmentProps.ASSIGNED_DEVICE_NAME like '%" + colVal + "%'))";
                        break;
                    }
                    case "MdModelInfo.MODEL_NAME": {
                        criteria = criteria + " and ((MdModelInfo.MODEL_NAME like '%" + colVal + "%') or (AppleDEPDeviceForEnrollment.MODEL_NAME like '%" + colVal + "%'))";
                        break;
                    }
                    case "AppleDEPDeviceForEnrollment.ASSIGNED_USER": {
                        criteria = criteria + " and (AppleDEPDeviceForEnrollment.ASSIGNED_USER like '%" + colVal + "%')";
                        break;
                    }
                    case "DeprovisionHistory.COMMENT": {
                        criteria = criteria + " and (DeprovisionHistory.COMMENT like '%" + colVal + "%')";
                        break;
                    }
                }
            }
        }
        return criteria;
    }
    
    private String getModelTypeCriteria(final ViewContext viewCtx) {
        final String modelType = viewCtx.getRequest().getParameter("deviceType");
        String cri = "";
        final int MODEL_TYPE_LAPTOP_OR_DESKTOP = 6;
        if (modelType != null && !modelType.equalsIgnoreCase("") && !modelType.equalsIgnoreCase("all")) {
            switch (Integer.parseInt(modelType)) {
                case 1: {
                    cri = " and (MdModelInfo.MODEL_TYPE = 1 or AppleDEPDeviceForEnrollment.DEVICE_MODEL in (2,3))";
                    break;
                }
                case 2: {
                    cri = " and (MdModelInfo.MODEL_TYPE = 2 or AppleDEPDeviceForEnrollment.DEVICE_MODEL = 1)";
                    final String multiUser = viewCtx.getRequest().getParameter("isMultiUser");
                    if (multiUser != null && !multiUser.equalsIgnoreCase("all")) {
                        final Boolean isMultiUser = Boolean.parseBoolean(multiUser);
                        cri = cri + " and (MdDeviceInfo.IS_MULTIUSER = '" + isMultiUser + "')";
                        break;
                    }
                    break;
                }
                case 5: {
                    cri = " and (MdModelInfo.MODEL_TYPE = 5 or AppleDEPDeviceForEnrollment.DEVICE_MODEL = 5)";
                    break;
                }
                case 0: {
                    cri = " and (MdModelInfo.MODEL_TYPE = 0)";
                    break;
                }
                case 6: {
                    cri = " and (MdModelInfo.MODEL_TYPE in(3,4) or (AppleDEPDeviceForEnrollment.DEVICE_MODEL = 4) or (WinModernMgmtDeviceForEnrollment.ENROLLMENT_DEVICE_ID is not null) or (WindowsLaptopDeviceForEnrollment.ENROLLMENT_DEVICE_ID is not null) or (WindowsICDDeviceForEnrollment.ENROLLMENT_DEVICE_ID is not null) or (WinAzureADDeviceForEnrollment.ENROLLMENT_DEVICE_ID is not null) or (MacModernMgmtDeviceForEnrollment.ENROLLMENT_DEVICE_ID is not null))";
                    break;
                }
            }
        }
        return cri;
    }
    
    private String setModernMgmtCriteria(final ViewContext viewCtx) {
        String criteria = "";
        try {
            final String modernMgmt = viewCtx.getRequest().getParameter("modernMgmt");
            if (modernMgmt != null && !modernMgmt.trim().equalsIgnoreCase("")) {
                final boolean uem = Boolean.parseBoolean(modernMgmt);
                if (uem) {
                    criteria = " and (WinModernMgmtDeviceForEnrollment.ENROLLMENT_DEVICE_ID is not null or WindowsLaptopDeviceForEnrollment.ENROLLMENT_DEVICE_ID is not null or WindowsICDDeviceForEnrollment.ENROLLMENT_DEVICE_ID is not null or WinAzureADDeviceForEnrollment.ENROLLMENT_DEVICE_ID is not null or (AppleDEPDeviceForEnrollment.ENROLLMENT_DEVICE_ID is not null  and AppleDEPDeviceForEnrollment.DEVICE_MODEL=4) or MacModernMgmtDeviceForEnrollment.ENROLLMENT_DEVICE_ID is not null)";
                    viewCtx.getRequest().setAttribute("selectedTabInput", (Object)"staged");
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in showing uem devices", exp);
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
