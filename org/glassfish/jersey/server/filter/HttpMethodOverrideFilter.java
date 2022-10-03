package org.glassfish.jersey.server.filter;

import java.util.Iterator;
import javax.ws.rs.core.UriBuilder;
import java.util.List;
import java.util.Map;
import org.glassfish.jersey.server.ContainerRequest;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedMap;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.util.Tokenizer;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Configuration;
import org.glassfish.jersey.server.ResourceConfig;
import javax.annotation.Priority;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.container.ContainerRequestFilter;

@PreMatching
@Priority(3050)
public final class HttpMethodOverrideFilter implements ContainerRequestFilter
{
    final int config;
    
    public static void enableFor(final ResourceConfig rc, final Source... sources) {
        rc.registerClasses(HttpMethodOverrideFilter.class);
        rc.property("jersey.config.server.httpMethodOverride", sources);
    }
    
    public HttpMethodOverrideFilter(@Context final Configuration rc) {
        this(parseConfig(rc.getProperty("jersey.config.server.httpMethodOverride")));
    }
    
    public HttpMethodOverrideFilter(final Source... sources) {
        int c = 0;
        for (final Source cf : sources) {
            if (cf != null) {
                c |= cf.getFlag();
            }
        }
        if (c == 0) {
            c = 3;
        }
        this.config = c;
    }
    
    private static Source[] parseConfig(final Object config) {
        if (config == null) {
            return new Source[0];
        }
        if (config instanceof Source[]) {
            return (Source[])config;
        }
        if (config instanceof Source) {
            return new Source[] { (Source)config };
        }
        String[] stringValues;
        if (config instanceof String) {
            stringValues = Tokenizer.tokenize((String)config, " ,;\n");
        }
        else {
            if (!(config instanceof String[])) {
                return new Source[0];
            }
            stringValues = Tokenizer.tokenize((String[])config, " ,;\n");
        }
        final Source[] result = new Source[stringValues.length];
        for (int i = 0; i < stringValues.length; ++i) {
            try {
                result[i] = Source.valueOf(stringValues[i]);
            }
            catch (final IllegalArgumentException e) {
                Logger.getLogger(HttpMethodOverrideFilter.class.getName()).log(Level.WARNING, LocalizationMessages.INVALID_CONFIG_PROPERTY_VALUE("jersey.config.server.httpMethodOverride", stringValues[i]));
            }
        }
        return result;
    }
    
    private String getParamValue(final Source source, final MultivaluedMap<String, String> paramsMap, final String paramName) {
        String value = source.isPresentIn(this.config) ? ((String)paramsMap.getFirst((Object)paramName)) : null;
        if (value == null) {
            return null;
        }
        value = value.trim();
        return (value.length() == 0) ? null : value.toUpperCase();
    }
    
    public void filter(final ContainerRequestContext request) {
        if (!request.getMethod().equalsIgnoreCase("POST")) {
            return;
        }
        final String header = this.getParamValue(Source.HEADER, (MultivaluedMap<String, String>)request.getHeaders(), "X-HTTP-Method-Override");
        final String query = this.getParamValue(Source.QUERY, (MultivaluedMap<String, String>)request.getUriInfo().getQueryParameters(), "_method");
        String override;
        if (header == null) {
            override = query;
        }
        else {
            override = header;
            if (query != null && !query.equals(header)) {
                throw new BadRequestException();
            }
        }
        if (override != null) {
            request.setMethod(override);
            if (override.equals("GET") && request.getMediaType() != null && MediaType.APPLICATION_FORM_URLENCODED_TYPE.getType().equals(request.getMediaType().getType())) {
                final UriBuilder ub = request.getUriInfo().getRequestUriBuilder();
                final Form f = ((ContainerRequest)request).readEntity(Form.class);
                for (final Map.Entry<String, List<String>> param : f.asMap().entrySet()) {
                    ub.queryParam((String)param.getKey(), param.getValue().toArray());
                }
                request.setRequestUri(request.getUriInfo().getBaseUri(), ub.build(new Object[0]));
            }
        }
    }
    
    public enum Source
    {
        HEADER(1), 
        QUERY(2);
        
        private final int flag;
        
        private Source(final int flag) {
            this.flag = flag;
        }
        
        public int getFlag() {
            return this.flag;
        }
        
        public boolean isPresentIn(final int config) {
            return (config & this.flag) == this.flag;
        }
    }
}
