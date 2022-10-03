package com.adventnet.client.usermgmt.web;

import java.util.List;
import java.util.Map;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.i18n.I18N;
import com.adventnet.persistence.Row;
import com.adventnet.authentication.util.AuthUtil;
import java.util.ArrayList;
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

public class UserAdminViewController extends AjaxFormController
{
    private static Logger logger;
    
    public String processPreRendering(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String viewUrl) throws Exception {
        UserAdminViewController.logger.log(Level.FINEST, "processPreRendering called in UserAdminViewController");
        final String loginId = request.getParameter("LOGIN_ID");
        UserAdminViewController.logger.finest("Login Id is : " + loginId);
        if (loginId != null) {
            final SelectQueryImpl queryImpl = new SelectQueryImpl(new Table("AaaLogin"));
            Column column = new Column("AaaLogin", "USER_ID");
            queryImpl.addSelectColumn(column);
            column = new Column("AaaLogin", "LOGIN_ID");
            queryImpl.addSelectColumn(column);
            column = new Column("AaaLogin", "NAME");
            queryImpl.addSelectColumn(column);
            column = new Column("AaaAccount", "ACCOUNT_ID");
            queryImpl.addSelectColumn(column);
            column = new Column("AaaAccount", "LOGIN_ID");
            queryImpl.addSelectColumn(column);
            column = new Column("AaaAuthorizedRole", "ACCOUNT_ID");
            queryImpl.addSelectColumn(column);
            column = new Column("AaaAuthorizedRole", "ROLE_ID");
            queryImpl.addSelectColumn(column);
            column = new Column("AaaRole", "ROLE_ID");
            queryImpl.addSelectColumn(column);
            column = new Column("AaaRole", "NAME");
            queryImpl.addSelectColumn(column);
            column = new Column("AaaUserContactInfo", "USER_ID");
            queryImpl.addSelectColumn(column);
            column = new Column("AaaUserContactInfo", "CONTACTINFO_ID");
            queryImpl.addSelectColumn(column);
            column = new Column("AaaContactInfo", "CONTACTINFO_ID");
            queryImpl.addSelectColumn(column);
            column = new Column("AaaContactInfo", "EMAILID");
            queryImpl.addSelectColumn(column);
            Join join = new Join("AaaLogin", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2);
            queryImpl.addJoin(join);
            join = new Join("AaaAccount", "AaaAuthorizedRole", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2);
            queryImpl.addJoin(join);
            join = new Join("AaaAuthorizedRole", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2);
            queryImpl.addJoin(join);
            join = new Join("AaaLogin", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
            queryImpl.addJoin(join);
            join = new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2);
            queryImpl.addJoin(join);
            final Criteria criteria = new Criteria(new Column("AaaLogin", "LOGIN_ID"), (Object)new Long(loginId), 0);
            queryImpl.setCriteria(criteria);
            final Persistence persistence = LookUpUtil.getPersistence();
            final DataObject dObj = persistence.get((SelectQuery)queryImpl);
            UserAdminViewController.logger.finest("DataObject is : " + dObj);
            final String loginName = (String)dObj.getFirstValue("AaaLogin", "NAME");
            request.setAttribute("LoginName", (Object)loginName);
            final String roleName = (String)dObj.getFirstValue("AaaRole", "NAME");
            request.setAttribute("RoleName", (Object)roleName);
            final String emailId = (String)dObj.getFirstValue("AaaContactInfo", "EMAILID");
            request.setAttribute("EmailId", (Object)emailId);
        }
        return super.processPreRendering(viewCtx, request, response, viewUrl);
    }
    
    public ActionForward processEvent(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String eventType) throws Exception {
        UserAdminViewController.logger.log(Level.FINEST, "processEvent called in UserAdminViewController");
        final String loginId = request.getParameter("LOGIN_ID");
        UserAdminViewController.logger.finest("Login Id is : " + loginId);
        String msgKey = null;
        boolean isSuccessMsg = true;
        if (loginId == null) {
            msgKey = "acl.createNewUser.Success";
            final String loginName = request.getParameter("loginName");
            try {
                final String emailaddress = request.getParameter("emailaddress");
                final String roleName = request.getParameter("roles");
                final String password = request.getParameter("password");
                UserManagementUtil.createUser(loginName, password, roleName, emailaddress);
            }
            catch (final Exception e) {
                UserAdminViewController.logger.log(Level.SEVERE, "Exception while adding new user", e);
                isSuccessMsg = false;
                msgKey = "acl.createNewUser.failure";
            }
        }
        else {
            msgKey = "acl.editUser.Success";
            isSuccessMsg = true;
            final String loginName = request.getParameter("loginName");
            try {
                final Persistence persistence = LookUpUtil.getPersistence();
                final List tables = new ArrayList();
                tables.add("AaaLogin");
                tables.add("AaaAccount");
                tables.add("AaaAuthorizedRole");
                tables.add("AaaUser");
                tables.add("AaaUserContactInfo");
                tables.add("AaaContactInfo");
                final Column col = new Column("AaaLogin", "LOGIN_ID");
                final Criteria criteria = new Criteria(col, (Object)new Long(loginId), 0);
                final DataObject dobj = persistence.get(tables, criteria);
                UserAdminViewController.logger.finest("DataObject in edit is : " + dobj);
                if (!loginName.equals("admin")) {
                    final String newRoleName = request.getParameter("roles");
                    final Long roleId = AuthUtil.getRoleId(newRoleName);
                    Row row = dobj.getFirstRow("AaaAuthorizedRole");
                    dobj.deleteRow(row);
                    final DataObject newdobj = persistence.constructDataObject();
                    row = new Row("AaaAuthorizedRole");
                    row.set("ACCOUNT_ID", dobj.getFirstValue("AaaAccount", "ACCOUNT_ID"));
                    row.set("ROLE_ID", (Object)roleId);
                    newdobj.addRow(row);
                    persistence.update(dobj);
                    persistence.add(newdobj);
                }
                final String emailaddress2 = request.getParameter("emailaddress");
                final Row row2 = dobj.getFirstRow("AaaContactInfo");
                row2.set("EMAILID", (Object)emailaddress2);
                dobj.updateRow(row2);
                UserAdminViewController.logger.finest("DataObject to be updated is : " + dobj);
                persistence.update(dobj);
            }
            catch (final Exception e) {
                UserAdminViewController.logger.log(Level.SEVERE, "Exception while editing user", e);
                isSuccessMsg = false;
                msgKey = "acl.editUser.failure";
            }
        }
        return WebViewAPI.sendResponse(request, response, isSuccessMsg, I18N.getMsg(msgKey, new Object[0]), (Map)null);
    }
    
    static {
        UserAdminViewController.logger = Logger.getLogger(UserAdminViewController.class.getName());
    }
}
