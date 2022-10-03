package sun.nio.fs;

import java.nio.file.InvalidPathException;

class WindowsPathParser
{
    private static final String reservedChars = "<>:\"|?*";
    
    private WindowsPathParser() {
    }
    
    static Result parse(final String s) {
        return parse(s, true);
    }
    
    static Result parseNormalizedPath(final String s) {
        return parse(s, false);
    }
    
    private static Result parse(final String s, final boolean b) {
        String s2 = "";
        WindowsPathType windowsPathType = null;
        final int length = s.length();
        int n = 0;
        if (length > 1) {
            final char char1 = s.charAt(0);
            final char char2 = s.charAt(1);
            final int n2 = 2;
            if (isSlash(char1) && isSlash(char2)) {
                windowsPathType = WindowsPathType.UNC;
                final int nextNonSlash = nextNonSlash(s, n2, length);
                final int nextSlash = nextSlash(s, nextNonSlash, length);
                if (nextNonSlash == nextSlash) {
                    throw new InvalidPathException(s, "UNC path is missing hostname");
                }
                final String substring = s.substring(nextNonSlash, nextSlash);
                final int nextNonSlash2 = nextNonSlash(s, nextSlash, length);
                final int nextSlash2 = nextSlash(s, nextNonSlash2, length);
                if (nextNonSlash2 == nextSlash2) {
                    throw new InvalidPathException(s, "UNC path is missing sharename");
                }
                s2 = "\\\\" + substring + "\\" + s.substring(nextNonSlash2, nextSlash2) + "\\";
                n = nextSlash2;
            }
            else if (isLetter(char1) && char2 == ':') {
                final char char3;
                if (length > 2 && isSlash(char3 = s.charAt(2))) {
                    if (char3 == '\\') {
                        s2 = s.substring(0, 3);
                    }
                    else {
                        s2 = s.substring(0, 2) + '\\';
                    }
                    n = 3;
                    windowsPathType = WindowsPathType.ABSOLUTE;
                }
                else {
                    s2 = s.substring(0, 2);
                    n = 2;
                    windowsPathType = WindowsPathType.DRIVE_RELATIVE;
                }
            }
        }
        if (n == 0) {
            if (length > 0 && isSlash(s.charAt(0))) {
                windowsPathType = WindowsPathType.DIRECTORY_RELATIVE;
                s2 = "\\";
            }
            else {
                windowsPathType = WindowsPathType.RELATIVE;
            }
        }
        if (b) {
            final StringBuilder sb = new StringBuilder(s.length());
            sb.append(s2);
            return new Result(windowsPathType, s2, normalize(sb, s, n));
        }
        return new Result(windowsPathType, s2, s);
    }
    
    private static String normalize(final StringBuilder sb, final String s, int i) {
        final int length = s.length();
        int nextNonSlash;
        i = (nextNonSlash = nextNonSlash(s, i, length));
        char c = '\0';
        while (i < length) {
            final char char1 = s.charAt(i);
            if (isSlash(char1)) {
                if (c == ' ') {
                    throw new InvalidPathException(s, "Trailing char <" + c + ">", i - 1);
                }
                sb.append(s, nextNonSlash, i);
                i = nextNonSlash(s, i, length);
                if (i != length) {
                    sb.append('\\');
                }
                nextNonSlash = i;
            }
            else {
                if (isInvalidPathChar(char1)) {
                    throw new InvalidPathException(s, "Illegal char <" + char1 + ">", i);
                }
                c = char1;
                ++i;
            }
        }
        if (nextNonSlash != i) {
            if (c == ' ') {
                throw new InvalidPathException(s, "Trailing char <" + c + ">", i - 1);
            }
            sb.append(s, nextNonSlash, i);
        }
        return sb.toString();
    }
    
    private static final boolean isSlash(final char c) {
        return c == '\\' || c == '/';
    }
    
    private static final int nextNonSlash(final String s, int n, final int n2) {
        while (n < n2 && isSlash(s.charAt(n))) {
            ++n;
        }
        return n;
    }
    
    private static final int nextSlash(final String s, int n, final int n2) {
        char char1;
        while (n < n2 && !isSlash(char1 = s.charAt(n))) {
            if (isInvalidPathChar(char1)) {
                throw new InvalidPathException(s, "Illegal character [" + char1 + "] in path", n);
            }
            ++n;
        }
        return n;
    }
    
    private static final boolean isLetter(final char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
    
    private static final boolean isInvalidPathChar(final char c) {
        return c < ' ' || "<>:\"|?*".indexOf(c) != -1;
    }
    
    static class Result
    {
        private final WindowsPathType type;
        private final String root;
        private final String path;
        
        Result(final WindowsPathType type, final String root, final String path) {
            this.type = type;
            this.root = root;
            this.path = path;
        }
        
        WindowsPathType type() {
            return this.type;
        }
        
        String root() {
            return this.root;
        }
        
        String path() {
            return this.path;
        }
    }
}
