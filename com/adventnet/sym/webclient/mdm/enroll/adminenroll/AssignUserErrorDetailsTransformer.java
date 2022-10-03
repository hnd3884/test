package com.adventnet.sym.webclient.mdm.enroll.adminenroll;

import com.me.mdm.server.enrollment.adminenroll.WinModernMgmtAssignUserCSVProcessor;
import com.me.mdm.server.enrollment.adminenroll.MultipleTemplateAssignUserCSVProcessor;
import com.me.mdm.server.enrollment.adminenroll.ModernMacManagementAssignUserCSVProcessor;
import com.me.mdm.server.enrollment.adminenroll.WinAzureADAssignUserCSVProcessor;
import com.me.mdm.server.enrollment.adminenroll.WinLaptopEnrollmentAssignUserCSVProcessor;
import com.me.mdm.server.enrollment.adminenroll.AndroidQRAssignUserCSVProcessor;
import com.me.mdm.server.enrollment.adminenroll.WindowsWICDAssignUserCSVProcessor;
import com.me.mdm.server.enrollment.adminenroll.KnoxAssignUserCSVProcessor;
import com.me.mdm.server.enrollment.adminenroll.AndroidNFCAssignUserCSVProcessor;
import com.me.mdm.server.enrollment.adminenroll.AppleConfigAssignUserCSVProcessor;
import java.util.HashMap;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.enrollment.adminenroll.AssignUserCSVProcessor;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class AssignUserErrorDetailsTransformer extends DefaultTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        try {
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            String templateTypeStr = tableContext.getViewContext().getRequest().getParameter("enrollmentTemplate");
            templateTypeStr = ((templateTypeStr == null) ? String.valueOf(-1) : templateTypeStr);
            if (templateTypeStr == null || templateTypeStr.isEmpty()) {
                return false;
            }
            final AssignUserCSVProcessor assignUserCSVProcessor = this.getAssignUserCSVProcessorForTemplate(Integer.parseInt(templateTypeStr));
            if (tableContext.getPropertyName().equals("AssignUserImportInfo.USER_NAME")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue(assignUserCSVProcessor.isUserNameInCSV, (long)customerID));
            }
            if (tableContext.getPropertyName().equals("AssignUserImportInfo.DOMAIN_NAME")) {
                return !CustomerInfoUtil.getInstance().isMSP() && Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue(assignUserCSVProcessor.isDomainNameInCSV, (long)customerID));
            }
            if (tableContext.getPropertyName().equals("AssignUserImportInfo.EMAIL_ADDRESS")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue(assignUserCSVProcessor.isEmailAddressInCSV, (long)customerID));
            }
            if (tableContext.getPropertyName().equals("AssignUserImportInfo.IMEI")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue(assignUserCSVProcessor.isIMEIInCSV, (long)customerID));
            }
            if (tableContext.getPropertyName().equals("AssignUserImportInfo.DEVICE_NAME")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue(assignUserCSVProcessor.isDeviceNameInCSV, (long)customerID));
            }
            if (tableContext.getPropertyName().equals("AssignUserImportInfo.GROUP_NAME")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue(assignUserCSVProcessor.isGroupNameInCSV, (long)customerID));
            }
            if (tableContext.getPropertyName().equals("AssignUserImportInfo.SERIAL_NUMBER")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue(assignUserCSVProcessor.isSerialNumberInCSV, (long)customerID));
            }
            if (tableContext.getPropertyName().equals("AssignUserImportInfo.EXCHANGE_ID")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue(assignUserCSVProcessor.isExchangeIDInCSV, (long)customerID));
            }
            return super.checkIfColumnRendererable(tableContext);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        try {
            super.renderCell(tableContext);
            if (tableContext.getPropertyName().equals("AssignUserImportInfo.ERROR_REMARKS")) {
                final HashMap columnProperties = tableContext.getRenderedAttributes();
                final String remarks_args = (String)tableContext.getAssociatedPropertyValue("AssignUserImportInfo.ERROR_REMARKS_ARGS");
                columnProperties.put("VALUE", I18N.getMsg((String)tableContext.getPropertyValue(), new Object[] { remarks_args }));
            }
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    private AssignUserCSVProcessor getAssignUserCSVProcessorForTemplate(final int templateType) {
        switch (templateType) {
            case 11: {
                return new AppleConfigAssignUserCSVProcessor();
            }
            case 20: {
                return new AndroidNFCAssignUserCSVProcessor();
            }
            case 21: {
                return new KnoxAssignUserCSVProcessor();
            }
            case 30: {
                return new WindowsWICDAssignUserCSVProcessor();
            }
            case 22: {
                return new AndroidQRAssignUserCSVProcessor();
            }
            case 31: {
                return new WinLaptopEnrollmentAssignUserCSVProcessor();
            }
            case 32: {
                return new WinAzureADAssignUserCSVProcessor();
            }
            case 12: {
                return new ModernMacManagementAssignUserCSVProcessor();
            }
            case -1: {
                return new MultipleTemplateAssignUserCSVProcessor();
            }
            case 33: {
                return new WinModernMgmtAssignUserCSVProcessor();
            }
            default: {
                return null;
            }
        }
    }
}
