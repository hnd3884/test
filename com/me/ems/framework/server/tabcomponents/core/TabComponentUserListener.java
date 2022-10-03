package com.me.ems.framework.server.tabcomponents.core;

import com.me.devicemanagement.framework.server.authentication.UserEvent;
import com.me.devicemanagement.framework.server.authentication.AbstractUserListener;

public class TabComponentUserListener extends AbstractUserListener
{
    @Override
    public void userDeleted(final UserEvent userEvent) {
        final Long userID = userEvent.userID;
        TabComponentCacheUtil.deleteTabComponentUserParameters(userID, ServerAPIConstants.TabComponentCacheParam.USER_TO_TAB_ORDER, ServerAPIConstants.TabComponentCacheParam.NEW_TABS);
        TabComponentUtil.deleteAllCustomTabsForUser(userID);
    }
}
