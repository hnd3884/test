package com.sun.net.httpserver;

import java.io.IOException;
import jdk.Exported;

@Exported
public interface HttpHandler
{
    void handle(final HttpExchange p0) throws IOException;
}
