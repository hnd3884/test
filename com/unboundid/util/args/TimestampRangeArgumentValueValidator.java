package com.unboundid.util.args;

import com.unboundid.util.StaticUtils;
import java.util.Date;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class TimestampRangeArgumentValueValidator extends ArgumentValueValidator implements Serializable
{
    private static final long serialVersionUID = 7248120077176469324L;
    private final Date mostRecentAllowedDate;
    private final Date oldestAllowedDate;
    
    public TimestampRangeArgumentValueValidator(final Date oldestAllowedDate, final Date mostRecentAllowedDate) {
        if (oldestAllowedDate == null) {
            this.oldestAllowedDate = null;
        }
        else {
            this.oldestAllowedDate = oldestAllowedDate;
        }
        if (mostRecentAllowedDate == null) {
            this.mostRecentAllowedDate = null;
        }
        else {
            this.mostRecentAllowedDate = mostRecentAllowedDate;
        }
    }
    
    public Date getOldestAllowedDate() {
        return this.oldestAllowedDate;
    }
    
    public Date getMostRecentAllowedDate() {
        return this.mostRecentAllowedDate;
    }
    
    @Override
    public void validateArgumentValue(final Argument argument, final String valueString) throws ArgumentException {
        Date parsedDate;
        try {
            parsedDate = TimestampArgument.parseTimestamp(valueString);
        }
        catch (final Exception e) {
            throw new ArgumentException(ArgsMessages.ERR_TIMESTAMP_VALUE_NOT_TIMESTAMP.get(valueString, argument.getIdentifierString()), e);
        }
        final long parsedTime = parsedDate.getTime();
        if (this.oldestAllowedDate != null && parsedTime < this.oldestAllowedDate.getTime()) {
            throw new ArgumentException(ArgsMessages.ERR_TIMESTAMP_RANGE_VALIDATOR_TOO_OLD.get(valueString, argument.getIdentifierString(), StaticUtils.encodeGeneralizedTime(this.oldestAllowedDate)));
        }
        if (this.mostRecentAllowedDate != null && parsedTime > this.mostRecentAllowedDate.getTime()) {
            throw new ArgumentException(ArgsMessages.ERR_TIMESTAMP_RANGE_VALIDATOR_TOO_NEW.get(valueString, argument.getIdentifierString(), StaticUtils.encodeGeneralizedTime(this.mostRecentAllowedDate)));
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("TimestampRangeArgumentValueValidator(");
        if (this.oldestAllowedDate != null) {
            buffer.append("oldestAllowedDate='");
            buffer.append(StaticUtils.encodeGeneralizedTime(this.oldestAllowedDate));
            buffer.append('\'');
            if (this.mostRecentAllowedDate != null) {
                buffer.append(", mostRecentAllowedDate='");
                buffer.append(StaticUtils.encodeGeneralizedTime(this.mostRecentAllowedDate));
                buffer.append('\'');
            }
        }
        else if (this.mostRecentAllowedDate != null) {
            buffer.append("mostRecentAllowedDate='");
            buffer.append(StaticUtils.encodeGeneralizedTime(this.mostRecentAllowedDate));
            buffer.append('\'');
        }
        buffer.append(')');
    }
}
