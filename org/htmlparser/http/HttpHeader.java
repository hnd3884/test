package org.htmlparser.http;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.net.HttpURLConnection;

public class HttpHeader
{
    private HttpHeader() {
    }
    
    public static String getRequestHeader(final HttpURLConnection connection) {
        final StringBuffer buffer = new StringBuffer(1024);
        buffer.append(connection.getRequestMethod());
        buffer.append(" ");
        buffer.append(connection.getURL());
        buffer.append(" HTTP/1.1\n");
        final Map map = connection.getRequestProperties();
        final Iterator iter = map.keySet().iterator();
        while (iter.hasNext()) {
            final String key = iter.next();
            final List items = map.get(key);
            buffer.append(key);
            buffer.append(": ");
            for (int i = 0; i < items.size(); ++i) {
                if (0 != i) {
                    buffer.append(", ");
                }
                buffer.append(items.get(i));
            }
            buffer.append("\n");
        }
        return buffer.toString();
    }
    
    public static String getResponseHeader(final HttpURLConnection conn) {
        final StringBuffer buffer = new StringBuffer(1024);
        try {
            final int code = conn.getResponseCode();
            if (-1 != code) {
                final String message = conn.getResponseMessage();
                String value;
                for (int i = 0; null != (value = conn.getHeaderField(i)); ++i) {
                    final String key = conn.getHeaderFieldKey(i);
                    if (null == key && 0 == i) {
                        buffer.append("HTTP/1.1 ");
                        buffer.append(code);
                        buffer.append(" ");
                        buffer.append(message);
                        buffer.append("\n");
                    }
                    else {
                        if (null != key) {
                            buffer.append(key);
                            buffer.append(": ");
                        }
                        buffer.append(value);
                        buffer.append("\n");
                    }
                }
            }
        }
        catch (final IOException ioe) {
            buffer.append(ioe.toString());
        }
        return buffer.toString();
    }
}
