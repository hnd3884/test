package com.adventnet.iam.security;

import java.util.Collection;
import org.json.JSONException;
import org.json.JSONArray;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.w3c.dom.Node;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.w3c.dom.Element;
import java.util.HashMap;
import com.zoho.security.api.Range;
import java.util.List;
import java.util.logging.Logger;
import java.util.Map;

public class JSONTemplateRule
{
    private String jsonTemplateName;
    private Map<String, ParameterRule> jsonKeyValueMap;
    private Map<String, ParameterRule> jsonParamNamesInRegexKeyValueMap;
    private static final Logger LOGGER;
    private static final String IAMARRAY = "IAMArray";
    public static final int DEFAULT_RANGE_EXTENSION = 10;
    private static final String INDEX_RANGE_FOR_IAMARRAY = "0-1000000000";
    private Map<String, String> jsonInnerTemplatesMap;
    private boolean hasSecretParam;
    private List<String> listOfJSONSecretParamNamesInRegex;
    private List<String> listOfJSONSecretParamNamesInNonRegex;
    private List<String> jsonKeyGroupList;
    private ParameterRule extraJSONKeyRule;
    private boolean disableExtraJSONKey;
    private Map<Range, ParameterRule> jsonArrayIndexMap;
    private HashMap<String, String> customAttributes;
    private boolean continueOnError;
    private List<OrCriteriaRule> orCriteriaRules;
    
    public JSONTemplateRule(final Element elem) {
        this.jsonTemplateName = null;
        this.jsonKeyValueMap = new LinkedHashMap<String, ParameterRule>();
        this.jsonParamNamesInRegexKeyValueMap = new LinkedHashMap<String, ParameterRule>();
        this.jsonInnerTemplatesMap = new HashMap<String, String>();
        this.hasSecretParam = false;
        this.listOfJSONSecretParamNamesInRegex = new ArrayList<String>();
        this.listOfJSONSecretParamNamesInNonRegex = new ArrayList<String>();
        this.jsonKeyGroupList = new ArrayList<String>();
        this.extraJSONKeyRule = null;
        this.disableExtraJSONKey = false;
        this.jsonArrayIndexMap = new LinkedHashMap<Range, ParameterRule>();
        this.customAttributes = null;
        this.customAttributes = SecurityUtil.convertToMap(elem);
        final String name = elem.getAttribute("name");
        if (SecurityUtil.isValid(name)) {
            this.jsonTemplateName = name;
        }
        final String disableExtraJSONKey = elem.getAttribute("disable-extra-key");
        if (SecurityUtil.isValid(name)) {
            this.disableExtraJSONKey = "true".equalsIgnoreCase(disableExtraJSONKey);
        }
        this.continueOnError = "true".equalsIgnoreCase(elem.getAttribute("continue-onerror"));
        this.initKeyValueRule(elem);
        this.initExtraJSONKeyRule(elem);
    }
    
    public String getName() {
        return this.jsonTemplateName;
    }
    
    public Map<String, String> getJSONInnerTemplatesMap() {
        return this.jsonInnerTemplatesMap;
    }
    
    public List<String> getListOfJSONSecretParamNamesInRegex() {
        return this.listOfJSONSecretParamNamesInRegex;
    }
    
    public List<String> getListOfJSONSecretParamNamesInNonRegex() {
        return this.listOfJSONSecretParamNamesInNonRegex;
    }
    
    public boolean hasSecretParam() {
        return this.hasSecretParam;
    }
    
    public void addKeyValuerRule(final ParameterRule rule) {
        this.validateJSONParamRule(rule);
        Range index = null;
        this.convertIAMArrayToIndex(rule);
        final String paramName = rule.getParamName();
        if (SecurityUtil.isValid(paramName) && !this.jsonKeyValueMap.containsKey(paramName) && !this.jsonParamNamesInRegexKeyValueMap.containsKey(paramName)) {
            if (rule.getJSONTemplate() != null && rule.getDataType().startsWith("JSON")) {
                this.jsonInnerTemplatesMap.put(paramName, rule.getJSONTemplate());
            }
            if (rule.isSecret() && !this.listOfJSONSecretParamNamesInNonRegex.contains(paramName) && !this.listOfJSONSecretParamNamesInRegex.contains(paramName)) {
                if (rule.isParamNameInRegex()) {
                    this.listOfJSONSecretParamNamesInRegex.add(paramName);
                }
                else {
                    this.listOfJSONSecretParamNamesInNonRegex.add(paramName);
                }
                if (!this.hasSecretParam) {
                    this.hasSecretParam = true;
                }
            }
            if (rule.isParamNameInRegex()) {
                this.jsonParamNamesInRegexKeyValueMap.put(paramName, rule);
            }
            else {
                this.jsonKeyValueMap.put(paramName, rule);
            }
        }
        else {
            if (!SecurityUtil.isValid(index = rule.getIndexRange())) {
                if (!SecurityUtil.isValid(paramName)) {
                    JSONTemplateRule.LOGGER.log(Level.SEVERE, " Either \"name\" or \"index\" must be defined under <jsontemplate> for <key configuration rule :::: {0} ", rule.toString());
                }
                else {
                    JSONTemplateRule.LOGGER.log(Level.SEVERE, " Duplicate <key... /> Configuration found , <key configuration rule :::: {0} ", rule.toString());
                }
                throw new IAMSecurityException("INVALID_CONFIGURATION");
            }
            if (rule.isSecret() && !this.hasSecretParam) {
                this.hasSecretParam = true;
            }
            this.jsonArrayIndexMap.put(index, rule);
        }
    }
    
    private void convertIAMArrayToIndex(final ParameterRule rule) {
        if ("IAMArray".equals(rule.getParamName())) {
            JSONTemplateRule.LOGGER.log(Level.SEVERE, " <key name=\"IAMArray\" configuration will be EOLed soon . Don't use IAMArray as a key name for JSONArray instead kindly use <key index=\"<low>-<high>\" ... based configuration");
            rule.createIndexRange("0-1000000000");
            rule.setParamName(null);
        }
    }
    
    public Map<String, ParameterRule> getKeyValueRule() {
        return this.jsonKeyValueMap;
    }
    
    public Map<String, ParameterRule> getParamNameRegexKeyValueRule() {
        return this.jsonParamNamesInRegexKeyValueMap;
    }
    
    public static JSONTemplateRule getKeyRule(final String template) {
        return SecurityFilterProperties.getInstance(SecurityUtil.getCurrentRequest()).getJSONTemplateRule(template);
    }
    
    public static JSONTemplateRule getKeyRule(final String webappName, final String template) {
        return SecurityFilterProperties.getInstance(webappName).getJSONTemplateRule(template);
    }
    
    public ParameterRule getParameterRule(final String paramName) {
        final ParameterRule rule = this.jsonKeyValueMap.get(paramName);
        if (rule != null) {
            return rule;
        }
        for (final ParameterRule pr : this.jsonParamNamesInRegexKeyValueMap.values()) {
            if (pr.getParamNameRegex().matcher(paramName).matches()) {
                return pr;
            }
        }
        return null;
    }
    
    public ParameterRule getJSONArrayIndexRule(final int currentArrayIndex) {
        for (final Range indexRange : this.jsonArrayIndexMap.keySet()) {
            if (indexRange.contains(currentArrayIndex)) {
                return this.jsonArrayIndexMap.get(indexRange);
            }
        }
        return null;
    }
    
    public Map<Range, ParameterRule> getJsonArrayIndexMap() {
        return this.jsonArrayIndexMap;
    }
    
    public ParameterRule getPrimitiveArrayRule() {
        return this.getJSONArrayIndexRule(0);
    }
    
    public List<String> getJSONKeyGroupList() {
        return this.jsonKeyGroupList;
    }
    
    public void initKeyValueRule(final Element element) {
        final List<Element> keyValueList = RuleSetParser.getChildNodesByTagName(element, RuleSetParser.TagName.KEY.getValue());
        if (keyValueList.size() > 0) {
            for (final Element keyElem : keyValueList) {
                if (SecurityUtil.isValid(keyElem.getAttribute("allow-empty"))) {
                    JSONTemplateRule.LOGGER.log(Level.SEVERE, " \"allow-empty\" configuration has been EOLed for keys with type= \"JSONObject\" or \"JSONArray\" and all jsontemplate keys kindly use \"allow-invalid\" instead for  key/Param : \"{0}\" ", keyElem.getAttribute("name"));
                    throw new IAMSecurityException("INVALID_CONFIGURATION");
                }
                final ParameterRule keyValueRule = new ParameterRule(keyElem);
                this.addKeyValuerRule(keyValueRule);
            }
            this.validateJSONArrayIndexRuleRange();
        }
        final List<Element> keysElemList = RuleSetParser.getChildNodesByTagName(element, "keys");
        for (final Element keysElem : keysElemList) {
            final List<Element> orCriteriaElemList = RuleSetParser.getChildNodesByTagName(keysElem, "or-criteria");
            if (orCriteriaElemList.size() > 0) {
                this.orCriteriaRules = new ArrayList<OrCriteriaRule>(orCriteriaElemList.size());
                for (final Element orCriteriaElem : orCriteriaElemList) {
                    OrCriteriaRule criteriaRule = null;
                    try {
                        criteriaRule = new OrCriteriaRule(orCriteriaElem, RuleSetParser.TagName.KEY.getValue());
                    }
                    catch (final RuntimeException e) {
                        JSONTemplateRule.LOGGER.log(Level.SEVERE, "Error Msg : {0}, JsonTemplate Name : {1}", new Object[] { e.getMessage(), this.jsonTemplateName });
                        throw new IAMSecurityException("INVALID_CONFIGURATION");
                    }
                    this.orCriteriaRules.add(criteriaRule);
                    for (final ParameterRule keyRule : criteriaRule.getParameterRules()) {
                        this.addKeyValuerRule(keyRule);
                    }
                }
            }
            final List<Element> keyElemList = RuleSetParser.getChildNodesByTagName(keysElem, RuleSetParser.TagName.KEY.getValue());
            for (final Element keyElem2 : keyElemList) {
                this.addKeyValuerRule(new ParameterRule(keyElem2));
            }
        }
        final List<Element> nodeList = RuleSetParser.getChildNodesByTagName(element, "key-group");
        if (nodeList.size() > 0) {
            for (int i = 0; i < nodeList.size(); ++i) {
                final Node keyGroupNode = nodeList.get(i);
                if (keyGroupNode.getNodeType() == 1) {
                    final Element keyGroupElement = (Element)keyGroupNode;
                    final String keyGroupName = keyGroupElement.getAttribute("name");
                    if (!SecurityUtil.isValid(keyGroupName)) {
                        throw new RuntimeException("Empty jsonkey-group name '" + keyGroupName + "' not allowed for the JSON template '" + this.jsonTemplateName + "' ");
                    }
                    if (this.jsonKeyGroupList.contains(keyGroupName)) {
                        throw new RuntimeException("The JSON key-group name '" + keyGroupName + "' for the json template '" + this.jsonTemplateName + "' is already defined");
                    }
                    this.jsonKeyGroupList.add(keyGroupName);
                }
            }
        }
    }
    
    public List<OrCriteriaRule> getOrCriteriaRules() {
        return this.orCriteriaRules;
    }
    
    private void validateJSONArrayIndexRuleRange() {
        final Map<String, Integer> indexRuleCount = new HashMap<String, Integer>();
        for (final Range range : this.jsonArrayIndexMap.keySet()) {
            if (indexRuleCount.containsKey(range.getRangeNotation())) {
                if (!this.continueOnError) {
                    JSONTemplateRule.LOGGER.log(Level.SEVERE, " Duplicate <key... /> Configuration found in <jsontemplate> : {0}, <key configuration rule :::: {1} ", new Object[] { this.jsonTemplateName, this.jsonArrayIndexMap.get(range).toString() });
                    throw new IAMSecurityException("INVALID_CONFIGURATION");
                }
                final int count = indexRuleCount.get(range.getRangeNotation()) + 1;
                if (count > 3) {
                    JSONTemplateRule.LOGGER.log(Level.SEVERE, "Number of index rules with same index range \"{0}\" exceeded the default limit \"3\" in jsontemplate config : \"{1}\"", new Object[] { range.getRangeNotation(), this.jsonTemplateName });
                    throw new IAMSecurityException("INVALID_CONFIGURATION");
                }
                indexRuleCount.put(range.getRangeNotation(), count);
            }
            else {
                indexRuleCount.put(range.getRangeNotation(), 1);
            }
        }
    }
    
    private static JSONObject checkForExtraParam(final String paramName, JSONObject resultJSON, final JSONObject inputJSON, final JSONTemplateRule templateRule) throws JSONException {
        final HttpServletRequest request = SecurityUtil.getCurrentRequest();
        final Map<String, Object> map = new HashMap<String, Object>();
        final ParameterRule extraJSONKeyRule = (templateRule.getExtraJSONKeyRule() == null) ? SecurityFilterProperties.getInstance(request).getExtraJSONKeyRule() : templateRule.getExtraJSONKeyRule();
        final JSONArray keyNames = inputJSON.names();
        for (int i = 0; i < keyNames.length(); ++i) {
            final String keyName = keyNames.getString(i);
            if (!resultJSON.has(keyName)) {
                JSONTemplateRule.LOGGER.log(Level.FINE, "JSON Extra Key validation : started");
                map.put(keyName, inputJSON.get(keyName));
                if (extraJSONKeyRule == null || templateRule.isDisableExtraJSONKey()) {
                    throw new IAMSecurityException("EXTRA_KEY_FOUND_IN_JSON", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), map.keySet().iterator().next(), extraJSONKeyRule);
                }
                if (map.size() > extraJSONKeyRule.getLimit()) {
                    JSONTemplateRule.LOGGER.log(Level.SEVERE, "Extra key limit({0}) exceeded in json : {1} for the URI : {2}", new Object[] { extraJSONKeyRule.getLimit(), ActionRule.PRINT(map.keySet()), request.getRequestURI() });
                    throw new IAMSecurityException("EXCEEDS_EXTRA_KEY_LIMIT_IN_JSON", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), map.keySet().iterator().next(), extraJSONKeyRule);
                }
                if ("".equals(keyName.trim())) {
                    invalidValueCheck(extraJSONKeyRule, request, SecurityFilterProperties.JSON_INVALID_VALUE_TYPE.EMPTYSTRING, null);
                }
                else {
                    validateKeyValue(extraJSONKeyRule, keyName, keyName);
                }
                extraJSONKeyRule.setParamName(keyName);
                resultJSON = validateJSONValue(inputJSON, resultJSON, extraJSONKeyRule, keyName);
            }
        }
        if (!map.isEmpty()) {
            JSONTemplateRule.LOGGER.log(Level.WARNING, "Extra key found in json : {0} for the URI : {1}", new Object[] { ActionRule.PRINT(map.keySet()), request.getRequestURI() });
        }
        return resultJSON;
    }
    
    private boolean isDisableExtraJSONKey() {
        return this.disableExtraJSONKey;
    }
    
    private void initExtraJSONKeyRule(final Element element) {
        final List<Element> nodeList = RuleSetParser.getChildNodesByTagName(element, "extrakey");
        if (nodeList.size() == 0) {
            return;
        }
        final int nodeListLength = nodeList.size();
        if (nodeListLength == 1) {
            final Node node = nodeList.get(0);
            if (node.getNodeType() == 1) {
                final Element extraJSONKeyElement = (Element)node;
                this.extraJSONKeyRule = new ParameterRule(extraJSONKeyElement);
            }
        }
        else if (nodeListLength > 1) {
            throw new RuntimeException("More than one extra key configuration found for jsontemplate");
        }
    }
    
    public ParameterRule getExtraJSONKeyRule() {
        return this.extraJSONKeyRule;
    }
    
    public static JSONArray validateJSONArray(final String paramName, final String paramValue, final String template) throws JSONException {
        final ParameterRule paramRule = new ParameterRule();
        paramRule.setParamName(paramName);
        paramRule.setJSONTemplate(template);
        paramRule.setArraysizeInRange(paramRule.getArraySize());
        return validateJSONArray(paramValue, paramRule);
    }
    
    public static JSONArray validateJSONArray(final String paramName, final JSONArray reqJSONArray, final String template) throws JSONException {
        final ParameterRule paramRule = new ParameterRule();
        paramRule.setParamName(paramName);
        paramRule.setJSONTemplate(template);
        paramRule.setArraysizeInRange(paramRule.getArraySize());
        return validateJSONArray(reqJSONArray, paramRule);
    }
    
    public static JSONArray validateJSONArray(final String paramValue, final ParameterRule paramRule) throws JSONException {
        return validateJSONArray(new JSONArray(paramValue), paramRule);
    }
    
    public static JSONArray validateJSONArray(final JSONArray reqJSONArray, final ParameterRule paramRule) throws JSONException {
        final HttpServletRequest request = SecurityUtil.getCurrentRequest();
        final String paramName = paramRule.getParamName();
        final String staticTemplateName = paramRule.getJSONTemplate();
        JSONTemplateRule staticTemplateRule = null;
        int elementIndex = -1;
        JSONArray resultJSONArray = null;
        try {
            checkForJSONArraySize(reqJSONArray.length(), paramRule);
            resultJSONArray = new JSONArray();
            if (reqJSONArray.length() == 0) {
                invalidValueCheck(paramRule, request, SecurityFilterProperties.JSON_INVALID_VALUE_TYPE.EMPTYARRAY, null);
                return resultJSONArray;
            }
            if (staticTemplateName != null && !"".equals(staticTemplateName)) {
                staticTemplateRule = getValidJSONTemplateRule(paramName, paramRule.getJSONTemplate());
            }
            Object jsonArrayElement = null;
            String jsonArrayElementStringValue = null;
            final String validatedValue = null;
            final String dynamicKey = paramRule.getTemplateParam();
            for (int i = 0; i < reqJSONArray.length(); ++i) {
                elementIndex = i;
                jsonArrayElement = reqJSONArray.get(i);
                if (dynamicKey != null && jsonArrayElement instanceof JSONObject && (((JSONObject)jsonArrayElement).has(dynamicKey) || staticTemplateRule == null)) {
                    resultJSONArray.put((Object)validateJSONObject((JSONObject)jsonArrayElement, null, paramRule));
                }
                else {
                    if (staticTemplateRule == null) {
                        JSONTemplateRule.LOGGER.log(Level.SEVERE, "Incomplete Template Configuration is found for JSONArray \"{0}\" , kindly define a \"index\" based configuration for element at index : {1}   ", new Object[] { paramName, i });
                        throw new IAMSecurityException("INVALID_CONFIGURATION", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName, paramRule);
                    }
                    final ParameterRule indexRule = staticTemplateRule.getJSONArrayIndexRule(i);
                    if (indexRule != null) {
                        jsonArrayElementStringValue = jsonArrayElement.toString();
                        jsonArrayElementStringValue = (indexRule.isContentReplacementEnabled() ? indexRule.matchAndReplace((SecurityRequestWrapper)request, paramName, jsonArrayElementStringValue) : jsonArrayElementStringValue);
                        final String trimmedValue = jsonArrayElementStringValue.trim();
                        final boolean isEmpty = "".equals(trimmedValue);
                        if (isEmpty || reqJSONArray.isNull(i)) {
                            if (isEmpty) {
                                invalidValueCheck(indexRule, request, SecurityFilterProperties.JSON_INVALID_VALUE_TYPE.EMPTYSTRING, paramName);
                                resultJSONArray.put((Object)(indexRule.isTrim(request) ? trimmedValue : jsonArrayElementStringValue));
                            }
                            else {
                                invalidValueCheck(indexRule, request, SecurityFilterProperties.JSON_INVALID_VALUE_TYPE.JSONNULL, paramName);
                                resultJSONArray.put(JSONObject.NULL);
                            }
                        }
                        else {
                            resultJSONArray.put(validateValueWithIndexRule(request, jsonArrayElement, jsonArrayElementStringValue, staticTemplateRule, paramRule.getParamName(), i));
                        }
                    }
                    else {
                        if (!(jsonArrayElement instanceof JSONObject)) {
                            JSONTemplateRule.LOGGER.log(Level.SEVERE, "Extra value is found inside JSONArray \"{0}\" , kindly define a \"index\" based configuration for extra-value at index : {1}   ", new Object[] { paramName, i });
                            throw new IAMSecurityException("EXTRA_VALUE_FOUND_IN_JSONARRAY", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName);
                        }
                        resultJSONArray.put((Object)validateJSONObject((JSONObject)jsonArrayElement, staticTemplateRule, paramRule));
                    }
                }
            }
        }
        catch (final IAMSecurityException ex) {
            final String indexAsString = (elementIndex > -1) ? Integer.toString(elementIndex) : null;
            generateAndUpdateJSONStackTrace(JSON_ELEMENT_TYPE_INDICATOR.INDEX, indexAsString);
            throw ex;
        }
        return resultJSONArray;
    }
    
    private static Object validateValueWithIndexRule(final HttpServletRequest request, final Object value, String valueAsString, final JSONTemplateRule templateRule, final String keyName, final int currentIndex) {
        if (templateRule.continueOnError()) {
            valueAsString = valueAsString.trim();
            final boolean isJsonValue = valueAsString.startsWith("{") || valueAsString.startsWith("[");
            IAMSecurityException ex = null;
            int retryCount = 0;
            for (final Map.Entry<Range, ParameterRule> entry : templateRule.getJsonArrayIndexMap().entrySet()) {
                if (entry.getKey().contains(currentIndex)) {
                    final ParameterRule indexRule = entry.getValue();
                    try {
                        return validateKeyValue(request, value, valueAsString, indexRule, keyName);
                    }
                    catch (final IAMSecurityException e) {
                        if ((isJsonValue && isTemplateDefinedJSONType(indexRule)) || ++retryCount == 3) {
                            throw e;
                        }
                        JSONTemplateRule.LOGGER.log(Level.FINE, "Exception : {0} occurred while trying to validate the value : {1} against the index rule : {2}. As continue-onerror=true is set, so going to re-try with next best-matched index rule", new Object[] { e.getErrorCode(), indexRule.getParameterValue(valueAsString, keyName), indexRule });
                        ex = e;
                    }
                }
            }
            if (ex != null) {
                throw ex;
            }
        }
        return validateKeyValue(request, value, valueAsString, templateRule.getJSONArrayIndexRule(currentIndex), keyName);
    }
    
    private static void checkForJSONArraySize(final int inputJSONArraySize, final ParameterRule paramRule) {
        final HttpServletRequest request = SecurityUtil.getCurrentRequest();
        final Range<Integer> range = paramRule.getArraysizeInRange();
        if (range.contains(inputJSONArraySize)) {
            return;
        }
        JSONTemplateRule.LOGGER.log(Level.SEVERE, "The size of the jsonarray parameter \"{0}\" is out of the configured range in the request URL : \"{1}\". InputArraySize : {2}, ConfiguredArraySize : {3}, ParameterRule : {4}", new Object[] { paramRule.getParamName(), request.getRequestURI(), inputJSONArraySize, paramRule.getArraySize(), paramRule });
        throw new IAMSecurityException("ARRAY_SIZE_OUT_OF_RANGE", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramRule.getParamName(), paramRule);
    }
    
    public static JSONObject validateJSONObject(final String paramName, final String parameterValue, final String template) throws JSONException {
        final ParameterRule paramRule = new ParameterRule();
        paramRule.setParamName(paramName);
        paramRule.setJSONTemplate(template);
        return validateJSONObject(new JSONObject(parameterValue), getKeyRule(template), paramRule);
    }
    
    public static JSONObject validateJSONObject(final String paramName, final JSONObject requestJSONObj, final String template) throws JSONException {
        final ParameterRule paramRule = new ParameterRule();
        paramRule.setParamName(paramName);
        paramRule.setJSONTemplate(template);
        return validateJSONObject(requestJSONObj, getKeyRule(template), paramRule);
    }
    
    public static JSONObject validateJSONObject(final String parameterValue, final ParameterRule paramRule) throws JSONException {
        return validateJSONObject(new JSONObject(parameterValue), getKeyRule(paramRule.getJSONTemplate()), paramRule);
    }
    
    public static JSONObject validateJSONObject(final JSONObject requestJSONObj, final ParameterRule paramRule) throws JSONException {
        return validateJSONObject(requestJSONObj, getKeyRule(paramRule.getJSONTemplate()), paramRule);
    }
    
    private static JSONObject validateJSONObject(final JSONObject requestJSONObj, JSONTemplateRule templateRule, final ParameterRule paramRule) throws JSONException {
        final HttpServletRequest request = SecurityUtil.getCurrentRequest();
        JSONObject resultObj = null;
        String elementName = null;
        try {
            final JSONTemplateRule resolvedTemplateRule = resolveTemplateSelection(request, requestJSONObj, paramRule);
            ((SecurityRequestWrapper)request).getURLActionRule().setJSONSecretParam(resolvedTemplateRule);
            resultObj = new JSONObject();
            if (resolvedTemplateRule != templateRule) {
                final String dynamicKey = paramRule.getTemplateParam();
                resultObj.put(dynamicKey, requestJSONObj.get(dynamicKey));
                templateRule = resolvedTemplateRule;
            }
            if (templateRule.getOrCriteriaRules() != null && isEnabledMinOccurrenceCheck(request)) {
                for (final OrCriteriaRule criteriaRule : templateRule.getOrCriteriaRules()) {
                    int noOfOccurrences = 0;
                    final List<String> mandatoryKeys = new ArrayList<String>();
                    for (final ParameterRule keyRule : criteriaRule.getParameterRules()) {
                        if (requestJSONObj.has(keyRule.getParamName())) {
                            ++noOfOccurrences;
                        }
                        mandatoryKeys.add(keyRule.getParamName());
                    }
                    if (criteriaRule.getMinOccurrences() > noOfOccurrences) {
                        if (noOfOccurrences == 0) {
                            JSONTemplateRule.LOGGER.log(Level.SEVERE, "Expecting one of the keys \"{0}\" configured in jsontemplate : \"{1}\", Criteria Rule is : \n{2}", new Object[] { mandatoryKeys.toString(), templateRule.getName(), criteriaRule.toString() });
                        }
                        else {
                            JSONTemplateRule.LOGGER.log(Level.SEVERE, "The Occurrences of the keys \"{0}\" configured in jsontemplate : \"{1}\" are less than the minimum occurances configured in the Criteria rule :\n{2}", new Object[] { mandatoryKeys.toString(), templateRule.getName(), criteriaRule.toString() });
                        }
                        throw new IAMSecurityException("LESS_THAN_MIN_OCCURANCE", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), mandatoryKeys.toString(), noOfOccurrences, null);
                    }
                }
            }
            if (requestJSONObj.length() != 0) {
                final Collection<ParameterRule> jsonParamRules = templateRule.getKeyValueRule().values();
                final Collection<ParameterRule> jsonParamNameRegexParamRules = templateRule.getParamNameRegexKeyValueRule().values();
                for (final ParameterRule jsonParamRule : jsonParamRules) {
                    final String keyName = elementName = jsonParamRule.getParamName();
                    if (requestJSONObj.has(keyName) && !resultObj.has(keyName)) {
                        resultObj = validateJSONValue(requestJSONObj, resultObj, jsonParamRule, keyName);
                    }
                    else {
                        checkForMinOccurrence(request, 0, jsonParamRule, keyName, templateRule.getName());
                    }
                }
                final SecurityFilterProperties filterProps = SecurityFilterProperties.getInstance(request);
                for (final ParameterRule jsonParamNameRegexRule : jsonParamNameRegexParamRules) {
                    int noOfOccurrence = 0;
                    final int maxOccurrences = jsonParamNameRegexRule.getMaxOccurrences();
                    final String keyName2 = jsonParamNameRegexRule.getParamName();
                    final RegexRule regexRule = filterProps.getRegexRule(keyName2);
                    final JSONArray keyNames = requestJSONObj.names();
                    for (int j = 0; j < keyNames.length(); ++j) {
                        final String name = elementName = keyNames.getString(j);
                        if (!resultObj.has(name) && SecurityUtil.matchPattern(name, regexRule)) {
                            ++noOfOccurrence;
                            if (!filterProps.isEnabledIndividualOccurrenceCheckForDynamicParams() && maxOccurrences != -1 && noOfOccurrence > maxOccurrences) {
                                JSONTemplateRule.LOGGER.log(Level.SEVERE, "Number of keys matched against name-regex key \"{0}\" are more than the maximum occurrences \"{1}\" configured in jsontemplate \"{2}\"", new Object[] { jsonParamNameRegexRule.getParamName(), maxOccurrences, templateRule.getName() });
                                throw new IAMSecurityException("MORE_THAN_MAX_OCCURANCE", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), keyName2, noOfOccurrence, jsonParamNameRegexRule);
                            }
                            resultObj = validateJSONValue(requestJSONObj, resultObj, jsonParamNameRegexRule, name);
                        }
                    }
                    if (filterProps.isEnabledIndividualOccurrenceCheckForDynamicParams()) {
                        if (isJSONKeyRequired(request, jsonParamNameRegexRule) && noOfOccurrence == 0) {
                            JSONTemplateRule.LOGGER.log(Level.SEVERE, "The Key = \"{0}\" occurrence is less than the minimum occurrence", keyName2);
                            throw new IAMSecurityException("LESS_THAN_MIN_OCCURANCE", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), keyName2, jsonParamNameRegexRule);
                        }
                        continue;
                    }
                    else {
                        checkForMinOccurrence(request, noOfOccurrence, jsonParamNameRegexRule, keyName2, templateRule.getName());
                    }
                }
                elementName = null;
                resultObj = checkForExtraParam(paramRule.getParamName(), resultObj, requestJSONObj, templateRule);
            }
            else {
                invalidValueCheck(paramRule, request, SecurityFilterProperties.JSON_INVALID_VALUE_TYPE.EMPTYOBJECT, null);
            }
        }
        catch (final IAMSecurityException ex) {
            generateAndUpdateJSONStackTrace(JSON_ELEMENT_TYPE_INDICATOR.KEY, elementName);
            throw ex;
        }
        return resultObj;
    }
    
    private static void checkForMinOccurrence(final HttpServletRequest request, final int noOfOccurrence, final ParameterRule keyRule, final String keyName, final String templateName) {
        if (isJSONKeyRequired(request, keyRule) && noOfOccurrence < keyRule.getMinOccurrences()) {
            JSONTemplateRule.LOGGER.log(Level.SEVERE, "Number of keys matched against key = \"{0}\" are less than the minimum occurrences \"{1}\" configured in jsontemplate \"{2}\"", new Object[] { keyName, keyRule.getMinOccurrences(), templateName });
            throw new IAMSecurityException("LESS_THAN_MIN_OCCURANCE", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), keyName, noOfOccurrence, keyRule);
        }
    }
    
    private static void generateAndUpdateJSONStackTrace(final JSON_ELEMENT_TYPE_INDICATOR jsonElementType, final String exceptionTracePoint) {
        if (exceptionTracePoint != null) {
            final JSONObject exceptionTraceDetail = new JSONObject();
            exceptionTraceDetail.put(JSON_EXCEPTION_TRACE.TYPE.name(), (Object)jsonElementType.name());
            exceptionTraceDetail.put(JSON_EXCEPTION_TRACE.VALUE.name(), (Object)exceptionTracePoint);
            SecurityUtil.getJsonexceptiontracelist().add(0, exceptionTraceDetail);
        }
    }
    
    private void validateJSONParamRule(final ParameterRule jsonParamRule) {
        if (!SecurityUtil.isValid(jsonParamRule.xssValidation) && !SecurityUtil.isValid(jsonParamRule.getDataType()) && !SecurityUtil.isValid(jsonParamRule.getAllowedValueRegex())) {
            throw new RuntimeException("Invalid security configuration - \"XSS\" or \"TYPE\" or \"REGEX\" is mandatory for the key '" + jsonParamRule.getParamName() + "' in jsontemplate '" + this.jsonTemplateName + "'");
        }
    }
    
    public static JSONTemplateRule getValidJSONTemplateRule(final String paramName, final String template) {
        final JSONTemplateRule keyRule = getKeyRule(template);
        if (keyRule == null) {
            final HttpServletRequest request = SecurityUtil.getCurrentRequest();
            JSONTemplateRule.LOGGER.log(Level.SEVERE, "The jsontemplate Rule for the template = {0} is not defined ", template);
            throw new IAMSecurityException("JSON_TEMPLATE_RULE_NOT_DEFINED", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramName);
        }
        return keyRule;
    }
    
    static JSONTemplateRule resolveTemplateSelection(final HttpServletRequest request, final JSONObject requestJSONObj, final ParameterRule paramRule) {
        String dynamicTemplateParam = null;
        String dynamicTemplateValue = null;
        if ((dynamicTemplateParam = paramRule.getTemplateParam()) != null) {
            if (requestJSONObj.has(dynamicTemplateParam)) {
                dynamicTemplateValue = requestJSONObj.get(dynamicTemplateParam).toString();
                String dynamicTemplateprefix = null;
                if ((dynamicTemplateprefix = paramRule.getTemplatePrefix()) != null) {
                    dynamicTemplateValue = dynamicTemplateprefix + "." + dynamicTemplateValue;
                }
                if (SecurityUtil.isValid(paramRule.getDynamicTemplateKeyRegex())) {
                    validateDynamicTemplateValue((SecurityRequestWrapper)request, dynamicTemplateValue, paramRule);
                }
            }
            else if (paramRule.getJSONTemplate() == null) {
                JSONTemplateRule.LOGGER.log(Level.SEVERE, "expected \"templateParam\" key :\"{0}\" is not present in the JSONObject for {1}", new Object[] { dynamicTemplateParam, paramRule });
                JSONTemplateRule.LOGGER.log(Level.INFO, "Dynamic JSON Template Format : \"${templateParam}_Prefix_StaticTemplate\"");
                throw new IAMSecurityException("LESS_THAN_MIN_OCCURANCE", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), dynamicTemplateParam, paramRule);
            }
        }
        final String template = (dynamicTemplateValue == null) ? paramRule.getJSONTemplate() : dynamicTemplateValue;
        final JSONTemplateRule templateRule = getValidJSONTemplateRule(paramRule.getParamName(), template);
        return templateRule;
    }
    
    private static void validateDynamicTemplateValue(final SecurityRequestWrapper request, final String dynamicTemplateValue, final ParameterRule paramRule) {
        final String dynamicTemplateRegex = paramRule.getDynamicTemplateKeyRegex();
        if (dynamicTemplateRegex != null) {
            final SecurityFilterProperties filterConfig = SecurityFilterProperties.getInstance((HttpServletRequest)request);
            final RegexRule regexRule = filterConfig.getRegexRule(dynamicTemplateRegex);
            if (!SecurityUtil.matchPattern(dynamicTemplateValue, regexRule)) {
                JSONTemplateRule.LOGGER.log(Level.SEVERE, "Template value : \"{0}\" for the Key : \"{1}\" does not matches the configured pattern : \"{2}\" ", new Object[] { dynamicTemplateValue, paramRule.getTemplateParam(), regexRule.getPattern().toString() });
                throw new IAMSecurityException("PATTERN_NOT_MATCHED", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), paramRule.getTemplateParam());
            }
        }
    }
    
    private static JSONObject validateJSONValue(final JSONObject requestJSONObj, final JSONObject resultObj, final ParameterRule jsonParamRule, final String keyName) throws JSONException {
        final HttpServletRequest request = SecurityUtil.getCurrentRequest();
        final Object value = requestJSONObj.get(keyName);
        String valueAsString = value.toString();
        valueAsString = (jsonParamRule.isContentReplacementEnabled() ? jsonParamRule.matchAndReplace((SecurityRequestWrapper)request, keyName, valueAsString) : valueAsString);
        final String trimmedValue = valueAsString.trim();
        final boolean isEmpty = "".equals(trimmedValue);
        if (isEmpty || requestJSONObj.isNull(keyName)) {
            if (isEmpty) {
                invalidValueCheck(jsonParamRule, request, SecurityFilterProperties.JSON_INVALID_VALUE_TYPE.EMPTYSTRING, null);
                resultObj.put(keyName, (Object)(jsonParamRule.isTrim(request) ? trimmedValue : valueAsString));
            }
            else {
                invalidValueCheck(jsonParamRule, request, SecurityFilterProperties.JSON_INVALID_VALUE_TYPE.JSONNULL, null);
                resultObj.put(keyName, JSONObject.NULL);
            }
        }
        else {
            resultObj.put(keyName, validateKeyValue(request, value, valueAsString, jsonParamRule, keyName));
        }
        return resultObj;
    }
    
    private static Object validateKeyValue(final HttpServletRequest request, final Object value, String valueAsString, final ParameterRule keyRule, final String keyName) {
        Object resultObject = null;
        if (isTemplateDefinedJSONType(keyRule)) {
            if (keyRule.isEncryptedParam() || keyRule.decrypt_label != null || keyRule.isDeltaContent()) {
                valueAsString = keyRule.validateParamValue((SecurityRequestWrapper)request, keyName, valueAsString, keyRule);
                resultObject = getDataTypeValue(valueAsString, keyRule.getDataType());
            }
            else {
                keyRule.checkParamValueLength(request, keyName, valueAsString);
                ((SecurityRequestWrapper)request).getURLActionRule().setJSONSecretParam(getKeyRule(keyRule.getJSONTemplate()));
                String dataType = keyRule.getDataType();
                if ("JSONObject|JSONArray".equals(dataType)) {
                    dataType = (valueAsString.trim().startsWith("{") ? "JSONObject" : "JSONArray");
                }
                try {
                    if ("JSONObject".equals(dataType)) {
                        final JSONObject inputJSONObj = (JSONObject)((value instanceof JSONObject) ? value : new JSONObject(valueAsString));
                        resultObject = validateJSONObject(inputJSONObj, keyRule);
                    }
                    else {
                        final JSONArray inputJSONArray = (JSONArray)((value instanceof JSONArray) ? value : new JSONArray(valueAsString));
                        resultObject = validateJSONArray(inputJSONArray, keyRule);
                    }
                }
                catch (final JSONException ex) {
                    JSONTemplateRule.LOGGER.log(Level.SEVERE, "\n Unable to parse String : \"{0}\" to : {1}", new Object[] { keyRule.getParameterValue(valueAsString, keyName), dataType });
                    throw new IAMSecurityException("JSON_PARSE_ERROR", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), keyName, keyRule);
                }
            }
        }
        else {
            valueAsString = validateKeyValue(keyRule, keyName, valueAsString);
            resultObject = getDataTypeValue(valueAsString, keyRule.getDataType());
        }
        return resultObject;
    }
    
    static boolean isTemplateDefinedJSONType(final ParameterRule paramRule) {
        return (paramRule.getJSONTemplate() != null || paramRule.getTemplateParam() != null) && paramRule.getDataType().startsWith("JSON");
    }
    
    private static String validateKeyValue(final ParameterRule jsonParamRule, final String keyName, final String jsonValue) {
        final HttpServletRequest request = SecurityUtil.getCurrentRequest();
        return jsonParamRule.validateParamValue((SecurityRequestWrapper)request, keyName, jsonValue, jsonParamRule);
    }
    
    private static boolean isJSONKeyRequired(final HttpServletRequest request, final ParameterRule parRule) {
        return parRule.getMinOccurrences() > 0 && isEnabledMinOccurrenceCheck(request);
    }
    
    private static boolean isEnabledMinOccurrenceCheck(final HttpServletRequest request) {
        final ActionRule rule = ((SecurityRequestWrapper)request).getURLActionRule();
        final boolean isPutCheckDisabled = SecurityFilterProperties.getInstance(request).isPutMinoccurCheckDisabled();
        return rule == null || ((!isPutCheckDisabled || !rule.isPutURL()) && ParameterRule.isEnabledMinOccurrenceCheck((SecurityRequestWrapper)request, rule));
    }
    
    static void invalidValueCheck(final ParameterRule jsonParamRule, final HttpServletRequest request, final SecurityFilterProperties.JSON_INVALID_VALUE_TYPE invalidToken, final String parentKeyName) {
        if (!jsonParamRule.isInvalidAllowed() && SecurityFilterProperties.getInstance(request).isInvalidJSONContentBlocked(invalidToken)) {
            String keyName = jsonParamRule.getParamName();
            keyName = (SecurityUtil.isValid(keyName) ? keyName : parentKeyName);
            JSONTemplateRule.LOGGER.log(Level.SEVERE, " \"{0}\" as a value is not allowed for the json param \"{1}\" in the request url \"{2}\" ", new Object[] { invalidToken.getValue(), keyName, request.getRequestURI() });
            throw new IAMSecurityException("INVALID_VALUE_NOT_ALLOWED", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), keyName, jsonParamRule);
        }
    }
    
    @Deprecated
    public static Object getDataTypeValues(final String value, final String dataType) throws JSONException {
        return getDataTypeValue(value, dataType);
    }
    
    public static Object getDataTypeValue(final String value, final String dataType) throws JSONException {
        if (value.length() == 0 || dataType == null || dataType.length() == 0 || dataType.startsWith("cleartext:")) {
            return value;
        }
        Object retValue = null;
        if ("long".equals(dataType)) {
            retValue = Long.parseLong(value);
        }
        else if ("int".equals(dataType)) {
            retValue = Integer.parseInt(value);
        }
        else if ("float".equals(dataType)) {
            retValue = Float.parseFloat(value);
        }
        else if ("double".equals(dataType)) {
            retValue = Double.parseDouble(value);
        }
        else if ("short".equals(dataType)) {
            retValue = Short.parseShort(value);
        }
        else if ("char".equals(dataType)) {
            if (value.length() == 1) {
                retValue = value;
            }
        }
        else if ("boolean".equals(dataType)) {
            retValue = Boolean.parseBoolean(value);
        }
        else if (dataType.startsWith("JSON")) {
            retValue = (value.trim().startsWith("{") ? new JSONObject(value) : new JSONArray(value));
        }
        else {
            retValue = value;
        }
        return retValue;
    }
    
    @Override
    public String toString() {
        return "JSON Name ::: \"" + this.jsonTemplateName + "\" \n";
    }
    
    void initializeParentVariables(final JSONTemplateRule parentJSONTemplatesRule) {
        for (final ParameterRule keyRule : parentJSONTemplatesRule.getKeyValueRule().values()) {
            if (!this.jsonKeyValueMap.containsKey(keyRule.getParamName())) {
                this.addKeyValuerRule(keyRule);
            }
        }
        for (final ParameterRule keyRule : parentJSONTemplatesRule.getParamNameRegexKeyValueRule().values()) {
            if (!this.jsonParamNamesInRegexKeyValueMap.containsKey(keyRule.getParamName())) {
                this.addKeyValuerRule(keyRule);
            }
        }
        if (parentJSONTemplatesRule.getOrCriteriaRules() != null) {
            (this.orCriteriaRules = ((this.orCriteriaRules == null) ? new ArrayList<OrCriteriaRule>() : this.orCriteriaRules)).addAll(parentJSONTemplatesRule.getOrCriteriaRules());
        }
        if (this.extraJSONKeyRule == null) {
            this.extraJSONKeyRule = parentJSONTemplatesRule.getExtraJSONKeyRule();
        }
    }
    
    void initializeJSONKeyGroup(final Map<String, List<ParameterRule>> jsonKeyGroupRuleMap, final Map<String, List<OrCriteriaRule>> jsonKeyGroupCriteriaRuleMap) {
        for (final String keyGroupName : this.jsonKeyGroupList) {
            if (!jsonKeyGroupRuleMap.containsKey(keyGroupName)) {
                throw new RuntimeException("The JSON key group rule for the jsonkey-group name '" + keyGroupName + "' is not defined");
            }
            final List<ParameterRule> jsonKeyGroupRules = jsonKeyGroupRuleMap.get(keyGroupName);
            for (final ParameterRule jsonKeyRule : jsonKeyGroupRules) {
                if (!this.jsonKeyValueMap.containsKey(jsonKeyRule.getParamName()) && !this.jsonParamNamesInRegexKeyValueMap.containsKey(jsonKeyRule)) {
                    this.addKeyValuerRule(jsonKeyRule);
                }
            }
            if (jsonKeyGroupCriteriaRuleMap == null || !jsonKeyGroupCriteriaRuleMap.containsKey(keyGroupName)) {
                continue;
            }
            (this.orCriteriaRules = ((this.orCriteriaRules == null) ? new ArrayList<OrCriteriaRule>() : this.orCriteriaRules)).addAll(jsonKeyGroupCriteriaRuleMap.get(keyGroupName));
        }
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
    
    public boolean continueOnError() {
        return this.continueOnError;
    }
    
    static {
        LOGGER = Logger.getLogger(JSONTemplateRule.class.getName());
    }
    
    public enum JSON_ELEMENT_TYPE_INDICATOR
    {
        KEY, 
        INDEX;
    }
    
    public enum JSON_EXCEPTION_TRACE
    {
        TYPE, 
        VALUE;
    }
}
