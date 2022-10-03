package com.adventnet.sym.webclient.mdm.config;

import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.me.mdm.webclient.transformer.TransformerUtil;
import com.me.mdm.server.role.RBDAUtil;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberSqlViewController;

public class EmberProfileListTRAction extends MDMEmberSqlViewController
{
    Logger logger;
    
    public EmberProfileListTRAction() {
        this.logger = Logger.getLogger(ProfileListTRAction.class.getName());
    }
    
    @Override
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        String associatedValue = super.getVariableValue(viewCtx, variableName);
        associatedValue = ((associatedValue == null) ? "" : associatedValue);
        if (variableName.equalsIgnoreCase("RBDAPROFGRPCOUNT")) {
            final String adminQueryForProfGrpCount = "LEFT JOIN (select RecentProfileForGroup.profile_id, count(group_id) AS GROUPCOUNT from RecentProfileForGroup where RecentProfileForGroup.marked_for_delete='false' group by RecentProfileForGroup.profile_id) AS GroupQuery ON GroupQuery.profile_id=Profile.PROFILE_ID";
            final Long loginID = SYMClientUtil.getLoginId(viewCtx.getRequest());
            final boolean isMDMAdmin = RBDAUtil.getInstance().hasUserAllDeviceScopeGroup(loginID, false);
            if (!isMDMAdmin) {
                associatedValue = associatedValue + "LEFT JOIN (select RecentProfileForGroup.profile_id, count(group_id) AS GROUPCOUNT from RecentProfileForGroup INNER JOIN CustomGroup on RecentProfileForGroup.GROUP_ID=CustomGroup.RESOURCE_ID INNER JOIN CustomGroupExtn on CustomGroup.RESOURCE_ID=CustomGroupExtn.RESOURCE_ID" + RBDAUtil.getInstance().getUserCustomGroupMappingJoinString("RecentProfileForGroup", "GROUP_ID") + "where RecentProfileForGroup.marked_for_delete='false'" + RBDAUtil.getInstance().getUserCustomGroupCriteriaString(loginID) + " group by RecentProfileForGroup.profile_id) AS GroupQuery ON GroupQuery.profile_id=Profile.PROFILE_ID";
            }
            else {
                associatedValue += adminQueryForProfGrpCount;
            }
        }
        if (variableName.equalsIgnoreCase("RBDAPROFDEVCOUNT")) {
            final String adminQueryForProfDevCount = "LEFT JOIN ( select RecentProfileForResource.profile_id, count(RecentProfileForResource.resource_id) AS DEVCOUNT from RecentProfileForResource INNER JOIN ManagedDevice on RecentProfileForResource.RESOURCE_ID=ManagedDevice.RESOURCE_ID inner join Profile on Profile.PROFILE_ID = RecentProfileForResource.PROFILE_ID where ManagedDevice.PLATFORM_TYPE = Profile.PLATFORM_TYPE and ManagedDevice.MANAGED_STATUS=2 group by RecentProfileForResource.profile_id) AS DevQuery ON DevQuery.profile_id=Profile.PROFILE_ID";
            final Long loginID = SYMClientUtil.getLoginId(viewCtx.getRequest());
            final boolean isMDMAdmin = TransformerUtil.hasUserAllDeviceScopeGroup(viewCtx, false);
            if (!isMDMAdmin) {
                associatedValue = associatedValue + "LEFT JOIN ( select RecentProfileForResource.profile_id, count(RecentProfileForResource.resource_id) AS DEVCOUNT from RecentProfileForResource INNER JOIN UserDeviceMapping ON UserDeviceMapping.RESOURCE_ID=RecentProfileForResource.RESOURCE_ID INNER JOIN ManagedDevice on RecentProfileForResource.RESOURCE_ID=ManagedDevice.RESOURCE_ID and ManagedDevice.MANAGED_STATUS=2  inner join Profile on Profile.PROFILE_ID = RecentProfileForResource.PROFILE_ID where ManagedDevice.PLATFORM_TYPE = Profile.PLATFORM_TYPE and UserDeviceMapping.LOGIN_ID=" + loginID + " group by RecentProfileForResource.profile_id) AS DevQuery ON DevQuery.profile_id=Profile.PROFILE_ID";
            }
            else {
                associatedValue += adminQueryForProfDevCount;
            }
        }
        final String profile_type = viewCtx.getRequest().getParameter("profileType");
        if (variableName.equalsIgnoreCase("PLATFORM_FILTER")) {
            final String platformType = viewCtx.getRequest().getParameter("platformType");
            String profileType = "1";
            if (profile_type != null) {
                profileType = profile_type;
            }
            viewCtx.getRequest().setAttribute("platformType", (Object)platformType);
            final Long customerID = MSPWebClientUtil.getCustomerID(viewCtx.getRequest());
            associatedValue = "and Profile.PROFILE_TYPE=" + profileType + " and CustomerInfo.CUSTOMER_ID=" + customerID;
            if (platformType == null || Integer.parseInt(platformType) <= 0) {
                associatedValue += "";
            }
            else {
                associatedValue = associatedValue + " and Profile.PLATFORM_TYPE=" + platformType;
            }
        }
        if (variableName.equalsIgnoreCase("CREATED_OR_MODIFIED_BY_CRITERIA")) {
            final Long loginId = SYMClientUtil.getLoginId(viewCtx.getRequest());
            associatedValue = RBDAUtil.getInstance().getProfileCreatedOrModifiedByCriteriaString(loginId);
        }
        final String viewName = viewCtx.getUniqueId();
        if (variableName.equalsIgnoreCase("LICENSE_CONFIG_CRITERIA")) {
            final LicenseProvider licenseProvider = LicenseProvider.getInstance();
            final String mdmLiceseEditionType;
            final String edition = mdmLiceseEditionType = licenseProvider.getMDMLicenseAPI().getMDMLiceseEditionType();
            licenseProvider.getMDMLicenseAPI();
            if (mdmLiceseEditionType.equalsIgnoreCase("Standard")) {
                final Long customerID2 = MSPWebClientUtil.getCustomerID(viewCtx.getRequest());
                final JSONObject profileDetails = ProfileUtil.getInstance().getProfileIdsFromConfig(ProfileUtil.STANDARDLICENSE_NOTAPPLICABLE_CONFIG, new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID2, 0));
                final JSONObject profileCustomerDetails = profileDetails.optJSONObject(customerID2.toString());
                if (profileCustomerDetails != null && profileCustomerDetails.length() > 0) {
                    final List<String> profileIdList = new ArrayList<String>();
                    final Iterator iterator = profileCustomerDetails.keys();
                    while (iterator.hasNext()) {
                        profileIdList.add(String.valueOf(iterator.next()));
                    }
                    if (!profileIdList.isEmpty()) {
                        String profileIdString = Arrays.toString(profileIdList.toArray());
                        profileIdString = profileIdString.replaceAll("\\[", "\\(");
                        profileIdString = profileIdString.replaceAll("\\]", "\\)");
                        associatedValue = associatedValue + " and Profile.PROFILE_ID not in " + profileIdString;
                    }
                }
            }
        }
        if (viewName.equalsIgnoreCase("TrashProfileList")) {
            associatedValue += " and Profile.IS_MOVED_TO_TRASH='true'";
        }
        else {
            associatedValue += " and Profile.IS_MOVED_TO_TRASH='false'";
        }
        return associatedValue;
    }
}
