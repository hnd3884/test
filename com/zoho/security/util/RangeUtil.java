package com.zoho.security.util;

import com.adventnet.iam.security.SecurityRequestWrapper;
import java.util.logging.Level;
import com.adventnet.iam.security.SecurityUtil;
import java.util.regex.Matcher;
import com.adventnet.iam.security.IAMSecurityException;
import com.zoho.security.api.Range;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class RangeUtil
{
    private static final Pattern BOUND_PATTERN;
    private static final Pattern CONDITIONAL_PATTERN;
    private static final Logger LOGGER;
    
    public static <T extends Comparable<T>> Range<?> createRange(final String rangeStr, final String dataType) throws IAMSecurityException {
        Matcher m;
        if ((m = RangeUtil.BOUND_PATTERN.matcher(rangeStr)).matches()) {
            final T lowerLimit = typeCastAsNumericType(m.group(2), dataType);
            final T upperLimit = typeCastAsNumericType(m.group(3), dataType);
            return Range.createRange(lowerLimit, upperLimit, null);
        }
        if ((m = RangeUtil.CONDITIONAL_PATTERN.matcher(rangeStr)).matches()) {
            final T lowerOrUpperLimit = typeCastAsNumericType(m.group(3), dataType);
            String relationalOperator = m.group(2);
            if (relationalOperator == null) {
                relationalOperator = "==";
            }
            if ("==".equals(relationalOperator)) {
                return Range.createRange(lowerOrUpperLimit, lowerOrUpperLimit, relationalOperator);
            }
            if ("<".equals(relationalOperator)) {
                return Range.createRange(null, lowerOrUpperLimit, relationalOperator);
            }
            if (">".equals(relationalOperator)) {
                return Range.createRange(lowerOrUpperLimit, null, relationalOperator);
            }
            if ("<=".equals(relationalOperator)) {
                return Range.createRange(null, lowerOrUpperLimit, relationalOperator);
            }
            if (">=".equals(relationalOperator)) {
                return Range.createRange(lowerOrUpperLimit, null, relationalOperator);
            }
        }
        throw new IAMSecurityException("Invalid Range configuration :: Unsupported format " + rangeStr);
    }
    
    public static <T extends Comparable<T>> T typeCastAsNumericType(final String value, final String dataType) {
        switch (dataType) {
            case "short":
            case "Short": {
                return (T)Short.valueOf(value);
            }
            case "int":
            case "Integer": {
                return (T)Integer.valueOf(value);
            }
            case "long":
            case "Long": {
                return (T)Long.valueOf(value);
            }
            case "float":
            case "Float": {
                return (T)Float.valueOf(value);
            }
            case "double":
            case "Double": {
                return (T)Double.valueOf(value);
            }
            default: {
                throw new IAMSecurityException("Data type: \"" + dataType + "\" is not a numeric type. Range is only supported for numeric types");
            }
        }
    }
    
    public static Range createFixedRangeForInteger(final String attributeName, String attributeValue, final int defaultRangeExtension, final boolean isMandatory) {
        if (!SecurityUtil.isValid(attributeValue)) {
            attributeValue = (isMandatory ? "1-1" : "0-1");
            return createRange(attributeValue, "int");
        }
        final Range tempRange = createRange(attributeValue, "int");
        final int expectedLowerLimit = isMandatory ? 1 : 0;
        int lowerLimitOfFixedRange = 0;
        int upperLimitOfFixedRange = 0;
        if (tempRange.getLowerLimit() == null) {
            upperLimitOfFixedRange = ("<=".equals(tempRange.getRelationalOperator()) ? tempRange.getUpperLimit() : (tempRange.getUpperLimit() - 1));
            if (upperLimitOfFixedRange < expectedLowerLimit) {
                RangeUtil.LOGGER.log(Level.SEVERE, " \"{0}\" = \"{1}\" is invalid , it should be <=X , (where X >= {2}) ", new Object[] { attributeName, attributeValue, expectedLowerLimit });
                throw new IAMSecurityException("INVALID_CONFIGURATION");
            }
            return defineStrictIntegerRange(attributeName, attributeValue, expectedLowerLimit, upperLimitOfFixedRange);
        }
        else if (tempRange.getUpperLimit() == null) {
            lowerLimitOfFixedRange = (">=".equals(tempRange.getRelationalOperator()) ? tempRange.getLowerLimit() : (tempRange.getLowerLimit() + 1));
            upperLimitOfFixedRange = ((lowerLimitOfFixedRange > Integer.MAX_VALUE - defaultRangeExtension) ? Integer.MAX_VALUE : (lowerLimitOfFixedRange + defaultRangeExtension));
            if (lowerLimitOfFixedRange < expectedLowerLimit) {
                RangeUtil.LOGGER.log(Level.SEVERE, " \"{0}\" = \"{1}\" is invalid ,  it should be >=X , (where X>= {2}) ", new Object[] { attributeName, attributeValue, expectedLowerLimit });
                throw new IAMSecurityException("INVALID_CONFIGURATION");
            }
            return defineStrictIntegerRange(attributeName, attributeValue, lowerLimitOfFixedRange, upperLimitOfFixedRange);
        }
        else {
            lowerLimitOfFixedRange = tempRange.getLowerLimit();
            upperLimitOfFixedRange = tempRange.getUpperLimit();
            if (upperLimitOfFixedRange < 0 || lowerLimitOfFixedRange < 0 || upperLimitOfFixedRange < lowerLimitOfFixedRange || lowerLimitOfFixedRange < expectedLowerLimit) {
                RangeUtil.LOGGER.log(Level.SEVERE, " \"{0}\" = \"{1}\" is invalid  , for a valid {0} = \"X-Y\"  (or)  {0} = \"X\" ,  X>= {2} and X < Y ", new Object[] { attributeName, attributeValue, expectedLowerLimit });
                throw new IAMSecurityException("INVALID_CONFIGURATION");
            }
            return tempRange;
        }
    }
    
    private static Range defineStrictIntegerRange(final String attributeName, final String attributeValue, final int minBoundary, final int maxBoundary) {
        if (minBoundary < 0 || maxBoundary < 0) {
            RangeUtil.LOGGER.log(Level.SEVERE, " \"{0}\" = \"{1}\" is invalid , Range is translated in the negative number range , it is an invalid configuration ", new Object[] { attributeName, attributeValue });
            throw new IAMSecurityException("INVALID_CONFIGURATION");
        }
        return Range.createRange(minBoundary, maxBoundary, null);
    }
    
    static {
        BOUND_PATTERN = Pattern.compile("(([+-]?\\d*\\.?\\d+)\\-([+-]?\\d*\\.?\\d+))");
        CONDITIONAL_PATTERN = Pattern.compile("((<=|>=|<|>)?([+-]?\\d*\\.?\\d+))");
        LOGGER = Logger.getLogger(SecurityRequestWrapper.class.getName());
    }
}
