package com.me.devicemanagement.onpremise.webclient.authentication;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class EmberUserAdministrationColumnTransformer extends DefaultTransformer
{
    private static Logger logger;
    
    public void renderCell(final TransformerContext tableContext) {
        EmberUserAdministrationColumnTransformer.logger.log(Level.FINE, "Entering UserAdministrationTransformer...");
        try {
            super.renderCell(tableContext);
            final String columnalais = tableContext.getPropertyName();
            EmberUserAdministrationColumnTransformer.logger.log(Level.FINE, "Columnalais : " + columnalais);
            final HttpServletRequest srvRequest = tableContext.getViewContext().getRequest();
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            if (columnalais.equals("AaaAccountStatusExtn.STATUS")) {
                final Integer status = (Integer)tableContext.getPropertyValue();
                String val = null;
                switch (status) {
                    case 1: {
                        val = "ems.user.uac.user_status_not_active";
                        break;
                    }
                    case 0: {
                        val = "dc.wc.inv.common.Active";
                        break;
                    }
                    default: {
                        val = "dc.wc.inv.common.Active";
                        break;
                    }
                }
                final String value = I18N.getMsg(val, new Object[0]);
                columnProperties.put("VALUE", value);
            }
            if (columnalais.equals("AaaAccountStatusExtn.REMARKS")) {
                final Object remarkCode = tableContext.getPropertyValue();
                String remarks = null;
                final JSONObject jsonObject = new JSONObject();
                final Long expiryTime = (Long)tableContext.getAssociatedPropertyValue("AaaUserLinkDetails.EXPIRY_TIME");
                final Integer tokenType = (Integer)tableContext.getAssociatedPropertyValue("AaaUserLinkDetails.TOKEN_TYPE");
                final Long currentTime = System.currentTimeMillis();
                if (expiryTime != null && expiryTime < currentTime) {
                    final String errorCode = (tokenType == 101) ? "UAC011" : "UAC014";
                    jsonObject.put("errorCode", (Object)errorCode);
                    columnProperties.put("PAYLOAD", jsonObject);
                    if (tokenType == 101) {
                        remarks = "ems.admin.admin.activation_token_expired";
                    }
                    else if (tokenType == 102) {
                        remarks = "ems.admin.admin.password_reset_token_expired";
                    }
                }
                else {
                    String errorCode = null;
                    switch ((int)remarkCode) {
                        case 1: {
                            remarks = "ems.admin.admin.user_account_activated";
                            break;
                        }
                        case 3: {
                            remarks = "ems.user.uac.user_activation_mail_sent";
                            break;
                        }
                        case 2: {
                            remarks = "ems.user.uac.user_activation_mail_not_sent";
                            errorCode = "UAC015";
                            break;
                        }
                        case 5: {
                            remarks = "ems.user.uac.user_password_mail_sent";
                            break;
                        }
                        case 6: {
                            remarks = "ems.user.uac.user_password_mail_not_sent";
                            errorCode = "UAC016";
                            break;
                        }
                    }
                    if (errorCode != null) {
                        jsonObject.put("errorCode", (Object)errorCode);
                        columnProperties.put("PAYLOAD", jsonObject);
                    }
                }
                final String value2 = (remarks != null) ? I18N.getMsg(remarks, new Object[0]) : "";
                columnProperties.put("VALUE", value2);
            }
            if (columnalais.equals("Action")) {
                final Integer remarkCode2 = (Integer)tableContext.getAssociatedPropertyValue("AaaAccountStatusExtn.REMARKS");
                boolean resentInvite = false;
                boolean resetPassword = false;
                final JSONObject jsonObject2 = new JSONObject();
                final Long expiryTime2 = (Long)tableContext.getAssociatedPropertyValue("AaaUserLinkDetails.EXPIRY_TIME");
                final Integer tokenType2 = (Integer)tableContext.getAssociatedPropertyValue("AaaUserLinkDetails.TOKEN_TYPE");
                final String domainName = (String)tableContext.getAssociatedPropertyValue("AaaLogin.DOMAINNAME");
                final Long loginId = (Long)tableContext.getAssociatedPropertyValue("AaaLogin.LOGIN_ID");
                final Long currentLoginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                final Long currentTime2 = System.currentTimeMillis();
                if (expiryTime2 != null && expiryTime2 < currentTime2) {
                    if (tokenType2 == 101) {
                        resentInvite = true;
                    }
                    else if (tokenType2 == 102) {
                        resetPassword = true;
                    }
                }
                else {
                    switch (remarkCode2) {
                        case 3: {
                            resentInvite = true;
                            break;
                        }
                        case 2: {
                            resentInvite = true;
                            break;
                        }
                        case 5: {
                            resetPassword = true;
                            break;
                        }
                        case 6: {
                            resetPassword = true;
                            break;
                        }
                        case 1: {
                            resetPassword = true;
                            break;
                        }
                    }
                }
                jsonObject2.put("resentInvite", resentInvite);
                if (domainName.equalsIgnoreCase("-") && !currentLoginId.equals(loginId)) {
                    jsonObject2.put("resetPassword", resetPassword);
                }
                columnProperties.put("PAYLOAD", jsonObject2);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String displyColumn = tableContext.getPropertyName();
        final HttpServletRequest request = tableContext.getViewContext().getRequest();
        final String isExport = request.getParameter("isExport");
        return (isExport == null || !isExport.equalsIgnoreCase("true") || !displyColumn.equals("Action")) && (!displyColumn.equals("DCSpiceworksUserMapping.SPICE_USER_NAME") || SyMUtil.getSyMParameter("isSpiceworksEnabled") == null || SyMUtil.getSyMParameter("isSpiceworksEnabled").equalsIgnoreCase("enabled")) && super.checkIfColumnRendererable(tableContext);
    }
    
    static {
        EmberUserAdministrationColumnTransformer.logger = Logger.getLogger("UserManagementLogger");
    }
}
