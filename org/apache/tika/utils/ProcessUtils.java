package org.apache.tika.utils;

public class ProcessUtils
{
    public static String escapeCommandLine(String arg) {
        if (arg == null) {
            return arg;
        }
        if (arg.contains(" ") && SystemUtils.IS_OS_WINDOWS && !arg.startsWith("\"") && !arg.endsWith("\"")) {
            arg = "\"" + arg + "\"";
        }
        return arg;
    }
    
    public static String unescapeCommandLine(String arg) {
        if (arg.contains(" ") && SystemUtils.IS_OS_WINDOWS && arg.startsWith("\"") && arg.endsWith("\"")) {
            arg = arg.substring(1, arg.length() - 1);
        }
        return arg;
    }
}
