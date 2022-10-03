package com.me.ems.onpremise.uac.api.v1.service.factory;

import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public interface ChangePasswordService
{
    Map<String, Object> changePassword(final Long p0, final Long p1, final String p2, final String p3, final String p4, final HttpServletRequest p5) throws APIException;
    
    Map<String, Object> getPasswordComplexity();
    
    void getExceptionMsgForPassword(final String p0, final Integer p1) throws APIException;
    
    Map<String, Object> changePasswordAndCloseSession(final User p0, final HttpServletRequest p1, final Map<String, String> p2) throws APIException;
}
