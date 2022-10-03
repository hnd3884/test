package com.me.ems.onpremise.common.factory;

import java.util.List;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.Map;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;

public interface RequestDemoService
{
    Response skipRequestDemoPage(final Long p0, final HttpServletRequest p1);
    
    Map<String, Boolean> isRequestDemoPageNeeded(final Long p0) throws APIException;
    
    Response registerRequestDemo(final Map<String, Object> p0, final Long p1, final String p2, final HttpServletRequest p3) throws APIException;
    
    Response neverShowRequestDemoPageAgain(final Long p0, final HttpServletRequest p1);
    
    Map<String, List<Map<String, Object>>> getCountries() throws APIException;
}
