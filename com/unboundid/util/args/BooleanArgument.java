package com.unboundid.util.args;

import java.util.Collections;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class BooleanArgument extends Argument
{
    private static final long serialVersionUID = -3366354214909534696L;
    
    public BooleanArgument(final Character shortIdentifier, final String longIdentifier, final String description) throws ArgumentException {
        super(shortIdentifier, longIdentifier, false, 1, null, description);
    }
    
    public BooleanArgument(final Character shortIdentifier, final String longIdentifier, final int maxOccurrences, final String description) throws ArgumentException {
        super(shortIdentifier, longIdentifier, false, maxOccurrences, null, description);
    }
    
    private BooleanArgument(final BooleanArgument source) {
        super(source);
    }
    
    @Override
    protected void addValue(final String valueString) throws ArgumentException {
        throw new ArgumentException(ArgsMessages.ERR_BOOLEAN_VALUES_NOT_ALLOWED.get(this.getIdentifierString()));
    }
    
    @Override
    public List<String> getValueStringRepresentations(final boolean useDefault) {
        return Collections.singletonList(String.valueOf(this.isPresent()));
    }
    
    @Override
    protected boolean hasDefaultValue() {
        return false;
    }
    
    @Override
    public String getDataTypeName() {
        return ArgsMessages.INFO_BOOLEAN_TYPE_NAME.get();
    }
    
    @Override
    public String getValueConstraints() {
        return ArgsMessages.INFO_BOOLEAN_CONSTRAINTS.get();
    }
    
    @Override
    public BooleanArgument getCleanCopy() {
        return new BooleanArgument(this);
    }
    
    @Override
    protected void addToCommandLine(final List<String> argStrings) {
        for (int i = 0; i < this.getNumOccurrences(); ++i) {
            argStrings.add(this.getIdentifierString());
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("BooleanArgument(");
        this.appendBasicToStringInfo(buffer);
        buffer.append(')');
    }
}
