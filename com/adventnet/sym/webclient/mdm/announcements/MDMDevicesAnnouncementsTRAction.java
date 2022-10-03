package com.adventnet.sym.webclient.mdm.announcements;

import com.adventnet.sym.webclient.mdm.encryption.ios.MDMDeviceRecentUserViewHandler;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.ds.query.Join;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMDevicesAnnouncementsTRAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMDevicesAnnouncementsTRAction() {
        this.logger = Logger.getLogger("AnnouncementHandler");
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final String statusStr = request.getParameter("status");
            final String sgroup = request.getParameter("groupId");
            final String platform = request.getParameter("platformType");
            Criteria cri = null;
            final String announcementIdStr = request.getParameter("announcementId");
            final Long announcementId = Long.valueOf(announcementIdStr);
            final Criteria announcementFilterCri = cri = new Criteria(Column.getColumn("Announcement", "ANNOUNCEMENT_ID"), (Object)announcementId, 0);
            if (statusStr != null && !statusStr.equalsIgnoreCase("all")) {
                final Long status = Long.parseLong(statusStr);
                final ArrayList statusLIst = new ArrayList();
                if (status == 1000L) {
                    statusLIst.add(18);
                    statusLIst.add(200);
                    statusLIst.add(300);
                    statusLIst.add(1);
                    statusLIst.add(3);
                    statusLIst.add(12);
                    statusLIst.add(16);
                    statusLIst.add(18);
                    statusLIst.add(13);
                }
                else if (status == 1001L) {
                    statusLIst.add(2);
                    statusLIst.add(4);
                    statusLIst.add(6);
                }
                else if (status == 1002L) {
                    statusLIst.add(7);
                    statusLIst.add(9);
                    statusLIst.add(10);
                    statusLIst.add(11);
                }
                else {
                    statusLIst.add(status);
                }
                final Criteria cStatus = new Criteria(new Column("CollnToResources", "STATUS"), (Object)statusLIst.toArray(), 8);
                cri = cri.and(cStatus);
            }
            final Criteria res = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)new Column("CollnToResources", "RESOURCE_ID"), 0);
            final Criteria announcementResCri = new Criteria(new Column("AnnouncementToResources", "RESOURCE_ID"), (Object)new Column("RecentProfileForResource", "RESOURCE_ID"), 0);
            final Criteria unmanaged = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            cri = cri.and(res);
            final Criteria profileTypeCriteria = new Criteria(new Column("Profile", "PROFILE_TYPE"), (Object)9, 0);
            final Criteria deletedCriteria = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
            cri = cri.and(unmanaged).and(announcementResCri).and(deletedCriteria).and(profileTypeCriteria);
            if (sgroup != null && !sgroup.equalsIgnoreCase("all")) {
                final Long grpid = Long.parseLong(sgroup);
                selectQuery.addJoin(new Join("Resource", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 2));
                final Criteria grp = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)grpid, 0);
                cri = cri.and(grp);
            }
            if (platform != null && !platform.equalsIgnoreCase("all")) {
                final int platformAsInt = Integer.parseInt(platform);
                final Criteria platformCri = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)platformAsInt, 0);
                cri = cri.and(platformCri);
            }
            selectQuery.setCriteria(cri);
            super.setCriteria(selectQuery, viewCtx);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in MdmDevicesAssociatedwithProfileRetAction...", e);
        }
    }
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewCtx) throws Exception {
        final String unique = viewCtx.getUniqueId();
        final SelectQuery selectQuery = super.fetchAndCacheSelectQuery(viewCtx);
        if (!selectQuery.containsSubQuery()) {
            new MDMDeviceRecentUserViewHandler().addRecentUsersTableJoin(selectQuery, unique);
        }
        return selectQuery;
    }
}
