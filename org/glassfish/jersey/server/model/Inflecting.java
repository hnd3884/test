package org.glassfish.jersey.server.model;

import javax.ws.rs.core.Request;
import org.glassfish.jersey.process.Inflector;

public class Inflecting<T>
{
    public Inflector<Request, T> getInflector() {
        return null;
    }
}
