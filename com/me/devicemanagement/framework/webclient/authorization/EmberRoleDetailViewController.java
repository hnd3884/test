package com.me.devicemanagement.framework.webclient.authorization;

import java.util.TreeMap;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.common.DMModuleHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.common.DMApplicationHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DMViewRetrieverAction;

public class EmberRoleDetailViewController extends DMViewRetrieverAction
{
    private static String className;
    private static Logger logger;
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final TreeMap roles = SyMUtil.getInstance().getRoleList(LicenseProvider.getInstance().getProductType());
            Criteria criteria = new Criteria(Column.getColumn("UMRole", "UM_ROLE_ID"), (Object)roles.values().toArray(), 8);
            criteria = criteria.and(new Criteria(Column.getColumn("UMRole", "STATUS"), (Object)SyMUtil.getVisibleUMRoles(), 8));
            if (DMApplicationHandler.isMdmProduct()) {
                criteria = criteria.and(new Criteria(Column.getColumn("UMRole", "UM_ROLE_NAME"), (Object)"Mobile Device Manager", 1));
            }
            if (CustomerInfoUtil.isOSDProduct()) {
                final String[] toExcludeRoles = { "Remote Desktop Viewer", "IT Asset Manager", "Patch Manager", "Mobile Device Manager", "Auditor", "Technician" };
                criteria = criteria.and(new Criteria(Column.getColumn("UMRole", "UM_ROLE_NAME"), (Object)toExcludeRoles, 9));
            }
            else if (!DMModuleHandler.isOSDEnabled() || CustomerInfoUtil.getInstance().isMSP()) {
                criteria = criteria.and(new Criteria(Column.getColumn("UMRole", "UM_ROLE_NAME"), (Object)"OS Deployer", 1));
            }
            selectQuery.setCriteria(criteria);
        }
        catch (final Exception e) {
            EmberRoleDetailViewController.logger.log(Level.INFO, EmberRoleDetailViewController.className + ":setCritetia:: Exception while setting license Criteria.", e);
        }
        super.setCriteria(selectQuery, viewCtx);
    }
    
    static {
        EmberRoleDetailViewController.className = RoleDetailViewController.class.getName();
        EmberRoleDetailViewController.logger = Logger.getLogger("UserManagementLogger");
    }
}
