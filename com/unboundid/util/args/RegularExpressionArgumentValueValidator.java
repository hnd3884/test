package com.unboundid.util.args;

import com.unboundid.util.Debug;
import java.util.regex.Pattern;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class RegularExpressionArgumentValueValidator extends ArgumentValueValidator implements Serializable
{
    private static final long serialVersionUID = -6676584334684453380L;
    
    @Override
    public void validateArgumentValue(final Argument argument, final String valueString) throws ArgumentException {
        try {
            Pattern.compile(valueString);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new ArgumentException(ArgsMessages.ERR_REGEX_VALIDATOR_VALUE_NOT_REGEX.get(valueString, argument.getIdentifierString()));
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("RegularExpressionArgumentValueValidator()");
    }
}
