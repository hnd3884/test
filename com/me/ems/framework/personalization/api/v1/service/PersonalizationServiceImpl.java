package com.me.ems.framework.personalization.api.v1.service;

import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.util.Map;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.logging.Logger;
import com.me.ems.framework.personalization.factory.PersonalizationService;

public class PersonalizationServiceImpl implements PersonalizationService
{
    protected Logger logger;
    
    public PersonalizationServiceImpl() {
        this.logger = Logger.getLogger(PersonalizationServiceImpl.class.getName());
    }
    
    @Override
    public Map<String, Object> showPersonalisePage(final User user) throws APIException {
        Map<String, Object> returnMap;
        try {
            this.logger.log(Level.INFO, "Entered into showPersonalisePage");
            returnMap = ApiFactoryProvider.getPersonalizationAPIForRest().getPersonalizeSettings(user);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while loading personalise page", e);
            throw new APIException("GENERIC0005");
        }
        return returnMap;
    }
    
    @Override
    public Map<String, Object> updatePersonalizationDetails(final Map<String, Object> detailsMap, final User dcUser, final HttpServletRequest request) throws APIException {
        Map<String, Object> returnMap;
        try {
            returnMap = ApiFactoryProvider.getPersonalizationAPIForRest().updatePersonalizeSettings(detailsMap, dcUser, request);
        }
        catch (final Exception e) {
            if (e instanceof APIException) {
                throw (APIException)e;
            }
            this.logger.log(Level.WARNING, "Exception while personalising ", e);
            throw new APIException("GENERIC0005");
        }
        return returnMap;
    }
    
    @Override
    public Map<String, Object> getUserDP(final User user) throws APIException {
        Map<String, Object> userImagePath;
        try {
            userImagePath = ApiFactoryProvider.getPersonalizationAPIForRest().getUserImage(user);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception while getting User Image ", ex);
            throw new APIException("GENERIC0005");
        }
        return userImagePath;
    }
    
    @Override
    public Map<String, Object> getActiveSession(final User user) throws APIException {
        try {
            return ApiFactoryProvider.getPersonalizationAPIForRest().getActiveSessions(user);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception while getting Active Session Count ", ex);
            throw new APIException("GENERIC0005");
        }
    }
    
    @Override
    public Response deleteActiveSession(final User user, final String operation, final HttpServletRequest request) throws APIException {
        try {
            final Long sessionID = operation.equals("allExceptCurrent") ? null : Long.valueOf(Long.parseLong(operation));
            final boolean isSessionDeleted = ApiFactoryProvider.getPersonalizationAPIForRest().deleteActiveSession(sessionID, request, user);
            if (!isSessionDeleted) {
                throw new APIException("USER0002");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception while deleting active session of user : " + user.getName(), ex);
            throw new APIException("GENERIC0005");
        }
        return Response.noContent().build();
    }
    
    @Override
    public Response closeAllSessions(final User user) throws APIException {
        try {
            ApiFactoryProvider.getPersonalizationAPIForRest().closeAllSessions(user);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception while deleting active session of user : " + user.getName(), ex);
            throw new APIException("GENERIC0005");
        }
        return Response.noContent().build();
    }
}
