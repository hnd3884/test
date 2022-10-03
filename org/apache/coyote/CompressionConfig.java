package org.apache.coyote;

import org.apache.juli.logging.LogFactory;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Set;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.AcceptEncoding;
import java.io.StringReader;
import org.apache.tomcat.util.http.ResponseUtil;
import java.io.IOException;
import java.util.Collection;
import org.apache.tomcat.util.http.parser.TokenList;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

public class CompressionConfig
{
    private static final Log log;
    private static final StringManager sm;
    private int compressionLevel;
    private Pattern noCompressionUserAgents;
    private String compressibleMimeType;
    private String[] compressibleMimeTypes;
    private int compressionMinSize;
    private boolean noCompressionStrongETag;
    
    public CompressionConfig() {
        this.compressionLevel = 0;
        this.noCompressionUserAgents = null;
        this.compressibleMimeType = "text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json,application/xml";
        this.compressibleMimeTypes = null;
        this.compressionMinSize = 2048;
        this.noCompressionStrongETag = true;
    }
    
    public void setCompression(final String compression) {
        if (compression.equals("on")) {
            this.compressionLevel = 1;
        }
        else if (compression.equals("force")) {
            this.compressionLevel = 2;
        }
        else if (compression.equals("off")) {
            this.compressionLevel = 0;
        }
        else {
            try {
                this.setCompressionMinSize(Integer.parseInt(compression));
                this.compressionLevel = 1;
            }
            catch (final Exception e) {
                this.compressionLevel = 0;
            }
        }
    }
    
    public String getCompression() {
        switch (this.compressionLevel) {
            case 0: {
                return "off";
            }
            case 1: {
                return "on";
            }
            case 2: {
                return "force";
            }
            default: {
                return "off";
            }
        }
    }
    
    public int getCompressionLevel() {
        return this.compressionLevel;
    }
    
    public String getNoCompressionUserAgents() {
        if (this.noCompressionUserAgents == null) {
            return null;
        }
        return this.noCompressionUserAgents.toString();
    }
    
    public Pattern getNoCompressionUserAgentsPattern() {
        return this.noCompressionUserAgents;
    }
    
    public void setNoCompressionUserAgents(final String noCompressionUserAgents) {
        if (noCompressionUserAgents == null || noCompressionUserAgents.length() == 0) {
            this.noCompressionUserAgents = null;
        }
        else {
            this.noCompressionUserAgents = Pattern.compile(noCompressionUserAgents);
        }
    }
    
    public String getCompressibleMimeType() {
        return this.compressibleMimeType;
    }
    
    public void setCompressibleMimeType(final String valueS) {
        this.compressibleMimeType = valueS;
        this.compressibleMimeTypes = null;
    }
    
    public String[] getCompressibleMimeTypes() {
        String[] result = this.compressibleMimeTypes;
        if (result != null) {
            return result;
        }
        final List<String> values = new ArrayList<String>();
        final StringTokenizer tokens = new StringTokenizer(this.compressibleMimeType, ",");
        while (tokens.hasMoreTokens()) {
            final String token = tokens.nextToken().trim();
            if (token.length() > 0) {
                values.add(token);
            }
        }
        result = values.toArray(new String[0]);
        return this.compressibleMimeTypes = result;
    }
    
    public int getCompressionMinSize() {
        return this.compressionMinSize;
    }
    
    public void setCompressionMinSize(final int compressionMinSize) {
        this.compressionMinSize = compressionMinSize;
    }
    
    @Deprecated
    public boolean getNoCompressionStrongETag() {
        return this.noCompressionStrongETag;
    }
    
    @Deprecated
    public void setNoCompressionStrongETag(final boolean noCompressionStrongETag) {
        this.noCompressionStrongETag = noCompressionStrongETag;
    }
    
    public boolean useCompression(final Request request, final Response response) {
        if (this.compressionLevel == 0) {
            return false;
        }
        final MimeHeaders responseHeaders = response.getMimeHeaders();
        final MessageBytes contentEncodingMB = responseHeaders.getValue("Content-Encoding");
        if (contentEncodingMB != null) {
            final Set<String> tokens = new HashSet<String>();
            try {
                TokenList.parseTokenList(responseHeaders.values("Content-Encoding"), tokens);
            }
            catch (final IOException e) {
                CompressionConfig.log.warn((Object)CompressionConfig.sm.getString("compressionConfig.ContentEncodingParseFail"), (Throwable)e);
                return false;
            }
            if (tokens.contains("gzip") || tokens.contains("br")) {
                return false;
            }
        }
        if (this.compressionLevel != 2) {
            final long contentLength = response.getContentLengthLong();
            if (contentLength != -1L && contentLength < this.compressionMinSize) {
                return false;
            }
            final String[] compressibleMimeTypes = this.getCompressibleMimeTypes();
            if (compressibleMimeTypes != null && !startsWithStringArray(compressibleMimeTypes, response.getContentType())) {
                return false;
            }
        }
        if (this.noCompressionStrongETag) {
            final String eTag = responseHeaders.getHeader("ETag");
            if (eTag != null && !eTag.trim().startsWith("W/")) {
                return false;
            }
        }
        ResponseUtil.addVaryFieldName(responseHeaders, "accept-encoding");
        Enumeration<String> headerValues;
        boolean foundGzip;
        List<AcceptEncoding> acceptEncodings;
        Iterator i$;
        AcceptEncoding acceptEncoding;
        for (headerValues = request.getMimeHeaders().values("accept-encoding"), foundGzip = false; !foundGzip && headerValues.hasMoreElements(); foundGzip = true) {
            acceptEncodings = null;
            try {
                acceptEncodings = AcceptEncoding.parse(new StringReader(headerValues.nextElement()));
            }
            catch (final IOException ioe) {
                return false;
            }
            i$ = acceptEncodings.iterator();
            while (i$.hasNext()) {
                acceptEncoding = i$.next();
                if ("gzip".equalsIgnoreCase(acceptEncoding.getEncoding())) {
                    break;
                }
            }
        }
        if (!foundGzip) {
            return false;
        }
        if (this.compressionLevel != 2) {
            final Pattern noCompressionUserAgents = this.noCompressionUserAgents;
            if (noCompressionUserAgents != null) {
                final MessageBytes userAgentValueMB = request.getMimeHeaders().getValue("user-agent");
                if (userAgentValueMB != null) {
                    final String userAgentValue = userAgentValueMB.toString();
                    if (noCompressionUserAgents.matcher(userAgentValue).matches()) {
                        return false;
                    }
                }
            }
        }
        response.setContentLength(-1L);
        responseHeaders.setValue("Content-Encoding").setString("gzip");
        return true;
    }
    
    private static boolean startsWithStringArray(final String[] sArray, final String value) {
        if (value == null) {
            return false;
        }
        for (final String s : sArray) {
            if (value.startsWith(s)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        log = LogFactory.getLog((Class)CompressionConfig.class);
        sm = StringManager.getManager((Class)CompressionConfig.class);
    }
}
