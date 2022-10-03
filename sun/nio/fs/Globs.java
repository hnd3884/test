package sun.nio.fs;

import java.util.regex.PatternSyntaxException;

public class Globs
{
    private static final String regexMetaChars = ".^$+{[]|()";
    private static final String globMetaChars = "\\*?[{";
    private static char EOL;
    
    private Globs() {
    }
    
    private static boolean isRegexMeta(final char c) {
        return ".^$+{[]|()".indexOf(c) != -1;
    }
    
    private static boolean isGlobMeta(final char c) {
        return "\\*?[{".indexOf(c) != -1;
    }
    
    private static char next(final String s, final int n) {
        if (n < s.length()) {
            return s.charAt(n);
        }
        return Globs.EOL;
    }
    
    private static String toRegexPattern(final String s, final boolean b) {
        int n = 0;
        final StringBuilder sb = new StringBuilder("^");
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i++);
            switch (c) {
                case 92: {
                    if (i == s.length()) {
                        throw new PatternSyntaxException("No character to escape", s, i - 1);
                    }
                    final char char1 = s.charAt(i++);
                    if (isGlobMeta(char1) || isRegexMeta(char1)) {
                        sb.append('\\');
                    }
                    sb.append(char1);
                    continue;
                }
                case 47: {
                    if (b) {
                        sb.append("\\\\");
                        continue;
                    }
                    sb.append(c);
                    continue;
                }
                case 91: {
                    if (b) {
                        sb.append("[[^\\\\]&&[");
                    }
                    else {
                        sb.append("[[^/]&&[");
                    }
                    if (next(s, i) == '^') {
                        sb.append("\\^");
                        ++i;
                    }
                    else {
                        if (next(s, i) == '!') {
                            sb.append('^');
                            ++i;
                        }
                        if (next(s, i) == '-') {
                            sb.append('-');
                            ++i;
                        }
                    }
                    int n2 = 0;
                    char c2 = '\0';
                    while (i < s.length()) {
                        c = s.charAt(i++);
                        if (c == ']') {
                            break;
                        }
                        if (c == '/' || (b && c == '\\')) {
                            throw new PatternSyntaxException("Explicit 'name separator' in class", s, i - 1);
                        }
                        if (c == '\\' || c == '[' || (c == '&' && next(s, i) == '&')) {
                            sb.append('\\');
                        }
                        sb.append(c);
                        if (c == '-') {
                            if (n2 == 0) {
                                throw new PatternSyntaxException("Invalid range", s, i - 1);
                            }
                            if ((c = next(s, i++)) == Globs.EOL) {
                                break;
                            }
                            if (c == ']') {
                                break;
                            }
                            if (c < c2) {
                                throw new PatternSyntaxException("Invalid range", s, i - 3);
                            }
                            sb.append(c);
                            n2 = 0;
                        }
                        else {
                            n2 = 1;
                            c2 = c;
                        }
                    }
                    if (c != ']') {
                        throw new PatternSyntaxException("Missing ']", s, i - 1);
                    }
                    sb.append("]]");
                    continue;
                }
                case 123: {
                    if (n != 0) {
                        throw new PatternSyntaxException("Cannot nest groups", s, i - 1);
                    }
                    sb.append("(?:(?:");
                    n = 1;
                    continue;
                }
                case 125: {
                    if (n != 0) {
                        sb.append("))");
                        n = 0;
                        continue;
                    }
                    sb.append('}');
                    continue;
                }
                case 44: {
                    if (n != 0) {
                        sb.append(")|(?:");
                        continue;
                    }
                    sb.append(',');
                    continue;
                }
                case 42: {
                    if (next(s, i) == '*') {
                        sb.append(".*");
                        ++i;
                        continue;
                    }
                    if (b) {
                        sb.append("[^\\\\]*");
                        continue;
                    }
                    sb.append("[^/]*");
                    continue;
                }
                case 63: {
                    if (b) {
                        sb.append("[^\\\\]");
                        continue;
                    }
                    sb.append("[^/]");
                    continue;
                }
                default: {
                    if (isRegexMeta(c)) {
                        sb.append('\\');
                    }
                    sb.append(c);
                    continue;
                }
            }
        }
        if (n != 0) {
            throw new PatternSyntaxException("Missing '}", s, i - 1);
        }
        return sb.append('$').toString();
    }
    
    static String toUnixRegexPattern(final String s) {
        return toRegexPattern(s, false);
    }
    
    static String toWindowsRegexPattern(final String s) {
        return toRegexPattern(s, true);
    }
    
    static {
        Globs.EOL = '\0';
    }
}
