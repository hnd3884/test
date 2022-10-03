package com.adventnet.iam.xss;

import java.util.regex.Matcher;
import java.net.URLDecoder;
import java.util.Iterator;
import com.adventnet.iam.security.IAMSecurityException;
import com.adventnet.iam.security.SecurityFilter;
import com.adventnet.iam.security.SecurityUtil;
import com.adventnet.iam.security.SecurityRequestWrapper;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.List;
import java.util.logging.Logger;

public class XSSFilterConfiguration
{
    public static final Logger LOGGER;
    public static final int MATCH_INDEX_OF = 1;
    public static final int MATCH_STARTS_WITH = 2;
    public static final int MATCH_PATTERN = 3;
    public static final int REMOVE_NONE = 0;
    public static final int REMOVE_ELEMENT = 1;
    public static final int REMOVE_ATTRIBUTE = 2;
    List<String> removeElementNames;
    Pattern removeElementValuePattern;
    List<String> removeAttributeNames;
    List<AttributeValue> removeAttributeValues;
    HashMap<String, List<AttributeValue>> removeElementWithAttributes;
    Map<String, Pattern> allowedElemAttributeToValueMap;
    HashMap<String, AttributeValue> insertElementAttrs;
    Pattern trustedScriptDomainsPattern;
    Pattern whitelistedScriptDomainsPattern;
    boolean balanceHtmlTags;
    boolean xssDetectThrowError;
    boolean xssDetectSpaceEntityReference;
    protected boolean encodeElementValuesMarkupEntities;
    boolean ignoreMetaCharset;
    boolean removeComments;
    boolean removeCData;
    public ParserType parserType;
    private boolean allowDoctype;
    
    public XSSFilter createXSSFilter() {
        switch (this.parserType) {
            case JTIDY: {
                return new JTidyXSSFilter();
            }
            case ANTISAMY:
            case ANTISAMY_CSS:
            case ANTISAMY_CSSPROPERTIES: {
                return new AntiSamyFilter();
            }
            default: {
                return new NekoXSSFilter();
            }
        }
    }
    
    public XSSFilterConfiguration(final Properties props) {
        this.removeElementNames = null;
        this.removeElementValuePattern = null;
        this.removeAttributeNames = null;
        this.removeAttributeValues = null;
        this.removeElementWithAttributes = null;
        this.allowedElemAttributeToValueMap = null;
        this.insertElementAttrs = null;
        this.trustedScriptDomainsPattern = null;
        this.whitelistedScriptDomainsPattern = null;
        this.balanceHtmlTags = false;
        this.xssDetectThrowError = false;
        this.xssDetectSpaceEntityReference = true;
        this.encodeElementValuesMarkupEntities = false;
        this.ignoreMetaCharset = false;
        this.removeComments = false;
        this.removeCData = false;
        this.parserType = ParserType.NEKO;
        this.allowDoctype = false;
        XSSFilterConfiguration.LOGGER.log(Level.FINE, "Initialising properties started...");
        if (props.containsKey("allow-doctype")) {
            this.allowDoctype = "true".equals(props.getProperty("allow-doctype"));
        }
        if (props.containsKey("balance-html-tags")) {
            this.balanceHtmlTags = "true".equals(props.getProperty("balance-html-tags"));
        }
        if (props.containsKey("xss-filter-type")) {
            this.parserType = ParserType.valueOf(props.getProperty("xss-filter-type").toUpperCase());
        }
        if (props.containsKey("xss-detect-throwerror")) {
            this.xssDetectThrowError = "true".equals(props.getProperty("xss-detect-throwerror"));
        }
        if (props.containsKey("xss-detect-space-entity-reference")) {
            this.xssDetectSpaceEntityReference = "true".equals(props.getProperty("xss-detect-space-entity-reference"));
        }
        if (props.containsKey("encode-element-values-markup-entities")) {
            this.encodeElementValuesMarkupEntities = "true".equals(props.getProperty("encode-element-values-markup-entities"));
        }
        this.removeElementNames = getTrimmedStringList(props.getProperty("remove-element-names"), ",");
        final String removeElementValues = props.getProperty("remove-element-values");
        this.removeElementValuePattern = ((removeElementValues == null) ? this.removeElementValuePattern : Pattern.compile(removeElementValues.trim()));
        this.removeAttributeNames = getTrimmedStringList(props.getProperty("remove-attribute-names"), ",");
        final String remattrvalues = props.getProperty("remove-attribute-values");
        if (remattrvalues != null) {
            final String[] trimmedStringArray;
            final String[] remAttrValues = trimmedStringArray = getTrimmedStringArray(remattrvalues, ",");
            for (final String remAttrVal : trimmedStringArray) {
                int type = 2;
                String str = remAttrVal;
                if (remAttrVal.indexOf("|") != -1) {
                    final String[] splittedStr = getTrimmedStringArray(remAttrVal, "[|]");
                    str = splittedStr[0];
                    if (splittedStr.length == 2 && "indexof".equalsIgnoreCase(splittedStr[1])) {
                        type = 1;
                    }
                    else if (splittedStr.length == 2 && "pattern".equalsIgnoreCase(splittedStr[1])) {
                        type = 3;
                    }
                }
                final AttributeValue attrValue = new AttributeValue(null, str, type);
                (this.removeAttributeValues = ((this.removeAttributeValues == null) ? new ArrayList<AttributeValue>() : this.removeAttributeValues)).add(attrValue);
            }
        }
        final String allowelmtattr = props.getProperty("allow-element-attributes");
        if (allowelmtattr != null) {
            final String[] trimmedStringArray2;
            final String[] allowElmtWitAttrs = trimmedStringArray2 = getTrimmedStringArray(allowelmtattr, ",", false);
            for (final String allowElmtWitAttr : trimmedStringArray2) {
                XSSFilterConfiguration.LOGGER.log(Level.FINE, "allowElmtWitAttr - {0} ", allowElmtWitAttr);
                if (allowElmtWitAttr.indexOf("|") != -1) {
                    final String[] splittedStr2 = getTrimmedStringArray(allowElmtWitAttr, "[|]", false);
                    if (splittedStr2.length == 3) {
                        final String element = splittedStr2[0].toLowerCase();
                        final String attribute = splittedStr2[1].toLowerCase();
                        final String attrvalue = splittedStr2[2];
                        if (this.allowedElemAttributeToValueMap == null) {
                            this.allowedElemAttributeToValueMap = new HashMap<String, Pattern>();
                        }
                        final SecurityRequestWrapper securityRequest = (SecurityRequestWrapper)SecurityUtil.getCurrentRequest();
                        if (securityRequest != null) {
                            final Pattern allwPat = SecurityFilter.getRegexPattern(securityRequest, attrvalue);
                            if (allwPat == null) {
                                XSSFilterConfiguration.LOGGER.log(Level.WARNING, "Undefined Allowed Element Attribute Pattern : {0}", attrvalue);
                                throw new IAMSecurityException("PATTERN_NOT_DEFINED");
                            }
                            this.allowedElemAttributeToValueMap.put(element + "|" + attribute, allwPat);
                            XSSFilterConfiguration.LOGGER.log(Level.FINE, "Added pattern To allowedElemAttributeToValueMap  - {0} ", this.allowedElemAttributeToValueMap);
                        }
                    }
                    else {
                        XSSFilterConfiguration.LOGGER.warning("Invalid allow-element-attribute values. Must be of format \"elementName|attributeName|attributeValuePatternName\"");
                    }
                }
            }
        }
        final String remelmtattr = props.getProperty("remove-element-attributes");
        if (remelmtattr != null) {
            final String[] trimmedStringArray3;
            final String[] remElmtWitAttrs = trimmedStringArray3 = getTrimmedStringArray(remelmtattr, ",");
            for (final String remElmtWitAttr : trimmedStringArray3) {
                XSSFilterConfiguration.LOGGER.log(Level.FINE, "remElmtWitAttr - {0} ", remElmtWitAttr);
                if (remElmtWitAttr.indexOf("|") != -1) {
                    final String[] splittedStr = getTrimmedStringArray(remElmtWitAttr, "[|]");
                    if (splittedStr.length > 1 && !remElmtWitAttr.contains("[")) {
                        final String element2 = splittedStr[0];
                        final String attribute2 = splittedStr[1];
                        String attrvalue2 = null;
                        int type2 = 2;
                        if (splittedStr.length == 4 && "pattern".equalsIgnoreCase(splittedStr[3])) {
                            attrvalue2 = splittedStr[2];
                            type2 = 3;
                        }
                        else if (splittedStr.length == 4 && "indexof".equalsIgnoreCase(splittedStr[3])) {
                            attrvalue2 = splittedStr[2];
                            type2 = 1;
                        }
                        else if (splittedStr.length >= 3) {
                            attrvalue2 = splittedStr[2];
                        }
                        final AttributeValue attrval = new AttributeValue(attribute2, attrvalue2, type2);
                        this.removeElementWithAttributes = ((this.removeElementWithAttributes == null) ? new HashMap<String, List<AttributeValue>>() : this.removeElementWithAttributes);
                        List<AttributeValue> attrvalueList = this.removeElementWithAttributes.get(element2);
                        attrvalueList = ((attrvalueList == null) ? new ArrayList<AttributeValue>() : attrvalueList);
                        attrvalueList.add(attrval);
                        this.removeElementWithAttributes.put(element2, attrvalueList);
                    }
                    else if (splittedStr.length == 2) {
                        final String attrStr = remElmtWitAttr.substring(remElmtWitAttr.indexOf("[") + 1, remElmtWitAttr.indexOf("]"));
                        final String[] attrArray = getTrimmedStringArray(attrStr, "/");
                        final String element3 = splittedStr[0];
                        final int type2 = 2;
                        for (final String attribute3 : attrArray) {
                            final AttributeValue attrval2 = new AttributeValue(attribute3, null, type2);
                            this.removeElementWithAttributes = ((this.removeElementWithAttributes == null) ? new HashMap<String, List<AttributeValue>>() : this.removeElementWithAttributes);
                            List<AttributeValue> attrvalueList2 = this.removeElementWithAttributes.get(element3);
                            attrvalueList2 = ((attrvalueList2 == null) ? new ArrayList<AttributeValue>() : attrvalueList2);
                            attrvalueList2.add(attrval2);
                            this.removeElementWithAttributes.put(element3, attrvalueList2);
                        }
                    }
                }
            }
        }
        final String inselmtattr = props.getProperty("insert-element-attributes");
        if (inselmtattr != null) {
            final String[] trimmedStringArray4;
            final String[] insElmtAttrs = trimmedStringArray4 = getTrimmedStringArray(inselmtattr, ",");
            for (final String insElmtAttr : trimmedStringArray4) {
                if (insElmtAttr.indexOf("|") != -1) {
                    final String[] splittedStr3 = getTrimmedStringArray(insElmtAttr, "[|]");
                    if (splittedStr3.length == 3) {
                        final AttributeValue attrval3 = new AttributeValue(splittedStr3[1], splittedStr3[2], 2);
                        (this.insertElementAttrs = ((this.insertElementAttrs == null) ? new HashMap<String, AttributeValue>() : this.insertElementAttrs)).put(splittedStr3[0], attrval3);
                    }
                }
            }
        }
        final String trustedScriptDomains = props.getProperty("trusted-script-domains");
        this.trustedScriptDomainsPattern = ((trustedScriptDomains == null) ? this.trustedScriptDomainsPattern : Pattern.compile(trustedScriptDomains.toLowerCase()));
        final String whitelistedScriptDomains = props.getProperty("whitelisted-script-domains");
        this.whitelistedScriptDomainsPattern = ((whitelistedScriptDomains == null) ? this.whitelistedScriptDomainsPattern : Pattern.compile(whitelistedScriptDomains.toLowerCase()));
        if (props.containsKey("ignore-meta-charset")) {
            this.ignoreMetaCharset = "true".equals(props.getProperty("ignore-meta-charset"));
        }
        if (props.containsKey("remove-comments")) {
            this.removeComments = "true".equals(props.getProperty("remove-comments"));
        }
        if (props.containsKey("remove-cdata")) {
            this.removeCData = "true".equals(props.getProperty("remove-cdata"));
        }
        XSSFilterConfiguration.LOGGER.log(Level.FINE, "Initialising properties ended...");
    }
    
    public int removeXSSElementOrAttribute(final String elemNameStr, final String attrNameStr, final String attrValueStr) {
        if (this.allowedElemAttributeToValueMap == null || !this.allowedElemAttributeToValueMap.containsKey(elemNameStr + "|" + attrNameStr)) {
            if (this.removeElementWithAttributes != null && this.removeElementWithAttributes.get(elemNameStr) != null) {
                final List<AttributeValue> attrvalueList = this.removeElementWithAttributes.get(elemNameStr);
                for (final AttributeValue attrvalue : attrvalueList) {
                    if (attrvalue == null) {
                        continue;
                    }
                    final String remAttrVal = attrvalue.value;
                    if (attrNameStr.startsWith(attrvalue.name) && (remAttrVal == null || this.isXSSAttributeValue(attrValueStr, remAttrVal, attrvalue.valueMatchType))) {
                        return 2;
                    }
                }
            }
            if (this.removeAttributeNames != null) {
                for (final String remAttrName : this.removeAttributeNames) {
                    if (attrNameStr.toLowerCase().startsWith(remAttrName)) {
                        return 2;
                    }
                }
            }
            if (this.removeAttributeValues != null) {
                for (final AttributeValue attrvalue2 : this.removeAttributeValues) {
                    if (attrvalue2 == null) {
                        continue;
                    }
                    final String remAttrVal2 = attrvalue2.value;
                    if (remAttrVal2 != null && this.isXSSAttributeValue(attrValueStr, remAttrVal2, attrvalue2.valueMatchType)) {
                        return 2;
                    }
                }
            }
            return 0;
        }
        final Pattern allowedPattern = this.allowedElemAttributeToValueMap.get(elemNameStr + "|" + attrNameStr);
        if (allowedPattern.matcher(attrValueStr).matches()) {
            XSSFilterConfiguration.LOGGER.log(Level.FINE, "value : {0}  matches AllowedURLPattern : {1}", new Object[] { attrValueStr, allowedPattern });
            return 0;
        }
        XSSFilterConfiguration.LOGGER.log(Level.FINE, "value : {0}  Does not matches AllowedURLPattern : {1}", new Object[] { attrValueStr, allowedPattern });
        return 1;
    }
    
    public static String removeUnwanted(final String value) {
        try {
            String tmpvalue = URLDecoder.decode(value, "UTF-8");
            tmpvalue = XSSUtil.removeComment(tmpvalue);
            tmpvalue = tmpvalue.replaceAll("\\s|\\/0", "");
            return tmpvalue;
        }
        catch (final Exception ex) {
            XSSFilterConfiguration.LOGGER.log(Level.FINE, ex.getMessage(), ex);
            return value;
        }
    }
    
    boolean isXSSAttributeValue(final String attrValue, final String xssAttrValue, final int type) {
        String tmpValue = removeUnwanted(attrValue);
        if (this.xssDetectSpaceEntityReference) {
            tmpValue = tmpValue.replaceAll("(&tab;|&newline;)", "");
            tmpValue = tmpValue.trim();
        }
        if (type == 3) {
            final SecurityRequestWrapper securityRequest = (SecurityRequestWrapper)SecurityUtil.getCurrentRequest();
            if (securityRequest != null) {
                final Pattern removePattern = SecurityFilter.getRegexPattern(securityRequest, xssAttrValue);
                if (removePattern != null) {
                    if (removePattern.matcher(attrValue).find()) {
                        return true;
                    }
                    if (removePattern.matcher(tmpValue).find()) {
                        return true;
                    }
                }
            }
        }
        else if (type == 2) {
            if (attrValue.startsWith(xssAttrValue) || XSSUtil.TRIM_CTRLCHARS_PATTERN.matcher(attrValue).replaceAll("").startsWith(xssAttrValue)) {
                return true;
            }
            if (tmpValue.startsWith(xssAttrValue) || XSSUtil.TRIM_CTRLCHARS_PATTERN.matcher(tmpValue).replaceAll("").startsWith(xssAttrValue)) {
                return true;
            }
        }
        else if (type == 1) {
            if (attrValue.indexOf(xssAttrValue) != -1 || XSSUtil.TRIM_CTRLCHARS_PATTERN.matcher(attrValue).replaceAll("").indexOf(xssAttrValue) != -1) {
                return true;
            }
            if (tmpValue.indexOf(xssAttrValue) != -1 || XSSUtil.TRIM_CTRLCHARS_PATTERN.matcher(tmpValue).replaceAll("").indexOf(xssAttrValue) != -1) {
                return true;
            }
        }
        return false;
    }
    
    public AttributeValue getInsertAttribute(final String elmt) {
        if (this.insertElementAttrs != null && this.insertElementAttrs.containsKey(elmt)) {
            return this.insertElementAttrs.get(elmt);
        }
        return null;
    }
    
    public boolean isRemoveElement(final String elmt) {
        return this.removeElementNames != null && this.removeElementNames.contains(elmt);
    }
    
    public boolean getRemoveElementValue(final String value) {
        if (this.removeElementValuePattern != null) {
            final Matcher mat = this.removeElementValuePattern.matcher(value);
            if (mat.find()) {
                return true;
            }
        }
        return false;
    }
    
    protected String encodeElementValuesMarkupEntities(String elemValue, final boolean isStyle) {
        if (!isStyle) {
            elemValue = elemValue.replaceAll(">", "&gt;");
        }
        elemValue = elemValue.replaceAll("<", "&lt;");
        return elemValue;
    }
    
    public boolean isAllowedScript(final String domain, final String srcValue) {
        if (this.isWhitelistedDomain(domain)) {
            return true;
        }
        if (srcValue != null) {
            final String srcDomain = SecurityUtil.getDomain(srcValue);
            if (this.trustedScriptDomainsPattern.matcher(srcDomain).matches() || this.whitelistedScriptDomainsPattern.matcher(srcDomain).matches()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isWhitelistedDomain(final String domain) {
        return domain != null && this.whitelistedScriptDomainsPattern.matcher(SecurityUtil.getDomain(domain)).matches();
    }
    
    public boolean isIgnoredMetaCharset() {
        return this.ignoreMetaCharset;
    }
    
    public boolean isRemoveComments() {
        return this.removeComments;
    }
    
    public boolean isRemoveCData() {
        return this.removeCData;
    }
    
    public boolean isAllowDoctype() {
        return this.allowDoctype;
    }
    
    @Override
    public String toString() {
        String retStr = "removeElementNames - " + this.removeElementNames.toString();
        retStr = retStr + "\n removeElementValuePattern - " + ((this.removeElementValuePattern == null) ? this.removeElementValuePattern : this.removeElementValuePattern.toString());
        retStr = retStr + "\n removeAttributeNames - " + ((this.removeAttributeNames == null) ? this.removeAttributeNames : this.removeAttributeNames.toString());
        retStr = retStr + "\n removeAttributeValues - " + ((this.removeAttributeValues == null) ? this.removeAttributeValues : this.removeAttributeValues.toString());
        retStr = retStr + "\n removeElementWithAttributes - " + ((this.removeElementWithAttributes == null) ? this.removeElementWithAttributes : this.removeElementWithAttributes.toString());
        retStr = retStr + "\n insertElementAttrs - " + ((this.insertElementAttrs == null) ? this.insertElementAttrs : this.insertElementAttrs.toString());
        retStr = retStr + "\n allowElementAttributes - " + this.allowedElemAttributeToValueMap;
        retStr = retStr + "\n trustedScriptDomainsPattern - " + ((this.trustedScriptDomainsPattern == null) ? this.trustedScriptDomainsPattern : this.trustedScriptDomainsPattern.toString());
        retStr = retStr + "\n whitelistedScriptDomainsPattern - " + ((this.whitelistedScriptDomainsPattern == null) ? this.whitelistedScriptDomainsPattern : this.whitelistedScriptDomainsPattern.toString());
        return retStr;
    }
    
    public static String[] getTrimmedStringArray(final String valStr, final String delim) {
        return getTrimmedStringArray(valStr, delim, true);
    }
    
    public static String[] getTrimmedStringArray(final String valStr, final String delim, final boolean toLowerCase) {
        if (valStr == null) {
            return null;
        }
        String[] vals = null;
        if (toLowerCase) {
            vals = valStr.toLowerCase().split(delim);
        }
        else {
            vals = valStr.split(delim);
        }
        for (int i = 0; i < vals.length; ++i) {
            vals[i] = vals[i].trim();
        }
        return vals;
    }
    
    public static List<String> getTrimmedStringList(final String valStr, final String delim) {
        if (valStr == null) {
            return null;
        }
        final String[] vals = valStr.toLowerCase().split(delim);
        final List<String> valList = new ArrayList<String>();
        for (int i = 0; i < vals.length; ++i) {
            valList.add(vals[i].trim());
        }
        return valList;
    }
    
    static {
        LOGGER = Logger.getLogger(XSSFilterConfiguration.class.getName());
    }
    
    public enum ParserType
    {
        JTIDY, 
        NEKO, 
        ANTISAMY, 
        ANTISAMY_CSS, 
        ANTISAMY_CSSPROPERTIES;
    }
    
    public class AttributeValue
    {
        public String name;
        public String value;
        public int valueMatchType;
        
        AttributeValue(final String name, final String value, final int type) {
            this.valueMatchType = 2;
            this.name = name;
            this.value = value;
            this.valueMatchType = type;
        }
        
        @Override
        public String toString() {
            return " name : " + this.name + ", value : " + this.value + ", valueMatchType : " + this.valueMatchType + ";";
        }
    }
}
