package com.adventnet.sym.webclient.mdm.apps.vpp;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.ds.query.Criteria;
import java.util.List;
import java.util.logging.Level;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMTableRetrieverAction;

public class VppUserTRAction extends MDMTableRetrieverAction
{
    private Logger logger;
    
    public VppUserTRAction() {
        this.logger = Logger.getLogger(VppUserTRAction.class.getName());
    }
    
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewContext) throws Exception {
        SelectQuery selectQuery = super.fetchAndCacheSelectQuery(viewContext);
        if (MDMApiFactoryProvider.getMDMUtilAPI().isFeatureAllowedForUser("temporary.selectqueryfix")) {
            selectQuery = MDMApiFactoryProvider.getMDMUtilAPI().deepCloneQuery(selectQuery);
        }
        final String viewName = viewContext.getUniqueId();
        if (!selectQuery.containsSubQuery() && viewName.equalsIgnoreCase("vppManagedUserView")) {
            try {
                final SelectQuery subSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUserToDevice"));
                final Column manageddeviceCount = Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID").count();
                manageddeviceCount.setColumnAlias("MANAGED_DEVICE_ID");
                subSQ.addSelectColumn(manageddeviceCount);
                subSQ.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"));
                final List list = new ArrayList();
                final Column groupByCol = Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID");
                list.add(groupByCol);
                final GroupByClause groupBy = new GroupByClause(list);
                subSQ.setGroupByClause(groupBy);
                final Table userInfo = Table.getTable("ManagedUser");
                final DerivedTable dtab = new DerivedTable("ManagedUserToDevice", (Query)subSQ);
                selectQuery.addJoin(new Join(userInfo, (Table)dtab, new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
                final Column managedCompcount = new Column("ManagedUserToDevice", "MANAGED_DEVICE_ID");
                managedCompcount.setColumnAlias("DEVICE_COUNT");
                selectQuery.addSelectColumn(managedCompcount);
                final SelectQuery licensesubSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("MdVPPLicenseToUser"));
                final Column managedlicenseCount = Column.getColumn("MdVPPLicenseToUser", "LICENSE_DETAIL_ID").count();
                managedlicenseCount.setColumnAlias("LICENSE_DETAIL_ID");
                licensesubSQ.addSelectColumn(managedlicenseCount);
                licensesubSQ.addSelectColumn(Column.getColumn("MdVPPLicenseToUser", "MANAGED_USER_ID"));
                final List licenselist = new ArrayList();
                final Column usergroupByCol = Column.getColumn("MdVPPLicenseToUser", "MANAGED_USER_ID");
                licenselist.add(usergroupByCol);
                final GroupByClause usergroupBy = new GroupByClause(licenselist);
                licensesubSQ.setGroupByClause(usergroupBy);
                final Table vppInfo = Table.getTable("ManagedUser");
                final DerivedTable dtab2 = new DerivedTable("MdVPPLicenseToUser", (Query)licensesubSQ);
                selectQuery.addJoin(new Join(vppInfo, (Table)dtab2, new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
                final Column licensecount = new Column("MdVPPLicenseToUser", "LICENSE_DETAIL_ID");
                licensecount.setColumnAlias("LICENSE_COUNT");
                selectQuery.addSelectColumn(licensecount);
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Exception while dynamic join ---- :", e);
            }
        }
        return selectQuery;
    }
    
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        final HttpServletRequest request = viewCtx.getRequest();
        final String statusStr = request.getParameter("status");
        final String vppUerIdStr = request.getParameter("vppUserId");
        Criteria cri = null;
        if (vppUerIdStr != null) {
            cri = new Criteria(Column.getColumn("MdVPPManagedUser", "VPP_USER_ID"), (Object)Long.parseLong(vppUerIdStr), 0);
        }
        if (statusStr != null && !statusStr.equalsIgnoreCase("all")) {
            request.setAttribute("status", (Object)statusStr);
            final Integer status = Integer.parseInt(statusStr);
            cri = new Criteria(Column.getColumn("MdVPPManagedUser", "MANAGED_STATUS"), (Object)status, 0);
        }
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1).or(new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)null, 0));
        query.setCriteria(cri.and(userNotInTrashCriteria));
        super.setCriteria(query, viewCtx);
    }
}
