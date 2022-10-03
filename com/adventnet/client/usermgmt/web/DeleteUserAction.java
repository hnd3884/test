package com.adventnet.client.usermgmt.web;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Persistence;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import java.util.Map;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.client.util.LookUpUtil;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import java.util.logging.Logger;
import com.adventnet.client.util.web.WebConstants;
import org.apache.struts.action.Action;

public class DeleteUserAction extends Action implements WebConstants
{
    private static Logger logger;
    
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        String msgKey = "acl.deleteUser.success";
        boolean isSuccessMsg = true;
        DeleteUserAction.logger.finest("execute called in DeleteUserAction");
        String loginName = "";
        try {
            final Persistence persistence = LookUpUtil.getPersistence();
            final String userId = request.getParameter("USER_ID");
            final Column col = new Column("AaaLogin", "USER_ID");
            final Criteria crit1 = new Criteria(col, (Object)new Long(userId), 0);
            final DataObject userLoginDo = persistence.get("AaaLogin", crit1);
            final Row rr = userLoginDo.getRow("AaaLogin");
            loginName = (String)rr.get("NAME");
            final String userName = request.getUserPrincipal().getName();
            if ("admin".equals(loginName) || userName.equals(loginName)) {
                msgKey = "Admin user cannot be deleted";
                return WebViewAPI.sendResponse(request, response, isSuccessMsg, msgKey, (Map)null);
            }
            UserManagementUtil.deleteUser(new Long(request.getParameter("USER_ID")), new Long(request.getParameter("CONTACTINFO_ID")));
        }
        catch (final Exception e) {
            DeleteUserAction.logger.log(Level.SEVERE, "Exception while deleting user", e);
            isSuccessMsg = false;
            msgKey = "acl.deleteUser.failure";
        }
        return WebViewAPI.sendResponse(request, response, isSuccessMsg, I18N.getMsg(msgKey, new Object[0]), (Map)null);
    }
    
    static {
        DeleteUserAction.logger = Logger.getLogger(DeleteUserAction.class.getName());
    }
}
