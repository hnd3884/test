package com.adventnet.sym.webclient.mdm.enroll;

import java.util.HashMap;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class BulkEnrollmentErrorDetailsTransformer extends DefaultTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        try {
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            if (tableContext.getPropertyName().equals("BulkEnrollmentImportInfo.USER_NAME")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("BulkEnroll_IsUserNameInCSV", (long)customerID));
            }
            if (tableContext.getPropertyName().equals("BulkEnrollmentImportInfo.DOMAIN_NAME")) {
                return !CustomerInfoUtil.getInstance().isMSP() && Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("BulkEnroll_IsDomainNameInCSV", (long)customerID));
            }
            if (tableContext.getPropertyName().equals("BulkEnrollmentImportInfo.EMAIL_ADDRESS")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("BulkEnroll_IsEmailInCSV", (long)customerID));
            }
            if (tableContext.getPropertyName().equals("BulkEnrollmentImportInfo.PHONE_NUMBER")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("BulkEnroll_IsPhoneNumberInCSV", (long)customerID));
            }
            if (tableContext.getPropertyName().equals("BulkEnrollmentImportInfo.OWNED_BY")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("BulkEnroll_IsOwnedByInCSV", (long)customerID));
            }
            if (tableContext.getPropertyName().equals("BulkEnrollmentImportInfo.PLATFORM_TYPE")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("BulkEnroll_IsPlatformTypeInCSV", (long)customerID));
            }
            if (tableContext.getPropertyName().equals("BulkEnrollmentImportInfo.GROUP_NAME")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("BulkEnroll_IsGroupNameInCSV", (long)customerID));
            }
            if (tableContext.getPropertyName().equals("BulkEnrollmentImportInfo.UDID")) {
                return Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("BulkEnroll_IsUdidInCSV", (long)customerID));
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
            if (tableContext.getPropertyName().equals("BulkEnrollmentImportInfo.ERROR_REMARKS")) {
                final HashMap columnProperties = tableContext.getRenderedAttributes();
                final String remarks_args = (String)tableContext.getAssociatedPropertyValue("BulkEnrollmentImportInfo.ERROR_REMARKS_ARGS");
                columnProperties.put("VALUE", I18N.getMsg((String)tableContext.getPropertyValue(), new Object[] { remarks_args }));
            }
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
}
