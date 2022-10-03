package com.unboundid.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.ArrayList;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.DurationArgument;
import java.util.concurrent.TimeUnit;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.List;

@ThreadSafety(level = ThreadSafetyLevel.MOSTLY_THREADSAFE)
public final class RateAdjustor extends Thread
{
    public static final char COMMENT_START = '#';
    public static final String END_HEADER_TEXT = "END HEADER";
    public static final String DEFAULT_DURATION_KEY = "default-duration";
    public static final String FORMAT_KEY = "format";
    public static final String FORMAT_VALUE_RATE_DURATION = "rate-and-duration";
    public static final List<String> FORMATS;
    public static final String REPEAT_KEY = "repeat";
    public static final List<String> KEYS;
    private final FixedRateBarrier barrier;
    private final List<ObjectPair<Double, Long>> ratesAndDurations;
    private final boolean repeat;
    private volatile boolean shutDown;
    private final CountDownLatch initialRateSetLatch;
    private final WakeableSleeper sleeper;
    
    public static RateAdjustor newInstance(final FixedRateBarrier barrier, final Integer baseRatePerSecond, final File rates) throws IOException, IllegalArgumentException {
        final Reader reader = new FileReader(rates);
        return new RateAdjustor(barrier, (baseRatePerSecond == null) ? 0L : ((long)baseRatePerSecond), reader);
    }
    
    public static String getVariableRateDataArgumentDescription(final String genArgName) {
        return UtilityMessages.INFO_RATE_ADJUSTOR_VARIABLE_RATE_DATA_ARG_DESCRIPTION.get(genArgName);
    }
    
    public static String getGenerateSampleVariableRateFileDescription(final String dataFileArgName) {
        return UtilityMessages.INFO_RATE_ADJUSTOR_GENERATE_SAMPLE_RATE_FILE_ARG_DESCRIPTION.get(dataFileArgName);
    }
    
    public static void writeSampleVariableRateFile(final File f) throws IOException {
        final PrintWriter w = new PrintWriter(f);
        try {
            w.println("# This is an example variable rate data file.  All blank lines will be ignored.");
            w.println("# All lines starting with the '#' character are considered comments and will");
            w.println("# also be ignored.");
            w.println();
            w.println("# The beginning of the file must be a header containing properties pertaining");
            w.println("# to the variable rate data.  All headers must be in the format 'name=value',");
            w.println("# in which any spaces surrounding the equal sign will be ignored.");
            w.println();
            w.println("# The first header should be the 'format' header, which specifies the format");
            w.println("# for the variable rate data file.  This header is required.  At present, the");
            w.println("# only supported format is 'rate-and-duration', although additional formats may");
            w.println("# be added in the future.");
            w.println("format = rate-and-duration");
            w.println();
            w.println("# The optional 'default-duration' header may be used to specify a duration that");
            w.println("# will be used for any interval that does not explicitly specify a duration.");
            w.println("# The duration must consist of a positive integer value followed by a time");
            w.println("# unit (with zero or more spaces separating the integer value from the unit).");
            w.println("# The supported time units are:");
            w.println("#");
            w.println("# - nanoseconds, nanosecond, nanos, nano, ns");
            w.println("# - microseconds, microseconds, micros, micro, us");
            w.println("# - milliseconds, millisecond, millis, milli, ms");
            w.println("# - seconds, second, secs, sec, s");
            w.println("# - minutes, minute, mins, min, m");
            w.println("# - hours, hour, hrs, hr, h");
            w.println("# - days, day, d");
            w.println("#");
            w.println("# If no 'default-duration' header is present, then every data interval must");
            w.println("# include an explicitly-specified duration.");
            w.println("default-duration = 10 seconds");
            w.println();
            w.println("# The optional 'repeat' header may be used to indicate how the tool should");
            w.println("# behave once the end of the variable rate data definitions has been reached.");
            w.println("# If the 'repeat' header is present with a value of 'true', then the tool will");
            w.println("# operate in an endless loop, returning to the beginning of the variable rate");
            w.println("# definitions once the end has been reached.  If the 'repeat' header is present");
            w.println("# with a value of 'false', or if the 'repeat' header is absent, then the tool");
            w.println("# will exit after it has processed all of the variable rate definitions.");
            w.println("repeat = true");
            w.println();
            w.println("# After all header properties have been specified, the end of the header must");
            w.println("# be signified with a line containing only the text 'END HEADER'.");
            w.println("END HEADER");
            w.println();
            w.println();
            w.println("# After the header is complete, the variable rate definitions should be");
            w.println("# provided.  Each definition should be given on a line by itself, and should");
            w.println("# contain a target rate per second and an optional length of time to maintain");
            w.println("# that rate.");
            w.println("#");
            w.println("# The target rate must always be present in a variable rate definition.  It may");
            w.println("# be either a positive integer value that specifies the absolute target rate");
            w.println("# per second (e.g., a value of '1000' indicates a target rate of 1000");
            w.println("# operations per second), or it may be a floating-point value followed by the");
            w.println("# letter 'x' to indicate that it is a multiplier of the value specified by the");
            w.println("# '--ratePerSecond' argument (e.g., if the '--ratePerSecond' argument is");
            w.println("# present with a value of 1000, then a target rate value of '0.75x' indicates a");
            w.println("# target rate that is 75% of the '--ratePerSecond' value, or 750 operations per");
            w.println("# second).  If the latter format is used, then the '--ratePerSecond' argument");
            w.println("# must be provided.");
            w.println("#");
            w.println("# The duration may optionally be present in a variable rate definition.  If");
            w.println("# present, it must be separated from the target rate by a comma (and there may");
            w.println("# be zero or more spaces on either side of the comma).  The duration must be in");
            w.println("# the same format as specified in the description of the 'default-duration'");
            w.println("# header above (i.e., a positive integer followed by a time unit).  If a");
            w.println("# variable rate definition does not include a duration, then the");
            w.println("# 'default-duration' header must have been specified, and that default duration");
            w.println("# will be used for that variable rate definition.");
            w.println("#");
            w.println("# The following variable rate definitions may be used to stairstep the target");
            w.println("# rate from 1000 operations per second to 10000 operations per second, in");
            w.println("# increments of 1000 operations per second, spending one minute at each level.");
            w.println("# If the 'repeat' header is present with a value of 'true', then the process");
            w.println("# will start back over at 1000 operations per second after completing one");
            w.println("# minute at 10000 operations per second.  Otherwise, the tool will exit after");
            w.println("# completing the 10000 operation-per-second interval.");
            w.println("1000, 1 minute");
            w.println("2000, 1 minute");
            w.println("3000, 1 minute");
            w.println("4000, 1 minute");
            w.println("5000, 1 minute");
            w.println("6000, 1 minute");
            w.println("7000, 1 minute");
            w.println("8000, 1 minute");
            w.println("9000, 1 minute");
            w.println("10000, 1 minute");
            w.println();
            w.println();
            w.println("# Additional sample rate definitions that represent common load patterns are");
            w.println("# provided below.  Each of these patterns makes use of the relative format for");
            w.println("# the target rate and therefore require the '--ratePerSecond' argument to");
            w.println("# specify the target rate.  These sample rate definitions are commented out to");
            w.println("# prevent them from being interpreted by default.");
            w.println();
            w.println();
            w.println("# Example:  Square Rate");
            w.println("#");
            w.println("# This pattern starts with a rate of zero operations per second, then");
            w.println("# immediately jumps to a rate of 100% of the target rate.  A graph of the load");
            w.println("# generated by repeating iterations of this pattern represents a series of");
            w.println("# squares that are alternately missing the top and bottom edges.");
            w.println("#");
            w.println("#0.00x");
            w.println("#1.00x");
            w.println();
            w.println();
            w.println("# Example:  Stairstep Rate");
            w.println("#");
            w.println("# This pattern starts with a rate that is 10% of the target rate, then jumps to");
            w.println("# 20% of the target rate, then 30%, 40%, 50%, etc. until it reaches 100% of the");
            w.println("# target rate.  A graph of the load generated by a single iteration of this");
            w.println("# pattern represents a series of stair steps.");
            w.println("#");
            w.println("#0.1x");
            w.println("#0.2x");
            w.println("#0.3x");
            w.println("#0.4x");
            w.println("#0.5x");
            w.println("#0.6x");
            w.println("#0.7x");
            w.println("#0.8x");
            w.println("#0.9x");
            w.println("#1.0x");
            w.println();
            w.println();
            w.println("# Example:  Sine Rate");
            w.println("#");
            w.println("# This pattern starts with a rate of zero operations per second and increases");
            w.println("# to # 100% of the target rate in a pattern that is gradual at first, rapid in");
            w.println("# the middle, and then gradual again at the end, and then decreases back to");
            w.println("# zero in a mirror image of the ascent.  A graph of the load generated by this");
            w.println("# pattern resembles a sine wave, but starting at the lowest point in the trough");
            w.println("# of the wave (mathematically, represented by the function 'y=sin(x-pi/2)+1').");
            w.println("#");
            w.println("#0.000x");
            w.println("#0.001x");
            w.println("#0.002x");
            w.println("#0.004x");
            w.println("#0.006x");
            w.println("#0.009x");
            w.println("#0.012x");
            w.println("#0.016x");
            w.println("#0.020x");
            w.println("#0.024x");
            w.println("#0.030x");
            w.println("#0.035x");
            w.println("#0.041x");
            w.println("#0.048x");
            w.println("#0.054x");
            w.println("#0.062x");
            w.println("#0.070x");
            w.println("#0.078x");
            w.println("#0.086x");
            w.println("#0.095x");
            w.println("#0.105x");
            w.println("#0.115x");
            w.println("#0.125x");
            w.println("#0.136x");
            w.println("#0.146x");
            w.println("#0.158x");
            w.println("#0.169x");
            w.println("#0.181x");
            w.println("#0.194x");
            w.println("#0.206x");
            w.println("#0.219x");
            w.println("#0.232x");
            w.println("#0.245x");
            w.println("#0.259x");
            w.println("#0.273x");
            w.println("#0.287x");
            w.println("#0.301x");
            w.println("#0.316x");
            w.println("#0.331x");
            w.println("#0.345x");
            w.println("#0.361x");
            w.println("#0.376x");
            w.println("#0.391x");
            w.println("#0.406x");
            w.println("#0.422x");
            w.println("#0.437x");
            w.println("#0.453x");
            w.println("#0.469x");
            w.println("#0.484x");
            w.println("#0.500x");
            w.println("#0.500x");
            w.println("#0.516x");
            w.println("#0.531x");
            w.println("#0.547x");
            w.println("#0.563x");
            w.println("#0.578x");
            w.println("#0.594x");
            w.println("#0.609x");
            w.println("#0.624x");
            w.println("#0.639x");
            w.println("#0.655x");
            w.println("#0.669x");
            w.println("#0.684x");
            w.println("#0.699x");
            w.println("#0.713x");
            w.println("#0.727x");
            w.println("#0.741x");
            w.println("#0.755x");
            w.println("#0.768x");
            w.println("#0.781x");
            w.println("#0.794x");
            w.println("#0.806x");
            w.println("#0.819x");
            w.println("#0.831x");
            w.println("#0.842x");
            w.println("#0.854x");
            w.println("#0.864x");
            w.println("#0.875x");
            w.println("#0.885x");
            w.println("#0.895x");
            w.println("#0.905x");
            w.println("#0.914x");
            w.println("#0.922x");
            w.println("#0.930x");
            w.println("#0.938x");
            w.println("#0.946x");
            w.println("#0.952x");
            w.println("#0.959x");
            w.println("#0.965x");
            w.println("#0.970x");
            w.println("#0.976x");
            w.println("#0.980x");
            w.println("#0.984x");
            w.println("#0.988x");
            w.println("#0.991x");
            w.println("#0.994x");
            w.println("#0.996x");
            w.println("#0.998x");
            w.println("#0.999x");
            w.println("#1.000x");
            w.println("#1.000x");
            w.println("#1.000x");
            w.println("#0.999x");
            w.println("#0.998x");
            w.println("#0.996x");
            w.println("#0.994x");
            w.println("#0.991x");
            w.println("#0.988x");
            w.println("#0.984x");
            w.println("#0.980x");
            w.println("#0.976x");
            w.println("#0.970x");
            w.println("#0.965x");
            w.println("#0.959x");
            w.println("#0.952x");
            w.println("#0.946x");
            w.println("#0.938x");
            w.println("#0.930x");
            w.println("#0.922x");
            w.println("#0.914x");
            w.println("#0.905x");
            w.println("#0.895x");
            w.println("#0.885x");
            w.println("#0.875x");
            w.println("#0.864x");
            w.println("#0.854x");
            w.println("#0.842x");
            w.println("#0.831x");
            w.println("#0.819x");
            w.println("#0.806x");
            w.println("#0.794x");
            w.println("#0.781x");
            w.println("#0.768x");
            w.println("#0.755x");
            w.println("#0.741x");
            w.println("#0.727x");
            w.println("#0.713x");
            w.println("#0.699x");
            w.println("#0.684x");
            w.println("#0.669x");
            w.println("#0.655x");
            w.println("#0.639x");
            w.println("#0.624x");
            w.println("#0.609x");
            w.println("#0.594x");
            w.println("#0.578x");
            w.println("#0.563x");
            w.println("#0.547x");
            w.println("#0.531x");
            w.println("#0.516x");
            w.println("#0.500x");
            w.println("#0.484x");
            w.println("#0.469x");
            w.println("#0.453x");
            w.println("#0.437x");
            w.println("#0.422x");
            w.println("#0.406x");
            w.println("#0.391x");
            w.println("#0.376x");
            w.println("#0.361x");
            w.println("#0.345x");
            w.println("#0.331x");
            w.println("#0.316x");
            w.println("#0.301x");
            w.println("#0.287x");
            w.println("#0.273x");
            w.println("#0.259x");
            w.println("#0.245x");
            w.println("#0.232x");
            w.println("#0.219x");
            w.println("#0.206x");
            w.println("#0.194x");
            w.println("#0.181x");
            w.println("#0.169x");
            w.println("#0.158x");
            w.println("#0.146x");
            w.println("#0.136x");
            w.println("#0.125x");
            w.println("#0.115x");
            w.println("#0.105x");
            w.println("#0.095x");
            w.println("#0.086x");
            w.println("#0.078x");
            w.println("#0.070x");
            w.println("#0.062x");
            w.println("#0.054x");
            w.println("#0.048x");
            w.println("#0.041x");
            w.println("#0.035x");
            w.println("#0.030x");
            w.println("#0.024x");
            w.println("#0.020x");
            w.println("#0.016x");
            w.println("#0.012x");
            w.println("#0.009x");
            w.println("#0.006x");
            w.println("#0.004x");
            w.println("#0.002x");
            w.println("#0.001x");
            w.println("#0.000x");
            w.println();
            w.println();
            w.println("# Example:  Sawtooth Rate");
            w.println("#");
            w.println("# This pattern starts with a rate of zero operations per second and increases");
            w.println("# linearly to 100% of the target rate.  A graph of the load generated by a");
            w.println("# single iteration of this pattern resembles the hypotenuse of a right");
            w.println("# triangle, and a graph of multiple iterations resembles the teeth of a saw");
            w.println("# blade.");
            w.println("#");
            w.println("#0.00x");
            w.println("#0.01x");
            w.println("#0.02x");
            w.println("#0.03x");
            w.println("#0.04x");
            w.println("#0.05x");
            w.println("#0.06x");
            w.println("#0.07x");
            w.println("#0.08x");
            w.println("#0.09x");
            w.println("#0.10x");
            w.println("#0.11x");
            w.println("#0.12x");
            w.println("#0.13x");
            w.println("#0.14x");
            w.println("#0.15x");
            w.println("#0.16x");
            w.println("#0.17x");
            w.println("#0.18x");
            w.println("#0.19x");
            w.println("#0.20x");
            w.println("#0.21x");
            w.println("#0.22x");
            w.println("#0.23x");
            w.println("#0.24x");
            w.println("#0.25x");
            w.println("#0.26x");
            w.println("#0.27x");
            w.println("#0.28x");
            w.println("#0.29x");
            w.println("#0.30x");
            w.println("#0.31x");
            w.println("#0.32x");
            w.println("#0.33x");
            w.println("#0.34x");
            w.println("#0.35x");
            w.println("#0.36x");
            w.println("#0.37x");
            w.println("#0.38x");
            w.println("#0.39x");
            w.println("#0.40x");
            w.println("#0.41x");
            w.println("#0.42x");
            w.println("#0.43x");
            w.println("#0.44x");
            w.println("#0.45x");
            w.println("#0.46x");
            w.println("#0.47x");
            w.println("#0.48x");
            w.println("#0.49x");
            w.println("#0.50x");
            w.println("#0.51x");
            w.println("#0.52x");
            w.println("#0.53x");
            w.println("#0.54x");
            w.println("#0.55x");
            w.println("#0.56x");
            w.println("#0.57x");
            w.println("#0.58x");
            w.println("#0.59x");
            w.println("#0.60x");
            w.println("#0.61x");
            w.println("#0.62x");
            w.println("#0.63x");
            w.println("#0.64x");
            w.println("#0.65x");
            w.println("#0.66x");
            w.println("#0.67x");
            w.println("#0.68x");
            w.println("#0.69x");
            w.println("#0.70x");
            w.println("#0.71x");
            w.println("#0.72x");
            w.println("#0.73x");
            w.println("#0.74x");
            w.println("#0.75x");
            w.println("#0.76x");
            w.println("#0.77x");
            w.println("#0.78x");
            w.println("#0.79x");
            w.println("#0.80x");
            w.println("#0.81x");
            w.println("#0.82x");
            w.println("#0.83x");
            w.println("#0.84x");
            w.println("#0.85x");
            w.println("#0.86x");
            w.println("#0.87x");
            w.println("#0.88x");
            w.println("#0.89x");
            w.println("#0.90x");
            w.println("#0.91x");
            w.println("#0.92x");
            w.println("#0.93x");
            w.println("#0.94x");
            w.println("#0.95x");
            w.println("#0.96x");
            w.println("#0.97x");
            w.println("#0.98x");
            w.println("#0.99x");
            w.println("#1.00x");
            w.println();
            w.println();
            w.println("# Example:  Triangle Rate");
            w.println("#");
            w.println("# This pattern starts with a rate of zero operations per second and increases");
            w.println("# linearly to 100% of the target rate before decreasing linearly back to 0%.");
            w.println("# A graph of the load generated by a single iteration of this tool is like that");
            w.println("# of the sawtooth pattern above followed immediately by its mirror image.");
            w.println("#");
            w.println("#0.00x");
            w.println("#0.01x");
            w.println("#0.02x");
            w.println("#0.03x");
            w.println("#0.04x");
            w.println("#0.05x");
            w.println("#0.06x");
            w.println("#0.07x");
            w.println("#0.08x");
            w.println("#0.09x");
            w.println("#0.10x");
            w.println("#0.11x");
            w.println("#0.12x");
            w.println("#0.13x");
            w.println("#0.14x");
            w.println("#0.15x");
            w.println("#0.16x");
            w.println("#0.17x");
            w.println("#0.18x");
            w.println("#0.19x");
            w.println("#0.20x");
            w.println("#0.21x");
            w.println("#0.22x");
            w.println("#0.23x");
            w.println("#0.24x");
            w.println("#0.25x");
            w.println("#0.26x");
            w.println("#0.27x");
            w.println("#0.28x");
            w.println("#0.29x");
            w.println("#0.30x");
            w.println("#0.31x");
            w.println("#0.32x");
            w.println("#0.33x");
            w.println("#0.34x");
            w.println("#0.35x");
            w.println("#0.36x");
            w.println("#0.37x");
            w.println("#0.38x");
            w.println("#0.39x");
            w.println("#0.40x");
            w.println("#0.41x");
            w.println("#0.42x");
            w.println("#0.43x");
            w.println("#0.44x");
            w.println("#0.45x");
            w.println("#0.46x");
            w.println("#0.47x");
            w.println("#0.48x");
            w.println("#0.49x");
            w.println("#0.50x");
            w.println("#0.51x");
            w.println("#0.52x");
            w.println("#0.53x");
            w.println("#0.54x");
            w.println("#0.55x");
            w.println("#0.56x");
            w.println("#0.57x");
            w.println("#0.58x");
            w.println("#0.59x");
            w.println("#0.60x");
            w.println("#0.61x");
            w.println("#0.62x");
            w.println("#0.63x");
            w.println("#0.64x");
            w.println("#0.65x");
            w.println("#0.66x");
            w.println("#0.67x");
            w.println("#0.68x");
            w.println("#0.69x");
            w.println("#0.70x");
            w.println("#0.71x");
            w.println("#0.72x");
            w.println("#0.73x");
            w.println("#0.74x");
            w.println("#0.75x");
            w.println("#0.76x");
            w.println("#0.77x");
            w.println("#0.78x");
            w.println("#0.79x");
            w.println("#0.80x");
            w.println("#0.81x");
            w.println("#0.82x");
            w.println("#0.83x");
            w.println("#0.84x");
            w.println("#0.85x");
            w.println("#0.86x");
            w.println("#0.87x");
            w.println("#0.88x");
            w.println("#0.89x");
            w.println("#0.90x");
            w.println("#0.91x");
            w.println("#0.92x");
            w.println("#0.93x");
            w.println("#0.94x");
            w.println("#0.95x");
            w.println("#0.96x");
            w.println("#0.97x");
            w.println("#0.98x");
            w.println("#0.99x");
            w.println("#1.00x");
            w.println("#0.99x");
            w.println("#0.98x");
            w.println("#0.97x");
            w.println("#0.96x");
            w.println("#0.95x");
            w.println("#0.94x");
            w.println("#0.93x");
            w.println("#0.92x");
            w.println("#0.91x");
            w.println("#0.90x");
            w.println("#0.89x");
            w.println("#0.88x");
            w.println("#0.87x");
            w.println("#0.86x");
            w.println("#0.85x");
            w.println("#0.84x");
            w.println("#0.83x");
            w.println("#0.82x");
            w.println("#0.81x");
            w.println("#0.80x");
            w.println("#0.79x");
            w.println("#0.78x");
            w.println("#0.77x");
            w.println("#0.76x");
            w.println("#0.75x");
            w.println("#0.74x");
            w.println("#0.73x");
            w.println("#0.72x");
            w.println("#0.71x");
            w.println("#0.70x");
            w.println("#0.69x");
            w.println("#0.68x");
            w.println("#0.67x");
            w.println("#0.66x");
            w.println("#0.65x");
            w.println("#0.64x");
            w.println("#0.63x");
            w.println("#0.62x");
            w.println("#0.61x");
            w.println("#0.60x");
            w.println("#0.59x");
            w.println("#0.58x");
            w.println("#0.57x");
            w.println("#0.56x");
            w.println("#0.55x");
            w.println("#0.54x");
            w.println("#0.53x");
            w.println("#0.52x");
            w.println("#0.51x");
            w.println("#0.50x");
            w.println("#0.49x");
            w.println("#0.48x");
            w.println("#0.47x");
            w.println("#0.46x");
            w.println("#0.45x");
            w.println("#0.44x");
            w.println("#0.43x");
            w.println("#0.42x");
            w.println("#0.41x");
            w.println("#0.40x");
            w.println("#0.39x");
            w.println("#0.38x");
            w.println("#0.37x");
            w.println("#0.36x");
            w.println("#0.35x");
            w.println("#0.34x");
            w.println("#0.33x");
            w.println("#0.32x");
            w.println("#0.31x");
            w.println("#0.30x");
            w.println("#0.29x");
            w.println("#0.28x");
            w.println("#0.27x");
            w.println("#0.26x");
            w.println("#0.25x");
            w.println("#0.24x");
            w.println("#0.23x");
            w.println("#0.22x");
            w.println("#0.21x");
            w.println("#0.20x");
            w.println("#0.19x");
            w.println("#0.18x");
            w.println("#0.17x");
            w.println("#0.16x");
            w.println("#0.15x");
            w.println("#0.14x");
            w.println("#0.13x");
            w.println("#0.12x");
            w.println("#0.11x");
            w.println("#0.10x");
            w.println("#0.09x");
            w.println("#0.08x");
            w.println("#0.07x");
            w.println("#0.06x");
            w.println("#0.05x");
            w.println("#0.04x");
            w.println("#0.03x");
            w.println("#0.02x");
            w.println("#0.01x");
            w.println("#0.00x");
            w.println();
            w.println();
            w.println("# Example:  'Hockey Stick' Rate");
            w.println("#");
            w.println("# This pattern starts with a rate of zero operations per second and increases");
            w.println("# slowly at first before ramping up much more quickly.  A graph of the load");
            w.println("# generated by a single iteration of this pattern vaguely resembles a hockey");
            w.println("# stick.");
            w.println("#");
            w.println("#0.000x");
            w.println("#0.000x");
            w.println("#0.000x");
            w.println("#0.000x");
            w.println("#0.000x");
            w.println("#0.000x");
            w.println("#0.000x");
            w.println("#0.000x");
            w.println("#0.001x");
            w.println("#0.001x");
            w.println("#0.001x");
            w.println("#0.001x");
            w.println("#0.002x");
            w.println("#0.002x");
            w.println("#0.003x");
            w.println("#0.003x");
            w.println("#0.004x");
            w.println("#0.005x");
            w.println("#0.006x");
            w.println("#0.007x");
            w.println("#0.008x");
            w.println("#0.009x");
            w.println("#0.011x");
            w.println("#0.012x");
            w.println("#0.014x");
            w.println("#0.016x");
            w.println("#0.018x");
            w.println("#0.020x");
            w.println("#0.022x");
            w.println("#0.024x");
            w.println("#0.027x");
            w.println("#0.030x");
            w.println("#0.033x");
            w.println("#0.036x");
            w.println("#0.039x");
            w.println("#0.043x");
            w.println("#0.047x");
            w.println("#0.051x");
            w.println("#0.055x");
            w.println("#0.059x");
            w.println("#0.064x");
            w.println("#0.069x");
            w.println("#0.074x");
            w.println("#0.080x");
            w.println("#0.085x");
            w.println("#0.091x");
            w.println("#0.097x");
            w.println("#0.104x");
            w.println("#0.111x");
            w.println("#0.118x");
            w.println("#0.125x");
            w.println("#0.133x");
            w.println("#0.141x");
            w.println("#0.149x");
            w.println("#0.157x");
            w.println("#0.166x");
            w.println("#0.176x");
            w.println("#0.185x");
            w.println("#0.195x");
            w.println("#0.205x");
            w.println("#0.216x");
            w.println("#0.227x");
            w.println("#0.238x");
            w.println("#0.250x");
            w.println("#0.262x");
            w.println("#0.275x");
            w.println("#0.287x");
            w.println("#0.301x");
            w.println("#0.314x");
            w.println("#0.329x");
            w.println("#0.343x");
            w.println("#0.358x");
            w.println("#0.373x");
            w.println("#0.389x");
            w.println("#0.405x");
            w.println("#0.422x");
            w.println("#0.439x");
            w.println("#0.457x");
            w.println("#0.475x");
            w.println("#0.493x");
            w.println("#0.512x");
            w.println("#0.531x");
            w.println("#0.551x");
            w.println("#0.572x");
            w.println("#0.593x");
            w.println("#0.614x");
            w.println("#0.636x");
            w.println("#0.659x");
            w.println("#0.681x");
            w.println("#0.705x");
            w.println("#0.729x");
            w.println("#0.754x");
            w.println("#0.779x");
            w.println("#0.804x");
            w.println("#0.831x");
            w.println("#0.857x");
            w.println("#0.885x");
            w.println("#0.913x");
            w.println("#0.941x");
            w.println("#0.970x");
            w.println("#1.000x");
            w.println();
        }
        finally {
            w.close();
        }
    }
    
    public RateAdjustor(final FixedRateBarrier barrier, final long baseRatePerSecond, final Reader rates) throws IOException, IllegalArgumentException {
        this.shutDown = false;
        this.initialRateSetLatch = new CountDownLatch(1);
        this.sleeper = new WakeableSleeper();
        List<String> lines;
        try {
            Validator.ensureNotNull(barrier, rates);
            this.setDaemon(true);
            this.barrier = barrier;
            lines = readLines(rates);
        }
        finally {
            rates.close();
        }
        final Map<String, String> header = consumeHeader(lines);
        final Set<String> invalidKeys = new LinkedHashSet<String>(header.keySet());
        invalidKeys.removeAll(RateAdjustor.KEYS);
        if (!invalidKeys.isEmpty()) {
            throw new IllegalArgumentException(UtilityMessages.ERR_RATE_ADJUSTOR_INVALID_KEYS.get(invalidKeys, RateAdjustor.KEYS));
        }
        final String format = header.get("format");
        if (format == null) {
            throw new IllegalArgumentException(UtilityMessages.ERR_RATE_ADJUSTOR_MISSING_FORMAT.get("format", RateAdjustor.FORMATS, '#'));
        }
        if (!format.equals("rate-and-duration")) {
            throw new IllegalArgumentException(UtilityMessages.ERR_RATE_ADJUSTOR_INVALID_FORMAT.get(format, "format", RateAdjustor.FORMATS));
        }
        this.repeat = Boolean.parseBoolean(header.get("repeat"));
        long defaultDurationMillis = 0L;
        final String defaultDurationStr = header.get("default-duration");
        if (defaultDurationStr != null) {
            try {
                defaultDurationMillis = DurationArgument.parseDuration(defaultDurationStr, TimeUnit.MILLISECONDS);
            }
            catch (final ArgumentException e) {
                Debug.debugException(e);
                throw new IllegalArgumentException(UtilityMessages.ERR_RATE_ADJUSTOR_INVALID_DEFAULT_DURATION.get(defaultDurationStr, e.getExceptionMessage()), e);
            }
        }
        final List<ObjectPair<Double, Long>> ratesAndDurationList = new ArrayList<ObjectPair<Double, Long>>(10);
        final Pattern splitPattern = Pattern.compile("\\s*,\\s*");
        for (String line : lines) {
            final String fullLine = line;
            final int commentStart = fullLine.indexOf(35);
            if (commentStart >= 0) {
                line = line.substring(0, commentStart);
            }
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            final String[] fields = splitPattern.split(line);
            if (fields.length != 2 && (fields.length != 1 || defaultDurationMillis == 0L)) {
                throw new IllegalArgumentException(UtilityMessages.ERR_RATE_ADJUSTOR_INVALID_LINE.get(fullLine, "default-duration"));
            }
            String rateStr = fields[0];
            boolean isRateMultiplier = false;
            if (rateStr.endsWith("X") || rateStr.endsWith("x")) {
                rateStr = rateStr.substring(0, rateStr.length() - 1).trim();
                isRateMultiplier = true;
            }
            double rate;
            try {
                rate = Double.parseDouble(rateStr);
            }
            catch (final NumberFormatException e2) {
                Debug.debugException(e2);
                throw new IllegalArgumentException(UtilityMessages.ERR_RATE_ADJUSTOR_INVALID_RATE.get(rateStr, fullLine), e2);
            }
            if (isRateMultiplier) {
                if (baseRatePerSecond <= 0L) {
                    throw new IllegalArgumentException(UtilityMessages.ERR_RATE_ADJUSTOR_RELATIVE_RATE_WITHOUT_BASELINE.get(rateStr, fullLine));
                }
                rate *= baseRatePerSecond;
            }
            long durationMillis;
            if (fields.length < 2) {
                durationMillis = defaultDurationMillis;
            }
            else {
                final String duration = fields[1];
                try {
                    durationMillis = DurationArgument.parseDuration(duration, TimeUnit.MILLISECONDS);
                }
                catch (final ArgumentException e3) {
                    Debug.debugException(e3);
                    throw new IllegalArgumentException(UtilityMessages.ERR_RATE_ADJUSTOR_INVALID_DURATION.get(duration, fullLine, e3.getExceptionMessage()), e3);
                }
            }
            ratesAndDurationList.add(new ObjectPair<Double, Long>(rate, durationMillis));
        }
        this.ratesAndDurations = Collections.unmodifiableList((List<? extends ObjectPair<Double, Long>>)ratesAndDurationList);
    }
    
    @Override
    public void start() {
        super.start();
        try {
            this.initialRateSetLatch.await();
        }
        catch (final InterruptedException e) {
            Debug.debugException(e);
            Thread.currentThread().interrupt();
        }
    }
    
    @Override
    public void run() {
        try {
            if (this.ratesAndDurations.isEmpty()) {
                return;
            }
            do {
                final List<ObjectPair<Double, Long>> ratesAndEndTimes = new ArrayList<ObjectPair<Double, Long>>(this.ratesAndDurations.size());
                long endTime = System.currentTimeMillis();
                for (final ObjectPair<Double, Long> rateAndDuration : this.ratesAndDurations) {
                    endTime += rateAndDuration.getSecond();
                    ratesAndEndTimes.add(new ObjectPair<Double, Long>(rateAndDuration.getFirst(), endTime));
                }
                for (final ObjectPair<Double, Long> rateAndEndTime : ratesAndEndTimes) {
                    if (this.shutDown) {
                        return;
                    }
                    final double rate = rateAndEndTime.getFirst();
                    final long intervalMillis = this.barrier.getTargetRate().getFirst();
                    final int perInterval = calculatePerInterval(intervalMillis, rate);
                    this.barrier.setRate(intervalMillis, perInterval);
                    if (this.initialRateSetLatch.getCount() > 0L) {
                        this.initialRateSetLatch.countDown();
                    }
                    final long durationMillis = rateAndEndTime.getSecond() - System.currentTimeMillis();
                    if (durationMillis <= 0L) {
                        continue;
                    }
                    this.sleeper.sleep(durationMillis);
                }
            } while (this.repeat);
        }
        finally {
            if (this.initialRateSetLatch.getCount() > 0L) {
                this.initialRateSetLatch.countDown();
            }
        }
    }
    
    public void shutDown() {
        this.shutDown = true;
        this.sleeper.wakeup();
    }
    
    List<ObjectPair<Double, Long>> getRatesAndDurations() {
        return this.ratesAndDurations;
    }
    
    static int calculatePerInterval(final long intervalDurationMillis, final double ratePerSecond) {
        final double intervalDurationSeconds = intervalDurationMillis / 1000.0;
        final double ratePerInterval = ratePerSecond * intervalDurationSeconds;
        return (int)Math.max(1L, Math.round(ratePerInterval));
    }
    
    static Map<String, String> consumeHeader(final List<String> lines) throws IllegalArgumentException {
        boolean endHeaderFound = false;
        final Map<String, String> headerMap = new LinkedHashMap<String, String>(StaticUtils.computeMapCapacity(3));
        final Iterator<String> lineIter = lines.iterator();
        while (lineIter.hasNext()) {
            final String line = lineIter.next().trim();
            lineIter.remove();
            if (!line.isEmpty()) {
                if (line.startsWith(String.valueOf('#'))) {
                    continue;
                }
                if (line.equalsIgnoreCase("END HEADER")) {
                    endHeaderFound = true;
                    break;
                }
                final int equalPos = line.indexOf(61);
                if (equalPos < 0) {
                    throw new IllegalArgumentException(UtilityMessages.ERR_RATE_ADJUSTOR_HEADER_NO_EQUAL.get(line));
                }
                final String key = line.substring(0, equalPos).trim();
                if (key.isEmpty()) {
                    throw new IllegalArgumentException(UtilityMessages.ERR_RATE_ADJUSTOR_HEADER_EMPTY_KEY.get(line));
                }
                final String newValue = line.substring(equalPos + 1).trim();
                final String existingValue = headerMap.get(key);
                if (existingValue != null) {
                    throw new IllegalArgumentException(UtilityMessages.ERR_RATE_ADJUSTOR_DUPLICATE_HEADER_KEY.get(key, existingValue, newValue));
                }
                headerMap.put(key, newValue);
            }
        }
        if (!endHeaderFound) {
            throw new IllegalArgumentException(UtilityMessages.ERR_RATE_ADJUSTOR_NO_END_HEADER_FOUND.get("END HEADER"));
        }
        return headerMap;
    }
    
    private static List<String> readLines(final Reader reader) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(reader);
        final List<String> lines = new LinkedList<String>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }
    
    static {
        FORMATS = Collections.singletonList("rate-and-duration");
        KEYS = Arrays.asList("default-duration", "format", "repeat");
    }
}
