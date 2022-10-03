package org.glassfish.jersey.logging;

import org.glassfish.jersey.message.MessageUtils;
import javax.ws.rs.client.ClientResponseContext;
import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.client.ClientRequestContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Priority;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.ClientRequestFilter;

@ConstrainedTo(RuntimeType.CLIENT)
@PreMatching
@Priority(Integer.MAX_VALUE)
final class ClientLoggingFilter extends LoggingInterceptor implements ClientRequestFilter, ClientResponseFilter
{
    public ClientLoggingFilter(final Logger logger, final Level level, final LoggingFeature.Verbosity verbosity, final int maxEntitySize) {
        super(logger, level, verbosity, maxEntitySize);
    }
    
    public void filter(final ClientRequestContext context) throws IOException {
        if (!this.logger.isLoggable(this.level)) {
            return;
        }
        final long id = this._id.incrementAndGet();
        context.setProperty(ClientLoggingFilter.LOGGING_ID_PROPERTY, (Object)id);
        final StringBuilder b = new StringBuilder();
        this.printRequestLine(b, "Sending client request", id, context.getMethod(), context.getUri());
        this.printPrefixedHeaders(b, id, "> ", (MultivaluedMap<String, String>)context.getStringHeaders());
        if (context.hasEntity() && LoggingInterceptor.printEntity(this.verbosity, context.getMediaType())) {
            final OutputStream stream = new LoggingStream(b, context.getEntityStream());
            context.setEntityStream(stream);
            context.setProperty(ClientLoggingFilter.ENTITY_LOGGER_PROPERTY, (Object)stream);
        }
        else {
            this.log(b);
        }
    }
    
    public void filter(final ClientRequestContext requestContext, final ClientResponseContext responseContext) throws IOException {
        if (!this.logger.isLoggable(this.level)) {
            return;
        }
        final Object requestId = requestContext.getProperty(ClientLoggingFilter.LOGGING_ID_PROPERTY);
        final long id = (long)((requestId != null) ? requestId : this._id.incrementAndGet());
        final StringBuilder b = new StringBuilder();
        this.printResponseLine(b, "Client response received", id, responseContext.getStatus());
        this.printPrefixedHeaders(b, id, "< ", (MultivaluedMap<String, String>)responseContext.getHeaders());
        if (responseContext.hasEntity() && LoggingInterceptor.printEntity(this.verbosity, responseContext.getMediaType())) {
            responseContext.setEntityStream(this.logInboundEntity(b, responseContext.getEntityStream(), MessageUtils.getCharset(responseContext.getMediaType())));
        }
        this.log(b);
    }
}
