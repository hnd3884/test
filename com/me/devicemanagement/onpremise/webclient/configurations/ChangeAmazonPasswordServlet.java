package com.me.devicemanagement.onpremise.webclient.configurations;

import java.io.IOException;
import javax.servlet.ServletException;
import com.me.devicemanagement.framework.webclient.common.QuickLoadUtil;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import java.util.logging.Level;
import com.adventnet.iam.xss.IAMEncoder;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class ChangeAmazonPasswordServlet extends HttpServlet
{
    private Logger logger;
    
    public ChangeAmazonPasswordServlet() {
        this.logger = Logger.getLogger(ChangeAmazonPasswordServlet.class.getName());
    }
    
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String loginName = null;
        try {
            loginName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            final boolean isDefaultPasswordChanged = Boolean.parseBoolean(SyMUtil.getServerParameter("IS_AMAZON_DEFAULT_PASSWORD_CHANGED"));
            final boolean isAWSLogin = Boolean.parseBoolean(SyMUtil.getServerParameter("IS_AWS_LOGIN"));
            if (!isAWSLogin || !loginName.equals("admin") || isDefaultPasswordChanged) {
                response.sendError(401, I18N.getMsg("desktopcentral.common.access_denied", new Object[0]));
                return;
            }
            final String productCode = ProductUrlLoader.getInstance().getValue("productcode");
            final String newUserPassword = request.getParameter("newUserPassword");
            SYMClientUtil.changeDefaultAwsPassword(loginName, newUserPassword);
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println(I18N.getMsg("dc.admin.uac.PWD_CHANGE_SUCCESS", new Object[] { IAMEncoder.encodeHTML(loginName) }));
            this.logger.log(Level.INFO, I18N.getMsg("dc.admin.uac.PWD_CHANGE_SUCCESS", new Object[] { loginName }));
            this.logger.log(Level.INFO, "AMAZON DEFAULT PASSWORD CHANGED SUCCESSFULLY ", loginName);
            DCEventLogUtil.getInstance().addEvent(713, ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName(), (HashMap)null, "dc.admin.uac.PWD_CHANGE_SUCCESS", (Object)loginName, true);
            final String redirectURL = this.getInitParameter(productCode);
            if (redirectURL == null || redirectURL.isEmpty()) {
                QuickLoadUtil.redirectURL(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/webclient", request, response);
            }
            else {
                QuickLoadUtil.redirectURL(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + redirectURL, request, response);
            }
        }
        catch (final Exception ex) {
            try {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().println(I18N.getMsg("dc.admin.uac.PWD_CHANGE_EXC", new Object[] { IAMEncoder.encodeHTML(loginName) }));
                this.logger.log(Level.INFO, I18N.getMsg("dc.admin.uac.PWD_CHANGE_EXC", new Object[] { loginName }), ex);
            }
            catch (final Exception e) {
                this.logger.log(Level.INFO, "Error occurred in ChangeAmazonPasswordServlet", e);
            }
            this.logger.log(Level.INFO, "AMAZON DEFAULT PASSWORD CHANGE FAILED", loginName);
        }
    }
}
