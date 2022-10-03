package com.me.ems.framework.home.core;

import com.adventnet.persistence.DataObject;
import java.util.List;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.function.Predicate;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import java.util.Map;
import com.me.ems.framework.uac.api.v1.model.User;
import com.adventnet.ds.query.SelectQuery;

public class DefaultHomePageHandler implements HomePageHandler
{
    @Override
    public SelectQuery handleSummarySelectQuery(final SelectQuery selectQuery) throws Exception {
        return selectQuery;
    }
    
    @Override
    public void setHomePageMessagesAndDetails(final User user, final Map<String, Object> responseMap) throws Exception {
        responseMap.put("freeLicenseNotificationForMDM", this.freeLicenseNotificationForMDM(user));
        final MessageProvider provider = MessageProvider.getInstance();
        final CustomerInfoUtil infoUtil = CustomerInfoUtil.getInstance();
        provider.hideMessage("CUSTOMER_NOT_ADDED");
        if (infoUtil.isMSP() && infoUtil.getCustomers().length == 0) {
            provider.unhideMessage("CUSTOMER_NOT_ADDED");
        }
    }
    
    @Override
    public Map<String, Object> getNotificationMsgContent() throws Exception {
        return new HashMap<String, Object>(1);
    }
    
    private Map<String, Object> freeLicenseNotificationForMDM(final User user) throws Exception {
        final List<String> mdmRoleList = new ArrayList<String>(Arrays.asList("MDM_Inventory_Read", "MDM_AppMgmt_Read", "MDM_Configurations_Read", "MDM_Settings_Read", "MDM_Report_Read", "MDM_Enrollment_Read"));
        final boolean isUserInMDMRole = mdmRoleList.stream().anyMatch((Predicate<? super Object>)user::isUserInRole);
        final Map<String, Object> notificationMessageContent = new HashMap<String, Object>();
        if (isUserInMDMRole) {
            final Long loginId = user.getLoginID();
            Criteria notifyCri = new Criteria(Column.getColumn("NotifyChangesToUser", "LOGIN_ID"), (Object)loginId, 0);
            notifyCri = notifyCri.and(new Criteria(Column.getColumn("NotifyChangesToUser", "FUNCTIONALITY"), (Object)"--", 0));
            final DataObject dObj = SyMUtil.getCachedPersistence().get("NotifyChangesToUser", notifyCri);
            if (!dObj.isEmpty()) {
                notificationMessageContent.put("notifyMDMUser", Boolean.TRUE);
                notificationMessageContent.putAll(this.getNotificationMsgContent());
            }
            else {
                notificationMessageContent.put("notifyMDMUser", Boolean.FALSE);
            }
        }
        else {
            notificationMessageContent.put("notifyMDMUser", Boolean.FALSE);
        }
        return notificationMessageContent;
    }
}
