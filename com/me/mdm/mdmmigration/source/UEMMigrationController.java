package com.me.mdm.mdmmigration.source;

import javax.ws.rs.GET;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.mdmmigration.target.AFWMigrationDataUpdateManager;
import javax.ws.rs.POST;
import java.util.Iterator;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.logging.Logger;
import javax.ws.rs.Path;
import com.me.mdm.api.common.controller.BaseController;

@Path("migration")
public class UEMMigrationController extends BaseController
{
    private final Logger logger;
    
    public UEMMigrationController() {
        this.logger = Logger.getLogger("MDMMigrationLogger");
    }
    
    @POST
    @Path("/fetchAFWAccount")
    public AFWResponseModel getAFWAccountDetails(@Context final ContainerRequestContext requestContext, final AFWRequestModel afwRequestModel, @Context final UriInfo uriInfo) throws Exception {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final HashMap<String, Object> queryParams = new HashMap<String, Object>();
            for (final Map.Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet()) {
                final String value = entry.getValue().get(0);
                queryParams.put(entry.getKey(), value);
            }
            final AFWRequestModel request = (AFWRequestModel)mapper.readValue(new JSONObject((Map)queryParams).toString(), (Class)AFWRequestModel.class);
            request.setTopic(afwRequestModel.getTopic());
            request.setKey(afwRequestModel.getKey());
            request.setCustomerUserDetails(requestContext);
            return new UEMMigrationService().getMigrationDataForRequest(request);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in syncing AFW ", ex);
            throw ex;
        }
    }
    
    @POST
    @Path("/addAFWAccount")
    public void AFWAccountMigration(@Context final ContainerRequestContext requestContext, final AFWRequestModel afwRequestModel) throws Exception {
        try {
            afwRequestModel.setCustomerUserDetails(requestContext);
            new AFWMigrationDataUpdateManager().AFWUpdateDetailsForMigration(afwRequestModel.getCustomerId(), afwRequestModel.getUserId(), afwRequestModel);
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in AFWAccountMigrationAPIRequestHandler", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @POST
    @Path("/addAFWUser")
    public void AFWUserMigration(@Context final ContainerRequestContext requestContext, final AFWRequestModel afwRequestModel) throws Exception {
        try {
            afwRequestModel.setCustomerUserDetails(requestContext);
            new AFWMigrationDataUpdateManager().AFWUpdateUsersAndAccounts(afwRequestModel.getCustomerId(), afwRequestModel);
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in AFWUserMigrationAPIRequestHandler", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @POST
    @Path("/feature_params")
    public Map enableMigrationfeatureParams(@Context final ContainerRequestContext requestContext, final Map requestParam) throws APIHTTPException {
        try {
            this.logger.log(Level.INFO, "Enabling Migration Feature Params Here");
            return new UEMMigrationService().updateMigrationFeatureParams(requestParam);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in EnableMigrationFeatureParams API", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @GET
    @Path("/build_check")
    public Map doMigrationBuildCheck(@Context final ContainerRequestContext requestContext) throws APIHTTPException {
        try {
            this.logger.log(Level.INFO, "Checking is buildLatest here");
            return new UEMMigrationService().isBuildCompatibleForMigration();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in checking build version", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
