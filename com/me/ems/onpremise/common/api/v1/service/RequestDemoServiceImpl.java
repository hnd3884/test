package com.me.ems.onpremise.common.api.v1.service;

import java.util.ArrayList;
import java.util.List;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.onpremise.server.metrack.EvaluatorTrackerUtil;
import com.me.devicemanagement.framework.server.util.CreatorDataPost;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.Map;
import com.me.ems.onpremise.common.core.RequestDemoUtil;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.ems.onpremise.common.factory.RequestDemoService;

public class RequestDemoServiceImpl implements RequestDemoService
{
    protected static Logger logger;
    
    @Override
    public Response skipRequestDemoPage(final Long loginID, final HttpServletRequest httpServletRequest) {
        RequestDemoUtil.skipRequestDemoPage(loginID);
        return Response.ok().build();
    }
    
    @Override
    public Map<String, Boolean> isRequestDemoPageNeeded(final Long loginID) throws APIException {
        try {
            final Map<String, Boolean> responseMap = new HashMap<String, Boolean>(2);
            final boolean isDemoMode = ApiFactoryProvider.getDemoUtilAPI().isDemoMode();
            responseMap.put("isRequestDemoPageNeeded", !isDemoMode && RequestDemoUtil.isRequestDemoPageNeeded(loginID));
            return responseMap;
        }
        catch (final Exception e) {
            RequestDemoServiceImpl.logger.log(Level.SEVERE, " Exception while checking request demo skip count" + e);
            throw new APIException("GENERIC0005");
        }
    }
    
    @Override
    public Response registerRequestDemo(final Map<String, Object> detailsMap, final Long loginID, final String loginName, final HttpServletRequest httpServletRequest) throws APIException {
        RequestDemoServiceImpl.logger.log(Level.INFO, "RequestDemoUtil registerRequestDemo() method called...!");
        try {
            detailsMap.put("loginName", loginName);
            detailsMap.put("loginID", loginID);
            this.validateInputFormFields(detailsMap);
            final int statusCode = ApiFactoryProvider.getRequestDemoHandler().registerRequestDemo(detailsMap);
            final String trackingCode = ProductUrlLoader.getInstance().getValue("trackingcode");
            final String failureParams = trackingCode + "-demopostFailure";
            if (statusCode != 200) {
                RequestDemoServiceImpl.logger.log(Level.INFO, "Error Code obtained while posting data to creator : " + CreatorDataPost.getReturnCode());
                EvaluatorTrackerUtil.getInstance().addOrIncrementClickCountForTrialUsers("RequestDemoRegisteredUser", String.valueOf(loginID));
                return Response.serverError().entity((Object)failureParams).build();
            }
            RequestDemoServiceImpl.logger.log(Level.INFO, "Status code obtained is 200..Connection established successfully");
            final String resultXML = CreatorDataPost.getResultData();
            RequestDemoServiceImpl.logger.log(Level.INFO, "Demo Response: " + resultXML);
            final String result = resultXML.substring(resultXML.lastIndexOf("<status>") + 8, resultXML.lastIndexOf("</status>"));
            if ("success".equalsIgnoreCase(result)) {
                DCEventLogUtil.getInstance().addEvent(121, loginName, (HashMap)null, "dm.request.demo.event.logger", (Object)loginName, false, CustomerInfoUtil.getInstance().getCustomerId());
                EvaluatorTrackerUtil.getInstance().addOrIncrementClickCountForTrialUsers("RequestDemoRegisteredUser", String.valueOf(loginID));
                return Response.ok().build();
            }
            RequestDemoServiceImpl.logger.log(Level.INFO, "Status obtained : " + result);
            EvaluatorTrackerUtil.getInstance().addOrIncrementClickCountForTrialUsers("RequestDemoRegisteredUser", String.valueOf(loginID));
            return Response.serverError().entity((Object)failureParams).build();
        }
        catch (final APIException apiEx) {
            throw apiEx;
        }
        catch (final Exception e) {
            RequestDemoServiceImpl.logger.log(Level.SEVERE, "Exception in Register Request Demo", e);
            throw new APIException("GENERIC0005");
        }
    }
    
    @Override
    public Response neverShowRequestDemoPageAgain(final Long loginID, final HttpServletRequest httpServletRequest) {
        RequestDemoUtil.neverShowRequestDemoPageAgain(loginID);
        return Response.ok().build();
    }
    
    @Override
    public Map<String, List<Map<String, Object>>> getCountries() throws APIException {
        try {
            final Map<String, List<Map<String, Object>>> responseObj = new HashMap<String, List<Map<String, Object>>>(1);
            final List<Map<String, Object>> finalList = ApiFactoryProvider.getRequestDemoHandler().getCountries();
            responseObj.put("countries", finalList);
            return responseObj;
        }
        catch (final Exception ex) {
            RequestDemoServiceImpl.logger.log(Level.SEVERE, "Caught exception while getting countries.", ex);
            throw new APIException("GENERIC0005");
        }
    }
    
    private void validateInputFormFields(final Map<String, Object> detailsMap) throws APIException {
        final String name = detailsMap.get("name");
        final String email = detailsMap.get("email");
        final String phone = detailsMap.get("telephoneNumber");
        final Integer offset = detailsMap.get("offset");
        final String dateForDemo = detailsMap.get("dateForDemo");
        final String managedDeviceCount = detailsMap.get("managedDeviceCount");
        final String countryCode = detailsMap.get("countryCode");
        final String consent = detailsMap.get("consent");
        final List<String> nullList = new ArrayList<String>(4);
        if (name == null) {
            nullList.add("name");
        }
        if (email == null) {
            nullList.add("email");
        }
        if (phone == null) {
            nullList.add("telephoneNumber");
        }
        if (offset == null) {
            nullList.add("offset");
        }
        if (dateForDemo == null) {
            nullList.add("dateForDemo");
        }
        if (managedDeviceCount == null) {
            nullList.add("managedDeviceCount");
        }
        if (countryCode == null) {
            nullList.add("countryCode");
        }
        if (!nullList.isEmpty()) {
            throw new APIException("GENERIC0009", (String)null, (String[])nullList.toArray(new String[0]));
        }
    }
    
    static {
        RequestDemoServiceImpl.logger = Logger.getLogger(RequestDemoServiceImpl.class.getName());
    }
}
