package com.me.ems.framework.common.api.v1.controller;

import com.me.devicemanagement.framework.common.api.v1.model.DCViewFilter;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import java.util.ArrayList;
import java.util.HashMap;
import javax.ws.rs.WebApplicationException;
import com.me.ems.framework.common.api.response.APIResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import java.util.List;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.Map;
import javax.ws.rs.QueryParam;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.framework.common.api.v1.service.ViewFilterService;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.Path;

@Path("viewFilters")
public class ViewFilterController
{
    @Context
    SecurityContext securityContext;
    ViewFilterService viewFilterService;
    
    public ViewFilterController() {
        this.viewFilterService = new ViewFilterService();
    }
    
    private User getDCUserFromContext() {
        final User dcUser = (User)this.securityContext.getUserPrincipal();
        return dcUser;
    }
    
    @GET
    @Path("savedFilters")
    @Produces({ "application/viewFilters.v1+json" })
    public Map fetchSavedFilters(@Context final SecurityContext securityContext, @QueryParam("pageID") final Long pageID, @QueryParam("viewID") final Long viewID) throws APIException {
        if (pageID == null || viewID == null) {
            final String missingParam = (pageID == null && viewID == null) ? "pageID,viewID" : ((pageID == null) ? "pageID" : "viewID");
            throw new APIException("GENERIC0003", null, new String[] { missingParam });
        }
        final ViewFilterService dcViewFilterService = new ViewFilterService();
        final User dcUser = (User)securityContext.getUserPrincipal();
        final Map savedFilterMap = dcViewFilterService.fetchSavedFilterMap(pageID, viewID, dcUser.getLoginID());
        return savedFilterMap;
    }
    
    @GET
    @Path("filterColumns")
    @Produces({ "application/viewFilterColumns.v1+json" })
    public List fetchApplicableCRColumnsForView(@QueryParam("viewID") final Long viewID) throws APIException {
        if (viewID == null) {
            final String missingParam = "viewID";
            throw new APIException("GENERIC0003", null, new String[] { missingParam });
        }
        final ViewFilterService dcViewFilterService = new ViewFilterService();
        return dcViewFilterService.fetchCRColumnDetailsForView(viewID);
    }
    
    @GET
    @Path("filterColumnValues/{columnID}")
    @Produces({ "application/columnBrowseValues.v1+json" })
    public List fetchColumnBrowseValues(@PathParam("columnID") final Long columnID, @QueryParam("viewID") final Long viewID, @DefaultValue("0") @QueryParam("offset") final Integer offset, @DefaultValue("0") @QueryParam("limit") final Integer limit, @DefaultValue("") @QueryParam("filter") final String filter) throws APIException {
        final User dcUser = this.getDCUserFromContext();
        final Long loginID = dcUser.getLoginID();
        if (viewID == null) {
            final String missingParam = "viewID";
            throw new WebApplicationException(APIResponse.missingParamErrorResponse(missingParam));
        }
        final Map filterMap = new HashMap();
        filterMap.put("offset", offset);
        filterMap.put("limit", limit);
        filterMap.put("filter", filter);
        filterMap.put("customSearchValues", new ArrayList());
        return this.viewFilterService.getColumnBrowseValues(columnID, viewID, filterMap, loginID);
    }
    
    @POST
    @Path("filterColumnValues/{columnID}")
    @Produces({ "application/columnBrowseValues.v1+json" })
    @Consumes({ "application/customBrowseValuesList.v1+json" })
    public List fetchCustomColumnBrowseValues(@PathParam("columnID") final Long columnID, @QueryParam("viewID") final Long viewID, final List<String> customSearchValues) throws APIException {
        final User dcUser = this.getDCUserFromContext();
        final Long loginID = dcUser.getLoginID();
        if (viewID == null) {
            final String missingParam = "viewID";
            throw new WebApplicationException(APIResponse.missingParamErrorResponse(missingParam));
        }
        final Map filterMap = new HashMap();
        filterMap.put("offset", 0);
        filterMap.put("limit", 0);
        filterMap.put("filter", null);
        filterMap.put("customSearchValues", customSearchValues);
        return this.viewFilterService.getColumnBrowseValues(columnID, viewID, filterMap, loginID);
    }
    
    @GET
    @Path("/filterExists")
    @Produces({ "application/filterExistStatus.v1+json" })
    public Map isFilterNameExists(@QueryParam("filterName") final String filterName, @QueryParam("viewID") final Long viewID, @QueryParam("pageID") final Long pageID) throws APIException {
        if (pageID == null || viewID == null || filterName == null) {
            final String missingParam = (filterName == null) ? "filterName" : ((pageID == null && viewID == null) ? "pageID,viewID" : ((pageID == null) ? "pageID" : "viewID"));
            throw new WebApplicationException(APIResponse.missingParamErrorResponse(missingParam));
        }
        return this.viewFilterService.isFilterNameExistsCheck(filterName, pageID, viewID, this.getDCUserFromContext().getLoginID(), null);
    }
    
    @POST
    @Consumes({ "application/viewFilterDetails.v1+json" })
    @Produces({ "application/viewFilter.v1+json" })
    public Map saveDCViewFilter(@DefaultValue("false") @QueryParam("isAnonymous") final Boolean isAnonymous, final Map dcViewFilterMap) throws APIException {
        return this.viewFilterService.saveDCViewFilter(isAnonymous, dcViewFilterMap, this.getDCUserFromContext().getLoginID(), this.getDCUserFromContext().getName());
    }
    
    @PUT
    @Path("{filterID}")
    @Consumes({ "application/viewFilterDetails.v1+json" })
    public Response updateDCViewFilter(@PathParam("filterID") final Long filterID, final Map filterMap) throws APIException {
        this.viewFilterService.updateDCViewFilter(filterID, filterMap, this.getDCUserFromContext().getLoginID(), this.getDCUserFromContext().getName());
        return Response.ok().build();
    }
    
    @DELETE
    @Path("{filterID}")
    public Response deleteFilter(@PathParam("filterID") final Long filterID) throws APIException {
        this.viewFilterService.deleteFilter(filterID, this.getDCUserFromContext().getLoginID(), this.getDCUserFromContext().getName());
        return Response.ok().build();
    }
    
    @GET
    @Path("{filterID}/criteria")
    @Produces({ "application/viewFilterCriteria.v1+json" })
    public DCViewFilter getCriteriaJSONForFilter(@PathParam("filterID") final Long filterID) throws APIException {
        return this.viewFilterService.getCriteriaJSONForFilter(filterID);
    }
}
