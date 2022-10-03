package org.glassfish.jersey.message.filtering;

import org.glassfish.jersey.internal.util.Tokenizer;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.lang.annotation.Annotation;
import javax.annotation.PostConstruct;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Configuration;
import javax.inject.Singleton;
import org.glassfish.jersey.message.filtering.spi.ScopeResolver;

@Singleton
public class SelectableScopeResolver implements ScopeResolver
{
    public static final String PREFIX;
    public static final String DEFAULT_SCOPE;
    private static String SELECTABLE_PARAM_NAME;
    @Context
    private Configuration configuration;
    @Context
    private UriInfo uriInfo;
    
    @PostConstruct
    private void init() {
        final String paramName = (String)this.configuration.getProperty("jersey.config.entityFiltering.selectable.query");
        SelectableScopeResolver.SELECTABLE_PARAM_NAME = ((paramName != null) ? paramName : SelectableScopeResolver.SELECTABLE_PARAM_NAME);
    }
    
    @Override
    public Set<String> resolve(final Annotation[] annotations) {
        final Set<String> scopes = new HashSet<String>();
        final List<String> fields = (List<String>)this.uriInfo.getQueryParameters().get((Object)SelectableScopeResolver.SELECTABLE_PARAM_NAME);
        if (fields != null && !fields.isEmpty()) {
            for (final String field : fields) {
                scopes.addAll(this.getScopesForField(field));
            }
        }
        else {
            scopes.add(SelectableScopeResolver.DEFAULT_SCOPE);
        }
        return scopes;
    }
    
    private Set<String> getScopesForField(final String fieldName) {
        final Set<String> scopes = new HashSet<String>();
        final String[] tokenize;
        final String[] fields = tokenize = Tokenizer.tokenize(fieldName, ",");
        for (final String field : tokenize) {
            final String[] subfields = Tokenizer.tokenize(field, ".");
            scopes.add(SelectableScopeResolver.PREFIX + subfields[0]);
            if (subfields.length > 1) {
                scopes.add(SelectableScopeResolver.PREFIX + field);
            }
        }
        return scopes;
    }
    
    static {
        PREFIX = SelectableScopeResolver.class.getName() + "_";
        DEFAULT_SCOPE = SelectableScopeResolver.PREFIX + "*";
        SelectableScopeResolver.SELECTABLE_PARAM_NAME = "select";
    }
}
