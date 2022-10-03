package com.adventnet.sym.webclient.mdm.config;

import java.util.HashMap;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.authorization.RolecheckerTransformer;

public class AppLicenseTransformer extends RolecheckerTransformer
{
    private static final int YET_TO_REDEEM = 0;
    private static Logger logger;
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        if (!columnalias.equalsIgnoreCase("checkbox_column") && !columnalias.equalsIgnoreCase("action_column")) {
            return super.checkIfColumnRendererable(tableContext);
        }
        final ViewContext vc = tableContext.getViewContext();
        final HttpServletRequest request = vc.getRequest();
        final String isExport = request.getParameter("isExport");
        if (isExport != null && isExport.equalsIgnoreCase("true")) {
            return false;
        }
        final boolean hasAppMgmtWritePrivillage = request.isUserInRole("MDM_AppMgmt_Write");
        return hasAppMgmtWritePrivillage;
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        try {
            final String checkbox = tableContext.getDisplayName();
            if (checkbox.equals(I18N.getMsg("dc.common.CHECKBOX_COLUMN", new Object[0]))) {
                final String checkAll = "<input type=\"checkbox\" id=\"selectAll\" value=\"SelectAll\" name=\"selectcheckbox\" onclick=\"javascript:selectAllObjects(this.checked)\">";
                headerProperties.put("VALUE", checkAll);
            }
        }
        catch (final Exception e) {
            AppLicenseTransformer.logger.log(Level.WARNING, "Exception in AppLicenseTransformer renderHeader", e);
        }
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
        final String viewName = tableContext.getViewContext().getUniqueId();
        if (columnalais.equals("checkbox_column")) {
            final Long licenseId = (Long)tableContext.getAssociatedPropertyValue("MdLicenseDetails.LICENSE_DETAILS_ID");
            final String check = "<input type=\"checkbox\" value=\"" + licenseId + "\" name=\"license_list\">";
            columnProperties.put("VALUE", check);
        }
        if (columnalais.equals("action_column")) {
            final Long resourceId = (Long)tableContext.getAssociatedPropertyValue("MdAppLicenseToResources.RESOURCE_ID");
            final String deviceName = (String)tableContext.getAssociatedPropertyValue("ManagedDeviceExtn.NAME");
            final String licenseCode = (String)tableContext.getAssociatedPropertyValue("MdLicenseCodes.APP_LICENSE_CODE");
            final Integer licenseCodeStatus = (Integer)tableContext.getAssociatedPropertyValue("MdAppLicenseToResources.LICENSE_CODE_STATUS");
            if (resourceId != null && licenseCodeStatus != null && licenseCodeStatus == 0) {
                final String action = "<a href=\"#\" onclick=\"javascript:confirmandClearVpp(" + resourceId + " , " + "'" + licenseCode + "'" + " , " + "'" + deviceName + "'" + ")\"><img src=\"/images/delete.png\" width=\"17\" alt=\"" + I18N.getMsg("dc.mdm.actionlog.appmgmt.clear_vpp", new Object[0]) + "\" title=\"" + I18N.getMsg("dc.mdm.actionlog.appmgmt.clear_vpp", new Object[0]) + "\" height=\"15\" hspace=\"3\" vspace=\"0\" border=\"0\" align=\"absmiddle\" ></a>";
                columnProperties.put("VALUE", action);
            }
        }
        if (columnalais.equals("MdAppLicenseToResources.LICENSE_CODE_STATUS")) {
            final Integer code = (Integer)tableContext.getPropertyValue();
            if (code != null) {
                if (code == 0) {
                    columnProperties.put("VALUE", I18N.getMsg("dc.db.mdm.licenseStatus.YET_TO_REDEEMED", new Object[0]));
                }
                else if (code == 1) {
                    columnProperties.put("VALUE", I18N.getMsg("dc.db.mdm.licenseStatus.REDEEMED", new Object[0]));
                }
                else if (code == 2) {
                    columnProperties.put("VALUE", I18N.getMsg("dc.db.mdm.licenseStatus.REVERTED", new Object[0]));
                }
                else {
                    columnProperties.put("VALUE", I18N.getMsg("dc.common.UNKNOWN", new Object[0]));
                }
            }
            else {
                columnProperties.put("VALUE", "--");
            }
        }
        if (columnalais.equalsIgnoreCase("MDVPPLicenseDetails.STATUS")) {
            final Integer status = (Integer)tableContext.getPropertyValue();
            if (status == 1) {
                columnProperties.put("VALUE", I18N.getMsg("dc.common.ASSOCIATED", new Object[0]));
            }
            else if (status == 0) {
                columnProperties.put("VALUE", I18N.getMsg("dc.common.AVAILABLE", new Object[0]));
            }
        }
        if (columnalais.equalsIgnoreCase("VPPManagedUser.MANAGED_STATUS")) {
            final Integer status = (Integer)tableContext.getPropertyValue();
            if (status != null) {
                if (status == 0) {
                    columnProperties.put("VALUE", I18N.getMsg("dc.db.mdm.licenseStatus.yet_to_invite", new Object[0]));
                }
                else if (status == 1) {
                    columnProperties.put("VALUE", I18N.getMsg("dc.db.mdm.licenseStatus.Invited", new Object[0]));
                }
                else if (status == 2) {
                    columnProperties.put("VALUE", I18N.getMsg("dc.common.ASSOCIATED", new Object[0]));
                }
                else if (status == 3) {
                    columnProperties.put("VALUE", I18N.getMsg("dc.mdm.retired", new Object[0]));
                }
                else if (status == 4) {
                    columnProperties.put("VALUE", I18N.getMsg("dc.db.mdm.licenseStatus.registration_failed", new Object[0]));
                }
            }
            else {
                columnProperties.put("VALUE", "--");
            }
        }
    }
    
    static {
        AppLicenseTransformer.logger = Logger.getLogger(AppLicenseTransformer.class.getName());
    }
}
