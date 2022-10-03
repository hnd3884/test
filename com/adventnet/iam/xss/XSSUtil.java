package com.adventnet.iam.xss;

import javax.xml.validation.Validator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import org.owasp.validator.html.Policy;
import java.io.File;
import org.cyberneko.html.HTMLScanner;
import java.util.regex.Matcher;
import com.adventnet.iam.security.IAMSecurityException;
import com.adventnet.iam.security.SecurityUtil;
import java.util.logging.Level;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class XSSUtil
{
    public static Pattern XSSPATTERN;
    public static Pattern TRIM_CTRLCHARS_PATTERN;
    public static Pattern ENCODE_CHECK_PATTERN;
    private static boolean enableTrustedDomainScriptTags;
    static final Logger logger;
    Properties xssElementRemoverProps;
    Properties cssFilterProps;
    boolean filterCSSOnly;
    boolean filterCSSRawDeclarations;
    boolean enableCanonicalizeStrict;
    boolean balanceHtmlTags;
    boolean htmlDontDecode;
    boolean enableCanonicalization;
    private static int maxLogSize;
    public static InheritableThreadLocal<HashMap<String, XSSParserVars>> currentXSSVars;
    String name;
    boolean extendsFilterInit;
    String extendsFilter;
    private static Pattern commentPattern;
    boolean antisamySchemaErrorOccurred;
    
    public List<String> getRemovedElements() {
        final XSSParserVars xssParserVars = this.getCurrentXSSUtilVars();
        if (xssParserVars != null && xssParserVars.xssFilter != null) {
            return xssParserVars.xssFilter.getRemovedElements();
        }
        return null;
    }
    
    XSSParserVars getCurrentXSSUtilVars() {
        final HashMap<String, XSSParserVars> xssVarMap = XSSUtil.currentXSSVars.get();
        if (xssVarMap != null) {
            return xssVarMap.get(this.name);
        }
        return null;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Properties getInitProperties() {
        return (this.xssElementRemoverProps != null) ? this.xssElementRemoverProps : this.cssFilterProps;
    }
    
    public boolean balanceHtmlTags() {
        return this.balanceHtmlTags;
    }
    
    public boolean isFilterExtended() {
        return this.extendsFilterInit;
    }
    
    public String getExtendsFilter() {
        return this.extendsFilter;
    }
    
    public void extendFilterProps(final XSSUtil extendsXssUtil) {
        if (extendsXssUtil.getExtendsFilter() != null) {
            XSSUtil.logger.severe("XSSUtil : " + this.name + " cannot extend already extended XSSUtil : " + extendsXssUtil.getExtendsFilter());
            return;
        }
        if (!this.extendsFilterInit) {
            final Properties extendsXSSProps = new Properties();
            extendsXSSProps.putAll(extendsXssUtil.getInitProperties());
            XSSUtil.logger.log(Level.FINE, "Name {0}  PROPS : {1} EXTEND-PROPS : {2}", new Object[] { this.name, this.getInitProperties(), extendsXSSProps });
            extendsXSSProps.putAll(this.getInitProperties());
            this.init(extendsXSSProps);
            XSSUtil.logger.log(Level.FINE, "XSSUtil {0} EXTENDED PROPS : {1} ", new Object[] { this.name, this.getInitProperties() });
            this.extendsFilterInit = true;
        }
    }
    
    public XSSUtil(final Properties props) {
        this.xssElementRemoverProps = null;
        this.cssFilterProps = null;
        this.filterCSSOnly = false;
        this.filterCSSRawDeclarations = false;
        this.enableCanonicalizeStrict = false;
        this.balanceHtmlTags = false;
        this.htmlDontDecode = false;
        this.enableCanonicalization = true;
        this.name = null;
        this.extendsFilterInit = false;
        this.extendsFilter = null;
        this.antisamySchemaErrorOccurred = false;
        this.init(props);
    }
    
    public static void initXSSDetectPattern(final String xssDetectPattern, final String xssDetectPatternExt, final String encodingCheckPattern, final String trimCtrlCharPattern) throws Exception {
        if (xssDetectPattern != null && xssDetectPattern.length() > 0) {
            if (xssDetectPatternExt != null && xssDetectPatternExt.length() > 0) {
                XSSUtil.XSSPATTERN = Pattern.compile("(?:" + xssDetectPattern + "|" + xssDetectPatternExt + ")");
            }
            else {
                XSSUtil.XSSPATTERN = Pattern.compile(xssDetectPattern);
            }
            XSSUtil.logger.log(Level.FINE, "Compiled XSS Pattern : {0}", XSSUtil.XSSPATTERN.toString());
            if (encodingCheckPattern != null && encodingCheckPattern.length() > 0) {
                XSSUtil.ENCODE_CHECK_PATTERN = Pattern.compile(encodingCheckPattern);
                XSSUtil.logger.log(Level.FINE, "Compiled ENCODING CHECK Pattern : {0}", XSSUtil.ENCODE_CHECK_PATTERN.toString());
            }
            if (trimCtrlCharPattern != null && trimCtrlCharPattern.length() > 0) {
                XSSUtil.TRIM_CTRLCHARS_PATTERN = Pattern.compile(trimCtrlCharPattern);
                XSSUtil.logger.log(Level.FINE, "Compiled TRIM_CTRLCHARS_PATTERN Pattern : {0}", XSSUtil.TRIM_CTRLCHARS_PATTERN.toString());
            }
            return;
        }
        throw new RuntimeException("Invalid xss.detect.pattern in SecurityFilter properties");
    }
    
    public static void enableTrustedDomainScriptTags() {
        XSSUtil.enableTrustedDomainScriptTags = true;
    }
    
    public static boolean isEnableTrustedDomainScriptTags() {
        return XSSUtil.enableTrustedDomainScriptTags;
    }
    
    void init(final Properties props) {
        if (props == null || props.isEmpty()) {
            XSSUtil.logger.log(Level.WARNING, "XSS Configurations null. Not initialized");
            return;
        }
        this.name = props.getProperty("name");
        if ("true".equals(props.getProperty("enable-canonicalize-strict"))) {
            this.enableCanonicalizeStrict = true;
        }
        if ("true".equals(props.getProperty("filter-css-only"))) {
            this.filterCSSOnly = true;
            this.filterCSSRawDeclarations = "true".equals(props.getProperty("filter-css-raw-declarations"));
            this.cssFilterProps = props;
        }
        else {
            this.xssElementRemoverProps = props;
            this.balanceHtmlTags = "true".equals(props.getProperty("balance-html-tags"));
            this.htmlDontDecode = "true".equals(props.getProperty("html-dont-decode"));
            if (props.containsKey("enable-canonicalization")) {
                this.enableCanonicalization = "true".equals(props.getProperty("enable-canonicalization"));
            }
            this.extendsFilter = props.getProperty("extends");
        }
        this.validateAntisamyPolicyAgainstSchema(props);
    }
    
    public static boolean detectXSS(final String value) {
        return detectXSS(value, false);
    }
    
    public static boolean detectXSS(final String value, final boolean enableXSSTimeoutMatcher) {
        if (value == null) {
            return false;
        }
        if (detect(value, enableXSSTimeoutMatcher)) {
            return true;
        }
        if (XSSUtil.ENCODE_CHECK_PATTERN.matcher(value).find()) {
            XSSUtil.logger.log(Level.FINE, "Encoding detected. Canonicalize the string");
            final String tmpValue = IAMEncoder.encoder().canonicalize(value);
            if (tmpValue == null || (tmpValue != null && detect(tmpValue, enableXSSTimeoutMatcher))) {
                return true;
            }
        }
        if (value.contains(";base64,")) {
            String tmpValue = value.substring(value.indexOf(";base64,") + ";base64".length(), value.length());
            try {
                tmpValue = new String(SecurityUtil.BASE64_DECODE(tmpValue));
                if (detect(tmpValue, enableXSSTimeoutMatcher)) {
                    XSSUtil.logger.log(Level.WARNING, "XSSUtil-Base64Decoder Hack{0}", getLogString(tmpValue));
                    return true;
                }
            }
            catch (final Exception ioe) {
                XSSUtil.logger.log(Level.WARNING, "XSSUtil-Base64Decoder {0}", ioe.getMessage());
            }
        }
        return false;
    }
    
    public String filterXSS(final String value) {
        return this.filterXSS(null, value);
    }
    
    public String filterXSS(final String domain, final String value) {
        return this.filterXSS(domain, value, "UTF-8");
    }
    
    public String filterXSS(final String domain, final String strValue, final String encoding) {
        if (this.extendsFilter != null && !this.extendsFilterInit) {
            throw new IAMSecurityException("XSS extends filter not loaded : " + this.extendsFilter);
        }
        if (strValue == null) {
            return strValue;
        }
        String value = strValue;
        if (this.enableCanonicalization && XSSUtil.ENCODE_CHECK_PATTERN.matcher(value).find()) {
            value = IAMEncoder.encoder().canonicalize(value);
            if (value == null && this.enableCanonicalizeStrict) {
                XSSUtil.logger.log(Level.SEVERE, "After Canonicalize value is {0}", getLogString(value));
                throw new IAMSecurityException("CANONICALIZE_FAILED");
            }
        }
        if (value != null) {
            final String result = this.filter(domain, value, encoding);
            if (this.checkXSSAlteredStatus() || (!this.htmlDontDecode && this.balanceHtmlTags)) {
                return result;
            }
            if (this.htmlDontDecode && this.balanceHtmlTags) {
                return this.balanceHTMLContent(strValue);
            }
            return strValue;
        }
        else {
            XSSUtil.logger.log(Level.SEVERE, "Canonicalize failed . Filtering orig STR for NOW {0}", getLogString(strValue));
            final String result = this.filter(domain, strValue, encoding);
            if (!this.checkAlteredStatus()) {
                return strValue;
            }
            return result;
        }
    }
    
    boolean checkAlteredStatus() {
        return this.balanceHtmlTags || this.checkXSSAlteredStatus();
    }
    
    boolean checkXSSAlteredStatus() {
        final XSSParserVars xssParserVars = this.getCurrentXSSUtilVars();
        if (xssParserVars == null) {
            return false;
        }
        final CSSUtil cssUtil = xssParserVars.cssUtil;
        final XSSFilter xssFilter = xssParserVars.xssFilter;
        if (xssFilter != null && xssFilter.isAltered()) {
            XSSUtil.logger.fine("xssFilter.altered " + xssFilter.isAltered());
            return true;
        }
        if (xssFilter != null && xssFilter.isCSSUtilAltered()) {
            XSSUtil.logger.fine("xssFilter.cssUtil.altered " + xssFilter.isCSSUtilAltered());
            return true;
        }
        if (cssUtil != null && cssUtil.altered) {
            XSSUtil.logger.fine("cssUtil.altered " + cssUtil.altered);
            return true;
        }
        return false;
    }
    
    public static boolean detect(String value, final boolean enableTimeoutMatcher) {
        value = value.toLowerCase();
        if (value != null) {
            if (!enableTimeoutMatcher) {
                if (XSSUtil.XSSPATTERN.matcher(value).find()) {
                    return true;
                }
                if (XSSUtil.TRIM_CTRLCHARS_PATTERN.matcher(value).find()) {
                    value = XSSUtil.TRIM_CTRLCHARS_PATTERN.matcher(value).replaceAll("");
                    if (value != null && XSSUtil.XSSPATTERN.matcher(value).find()) {
                        return true;
                    }
                }
            }
            else {
                XSSUtil.logger.log(Level.FINE, "Using  timeout matcher utility - MatcherUtil for XSS Detection Value : " + getLogString(value));
                try {
                    if (SecurityUtil.getMatcherUtil().find(XSSUtil.XSSPATTERN, value)) {
                        return true;
                    }
                    if (XSSUtil.TRIM_CTRLCHARS_PATTERN.matcher(value).find()) {
                        value = XSSUtil.TRIM_CTRLCHARS_PATTERN.matcher(value).replaceAll("");
                        if (value != null && SecurityUtil.getMatcherUtil().find(XSSUtil.XSSPATTERN, value)) {
                            return true;
                        }
                    }
                }
                catch (final Exception e) {
                    XSSUtil.logger.log(Level.SEVERE, "Exception occurred while validating the input against the XSS pattern.");
                    XSSUtil.logger.log(Level.FINE, "", e);
                    return true;
                }
            }
        }
        return false;
    }
    
    XSSParserVars initializeXSSParserVars() {
        XSSParserVars curXssVars = this.getCurrentXSSUtilVars();
        XSSFilter xssFilter = null;
        CSSUtil cssUtil = null;
        if (curXssVars != null) {
            xssFilter = curXssVars.xssFilter;
            cssUtil = curXssVars.cssUtil;
        }
        if (this.filterCSSOnly) {
            if (this.cssFilterProps != null) {
                if (cssUtil == null) {
                    cssUtil = new CSSUtil(this.cssFilterProps);
                }
                cssUtil.altered = false;
            }
        }
        else if (xssFilter == null) {
            final XSSFilterConfiguration xssFilterConfig = new XSSFilterConfiguration(this.xssElementRemoverProps);
            xssFilter = xssFilterConfig.createXSSFilter();
            xssFilter.init(this.xssElementRemoverProps, xssFilterConfig);
        }
        else {
            xssFilter.reset();
            if (xssFilter.cssUtil == null) {
                xssFilter.cssUtil = new CSSUtil(this.xssElementRemoverProps);
            }
        }
        if (curXssVars == null) {
            curXssVars = new XSSParserVars(cssUtil, xssFilter);
            HashMap<String, XSSParserVars> xssVarMap = XSSUtil.currentXSSVars.get();
            if (xssVarMap == null) {
                xssVarMap = new HashMap<String, XSSParserVars>();
                XSSUtil.currentXSSVars.set(xssVarMap);
            }
            xssVarMap.put(this.name, curXssVars);
        }
        return curXssVars;
    }
    
    public String filter(final String domain, String string, final String encoding) {
        if (string == null) {
            return null;
        }
        try {
            string = string.replaceAll("\u0000", "");
            final XSSParserVars xssParserVars = this.initializeXSSParserVars();
            final XSSFilter xssFilter = xssParserVars.xssFilter;
            final CSSUtil cssUtil = xssParserVars.cssUtil;
            if (this.filterCSSOnly) {
                final String value = this.filterCSSRawDeclarations ? cssUtil.cleanStyleDeclaration(string) : cssUtil.cleanStyleSheet(string);
                return value;
            }
            if (xssFilter == null) {
                XSSUtil.logger.log(Level.SEVERE, "Skip cleanXSS as no remove element/attributes specified or Parser init failed");
                return string;
            }
            return xssFilter.filterXSS(domain, string, encoding);
        }
        catch (final Exception e) {
            if (e.getClass().getSimpleName().equals("IAMSecurityException")) {
                final IAMSecurityException ise = (IAMSecurityException)e;
                if (ise.getErrorCode().equals("XSS_DETECTED") || ise.getErrorCode().equals("PATTERN_NOT_DEFINED") || ise.getErrorCode().equals("INVALID_XSSFILTER_CONFIGURATION") || ise.getErrorCode().equals("HTML_PARSE_FAILED")) {
                    throw ise;
                }
            }
            XSSUtil.logger.log(Level.WARNING, "Error cleanXSSViaHTMLParser {0} Returning string for NOW \n{1}", new Object[] { e.getMessage(), getLogString(string) });
            return string;
        }
    }
    
    public String balanceHTMLContent(final String content) {
        final XSSParserVars xssParserVars = this.initializeXSSParserVars();
        final XSSFilter xssFilter = xssParserVars.xssFilter;
        return xssFilter.balanceHTMLContent(content, "UTF-8");
    }
    
    public static boolean isValid(final Object value) {
        return value != null && !value.equals("") && !value.equals("null");
    }
    
    public static String removeComment(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        final Matcher m = XSSUtil.commentPattern.matcher(value);
        while (m.find()) {
            final String temp = m.group();
            value = value.replace(temp, "");
        }
        return value;
    }
    
    public void setMaxLogSize(final int maxLogSize) {
        XSSUtil.maxLogSize = maxLogSize;
    }
    
    public static String getLogString(final String origString) {
        String logString = origString;
        if (logString.length() > XSSUtil.maxLogSize && XSSUtil.maxLogSize != -1) {
            logString = logString.substring(0, XSSUtil.maxLogSize);
            logString += "...";
        }
        return logString;
    }
    
    public static boolean isValidHTMLDoctypeDeclaration(final String rootElementName, final String publicId, final String systemId) {
        if ("html".equalsIgnoreCase(rootElementName)) {
            if (publicId == null && (systemId == null || "about:legacy-compat".equalsIgnoreCase(systemId))) {
                return true;
            }
            final String publicIdLc;
            if (publicId != null && HTMLScanner.HTML_4_01_PUB_VS_SYS_ID_MAP.containsKey(publicIdLc = publicId.toLowerCase()) && (systemId == null || HTMLScanner.HTML_4_01_PUB_VS_SYS_ID_MAP.get(publicIdLc).equalsIgnoreCase(systemId))) {
                return true;
            }
        }
        return false;
    }
    
    private void validateAntisamyPolicyAgainstSchema(final Properties properties) throws IAMSecurityException {
        if (!properties.containsKey("xss-filter-type")) {
            return;
        }
        final XSSFilterConfiguration.ParserType parserType = XSSFilterConfiguration.ParserType.valueOf(properties.getProperty("xss-filter-type").toUpperCase());
        switch (parserType) {
            case ANTISAMY:
            case ANTISAMY_CSS:
            case ANTISAMY_CSSPROPERTIES: {
                final String antisamyPolicyFile = SecurityUtil.getSecurityConfigurationDir() + File.separator + properties.getProperty("whitelist-policy");
                final InputStream antisamyXSDStream = Policy.class.getClassLoader().getResourceAsStream("antisamy.xsd");
                this.validateAntisamyPolicyAgainstSchema(new File(antisamyPolicyFile), antisamyXSDStream);
                break;
            }
        }
    }
    
    private void validateAntisamyPolicyAgainstSchema(final File antisamyPolicyFile, final InputStream antisamyXSDStream) {
        if (!antisamyPolicyFile.exists() || antisamyXSDStream == null) {
            return;
        }
        try {
            final Validator validator = SecurityUtil.getXMLSchemaFactoryFromThreadLocal().newSchema(new StreamSource(antisamyXSDStream)).newValidator();
            validator.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(final SAXParseException exception) throws SAXException {
                    XSSUtil.logger.log(Level.WARNING, exception.toString());
                }
                
                @Override
                public void fatalError(final SAXParseException exception) throws SAXException {
                    XSSUtil.this.antisamySchemaErrorOccurred = true;
                    XSSUtil.logger.log(Level.SEVERE, exception.toString());
                }
                
                @Override
                public void error(final SAXParseException exception) throws SAXException {
                    XSSUtil.this.antisamySchemaErrorOccurred = true;
                    XSSUtil.logger.log(Level.SEVERE, exception.toString());
                }
            });
            validator.validate(new StreamSource(antisamyPolicyFile));
        }
        catch (final Exception e) {
            XSSUtil.logger.log(Level.SEVERE, "Antisamy XSD validation gets failed, Error Msg: {0}", new Object[] { e });
            throw new IAMSecurityException("INVALID_XSSFILTER_CONFIGURATION");
        }
        if (this.antisamySchemaErrorOccurred) {
            XSSUtil.logger.log(Level.SEVERE, "Antisamy XSD validation gets failed");
            throw new IAMSecurityException("INVALID_XSSFILTER_CONFIGURATION");
        }
    }
    
    static {
        XSSUtil.XSSPATTERN = null;
        XSSUtil.TRIM_CTRLCHARS_PATTERN = Pattern.compile("[\\x00]");
        XSSUtil.ENCODE_CHECK_PATTERN = Pattern.compile("(?:(%3(c|C)))");
        XSSUtil.enableTrustedDomainScriptTags = false;
        logger = Logger.getLogger(XSSUtil.class.getName());
        XSSUtil.maxLogSize = -1;
        XSSUtil.currentXSSVars = new InheritableThreadLocal<HashMap<String, XSSParserVars>>();
        XSSUtil.commentPattern = Pattern.compile("((<!--((?!-->).)*-->)|(/\\*((?!\\*/).)*\\*/))", 32);
    }
    
    static class XSSParserVars
    {
        CSSUtil cssUtil;
        XSSFilter xssFilter;
        
        XSSParserVars(final CSSUtil cssUtil, final XSSFilter xssFilter) {
            this.cssUtil = null;
            this.xssFilter = null;
            this.cssUtil = cssUtil;
            this.xssFilter = xssFilter;
        }
    }
}
