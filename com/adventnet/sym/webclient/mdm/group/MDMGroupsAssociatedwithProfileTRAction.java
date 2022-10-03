package com.adventnet.sym.webclient.mdm.group;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.me.mdm.server.customgroup.MDMCustomGroupUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMGroupsAssociatedwithProfileTRAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMGroupsAssociatedwithProfileTRAction() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext arg0) throws Exception {
        MDMUtil.addOrIncrementClickCountForView("mdmGroupsAssociatedwithProfile");
        SelectQuery selectQuery = super.fetchAndCacheSelectQuery(arg0);
        if (MDMApiFactoryProvider.getMDMUtilAPI().isFeatureAllowedForUser("temporary.selectqueryfix")) {
            selectQuery = MDMApiFactoryProvider.getMDMUtilAPI().deepCloneQuery(selectQuery);
        }
        if (!selectQuery.containsSubQuery()) {
            try {
                selectQuery = MDMCustomGroupUtil.getInstance().getQueryforGroupControllers(selectQuery, true);
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Exception while dynamic join ---- :", e);
            }
        }
        return selectQuery;
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final String status = request.getParameter("status");
            final String profileIdStr = request.getParameter("profileId");
            Long profileId = null;
            Criteria resTypeCri = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)new Integer(101), 0);
            if (profileIdStr != null) {
                profileId = Long.valueOf(profileIdStr);
                final Criteria profId = new Criteria(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), (Object)profileId, 0);
                resTypeCri = resTypeCri.and(profId);
            }
            if (status != null && !status.equalsIgnoreCase("all")) {
                request.setAttribute("status", (Object)status);
                final Long lstatus = Long.parseLong(status);
                final Criteria cStatus = new Criteria(new Column("ConfigStatusDefn", "STATUS_ID"), (Object)lstatus, 0);
                resTypeCri = resTypeCri.and(cStatus);
            }
            final List groupList = MDMGroupHandler.getMDMGroupType();
            groupList.add(7);
            final Criteria gTypeCri = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupList.toArray(), 8);
            selectQuery.setCriteria(resTypeCri.and(gTypeCri));
            final Column maxCountCol = new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID");
            maxCountCol.setColumnAlias("MEMBER_COUNT");
            selectQuery.addSelectColumn(maxCountCol);
            super.setCriteria(selectQuery, viewCtx);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in MDMGroupTableRetrieverAction...", e);
        }
    }
}
