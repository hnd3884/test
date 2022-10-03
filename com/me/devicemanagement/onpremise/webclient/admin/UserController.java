package com.me.devicemanagement.onpremise.webclient.admin;

import com.adventnet.persistence.Row;
import java.util.TreeMap;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.twofactor.TwoFactorAction;
import org.json.JSONObject;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.client.components.form.web.AjaxFormController;

public class UserController extends AjaxFormController
{
    private static Logger logger;
    
    public void processPostRendering(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        super.processPostRendering(viewCtx, request, response);
    }
    
    public String processPreRendering(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String viewUrl) throws Exception {
        super.processPreRendering(viewCtx, request, response, viewUrl);
        final String actionToCall = request.getParameter("actionToCall");
        if (actionToCall != null && actionToCall.equalsIgnoreCase("TwoFactor")) {
            final boolean isMailServerNotConfigured = !ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured();
            request.setAttribute("MAIL_SERVER_NOT_CONFIGURED", (Object)String.valueOf(isMailServerNotConfigured));
            try {
                final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaLogin"));
                query.addJoin(new Join("AaaLogin", "AaaUserStatus", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
                query.addJoin(new Join("AaaUserStatus", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
                query.addJoin(new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
                query.addSelectColumn(new Column("AaaLogin", "USER_ID"));
                query.addSelectColumn(new Column("AaaUserStatus", "USER_ID"));
                query.addSelectColumn(new Column("AaaUserStatus", "STATUS"));
                query.addSelectColumn(new Column("AaaUserContactInfo", "CONTACTINFO_ID"));
                query.addSelectColumn(new Column("AaaUserContactInfo", "USER_ID"));
                query.addSelectColumn(new Column("AaaContactInfo", "CONTACTINFO_ID"));
                query.addSelectColumn(new Column("AaaContactInfo", "EMAILID"));
                Criteria emailCriteria = new Criteria(new Column("AaaContactInfo", "EMAILID"), (Object)"", 0);
                emailCriteria = emailCriteria.and(new Criteria(Column.getColumn("AaaUserStatus", "STATUS"), (Object)"ACTIVE", 12));
                query.setCriteria(emailCriteria);
                final DataObject emailDO = SyMUtil.getPersistence().get(query);
                if (emailDO.isEmpty()) {
                    request.setAttribute("MAIL_NOT_PROVIDED", (Object)"false");
                }
                else {
                    final JSONObject frameworkConfigurations = FrameworkConfigurations.getFrameworkConfigurations();
                    final JSONObject userAccountJsonObject = (JSONObject)frameworkConfigurations.get("user_account_handling");
                    if (userAccountJsonObject != null && userAccountJsonObject.get("enable_tfa_plugin_users").equals("true")) {
                        request.setAttribute("MAIL_NOT_PROVIDED", (Object)"true");
                    }
                    else {
                        final int dcUserSize = getDOSize(emailDO, "AaaContactInfo");
                        final SelectQuery sdpUserQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DCAaaLogin"));
                        sdpUserQuery.addJoin(new Join("DCAaaLogin", "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
                        sdpUserQuery.addJoin(new Join("AaaLogin", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
                        sdpUserQuery.addJoin(new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
                        sdpUserQuery.addSelectColumn(new Column("DCAaaLogin", "LOGIN_ID"));
                        sdpUserQuery.addSelectColumn(new Column("AaaLogin", "LOGIN_ID"));
                        sdpUserQuery.addSelectColumn(new Column("AaaLogin", "USER_ID"));
                        sdpUserQuery.addSelectColumn(new Column("AaaContactInfo", "CONTACTINFO_ID"));
                        sdpUserQuery.addSelectColumn(new Column("AaaContactInfo", "EMAILID"));
                        sdpUserQuery.addSelectColumn(new Column("AaaUserContactInfo", "CONTACTINFO_ID"));
                        sdpUserQuery.addSelectColumn(new Column("AaaUserContactInfo", "USER_ID"));
                        emailCriteria = new Criteria(new Column("AaaContactInfo", "EMAILID"), (Object)"", 0);
                        sdpUserQuery.setCriteria(emailCriteria);
                        final DataObject sdpUserDO = SyMUtil.getPersistence().get(sdpUserQuery);
                        final int sdpUserSize = getDOSize(sdpUserDO, "AaaContactInfo");
                        if (dcUserSize == sdpUserSize) {
                            request.setAttribute("MAIL_NOT_PROVIDED", (Object)"false");
                        }
                        else {
                            request.setAttribute("MAIL_NOT_PROVIDED", (Object)"true");
                        }
                    }
                }
                final String isDemoMode = String.valueOf(ApiFactoryProvider.getDemoUtilAPI().isDemoMode());
                request.setAttribute("isDemoMode", (Object)isDemoMode);
                final int otpTimeout = TwoFactorAction.getOtpTimeout();
                request.setAttribute("otpTimeout", (Object)otpTimeout);
            }
            catch (final Exception ex) {
                UserController.logger.log(Level.WARNING, "Caught exception while fetching data", ex);
            }
        }
        final String authType = TwoFactorAction.getTwoFactorAuthType();
        request.setAttribute("TWOFACTORAUTH_TYPE", (Object)authType);
        request.setAttribute("actionToCall", (Object)actionToCall);
        return viewUrl;
    }
    
    public static int getDOSize(final DataObject dobj, final String tableName) {
        return getDOSize(dobj, tableName, null);
    }
    
    public static int getDOSize(final DataObject dobj, final String tableName, final Criteria criteria) {
        int size = 0;
        try {
            size = ((criteria != null) ? getIteratorSize(dobj.getRows(tableName, criteria)) : (dobj.isEmpty() ? 0 : dobj.size(tableName)));
        }
        catch (final Exception ex) {
            UserController.logger.log(Level.SEVERE, "Error occured in  getting DO size", ex);
        }
        return size;
    }
    
    public static Integer getIteratorSize(final Iterator itr) {
        Integer size = 0;
        while (itr.hasNext()) {
            itr.next();
            ++size;
        }
        return size;
    }
    
    public static TreeMap<String, String> getADDomainNamesForLoginPage() {
        try {
            TreeMap<String, String> tmDomain = null;
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("ActiveDirectoryInfo"));
            final Join loginJoin = new Join("ActiveDirectoryInfo", "AaaLogin", new String[] { "DEFAULTDOMAIN" }, new String[] { "DOMAINNAME" }, 2);
            final Join userStatusJoin = new Join("AaaLogin", "AaaUserStatus", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
            query.addJoin(loginJoin);
            query.addJoin(userStatusJoin);
            query.addSelectColumn(new Column("ActiveDirectoryInfo", "AD_ID"));
            query.addSelectColumn(new Column("ActiveDirectoryInfo", "DEFAULTDOMAIN"));
            query.addSelectColumn(new Column("AaaLogin", "DOMAINNAME"));
            query.setCriteria(new Criteria(Column.getColumn("AaaUserStatus", "STATUS"), (Object)"ACTIVE", 0));
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            if (!dobj.isEmpty()) {
                tmDomain = new TreeMap<String, String>();
                final Iterator resRows = dobj.getRows("ActiveDirectoryInfo");
                while (resRows.hasNext()) {
                    final Row resRow = resRows.next();
                    final String domainName = (String)resRow.get("DEFAULTDOMAIN");
                    tmDomain.put(domainName, domainName);
                }
            }
            return tmDomain;
        }
        catch (final Exception e) {
            UserController.logger.log(Level.SEVERE, "Error occured in  getDomainList  method", e);
            return null;
        }
    }
    
    static {
        UserController.logger = Logger.getLogger("UserManagementLogger");
    }
}
