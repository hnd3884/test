package com.adventnet.iam.security;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.logging.Level;
import java.util.LinkedHashMap;
import org.w3c.dom.Element;
import java.util.logging.Logger;
import java.util.Map;

public class TemplateRule
{
    private String templateName;
    private Map<String, ParameterRule> templateParamInRegexKeyValueMap;
    private Map<String, ParameterRule> templateKeyValueMap;
    private static final Logger LOGGER;
    
    protected TemplateRule(final Element elem) {
        this.templateName = null;
        this.templateParamInRegexKeyValueMap = new LinkedHashMap<String, ParameterRule>();
        this.templateKeyValueMap = new LinkedHashMap<String, ParameterRule>();
        final String name = elem.getAttribute("name");
        if (SecurityUtil.isValid(name)) {
            this.templateName = name;
        }
        try {
            this.initKeyValueRule(elem);
        }
        catch (final IAMSecurityException e) {
            TemplateRule.LOGGER.log(Level.SEVERE, "Invalid configuration at the <template> rule. Name: \"{0}\"", new Object[] { this.templateName });
            throw e;
        }
    }
    
    public TemplateRule() {
        this.templateName = null;
        this.templateParamInRegexKeyValueMap = new LinkedHashMap<String, ParameterRule>();
        this.templateKeyValueMap = new LinkedHashMap<String, ParameterRule>();
    }
    
    private void initKeyValueRule(final Element element) {
        final List<Element> keyNodeList = RuleSetParser.getChildNodesByTagName(element, RuleSetParser.TagName.KEY.getValue());
        if (keyNodeList.size() > 0) {
            for (int i = 0; i < keyNodeList.size(); ++i) {
                if (keyNodeList.get(i).getNodeType() == 1) {
                    final Element keyElement = keyNodeList.get(i);
                    final ParameterRule keyRule = new ParameterRule(keyElement);
                    if (keyRule.getXSSValidation() != null) {
                        TemplateRule.LOGGER.log(Level.SEVERE, "The \"xss\" configuration is not supported for <key> rule. Name: \"{0}\"", new Object[] { keyRule.getParamName() });
                        throw new IAMSecurityException("INVALID_CONFIGURATION");
                    }
                    this.addTemplateKeyRule(keyRule);
                }
            }
        }
    }
    
    private void addTemplateKeyRule(final ParameterRule keyRule) {
        if (!this.templateParamInRegexKeyValueMap.containsKey(keyRule.getParamName()) && !this.templateKeyValueMap.containsKey(keyRule.getParamName())) {
            if (keyRule.isParamNameInRegex()) {
                this.templateParamInRegexKeyValueMap.put(keyRule.getParamName(), keyRule);
            }
            else {
                this.templateKeyValueMap.put(keyRule.getParamName(), keyRule);
            }
            return;
        }
        TemplateRule.LOGGER.log(Level.SEVERE, " Rule \"{0}\" is already defined", keyRule.toString());
        throw new IAMSecurityException("INVALID_TEMPLATE_CONFIGURATION");
    }
    
    String getTemplateName() {
        return this.templateName;
    }
    
    public Map<String, ParameterRule> getTemplateKeyRule() {
        return this.templateKeyValueMap;
    }
    
    public Map<String, ParameterRule> getTemplateParamInRegexRule() {
        return this.templateParamInRegexKeyValueMap;
    }
    
    public static TemplateRule getTemplateRule(final String templateName) {
        return SecurityFilterProperties.getInstance(SecurityUtil.getCurrentRequest()).getTemplateRule(templateName);
    }
    
    void validateDataFormat(final HttpServletRequest request, final DataFormatValidator dataFormat) {
        final List<String> formatKeyList = dataFormat.getKeySet();
        for (final ParameterRule paramRule : this.getTemplateKeyRule().values()) {
            final String keyName = paramRule.getParamName();
            int noOfOccurence = 0;
            if (SecurityUtil.isValid(keyName) && formatKeyList.contains(keyName) && !dataFormat.hasValidated(keyName)) {
                noOfOccurence = this.validateKeyValuePair(request, paramRule, keyName, dataFormat);
            }
            this.validateNoOfOccurence(noOfOccurence, paramRule, request, this.getTemplateName());
        }
        for (final ParameterRule paramRule : this.getTemplateParamInRegexRule().values()) {
            int noOfOccurence2 = 0;
            final String keyName2 = paramRule.getParamName();
            for (final String parsedKeyString : formatKeyList) {
                final String parsedKey = parsedKeyString;
                if (!dataFormat.hasValidated(parsedKeyString) && SecurityUtil.matchPattern(parsedKeyString, keyName2, (SecurityRequestWrapper)request)) {
                    noOfOccurence2 += this.validateKeyValuePair(request, paramRule, parsedKeyString, dataFormat);
                }
            }
            this.validateNoOfOccurence(noOfOccurence2, paramRule, request, this.getTemplateName());
        }
    }
    
    private int validateKeyValuePair(final HttpServletRequest request, final ParameterRule paramRule, final String keyName, final DataFormatValidator dataFormat) {
        if (dataFormat.getDataFormatType() == ZSecConstants.DataType.Vcard) {
            final List<String> validatedValueList = new LinkedList<String>();
            for (final String value : dataFormat.getList(keyName)) {
                validatedValueList.add(this.validateParamValue(request, paramRule.getParamName(), value, paramRule));
            }
            dataFormat.setList(keyName, validatedValueList);
            return validatedValueList.size();
        }
        String validatedValue = null;
        final String value2 = dataFormat.get(keyName);
        validatedValue = this.validateParamValue(request, keyName, value2, paramRule);
        dataFormat.set(keyName, validatedValue);
        return 1;
    }
    
    private String validateParamValue(final HttpServletRequest request, final String keyName, final String value, final ParameterRule paramRule) {
        String validatedValue = null;
        if (!SecurityUtil.isValid(value.trim())) {
            this.checkForEmptyValue(request, keyName, paramRule.isEmptyValueAllowed());
            validatedValue = value;
        }
        else {
            validatedValue = paramRule.validateParamValue((SecurityRequestWrapper)request, keyName, value, paramRule);
        }
        return validatedValue;
    }
    
    void validateNoOfOccurence(final int noOfOccurance, final ParameterRule paramRule, final HttpServletRequest request, final String templateName) {
        final int minOccurrence = paramRule.getMinOccurrences();
        final int maxOccurrence = paramRule.getMaxOccurrences();
        if (noOfOccurance < minOccurrence) {
            TemplateRule.LOGGER.log(Level.SEVERE, "The key \"{0}\" for the template \"{1}\" is less than the minimum occurrences configured in the param rule :\n", new Object[] { paramRule.getParamName(), templateName });
            throw new IAMSecurityException("LESS_THAN_MIN_OCCURANCE", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"));
        }
        if (noOfOccurance > maxOccurrence) {
            TemplateRule.LOGGER.log(Level.SEVERE, "The key \"{0}\" for the template \"{1}\" is more than the maximum occurrences configured in the param rule :\n", new Object[] { paramRule.getParamName(), templateName });
            throw new IAMSecurityException("MORE_THAN_MAX_OCCURANCE", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"));
        }
    }
    
    private void checkForEmptyValue(final HttpServletRequest request, final String paramName, final boolean allowEmptyValue) {
        if (!allowEmptyValue) {
            TemplateRule.LOGGER.log(Level.SEVERE, "Empty value is not allowed for the param \"{0}\" in the request url \"{1}\" ", new Object[] { paramName, request.getRequestURI() });
            throw new IAMSecurityException("EMPTY_VALUE_NOT_ALLOWED", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName);
        }
    }
    
    private static TemplateRule getTemplateRule(final String paramName, final String templateName, final String parameterValue) {
        final HttpServletRequest request = SecurityUtil.getCurrentRequest();
        TemplateRule.LOGGER.log(Level.FINE, "\ntemplate = \"{0}\"\n paremeterValue = \"{1}\"\n", new Object[] { templateName, parameterValue });
        final TemplateRule templateRule = getTemplateRule(templateName);
        if (templateRule == null) {
            TemplateRule.LOGGER.log(Level.SEVERE, "The template Rule for the template = {0} is not defined ", templateName);
            throw new IAMSecurityException("TEMPLATE_RULE_NOT_DEFINED", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName);
        }
        return templateRule;
    }
    
    static Object validateDataFormat(final String paramName, final String parameterValue, final ParameterRule paramRule) throws IOException {
        final TemplateRule templateRule = getTemplateRule(paramName, paramRule.getTemplateName(), parameterValue);
        final String dataType = paramRule.getDataType();
        switch (dataType) {
            case "vcardArray": {
                final VcardValidator vcardValidator = new VcardValidator();
                return vcardValidator.getVcardFormatAsString(vcardValidator.parseAndValidateVcardFormat(paramName, parameterValue, templateRule, paramRule));
            }
            case "properties": {
                final PropertiesValidator propertiesProperty = new PropertiesValidator();
                return propertiesProperty.parseAndValidatePropertiesFormat(paramName, parameterValue, templateRule, paramRule.isEmptyValueAllowed());
            }
            case "csv": {
                final CsvValidator csvValidator = new CsvValidator();
                return csvValidator.parseAndValidateCSVFormat(paramName, parameterValue, templateRule, paramRule.getFormat(), paramRule.isEmptyValueAllowed());
            }
            default: {
                return null;
            }
        }
    }
    
    static CsvValidator csvValidation(final String paramName, final String paramValue, final String templateName, final String format, final boolean allowEmptyValue) throws IOException {
        return new CsvValidator().parseAndValidateCSVFormat(paramName, paramValue, getTemplateRule(templateName), format, allowEmptyValue);
    }
    
    static {
        LOGGER = Logger.getLogger(TemplateRule.class.getName());
    }
}
