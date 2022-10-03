package com.me.ems.framework.common.api.v1.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HATEOAS
{
    private HATEOASLink hateoasLinks;
    
    @JsonProperty("_links")
    public HATEOASLink getHateoasLinks() {
        return this.hateoasLinks;
    }
    
    @JsonProperty("_links")
    public void setHateoasLinks(final HATEOASLink dcHateoasLinks) {
        this.hateoasLinks = dcHateoasLinks;
    }
    
    public static Map getHateoasAsMAP(final HATEOAS dchateoas) {
        final ObjectMapper objectMapper = new ObjectMapper();
        return (Map)objectMapper.convertValue((Object)dchateoas, (Class)Map.class);
    }
}
