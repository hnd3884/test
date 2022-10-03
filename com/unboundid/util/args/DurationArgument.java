package com.unboundid.util.args;

import com.unboundid.util.StaticUtils;
import java.util.Iterator;
import com.unboundid.util.Debug;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.LDAPSDKUsageException;
import java.util.concurrent.TimeUnit;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class DurationArgument extends Argument
{
    private static final long serialVersionUID = -8824262632728709264L;
    private final List<ArgumentValueValidator> validators;
    private final Long defaultValueNanos;
    private final long maxValueNanos;
    private final long minValueNanos;
    private Long valueNanos;
    private final String lowerBoundStr;
    private final String upperBoundStr;
    
    public DurationArgument(final Character shortIdentifier, final String longIdentifier, final String description) throws ArgumentException {
        this(shortIdentifier, longIdentifier, false, null, description);
    }
    
    public DurationArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final String valuePlaceholder, final String description) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, valuePlaceholder, description, null, null, null, null, null, null);
    }
    
    public DurationArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final String valuePlaceholder, final String description, final Long defaultValue, final TimeUnit defaultValueUnit, final Long lowerBound, final TimeUnit lowerBoundUnit, final Long upperBound, final TimeUnit upperBoundUnit) throws ArgumentException {
        super(shortIdentifier, longIdentifier, isRequired, 1, (valuePlaceholder == null) ? ArgsMessages.INFO_PLACEHOLDER_DURATION.get() : valuePlaceholder, description);
        if (defaultValue == null) {
            this.defaultValueNanos = null;
        }
        else {
            if (defaultValueUnit == null) {
                throw new ArgumentException(ArgsMessages.ERR_DURATION_DEFAULT_REQUIRES_UNIT.get(this.getIdentifierString()));
            }
            this.defaultValueNanos = defaultValueUnit.toNanos(defaultValue);
        }
        if (lowerBound == null) {
            this.minValueNanos = 0L;
            this.lowerBoundStr = "0ns";
        }
        else {
            if (lowerBoundUnit == null) {
                throw new ArgumentException(ArgsMessages.ERR_DURATION_LOWER_REQUIRES_UNIT.get(this.getIdentifierString()));
            }
            this.minValueNanos = lowerBoundUnit.toNanos(lowerBound);
            switch (lowerBoundUnit) {
                case NANOSECONDS: {
                    this.lowerBoundStr = this.minValueNanos + "ns";
                    break;
                }
                case MICROSECONDS: {
                    this.lowerBoundStr = lowerBound + "us";
                    break;
                }
                case MILLISECONDS: {
                    this.lowerBoundStr = lowerBound + "ms";
                    break;
                }
                case SECONDS: {
                    this.lowerBoundStr = lowerBound + "s";
                    break;
                }
                case MINUTES: {
                    this.lowerBoundStr = lowerBound + "m";
                    break;
                }
                case HOURS: {
                    this.lowerBoundStr = lowerBound + "h";
                    break;
                }
                case DAYS: {
                    this.lowerBoundStr = lowerBound + "d";
                    break;
                }
                default: {
                    throw new LDAPSDKUsageException(ArgsMessages.ERR_DURATION_UNSUPPORTED_LOWER_BOUND_UNIT.get(lowerBoundUnit.name()));
                }
            }
        }
        if (upperBound == null) {
            this.maxValueNanos = Long.MAX_VALUE;
            this.upperBoundStr = "9223372036854775807ns";
        }
        else {
            if (upperBoundUnit == null) {
                throw new ArgumentException(ArgsMessages.ERR_DURATION_UPPER_REQUIRES_UNIT.get(this.getIdentifierString()));
            }
            this.maxValueNanos = upperBoundUnit.toNanos(upperBound);
            switch (upperBoundUnit) {
                case NANOSECONDS: {
                    this.upperBoundStr = this.minValueNanos + "ns";
                    break;
                }
                case MICROSECONDS: {
                    this.upperBoundStr = upperBound + "us";
                    break;
                }
                case MILLISECONDS: {
                    this.upperBoundStr = upperBound + "ms";
                    break;
                }
                case SECONDS: {
                    this.upperBoundStr = upperBound + "s";
                    break;
                }
                case MINUTES: {
                    this.upperBoundStr = upperBound + "m";
                    break;
                }
                case HOURS: {
                    this.upperBoundStr = upperBound + "h";
                    break;
                }
                case DAYS: {
                    this.upperBoundStr = upperBound + "d";
                    break;
                }
                default: {
                    throw new LDAPSDKUsageException(ArgsMessages.ERR_DURATION_UNSUPPORTED_UPPER_BOUND_UNIT.get(upperBoundUnit.name()));
                }
            }
        }
        if (this.minValueNanos > this.maxValueNanos) {
            throw new ArgumentException(ArgsMessages.ERR_DURATION_LOWER_GT_UPPER.get(this.getIdentifierString(), this.lowerBoundStr, this.upperBoundStr));
        }
        this.valueNanos = null;
        this.validators = new ArrayList<ArgumentValueValidator>(5);
    }
    
    private DurationArgument(final DurationArgument source) {
        super(source);
        this.defaultValueNanos = source.defaultValueNanos;
        this.maxValueNanos = source.maxValueNanos;
        this.minValueNanos = source.minValueNanos;
        this.lowerBoundStr = source.lowerBoundStr;
        this.upperBoundStr = source.upperBoundStr;
        this.validators = new ArrayList<ArgumentValueValidator>(source.validators);
        this.valueNanos = null;
    }
    
    public long getLowerBound(final TimeUnit unit) {
        return unit.convert(this.minValueNanos, TimeUnit.NANOSECONDS);
    }
    
    public long getUpperBound(final TimeUnit unit) {
        return unit.convert(this.maxValueNanos, TimeUnit.NANOSECONDS);
    }
    
    @Override
    public List<String> getValueStringRepresentations(final boolean useDefault) {
        long v;
        if (this.valueNanos != null) {
            v = this.valueNanos;
        }
        else {
            if (!useDefault || this.defaultValueNanos == null) {
                return Collections.emptyList();
            }
            v = this.defaultValueNanos;
        }
        return Collections.singletonList(nanosToDuration(v));
    }
    
    @Override
    protected boolean hasDefaultValue() {
        return this.defaultValueNanos != null;
    }
    
    public Long getDefaultValue(final TimeUnit unit) {
        if (this.defaultValueNanos == null) {
            return null;
        }
        return unit.convert(this.defaultValueNanos, TimeUnit.NANOSECONDS);
    }
    
    public Long getValue(final TimeUnit unit) {
        if (this.valueNanos != null) {
            return unit.convert(this.valueNanos, TimeUnit.NANOSECONDS);
        }
        if (this.defaultValueNanos == null) {
            return null;
        }
        return unit.convert(this.defaultValueNanos, TimeUnit.NANOSECONDS);
    }
    
    public void addValueValidator(final ArgumentValueValidator validator) {
        this.validators.add(validator);
    }
    
    @Override
    protected void addValue(final String valueString) throws ArgumentException {
        if (this.valueNanos != null) {
            throw new ArgumentException(ArgsMessages.ERR_ARG_MAX_OCCURRENCES_EXCEEDED.get(this.getIdentifierString()));
        }
        long proposedValueNanos;
        try {
            proposedValueNanos = parseDuration(valueString, TimeUnit.NANOSECONDS);
        }
        catch (final ArgumentException ae) {
            Debug.debugException(ae);
            throw new ArgumentException(ArgsMessages.ERR_DURATION_MALFORMED_VALUE.get(valueString, this.getIdentifierString(), ae.getMessage()), ae);
        }
        if (proposedValueNanos < this.minValueNanos) {
            throw new ArgumentException(ArgsMessages.ERR_DURATION_BELOW_LOWER_BOUND.get(this.getIdentifierString(), this.lowerBoundStr));
        }
        if (proposedValueNanos > this.maxValueNanos) {
            throw new ArgumentException(ArgsMessages.ERR_DURATION_ABOVE_UPPER_BOUND.get(this.getIdentifierString(), this.upperBoundStr));
        }
        for (final ArgumentValueValidator v : this.validators) {
            v.validateArgumentValue(this, valueString);
        }
        this.valueNanos = proposedValueNanos;
    }
    
    public static long parseDuration(final String durationString, final TimeUnit timeUnit) throws ArgumentException {
        final String lowerStr = StaticUtils.toLowerCase(durationString);
        if (lowerStr.isEmpty()) {
            throw new ArgumentException(ArgsMessages.ERR_DURATION_EMPTY_VALUE.get());
        }
        boolean digitFound = false;
        boolean nonDigitFound = false;
        int nonDigitPos = -1;
        int i = 0;
        while (i < lowerStr.length()) {
            final char c = lowerStr.charAt(i);
            if (Character.isDigit(c)) {
                digitFound = true;
                ++i;
            }
            else {
                nonDigitFound = true;
                nonDigitPos = i;
                if (!digitFound) {
                    throw new ArgumentException(ArgsMessages.ERR_DURATION_NO_DIGIT.get());
                }
                break;
            }
        }
        if (!nonDigitFound) {
            throw new ArgumentException(ArgsMessages.ERR_DURATION_NO_UNIT.get());
        }
        long integerPortion = Long.parseLong(lowerStr.substring(0, nonDigitPos));
        final String unitStr = lowerStr.substring(nonDigitPos).trim();
        TimeUnit unitFromString;
        if (unitStr.equals("ns") || unitStr.equals("nano") || unitStr.equals("nanos") || unitStr.equals("nanosecond") || unitStr.equals("nanoseconds")) {
            unitFromString = TimeUnit.NANOSECONDS;
        }
        else if (unitStr.equals("us") || unitStr.equals("micro") || unitStr.equals("micros") || unitStr.equals("microsecond") || unitStr.equals("microseconds")) {
            unitFromString = TimeUnit.MICROSECONDS;
        }
        else if (unitStr.equals("ms") || unitStr.equals("milli") || unitStr.equals("millis") || unitStr.equals("millisecond") || unitStr.equals("milliseconds")) {
            unitFromString = TimeUnit.MILLISECONDS;
        }
        else if (unitStr.equals("s") || unitStr.equals("sec") || unitStr.equals("secs") || unitStr.equals("second") || unitStr.equals("seconds")) {
            unitFromString = TimeUnit.SECONDS;
        }
        else if (unitStr.equals("m") || unitStr.equals("min") || unitStr.equals("mins") || unitStr.equals("minute") || unitStr.equals("minutes")) {
            integerPortion *= 60L;
            unitFromString = TimeUnit.SECONDS;
        }
        else if (unitStr.equals("h") || unitStr.equals("hr") || unitStr.equals("hrs") || unitStr.equals("hour") || unitStr.equals("hours")) {
            integerPortion *= 3600L;
            unitFromString = TimeUnit.SECONDS;
        }
        else if (unitStr.equals("d") || unitStr.equals("day") || unitStr.equals("days")) {
            integerPortion *= 86400L;
            unitFromString = TimeUnit.SECONDS;
        }
        else {
            if (!unitStr.equals("w") && !unitStr.equals("week") && !unitStr.equals("weeks")) {
                throw new ArgumentException(ArgsMessages.ERR_DURATION_UNRECOGNIZED_UNIT.get(unitStr));
            }
            integerPortion *= 604800L;
            unitFromString = TimeUnit.SECONDS;
        }
        return timeUnit.convert(integerPortion, unitFromString);
    }
    
    @Override
    public String getDataTypeName() {
        return ArgsMessages.INFO_DURATION_TYPE_NAME.get();
    }
    
    @Override
    public String getValueConstraints() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(ArgsMessages.INFO_DURATION_CONSTRAINTS_FORMAT.get());
        if (this.lowerBoundStr != null) {
            if (this.upperBoundStr == null) {
                buffer.append("  ");
                buffer.append(ArgsMessages.INFO_DURATION_CONSTRAINTS_LOWER_BOUND.get(this.lowerBoundStr));
            }
            else {
                buffer.append("  ");
                buffer.append(ArgsMessages.INFO_DURATION_CONSTRAINTS_LOWER_AND_UPPER_BOUND.get(this.lowerBoundStr, this.upperBoundStr));
            }
        }
        else if (this.upperBoundStr != null) {
            buffer.append("  ");
            buffer.append(ArgsMessages.INFO_DURATION_CONSTRAINTS_UPPER_BOUND.get(this.upperBoundStr));
        }
        return buffer.toString();
    }
    
    @Override
    protected void reset() {
        super.reset();
        this.valueNanos = null;
    }
    
    @Override
    public DurationArgument getCleanCopy() {
        return new DurationArgument(this);
    }
    
    public static String nanosToDuration(final long nanos) {
        if (nanos == 0L) {
            return "0 nanoseconds";
        }
        if (nanos == 604800000000000L) {
            return "1 week";
        }
        if (nanos % 604800000000000L == 0L) {
            return nanos / 604800000000000L + " weeks";
        }
        if (nanos == 86400000000000L) {
            return "1 day";
        }
        if (nanos % 86400000000000L == 0L) {
            return nanos / 86400000000000L + " days";
        }
        if (nanos == 3600000000000L) {
            return "1 hour";
        }
        if (nanos % 3600000000000L == 0L) {
            return nanos / 3600000000000L + " hours";
        }
        if (nanos == 60000000000L) {
            return "1 minute";
        }
        if (nanos % 60000000000L == 0L) {
            return nanos / 60000000000L + " minutes";
        }
        if (nanos == 1000000000L) {
            return "1 second";
        }
        if (nanos % 1000000000L == 0L) {
            return nanos / 1000000000L + " seconds";
        }
        if (nanos == 1000000L) {
            return "1 millisecond";
        }
        if (nanos % 1000000L == 0L) {
            return nanos / 1000000L + " milliseconds";
        }
        if (nanos == 1000L) {
            return "1 microsecond";
        }
        if (nanos % 1000L == 0L) {
            return nanos / 1000L + " microseconds";
        }
        if (nanos == 1L) {
            return "1 nanosecond";
        }
        return nanos + " nanoseconds";
    }
    
    @Override
    protected void addToCommandLine(final List<String> argStrings) {
        if (this.valueNanos != null) {
            argStrings.add(this.getIdentifierString());
            if (this.isSensitive()) {
                argStrings.add("***REDACTED***");
            }
            else {
                argStrings.add(nanosToDuration(this.valueNanos));
            }
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("DurationArgument(");
        this.appendBasicToStringInfo(buffer);
        if (this.lowerBoundStr != null) {
            buffer.append(", lowerBound='");
            buffer.append(this.lowerBoundStr);
            buffer.append('\'');
        }
        if (this.upperBoundStr != null) {
            buffer.append(", upperBound='");
            buffer.append(this.upperBoundStr);
            buffer.append('\'');
        }
        if (this.defaultValueNanos != null) {
            buffer.append(", defaultValueNanos=");
            buffer.append(this.defaultValueNanos);
        }
        buffer.append(')');
    }
}
