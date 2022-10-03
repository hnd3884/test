package com.adventnet.iam.xss;

import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.util.DefaultErrorHandler;
import java.io.InputStream;
import org.apache.xerces.xni.parser.XMLInputSource;
import java.io.ByteArrayInputStream;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import java.io.Writer;
import java.io.StringWriter;
import java.util.logging.Level;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import java.util.Properties;
import org.cyberneko.html.HTMLConfiguration;
import java.util.logging.Logger;

public class NekoXSSFilter extends XSSFilter
{
    public static Logger currentLogger;
    HTMLConfiguration htmlConfigParser;
    XSSElementRemover xssElementRemover;
    boolean skipNekoOutputEscaping;
    boolean handleNekoParseError;
    boolean enableNekoParserExceptionTrace;
    
    NekoXSSFilter() {
        this.htmlConfigParser = null;
        this.xssElementRemover = null;
        this.skipNekoOutputEscaping = false;
        this.handleNekoParseError = true;
        this.enableNekoParserExceptionTrace = false;
    }
    
    @Override
    void init(final Properties prop, final XSSFilterConfiguration xssFilterConfig) {
        super.init(prop, xssFilterConfig);
        String attributeLimit = "1000";
        this.xssElementRemover = new XSSElementRemover();
        this.xssElementRemover.xssFilterConf = this.xssFilterConfig;
        this.xssElementRemover.cssUtil = this.cssUtil;
        if (prop.containsKey("skip-neko-output-escaping")) {
            this.skipNekoOutputEscaping = "true".equalsIgnoreCase(prop.getProperty("skip-neko-output-escaping"));
        }
        if (prop.containsKey("handle-neko-parseerror")) {
            this.handleNekoParseError = "true".equalsIgnoreCase(prop.getProperty("handle-neko-parseerror"));
        }
        if (prop.containsKey("enable-neko-parser-exception-trace")) {
            this.enableNekoParserExceptionTrace = "true".equalsIgnoreCase(prop.getProperty("enable-neko-parser-exception-trace"));
        }
        if (prop.containsKey("attribute-limit")) {
            attributeLimit = prop.getProperty("attribute-limit");
        }
        if (this.htmlConfigParser == null) {
            this.htmlConfigParser = new HTMLConfiguration();
            if (this.skipNekoOutputEscaping) {
                this.htmlConfigParser.setSkipDecode(this.skipNekoOutputEscaping);
            }
            this.htmlConfigParser.setProperty("http://cyberneko.org/html/properties/names/attrs", (Object)"lower");
            this.htmlConfigParser.setProperty("http://cyberneko.org/html/properties/names/elems", (Object)"lower");
            this.htmlConfigParser.setProperty("http://cyberneko.org/html/zoho/features/attribute-limit", (Object)attributeLimit);
            this.htmlConfigParser.setFeature("http://cyberneko.org/html/features/balance-tags", xssFilterConfig.balanceHtmlTags);
            this.htmlConfigParser.setFeature("http://cyberneko.org/html/features/report-errors", this.handleNekoParseError);
            this.htmlConfigParser.setFeature("http://cyberneko.org/html/features/scanner/ignore-specified-charset", xssFilterConfig.ignoreMetaCharset);
            if (xssFilterConfig.balanceHtmlTags) {
                this.htmlConfigParser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment", true);
            }
            this.htmlConfigParser.setFeature("http://cyberneko.org/html/features/parse-noscript-content", false);
            this.htmlConfigParser.setErrorHandler((XMLErrorHandler)new XSSErrorHandler());
        }
    }
    
    @Override
    public String filterXSS(final String domain, final String value, final String encoding) {
        NekoXSSFilter.currentLogger.log(Level.FINE, "Neko XSS Filter started....");
        final StringWriter swriter = new StringWriter();
        org.cyberneko.html.filters.Writer writer = null;
        if (this.skipNekoOutputEscaping) {
            writer = new UnEncodedWriter(swriter, encoding);
        }
        else {
            writer = new org.cyberneko.html.filters.Writer((Writer)swriter, encoding);
        }
        final XMLDocumentFilter[] filters = { (XMLDocumentFilter)this.xssElementRemover, (XMLDocumentFilter)writer };
        NekoXSSFilter.currentLogger.log(Level.FINE, "INPUT  STR = {0}", XSSUtil.getLogString(value));
        if (XSSUtil.isEnableTrustedDomainScriptTags()) {
            this.xssElementRemover.setDomain(domain);
        }
        final String resString = this.filterOrBalanceHtmlContent(filters, value, encoding, swriter);
        this.setNekoXSSFilterVars();
        NekoXSSFilter.currentLogger.log(Level.FINE, "Neko XSS Filter ended....");
        return resString;
    }
    
    public String filterOrBalanceHtmlContent(final XMLDocumentFilter[] filters, String resString, final String encoding, final StringWriter swriter) {
        this.htmlConfigParser.setProperty("http://cyberneko.org/html/properties/filters", (Object)filters);
        final XMLInputSource source = new XMLInputSource((String)null, (String)null, (String)null, (InputStream)new ByteArrayInputStream(resString.getBytes()), encoding);
        try {
            this.htmlConfigParser.parse(source);
        }
        catch (final Exception e) {
            if (this.enableNekoParserExceptionTrace) {
                NekoXSSFilter.currentLogger.log(Level.WARNING, "Exception : ", e);
            }
            else {
                NekoXSSFilter.currentLogger.log(Level.WARNING, "Exception : {0}", e.getMessage());
            }
        }
        finally {
            this.htmlConfigParser.clearFilterComponents(filters);
        }
        swriter.flush();
        resString = swriter.toString();
        NekoXSSFilter.currentLogger.log(Level.FINE, "OUTPUT ENC STR = {0}", XSSUtil.getLogString(resString));
        return resString;
    }
    
    @Override
    public String balanceHTMLContent(final String content, final String encoding) {
        final StringWriter swriter = new StringWriter();
        org.cyberneko.html.filters.Writer writer = null;
        if (this.skipNekoOutputEscaping) {
            writer = new UnEncodedWriter(swriter, encoding);
        }
        else {
            writer = new org.cyberneko.html.filters.Writer((Writer)swriter, encoding);
        }
        final XMLDocumentFilter[] filters = { (XMLDocumentFilter)writer };
        return this.filterOrBalanceHtmlContent(filters, content, encoding, swriter);
    }
    
    private void setNekoXSSFilterVars() {
        if (this.xssElementRemover != null) {
            this.altered = this.xssElementRemover.altered;
            this.removedElementsList = this.xssElementRemover.removedElementsList;
            this.cssUtil = this.xssElementRemover.cssUtil;
        }
    }
    
    @Override
    public void reset() {
        super.reset();
        if (this.xssElementRemover != null) {
            this.xssElementRemover.altered = false;
            this.xssElementRemover.removedElementsList = null;
            if (this.xssElementRemover.cssUtil != null) {
                this.xssElementRemover.cssUtil.altered = false;
            }
        }
    }
    
    static {
        NekoXSSFilter.currentLogger = Logger.getLogger(NekoXSSFilter.class.getName());
    }
    
    public class XSSErrorHandler extends DefaultErrorHandler
    {
        public void warning(final String domain, final String key, final XMLParseException ex) throws XNIException {
            NekoXSSFilter.currentLogger.log(Level.FINE, "{0} ", ex.getMessage());
        }
        
        public void error(final String domain, final String key, final XMLParseException ex) throws XNIException {
            NekoXSSFilter.currentLogger.log(Level.SEVERE, "{0} ", ex.getMessage());
            if (NekoXSSFilter.this.xssElementRemover != null) {
                NekoXSSFilter.this.xssElementRemover.altered = true;
            }
        }
        
        public void fatalError(final String domain, final String key, final XMLParseException ex) throws XNIException {
            NekoXSSFilter.currentLogger.log(Level.SEVERE, "{0} ", ex.getMessage());
            if (NekoXSSFilter.this.xssElementRemover != null) {
                NekoXSSFilter.this.xssElementRemover.altered = true;
            }
        }
    }
}
