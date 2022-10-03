package com.me.devicemanagement.framework.webclient.authorization;

import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Persistence;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.List;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Random;
import org.apache.commons.lang.RandomStringUtils;
import java.security.SecureRandom;
import com.me.devicemanagement.framework.server.common.DMModuleHandler;
import com.adventnet.i18n.I18N;
import java.util.TreeMap;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.client.components.form.web.AjaxFormController;

public class RoleController extends AjaxFormController
{
    private static Logger logger;
    
    public void processPostRendering(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        super.processPostRendering(viewCtx, request, response);
    }
    
    public String processPreRendering(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String viewUrl) throws Exception {
        try {
            super.processPreRendering(viewCtx, request, response, viewUrl);
            final String actionToCall = request.getParameter("actionToCall");
            TreeMap tm = new TreeMap();
            TreeMap tmselected = null;
            TreeMap tmModules = null;
            String display = I18N.getMsg("dc.common.add", new Object[0]);
            String actionFor = "AddRole";
            String roleName = "";
            String roleDesc = "";
            String roleID = "";
            if (actionToCall != null && actionToCall.equalsIgnoreCase("addForm")) {
                tm = this.getRoleMap();
            }
            else if (actionToCall != null && actionToCall.equalsIgnoreCase("modForm")) {
                tm = this.getRoleMap();
                roleID = request.getParameter("roleID");
                tmselected = this.getSelectedID(roleID);
                display = I18N.getMsg("dc.common.UPDATE", new Object[0]);
                actionFor = "UpdateRole";
                roleName = request.getParameter("roleName");
                roleDesc = request.getParameter("roleDesc");
                roleDesc = I18N.getMsg(roleDesc, new Object[0]);
            }
            tmModules = this.getLicenseModules();
            viewCtx.getRequest().setAttribute("ROLELIST", (Object)tm);
            viewCtx.getRequest().setAttribute("SELECTEDROLELIST", (Object)tmselected);
            viewCtx.getRequest().setAttribute("DISPLAY", (Object)display);
            viewCtx.getRequest().setAttribute("ACTIONFOR", (Object)actionFor);
            viewCtx.getRequest().setAttribute("ROLEID", (Object)roleID);
            viewCtx.getRequest().setAttribute("ROLENAME", (Object)roleName);
            viewCtx.getRequest().setAttribute("ROLEDESC", (Object)roleDesc);
            viewCtx.getRequest().setAttribute("MODULES", (Object)tmModules);
            viewCtx.getRequest().setAttribute("isOSDEnabled", (Object)DMModuleHandler.isOSDEnabled());
            final String salt = RandomStringUtils.random(20, 0, 0, true, true, (char[])null, (Random)new SecureRandom());
            viewCtx.getRequest().getSession().setAttribute("rolePageCsrfPreventionSalt", (Object)salt);
            viewCtx.getRequest().setAttribute("allowAddRole", (Object)ApiFactoryProvider.getAuthUtilAccessAPI().isSuperAdminVerified());
        }
        catch (final Exception e) {
            RoleController.logger.log(Level.SEVERE, "Error occured in  processPreRendering  method", e);
        }
        return viewUrl;
    }
    
    private TreeMap getRoleMap() {
        try {
            final TreeMap tmRole = new TreeMap();
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("AaaRole"));
            final ArrayList columnList = new ArrayList();
            columnList.add(Column.getColumn("AaaRole", "ROLE_ID"));
            columnList.add(Column.getColumn("AaaRole", "NAME"));
            sq.addSelectColumns((List)columnList);
            final Persistence per = SyMUtil.getPersistence();
            final DataObject dataObj = per.get(sq);
            if (dataObj != null) {
                final Iterator ite = dataObj.getRows("AaaRole");
                while (ite.hasNext()) {
                    final Row r = ite.next();
                    final String name = (String)r.get("NAME");
                    final Long roleID = (Long)r.get("ROLE_ID");
                    tmRole.put(name, roleID);
                }
            }
            return tmRole;
        }
        catch (final Exception e) {
            RoleController.logger.log(Level.SEVERE, "Error occured in  getRoleMap  method", e);
            return null;
        }
    }
    
    private TreeMap getSelectedID(final String roleid) {
        try {
            final TreeMap tmSelectedRoles = new TreeMap();
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("UMRoleModuleRelation"));
            final ArrayList columnList = new ArrayList();
            columnList.add(Column.getColumn("UMRoleModuleRelation", "UM_ROLE_ID"));
            columnList.add(Column.getColumn("UMModule", "UM_MODULE_ID"));
            columnList.add(Column.getColumn("UMModule", "ROLE_ID"));
            columnList.add(Column.getColumn("AaaRole", "ROLE_ID"));
            columnList.add(Column.getColumn("AaaRole", "NAME"));
            final Join join = new Join("UMRoleModuleRelation", "UMModule", new String[] { "UM_MODULE_ID" }, new String[] { "UM_MODULE_ID" }, 1);
            final Join join2 = new Join("UMModule", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 1);
            sq.addJoin(join);
            sq.addJoin(join2);
            sq.addJoin(new Join("UMModule", "DCUserModuleExtn", new String[] { "DC_MODULE_ID" }, new String[] { "MODULE_ID" }, 2));
            sq.addSelectColumns((List)columnList);
            final Criteria c = new Criteria(Column.getColumn("UMRoleModuleRelation", "UM_ROLE_ID"), (Object)roleid, 0);
            sq.setCriteria(c);
            final Persistence per = SyMUtil.getPersistence();
            final DataObject dataObj = per.get(sq);
            if (dataObj != null) {
                final Iterator ite = dataObj.getRows("AaaRole");
                while (ite.hasNext()) {
                    final Row r = ite.next();
                    final String name = (String)r.get("NAME");
                    tmSelectedRoles.put(name, name);
                }
            }
            return tmSelectedRoles;
        }
        catch (final Exception e) {
            RoleController.logger.log(Level.SEVERE, "Error occured in  getSelectedID  method", e);
            return new TreeMap();
        }
    }
    
    private TreeMap getLicenseModules() {
        try {
            final TreeMap tmLicModules = new TreeMap();
            final String productType = LicenseProvider.getInstance().getProductType();
            Criteria cri = null;
            if (productType.equalsIgnoreCase("Standard")) {
                cri = new Criteria(Column.getColumn("UMModule", "LICENSE_TYPE"), (Object)"S", 12);
            }
            else if (productType.equalsIgnoreCase("TOOLSADDON")) {
                cri = new Criteria(Column.getColumn("UMModule", "LICENSE_TYPE"), (Object)"T", 12);
            }
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("UMModule"));
            sq.addSelectColumn(new Column((String)null, "*"));
            final Join join = new Join("UMModule", "DCUserModuleExtn", new String[] { "DC_MODULE_ID" }, new String[] { "MODULE_ID" }, 1);
            sq.addJoin(join);
            sq.addJoin(new Join("DCUserModuleExtn", "DCUserModule", new String[] { "MODULE_ID" }, new String[] { "MODULE_ID" }, 2));
            if (LicenseProvider.getInstance().getMDMLicenseAPI() != null) {
                final String mdmLiceseEditionType;
                final String mdmEdition = mdmLiceseEditionType = LicenseProvider.getInstance().getMDMLicenseAPI().getMDMLiceseEditionType();
                LicenseProvider.getInstance().getMDMLicenseAPI();
                if (mdmLiceseEditionType.equals("Standard")) {
                    final Criteria mdmEditionCrti = new Criteria(Column.getColumn("UMModule", "LICENSE_TYPE"), (Object)"S", 0, false).negate().and(new Criteria(Column.getColumn("DCUserModule", "MODULE_NAME"), (Object)"MDM", 10, false).or(new Criteria(Column.getColumn("DCUserModule", "MODULE_NAME"), (Object)"ModernMgmt", 10, false))).negate();
                    cri = ((cri == null) ? mdmEditionCrti : cri.and(mdmEditionCrti));
                }
            }
            sq.setCriteria(cri);
            final DataObject dataObj = SyMUtil.getPersistence().get(sq);
            if (dataObj != null) {
                final Iterator ite = dataObj.getRows("DCUserModule");
                while (ite.hasNext()) {
                    final Row r = ite.next();
                    String name = (String)r.get("MODULE_NAME");
                    name = name.replace(" ", "").trim();
                    tmLicModules.put(name, name);
                }
            }
            return tmLicModules;
        }
        catch (final Exception e) {
            RoleController.logger.log(Level.SEVERE, "Error occured in  getLicenseModules  method", e);
            return new TreeMap();
        }
    }
    
    static {
        RoleController.logger = Logger.getLogger("UserManagementLogger");
    }
}
