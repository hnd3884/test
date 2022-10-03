package org.glassfish.jersey.logging;

import java.io.OutputStream;
import javax.ws.rs.container.ContainerResponseContext;
import java.io.IOException;
import org.glassfish.jersey.message.MessageUtils;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Priority;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ContainerRequestFilter;

@ConstrainedTo(RuntimeType.SERVER)
@PreMatching
@Priority(Integer.MIN_VALUE)
final class ServerLoggingFilter extends LoggingInterceptor implements ContainerRequestFilter, ContainerResponseFilter
{
    public ServerLoggingFilter(final Logger logger, final Level level, final LoggingFeature.Verbosity verbosity, final int maxEntitySize) {
        super(logger, level, verbosity, maxEntitySize);
    }
    
    public void filter(final ContainerRequestContext context) throws IOException {
        if (!this.logger.isLoggable(this.level)) {
            return;
        }
        final long id = this._id.incrementAndGet();
        context.setProperty(ServerLoggingFilter.LOGGING_ID_PROPERTY, (Object)id);
        final StringBuilder b = new StringBuilder();
        this.printRequestLine(b, "Server has received a request", id, context.getMethod(), context.getUriInfo().getRequestUri());
        this.printPrefixedHeaders(b, id, "> ", (MultivaluedMap<String, String>)context.getHeaders());
        if (context.hasEntity() && LoggingInterceptor.printEntity(this.verbosity, context.getMediaType())) {
            context.setEntityStream(this.logInboundEntity(b, context.getEntityStream(), MessageUtils.getCharset(context.getMediaType())));
        }
        this.log(b);
    }
    
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {
        if (!this.logger.isLoggable(this.level)) {
            return;
        }
        final Object requestId = requestContext.getProperty(ServerLoggingFilter.LOGGING_ID_PROPERTY);
        final long id = (long)((requestId != null) ? requestId : this._id.incrementAndGet());
        final StringBuilder b = new StringBuilder();
        this.printResponseLine(b, "Server responded with a response", id, responseContext.getStatus());
        this.printPrefixedHeaders(b, id, "< ", (MultivaluedMap<String, String>)responseContext.getStringHeaders());
        if (responseContext.hasEntity() && LoggingInterceptor.printEntity(this.verbosity, responseContext.getMediaType())) {
            final OutputStream stream = new LoggingStream(b, responseContext.getEntityStream());
            responseContext.setEntityStream(stream);
            requestContext.setProperty(ServerLoggingFilter.ENTITY_LOGGER_PROPERTY, (Object)stream);
        }
        else {
            this.log(b);
        }
    }
}
