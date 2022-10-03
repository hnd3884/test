package com.adventnet.sym.webclient.mdm.group;

import com.adventnet.sym.webclient.mdm.encryption.ios.MDMDeviceRecentUserViewHandler;
import java.util.Map;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMDevicesAssociatedwithProfileRetAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMDevicesAssociatedwithProfileRetAction() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        MDMUtil.addOrIncrementClickCountForView("mdmDevicesAssociatedwithProfile");
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final String status = request.getParameter("status");
            final String sdevice = request.getParameter("device");
            final String osVersion = request.getParameter("osVersion");
            final String sProfileId = request.getParameter("profileId");
            final String sgroup = request.getParameter("group");
            int platformType = 0;
            Criteria cri = null;
            if (sProfileId != null) {
                final Long profileId = Long.parseLong(sProfileId);
                final ProfileUtil pu = new ProfileUtil();
                platformType = pu.getPlatformType(profileId);
                final List osList = pu.getOSVersionsAssignedForProfile(profileId);
                final Map grpList = pu.getGroupsAssignedForProfile(profileId);
                request.setAttribute("osList", (Object)osList);
                request.setAttribute("grpList", (Object)grpList);
                cri = new Criteria(new Column("RecentProfileForResource", "PROFILE_ID"), (Object)profileId, 0);
            }
            if (status != null && !status.equalsIgnoreCase("all")) {
                request.setAttribute("status", (Object)status);
                final Long lstatus = Long.parseLong(status);
                final Criteria cStatus = new Criteria(new Column("CollnToResources", "STATUS"), (Object)lstatus, 0);
                cri = cri.and(cStatus);
            }
            if (sdevice != null && !sdevice.equalsIgnoreCase("all")) {
                request.setAttribute("device", (Object)sdevice);
                final int device = Integer.parseInt(sdevice);
                final Criteria cDevice = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)device, 0);
                cri = cri.and(cDevice);
            }
            if (osVersion != null && !osVersion.equalsIgnoreCase("all")) {
                request.setAttribute("osVersion", (Object)osVersion);
                final Criteria cOS = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)osVersion, 0);
                cri = cri.and(cOS);
            }
            final Criteria res = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)new Column("CollnToResources", "RESOURCE_ID"), 0);
            final Criteria unmanaged = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            cri = cri.and(res);
            cri = cri.and(unmanaged);
            if (sgroup != null && !sgroup.equalsIgnoreCase("all")) {
                request.setAttribute("group", (Object)sgroup);
                final Long grpid = Long.parseLong(sgroup);
                selectQuery.addJoin(new Join("Resource", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 2));
                final Criteria grp = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)grpid, 0);
                cri = cri.and(grp);
            }
            else {
                Criteria cplatformType = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)platformType, 0);
                if (platformType == 7 || platformType == 6) {
                    final Criteria appleProPlatformTempCri = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)new Integer[] { 6, 7 }, 8);
                    final Criteria appleDevPlatformTempCri = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)new Integer[] { 1, 6, 7 }, 8);
                    final Criteria applePlatformExclCriteria = appleDevPlatformTempCri.and(appleProPlatformTempCri);
                    cplatformType = cplatformType.or(applePlatformExclCriteria);
                }
                cri = cri.and(cplatformType);
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
