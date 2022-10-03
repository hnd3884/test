package com.me.ems.onpremise.uac.api.v1.controller;

import com.me.ems.onpremise.uac.api.v1.service.factory.PasswordServiceFactoryProvider;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.annotations.RestrictMatched;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import com.me.ems.onpremise.uac.api.v1.service.factory.ChangePasswordService;
import javax.ws.rs.Path;

@Path("password")
public class ChangePasswordController
{
    private static final ChangePasswordService CHANGE_PWD_SERVICE;
    
    @PUT
    @Consumes({ "application/changePassword.v1+json" })
    @Produces({ "application/changePasswordStatus.v1+json" })
    @RestrictMatched("Probe")
    public Map<String, Object> modifyPassword(@Context final SecurityContext securityContext, @Context final HttpServletRequest httpServletRequest, final Map<String, String> passwordDetails) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        final JSONObject jsonObject = new JSONObject();
        Level level = Level.INFO;
        try {
            jsonObject.put((Object)"REMARKS", (Object)"success");
            return ChangePasswordController.CHANGE_PWD_SERVICE.changePasswordAndCloseSession(user, httpServletRequest, passwordDetails);
        }
        catch (final APIException exception) {
            level = Level.SEVERE;
            jsonObject.put((Object)"REMARKS", (Object)exception.getErrorMsg());
            throw exception;
        }
        finally {
            SecurityOneLineLogger.log("User_Management", "Password_Updation", jsonObject, level);
        }
    }
    
    @GET
    @Path("complexity")
    @Consumes({ "application/complexityData.v1+json" })
    @RestrictMatched("Probe")
    public Map<String, Object> getComplexityDetails() {
        return ChangePasswordController.CHANGE_PWD_SERVICE.getPasswordComplexity();
    }
    
    static {
        CHANGE_PWD_SERVICE = PasswordServiceFactoryProvider.getChangePasswordServiceObject();
    }
}
