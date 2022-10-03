package com.sun.xml.internal.ws.transport.http.client;

import com.oracle.webservices.internal.api.message.PropertySet;
import java.util.List;
import java.util.Map;
import com.sun.istack.internal.NotNull;
import com.oracle.webservices.internal.api.message.BasePropertySet;

final class HttpResponseProperties extends BasePropertySet
{
    private final HttpClientTransport deferedCon;
    private static final PropertyMap model;
    
    public HttpResponseProperties(@NotNull final HttpClientTransport con) {
        this.deferedCon = con;
    }
    
    @PropertySet.Property({ "javax.xml.ws.http.response.headers" })
    public Map<String, List<String>> getResponseHeaders() {
        return this.deferedCon.getHeaders();
    }
    
    @PropertySet.Property({ "javax.xml.ws.http.response.code" })
    public int getResponseCode() {
        return this.deferedCon.statusCode;
    }
    
    @Override
    protected PropertyMap getPropertyMap() {
        return HttpResponseProperties.model;
    }
    
    static {
        model = BasePropertySet.parse(HttpResponseProperties.class);
    }
}
