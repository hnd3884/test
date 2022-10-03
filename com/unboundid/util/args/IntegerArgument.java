package com.unboundid.util.args;

import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class IntegerArgument extends Argument
{
    private static final long serialVersionUID = 3364985217337213643L;
    private final ArrayList<Integer> values;
    private final int lowerBound;
    private final int upperBound;
    private final List<ArgumentValueValidator> validators;
    private final List<Integer> defaultValues;
    
    public IntegerArgument(final Character shortIdentifier, final String longIdentifier, final String description) throws ArgumentException {
        this(shortIdentifier, longIdentifier, false, 1, null, description);
    }
    
    public IntegerArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, maxOccurrences, valuePlaceholder, description, Integer.MIN_VALUE, Integer.MAX_VALUE, (List<Integer>)null);
    }
    
    public IntegerArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description, final int lowerBound, final int upperBound) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, maxOccurrences, valuePlaceholder, description, lowerBound, upperBound, (List<Integer>)null);
    }
    
    public IntegerArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description, final Integer defaultValue) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, maxOccurrences, valuePlaceholder, description, Integer.MIN_VALUE, Integer.MAX_VALUE, (defaultValue == null) ? null : Collections.singletonList(defaultValue));
    }
    
    public IntegerArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description, final List<Integer> defaultValues) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, maxOccurrences, valuePlaceholder, description, Integer.MIN_VALUE, Integer.MAX_VALUE, defaultValues);
    }
    
    public IntegerArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description, final int lowerBound, final int upperBound, final Integer defaultValue) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, maxOccurrences, valuePlaceholder, description, lowerBound, upperBound, (defaultValue == null) ? null : Collections.singletonList(defaultValue));
    }
    
    public IntegerArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description, final int lowerBound, final int upperBound, final List<Integer> defaultValues) throws ArgumentException {
        super(shortIdentifier, longIdentifier, isRequired, maxOccurrences, (valuePlaceholder == null) ? ArgsMessages.INFO_PLACEHOLDER_VALUE.get() : valuePlaceholder, description);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        if (defaultValues == null || defaultValues.isEmpty()) {
            this.defaultValues = null;
        }
        else {
            this.defaultValues = Collections.unmodifiableList((List<? extends Integer>)defaultValues);
        }
        this.values = new ArrayList<Integer>(5);
        this.validators = new ArrayList<ArgumentValueValidator>(5);
    }
    
    private IntegerArgument(final IntegerArgument source) {
        super(source);
        this.lowerBound = source.lowerBound;
        this.upperBound = source.upperBound;
        this.defaultValues = source.defaultValues;
        this.validators = new ArrayList<ArgumentValueValidator>(source.validators);
        this.values = new ArrayList<Integer>(5);
    }
    
    public int getLowerBound() {
        return this.lowerBound;
    }
    
    public int getUpperBound() {
        return this.upperBound;
    }
    
    public List<Integer> getDefaultValues() {
        return this.defaultValues;
    }
    
    public void addValueValidator(final ArgumentValueValidator validator) {
        this.validators.add(validator);
    }
    
    @Override
    protected void addValue(final String valueString) throws ArgumentException {
        int intValue;
        try {
            intValue = Integer.parseInt(valueString);
        }
        catch (final Exception e) {
            throw new ArgumentException(ArgsMessages.ERR_INTEGER_VALUE_NOT_INT.get(valueString, this.getIdentifierString()), e);
        }
        if (intValue < this.lowerBound) {
            throw new ArgumentException(ArgsMessages.ERR_INTEGER_VALUE_BELOW_LOWER_BOUND.get(intValue, this.getIdentifierString(), this.lowerBound));
        }
        if (intValue > this.upperBound) {
            throw new ArgumentException(ArgsMessages.ERR_INTEGER_VALUE_ABOVE_UPPER_BOUND.get(intValue, this.getIdentifierString(), this.upperBound));
        }
        if (this.values.size() >= this.getMaxOccurrences()) {
            throw new ArgumentException(ArgsMessages.ERR_ARG_MAX_OCCURRENCES_EXCEEDED.get(this.getIdentifierString()));
        }
        for (final ArgumentValueValidator v : this.validators) {
            v.validateArgumentValue(this, valueString);
        }
        this.values.add(intValue);
    }
    
    public Integer getValue() {
        if (!this.values.isEmpty()) {
            return this.values.get(0);
        }
        if (this.defaultValues == null || this.defaultValues.isEmpty()) {
            return null;
        }
        return this.defaultValues.get(0);
    }
    
    public List<Integer> getValues() {
        if (this.values.isEmpty() && this.defaultValues != null) {
            return this.defaultValues;
        }
        return Collections.unmodifiableList((List<? extends Integer>)this.values);
    }
    
    @Override
    public List<String> getValueStringRepresentations(final boolean useDefault) {
        List<Integer> intValues;
        if (this.values.isEmpty()) {
            if (!useDefault) {
                return Collections.emptyList();
            }
            intValues = this.defaultValues;
        }
        else {
            intValues = this.values;
        }
        if (intValues == null || intValues.isEmpty()) {
            return Collections.emptyList();
        }
        final ArrayList<String> valueStrings = new ArrayList<String>(intValues.size());
        for (final Integer i : intValues) {
            valueStrings.add(i.toString());
        }
        return Collections.unmodifiableList((List<? extends String>)valueStrings);
    }
    
    @Override
    protected boolean hasDefaultValue() {
        return this.defaultValues != null && !this.defaultValues.isEmpty();
    }
    
    @Override
    public String getDataTypeName() {
        return ArgsMessages.INFO_INTEGER_TYPE_NAME.get();
    }
    
    @Override
    public String getValueConstraints() {
        return ArgsMessages.INFO_INTEGER_CONSTRAINTS_LOWER_AND_UPPER_BOUND.get(this.lowerBound, this.upperBound);
    }
    
    @Override
    protected void reset() {
        super.reset();
        this.values.clear();
    }
    
    @Override
    public IntegerArgument getCleanCopy() {
        return new IntegerArgument(this);
    }
    
    @Override
    protected void addToCommandLine(final List<String> argStrings) {
        if (this.values != null) {
            for (final Integer i : this.values) {
                argStrings.add(this.getIdentifierString());
                if (this.isSensitive()) {
                    argStrings.add("***REDACTED");
                }
                else {
                    argStrings.add(i.toString());
                }
            }
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("IntegerArgument(");
        this.appendBasicToStringInfo(buffer);
        buffer.append(", lowerBound=");
        buffer.append(this.lowerBound);
        buffer.append(", upperBound=");
        buffer.append(this.upperBound);
        if (this.defaultValues != null && !this.defaultValues.isEmpty()) {
            if (this.defaultValues.size() == 1) {
                buffer.append(", defaultValue='");
                buffer.append(this.defaultValues.get(0).toString());
            }
            else {
                buffer.append(", defaultValues={");
                final Iterator<Integer> iterator = this.defaultValues.iterator();
                while (iterator.hasNext()) {
                    buffer.append('\'');
                    buffer.append(iterator.next().toString());
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
