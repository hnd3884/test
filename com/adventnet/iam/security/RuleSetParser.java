package com.adventnet.iam.security;

import java.util.Collection;
import com.adventnet.iam.xss.XSSUtil;
import com.zoho.security.rule.ExceptionRule;
import java.util.ArrayList;
import com.zoho.security.validator.url.URLValidatorRule;
import com.zoho.security.cache.CacheConfiguration;
import com.zoho.security.validator.zip.ZipSanitizerRule;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.Properties;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import java.io.File;
import java.util.logging.Logger;

public class RuleSetParser
{
    private static final Logger logger;
    
    public static void initSecurityRules(final SecurityFilterProperties filterProps, final File file) throws Exception {
        final Document document = parseXmlFile(file);
        final Element root = document.getDocumentElement();
        initProperties(filterProps, root);
        initRegexPattern(filterProps, root, file.getName());
        initParamGroups(filterProps, root);
        initURLRuleSet(filterProps, root, file);
        parseXssPattern(filterProps, root);
        initContentTypes(filterProps, root);
        initJSONKeyGroups(filterProps, root);
        initJSONTemplateRule(filterProps, root, file);
        initTemplateRule(filterProps, root, file);
        initExtraParamRule(filterProps, root);
        initExtraJSONKeyRule(filterProps, root);
        initDefaultParams(root);
        initProxyURLs(filterProps, root);
        initProxyPolicies(filterProps, root, file.getName());
        initInternalRequestHeaders(filterProps, root, file.getName());
        initDefaultRequestHeaders(filterProps, root);
        initSafeResponseHeaders(filterProps, root);
        initDefaultResponseHeaders(filterProps, root);
        initURLValidatorRule(filterProps, root, file);
        initXMLSchema(filterProps, root, file);
        initCacheConfigurations(root, filterProps);
        initZipSanitizerRule(filterProps, root, file);
        initThrottlesRule(filterProps, root, file);
        initPiiDetectorRule(filterProps, root, file);
        initContentTypesRule(filterProps, root, file);
        initExceptionsRule(filterProps, root, file);
    }
    
    public static void initWAFProperties(final SecurityFilterProperties filterProps, final File file) throws Exception {
        final Document document = parseXmlFile(file);
        final Element root = document.getDocumentElement();
        final NodeList nodelist = root.getElementsByTagName("property");
        final Properties props = new Properties();
        for (int i = 0; i < nodelist.getLength(); ++i) {
            final Node node = nodelist.item(i);
            if (node.getNodeType() == 1) {
                final Element element = (Element)nodelist.item(i);
                final String name = element.getAttribute("name");
                final String value = element.getAttribute("value");
                props.setProperty(name, value);
            }
        }
        filterProps.addProperties(props, "WAF");
        filterProps.initSecurityProperties();
    }
    
    private static void initThrottlesRule(final SecurityFilterProperties securityFilterProperties, final Element securityElement, final File securityXMLFile) {
        final List<Element> throttlesList = getChildNodesByTagName(securityElement, "throttles");
        if (!SecurityUtil.isValid(throttlesList)) {
            return;
        }
        for (final Element throttles : throttlesList) {
            try {
                securityFilterProperties.addCommonThrottlesRule(new ThrottlesRule(throttles));
            }
            catch (final Exception e) {
                RuleSetParser.logger.log(Level.SEVERE, "SecurityMisConfiguration in parsing common <throttles> element. File: {0}", new Object[] { securityXMLFile.getName() });
                throw e;
            }
        }
    }
    
    private static void initZipSanitizerRule(final SecurityFilterProperties filterProps, final Element root, final File file) {
        if (getFirstChildNodeByTagName(root, TagName.ZIP_SANITIZER.value) != null) {
            throw new RuntimeException("<zip-sanitizer> element's should be enclosed with <zip-sanitizers> element in file :" + file.getName());
        }
        for (final Element zipsanitizersElement : getChildNodesByTagName(root, TagName.ZIP_SANITIZERS.value)) {
            for (final Element zipsanitizerElement : getChildNodesByTagName(zipsanitizersElement, TagName.ZIP_SANITIZER.value)) {
                final ZipSanitizerRule zipRule = new ZipSanitizerRule(zipsanitizerElement);
                filterProps.addZipSanitizerRule(zipRule);
            }
        }
    }
    
    private static void initCacheConfigurations(final Element root, final SecurityFilterProperties filterProps) {
        List<Element> elements = getChildNodesByTagName(root, TagName.CACHE_CONFIGURATIONS.value);
        if (elements.size() == 0) {
            return;
        }
        if (elements.size() > 1) {
            throw new RuntimeException("Multiple <cache-configurations> elements are not allowed");
        }
        elements = getChildNodesByTagName(elements.get(0), TagName.CACHE_CONFIGURATION.value);
        CacheConfiguration cacheConfiguration = null;
        for (final Element element : elements) {
            cacheConfiguration = new CacheConfiguration(element);
            filterProps.addCacheConfiguration(cacheConfiguration);
        }
    }
    
    private static void initXMLSchema(final SecurityFilterProperties filterProps, final Element root, final File file) {
        final NodeList nodelist = root.getElementsByTagName(TagName.XML_SCHEMA.value);
        for (int i = 0; i < nodelist.getLength(); ++i) {
            final Node node = nodelist.item(i);
            if (node.getNodeType() == 1) {
                final Element xmlSchemaElement = (Element)nodelist.item(i);
                final XMLSchemaRule schemaRule = new XMLSchemaRule(xmlSchemaElement);
                filterProps.addXMLSchemaRule(schemaRule.getName(), schemaRule);
            }
        }
    }
    
    private static void initURLValidatorRule(final SecurityFilterProperties filterProps, final Element root, final File file) {
        if (getChildNodesByTagName(root, TagName.URL_VALIDATOR.value).size() > 0) {
            throw new RuntimeException("<url-validator> element's should be enclosed with <url-validators> element in file :" + file.getName());
        }
        final List<Element> urlValidatorsList = getChildNodesByTagName(root, TagName.URL_VALIDATORS.value);
        for (final Element urlValidatorsElement : urlValidatorsList) {
            final URLValidatorRule parentUrlValidatorsRule = new URLValidatorRule(urlValidatorsElement);
            final List<Element> urlvalidatorList = getChildNodesByTagName(urlValidatorsElement, TagName.URL_VALIDATOR.value);
            for (final Element urlValidatorElement : urlvalidatorList) {
                filterProps.addURLValidatorRule(urlValidatorElement, parentUrlValidatorsRule);
            }
        }
    }
    
    private static void initSafeResponseHeaders(final SecurityFilterProperties filterProps, final Element root) {
        initHeaders(filterProps, root, "secure-headers");
        initHeaders(filterProps, root, "download-headers");
        initHeaders(filterProps, root, "json-headers");
        initHeaders(filterProps, root, "cache-headers");
        initHeaders(filterProps, root, "connection-headers");
        initHeaders(filterProps, root, "error-page-headers");
    }
    
    private static void initHeaders(final SecurityFilterProperties filterProps, final Element root, final String tagName) {
        final NodeList nodeList = root.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0 && nodeList.item(0).getNodeType() == 1) {
            final Element rElement = (Element)nodeList.item(0);
            final NodeList headerList = rElement.getElementsByTagName("header");
            final List<ResponseHeaderRule> responseHeaderRules = new ArrayList<ResponseHeaderRule>();
            for (int i = 0; i < headerList.getLength(); ++i) {
                final Node node = headerList.item(i);
                if (node.getNodeType() == 1) {
                    final Element element = (Element)node;
                    responseHeaderRules.add(new ResponseHeaderRule(element));
                }
            }
            filterProps.addSafeResponseHeaderRule(tagName, responseHeaderRules);
        }
    }
    
    private static void initDefaultResponseHeaders(final SecurityFilterProperties filterProps, final Element root) {
        final NodeList nodeList = root.getElementsByTagName("default-response-headers");
        if (nodeList.getLength() > 0 && nodeList.item(0).getNodeType() == 1) {
            final Element rElement = (Element)nodeList.item(0);
            final List<Element> headerList = getChildNodesByTagName(rElement, "header");
            if (headerList.size() > 0) {
                for (int i = 0; i < headerList.size(); ++i) {
                    if (headerList.get(i).getNodeType() == 1) {
                        final Element element = headerList.get(i);
                        if (!SecurityUtil.isValid(element.getAttribute("name"))) {
                            throw new RuntimeException("Default responseHeaderRule with empty responseheader name not allowed  \n");
                        }
                        final String headerName = element.getAttribute("name").trim();
                        final String headerValue = element.getAttribute("value").trim();
                        if (headerName.equals("Access-Control-Allow-Origin")) {
                            if (headerValue.equals("*")) {
                                RuleSetParser.logger.log(Level.SEVERE, "Default Response header must not set \"Access-Control-Allow-Origin\"=\"*\" , try to opt other options to validate the Origin");
                                throw new IAMSecurityException("INVALID_CONFIGURATION");
                            }
                            filterProps.corsConfigType = ActionRule.CORSConfigType.STATIC;
                        }
                        filterProps.addDefaultResponseHeaderRule(new ResponseHeaderRule(element));
                    }
                }
            }
            final String disableSafeheaders = rElement.getAttribute("disable-safe-headers");
            filterProps.disableDefaultSafeHeaders(disableSafeheaders);
            if ("true".equalsIgnoreCase(rElement.getAttribute("dynamic-cors-headers"))) {
                filterProps.corsConfigType = ActionRule.CORSConfigType.DYNAMIC;
            }
        }
    }
    
    private static void initInternalRequestHeaders(final SecurityFilterProperties filterProps, final Element root, final String fileName) {
        final Element internalElement = getFirstChildNodeByTagName(root, TagName.INTERNAL_REQUEST_HEADER_TAG.getValue());
        if (internalElement != null) {
            final List<Element> headerList = getChildNodesByTagName(internalElement, "header");
            for (final Element headerElement : headerList) {
                final String headerName = headerElement.getAttribute("name").trim();
                if (!SecurityUtil.isValid(headerName)) {
                    throw new RuntimeException("REQUEST-HEADER : <header> with empty(\" \") or \"null\"  as a requestHeader name not allowed \n");
                }
                final HeaderRule headerRule = new GeneralRequestHeaderRule(headerElement);
                filterProps.getInternalRequestHeaders().put(headerName, headerRule);
                final ParameterRule hrRule = headerRule.getHeaderRule();
                if (!hrRule.isMaskingEnabled()) {
                    continue;
                }
                if (hrRule.isMaskingRequiredPartially()) {
                    filterProps.getPartialMaskingInternalReqHeaderRules().add(hrRule);
                }
                else {
                    filterProps.getInternalSecretRequestHeaders().add(headerName);
                }
            }
            final Element cookiesElement = getFirstChildNodeByTagName(internalElement, "cookies");
            if (cookiesElement != null) {
                filterProps.setInternalCookieRule(new CookieRequestHeaderRule(cookiesElement));
            }
        }
    }
    
    private static void initDefaultRequestHeaders(final SecurityFilterProperties filterProps, final Element root) {
        final Element rElement = getFirstChildNodeByTagName(root, TagName.DEFAULT_REQUEST_HEADER_TAG.getValue());
        if (rElement != null) {
            initDefaultHeadersRule(rElement, filterProps);
        }
    }
    
    static void initDefaultHeadersRule(final Element element, final SecurityFilterProperties filterProps) {
        final Element headersElement = getFirstChildNodeByTagName(element, "headers");
        if (headersElement != null) {
            final List<Element> headerList = getChildNodesByTagName(headersElement, "header");
            for (final Element headerElement : headerList) {
                final String headerName = headerElement.getAttribute("name").trim();
                if (!SecurityUtil.isValid(headerName)) {
                    throw new RuntimeException("REQUEST-HEADER : <header> with empty(\" \") or \"null\"  as a requestHeader name not allowed \n");
                }
                if ("cookie".equalsIgnoreCase(headerName) || "user-agent".equalsIgnoreCase(headerName)) {
                    throw new RuntimeException("REQUEST-HEADER : \"cookie\" or \"user-agent\" headers shouldn't be defined under <header> section , they must be defined under specific tags of <cookies> or <user-agent> ");
                }
                final HeaderRule headerRule = new GeneralRequestHeaderRule(headerElement);
                if (headerRule.getHeaderRule().isParamNameInRegex()) {
                    filterProps.getDefaultRequestHeadersRuleWithRegexName().put(headerName, headerRule);
                }
                else {
                    filterProps.getDefaultRequestHeadersRuleWithStrictName().put(headerName, headerRule);
                    if (!headerRule.getHeaderRule().isSecret()) {
                        continue;
                    }
                    filterProps.getDefaultSecretRequestHeaders().add(headerName);
                }
            }
            final Element cookiesElement = getFirstChildNodeByTagName(headersElement, "cookies");
            if (cookiesElement != null) {
                filterProps.setDefaultCookieRule(new CookieRequestHeaderRule(cookiesElement));
            }
            final Element userAgentsElement = getFirstChildNodeByTagName(headersElement, "user-agent");
            if (userAgentsElement != null) {
                filterProps.setDefaultUserAgentRule(new UserAgentRequestHeaderRule(userAgentsElement));
            }
        }
    }
    
    private static void initExtraParamRule(final SecurityFilterProperties filterProps, final Element root) {
        final List<Element> nodeList = getChildNodesByTagName(root, "extraparam");
        if (nodeList.size() > 0) {
            final Node node = nodeList.get(0);
            if (node.getNodeType() == 1) {
                final Element element = nodeList.get(0);
                final ParameterRule rule = new ParameterRule(element);
                filterProps.addExtraParamRule(rule);
            }
        }
    }
    
    private static void initExtraJSONKeyRule(final SecurityFilterProperties filterProps, final Element root) {
        final List<Element> nodeList = getChildNodesByTagName(root, "extrakey");
        if (nodeList.size() > 0) {
            final Node node = nodeList.get(0);
            if (node.getNodeType() == 1) {
                final Element element = nodeList.get(0);
                final ParameterRule rule = new ParameterRule(element);
                filterProps.addExtraJSONKeyRule(rule);
            }
        }
    }
    
    private static void initURLRuleSet(final SecurityFilterProperties filterProps, final Element root, final File file) {
        final List<Element> urlNodelist = getChildNodesByTagName(root, TagName.URL.value);
        if (urlNodelist.size() > 0) {
            throw new RuntimeException("<url> element's should be enclosed with <urls> element in file :" + file.getName());
        }
        final List<Element> urlsNodeList = getChildNodesByTagName(root, TagName.URLS.value);
        if (urlsNodeList == null) {
            return;
        }
        for (int i = 0; i < urlsNodeList.size(); ++i) {
            final Node urlsNode = urlsNodeList.get(i);
            if (urlsNode.getNodeType() == 1) {
                final Element urlsElement = (Element)urlsNode;
                final ActionRule urlsActionRule = new ActionRule(filterProps, urlsElement);
                final List<Element> urlNodeList = getChildNodesByTagName(urlsElement, TagName.URL.value);
                if (urlNodeList.size() != 0) {
                    final String urls_prefix = ActionRule.getURLAttribute(urlsElement, "prefix");
                    if (SecurityUtil.isValid(urls_prefix)) {
                        urlsActionRule.setPrefix(urls_prefix);
                    }
                    for (int j = 0; j < urlNodeList.size(); ++j) {
                        final Node urlNode = urlNodeList.get(j);
                        if (urlNode.getNodeType() == 1) {
                            final Element urlElement = (Element)urlNode;
                            filterProps.addURLRule(urlElement, urlsActionRule);
                        }
                    }
                }
            }
        }
    }
    
    private static void initJSONTemplateRule(final SecurityFilterProperties filterProps, final Element root, final File file) {
        final List<Element> jsonTemplateNodelist = getChildNodesByTagName(root, TagName.JSON_TEMPLATE.value);
        if (jsonTemplateNodelist.size() > 0) {
            throw new RuntimeException("<jsontemplate> element's should be enclosed with <jsontemplates> element in file :" + file.getName());
        }
        final List<Element> jsonTemplatesNodeList = getChildNodesByTagName(root, TagName.JSON_TEMPLATES.value);
        if (jsonTemplatesNodeList == null) {
            return;
        }
        for (int i = 0; i < jsonTemplatesNodeList.size(); ++i) {
            final Node jsonTemplatesNode = jsonTemplatesNodeList.get(i);
            if (jsonTemplatesNode.getNodeType() == 1) {
                final Element jsonTemplatesElement = (Element)jsonTemplatesNode;
                final JSONTemplateRule jsonTemplatesRule = new JSONTemplateRule(jsonTemplatesElement);
                final List<Element> jsonTemplateNodeList = getChildNodesByTagName(jsonTemplatesElement, TagName.JSON_TEMPLATE.value);
                if (jsonTemplateNodeList != null) {
                    for (int j = 0; j < jsonTemplateNodeList.size(); ++j) {
                        final Node jsonTemplateNode = jsonTemplateNodeList.get(j);
                        if (jsonTemplateNode.getNodeType() == 1) {
                            final Element jsonTemplateElement = (Element)jsonTemplateNode;
                            filterProps.addJSONTemplateRule(jsonTemplateElement, jsonTemplatesRule);
                        }
                    }
                }
            }
        }
    }
    
    private static void initParamGroups(final SecurityFilterProperties filterProps, final Element root) {
        final List<Element> paramGroupsElemList = getChildNodesByTagName(root, TagName.PARAM_GROUPS.value);
        for (final Element paramGroupsElement : paramGroupsElemList) {
            initParamGroups(filterProps, paramGroupsElement, TagName.PARAM_GROUP.value, TagName.PARAM.value);
        }
    }
    
    private static void initJSONKeyGroups(final SecurityFilterProperties filterProps, final Element root) {
        final List<Element> jsonKeyGroupsElemList = getChildNodesByTagName(root, TagName.JSONKEY_GROUPS.value);
        for (final Element jsonKeyGroupsElement : jsonKeyGroupsElemList) {
            initParamGroups(filterProps, jsonKeyGroupsElement, TagName.JSONKEY_GROUP.value, TagName.KEY.value);
        }
    }
    
    private static void initParamGroups(final SecurityFilterProperties filterProps, final Element paramGroupsElement, final String paramGroupTagName, final String paramTagName) {
        final List<Element> paramGroupElemList = getChildNodesByTagName(paramGroupsElement, paramGroupTagName);
        for (final Element paramGroupElem : paramGroupElemList) {
            final List<Element> paramElemList = getChildNodesByTagName(paramGroupElem, paramTagName);
            List<ParameterRule> paramRules = null;
            if (paramElemList.size() > 0) {
                paramRules = new ArrayList<ParameterRule>();
                for (final Element paramElem : paramElemList) {
                    paramRules.add(new ParameterRule(paramElem));
                }
            }
            final List<Element> criteriaElemList = getChildNodesByTagName(paramGroupElem, "or-criteria");
            List<OrCriteriaRule> orCriteriaRules = null;
            if (criteriaElemList.size() > 0) {
                orCriteriaRules = new ArrayList<OrCriteriaRule>(criteriaElemList.size());
                paramRules = ((paramRules == null) ? new ArrayList<ParameterRule>() : paramRules);
                for (final Element criteriaElem : criteriaElemList) {
                    OrCriteriaRule criteriaRule = null;
                    try {
                        criteriaRule = new OrCriteriaRule(criteriaElem, paramTagName);
                    }
                    catch (final RuntimeException e) {
                        RuleSetParser.logger.log(Level.SEVERE, "Error Msg : {0}, ParamGroup Name : {1}", new Object[] { e.getMessage(), paramGroupElem.getAttribute("name") });
                        throw new IAMSecurityException("INVALID_CONFIGURATION");
                    }
                    orCriteriaRules.add(criteriaRule);
                    for (final ParameterRule paramRule : criteriaRule.getParameterRules()) {
                        paramRules.add(paramRule);
                    }
                }
            }
            filterProps.addParamGroupRule(paramGroupElem.getAttribute("name"), paramRules, orCriteriaRules, paramTagName);
        }
    }
    
    private static void initTemplateRule(final SecurityFilterProperties filterProps, final Element root, final File file) {
        final List<Element> templateNodelist = getChildNodesByTagName(root, TagName.TEMPLATE.value);
        if (templateNodelist.size() > 0) {
            throw new RuntimeException("<template> element's should be enclosed with in <templates> element in file :" + file.getName());
        }
        final List<Element> templatesNodeList = getChildNodesByTagName(root, TagName.TEMPLATES.value);
        if (templatesNodeList.isEmpty()) {
            return;
        }
        for (int i = 0; i < templatesNodeList.size(); ++i) {
            final Node templatesNode = templatesNodeList.get(i);
            if (templatesNode.getNodeType() == 1) {
                final Element templatesElement = (Element)templatesNode;
                final List<Element> templateNodeList = getChildNodesByTagName(templatesElement, TagName.TEMPLATE.value);
                for (int j = 0; j < templateNodeList.size(); ++j) {
                    final Node templateNode = templateNodeList.get(j);
                    if (templateNode.getNodeType() == 1) {
                        final Element templateElement = (Element)templateNode;
                        filterProps.addTemplateRule(templateElement);
                    }
                }
            }
        }
    }
    
    private static void initRegexPattern(final SecurityFilterProperties filterProps, final Element root, final String fileName) {
        final NodeList nodelist = root.getElementsByTagName("regex");
        final List<RegexRule> regexRules = new ArrayList<RegexRule>();
        for (int i = 0; i < nodelist.getLength(); ++i) {
            final Node node = nodelist.item(i);
            if (node.getNodeType() == 1) {
                final Element element = (Element)nodelist.item(i);
                regexRules.add(new RegexRule(element));
            }
        }
        filterProps.addRegularExpressions(regexRules, fileName);
    }
    
    private static void initProperties(final SecurityFilterProperties filterProps, final Element root) throws Exception {
        final NodeList nodelist = root.getElementsByTagName("property");
        final Properties props = new Properties();
        for (int i = 0; i < nodelist.getLength(); ++i) {
            final Node node = nodelist.item(i);
            if (node.getNodeType() == 1) {
                final Element element = (Element)nodelist.item(i);
                final String name = element.getAttribute("name");
                final String value = element.getAttribute("value");
                props.setProperty(name, value);
            }
        }
        filterProps.addProperties(props);
        filterProps.initSecurityProperties();
    }
    
    private static void initContentTypes(final SecurityFilterProperties filterProps, final Element root) {
        final NodeList nodelist = root.getElementsByTagName("content-type");
        final Properties props = new Properties();
        final Properties xssProps = new Properties();
        for (int i = 0; i < nodelist.getLength(); ++i) {
            final Node node = nodelist.item(i);
            if (node.getNodeType() == 1) {
                final Element element = (Element)nodelist.item(i);
                final String name = element.getAttribute("name");
                final String value = element.getAttribute("content-types");
                if (SecurityUtil.isValid(value)) {
                    final String xssPatternName = element.getAttribute("xss");
                    props.setProperty(name, value);
                    if (!"".equals(xssPatternName)) {
                        xssProps.setProperty(name, xssPatternName);
                    }
                }
            }
        }
        filterProps.addContentTypes(props);
        filterProps.addContentTypesXSS(xssProps);
    }
    
    private static void initContentTypesRule(final SecurityFilterProperties filterProps, final Element securityElement, final File securityXMLFile) {
        for (final Element contentTypesElement : getChildNodesByTagName(securityElement, "content-types")) {
            for (final Element contentTypeElement : getChildNodesByTagName(contentTypesElement, "content-type")) {
                try {
                    if (!SecurityUtil.isValid(contentTypeElement.getAttribute("extends")) && contentTypeElement.getChildNodes().getLength() <= 0) {
                        continue;
                    }
                    final ContentTypeRule contentTypeRule = new ContentTypeRule(contentTypeElement);
                    filterProps.addContentTypeRule(contentTypeRule);
                }
                catch (final Exception e) {
                    RuleSetParser.logger.log(Level.SEVERE, "Invalid configuration at the file: {0}", new Object[] { securityXMLFile.getName() });
                    throw e;
                }
            }
        }
    }
    
    private static void initDefaultParams(final Element root) {
        final NodeList nodelist = root.getElementsByTagName("defaultparams");
        for (int i = 0; i < nodelist.getLength(); ++i) {
            final NodeList paramsList = nodelist.item(i).getChildNodes();
            for (int j = 0; j < paramsList.getLength(); ++j) {
                if (paramsList.item(j).getNodeType() == 1) {
                    final Element ele = (Element)paramsList.item(j);
                    final ParameterRule rule = new ParameterRule(ele);
                    SecurityRequestWrapper.addDefaultParameter(rule.getParamName(), rule);
                }
            }
        }
    }
    
    private static void initProxyURLs(final SecurityFilterProperties filterProps, final Element root) {
        final NodeList nodelist = root.getElementsByTagName("proxy");
        for (int i = 0; i < nodelist.getLength(); ++i) {
            final Node node = nodelist.item(i);
            if (node.getNodeType() == 1) {
                final Element element = (Element)nodelist.item(i);
                filterProps.addProxyURL(element);
            }
        }
    }
    
    private static void initProxyPolicies(final SecurityFilterProperties filterProps, final Element root, final String fileName) {
        final List<Element> proxyPolicies = getChildNodesByTagName(root, TagName.PROXY_POLICY.value);
        final int size = proxyPolicies.size();
        if (size > 1 || (size == 1 && filterProps.allowedServicesViaProxy != null)) {
            RuleSetParser.logger.log(Level.SEVERE, "More than one global <proxy-policy> rules are not allowed . Duplicate global <proxy-policy> found in file : {0}", fileName);
            throw new IAMSecurityException("More than one global <proxy-policy> rules are not allowed. Duplicate global <proxy-policy> found in file : " + fileName);
        }
        if (size == 1) {
            filterProps.addProxyPolicy(proxyPolicies.get(0).getAttribute("allowed-services"));
        }
    }
    
    private static void initPiiDetectorRule(final SecurityFilterProperties filterProps, final Element root, final File file) {
        if (getChildNodesByTagName(root, TagName.PIIDETECTOR.value).size() > 1) {
            throw new RuntimeException("More then one <pii-detector> element in file :" + file.getName());
        }
        final Element detector = getFirstChildNodeByTagName(root, TagName.PIIDETECTOR.value);
        if (detector != null && detector.getAttributes() != null) {
            final Element classifier = getFirstChildNodeByTagName(detector, TagName.PIICLASSIFIER.value);
            if (classifier != null && classifier.getChildNodes() != null) {
                filterProps.setPIIDetectorRule(classifier);
            }
        }
    }
    
    private static void initExceptionsRule(final SecurityFilterProperties securityFilterProperties, final Element securityElement, final File securityXMLFile) {
        final List<Element> exceptionsElems = getChildNodesByTagName(securityElement, "exceptions");
        if (exceptionsElems.isEmpty()) {
            return;
        }
        for (final Element exceptionsElem : exceptionsElems) {
            for (final Element exceptionElem : getChildNodesByTagName(exceptionsElem, "exception")) {
                securityFilterProperties.addExceptionRule(new ExceptionRule(exceptionElem));
            }
        }
    }
    
    private static Document parseXmlFile(final File file) throws Exception {
        Document document = null;
        document = SecurityUtil.getDocumentBuilder().parse(file);
        return document;
    }
    
    public static List<Element> getChildNodesByTagName(final Element element, final String tagName) {
        final List<Element> nodeList = new ArrayList<Element>();
        for (Node childNode = element.getFirstChild(); childNode != null; childNode = childNode.getNextSibling()) {
            if (childNode.getNodeType() == 1 && childNode.getNodeName().equalsIgnoreCase(tagName)) {
                final Element childElement = (Element)childNode;
                nodeList.add(childElement);
            }
        }
        return nodeList;
    }
    
    public static List<Element> getUniqueChildNodes(final Element element) {
        final List<Element> nodeList = new ArrayList<Element>();
        final List<String> eleNameList = new ArrayList<String>();
        for (Node childNode = element.getFirstChild(); childNode != null; childNode = childNode.getNextSibling()) {
            final String nodeName = childNode.getNodeName().toLowerCase();
            if (childNode.getNodeType() == 1 && !eleNameList.contains(nodeName)) {
                final Element childElement = (Element)childNode;
                nodeList.add(childElement);
                eleNameList.add(nodeName);
            }
        }
        return nodeList;
    }
    
    public static Element getFirstChildNodeByTagName(final Element element, final String tagName) {
        for (Node childNode = element.getFirstChild(); childNode != null; childNode = childNode.getNextSibling()) {
            if (childNode.getNodeType() == 1 && childNode.getNodeName().equalsIgnoreCase(tagName)) {
                return (Element)childNode;
            }
        }
        return null;
    }
    
    private static void parseXssPattern(final SecurityFilterProperties filterProps, final Element root) {
        final NodeList nl = root.getElementsByTagName("xsspatterns");
        if (nl == null || nl.getLength() <= 0) {
            return;
        }
        final Element regexset = (Element)nl.item(0);
        final NodeList children = regexset.getElementsByTagName("xsspattern");
        for (int i = 0; i < children.getLength(); ++i) {
            final Node child = children.item(i);
            if (child.getNodeType() == 1) {
                final Element e = (Element)child;
                final String name = e.getAttribute("name");
                final XSSUtil xssUtil = new XSSUtil(SecurityUtil.getElementProperties(e));
                filterProps.addXSSUtil(name, xssUtil);
            }
        }
    }
    
    public static void validateXMLConfiguration(final SecurityFilterProperties sfp, final Collection<URLRule> allURLRule) {
        final List<String> errorMessages = new ArrayList<String>();
        for (final URLRule rule : allURLRule) {
            final String result = rule.validateConfiguration(sfp);
            if (result != null) {
                errorMessages.add(result);
            }
        }
        final ParameterRule extraParamRule = sfp.getExtraParamRule();
        if (extraParamRule != null) {
            if (sfp.isIgnoreExtraParam()) {
                errorMessages.add("Extra parameter configuration error : Either extraparameter rule or ignore.extra.param property should be specified");
            }
            else {
                final String result2 = extraParamRule.validateConfiguration(sfp);
                if (result2 != null) {
                    errorMessages.add("Extra parameter configuration error : " + result2);
                }
            }
        }
        final Collection<XSSUtil> xssUtils = sfp.getXSSUtils();
        if (xssUtils != null) {
            for (final XSSUtil xssUtil : xssUtils) {
                if (xssUtil.getExtendsFilter() != null && !xssUtil.isFilterExtended()) {
                    errorMessages.add("XSS Pattern extends configuration error : " + xssUtil.getName() + " Check if extended xsspattern  : " + xssUtil.getExtendsFilter() + " exists (or) if the extended xsspattern already extends another xsspattern (Currently only one level of xsspattern inheritance supported.)");
                }
            }
        }
        if (errorMessages.size() > 0) {
            RuleSetParser.logger.log(Level.SEVERE, convertToString(errorMessages));
            throw new RuntimeException("Invalid security configuration");
        }
    }
    
    static String convertToString(final List<String> messages) {
        String str = "\n\n******************************************\n\nSECURITY.XML VALIDATION FAILED :\n\n******************************************\n";
        for (final String message : messages) {
            str = str + "\n" + message;
        }
        return str;
    }
    
    static {
        logger = Logger.getLogger(RuleSetParser.class.getName());
    }
    
    public enum TagName
    {
        URLS("urls"), 
        URL("url"), 
        PARAM("param"), 
        JSON_TEMPLATES("jsontemplates"), 
        JSON_TEMPLATE("jsontemplate"), 
        KEY("key"), 
        PARAM_GROUPS("param-groups"), 
        PARAM_GROUP("param-group"), 
        JSONKEY_GROUPS("jsonkey-groups"), 
        JSONKEY_GROUP("jsonkey-group"), 
        TEMPLATE("template"), 
        TEMPLATES("templates"), 
        URL_VALIDATOR("url-validator"), 
        URL_VALIDATORS("url-validators"), 
        SCHEME("scheme"), 
        CUSTOM_SCHEME("custom-scheme"), 
        DOMAIN_AUTHORITY("domainauthority"), 
        PATHINFO("pathinfo"), 
        QUERYSTRING("querystring"), 
        FRAGMENT("fragment"), 
        DATAPART("datapart"), 
        MIMETYPES("mimetypes"), 
        CHARSETS("charsets"), 
        ENCODING("encoding"), 
        DEFAULT_REQUEST_HEADER_TAG("default-request-headers"), 
        PROXY_POLICY("proxy-policy"), 
        XML_SCHEMA("xml-schema"), 
        CACHE_CONFIGURATIONS("cache-configurations"), 
        CACHE_CONFIGURATION("cache-configuration"), 
        INTERNAL_REQUEST_HEADER_TAG("internal-request-headers"), 
        ZIP_SANITIZER("zip-sanitizer"), 
        ZIP_SANITIZERS("zip-sanitizers"), 
        PIIDETECTOR("pii-detector"), 
        PIICLASSIFIER("pii-classifier"), 
        PARAM_OR_STREAM("paramorstream"), 
        URL_BLACKLIST("url-blacklist");
        
        private String value;
        
        private TagName(final String value) {
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
    }
}
