package com.me.mdm.api.controller;

import com.me.mdm.api.paging.annotations.AllCustomerSearchParam;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Iterator;
import org.json.JSONObject;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

public class JerseyUtil
{
    public static <T> T extractQueryParams(@Context final UriInfo uriInfo, final Class<T> valueType) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final HashMap<String, Object> queryParams = new HashMap<String, Object>();
        for (final Map.Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet()) {
            final String value = entry.getValue().get(0);
            queryParams.put(entry.getKey(), value);
        }
        return (T)mapper.readValue(new JSONObject((Map)queryParams).toString(), (Class)valueType);
    }
    
    public static AllCustomerSearchParam getInstance(final String tableName, final String columnName) {
        return getInstance(tableName, columnName, 8);
    }
    
    public static AllCustomerSearchParam getInstance(final String tableName, final String columnName, final int comparator) {
        final AllCustomerSearchParam annotation = new AllCustomerSearchParam() {
            @Override
            public String tableName() {
                return tableName;
            }
            
            @Override
            public String columnName() {
                return columnName;
            }
            
            @Override
            public int comparator() {
                return comparator;
            }
            
            @Override
            public Class<? extends AllCustomerSearchParam> annotationType() {
                return AllCustomerSearchParam.class;
            }
        };
        return annotation;
    }
}
