package org.owasp.validator.html;

import org.xml.sax.SAXParseException;
import org.slf4j.LoggerFactory;
import javax.xml.transform.Source;
import javax.xml.validation.SchemaFactory;
import javax.xml.transform.stream.StreamSource;
import java.util.Set;
import com.zoho.security.validator.url.CustomScheme;
import com.zoho.security.validator.url.Scheme;
import com.zoho.security.validator.url.URLValidatorRule;
import org.w3c.dom.NodeList;
import java.util.Collection;
import org.owasp.validator.html.scan.Constants;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import org.owasp.validator.html.util.URIUtils;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.ErrorHandler;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.Reader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import javax.xml.parsers.ParserConfigurationException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import java.util.concurrent.Callable;
import java.util.Iterator;
import org.owasp.validator.html.util.XMLUtil;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.Collections;
import java.net.URL;
import java.net.URI;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import javax.xml.validation.Schema;
import com.zoho.security.validator.url.URLValidatorAPI;
import java.util.List;
import org.owasp.validator.html.model.Attribute;
import org.owasp.validator.html.model.Property;
import org.owasp.validator.html.model.Tag;
import org.owasp.validator.html.model.AntiSamyPattern;
import java.util.Map;
import java.util.regex.Pattern;
import org.slf4j.Logger;

public class Policy
{
    private static final Logger logger;
    public static final Pattern ANYTHING_REGEXP;
    private static final String POLICY_SCHEMA_URI = "antisamy.xsd";
    protected static final String DEFAULT_POLICY_URI = "resources/antisamy.xml";
    private static final String DEFAULT_ONINVALID = "removeAttribute";
    public static final int DEFAULT_MAX_INPUT_SIZE = 100000;
    public static final int DEFAULT_MAX_STYLESHEET_IMPORTS = 1;
    public static final String OMIT_XML_DECLARATION = "omitXmlDeclaration";
    public static final String OMIT_DOCTYPE_DECLARATION = "omitDoctypeDeclaration";
    public static final String USE_XHTML = "useXHTML";
    public static final String FORMAT_OUTPUT = "formatOutput";
    public static final String EMBED_STYLESHEETS = "embedStyleSheets";
    public static final String CONNECTION_TIMEOUT = "connectionTimeout";
    public static final String ANCHORS_NOFOLLOW = "nofollowAnchors";
    public static final String VALIDATE_PARAM_AS_EMBED = "validateParamAsEmbed";
    public static final String PRESERVE_SPACE = "preserveSpace";
    public static final String PRESERVE_COMMENTS = "preserveComments";
    public static final String ENTITY_ENCODE_INTL_CHARS = "entityEncodeIntlChars";
    public static final String ALLOW_DYNAMIC_ATTRIBUTES = "allowDynamicAttributes";
    public static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    public static final String EXTERNAL_PARAM_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
    public static final String DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";
    public static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    public static final String URLVALIDATION_ENABLE = "urlvalidation-enable";
    public static final String ACTION_VALIDATE = "validate";
    public static final String ACTION_FILTER = "filter";
    public static final String ACTION_TRUNCATE = "truncate";
    private final Map<String, AntiSamyPattern> commonRegularExpressions;
    protected final Map<String, Tag> tagRules;
    private final Map<String, Property> cssRules;
    protected final Map<String, String> directives;
    private final Map<String, Attribute> globalAttributes;
    private final Map<String, Attribute> dynamicAttributes;
    private final TagMatcher allowedEmptyTagsMatcher;
    private final TagMatcher requiresClosingTagsMatcher;
    private final List<String> atKeyFrameCSSSelectors;
    protected URLValidatorAPI urlValidator;
    private final List<String> urlvalidation_attributes;
    private static volatile Schema schema;
    private static boolean validateSchema;
    public static final String VALIDATIONPROPERTY = "owasp.validator.validateschema";
    
    private static void loadValidateSchemaProperty() {
        final String validateProperty = System.getProperty("owasp.validator.validateschema");
        if (validateProperty != null) {
            setSchemaValidation(Boolean.parseBoolean(validateProperty));
            Policy.logger.warn("Setting AntiSamy policy schema validation to '" + getSchemaValidation() + "' because '" + "owasp.validator.validateschema" + "' system property set to: '" + validateProperty + "'. Note: this feature is temporary and will go away in AntiSamy v1.7.0 (~mid/late 2022) when validation will become mandatory.");
        }
        else {
            Policy.validateSchema = true;
        }
    }
    
    public Tag getTagByLowercaseName(final String tagName) {
        return this.tagRules.get(tagName);
    }
    
    public Property getPropertyByName(final String propertyName) {
        return this.cssRules.get(propertyName.toLowerCase());
    }
    
    @Deprecated
    public static boolean getSchemaValidation() {
        return Policy.validateSchema;
    }
    
    @Deprecated
    public static void setSchemaValidation(final boolean enable) {
        Policy.validateSchema = enable;
    }
    
    public static Policy getInstance() throws PolicyException {
        return getInstance("resources/antisamy.xml");
    }
    
    public static Policy getInstance(final String filename) throws PolicyException {
        final File file = new File(filename);
        return getInstance(file);
    }
    
    public static Policy getInstance(final InputStream inputStream) throws PolicyException {
        final String logMsg = "Attempting to load AntiSamy policy from an input stream.";
        if (Policy.validateSchema) {
            Policy.logger.info("Attempting to load AntiSamy policy from an input stream.");
        }
        else {
            Policy.logger.warn("Attempting to load AntiSamy policy from an input stream.");
        }
        return new InternalPolicy(getSimpleParseContext(getTopLevelElement(inputStream)));
    }
    
    public static Policy getInstance(final File file) throws PolicyException {
        try {
            final URI uri = file.toURI();
            return getInstance(uri.toURL());
        }
        catch (final IOException e) {
            throw new PolicyException(e);
        }
    }
    
    public static Policy getInstance(final URL url) throws PolicyException {
        final String logMsg = "Attempting to load AntiSamy policy from URL: " + url.toString();
        if (Policy.validateSchema) {
            Policy.logger.info(logMsg);
        }
        else {
            Policy.logger.warn(logMsg);
        }
        return new InternalPolicy(getParseContext(getTopLevelElement(url), url));
    }
    
    protected Policy(final ParseContext parseContext) throws PolicyException {
        this.allowedEmptyTagsMatcher = new TagMatcher(parseContext.allowedEmptyTags);
        this.requiresClosingTagsMatcher = new TagMatcher(parseContext.requireClosingTags);
        this.commonRegularExpressions = Collections.unmodifiableMap((Map<? extends String, ? extends AntiSamyPattern>)parseContext.commonRegularExpressions);
        this.tagRules = Collections.unmodifiableMap((Map<? extends String, ? extends Tag>)parseContext.tagRules);
        this.cssRules = Collections.unmodifiableMap((Map<? extends String, ? extends Property>)parseContext.cssRules);
        this.directives = Collections.unmodifiableMap((Map<? extends String, ? extends String>)parseContext.directives);
        this.globalAttributes = Collections.unmodifiableMap((Map<? extends String, ? extends Attribute>)parseContext.globalAttributes);
        this.dynamicAttributes = Collections.unmodifiableMap((Map<? extends String, ? extends Attribute>)parseContext.dynamicAttributes);
        this.atKeyFrameCSSSelectors = Collections.unmodifiableList((List<? extends String>)parseContext.atKeyFrameCSSSelectors);
        this.urlValidator = parseContext.urlValidator;
        this.urlvalidation_attributes = Collections.unmodifiableList((List<? extends String>)this.getURLValidationProperties("urlvalidation-attributes", "href,xlink:href,action,src"));
    }
    
    protected Policy(final Policy old, final Map<String, String> directives, final Map<String, Tag> tagRules) {
        this.allowedEmptyTagsMatcher = old.allowedEmptyTagsMatcher;
        this.requiresClosingTagsMatcher = old.requiresClosingTagsMatcher;
        this.commonRegularExpressions = old.commonRegularExpressions;
        this.tagRules = tagRules;
        this.cssRules = old.cssRules;
        this.directives = directives;
        this.globalAttributes = old.globalAttributes;
        this.dynamicAttributes = old.dynamicAttributes;
        this.atKeyFrameCSSSelectors = old.atKeyFrameCSSSelectors;
        this.urlValidator = old.urlValidator;
        this.urlvalidation_attributes = old.urlvalidation_attributes;
    }
    
    private List<String> getURLValidationProperties(final String Directive, final String defaultValues) {
        final List<String> properties = new ArrayList<String>();
        final String urlValidationProperties = this.getDirective(Directive);
        final String[] array;
        final String[] attributes = array = ((urlValidationProperties != null) ? urlValidationProperties.split(",") : defaultValues.split(","));
        for (final String attr : array) {
            properties.add(attr.trim().toLowerCase());
        }
        return properties;
    }
    
    protected static ParseContext getSimpleParseContext(final Element topLevelElement) throws PolicyException {
        final ParseContext parseContext = new ParseContext();
        if (getByTagName(topLevelElement, "include").iterator().hasNext()) {
            throw new IllegalArgumentException("A policy file loaded with an InputStream cannot contain include references");
        }
        parsePolicy(topLevelElement, parseContext);
        return parseContext;
    }
    
    protected static ParseContext getParseContext(final Element topLevelElement, final URL baseUrl) throws PolicyException {
        final ParseContext parseContext = new ParseContext();
        for (final Element include : getByTagName(topLevelElement, "include")) {
            final String href = XMLUtil.getAttributeValue(include, "href");
            final Element includedPolicy = getPolicy(href, baseUrl);
            parseCommonRules(includedPolicy, parseContext);
            parseContext.rootElementOfIncludedPolicies.add(includedPolicy);
        }
        parseCommonRules(topLevelElement, parseContext);
        for (final Element rootElement : parseContext.rootElementOfIncludedPolicies) {
            parsePolicy(rootElement, parseContext, true);
        }
        parsePolicy(topLevelElement, parseContext);
        return parseContext;
    }
    
    protected static Element getTopLevelElement(final URL baseUrl) throws PolicyException {
        final InputSource source = getSourceFromUrl(baseUrl);
        return getTopLevelElement(source, new Callable<InputSource>() {
            @Override
            public InputSource call() throws PolicyException {
                return Policy.getSourceFromUrl(baseUrl);
            }
        });
    }
    
    @SuppressFBWarnings(value = { "SECURITY" }, justification = "Opening a stream to the provided URL is not a vulnerability because it points to a local JAR file.")
    protected static InputSource getSourceFromUrl(final URL baseUrl) throws PolicyException {
        try {
            InputSource source = resolveEntity(baseUrl.toExternalForm(), baseUrl);
            if (source == null) {
                source = new InputSource(baseUrl.toExternalForm());
                source.setByteStream(baseUrl.openStream());
            }
            else {
                source.setSystemId(baseUrl.toExternalForm());
            }
            return source;
        }
        catch (final SAXException | IOException e) {
            throw new PolicyException(e);
        }
    }
    
    private static Element getTopLevelElement(final InputStream is) throws PolicyException {
        final InputSource source = new InputSource(toByteArrayStream(is));
        return getTopLevelElement(source, new Callable<InputSource>() {
            @Override
            public InputSource call() throws IOException {
                source.getByteStream().reset();
                return source;
            }
        });
    }
    
    protected static Element getTopLevelElement(InputSource source, final Callable<InputSource> getResetSource) throws PolicyException {
        Exception thrownException = null;
        try {
            return getDocumentElementFromSource(source, true);
        }
        catch (final SAXException e) {
            thrownException = e;
            if (!Policy.validateSchema) {
                try {
                    source = getResetSource.call();
                    final Element theElement = getDocumentElementFromSource(source, false);
                    Policy.logger.warn("Invalid AntiSamy policy file: " + e.getMessage());
                    return theElement;
                }
                catch (final Exception e2) {
                    throw new PolicyException(e2);
                }
            }
            throw new PolicyException(e);
        }
        catch (final ParserConfigurationException | IOException e3) {
            thrownException = e3;
            throw new PolicyException(e3);
        }
        finally {
            if (!Policy.validateSchema && thrownException == null) {
                Policy.logger.warn("XML schema validation is disabled for a valid AntiSamy policy. Please reenable policy validation.");
            }
        }
    }
    
    private static InputStream toByteArrayStream(final InputStream in) throws PolicyException {
        byte[] byteArray;
        try (final Reader reader = new InputStreamReader(in, Charset.forName("UTF8"))) {
            final char[] charArray = new char[8192];
            final StringBuilder builder = new StringBuilder();
            int numCharsRead;
            while ((numCharsRead = reader.read(charArray, 0, charArray.length)) != -1) {
                builder.append(charArray, 0, numCharsRead);
            }
            byteArray = builder.toString().getBytes(Charset.forName("UTF8"));
        }
        catch (final IOException ioe) {
            throw new PolicyException(ioe);
        }
        return new ByteArrayInputStream(byteArray);
    }
    
    private static Element getDocumentElementFromSource(final InputSource source, final boolean schemaValidationEnabled) throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        if (schemaValidationEnabled) {
            getPolicySchema();
            dbf.setNamespaceAware(true);
            dbf.setSchema(Policy.schema);
        }
        final DocumentBuilder db = dbf.newDocumentBuilder();
        db.setErrorHandler(new SAXErrorHandler());
        final Document dom = db.parse(source);
        return dom.getDocumentElement();
    }
    
    private static void parseCommonRules(final Element topLevelElement, final ParseContext parseContext) throws PolicyException {
        parseCommonRegExps(getFirstChild(topLevelElement, "common-regexps"), parseContext.commonRegularExpressions);
        parseCommonAttributes(getFirstChild(topLevelElement, "common-attributes"), parseContext.commonAttributes, parseContext.commonRegularExpressions);
    }
    
    private static void parsePolicy(final Element topLevelElement, final ParseContext parseContext) throws PolicyException {
        parsePolicy(topLevelElement, parseContext, false);
    }
    
    private static void parsePolicy(final Element topLevelElement, final ParseContext parseContext, final boolean isLoadedCommonRules) throws PolicyException {
        if (topLevelElement == null) {
            return;
        }
        parseContext.resetParamsWhereLastConfigWins();
        if (!isLoadedCommonRules) {
            parseCommonRegExps(getFirstChild(topLevelElement, "common-regexps"), parseContext.commonRegularExpressions);
            parseCommonAttributes(getFirstChild(topLevelElement, "common-attributes"), parseContext.commonAttributes, parseContext.commonRegularExpressions);
        }
        parseDirectives(getFirstChild(topLevelElement, "directives"), parseContext.directives);
        parseGlobalAttributes(getFirstChild(topLevelElement, "global-tag-attributes"), parseContext.globalAttributes, parseContext.commonAttributes);
        parseDynamicAttributes(getFirstChild(topLevelElement, "dynamic-tag-attributes"), parseContext.dynamicAttributes, parseContext.commonAttributes);
        parseTagRules(getFirstChild(topLevelElement, "tag-rules"), parseContext.commonAttributes, parseContext.commonRegularExpressions, parseContext.tagRules);
        parseCSSRules(getFirstChild(topLevelElement, "css-rules"), parseContext.cssRules, parseContext.commonRegularExpressions);
        parseAllowedEmptyTags(getFirstChild(topLevelElement, "allowed-empty-tags"), parseContext.allowedEmptyTags);
        parseRequireClosingTags(getFirstChild(topLevelElement, "require-closing-tags"), parseContext.requireClosingTags);
        parseRequiresAtKeyFrameCSSSelectors(getFirstChild(topLevelElement, "require-atkeyframe-css-selectors"), parseContext.atKeyFrameCSSSelectors);
        parseURLValidatorTag(getFirstChild(topLevelElement, "url-validator"), parseContext);
    }
    
    @SuppressFBWarnings(value = { "SECURITY" }, justification = "Opening a stream to the provided URL is not a vulnerability because only local file URLs are allowed.")
    private static Element getPolicy(final String href, final URL baseUrl) throws PolicyException {
        Exception thrownException = null;
        try {
            return getDocumentElementByUrl(href, baseUrl, true);
        }
        catch (final SAXException e) {
            thrownException = e;
            if (!Policy.validateSchema) {
                try {
                    final Element theElement = getDocumentElementByUrl(href, baseUrl, false);
                    Policy.logger.warn("Invalid AntiSamy policy file: " + e.getMessage());
                    return theElement;
                }
                catch (final SAXException | ParserConfigurationException | IOException e2) {
                    throw new PolicyException(e2);
                }
            }
            throw new PolicyException(e);
        }
        catch (final ParserConfigurationException | IOException e3) {
            thrownException = e3;
            throw new PolicyException(e3);
        }
        finally {
            if (!Policy.validateSchema && thrownException == null) {
                Policy.logger.warn("XML schema validation is disabled for a valid AntiSamy policy. Please reenable policy validation.");
            }
        }
    }
    
    @SuppressFBWarnings(value = { "SECURITY" }, justification = "Opening a stream to the provided URL is not a vulnerability because only local file URLs are allowed.")
    private static Element getDocumentElementByUrl(final String href, final URL baseUrl, final boolean schemaValidationEnabled) throws IOException, ParserConfigurationException, SAXException {
        InputSource source = null;
        if (href != null && baseUrl != null) {
            verifyLocalUrl(baseUrl);
            try {
                final URL url = new URL(baseUrl, href);
                final String logMsg = "Attempting to load AntiSamy policy from URL: " + url.toString();
                if (Policy.validateSchema) {
                    Policy.logger.info(logMsg);
                }
                else {
                    Policy.logger.warn(logMsg);
                }
                source = new InputSource(url.openStream());
                source.setSystemId(href);
            }
            catch (final MalformedURLException | FileNotFoundException e) {
                try {
                    final String absURL = URIUtils.resolveAsString(href, baseUrl.toString());
                    final URL url = new URL(absURL);
                    source = new InputSource(url.openStream());
                    source.setSystemId(href);
                }
                catch (final MalformedURLException ex) {}
            }
        }
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        if (schemaValidationEnabled) {
            getPolicySchema();
            dbf.setNamespaceAware(true);
            dbf.setSchema(Policy.schema);
        }
        final DocumentBuilder db = dbf.newDocumentBuilder();
        db.setErrorHandler(new SAXErrorHandler());
        if (source != null) {
            final Document dom = db.parse(source);
            return dom.getDocumentElement();
        }
        return null;
    }
    
    public Policy cloneWithDirective(final String name, final String value) {
        final Map<String, String> directives = new HashMap<String, String>(this.directives);
        directives.put(name, value);
        return new InternalPolicy(this, Collections.unmodifiableMap((Map<? extends String, ? extends String>)directives), this.tagRules);
    }
    
    private static boolean isRemoveConfiguration(final Element ele) {
        return "true".equals(XMLUtil.getAttributeValue(ele, "remove"));
    }
    
    private static void parseDirectives(final Element root, final Map<String, String> directives) {
        for (final Element ele : getByTagName(root, "directive")) {
            final String name = XMLUtil.getAttributeValue(ele, "name");
            if (isRemoveConfiguration(ele)) {
                directives.remove(name);
            }
            else {
                final String value = XMLUtil.getAttributeValue(ele, "value");
                directives.put(name, value);
            }
        }
    }
    
    private static void parseAllowedEmptyTags(final Element allowedEmptyTagsListNode, final List<String> allowedEmptyTags) throws PolicyException {
        if (allowedEmptyTagsListNode != null) {
            for (final Element literalNode : getGrandChildrenByTagName(allowedEmptyTagsListNode, "literal-list", "literal")) {
                final String value = XMLUtil.getAttributeValue(literalNode, "value");
                if (value != null && value.length() > 0) {
                    allowedEmptyTags.add(value);
                }
            }
        }
        else {
            allowedEmptyTags.addAll(Constants.defaultAllowedEmptyTags);
        }
    }
    
    private static void parseRequireClosingTags(final Element requireClosingTagsListNode, final List<String> requireClosingTags) throws PolicyException {
        if (requireClosingTagsListNode != null) {
            for (final Element literalNode : getGrandChildrenByTagName(requireClosingTagsListNode, "literal-list", "literal")) {
                final String value = XMLUtil.getAttributeValue(literalNode, "value");
                if (value != null && value.length() > 0) {
                    requireClosingTags.add(value);
                }
            }
        }
        else {
            requireClosingTags.addAll(Constants.defaultRequireClosingTags);
        }
    }
    
    private static void parseRequiresAtKeyFrameCSSSelectors(final Element requiresAtKeyFrameCSSSelectorNode, final List<String> requiresAtKeyFrameCSSSelectors) {
        if (requiresAtKeyFrameCSSSelectorNode != null) {
            requiresAtKeyFrameCSSSelectors.clear();
            for (final Element literalNode : getGrandChildrenByTagName(requiresAtKeyFrameCSSSelectorNode, "literal-list", "literal")) {
                final String value = XMLUtil.getAttributeValue(literalNode, "value");
                if (value != null && value.length() > 0) {
                    if (isRemoveConfiguration(literalNode)) {
                        requiresAtKeyFrameCSSSelectors.remove(value);
                    }
                    else {
                        if (requiresAtKeyFrameCSSSelectors.contains(value)) {
                            continue;
                        }
                        requiresAtKeyFrameCSSSelectors.add(value);
                    }
                }
            }
        }
    }
    
    private static void parseGlobalAttributes(final Element root, final Map<String, Attribute> globalAttributes1, final Map<String, Attribute> commonAttributes) throws PolicyException {
        for (final Element ele : getByTagName(root, "attribute")) {
            final String name = XMLUtil.getAttributeValue(ele, "name");
            if (isRemoveConfiguration(ele)) {
                globalAttributes1.remove(name.toLowerCase());
            }
            else {
                final Attribute toAdd = commonAttributes.get(name.toLowerCase());
                if (toAdd == null) {
                    throw new PolicyException("Global attribute '" + name + "' was not defined in <common-attributes>");
                }
                globalAttributes1.put(name.toLowerCase(), toAdd);
            }
        }
    }
    
    private static void parseDynamicAttributes(final Element root, final Map<String, Attribute> dynamicAttributes, final Map<String, Attribute> commonAttributes) throws PolicyException {
        for (final Element ele : getByTagName(root, "attribute")) {
            final String name = XMLUtil.getAttributeValue(ele, "name");
            final Attribute toAdd = commonAttributes.get(name.toLowerCase());
            if (toAdd == null) {
                throw new PolicyException("Dynamic attribute '" + name + "' was not defined in <common-attributes>");
            }
            final String attrName = name.toLowerCase().substring(0, name.length() - 1);
            dynamicAttributes.put(attrName, toAdd);
        }
    }
    
    private static void parseCommonRegExps(final Element root, final Map<String, AntiSamyPattern> commonRegularExpressions1) {
        for (final Element ele : getByTagName(root, "regexp")) {
            final String name = XMLUtil.getAttributeValue(ele, "name");
            if (isRemoveConfiguration(ele)) {
                commonRegularExpressions1.remove(name);
            }
            else {
                final Pattern pattern = Pattern.compile(XMLUtil.getAttributeValue(ele, "value"), 32);
                commonRegularExpressions1.put(name, new AntiSamyPattern(pattern));
            }
        }
    }
    
    private static void parseCommonAttributes(final Element root, final Map<String, Attribute> commonAttributes1, final Map<String, AntiSamyPattern> commonRegularExpressions1) {
        for (final Element ele : getByTagName(root, "attribute")) {
            final String onInvalid = XMLUtil.getAttributeValue(ele, "onInvalid");
            final String name = XMLUtil.getAttributeValue(ele, "name");
            if (isRemoveConfiguration(ele)) {
                commonAttributes1.remove(name.toLowerCase());
            }
            else {
                final List<Pattern> allowedRegexps = getAllowedRegexps(commonRegularExpressions1, ele);
                final List<String> allowedValues = getAllowedLiterals(ele);
                String onInvalidStr;
                if (onInvalid != null && onInvalid.length() > 0) {
                    onInvalidStr = onInvalid;
                }
                else {
                    onInvalidStr = "removeAttribute";
                }
                final String description = XMLUtil.getAttributeValue(ele, "description");
                final Attribute attribute = new Attribute(XMLUtil.getAttributeValue(ele, "name"), allowedRegexps, allowedValues, onInvalidStr, description);
                commonAttributes1.put(name.toLowerCase(), attribute);
            }
        }
    }
    
    private static List<String> getAllowedLiterals(final Element ele) {
        final List<String> allowedValues = new ArrayList<String>();
        for (final Element literalNode : getGrandChildrenByTagName(ele, "literal-list", "literal")) {
            final String value = XMLUtil.getAttributeValue(literalNode, "value");
            if (value != null && value.length() > 0) {
                allowedValues.add(value);
            }
            else {
                if (literalNode.getNodeValue() == null) {
                    continue;
                }
                allowedValues.add(literalNode.getNodeValue());
            }
        }
        return allowedValues;
    }
    
    private static List<Pattern> getAllowedRegexps(final Map<String, AntiSamyPattern> commonRegularExpressions1, final Element ele) {
        final List<Pattern> allowedRegExp = new ArrayList<Pattern>();
        for (final Element regExpNode : getGrandChildrenByTagName(ele, "regexp-list", "regexp")) {
            final String regExpName = XMLUtil.getAttributeValue(regExpNode, "name");
            final String value = XMLUtil.getAttributeValue(regExpNode, "value");
            if (regExpName != null && regExpName.length() > 0) {
                allowedRegExp.add(commonRegularExpressions1.get(regExpName).getPattern());
            }
            else {
                allowedRegExp.add(Pattern.compile(value, 32));
            }
        }
        return allowedRegExp;
    }
    
    private static List<Pattern> getAllowedRegexps2(final Map<String, AntiSamyPattern> commonRegularExpressions1, final Element attributeNode, final String tagName) throws PolicyException {
        final List<Pattern> allowedRegexps = new ArrayList<Pattern>();
        for (final Element regExpNode : getGrandChildrenByTagName(attributeNode, "regexp-list", "regexp")) {
            final String regExpName = XMLUtil.getAttributeValue(regExpNode, "name");
            final String value = XMLUtil.getAttributeValue(regExpNode, "value");
            if (regExpName != null && regExpName.length() > 0) {
                final AntiSamyPattern pattern = commonRegularExpressions1.get(regExpName);
                if (pattern == null) {
                    throw new PolicyException("Regular expression '" + regExpName + "' was referenced as a common regexp in definition of '" + tagName + "', but does not exist in <common-regexp>");
                }
                allowedRegexps.add(pattern.getPattern());
            }
            else {
                if (value == null || value.length() <= 0) {
                    continue;
                }
                allowedRegexps.add(Pattern.compile(value, 32));
            }
        }
        return allowedRegexps;
    }
    
    private static List<Pattern> getAllowedRegexp3(final Map<String, AntiSamyPattern> commonRegularExpressions1, final Element ele, final String name) throws PolicyException {
        final List<Pattern> allowedRegExp = new ArrayList<Pattern>();
        for (final Element regExpNode : getGrandChildrenByTagName(ele, "regexp-list", "regexp")) {
            final String regExpName = XMLUtil.getAttributeValue(regExpNode, "name");
            final String value = XMLUtil.getAttributeValue(regExpNode, "value");
            final AntiSamyPattern pattern = commonRegularExpressions1.get(regExpName);
            if (pattern != null) {
                allowedRegExp.add(pattern.getPattern());
            }
            else {
                if (value == null) {
                    throw new PolicyException("Regular expression '" + regExpName + "' was referenced as a common regexp in definition of '" + name + "', but does not exist in <common-regexp>");
                }
                allowedRegExp.add(Pattern.compile(value, 32));
            }
        }
        return allowedRegExp;
    }
    
    private static void parseTagRules(final Element root, final Map<String, Attribute> commonAttributes1, final Map<String, AntiSamyPattern> commonRegularExpressions1, final Map<String, Tag> tagRules1) throws PolicyException {
        if (root == null) {
            return;
        }
        for (final Element tagNode : getByTagName(root, "tag")) {
            final String name = XMLUtil.getAttributeValue(tagNode, "name");
            final String action = XMLUtil.getAttributeValue(tagNode, "action");
            if (isRemoveConfiguration(tagNode)) {
                tagRules1.remove(name.toLowerCase());
            }
            else {
                final NodeList attributeList = tagNode.getElementsByTagName("attribute");
                final Map<String, Attribute> tagAttributes = getTagAttributes(commonAttributes1, commonRegularExpressions1, attributeList, name);
                final List<String> mandatoryTagAttributes = getMandatoryTagAttributes(attributeList);
                final NodeList insertAttributeList = tagNode.getElementsByTagName("insert-attribute");
                List<Attribute> insertAttributes = null;
                if (insertAttributeList.getLength() > 0) {
                    insertAttributes = getInsertAttributes(insertAttributeList);
                }
                final Tag tag = new Tag(name, tagAttributes, mandatoryTagAttributes, action, insertAttributes);
                tagRules1.put(name.toLowerCase(), tag);
            }
        }
    }
    
    private static Map<String, Attribute> getTagAttributes(final Map<String, Attribute> commonAttributes1, final Map<String, AntiSamyPattern> commonRegularExpressions1, final NodeList attributeList, final String tagName) throws PolicyException {
        final Map<String, Attribute> tagAttributes = new HashMap<String, Attribute>();
        for (int j = 0; j < attributeList.getLength(); ++j) {
            final Element attributeNode = (Element)attributeList.item(j);
            final String attrName = XMLUtil.getAttributeValue(attributeNode, "name").toLowerCase();
            if (!attributeNode.hasChildNodes()) {
                final Attribute attribute = commonAttributes1.get(attrName);
                if (attribute == null) {
                    throw new PolicyException("Attribute '" + XMLUtil.getAttributeValue(attributeNode, "name") + "' was referenced as a common attribute in definition of '" + tagName + "', but does not exist in <common-attributes>");
                }
                final String onInvalid = XMLUtil.getAttributeValue(attributeNode, "onInvalid");
                final String description = XMLUtil.getAttributeValue(attributeNode, "description");
                final Attribute changed = attribute.mutate(onInvalid, description);
                commonAttributes1.put(attrName, changed);
                tagAttributes.put(attrName, changed);
            }
            else {
                final List<Pattern> allowedRegexps2 = getAllowedRegexps2(commonRegularExpressions1, attributeNode, tagName);
                final List<String> allowedValues2 = getAllowedLiterals(attributeNode);
                final String onInvalid2 = XMLUtil.getAttributeValue(attributeNode, "onInvalid");
                final String description2 = XMLUtil.getAttributeValue(attributeNode, "description");
                final Attribute attribute2 = new Attribute(XMLUtil.getAttributeValue(attributeNode, "name"), allowedRegexps2, allowedValues2, onInvalid2, description2);
                tagAttributes.put(attrName, attribute2);
            }
        }
        return tagAttributes;
    }
    
    private static List<String> getMandatoryTagAttributes(final NodeList attributeList) throws PolicyException {
        List<String> mandatoryTagAttributes = null;
        for (int j = 0; j < attributeList.getLength(); ++j) {
            final Element attributeNode = (Element)attributeList.item(j);
            final String attrName = XMLUtil.getAttributeValue(attributeNode, "name").toLowerCase();
            final boolean isMandatory = "true".equalsIgnoreCase(XMLUtil.getAttributeValue(attributeNode, "mandatory"));
            if (isMandatory) {
                if (mandatoryTagAttributes == null) {
                    mandatoryTagAttributes = new ArrayList<String>();
                }
                mandatoryTagAttributes.add(attrName);
            }
        }
        return mandatoryTagAttributes;
    }
    
    private static List<Attribute> getInsertAttributes(final NodeList insertAttributeList) throws PolicyException {
        final List<Attribute> insertAttributes = new ArrayList<Attribute>();
        for (int j = 0; j < insertAttributeList.getLength(); ++j) {
            final Element insertAttributeNode = (Element)insertAttributeList.item(j);
            final String insertAttrName = XMLUtil.getAttributeValue(insertAttributeNode, "name");
            final List<String> insertAttrValues = getAllowedLiterals(insertAttributeNode);
            Map<String, List<String>> criteriaAttributes = null;
            final Iterable<Element> criteriaAttributesEleList = getGrandChildrenByTagName(insertAttributeNode, "criteria-attributes", "criteria-attribute");
            if (criteriaAttributesEleList.iterator().hasNext()) {
                criteriaAttributes = new HashMap<String, List<String>>();
            }
            for (final Element criteriaAttributeEle : criteriaAttributesEleList) {
                final String criteriaAttrName = XMLUtil.getAttributeValue(criteriaAttributeEle, "name");
                final List<String> criteriaAttrValue = getAllowedLiterals(criteriaAttributeEle);
                criteriaAttributes.put(criteriaAttrName, criteriaAttrValue);
            }
            final Attribute insertAttribute = new Attribute(insertAttrName, insertAttrValues, criteriaAttributes);
            insertAttributes.add(insertAttribute);
        }
        return insertAttributes;
    }
    
    private static void parseCSSRules(final Element root, final Map<String, Property> cssRules1, final Map<String, AntiSamyPattern> commonRegularExpressions1) throws PolicyException {
        for (final Element ele : getByTagName(root, "property")) {
            final String name = XMLUtil.getAttributeValue(ele, "name");
            if (isRemoveConfiguration(ele)) {
                cssRules1.remove(name.toLowerCase());
            }
            else {
                final String description = XMLUtil.getAttributeValue(ele, "description");
                final List<Pattern> allowedRegexp3 = getAllowedRegexp3(commonRegularExpressions1, ele, name);
                final List<String> allowedValue = new ArrayList<String>();
                for (final Element literalNode : getGrandChildrenByTagName(ele, "literal-list", "literal")) {
                    allowedValue.add(XMLUtil.getAttributeValue(literalNode, "value"));
                }
                final List<String> shortHandRefs = new ArrayList<String>();
                for (final Element shorthandNode : getGrandChildrenByTagName(ele, "shorthand-list", "shorthand")) {
                    shortHandRefs.add(XMLUtil.getAttributeValue(shorthandNode, "name"));
                }
                final List<String> category_list = new ArrayList<String>();
                for (final Element category : getGrandChildrenByTagName(ele, "category-list", "category")) {
                    category_list.add(XMLUtil.getAttributeValue(category, "value"));
                }
                final String onInvalid = XMLUtil.getAttributeValue(ele, "onInvalid");
                String onInvalidStr;
                if (onInvalid != null && onInvalid.length() > 0) {
                    onInvalidStr = onInvalid;
                }
                else {
                    onInvalidStr = "removeAttribute";
                }
                final Property property = new Property(name, allowedRegexp3, allowedValue, shortHandRefs, category_list, description, onInvalidStr);
                cssRules1.put(name.toLowerCase(), property);
            }
        }
    }
    
    private static void parseURLValidatorTag(final Element urlValidatorElement, final ParseContext parseContext) {
        if (urlValidatorElement != null) {
            final URLValidatorRule urlValidatorRule = new URLValidatorRule(urlValidatorElement);
            final URLValidatorAPI urlValidator = new URLValidatorAPI(false, urlValidatorRule.isAllowRelativeURL());
            parseContext.urlValidator = urlValidator;
            final URLValidatorAPI urlvalidator = urlValidator;
            urlvalidator.setMode(urlValidatorRule.getMode());
            urlvalidator.setMaxlength(urlValidatorRule.getMaxLen());
            urlvalidator.setDataURIMaxLength(urlValidatorRule.getDatauriMaxLen());
            if (urlValidatorRule.getSchemeMap() != null) {
                for (final Scheme scheme : urlValidatorRule.getSchemeMap().values()) {
                    urlvalidator.addScheme(scheme);
                }
            }
            if (urlValidatorRule.getCustomSchemeMap() != null) {
                for (final CustomScheme customScheme : urlValidatorRule.getCustomSchemeMap().values()) {
                    urlvalidator.addCustomScheme(customScheme);
                }
            }
        }
    }
    
    public Attribute getGlobalAttributeByName(final String name) {
        return this.globalAttributes.get(name.toLowerCase());
    }
    
    public Attribute getDynamicAttributeByName(final String name) {
        Attribute dynamicAttribute = null;
        final Set<Map.Entry<String, Attribute>> entries = this.dynamicAttributes.entrySet();
        for (final Map.Entry<String, Attribute> entry : entries) {
            if (name.startsWith(entry.getKey())) {
                dynamicAttribute = entry.getValue();
                break;
            }
        }
        return dynamicAttribute;
    }
    
    public TagMatcher getAllowedEmptyTags() {
        return this.allowedEmptyTagsMatcher;
    }
    
    public TagMatcher getRequiresClosingTags() {
        return this.requiresClosingTagsMatcher;
    }
    
    public List<String> getURLValidation_Attributes() {
        return this.urlvalidation_attributes;
    }
    
    public URLValidatorAPI getUrlValidator() {
        return this.urlValidator;
    }
    
    public List<String> getAtKeyFrameCSSSelectorsList() {
        return this.atKeyFrameCSSSelectors;
    }
    
    public String getDirective(final String name) {
        return this.directives.get(name);
    }
    
    @SuppressFBWarnings(value = { "SECURITY" }, justification = "Opening a stream to the provided URL is not a vulnerability because only local file URLs are allowed.")
    public static InputSource resolveEntity(final String systemId, final URL baseUrl) throws IOException, SAXException {
        if (systemId != null && baseUrl != null) {
            verifyLocalUrl(baseUrl);
            try {
                final URL url = new URL(baseUrl, systemId);
                final InputSource source = new InputSource(url.openStream());
                source.setSystemId(systemId);
                return source;
            }
            catch (final MalformedURLException | FileNotFoundException e) {
                try {
                    final String absURL = URIUtils.resolveAsString(systemId, baseUrl.toString());
                    final URL url = new URL(absURL);
                    final InputSource source = new InputSource(url.openStream());
                    source.setSystemId(systemId);
                    return source;
                }
                catch (final MalformedURLException ex) {
                    return null;
                }
            }
        }
        return null;
    }
    
    private static void verifyLocalUrl(final URL url) throws MalformedURLException {
        final String protocol = url.getProtocol();
        switch (protocol) {
            case "file":
            case "jar": {
                return;
            }
            default: {
                throw new MalformedURLException("Only local files can be accessed with a policy URL. Illegal value supplied was: " + url);
            }
        }
    }
    
    private static Element getFirstChild(final Element element, final String tagName) {
        if (element == null) {
            return null;
        }
        final NodeList elementsByTagName = element.getElementsByTagName(tagName);
        if (elementsByTagName != null && elementsByTagName.getLength() > 0) {
            return (Element)elementsByTagName.item(0);
        }
        return null;
    }
    
    private static Iterable<Element> getGrandChildrenByTagName(final Element parent, final String immediateChildName, final String subChild) {
        final NodeList elementsByTagName = parent.getElementsByTagName(immediateChildName);
        if (elementsByTagName.getLength() == 0) {
            return (Iterable<Element>)Collections.emptyList();
        }
        final Element regExpListNode = (Element)elementsByTagName.item(0);
        return getByTagName(regExpListNode, subChild);
    }
    
    private static Iterable<Element> getByTagName(final Element parent, final String tagName) {
        if (parent == null) {
            return (Iterable<Element>)Collections.emptyList();
        }
        final NodeList nodes = parent.getElementsByTagName(tagName);
        return new Iterable<Element>() {
            @Override
            public Iterator<Element> iterator() {
                return new Iterator<Element>() {
                    int pos = 0;
                    int len = nodes.getLength();
                    
                    @Override
                    public boolean hasNext() {
                        return this.pos < this.len;
                    }
                    
                    @Override
                    public Element next() {
                        return (Element)nodes.item(this.pos++);
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Cant remove");
                    }
                };
            }
        };
    }
    
    public AntiSamyPattern getCommonRegularExpressions(final String name) {
        return this.commonRegularExpressions.get(name);
    }
    
    private static void getPolicySchema() throws SAXException {
        if (Policy.schema == null) {
            final InputStream schemaStream = Policy.class.getClassLoader().getResourceAsStream("antisamy.xsd");
            final Source schemaSource = new StreamSource(schemaStream);
            Policy.schema = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema").newSchema(schemaSource);
        }
    }
    
    static {
        logger = LoggerFactory.getLogger((Class)Policy.class);
        ANYTHING_REGEXP = Pattern.compile(".*", 32);
        Policy.schema = null;
        Policy.validateSchema = true;
        loadValidateSchemaProperty();
    }
    
    protected static class ParseContext
    {
        Map<String, AntiSamyPattern> commonRegularExpressions;
        Map<String, Attribute> commonAttributes;
        Map<String, Tag> tagRules;
        Map<String, Property> cssRules;
        Map<String, String> directives;
        Map<String, Attribute> globalAttributes;
        Map<String, Attribute> dynamicAttributes;
        List<String> allowedEmptyTags;
        List<String> requireClosingTags;
        List<String> atKeyFrameCSSSelectors;
        List<Element> rootElementOfIncludedPolicies;
        URLValidatorAPI urlValidator;
        
        protected ParseContext() {
            this.commonRegularExpressions = new HashMap<String, AntiSamyPattern>();
            this.commonAttributes = new HashMap<String, Attribute>();
            this.tagRules = new HashMap<String, Tag>();
            this.cssRules = new HashMap<String, Property>();
            this.directives = new HashMap<String, String>();
            this.globalAttributes = new HashMap<String, Attribute>();
            this.dynamicAttributes = new HashMap<String, Attribute>();
            this.allowedEmptyTags = new ArrayList<String>();
            this.requireClosingTags = new ArrayList<String>();
            this.atKeyFrameCSSSelectors = new ArrayList<String>();
            this.rootElementOfIncludedPolicies = new ArrayList<Element>();
        }
        
        public void resetParamsWhereLastConfigWins() {
            this.allowedEmptyTags.clear();
            this.requireClosingTags.clear();
        }
    }
    
    static class SAXErrorHandler implements ErrorHandler
    {
        @Override
        public void error(final SAXParseException arg0) throws SAXException {
            throw arg0;
        }
        
        @Override
        public void fatalError(final SAXParseException arg0) throws SAXException {
            throw arg0;
        }
        
        @Override
        public void warning(final SAXParseException arg0) throws SAXException {
            throw arg0;
        }
    }
}
