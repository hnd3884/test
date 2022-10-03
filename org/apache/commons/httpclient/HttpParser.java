package org.apache.commons.httpclient;

import org.apache.commons.logging.LogFactory;
import java.util.ArrayList;
import org.apache.commons.httpclient.util.EncodingUtil;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.apache.commons.logging.Log;

public class HttpParser
{
    private static final Log LOG;
    
    private HttpParser() {
    }
    
    public static byte[] readRawLine(final InputStream inputStream) throws IOException {
        HttpParser.LOG.trace((Object)"enter HttpParser.readRawLine()");
        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int ch;
        while ((ch = inputStream.read()) >= 0) {
            buf.write(ch);
            if (ch == 10) {
                break;
            }
        }
        if (buf.size() == 0) {
            return null;
        }
        return buf.toByteArray();
    }
    
    public static String readLine(final InputStream inputStream, final String charset) throws IOException {
        HttpParser.LOG.trace((Object)"enter HttpParser.readLine(InputStream, String)");
        final byte[] rawdata = readRawLine(inputStream);
        if (rawdata == null) {
            return null;
        }
        final int len = rawdata.length;
        int offset = 0;
        if (len > 0 && rawdata[len - 1] == 10) {
            ++offset;
            if (len > 1 && rawdata[len - 2] == 13) {
                ++offset;
            }
        }
        final String result = EncodingUtil.getString(rawdata, 0, len - offset, charset);
        if (Wire.HEADER_WIRE.enabled()) {
            String logoutput = result;
            if (offset == 2) {
                logoutput = result + "\r\n";
            }
            else if (offset == 1) {
                logoutput = result + "\n";
            }
            Wire.HEADER_WIRE.input(logoutput);
        }
        return result;
    }
    
    public static String readLine(final InputStream inputStream) throws IOException {
        HttpParser.LOG.trace((Object)"enter HttpParser.readLine(InputStream)");
        return readLine(inputStream, "US-ASCII");
    }
    
    public static Header[] parseHeaders(final InputStream is, final String charset) throws IOException, HttpException {
        HttpParser.LOG.trace((Object)"enter HeaderParser.parseHeaders(InputStream, String)");
        final ArrayList headers = new ArrayList();
        String name = null;
        StringBuffer value = null;
        while (true) {
            final String line = readLine(is, charset);
            if (line == null || line.trim().length() < 1) {
                if (name != null) {
                    headers.add(new Header(name, value.toString()));
                }
                return headers.toArray(new Header[headers.size()]);
            }
            if (line.charAt(0) == ' ' || line.charAt(0) == '\t') {
                if (value == null) {
                    continue;
                }
                value.append(' ');
                value.append(line.trim());
            }
            else {
                if (name != null) {
                    headers.add(new Header(name, value.toString()));
                }
                final int colon = line.indexOf(":");
                if (colon < 0) {
                    throw new ProtocolException("Unable to parse header: " + line);
                }
                name = line.substring(0, colon).trim();
                value = new StringBuffer(line.substring(colon + 1).trim());
            }
        }
    }
    
    public static Header[] parseHeaders(final InputStream is) throws IOException, HttpException {
        HttpParser.LOG.trace((Object)"enter HeaderParser.parseHeaders(InputStream, String)");
        return parseHeaders(is, "US-ASCII");
    }
    
    static {
        LOG = LogFactory.getLog(HttpParser.class);
    }
}
