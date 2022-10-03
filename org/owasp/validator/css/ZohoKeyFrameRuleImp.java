package org.owasp.validator.css;

import org.w3c.dom.css.CSSValueList;
import com.steadystate.css.dom.CSSValueImpl;
import org.owasp.validator.html.model.Property;
import java.util.Iterator;
import com.steadystate.css.dom.DOMExceptionImpl;
import org.owasp.validator.html.util.HTMLEntityEncoder;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class ZohoKeyFrameRuleImp
{
    ZohoCssValidator cssValidator;
    
    public ZohoKeyFrameRuleImp(final ZohoCssValidator zohoCssValidator) {
        this.cssValidator = zohoCssValidator;
    }
    
    Map<String, String> getKeyFrameRuleMap(final String unknownRuleText) {
        final Map<String, String> keyFrameRuleMap = new LinkedHashMap<String, String>();
        String innercssrules;
        for (String keyFrameCSSRules = getKeyFrameCSSRules(unknownRuleText); keyFrameCSSRules.length() > 0; keyFrameCSSRules = keyFrameCSSRules.substring(keyFrameCSSRules.indexOf(innercssrules) + innercssrules.length() + 1)) {
            final String innerselector = this.cssValidator.getKeyFrameSelectorString(keyFrameCSSRules);
            if (innerselector.length() <= 0) {
                break;
            }
            keyFrameCSSRules = keyFrameCSSRules.substring(keyFrameCSSRules.indexOf(innerselector) + innerselector.length());
            innercssrules = getKeyFrameInnerCSSRules(keyFrameCSSRules);
            if (innercssrules.trim().length() <= 0) {
                break;
            }
            keyFrameRuleMap.put(innerselector, innercssrules.trim());
        }
        return keyFrameRuleMap;
    }
    
    private static String getKeyFrameCSSRules(String kframeCSSString) {
        if (kframeCSSString.indexOf("{") != -1 && kframeCSSString.lastIndexOf("}") != -1) {
            kframeCSSString = kframeCSSString.substring(kframeCSSString.indexOf("{") + 1, kframeCSSString.lastIndexOf("}"));
            return kframeCSSString.trim();
        }
        return "";
    }
    
    private static String getKeyFrameInnerCSSRules(String kframeInnerCSSRules) {
        if (kframeInnerCSSRules.indexOf("{") != -1 && kframeInnerCSSRules.indexOf("}") != -1) {
            kframeInnerCSSRules = kframeInnerCSSRules.substring(kframeInnerCSSRules.indexOf("{") + 1, kframeInnerCSSRules.indexOf("}"));
            return kframeInnerCSSRules;
        }
        return "";
    }
    
    String getValidatedKeyFrameRule(final Map<String, String> keyFrameRuleMap, final List<String> errorMessages) {
        final StringBuilder innerCSSRuleBuilder = new StringBuilder();
        for (final Map.Entry<String, String> cssRules : keyFrameRuleMap.entrySet()) {
            final String selector = cssRules.getKey();
            if (this.cssValidator.isValidSimpleSelectorName(selector, "keyFrameInnerSelector")) {
                final String css = cssRules.getValue();
                final String[] cssPropertyArray = css.split(";");
                final StringBuilder innerCSSRulePropertyBuilder = new StringBuilder();
                for (final String cssProperty : cssPropertyArray) {
                    if (cssProperty.contains(":")) {
                        final String[] cssProp = cssProperty.split(":", 2);
                        final String propName = cssProp[0].trim();
                        final Property antisamyPropertyRule = this.cssValidator.getPolicy().getPropertyByName(propName.toLowerCase());
                        if (antisamyPropertyRule != null) {
                            try {
                                final String validatePropertyValue = this.getvalidatedKeyFramePropertyValue(cssProp[1].trim(), antisamyPropertyRule);
                                if (validatePropertyValue.length() > 0) {
                                    innerCSSRulePropertyBuilder.append("    " + propName + " : " + validatePropertyValue + ";\n");
                                }
                            }
                            catch (final DOMExceptionImpl e) {
                                this.cssValidator.addError(errorMessages, "error.css.keyframe.property.invalid", new Object[] { HTMLEntityEncoder.htmlEntityEncode(propName) });
                            }
                        }
                    }
                }
                if (innerCSSRulePropertyBuilder.length() <= 0) {
                    continue;
                }
                innerCSSRuleBuilder.append(selector + "{\n");
                innerCSSRuleBuilder.append((Object)innerCSSRulePropertyBuilder + "}\n");
            }
        }
        return innerCSSRuleBuilder.toString();
    }
    
    private String getvalidatedKeyFramePropertyValue(final String value, final Property antisamyPropertyRule) {
        final CSSValueImpl cssValue = new CSSValueImpl();
        cssValue.setCssText(value);
        final StringBuilder cssvalueBuilder = new StringBuilder();
        if (cssValue.getCssValueType() == 2) {
            final CSSValueList cssValList = (CSSValueList)cssValue;
            for (int n = 0; n < cssValList.getLength(); ++n) {
                final CSSValueImpl cssSubValue = (CSSValueImpl)cssValList.item(n);
                this.validateKeyFramePropertyValue(cssSubValue, antisamyPropertyRule, cssvalueBuilder);
            }
        }
        else {
            this.validateKeyFramePropertyValue(cssValue, antisamyPropertyRule, cssvalueBuilder);
        }
        return cssvalueBuilder.toString();
    }
    
    private void validateKeyFramePropertyValue(final CSSValueImpl cssValue, final Property antisamyPropertyRule, final StringBuilder cssvalueBuilder) {
        String propValue = cssValue.getCssText();
        if (cssValue.getPrimitiveType() == 25) {
            propValue = propValue.replaceAll("\\s", "");
        }
        if (this.cssValidator.isValidValue(antisamyPropertyRule, propValue)) {
            cssvalueBuilder.append(propValue + " ");
        }
    }
}
