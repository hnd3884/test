package com.adventnet.client.util.web;

import java.util.Locale;
import java.util.Date;
import org.apache.commons.validator.GenericTypeValidator;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.util.ValidatorUtils;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMessages;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.ValidatorAction;

public class GenericFormValidator
{
    public static final String FIELD_TEST_NULL = "NULL";
    public static final String FIELD_TEST_NOTNULL = "NOTNULL";
    public static final String FIELD_TEST_EQUAL = "EQUAL";
    
    public static boolean validateRequired(final Object bean, final ValidatorAction va, final Field field, final ActionMessages errors, final HttpServletRequest request) {
        String value = null;
        if (isString(bean)) {
            value = (String)bean;
        }
        else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        return !GenericValidator.isBlankOrNull(value);
    }
    
    public static boolean validateRequiredIf(final Object bean, final ValidatorAction va, final Field field, final ActionMessages errors, final Validator validator, final HttpServletRequest request) {
        final Object form = validator.getParameterValue("java.lang.Object");
        String value = null;
        boolean required = false;
        if (isString(bean)) {
            value = (String)bean;
        }
        else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        int i = 0;
        String fieldJoin = "AND";
        if (!GenericValidator.isBlankOrNull(field.getVarValue("fieldJoin"))) {
            fieldJoin = field.getVarValue("fieldJoin");
        }
        if (fieldJoin.equalsIgnoreCase("AND")) {
            required = true;
        }
        while (!GenericValidator.isBlankOrNull(field.getVarValue("field[" + i + "]"))) {
            String dependProp = field.getVarValue("field[" + i + "]");
            final String dependTest = field.getVarValue("fieldTest[" + i + "]");
            final String dependTestValue = field.getVarValue("fieldValue[" + i + "]");
            String dependIndexed = field.getVarValue("fieldIndexed[" + i + "]");
            if (dependIndexed == null) {
                dependIndexed = "false";
            }
            String dependVal = null;
            boolean thisRequired = false;
            if (field.isIndexed() && dependIndexed.equalsIgnoreCase("true")) {
                final String key = field.getKey();
                if (key.indexOf("[") > -1 && key.indexOf("]") > -1) {
                    final String ind = key.substring(0, key.indexOf(".") + 1);
                    dependProp = ind + dependProp;
                }
            }
            dependVal = ValidatorUtils.getValueAsString(form, dependProp);
            if (dependTest.equals("NULL")) {
                thisRequired = (dependVal == null || dependVal.length() <= 0);
            }
            if (dependTest.equals("NOTNULL")) {
                thisRequired = (dependVal != null && dependVal.length() > 0);
            }
            if (dependTest.equals("EQUAL")) {
                thisRequired = dependTestValue.equalsIgnoreCase(dependVal);
            }
            if (fieldJoin.equalsIgnoreCase("AND")) {
                required = (required && thisRequired);
            }
            else {
                required = (required || thisRequired);
            }
            ++i;
        }
        return !required || !GenericValidator.isBlankOrNull(value);
    }
    
    public static boolean validateMask(final Object bean, final ValidatorAction va, final Field field, final ActionMessages errors, final HttpServletRequest request) {
        final String mask = field.getVarValue("mask");
        String value = null;
        if (isString(bean)) {
            value = (String)bean;
        }
        else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        try {
            return GenericValidator.isBlankOrNull(value) || GenericValidator.matchRegexp(value, mask);
        }
        catch (final Exception ex) {
            return true;
        }
    }
    
    public static Byte validateByte(final Object bean, final ValidatorAction va, final Field field, final ActionMessages errors, final HttpServletRequest request) {
        Byte result = null;
        String value = null;
        if (isString(bean)) {
            value = (String)bean;
        }
        else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        if (!GenericValidator.isBlankOrNull(value)) {
            result = GenericTypeValidator.formatByte(value);
            if (result == null) {}
        }
        return result;
    }
    
    public static Short validateShort(final Object bean, final ValidatorAction va, final Field field, final ActionMessages errors, final HttpServletRequest request) {
        Short result = null;
        String value = null;
        if (isString(bean)) {
            value = (String)bean;
        }
        else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        if (!GenericValidator.isBlankOrNull(value)) {
            result = GenericTypeValidator.formatShort(value);
            if (result == null) {}
        }
        return result;
    }
    
    public static Integer validateInteger(final Object bean, final ValidatorAction va, final Field field, final HttpServletRequest request) {
        Integer result = null;
        String value = null;
        if (isString(bean)) {
            value = (String)bean;
        }
        else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        if (!GenericValidator.isBlankOrNull(value)) {
            result = GenericTypeValidator.formatInt(value);
            if (result == null) {}
        }
        return result;
    }
    
    public static Long validateLong(final Object bean, final ValidatorAction va, final Field field, final ActionMessages errors, final HttpServletRequest request) {
        Long result = null;
        String value = null;
        if (isString(bean)) {
            value = (String)bean;
        }
        else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        if (!GenericValidator.isBlankOrNull(value)) {
            result = GenericTypeValidator.formatLong(value);
            if (result == null) {}
        }
        return result;
    }
    
    public static Float validateFloat(final Object bean, final ValidatorAction va, final Field field, final ActionMessages errors, final HttpServletRequest request) {
        Float result = null;
        String value = null;
        if (isString(bean)) {
            value = (String)bean;
        }
        else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        if (!GenericValidator.isBlankOrNull(value)) {
            result = GenericTypeValidator.formatFloat(value);
            if (result == null) {}
        }
        return result;
    }
    
    public static Double validateDouble(final Object bean, final ValidatorAction va, final Field field, final ActionMessages errors, final HttpServletRequest request) {
        Double result = null;
        String value = null;
        if (isString(bean)) {
            value = (String)bean;
        }
        else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        if (!GenericValidator.isBlankOrNull(value)) {
            result = GenericTypeValidator.formatDouble(value);
            if (result == null) {}
        }
        return result;
    }
    
    public static Date validateDate(final Object bean, final ValidatorAction va, final Field field, final ActionMessages errors, final HttpServletRequest request) {
        Date result = null;
        String value = null;
        if (isString(bean)) {
            value = (String)bean;
        }
        else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        final String datePattern = field.getVarValue("datePattern");
        final String datePatternStrict = field.getVarValue("datePatternStrict");
        final Locale locale = request.getLocale();
        if (!GenericValidator.isBlankOrNull(value)) {
            try {
                if (datePattern != null && datePattern.length() > 0) {
                    result = GenericTypeValidator.formatDate(value, datePattern, false);
                }
                else if (datePatternStrict != null && datePatternStrict.length() > 0) {
                    result = GenericTypeValidator.formatDate(value, datePatternStrict, true);
                }
                else {
                    result = GenericTypeValidator.formatDate(value, locale);
                }
            }
            catch (final Exception ex) {}
            if (result == null) {}
        }
        return result;
    }
    
    public static boolean validateIntRange(final Object bean, final ValidatorAction va, final Field field, final ActionMessages errors, final HttpServletRequest request) {
        String value = null;
        if (isString(bean)) {
            value = (String)bean;
        }
        else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        if (!GenericValidator.isBlankOrNull(value)) {
            try {
                final int intValue = Integer.parseInt(value);
                final int min = Integer.parseInt(field.getVarValue("min"));
                final int max = Integer.parseInt(field.getVarValue("max"));
                if (!GenericValidator.isInRange(intValue, min, max)) {
                    return false;
                }
            }
            catch (final Exception e) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean validateDoubleRange(final Object bean, final ValidatorAction va, final Field field, final ActionMessages errors, final HttpServletRequest request) {
        String value = null;
        if (isString(bean)) {
            value = (String)bean;
        }
        else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        if (!GenericValidator.isBlankOrNull(value)) {
            try {
                final double doubleValue = Double.parseDouble(value);
                final double min = Double.parseDouble(field.getVarValue("min"));
                final double max = Double.parseDouble(field.getVarValue("max"));
                if (!GenericValidator.isInRange(doubleValue, min, max)) {
                    return false;
                }
            }
            catch (final Exception e) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean validateFloatRange(final Object bean, final ValidatorAction va, final Field field, final ActionMessages errors, final HttpServletRequest request) {
        String value = null;
        if (isString(bean)) {
            value = (String)bean;
        }
        else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        if (!GenericValidator.isBlankOrNull(value)) {
            try {
                final float floatValue = Float.parseFloat(value);
                final float min = Float.parseFloat(field.getVarValue("min"));
                final float max = Float.parseFloat(field.getVarValue("max"));
                if (!GenericValidator.isInRange(floatValue, min, max)) {
                    return false;
                }
            }
            catch (final Exception e) {
                return false;
            }
        }
        return true;
    }
    
    public static Long validateCreditCard(final Object bean, final ValidatorAction va, final Field field, final ActionMessages errors, final HttpServletRequest request) {
        Long result = null;
        String value = null;
        if (isString(bean)) {
            value = (String)bean;
        }
        else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        if (!GenericValidator.isBlankOrNull(value)) {
            result = GenericTypeValidator.formatCreditCard(value);
            if (result == null) {}
        }
        return result;
    }
    
    public static boolean validateEmail(final Object bean, final ValidatorAction va, final Field field, final ActionMessages errors, final HttpServletRequest request) {
        String value = null;
        if (isString(bean)) {
            value = (String)bean;
        }
        else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        return GenericValidator.isBlankOrNull(value) || GenericValidator.isEmail(value);
    }
    
    public static boolean validateMaxLength(final Object bean, final ValidatorAction va, final Field field, final ActionMessages errors, final HttpServletRequest request) {
        String value = null;
        if (isString(bean)) {
            value = (String)bean;
        }
        else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        if (value != null) {
            try {
                final int max = Integer.parseInt(field.getVarValue("maxlength"));
                if (!GenericValidator.maxLength(value, max)) {
                    return false;
                }
            }
            catch (final Exception e) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean validateMinLength(final Object bean, final ValidatorAction va, final Field field, final ActionMessages errors, final HttpServletRequest request) {
        String value = null;
        if (isString(bean)) {
            value = (String)bean;
        }
        else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        if (!GenericValidator.isBlankOrNull(value)) {
            try {
                final int min = Integer.parseInt(field.getVarValue("minlength"));
                if (!GenericValidator.minLength(value, min)) {
                    return false;
                }
            }
            catch (final Exception e) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean validateUrl(final Object bean, final ValidatorAction va, final Field field, final ActionMessages errors, final HttpServletRequest request) {
        String value = null;
        if (isString(bean)) {
            value = (String)bean;
        }
        else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        return GenericValidator.isBlankOrNull(value) || GenericValidator.isUrl(value);
    }
    
    protected static boolean isString(final Object o) {
        return o == null || String.class.isInstance(o);
    }
}
