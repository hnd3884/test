package com.me.ems.framework.personalization.api.v1.controller;

import javax.ws.rs.DELETE;
import javax.ws.rs.core.Response;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import com.me.ems.framework.personalization.core.PersonalizationUtil;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.Map;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import com.me.ems.framework.personalization.factory.PersonalizationServiceFactoryProvider;
import com.me.ems.framework.personalization.factory.PersonalizationService;
import javax.ws.rs.Path;

@Path("personalization")
public class PersonalizationController
{
    private PersonalizationService service;
    
    public PersonalizationController() {
        this.service = PersonalizationServiceFactoryProvider.getPersonalizationService();
    }
    
    @GET
    @Produces({ "application/personalizationPageDetails.v1+json" })
    public Map<String, Object> showPersonalisePage(@Context final SecurityContext securityContext) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        return this.service.showPersonalisePage(user);
    }
    
    @POST
    @Produces({ "application/personalizationStatus.v1+json" })
    @Consumes({ "application/personalizationDetails.v1+json" })
    public Map<String, Object> putPersonalisePage(@Context final HttpServletRequest request, final Map<String, Object> detailsMap, @Context final SecurityContext securityContext, @Context final HttpServletRequest httpServletRequest) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        return this.service.updatePersonalizationDetails(detailsMap, user, request);
    }
    
    @GET
    @Path("userImage")
    @Produces({ "application/userProfileImage.v1+json" })
    public Map<String, Object> getUserImage(@Context final SecurityContext securityContext) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        return this.service.getUserDP(user);
    }
    
    @GET
    @Path("activeSession")
    @Produces({ "application/getActiveSession.v1+json" })
    public Map<String, Object> getActiveSession(@Context final SecurityContext securityContext) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        new PersonalizationUtil().incrementActiveSessionCalls();
        return this.service.getActiveSession(user);
    }
    
    @DELETE
    @Path("activeSession")
    public Response deleteActiveSession(@DefaultValue("allExceptCurrent") @QueryParam("sessionID") final String sessionID, @Context final HttpServletRequest request, @Context final SecurityContext securityContext) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        return this.service.deleteActiveSession(user, sessionID, request);
    }
    
    @DELETE
    @Path("closeAllSessions")
    public Response deleteAllSessions(@Context final SecurityContext securityContext) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        return this.service.closeAllSessions(user);
    }
}
