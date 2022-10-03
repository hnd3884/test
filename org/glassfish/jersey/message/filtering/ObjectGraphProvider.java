package org.glassfish.jersey.message.filtering;

import org.glassfish.jersey.message.filtering.spi.ObjectGraph;
import org.glassfish.jersey.message.filtering.spi.AbstractObjectProvider;

final class ObjectGraphProvider extends AbstractObjectProvider<ObjectGraph>
{
    @Override
    public ObjectGraph transform(final ObjectGraph graph) {
        return graph;
    }
}
