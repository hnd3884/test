package com.me.ems.framework.server.quicklaunch.core;

import com.me.ems.framework.uac.api.v1.model.User;
import java.util.Map;

public interface QuickLaunchHandler
{
    Map<String, Object> customHandling(final Map<String, Object> p0, final User p1) throws Exception;
}
