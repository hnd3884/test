package org.apache.tomcat.util.http;

import org.apache.juli.logging.LogFactory;
import java.util.Iterator;
import org.apache.tomcat.util.buf.StringUtils;
import java.io.UnsupportedEncodingException;
import org.apache.tomcat.util.buf.B2CConverter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import org.apache.tomcat.util.buf.ByteChunk;
import java.nio.charset.Charset;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.buf.MessageBytes;
import java.util.ArrayList;
import java.util.Map;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.log.UserDataHelper;
import org.apache.juli.logging.Log;

public final class Parameters
{
    private static final Log LOG;
    private static final UserDataHelper USERDATALOG;
    private static final UserDataHelper MAXPARAMCOUNTLOG;
    private static final StringManager SM;
    private final Map<String, ArrayList<String>> paramHashValues;
    private boolean didQueryParameters;
    private MessageBytes queryMB;
    private UDecoder urlDec;
    private final MessageBytes decodedQuery;
    private Charset charset;
    private Charset queryStringCharset;
    private int limit;
    private int parameterCount;
    private FailReason parseFailedReason;
    private final ByteChunk tmpName;
    private final ByteChunk tmpValue;
    private final ByteChunk origName;
    private final ByteChunk origValue;
    @Deprecated
    public static final String DEFAULT_ENCODING = "ISO-8859-1";
    private static final Charset DEFAULT_BODY_CHARSET;
    private static final Charset DEFAULT_URI_CHARSET;
    
    public Parameters() {
        this.paramHashValues = new LinkedHashMap<String, ArrayList<String>>();
        this.didQueryParameters = false;
        this.decodedQuery = MessageBytes.newInstance();
        this.charset = StandardCharsets.ISO_8859_1;
        this.queryStringCharset = StandardCharsets.UTF_8;
        this.limit = -1;
        this.parameterCount = 0;
        this.parseFailedReason = null;
        this.tmpName = new ByteChunk();
        this.tmpValue = new ByteChunk();
        this.origName = new ByteChunk();
        this.origValue = new ByteChunk();
    }
    
    public void setQuery(final MessageBytes queryMB) {
        this.queryMB = queryMB;
    }
    
    public void setLimit(final int limit) {
        this.limit = limit;
    }
    
    @Deprecated
    public String getEncoding() {
        return this.charset.name();
    }
    
    public Charset getCharset() {
        return this.charset;
    }
    
    @Deprecated
    public void setEncoding(final String s) {
        this.setCharset(this.getCharset(s, Parameters.DEFAULT_BODY_CHARSET));
    }
    
    public void setCharset(Charset charset) {
        if (charset == null) {
            charset = Parameters.DEFAULT_BODY_CHARSET;
        }
        this.charset = charset;
        if (Parameters.LOG.isDebugEnabled()) {
            Parameters.LOG.debug((Object)("Set encoding to " + charset.name()));
        }
    }
    
    @Deprecated
    public void setQueryStringEncoding(final String s) {
        this.setQueryStringCharset(this.getCharset(s, Parameters.DEFAULT_URI_CHARSET));
    }
    
    public void setQueryStringCharset(Charset queryStringCharset) {
        if (queryStringCharset == null) {
            queryStringCharset = Parameters.DEFAULT_URI_CHARSET;
        }
        this.queryStringCharset = queryStringCharset;
        if (Parameters.LOG.isDebugEnabled()) {
            Parameters.LOG.debug((Object)("Set query string encoding to " + queryStringCharset.name()));
        }
    }
    
    public boolean isParseFailed() {
        return this.parseFailedReason != null;
    }
    
    public FailReason getParseFailedReason() {
        return this.parseFailedReason;
    }
    
    public void setParseFailedReason(final FailReason failReason) {
        if (this.parseFailedReason == null) {
            this.parseFailedReason = failReason;
        }
    }
    
    public void recycle() {
        this.parameterCount = 0;
        this.paramHashValues.clear();
        this.didQueryParameters = false;
        this.charset = Parameters.DEFAULT_BODY_CHARSET;
        this.decodedQuery.recycle();
        this.parseFailedReason = null;
    }
    
    public String[] getParameterValues(final String name) {
        this.handleQueryParameters();
        final ArrayList<String> values = this.paramHashValues.get(name);
        if (values == null) {
            return null;
        }
        return values.toArray(new String[0]);
    }
    
    public Enumeration<String> getParameterNames() {
        this.handleQueryParameters();
        return Collections.enumeration(this.paramHashValues.keySet());
    }
    
    public String getParameter(final String name) {
        this.handleQueryParameters();
        final ArrayList<String> values = this.paramHashValues.get(name);
        if (values == null) {
            return null;
        }
        if (values.size() == 0) {
            return "";
        }
        return values.get(0);
    }
    
    public void handleQueryParameters() {
        if (this.didQueryParameters) {
            return;
        }
        this.didQueryParameters = true;
        if (this.queryMB == null || this.queryMB.isNull()) {
            return;
        }
        if (Parameters.LOG.isDebugEnabled()) {
            Parameters.LOG.debug((Object)("Decoding query " + this.decodedQuery + " " + this.queryStringCharset.name()));
        }
        try {
            this.decodedQuery.duplicate(this.queryMB);
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
        this.processParameters(this.decodedQuery, this.queryStringCharset);
    }
    
    public void addParameter(final String key, final String value) throws IllegalStateException {
        if (key == null) {
            return;
        }
        ++this.parameterCount;
        if (this.limit > -1 && this.parameterCount > this.limit) {
            this.setParseFailedReason(FailReason.TOO_MANY_PARAMETERS);
            throw new IllegalStateException(Parameters.SM.getString("parameters.maxCountFail", new Object[] { this.limit }));
        }
        ArrayList<String> values = this.paramHashValues.get(key);
        if (values == null) {
            values = new ArrayList<String>(1);
            this.paramHashValues.put(key, values);
        }
        values.add(value);
    }
    
    public void setURLDecoder(final UDecoder u) {
        this.urlDec = u;
    }
    
    public void processParameters(final byte[] bytes, final int start, final int len) {
        this.processParameters(bytes, start, len, this.charset);
    }
    
    private void processParameters(final byte[] bytes, final int start, final int len, final Charset charset) {
        if (Parameters.LOG.isDebugEnabled()) {
            Parameters.LOG.debug((Object)Parameters.SM.getString("parameters.bytes", new Object[] { new String(bytes, start, len, Parameters.DEFAULT_BODY_CHARSET) }));
        }
        int decodeFailCount = 0;
        int pos = start;
        final int end = start + len;
        while (pos < end) {
            final int nameStart = pos;
            int nameEnd = -1;
            int valueStart = -1;
            int valueEnd = -1;
            boolean parsingName = true;
            boolean decodeName = false;
            boolean decodeValue = false;
            boolean parameterComplete = false;
            do {
                switch (bytes[pos]) {
                    case 61: {
                        if (parsingName) {
                            nameEnd = pos;
                            parsingName = false;
                            valueStart = ++pos;
                            continue;
                        }
                        ++pos;
                        continue;
                    }
                    case 38: {
                        if (parsingName) {
                            nameEnd = pos;
                        }
                        else {
                            valueEnd = pos;
                        }
                        parameterComplete = true;
                        ++pos;
                        continue;
                    }
                    case 37:
                    case 43: {
                        if (parsingName) {
                            decodeName = true;
                        }
                        else {
                            decodeValue = true;
                        }
                        ++pos;
                        continue;
                    }
                    default: {
                        ++pos;
                        continue;
                    }
                }
            } while (!parameterComplete && pos < end);
            if (pos == end) {
                if (nameEnd == -1) {
                    nameEnd = pos;
                }
                else if (valueStart > -1 && valueEnd == -1) {
                    valueEnd = pos;
                }
            }
            if (Parameters.LOG.isDebugEnabled() && valueStart == -1) {
                Parameters.LOG.debug((Object)Parameters.SM.getString("parameters.noequal", new Object[] { nameStart, nameEnd, new String(bytes, nameStart, nameEnd - nameStart, Parameters.DEFAULT_BODY_CHARSET) }));
            }
            if (nameEnd <= nameStart) {
                if (valueStart == -1) {
                    if (!Parameters.LOG.isDebugEnabled()) {
                        continue;
                    }
                    Parameters.LOG.debug((Object)Parameters.SM.getString("parameters.emptyChunk"));
                }
                else {
                    final UserDataHelper.Mode logMode = Parameters.USERDATALOG.getNextMode();
                    if (logMode != null) {
                        String extract;
                        if (valueEnd > nameStart) {
                            extract = new String(bytes, nameStart, valueEnd - nameStart, Parameters.DEFAULT_BODY_CHARSET);
                        }
                        else {
                            extract = "";
                        }
                        String message = Parameters.SM.getString("parameters.invalidChunk", new Object[] { nameStart, valueEnd, extract });
                        switch (logMode) {
                            case INFO_THEN_DEBUG: {
                                message += Parameters.SM.getString("parameters.fallToDebug");
                            }
                            case INFO: {
                                Parameters.LOG.info((Object)message);
                                break;
                            }
                            case DEBUG: {
                                Parameters.LOG.debug((Object)message);
                                break;
                            }
                        }
                    }
                    this.setParseFailedReason(FailReason.NO_NAME);
                }
            }
            else {
                this.tmpName.setBytes(bytes, nameStart, nameEnd - nameStart);
                if (valueStart >= 0) {
                    this.tmpValue.setBytes(bytes, valueStart, valueEnd - valueStart);
                }
                else {
                    this.tmpValue.setBytes(bytes, 0, 0);
                }
                if (Parameters.LOG.isDebugEnabled()) {
                    try {
                        this.origName.append(bytes, nameStart, nameEnd - nameStart);
                        if (valueStart >= 0) {
                            this.origValue.append(bytes, valueStart, valueEnd - valueStart);
                        }
                        else {
                            this.origValue.append(bytes, 0, 0);
                        }
                    }
                    catch (final IOException ioe) {
                        Parameters.LOG.error((Object)Parameters.SM.getString("parameters.copyFail"), (Throwable)ioe);
                    }
                }
                try {
                    if (decodeName) {
                        this.urlDecode(this.tmpName);
                    }
                    this.tmpName.setCharset(charset);
                    final String name = this.tmpName.toString();
                    String value;
                    if (valueStart >= 0) {
                        if (decodeValue) {
                            this.urlDecode(this.tmpValue);
                        }
                        this.tmpValue.setCharset(charset);
                        value = this.tmpValue.toString();
                    }
                    else {
                        value = "";
                    }
                    try {
                        this.addParameter(name, value);
                    }
                    catch (final IllegalStateException ise) {
                        final UserDataHelper.Mode logMode2 = Parameters.MAXPARAMCOUNTLOG.getNextMode();
                        if (logMode2 != null) {
                            String message2 = ise.getMessage();
                            switch (logMode2) {
                                case INFO_THEN_DEBUG: {
                                    message2 += Parameters.SM.getString("parameters.maxCountFail.fallToDebug");
                                }
                                case INFO: {
                                    Parameters.LOG.info((Object)message2);
                                    break;
                                }
                                case DEBUG: {
                                    Parameters.LOG.debug((Object)message2);
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
                catch (final IOException e) {
                    this.setParseFailedReason(FailReason.URL_DECODING);
                    if (++decodeFailCount == 1 || Parameters.LOG.isDebugEnabled()) {
                        if (Parameters.LOG.isDebugEnabled()) {
                            Parameters.LOG.debug((Object)Parameters.SM.getString("parameters.decodeFail.debug", new Object[] { "data", "***" }), (Throwable)e);
                        }
                        else if (Parameters.LOG.isInfoEnabled()) {
                            final UserDataHelper.Mode logMode3 = Parameters.USERDATALOG.getNextMode();
                            if (logMode3 != null) {
                                String message = Parameters.SM.getString("parameters.decodeFail.info", new Object[] { this.tmpName.toString(), "***" });
                                switch (logMode3) {
                                    case DEBUG: {
                                        Parameters.LOG.debug((Object)message);
                                        break;
                                    }
                                    case INFO_THEN_DEBUG: {
                                        message += Parameters.SM.getString("parameters.fallToDebug");
                                    }
                                    case INFO: {
                                        Parameters.LOG.info((Object)message);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                this.tmpName.recycle();
                this.tmpValue.recycle();
                if (!Parameters.LOG.isDebugEnabled()) {
                    continue;
                }
                this.origName.recycle();
                this.origValue.recycle();
            }
        }
        if (decodeFailCount > 1 && !Parameters.LOG.isDebugEnabled()) {
            final UserDataHelper.Mode logMode4 = Parameters.USERDATALOG.getNextMode();
            if (logMode4 != null) {
                String message3 = Parameters.SM.getString("parameters.multipleDecodingFail", new Object[] { decodeFailCount });
                switch (logMode4) {
                    case INFO_THEN_DEBUG: {
                        message3 += Parameters.SM.getString("parameters.fallToDebug");
                    }
                    case INFO: {
                        Parameters.LOG.info((Object)message3);
                        break;
                    }
                    case DEBUG: {
                        Parameters.LOG.debug((Object)message3);
                        break;
                    }
                }
            }
        }
    }
    
    private void urlDecode(final ByteChunk bc) throws IOException {
        if (this.urlDec == null) {
            this.urlDec = new UDecoder();
        }
        this.urlDec.convert(bc, true);
    }
    
    @Deprecated
    public void processParameters(final MessageBytes data, final String encoding) {
        this.processParameters(data, this.getCharset(encoding, Parameters.DEFAULT_BODY_CHARSET));
    }
    
    public void processParameters(final MessageBytes data, final Charset charset) {
        if (data == null || data.isNull() || data.getLength() <= 0) {
            return;
        }
        if (data.getType() != 2) {
            data.toBytes();
        }
        final ByteChunk bc = data.getByteChunk();
        this.processParameters(bc.getBytes(), bc.getOffset(), bc.getLength(), charset);
    }
    
    private Charset getCharset(final String encoding, final Charset defaultCharset) {
        if (encoding == null) {
            return defaultCharset;
        }
        try {
            return B2CConverter.getCharset(encoding);
        }
        catch (final UnsupportedEncodingException e) {
            return defaultCharset;
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<String, ArrayList<String>> e : this.paramHashValues.entrySet()) {
            sb.append(e.getKey()).append('=');
            StringUtils.join((Iterable)e.getValue(), ',', sb);
            sb.append('\n');
        }
        return sb.toString();
    }
    
    static {
        LOG = LogFactory.getLog((Class)Parameters.class);
        USERDATALOG = new UserDataHelper(Parameters.LOG);
        MAXPARAMCOUNTLOG = new UserDataHelper(Parameters.LOG);
        SM = StringManager.getManager("org.apache.tomcat.util.http");
        DEFAULT_BODY_CHARSET = StandardCharsets.ISO_8859_1;
        DEFAULT_URI_CHARSET = StandardCharsets.UTF_8;
    }
    
    public enum FailReason
    {
        CLIENT_DISCONNECT, 
        MULTIPART_CONFIG_INVALID, 
        INVALID_CONTENT_TYPE, 
        IO_ERROR, 
        NO_NAME, 
        POST_TOO_LARGE, 
        REQUEST_BODY_INCOMPLETE, 
        TOO_MANY_PARAMETERS, 
        UNKNOWN, 
        URL_DECODING;
    }
}
