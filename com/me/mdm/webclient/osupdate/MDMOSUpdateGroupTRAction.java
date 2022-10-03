package com.me.mdm.webclient.osupdate;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.me.mdm.server.customgroup.MDMCustomGroupUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMOSUpdateGroupTRAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMOSUpdateGroupTRAction() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext arg0) throws Exception {
        SelectQuery selectQuery = super.fetchAndCacheSelectQuery(arg0);
        if (MDMApiFactoryProvider.getMDMUtilAPI().isFeatureAllowedForUser("temporary.selectqueryfix")) {
            selectQuery = MDMApiFactoryProvider.getMDMUtilAPI().deepCloneQuery(selectQuery);
        }
        if (!selectQuery.containsSubQuery()) {
            try {
                selectQuery = MDMCustomGroupUtil.getInstance().getQueryforGroupControllers(selectQuery, true);
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Exception while dynamic join in MDMOSUpdateGroupTRAction :", e);
            }
        }
        return selectQuery;
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final String statusId = request.getParameter("statusId");
            final String collectionId = request.getParameter("collectionId");
            final String profileId = request.getParameter("profileId");
            final Criteria collectionCri = new Criteria(new Column("RecentProfileForGroup", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria markForDeleteCri = new Criteria(new Column("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
            final Criteria resTypeCri = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)new Integer(101), 0);
            selectQuery.setCriteria(collectionCri.and(markForDeleteCri).and(resTypeCri));
            final Column maxCountCol = new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID");
            maxCountCol.setColumnAlias("MEMBER_COUNT");
            selectQuery.addSelectColumn(maxCountCol);
            super.setCriteria(selectQuery, viewCtx);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in MDMOSUpdateGroupTRAction... {0}", e);
        }
    }
}
