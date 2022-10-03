package com.adventnet.sym.webclient.mdm.announcements;

import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.me.mdm.server.role.RBDAUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.adventnet.client.components.table.web.DMSqlViewRetriever;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberSqlViewController;

public class AnnouncementListTRAction extends MDMEmberSqlViewController
{
    private Logger logger;
    
    public AnnouncementListTRAction() {
        this.logger = Logger.getLogger("AnnouncementHandler");
    }
    
    @Override
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        String associatedValue = super.getVariableValue(viewCtx, variableName);
        associatedValue = ((associatedValue == null) ? "" : associatedValue);
        if (variableName.trim().startsWith("DBRANGECRITERIA")) {
            return DMSqlViewRetriever.checkAndGetDBRangeCriteriaAssociatedValue(variableName);
        }
        if (variableName.equalsIgnoreCase("RBDAPROFGRPCOUNT")) {
            final String sql = DMSqlViewRetriever.checkAndGetDBRangeCriteriaAssociatedValue("DBRANGECRITERIA-RecentProfileForGroup");
            final String adminQueryForProfGrpCount = "LEFT JOIN (select RecentProfileForGroup.profile_id, count(group_id) AS GROUPCOUNT from RecentProfileForGroup where RecentProfileForGroup.marked_for_delete='false' and " + sql + " group by RecentProfileForGroup.profile_id) AS GroupQuery " + "ON GroupQuery.profile_id=Profile.PROFILE_ID";
            final Long loginID = SYMClientUtil.getLoginId(viewCtx.getRequest());
            final boolean isMDMAdmin = DMUserHandler.isUserInRole(loginID, "All_Managed_Mobile_Devices");
            if (!isMDMAdmin) {
                associatedValue = associatedValue + "LEFT JOIN (select RecentProfileForGroup.profile_id, count(group_id) AS GROUPCOUNT from RecentProfileForGroup INNER JOIN CustomGroup on RecentProfileForGroup.GROUP_ID=CustomGroup.RESOURCE_ID INNER JOIN CustomGroupExtn on CustomGroup.RESOURCE_ID=CustomGroupExtn.RESOURCE_ID" + RBDAUtil.getInstance().getUserCustomGroupMappingJoinString("RecentProfileForGroup", "GROUP_ID") + "where RecentProfileForGroup.marked_for_delete='false' AND " + sql + " " + RBDAUtil.getInstance().getUserCustomGroupCriteriaString(loginID) + " group by RecentProfileForGroup.profile_id) AS GroupQuery ON GroupQuery.profile_id=Profile.PROFILE_ID";
            }
            else {
                associatedValue += adminQueryForProfGrpCount;
            }
        }
        if (variableName.equalsIgnoreCase("RBDAPROFDEVCOUNT")) {
            final String sql = DMSqlViewRetriever.checkAndGetDBRangeCriteriaAssociatedValue("DBRANGECRITERIA-RecentProfileForResource");
            final String adminQueryForProfDevCount = "LEFT JOIN (select RecentProfileForResource.profile_id, count(RecentProfileForResource.resource_id) AS DEVCOUNT from RecentProfileForResource INNER JOIN ManagedDevice on RecentProfileForResource.RESOURCE_ID=ManagedDevice.RESOURCE_ID inner join Profile on Profile.PROFILE_ID = RecentProfileForResource.PROFILE_ID where ManagedDevice.MANAGED_STATUS=2 AND RecentProfileForResource.MARKED_FOR_DELETE='false' AND (" + sql + ") group by RecentProfileForResource.profile_id) AS DevQuery " + "ON DevQuery.profile_id=Profile.PROFILE_ID";
            final Long loginID = SYMClientUtil.getLoginId(viewCtx.getRequest());
            final boolean isMDMAdmin = DMUserHandler.isUserInRole(loginID, "All_Managed_Mobile_Devices");
            if (!isMDMAdmin) {
                associatedValue = associatedValue + "LEFT JOIN (select RecentProfileForResource.profile_id, count(RecentProfileForResource.resource_id) AS DEVCOUNT from RecentProfileForResource INNER JOIN UserDeviceMapping ON UserDeviceMapping.RESOURCE_ID=RecentProfileForResource.RESOURCE_ID INNER JOIN ManagedDevice on RecentProfileForResource.RESOURCE_ID=ManagedDevice.RESOURCE_ID and ManagedDevice.MANAGED_STATUS=2  inner join Profile on Profile.PROFILE_ID = RecentProfileForResource.PROFILE_ID where UserDeviceMapping.LOGIN_ID=" + loginID + " AND RecentProfileForResource.MARKED_FOR_DELETE='false' AND " + sql + " group by RecentProfileForResource.profile_id) AS DevQuery " + "ON DevQuery.profile_id=Profile.PROFILE_ID";
            }
            else {
                associatedValue += adminQueryForProfDevCount;
            }
        }
        if (variableName.equalsIgnoreCase("DistributedTime")) {
            final String sql = DMSqlViewRetriever.checkAndGetDBRangeCriteriaAssociatedValue("DBRANGECRITERIA-RESOURCETOPROFILEHISTORY");
            associatedValue = associatedValue + " LEFT JOIN  ( select RESOURCETOPROFILEHISTORY.PROFILE_ID,max(RESOURCETOPROFILEHISTORY.ASSOCIATED_TIME) AS LAST_DIST_TIME from RESOURCETOPROFILEHISTORY where " + sql + " group by " + "RESOURCETOPROFILEHISTORY.Profile_id) AS distTime ON distTime.profile_id=Profile.PROFILE_ID ";
        }
        if (variableName.equalsIgnoreCase("CREATED_OR_MODIFIED_BY_CRITERIA")) {
            final Long loginId = SYMClientUtil.getLoginId(viewCtx.getRequest());
            associatedValue = RBDAUtil.getInstance().getProfileCreatedOrModifiedByCriteriaString(loginId);
        }
        if (variableName.equalsIgnoreCase("SCRITERIA")) {
            associatedValue += " and PROFILE.IS_MOVED_TO_TRASH = 'false'";
            final Long customerID = MSPWebClientUtil.getCustomerID(viewCtx.getRequest());
            associatedValue = associatedValue + " and CustomerInfo.CUSTOMER_ID=" + customerID;
        }
        return associatedValue;
    }
}
