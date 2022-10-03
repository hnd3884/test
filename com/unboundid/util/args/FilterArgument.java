package com.unboundid.util.args;

import java.util.Iterator;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import com.unboundid.ldap.sdk.Filter;
import java.util.ArrayList;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class FilterArgument extends Argument
{
    private static final long serialVersionUID = -1889200072476038957L;
    private final ArrayList<Filter> values;
    private final List<ArgumentValueValidator> validators;
    private final List<Filter> defaultValues;
    
    public FilterArgument(final Character shortIdentifier, final String longIdentifier, final String description) throws ArgumentException {
        this(shortIdentifier, longIdentifier, false, 1, null, description);
    }
    
    public FilterArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, maxOccurrences, valuePlaceholder, description, (List<Filter>)null);
    }
    
    public FilterArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description, final Filter defaultValue) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, maxOccurrences, valuePlaceholder, description, (defaultValue == null) ? null : Collections.singletonList(defaultValue));
    }
    
    public FilterArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description, final List<Filter> defaultValues) throws ArgumentException {
        super(shortIdentifier, longIdentifier, isRequired, maxOccurrences, (valuePlaceholder == null) ? ArgsMessages.INFO_PLACEHOLDER_FILTER.get() : valuePlaceholder, description);
        if (defaultValues == null || defaultValues.isEmpty()) {
            this.defaultValues = null;
        }
        else {
            this.defaultValues = Collections.unmodifiableList((List<? extends Filter>)defaultValues);
        }
        this.values = new ArrayList<Filter>(5);
        this.validators = new ArrayList<ArgumentValueValidator>(5);
    }
    
    private FilterArgument(final FilterArgument source) {
        super(source);
        this.defaultValues = source.defaultValues;
        this.validators = new ArrayList<ArgumentValueValidator>(source.validators);
        this.values = new ArrayList<Filter>(5);
    }
    
    public List<Filter> getDefaultValues() {
        return this.defaultValues;
    }
    
    public void addValueValidator(final ArgumentValueValidator validator) {
        this.validators.add(validator);
    }
    
    @Override
    protected void addValue(final String valueString) throws ArgumentException {
        Filter filter;
        try {
            filter = Filter.create(valueString);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new ArgumentException(ArgsMessages.ERR_FILTER_VALUE_NOT_FILTER.get(valueString, this.getIdentifierString(), le.getMessage()), le);
        }
        if (this.values.size() >= this.getMaxOccurrences()) {
            throw new ArgumentException(ArgsMessages.ERR_ARG_MAX_OCCURRENCES_EXCEEDED.get(this.getIdentifierString()));
        }
        for (final ArgumentValueValidator v : this.validators) {
            v.validateArgumentValue(this, valueString);
        }
        this.values.add(filter);
    }
    
    public Filter getValue() {
        if (!this.values.isEmpty()) {
            return this.values.get(0);
        }
        if (this.defaultValues == null || this.defaultValues.isEmpty()) {
            return null;
        }
        return this.defaultValues.get(0);
    }
    
    public List<Filter> getValues() {
        if (this.values.isEmpty() && this.defaultValues != null) {
            return this.defaultValues;
        }
        return Collections.unmodifiableList((List<? extends Filter>)this.values);
    }
    
    @Override
    public List<String> getValueStringRepresentations(final boolean useDefault) {
        List<Filter> filters;
        if (this.values.isEmpty()) {
            if (!useDefault) {
                return Collections.emptyList();
            }
            filters = this.defaultValues;
        }
        else {
            filters = this.values;
        }
        if (filters == null || filters.isEmpty()) {
            return Collections.emptyList();
        }
        final ArrayList<String> valueStrings = new ArrayList<String>(filters.size());
        for (final Filter f : filters) {
            valueStrings.add(f.toString());
        }
        return Collections.unmodifiableList((List<? extends String>)valueStrings);
    }
    
    @Override
    protected boolean hasDefaultValue() {
        return this.defaultValues != null && !this.defaultValues.isEmpty();
    }
    
    @Override
    public String getDataTypeName() {
        return ArgsMessages.INFO_FILTER_TYPE_NAME.get();
    }
    
    @Override
    public String getValueConstraints() {
        return ArgsMessages.INFO_FILTER_CONSTRAINTS.get();
    }
    
    @Override
    protected void reset() {
        super.reset();
        this.values.clear();
    }
    
    @Override
    public FilterArgument getCleanCopy() {
        return new FilterArgument(this);
    }
    
    @Override
    protected void addToCommandLine(final List<String> argStrings) {
        if (this.values != null) {
            for (final Filter f : this.values) {
                argStrings.add(this.getIdentifierString());
                if (this.isSensitive()) {
                    argStrings.add("***REDACTED***");
                }
                else {
                    argStrings.add(f.toString());
                }
            }
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("FilterArgument(");
        this.appendBasicToStringInfo(buffer);
        if (this.defaultValues != null && !this.defaultValues.isEmpty()) {
            if (this.defaultValues.size() == 1) {
                buffer.append(", defaultValue='");
                buffer.append(this.defaultValues.get(0).toString());
            }
            else {
                buffer.append(", defaultValues={");
                final Iterator<Filter> iterator = this.defaultValues.iterator();
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
