package org.apache.commons.lang.time;

class DurationFormatUtils
{
    public static final String ISO_EXTENDED_FORMAT_PATTERN = "'P'yyyy'Y'M'M'd'DT'H'H'm'M's.S'S'";
    public static final FastDateFormat ISO_EXTENDED_FORMAT;
    
    public static String formatISO(long millis) {
        final int hours = (int)(millis / 3600000L);
        millis -= hours * 3600000;
        final int minutes = (int)(millis / 60000L);
        millis -= minutes * 60000;
        final int seconds = (int)(millis / 1000L);
        millis -= seconds * 1000;
        final int milliseconds = (int)millis;
        final StringBuffer buf = new StringBuffer(32);
        buf.append(hours);
        buf.append(':');
        buf.append((char)(minutes / 10 + 48));
        buf.append((char)(minutes % 10 + 48));
        buf.append(':');
        buf.append((char)(seconds / 10 + 48));
        buf.append((char)(seconds % 10 + 48));
        buf.append('.');
        if (milliseconds < 10) {
            buf.append('0').append('0');
        }
        else if (milliseconds < 100) {
            buf.append('0');
        }
        buf.append(milliseconds);
        return buf.toString();
    }
    
    public static String formatWords(final long millis, final boolean supressLeadingZeroElements, final boolean supressTrailingZeroElements) {
        final long[] values = { millis / 86400000L, millis / 3600000L % 24L, millis / 60000L % 60L, millis / 1000L % 60L };
        final String[] fieldsOne = { " day ", " hour ", " minute ", " second" };
        final String[] fieldsPlural = { " days ", " hours ", " minutes ", " seconds" };
        final StringBuffer buf = new StringBuffer(64);
        boolean valueOutput = false;
        for (int i = 0; i < 4; ++i) {
            final long value = values[i];
            if (value == 0L) {
                if (valueOutput) {
                    if (!supressTrailingZeroElements) {
                        buf.append('0').append(fieldsPlural[i]);
                    }
                }
                else if (!supressLeadingZeroElements) {
                    buf.append('0').append(fieldsPlural[i]);
                }
            }
            else if (value == 1L) {
                valueOutput = true;
                buf.append('1').append(fieldsOne[i]);
            }
            else {
                valueOutput = true;
                buf.append(value).append(fieldsPlural[i]);
            }
        }
        return buf.toString().trim();
    }
    
    public DurationFormatUtils() {
    }
    
    static {
        ISO_EXTENDED_FORMAT = FastDateFormat.getInstance("'P'yyyy'Y'M'M'd'DT'H'H'm'M's.S'S'");
    }
}
