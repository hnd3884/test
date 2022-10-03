package com.unboundid.util.args;

import java.util.regex.Matcher;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashSet;
import com.unboundid.util.StaticUtils;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class StringArgument extends Argument
{
    private static final long serialVersionUID = 1088032496970585118L;
    private final ArrayList<String> values;
    private final List<ArgumentValueValidator> validators;
    private final List<String> defaultValues;
    private volatile Pattern valueRegex;
    private final Set<String> allowedValues;
    private volatile String valueRegexExplanation;
    
    public StringArgument(final Character shortIdentifier, final String longIdentifier, final String description) throws ArgumentException {
        this(shortIdentifier, longIdentifier, false, 1, null, description);
    }
    
    public StringArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, maxOccurrences, valuePlaceholder, description, null, (List<String>)null);
    }
    
    public StringArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description, final Set<String> allowedValues) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, maxOccurrences, valuePlaceholder, description, allowedValues, (List<String>)null);
    }
    
    public StringArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description, final String defaultValue) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, maxOccurrences, valuePlaceholder, description, null, (defaultValue == null) ? null : Collections.singletonList(defaultValue));
    }
    
    public StringArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description, final List<String> defaultValues) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, maxOccurrences, valuePlaceholder, description, null, defaultValues);
    }
    
    public StringArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description, final Set<String> allowedValues, final String defaultValue) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, maxOccurrences, valuePlaceholder, description, allowedValues, (defaultValue == null) ? null : Collections.singletonList(defaultValue));
    }
    
    public StringArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description, final Set<String> allowedValues, final List<String> defaultValues) throws ArgumentException {
        super(shortIdentifier, longIdentifier, isRequired, maxOccurrences, (valuePlaceholder == null) ? ArgsMessages.INFO_PLACEHOLDER_VALUE.get() : valuePlaceholder, description);
        if (allowedValues == null || allowedValues.isEmpty()) {
            this.allowedValues = null;
        }
        else {
            final HashSet<String> lowerValues = new HashSet<String>(StaticUtils.computeMapCapacity(allowedValues.size()));
            for (final String s : allowedValues) {
                lowerValues.add(StaticUtils.toLowerCase(s));
            }
            this.allowedValues = Collections.unmodifiableSet((Set<? extends String>)lowerValues);
        }
        if (defaultValues == null || defaultValues.isEmpty()) {
            this.defaultValues = null;
        }
        else {
            this.defaultValues = Collections.unmodifiableList((List<? extends String>)defaultValues);
        }
        if (this.allowedValues != null && this.defaultValues != null) {
            for (final String s2 : this.defaultValues) {
                final String lowerDefault = StaticUtils.toLowerCase(s2);
                if (!this.allowedValues.contains(lowerDefault)) {
                    throw new ArgumentException(ArgsMessages.ERR_ARG_DEFAULT_VALUE_NOT_ALLOWED.get(s2, this.getIdentifierString()));
                }
            }
        }
        this.values = new ArrayList<String>(5);
        this.validators = new ArrayList<ArgumentValueValidator>(5);
        this.valueRegex = null;
        this.valueRegexExplanation = null;
    }
    
    private StringArgument(final StringArgument source) {
        super(source);
        this.allowedValues = source.allowedValues;
        this.defaultValues = source.defaultValues;
        this.valueRegex = source.valueRegex;
        this.valueRegexExplanation = source.valueRegexExplanation;
        this.values = new ArrayList<String>(5);
        this.validators = new ArrayList<ArgumentValueValidator>(source.validators);
    }
    
    public Set<String> getAllowedValues() {
        return this.allowedValues;
    }
    
    public List<String> getDefaultValues() {
        return this.defaultValues;
    }
    
    public Pattern getValueRegex() {
        return this.valueRegex;
    }
    
    public String getValueRegexExplanation() {
        return this.valueRegexExplanation;
    }
    
    public void setValueRegex(final Pattern valueRegex, final String explanation) {
        this.valueRegex = valueRegex;
        this.valueRegexExplanation = explanation;
    }
    
    public void addValueValidator(final ArgumentValueValidator validator) {
        this.validators.add(validator);
    }
    
    @Override
    protected void addValue(final String valueString) throws ArgumentException {
        final String lowerValue = StaticUtils.toLowerCase(valueString);
        if (this.allowedValues != null && !this.allowedValues.contains(lowerValue)) {
            final StringBuilder allowedValuesBuffer = new StringBuilder();
            for (final String allowedValue : this.allowedValues) {
                if (allowedValuesBuffer.length() > 0) {
                    allowedValuesBuffer.append(", ");
                }
                allowedValuesBuffer.append('\'');
                allowedValuesBuffer.append(allowedValue);
                allowedValuesBuffer.append('\'');
            }
            throw new ArgumentException(ArgsMessages.ERR_ARG_VALUE_NOT_ALLOWED.get(valueString, this.getIdentifierString(), allowedValuesBuffer.toString()));
        }
        if (this.values.size() >= this.getMaxOccurrences()) {
            throw new ArgumentException(ArgsMessages.ERR_ARG_MAX_OCCURRENCES_EXCEEDED.get(this.getIdentifierString()));
        }
        if (this.valueRegex != null) {
            final Matcher matcher = this.valueRegex.matcher(valueString);
            if (!matcher.matches()) {
                final String pattern = this.valueRegex.pattern();
                if (this.valueRegexExplanation == null) {
                    throw new ArgumentException(ArgsMessages.ERR_ARG_VALUE_DOES_NOT_MATCH_PATTERN_WITHOUT_EXPLANATION.get(valueString, this.getIdentifierString(), pattern));
                }
                throw new ArgumentException(ArgsMessages.ERR_ARG_VALUE_DOES_NOT_MATCH_PATTERN_WITH_EXPLANATION.get(valueString, this.getIdentifierString(), pattern, this.valueRegexExplanation));
            }
        }
        for (final ArgumentValueValidator v : this.validators) {
            v.validateArgumentValue(this, valueString);
        }
        this.values.add(valueString);
    }
    
    public String getValue() {
        if (!this.values.isEmpty()) {
            return this.values.get(0);
        }
        if (this.defaultValues == null || this.defaultValues.isEmpty()) {
            return null;
        }
        return this.defaultValues.get(0);
    }
    
    public List<String> getValues() {
        if (this.values.isEmpty() && this.defaultValues != null) {
            return this.defaultValues;
        }
        return Collections.unmodifiableList((List<? extends String>)this.values);
    }
    
    @Override
    public List<String> getValueStringRepresentations(final boolean useDefault) {
        if (!this.values.isEmpty()) {
            return Collections.unmodifiableList((List<? extends String>)this.values);
        }
        if (useDefault && this.defaultValues != null) {
            return Collections.unmodifiableList((List<? extends String>)this.defaultValues);
        }
        return Collections.emptyList();
    }
    
    @Override
    protected boolean hasDefaultValue() {
        return this.defaultValues != null && !this.defaultValues.isEmpty();
    }
    
    @Override
    public String getDataTypeName() {
        return ArgsMessages.INFO_STRING_TYPE_NAME.get();
    }
    
    @Override
    public String getValueConstraints() {
        StringBuilder buffer = null;
        if (this.valueRegex != null) {
            buffer = new StringBuilder();
            final String pattern = this.valueRegex.pattern();
            if (this.valueRegexExplanation == null || this.valueRegexExplanation.length() == 0) {
                buffer.append(ArgsMessages.INFO_STRING_CONSTRAINTS_REGEX_WITHOUT_EXPLANATION.get(pattern));
            }
            else {
                buffer.append(ArgsMessages.INFO_STRING_CONSTRAINTS_REGEX_WITHOUT_EXPLANATION.get(pattern, this.valueRegexExplanation));
            }
        }
        if (this.allowedValues != null && !this.allowedValues.isEmpty()) {
            if (buffer == null) {
                buffer = new StringBuilder();
            }
            else {
                buffer.append("  ");
            }
            buffer.append(ArgsMessages.INFO_STRING_CONSTRAINTS_ALLOWED_VALUE.get());
            buffer.append("  ");
            final Iterator<String> iterator = this.allowedValues.iterator();
            while (iterator.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator.next());
                buffer.append('\'');
                if (iterator.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append('.');
        }
        if (buffer == null) {
            return null;
        }
        return buffer.toString();
    }
    
    @Override
    protected void reset() {
        super.reset();
        this.values.clear();
    }
    
    @Override
    public StringArgument getCleanCopy() {
        return new StringArgument(this);
    }
    
    @Override
    protected void addToCommandLine(final List<String> argStrings) {
        if (this.values != null) {
            for (final String s : this.values) {
                argStrings.add(this.getIdentifierString());
                if (this.isSensitive()) {
                    argStrings.add("***REDACTED***");
                }
                else {
                    argStrings.add(s);
                }
            }
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("StringArgument(");
        this.appendBasicToStringInfo(buffer);
        if (this.allowedValues != null && !this.allowedValues.isEmpty()) {
            buffer.append(", allowedValues={");
            final Iterator<String> iterator = this.allowedValues.iterator();
            while (iterator.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator.next());
                buffer.append('\'');
                if (iterator.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append('}');
        }
        if (this.valueRegex != null) {
            buffer.append(", valueRegex='");
            buffer.append(this.valueRegex.pattern());
            buffer.append('\'');
            if (this.valueRegexExplanation != null) {
                buffer.append(", valueRegexExplanation='");
                buffer.append(this.valueRegexExplanation);
                buffer.append('\'');
            }
        }
        if (this.defaultValues != null && !this.defaultValues.isEmpty()) {
            if (this.defaultValues.size() == 1) {
                buffer.append(", defaultValue='");
                buffer.append(this.defaultValues.get(0));
            }
            else {
                buffer.append(", defaultValues={");
                final Iterator<String> iterator = this.defaultValues.iterator();
                while (iterator.hasNext()) {
                    buffer.append('\'');
                    buffer.append(iterator.next());
                    buffer.append('\'');
                    if (iterator.hasNext()) {
                        buffer.append(", ");
                    }
                }
                buffer.append('}');
            }
        }
        buffer.append(')');
    }
}
