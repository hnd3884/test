package org.glassfish.jersey.server.model;

import javax.ws.rs.core.MediaType;
import java.util.List;

public interface Producing
{
    List<MediaType> getProducedTypes();
}
