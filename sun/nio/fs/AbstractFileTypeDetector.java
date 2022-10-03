package sun.nio.fs;

import java.util.Locale;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;

public abstract class AbstractFileTypeDetector extends FileTypeDetector
{
    private static final String TSPECIALS = "()<>@,;:/[]?=\\\"";
    
    protected AbstractFileTypeDetector() {
    }
    
    @Override
    public final String probeContentType(final Path path) throws IOException {
        if (path == null) {
            throw new NullPointerException("'file' is null");
        }
        final String implProbeContentType = this.implProbeContentType(path);
        return (implProbeContentType == null) ? null : parse(implProbeContentType);
    }
    
    protected abstract String implProbeContentType(final Path p0) throws IOException;
    
    private static String parse(final String s) {
        final int index = s.indexOf(47);
        final int index2 = s.indexOf(59);
        if (index < 0) {
            return null;
        }
        final String lowerCase = s.substring(0, index).trim().toLowerCase(Locale.ENGLISH);
        if (!isValidToken(lowerCase)) {
            return null;
        }
        final String lowerCase2 = ((index2 < 0) ? s.substring(index + 1) : s.substring(index + 1, index2)).trim().toLowerCase(Locale.ENGLISH);
        if (!isValidToken(lowerCase2)) {
            return null;
        }
        final StringBuilder sb = new StringBuilder(lowerCase.length() + lowerCase2.length() + 1);
        sb.append(lowerCase);
        sb.append('/');
        sb.append(lowerCase2);
        return sb.toString();
    }
    
    private static boolean isTokenChar(final char c) {
        return c > ' ' && c < '\u007f' && "()<>@,;:/[]?=\\\"".indexOf(c) < 0;
    }
    
    private static boolean isValidToken(final String s) {
        final int length = s.length();
        if (length == 0) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (!isTokenChar(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
