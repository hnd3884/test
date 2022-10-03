package com.unboundid.util.args;

import java.util.List;
import com.unboundid.util.OID;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class OIDArgumentValueValidator extends ArgumentValueValidator implements Serializable
{
    private static final long serialVersionUID = 2195078137238476902L;
    private final boolean isStrict;
    
    public OIDArgumentValueValidator() {
        this(true);
    }
    
    public OIDArgumentValueValidator(final boolean isStrict) {
        this.isStrict = isStrict;
    }
    
    public boolean isStrict() {
        return this.isStrict;
    }
    
    @Override
    public void validateArgumentValue(final Argument argument, final String valueString) throws ArgumentException {
        if (valueString.isEmpty()) {
            throw new ArgumentException(ArgsMessages.ERR_OID_VALIDATOR_EMPTY.get(valueString, argument.getIdentifierString()));
        }
        if (valueString.startsWith(".") || valueString.endsWith(".")) {
            throw new ArgumentException(ArgsMessages.ERR_OID_VALIDATOR_STARTS_OR_ENDS_WITH_PERIOD.get(valueString, argument.getIdentifierString()));
        }
        if (valueString.contains("..")) {
            throw new ArgumentException(ArgsMessages.ERR_OID_VALIDATOR_CONSECUTIVE_PERIODS.get(valueString, argument.getIdentifierString()));
        }
        final OID oid = new OID(valueString);
        if (!oid.isValidNumericOID()) {
            throw new ArgumentException(ArgsMessages.ERR_OID_VALIDATOR_ILLEGAL_CHARACTER.get(valueString, argument.getIdentifierString()));
        }
        if (!this.isStrict) {
            return;
        }
        final List<Integer> components = oid.getComponents();
        if (components.size() < 2) {
            throw new ArgumentException(ArgsMessages.ERR_OID_VALIDATOR_NOT_ENOUGH_COMPONENTS.get(valueString, argument.getIdentifierString()));
        }
        final int firstComponent = components.get(0);
        final int secondComponent = components.get(1);
        switch (firstComponent) {
            case 0:
            case 1: {
                if (secondComponent > 39) {
                    throw new ArgumentException(ArgsMessages.ERR_OID_VALIDATOR_ILLEGAL_SECOND_COMPONENT.get(valueString, argument.getIdentifierString()));
                }
                break;
            }
            case 2: {
                break;
            }
            default: {
                throw new ArgumentException(ArgsMessages.ERR_OID_VALIDATOR_ILLEGAL_FIRST_COMPONENT.get(valueString, argument.getIdentifierString()));
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("OIDArgumentValueValidator(isStrict=");
        buffer.append(this.isStrict);
        buffer.append(')');
    }
}
