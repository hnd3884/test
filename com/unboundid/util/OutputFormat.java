package com.unboundid.util;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum OutputFormat
{
    COLUMNS, 
    TAB_DELIMITED_TEXT, 
    CSV;
    
    public static OutputFormat forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "columns": {
                return OutputFormat.COLUMNS;
            }
            case "tabdelimitedtext":
            case "tab-delimited-text":
            case "tab_delimited_text": {
                return OutputFormat.TAB_DELIMITED_TEXT;
            }
            case "csv": {
                return OutputFormat.CSV;
            }
            default: {
                return null;
            }
        }
    }
}
