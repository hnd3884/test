package com.unboundid.util.args;

import java.util.Iterator;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import com.unboundid.ldap.sdk.DN;
import java.util.ArrayList;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class DNArgument extends Argument
{
    private static final long serialVersionUID = 7956577383262400167L;
    private final ArrayList<DN> values;
    private final List<ArgumentValueValidator> validators;
    private final List<DN> defaultValues;
    
    public DNArgument(final Character shortIdentifier, final String longIdentifier, final String description) throws ArgumentException {
        this(shortIdentifier, longIdentifier, false, 1, null, description);
    }
    
    public DNArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, maxOccurrences, valuePlaceholder, description, (List<DN>)null);
    }
    
    public DNArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description, final DN defaultValue) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, maxOccurrences, valuePlaceholder, description, (defaultValue == null) ? null : Collections.singletonList(defaultValue));
    }
    
    public DNArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description, final List<DN> defaultValues) throws ArgumentException {
        super(shortIdentifier, longIdentifier, isRequired, maxOccurrences, (valuePlaceholder == null) ? ArgsMessages.INFO_PLACEHOLDER_DN.get() : valuePlaceholder, description);
        if (defaultValues == null || defaultValues.isEmpty()) {
            this.defaultValues = null;
        }
        else {
            this.defaultValues = Collections.unmodifiableList((List<? extends DN>)defaultValues);
        }
        this.values = new ArrayList<DN>(5);
        this.validators = new ArrayList<ArgumentValueValidator>(5);
    }
    
    private DNArgument(final DNArgument source) {
        super(source);
        this.defaultValues = source.defaultValues;
        this.values = new ArrayList<DN>(5);
        this.validators = new ArrayList<ArgumentValueValidator>(source.validators);
    }
    
    public List<DN> getDefaultValues() {
        return this.defaultValues;
    }
    
    public void addValueValidator(final ArgumentValueValidator validator) {
        this.validators.add(validator);
    }
    
    @Override
    protected void addValue(final String valueString) throws ArgumentException {
        DN parsedDN;
        try {
            parsedDN = new DN(valueString);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new ArgumentException(ArgsMessages.ERR_DN_VALUE_NOT_DN.get(valueString, this.getIdentifierString(), le.getMessage()), le);
        }
        if (this.values.size() >= this.getMaxOccurrences()) {
            throw new ArgumentException(ArgsMessages.ERR_ARG_MAX_OCCURRENCES_EXCEEDED.get(this.getIdentifierString()));
        }
        for (final ArgumentValueValidator v : this.validators) {
            v.validateArgumentValue(this, valueString);
        }
        this.values.add(parsedDN);
    }
    
    public DN getValue() {
        if (!this.values.isEmpty()) {
            return this.values.get(0);
        }
        if (this.defaultValues == null || this.defaultValues.isEmpty()) {
            return null;
        }
        return this.defaultValues.get(0);
    }
    
    public List<DN> getValues() {
        if (this.values.isEmpty() && this.defaultValues != null) {
            return this.defaultValues;
        }
        return Collections.unmodifiableList((List<? extends DN>)this.values);
    }
    
    public String getStringValue() {
        final DN valueDN = this.getValue();
        if (valueDN == null) {
            return null;
        }
        return valueDN.toString();
    }
    
    @Override
    public List<String> getValueStringRepresentations(final boolean useDefault) {
        if (!this.values.isEmpty()) {
            final ArrayList<String> valueStrings = new ArrayList<String>(this.values.size());
            for (final DN dn : this.values) {
                valueStrings.add(dn.toString());
            }
            return Collections.unmodifiableList((List<? extends String>)valueStrings);
        }
        if (useDefault && this.defaultValues != null) {
            final ArrayList<String> valueStrings = new ArrayList<String>(this.defaultValues.size());
            for (final DN dn : this.defaultValues) {
                valueStrings.add(dn.toString());
            }
            return Collections.unmodifiableList((List<? extends String>)valueStrings);
        }
        return Collections.emptyList();
    }
    
    @Override
    protected boolean hasDefaultValue() {
        return this.defaultValues != null && !this.defaultValues.isEmpty();
    }
    
    @Override
    public String getDataTypeName() {
        return ArgsMessages.INFO_DN_TYPE_NAME.get();
    }
    
    @Override
    public String getValueConstraints() {
        return ArgsMessages.INFO_DN_CONSTRAINTS.get();
    }
    
    @Override
    protected void reset() {
        super.reset();
        this.values.clear();
    }
    
    @Override
    public DNArgument getCleanCopy() {
        return new DNArgument(this);
    }
    
    @Override
    protected void addToCommandLine(final List<String> argStrings) {
        if (this.values != null) {
            for (final DN dn : this.values) {
                argStrings.add(this.getIdentifierString());
                if (this.isSensitive()) {
                    argStrings.add("***REDACTED***");
                }
                else {
                    argStrings.add(String.valueOf(dn));
                }
            }
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("DNArgument(");
        this.appendBasicToStringInfo(buffer);
        if (this.defaultValues != null && !this.defaultValues.isEmpty()) {
            if (this.defaultValues.size() == 1) {
                buffer.append(", defaultValue='");
                buffer.append(this.defaultValues.get(0).toString());
            }
            else {
                buffer.append(", defaultValues={");
                final Iterator<DN> iterator = this.defaultValues.iterator();
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
