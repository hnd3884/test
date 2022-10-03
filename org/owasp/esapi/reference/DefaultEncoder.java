package org.owasp.esapi.reference;

import java.io.IOException;
import org.owasp.esapi.codecs.Base64;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;
import org.owasp.esapi.errors.EncodingException;
import java.net.URLEncoder;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.codecs.Codec;
import java.util.Iterator;
import org.owasp.esapi.ESAPI;
import java.util.ArrayList;
import org.owasp.esapi.Logger;
import org.owasp.esapi.codecs.CSSCodec;
import org.owasp.esapi.codecs.VBScriptCodec;
import org.owasp.esapi.codecs.JavaScriptCodec;
import org.owasp.esapi.codecs.PercentCodec;
import org.owasp.esapi.codecs.XMLEntityCodec;
import org.owasp.esapi.codecs.HTMLEntityCodec;
import java.util.List;
import org.owasp.esapi.Encoder;

public class DefaultEncoder implements Encoder
{
    private static volatile Encoder singletonInstance;
    private List codecs;
    private HTMLEntityCodec htmlCodec;
    private XMLEntityCodec xmlCodec;
    private PercentCodec percentCodec;
    private JavaScriptCodec javaScriptCodec;
    private VBScriptCodec vbScriptCodec;
    private CSSCodec cssCodec;
    private final Logger logger;
    private static final char[] IMMUNE_HTML;
    private static final char[] IMMUNE_HTMLATTR;
    private static final char[] IMMUNE_CSS;
    private static final char[] IMMUNE_JAVASCRIPT;
    private static final char[] IMMUNE_VBSCRIPT;
    private static final char[] IMMUNE_XML;
    private static final char[] IMMUNE_SQL;
    private static final char[] IMMUNE_OS;
    private static final char[] IMMUNE_XMLATTR;
    private static final char[] IMMUNE_XPATH;
    
    public static Encoder getInstance() {
        if (DefaultEncoder.singletonInstance == null) {
            synchronized (DefaultEncoder.class) {
                if (DefaultEncoder.singletonInstance == null) {
                    DefaultEncoder.singletonInstance = new DefaultEncoder();
                }
            }
        }
        return DefaultEncoder.singletonInstance;
    }
    
    private DefaultEncoder() {
        this.codecs = new ArrayList();
        this.htmlCodec = new HTMLEntityCodec();
        this.xmlCodec = new XMLEntityCodec();
        this.percentCodec = new PercentCodec();
        this.javaScriptCodec = new JavaScriptCodec();
        this.vbScriptCodec = new VBScriptCodec();
        this.cssCodec = new CSSCodec();
        this.logger = ESAPI.getLogger("Encoder");
        this.codecs.add(this.htmlCodec);
        this.codecs.add(this.percentCodec);
        this.codecs.add(this.javaScriptCodec);
    }
    
    public DefaultEncoder(final List<String> codecNames) {
        this.codecs = new ArrayList();
        this.htmlCodec = new HTMLEntityCodec();
        this.xmlCodec = new XMLEntityCodec();
        this.percentCodec = new PercentCodec();
        this.javaScriptCodec = new JavaScriptCodec();
        this.vbScriptCodec = new VBScriptCodec();
        this.cssCodec = new CSSCodec();
        this.logger = ESAPI.getLogger("Encoder");
        for (String clazz : codecNames) {
            try {
                if (clazz.indexOf(46) == -1) {
                    clazz = "org.owasp.esapi.codecs." + clazz;
                }
                this.codecs.add(Class.forName(clazz).newInstance());
            }
            catch (final Exception e) {
                this.logger.warning(Logger.EVENT_FAILURE, "Codec " + clazz + " listed in ESAPI.properties not on classpath");
            }
        }
    }
    
    @Override
    public String canonicalize(final String input) {
        if (input == null) {
            return null;
        }
        return this.canonicalize(input, !ESAPI.securityConfiguration().getAllowMultipleEncoding(), !ESAPI.securityConfiguration().getAllowMixedEncoding());
    }
    
    @Override
    public String canonicalize(final String input, final boolean strict) {
        return this.canonicalize(input, strict, strict);
    }
    
    @Override
    public String canonicalize(final String input, final boolean restrictMultiple, final boolean restrictMixed) {
        if (input == null) {
            return null;
        }
        String working = input;
        Codec codecFound = null;
        int mixedCount = 1;
        int foundCount = 0;
        boolean clean = false;
        while (!clean) {
            clean = true;
            for (final Codec codec : this.codecs) {
                final String old = working;
                working = codec.decode(working);
                if (!old.equals(working)) {
                    if (codecFound != null && codecFound != codec) {
                        ++mixedCount;
                    }
                    codecFound = codec;
                    if (clean) {
                        ++foundCount;
                    }
                    clean = false;
                }
            }
        }
        if (foundCount >= 2 && mixedCount > 1) {
            if (restrictMultiple || restrictMixed) {
                throw new IntrusionException("Input validation failure", "Multiple (" + foundCount + "x) and mixed encoding (" + mixedCount + "x) detected in " + input);
            }
            this.logger.warning(Logger.SECURITY_FAILURE, "Multiple (" + foundCount + "x) and mixed encoding (" + mixedCount + "x) detected in " + input);
        }
        else if (foundCount >= 2) {
            if (restrictMultiple) {
                throw new IntrusionException("Input validation failure", "Multiple (" + foundCount + "x) encoding detected in the input");
            }
            this.logger.warning(Logger.SECURITY_FAILURE, "Multiple (" + foundCount + "x) encoding detected in " + input);
        }
        else if (mixedCount > 1) {
            if (restrictMixed) {
                throw new IntrusionException("Input validation failure", "Mixed encoding (" + mixedCount + "x) detected in " + input);
            }
            this.logger.warning(Logger.SECURITY_FAILURE, "Mixed encoding (" + mixedCount + "x) detected in " + input);
        }
        return working;
    }
    
    @Override
    public String encodeForHTML(final String input) {
        if (input == null) {
            return null;
        }
        return this.htmlCodec.encode(DefaultEncoder.IMMUNE_HTML, input);
    }
    
    @Override
    public String decodeForHTML(final String input) {
        if (input == null) {
            return null;
        }
        return this.htmlCodec.decode(input);
    }
    
    @Override
    public String encodeForHTMLAttribute(final String input) {
        if (input == null) {
            return null;
        }
        return this.htmlCodec.encode(DefaultEncoder.IMMUNE_HTMLATTR, input);
    }
    
    @Override
    public String encodeForCSS(final String input) {
        if (input == null) {
            return null;
        }
        return this.cssCodec.encode(DefaultEncoder.IMMUNE_CSS, input);
    }
    
    @Override
    public String encodeForJavaScript(final String input) {
        if (input == null) {
            return null;
        }
        return this.javaScriptCodec.encode(DefaultEncoder.IMMUNE_JAVASCRIPT, input);
    }
    
    @Override
    public String encodeForVBScript(final String input) {
        if (input == null) {
            return null;
        }
        return this.vbScriptCodec.encode(DefaultEncoder.IMMUNE_VBSCRIPT, input);
    }
    
    @Override
    public String encodeForSQL(final Codec codec, final String input) {
        if (input == null) {
            return null;
        }
        return codec.encode(DefaultEncoder.IMMUNE_SQL, input);
    }
    
    @Override
    public String encodeForOS(final Codec codec, final String input) {
        if (input == null) {
            return null;
        }
        return codec.encode(DefaultEncoder.IMMUNE_OS, input);
    }
    
    @Override
    public String encodeForLDAP(final String input) {
        if (input == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); ++i) {
            final char c = input.charAt(i);
            switch (c) {
                case '\\': {
                    sb.append("\\5c");
                    break;
                }
                case '*': {
                    sb.append("\\2a");
                    break;
                }
                case '(': {
                    sb.append("\\28");
                    break;
                }
                case ')': {
                    sb.append("\\29");
                    break;
                }
                case '\0': {
                    sb.append("\\00");
                    break;
                }
                default: {
                    sb.append(c);
                    break;
                }
            }
        }
        return sb.toString();
    }
    
    @Override
    public String encodeForDN(final String input) {
        if (input == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        if (input.length() > 0 && (input.charAt(0) == ' ' || input.charAt(0) == '#')) {
            sb.append('\\');
        }
        for (int i = 0; i < input.length(); ++i) {
            final char c = input.charAt(i);
            switch (c) {
                case '\\': {
                    sb.append("\\\\");
                    break;
                }
                case ',': {
                    sb.append("\\,");
                    break;
                }
                case '+': {
                    sb.append("\\+");
                    break;
                }
                case '\"': {
                    sb.append("\\\"");
                    break;
                }
                case '<': {
                    sb.append("\\<");
                    break;
                }
                case '>': {
                    sb.append("\\>");
                    break;
                }
                case ';': {
                    sb.append("\\;");
                    break;
                }
                default: {
                    sb.append(c);
                    break;
                }
            }
        }
        if (input.length() > 1 && input.charAt(input.length() - 1) == ' ') {
            sb.insert(sb.length() - 1, '\\');
        }
        return sb.toString();
    }
    
    @Override
    public String encodeForXPath(final String input) {
        if (input == null) {
            return null;
        }
        return this.htmlCodec.encode(DefaultEncoder.IMMUNE_XPATH, input);
    }
    
    @Override
    public String encodeForXML(final String input) {
        if (input == null) {
            return null;
        }
        return this.xmlCodec.encode(DefaultEncoder.IMMUNE_XML, input);
    }
    
    @Override
    public String encodeForXMLAttribute(final String input) {
        if (input == null) {
            return null;
        }
        return this.xmlCodec.encode(DefaultEncoder.IMMUNE_XMLATTR, input);
    }
    
    @Override
    public String encodeForURL(final String input) throws EncodingException {
        if (input == null) {
            return null;
        }
        try {
            return URLEncoder.encode(input, ESAPI.securityConfiguration().getCharacterEncoding());
        }
        catch (final UnsupportedEncodingException ex) {
            throw new EncodingException("Encoding failure", "Character encoding not supported", ex);
        }
        catch (final Exception e) {
            throw new EncodingException("Encoding failure", "Problem URL encoding input", e);
        }
    }
    
    @Override
    public String decodeFromURL(final String input) throws EncodingException {
        if (input == null) {
            return null;
        }
        final String canonical = this.canonicalize(input);
        try {
            return URLDecoder.decode(canonical, ESAPI.securityConfiguration().getCharacterEncoding());
        }
        catch (final UnsupportedEncodingException ex) {
            throw new EncodingException("Decoding failed", "Character encoding not supported", ex);
        }
        catch (final Exception e) {
            throw new EncodingException("Decoding failed", "Problem URL decoding input", e);
        }
    }
    
    @Override
    public String encodeForBase64(final byte[] input, final boolean wrap) {
        if (input == null) {
            return null;
        }
        int options = 0;
        if (!wrap) {
            options |= 0x8;
        }
        return Base64.encodeBytes(input, options);
    }
    
    @Override
    public byte[] decodeFromBase64(final String input) throws IOException {
        if (input == null) {
            return null;
        }
        return Base64.decode(input);
    }
    
    static {
        IMMUNE_HTML = new char[] { ',', '.', '-', '_', ' ' };
        IMMUNE_HTMLATTR = new char[] { ',', '.', '-', '_' };
        IMMUNE_CSS = new char[] { '#' };
        IMMUNE_JAVASCRIPT = new char[] { ',', '.', '_' };
        IMMUNE_VBSCRIPT = new char[] { ',', '.', '_' };
        IMMUNE_XML = new char[] { ',', '.', '-', '_', ' ' };
        IMMUNE_SQL = new char[] { ' ' };
        IMMUNE_OS = new char[] { '-' };
        IMMUNE_XMLATTR = new char[] { ',', '.', '-', '_' };
        IMMUNE_XPATH = new char[] { ',', '.', '-', '_', ' ' };
    }
}
