package org.glassfish.jersey.server.internal.inject;

import org.glassfish.jersey.server.model.Parameter;

public interface MultivaluedParameterExtractorProvider
{
    MultivaluedParameterExtractor<?> get(final Parameter p0);
}
