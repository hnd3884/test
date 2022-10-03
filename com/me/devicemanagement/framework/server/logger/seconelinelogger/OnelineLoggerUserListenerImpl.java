package com.me.devicemanagement.framework.server.logger.seconelinelogger;

import com.me.devicemanagement.framework.server.authentication.UserEvent;
import com.me.devicemanagement.framework.server.authentication.AbstractUserListener;

public class OnelineLoggerUserListenerImpl extends AbstractUserListener
{
    @Override
    public void userModified(final UserEvent userEvent) {
        OneLineLoggerThreadLocal.invalidateRoleNameInCache(userEvent.userID.toString());
    }
}
