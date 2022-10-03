package com.adventnet.iam.security;

import java.io.ByteArrayInputStream;
import java.util.Properties;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import org.json.JSONException;
import com.adventnet.iam.xss.XSSUtil;
import com.zoho.security.eventfw.pojos.log.ZSEC_PERFORMANCE_ANOMALY;
import com.zoho.security.eventfw.ExecutionTimer;
import java.util.Map;
import java.io.InputStream;
import java.util.List;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import com.zoho.security.util.RangeUtil;
import java.util.logging.Level;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import com.zoho.security.api.Range;
import java.util.regex.Pattern;

public class ParameterRule
{
    private static Pattern supportedDataTypes;
    private static Pattern primitiveDataTypes;
    private String paramName;
    private Range index;
    private Pattern paramNameInRegex;
    private String allowedValueRegex;
    private int minLength;
    private int maxLength;
    private int minOccurrences;
    private int maxOccurrences;
    private int limit;
    private boolean canonicalize;
    private boolean deltaContent;
    private boolean isParamNameInRegex;
    private String dataType;
    String xssValidation;
    private String defaultValue;
    private String split;
    private Pattern splitPattern;
    private boolean isSplitDefined;
    private boolean decrypt;
    String decrypt_label;
    private boolean saltTicket;
    private long saltValidity;
    private String saltDelimiter;
    private static final Logger logger;
    private HashMap<String, String> customAttributes;
    private Boolean trim;
    private String mask;
    private String preserveChars;
    private int preserveCharPrefix;
    private int preserveCharSuffix;
    private int preserveCharsLimitInPercent;
    private String jsonTemplate;
    private String templateName;
    private String format;
    private UploadFileRule fileRuleForImportedData;
    private boolean filterMatchedPattern;
    private String replacementString;
    private boolean isValidInputStreamNeeded;
    private boolean xmlSchemaValidation;
    Boolean allowEmpty;
    private boolean allowXMLInlineEntityExpansion;
    private ArrayList<Range<?>> ranges;
    private String rangeStr;
    private String arraySize;
    private Range<Integer> arraySizeInRange;
    private String labelStr;
    private String xmlSchemaName;
    private String dynamicTemplateKeyRegex;
    boolean storeParameterValue;
    private Boolean allowInvalid;
    private String templateParam;
    private String templatePrefix;
    private HashMap<String, String> labelMap;
    private boolean antispam;
    private boolean isPrimitive;
    private boolean isRegexValidation;
    boolean isMaxOccurrenceConfigured;
    private boolean isEditableOnValidation;
    boolean paramOrStreamConfig;
    private boolean isExtraParamRule;
    static final String ZSEC_DEFAULT_LABEL = "ZSEC_DEFAULT_LABEL";
    private static final Pattern LABEL_MESSAGE_PATTERN;
    private static final Pattern JSON_TEMPLATE_PATTERN;
    String ruleName;
    SecurityFilterProperties.InputStreamValidationMode streamContentValidationMode;
    boolean skipDefaultMaxOccurrenceCheckForOrCriteriaParam;
    private UploadFileRule fileRuleForStreamContent;
    private long inputStreamMaxSizeInKB;
    private boolean isImportFile;
    
    public ParameterRule() {
        this.paramName = null;
        this.index = null;
        this.paramNameInRegex = null;
        this.allowedValueRegex = null;
        this.minLength = 0;
        this.maxLength = 100;
        this.minOccurrences = 0;
        this.maxOccurrences = 1;
        this.limit = 10;
        this.canonicalize = false;
        this.deltaContent = false;
        this.isParamNameInRegex = false;
        this.dataType = null;
        this.xssValidation = null;
        this.defaultValue = null;
        this.split = null;
        this.splitPattern = null;
        this.isSplitDefined = false;
        this.decrypt = false;
        this.decrypt_label = null;
        this.saltTicket = false;
        this.saltValidity = -1L;
        this.saltDelimiter = null;
        this.customAttributes = null;
        this.trim = null;
        this.mask = null;
        this.preserveChars = "0,5";
        this.preserveCharsLimitInPercent = 10;
        this.jsonTemplate = null;
        this.templateName = null;
        this.format = null;
        this.fileRuleForImportedData = null;
        this.filterMatchedPattern = false;
        this.replacementString = " ";
        this.isValidInputStreamNeeded = false;
        this.xmlSchemaValidation = false;
        this.allowEmpty = null;
        this.allowXMLInlineEntityExpansion = false;
        this.ranges = null;
        this.rangeStr = null;
        this.arraySize = "0-10";
        this.arraySizeInRange = null;
        this.labelStr = null;
        this.storeParameterValue = true;
        this.allowInvalid = null;
        this.templateParam = null;
        this.templatePrefix = null;
        this.labelMap = null;
        this.antispam = false;
        this.isPrimitive = false;
        this.isRegexValidation = false;
        this.isMaxOccurrenceConfigured = false;
        this.isEditableOnValidation = false;
        this.paramOrStreamConfig = false;
        this.isExtraParamRule = false;
        this.streamContentValidationMode = SecurityFilterProperties.InputStreamValidationMode.ERROR;
        this.skipDefaultMaxOccurrenceCheckForOrCriteriaParam = false;
        this.inputStreamMaxSizeInKB = -1L;
    }
    
    public ParameterRule(final String paramName, final String allowedValueRegex) {
        this.paramName = null;
        this.index = null;
        this.paramNameInRegex = null;
        this.allowedValueRegex = null;
        this.minLength = 0;
        this.maxLength = 100;
        this.minOccurrences = 0;
        this.maxOccurrences = 1;
        this.limit = 10;
        this.canonicalize = false;
        this.deltaContent = false;
        this.isParamNameInRegex = false;
        this.dataType = null;
        this.xssValidation = null;
        this.defaultValue = null;
        this.split = null;
        this.splitPattern = null;
        this.isSplitDefined = false;
        this.decrypt = false;
        this.decrypt_label = null;
        this.saltTicket = false;
        this.saltValidity = -1L;
        this.saltDelimiter = null;
        this.customAttributes = null;
        this.trim = null;
        this.mask = null;
        this.preserveChars = "0,5";
        this.preserveCharsLimitInPercent = 10;
        this.jsonTemplate = null;
        this.templateName = null;
        this.format = null;
        this.fileRuleForImportedData = null;
        this.filterMatchedPattern = false;
        this.replacementString = " ";
        this.isValidInputStreamNeeded = false;
        this.xmlSchemaValidation = false;
        this.allowEmpty = null;
        this.allowXMLInlineEntityExpansion = false;
        this.ranges = null;
        this.rangeStr = null;
        this.arraySize = "0-10";
        this.arraySizeInRange = null;
        this.labelStr = null;
        this.storeParameterValue = true;
        this.allowInvalid = null;
        this.templateParam = null;
        this.templatePrefix = null;
        this.labelMap = null;
        this.antispam = false;
        this.isPrimitive = false;
        this.isRegexValidation = false;
        this.isMaxOccurrenceConfigured = false;
        this.isEditableOnValidation = false;
        this.paramOrStreamConfig = false;
        this.isExtraParamRule = false;
        this.streamContentValidationMode = SecurityFilterProperties.InputStreamValidationMode.ERROR;
        this.skipDefaultMaxOccurrenceCheckForOrCriteriaParam = false;
        this.inputStreamMaxSizeInKB = -1L;
        this.paramName = paramName;
        this.setAllowedValueRegex(allowedValueRegex);
    }
    
    public ParameterRule(final String paramName, final String allowedValueRegex, final int minLength, final int maxLength) {
        this.paramName = null;
        this.index = null;
        this.paramNameInRegex = null;
        this.allowedValueRegex = null;
        this.minLength = 0;
        this.maxLength = 100;
        this.minOccurrences = 0;
        this.maxOccurrences = 1;
        this.limit = 10;
        this.canonicalize = false;
        this.deltaContent = false;
        this.isParamNameInRegex = false;
        this.dataType = null;
        this.xssValidation = null;
        this.defaultValue = null;
        this.split = null;
        this.splitPattern = null;
        this.isSplitDefined = false;
        this.decrypt = false;
        this.decrypt_label = null;
        this.saltTicket = false;
        this.saltValidity = -1L;
        this.saltDelimiter = null;
        this.customAttributes = null;
        this.trim = null;
        this.mask = null;
        this.preserveChars = "0,5";
        this.preserveCharsLimitInPercent = 10;
        this.jsonTemplate = null;
        this.templateName = null;
        this.format = null;
        this.fileRuleForImportedData = null;
        this.filterMatchedPattern = false;
        this.replacementString = " ";
        this.isValidInputStreamNeeded = false;
        this.xmlSchemaValidation = false;
        this.allowEmpty = null;
        this.allowXMLInlineEntityExpansion = false;
        this.ranges = null;
        this.rangeStr = null;
        this.arraySize = "0-10";
        this.arraySizeInRange = null;
        this.labelStr = null;
        this.storeParameterValue = true;
        this.allowInvalid = null;
        this.templateParam = null;
        this.templatePrefix = null;
        this.labelMap = null;
        this.antispam = false;
        this.isPrimitive = false;
        this.isRegexValidation = false;
        this.isMaxOccurrenceConfigured = false;
        this.isEditableOnValidation = false;
        this.paramOrStreamConfig = false;
        this.isExtraParamRule = false;
        this.streamContentValidationMode = SecurityFilterProperties.InputStreamValidationMode.ERROR;
        this.skipDefaultMaxOccurrenceCheckForOrCriteriaParam = false;
        this.inputStreamMaxSizeInKB = -1L;
        this.paramName = paramName;
        this.setAllowedValueRegex(allowedValueRegex);
        this.minLength = minLength;
        this.maxLength = maxLength;
    }
    
    public ParameterRule(final String paramName, final String allowedValueRegex, final int minLength, final int maxLength, final int minOccurrences, final int maxOccurrences, final String dataType, final String xssPatternName) {
        this.paramName = null;
        this.index = null;
        this.paramNameInRegex = null;
        this.allowedValueRegex = null;
        this.minLength = 0;
        this.maxLength = 100;
        this.minOccurrences = 0;
        this.maxOccurrences = 1;
        this.limit = 10;
        this.canonicalize = false;
        this.deltaContent = false;
        this.isParamNameInRegex = false;
        this.dataType = null;
        this.xssValidation = null;
        this.defaultValue = null;
        this.split = null;
        this.splitPattern = null;
        this.isSplitDefined = false;
        this.decrypt = false;
        this.decrypt_label = null;
        this.saltTicket = false;
        this.saltValidity = -1L;
        this.saltDelimiter = null;
        this.customAttributes = null;
        this.trim = null;
        this.mask = null;
        this.preserveChars = "0,5";
        this.preserveCharsLimitInPercent = 10;
        this.jsonTemplate = null;
        this.templateName = null;
        this.format = null;
        this.fileRuleForImportedData = null;
        this.filterMatchedPattern = false;
        this.replacementString = " ";
        this.isValidInputStreamNeeded = false;
        this.xmlSchemaValidation = false;
        this.allowEmpty = null;
        this.allowXMLInlineEntityExpansion = false;
        this.ranges = null;
        this.rangeStr = null;
        this.arraySize = "0-10";
        this.arraySizeInRange = null;
        this.labelStr = null;
        this.storeParameterValue = true;
        this.allowInvalid = null;
        this.templateParam = null;
        this.templatePrefix = null;
        this.labelMap = null;
        this.antispam = false;
        this.isPrimitive = false;
        this.isRegexValidation = false;
        this.isMaxOccurrenceConfigured = false;
        this.isEditableOnValidation = false;
        this.paramOrStreamConfig = false;
        this.isExtraParamRule = false;
        this.streamContentValidationMode = SecurityFilterProperties.InputStreamValidationMode.ERROR;
        this.skipDefaultMaxOccurrenceCheckForOrCriteriaParam = false;
        this.inputStreamMaxSizeInKB = -1L;
        this.paramName = paramName;
        this.setAllowedValueRegex(allowedValueRegex);
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.minOccurrences = minOccurrences;
        this.maxOccurrences = maxOccurrences;
        if (dataType != null) {
            this.setDataType(dataType);
        }
        this.setXSSValidation(xssPatternName);
        this.validateParameterRuleConf();
    }
    
    public ParameterRule(final Element element) {
        this.paramName = null;
        this.index = null;
        this.paramNameInRegex = null;
        this.allowedValueRegex = null;
        this.minLength = 0;
        this.maxLength = 100;
        this.minOccurrences = 0;
        this.maxOccurrences = 1;
        this.limit = 10;
        this.canonicalize = false;
        this.deltaContent = false;
        this.isParamNameInRegex = false;
        this.dataType = null;
        this.xssValidation = null;
        this.defaultValue = null;
        this.split = null;
        this.splitPattern = null;
        this.isSplitDefined = false;
        this.decrypt = false;
        this.decrypt_label = null;
        this.saltTicket = false;
        this.saltValidity = -1L;
        this.saltDelimiter = null;
        this.customAttributes = null;
        this.trim = null;
        this.mask = null;
        this.preserveChars = "0,5";
        this.preserveCharsLimitInPercent = 10;
        this.jsonTemplate = null;
        this.templateName = null;
        this.format = null;
        this.fileRuleForImportedData = null;
        this.filterMatchedPattern = false;
        this.replacementString = " ";
        this.isValidInputStreamNeeded = false;
        this.xmlSchemaValidation = false;
        this.allowEmpty = null;
        this.allowXMLInlineEntityExpansion = false;
        this.ranges = null;
        this.rangeStr = null;
        this.arraySize = "0-10";
        this.arraySizeInRange = null;
        this.labelStr = null;
        this.storeParameterValue = true;
        this.allowInvalid = null;
        this.templateParam = null;
        this.templatePrefix = null;
        this.labelMap = null;
        this.antispam = false;
        this.isPrimitive = false;
        this.isRegexValidation = false;
        this.isMaxOccurrenceConfigured = false;
        this.isEditableOnValidation = false;
        this.paramOrStreamConfig = false;
        this.isExtraParamRule = false;
        this.streamContentValidationMode = SecurityFilterProperties.InputStreamValidationMode.ERROR;
        this.skipDefaultMaxOccurrenceCheckForOrCriteriaParam = false;
        this.inputStreamMaxSizeInKB = -1L;
        this.setRuleName(element.getTagName());
        this.customAttributes = SecurityUtil.convertToMap(element);
        this.setParamName(element.getAttribute("name"));
        final String index = element.getAttribute("index");
        this.setAllowedValueRegex(element.getAttribute("regex"));
        final String minLenStr = element.getAttribute("min-len");
        final String maxLenStr = element.getAttribute("max-len");
        final String minOccurStr = element.getAttribute("min-occurrences");
        final String maxOccurStr = element.getAttribute("max-occurrences");
        final String limit = element.getAttribute("limit");
        final String defaultStr = element.getAttribute("default");
        final String typeStr = element.getAttribute("type");
        final String xssStr = element.getAttribute("xss");
        final String csv_format = element.getAttribute("format");
        final String splitStr = element.getAttribute("split");
        final String saltValidityStr = element.getAttribute("salt-validity");
        final String jsonTemplateText = element.getAttribute("template");
        final String rangeVal = element.getAttribute("range");
        if ("extraparam".equalsIgnoreCase(element.getNodeName())) {
            this.isExtraParamRule = true;
        }
        if (jsonTemplateText != null && !"".equalsIgnoreCase(jsonTemplateText)) {
            if (!jsonTemplateText.startsWith("${")) {
                this.setJSONTemplate(jsonTemplateText);
            }
            else {
                this.extractJSONTemplate(jsonTemplateText);
                final String dynamicTempKeyRegex = element.getAttribute("dynamic-template-key-regex");
                if (SecurityUtil.isValid(dynamicTempKeyRegex)) {
                    this.dynamicTemplateKeyRegex = dynamicTempKeyRegex;
                }
            }
        }
        final String templateName = element.getAttribute("template");
        if (templateName != null && !"".equalsIgnoreCase(templateName)) {
            this.setTemplateName(templateName);
        }
        if (typeStr != null && !"".equals(typeStr)) {
            this.setDataType(typeStr);
        }
        if ("url".equalsIgnoreCase(this.dataType)) {
            if (this.templateName == null) {
                ParameterRule.logger.log(Level.SEVERE, "Incorrect security configuration - \"TEMPLATE\"  is not configured for the type = \"URL\": {0}", this);
                throw new IAMSecurityException("URL_VALIDATOR_TEMPLATE_NAME_NOT_CONFIGURED");
            }
            final boolean isImport = "true".equalsIgnoreCase(element.getAttribute("import-url"));
            if (isImport) {
                this.fileRuleForImportedData = new UploadFileRule(element);
            }
            this.antispam = "true".equalsIgnoreCase(element.getAttribute("antispam"));
        }
        this.isImportFile = "true".equalsIgnoreCase(element.getAttribute("import-file"));
        if (this.isImportFile || "binary".equals(this.dataType)) {
            if (!"inputstream".equals(element.getNodeName())) {
                final String logMessage = this.isImportFile ? "Invalid attribute 'import-file=true'" : "Unsupported datatype 'binary'";
                ParameterRule.logger.log(Level.SEVERE, "{0} is configured for the {1} \"{2}\", it is supported only for <inputstream ../>", new Object[] { logMessage, element.getNodeName(), this.paramName });
                throw new IAMSecurityException("INVALID_CONFIGURATION");
            }
            final String maxSize = element.getAttribute("max-size");
            if (!SecurityUtil.isValid(maxSize)) {
                ParameterRule.logger.log(Level.SEVERE, "'max-size' attribute is mandatory for inputstream rule when it has type=\"binary\" or import-file=\"true\" configuration");
                throw new IAMSecurityException("INVALID_CONFIGURATION");
            }
            this.setInputStreamMaxSizeInKB(Long.parseLong(maxSize));
            if (this.isImportFile) {
                this.fileRuleForStreamContent = new UploadFileRule(element);
            }
        }
        this.xmlSchemaValidation = "true".equals(element.getAttribute("xml-schema-validation"));
        this.xmlSchemaName = SecurityUtil.getValidValue(element.getAttribute("xml-schema"), null);
        this.filterMatchedPattern = "true".equals(element.getAttribute("filter-matched-content"));
        this.isRegexValidation = (SecurityUtil.isValid(this.allowedValueRegex) && !this.filterMatchedPattern);
        final String replacementStr = element.getAttribute("replacement-string");
        this.replacementString = ((replacementStr != null) ? replacementStr : " ");
        this.isValidInputStreamNeeded = "true".equals(element.getAttribute("non-empty"));
        if (SecurityUtil.isValid(rangeVal)) {
            this.setRange(rangeVal);
        }
        if (minLenStr != null && !"".equals(minLenStr)) {
            this.setMinLength(Integer.parseInt(minLenStr));
        }
        if (maxLenStr != null && !"".equals(maxLenStr)) {
            this.setMaxLength(Integer.parseInt(maxLenStr));
        }
        if (minOccurStr != null && !"".equals(minOccurStr)) {
            this.setMinOccurrences(Integer.parseInt(minOccurStr));
        }
        if (maxOccurStr != null && !"".equals(maxOccurStr)) {
            this.setMaxOccurrences(Integer.parseInt(maxOccurStr));
        }
        if (limit != null && !"".equalsIgnoreCase(limit)) {
            this.setLimit(Integer.parseInt(limit));
        }
        if (csv_format != null && !"".equalsIgnoreCase(csv_format)) {
            this.setFormat(csv_format.trim());
        }
        if (defaultStr != null && !"".equals(defaultStr)) {
            this.defaultValue = defaultStr;
        }
        if (SecurityUtil.isValid(index)) {
            if (SecurityUtil.isValid(this.paramName)) {
                ParameterRule.logger.log(Level.SEVERE, "Incorrect configuration for key/param {0} , \"name\" and \"index\" both should not be used together , use only one of them : {0}", new Object[] { this.paramName });
                throw new IAMSecurityException("INVALID_CONFIGURATION");
            }
            this.createIndexRange(index);
        }
        if (xssStr != null && !"".equals(xssStr)) {
            this.setXSSValidation(xssStr);
        }
        if (splitStr != null && !"".equals(splitStr)) {
            this.setSplit(splitStr);
        }
        if ("true".equals(element.getAttribute("canonicalize"))) {
            this.setCanonicalize(true);
        }
        if ("true".equals(element.getAttribute("deltacontent"))) {
            this.setDeltaContent(true);
        }
        this.isParamNameInRegex = "true".equals(element.getAttribute("name-regex"));
        final String saltStr = element.getAttribute("salt-delimiter");
        if (saltStr != null && !"".equals(saltStr)) {
            this.saltDelimiter = saltStr;
        }
        if (saltValidityStr != null && !"".equals(saltValidityStr)) {
            this.setSaltValidity(Integer.parseInt(saltValidityStr));
        }
        this.decrypt = "true".equalsIgnoreCase(element.getAttribute("decrypt"));
        final String decryptStr = element.getAttribute("decrypt-label");
        if (SecurityUtil.isValid(decryptStr)) {
            this.decrypt_label = decryptStr;
        }
        this.saltTicket = "true".equalsIgnoreCase(element.getAttribute("salt-ticket"));
        final String trimStr = element.getAttribute("trim");
        if (SecurityUtil.isValid(trimStr)) {
            this.setTrim("true".equalsIgnoreCase(trimStr));
        }
        final boolean secret = "true".equals(element.getAttribute("secret"));
        if (secret) {
            this.mask = "required";
        }
        final String mask = element.getAttribute("mask");
        if (SecurityUtil.isValid(mask)) {
            this.mask = mask;
            if ("partial".equals(this.mask)) {
                if (this.isParamNameInRegex) {
                    ParameterRule.logger.log(Level.SEVERE, "Invalid Masking configuration :: Partial masking is not supported for ''name-regex=\"true\"'' configured param : {0}", this.paramName);
                    throw new IAMSecurityException("INVALID_CONFIGURATION");
                }
                final String preserveChars = element.getAttribute("preserve-chars");
                if (SecurityUtil.isValid(preserveChars)) {
                    this.preserveChars = preserveChars;
                }
                final String[] values = this.preserveChars.split(",", 2);
                this.preserveCharPrefix = Integer.parseInt(values[0].trim());
                this.preserveCharSuffix = ((values.length > 1) ? Integer.parseInt(values[1].trim()) : 0);
                if (this.preserveCharPrefix < 0 || this.preserveCharSuffix < 0) {
                    ParameterRule.logger.log(Level.SEVERE, "Invalid partial masking configuration :: ''preserve-char'' values should be a positive integer values", this.paramName);
                    throw new IAMSecurityException("INVALID_CONFIGURATION");
                }
                final String preserveCharsLimit = element.getAttribute("preserve-chars-limit");
                if (SecurityUtil.isValid(preserveCharsLimit)) {
                    this.preserveCharsLimitInPercent = Integer.parseInt(preserveCharsLimit);
                    if (this.preserveCharsLimitInPercent > 50) {
                        ParameterRule.logger.log(Level.SEVERE, "Invalid partial mask configuration : \"preserve-chars-limit\" value should not exceed 50 as it may lead to sensitive info leakage", this.paramName);
                        throw new IAMSecurityException("INVALID_CONFIGURATION");
                    }
                }
            }
        }
        final String allowEmpty = element.getAttribute("allow-empty");
        if (SecurityUtil.isValid(allowEmpty)) {
            if (this.dataType != null && this.dataType.startsWith("JSON")) {
                ParameterRule.logger.log(Level.SEVERE, " \"allow-empty\" configuration should not be used with the type= \"JSONObject\" or \"JSONArray\" use \"allow-invalid\" instead for key/Param : \"{0}\" ", this.paramName);
                throw new IAMSecurityException("INVALID_CONFIGURATION");
            }
            this.setAllowEmpty("true".equalsIgnoreCase(allowEmpty));
        }
        final String allowInvalid = element.getAttribute("allow-invalid");
        if (SecurityUtil.isValid(allowInvalid)) {
            this.allowInvalid = !"false".equals(element.getAttribute("allow-invalid"));
        }
        this.allowXMLInlineEntityExpansion = "true".equals(element.getAttribute("allow-xml-inline-entity-expansion"));
        final String labelStr = element.getAttribute("label");
        if (SecurityUtil.isValid(labelStr)) {
            this.setLabel(labelStr);
        }
        if ("JSONArray".equals(this.dataType) || "JSONObject|JSONArray".equals(this.dataType) || "vcardArray".equals(this.dataType)) {
            final String arraySize = element.getAttribute("array-size");
            if (SecurityUtil.isValid(arraySize)) {
                this.arraySize = arraySize;
            }
            this.setArraysizeInRange(this.arraySize);
        }
        this.isEditableOnValidation = (this.xssValidation != null || this.defaultValue != null || this.decrypt_label != null || this.deltaContent || this.isSplitDefined || this.decrypt);
        this.validateParameterRuleConf();
    }
    
    public UploadFileRule getFileRuleForStreamContent() {
        return this.fileRuleForStreamContent;
    }
    
    public String getDynamicTemplateKeyRegex() {
        return this.dynamicTemplateKeyRegex;
    }
    
    void createIndexRange(final String index) {
        (this.index = RangeUtil.createFixedRangeForInteger("index", index, 10, false)).setRangeNotation(index);
    }
    
    public UploadFileRule getFileRuleForImportedData() {
        return this.fileRuleForImportedData;
    }
    
    private void validateParameterRuleConf() {
        if ((!this.isParamNameInRegex || this.isMaxOccurrenceConfigured) && this.maxOccurrences != -1 && (this.minOccurrences < 0 || this.maxOccurrences < 1 || this.minOccurrences > this.maxOccurrences)) {
            ParameterRule.logger.log(Level.SEVERE, "Incorrect min/max occurrences configuration : {0}", this);
            throw new IAMSecurityException("INVALID_CONFIGURATION");
        }
        if (!SecurityUtil.isValid(this.xssValidation) && !SecurityUtil.isValid(this.dataType) && !SecurityUtil.isValid(this.allowedValueRegex) && !this.isImportFile) {
            ParameterRule.logger.log(Level.SEVERE, "Incorrect security configuration - \"XSS\" or \"TYPE\"  or \"REGEX\" is mandatory : {0}", this);
            throw new IAMSecurityException("INVALID_CONFIGURATION");
        }
        if (SecurityUtil.isValid(this.dataType) && SecurityUtil.isValid(this.xssValidation) && !this.dataType.startsWith("JSON") && !"xml".equalsIgnoreCase(this.dataType)) {
            ParameterRule.logger.log(Level.SEVERE, "Incorrect security configuration - Either \"TYPE\" or \"XSS\" should be used for the param : \"{0}\", both are allowed only for JSON and XML datatypes : {1}", new Object[] { this.paramName, this });
            throw new IAMSecurityException("INVALID_CONFIGURATION");
        }
        this.dataTypeSpecificChecks();
    }
    
    private void dataTypeSpecificChecks() {
        if (SecurityUtil.isValid(this.dataType) && this.dataType.equals("csv") && (!SecurityUtil.isValid(this.format) || !CsvValidator.CSV_SUPPORTED_FORMATS.contains(this.format))) {
            ParameterRule.logger.log(Level.SEVERE, " Invalid CSV FORMAT :{0} \t expected one from : {1} refer the Configuration {2}", new Object[] { this.format, CsvValidator.CSV_SUPPORTED_FORMATS.toString(), this });
            throw new IAMSecurityException(" INVALID CSV FORMAT ");
        }
    }
    
    private void setRuleName(final String tagName) {
        if (tagName == "param" || tagName == "extraparam" || tagName == "paramorstream") {
            this.ruleName = "param";
        }
        else {
            this.ruleName = tagName;
        }
    }
    
    private void extractJSONTemplate(final String jsonTemplateText) {
        final Matcher matcher = ParameterRule.JSON_TEMPLATE_PATTERN.matcher(jsonTemplateText);
        if (matcher.find()) {
            final String templateParam = matcher.group(1);
            final String templatePrefix = matcher.group(2);
            final String jsonStaticTemplate = matcher.group(3);
            if (SecurityUtil.isValid(templateParam)) {
                this.templateParam = templateParam.substring(2, templateParam.length() - 1);
            }
            if (SecurityUtil.isValid(templatePrefix)) {
                if (this.templateParam == null) {
                    ParameterRule.logger.log(Level.SEVERE, " \"templatePrefix\" for a JSON must be defined with a valid \"templateParam\" expected a valid Format like template=\"${templateParam}_TemplatePrefix_\" for : {0} & Be careful with the use of _(underscore) it is a reserve char for <template>", this);
                    throw new IAMSecurityException("INVALID_CONFIGURATION");
                }
                this.templatePrefix = templatePrefix.substring(1, templatePrefix.length() - 1);
            }
            if (SecurityUtil.isValid(jsonStaticTemplate)) {
                this.setJSONTemplate(jsonStaticTemplate);
            }
        }
    }
    
    public String getArraySize() {
        return this.arraySize;
    }
    
    public void setArraySize(final String size) {
        this.setArraysizeInRange(this.arraySize = size);
    }
    
    Range<Integer> getArraysizeInRange() {
        if (this.arraySizeInRange == null) {
            this.setArraysizeInRange(this.arraySize);
        }
        return this.arraySizeInRange;
    }
    
    void setArraysizeInRange(final String size) {
        try {
            this.arraySizeInRange = RangeUtil.createFixedRangeForInteger("array-size", size, Integer.MAX_VALUE, false);
        }
        catch (final RuntimeException ex) {
            throw new RuntimeException("Invalid array-size configuration for the param : '" + this.paramName + "' and Error message : " + ex.getMessage());
        }
    }
    
    public String getJSONTemplate() {
        return this.jsonTemplate;
    }
    
    public void setJSONTemplate(final String str) {
        this.jsonTemplate = str;
    }
    
    public String getTemplateParam() {
        return this.templateParam;
    }
    
    public String getTemplatePrefix() {
        return this.templatePrefix;
    }
    
    public String getTemplateName() {
        return this.templateName;
    }
    
    public void setTemplateName(final String templateName) {
        this.templateName = templateName;
    }
    
    public boolean isEnabledXMLSchemaValidation() {
        return this.xmlSchemaValidation;
    }
    
    public void setXMLSchemaValidation(final boolean schemaValidation) {
        this.xmlSchemaValidation = schemaValidation;
    }
    
    public String getXMLSchemaName() {
        return this.xmlSchemaName;
    }
    
    public void setXMLSchemaName(final String xmlSchema) {
        this.xmlSchemaName = xmlSchema;
    }
    
    public String getParamName() {
        return this.paramName;
    }
    
    public Range getIndexRange() {
        return this.index;
    }
    
    public String getDefaultValue() {
        return this.defaultValue;
    }
    
    public void setParamNameInRegex(final Pattern paramNameInRegex) {
        this.paramNameInRegex = paramNameInRegex;
    }
    
    public String getXSSValidation() {
        return this.xssValidation;
    }
    
    public void setParamName(final String paramName) {
        this.paramName = paramName;
    }
    
    public String getAllowedValueRegex() {
        return this.allowedValueRegex;
    }
    
    public void setAllowedValueRegex(final String regexPattern) {
        if (regexPattern != null) {
            if ("binary".equals(regexPattern)) {
                this.allowedValueRegex = regexPattern.trim();
            }
            else if (regexPattern.toLowerCase().startsWith("xss:")) {
                this.setXSSValidation(regexPattern.substring("xss:".length(), regexPattern.length()));
            }
            else if (ParameterRule.supportedDataTypes.matcher(regexPattern).matches()) {
                this.setDataType(regexPattern);
            }
            else {
                this.allowedValueRegex = regexPattern.trim();
            }
        }
    }
    
    public void setXSSValidation(final String xssPattern) {
        this.xssValidation = xssPattern.trim();
    }
    
    public int getMinLength() {
        return this.minLength;
    }
    
    public void setMinLength(final int minLength) {
        this.minLength = minLength;
    }
    
    public int getMaxLength() {
        return this.maxLength;
    }
    
    public void setMaxLength(final int maxLength) {
        this.maxLength = maxLength;
    }
    
    public int getMinOccurrences() {
        return this.minOccurrences;
    }
    
    public void setMinOccurrences(final int minOccurrences) {
        this.minOccurrences = minOccurrences;
    }
    
    public int getMaxOccurrences() {
        return this.maxOccurrences;
    }
    
    public String getFormat() {
        return this.format;
    }
    
    public void setFormat(final String format) {
        this.format = format;
    }
    
    public void setDataType(final String typeStr) {
        this.dataType = typeStr.trim();
        if (ParameterRule.primitiveDataTypes.matcher(this.dataType).matches()) {
            this.isPrimitive = true;
        }
        else if ("vcardArray".equals(typeStr)) {
            this.arraySize = "0-1";
        }
    }
    
    public void setLabel(final String labelStr) {
        this.labelStr = labelStr;
        if (this.labelMap == null) {
            this.labelMap = new HashMap<String, String>();
        }
        SecurityUtil.addToLabelMap(this.labelMap, labelStr, this);
    }
    
    public String getLabel() {
        return this.labelStr;
    }
    
    public String substituteAttributeInLabelMsg(String labelMsg) {
        String attribute = null;
        if (labelMsg.contains("$")) {
            final Matcher matcher = ParameterRule.LABEL_MESSAGE_PATTERN.matcher(labelMsg);
            if (matcher.find()) {
                attribute = matcher.group(1);
                ATTRIBUTES col;
                try {
                    col = ATTRIBUTES.valueOf(attribute);
                }
                catch (final IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Unsupported attribute '" + attribute + "' in label message for the param " + this.paramName);
                }
                switch (col) {
                    case type: {
                        if (this.dataType == null) {
                            throw new IllegalArgumentException("Invalid label configuration for the param '" + this.paramName + "' DataType is null");
                        }
                        labelMsg = labelMsg.replace("$type", this.dataType);
                        break;
                    }
                    case maxlen: {
                        labelMsg = labelMsg.replace("$maxlen", String.valueOf(this.maxLength));
                        break;
                    }
                    case minlen: {
                        labelMsg = labelMsg.replace("$minlen", String.valueOf(this.minLength));
                        break;
                    }
                    case maxoccur: {
                        labelMsg = labelMsg.replace("$maxoccur", String.valueOf(this.maxOccurrences));
                        break;
                    }
                    case minoccur: {
                        labelMsg = labelMsg.replace("$minoccur", String.valueOf(this.minOccurrences));
                        break;
                    }
                    case split: {
                        if (this.split == null) {
                            throw new IllegalArgumentException("Invalid label configuration for the param '" + this.paramName + "' split is null");
                        }
                        labelMsg = labelMsg.replace("$split", this.split);
                        break;
                    }
                    case defaultvalue: {
                        if (this.defaultValue == null) {
                            throw new IllegalArgumentException("Invalid label configuration for the param '" + this.paramName + "' default value is null");
                        }
                        labelMsg = labelMsg.replace("$defaultvalue", this.defaultValue);
                        break;
                    }
                }
            }
        }
        return labelMsg;
    }
    
    public String getLabelMessage(final HttpServletRequest request, final String errorCode) {
        String labelMsg = null;
        String defaultLabelMsg = null;
        if (this.labelMap != null) {
            if (this.labelMap.containsKey(errorCode)) {
                labelMsg = this.labelMap.get(errorCode);
            }
            else {
                defaultLabelMsg = this.labelMap.get("ZSEC_DEFAULT_LABEL");
            }
        }
        final HashMap<String, String> defaultLabelMap = SecurityFilterProperties.getInstance(request).getDefaultLabelMap();
        if (defaultLabelMap != null) {
            if (labelMsg == null && defaultLabelMap.containsKey(errorCode)) {
                try {
                    labelMsg = this.substituteAttributeInLabelMsg(defaultLabelMap.get(errorCode));
                    return (labelMsg != null) ? labelMsg : defaultLabelMsg;
                }
                catch (final IllegalArgumentException e) {
                    throw e;
                }
                catch (final Exception e2) {
                    defaultLabelMsg = ((defaultLabelMsg != null) ? defaultLabelMsg : defaultLabelMap.get("ZSEC_DEFAULT_LABEL"));
                    return (labelMsg != null) ? labelMsg : defaultLabelMsg;
                }
            }
            if (defaultLabelMsg == null) {
                defaultLabelMsg = defaultLabelMap.get("ZSEC_DEFAULT_LABEL");
            }
        }
        return (labelMsg != null) ? labelMsg : defaultLabelMsg;
    }
    
    String matchAndReplace(final SecurityRequestWrapper request, final String paramName, final String parameterValue) {
        if (this.allowedValueRegex == null || this.allowedValueRegex.length() == 0) {
            return parameterValue;
        }
        final Pattern rx = SecurityUtil.getRegexPattern(request, this.allowedValueRegex);
        if (rx == null) {
            ParameterRule.logger.log(Level.SEVERE, " regex pattern name : {0} is not defined in the configuration files , kindly recheck ", this.allowedValueRegex);
            throw new IAMSecurityException("PATTERN_NOT_MATCHED", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
        }
        return rx.matcher(parameterValue).replaceAll(this.replacementString).trim();
    }
    
    public String getDataType() {
        return this.dataType;
    }
    
    public void setMaxOccurrences(final int maxOccurrences) {
        this.isMaxOccurrenceConfigured = true;
        this.maxOccurrences = maxOccurrences;
    }
    
    public boolean isCanonicalize() {
        return this.canonicalize;
    }
    
    public void setCanonicalize(final boolean canonicalize) {
        this.canonicalize = canonicalize;
    }
    
    public boolean isDeltaContent() {
        return this.deltaContent;
    }
    
    public void setDeltaContent(final boolean deltaContent) {
        this.deltaContent = deltaContent;
    }
    
    public void setSplit(final String split) {
        this.split = split;
        this.splitPattern = Pattern.compile(split);
        this.isSplitDefined = true;
    }
    
    public String getSplit() {
        return this.split;
    }
    
    public void validate(final SecurityRequestWrapper request) {
        String parameterName = this.getParamName();
        try {
            if (this.isParamNameInRegex()) {
                int noOfOccurrence = 0;
                final boolean enableIndividualOccurrenceCheck = SecurityFilterProperties.getInstance((HttpServletRequest)request).isEnabledIndividualOccurrenceCheckForDynamicParams();
                final Enumeration<String> e = request.getParameterNames();
                if (e.hasMoreElements()) {
                    final SecurityFilterProperties filterConfig = SecurityFilterProperties.getInstance((HttpServletRequest)request);
                    RegexRule regexRule = filterConfig.getRegexRule(parameterName);
                    if (regexRule == null) {
                        regexRule = new RegexRule(this.paramName, this.paramName);
                        this.paramNameInRegex = regexRule.getPattern();
                    }
                    while (e.hasMoreElements()) {
                        parameterName = e.nextElement();
                        if (!request.isValidated(parameterName) && !request.isInvalidParam(parameterName) && SecurityUtil.matchPattern(parameterName, regexRule)) {
                            noOfOccurrence += this.getNumberOfOccurances(request.getParameterValuesForValidation(parameterName));
                            if (!enableIndividualOccurrenceCheck) {
                                this.checkForMaxOccurrence(request, noOfOccurrence, parameterName);
                            }
                            this.validateParam(request, parameterName);
                        }
                    }
                }
                if (!enableIndividualOccurrenceCheck) {
                    this.checkForMinOccurrence(request, noOfOccurrence, parameterName);
                }
            }
            else {
                this.validateParam(request, parameterName);
            }
        }
        catch (final IAMSecurityException e2) {
            if (((ActionRule)request.getAttribute("urlrule")).throwAllErrors()) {
                request.addInvalidParam(parameterName);
            }
            throw e2;
        }
    }
    
    boolean isDecryptionEnabled() {
        return this.isEncryptedParam() || this.getDecryptLabel() != null;
    }
    
    public boolean isEncryptedParam() {
        return this.decrypt;
    }
    
    public void setEncrypted(final boolean encrypt) {
        this.decrypt = encrypt;
    }
    
    public void setTicketAsSalt(final boolean ticketAsSalt) {
        this.saltTicket = ticketAsSalt;
    }
    
    public boolean isTicketAsSalt() {
        return this.saltTicket;
    }
    
    public boolean isInputStream(final String paramName) {
        return "zoho-inputstream".equals(paramName);
    }
    
    public long getSaltValidity() {
        return this.saltValidity / 60000L;
    }
    
    public void setSaltValidity(final long noOfMin) {
        this.saltValidity = noOfMin * 60L * 1000L;
    }
    
    public void setRange(final String range) {
        this.rangeStr = range;
        if (SecurityUtil.isValid(range) && SecurityUtil.isValid(this.dataType)) {
            final String[] rangeVal = range.split(",");
            this.ranges = new ArrayList<Range<?>>();
            for (int i = 0; i < rangeVal.length; ++i) {
                this.ranges.add(RangeUtil.createRange(rangeVal[i], this.dataType));
            }
        }
    }
    
    public String getRange() {
        return this.rangeStr;
    }
    
    public void validateParam(final SecurityRequestWrapper request, String paramName) {
        String[] parameterValues = null;
        boolean isInputStream = false;
        if (this.paramOrStreamConfig) {
            parameterValues = request.getParameterValuesForValidation(paramName);
            if (parameterValues == null && SecurityUtil.isValid(request.getOriginalInputStreamContent())) {
                parameterValues = new String[] { request.getOriginalInputStreamContent() };
                paramName = "zoho-inputstream";
                isInputStream = true;
            }
        }
        else if (this.isInputStream(paramName)) {
            if (this.isImportFile) {
                this.validateStreamContentAsFile(request);
                return;
            }
            if (this.isInputStreamTypeBinary()) {
                return;
            }
            parameterValues = (String[])(SecurityUtil.isValid(request.getOriginalInputStreamContent()) ? new String[] { request.getOriginalInputStreamContent() } : null);
        }
        else {
            parameterValues = request.getParameterValuesForValidation(paramName);
        }
        boolean partialMaskingEnabled = false;
        SecurityLogRequestWrapper logRequest = null;
        if (request.enableSecretValueMasking && parameterValues != null && this.storeParameterValue && this.isSecretParam(request, paramName)) {
            logRequest = (SecurityLogRequestWrapper)SecurityUtil.getCurrentLogRequest();
            if (this.isMaskingRequiredPartially()) {
                partialMaskingEnabled = true;
                logRequest.addPartiallyMaskedParameter(paramName, new ArrayList<String>());
            }
            else {
                logRequest.addSecretParameter(paramName);
            }
        }
        if (this.isInputStream(paramName) && !this.paramOrStreamConfig && this.isValidInputStreamNeeded && parameterValues == null) {
            ParameterRule.logger.log(Level.SEVERE, "Invalid Inputstream passed through this request : {0} , param Value : {1}", new Object[] { request.getRequestURI(), parameterValues });
            throw new IAMSecurityException("INVALID_INPUTSTREAM");
        }
        if (!isInputStream) {
            final int noOfOccurance = this.getNumberOfOccurances(parameterValues);
            final boolean doOccurrenceCheck = this.isParamNameInRegex ? SecurityFilterProperties.getInstance((HttpServletRequest)request).isEnabledIndividualOccurrenceCheckForDynamicParams() : (!this.skipDefaultMaxOccurrenceCheckForOrCriteriaParam);
            if (doOccurrenceCheck) {
                this.checkForMinOccurrence(request, noOfOccurance, paramName);
            }
            if (parameterValues == null) {
                return;
            }
            if (doOccurrenceCheck) {
                this.checkForMaxOccurrence(request, noOfOccurance, paramName);
            }
        }
        for (String parameterValue : parameterValues) {
            if (parameterValue != null) {
                if (partialMaskingEnabled) {
                    logRequest.getPartiallyMaskedParameter(paramName).add(MaskUtil.getPartiallyMaskedValue(parameterValue, this));
                }
                if (this.filterMatchedPattern) {
                    parameterValue = this.matchAndReplace(request, paramName, parameterValue);
                }
                if (parameterValue.length() != 0) {
                    if (this.isSplitDefined) {
                        final String[] split;
                        final String[] subParamValues = split = this.splitPattern.split(parameterValue);
                        for (final String subParamValue : split) {
                            this.validateParamValue(request, paramName, subParamValue);
                        }
                    }
                    else {
                        this.validateParamValue(request, paramName, parameterValue);
                    }
                }
                else {
                    if (!this.isEmptyValueAllowed()) {
                        ParameterRule.logger.log(Level.SEVERE, "Empty value is not allowed for the param \"{0}\" for the request url \"{1}\" in the param rule : \n {2}", new Object[] { paramName, request.getRequestURI(), this.toString() });
                        throw new IAMSecurityException("EMPTY_VALUE_NOT_ALLOWED", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
                    }
                    if (SecurityUtil.isValid(this.dataType) && this.dataType.startsWith("JSON") && !this.isInvalidAllowed()) {
                        JSONTemplateRule.invalidValueCheck(this, (HttpServletRequest)request, SecurityFilterProperties.JSON_INVALID_VALUE_TYPE.EMPTYSTRING, null);
                    }
                    else if (this.storeParameterValue) {
                        request.addValidatedParameterValue(paramName, parameterValue, this);
                    }
                }
                request.addValidatedParameter(paramName);
            }
        }
    }
    
    private void validateStreamContentAsFile(final SecurityRequestWrapper request) {
        UploadedFileItem fileItem = null;
        try {
            fileItem = SecurityUtil.createUploadedFileItem("zoho-inputstream", null, null, this.getFileRuleForStreamContent(), (InputStream)request.getInputStream(), null, request);
        }
        catch (final Exception ex) {
            if (ex instanceof IAMSecurityException) {
                throw (IAMSecurityException)ex;
            }
            ParameterRule.logger.log(Level.SEVERE, "Error occurred while processing the stream content in the request URI : {0}, ErrorMsg : {1}", new Object[] { request.getRequestURI(), ex.getMessage() });
            throw new IAMSecurityException("UNABLE_TO_READ_INPUTSTREAM");
        }
        if (fileItem != null) {
            this.fileRuleForStreamContent.validate(fileItem);
            request.setStreamContentAsFile(fileItem);
        }
        else if (this.isValidInputStreamNeeded) {
            ParameterRule.logger.log(Level.SEVERE, "Invalid Inputstream passed through this request, URI : {0} ", new Object[] { request.getRequestURI() });
            throw new IAMSecurityException("INVALID_INPUTSTREAM");
        }
    }
    
    private boolean isSecretParam(final SecurityRequestWrapper request, final String paramName) {
        if (this.isMaskingEnabled()) {
            return true;
        }
        final List<String> globalSecretParams = SecurityFilterProperties.getInstance((HttpServletRequest)request).getSecretRequestParamsFromProperty();
        return globalSecretParams != null && globalSecretParams.contains(paramName.toLowerCase());
    }
    
    private void checkForMinOccurrence(final SecurityRequestWrapper request, final int noOfOccurance, final String paramName) {
        if (isParamOccuranceLessThanMinLimit(noOfOccurance, this.getMinOccurrences(), request)) {
            ParameterRule.logger.log(Level.SEVERE, "The parameter \"{0}\" for the URL \"{1}\" is less than the minimum occurances configured in the param rule :\n{2}", new Object[] { paramName, request.getRequestURI(), this.toString() });
            throw new IAMSecurityException("LESS_THAN_MIN_OCCURANCE", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, noOfOccurance, this);
        }
    }
    
    void checkForMaxOccurrence(final SecurityRequestWrapper request, final int noOfOccurance, final String paramName) {
        if (isParamOccuranceExceedsMaxLimit(noOfOccurance, this.getMaxOccurrences())) {
            ParameterRule.logger.log(Level.SEVERE, "The parameter \"{0}\" for the URL \"{1}\" is more than the maximum occurances configured in the param rule :\n {2}", new Object[] { paramName, request.getRequestURI(), this.toString() });
            throw new IAMSecurityException("MORE_THAN_MAX_OCCURANCE", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, noOfOccurance, this);
        }
    }
    
    static boolean isParamOccuranceExceedsMaxLimit(final int paramOccurance, final int configuredMaxOccurance) {
        return configuredMaxOccurance != -1 && configuredMaxOccurance < paramOccurance;
    }
    
    static boolean isParamOccuranceLessThanMinLimit(final int paramOccurance, final int configuredMinOccurance, final SecurityRequestWrapper request) {
        final ActionRule rule = request.getURLActionRule();
        return configuredMinOccurance > paramOccurance && rule != null && isEnabledMinOccurrenceCheck(request, rule);
    }
    
    static boolean isEnabledMinOccurrenceCheck(final SecurityRequestWrapper request, final ActionRule rule) {
        return !rule.isPatchURL() || SecurityFilterProperties.getInstance((HttpServletRequest)request).isEnabledPatchMethodMinOccurCheck();
    }
    
    public String canonicalize(final String parameterValue) {
        if (this.canonicalize) {
            return parameterValue;
        }
        return parameterValue;
    }
    
    public void setParamNameInRegex(final boolean b) {
        if (b) {
            this.paramNameInRegex = Pattern.compile(this.paramName);
            this.isParamNameInRegex = b;
        }
    }
    
    public boolean isParamNameInRegex() {
        return this.isParamNameInRegex;
    }
    
    public Pattern getParamNameRegex() {
        return this.paramNameInRegex;
    }
    
    public String getSaltDelimiter() {
        return this.saltDelimiter;
    }
    
    public void setSaltDelimiter(final String saltDelimiter) {
        this.saltDelimiter = saltDelimiter;
    }
    
    public boolean isContentReplacementEnabled() {
        return this.filterMatchedPattern;
    }
    
    public void setCustomAttribute(final String name, final String value) {
        if (this.customAttributes == null) {
            this.customAttributes = new HashMap<String, String>();
        }
        this.customAttributes.put(name, value);
    }
    
    public String getCustomAttribute(final String name) {
        if (this.customAttributes != null) {
            return this.customAttributes.get(name);
        }
        return null;
    }
    
    private String decryptValue(final SecurityRequestWrapper request, final String paramName, final String parameterValue) {
        if ("".equals(parameterValue)) {
            return parameterValue;
        }
        if (this.isEncryptedParam() || this.decrypt_label != null) {
            final SecurityFilterProperties filterProps = SecurityFilterProperties.getInstance((HttpServletRequest)request);
            String value = filterProps.getSecurityProvider().decrypt((HttpServletRequest)request, paramName, parameterValue, this.decrypt_label);
            if (value != null) {
                return value;
            }
            if (filterProps.isAuthenticationProviderConfigured()) {
                try {
                    final Authenticator authProviderImpl = filterProps.getAuthenticationProvider();
                    value = authProviderImpl.decrypt(this.decrypt_label, parameterValue);
                    if (this.saltDelimiter == null || "".equals(this.saltDelimiter)) {
                        return value;
                    }
                    final String[] temp = value.split(this.saltDelimiter);
                    if (this.saltTicket && !temp[1].equals(authProviderImpl.GET_IAM_COOKIE((HttpServletRequest)request))) {
                        throw new IAMSecurityException("INVALID_TICKET_SALT", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
                    }
                    if (this.saltValidity != -1L && System.currentTimeMillis() - Long.parseLong(temp[1]) > this.saltValidity) {
                        throw new IAMSecurityException("INVALID_TIME_SALT", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
                    }
                    return temp[0];
                }
                catch (final IAMSecurityException ex) {
                    throw ex;
                }
                catch (final Exception ex2) {
                    ParameterRule.logger.log(Level.SEVERE, "Exception while decrypting the param {0}, value {1} for the URL {2}. {3}", new Object[] { paramName, this.getParameterValue(parameterValue, paramName), request.getRequestURI(), ex2 });
                    throw new IAMSecurityException("UNABLE_TO_DECRYPT", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
                }
            }
        }
        return parameterValue;
    }
    
    int getNumberOfOccurances(final String[] parameterValues) {
        int noOfOccurance = 0;
        if (parameterValues == null) {
            return noOfOccurance;
        }
        for (final String value : parameterValues) {
            if (!value.trim().equals("")) {
                if (this.isSplitDefined) {
                    noOfOccurance += this.splitPattern.split(value).length;
                }
                else {
                    ++noOfOccurance;
                }
            }
        }
        return noOfOccurance;
    }
    
    String getParameterValue(final String parameterValue, final String paramName) {
        final HttpServletRequest request = SecurityUtil.getCurrentRequest();
        final SecurityFilterProperties filterConfig = SecurityFilterProperties.getInstance(request);
        if (filterConfig != null && ((filterConfig.isExtraParamMaskingEnabled() && this.isExtraParamRule) || MaskUtil.isSecretComponent(paramName, filterConfig, this))) {
            return "*****";
        }
        return parameterValue;
    }
    
    private String validateParamValue(final SecurityRequestWrapper request, final String paramName, final String parameterValue) {
        return this.validateParamValue(request, paramName, parameterValue, null);
    }
    
    String validateParamValue(final SecurityRequestWrapper request, final String paramName, String parameterValue, final ParameterRule embedParamRule) {
        if (this.isTrim((HttpServletRequest)request)) {
            parameterValue = parameterValue.trim();
        }
        parameterValue = this.decryptValue(request, paramName, parameterValue);
        this.checkParamValueLength((HttpServletRequest)request, paramName, parameterValue);
        if (this.isDeltaContent()) {
            final SecurityProvider securityProvider = SecurityFilterProperties.getInstance((HttpServletRequest)request).getSecurityProvider();
            if (securityProvider != null) {
                ParameterRule.logger.log(Level.FINE, "ParamName : {0}  DELTA CONTENT : {1} ", new Object[] { paramName, this.getParameterValue(parameterValue, paramName) });
                parameterValue = securityProvider.getCompleteContent((HttpServletRequest)request, paramName, parameterValue);
                ParameterRule.logger.log(Level.FINE, "ParamName : {0}  COMPLETE CONTENT : {1}", new Object[] { paramName, this.getParameterValue(parameterValue, paramName) });
            }
        }
        if (request.disableParamInputValidationForTestingOE && this.storeParameterValue) {
            this.storeParameterValueObjectWithoutValidation(request, paramName, parameterValue, embedParamRule);
            if (embedParamRule == null) {
                final SecurityFilterProperties secFilterProp = SecurityFilterProperties.getInstance((HttpServletRequest)request);
                final Pattern ignoredRegex = secFilterProp.getOETestExcludedRegexPattern();
                final Pattern ignoredParams = secFilterProp.getOETestExcludedParams();
                if (paramName != null && ignoredParams != null && !ignoredParams.matcher(paramName).matches() && ("String".equals(this.getDataType()) || "cleartext:check".equals(this.getDataType()) || "cleartext:filter".equals(this.getDataType()) || (this.allowedValueRegex != null && !"".equals(this.allowedValueRegex) && !ignoredRegex.matcher(this.allowedValueRegex).matches()))) {
                    ParameterRule.logger.log(Level.INFO, "Skipping Validation for ParamName : {0}  as DisableParamInputValidationForTestingOutputEncoding is SET", new Object[] { paramName });
                    parameterValue = "<xss'\">";
                }
                request.addValidatedParameterValue(paramName, parameterValue, this);
            }
            return parameterValue;
        }
        parameterValue = this.canonicalize(parameterValue);
        parameterValue = this.checkForDataType(request, paramName, parameterValue, embedParamRule);
        this.checkForRange((HttpServletRequest)request, paramName, parameterValue);
        parameterValue = this.checkForXSS(request, paramName, parameterValue);
        parameterValue = this.checkForAllowedValueRegex(request, paramName, parameterValue);
        if (embedParamRule == null && this.storeParameterValue) {
            request.addValidatedParameterValue(paramName, parameterValue, this);
        }
        return parameterValue;
    }
    
    void checkParamValueLength(final HttpServletRequest request, final String paramName, final String parameterValue) {
        if (this.getMinLength() > parameterValue.length()) {
            ParameterRule.logger.log(Level.SEVERE, "The parameter \"{0}\" for the URL \"{1}\" is less than the minimum length configured in the param rule :\n{2}", new Object[] { paramName, request.getRequestURI(), this.toString() });
            throw new IAMSecurityException("LESS_THAN_MIN_LENGTH", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
        }
        if (this.getMaxLength() != -1 && this.getMaxLength() < parameterValue.length()) {
            ParameterRule.logger.log(Level.SEVERE, "The parameter \"{0}\" for the URL \"{1}\" is greater than the maximum length configured in the param rule :\n{2}", new Object[] { paramName, request.getRequestURI(), this.toString() });
            throw new IAMSecurityException("MORE_THAN_MAX_LENGTH", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
        }
    }
    
    String checkForXSS(final SecurityRequestWrapper request, final String paramName, final String parameterValue) {
        if (this.xssValidation == null || this.xssValidation.length() == 0 || (this.dataType != null && this.dataType.length() > 0)) {
            return parameterValue;
        }
        final ExecutionTimer xsstimer = ExecutionTimer.startInstance();
        try {
            final String filterStr = SecurityUtil.applyXSSPattern(request, this.xssValidation, paramName, parameterValue);
            ZSEC_PERFORMANCE_ANOMALY.pushXssValidation(request.getRequestURI(), paramName, this.xssValidation, (String)null, xsstimer);
            return filterStr;
        }
        catch (final IAMSecurityException ex) {
            if (ex.getErrorCode().equals("PATTERN_NOT_DEFINED") || ex.getErrorCode().equals("INVALID_XSSFILTER_CONFIGURATION") || ex.getErrorCode().equals("HTML_PARSE_FAILED")) {
                throw ex;
            }
            ZSEC_PERFORMANCE_ANOMALY.pushXssValidation(request.getRequestURI(), paramName, this.xssValidation, "XSS_DETECTED", xsstimer);
            ParameterRule.logger.log(Level.SEVERE, "XSS detected for the parameter \"{0}\" in the request URI \"{1}\". The value is  \"{2}\" and the param rule is :\n{3}", new Object[] { paramName, request.getRequestURI(), XSSUtil.getLogString(this.getParameterValue(parameterValue, paramName)), this.toString() });
            throw new IAMSecurityException("XSS_DETECTED", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
        }
    }
    
    String checkForAllowedValueRegex(final SecurityRequestWrapper request, final String paramName, final String parameterValue) {
        if (this.allowedValueRegex == null || this.allowedValueRegex.length() == 0 || this.filterMatchedPattern) {
            return parameterValue;
        }
        final ExecutionTimer rxMatchTimer = ExecutionTimer.startInstance();
        final SecurityFilterProperties filterProps = SecurityFilterProperties.getInstance((HttpServletRequest)request);
        final RegexRule regexRule = filterProps.getRegexRule(this.allowedValueRegex);
        if (regexRule != null) {
            if (!filterProps.isEnableTimeoutMatcher()) {
                if (SecurityUtil.matchPattern(parameterValue, regexRule.getPattern(), regexRule.getTimeOut(), regexRule.getIterationCount(), MaskUtil.isSecretComponent(paramName, filterProps, this))) {
                    ZSEC_PERFORMANCE_ANOMALY.pushRegexMatches(request.getRequestURI(), this.paramName, this.allowedValueRegex, (String)null, rxMatchTimer);
                    return parameterValue;
                }
            }
            else {
                ParameterRule.logger.log(Level.FINE, "Using timeout matcher utility - MatcherUtil for ParameterValue pattern checking : {0}", this.getParameterValue(parameterValue, paramName));
                try {
                    if (SecurityUtil.getMatcherUtil().matches(regexRule.getPattern(), parameterValue)) {
                        return parameterValue;
                    }
                }
                catch (final Exception e) {
                    ParameterRule.logger.log(Level.SEVERE, "", e);
                }
            }
        }
        ParameterRule.logger.log(Level.SEVERE, "Incorrect value for the parameter \"{0}\" in the request URI \"{1}\". The value is  \"{2}\" and loaded regex : {3} and param rule is :\n{4}", new Object[] { paramName, request.getRequestURI(), this.getParameterValue(parameterValue, paramName), (regexRule != null) ? regexRule.getValue() : null, this.toString() });
        ZSEC_PERFORMANCE_ANOMALY.pushRegexMatches(request.getRequestURI(), this.paramName, this.allowedValueRegex, "PATTERN_NOT_MATCHED", rxMatchTimer);
        throw new IAMSecurityException("PATTERN_NOT_MATCHED", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
    }
    
    String checkForDataType(final SecurityRequestWrapper request, final String paramName, final String parameterValue, final ParameterRule embedParamRule) {
        if (this.dataType == null || this.dataType.length() == 0 || "String".equals(this.dataType)) {
            return parameterValue;
        }
        Object resultObject = null;
        boolean isJSONTemplateDefined = false;
        boolean returnOriginalParamValue = true;
        try {
            if ("long".equals(this.dataType)) {
                resultObject = new Long(parameterValue);
            }
            else if ("int".equals(this.dataType)) {
                resultObject = new Integer(parameterValue);
            }
            else if ("float".equals(this.dataType)) {
                resultObject = new Float(parameterValue);
            }
            else if ("double".equals(this.dataType)) {
                resultObject = new Double(parameterValue);
            }
            else if ("short".equals(this.dataType)) {
                resultObject = new Short(parameterValue);
            }
            else if ("char".equals(this.dataType)) {
                if (parameterValue.length() == 1) {
                    resultObject = parameterValue.charAt(0);
                }
            }
            else if ("boolean".equals(this.dataType)) {
                if (parameterValue.equalsIgnoreCase("true") || parameterValue.equalsIgnoreCase("false")) {
                    resultObject = Boolean.valueOf(parameterValue);
                }
            }
            else if (this.dataType.startsWith("JSON")) {
                final ActionRule rule = request.getURLActionRule();
                final ParameterRule jsonParamRule = (embedParamRule != null) ? embedParamRule : this;
                final String jsonTemplate = jsonParamRule.getJSONTemplate();
                String type = this.dataType;
                if ("JSONObject|JSONArray".equals(this.dataType)) {
                    type = (parameterValue.trim().startsWith("{") ? "JSONObject" : "JSONArray");
                }
                Label_0569: {
                    if (SecurityUtil.isValid(jsonTemplate) || this.templateParam != null) {
                        isJSONTemplateDefined = true;
                        request.getURLActionRule().setJSONSecretParam(JSONTemplateRule.getKeyRule(jsonTemplate));
                        try {
                            if ("".equals(parameterValue.trim())) {
                                JSONTemplateRule.invalidValueCheck(jsonParamRule, (HttpServletRequest)request, SecurityFilterProperties.JSON_INVALID_VALUE_TYPE.EMPTYSTRING, null);
                                return parameterValue;
                            }
                            if ("null".equals(parameterValue)) {
                                JSONTemplateRule.invalidValueCheck(jsonParamRule, (HttpServletRequest)request, SecurityFilterProperties.JSON_INVALID_VALUE_TYPE.JSONNULL, null);
                                return parameterValue;
                            }
                            if ("JSONObject".equals(type)) {
                                resultObject = JSONTemplateRule.validateJSONObject(parameterValue, jsonParamRule);
                            }
                            else {
                                resultObject = JSONTemplateRule.validateJSONArray(parameterValue, jsonParamRule);
                            }
                            break Label_0569;
                        }
                        catch (final JSONException ex) {
                            ParameterRule.logger.log(Level.SEVERE, "\n Unable to parse String : \"{0}\" to : {1} & Exception Message : {2}", new Object[] { this.getParameterValue(parameterValue, paramName), this.dataType, ex.getMessage() });
                            final Exception jsonEx = new IAMSecurityException("JSON_PARSE_ERROR", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
                            jsonEx.initCause((Throwable)ex);
                            throw jsonEx;
                        }
                    }
                    if (this.xssValidation == null) {
                        ParameterRule.logger.log(Level.SEVERE, "Invalid Configuration found , JsonTemplate (static or Dynamic) is not defined for {0}", this);
                        throw new IAMSecurityException("INVALID_CONFIGURATION");
                    }
                    resultObject = SecurityUtil.applyJSONXSSPattern(request, type, this.xssValidation, paramName, parameterValue);
                }
                returnOriginalParamValue = false;
            }
            else if ("url".equals(this.dataType)) {
                final String urlValidatorName = this.getTemplateName();
                if (SecurityUtil.isValid(urlValidatorName)) {
                    resultObject = SecurityUtil.validateURLParameter(paramName, parameterValue, this);
                }
                returnOriginalParamValue = false;
            }
            else if (ZSecConstants.DataType.getDataTypeList().contains(this.dataType)) {
                final ParameterRule dataFormatParamRule = (embedParamRule != null) ? embedParamRule : this;
                final String dataFormatTemplate = dataFormatParamRule.getTemplateName();
                final boolean allowEmpty = dataFormatParamRule.isEmptyValueAllowed();
                if (!SecurityUtil.isValid(dataFormatTemplate)) {
                    ParameterRule.logger.log(Level.SEVERE, "Invalid Configuration found , {0} Template is not defined for {1}", new Object[] { this.dataType, this });
                    throw new IAMSecurityException("INVALID_CONFIGURATION");
                }
                try {
                    resultObject = TemplateRule.validateDataFormat(paramName, parameterValue, dataFormatParamRule);
                }
                catch (final IOException ex2) {
                    ParameterRule.logger.log(Level.SEVERE, "\n Unable to parse String : \"{0}\" to : {1} & Exception Message : {2}", new Object[] { this.getParameterValue(parameterValue, paramName), this.dataType, ex2.getMessage() });
                    throw new IAMSecurityException(ZSecConstants.DataType.errorcodeMap.get(this.dataType), request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
                }
                catch (final IAMSecurityException ex3) {
                    throw new IAMSecurityException(ex3.getErrorCode(), request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
                }
                returnOriginalParamValue = false;
            }
            else if (this.dataType != null && this.dataType.startsWith("cleartext:")) {
                resultObject = SecurityUtil.applyClearTextPattern(request, this.dataType, paramName, parameterValue);
                returnOriginalParamValue = false;
            }
            else if ("xml".equalsIgnoreCase(this.dataType)) {
                try {
                    Element root = null;
                    if (this.xmlSchemaName != null || this.xmlSchemaValidation) {
                        final XMLParameterValidation xmlParamValidation = new XMLParameterValidation(request.getRequestWebContext(), parameterValue, this.allowXMLInlineEntityExpansion, this.xmlSchemaName);
                        root = xmlParamValidation.validateXML();
                    }
                    else {
                        if (this.xssValidation == null) {
                            ParameterRule.logger.log(Level.SEVERE, "Invalid Configuration found , type=\"xml\" must be defined with xss=\"...\" or xml-schema-validation=\"true\" for Conf {0}", this);
                            throw new IAMSecurityException("INVALID_CONFIGURATION");
                        }
                        final XMLXSSRemover remover = new XMLXSSRemover(request, parameterValue, this.xssValidation, this.allowXMLInlineEntityExpansion);
                        root = remover.filterXSS();
                    }
                    returnOriginalParamValue = false;
                    resultObject = root;
                }
                catch (final IAMSecurityException e) {
                    if ("UNABLE_TO_PARSE_DOCUMENT".equals(e.getErrorCode())) {
                        ParameterRule.logger.log(Level.SEVERE, "Unable to parse xml document string \"{0}\"", this.getParameterValue(parameterValue, paramName));
                    }
                    if (e.getErrorCode().equals("XSS_DETECTED")) {
                        ParameterRule.logger.log(Level.SEVERE, "XSS detected for the parameter \"{0}\" in the request URI \"{1}\". The value is  \"{2}\" and the param rule is :\n{3}", new Object[] { paramName, request.getRequestURI(), this.getParameterValue(parameterValue, paramName), this.toString() });
                        throw new IAMSecurityException("XSS_DETECTED", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
                    }
                    throw new IAMSecurityException(e.getErrorCode(), request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this, e.getXMLErrorMessage(), e.getXMLErrorLineNumber(), e.getXMLErrorColumnNumber());
                }
            }
        }
        catch (final IAMSecurityException e) {
            if (embedParamRule == null && isJSONTemplateDefined && !paramName.equals(e.getParameterName())) {
                throw new IAMSecurityException(e.getErrorCode(), request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, null, e.getParameterName(), this, e.getParameterRule());
            }
            throw e;
        }
        catch (final NumberFormatException e2) {
            ParameterRule.logger.log(Level.SEVERE, "Exception while parsing the datatype : \"{0}\" for the parameter \"{1}\" in the request URI \"{2}\". The value is  \"{3}\" and the param rule is :\n{4}\nException Message : Number Format Exception", new Object[] { this.dataType, paramName, request.getRequestURI(), this.getParameterValue(parameterValue, paramName), this.toString() });
            throw new IAMSecurityException("UNABLE_TO_PARSE_DATA_TYPE", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
        }
        catch (final Exception e3) {
            ParameterRule.logger.log(Level.SEVERE, "Exception while parsing the datatype : \"{0}\" for the parameter \"{1}\" in the request URI \"{2}\". The value is  \"{3}\" and the param rule is :\n{4}\nException Message : {5}", new Object[] { this.dataType, paramName, request.getRequestURI(), this.getParameterValue(parameterValue, paramName), this.toString(), e3.getMessage() });
            throw new IAMSecurityException("UNABLE_TO_PARSE_DATA_TYPE", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
        }
        if (resultObject == null) {
            ParameterRule.logger.log(Level.SEVERE, "Incorrect datatype for the parameter \"{0}\" in the request URI \"{1}\". Thevalue is  \"{2}\" and the param rule is :\n{3}", new Object[] { paramName, request.getRequestURI(), this.getParameterValue(parameterValue, paramName), this.toString() });
            throw new IAMSecurityException("DATATYPE_NOT_MATCHED", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
        }
        if (embedParamRule == null && this.storeParameterValue) {
            request.addValidatedParameterValueObject(paramName, resultObject, this);
        }
        if (returnOriginalParamValue) {
            return parameterValue;
        }
        return resultObject.toString();
    }
    
    void checkForRange(final HttpServletRequest request, final String paramName, final String parameterValue) {
        if (SecurityUtil.isValid(this.ranges)) {
            for (int i = 0; i < this.ranges.size(); ++i) {
                if (this.ranges.get(i).contains(parameterValue)) {
                    return;
                }
            }
            ParameterRule.logger.log(Level.SEVERE, "The parameter \"{0}\" for the URL \"{1}\" is out of the range value configured in the param rule :\n{2}", new Object[] { paramName, request.getRequestURI(), this.toString() });
            throw new IAMSecurityException("OUT_OF_RANGE", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
        }
    }
    
    String validateConfiguration(final SecurityFilterProperties sfp) {
        if ((this.dataType == null || this.dataType.length() == 0) && (this.allowedValueRegex == null || this.allowedValueRegex.length() == 0) && (this.xssValidation == null || this.xssValidation.length() == 0)) {
            return "Missing input validation. At least any one of \"type\", \"regex\" or \"xss\" should be configured for the parameter rule :\n" + this.toString() + "\n";
        }
        if (this.dataType != null && !"".equals(this.dataType) && !ParameterRule.supportedDataTypes.matcher(this.dataType).matches()) {
            return "Unsupported data type \"" + this.dataType + "\" given for the parameter rule :\n" + this.toString() + "\n";
        }
        if (this.dataType != null && this.dataType.startsWith("JSON") && this.jsonTemplate == null && (this.xssValidation == null || this.xssValidation.length() == 0)) {
            return "JSONTemplate or XSSValidation  is not set for JSON data type, in the rule :\n" + this.toString() + "\n";
        }
        if ("xml".equalsIgnoreCase(this.dataType)) {
            if (this.xmlSchemaName != null && sfp.getXMLSchemaRule(this.xmlSchemaName) == null) {
                return "XMLSchema rule for the name \"" + this.xmlSchemaName + "\" is not found for the parameter rule :\n" + this.toString() + "\n";
            }
            if (this.xmlSchemaValidation && sfp.getXMLSchemas().isEmpty()) {
                return "XMLSchema is not configured in web.xml for XML data type, in the parameter rule :\n" + this.toString() + "\n";
            }
            if (this.xmlSchemaName == null && !this.xmlSchemaValidation && (this.xssValidation == null || this.xssValidation.length() == 0)) {
                return "XML Schema or XSS Validation is not set for XML data type, in the rule :\n" + this.toString() + "\n";
            }
        }
        if (this.xssValidation != null && !"".equals(this.xssValidation) && !"throwerror".equalsIgnoreCase(this.xssValidation) && !"throw".equals(this.xssValidation) && !"escape".equalsIgnoreCase(this.xssValidation) && sfp.getXSSUtil(this.xssValidation) == null) {
            return "XSS Pattern definition for the pattern name \"" + this.xssValidation + "\" is not found for the parameter rule :\n" + this.toString() + "\n";
        }
        int maxOccur = this.maxOccurrences;
        if (this.isParamNameInRegex) {
            maxOccur = (this.isMaxOccurrenceConfigured ? maxOccur : sfp.getDynamicParamsMaxOccurrenceLimit());
        }
        if (this.minOccurrences > maxOccur) {
            return "Minimum occurences greater than max occurences for the rule :\n" + this.toString() + "\n";
        }
        if (this.maxLength != -1 && this.minLength > this.maxLength) {
            return "Minimum length greater than max length for the rule :\n" + this.toString() + "\n";
        }
        if (this.split != null && this.split.length() > 0 && maxOccur == 1) {
            return "Max-occurences should be more than one if split configured for the rule :\n" + this.toString() + "\n";
        }
        if ((this.saltValidity != -1L || this.saltTicket) && (this.saltDelimiter == null || this.saltDelimiter.length() == 0)) {
            return "Salt based decryption configured without \"salt-delimiter\" attribute, for the rule :\n" + this.toString() + "\n";
        }
        if (this.saltDelimiter != null && this.saltDelimiter.length() > 0 && !this.decrypt) {
            return "Salt based decryption configured without \"decrypt\" attribute set to true, for the rule :\n" + this.toString() + "\n";
        }
        if (SecurityUtil.isValid(this.ranges) && !"int".equals(this.dataType) && !"long".equals(this.dataType)) {
            return "Range value specified for non-Integer and non-Long data type\n" + this.toString() + "\n";
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "ParameterRule :: paramName : \"" + this.paramName + "\" index : \"" + this.getRangeNotation() + "\" paramNameInRegex : \"" + this.paramNameInRegex + "\" allowedValueRegex : \"" + this.allowedValueRegex + "\" minLength : \"" + this.minLength + "\" maxLength : \"" + this.maxLength + "\" minOccurrences : \"" + this.minOccurrences + "\" maxOccurrences  : \"" + this.maxOccurrences + "\" canonicalize  : \"" + this.canonicalize + "\" isParamNameInRegex : \"" + this.isParamNameInRegex + "\" dataType : \"" + this.dataType + "\" xssValidation : \"" + this.xssValidation + "\" decrypt : \"" + this.decrypt + "\" salt-delim : \"" + this.saltDelimiter + "\" salt-ticket : \"" + this.saltTicket + "\" salt-validity : \"" + this.saltValidity + "\" trim : \"" + this.trim + "\" secret : \"" + this.isMaskingEnabled() + "\" Range : \"" + this.rangeStr + "\" defaultValue : \"" + this.defaultValue + "\" template : \"" + this.jsonTemplate + "\" xmlSchemaValidation : \"" + this.xmlSchemaValidation + "\" xmlSchemaName : \"" + this.xmlSchemaName + "\" arraySize : \"" + this.arraySize + "\" allowEmpty : \"" + this.allowEmpty + "\"";
    }
    
    public boolean isTrim(final HttpServletRequest request) {
        return (this.trim == null) ? SecurityFilterProperties.getInstance(request).isTrimEnabled() : this.trim;
    }
    
    public boolean isTrim() {
        return this.trim != null && this.trim;
    }
    
    public void setTrim(final boolean trim) {
        this.trim = trim;
    }
    
    void setSecret(final boolean secret) {
        this.mask = "required";
    }
    
    public boolean isSecret() {
        return this.isMaskingRequired();
    }
    
    public boolean isMaskingEnabled() {
        return this.mask != null;
    }
    
    public boolean isMaskingRequired() {
        return "required".equals(this.mask);
    }
    
    public boolean isMaskingRequiredPartially() {
        return "partial".equals(this.mask);
    }
    
    public int getPreserveCharPrefix() {
        return this.preserveCharPrefix;
    }
    
    public int getPreserveCharSuffix() {
        return this.preserveCharSuffix;
    }
    
    public int getPreserveCharLimit() {
        return this.preserveCharsLimitInPercent;
    }
    
    public boolean isInvalidAllowed() {
        return (this.allowInvalid == null) ? SecurityFilterProperties.getInstance(SecurityUtil.getCurrentRequest()).allowInvalidValue() : this.allowInvalid;
    }
    
    boolean isEmptyValueAllowed() {
        return (this.allowEmpty == null) ? SecurityFilterProperties.getInstance(SecurityUtil.getCurrentRequest()).allowEmptyValue() : this.allowEmpty;
    }
    
    public void setAllowEmpty(final boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
    }
    
    protected int getLimit() {
        return this.limit;
    }
    
    private void setLimit(final int limit) {
        this.limit = limit;
    }
    
    boolean isSplitDefined() {
        return this.isSplitDefined;
    }
    
    Pattern getSplitPattern() {
        return this.splitPattern;
    }
    
    public String getRangeNotation() {
        if (this.index != null) {
            return this.index.getRangeNotation();
        }
        return null;
    }
    
    public boolean isPrimitive() {
        return this.isPrimitive;
    }
    
    public boolean isRegExValidation() {
        return this.isRegexValidation;
    }
    
    public boolean isSpamCheckEnabled() {
        return this.antispam;
    }
    
    public boolean isEditableOnValidation() {
        return this.isEditableOnValidation;
    }
    
    private void storeParameterValueObjectWithoutValidation(final SecurityRequestWrapper request, final String paramName, final String parameterValue, final ParameterRule embedParamRule) {
        if (this.dataType == null || this.dataType.length() == 0 || "String".equals(this.dataType) || this.dataType.startsWith("cleartext:") || this.dataType.equalsIgnoreCase("url")) {
            return;
        }
        Object resultObject = null;
        boolean isJSONTemplateDefined = false;
        try {
            Label_0926: {
                if ("long".equals(this.dataType)) {
                    resultObject = new Long(parameterValue);
                }
                else if ("int".equals(this.dataType)) {
                    resultObject = new Integer(parameterValue);
                }
                else if ("float".equals(this.dataType)) {
                    resultObject = new Float(parameterValue);
                }
                else if ("double".equals(this.dataType)) {
                    resultObject = new Double(parameterValue);
                }
                else if ("short".equals(this.dataType)) {
                    resultObject = new Short(parameterValue);
                }
                else if ("char".equals(this.dataType)) {
                    if (parameterValue.length() == 1) {
                        resultObject = parameterValue.charAt(0);
                    }
                }
                else if ("boolean".equals(this.dataType)) {
                    if (parameterValue.equalsIgnoreCase("true") || parameterValue.equalsIgnoreCase("false")) {
                        resultObject = Boolean.valueOf(parameterValue);
                    }
                }
                else if (this.dataType.startsWith("JSON")) {
                    String type = this.dataType;
                    if ("JSONObject|JSONArray".equals(this.dataType)) {
                        type = (parameterValue.trim().startsWith("{") ? "JSONObject" : "JSONArray");
                    }
                    Label_0567: {
                        if (SecurityUtil.isValid(this.jsonTemplate) || this.templateParam != null) {
                            isJSONTemplateDefined = true;
                            request.getURLActionRule().setJSONSecretParam(JSONTemplateRule.getKeyRule(this.jsonTemplate));
                            try {
                                if ("".equals(parameterValue.trim())) {
                                    JSONTemplateRule.invalidValueCheck(this, (HttpServletRequest)request, SecurityFilterProperties.JSON_INVALID_VALUE_TYPE.EMPTYSTRING, null);
                                    return;
                                }
                                if ("null".equals(parameterValue)) {
                                    JSONTemplateRule.invalidValueCheck(this, (HttpServletRequest)request, SecurityFilterProperties.JSON_INVALID_VALUE_TYPE.JSONNULL, null);
                                    return;
                                }
                                resultObject = ("JSONObject".equals(type) ? JSONTemplateRule.validateJSONObject(parameterValue, this) : JSONTemplateRule.validateJSONArray(parameterValue, this));
                                break Label_0567;
                            }
                            catch (final JSONException ex) {
                                ParameterRule.logger.log(Level.SEVERE, "\n Unable to parse String : \"{0}\" to : {1} & Exception Message : {2}", new Object[] { this.getParameterValue(parameterValue, paramName), this.dataType, ex.getMessage() });
                                throw new IAMSecurityException("JSON_PARSE_ERROR", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
                            }
                        }
                        if (this.xssValidation == null) {
                            ParameterRule.logger.log(Level.SEVERE, "Invalid Configuration found , JsonTemplate (static or Dynamic) is not defined for {0}", this);
                            throw new IAMSecurityException("INVALID_CONFIGURATION");
                        }
                        resultObject = ("JSONObject".equals(type) ? new JSONObject(parameterValue) : new JSONArray(parameterValue));
                    }
                }
                else {
                    if (ZSecConstants.DataType.getDataTypeList().contains(this.dataType)) {
                        if (SecurityUtil.isValid(this.templateName)) {
                            try {
                                resultObject = TemplateRule.validateDataFormat(paramName, parameterValue, this);
                                break Label_0926;
                            }
                            catch (final IOException ex2) {
                                ParameterRule.logger.log(Level.SEVERE, "\n Unable to parse String : \"{0}\" to : {1} & Exception Message : {2}", new Object[] { this.getParameterValue(parameterValue, paramName), this.dataType, ex2.getMessage() });
                                throw new IAMSecurityException(ZSecConstants.DataType.errorcodeMap.get(this.dataType), request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
                            }
                            catch (final IAMSecurityException ex3) {
                                throw new IAMSecurityException(ex3.getErrorCode(), request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
                            }
                        }
                        ParameterRule.logger.log(Level.SEVERE, "Invalid Configuration found , {0} Template is not defined for {1}", new Object[] { this.dataType, this });
                        throw new IAMSecurityException("INVALID_CONFIGURATION");
                    }
                    if ("xml".equalsIgnoreCase(this.dataType)) {
                        if (this.xmlSchemaName == null && !this.xmlSchemaValidation && this.xssValidation == null) {
                            ParameterRule.logger.log(Level.SEVERE, "Invalid Configuration found , type=\"xml\" must be defined with xss=\"...\" or xml-schema-validation=\"true\" for Conf {0}", this);
                            throw new IAMSecurityException("INVALID_CONFIGURATION", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"));
                        }
                        try {
                            resultObject = SecurityUtil.createDocumentBuilder(this.allowXMLInlineEntityExpansion, false, null).parse(new ByteArrayInputStream(parameterValue.getBytes())).getDocumentElement();
                        }
                        catch (final Exception ex4) {
                            ParameterRule.logger.log(Level.SEVERE, "Unable to parse xml document string \"{0}\", Exception Message :\"{1}\"", new Object[] { this.getParameterValue(parameterValue, paramName), ex4.getMessage() });
                            throw new IAMSecurityException("UNABLE_TO_PARSE_DOCUMENT", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName);
                        }
                    }
                }
            }
        }
        catch (final IAMSecurityException e) {
            if (embedParamRule == null && isJSONTemplateDefined && !paramName.equals(e.getParameterName())) {
                throw new IAMSecurityException(e.getErrorCode(), request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, null, e.getParameterName(), this, e.getParameterRule());
            }
            throw e;
        }
        catch (final NumberFormatException e2) {
            ParameterRule.logger.log(Level.SEVERE, "Exception while parsing the datatype : \"{0}\" for the parameter \"{1}\" in the request URI \"{2}\". The value is  \"{3}\" and the param rule is :\n{4}\nException Message : Number Format Exception", new Object[] { this.dataType, paramName, request.getRequestURI(), this.getParameterValue(parameterValue, paramName), this.toString() });
            throw new IAMSecurityException("UNABLE_TO_PARSE_DATA_TYPE", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
        }
        catch (final Exception e3) {
            ParameterRule.logger.log(Level.SEVERE, "Exception while parsing the datatype : \"{0}\" for the parameter \"{1}\" in the request URI \"{2}\". The value is  \"{3}\" and the param rule is :\n{4}\nException Message : {5}", new Object[] { this.dataType, paramName, request.getRequestURI(), this.getParameterValue(parameterValue, paramName), this.toString(), e3.getMessage() });
            throw new IAMSecurityException("UNABLE_TO_PARSE_DATA_TYPE", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
        }
        if (resultObject != null) {
            if (embedParamRule == null && this.storeParameterValue) {
                request.addValidatedParameterValueObject(paramName, resultObject, this);
            }
            return;
        }
        ParameterRule.logger.log(Level.SEVERE, "Incorrect datatype for the parameter \"{0}\" in the request URI \"{1}\". Thevalue is  \"{2}\" and the param rule is :\n{3}", new Object[] { paramName, request.getRequestURI(), this.getParameterValue(parameterValue, paramName), this.toString() });
        throw new IAMSecurityException("DATATYPE_NOT_MATCHED", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, this);
    }
    
    public boolean isExtraParamRule() {
        return this.isExtraParamRule;
    }
    
    public void setNonEmpty(final boolean nonEmpty) {
        this.isValidInputStreamNeeded = nonEmpty;
    }
    
    public String getReplacementString() {
        return this.replacementString;
    }
    
    public void setReplacementString(final String replacementStr) {
        this.replacementString = replacementStr;
    }
    
    public boolean isInputStreamRequired() {
        return this.isValidInputStreamNeeded;
    }
    
    public void inputStreamRequired(final boolean streamRequired) {
        this.isValidInputStreamNeeded = streamRequired;
    }
    
    public String getDecryptLabel() {
        return this.decrypt_label;
    }
    
    public void setDecryptLabel(final String decryptLabel) {
        this.decrypt_label = decryptLabel;
    }
    
    public String getPreserveChars() {
        return this.preserveChars;
    }
    
    public boolean isEnabledXMLInlineEntityExpansion() {
        return this.allowXMLInlineEntityExpansion;
    }
    
    public void allowXMLInlineEntityExpansion(final boolean allowInlineEntityExpansion) {
        this.allowXMLInlineEntityExpansion = allowInlineEntityExpansion;
    }
    
    public void setInputStreamMaxSizeInKB(final long maxSizeInKB) {
        this.inputStreamMaxSizeInKB = maxSizeInKB;
    }
    
    public long getInputStreamMaxSizeInKB() {
        return this.inputStreamMaxSizeInKB;
    }
    
    boolean isInputStreamTypeBinary() {
        return "binary".equals(this.dataType);
    }
    
    public boolean isImportFile() {
        return this.isImportFile;
    }
    
    public void setImportFile(final boolean importFile) {
        this.isImportFile = importFile;
    }
    
    static {
        ParameterRule.supportedDataTypes = Pattern.compile("long|int|short|double|float|boolean|char|properties|csv|JSONObject|JSONArray|JSONObject\\|JSONArray|vcardArray|String|xml|cleartext:check|cleartext:filter|cleartext:removehtmlentities|binary");
        ParameterRule.primitiveDataTypes = Pattern.compile("long|int|short|double|float|boolean|char|String");
        logger = Logger.getLogger(ParameterRule.class.getName());
        LABEL_MESSAGE_PATTERN = Pattern.compile("\\$([\\w]+)");
        JSON_TEMPLATE_PATTERN = Pattern.compile("^([$]\\{[\\w.-]+\\})?(_[a-zA-Z0-9.-]*_)?([\\w.-]+)?$");
    }
    
    public enum ATTRIBUTES
    {
        type, 
        maxlen, 
        minlen, 
        maxoccur, 
        minoccur, 
        split, 
        defaultvalue;
    }
}
