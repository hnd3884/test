package javax.xml.ws.spi.http;

import java.io.IOException;

public abstract class HttpHandler
{
    public abstract void handle(final HttpExchange p0) throws IOException;
}
