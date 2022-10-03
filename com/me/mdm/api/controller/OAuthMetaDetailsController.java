package com.me.mdm.api.controller;

import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;
import javax.ws.rs.POST;
import com.me.mdm.directory.api.oauth.model.OAuthMetaModel;
import com.me.mdm.directory.api.oauth.model.OAuthMetaInputModel;
import com.me.mdm.api.common.Validator;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import com.me.mdm.directory.api.oauth.model.OAuthMetaListModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.me.mdm.directory.api.oauth.model.SearchOAuth;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import com.me.mdm.directory.api.oauth.service.OAuthMetaService;
import javax.ws.rs.Path;
import com.me.mdm.api.common.controller.BaseController;

@Path("oauthmeta")
public class OAuthMetaDetailsController extends BaseController
{
    private OAuthMetaService oAuthMetaService;
    
    public OAuthMetaDetailsController() {
        this.oAuthMetaService = new OAuthMetaService();
    }
    
    private SearchOAuth getOAuthMeta(@Context final ContainerRequestContext requestContext, @Context final UriInfo uriInfo) throws JsonProcessingException {
        final SearchOAuth searchOAuth = JerseyUtil.extractQueryParams(uriInfo, SearchOAuth.class);
        searchOAuth.setCustomerUserDetails(requestContext);
        return searchOAuth;
    }
    
    @GET
    public OAuthMetaListModel getOAuthMetas(@Context final ContainerRequestContext requestContext, @Context final UriInfo uriInfo) throws Exception {
        final SearchOAuth searchOAuthMeta = this.getOAuthMeta(requestContext, uriInfo);
        return this.oAuthMetaService.getOAuthMetas(searchOAuthMeta);
    }
    
    @GET
    @Validator(pathParam = "oauth_meta_id", authorizerClass = "com.me.mdm.directory.api.oauth.service.OAuthAuthorizer")
    @Path("/{oauth_meta_id}")
    public OAuthMetaListModel getOAuthBasedOnMetaId(@Context final ContainerRequestContext requestContext, @Context final UriInfo uriInfo, @PathParam("oauth_meta_id") final Long oauthMetaID) throws Exception {
        final SearchOAuth searchOAuthMeta = this.getOAuthMeta(requestContext, uriInfo);
        searchOAuthMeta.setOAUthMetaID(oauthMetaID);
        return this.oAuthMetaService.getOAuthMetas(searchOAuthMeta);
    }
    
    @GET
    @Validator(pathParam = "oauth_token_id", authorizerClass = "com.me.mdm.directory.api.oauth.service.OAuthAuthorizer")
    @Path("token/{oauth_token_id}")
    public OAuthMetaListModel getOAuthBasedOnTokenId(@Context final ContainerRequestContext requestContext, @Context final UriInfo uriInfo, @PathParam("oauth_token_id") final Long oauthTokenID) throws Exception {
        final SearchOAuth searchOAuthMeta = this.getOAuthMeta(requestContext, uriInfo);
        searchOAuthMeta.setOAUthTokenID(oauthTokenID);
        return this.oAuthMetaService.getOAuthMetas(searchOAuthMeta);
    }
    
    @GET
    @Validator(pathParam = "client_id", authorizerClass = "com.me.mdm.directory.api.oauth.service.OAuthAuthorizer")
    @Path("clientid/{client_id}")
    public OAuthMetaListModel getOAuthBasedOnClientID(@Context final ContainerRequestContext requestContext, @Context final UriInfo uriInfo, @PathParam("client_id") final String clientID) throws Exception {
        final SearchOAuth searchOAuthMeta = this.getOAuthMeta(requestContext, uriInfo);
        searchOAuthMeta.setOAUthClientID(clientID);
        return this.oAuthMetaService.getOAuthMetas(searchOAuthMeta);
    }
    
    @GET
    @Path("type/{oauth_type}")
    public OAuthMetaListModel getOAuthBasedOnType(@Context final ContainerRequestContext requestContext, @Context final UriInfo uriInfo, @PathParam("oauth_type") final int oauthType) throws Exception {
        final SearchOAuth searchOAuthMeta = this.getOAuthMeta(requestContext, uriInfo);
        searchOAuthMeta.setOAUthType(oauthType);
        return this.oAuthMetaService.getOAuthMetas(searchOAuthMeta);
    }
    
    @POST
    public OAuthMetaModel addOAuthDetails(@Context final ContainerRequestContext requestContext, final OAuthMetaInputModel oAuthMetaInputModel) throws Exception {
        oAuthMetaInputModel.setCustomerUserDetails(requestContext);
        final OAuthMetaModel oAuthMetaModel = this.oAuthMetaService.addOAuthDetails(oAuthMetaInputModel);
        return oAuthMetaModel;
    }
    
    @PUT
    @Validator(pathParam = "oauth_meta_id", authorizerClass = "com.me.mdm.directory.api.oauth.service.OAuthAuthorizer")
    @Path("/{oauth_meta_id}")
    public Response updateOAuthDetails(@Context final ContainerRequestContext requestContext, @PathParam("oauth_meta_id") final Long oauthMetaID, final OAuthMetaInputModel oAuthMetaInputModel) throws Exception {
        oAuthMetaInputModel.setOauthMetaID(oauthMetaID);
        oAuthMetaInputModel.setCustomerUserDetails(requestContext);
        this.oAuthMetaService.updateOAuthDetails(oAuthMetaInputModel);
        return Response.status(202).build();
    }
    
    @DELETE
    @Validator(pathParam = "oauth_meta_id", authorizerClass = "com.me.mdm.directory.api.oauth.service.OAuthAuthorizer")
    @Path("/{oauth_meta_id}")
    public Response deleteOAuthDetails(@Context final ContainerRequestContext requestContext, @Context final UriInfo uriInfo, @PathParam("oauth_meta_id") final Long oauthMetaID) throws Exception {
        final SearchOAuth searchOAuthMeta = this.getOAuthMeta(requestContext, uriInfo);
        searchOAuthMeta.setOAUthMetaID(oauthMetaID);
        this.oAuthMetaService.deleteOAuthDetails(searchOAuthMeta);
        return Response.status(202).build();
    }
}
