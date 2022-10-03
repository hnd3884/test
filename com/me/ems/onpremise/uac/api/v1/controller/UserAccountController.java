package com.me.ems.onpremise.uac.api.v1.controller;

import com.me.ems.framework.common.api.annotations.RestrictMatched;
import javax.ws.rs.GET;
import javax.ws.rs.DELETE;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.Map;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.onpremise.uac.factory.UserAccountValidator;
import org.json.simple.JSONObject;
import com.me.ems.onpremise.uac.api.v1.service.factory.UserAccountService;
import javax.ws.rs.core.SecurityContext;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import java.util.logging.Level;
import com.me.ems.onpremise.uac.factory.UacFactoryProvider;
import com.me.ems.framework.uac.api.v1.model.User;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import com.me.ems.onpremise.uac.api.v1.model.UserDetails;
import java.util.logging.Logger;
import javax.ws.rs.Path;

@Path("users")
public class UserAccountController
{
    private static Logger logger;
    
    @POST
    @Consumes({ "application/userAddition.v1+json" })
    @Produces({ "application/userAddition.v1+json" })
    public Response addUser(final UserDetails userDetails, @Context final ContainerRequestContext requestContext, @Context final HttpServletRequest httpServletRequest) throws APIException {
        final SecurityContext securityContext = requestContext.getSecurityContext();
        final User user = (User)securityContext.getUserPrincipal();
        final UserAccountService userAccountService = UacFactoryProvider.getUserAccountServiceObject();
        final JSONObject logData = userAccountService.getAddOrUpdateUserLogData(userDetails);
        Response response = null;
        Boolean success = Boolean.FALSE;
        try {
            final UserAccountValidator userAccountValidator = UacFactoryProvider.getUserAccountValidatorObject();
            userAccountValidator.validateAddition(userDetails);
            response = userAccountService.addUser(userDetails, user, httpServletRequest);
            success = (response.getStatus() == Response.Status.OK.getStatusCode());
        }
        finally {
            logData.put((Object)"is_successfull", (Object)success);
            SecurityOneLineLogger.log("User_Management", "user_addition", logData, Level.INFO);
        }
        return response;
    }
    
    @POST
    @Path("{loginID}")
    @Consumes({ "application/userModification.v1+json" })
    @Produces({ "application/userModification.v1+json" })
    public Response modifyUser(@PathParam("loginID") final Long loginID, final UserDetails userDetails, @Context final ContainerRequestContext requestContext, @Context final HttpServletRequest httpServletRequest) throws APIException {
        final SecurityContext securityContext = requestContext.getSecurityContext();
        final User user = (User)securityContext.getUserPrincipal();
        userDetails.setUserID(loginID);
        final UserAccountService userAccountService = UacFactoryProvider.getUserAccountServiceObject();
        final JSONObject logData = userAccountService.getAddOrUpdateUserLogData(userDetails);
        Response response = null;
        Boolean success = Boolean.FALSE;
        try {
            final UserAccountValidator userAccountValidator = UacFactoryProvider.getUserAccountValidatorObject();
            userAccountValidator.validateUpdate(userDetails);
            response = UacFactoryProvider.getUserAccountServiceObject().modifyUser(userDetails, user, httpServletRequest);
            success = (response.getStatus() == Response.Status.OK.getStatusCode());
        }
        finally {
            logData.put((Object)"is_successfull", (Object)success);
            SecurityOneLineLogger.log("User_Management", "user_modification", logData, Level.INFO);
        }
        return response;
    }
    
    @DELETE
    @Path("{loginID}")
    @Consumes({ "application/userDeletion.v1+json" })
    @Produces({ "application/userDeletion.v1+json" })
    public Response deleteUser(final Map<String, Object> contactDetails, @PathParam("loginID") final Long loginID, @Context final ContainerRequestContext requestContext, @Context final HttpServletRequest httpServletRequest) throws APIException {
        final SecurityContext securityContext = requestContext.getSecurityContext();
        final User currentUser = (User)securityContext.getUserPrincipal();
        final JSONObject logData = new JSONObject();
        logData.put((Object)"userid_tobe_deleted", (Object)loginID);
        logData.put((Object)"username_tobe_deleted", (Object)DMUserHandler.getDCUser(loginID));
        logData.put((Object)"domainname_tobe_deleted", (Object)DMUserHandler.getDCUserDomain(loginID));
        logData.put((Object)"requested_time", (Object)System.currentTimeMillis());
        Response response = null;
        Boolean success = Boolean.FALSE;
        try {
            final UserAccountValidator userAccountValidator = UacFactoryProvider.getUserAccountValidatorObject();
            userAccountValidator.validateDelete(loginID, contactDetails);
            response = UacFactoryProvider.getUserAccountServiceObject().deleteUser(loginID, currentUser, contactDetails, httpServletRequest);
            success = (response.getStatus() == Response.Status.OK.getStatusCode());
        }
        finally {
            logData.put((Object)"is_successfull", (Object)success);
            SecurityOneLineLogger.log("User_Management", "user_deleted", logData, Level.INFO);
        }
        return response;
    }
    
    @GET
    @Path("{loginID}")
    @Produces({ "application/userDetails.v1+json" })
    public Response getUserDetails(@PathParam("loginID") final Long loginID) throws APIException {
        return Response.status(Response.Status.OK).entity((Object)UacFactoryProvider.getUserAccountServiceObject().getUserDetails(loginID)).build();
    }
    
    @POST
    @Path("{loginID}/contact")
    @Consumes({ "application/userContactModification.v1+json" })
    @Produces({ "application/userContactModification.v1+json" })
    public Response modifyUserContactDetails(@PathParam("loginID") final Long loginID, final Map<String, Object> userDetails, @Context final ContainerRequestContext requestContext, @Context final HttpServletRequest httpServletRequest) throws APIException {
        final SecurityContext securityContext = requestContext.getSecurityContext();
        final User user = (User)securityContext.getUserPrincipal();
        userDetails.put("loginID", loginID);
        final UserAccountService userAccountService = UacFactoryProvider.getUserAccountServiceObject();
        return Response.status(Response.Status.OK).entity((Object)userAccountService.modifyUserContact(userDetails, user, httpServletRequest)).build();
    }
    
    @POST
    @Path("{loginID}/password/reset")
    @Produces({ "application/resetpassword.v1+json" })
    @Consumes({ "application/resetpassword.v1+json" })
    public Response resetpassword(@PathParam("loginID") final Long loginID, final Map<String, Object> probeDetails, @Context final ContainerRequestContext requestContext, @Context final HttpServletRequest httpServletRequest) throws APIException {
        final SecurityContext securityContext = requestContext.getSecurityContext();
        final User user = (User)securityContext.getUserPrincipal();
        final UserAccountService userAccountService = UacFactoryProvider.getUserAccountServiceObject();
        return Response.status(Response.Status.OK).entity((Object)userAccountService.sendPasswordLink(loginID, user, probeDetails, httpServletRequest)).build();
    }
    
    @GET
    @Path("{loginID}/token")
    @Produces({ "application/tokendetails.v1+json" })
    public Response getTokenDetail(@PathParam("loginID") final Long loginID, @Context final ContainerRequestContext requestContext) throws APIException {
        final UserAccountService userAccountService = UacFactoryProvider.getUserAccountServiceObject();
        return Response.status(Response.Status.OK).entity((Object)userAccountService.getTokenDetails(loginID)).build();
    }
    
    @POST
    @Path("{loginID}/reinvite")
    @Produces({ "application/reinviteuser.v1+json" })
    @Consumes({ "application/reinviteuser.v1+json" })
    public Response reinviteUser(@PathParam("loginID") final Long loginID, final Map<String, Object> probeDetails, @Context final ContainerRequestContext requestContext, @Context final HttpServletRequest httpServletRequest) throws APIException {
        final SecurityContext securityContext = requestContext.getSecurityContext();
        final User user = (User)securityContext.getUserPrincipal();
        final UserAccountService userAccountService = UacFactoryProvider.getUserAccountServiceObject();
        return Response.status(Response.Status.OK).entity((Object)userAccountService.resendUserInvite(loginID, user, probeDetails, httpServletRequest)).build();
    }
    
    @GET
    @Path("pre-add")
    @Produces({ "application/addUserDetails.v1+json" })
    @RestrictMatched("Probe")
    public Response getAddUserDetails(@Context final ContainerRequestContext requestContext) {
        final SecurityContext securityContext = requestContext.getSecurityContext();
        final User user = (User)securityContext.getUserPrincipal();
        final UserAccountService userAccountService = UacFactoryProvider.getUserAccountServiceObject();
        return Response.status(Response.Status.OK).entity((Object)userAccountService.getAddUserDetails(user)).build();
    }
    
    @POST
    @Path("mail/duplicate")
    @Produces({ "application/duplicateemail.v1+json" })
    @Consumes({ "application/duplicateemail.v1+json" })
    @RestrictMatched("Probe")
    public Response checkDuplicateMail(final Map<String, Object> userDetails, @Context final ContainerRequestContext requestContext) throws APIException {
        final SecurityContext securityContext = requestContext.getSecurityContext();
        final User user = (User)securityContext.getUserPrincipal();
        final UserAccountService userAccountService = UacFactoryProvider.getUserAccountServiceObject();
        return Response.status(Response.Status.OK).entity((Object)userAccountService.checkMailId(userDetails, user)).build();
    }
    
    @GET
    @Path("pre-start")
    @Produces({ "application/userprestart.v1+json" })
    public Map<String, Object> getPrestartDetails(@Context final SecurityContext securityContext) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        final UserAccountService userAccountService = UacFactoryProvider.getUserAccountServiceObject();
        return userAccountService.getUserStartDetails(user);
    }
    
    @POST
    @Path("admin/personalization")
    @Produces({ "application/adminPersonalizationStatus.v1+json" })
    @Consumes({ "application/adminPersonalizationStatus.v1+json" })
    public Map<String, Object> putAdminPersonalisePage(final Map<String, Object> detailsMap, @Context final SecurityContext securityContext, @Context final HttpServletRequest httpServletRequest) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        final UserAccountService userAccountService = UacFactoryProvider.getUserAccountServiceObject();
        return userAccountService.updateAdminPersonalizationDetails(detailsMap, user, httpServletRequest);
    }
    
    static {
        UserAccountController.logger = Logger.getLogger("UserManagementLogger");
    }
}
