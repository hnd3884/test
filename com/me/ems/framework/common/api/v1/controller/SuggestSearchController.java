package com.me.ems.framework.common.api.v1.controller;

import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.logging.Level;
import com.fasterxml.jackson.core.type.TypeReference;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.ems.framework.common.api.v1.service.SuggestSearchService;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.logging.Logger;
import javax.ws.rs.Path;

@Path("suggestsearch")
public class SuggestSearchController
{
    private static Logger advSearchErrorLogger;
    
    @GET
    @Produces({ "application/searchsuggestionresponse.v1+json" })
    public Map suggestSearch(@Context final UriInfo uriInfo) throws APIException {
        try {
            final MultivaluedMap<String, String> userParams = (MultivaluedMap<String, String>)uriInfo.getQueryParameters();
            final String searchValue = ((List)userParams.get((Object)"searchValue")).get(0);
            final String selectedSearchParamId = ((List)userParams.get((Object)"selectedSearchParamId")).get(0);
            final Map map = new SuggestSearchService().getSearchSuggestionList(searchValue, selectedSearchParamId);
            return (Map)new ObjectMapper().readValue(new JSONObject(map).toString(), (TypeReference)new TypeReference<Map<String, Object>>() {});
        }
        catch (final IOException e) {
            SuggestSearchController.advSearchErrorLogger.log(Level.SEVERE, "Exception in getting Suggestions for search", e);
            throw new APIException("ADV_SEARCH0001");
        }
    }
    
    static {
        SuggestSearchController.advSearchErrorLogger = Logger.getLogger("AdvSearchError");
    }
}
