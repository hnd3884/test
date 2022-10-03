package com.adventnet.sym.webclient.mdm.enroll;

import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.authorization.RolecheckerTransformer;

public class RetiredViewTransformer extends RolecheckerTransformer
{
    public Logger logger;
    
    public RetiredViewTransformer() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
        if (!columnalias.equalsIgnoreCase("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID") && !columnalias.equalsIgnoreCase("Action")) {
            return super.checkIfColumnRendererable(tableContext);
        }
        if (isExport != null && isExport.equalsIgnoreCase("true")) {
            return false;
        }
        final boolean enrollWrite = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_Enrollment_Write");
        final boolean modernEnrollWrite = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("ModernMgmt_Enrollment_Write");
        return enrollWrite || modernEnrollWrite;
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
        if (columnalais.equals("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID")) {
            final String checkboxButton = "<input type=\"checkbox\" id=\"selectHead\" onclick=\"checkAllRetiredDevices(this.checked)\">";
            headerProperties.put("VALUE", checkboxButton);
        }
    }
    
    public void renderCell(final TransformerContext tableContext) {
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final String columnalais = tableContext.getPropertyName();
            final Object data = tableContext.getPropertyValue();
            if (columnalais.equals("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID")) {
                final Long resourceID = (Long)data;
                final String checkboxButton = "<input type=\"checkbox\" value=\"" + resourceID + "\" name=\"selectRetiredDevice\" onclick=\"setSelectHead(this.checked)\">";
                columnProperties.put("VALUE", checkboxButton);
            }
            if (columnalais.equals("Action")) {
                final Long erid = (Long)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID");
                final String deleteStr = "<a href=\"javascript:confirmAction('/mdmEnroll.do?actionToCall=removeDevice&deviceIDs=" + erid + "');\" style=\"text-decoration:none;\"><img src=\"/images/delete_1.png\" title=\"" + I18N.getMsg("dc.common.REMOVE", new Object[0]) + "\" class=\"menuItemImage\" width=\"12\" height=\"12\" border=\"0\" style=\"vertical-align: bottom;padding-bottom: 2px; width:11px !important;height:11px !important;\"/> &nbsp;<span class=\"blueTxt\" style=\"color: black !important;\">" + I18N.getMsg("dc.common.REMOVE", new Object[0]) + "</span></a>";
                columnProperties.put("VALUE", deleteStr);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured while rendering cell value in retiredViewTransformer ", ex);
            final HashMap columnProperties2 = tableContext.getRenderedAttributes();
            columnProperties2.put("VALUE", "--");
        }
    }
}
