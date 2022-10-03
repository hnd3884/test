package com.unboundid.util.args;

import com.unboundid.util.StaticUtils;
import java.util.Collections;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class BooleanValueArgument extends Argument
{
    private static final long serialVersionUID = -3903872574065550222L;
    private final Boolean defaultValue;
    private Boolean value;
    
    public BooleanValueArgument(final Character shortIdentifier, final String longIdentifier, final String description) throws ArgumentException {
        this(shortIdentifier, longIdentifier, false, null, description);
    }
    
    public BooleanValueArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final String valuePlaceholder, final String description) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, valuePlaceholder, description, null);
    }
    
    public BooleanValueArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final String valuePlaceholder, final String description, final Boolean defaultValue) throws ArgumentException {
        super(shortIdentifier, longIdentifier, isRequired, 1, (valuePlaceholder == null) ? ArgsMessages.INFO_PLACEHOLDER_TRUE_FALSE.get() : valuePlaceholder, description);
        this.defaultValue = defaultValue;
        this.value = null;
    }
    
    private BooleanValueArgument(final BooleanValueArgument source) {
        super(source);
        this.defaultValue = source.defaultValue;
        this.value = null;
    }
    
    @Override
    public List<String> getValueStringRepresentations(final boolean useDefault) {
        if (this.value != null) {
            return Collections.singletonList(this.value.toString());
        }
        if (useDefault && this.defaultValue != null) {
            return Collections.singletonList(this.defaultValue.toString());
        }
        return Collections.emptyList();
    }
    
    @Override
    protected boolean hasDefaultValue() {
        return this.defaultValue != null;
    }
    
    public Boolean getDefaultValue() {
        return this.defaultValue;
    }
    
    public Boolean getValue() {
        if (this.value == null) {
            return this.defaultValue;
        }
        return this.value;
    }
    
    @Override
    protected void addValue(final String valueString) throws ArgumentException {
        if (this.value != null) {
            throw new ArgumentException(ArgsMessages.ERR_ARG_MAX_OCCURRENCES_EXCEEDED.get(this.getIdentifierString()));
        }
        final String lowerStr = StaticUtils.toLowerCase(valueString);
        if (lowerStr.equals("true") || lowerStr.equals("t") || lowerStr.equals("yes") || lowerStr.equals("y") || lowerStr.equals("on") || lowerStr.equals("1")) {
            this.value = Boolean.TRUE;
        }
        else {
            if (!lowerStr.equals("false") && !lowerStr.equals("f") && !lowerStr.equals("no") && !lowerStr.equals("n") && !lowerStr.equals("off") && !lowerStr.equals("0")) {
                throw new ArgumentException(ArgsMessages.ERR_ARG_VALUE_NOT_ALLOWED.get(valueString, this.getIdentifierString(), "'true', 'false'"));
            }
            this.value = Boolean.FALSE;
        }
    }
    
    @Override
    public String getDataTypeName() {
        return ArgsMessages.INFO_BOOLEAN_VALUE_TYPE_NAME.get();
    }
    
    @Override
    public String getValueConstraints() {
        return ArgsMessages.INFO_BOOLEAN_VALUE_CONSTRAINTS.get();
    }
    
    @Override
    protected void reset() {
        super.reset();
        this.value = null;
    }
    
    @Override
    public BooleanValueArgument getCleanCopy() {
        return new BooleanValueArgument(this);
    }
    
    @Override
    protected void addToCommandLine(final List<String> argStrings) {
        if (this.value != null) {
            argStrings.add(this.getIdentifierString());
            if (this.isSensitive()) {
                argStrings.add("***REDACTED***");
            }
            else {
                argStrings.add(String.valueOf(this.value));
            }
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("BooleanValueArgument(");
        this.appendBasicToStringInfo(buffer);
        if (this.defaultValue != null) {
            buffer.append(", defaultValue=");
            buffer.append(this.defaultValue);
        }
        buffer.append(')');
    }
}
