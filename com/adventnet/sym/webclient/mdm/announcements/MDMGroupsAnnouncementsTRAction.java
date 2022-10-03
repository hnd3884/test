package com.adventnet.sym.webclient.mdm.announcements;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.me.mdm.server.customgroup.MDMCustomGroupUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMGroupsAnnouncementsTRAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMGroupsAnnouncementsTRAction() {
        this.logger = Logger.getLogger("AnnouncementHandler");
    }
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext arg0) throws Exception {
        SelectQuery selectQuery = super.fetchAndCacheSelectQuery(arg0);
        if (!selectQuery.containsSubQuery()) {
            try {
                selectQuery = MDMCustomGroupUtil.getInstance().getQueryforGroupControllers(selectQuery, true);
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Exception while dynamic join in MDMGroupsAnnouncementsTRAction {0}", e);
            }
        }
        return selectQuery;
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final String status = request.getParameter("status");
            final String announcementIdStr = request.getParameter("announcementId");
            final Long announcementId = Long.valueOf(announcementIdStr);
            Criteria announcementFilterCri = new Criteria(Column.getColumn("Announcement", "ANNOUNCEMENT_ID"), (Object)announcementId, 0);
            if (status != null && !status.equalsIgnoreCase("all")) {
                final Long lstatus = Long.parseLong(status);
                final Criteria cStatus = new Criteria(new Column("GroupToProfileHistory", "COLLECTION_STATUS"), (Object)lstatus, 0);
                announcementFilterCri = announcementFilterCri.and(cStatus);
            }
            selectQuery.setCriteria(announcementFilterCri);
            final Column maxCountCol = new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID");
            maxCountCol.setColumnAlias("MEMBER_COUNT");
            selectQuery.addSelectColumn(maxCountCol);
            super.setCriteria(selectQuery, viewCtx);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occoured in MDMGroupsAnnouncementsTRAction... {0}", e);
        }
    }
}
