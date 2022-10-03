package org.glassfish.jersey.message.internal;

import java.util.Arrays;
import org.glassfish.jersey.internal.util.Tokenizer;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.StringTokenizer;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.util.logging.Logger;
import javax.inject.Singleton;
import javax.ws.rs.core.Link;
import org.glassfish.jersey.spi.HeaderDelegateProvider;

@Singleton
public class LinkProvider implements HeaderDelegateProvider<Link>
{
    private static final Logger LOGGER;
    
    @Override
    public boolean supports(final Class<?> type) {
        return Link.class.isAssignableFrom(type);
    }
    
    public Link fromString(final String value) throws IllegalArgumentException {
        return initBuilder(new JerseyLink.Builder(), value).build(new Object[0]);
    }
    
    static JerseyLink.Builder initBuilder(JerseyLink.Builder lb, String value) {
        Utils.throwIllegalArgumentExceptionIfNull(value, LocalizationMessages.LINK_IS_NULL());
        try {
            value = value.trim();
            if (!value.startsWith("<")) {
                throw new IllegalArgumentException("Missing starting token < in " + value);
            }
            final int gtIndex = value.indexOf(62);
            if (gtIndex == -1) {
                throw new IllegalArgumentException("Missing token > in " + value);
            }
            lb.uri(value.substring(1, gtIndex).trim());
            final String params = value.substring(gtIndex + 1).trim();
            final StringTokenizer st = new StringTokenizer(params, ";=\"", true);
            while (st.hasMoreTokens()) {
                checkToken(st, ";");
                final String n = st.nextToken().trim();
                checkToken(st, "=");
                String v = nextNonEmptyToken(st);
                if (v.equals("\"")) {
                    v = st.nextToken();
                    checkToken(st, "\"");
                }
                lb.param(n, v);
            }
        }
        catch (final Throwable e) {
            if (LinkProvider.LOGGER.isLoggable(Level.FINER)) {
                LinkProvider.LOGGER.log(Level.FINER, "Error parsing link value '" + value + "'", e);
            }
            lb = null;
        }
        if (lb == null) {
            throw new IllegalArgumentException("Unable to parse link " + value);
        }
        return lb;
    }
    
    private static String nextNonEmptyToken(final StringTokenizer st) throws IllegalArgumentException {
        String token;
        do {
            token = st.nextToken().trim();
        } while (token.length() == 0);
        return token;
    }
    
    private static void checkToken(final StringTokenizer st, final String expected) throws IllegalArgumentException {
        String token;
        do {
            token = st.nextToken().trim();
        } while (token.length() == 0);
        if (!token.equals(expected)) {
            throw new IllegalArgumentException("Expected token " + expected + " but found " + token);
        }
    }
    
    public String toString(final Link value) {
        return stringfy(value);
    }
    
    static String stringfy(final Link value) {
        Utils.throwIllegalArgumentExceptionIfNull(value, LocalizationMessages.LINK_IS_NULL());
        final Map<String, String> map = value.getParams();
        final StringBuilder sb = new StringBuilder();
        sb.append('<').append(value.getUri()).append('>');
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            sb.append("; ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
        }
        return sb.toString();
    }
    
    static List<String> getLinkRelations(final String rel) {
        return (rel == null) ? null : Arrays.asList(Tokenizer.tokenize(rel, "\" "));
    }
    
    static {
        LOGGER = Logger.getLogger(LinkProvider.class.getName());
    }
}
