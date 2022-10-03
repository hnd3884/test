package com.me.devicemanagement.onpremise.server.twofactor;

import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.onpremise.server.alerts.AlertConstants;
import com.me.devicemanagement.framework.server.alerts.AlertsUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.ems.onpremise.uac.core.UserManagementUtil;
import com.adventnet.authentication.Credential;
import com.me.devicemanagement.framework.webclient.common.QuickLoadUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.authentication.util.AuthUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.security.DMCookieUtil;
import javax.servlet.http.HttpServletResponse;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.Calendar;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import javax.servlet.http.Cookie;
import java.net.URLEncoder;
import java.util.Locale;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.logging.Logger;
import com.adventnet.authentication.twofactor.TwoFactorAuthImpl;

public class TwoFactorPassword extends TwoFactorAuthImpl
{
    private static Logger logger;
    
    public boolean handle(final Long userId, final ServletRequest req, final ServletResponse response) throws Exception {
        final HttpServletRequest request = (HttpServletRequest)req;
        boolean isMailServerConfigured = ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured();
        isMailServerConfigured = (TwoFactorAction.getEmailId(userId).trim().length() > 1 && isMailServerConfigured);
        request.getServletContext().setAttribute("mailServerConfigured", (Object)isMailServerConfigured);
        Boolean userCookie = false;
        if (request.getParameter("browserLocale") != null) {
            final String browser = request.getParameter("browserLocale");
            final Locale browserLocale = new Locale(browser, "");
            request.getSession().setAttribute("browserLocale", (Object)browserLocale);
        }
        if (request.getParameter("otpTimeout") != null) {
            request.getSession().setAttribute("otpTimeout", (Object)request.getParameter("otpTimeout"));
        }
        if (request.getParameter("restrictedLoginPage") != null) {
            request.getSession().setAttribute("restrictedLoginPage", (Object)request.getParameter("restrictedLoginPage"));
        }
        final String username = TwoFactorAction.getUserName(userId);
        final Cookie[] cookies = request.getCookies();
        for (int i = 0; i < cookies.length; ++i) {
            if (cookies[i].getName().equals(URLEncoder.encode(username, "UTF-8"))) {
                final String token = cookies[i].getValue();
                if (this.isValidToken(username, token)) {
                    userCookie = true;
                }
            }
        }
        if (request.getParameter("loginStatus") != null && request.getParameter("loginStatus").equalsIgnoreCase("failed")) {
            return true;
        }
        if (userCookie) {
            return false;
        }
        final String authType = TwoFactorAction.getTwoFactorAuthType();
        if (authType.equalsIgnoreCase("mail")) {
            final MailTwoFactorPassword mail = new MailTwoFactorPassword();
            return mail.handleAuth(userId, request);
        }
        if (authType.equalsIgnoreCase("googleApp")) {
            final GoogleTwoFactorPassword google = new GoogleTwoFactorPassword();
            return google.handleAuth(userId, (ServletRequest)request);
        }
        return false;
    }
    
    private boolean isValidToken(final String username, final String token) throws Exception {
        try {
            Criteria criteria = new Criteria(Column.getColumn("TwoFactorTokenDetails", "USER_NAME"), (Object)username, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("TwoFactorTokenDetails", "TOKEN"), (Object)token, 0));
            final DataObject dataObject = SyMUtil.getPersistence().get("TwoFactorTokenDetails", criteria);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("TwoFactorTokenDetails");
                final Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(row.get("CREATED_TIME").toString()));
                calendar.add(5, Integer.valueOf(row.get("REMEMBER_ME").toString()));
                if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
                    return true;
                }
            }
        }
        catch (final Exception ex) {
            TwoFactorPassword.logger.log(Level.SEVERE, "Exception while checking two factor token available " + ex);
        }
        return false;
    }
    
    public void setCookie(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            final int timeout = Integer.parseInt(request.getParameter("otpTimeout"));
            final String userName = (String)request.getSession().getAttribute("username");
            final Long userId = (Long)request.getSession().getAttribute("userId");
            final String twoFactorToken = this.generateTwoFactorToken(userId);
            final Cookie userCookie = DMCookieUtil.generateDMCookies(request, URLEncoder.encode(userName, "UTF-8"), twoFactorToken);
            this.addTwoFactorToken(userName, twoFactorToken, timeout);
            final int otpTimeout = timeout * 24 * 60 * 60;
            userCookie.setMaxAge(otpTimeout);
            userCookie.setSecure(true);
            userCookie.setHttpOnly(true);
            response.addCookie(userCookie);
            TwoFactorPassword.logger.log(Level.INFO, "Cookie added successfully");
        }
        catch (final Exception e) {
            TwoFactorPassword.logger.log(Level.SEVERE, "Exception while adding cookie " + e);
        }
    }
    
    private void addTwoFactorToken(final String userName, final String twoFactorToken, final int noOfDays) throws Exception {
        try {
            final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
            final Row row = new Row("TwoFactorTokenDetails");
            row.set("USER_NAME", (Object)userName);
            row.set("TOKEN", (Object)twoFactorToken);
            row.set("REMEMBER_ME", (Object)noOfDays);
            row.set("CREATED_TIME", (Object)System.currentTimeMillis());
            dataObject.addRow(row);
            SyMUtil.getPersistence().add(dataObject);
            TwoFactorPassword.logger.log(Level.INFO, "Two factor token added to DB successfully ");
        }
        catch (final Exception ex) {
            TwoFactorPassword.logger.log(Level.SEVERE, "Exception while adding two factor token to DB " + ex);
        }
    }
    
    private String generateTwoFactorToken(final Long userID) throws Exception {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaPassword"));
            final Criteria criteria = new Criteria(Column.getColumn("AaaLogin", "USER_ID"), (Object)userID, 0);
            selectQuery.addSelectColumn(Column.getColumn("AaaPassword", "*"));
            selectQuery.addJoin(new Join("AaaPassword", "AaaAccPassword", new String[] { "PASSWORD_ID" }, new String[] { "PASSWORD_ID" }, 2));
            selectQuery.addJoin(new Join("AaaAccPassword", "AaaAccount", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
            selectQuery.addJoin(new Join("AaaAccount", "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final MailTwoFactorPassword mailTwoFactorPassword = new MailTwoFactorPassword();
                final Row row = dataObject.getFirstRow("AaaPassword");
                String token = mailTwoFactorPassword.generateRandomPassword() + "" + row.get("SALT") + "" + mailTwoFactorPassword.generateRandomPassword();
                token = AuthUtil.getEncryptedPassword(token, (String)row.get("SALT"), (String)row.get("ALGORITHM"));
                return token;
            }
        }
        catch (final Exception ex) {
            TwoFactorPassword.logger.log(Level.SEVERE, "Exception while generating two factor token " + ex);
        }
        return "TwoFactor";
    }
    
    private void redirect(final HttpServletRequest request, final HttpServletResponse response) {
        if (this.isAccountLocked(request)) {
            this.redirectToBadLoginPage(request, response);
        }
        else {
            final String url = "two_fact_auth?loginStatus=failed";
            QuickLoadUtil.redirectURL(url, request, response);
        }
    }
    
    private void redirectToBadLoginPage(final HttpServletRequest request, final HttpServletResponse response) {
        request.getSession().setAttribute("2FactorPrincipal", (Object)null);
        request.setAttribute("login_status", (Object)"badlogin");
        request.getSession(false).setAttribute("login_status", (Object)"badlogin");
        final String url = "/configurations";
        QuickLoadUtil.redirectURL(url, request, response);
    }
    
    public boolean validate(final Long userId, final HttpServletRequest request, final HttpServletResponse response) {
        try {
            if (this.isAccountLocked(request)) {
                this.redirectToBadLoginPage(request, response);
                return false;
            }
            final String authType = TwoFactorAction.getTwoFactorAuthType();
            if (authType.equalsIgnoreCase("mail")) {
                final MailTwoFactorPassword mail = new MailTwoFactorPassword();
                if (mail.validate(userId, request)) {
                    if (request.getParameter("rememberMe") != null) {
                        this.setCookie(request, response);
                    }
                    return true;
                }
                this.handleOtpFailure(request);
                this.redirect(request, response);
                this.addUserAlerts(userId);
                return false;
            }
            else if (authType.equalsIgnoreCase("googleApp")) {
                final GoogleTwoFactorPassword google = new GoogleTwoFactorPassword();
                if (google.validate(userId, request)) {
                    if (request.getParameter("rememberMe") != null) {
                        this.setCookie(request, response);
                    }
                    return true;
                }
                this.handleOtpFailure(request);
                this.addUserAlerts(userId);
                this.redirect(request, response);
                return false;
            }
        }
        catch (final Exception e) {
            TwoFactorPassword.logger.log(Level.SEVERE, "Exception in validation of TwoFactorPassword " + e);
        }
        return false;
    }
    
    private void handleOtpFailure(final HttpServletRequest request) {
        final Credential cred = (Credential)request.getSession().getValue("com.adventnet.authentication.Credential");
        final String username = cred.getLoginName();
        final String domain = cred.getDomainName();
        final String service = cred.getServiceName();
        final String hostName = cred.getHostName();
        UserManagementUtil.updateBadLoginCount(username, service, domain, hostName);
    }
    
    private boolean isAccountLocked(final HttpServletRequest request) {
        final Credential cred = (Credential)request.getSession().getValue("com.adventnet.authentication.Credential");
        final String username = cred.getLoginName();
        final String domain = cred.getDomainName();
        final String service = cred.getServiceName();
        return UserManagementUtil.isAccountLocked(username, service, domain);
    }
    
    public void twoFactorTokenCleanUp() throws Exception {
        try {
            final Calendar calendar = Calendar.getInstance();
            calendar.add(5, -180);
            final Criteria criteria = new Criteria(Column.getColumn("TwoFactorTokenDetails", "CREATED_TIME"), (Object)calendar.getTimeInMillis(), 7);
            SyMUtil.getPersistence().delete(criteria);
            TwoFactorPassword.logger.log(Level.INFO, "Two factor token cleanup completed successfully ");
        }
        catch (final Exception ex) {
            TwoFactorPassword.logger.log(Level.SEVERE, "Exception in cleaning two factor token details " + ex);
        }
    }
    
    public void addUserAlerts(final Long userId) throws SyMException {
        String username = DMUserHandler.getUserNameFromUserID(userId);
        final String domainname = DMUserHandler.getDCUserDomain(DMUserHandler.getLoginIdForUserId(userId));
        username = domainname + "\\" + username;
        AlertsUtil.getInstance().addAlert(AlertConstants.USER_INCORRECT_OTP_ALERT, "ems.user.security.user_otp_incorrect", (Object)username);
    }
    
    static {
        TwoFactorPassword.logger = Logger.getLogger("UserManagementLogger");
    }
}
