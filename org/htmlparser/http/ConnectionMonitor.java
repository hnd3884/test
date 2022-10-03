package org.htmlparser.http;

import org.htmlparser.util.ParserException;
import java.net.HttpURLConnection;

public interface ConnectionMonitor
{
    void preConnect(final HttpURLConnection p0) throws ParserException;
    
    void postConnect(final HttpURLConnection p0) throws ParserException;
}
