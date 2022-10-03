package org.owasp.validator.css;

import java.util.MissingResourceException;
import java.util.logging.Level;
import org.owasp.validator.html.util.ErrorMessageUtil;
import org.owasp.validator.html.Policy;
import java.util.Iterator;
import org.w3c.dom.css.CSSValueList;
import com.steadystate.css.dom.CSSValueImpl;
import java.util.Map;
import com.steadystate.css.parser.media.MediaQuery;
import com.steadystate.css.dom.Property;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.DocumentFragment;
import com.steadystate.css.dom.CSSStyleSheetImpl;
import com.steadystate.css.dom.DOMExceptionImpl;
import org.w3c.dom.css.CSSUnknownRule;
import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSPageRule;
import com.steadystate.css.dom.MediaListImpl;
import org.w3c.dom.css.CSSMediaRule;
import org.w3c.dom.css.CSSFontFaceRule;
import org.w3c.dom.css.CSSCharsetRule;
import com.steadystate.css.dom.CSSStyleDeclarationImpl;
import org.owasp.validator.html.util.HTMLEntityEncoder;
import org.w3c.dom.css.CSSStyleRule;
import java.util.ArrayList;
import org.owasp.validator.html.CleanResults;
import org.w3c.dom.css.CSSStyleSheet;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.owasp.validator.html.model.AntiSamyPattern;
import java.util.ResourceBundle;
import com.steadystate.css.format.CSSFormat;
import org.owasp.validator.html.InternalPolicy;
import java.util.List;
import java.util.logging.Logger;

public class ZohoCssValidator
{
    private static final Logger LOGGER;
    protected static final short CSS_RGBCOLOR = 25;
    protected static final List<String> DEFAULT_CSS_SEPARATOR;
    protected final List<String> cssSeparator;
    protected final InternalPolicy policy;
    CSSFormat cssFormatter;
    protected final ResourceBundle messages;
    protected final AntiSamyPattern cssSelectorDefaultRegex;
    protected final AntiSamyPattern cssSelectorDefaultRegexExclusion;
    protected final AntiSamyPattern cssAtCharsetDefaultEncoding;
    
    protected ZohoCssValidator(final InternalPolicy policy, final ResourceBundle messages) {
        this.cssSelectorDefaultRegex = new AntiSamyPattern(Pattern.compile("[a-zA-Z0-9\\-_\"\\[\\]\\|\\s.*+,>#%:=~^$()\\P{InBasicLatin}]+"));
        this.cssSelectorDefaultRegexExclusion = new AntiSamyPattern(Pattern.compile(""));
        this.cssAtCharsetDefaultEncoding = new AntiSamyPattern(Pattern.compile("(utf-8|iso-8859-15)"));
        this.policy = policy;
        this.messages = messages;
        final String cssSeparatorList = policy.getDirective("cssSeparatorList");
        this.cssSeparator = (this.isValid(cssSeparatorList) ? Arrays.asList(cssSeparatorList.split("\\|")) : ZohoCssValidator.DEFAULT_CSS_SEPARATOR);
        this.cssFormatter = new CSSFormat();
        this.setCSSOutputFormatter();
    }
    
    private void setCSSOutputFormatter() {
        this.cssFormatter.setPropertiesInSeparateLines(4);
    }
    
    public CleanResults validateStyleSheet(final CSSStyleSheet stylesheet, final boolean isCdata, final long startOfScan) {
        String cleanedCSS = "";
        final List<String> errorMessages = new ArrayList<String>();
        final CSSRuleList rules = stylesheet.getCssRules();
        for (int currentIndex = 0; currentIndex < rules.getLength(); ++currentIndex) {
            final CSSRule rule = rules.item(currentIndex);
            try {
                if (rule.getType() == 1) {
                    final CSSStyleRule styleRule = (CSSStyleRule)rule;
                    if (!this.isValidSelectorName(styleRule.getSelectorText().toLowerCase())) {
                        this.addError(errorMessages, "error.css.tag.selector.disallowed", new Object[] { HTMLEntityEncoder.htmlEntityEncode(styleRule.getSelectorText()) });
                        stylesheet.deleteRule(currentIndex);
                        --currentIndex;
                    }
                    else {
                        final CSSStyleDeclaration propertiesList = styleRule.getStyle();
                        final CSSStyleDeclarationImpl cssProperties = (CSSStyleDeclarationImpl)propertiesList;
                        final List<Property> cssPropertiesList = cssProperties.getProperties();
                        this.validateCSSProperties(cssPropertiesList, "", errorMessages);
                    }
                }
                else if (rule.getType() == 2) {
                    final CSSCharsetRule charsetRule = (CSSCharsetRule)rule;
                    final String encoding = charsetRule.getEncoding().toLowerCase();
                    if (!this.isRegexMatches(encoding, "cssAtCharsetEncoding", this.cssAtCharsetDefaultEncoding)) {
                        this.addError(errorMessages, "error.css.charset.encoding.invalid", new Object[] { HTMLEntityEncoder.htmlEntityEncode(charsetRule.getEncoding()) });
                        stylesheet.deleteRule(currentIndex);
                        --currentIndex;
                    }
                }
                else if (rule.getType() == 5) {
                    final CSSFontFaceRule fontfaceRule = (CSSFontFaceRule)rule;
                    final CSSStyleDeclaration propertiesList = fontfaceRule.getStyle();
                    final CSSStyleDeclarationImpl cssProperties = (CSSStyleDeclarationImpl)propertiesList;
                    final List<Property> cssPropertiesList = cssProperties.getProperties();
                    this.validateCSSProperties(cssPropertiesList, "atfontface", errorMessages);
                }
                else if (rule.getType() == 4) {
                    final CSSMediaRule mediaRule = (CSSMediaRule)rule;
                    final MediaListImpl mediaList = (MediaListImpl)mediaRule.getMedia();
                    boolean isInvalid = false;
                    if (mediaList.getLength() > 0) {
                        for (int media_i = 0; media_i < mediaList.getLength(); ++media_i) {
                            final MediaQuery mediaquery = mediaList.mediaQuery(media_i);
                            final String mediaType = mediaquery.getMedia();
                            if (!"".equals(mediaType) && !this.isRegexMatches(mediaType, "mediaType", null)) {
                                this.addError(errorMessages, "error.css.media.type.invalid", new Object[] { HTMLEntityEncoder.htmlEntityEncode(mediaType.toLowerCase()) });
                                stylesheet.deleteRule(currentIndex);
                                --currentIndex;
                                isInvalid = true;
                                break;
                            }
                            final List<Property> mediaPropertiesList = mediaquery.getProperties();
                            this.validateCSSProperties(mediaPropertiesList, "atmedia", errorMessages);
                        }
                    }
                    if (!isInvalid) {
                        final CSSRuleList mediaStyleRules = mediaRule.getCssRules();
                        for (int j = 0; j < mediaStyleRules.getLength(); ++j) {
                            final CSSRule mediaStyle = mediaStyleRules.item(j);
                            if (!this.isValidMediaChildRules(mediaStyle, errorMessages)) {
                                mediaRule.deleteRule(j);
                                --j;
                            }
                        }
                    }
                }
                else if (rule.getType() == 6) {
                    final CSSPageRule pageRule = (CSSPageRule)rule;
                    final String pageSelectorName = pageRule.getSelectorText();
                    if (!this.isValidSimpleSelectorName(pageSelectorName, "pageSelector")) {
                        this.addError(errorMessages, "error.css.page.selector.invalid", new Object[] { HTMLEntityEncoder.htmlEntityEncode(pageSelectorName) });
                        stylesheet.deleteRule(currentIndex);
                        --currentIndex;
                    }
                    else {
                        final CSSStyleDeclaration propertiesList2 = pageRule.getStyle();
                        final CSSStyleDeclarationImpl cssProperties2 = (CSSStyleDeclarationImpl)propertiesList2;
                        final List<Property> cssPropertiesList2 = cssProperties2.getProperties();
                        this.validateCSSProperties(cssPropertiesList2, "paged", errorMessages);
                    }
                }
                else if (rule.getType() == 3) {
                    final CSSImportRule importRule = (CSSImportRule)rule;
                    final boolean allowAtImport = "true".equalsIgnoreCase(this.policy.getDirective("allowAtImport"));
                    final boolean isValidImportURL = this.isRegexMatches(importRule.getHref(), "onsiteURL", null) || this.isRegexMatches(importRule.getHref(), "offsiteURL", null);
                    if (!allowAtImport || !isValidImportURL) {
                        this.addError(errorMessages, "error.css.import.url.invalid", new Object[] { HTMLEntityEncoder.htmlEntityEncode(importRule.getHref()) });
                        stylesheet.deleteRule(currentIndex);
                        --currentIndex;
                    }
                }
                else if (rule.getType() == 0) {
                    final CSSUnknownRule unknownRule = (CSSUnknownRule)rule;
                    final String unknownRuleText = unknownRule.getCssText();
                    final StringBuilder keyFrameRuleBuilder = new StringBuilder();
                    String validateKeyFrameCSSRule = null;
                    if (this.isAtKeyFrameRule(unknownRuleText)) {
                        final String keyFrameSelectorString = this.getKeyFrameSelectorString(unknownRuleText);
                        if (keyFrameSelectorString.length() > 0 && this.isValidSimpleSelectorName(keyFrameSelectorString, "unknownSelector")) {
                            final ZohoKeyFrameRuleImp zki = new ZohoKeyFrameRuleImp(this);
                            final Map<String, String> keyFrameRuleMap = zki.getKeyFrameRuleMap(unknownRuleText);
                            final String validatedKeyFrameRule = zki.getValidatedKeyFrameRule(keyFrameRuleMap, errorMessages);
                            if (validatedKeyFrameRule.length() > 0) {
                                keyFrameRuleBuilder.append(keyFrameSelectorString + "{\n");
                                keyFrameRuleBuilder.append(validatedKeyFrameRule.toString() + "\n}\n");
                                validateKeyFrameCSSRule = keyFrameRuleBuilder.toString();
                            }
                        }
                        stylesheet.deleteRule(currentIndex);
                        if (validateKeyFrameCSSRule != null) {
                            stylesheet.insertRule(validateKeyFrameCSSRule, currentIndex);
                        }
                        else {
                            --currentIndex;
                        }
                    }
                    else {
                        this.addError(errorMessages, "error.css.unknown.rule", new Object[] { HTMLEntityEncoder.htmlEntityEncode(unknownRuleText) });
                        stylesheet.deleteRule(currentIndex);
                        --currentIndex;
                    }
                }
            }
            catch (final DOMExceptionImpl e) {
                stylesheet.deleteRule(currentIndex);
                --currentIndex;
            }
        }
        final CSSStyleSheetImpl resultStyleSheet = (CSSStyleSheetImpl)stylesheet;
        cleanedCSS = resultStyleSheet.getCssText(this.cssFormatter).trim();
        cleanedCSS = ((cleanedCSS.length() > 0) ? ("\n" + cleanedCSS + "\n") : cleanedCSS);
        if (isCdata && !this.policy.isUseXhtml() && cleanedCSS.trim().length() > 0) {
            cleanedCSS = "<![CDATA[[" + cleanedCSS + "]]>";
        }
        return new CleanResults(startOfScan, cleanedCSS, null, errorMessages);
    }
    
    protected CleanResults validateInlineStyles(final long startOfScan, final CSSStyleDeclarationImpl cssProperties, final List<String> errorMessages) {
        final List<Property> cssPropertiesList = cssProperties.getProperties();
        this.validateCSSProperties(cssPropertiesList, "", errorMessages);
        final String cleanedCSS = "" + cssProperties.getCssText();
        return new CleanResults(startOfScan, cleanedCSS, null, errorMessages);
    }
    
    protected void validateCSSProperties(final List<Property> cssPropertiesList, final String category, final List<String> errorMessages) {
        for (int m = 0; m < cssPropertiesList.size(); ++m) {
            boolean isInvalidProperty = false;
            final Property cssProperty = cssPropertiesList.get(m);
            final String propName = cssProperty.getName();
            final org.owasp.validator.html.model.Property antisamyPropertyRule = this.policy.getPropertyByName(propName.toLowerCase());
            if (antisamyPropertyRule != null && (category.isEmpty() || antisamyPropertyRule.getCategoryValues().contains(category.toLowerCase()))) {
                final CSSValueImpl cssValue = (CSSValueImpl)cssProperty.getValue();
                if (cssValue == null && "atmedia".equalsIgnoreCase(category)) {
                    if (!this.isRegexMatches(propName, "mediaFeatureWithEmptyValue", null)) {
                        isInvalidProperty = true;
                    }
                }
                else if (cssValue.getCssValueType() == 2) {
                    final CSSValueList cssValList = (CSSValueList)cssValue;
                    for (int n = 0; n < cssValList.getLength(); ++n) {
                        final CSSValueImpl cssSubValue = (CSSValueImpl)cssValList.item(n);
                        if (!this.isValidCSSValue(cssSubValue, antisamyPropertyRule)) {
                            isInvalidProperty = true;
                            break;
                        }
                    }
                }
                else if (!this.isValidCSSValue(cssValue, antisamyPropertyRule)) {
                    isInvalidProperty = true;
                }
            }
            else {
                isInvalidProperty = true;
            }
            if (isInvalidProperty) {
                cssPropertiesList.remove(m);
                --m;
            }
        }
    }
    
    private boolean isValidCSSValue(final CSSValueImpl cssValue, final org.owasp.validator.html.model.Property antisamyPropertyRule) {
        String propValue = cssValue.getCssText().toLowerCase();
        if (cssValue.getPrimitiveType() == 25) {
            propValue = propValue.replaceAll("\\s", "");
        }
        return (propValue.length() == 1 && this.cssSeparator.contains(propValue)) || this.isValidValue(antisamyPropertyRule, propValue);
    }
    
    private boolean isValidMediaChildRules(final CSSRule mediaChildRule, final List<String> errorMessages) {
        if (mediaChildRule.getType() != 1) {
            return false;
        }
        final CSSStyleRule styleRule = (CSSStyleRule)mediaChildRule;
        if (!this.isValidSelectorName(styleRule.getSelectorText().toLowerCase())) {
            this.addError(errorMessages, "error.css.atmedia.selector.invalid", new Object[] { HTMLEntityEncoder.htmlEntityEncode(styleRule.getSelectorText()) });
            return false;
        }
        final CSSStyleDeclaration propertiesList = styleRule.getStyle();
        final CSSStyleDeclarationImpl cssProperties = (CSSStyleDeclarationImpl)propertiesList;
        final List<Property> cssPropertiesList = cssProperties.getProperties();
        this.validateCSSProperties(cssPropertiesList, "", errorMessages);
        return true;
    }
    
    boolean isValidValue(final org.owasp.validator.html.model.Property property, String propertyValue) {
        propertyValue = propertyValue.toLowerCase();
        for (final String allowedValue : property.getAllowedValues()) {
            if (allowedValue != null && allowedValue.equals(propertyValue)) {
                return true;
            }
        }
        for (final Pattern pattern : property.getAllowedRegExp()) {
            if (pattern != null && pattern.matcher(propertyValue).matches()) {
                return true;
            }
        }
        for (final String shorthandRef : property.getShorthandRefs()) {
            final org.owasp.validator.html.model.Property shorthandProperty = this.policy.getPropertyByName(shorthandRef);
            if (shorthandProperty != null && this.isValidValue(shorthandProperty, propertyValue)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isValidSelectorName(final String selectorText) {
        return this.isRegexMatches(selectorText, "cssSelectorCommonRegex", this.cssSelectorDefaultRegex) && !this.isRegexMatches(selectorText, "cssSelectorCommonRegexExclusion", this.cssSelectorDefaultRegexExclusion);
    }
    
    boolean isValidSimpleSelectorName(final String selectorName, final String policyRegexName) {
        return selectorName.isEmpty() || this.isRegexMatches(selectorName, policyRegexName, null);
    }
    
    private boolean isRegexMatches(final String text, final String policyRegexName, final AntiSamyPattern cssSelectorDefaultRegex) {
        AntiSamyPattern regexPattern = this.policy.getCommonRegularExpressions(policyRegexName);
        regexPattern = ((regexPattern == null) ? cssSelectorDefaultRegex : regexPattern);
        return regexPattern != null && regexPattern.matches(text);
    }
    
    private boolean isAtKeyFrameRule(final String unknownRuleText) {
        final List<String> atKeyFrameCSSSelector = this.policy.getAtKeyFrameCSSSelectorsList();
        for (final String keyFrameSelector : atKeyFrameCSSSelector) {
            if (unknownRuleText.startsWith(keyFrameSelector)) {
                return true;
            }
        }
        return false;
    }
    
    String getKeyFrameSelectorString(final String kf) {
        final String kfSelectorString = (kf.indexOf("{") != -1) ? kf.substring(0, kf.indexOf("{")).trim() : "";
        return kfSelectorString;
    }
    
    Policy getPolicy() {
        return this.policy;
    }
    
    protected void addError(final List<String> errorMessages, final String errorKey, final Object[] objects) {
        try {
            errorMessages.add(ErrorMessageUtil.getMessage(this.messages, errorKey, objects));
        }
        catch (final MissingResourceException e) {
            ZohoCssValidator.LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }
    
    public boolean isValid(final Object value) {
        return value != null && !value.equals("null") && !value.equals("");
    }
    
    static {
        LOGGER = Logger.getLogger(ZohoCssValidator.class.getName());
        DEFAULT_CSS_SEPARATOR = Arrays.asList(",", "/");
    }
}
