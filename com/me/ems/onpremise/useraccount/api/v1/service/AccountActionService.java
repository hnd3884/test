package com.me.ems.onpremise.useraccount.api.v1.service;

import com.me.ems.framework.common.api.v1.service.CommonService;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.HashMap;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.alerts.AlertConstants;
import com.me.devicemanagement.framework.server.alerts.AlertsUtil;
import com.me.ems.onpremise.uac.core.UserConstants;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import javax.ws.rs.core.Response;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.ems.onpremise.uac.core.CoreUserUtil;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import java.util.Map;
import java.util.logging.Logger;

public class AccountActionService
{
    private static Logger logger;
    private static AccountActionService accountActionService;
    
    public static AccountActionService getInstance() {
        if (AccountActionService.accountActionService == null) {
            AccountActionService.accountActionService = new AccountActionService();
        }
        return AccountActionService.accountActionService;
    }
    
    public void userAccountService(final Map requestDetails) throws APIException {
        try {
            if (!ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured()) {
                throw new APIException("SMTP002");
            }
            final CoreUserUtil coreUserUtil = new CoreUserUtil();
            Long userId = null;
            final String mail = requestDetails.get("mailId");
            String name = null;
            String domainName = null;
            final Criteria criteria = new Criteria(Column.getColumn("AaaContactInfo", "EMAILID"), (Object)mail, 0);
            final DataObject dataObject = coreUserUtil.getContactDOFromMail(criteria);
            if (dataObject == null || dataObject.isEmpty()) {
                return;
            }
            final int accountStatus = (int)dataObject.getFirstValue("AaaAccountStatusExtn", "STATUS");
            if (accountStatus == 1) {
                throw new APIException(Response.Status.PRECONDITION_FAILED, "UAC013", "ems.admin.uac.invalid_mail_id");
            }
            final Iterator<Row> loginRows = dataObject.getRows("AaaLogin");
            while (loginRows.hasNext()) {
                final Row loginRow = loginRows.next();
                if (userId != null) {
                    throw new APIException(Response.Status.PRECONDITION_FAILED, "UAC012", "ems.admin.admin.user_email_duplicate");
                }
                userId = loginRow.getLong("USER_ID");
                name = loginRow.getString("NAME");
                domainName = loginRow.getString("DOMAINNAME");
            }
            final String userMail = (String)dataObject.getFirstValue("AaaContactInfo", "EMAILID");
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("newUser", false);
            jsonObject.put("userEmail", (Object)userMail);
            jsonObject.put("domainName", (Object)domainName);
            jsonObject.put("technicianID", (Object)userId);
            jsonObject.put("userID", (Object)userId);
            jsonObject.put("eventCode", (Object)UserConstants.UserAlertConstant.THIRD_PARTY_RESET_PASSWORD);
            if (domainName == null || domainName.isEmpty() || domainName.equalsIgnoreCase("-")) {
                new CoreUserUtil().addOrUpdateUserStatus(dataObject, jsonObject);
            }
            else {
                final Map alertResponse = new CoreUserUtil().sendAccountPasswordResetMail(jsonObject);
                jsonObject.put("mailSent", alertResponse.get("status").equals("success"));
            }
            final boolean mailStatus = jsonObject.optBoolean("mailSent");
            String remarks = "ems.user.uac.reset_password_failed";
            if (mailStatus) {
                remarks = "ems.user.security.reset_password_initiated";
            }
            AlertsUtil.getInstance().addAlert(AlertConstants.USER_RESET_PASSWORD_INITIATED_ALERT, remarks, (Object)(domainName + "@@@" + name));
            if (!mailStatus) {
                throw new APIException(Response.Status.EXPECTATION_FAILED, "UAC019", "ems.admin.admin.email_server_not_reachable");
            }
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            AccountActionService.logger.log(Level.WARNING, "Exception while sent password link", ex2);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    public Map<String, Object> getProductMeta() throws APIException {
        final Map<String, Object> productMeta = new HashMap<String, Object>();
        SyMUtil.getInstance();
        final String isRestictedLogin = com.me.devicemanagement.framework.server.util.SyMUtil.getSyMParameter("SHOW_RESTICTED_LOGIN");
        productMeta.put("productTitle", ProductUrlLoader.getInstance().getValue("productname"));
        productMeta.put("productDisplayName", ProductUrlLoader.getInstance().getValue("displayname"));
        productMeta.put("logoImagePath", CustomerInfoUtil.getInstance().getRebrandLogoPathForWebConsole());
        productMeta.put("productUrl", ProductUrlLoader.getInstance().getValue("prodUrl"));
        productMeta.put("isRestrictedLogin", isRestictedLogin != null && isRestictedLogin.equalsIgnoreCase("true"));
        productMeta.put("productCode", ProductUrlLoader.getInstance().getValue("productcode"));
        productMeta.put("playStoreLink", ProductUrlLoader.getInstance().getValue("playstorelink", ""));
        productMeta.put("iTunesLink", ProductUrlLoader.getInstance().getValue("ituneslink", ""));
        productMeta.put("architectureUrl", ProductUrlLoader.getInstance().getValue("architectureHelpLink", "-"));
        productMeta.put("featureUrl", ProductUrlLoader.getInstance().getValue("featuresHelpLink", "-"));
        productMeta.put("productWebsiteUrl", ProductUrlLoader.getInstance().getValue("websiteLink", "-"));
        productMeta.put("helpUrls", new CommonService().getHelpURLs());
        final Map<String, Object> map = productMeta;
        final String s = "careNumbers";
        new CoreUserUtil();
        map.put(s, CoreUserUtil.getCareNumbersUIListJson());
        return productMeta;
    }
    
    static {
        AccountActionService.logger = Logger.getLogger("UserManagementLogger");
    }
}
