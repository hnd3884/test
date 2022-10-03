package com.me.mdm.webclient.osupdate;

import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.i18n.I18N;
import java.util.HashMap;
import com.adventnet.client.view.web.ViewContext;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class OSUpdatePolicyTransformer extends DefaultTransformer
{
    public Logger logger;
    
    public OSUpdatePolicyTransformer() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final int reportType = tableContext.getViewContext().getRenderType();
        final String columnalais = tableContext.getPropertyName();
        final ViewContext vc = tableContext.getViewContext();
        final String viewname = vc.getUniqueId();
        if (!columnalais.equals("Profile.PROFILE_ID") && !columnalais.equals("checkbox")) {
            return super.checkIfColumnRendererable(tableContext);
        }
        if (reportType != 4) {
            return false;
        }
        final boolean hasConfigurationWritePrivillage = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_OSUpdateMgmt_Admin");
        return hasConfigurationWritePrivillage;
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
    }
    
    public void renderCell(final TransformerContext tableContext) {
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final String columnalais = tableContext.getPropertyName();
            final Object data = tableContext.getPropertyValue();
            final String viewName = tableContext.getViewContext().getUniqueId();
            final int latestVersion = (int)tableContext.getAssociatedPropertyValue("ProfileToCollection.PROFILE_VERSION");
            final long profileIdforCheck = (long)tableContext.getAssociatedPropertyValue("RecentProfileToColln.PROFILE_ID");
            final int reportType = tableContext.getViewContext().getRenderType();
            String isExport = "false";
            if (reportType != 4) {
                isExport = "true";
            }
            if (columnalais.equals("Profile.PROFILE_NAME")) {
                final String profileName = (String)data;
                columnProperties.put("VALUE", profileName);
            }
            if (columnalais.equals("OSUpdatePolicy.POLICY_TYPE")) {
                String policyType = "--";
                if (data != null) {
                    final String policyInt = String.valueOf(data);
                    if (policyInt.equals("2")) {
                        policyType = I18N.getMsg("mdm.osupdate.policytype.immediately", new Object[0]);
                    }
                    else if (policyInt.equals("3")) {
                        policyType = I18N.getMsg("mdm.osupdate.policytype.deffered", new Object[0]);
                    }
                    else if (policyInt.equals("4")) {
                        policyType = I18N.getMsg("mdm.osupdate.policytype.download", new Object[0]);
                    }
                }
                columnProperties.put("VALUE", policyType);
            }
            if (columnalais.equals("DevQuery.DEVCOUNT")) {
                String devCount;
                if (data == null) {
                    devCount = "--";
                }
                else {
                    devCount = String.valueOf(data);
                }
                columnProperties.put("VALUE", devCount);
            }
            if (columnalais.equals("GroupQuery.GROUPCOUNT")) {
                String grpCount;
                if (data == null) {
                    grpCount = "--";
                }
                else {
                    grpCount = String.valueOf(data);
                }
                columnProperties.put("VALUE", grpCount);
            }
            if (columnalais.equals("Profile.PLATFORM_TYPE")) {
                final Integer platformType = (Integer)data;
                String platformName = "";
                platformName = MDMUtil.getInstance().getPlatformColumnValue(platformType, isExport);
                columnProperties.put("VALUE", platformName);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured while rendering cell value in ProfileListTransformer ", ex);
            final HashMap columnProperties2 = tableContext.getRenderedAttributes();
            columnProperties2.put("VALUE", "&nbsp;&nbsp;&nbsp;&nbsp;--");
        }
    }
}
