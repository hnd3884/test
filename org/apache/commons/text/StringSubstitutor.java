package org.apache.commons.text;

import java.util.ArrayList;
import org.apache.commons.lang3.Validate;
import org.apache.commons.text.matcher.StringMatcherFactory;
import java.util.List;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map;
import org.apache.commons.text.lookup.StringLookupFactory;
import org.apache.commons.text.lookup.StringLookup;
import org.apache.commons.text.matcher.StringMatcher;

public class StringSubstitutor
{
    public static final String DEFAULT_VAR_DEFAULT = ":-";
    public static final String DEFAULT_VAR_END = "}";
    public static final String DEFAULT_VAR_START = "${";
    public static final char DEFAULT_ESCAPE = '$';
    public static final StringMatcher DEFAULT_PREFIX;
    public static final StringMatcher DEFAULT_SUFFIX;
    public static final StringMatcher DEFAULT_VALUE_DELIMITER;
    private char escapeChar;
    private StringMatcher prefixMatcher;
    private StringMatcher suffixMatcher;
    private StringMatcher valueDelimiterMatcher;
    private StringLookup variableResolver;
    private boolean enableSubstitutionInVariables;
    private boolean preserveEscapes;
    private boolean disableSubstitutionInValues;
    private boolean enableUndefinedVariableException;
    
    public static StringSubstitutor createInterpolator() {
        return new StringSubstitutor(StringLookupFactory.INSTANCE.interpolatorStringLookup());
    }
    
    public static <V> String replace(final Object source, final Map<String, V> valueMap) {
        return new StringSubstitutor((Map<String, V>)valueMap).replace(source);
    }
    
    public static <V> String replace(final Object source, final Map<String, V> valueMap, final String prefix, final String suffix) {
        return new StringSubstitutor((Map<String, V>)valueMap, prefix, suffix).replace(source);
    }
    
    public static String replace(final Object source, final Properties valueProperties) {
        if (valueProperties == null) {
            return source.toString();
        }
        final Map<String, String> valueMap = new HashMap<String, String>();
        final Enumeration<?> propNames = valueProperties.propertyNames();
        while (propNames.hasMoreElements()) {
            final String propName = (String)propNames.nextElement();
            final String propValue = valueProperties.getProperty(propName);
            valueMap.put(propName, propValue);
        }
        return replace(source, valueMap);
    }
    
    public static String replaceSystemProperties(final Object source) {
        return new StringSubstitutor(StringLookupFactory.INSTANCE.systemPropertyStringLookup()).replace(source);
    }
    
    public StringSubstitutor() {
        this(null, StringSubstitutor.DEFAULT_PREFIX, StringSubstitutor.DEFAULT_SUFFIX, '$');
    }
    
    public <V> StringSubstitutor(final Map<String, V> valueMap) {
        this(StringLookupFactory.INSTANCE.mapStringLookup(valueMap), StringSubstitutor.DEFAULT_PREFIX, StringSubstitutor.DEFAULT_SUFFIX, '$');
    }
    
    public <V> StringSubstitutor(final Map<String, V> valueMap, final String prefix, final String suffix) {
        this(StringLookupFactory.INSTANCE.mapStringLookup(valueMap), prefix, suffix, '$');
    }
    
    public <V> StringSubstitutor(final Map<String, V> valueMap, final String prefix, final String suffix, final char escape) {
        this(StringLookupFactory.INSTANCE.mapStringLookup(valueMap), prefix, suffix, escape);
    }
    
    public <V> StringSubstitutor(final Map<String, V> valueMap, final String prefix, final String suffix, final char escape, final String valueDelimiter) {
        this(StringLookupFactory.INSTANCE.mapStringLookup(valueMap), prefix, suffix, escape, valueDelimiter);
    }
    
    public StringSubstitutor(final StringLookup variableResolver) {
        this(variableResolver, StringSubstitutor.DEFAULT_PREFIX, StringSubstitutor.DEFAULT_SUFFIX, '$');
    }
    
    public StringSubstitutor(final StringLookup variableResolver, final String prefix, final String suffix, final char escape) {
        this.setVariableResolver(variableResolver);
        this.setVariablePrefix(prefix);
        this.setVariableSuffix(suffix);
        this.setEscapeChar(escape);
        this.setValueDelimiterMatcher(StringSubstitutor.DEFAULT_VALUE_DELIMITER);
    }
    
    public StringSubstitutor(final StringLookup variableResolver, final String prefix, final String suffix, final char escape, final String valueDelimiter) {
        this.setVariableResolver(variableResolver);
        this.setVariablePrefix(prefix);
        this.setVariableSuffix(suffix);
        this.setEscapeChar(escape);
        this.setValueDelimiter(valueDelimiter);
    }
    
    public StringSubstitutor(final StringLookup variableResolver, final StringMatcher prefixMatcher, final StringMatcher suffixMatcher, final char escape) {
        this(variableResolver, prefixMatcher, suffixMatcher, escape, StringSubstitutor.DEFAULT_VALUE_DELIMITER);
    }
    
    public StringSubstitutor(final StringLookup variableResolver, final StringMatcher prefixMatcher, final StringMatcher suffixMatcher, final char escape, final StringMatcher valueDelimiterMatcher) {
        this.setVariableResolver(variableResolver);
        this.setVariablePrefixMatcher(prefixMatcher);
        this.setVariableSuffixMatcher(suffixMatcher);
        this.setEscapeChar(escape);
        this.setValueDelimiterMatcher(valueDelimiterMatcher);
    }
    
    private void checkCyclicSubstitution(final String varName, final List<String> priorVariables) {
        if (!priorVariables.contains(varName)) {
            return;
        }
        final TextStringBuilder buf = new TextStringBuilder(256);
        buf.append("Infinite loop in property interpolation of ");
        buf.append(priorVariables.remove(0));
        buf.append(": ");
        buf.appendWithSeparators(priorVariables, "->");
        throw new IllegalStateException(buf.toString());
    }
    
    public char getEscapeChar() {
        return this.escapeChar;
    }
    
    public StringLookup getStringLookup() {
        return this.variableResolver;
    }
    
    public StringMatcher getValueDelimiterMatcher() {
        return this.valueDelimiterMatcher;
    }
    
    public StringMatcher getVariablePrefixMatcher() {
        return this.prefixMatcher;
    }
    
    public StringMatcher getVariableSuffixMatcher() {
        return this.suffixMatcher;
    }
    
    public boolean isDisableSubstitutionInValues() {
        return this.disableSubstitutionInValues;
    }
    
    public boolean isEnableSubstitutionInVariables() {
        return this.enableSubstitutionInVariables;
    }
    
    public boolean isEnableUndefinedVariableException() {
        return this.enableUndefinedVariableException;
    }
    
    public boolean isPreserveEscapes() {
        return this.preserveEscapes;
    }
    
    public String replace(final char[] source) {
        if (source == null) {
            return null;
        }
        final TextStringBuilder buf = new TextStringBuilder(source.length).append(source);
        this.substitute(buf, 0, source.length);
        return buf.toString();
    }
    
    public String replace(final char[] source, final int offset, final int length) {
        if (source == null) {
            return null;
        }
        final TextStringBuilder buf = new TextStringBuilder(length).append(source, offset, length);
        this.substitute(buf, 0, length);
        return buf.toString();
    }
    
    public String replace(final CharSequence source) {
        if (source == null) {
            return null;
        }
        return this.replace(source, 0, source.length());
    }
    
    public String replace(final CharSequence source, final int offset, final int length) {
        if (source == null) {
            return null;
        }
        final TextStringBuilder buf = new TextStringBuilder(length).append(source.toString(), offset, length);
        this.substitute(buf, 0, length);
        return buf.toString();
    }
    
    public String replace(final Object source) {
        if (source == null) {
            return null;
        }
        final TextStringBuilder buf = new TextStringBuilder().append(source);
        this.substitute(buf, 0, buf.length());
        return buf.toString();
    }
    
    public String replace(final String source) {
        if (source == null) {
            return null;
        }
        final TextStringBuilder buf = new TextStringBuilder(source);
        if (!this.substitute(buf, 0, source.length())) {
            return source;
        }
        return buf.toString();
    }
    
    public String replace(final String source, final int offset, final int length) {
        if (source == null) {
            return null;
        }
        final TextStringBuilder buf = new TextStringBuilder(length).append(source, offset, length);
        if (!this.substitute(buf, 0, length)) {
            return source.substring(offset, offset + length);
        }
        return buf.toString();
    }
    
    public String replace(final StringBuffer source) {
        if (source == null) {
            return null;
        }
        final TextStringBuilder buf = new TextStringBuilder(source.length()).append(source);
        this.substitute(buf, 0, buf.length());
        return buf.toString();
    }
    
    public String replace(final StringBuffer source, final int offset, final int length) {
        if (source == null) {
            return null;
        }
        final TextStringBuilder buf = new TextStringBuilder(length).append(source, offset, length);
        this.substitute(buf, 0, length);
        return buf.toString();
    }
    
    public String replace(final TextStringBuilder source) {
        if (source == null) {
            return null;
        }
        final TextStringBuilder buf = new TextStringBuilder(source.length()).append(source);
        this.substitute(buf, 0, buf.length());
        return buf.toString();
    }
    
    public String replace(final TextStringBuilder source, final int offset, final int length) {
        if (source == null) {
            return null;
        }
        final TextStringBuilder buf = new TextStringBuilder(length).append(source, offset, length);
        this.substitute(buf, 0, length);
        return buf.toString();
    }
    
    public boolean replaceIn(final StringBuffer source) {
        return source != null && this.replaceIn(source, 0, source.length());
    }
    
    public boolean replaceIn(final StringBuffer source, final int offset, final int length) {
        if (source == null) {
            return false;
        }
        final TextStringBuilder buf = new TextStringBuilder(length).append(source, offset, length);
        if (!this.substitute(buf, 0, length)) {
            return false;
        }
        source.replace(offset, offset + length, buf.toString());
        return true;
    }
    
    public boolean replaceIn(final StringBuilder source) {
        return source != null && this.replaceIn(source, 0, source.length());
    }
    
    public boolean replaceIn(final StringBuilder source, final int offset, final int length) {
        if (source == null) {
            return false;
        }
        final TextStringBuilder buf = new TextStringBuilder(length).append(source, offset, length);
        if (!this.substitute(buf, 0, length)) {
            return false;
        }
        source.replace(offset, offset + length, buf.toString());
        return true;
    }
    
    public boolean replaceIn(final TextStringBuilder source) {
        return source != null && this.substitute(source, 0, source.length());
    }
    
    public boolean replaceIn(final TextStringBuilder source, final int offset, final int length) {
        return source != null && this.substitute(source, offset, length);
    }
    
    protected String resolveVariable(final String variableName, final TextStringBuilder buf, final int startPos, final int endPos) {
        final StringLookup resolver = this.getStringLookup();
        if (resolver == null) {
            return null;
        }
        return resolver.lookup(variableName);
    }
    
    public StringSubstitutor setDisableSubstitutionInValues(final boolean disableSubstitutionInValues) {
        this.disableSubstitutionInValues = disableSubstitutionInValues;
        return this;
    }
    
    public StringSubstitutor setEnableSubstitutionInVariables(final boolean enableSubstitutionInVariables) {
        this.enableSubstitutionInVariables = enableSubstitutionInVariables;
        return this;
    }
    
    public StringSubstitutor setEnableUndefinedVariableException(final boolean failOnUndefinedVariable) {
        this.enableUndefinedVariableException = failOnUndefinedVariable;
        return this;
    }
    
    public StringSubstitutor setEscapeChar(final char escapeCharacter) {
        this.escapeChar = escapeCharacter;
        return this;
    }
    
    public StringSubstitutor setPreserveEscapes(final boolean preserveEscapes) {
        this.preserveEscapes = preserveEscapes;
        return this;
    }
    
    public StringSubstitutor setValueDelimiter(final char valueDelimiter) {
        return this.setValueDelimiterMatcher(StringMatcherFactory.INSTANCE.charMatcher(valueDelimiter));
    }
    
    public StringSubstitutor setValueDelimiter(final String valueDelimiter) {
        if (valueDelimiter == null || valueDelimiter.length() == 0) {
            this.setValueDelimiterMatcher(null);
            return this;
        }
        return this.setValueDelimiterMatcher(StringMatcherFactory.INSTANCE.stringMatcher(valueDelimiter));
    }
    
    public StringSubstitutor setValueDelimiterMatcher(final StringMatcher valueDelimiterMatcher) {
        this.valueDelimiterMatcher = valueDelimiterMatcher;
        return this;
    }
    
    public StringSubstitutor setVariablePrefix(final char prefix) {
        return this.setVariablePrefixMatcher(StringMatcherFactory.INSTANCE.charMatcher(prefix));
    }
    
    public StringSubstitutor setVariablePrefix(final String prefix) {
        Validate.isTrue(prefix != null, "Variable prefix must not be null!", new Object[0]);
        return this.setVariablePrefixMatcher(StringMatcherFactory.INSTANCE.stringMatcher(prefix));
    }
    
    public StringSubstitutor setVariablePrefixMatcher(final StringMatcher prefixMatcher) {
        Validate.isTrue(prefixMatcher != null, "Variable prefix matcher must not be null!", new Object[0]);
        this.prefixMatcher = prefixMatcher;
        return this;
    }
    
    public StringSubstitutor setVariableResolver(final StringLookup variableResolver) {
        this.variableResolver = variableResolver;
        return this;
    }
    
    public StringSubstitutor setVariableSuffix(final char suffix) {
        return this.setVariableSuffixMatcher(StringMatcherFactory.INSTANCE.charMatcher(suffix));
    }
    
    public StringSubstitutor setVariableSuffix(final String suffix) {
        Validate.isTrue(suffix != null, "Variable suffix must not be null!", new Object[0]);
        return this.setVariableSuffixMatcher(StringMatcherFactory.INSTANCE.stringMatcher(suffix));
    }
    
    public StringSubstitutor setVariableSuffixMatcher(final StringMatcher suffixMatcher) {
        Validate.isTrue(suffixMatcher != null, "Variable suffix matcher must not be null!", new Object[0]);
        this.suffixMatcher = suffixMatcher;
        return this;
    }
    
    protected boolean substitute(final TextStringBuilder buf, final int offset, final int length) {
        return this.substitute(buf, offset, length, null) > 0;
    }
    
    private int substitute(final TextStringBuilder buf, final int offset, final int length, List<String> priorVariables) {
        final StringMatcher pfxMatcher = this.getVariablePrefixMatcher();
        final StringMatcher suffMatcher = this.getVariableSuffixMatcher();
        final char escape = this.getEscapeChar();
        final StringMatcher valueDelimMatcher = this.getValueDelimiterMatcher();
        final boolean substitutionInVariablesEnabled = this.isEnableSubstitutionInVariables();
        final boolean substitutionInValuesDisabled = this.isDisableSubstitutionInValues();
        final boolean undefinedVariableException = this.isEnableUndefinedVariableException();
        final boolean top = priorVariables == null;
        boolean altered = false;
        int lengthChange = 0;
        char[] chars = buf.buffer;
        int bufEnd = offset + length;
        int pos = offset;
        while (pos < bufEnd) {
            final int startMatchLen = pfxMatcher.isMatch(chars, pos, offset, bufEnd);
            if (startMatchLen == 0) {
                ++pos;
            }
            else if (pos > offset && chars[pos - 1] == escape) {
                if (this.preserveEscapes) {
                    ++pos;
                }
                else {
                    buf.deleteCharAt(pos - 1);
                    chars = buf.buffer;
                    --lengthChange;
                    altered = true;
                    --bufEnd;
                }
            }
            else {
                final int startPos = pos;
                pos += startMatchLen;
                int endMatchLen = 0;
                int nestedVarCount = 0;
                while (pos < bufEnd) {
                    if (substitutionInVariablesEnabled && pfxMatcher.isMatch(chars, pos, offset, bufEnd) != 0) {
                        endMatchLen = pfxMatcher.isMatch(chars, pos, offset, bufEnd);
                        ++nestedVarCount;
                        pos += endMatchLen;
                    }
                    else {
                        endMatchLen = suffMatcher.isMatch(chars, pos, offset, bufEnd);
                        if (endMatchLen == 0) {
                            ++pos;
                        }
                        else {
                            if (nestedVarCount == 0) {
                                String varNameExpr = new String(chars, startPos + startMatchLen, pos - startPos - startMatchLen);
                                if (substitutionInVariablesEnabled) {
                                    final TextStringBuilder bufName = new TextStringBuilder(varNameExpr);
                                    this.substitute(bufName, 0, bufName.length());
                                    varNameExpr = bufName.toString();
                                }
                                final int endPos;
                                pos = (endPos = pos + endMatchLen);
                                String varName = varNameExpr;
                                String varDefaultValue = null;
                                if (valueDelimMatcher != null) {
                                    final char[] varNameExprChars = varNameExpr.toCharArray();
                                    int valueDelimiterMatchLen = 0;
                                    for (int i = 0; i < varNameExprChars.length; ++i) {
                                        if (!substitutionInVariablesEnabled && pfxMatcher.isMatch(varNameExprChars, i, i, varNameExprChars.length) != 0) {
                                            break;
                                        }
                                        if (valueDelimMatcher.isMatch(varNameExprChars, i, 0, varNameExprChars.length) != 0) {
                                            valueDelimiterMatchLen = valueDelimMatcher.isMatch(varNameExprChars, i, 0, varNameExprChars.length);
                                            varName = varNameExpr.substring(0, i);
                                            varDefaultValue = varNameExpr.substring(i + valueDelimiterMatchLen);
                                            break;
                                        }
                                    }
                                }
                                if (priorVariables == null) {
                                    priorVariables = new ArrayList<String>();
                                    priorVariables.add(new String(chars, offset, length));
                                }
                                this.checkCyclicSubstitution(varName, priorVariables);
                                priorVariables.add(varName);
                                String varValue = this.resolveVariable(varName, buf, startPos, endPos);
                                if (varValue == null) {
                                    varValue = varDefaultValue;
                                }
                                if (varValue != null) {
                                    final int varLen = varValue.length();
                                    buf.replace(startPos, endPos, varValue);
                                    altered = true;
                                    int change = 0;
                                    if (!substitutionInValuesDisabled) {
                                        change = this.substitute(buf, startPos, varLen, priorVariables);
                                    }
                                    change = change + varLen - (endPos - startPos);
                                    pos += change;
                                    bufEnd += change;
                                    lengthChange += change;
                                    chars = buf.buffer;
                                }
                                else if (undefinedVariableException) {
                                    throw new IllegalArgumentException(String.format("Cannot resolve variable '%s' (enableSubstitutionInVariables=%s).", varName, this.enableSubstitutionInVariables));
                                }
                                priorVariables.remove(priorVariables.size() - 1);
                                break;
                            }
                            --nestedVarCount;
                            pos += endMatchLen;
                        }
                    }
                }
            }
        }
        if (top) {
            return altered ? 1 : 0;
        }
        return lengthChange;
    }
    
    static {
        DEFAULT_PREFIX = StringMatcherFactory.INSTANCE.stringMatcher("${");
        DEFAULT_SUFFIX = StringMatcherFactory.INSTANCE.stringMatcher("}");
        DEFAULT_VALUE_DELIMITER = StringMatcherFactory.INSTANCE.stringMatcher(":-");
    }
}
