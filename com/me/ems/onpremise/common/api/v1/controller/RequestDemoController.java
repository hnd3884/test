package com.me.ems.onpremise.common.api.v1.controller;

import com.me.ems.onpremise.common.factory.CommonOnPremiseServiceFactoryProvider;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import java.util.List;
import javax.ws.rs.Produces;
import com.me.ems.framework.common.api.annotations.AllowEntityFilter;
import javax.ws.rs.GET;
import java.util.Map;
import javax.ws.rs.PUT;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.framework.uac.api.v1.model.User;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import com.me.ems.onpremise.common.factory.RequestDemoService;
import javax.ws.rs.Path;

@Path("demo")
public class RequestDemoController
{
    private static final RequestDemoService SERVICE;
    
    @PUT
    public Response skipRequestDemoPage(@Context final SecurityContext securityContext, @QueryParam("action") final String action, @Context final HttpServletRequest httpServletRequest) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        if (action != null) {
            if (action.equalsIgnoreCase("skip")) {
                return RequestDemoController.SERVICE.skipRequestDemoPage(user.getLoginID(), httpServletRequest);
            }
            if (action.equalsIgnoreCase("stop")) {
                return RequestDemoController.SERVICE.neverShowRequestDemoPageAgain(user.getLoginID(), httpServletRequest);
            }
        }
        throw new APIException("GENERIC0003");
    }
    
    @GET
    @AllowEntityFilter
    @Produces({ "application/showDemoPageStatus.v1+json" })
    public Map<String, Boolean> isRequestDemoPageNeeded(@Context final SecurityContext securityContext) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        return RequestDemoController.SERVICE.isRequestDemoPageNeeded(user.getLoginID());
    }
    
    @GET
    @AllowEntityFilter
    @Path("countries")
    @Produces({ "application/countriesList.v1+json" })
    public Map<String, List<Map<String, Object>>> getCountries() throws APIException {
        return RequestDemoController.SERVICE.getCountries();
    }
    
    @POST
    @Path("register")
    @Consumes({ "application/demoRegistrationUserDetails.v1+json" })
    public Response registerRequestDemo(final Map<String, Object> registrationDetails, @Context final SecurityContext securityContext, @Context final HttpServletRequest httpServletRequest) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        return RequestDemoController.SERVICE.registerRequestDemo(registrationDetails, user.getLoginID(), user.getName(), httpServletRequest);
    }
    
    static {
        SERVICE = CommonOnPremiseServiceFactoryProvider.getRequestDemoService();
    }
}
