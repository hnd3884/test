package org.apache.jasper.optimizations;

import java.util.regex.Matcher;
import org.apache.jasper.compiler.JspUtil;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.apache.jasper.JspCompilationContext;
import org.apache.juli.logging.LogFactory;
import java.util.regex.Pattern;
import org.apache.juli.logging.Log;
import org.apache.jasper.compiler.ELInterpreter;

public class ELInterpreterTagSetters implements ELInterpreter
{
    private final Log log;
    private final Pattern PATTERN_BOOLEAN;
    private final Pattern PATTERN_STRING_CONSTANT;
    private final Pattern PATTERN_NUMERIC;
    
    public ELInterpreterTagSetters() {
        this.log = LogFactory.getLog((Class)ELInterpreterTagSetters.class);
        this.PATTERN_BOOLEAN = Pattern.compile("[$][{]([\"']?)(true|false)\\1[}]");
        this.PATTERN_STRING_CONSTANT = Pattern.compile("[$][{]([\"'])(\\w+)\\1[}]");
        this.PATTERN_NUMERIC = Pattern.compile("[$][{]([\"'])([+-]?\\d+(\\.\\d+)?)\\1[}]");
    }
    
    @Override
    public String interpreterCall(final JspCompilationContext context, final boolean isTagFile, final String expression, final Class<?> expectedType, final String fnmapvar) {
        String result = null;
        if (Boolean.TYPE == expectedType) {
            final Matcher m = this.PATTERN_BOOLEAN.matcher(expression);
            if (m.matches()) {
                result = m.group(2);
            }
        }
        else if (Boolean.class == expectedType) {
            final Matcher m = this.PATTERN_BOOLEAN.matcher(expression);
            if (m.matches()) {
                if ("true".equals(m.group(2))) {
                    result = "Boolean.TRUE";
                }
                else {
                    result = "Boolean.FALSE";
                }
            }
        }
        else if (Character.TYPE == expectedType) {
            final Matcher m = this.PATTERN_STRING_CONSTANT.matcher(expression);
            if (m.matches()) {
                return "'" + m.group(2).charAt(0) + "'";
            }
        }
        else if (Character.class == expectedType) {
            final Matcher m = this.PATTERN_STRING_CONSTANT.matcher(expression);
            if (m.matches()) {
                return "Character.valueOf('" + m.group(2).charAt(0) + "')";
            }
        }
        else if (BigDecimal.class == expectedType) {
            final Matcher m = this.PATTERN_NUMERIC.matcher(expression);
            if (m.matches()) {
                try {
                    final BigDecimal unused = new BigDecimal(m.group(2));
                    result = "new java.math.BigDecimal(\"" + m.group(2) + "\")";
                }
                catch (final NumberFormatException e) {
                    this.log.debug((Object)("Failed to convert [" + m.group(2) + "] to BigDecimal"), (Throwable)e);
                }
            }
        }
        else if (Long.TYPE == expectedType || Long.class == expectedType) {
            final Matcher m = this.PATTERN_NUMERIC.matcher(expression);
            if (m.matches()) {
                try {
                    final Long unused2 = Long.valueOf(m.group(2));
                    if (expectedType.isPrimitive()) {
                        result = m.group(2) + "L";
                    }
                    else {
                        result = "Long.valueOf(\"" + m.group(2) + "\")";
                    }
                }
                catch (final NumberFormatException e) {
                    this.log.debug((Object)("Failed to convert [" + m.group(2) + "] to Long"), (Throwable)e);
                }
            }
        }
        else if (Integer.TYPE == expectedType || Integer.class == expectedType) {
            final Matcher m = this.PATTERN_NUMERIC.matcher(expression);
            if (m.matches()) {
                try {
                    final Integer unused3 = Integer.valueOf(m.group(2));
                    if (expectedType.isPrimitive()) {
                        result = m.group(2);
                    }
                    else {
                        result = "Integer.valueOf(\"" + m.group(2) + "\")";
                    }
                }
                catch (final NumberFormatException e) {
                    this.log.debug((Object)("Failed to convert [" + m.group(2) + "] to Integer"), (Throwable)e);
                }
            }
        }
        else if (Short.TYPE == expectedType || Short.class == expectedType) {
            final Matcher m = this.PATTERN_NUMERIC.matcher(expression);
            if (m.matches()) {
                try {
                    final Short unused4 = Short.valueOf(m.group(2));
                    if (expectedType.isPrimitive()) {
                        result = "(short) " + m.group(2);
                    }
                    else {
                        result = "Short.valueOf(\"" + m.group(2) + "\")";
                    }
                }
                catch (final NumberFormatException e) {
                    this.log.debug((Object)("Failed to convert [" + m.group(2) + "] to Short"), (Throwable)e);
                }
            }
        }
        else if (Byte.TYPE == expectedType || Byte.class == expectedType) {
            final Matcher m = this.PATTERN_NUMERIC.matcher(expression);
            if (m.matches()) {
                try {
                    final Byte unused5 = Byte.valueOf(m.group(2));
                    if (expectedType.isPrimitive()) {
                        result = "(byte) " + m.group(2);
                    }
                    else {
                        result = "Byte.valueOf(\"" + m.group(2) + "\")";
                    }
                }
                catch (final NumberFormatException e) {
                    this.log.debug((Object)("Failed to convert [" + m.group(2) + "] to Byte"), (Throwable)e);
                }
            }
        }
        else if (Double.TYPE == expectedType || Double.class == expectedType) {
            final Matcher m = this.PATTERN_NUMERIC.matcher(expression);
            if (m.matches()) {
                try {
                    final Double unused6 = Double.valueOf(m.group(2));
                    if (expectedType.isPrimitive()) {
                        result = m.group(2);
                    }
                    else {
                        result = "Double.valueOf(\"" + m.group(2) + "\")";
                    }
                }
                catch (final NumberFormatException e) {
                    this.log.debug((Object)("Failed to convert [" + m.group(2) + "] to Double"), (Throwable)e);
                }
            }
        }
        else if (Float.TYPE == expectedType || Float.class == expectedType) {
            final Matcher m = this.PATTERN_NUMERIC.matcher(expression);
            if (m.matches()) {
                try {
                    final Float unused7 = Float.valueOf(m.group(2));
                    if (expectedType.isPrimitive()) {
                        result = m.group(2) + "f";
                    }
                    else {
                        result = "Float.valueOf(\"" + m.group(2) + "\")";
                    }
                }
                catch (final NumberFormatException e) {
                    this.log.debug((Object)("Failed to convert [" + m.group(2) + "] to Float"), (Throwable)e);
                }
            }
        }
        else if (BigInteger.class == expectedType) {
            final Matcher m = this.PATTERN_NUMERIC.matcher(expression);
            if (m.matches()) {
                try {
                    final BigInteger unused8 = new BigInteger(m.group(2));
                    result = "new java.math.BigInteger(\"" + m.group(2) + "\")";
                }
                catch (final NumberFormatException e) {
                    this.log.debug((Object)("Failed to convert [" + m.group(2) + "] to BigInteger"), (Throwable)e);
                }
            }
        }
        else if (expectedType.isEnum()) {
            final Matcher m = this.PATTERN_STRING_CONSTANT.matcher(expression);
            if (m.matches()) {
                try {
                    final Enum<?> enumValue = Enum.valueOf(expectedType, m.group(2));
                    result = expectedType.getName() + "." + enumValue.name();
                }
                catch (final IllegalArgumentException iae) {
                    this.log.debug((Object)("Failed to convert [" + m.group(2) + "] to Enum type [" + expectedType.getName() + "]"), (Throwable)iae);
                }
            }
        }
        else if (String.class == expectedType) {
            final Matcher m = this.PATTERN_STRING_CONSTANT.matcher(expression);
            if (m.matches()) {
                result = "\"" + m.group(2) + "\"";
            }
        }
        if (result == null) {
            result = JspUtil.interpreterCall(isTagFile, expression, expectedType, fnmapvar);
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Expression [" + expression + "], type [" + expectedType.getName() + "], returns [" + result + "]"));
        }
        return result;
    }
}
