package com.adventnet.sym.webclient.mdm.user;

import com.me.mdm.webclient.transformer.TransformerUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.client.view.web.ViewContext;
import org.json.JSONObject;
import com.me.idps.core.util.DirectoryUtil;
import java.util.HashMap;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class MDMUserTransformer extends DefaultTransformer
{
    private Logger logger;
    
    public MDMUserTransformer() {
        this.logger = Logger.getLogger(MDMUserTransformer.class.getName());
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final int reportType = tableContext.getViewContext().getRenderType();
        final HttpServletRequest request = tableContext.getViewContext().getRequest();
        final String viewName = tableContext.getViewContext().getUniqueId();
        final boolean hasEnrollmentWritePrivillage = request.isUserInRole("MDM_Enrollment_Write") || request.isUserInRole("ModernMgmt_Enrollment_Write");
        if (columnalias.equalsIgnoreCase("Checkbox") && !hasEnrollmentWritePrivillage) {
            return false;
        }
        if (columnalias.equalsIgnoreCase("Action") && !hasEnrollmentWritePrivillage) {
            return false;
        }
        if (columnalias.equalsIgnoreCase("Checkbox")) {
            return reportType == 4 && viewName.equalsIgnoreCase("mdmUserEnrollView") && !ManagedDeviceHandler.getInstance().isDeviceProvisioningUser();
        }
        if (columnalias.equalsIgnoreCase("Resource.RESOURCE_ID")) {
            return false;
        }
        if (columnalias.equalsIgnoreCase("Action")) {
            return reportType == 4 && viewName.equalsIgnoreCase("mdmUserEnrollView") && !ManagedDeviceHandler.getInstance().isDeviceProvisioningUser();
        }
        return super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        this.logger.log(Level.FINE, "Entering MDMUserTransformer renderHeader().....");
        super.renderHeader(tableContext);
        final String checkbox = tableContext.getDisplayName();
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        try {
            if (checkbox.equals(I18N.getMsg("dc.common.CHECKBOX_COLUMN", new Object[0]))) {
                final String checkAll = "";
                headerProperties.put("VALUE", checkAll);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in MDMUserTransformer renderHeader", e);
        }
    }
    
    public void renderCell(final TransformerContext tableContext) {
        try {
            super.renderCell(tableContext);
            final Object data = tableContext.getPropertyValue();
            final ViewContext vc = tableContext.getViewContext();
            final String columnalais = tableContext.getPropertyName();
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            if (columnalais.equals("Resource.NAME")) {
                final String username = (String)tableContext.getAssociatedPropertyValue("Resource.NAME");
                final int usernameLength = username.length();
                String usernameInShort = "";
                if (usernameLength > 15) {
                    usernameInShort = username.substring(0, 14) + "...";
                }
                final int dirObjStatus = DirectoryUtil.getInstance().getDirObjStatus(tableContext);
                final JSONObject payload = new JSONObject();
                payload.put("username", (Object)username);
                payload.put("displayName", (Object)usernameInShort);
                payload.put("dirObjStatus", dirObjStatus);
                columnProperties.put("PAYLOAD", payload);
            }
            if (columnalais.equals("Checkbox")) {
                boolean isDisabled = true;
                boolean allowDeletion = false;
                String disbaledReason = "";
                final int count = this.getAssociatedManagedDeviceCount(tableContext);
                final String domainName = (String)tableContext.getAssociatedPropertyValue("Resource.DOMAIN_NETBIOS_NAME");
                if (domainName.equalsIgnoreCase("MDM")) {
                    if (count == 0) {
                        allowDeletion = true;
                        isDisabled = false;
                    }
                    else {
                        disbaledReason = "ManagedUser";
                    }
                }
                else {
                    final int dirObjStatus2 = DirectoryUtil.getInstance().getDirObjStatus(tableContext);
                    if (dirObjStatus2 != 1 && dirObjStatus2 != 3 && count == 0) {
                        allowDeletion = true;
                        isDisabled = false;
                    }
                    else {
                        disbaledReason = "AdUser";
                    }
                }
                final JSONObject payload2 = new JSONObject();
                payload2.put("isDisabled", isDisabled);
                payload2.put("allowDeletion", allowDeletion);
                payload2.put("disbaledReason", (Object)disbaledReason);
                columnProperties.put("PAYLOAD", payload2);
            }
            if (columnalais.equals("Action")) {
                boolean disableAction = true;
                boolean showModifyuser = false;
                boolean showRemoveuser = false;
                boolean showRemoveAlert = false;
                final int count2 = this.getAssociatedManagedDeviceCount(tableContext);
                final String domainName2 = (String)tableContext.getAssociatedPropertyValue("Resource.DOMAIN_NETBIOS_NAME");
                if (domainName2.equalsIgnoreCase("MDM")) {
                    if (count2 == 0) {
                        showRemoveuser = true;
                    }
                    showModifyuser = true;
                    disableAction = false;
                }
                else {
                    final int dirObjStatus3 = DirectoryUtil.getInstance().getDirObjStatus(tableContext);
                    if (dirObjStatus3 != 1) {
                        disableAction = false;
                        if (count2 == 0) {
                            showRemoveuser = true;
                        }
                        else {
                            showRemoveAlert = true;
                        }
                    }
                }
                final JSONObject payload3 = new JSONObject();
                payload3.put("disableAction", disableAction);
                payload3.put("showModifyuser", showModifyuser);
                payload3.put("showRemoveuser", showRemoveuser);
                payload3.put("showRemoveAlert", showRemoveAlert);
                columnProperties.put("PAYLOAD", payload3);
            }
            if (columnalais.equals("Resource.DOMAIN_NETBIOS_NAME")) {
                if (data.toString().equalsIgnoreCase("MDM")) {
                    columnProperties.put("VALUE", I18N.getMsg("dc.mdm.enroll.local_user", new Object[0]));
                }
                else {
                    columnProperties.put("VALUE", data);
                }
            }
            if (columnalais.contains("ManagedUser.") || columnalais.contains("DEPARTMENT")) {
                Long attrID = null;
                String managedUserVal = (String)tableContext.getAssociatedPropertyValue(columnalais);
                if (columnalais.equals("ManagedUser.LAST_NAME")) {
                    attrID = 108L;
                }
                if (columnalais.equals("ManagedUser.DISPLAY_NAME")) {
                    attrID = 111L;
                }
                if (columnalais.equals("ManagedUser.EMAIL_ADDRESS")) {
                    managedUserVal = (String)data;
                    attrID = 106L;
                }
                if (columnalais.equals("ManagedUser.FIRST_NAME")) {
                    attrID = 109L;
                }
                if (columnalais.equals("ManagedUser.MIDDLE_NAME")) {
                    attrID = 110L;
                }
                if (columnalais.equals("ManagedUser.PHONE_NUMBER")) {
                    attrID = 114L;
                }
                if (columnalais.equals("DEPARTMENT")) {
                    attrID = 128L;
                }
                this.setVal(managedUserVal, tableContext, vc, columnProperties, attrID);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in renderCell", e);
        }
    }
    
    private int getAssociatedManagedDeviceCount(final TransformerContext tableContext) {
        Integer count = (Integer)tableContext.getAssociatedPropertyValue("MANAGED_COUNT");
        count = ((count == null) ? 0 : count);
        return count;
    }
    
    private void setVal(final String managedUserVal, final TransformerContext tableContext, final ViewContext vc, final HashMap columnProperties, final Long attrID) {
        String dataVal = null;
        boolean pickDirVal = true;
        if (managedUserVal != null) {
            pickDirVal = false;
            if (attrID != null && attrID == 106L && !MDMUtil.getInstance().isValidEmail(managedUserVal)) {
                pickDirVal = true;
            }
        }
        if (!pickDirVal) {
            dataVal = managedUserVal;
        }
        else if (attrID != null) {
            final HashMap<Long, HashMap<Long, String>> dirObjAttrVal = (HashMap<Long, HashMap<Long, String>>)TransformerUtil.getPreValuesForTransformer(vc, "DIR_ATTR_VAL");
            final Long objID = (Long)tableContext.getAssociatedPropertyValue("OBJ_ID");
            if (objID != null && dirObjAttrVal.containsKey(objID)) {
                final HashMap<Long, String> userAttr = dirObjAttrVal.get(objID);
                if (userAttr != null && userAttr.containsKey(attrID)) {
                    dataVal = userAttr.get(attrID);
                }
            }
        }
        final Object data = dataVal;
        if (data != null && !data.toString().equalsIgnoreCase("")) {
            columnProperties.put("VALUE", data);
        }
        else {
            columnProperties.put("VALUE", "--");
        }
    }
}
