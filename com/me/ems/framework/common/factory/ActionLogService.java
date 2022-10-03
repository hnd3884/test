package com.me.ems.framework.common.factory;

import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.Map;
import com.me.ems.framework.uac.api.v1.model.User;

public interface ActionLogService
{
    Map<String, Object> getSettings(final User p0) throws APIException;
    
    Map<String, String> getRetentionPeriod() throws APIException;
    
    void updateRetentionPeriod(final Map<String, String> p0, final User p1, final HttpServletRequest p2) throws APIException;
}
