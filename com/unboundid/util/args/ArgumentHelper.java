package com.unboundid.util.args;

import java.util.List;
import com.unboundid.util.Debug;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ArgumentHelper
{
    private ArgumentHelper() {
    }
    
    @InternalUseOnly
    public static void reset(final ArgumentParser parser) {
        parser.reset();
    }
    
    @InternalUseOnly
    public static void incrementOccurrences(final Argument argument) throws ArgumentException {
        argument.incrementOccurrences();
    }
    
    @InternalUseOnly
    public static void incrementOccurrencesSuppressException(final Argument argument) {
        try {
            argument.incrementOccurrences();
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
    }
    
    @InternalUseOnly
    public static void setSelectedSubCommand(final ArgumentParser parser, final SubCommand subcommand) {
        parser.setSelectedSubCommand(subcommand);
    }
    
    @InternalUseOnly
    public static void addValue(final Argument argument, final String valueString) throws ArgumentException {
        argument.addValue(valueString);
        incrementOccurrencesSuppressException(argument);
    }
    
    @InternalUseOnly
    public static void addValueSuppressException(final Argument argument, final String valueString) {
        try {
            argument.addValue(valueString);
            incrementOccurrencesSuppressException(argument);
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
    }
    
    @InternalUseOnly
    public static boolean hasDefaultValue(final Argument argument) {
        return argument.hasDefaultValue();
    }
    
    @InternalUseOnly
    public static void reset(final Argument argument) {
        argument.reset();
    }
    
    @InternalUseOnly
    public static void addToCommandLine(final Argument argument, final List<String> argStrings) {
        argument.addToCommandLine(argStrings);
    }
    
    @InternalUseOnly
    public static void resetTrailingArguments(final ArgumentParser parser) {
        parser.resetTrailingArguments();
    }
    
    @InternalUseOnly
    public static void addTrailingArgument(final ArgumentParser parser, final String value) throws ArgumentException {
        parser.addTrailingArgument(value);
    }
}
