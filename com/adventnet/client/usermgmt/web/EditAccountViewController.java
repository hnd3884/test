package com.adventnet.client.usermgmt.web;

import com.adventnet.persistence.Row;
import java.util.List;
import java.util.Map;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.i18n.I18N;
import com.adventnet.authentication.PasswordException;
import java.util.ArrayList;
import com.adventnet.authentication.util.AuthUtil;
import org.apache.struts.action.ActionForward;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Persistence;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.client.components.form.web.AjaxFormController;

public class EditAccountViewController extends AjaxFormController
{
    private static Logger logger;
    
    public String processPreRendering(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String viewUrl) throws Exception {
        EditAccountViewController.logger.log(Level.FINEST, "processPreRendering called in EditAccountViewController");
        final String loginName = request.getUserPrincipal().getName();
        EditAccountViewController.logger.log(Level.FINEST, "Login Name is : " + loginName);
        final SelectQueryImpl queryImpl = new SelectQueryImpl(new Table("AaaLogin"));
        Column column = new Column("AaaLogin", "USER_ID");
        queryImpl.addSelectColumn(column);
        column = new Column("AaaLogin", "LOGIN_ID");
        queryImpl.addSelectColumn(column);
        column = new Column("AaaLogin", "NAME");
        queryImpl.addSelectColumn(column);
        column = new Column("AaaContactInfo", "CONTACTINFO_ID");
        queryImpl.addSelectColumn(column);
        column = new Column("AaaContactInfo", "EMAILID");
        queryImpl.addSelectColumn(column);
        Join join = new Join("AaaLogin", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
        queryImpl.addJoin(join);
        join = new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2);
        queryImpl.addJoin(join);
        final Criteria criteria = new Criteria(new Column("AaaLogin", "NAME"), (Object)loginName, 0);
        queryImpl.setCriteria(criteria);
        final Persistence persistence = LookUpUtil.getPersistence();
        final DataObject dObj = persistence.get((SelectQuery)queryImpl);
        EditAccountViewController.logger.finest("DataObject is : " + dObj);
        final String emailId = (String)dObj.getFirstValue("AaaContactInfo", "EMAILID");
        EditAccountViewController.logger.finest("Email Id is : " + emailId);
        request.setAttribute("EmailId", (Object)emailId);
        return super.processPreRendering(viewCtx, request, response, viewUrl);
    }
    
    public ActionForward processEvent(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String eventType) throws Exception {
        EditAccountViewController.logger.log(Level.FINEST, "processEvent called in EditAccountViewController");
        String msgKey = "acl.editAccountSettings.success";
        boolean isSuccessMsg = true;
        try {
            final String oldPassword = request.getParameter("oldPassword");
            final String newPassword = request.getParameter("newPassword");
            final String confirmPassword = request.getParameter("confirmPassword");
            final String serviceName = "System";
            final String loginName = request.getUserPrincipal().getName();
            AuthUtil.changePassword(loginName, serviceName, oldPassword, newPassword);
            final Persistence persistence = LookUpUtil.getPersistence();
            final List tables = new ArrayList();
            tables.add("AaaLogin");
            tables.add("AaaUser");
            tables.add("AaaUserContactInfo");
            tables.add("AaaContactInfo");
            final Column col = new Column("AaaLogin", "NAME");
            final Criteria criteria = new Criteria(col, (Object)loginName, 0);
            final DataObject dobj = persistence.get(tables, criteria);
            final String emailaddress = request.getParameter("emailaddress");
            final Row row = dobj.getFirstRow("AaaContactInfo");
            row.set("EMAILID", (Object)emailaddress);
            dobj.updateRow(row);
            EditAccountViewController.logger.finest("DataObject to update is : " + dobj);
            persistence.update(dobj);
        }
        catch (final PasswordException pe) {
            EditAccountViewController.logger.log(Level.SEVERE, pe.getMessage(), (Throwable)pe);
            msgKey = "acl.editAccountIncorrectPWFailure";
            isSuccessMsg = false;
        }
        catch (final Exception e) {
            EditAccountViewController.logger.log(Level.SEVERE, "Exception in editing account settings", e);
            isSuccessMsg = false;
            msgKey = "acl.editAccountSettings.failure";
        }
        return WebViewAPI.sendResponse(request, response, isSuccessMsg, I18N.getMsg(msgKey, new Object[0]), (Map)null);
    }
    
    static {
        EditAccountViewController.logger = Logger.getLogger(EditAccountViewController.class.getName());
    }
}
