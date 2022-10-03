package com.sun.org.apache.xml.internal.serializer;

import java.util.Iterator;
import java.util.Enumeration;
import com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException;
import java.util.StringTokenizer;
import java.util.Properties;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.Charset;
import java.io.UnsupportedEncodingException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.OutputStream;

public final class Encodings
{
    private static final int m_defaultLastPrintable = 127;
    private static final String ENCODINGS_FILE = "com/sun/org/apache/xml/internal/serializer/Encodings.properties";
    private static final String ENCODINGS_PROP = "com.sun.org.apache.xalan.internal.serialize.encodings";
    static final String DEFAULT_MIME_ENCODING = "UTF-8";
    private static final EncodingInfos _encodingInfos;
    
    static Writer getWriter(final OutputStream output, final String encoding) throws UnsupportedEncodingException {
        final EncodingInfo ei = Encodings._encodingInfos.findEncoding(toUpperCaseFast(encoding));
        if (ei != null) {
            try {
                return new BufferedWriter(new OutputStreamWriter(output, ei.javaName));
            }
            catch (final UnsupportedEncodingException ex) {}
        }
        return new BufferedWriter(new OutputStreamWriter(output, encoding));
    }
    
    public static int getLastPrintable() {
        return 127;
    }
    
    static EncodingInfo getEncodingInfo(final String encoding) {
        final String normalizedEncoding = toUpperCaseFast(encoding);
        EncodingInfo ei = Encodings._encodingInfos.findEncoding(normalizedEncoding);
        if (ei == null) {
            try {
                final Charset c = Charset.forName(encoding);
                final String name = c.name();
                ei = new EncodingInfo(name, name);
                Encodings._encodingInfos.putEncoding(normalizedEncoding, ei);
            }
            catch (final IllegalCharsetNameException | UnsupportedCharsetException x) {
                ei = new EncodingInfo(null, null);
            }
        }
        return ei;
    }
    
    private static String toUpperCaseFast(final String s) {
        boolean different = false;
        final int mx = s.length();
        final char[] chars = new char[mx];
        for (int i = 0; i < mx; ++i) {
            char ch = s.charAt(i);
            if ('a' <= ch && ch <= 'z') {
                ch -= 32;
                different = true;
            }
            chars[i] = ch;
        }
        String upper;
        if (different) {
            upper = String.valueOf(chars);
        }
        else {
            upper = s;
        }
        return upper;
    }
    
    static String getMimeEncoding(String encoding) {
        if (null == encoding) {
            try {
                encoding = SecuritySupport.getSystemProperty("file.encoding", "UTF8");
                if (null != encoding) {
                    final String jencoding = (encoding.equalsIgnoreCase("Cp1252") || encoding.equalsIgnoreCase("ISO8859_1") || encoding.equalsIgnoreCase("8859_1") || encoding.equalsIgnoreCase("UTF8")) ? "UTF-8" : convertJava2MimeEncoding(encoding);
                    encoding = ((null != jencoding) ? jencoding : "UTF-8");
                }
                else {
                    encoding = "UTF-8";
                }
            }
            catch (final SecurityException se) {
                encoding = "UTF-8";
            }
        }
        else {
            encoding = convertJava2MimeEncoding(encoding);
        }
        return encoding;
    }
    
    private static String convertJava2MimeEncoding(final String encoding) {
        final EncodingInfo enc = Encodings._encodingInfos.getEncodingFromJavaKey(toUpperCaseFast(encoding));
        if (null != enc) {
            return enc.name;
        }
        return encoding;
    }
    
    public static String convertMime2JavaEncoding(final String encoding) {
        final EncodingInfo info = Encodings._encodingInfos.findEncoding(toUpperCaseFast(encoding));
        return (info != null) ? info.javaName : encoding;
    }
    
    static boolean isHighUTF16Surrogate(final char ch) {
        return '\ud800' <= ch && ch <= '\udbff';
    }
    
    static boolean isLowUTF16Surrogate(final char ch) {
        return '\udc00' <= ch && ch <= '\udfff';
    }
    
    static int toCodePoint(final char highSurrogate, final char lowSurrogate) {
        final int codePoint = (highSurrogate - '\ud800' << 10) + (lowSurrogate - '\udc00') + 65536;
        return codePoint;
    }
    
    static int toCodePoint(final char ch) {
        final int codePoint = ch;
        return codePoint;
    }
    
    static {
        _encodingInfos = new EncodingInfos();
    }
    
    private static final class EncodingInfos
    {
        private final Map<String, EncodingInfo> _encodingTableKeyJava;
        private final Map<String, EncodingInfo> _encodingTableKeyMime;
        private final Map<String, EncodingInfo> _encodingDynamicTable;
        
        private EncodingInfos() {
            this._encodingTableKeyJava = new HashMap<String, EncodingInfo>();
            this._encodingTableKeyMime = new HashMap<String, EncodingInfo>();
            this._encodingDynamicTable = Collections.synchronizedMap(new HashMap<String, EncodingInfo>());
            this.loadEncodingInfo();
        }
        
        private InputStream openEncodingsFileStream() throws MalformedURLException, IOException {
            String urlString = null;
            InputStream is = null;
            try {
                urlString = SecuritySupport.getSystemProperty("com.sun.org.apache.xalan.internal.serialize.encodings", "");
            }
            catch (final SecurityException ex) {}
            if (urlString != null && urlString.length() > 0) {
                final URL url = new URL(urlString);
                is = url.openStream();
            }
            if (is == null) {
                is = SecuritySupport.getResourceAsStream("com/sun/org/apache/xml/internal/serializer/Encodings.properties");
            }
            return is;
        }
        
        private Properties loadProperties() throws MalformedURLException, IOException {
            final Properties props = new Properties();
            try (final InputStream is = this.openEncodingsFileStream()) {
                if (is != null) {
                    props.load(is);
                }
            }
            return props;
        }
        
        private String[] parseMimeTypes(final String val) {
            final int pos = val.indexOf(32);
            if (pos < 0) {
                return new String[] { val };
            }
            final StringTokenizer st = new StringTokenizer(val.substring(0, pos), ",");
            final String[] values = new String[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens()) {
                values[i] = st.nextToken();
                ++i;
            }
            return values;
        }
        
        private String findCharsetNameFor(final String name) {
            try {
                return Charset.forName(name).name();
            }
            catch (final Exception x) {
                return null;
            }
        }
        
        private String findCharsetNameFor(final String javaName, final String[] mimes) {
            String cs = this.findCharsetNameFor(javaName);
            if (cs != null) {
                return javaName;
            }
            for (final String m : mimes) {
                cs = this.findCharsetNameFor(m);
                if (cs != null) {
                    break;
                }
            }
            return cs;
        }
        
        private void loadEncodingInfo() {
            try {
                final Properties props = this.loadProperties();
                final Enumeration keys = props.keys();
                final Map<String, EncodingInfo> canonicals = new HashMap<String, EncodingInfo>();
                while (keys.hasMoreElements()) {
                    final String javaName = keys.nextElement();
                    final String[] mimes = this.parseMimeTypes(props.getProperty(javaName));
                    final String charsetName = this.findCharsetNameFor(javaName, mimes);
                    if (charsetName != null) {
                        final String kj = toUpperCaseFast(javaName);
                        final String kc = toUpperCaseFast(charsetName);
                        for (int i = 0; i < mimes.length; ++i) {
                            final String mimeName = mimes[i];
                            final String km = toUpperCaseFast(mimeName);
                            final EncodingInfo info = new EncodingInfo(mimeName, charsetName);
                            this._encodingTableKeyMime.put(km, info);
                            if (!canonicals.containsKey(kc)) {
                                canonicals.put(kc, info);
                                this._encodingTableKeyJava.put(kc, info);
                            }
                            this._encodingTableKeyJava.put(kj, info);
                        }
                    }
                }
                for (final Map.Entry<String, EncodingInfo> e : this._encodingTableKeyJava.entrySet()) {
                    e.setValue(canonicals.get(toUpperCaseFast(e.getValue().javaName)));
                }
            }
            catch (final MalformedURLException mue) {
                throw new WrappedRuntimeException(mue);
            }
            catch (final IOException ioe) {
                throw new WrappedRuntimeException(ioe);
            }
        }
        
        EncodingInfo findEncoding(final String normalizedEncoding) {
            EncodingInfo info = this._encodingTableKeyJava.get(normalizedEncoding);
            if (info == null) {
                info = this._encodingTableKeyMime.get(normalizedEncoding);
            }
            if (info == null) {
                info = this._encodingDynamicTable.get(normalizedEncoding);
            }
            return info;
        }
        
        EncodingInfo getEncodingFromMimeKey(final String normalizedMimeName) {
            return this._encodingTableKeyMime.get(normalizedMimeName);
        }
        
        EncodingInfo getEncodingFromJavaKey(final String normalizedJavaName) {
            return this._encodingTableKeyJava.get(normalizedJavaName);
        }
        
        void putEncoding(final String key, final EncodingInfo info) {
            this._encodingDynamicTable.put(key, info);
        }
    }
}
