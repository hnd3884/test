package sun.security.timestamp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.io.EOFException;
import sun.misc.IOUtils;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.util.List;
import java.util.Map;
import java.net.HttpURLConnection;
import java.net.URI;
import sun.security.util.Debug;

public class HttpTimestamper implements Timestamper
{
    private static final int CONNECT_TIMEOUT = 15000;
    private static final String TS_QUERY_MIME_TYPE = "application/timestamp-query";
    private static final String TS_REPLY_MIME_TYPE = "application/timestamp-reply";
    private static final Debug debug;
    private URI tsaURI;
    
    public HttpTimestamper(final URI tsaURI) {
        this.tsaURI = null;
        if (!tsaURI.getScheme().equalsIgnoreCase("http") && !tsaURI.getScheme().equalsIgnoreCase("https")) {
            throw new IllegalArgumentException("TSA must be an HTTP or HTTPS URI");
        }
        this.tsaURI = tsaURI;
    }
    
    @Override
    public TSResponse generateTimestamp(final TSRequest tsRequest) throws IOException {
        final HttpURLConnection httpURLConnection = (HttpURLConnection)this.tsaURI.toURL().openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setRequestProperty("Content-Type", "application/timestamp-query");
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(15000);
        if (HttpTimestamper.debug != null) {
            final Set<Map.Entry<String, List<String>>> entrySet = httpURLConnection.getRequestProperties().entrySet();
            HttpTimestamper.debug.println(httpURLConnection.getRequestMethod() + " " + this.tsaURI + " HTTP/1.1");
            final Iterator<Map.Entry<String, List<String>>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                HttpTimestamper.debug.println("  " + iterator.next());
            }
            HttpTimestamper.debug.println();
        }
        httpURLConnection.connect();
        DataOutputStream dataOutputStream = null;
        try {
            dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            final byte[] encode = tsRequest.encode();
            dataOutputStream.write(encode, 0, encode.length);
            dataOutputStream.flush();
            if (HttpTimestamper.debug != null) {
                HttpTimestamper.debug.println("sent timestamp query (length=" + encode.length + ")");
            }
        }
        finally {
            if (dataOutputStream != null) {
                dataOutputStream.close();
            }
        }
        InputStream inputStream = null;
        byte[] allBytes = null;
        try {
            inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            if (HttpTimestamper.debug != null) {
                HttpTimestamper.debug.println(httpURLConnection.getHeaderField(0));
                String headerField;
                for (int n = 1; (headerField = httpURLConnection.getHeaderField(n)) != null; ++n) {
                    final String headerFieldKey = httpURLConnection.getHeaderFieldKey(n);
                    HttpTimestamper.debug.println("  " + ((headerFieldKey == null) ? "" : (headerFieldKey + ": ")) + headerField);
                }
                HttpTimestamper.debug.println();
            }
            verifyMimeType(httpURLConnection.getContentType());
            final int contentLength = httpURLConnection.getContentLength();
            allBytes = IOUtils.readAllBytes(inputStream);
            if (contentLength != -1 && allBytes.length != contentLength) {
                throw new EOFException("Expected:" + contentLength + ", read:" + allBytes.length);
            }
            if (HttpTimestamper.debug != null) {
                HttpTimestamper.debug.println("received timestamp response (length=" + allBytes.length + ")");
            }
        }
        finally {
            if (inputStream != null) {
                ((BufferedInputStream)inputStream).close();
            }
        }
        return new TSResponse(allBytes);
    }
    
    private static void verifyMimeType(final String s) throws IOException {
        if (!"application/timestamp-reply".equalsIgnoreCase(s)) {
            throw new IOException("MIME Content-Type is not application/timestamp-reply");
        }
    }
    
    static {
        debug = Debug.getInstance("ts");
    }
}
