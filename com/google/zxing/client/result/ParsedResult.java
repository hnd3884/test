package com.google.zxing.client.result;

public abstract class ParsedResult
{
    private final ParsedResultType type;
    
    protected ParsedResult(final ParsedResultType type) {
        this.type = type;
    }
    
    public ParsedResultType getType() {
        return this.type;
    }
    
    public abstract String getDisplayResult();
    
    @Override
    public String toString() {
        return this.getDisplayResult();
    }
    
    public static void maybeAppend(final String value, final StringBuilder result) {
        if (value != null && value.length() > 0) {
            if (result.length() > 0) {
                result.append('\n');
            }
            result.append(value);
        }
    }
    
    public static void maybeAppend(final String[] value, final StringBuilder result) {
        if (value != null) {
            for (final String s : value) {
                if (s != null && s.length() > 0) {
                    if (result.length() > 0) {
                        result.append('\n');
                    }
                    result.append(s);
                }
            }
        }
    }
}
