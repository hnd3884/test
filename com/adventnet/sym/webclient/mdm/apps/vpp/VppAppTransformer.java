package com.adventnet.sym.webclient.mdm.apps.vpp;

import com.adventnet.i18n.I18N;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import com.me.devicemanagement.framework.webclient.authorization.RolecheckerTransformer;

public class VppAppTransformer extends RolecheckerTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        if (!columnalias.equalsIgnoreCase("checkbox_column") && !columnalias.equalsIgnoreCase("action_column")) {
            return super.checkIfColumnRendererable(tableContext);
        }
        final ViewContext vc = tableContext.getViewContext();
        final HttpServletRequest request = vc.getRequest();
        final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
        if (isExport != null && isExport.equalsIgnoreCase("true")) {
            return false;
        }
        final boolean hasAppMgmtWritePrivillage = request.isUserInRole("MDM_AppMgmt_Write");
        return hasAppMgmtWritePrivillage;
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
        final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
        if (columnalais.equals("checkbox_column")) {
            String checkAll = "";
            if (isExport == null) {
                checkAll = "<input type=\"checkbox\" id=\"selectAll\" value=\"SelectAll\" name=\"selectcheckbox\" onclick=\"javascript:selectAllObjects(this.checked)\">";
            }
            headerProperties.put("VALUE", checkAll);
        }
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
        final String viewName = tableContext.getViewContext().getUniqueId();
        final ViewContext viewCtx = tableContext.getViewContext();
        final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
        if (columnalais.equals("action_column")) {
            if (viewName.equalsIgnoreCase("VppAppLicense")) {
                final Long licenseId = (Long)tableContext.getAssociatedPropertyValue("MdVPPLicenseDetails.VPP_LICENSE_ID");
                final Long resourceId = (Long)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_ID");
                final int status = (int)tableContext.getAssociatedPropertyValue("MdVPPLicenseDetails.STATUS");
                final Boolean irrevokable = (Boolean)tableContext.getAssociatedPropertyValue("MdVPPLicenseDetails.IS_IRREVOCABLE");
                if (status == 1 && !irrevokable) {
                    String action = "<a href=\"#\" onclick=\"javascript:confirmandClearVppLicense('" + licenseId + "'" + ")\"><img src=\"/images/revoke_license.png\" width=\"16\" alt=\"" + I18N.getMsg("desktopcentral.admin.Revoke", new Object[0]) + "\" title=\"" + I18N.getMsg("desktopcentral.admin.Revoke", new Object[0]) + "\" height=\"16\" hspace=\"3\" vspace=\"0\" border=\"0\" align=\"absmiddle\" ></a>";
                    if (resourceId != null) {
                        final String deviceName = (String)tableContext.getAssociatedPropertyValue("Resource.NAME");
                        action = "<a href=\"#\" onclick=\"javascript:confirmandClearVppAppAssign(" + resourceId + " , " + "'" + licenseId + "'" + " , " + "'" + deviceName + "'" + ")\"><img src=\"/images/revoke_license.png\" width=\"16\" alt=\"" + I18N.getMsg("desktopcentral.admin.Revoke", new Object[0]) + "\" title=\"" + I18N.getMsg("desktopcentral.admin.Revoke", new Object[0]) + "\" height=\"16\" hspace=\"3\" vspace=\"0\" border=\"0\" align=\"absmiddle\" ></a>";
                    }
                    columnProperties.put("VALUE", action);
                }
                else {
                    columnProperties.put("VALUE", "--");
                }
            }
            else if (viewName.equalsIgnoreCase("vppManagedUserView")) {
                final Long resourceId2 = (Long)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_ID");
                final String userName = (String)tableContext.getAssociatedPropertyValue("Resource.NAME");
                final Long vppId = (Long)tableContext.getAssociatedPropertyValue("MdVPPManagedUser.VPP_USER_ID");
                final Integer status2 = (Integer)tableContext.getAssociatedPropertyValue("MdVPPManagedUser.MANAGED_STATUS");
                if (status2 != 3) {
                    String action = "<a href=\"#\" onclick=\"javascript:retierVPPUser('" + resourceId2 + "' , '" + vppId + "', '" + userName + "')\"><img src=\"/images/retireuser.png\" width=\"16\" alt=\"" + I18N.getMsg("dc.mdm.app.retireUser", new Object[0]) + "\" title=\"" + I18N.getMsg("dc.mdm.app.retireUser", new Object[0]) + "\" height=\"16\" hspace=\"3\" vspace=\"0\" border=\"0\" align=\"absmiddle\" ></a>";
                    if (status2 == 1) {
                        action = action + "&nbsp;<a href=\"#\" onclick=\"javascript:resendInvitationCode('" + resourceId2 + "' , '" + vppId + "', '" + userName + "')\"><img src=\"/images/resend_request.png\" width=\"14\" alt=\"" + I18N.getMsg("dc.mdm.app.resend_invitation_code", new Object[0]) + "\" title=\"" + I18N.getMsg("dc.mdm.app.resend_invitation_code", new Object[0]) + "\" height=\"14\" hspace=\"3\" vspace=\"0\" border=\"0\" align=\"absmiddle\" ></a>";
                    }
                    columnProperties.put("VALUE", action);
                }
            }
            else if (viewName.equalsIgnoreCase("vppUserToLicenseView")) {
                final Long resourceId2 = (Long)tableContext.getAssociatedPropertyValue("MdVPPManagedUser.MANAGED_USER_ID");
                final Long licenseId2 = (Long)tableContext.getAssociatedPropertyValue("MdVPPLicenseDetails.VPP_LICENSE_ID");
                final Long storeId = (Long)tableContext.getAssociatedPropertyValue("MdVPPLicenseDetails.ADAM_ID");
                final Long appGroupId = (Long)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.APP_GROUP_ID");
                final String action = "<a href=\"#\" onclick=\"javascript:confirmandClearVppLicenseToUser('" + resourceId2 + "','" + licenseId2 + "' , '" + appGroupId + "', '" + storeId + "')\"><img src=\"/images/revoke_license.png\" width=\"16\" alt=\"" + I18N.getMsg("desktopcentral.admin.Revoke", new Object[0]) + "\" title=\"" + I18N.getMsg("desktopcentral.admin.Revoke", new Object[0]) + "\" height=\"16\" hspace=\"3\" vspace=\"0\" border=\"0\" align=\"absmiddle\" ></a>";
                columnProperties.put("VALUE", action);
            }
        }
        if (columnalais.equals("checkbox_column")) {
            final Long vppUserId = (Long)tableContext.getAssociatedPropertyValue("MdVPPManagedUser.VPP_USER_ID");
            final Long managedUserId = (Long)tableContext.getAssociatedPropertyValue("ManagedUser.MANAGED_USER_ID");
            final String check = "<input type=\"checkbox\" value=\"" + managedUserId + "\" name=\"object_list\"><input type=\"hidden\" value=\"" + vppUserId + "\" id=\"vppUserId_" + managedUserId + "\">";
            columnProperties.put("VALUE", check);
        }
        if (columnalais.equalsIgnoreCase("MdVPPManagedUser.MANAGED_STATUS")) {
            final Integer status3 = (Integer)tableContext.getPropertyValue();
            if (status3 != null) {
                if (status3 == 0) {
                    columnProperties.put("VALUE", I18N.getMsg("dc.db.mdm.licenseStatus.yet_to_invite", new Object[0]));
                }
                else if (status3 == 1) {
                    columnProperties.put("VALUE", I18N.getMsg("dc.db.mdm.licenseStatus.invited", new Object[0]));
                }
                else if (status3 == 2) {
                    columnProperties.put("VALUE", I18N.getMsg("dc.db.mdm.licenseStatus.Register", new Object[0]));
                }
                else if (status3 == 3) {
                    columnProperties.put("VALUE", I18N.getMsg("dc.mdm.retired", new Object[0]));
                }
                else if (status3 == 4) {
                    columnProperties.put("VALUE", I18N.getMsg("dc.db.mdm.licenseStatus.registration_failed", new Object[0]));
                }
            }
            else {
                columnProperties.put("VALUE", "--");
            }
        }
        if (columnalais.equalsIgnoreCase("MDVPPLicenseDetails.STATUS")) {
            final Integer status3 = (Integer)tableContext.getPropertyValue();
            if (status3 == 1) {
                columnProperties.put("VALUE", I18N.getMsg("dc.common.ASSOCIATED", new Object[0]));
            }
            else if (status3 == 0) {
                columnProperties.put("VALUE", I18N.getMsg("dc.mdm.vpp.unused", new Object[0]));
            }
        }
        if (columnalais.equalsIgnoreCase("MdVPPLicenseDetails.IS_IRREVOCABLE")) {
            final Boolean irrevocable = (Boolean)tableContext.getPropertyValue();
            if (irrevocable) {
                columnProperties.put("VALUE", I18N.getMsg("dc.common.NO", new Object[0]));
            }
            else {
                columnProperties.put("VALUE", I18N.getMsg("dc.common.YES", new Object[0]));
            }
        }
        if (columnalais.equalsIgnoreCase("Resource.NAME") && isExport == null) {
            final String userName2 = (String)tableContext.getPropertyValue();
            final Integer licenseCount = (Integer)tableContext.getAssociatedPropertyValue("LICENSE_COUNT");
            final String emailAddress = (String)tableContext.getAssociatedPropertyValue("ManagedUser.EMAIL_ADDRESS");
            final Long managedUserId2 = (Long)tableContext.getAssociatedPropertyValue("ManagedUser.MANAGED_USER_ID");
            final Long vppUserId2 = (Long)tableContext.getAssociatedPropertyValue("MdVPPManagedUser.VPP_USER_ID");
            String linceseStr = "--";
            if (licenseCount != null) {
                linceseStr = licenseCount + "";
            }
            columnProperties.put("VALUE", "<a href=\"javascript:showVpplicenseToUser('" + emailAddress + "' , '" + managedUserId2 + "', '" + linceseStr + "', '" + vppUserId2 + "')\">" + userName2 + "</a>");
        }
        if (columnalais.equalsIgnoreCase("MdVPPManagedUser.REMARKS")) {
            final String remarks = (String)tableContext.getPropertyValue();
            columnProperties.put("VALUE", I18N.getMsg(remarks, new Object[0]));
        }
    }
}
